package net.gudenau.minecraft.customskins.mixin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.gudenau.minecraft.customskins.duck.PlayerSkinTextureDuck;
import net.gudenau.minecraft.customskins.hooks.PlayerSkinTextureHooks;
import net.gudenau.minecraft.customskins.hooks.UnsafeHelper;
import net.gudenau.minecraft.customskins.renderer.PlayerSkinExtras;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Mixin(PlayerSkinTexture.class)
public abstract class PlayerSkinTextureMixin extends ResourceTexture implements PlayerSkinTextureDuck{
    private PlayerSkinTextureMixin(){
        super(null);
    }

    @Shadow private static void stripAlpha(NativeImage image, int x, int y, int width, int height){}

    @Shadow @Final private Runnable loadedCallback;
    private PlayerSkinExtras extras;

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void init(File cacheFile, String url, Identifier fallbackSkin, boolean convertLegacy, Runnable callback, CallbackInfo callbackInfo){
        MinecraftProfileTexture.Type type = UnsafeHelper.stealLambdaParam(callback, 1, MinecraftProfileTexture.Type.class);
        if(type == MinecraftProfileTexture.Type.SKIN){
            extras = new PlayerSkinExtras();
        }else{
            extras = null;
        }
    }

    @Inject(
        method = "onTextureLoaded",
        at = @At("HEAD")
    )
    private void onTextureLoaded(NativeImage image, CallbackInfo callbackInfo){
        if(extras != null){
            Object step = UnsafeHelper.stealLambdaParam(loadedCallback, 0, Object.class);
            PlayerListEntry entry = UnsafeHelper.stealLambdaParam(step, 0, PlayerListEntry.class);
            PlayerSkinTextureHooks.onTextureLoaded(extras, image, entry);
        }
    }

    @Redirect(
        method = "remapTexture",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/texture/PlayerSkinTexture;stripAlpha(Lnet/minecraft/client/texture/NativeImage;IIII)V"
        ),
        expect = 3
    )
    private static void remapTexture(NativeImage image, int x, int y, int x2, int y2){}

    @Inject(
        method = "remapTexture",
        at = @At("RETURN")
    )
    private static void remapTexture(NativeImage image, CallbackInfoReturnable<NativeImage> info){
        stripAlpha2(image, 8, 0, 16, 8);
        stripAlpha2(image, 25, 0, 12, 1);
        stripAlpha2(image, 24, 1, 14, 6);
        stripAlpha2(image, 0, 8, 32, 8);
        stripAlpha2(image, 4, 16, 8, 4);
        stripAlpha2(image, 4, 16, 8, 4);
        stripAlpha2(image, 20, 16, 16, 4);
        stripAlpha2(image, 44, 16, 8, 4);
        stripAlpha2(image, 0, 20, 56, 12);
        stripAlpha2(image, 20, 48, 8, 4);
        stripAlpha2(image, 36, 48, 8, 4);
        stripAlpha2(image, 16, 52, 32, 12);
    }

    private static void stripAlpha2(NativeImage image, int x, int y, int width, int height){
        stripAlpha(image, x, y, x + width, y + height);
    }

    @Override
    public PlayerSkinExtras getExtras(){
        return extras;
    }
}
