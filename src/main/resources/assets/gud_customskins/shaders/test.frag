#version 330 core

in vec4 frag_color;
in vec2 frag_uv;

out vec4 fragmentColor;

uniform sampler2D primaryTexture;

void main(){
    fragmentColor = frag_color * texture(primaryTexture, frag_uv);
}
