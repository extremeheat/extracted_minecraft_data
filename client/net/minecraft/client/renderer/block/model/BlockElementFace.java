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

   public BlockElementFace {
      super();
   }

   public static float getU(final UVs uvs, final Quadrant rotation, final int vertex) {
      return uvs.getVertexU(rotation.rotateVertexIndex(vertex)) / 16.0F;
   }

   public static float getV(final UVs uvs, final Quadrant rotation, final int index) {
      return uvs.getVertexV(rotation.rotateVertexIndex(index)) / 16.0F;
   }

   public static record UVs(float minU, float minV, float maxU, float maxV) {
      public UVs {
         super();
      }

      public float getVertexU(final int index) {
         return index != 0 && index != 1 ? this.maxU : this.minU;
      }

      public float getVertexV(final int index) {
         return index != 0 && index != 3 ? this.maxV : this.minV;
      }
   }

   protected static class Deserializer implements JsonDeserializer<BlockElementFace> {
      private static final int DEFAULT_TINT_INDEX = -1;
      private static final int DEFAULT_ROTATION = 0;

      protected Deserializer() {
         super();
      }

      public BlockElementFace deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
         JsonObject object = json.getAsJsonObject();
         Direction cullDirection = getCullFacing(object);
         int tintIndex = getTintIndex(object);
         String texture = getTexture(object);
         UVs uvs = getUVs(object);
         Quadrant rotation = getRotation(object);
         return new BlockElementFace(cullDirection, tintIndex, texture, uvs, rotation);
      }

      private static int getTintIndex(final JsonObject object) {
         return GsonHelper.getAsInt(object, "tintindex", -1);
      }

      private static String getTexture(final JsonObject object) {
         return GsonHelper.getAsString(object, "texture");
      }

      private static @Nullable Direction getCullFacing(final JsonObject object) {
         String cullFace = GsonHelper.getAsString(object, "cullface", "");
         return Direction.byName(cullFace);
      }

      private static Quadrant getRotation(final JsonObject object) {
         int rotation = GsonHelper.getAsInt(object, "rotation", 0);
         return Quadrant.parseJson(rotation);
      }

      private static @Nullable UVs getUVs(final JsonObject object) {
         if (!object.has("uv")) {
            return null;
         } else {
            JsonArray uvArray = GsonHelper.getAsJsonArray(object, "uv");
            if (uvArray.size() != 4) {
               throw new JsonParseException("Expected 4 uv values, found: " + uvArray.size());
            } else {
               float minU = GsonHelper.convertToFloat(uvArray.get(0), "minU");
               float minV = GsonHelper.convertToFloat(uvArray.get(1), "minV");
               float maxU = GsonHelper.convertToFloat(uvArray.get(2), "maxU");
               float maxV = GsonHelper.convertToFloat(uvArray.get(3), "maxV");
               return new UVs(minU, minV, maxU, maxV);
            }
         }
      }
   }
}
