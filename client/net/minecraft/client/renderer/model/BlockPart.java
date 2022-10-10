package net.minecraft.client.renderer.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.math.MathHelper;

public class BlockPart {
   public final Vector3f field_178241_a;
   public final Vector3f field_178239_b;
   public final Map<EnumFacing, BlockPartFace> field_178240_c;
   public final BlockPartRotation field_178237_d;
   public final boolean field_178238_e;

   public BlockPart(Vector3f var1, Vector3f var2, Map<EnumFacing, BlockPartFace> var3, @Nullable BlockPartRotation var4, boolean var5) {
      super();
      this.field_178241_a = var1;
      this.field_178239_b = var2;
      this.field_178240_c = var3;
      this.field_178237_d = var4;
      this.field_178238_e = var5;
      this.func_178235_a();
   }

   private void func_178235_a() {
      Iterator var1 = this.field_178240_c.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         float[] var3 = this.func_178236_a((EnumFacing)var2.getKey());
         ((BlockPartFace)var2.getValue()).field_178243_e.func_178349_a(var3);
      }

   }

   private float[] func_178236_a(EnumFacing var1) {
      switch(var1) {
      case DOWN:
         return new float[]{this.field_178241_a.func_195899_a(), 16.0F - this.field_178239_b.func_195902_c(), this.field_178239_b.func_195899_a(), 16.0F - this.field_178241_a.func_195902_c()};
      case UP:
         return new float[]{this.field_178241_a.func_195899_a(), this.field_178241_a.func_195902_c(), this.field_178239_b.func_195899_a(), this.field_178239_b.func_195902_c()};
      case NORTH:
      default:
         return new float[]{16.0F - this.field_178239_b.func_195899_a(), 16.0F - this.field_178239_b.func_195900_b(), 16.0F - this.field_178241_a.func_195899_a(), 16.0F - this.field_178241_a.func_195900_b()};
      case SOUTH:
         return new float[]{this.field_178241_a.func_195899_a(), 16.0F - this.field_178239_b.func_195900_b(), this.field_178239_b.func_195899_a(), 16.0F - this.field_178241_a.func_195900_b()};
      case WEST:
         return new float[]{this.field_178241_a.func_195902_c(), 16.0F - this.field_178239_b.func_195900_b(), this.field_178239_b.func_195902_c(), 16.0F - this.field_178241_a.func_195900_b()};
      case EAST:
         return new float[]{16.0F - this.field_178239_b.func_195902_c(), 16.0F - this.field_178239_b.func_195900_b(), 16.0F - this.field_178241_a.func_195902_c(), 16.0F - this.field_178241_a.func_195900_b()};
      }
   }

   static class Deserializer implements JsonDeserializer<BlockPart> {
      Deserializer() {
         super();
      }

      public BlockPart deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         Vector3f var5 = this.func_199330_e(var4);
         Vector3f var6 = this.func_199329_d(var4);
         BlockPartRotation var7 = this.func_178256_a(var4);
         Map var8 = this.func_178250_a(var3, var4);
         if (var4.has("shade") && !JsonUtils.func_180199_c(var4, "shade")) {
            throw new JsonParseException("Expected shade to be a Boolean");
         } else {
            boolean var9 = JsonUtils.func_151209_a(var4, "shade", true);
            return new BlockPart(var5, var6, var8, var7, var9);
         }
      }

      @Nullable
      private BlockPartRotation func_178256_a(JsonObject var1) {
         BlockPartRotation var2 = null;
         if (var1.has("rotation")) {
            JsonObject var3 = JsonUtils.func_152754_s(var1, "rotation");
            Vector3f var4 = this.func_199328_a(var3, "origin");
            var4.func_195898_a(0.0625F);
            EnumFacing.Axis var5 = this.func_178252_c(var3);
            float var6 = this.func_178255_b(var3);
            boolean var7 = JsonUtils.func_151209_a(var3, "rescale", false);
            var2 = new BlockPartRotation(var4, var5, var6, var7);
         }

         return var2;
      }

      private float func_178255_b(JsonObject var1) {
         float var2 = JsonUtils.func_151217_k(var1, "angle");
         if (var2 != 0.0F && MathHelper.func_76135_e(var2) != 22.5F && MathHelper.func_76135_e(var2) != 45.0F) {
            throw new JsonParseException("Invalid rotation " + var2 + " found, only -45/-22.5/0/22.5/45 allowed");
         } else {
            return var2;
         }
      }

      private EnumFacing.Axis func_178252_c(JsonObject var1) {
         String var2 = JsonUtils.func_151200_h(var1, "axis");
         EnumFacing.Axis var3 = EnumFacing.Axis.func_176717_a(var2.toLowerCase(Locale.ROOT));
         if (var3 == null) {
            throw new JsonParseException("Invalid rotation axis: " + var2);
         } else {
            return var3;
         }
      }

      private Map<EnumFacing, BlockPartFace> func_178250_a(JsonDeserializationContext var1, JsonObject var2) {
         Map var3 = this.func_178253_b(var1, var2);
         if (var3.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return var3;
         }
      }

      private Map<EnumFacing, BlockPartFace> func_178253_b(JsonDeserializationContext var1, JsonObject var2) {
         EnumMap var3 = Maps.newEnumMap(EnumFacing.class);
         JsonObject var4 = JsonUtils.func_152754_s(var2, "faces");
         Iterator var5 = var4.entrySet().iterator();

         while(var5.hasNext()) {
            Entry var6 = (Entry)var5.next();
            EnumFacing var7 = this.func_178248_a((String)var6.getKey());
            var3.put(var7, var1.deserialize((JsonElement)var6.getValue(), BlockPartFace.class));
         }

         return var3;
      }

      private EnumFacing func_178248_a(String var1) {
         EnumFacing var2 = EnumFacing.func_176739_a(var1);
         if (var2 == null) {
            throw new JsonParseException("Unknown facing: " + var1);
         } else {
            return var2;
         }
      }

      private Vector3f func_199329_d(JsonObject var1) {
         Vector3f var2 = this.func_199328_a(var1, "to");
         if (var2.func_195899_a() >= -16.0F && var2.func_195900_b() >= -16.0F && var2.func_195902_c() >= -16.0F && var2.func_195899_a() <= 32.0F && var2.func_195900_b() <= 32.0F && var2.func_195902_c() <= 32.0F) {
            return var2;
         } else {
            throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + var2);
         }
      }

      private Vector3f func_199330_e(JsonObject var1) {
         Vector3f var2 = this.func_199328_a(var1, "from");
         if (var2.func_195899_a() >= -16.0F && var2.func_195900_b() >= -16.0F && var2.func_195902_c() >= -16.0F && var2.func_195899_a() <= 32.0F && var2.func_195900_b() <= 32.0F && var2.func_195902_c() <= 32.0F) {
            return var2;
         } else {
            throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + var2);
         }
      }

      private Vector3f func_199328_a(JsonObject var1, String var2) {
         JsonArray var3 = JsonUtils.func_151214_t(var1, var2);
         if (var3.size() != 3) {
            throw new JsonParseException("Expected 3 " + var2 + " values, found: " + var3.size());
         } else {
            float[] var4 = new float[3];

            for(int var5 = 0; var5 < var4.length; ++var5) {
               var4[var5] = JsonUtils.func_151220_d(var3.get(var5), var2 + "[" + var5 + "]");
            }

            return new Vector3f(var4[0], var4[1], var4[2]);
         }
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
