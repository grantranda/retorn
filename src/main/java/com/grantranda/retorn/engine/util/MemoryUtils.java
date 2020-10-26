package com.grantranda.retorn.engine.util;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class MemoryUtils {

    private MemoryUtils() {

    }

    public static ByteBuffer allocateByteBuffer(byte[] array) {
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(array.length);
        byteBuffer.put(array).flip();
        return byteBuffer;
    }

    public static FloatBuffer allocateFloatBuffer(float[] array) {
        FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(array.length);
        floatBuffer.put(array).flip();
        return floatBuffer;
    }
}
