package net.minecraft.server.packs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.resources.IoSupplier;

public interface PackResources extends AutoCloseable {
   String METADATA_EXTENSION = ".mcmeta";
   String PACK_META = "pack.mcmeta";

   @Nullable
   IoSupplier<InputStream> getRootResource(String... var1);

   @Nullable
   IoSupplier<InputStream> getResource(PackType var1, ResourceLocation var2);

   void listResources(PackType var1, String var2, String var3, ResourceOutput var4);

   Set<String> getNamespaces(PackType var1);

   @Nullable
   <T> T getMetadataSection(MetadataSectionSerializer<T> var1) throws IOException;

   PackLocationInfo location();

   default String packId() {
      return this.location().id();
   }

   default Optional<KnownPack> knownPackInfo() {
      return this.location().knownPackInfo();
   }

   void close();

   @FunctionalInterface
   public interface ResourceOutput extends BiConsumer<ResourceLocation, IoSupplier<InputStream>> {
   }
}
