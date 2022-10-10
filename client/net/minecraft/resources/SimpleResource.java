package net.minecraft.resources;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleResource implements IResource {
   private static final Logger field_199884_b = LogManager.getLogger();
   public static final Executor field_199031_a;
   private final String field_199032_b;
   private final ResourceLocation field_199033_c;
   private final InputStream field_199034_d;
   private final InputStream field_199035_e;
   private boolean field_199036_f;
   private JsonObject field_199037_g;

   public SimpleResource(String var1, ResourceLocation var2, InputStream var3, @Nullable InputStream var4) {
      super();
      this.field_199032_b = var1;
      this.field_199033_c = var2;
      this.field_199034_d = var3;
      this.field_199035_e = var4;
   }

   public ResourceLocation func_199029_a() {
      return this.field_199033_c;
   }

   public InputStream func_199027_b() {
      return this.field_199034_d;
   }

   public boolean func_199030_c() {
      return this.field_199035_e != null;
   }

   @Nullable
   public <T> T func_199028_a(IMetadataSectionSerializer<T> var1) {
      if (!this.func_199030_c()) {
         return null;
      } else {
         if (this.field_199037_g == null && !this.field_199036_f) {
            this.field_199036_f = true;
            BufferedReader var2 = null;

            try {
               var2 = new BufferedReader(new InputStreamReader(this.field_199035_e, StandardCharsets.UTF_8));
               this.field_199037_g = JsonUtils.func_212743_a(var2);
            } finally {
               IOUtils.closeQuietly(var2);
            }
         }

         if (this.field_199037_g == null) {
            return null;
         } else {
            String var6 = var1.func_110483_a();
            return this.field_199037_g.has(var6) ? var1.func_195812_a(JsonUtils.func_152754_s(this.field_199037_g, var6)) : null;
         }
      }
   }

   public String func_199026_d() {
      return this.field_199032_b;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SimpleResource)) {
         return false;
      } else {
         SimpleResource var2 = (SimpleResource)var1;
         if (this.field_199033_c != null) {
            if (!this.field_199033_c.equals(var2.field_199033_c)) {
               return false;
            }
         } else if (var2.field_199033_c != null) {
            return false;
         }

         if (this.field_199032_b != null) {
            if (!this.field_199032_b.equals(var2.field_199032_b)) {
               return false;
            }
         } else if (var2.field_199032_b != null) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int var1 = this.field_199032_b != null ? this.field_199032_b.hashCode() : 0;
      var1 = 31 * var1 + (this.field_199033_c != null ? this.field_199033_c.hashCode() : 0);
      return var1;
   }

   public void close() throws IOException {
      this.field_199034_d.close();
      if (this.field_199035_e != null) {
         this.field_199035_e.close();
      }

   }

   static {
      field_199031_a = Executors.newSingleThreadExecutor((new ThreadFactoryBuilder()).setDaemon(true).setNameFormat("Resource IO {0}").setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(field_199884_b)).build());
   }
}
