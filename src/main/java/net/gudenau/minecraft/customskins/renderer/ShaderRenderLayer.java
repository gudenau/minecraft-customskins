package net.gudenau.minecraft.customskins.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.util.Pair;
import net.gudenau.minecraft.customskins.duck.RenderLayerDuck;
import net.gudenau.minecraft.customskins.gl.GlBuffer;
import net.gudenau.minecraft.customskins.gl.ShaderProgram;
import net.gudenau.minecraft.customskins.gl.Shaders;
import net.gudenau.minecraft.customskins.gl.ShaderTexture;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class ShaderRenderLayer extends RenderLayer{
    private final ShaderProgram shader;

    public ShaderRenderLayer(ShaderProgram shader, ShaderTexture... textures){
        super(
            shader.getName(),
            shader.getVertexFormat(),
            GL20.GL_QUADS,
            256,
            true,
            false,
            ()->shader.setupProgram(textures),
            ()->shader.teardownProgram(textures)
        );
        this.shader = shader;
    }

    @Override
    public void draw(BufferBuilder bufferBuilder, int cameraX, int cameraY, int cameraZ){
        if(bufferBuilder.isBuilding()){
            if(((RenderLayerDuck)this).isTranslucent()){
                bufferBuilder.sortQuads(cameraX, cameraY, cameraZ);
            }

            bufferBuilder.end();

            GlBuffer glBuffer = lockBuffer();
            try{
                Pair<BufferBuilder.DrawArrayParameters, ByteBuffer> pair = bufferBuilder.popData();
                BufferBuilder.DrawArrayParameters drawArrayParameters = pair.getFirst();

                ByteBuffer buffer = pair.getSecond();
                int mode = drawArrayParameters.getMode();
                VertexFormat vertexFormat = drawArrayParameters.getVertexFormat();
                int count = drawArrayParameters.getCount();

                buffer.clear();
                glBuffer.bind(GL15.GL_ARRAY_BUFFER);
                glBuffer.data(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

                startDrawing();

                shader.uploadProjectionMatrix(Shaders.PROJECTION_MATRIX);

                if(count > 0){
                    vertexFormat.startDrawing(MemoryUtil.memAddress(buffer));
                    GlStateManager.drawArrays(mode, 0, count);
                    vertexFormat.endDrawing();
                }

                endDrawing();
            }finally{
                unlockBuffer(glBuffer);
            }
        }
    }

    private static final List<GlBuffer> BUFFERS_FREE = new LinkedList<>();
    private static final List<GlBuffer> BUFFERS_USED = new LinkedList<>();
    private static final Object BUFFERS_LOCK = new Object[0];

    private static GlBuffer lockBuffer(){
        synchronized(BUFFERS_LOCK){
            GlBuffer buffer;
            if(BUFFERS_FREE.isEmpty()){
                buffer = new GlBuffer();
            }else{
                buffer = BUFFERS_FREE.remove(0);
            }
            BUFFERS_USED.add(buffer);
            return buffer;
        }
    }

    private static void unlockBuffer(GlBuffer buffer){
        synchronized(BUFFERS_LOCK){
            if(BUFFERS_USED.remove(buffer)){
                BUFFERS_FREE.add(buffer);
            }else{
                throw new RuntimeException("Buffer was not being used");
            }
        }
    }
}
