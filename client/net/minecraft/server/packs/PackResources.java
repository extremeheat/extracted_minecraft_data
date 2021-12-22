package net.minecraft.server.packs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public interface PackResources extends AutoCloseable {
   String METADATA_EXTENSION = ".mcmeta";
   String PACK_META = "pack.mcmeta";

   @Nullable
   InputStream getRootResource(String var1) throws IOException;

   InputStream getResource(PackType var1, ResourceLocation var2) throws IOException;

   Collection<ResourceLocation> getResources(PackType var1, String var2, String var3, int var4, Predicate<String> var5);

   boolean hasResource(PackType var1, ResourceLocation var2);

   Set<String> getNamespaces(PackType var1);

   @Nullable
   <T> T getMetadataSection(MetadataSectionSerializer<T> var1) throws IOException;

   String getName();

   void close();
}
