package net.minecraft.server.packs;

import java.io.IOException;
import java.io.InputStream;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.jspecify.annotations.Nullable;

public abstract class AbstractPackMetadataResources implements PackMetadataResources {
   private final PackLocationInfo location;
   private @Nullable ResourceMetadata metadata;

   protected AbstractPackMetadataResources(final PackLocationInfo location) {
      super();
      this.location = location;
   }

   public <T> @Nullable T getMetadataSection(final MetadataSectionType<T> metadataSerializer) throws IOException {
      if (this.metadata == null) {
         this.metadata = loadMetadata(this);
      }

      return (T)this.metadata.getSection(metadataSerializer).orElse((Object)null);
   }

   public static ResourceMetadata loadMetadata(final PackMetadataResources packResources) throws IOException {
      IoSupplier<InputStream> metadata = packResources.getRootResource("pack.mcmeta");
      if (metadata == null) {
         return ResourceMetadata.EMPTY;
      } else {
         InputStream resource = metadata.get();

         ResourceMetadata var3;
         try {
            var3 = ResourceMetadata.fromJsonStream(resource);
         } catch (Throwable var6) {
            if (resource != null) {
               try {
                  resource.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (resource != null) {
            resource.close();
         }

         return var3;
      }
   }

   public PackLocationInfo location() {
      return this.location;
   }
}
