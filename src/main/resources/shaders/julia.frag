#version 460 core

layout (location = 0) out vec4 frag_color;
in vec4 pos;

uniform int max_iterations;
uniform double scale;
uniform dvec2 offset;
uniform sampler1D palette_texture;

vec4 julia(int max_iterations);
int calc_julia_iterations(int max_iterations);

vec4 julia(int max_iterations) {
    vec4 color;
    int iterations = calc_julia_iterations(max_iterations);

    if (iterations == max_iterations) {
        color = texture(palette_texture, 0.0f).rgba;
    } else {
        color = texture(palette_texture, float(iterations) / float(max_iterations)).rgba;
    }
    return color;
}

int calc_julia_iterations(int max_iterations) {
    int iterations = 0;
    int r = 4; // TODO: Define escape radius
    double x0 = pos.x * scale + offset.x;
    double y0 = pos.y * scale + offset.y;
    double x1 = 0.0f, y1 = 0.0f;
    double x2 = 0.0f, y2 = 0.0f;

    while (x2 + y2 <= r && iterations < max_iterations) {

        iterations++;
    }
    return iterations;
}

void main() {
    frag_color = julia(max_iterations);
}
