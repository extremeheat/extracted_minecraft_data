package net.minecraft.client.renderer.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class Variant {
   private final ResourceLocation field_188050_a;
   private final ModelRotation field_188051_b;
   private final boolean field_188052_c;
   private final int field_188053_d;

   public Variant(ResourceLocation var1, ModelRotation var2, boolean var3, int var4) {
      super();
      this.field_188050_a = var1;
      this.field_188051_b = var2;
      this.field_188052_c = var3;
      this.field_188053_d = var4;
   }

   public ResourceLocation func_188046_a() {
      return this.field_188050_a;
   }

   public ModelRotation func_188048_b() {
      return this.field_188051_b;
   }

   public boolean func_188049_c() {
      return this.field_188052_c;
   }

   public int func_188047_d() {
      return this.field_188053_d;
   }

   public String toString() {
      return "Variant{modelLocation=" + this.field_188050_a + ", rotation=" + this.field_188051_b + ", uvLock=" + this.field_188052_c + ", weight=" + this.field_188053_d + '}';
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Variant)) {
         return false;
      } else {
         Variant var2 = (Variant)var1;
         return this.field_188050_a.equals(var2.field_188050_a) && this.field_188051_b == var2.field_188051_b && this.field_188052_c == var2.field_188052_c && this.field_188053_d == var2.field_188053_d;
      }
   }

   public int hashCode() {
      int var1 = this.field_188050_a.hashCode();
      var1 = 31 * var1 + this.field_188051_b.hashCode();
      var1 = 31 * var1 + Boolean.valueOf(this.field_188052_c).hashCode();
      var1 = 31 * var1 + this.field_188053_d;
      return var1;
   }

   public static class Deserializer implements JsonDeserializer<Variant> {
      public Deserializer() {
         super();
      }

      public Variant deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         ResourceLocation var5 = this.func_188043_b(var4);
         ModelRotation var6 = this.func_188042_a(var4);
         boolean var7 = this.func_188044_d(var4);
         int var8 = this.func_188045_c(var4);
         return new Variant(var5, var6, var7, var8);
      }

      private boolean func_188044_d(JsonObject var1) {
         return JsonUtils.func_151209_a(var1, "uvlock", false);
      }

      protected ModelRotation func_188042_a(JsonObject var1) {
         int var2 = JsonUtils.func_151208_a(var1, "x", 0);
         int var3 = JsonUtils.func_151208_a(var1, "y", 0);
         ModelRotation var4 = ModelRotation.func_177524_a(var2, var3);
         if (var4 == null) {
            throw new JsonParseException("Invalid BlockModelRotation x: " + var2 + ", y: " + var3);
         } else {
            return var4;
         }
      }

      protected ResourceLocation func_188043_b(JsonObject var1) {
         return new ResourceLocation(JsonUtils.func_151200_h(var1, "model"));
      }

      protected int func_188045_c(JsonObject var1) {
         int var2 = JsonUtils.func_151208_a(var1, "weight", 1);
         if (var2 < 1) {
            throw new JsonParseException("Invalid weight " + var2 + " found, expected integer >= 1");
         } else {
            return var2;
         }
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
