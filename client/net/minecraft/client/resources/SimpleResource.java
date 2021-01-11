package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class SimpleResource implements IResource {
   private final Map<String, IMetadataSection> field_110535_a = Maps.newHashMap();
   private final String field_177242_b;
   private final ResourceLocation field_110533_b;
   private final InputStream field_110534_c;
   private final InputStream field_110531_d;
   private final IMetadataSerializer field_110532_e;
   private boolean field_110529_f;
   private JsonObject field_110530_g;

   public SimpleResource(String var1, ResourceLocation var2, InputStream var3, InputStream var4, IMetadataSerializer var5) {
      super();
      this.field_177242_b = var1;
      this.field_110533_b = var2;
      this.field_110534_c = var3;
      this.field_110531_d = var4;
      this.field_110532_e = var5;
   }

   public ResourceLocation func_177241_a() {
      return this.field_110533_b;
   }

   public InputStream func_110527_b() {
      return this.field_110534_c;
   }

   public boolean func_110528_c() {
      return this.field_110531_d != null;
   }

   public <T extends IMetadataSection> T func_110526_a(String var1) {
      if (!this.func_110528_c()) {
         return null;
      } else {
         if (this.field_110530_g == null && !this.field_110529_f) {
            this.field_110529_f = true;
            BufferedReader var2 = null;

            try {
               var2 = new BufferedReader(new InputStreamReader(this.field_110531_d));
               this.field_110530_g = (new JsonParser()).parse(var2).getAsJsonObject();
            } finally {
               IOUtils.closeQuietly(var2);
            }
         }

         IMetadataSection var6 = (IMetadataSection)this.field_110535_a.get(var1);
         if (var6 == null) {
            var6 = this.field_110532_e.func_110503_a(var1, this.field_110530_g);
         }

         return var6;
      }
   }

   public String func_177240_d() {
      return this.field_177242_b;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SimpleResource)) {
         return false;
      } else {
         SimpleResource var2 = (SimpleResource)var1;
         if (this.field_110533_b != null) {
            if (!this.field_110533_b.equals(var2.field_110533_b)) {
               return false;
            }
         } else if (var2.field_110533_b != null) {
            return false;
         }

         if (this.field_177242_b != null) {
            if (!this.field_177242_b.equals(var2.field_177242_b)) {
               return false;
            }
         } else if (var2.field_177242_b != null) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int var1 = this.field_177242_b != null ? this.field_177242_b.hashCode() : 0;
      var1 = 31 * var1 + (this.field_110533_b != null ? this.field_110533_b.hashCode() : 0);
      return var1;
   }
}
