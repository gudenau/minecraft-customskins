package net.gudenau.minecraft.customskins.gl;

import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;

public class GlBuffer implements AutoCloseable{
    private final int id;

    public GlBuffer(){
        id = GL15.glGenBuffers();
    }

    public void bind(int target){
        GL15.glBindBuffer(target, id);
    }

    public void data(int target, ByteBuffer buffer, int usage){
        GL15.glBufferData(target, buffer, usage);
    }

    @Override
    public void close() {
        GL15.glDeleteBuffers(id);
    }
}
