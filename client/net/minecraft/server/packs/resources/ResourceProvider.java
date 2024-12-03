package net.minecraft.server.packs.resources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface ResourceProvider {
   ResourceProvider EMPTY = (var0) -> Optional.empty();

   Optional<Resource> getResource(ResourceLocation var1);

   default Resource getResourceOrThrow(ResourceLocation var1) throws FileNotFoundException {
      return (Resource)this.getResource(var1).orElseThrow(() -> new FileNotFoundException(var1.toString()));
   }

   default InputStream open(ResourceLocation var1) throws IOException {
      return this.getResourceOrThrow(var1).open();
   }

   default BufferedReader openAsReader(ResourceLocation var1) throws IOException {
      return this.getResourceOrThrow(var1).openAsReader();
   }

   static ResourceProvider fromMap(Map<ResourceLocation, Resource> var0) {
      return (var1) -> Optional.ofNullable((Resource)var0.get(var1));
   }
}
