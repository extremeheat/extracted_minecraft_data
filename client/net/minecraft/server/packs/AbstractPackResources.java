package net.minecraft.server.packs;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.server.packs.metadata.MetadataSectionType;
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
   public <T> T getMetadataSection(MetadataSectionType<T> var1) throws IOException {
      IoSupplier var2 = this.getRootResource(new String[]{"pack.mcmeta"});
      if (var2 == null) {
         return null;
      } else {
         InputStream var3 = (InputStream)var2.get();

         Object var4;
         try {
            var4 = getMetadataFromStream(var1, var3);
         } catch (Throwable var7) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var3 != null) {
            var3.close();
         }

         return (T)var4;
      }
   }

   @Nullable
   public static <T> T getMetadataFromStream(MetadataSectionType<T> var0, InputStream var1) {
      JsonObject var2;
      try {
         BufferedReader var3 = new BufferedReader(new InputStreamReader(var1, StandardCharsets.UTF_8));

         try {
            var2 = GsonHelper.parse((Reader)var3);
         } catch (Throwable var7) {
            try {
               var3.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         var3.close();
      } catch (Exception var8) {
         LOGGER.error("Couldn't load {} metadata", var0.name(), var8);
         return null;
      }

      return (T)(!var2.has(var0.name()) ? null : var0.codec().parse(JsonOps.INSTANCE, var2.get(var0.name())).ifError((var1x) -> LOGGER.error("Couldn't load {} metadata: {}", var0.name(), var1x)).result().orElse((Object)null));
   }

   public PackLocationInfo location() {
      return this.location;
   }
}
