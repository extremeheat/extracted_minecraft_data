package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.JsonUtils;
import org.apache.commons.lang3.Validate;

public class SoundListSerializer implements JsonDeserializer<SoundList> {
   public SoundListSerializer() {
      super();
   }

   public SoundList deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var4 = JsonUtils.func_151210_l(var1, "entry");
      boolean var5 = JsonUtils.func_151209_a(var4, "replace", false);
      String var6 = JsonUtils.func_151219_a(var4, "subtitle", (String)null);
      List var7 = this.func_188733_a(var4);
      return new SoundList(var7, var5, var6);
   }

   private List<Sound> func_188733_a(JsonObject var1) {
      ArrayList var2 = Lists.newArrayList();
      if (var1.has("sounds")) {
         JsonArray var3 = JsonUtils.func_151214_t(var1, "sounds");

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            JsonElement var5 = var3.get(var4);
            if (JsonUtils.func_151211_a(var5)) {
               String var6 = JsonUtils.func_151206_a(var5, "sound");
               var2.add(new Sound(var6, 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 16));
            } else {
               var2.add(this.func_188734_b(JsonUtils.func_151210_l(var5, "sound")));
            }
         }
      }

      return var2;
   }

   private Sound func_188734_b(JsonObject var1) {
      String var2 = JsonUtils.func_151200_h(var1, "name");
      Sound.Type var3 = this.func_188732_a(var1, Sound.Type.FILE);
      float var4 = JsonUtils.func_151221_a(var1, "volume", 1.0F);
      Validate.isTrue(var4 > 0.0F, "Invalid volume", new Object[0]);
      float var5 = JsonUtils.func_151221_a(var1, "pitch", 1.0F);
      Validate.isTrue(var5 > 0.0F, "Invalid pitch", new Object[0]);
      int var6 = JsonUtils.func_151208_a(var1, "weight", 1);
      Validate.isTrue(var6 > 0, "Invalid weight", new Object[0]);
      boolean var7 = JsonUtils.func_151209_a(var1, "preload", false);
      boolean var8 = JsonUtils.func_151209_a(var1, "stream", false);
      int var9 = JsonUtils.func_151208_a(var1, "attenuation_distance", 16);
      return new Sound(var2, var4, var5, var6, var3, var8, var7, var9);
   }

   private Sound.Type func_188732_a(JsonObject var1, Sound.Type var2) {
      Sound.Type var3 = var2;
      if (var1.has("type")) {
         var3 = Sound.Type.func_188704_a(JsonUtils.func_151200_h(var1, "type"));
         Validate.notNull(var3, "Invalid type", new Object[0]);
      }

      return var3;
   }

   // $FF: synthetic method
   public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      return this.deserialize(var1, var2, var3);
   }
}
