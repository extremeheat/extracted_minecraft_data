package net.minecraft.server.packs.resources;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class SimpleResource implements Resource {
   private final String sourceName;
   private final ResourceLocation location;
   private final InputStream resourceStream;
   private final InputStream metadataStream;

   public SimpleResource(String var1, ResourceLocation var2, InputStream var3, @Nullable InputStream var4) {
      super();
      this.sourceName = var1;
      this.location = var2;
      this.resourceStream = var3;
      this.metadataStream = var4;
   }

   public InputStream getInputStream() {
      return this.resourceStream;
   }

   public String getSourceName() {
      return this.sourceName;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SimpleResource)) {
         return false;
      } else {
         SimpleResource var2 = (SimpleResource)var1;
         if (this.location != null) {
            if (!this.location.equals(var2.location)) {
               return false;
            }
         } else if (var2.location != null) {
            return false;
         }

         if (this.sourceName != null) {
            if (!this.sourceName.equals(var2.sourceName)) {
               return false;
            }
         } else if (var2.sourceName != null) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int var1 = this.sourceName != null ? this.sourceName.hashCode() : 0;
      var1 = 31 * var1 + (this.location != null ? this.location.hashCode() : 0);
      return var1;
   }

   public void close() throws IOException {
      this.resourceStream.close();
      if (this.metadataStream != null) {
         this.metadataStream.close();
      }

   }
}
