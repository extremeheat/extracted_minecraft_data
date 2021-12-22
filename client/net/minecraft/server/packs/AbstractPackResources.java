package net.minecraft.server.packs;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractPackResources implements PackResources {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final File file;

   public AbstractPackResources(File var1) {
      super();
      this.file = var1;
   }

   private static String getPathFromLocation(PackType var0, ResourceLocation var1) {
      return String.format("%s/%s/%s", var0.getDirectory(), var1.getNamespace(), var1.getPath());
   }

   protected static String getRelativePath(File var0, File var1) {
      return var0.toURI().relativize(var1.toURI()).getPath();
   }

   public InputStream getResource(PackType var1, ResourceLocation var2) throws IOException {
      return this.getResource(getPathFromLocation(var1, var2));
   }

   public boolean hasResource(PackType var1, ResourceLocation var2) {
      return this.hasResource(getPathFromLocation(var1, var2));
   }

   protected abstract InputStream getResource(String var1) throws IOException;

   public InputStream getRootResource(String var1) throws IOException {
      if (!var1.contains("/") && !var1.contains("\\")) {
         return this.getResource(var1);
      } else {
         throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
      }
   }

   protected abstract boolean hasResource(String var1);

   protected void logWarning(String var1) {
      LOGGER.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", var1, this.file);
   }

   @Nullable
   public <T> T getMetadataSection(MetadataSectionSerializer<T> var1) throws IOException {
      InputStream var2 = this.getResource("pack.mcmeta");

      Object var3;
      try {
         var3 = getMetadataFromStream(var1, var2);
      } catch (Throwable var6) {
         if (var2 != null) {
            try {
               var2.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (var2 != null) {
         var2.close();
      }

      return var3;
   }

   @Nullable
   public static <T> T getMetadataFromStream(MetadataSectionSerializer<T> var0, InputStream var1) {
      JsonObject var2;
      try {
         BufferedReader var3 = new BufferedReader(new InputStreamReader(var1, StandardCharsets.UTF_8));

         try {
            var2 = GsonHelper.parse((Reader)var3);
         } catch (Throwable var8) {
            try {
               var3.close();
            } catch (Throwable var6) {
               var8.addSuppressed(var6);
            }

            throw var8;
         }

         var3.close();
      } catch (Exception var9) {
         LOGGER.error("Couldn't load {} metadata", var0.getMetadataSectionName(), var9);
         return null;
      }

      if (!var2.has(var0.getMetadataSectionName())) {
         return null;
      } else {
         try {
            return var0.fromJson(GsonHelper.getAsJsonObject(var2, var0.getMetadataSectionName()));
         } catch (Exception var7) {
            LOGGER.error("Couldn't load {} metadata", var0.getMetadataSectionName(), var7);
            return null;
         }
      }
   }

   public String getName() {
      return this.file.getName();
   }
}
