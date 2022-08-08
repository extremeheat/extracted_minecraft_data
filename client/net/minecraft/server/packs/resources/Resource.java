package net.minecraft.server.packs.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;

public class Resource {
   private final String packId;
   private final IoSupplier<InputStream> streamSupplier;
   private final IoSupplier<ResourceMetadata> metadataSupplier;
   @Nullable
   private ResourceMetadata cachedMetadata;

   public Resource(String var1, IoSupplier<InputStream> var2, IoSupplier<ResourceMetadata> var3) {
      super();
      this.packId = var1;
      this.streamSupplier = var2;
      this.metadataSupplier = var3;
   }

   public Resource(String var1, IoSupplier<InputStream> var2) {
      super();
      this.packId = var1;
      this.streamSupplier = var2;
      this.metadataSupplier = () -> {
         return ResourceMetadata.EMPTY;
      };
      this.cachedMetadata = ResourceMetadata.EMPTY;
   }

   public String sourcePackId() {
      return this.packId;
   }

   public InputStream open() throws IOException {
      return (InputStream)this.streamSupplier.get();
   }

   public BufferedReader openAsReader() throws IOException {
      return new BufferedReader(new InputStreamReader(this.open(), StandardCharsets.UTF_8));
   }

   public ResourceMetadata metadata() throws IOException {
      if (this.cachedMetadata == null) {
         this.cachedMetadata = (ResourceMetadata)this.metadataSupplier.get();
      }

      return this.cachedMetadata;
   }

   @FunctionalInterface
   public interface IoSupplier<T> {
      T get() throws IOException;
   }
}
