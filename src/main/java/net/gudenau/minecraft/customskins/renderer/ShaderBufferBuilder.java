package net.gudenau.minecraft.customskins.renderer;

import com.mojang.datafixers.util.Pair;
import net.gudenau.minecraft.customskins.gl.ShaderProgram;
import net.gudenau.minecraft.customskins.mixin.BufferBuilderMixin;
import net.minecraft.client.render.*;

import java.nio.ByteBuffer;

public class ShaderBufferBuilder extends BufferBuilder{
    public ShaderBufferBuilder(ShaderProgram shader){
        super(512);
    }

    @Override
    public void vertex(
        float x, float y, float z,
        float red, float green, float blue, float alpha,
        float u, float v,
        int overlay, int light,
        float normalX, float normalY, float normalZ
    ){
        if(colorFixed){
            throw new IllegalStateException();
        }else if(((BufferBuilderMixin)this).getField_21594()){
            putFloat(0, x);
            putFloat(4, y);
            putFloat(8, z);
            putFloat(12, red);
            putFloat(16, green);
            putFloat(20, blue);
            putFloat(24, alpha);
            putFloat(28, u);
            putFloat(32, v);
            putFloat(36, (overlay & 0xFFFF) * (1 / 65536F));
            putFloat(40, (overlay >> 16 & 0xFFFF) * (1 / 65536F));
            putFloat(44, ((light & 0xFFFF) + 8) * (1 / 256F)); // 0.00390625f
            putFloat(48, ((light >>> 16) + 8) * (1 / 256F)); // (1 / 65536F)
            putByte(52, BufferVertexConsumer.method_24212(normalX));
            putByte(53, BufferVertexConsumer.method_24212(normalY));
            putByte(54, BufferVertexConsumer.method_24212(normalZ));
            ((BufferBuilderMixin)this).setElementOffset(((BufferBuilderMixin)this).getElementOffset() + 56);
            next();
        }else{
            throw new IllegalStateException();
        }
    }

    @Override
    protected void grow(){
        ((BufferBuilderMixin)this).invokeGrow(56);
    }

    @Override
    public void sortQuads(float cameraX, float cameraY, float cameraZ){
        super.sortQuads(cameraX, cameraY, cameraZ);
    }

    @Override
    public BufferBuilder.State popState(){
        return super.popState();
    }

    @Override
    public void restoreState(BufferBuilder.State state){
        super.restoreState(state);
    }

    @Override
    public void begin(int drawMode, VertexFormat format){
        super.begin(drawMode, format);
    }

    @Override
    public void end(){
        super.end();
    }

    @Override
    public void putByte(int index, byte value){
        super.putByte(index, value);
    }

    @Override
    public void putShort(int index, short value){
        super.putShort(index, value);
    }

    @Override
    public void putFloat(int index, float value){
        super.putFloat(index, value);
    }

    @Override
    public void next(){
        super.next();
    }

    @Override
    public void nextElement(){
        super.nextElement();
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha){
        return super.color(red, green, blue, alpha);
    }

    @Override
    public Pair<DrawArrayParameters, ByteBuffer> popData(){
        return super.popData();
    }

    @Override
    public void clear(){
        super.clear();
    }

    @Override
    public void reset(){
        super.reset();
    }

    @Override
    public VertexFormatElement getCurrentElement(){
        return super.getCurrentElement();
    }

    @Override
    public boolean isBuilding(){
        return super.isBuilding();
    }
}
