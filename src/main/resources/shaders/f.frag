// TODO: Remove

#version 460 core

layout (location = 0) out vec4 frag_color;
in vec4 pos;

uniform sampler2D scene_texture;

void main() {
    vec3 col = texture(scene_texture, vec2(pos.x, pos.y)).rgb;
    frag_color = vec4(col, 1.0);
}
