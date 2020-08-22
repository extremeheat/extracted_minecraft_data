package net.minecraft.server.packs;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public interface Pack extends Closeable {
   InputStream getRootResource(String var1) throws IOException;

   InputStream getResource(PackType var1, ResourceLocation var2) throws IOException;

   Collection getResources(PackType var1, String var2, String var3, int var4, Predicate var5);

   boolean hasResource(PackType var1, ResourceLocation var2);

   Set getNamespaces(PackType var1);

   @Nullable
   Object getMetadataSection(MetadataSectionSerializer var1) throws IOException;

   String getName();
}
