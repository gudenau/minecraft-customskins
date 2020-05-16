#version 330 core

in vec4 frag_color;
in vec2 frag_uv;
in vec2 frag_lightmapUv;

out vec4 fragmentColor;

uniform sampler2D primaryTexture;
uniform sampler2D lightmapTexture;
uniform sampler2D emissiveTexture;

void main(){
    vec4 emissiveValue = texture(emissiveTexture, frag_uv);
    if(emissiveValue.z == 0){
        fragmentColor =
            frag_color *
            texture(primaryTexture, frag_uv) *
            vec4(texture(lightmapTexture, frag_lightmapUv).xyz, 1);
    }else{
        fragmentColor =
            frag_color *
            texture(primaryTexture, frag_uv) *
            vec4(texture(lightmapTexture, frag_lightmapUv).xyz, 1) +
            vec4(emissiveValue.xyz, 1);
    }
}
