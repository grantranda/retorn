#version 460 core

layout (location = 0) in vec3 position;
out vec3 pos;

uniform mat4 model_matrix;
uniform mat4 projection_matrix;

void main() {
    gl_Position = projection_matrix * model_matrix * vec4(position, 1.0f);
    pos = position;
}
