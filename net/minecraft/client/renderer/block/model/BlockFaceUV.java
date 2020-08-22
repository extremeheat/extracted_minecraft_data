package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;

public class BlockFaceUV {
   public float[] uvs;
   public final int rotation;

   public BlockFaceUV(@Nullable float[] var1, int var2) {
      this.uvs = var1;
      this.rotation = var2;
   }

   public float getU(int var1) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      } else {
         int var2 = this.getShiftedIndex(var1);
         return this.uvs[var2 != 0 && var2 != 1 ? 2 : 0];
      }
   }

   public float getV(int var1) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      } else {
         int var2 = this.getShiftedIndex(var1);
         return this.uvs[var2 != 0 && var2 != 3 ? 3 : 1];
      }
   }

   private int getShiftedIndex(int var1) {
      return (var1 + this.rotation / 90) % 4;
   }

   public int getReverseIndex(int var1) {
      return (var1 + 4 - this.rotation / 90) % 4;
   }

   public void setMissingUv(float[] var1) {
      if (this.uvs == null) {
         this.uvs = var1;
      }

   }

   public static class Deserializer implements JsonDeserializer {
      protected Deserializer() {
      }

      public BlockFaceUV deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         JsonObject var4 = var1.getAsJsonObject();
         float[] var5 = this.getUVs(var4);
         int var6 = this.getRotation(var4);
         return new BlockFaceUV(var5, var6);
      }

      protected int getRotation(JsonObject var1) {
         int var2 = GsonHelper.getAsInt(var1, "rotation", 0);
         if (var2 >= 0 && var2 % 90 == 0 && var2 / 90 <= 3) {
            return var2;
         } else {
            throw new JsonParseException("Invalid rotation " + var2 + " found, only 0/90/180/270 allowed");
         }
      }

      @Nullable
      private float[] getUVs(JsonObject var1) {
         if (!var1.has("uv")) {
            return null;
         } else {
            JsonArray var2 = GsonHelper.getAsJsonArray(var1, "uv");
            if (var2.size() != 4) {
               throw new JsonParseException("Expected 4 uv values, found: " + var2.size());
            } else {
               float[] var3 = new float[4];

               for(int var4 = 0; var4 < var3.length; ++var4) {
                  var3[var4] = GsonHelper.convertToFloat(var2.get(var4), "uv[" + var4 + "]");
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
