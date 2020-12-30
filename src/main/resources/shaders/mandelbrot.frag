#version 460 core

layout (location = 0) out vec4 frag_color;
in vec4 pos;

const int escape_radius = 4;

uniform int max_iterations;
uniform double scale;
uniform dvec2 offset;
uniform sampler1D palette_texture;

vec4 mandelbrot(int max_iterations, int escape_radius);
int calc_mandelbrot_iterations(int max_iterations, int escape_radius);

vec4 mandelbrot(int max_iterations, int escape_radius) {
    vec4 color;
    int iterations = calc_mandelbrot_iterations(max_iterations, escape_radius);

    if (iterations == max_iterations) {
        //color = vec4(1.0f, 1.0f, 1.0f, 1.0f);
        color = texture(palette_texture, 0.0f).rgba;
    } else {
        //float rgb = sqrt(float(iterations) / max_iterations);
        //color = vec4(rgb, 0.0f, rgb, 1.0f);
        color = texture(palette_texture, float(iterations) / float(max_iterations)).rgba;
    }
    return color;
}

int calc_mandelbrot_iterations(int max_iterations, int escape_radius) {
    int iterations = 0;
    double x0 = pos.x * scale + offset.x;
    double y0 = pos.y * scale + offset.y;
    double x1 = 0.0f, y1 = 0.0f;
    double x2 = 0.0f, y2 = 0.0f;

    while (x2 + y2 <= escape_radius && iterations < max_iterations) {
        y1 = 2 * x1 * y1 + y0;
        x1 = x2 - y2 + x0;
        x2 = x1 * x1;
        y2 = y1 * y1;
        iterations++;
    }
    return iterations;
}

void main() {
    frag_color = mandelbrot(max_iterations, escape_radius);
}
