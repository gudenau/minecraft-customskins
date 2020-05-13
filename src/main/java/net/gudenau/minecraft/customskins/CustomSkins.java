package net.gudenau.minecraft.customskins;

import net.fabricmc.api.*;
import net.gudenau.minecraft.customskins.gl.Shaders;
import net.gudenau.minecraft.customskins.hooks.UnsafeHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ClientModInitializer.class)
public class CustomSkins implements ModInitializer, ClientModInitializer{
    @Environment(EnvType.CLIENT)
    public static BitSet SKIN_MASK = CommonStuff.loadMask();

    @Override
    public void onInitialize(){}

    @Environment(EnvType.CLIENT)
    @Override
    public void onInitializeClient(){
        try {
            Field URL$handlers = URL.class.getDeclaredField("handlers");
            Field URL$streamHandlerLock = URL.class.getDeclaredField("streamHandlerLock");
            @SuppressWarnings("unchecked")
            Hashtable<String, URLStreamHandler> handlers = UnsafeHelper.getStatic(URL.class, URL$handlers);
            Object streamHandlerLock = UnsafeHelper.getStatic(URL.class, URL$streamHandlerLock);

            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (streamHandlerLock){
                handlers.put("data", new UrlDataHandler());
            }
        }catch (ReflectiveOperationException e){
            e.printStackTrace();
        }
    }
}
