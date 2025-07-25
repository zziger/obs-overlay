#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform vec4 ColorModulator;
uniform int OverrideDepth;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    vec4 depth = texture(Sampler1, texCoord0);

    if (color.a == 0.0) {
        discard;
    }
    fragColor = color;
    gl_FragDepth = OverrideDepth == 1 && depth.r < 1 ? 0.0 : depth.r;
}
