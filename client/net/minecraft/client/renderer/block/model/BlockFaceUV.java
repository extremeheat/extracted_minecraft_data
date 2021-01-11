package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;

public class BlockFaceUV {
   public float[] field_178351_a;
   public final int field_178350_b;

   public BlockFaceUV(float[] var1, int var2) {
      super();
      this.field_178351_a = var1;
      this.field_178350_b = var2;
   }

   public float func_178348_a(int var1) {
      if (this.field_178351_a == null) {
         throw new NullPointerException("uvs");
      } else {
         int var2 = this.func_178347_d(var1);
         return var2 != 0 && var2 != 1 ? this.field_178351_a[2] : this.field_178351_a[0];
      }
   }

   public float func_178346_b(int var1) {
      if (this.field_178351_a == null) {
         throw new NullPointerException("uvs");
      } else {
         int var2 = this.func_178347_d(var1);
         return var2 != 0 && var2 != 3 ? this.field_178351_a[3] : this.field_178351_a[1];
      }
   }

   private int func_178347_d(int var1) {
      return (var1 + this.field_178350_b / 90) % 4;
   }

   public int func_178345_c(int var1) {
      return (var1 + (4 - this.field_178350_b / 90)) % 4;
   }

   public void func_178349_a(float[] var1) {
      if (this.field_178351_a == null) {
         this.field_178351_a = var1;
      }

   }

   static class Deserializer implements JsonDeserializer<BlockFaceUV> {
      Deserializer() {
         super();
      }

      public BlockFaceUV deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         float[] var5 = this.func_178292_b(var4);
         int var6 = this.func_178291_a(var4);
         return new BlockFaceUV(var5, var6);
      }

      protected int func_178291_a(JsonObject var1) {
         int var2 = JsonUtils.func_151208_a(var1, "rotation", 0);
         if (var2 >= 0 && var2 % 90 == 0 && var2 / 90 <= 3) {
            return var2;
         } else {
            throw new JsonParseException("Invalid rotation " + var2 + " found, only 0/90/180/270 allowed");
         }
      }

      private float[] func_178292_b(JsonObject var1) {
         if (!var1.has("uv")) {
            return null;
         } else {
            JsonArray var2 = JsonUtils.func_151214_t(var1, "uv");
            if (var2.size() != 4) {
               throw new JsonParseException("Expected 4 uv values, found: " + var2.size());
            } else {
               float[] var3 = new float[4];

               for(int var4 = 0; var4 < var3.length; ++var4) {
                  var3[var4] = JsonUtils.func_151220_d(var2.get(var4), "uv[" + var4 + "]");
               }

               return var3;
            }
         }
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
