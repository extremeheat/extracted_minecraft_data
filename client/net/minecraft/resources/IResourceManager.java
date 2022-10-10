package net.minecraft.resources;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;

public interface IResourceManager {
   Set<String> func_199001_a();

   IResource func_199002_a(ResourceLocation var1) throws IOException;

   List<IResource> func_199004_b(ResourceLocation var1) throws IOException;

   Collection<ResourceLocation> func_199003_a(String var1, Predicate<String> var2);
}
