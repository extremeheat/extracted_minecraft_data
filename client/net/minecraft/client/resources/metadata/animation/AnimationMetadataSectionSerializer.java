package net.minecraft.client.resources.metadata.animation;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import javax.annotation.Nullable;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.Validate;

public class AnimationMetadataSectionSerializer implements MetadataSectionSerializer<AnimationMetadataSection> {
   public AnimationMetadataSectionSerializer() {
      super();
   }

   public AnimationMetadataSection fromJson(JsonObject var1) {
      ImmutableList.Builder var2 = ImmutableList.builder();
      int var3 = GsonHelper.getAsInt(var1, "frametime", 1);
      if (var3 != 1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)var3, "Invalid default frame time");
      }

      int var5;
      if (var1.has("frames")) {
         try {
            JsonArray var4 = GsonHelper.getAsJsonArray(var1, "frames");

            for(var5 = 0; var5 < var4.size(); ++var5) {
               JsonElement var6 = var4.get(var5);
               AnimationFrame var7 = this.getFrame(var5, var6);
               if (var7 != null) {
                  var2.add(var7);
               }
            }
         } catch (ClassCastException var8) {
            throw new JsonParseException("Invalid animation->frames: expected array, was " + String.valueOf(var1.get("frames")), var8);
         }
      }

      int var9 = GsonHelper.getAsInt(var1, "width", -1);
      var5 = GsonHelper.getAsInt(var1, "height", -1);
      if (var9 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)var9, "Invalid width");
      }

      if (var5 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)var5, "Invalid height");
      }

      boolean var10 = GsonHelper.getAsBoolean(var1, "interpolate", false);
      return new AnimationMetadataSection(var2.build(), var9, var5, var3, var10);
   }

   @Nullable
   private AnimationFrame getFrame(int var1, JsonElement var2) {
      if (var2.isJsonPrimitive()) {
         return new AnimationFrame(GsonHelper.convertToInt(var2, "frames[" + var1 + "]"));
      } else if (var2.isJsonObject()) {
         JsonObject var3 = GsonHelper.convertToJsonObject(var2, "frames[" + var1 + "]");
         int var4 = GsonHelper.getAsInt(var3, "time", -1);
         if (var3.has("time")) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)var4, "Invalid frame time");
         }

         int var5 = GsonHelper.getAsInt(var3, "index");
         Validate.inclusiveBetween(0L, 2147483647L, (long)var5, "Invalid frame index");
         return new AnimationFrame(var5, var4);
      } else {
         return null;
      }
   }

   public String getMetadataSectionName() {
      return "animation";
   }

   // $FF: synthetic method
   public Object fromJson(final JsonObject var1) {
      return this.fromJson(var1);
   }
}
