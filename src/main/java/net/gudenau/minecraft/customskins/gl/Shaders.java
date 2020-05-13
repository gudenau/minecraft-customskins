package net.gudenau.minecraft.customskins.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormats;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.KHRDebug;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

import java.io.PrintStream;

@Environment(EnvType.CLIENT)
public class Shaders {
    public static void loadShaders() {
        setupLogging();
    }

    private static void setupLogging(){
        GL20.glEnable(KHRDebug.GL_DEBUG_OUTPUT);
        KHRDebug.glDebugMessageCallback((source, type, id, severity, length, message, userParam)->{
            PrintStream stream = type == KHRDebug.GL_DEBUG_TYPE_ERROR ? System.err : System.out;
            stream.println(MemoryUtil.memASCII(message, length));
        }, 0);
    }
}
