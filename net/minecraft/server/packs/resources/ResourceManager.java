package net.minecraft.server.packs.resources;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;

public interface ResourceManager {
   Set getNamespaces();

   Resource getResource(ResourceLocation var1) throws IOException;

   boolean hasResource(ResourceLocation var1);

   List getResources(ResourceLocation var1) throws IOException;

   Collection listResources(String var1, Predicate var2);
}
