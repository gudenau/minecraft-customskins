package net.gudenau.minecraft.customskins.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL20;

import static net.gudenau.minecraft.customskins.CustomSkins.MOD_ID;

@Environment(EnvType.CLIENT)
public class Shaders {
    public static final ShaderProgram SHADER_TEST = new ShaderProgram(
        "test", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, "projection"
    );

    public static Matrix4f PROJECTION_MATRIX = new Matrix4f();

    public static void loadShaders(ResourceManager manager) {
        SHADER_TEST.load(
            new Shader(new Identifier(MOD_ID, "shaders/test.vert"), GL20.GL_VERTEX_SHADER, manager),
            new Shader(new Identifier(MOD_ID, "shaders/test.frag"), GL20.GL_FRAGMENT_SHADER, manager)
        );
    }

    public static void projectionMatrixChanged(Matrix4f projectionMatrix){
        PROJECTION_MATRIX = projectionMatrix.copy();
    }
}
