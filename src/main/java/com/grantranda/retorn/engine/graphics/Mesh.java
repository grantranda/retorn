package com.grantranda.retorn.engine.graphics;

import com.grantranda.retorn.engine.util.MemoryUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    public static int VBO_ATTRIB_INDEX = 0;
    public static int TBO_ATTRIB_INDEX = 1;

    private final int vaoId, vboId, tboId, iboId;
    private final int vertexCount;

    public Mesh(float[] vertices, float[] textureCoordinates, byte[] indices) {
        vertexCount = indices.length;

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, MemoryUtils.allocateFloatBuffer(vertices), GL_STATIC_DRAW);
        glVertexAttribPointer(VBO_ATTRIB_INDEX, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(VBO_ATTRIB_INDEX);

        tboId = glGenBuffers();
        if (textureCoordinates != null) {
            glBindBuffer(GL_ARRAY_BUFFER, tboId);
            glBufferData(GL_ARRAY_BUFFER, MemoryUtils.allocateFloatBuffer(textureCoordinates), GL_STATIC_DRAW);
            glVertexAttribPointer(TBO_ATTRIB_INDEX, 2, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(TBO_ATTRIB_INDEX);
        }
        
        iboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, MemoryUtils.allocateByteBuffer(indices), GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0); // Unbind IBO
        glBindBuffer(GL_ARRAY_BUFFER, 0); // Unbind VBO & TBO
        glBindVertexArray(0); // Unbind VAO
    }

    public void bind() {
        glBindVertexArray(vaoId);
        if (iboId > 0) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
        }
    }

    public void unbind() {
        if (iboId > 0) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
        }
        glBindVertexArray(0);
    }

    public void draw() {
        if (iboId > 0) {
            glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_BYTE, 0);
        } else {
            glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        }
    }

    public void render() {
        bind();
        draw();
        unbind();
    }
}
