package net.minecraft.resources;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractResourcePack implements IResourcePack {
   private static final Logger field_195772_b = LogManager.getLogger();
   protected final File field_195771_a;

   public AbstractResourcePack(File var1) {
      super();
      this.field_195771_a = var1;
   }

   private static String func_195765_c(ResourcePackType var0, ResourceLocation var1) {
      return String.format("%s/%s/%s", var0.func_198956_a(), var1.func_110624_b(), var1.func_110623_a());
   }

   protected static String func_195767_a(File var0, File var1) {
      return var0.toURI().relativize(var1.toURI()).getPath();
   }

   public InputStream func_195761_a(ResourcePackType var1, ResourceLocation var2) throws IOException {
      return this.func_195766_a(func_195765_c(var1, var2));
   }

   public boolean func_195764_b(ResourcePackType var1, ResourceLocation var2) {
      return this.func_195768_c(func_195765_c(var1, var2));
   }

   protected abstract InputStream func_195766_a(String var1) throws IOException;

   public InputStream func_195763_b(String var1) throws IOException {
      if (!var1.contains("/") && !var1.contains("\\")) {
         return this.func_195766_a(var1);
      } else {
         throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
      }
   }

   protected abstract boolean func_195768_c(String var1);

   protected void func_195769_d(String var1) {
      field_195772_b.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", var1, this.field_195771_a);
   }

   @Nullable
   public <T> T func_195760_a(IMetadataSectionSerializer<T> var1) throws IOException {
      return func_195770_a(var1, this.func_195766_a("pack.mcmeta"));
   }

   @Nullable
   public static <T> T func_195770_a(IMetadataSectionSerializer<T> var0, InputStream var1) {
      JsonObject var2;
      try {
         BufferedReader var3 = new BufferedReader(new InputStreamReader(var1, StandardCharsets.UTF_8));
         Throwable var4 = null;

         try {
            var2 = JsonUtils.func_212743_a(var3);
         } catch (Throwable var16) {
            var4 = var16;
            throw var16;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var14) {
                     var4.addSuppressed(var14);
                  }
               } else {
                  var3.close();
               }
            }

         }
      } catch (JsonParseException | IOException var18) {
         field_195772_b.error("Couldn't load {} metadata", var0.func_110483_a(), var18);
         return null;
      }

      if (!var2.has(var0.func_110483_a())) {
         return null;
      } else {
         try {
            return var0.func_195812_a(JsonUtils.func_152754_s(var2, var0.func_110483_a()));
         } catch (JsonParseException var15) {
            field_195772_b.error("Couldn't load {} metadata", var0.func_110483_a(), var15);
            return null;
         }
      }
   }

   public String func_195762_a() {
      return this.field_195771_a.getName();
   }
}
