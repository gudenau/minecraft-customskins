package net.gudenau.minecraft.customskins.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureUtil;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

@Environment(EnvType.CLIENT)
public class Shader implements AutoCloseable{
    private final int shader;

    public Shader(String name, int type){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(Shader.class.getResourceAsStream("/assets/gud_customskins/shaders/" + name)))){
            StringBuilder builder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                builder.append(line).append('\n');
            }

            shader = GL20.glCreateShader(type);
            GL20.glShaderSource(shader, builder);

            GL20.glCompileShader(shader);
            String log = GL20.glGetShaderInfoLog(shader);
            if(!log.isEmpty()){
                throw new RuntimeException("Failed to load shader " + name + ": " + log);
            }
        }catch (IOException e){
            throw new RuntimeException("Failed to read shader " + name, e);
        }
    }

    public void attach(int program) {
        GL20.glAttachShader(program, shader);
    }

    @Override
    public void close() {
        GL20.glDeleteShader(shader);
    }
}
