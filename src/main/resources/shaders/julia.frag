#version 460 core

layout (location = 0) out vec4 frag_color;
in vec4 pos;

const int escape_radius = 4;

uniform int max_iterations;
uniform double scale;
uniform dvec2 offset;
uniform dvec2 seed;
uniform sampler1D palette_texture;

vec4 julia(int max_iterations, int escape_radius, dvec2 c);
int calc_julia_iterations(int max_iterations, int escape_radius, dvec2 c);

vec4 julia(int max_iterations, int escape_radius, dvec2 c) {
    vec4 color;
    int iterations = calc_julia_iterations(max_iterations, escape_radius, c);

    if (iterations == max_iterations) {
        color = texture(palette_texture, 0.0f).rgba;
    } else {
        color = texture(palette_texture, float(iterations) / float(max_iterations)).rgba;
    }
    return color;
}

int calc_julia_iterations(int max_iterations, int escape_radius, dvec2 c) {
    int iterations = 0;
    double x0 = pos.x * scale + offset.x + 0.8; // TODO: Remove constant
    double y0 = pos.y * scale + offset.y;
    double x1 = 0.0;

    while (x0 * x0 + y0 * y0 <= escape_radius && iterations < max_iterations) {
        x1 = x0 * x0 - y0 * y0;
        y0 = 2 * x0 * y0 + c.y;
        x0 = x1 + c.x;
        iterations++;
    }
    return iterations;
}

void main() {
    frag_color = julia(max_iterations, escape_radius, seed);
}
