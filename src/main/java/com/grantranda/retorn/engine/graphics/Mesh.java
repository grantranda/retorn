package com.grantranda.retorn.engine.graphics;

import com.grantranda.retorn.engine.util.MemoryUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    public static int VBO_ATTRIB_INDEX = 0;
    public static int TBO_ATTRIB_INDEX = 1;

    private final int VAO, VBO, TBO, IBO;
    private int VERTEX_COUNT;

    public Mesh(float[] vertices, float[] textureCoordinates, byte[] indices) {
        VERTEX_COUNT = indices.length;

        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, MemoryUtils.allocateFloatBuffer(vertices), GL_STATIC_DRAW);
        glVertexAttribPointer(VBO_ATTRIB_INDEX, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(VBO_ATTRIB_INDEX);

        TBO_ID = glGenBuffers();
        if (textureCoordinates != null) {
            TBO = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, TBO);
            glBufferData(GL_ARRAY_BUFFER, MemoryUtils.allocateFloatBuffer(textureCoordinates), GL_STATIC_DRAW);
            glVertexAttribPointer(TBO_ATTRIB_INDEX, 2, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(TBO_ATTRIB_INDEX);
        } else {
            TBO = 0;
        }
        
        if (indices != null) {
            IBO = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, IBO);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, MemoryUtils.allocateByteBuffer(indices), GL_STATIC_DRAW);
        } else {
            IBO = 0;
        }

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0); // Unbind IBO
        glBindBuffer(GL_ARRAY_BUFFER, 0); // Unbind VBO & TBO
        glBindVertexArray(0); // Unbind VAO
    }

    public int getVAO() {
        return VAO;
    }

    public int getVBO() {
        return VBO;
    }

    public int getTBO() {
        return TBO;
    }

    public int getIBO() {
        return IBO;
    }

    public int getVertexCount() {
        return VERTEX_COUNT;
    }

    public void setVertexCount(int vertexCount) {
        this.VERTEX_COUNT = vertexCount;
    }

    public void bind() {
        glBindVertexArray(VAO);
        if (IBO > 0) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, IBO);
        }
    }

    public void unbind() {
        if (IBO > 0) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, IBO);
        }
        glBindVertexArray(0);
    }

    public void draw() {
        if (IBO > 0) {
            glDrawElements(GL_TRIANGLES, VERTEX_COUNT, GL_UNSIGNED_BYTE, 0);
        } else {
            glDrawArrays(GL_TRIANGLES, 0, VERTEX_COUNT);
        }
    }

    public void render() {
        bind();
        draw();
        unbind();
    }
}
