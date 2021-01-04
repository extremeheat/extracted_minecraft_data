package net.minecraft.server.packs.resources;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.Pack;

public interface ResourceManager {
   Set<String> getNamespaces();

   Resource getResource(ResourceLocation var1) throws IOException;

   boolean hasResource(ResourceLocation var1);

   List<Resource> getResources(ResourceLocation var1) throws IOException;

   Collection<ResourceLocation> listResources(String var1, Predicate<String> var2);

   void add(Pack var1);
}
