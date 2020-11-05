package net.minecraft.server.packs.resources;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;

public class SimpleResource implements Resource {
   private final String sourceName;
   private final ResourceLocation location;
   private final InputStream resourceStream;
   private final InputStream metadataStream;
   private boolean triedMetadata;
   private JsonObject metadata;

   public SimpleResource(String var1, ResourceLocation var2, InputStream var3, @Nullable InputStream var4) {
      super();
      this.sourceName = var1;
      this.location = var2;
      this.resourceStream = var3;
      this.metadataStream = var4;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }

   public InputStream getInputStream() {
      return this.resourceStream;
   }

   public boolean hasMetadata() {
      return this.metadataStream != null;
   }

   @Nullable
   public <T> T getMetadata(MetadataSectionSerializer<T> var1) {
      if (!this.hasMetadata()) {
         return null;
      } else {
         if (this.metadata == null && !this.triedMetadata) {
            this.triedMetadata = true;
            BufferedReader var2 = null;

            try {
               var2 = new BufferedReader(new InputStreamReader(this.metadataStream, StandardCharsets.UTF_8));
               this.metadata = GsonHelper.parse((Reader)var2);
            } finally {
               IOUtils.closeQuietly(var2);
            }
         }

         if (this.metadata == null) {
            return null;
         } else {
            String var6 = var1.getMetadataSectionName();
            return this.metadata.has(var6) ? var1.fromJson(GsonHelper.getAsJsonObject(this.metadata, var6)) : null;
         }
      }
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
