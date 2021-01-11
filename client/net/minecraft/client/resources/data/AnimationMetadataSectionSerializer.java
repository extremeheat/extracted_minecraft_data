package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class AnimationMetadataSectionSerializer extends BaseMetadataSectionSerializer<AnimationMetadataSection> implements JsonSerializer<AnimationMetadataSection> {
   public AnimationMetadataSectionSerializer() {
      super();
   }

   public AnimationMetadataSection deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      ArrayList var4 = Lists.newArrayList();
      JsonObject var5 = JsonUtils.func_151210_l(var1, "metadata section");
      int var6 = JsonUtils.func_151208_a(var5, "frametime", 1);
      if (var6 != 1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)var6, "Invalid default frame time");
      }

      int var8;
      if (var5.has("frames")) {
         try {
            JsonArray var7 = JsonUtils.func_151214_t(var5, "frames");

            for(var8 = 0; var8 < var7.size(); ++var8) {
               JsonElement var9 = var7.get(var8);
               AnimationFrame var10 = this.func_110492_a(var8, var9);
               if (var10 != null) {
                  var4.add(var10);
               }
            }
         } catch (ClassCastException var11) {
            throw new JsonParseException("Invalid animation->frames: expected array, was " + var5.get("frames"), var11);
         }
      }

      int var12 = JsonUtils.func_151208_a(var5, "width", -1);
      var8 = JsonUtils.func_151208_a(var5, "height", -1);
      if (var12 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)var12, "Invalid width");
      }

      if (var8 != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)var8, "Invalid height");
      }

      boolean var13 = JsonUtils.func_151209_a(var5, "interpolate", false);
      return new AnimationMetadataSection(var4, var12, var8, var6, var13);
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

   public JsonElement serialize(AnimationMetadataSection var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("frametime", var1.func_110469_d());
      if (var1.func_110474_b() != -1) {
         var4.addProperty("width", var1.func_110474_b());
      }

      if (var1.func_110471_a() != -1) {
         var4.addProperty("height", var1.func_110471_a());
      }

      if (var1.func_110473_c() > 0) {
         JsonArray var5 = new JsonArray();

         for(int var6 = 0; var6 < var1.func_110473_c(); ++var6) {
            if (var1.func_110470_b(var6)) {
               JsonObject var7 = new JsonObject();
               var7.addProperty("index", var1.func_110468_c(var6));
               var7.addProperty("time", var1.func_110472_a(var6));
               var5.add(var7);
            } else {
               var5.add(new JsonPrimitive(var1.func_110468_c(var6)));
            }
         }

         var4.add("frames", var5);
      }

      return var4;
   }

   public String func_110483_a() {
      return "animation";
   }

   // $FF: synthetic method
   public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      return this.deserialize(var1, var2, var3);
   }

   // $FF: synthetic method
   public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
      return this.serialize((AnimationMetadataSection)var1, var2, var3);
   }
}
