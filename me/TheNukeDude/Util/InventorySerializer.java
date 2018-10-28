package me.TheNukeDude.Util;

import java.io.*;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class InventorySerializer {

    private static HashMap<String, Class<?>> nmsReflectionCache = new HashMap<>();
    private static HashMap<String, Class<?>> obReflectionCache = new HashMap<>();
    private static HashMap<Class<?>, Constructor<?>> constructorCache = new HashMap<>();

    public static ItemStack deserializeItemStack(String data){
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        ItemStack itemStack = null;
        try {
            Class<?> nbtTagCompoundClass = getNMSClass("NBTTagCompound");
            Class<?> nmsItemStackClass = getNMSClass("ItemStack");
            Object nbtTagCompound = getNMSClass("NBTCompressedStreamTools").getMethod("a", DataInputStream.class).invoke(null, dataInputStream);
            Object craftItemStack = getConstructor(nmsItemStackClass, nbtTagCompoundClass).newInstance(nbtTagCompound);
            itemStack = (ItemStack) getOBClass("inventory.CraftItemStack").getMethod("asBukkitCopy", nmsItemStackClass).invoke(null, craftItemStack);
        } catch(ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return itemStack;
    }

    public static String serializeItemStack(ItemStack item) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(outputStream);

        try {
            Class<?> nbtTagCompoundClass = getNMSClass("NBTTagCompound");
            Constructor<?> nbtTagCompoundConstructor = getConstructor(nbtTagCompoundClass);
            Object nbtTagCompound = nbtTagCompoundConstructor.newInstance();
            Object nmsItemStack = getOBClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
            getNMSClass("ItemStack").getMethod("save", nbtTagCompoundClass).invoke(nmsItemStack, nbtTagCompound);
            getNMSClass("NBTCompressedStreamTools").getMethod("a", nbtTagCompoundClass, DataOutput.class).invoke(null, nbtTagCompound, dataOutput);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        //return BaseEncoding.base64().encode(outputStream.toByteArray());
        return new BigInteger(1, outputStream.toByteArray()).toString(32);
    }

    private static Constructor<?> getConstructor(Class<?> genericClass, Class<?>... parameters){
        if(constructorCache.containsKey(genericClass))
            return constructorCache.get(genericClass);

        try {
            Constructor<?> constructor = genericClass.getConstructor(parameters);
            constructorCache.put(genericClass, constructor);
            return constructor;
        }
        catch(ReflectiveOperationException e){
            e.printStackTrace();
            return null;
        }
    }

    private static Class<?> getNMSClass(String name) {
        if(nmsReflectionCache.containsKey(name))
            return nmsReflectionCache.get(name);

        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            Class<?> requested = Class.forName("net.minecraft.server." + version + "." + name);
            nmsReflectionCache.put(name, requested);
            return requested;
        } catch (ClassNotFoundException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    private static Class<?> getOBClass(String name) {
        if(obReflectionCache.containsKey(name))
            return obReflectionCache.get(name);

        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            Class<?> requested =  Class.forName("org.bukkit.craftbukkit." + version + "." + name);
            obReflectionCache.put(name, requested);
            return requested;
        } catch (ClassNotFoundException var3) {
            var3.printStackTrace();
            return null;
        }
    }

}
