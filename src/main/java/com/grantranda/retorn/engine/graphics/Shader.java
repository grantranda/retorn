package com.grantranda.retorn.engine.graphics;

import com.grantranda.retorn.app.Main;
import com.grantranda.retorn.engine.math.Matrix4f;
import com.grantranda.retorn.engine.math.Vector3f;
import com.grantranda.retorn.engine.util.FileUtils;

import static org.lwjgl.opengl.ARBGPUShaderFP64.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private final int PROGRAM_ID;

    public Shader(String vertexPath, String fragmentPath) {
        PROGRAM_ID = create(vertexPath, fragmentPath);
    }

    public void bind() {
        glUseProgram(PROGRAM_ID);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void setUniform1f(String name, float x) {
        bind();
        glUniform1f(glGetUniformLocation(PROGRAM_ID, name), x);
    }

    public void setUniform2f(String name, float x, float y) {
        bind();
        glUniform2f(glGetUniformLocation(PROGRAM_ID, name), x, y);
    }

    public void setUniform3f(String name, Vector3f vector) {
        bind();
        glUniform3f(glGetUniformLocation(PROGRAM_ID, name), vector.x, vector.y, vector.z);
    }

    public void setUniformMatrix4f(String name, Matrix4f matrix) {
        bind();
        glUniformMatrix4fv(glGetUniformLocation(PROGRAM_ID, name), false, matrix.toFloatBuffer());
    }

    public void setUniform1d(String name, double x) {
        bind();
        glUniform1d(glGetUniformLocation(PROGRAM_ID, name), x);
    }

    public void setUniform2d(String name, double x, double y) {
        bind();
        glUniform2d(glGetUniformLocation(PROGRAM_ID, name), x, y);
    }

    public void setUniform1i(String name, int x) {
        bind();
        glUniform1i(glGetUniformLocation(PROGRAM_ID, name), x);
    }

    public void setUniform2i(String name, int x, int y) {
        bind();
        glUniform2i(glGetUniformLocation(PROGRAM_ID, name), x, y);
    }

    private int create(String vertexPath, String fragmentPath) {
        String vertexSource = FileUtils.fileToString(vertexPath);
        String fragmentSource = FileUtils.fileToString(fragmentPath);
        int vertexID = 0, fragmentID = 0;
        int programID = 0;

        // Compile vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);
        if (glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE) {
            Main.logger.error("Error compiling vertex shader");
            Main.logger.info(glGetShaderInfoLog(vertexID));
        }

        // Compile fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);
        if (glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE) {
            Main.logger.error("Error compiling fragment shader");
            Main.logger.info(glGetShaderInfoLog(fragmentID));
        }

        programID = glCreateProgram();
        glAttachShader(programID, vertexID);
        glAttachShader(programID, fragmentID);

        glLinkProgram(programID);
        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            Main.logger.error("Error linking Shader");
            Main.logger.info(glGetProgramInfoLog(programID));
        }

        glValidateProgram(programID);
        if (glGetProgrami(programID, GL_VALIDATE_STATUS) == GL_FALSE) {
            Main.logger.error("Error validating Shader");
            Main.logger.info(glGetProgramInfoLog(programID));
        }

        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);

        return programID;
    }
}
