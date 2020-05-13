package net.gudenau.minecraft.customskins.hooks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class UnsafeHelper{
    private static final Class<?> Unsafe = findUnsafe();
    private static final Object UNSAFE = getUnsafe();
    private static final MethodHandle Unsafe$objectFieldOffset = findMethod("objectFieldOffset", long.class, Field.class);
    private static final MethodHandle Unsafe$getObject = findMethod("getObject", Object.class, Object.class, long.class);
    private static final MethodHandle Unsafe$staticFieldOffset = findMethod("staticFieldOffset", long.class, Field.class);

    private static Class<?> findUnsafe(){
        try{
            return UnsafeHelper.class.getClassLoader().loadClass("sun.misc.Unsafe");
        }catch(ClassNotFoundException e){
            throw new RuntimeException("Failed to load Unsafe", e);
        }
    }

    private static Object getUnsafe(){
        for(Field field : Unsafe.getDeclaredFields()){
            if(field.getType() == Unsafe && Modifier.isStatic(field.getModifiers())){
                try{
                    field.setAccessible(true);
                    return field.get(null);
                }catch(ReflectiveOperationException e){
                    throw new RuntimeException("Failed to get Unsafe handle", e);
                }
            }
        }
        throw new RuntimeException("Failed to find Unsafe handle");
    }

    private static MethodHandle findMethod(String name, Class<?> returnType, Class<?>... params){
        Method foundMethod = null;
        for(Method method : Unsafe.getDeclaredMethods()){
            if(method.getName().equals(name) && method.getReturnType() == returnType && Arrays.equals(method.getParameterTypes(), params)){
                foundMethod = method;
                break;
            }
        }
        if(foundMethod == null){
            throw new RuntimeException("Failed to find Unsafe." + name);
        }
        foundMethod.setAccessible(true);
        try{
            return MethodHandles.lookup()
                .in(Unsafe)
                .unreflect(foundMethod)
                .bindTo(UNSAFE);
        }catch(IllegalAccessException e){
            throw new RuntimeException("Failed to make Unsafe." + name + " into a MethodHandle");
        }
    }

    public static long objectFieldOffset(Field field){
        try{
            return (long)Unsafe$objectFieldOffset.invokeExact(field);
        }catch(Throwable throwable){
            throw new RuntimeException(throwable);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObject(Object object, long cookie){
        try{
            return (T)Unsafe$getObject.invokeExact(object, cookie);
        }catch(Throwable throwable){
            throw new RuntimeException(throwable);
        }
    }

    public static long staticFieldOffset(Field field){
        try{
            return (long)Unsafe$staticFieldOffset.invokeExact(field);
        }catch(Throwable throwable){
            throw new RuntimeException(throwable);
        }
    }

    private static final Map<Class<?>, LambdaCache> lambdaCache = new HashMap<>();
    public static <T> T stealLambdaParam(Object lambda, int index, Class<T> type) {
        if(lambda == null){
            return null;
        }
        return lambdaCache.computeIfAbsent(lambda.getClass(), LambdaCache::new).stealParam(lambda, index, type);
    }

    public static <T> T getStatic(Class<?> owner, Field field) {
        long cookie = staticFieldOffset(field);
        return getObject(owner, cookie);
    }

    private static final class LambdaCache{
        private final long[] cookies;

        private LambdaCache(Class<?> victim){
            Field[] fields = victim.getDeclaredFields();
            cookies = new long[fields.length];

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                cookies[i] = objectFieldOffset(field);
            }
        }

        @SuppressWarnings("unchecked")
        private <T> T stealParam(Object lambda, int index, Class<T> type){
            if(index >= cookies.length){
                return null;
            }
            Object object = getObject(lambda, cookies[index]);
            if(type.isAssignableFrom(object.getClass())){
                return (T)object;
            }else{
                return null;
            }
        }
    }
}
