#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec4 color;
layout(location = 2) in vec2 uv;
layout(location = 3) in vec2 lightmapUv;

out vec4 frag_color;
out vec2 frag_uv;
out vec2 frag_lightmapUv;

uniform mat4 projection;

void main(){
    gl_Position = projection * vec4(position, 1);
    frag_color = color;
    frag_uv = uv;
    frag_lightmapUv = lightmapUv;
}
