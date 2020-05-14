package net.gudenau.minecraft.customskins;

import net.fabricmc.api.*;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.gudenau.minecraft.customskins.gl.Shaders;
import net.gudenau.minecraft.customskins.hooks.UnsafeHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@EnvironmentInterface(value = EnvType.CLIENT, itf = ClientModInitializer.class)
public class CustomSkins implements ModInitializer, ClientModInitializer{
    @Environment(EnvType.CLIENT)
    public static final String MOD_ID = "gud_customskins";

    @Environment(EnvType.CLIENT)
    public static BitSet SKIN_MASK = CommonStuff.loadMask();

    @Override
    public void onInitialize(){}

    @Environment(EnvType.CLIENT)
    @Override
    public void onInitializeClient(){
        //FIXME Make this less of a hack
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

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener(){
            private final Identifier identifier = new Identifier(MOD_ID, "shaders");

            @Override
            public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor){
                return CompletableFuture.supplyAsync(()->{
                    Shaders.loadShaders(manager);
                    return null;
                }, applyExecutor).thenCompose((v)->synchronizer.whenPrepared(null));
            }

            @Override
            public Identifier getFabricId(){
                return identifier;
            }
        });
    }
}
