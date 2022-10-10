package net.minecraft.resources;

import java.util.Map;

public interface IPackFinder {
   <T extends ResourcePackInfo> void func_195730_a(Map<String, T> var1, ResourcePackInfo.IFactory<T> var2);
}
