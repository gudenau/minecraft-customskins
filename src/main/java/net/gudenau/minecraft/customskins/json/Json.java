package net.gudenau.minecraft.customskins.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.Reader;

@Environment(EnvType.CLIENT)
public class Json{
    private static final Gson GSON = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create();

    public static <T> T fromJson(Reader reader, Class<T> type){
        return GSON.fromJson(reader, type);
    }
}
