#version 460 core

layout (location = 0) out vec4 frag_color;
in vec4 pos;

const double PI = 3.14159265359;

uniform int use_orbit_trap;
uniform int max_iterations;
uniform int escape_radius;
uniform double scale;
uniform dvec2 offset;
uniform dvec2 trappingPointOffset;
uniform sampler1D palette_texture;

vec4 mandelbrot(int max_iterations);
int escape_time(int max_iterations);
double orbit_trap(int max_iterations);
float distance_to_line(vec2 pt1, vec2 pt2, vec2 pt3);

vec4 mandelbrot(int max_iterations) {
    vec4 color;

    if (use_orbit_trap == 1) {
        double distance = orbit_trap(max_iterations);
        color = texture(palette_texture, float(distance)).rgba;
    } else {
        int iterations = escape_time(max_iterations);

        if (iterations == max_iterations) {
            color = texture(palette_texture, 0.0f).rgba;
        } else {
            color = texture(palette_texture, float(iterations) / float(max_iterations)).rgba;
        }
    }
    return color;
}

int escape_time(int max_iterations) {
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

double orbit_trap(int max_iterations) {
    double distance = 1e20;
    double x0 = pos.x * scale + offset.x;
    double y0 = pos.y * scale + offset.y;
    double x1 = 0.0f, y1 = 0.0f;
    double x2 = 0.0f, y2 = 0.0f;

    vec2 p1 = vec2(pow(float(trappingPointOffset.x * PI), 2), 0);
    vec2 p2 = vec2(0, pow(float(trappingPointOffset.y * PI), 2));

    for (int i = 0; i < max_iterations; i++) {
        y1 = 2 * x1 * y1 + y0;
        x1 = x2 - y2 + x0;
        x2 = x1 * x1;
        y2 = y1 * y1;

        if (escape_radius != 0 && abs(x2 + y2) > escape_radius) return 0;

        vec2 p = vec2(x2, y2);
        float distanceToLine = distance_to_line(p1, p2, p);
        distance = min(distance, abs(distanceToLine));
    }
    return sqrt(distance);
}

float distance_to_line(vec2 pt1, vec2 pt2, vec2 pt3) {
    vec2 v1 = pt2 - pt1;
    vec2 v2 = pt1 - pt3;
    vec2 v3 = vec2(v1.y, -v1.x);
    return abs(dot(normalize(v3), v2));
}

void main() {
    frag_color = mandelbrot(max_iterations);
}
