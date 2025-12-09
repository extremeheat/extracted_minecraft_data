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
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.GsonHelper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public abstract class AbstractPackResources implements PackResources {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackLocationInfo location;

   protected AbstractPackResources(PackLocationInfo var1) {
      super();
      this.location = var1;
   }

   public <T> @Nullable T getMetadataSection(MetadataSectionType<T> var1) throws IOException {
      IoSupplier var2 = this.getRootResource(new String[]{"pack.mcmeta"});
      if (var2 == null) {
         return null;
      } else {
         InputStream var3 = (InputStream)var2.get();

         Object var4;
         try {
            var4 = getMetadataFromStream(var1, var3, this.location);
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

   public static <T> @Nullable T getMetadataFromStream(MetadataSectionType<T> var0, InputStream var1, PackLocationInfo var2) {
      JsonObject var3;
      try {
         BufferedReader var4 = new BufferedReader(new InputStreamReader(var1, StandardCharsets.UTF_8));

         try {
            var3 = GsonHelper.parse((Reader)var4);
         } catch (Throwable var8) {
            try {
               var4.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         var4.close();
      } catch (Exception var9) {
         LOGGER.error("Couldn't load {} {} metadata: {}", new Object[]{var2.id(), var0.name(), var9.getMessage()});
         return null;
      }

      return (T)(!var3.has(var0.name()) ? null : var0.codec().parse(JsonOps.INSTANCE, var3.get(var0.name())).ifError((var2x) -> LOGGER.error("Couldn't load {} {} metadata: {}", new Object[]{var2.id(), var0.name(), var2x.message()})).result().orElse((Object)null));
   }

   public PackLocationInfo location() {
      return this.location;
   }
}
