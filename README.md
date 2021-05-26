# Retorn
Retorn is a fractal renderer built with LWJGL that can generate interactive Mandelbrot and Julia set fractals. It features a GUI with customizable rendering parameters, coloring algorithm selection (escape time and orbit trap), and a gradient editor to modify fractal colors and generate random color palettes.

 ## Images
![Julia](images/julia.png) ![Mandelbrot](images/mandelbrot.png)
<p align="center">
  <img src="/images/example_1.png" width="45%" />
  &nbsp; &nbsp; &nbsp; &nbsp;
  <img src="/images/example_2.png" width="45%" /> 
</p>

## Technology
  - [LWJGL 3](https://github.com/LWJGL/lwjgl3)
  - [LWJGUI](https://github.com/orange451/LWJGUI)
  - [Log4j](https://github.com/apache/log4j)
  - [Gson](https://github.com/google/gson)

## Running
1. Download the latest release.
2. Place the jar file and resources folder in the same directory.
3. Run the application:

```
java -jar retorn-1.0.jar
```

## Key Bindings
| Command | Key 1 | Key 2 |
| --- | --- | --- |
| Drag | LMB | |
| Zoom In | RMB | Scroll Up |
| Zoom Out | Shift + RMB | Scroll Down |
| Toggle GUI | F1 | |

## License
[![License: GPL v3](https://img.shields.io/badge/license-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
