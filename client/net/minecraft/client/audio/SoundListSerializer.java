package net.minecraft.client.audio;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class SoundListSerializer implements JsonDeserializer<SoundList> {
   public SoundListSerializer() {
      super();
   }

   public SoundList deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var4 = JsonUtils.func_151210_l(var1, "entry");
      SoundList var5 = new SoundList();
      var5.func_148572_a(JsonUtils.func_151209_a(var4, "replace", false));
      SoundCategory var6 = SoundCategory.func_147154_a(JsonUtils.func_151219_a(var4, "category", SoundCategory.MASTER.func_147155_a()));
      var5.func_148571_a(var6);
      Validate.notNull(var6, "Invalid category", new Object[0]);
      if (var4.has("sounds")) {
         JsonArray var7 = JsonUtils.func_151214_t(var4, "sounds");

         for(int var8 = 0; var8 < var7.size(); ++var8) {
            JsonElement var9 = var7.get(var8);
            SoundList.SoundEntry var10 = new SoundList.SoundEntry();
            if (JsonUtils.func_151211_a(var9)) {
               var10.func_148561_a(JsonUtils.func_151206_a(var9, "sound"));
            } else {
               JsonObject var11 = JsonUtils.func_151210_l(var9, "sound");
               var10.func_148561_a(JsonUtils.func_151200_h(var11, "name"));
               if (var11.has("type")) {
                  SoundList.SoundEntry.Type var12 = SoundList.SoundEntry.Type.func_148580_a(JsonUtils.func_151200_h(var11, "type"));
                  Validate.notNull(var12, "Invalid type", new Object[0]);
                  var10.func_148562_a(var12);
               }

               float var13;
               if (var11.has("volume")) {
                  var13 = JsonUtils.func_151217_k(var11, "volume");
                  Validate.isTrue(var13 > 0.0F, "Invalid volume", new Object[0]);
                  var10.func_148553_a(var13);
               }

               if (var11.has("pitch")) {
                  var13 = JsonUtils.func_151217_k(var11, "pitch");
                  Validate.isTrue(var13 > 0.0F, "Invalid pitch", new Object[0]);
                  var10.func_148559_b(var13);
               }

               if (var11.has("weight")) {
                  int var14 = JsonUtils.func_151203_m(var11, "weight");
                  Validate.isTrue(var14 > 0, "Invalid weight", new Object[0]);
                  var10.func_148554_a(var14);
               }

               if (var11.has("stream")) {
                  var10.func_148557_a(JsonUtils.func_151212_i(var11, "stream"));
               }
            }

            var5.func_148570_a().add(var10);
         }
      }

      return var5;
   }

   // $FF: synthetic method
   public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      return this.deserialize(var1, var2, var3);
   }
}
