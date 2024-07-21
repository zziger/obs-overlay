# API Documentation

## Installation

This mod is uploaded to Modrinth Maven. You can add it to your project by adding the following to your `build.gradle`:

```gradle
repositories {
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
    }
}

dependencies {
    modImplementation "maven.modrinth:obs-overlay:${project.obs_overlay_version}"
}
```

## Usage

Most of the API is accessible through `me.zziger.obsoverlay.OverlayRenderer` and `me.zziger.obsoverlay.registry.OverlayComponentRegistry` classes.
### Drawing to an overlay

In order to draw your own HUD components to the overlay, you need to wrap your rendering code with following methods:

```java
OverlayRenderer.beginDraw();
// Your rendering code here
OverlayRenderer.endDraw();
```

This internally swaps the framebuffer to a custom one, that will be rendered at a later stage.\
Beware that since rendering is happening into a custom framebuffer, you might have some issues when using custom blending options.

### Adding your own HUD components to settings

You can extend list of components that are displayed in mod settings.\
To achieve that you can use `OverlayComponentRegistry.registerComponents` method.

Creating an instance of DefaultOverlayComponent would store state of the component in OBS Overlay config. If you wish to store the data in your own config, you can implement `OverlayComponent` interface or extend from a `DefaultOverlayComponent`  class.\
If you have your own `OverlayComponent` class you can pass it to `beginDraw`/`endDraw` to automatically decide whether switch to a overlay framebuffer needed, or whether component should be hidden completely.\

You will also need to define `obs_overlay.component.your_component_id` and `obs_overlay.component.your_component_id.tooltip` localization keys.

For example check [ChatHudMixin](common/src/main/java/me/zziger/obsoverlay/mixin/hud/ChatHudMixin.java) and [DefaultOverlayComponent](common/src/main/java/me/zziger/obsoverlay/registry/DefaultOverlayComponent.java).

### Adding your own non-fullscreen screens

By default, this mod automatically hides most components when any screen except for Chat is opened.\
You can add your own Screen that will also be excluded from this check by calling `OverlayComponentRegistry.addIgnoredScreen` method. 