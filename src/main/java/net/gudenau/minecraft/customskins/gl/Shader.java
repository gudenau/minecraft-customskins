package net.gudenau.minecraft.customskins.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Environment(EnvType.CLIENT)
public class Shader implements AutoCloseable{
    private final int shader;

    public Shader(Identifier identifier, int type, ResourceManager manager){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(manager.getResource(identifier).getInputStream()))){
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
                throw new RuntimeException("Failed to load shader " + identifier + ": " + log);
            }
        }catch (IOException e){
            throw new RuntimeException("Failed to read shader " + identifier, e);
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
