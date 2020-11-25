package com.grantranda.retorn.engine.graphics;

import com.grantranda.retorn.engine.util.MemoryUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    public static int VBO_ATTRIB_INDEX = 0;
    public static int TBO_ATTRIB_INDEX = 1;

    private final int VAO_ID, VBO_ID, TBO_ID, IBO_ID;
    private final int VERTEX_COUNT;

    public Mesh(float[] vertices, float[] textureCoordinates, byte[] indices) {
        VERTEX_COUNT = indices.length;

        VAO_ID = glGenVertexArrays();
        glBindVertexArray(VAO_ID);

        VBO_ID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO_ID);
        glBufferData(GL_ARRAY_BUFFER, MemoryUtils.allocateFloatBuffer(vertices), GL_STATIC_DRAW);
        glVertexAttribPointer(VBO_ATTRIB_INDEX, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(VBO_ATTRIB_INDEX);

        TBO_ID = glGenBuffers();
        if (textureCoordinates != null) {
            glBindBuffer(GL_ARRAY_BUFFER, TBO_ID);
            glBufferData(GL_ARRAY_BUFFER, MemoryUtils.allocateFloatBuffer(textureCoordinates), GL_STATIC_DRAW);
            glVertexAttribPointer(TBO_ATTRIB_INDEX, 2, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(TBO_ATTRIB_INDEX);
        }
        
        IBO_ID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, IBO_ID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, MemoryUtils.allocateByteBuffer(indices), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0); // Unbind IBO
        glBindBuffer(GL_ARRAY_BUFFER, 0); // Unbind VBO & TBO
        glBindVertexArray(0); // Unbind VAO
    }

    public void bind() {
        glBindVertexArray(VAO_ID);
        if (IBO_ID > 0) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, IBO_ID);
        }
    }

    public void unbind() {
        if (IBO_ID > 0) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, IBO_ID);
        }
        glBindVertexArray(0);
    }

    public void draw() {
        if (IBO_ID > 0) {
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
