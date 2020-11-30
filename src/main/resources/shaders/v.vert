// TODO: Remove

#version 460 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec4 tex_coords;
out vec4 pos;

uniform mat4 model_matrix;

void main() {
    gl_Position = model_matrix * position;
    pos = position;
}
