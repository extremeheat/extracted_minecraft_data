package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class AnimationMetadataSectionSerializer implements IMetadataSectionSerializer<AnimationMetadataSection> {
   public AnimationMetadataSectionSerializer() {
      super();
   }

   public AnimationMetadataSection func_195812_a(JsonObject var1) {
      ArrayList var2 = Lists.newArrayList();
      int var3 = JsonUtils.func_151208_a(var1, "frametime", 1);
      if (var3 != 1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)var3, "Invalid default frame time");
      }

      int var5;
      if (var1.has("frames")) {
         try {
            JsonArray var4 = JsonUtils.func_151214_t(var1, "frames");

            for(var5 = 0; var5 < var4.size(); ++var5) {
               JsonElement var6 = var4.get(var5);
               AnimationFrame var7 = this.func_110492_a(var5, var6);
               if (var7 != null) {
                  var2.add(var7);
               }
            }
         } catch (ClassCastException var8) {
            throw new JsonParseException("Invalid animation->frames: expected array, was " + var1.get("frames"), var8);
         }
      }

      int var9 = JsonUtils.func_151208_a(var1, "width", -1);
      var5 = JsonUtils.func_151208_a(var1, "height", -1);
      if (var9 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)var9, "Invalid width");
      }

      if (var5 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)var5, "Invalid height");
      }

      boolean var10 = JsonUtils.func_151209_a(var1, "interpolate", false);
      return new AnimationMetadataSection(var2, var9, var5, var3, var10);
   }

   private AnimationFrame func_110492_a(int var1, JsonElement var2) {
      if (var2.isJsonPrimitive()) {
         return new AnimationFrame(JsonUtils.func_151215_f(var2, "frames[" + var1 + "]"));
      } else if (var2.isJsonObject()) {
         JsonObject var3 = JsonUtils.func_151210_l(var2, "frames[" + var1 + "]");
         int var4 = JsonUtils.func_151208_a(var3, "time", -1);
         if (var3.has("time")) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)var4, "Invalid frame time");
         }

         int var5 = JsonUtils.func_151203_m(var3, "index");
         Validate.inclusiveBetween(0L, 2147483647L, (long)var5, "Invalid frame index");
         return new AnimationFrame(var5, var4);
      } else {
         return null;
      }
   }

   public String func_110483_a() {
      return "animation";
   }

   // $FF: synthetic method
   public Object func_195812_a(JsonObject var1) {
      return this.func_195812_a(var1);
   }
}
