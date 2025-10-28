package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Quadrant;
import java.lang.reflect.Type;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import org.jspecify.annotations.Nullable;

public record BlockElementFace(@Nullable Direction cullForDirection, int tintIndex, String texture, @Nullable UVs uvs, Quadrant rotation) {
   public static final int NO_TINT = -1;

   public BlockElementFace(@Nullable Direction var1, int var2, String var3, @Nullable UVs var4, Quadrant var5) {
      super();
      this.cullForDirection = var1;
      this.tintIndex = var2;
      this.texture = var3;
      this.uvs = var4;
      this.rotation = var5;
   }

   public static float getU(UVs var0, Quadrant var1, int var2) {
      return var0.getVertexU(var1.rotateVertexIndex(var2)) / 16.0F;
   }

   public static float getV(UVs var0, Quadrant var1, int var2) {
      return var0.getVertexV(var1.rotateVertexIndex(var2)) / 16.0F;
   }

   public static record UVs(float minU, float minV, float maxU, float maxV) {
      public UVs(float var1, float var2, float var3, float var4) {
         super();
         this.minU = var1;
         this.minV = var2;
         this.maxU = var3;
         this.maxV = var4;
      }

      public float getVertexU(int var1) {
         return var1 != 0 && var1 != 1 ? this.maxU : this.minU;
      }

      public float getVertexV(int var1) {
         return var1 != 0 && var1 != 3 ? this.maxV : this.minV;
      }
   }

   protected static class Deserializer implements JsonDeserializer<BlockElementFace> {
      private static final int DEFAULT_TINT_INDEX = -1;
      private static final int DEFAULT_ROTATION = 0;

      protected Deserializer() {
         super();
      }

      public BlockElementFace deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         Direction var5 = getCullFacing(var4);
         int var6 = getTintIndex(var4);
         String var7 = getTexture(var4);
         UVs var8 = getUVs(var4);
         Quadrant var9 = getRotation(var4);
         return new BlockElementFace(var5, var6, var7, var8, var9);
      }

      private static int getTintIndex(JsonObject var0) {
         return GsonHelper.getAsInt(var0, "tintindex", -1);
      }

      private static String getTexture(JsonObject var0) {
         return GsonHelper.getAsString(var0, "texture");
      }

      private static @Nullable Direction getCullFacing(JsonObject var0) {
         String var1 = GsonHelper.getAsString(var0, "cullface", "");
         return Direction.byName(var1);
      }

      private static Quadrant getRotation(JsonObject var0) {
         int var1 = GsonHelper.getAsInt(var0, "rotation", 0);
         return Quadrant.parseJson(var1);
      }

      private static @Nullable UVs getUVs(JsonObject var0) {
         if (!var0.has("uv")) {
            return null;
         } else {
            JsonArray var1 = GsonHelper.getAsJsonArray(var0, "uv");
            if (var1.size() != 4) {
               throw new JsonParseException("Expected 4 uv values, found: " + var1.size());
            } else {
               float var2 = GsonHelper.convertToFloat(var1.get(0), "minU");
               float var3 = GsonHelper.convertToFloat(var1.get(1), "minV");
               float var4 = GsonHelper.convertToFloat(var1.get(2), "maxU");
               float var5 = GsonHelper.convertToFloat(var1.get(3), "maxV");
               return new UVs(var2, var3, var4, var5);
            }
         }
      }

      // $FF: synthetic method
      public Object deserialize(final JsonElement var1, final Type var2, final JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }
   }
}
