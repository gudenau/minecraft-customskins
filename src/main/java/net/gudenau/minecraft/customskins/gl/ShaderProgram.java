package net.gudenau.minecraft.customskins.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.util.math.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ShaderProgram implements AutoCloseable {
    private int program;
    private final String name;
    private final VertexFormat format;

    public ShaderProgram(String name, VertexFormat format){
        this.name = name;
        this.format = format;
    }

    @Override
    public void close() throws Exception {
        GL20.glDeleteProgram(program);
    }

    public void setupProgram(){
        GL20.glUseProgram(program);
        int location = 0;
        int offset = 0;
        for(VertexFormatElement element : format.getElements()){
            GL20.glEnableVertexAttribArray(location);
            GL20.glVertexAttribPointer(
                location,
                element.getCount(),
                element.getFormat().getGlId(),
                false,
                format.getVertexSize(),
                offset
            );
            offset += element.getSize();
            location++;
        }
    }

    public void teardownProgram(){
        int location = 0;
        for(VertexFormatElement ignored : format.getElements()){
            GL20.glDisableVertexAttribArray(location);
            location++;
        }
        GL20.glUseProgram(0);
    }

    public String getName() {
        return name;
    }

    public VertexFormat getVertexFormat() {
        return format;
    }

    public void uploadUniform(String name, Matrix4f data) {
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.mallocFloat(4 * 4);
            data.writeToBuffer(buffer);
            int location = getUniform(name);
            GL20.glUniformMatrix4fv(location, false, buffer);
        }
    }

    public void load(Shader... shaders){
        program = GL20.glCreateProgram();
        for (Shader shader : shaders) {
            shader.attach(program);
        }
        GL20.glLinkProgram(program);
        String log = GL20.glGetProgramInfoLog(program);
        if(!log.isEmpty()){
            throw new RuntimeException("Failed to link program " + name + ": " + log);
        }
        for(Shader shader : shaders){
            shader.close();
        }
    }

    private Map<String, Integer> uniforms = new HashMap<>();
    public int getUniform(String name){
        return uniforms.computeIfAbsent(name, (n)->GL20.glGetUniformLocation(program, n));
    }
}
