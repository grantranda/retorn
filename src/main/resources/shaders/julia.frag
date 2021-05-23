#version 460 core

layout (location = 0) out vec4 frag_color;
in vec4 pos;

const double PI = 3.14159265359;
const float X_OFFSET = 0.8f;

uniform int use_orbit_trap;
uniform int max_iterations;
uniform int escape_radius;
uniform double scale;
uniform dvec2 offset;
uniform dvec2 seed;
uniform dvec2 trappingPointOffset;
uniform sampler1D palette_texture;

vec4 julia(int max_iterations, dvec2 c);
int escape_time(int max_iterations, dvec2 c);
double orbit_trap(int max_iterations, dvec2 c);
float distance_to_line(vec2 pt1, vec2 pt2, vec2 pt3);

vec4 julia(int max_iterations, dvec2 c) {
    vec4 color;

    if (use_orbit_trap == 1) {
        double distance = orbit_trap(max_iterations, c);
        color = texture(palette_texture, float(distance)).rgba;
    } else {
        int iterations = escape_time(max_iterations, c);

        if (iterations == max_iterations) {
            color = texture(palette_texture, 0.0f).rgba;
        } else {
            color = texture(palette_texture, float(iterations) / float(max_iterations)).rgba;
        }
    }
    return color;
}

int escape_time(int max_iterations, dvec2 c) {
    int iterations = 0;
    double x0 = pos.x * scale + offset.x + X_OFFSET;
    double y0 = pos.y * scale + offset.y;
    double x1 = 0.0;
    double x2 = 0.0, y2 = 0.0;

    while (x2 + y2 <= escape_radius && iterations < max_iterations) {
        x1 = x0 * x0 - y0 * y0;
        y0 = 2 * x0 * y0 + c.y;
        x0 = x1 + c.x;
        x2 = x0 * x0;
        y2 = y0 * y0;
        iterations++;
    }
    return iterations;
}

double orbit_trap(int max_iterations, dvec2 c) {
    double distance = 1e20;
    double x0 = pos.x * scale + offset.x + X_OFFSET;
    double y0 = pos.y * scale + offset.y;
    double x1 = 0.0;
    double x2 = 0.0, y2 = 0.0;

    vec2 p1 = vec2(pow(float(trappingPointOffset.x * PI), 2), 0);
    vec2 p2 = vec2(0, pow(float(trappingPointOffset.y * PI), 2));

    for (int i = 0; i < max_iterations; i++) {
        x1 = x0 * x0 - y0 * y0;
        y0 = 2 * x0 * y0 + c.y;
        x0 = x1 + c.x;
        x2 = x0 * x0;
        y2 = y0 * y0;

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
    frag_color = julia(max_iterations, seed);
}
