#version 330 core

in vec4 frag_color;
in vec2 frag_uv;
in vec2 frag_lightmapUv;

out vec4 fragmentColor;

uniform sampler2D primaryTexture;
uniform sampler2D lightmapTexture;

void main(){
    fragmentColor =
        frag_color *
        texture(primaryTexture, frag_uv) *
        vec4(texture(lightmapTexture, frag_lightmapUv).xyz, 1);
}
