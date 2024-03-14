package net.minecraft.server.packs;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public abstract class AbstractPackResources implements PackResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackLocationInfo location;

   protected AbstractPackResources(PackLocationInfo var1) {
      super();
      this.location = var1;
   }

   @Nullable
   @Override
   public <T> T getMetadataSection(MetadataSectionSerializer<T> var1) throws IOException {
      IoSupplier var2 = this.getRootResource(new String[]{"pack.mcmeta"});
      if (var2 == null) {
         return null;
      } else {
         Object var4;
         try (InputStream var3 = (InputStream)var2.get()) {
            var4 = getMetadataFromStream(var1, var3);
         }

         return (T)var4;
      }
   }

   @Nullable
   public static <T> T getMetadataFromStream(MetadataSectionSerializer<T> var0, InputStream var1) {
      JsonObject var2;
      try (BufferedReader var3 = new BufferedReader(new InputStreamReader(var1, StandardCharsets.UTF_8))) {
         var2 = GsonHelper.parse(var3);
      } catch (Exception var9) {
         LOGGER.error("Couldn't load {} metadata", var0.getMetadataSectionName(), var9);
         return null;
      }

      if (!var2.has(var0.getMetadataSectionName())) {
         return null;
      } else {
         try {
            return (T)var0.fromJson(GsonHelper.getAsJsonObject(var2, var0.getMetadataSectionName()));
         } catch (Exception var7) {
            LOGGER.error("Couldn't load {} metadata", var0.getMetadataSectionName(), var7);
            return null;
         }
      }
   }

   @Override
   public PackLocationInfo location() {
      return this.location;
   }
}
