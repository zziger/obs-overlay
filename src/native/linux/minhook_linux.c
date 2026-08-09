/*
 * minhook_linux.c
 *
 * MinHook-compatible hooking for Linux, implemented on top of funchook
 * (https://github.com/kubo/funchook, GPLv2+ with linking exception).
 *
 * The Java side (me.zziger.obsoverlay.modules.MinHook.java) speaks the same
 * 3-function API on Windows (MinHook.dll) and here (libMinHook.so):
 *
 *   int MH_Initialize(void);
 *   int MH_CreateHook(void *pTarget, void *pDetour, void **ppOriginal);
 *   int MH_EnableHook(void *pTarget);
 *
 * funchook handles the hard parts an inline hook needs on x86_64/aarch64:
 * instruction decoding, RIP-relative relocation into the trampoline,
 * near-allocation of the trampoline and atomic hotpatching.
 */
#define _GNU_SOURCE

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>

#include "funchook.h"

typedef struct mh_hook {
    funchook_t *fh;      /* funchook handle (one per target)      */
    void *target;        /* original function address (unpatched) */
    void *trampoline;    /* call this to reach the original code  */
    int installed;
    struct mh_hook *next;
} mh_hook_t;

static mh_hook_t *g_hooks;
static pthread_mutex_t g_lock = PTHREAD_MUTEX_INITIALIZER;

int MH_Initialize(void)
{
    /* funchook needs no global setup; hooks allocate their own trampolines. */
    return 0;
}

int MH_Uninitialize(void)
{
    pthread_mutex_lock(&g_lock);
    while (g_hooks) {
        mh_hook_t *h = g_hooks;
        g_hooks = h->next;
        if (h->installed && h->fh)
            funchook_uninstall(h->fh, 0);
        if (h->fh)
            funchook_destroy(h->fh);
        free(h);
    }
    pthread_mutex_unlock(&g_lock);
    return 0;
}

int MH_CreateHook(void *pTarget, void *pDetour, void **ppOriginal)
{
    if (!pTarget || !pDetour)
        return -1;

    mh_hook_t *h = calloc(1, sizeof(*h));
    if (!h)
        return -1;

    h->target = pTarget;
    h->fh = funchook_create();
    if (!h->fh) {
        free(h);
        return -1;
    }

    /* funchook_prepare rewrites *pTargetFunc to point at the original
     * (trampoline). Nothing is patched yet, so the observable behaviour of the
     * target is unchanged until MH_EnableHook() -> funchook_install(). */
    void *target = pTarget;
    int rc = funchook_prepare(h->fh, &target, pDetour);
    if (rc != 0) {
        fprintf(stderr, "overlay: funchook_prepare(%p) failed (%d)%s%s\n",
                pTarget, rc,
                rc == FUNCHOOK_ERROR_DISASSEMBLY ? " disassembly" :
                rc == FUNCHOOK_ERROR_TOO_SHORT_INSTRUCTIONS ? " too-short-insns" :
                rc == FUNCHOOK_ERROR_NO_SPACE_NEAR_TARGET_ADDR ? " no-near-space" : "",
                h->fh ? funchook_error_message(h->fh) : "");
        funchook_destroy(h->fh);
        free(h);
        return rc;
    }

    h->trampoline = target;
    if (ppOriginal)
        *ppOriginal = target;

    pthread_mutex_lock(&g_lock);
    h->next = g_hooks;
    g_hooks = h;
    pthread_mutex_unlock(&g_lock);
    return 0;
}

static mh_hook_t *find_hook(const void *pTarget)
{
    for (mh_hook_t *h = g_hooks; h; h = h->next) {
        if (h->target == pTarget)
            return h;
    }
    return NULL;
}

int MH_EnableHook(void *pTarget)
{
    pthread_mutex_lock(&g_lock);
    mh_hook_t *h = find_hook(pTarget);
    if (!h) {
        pthread_mutex_unlock(&g_lock);
        fprintf(stderr, "overlay: MH_EnableHook: target %p was never hooked\n", pTarget);
        return -1;
    }
    if (!h->installed) {
        int rc = funchook_install(h->fh, 0);
        if (rc != 0) {
            pthread_mutex_unlock(&g_lock);
            fprintf(stderr, "overlay: funchook_install(%p) failed (%d %s)\n",
                    pTarget, rc, h->fh ? funchook_error_message(h->fh) : "");
            return rc;
        }
        h->installed = 1;
    }
    pthread_mutex_unlock(&g_lock);
    return 0;
}