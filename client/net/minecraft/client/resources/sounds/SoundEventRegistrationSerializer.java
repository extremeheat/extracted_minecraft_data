package net.minecraft.client.resources.sounds;

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
import net.minecraft.util.GsonHelper;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import org.apache.commons.lang3.Validate;

public class SoundEventRegistrationSerializer implements JsonDeserializer<SoundEventRegistration> {
   private static final FloatProvider DEFAULT_FLOAT = ConstantFloat.of(1.0F);

   public SoundEventRegistrationSerializer() {
      super();
   }

   public SoundEventRegistration deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var4 = GsonHelper.convertToJsonObject(var1, "entry");
      boolean var5 = GsonHelper.getAsBoolean(var4, "replace", false);
      String var6 = GsonHelper.getAsString(var4, "subtitle", (String)null);
      List var7 = this.getSounds(var4);
      return new SoundEventRegistration(var7, var5, var6);
   }

   private List<Sound> getSounds(JsonObject var1) {
      ArrayList var2 = Lists.newArrayList();
      if (var1.has("sounds")) {
         JsonArray var3 = GsonHelper.getAsJsonArray(var1, "sounds");

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            JsonElement var5 = var3.get(var4);
            if (GsonHelper.isStringValue(var5)) {
               String var6 = GsonHelper.convertToString(var5, "sound");
               var2.add(new Sound(var6, DEFAULT_FLOAT, DEFAULT_FLOAT, 1, Sound.Type.FILE, false, false, 16));
            } else {
               var2.add(this.getSound(GsonHelper.convertToJsonObject(var5, "sound")));
            }
         }
      }

      return var2;
   }

   private Sound getSound(JsonObject var1) {
      String var2 = GsonHelper.getAsString(var1, "name");
      Sound.Type var3 = this.getType(var1, Sound.Type.FILE);
      float var4 = GsonHelper.getAsFloat(var1, "volume", 1.0F);
      Validate.isTrue(var4 > 0.0F, "Invalid volume", new Object[0]);
      float var5 = GsonHelper.getAsFloat(var1, "pitch", 1.0F);
      Validate.isTrue(var5 > 0.0F, "Invalid pitch", new Object[0]);
      int var6 = GsonHelper.getAsInt(var1, "weight", 1);
      Validate.isTrue(var6 > 0, "Invalid weight", new Object[0]);
      boolean var7 = GsonHelper.getAsBoolean(var1, "preload", false);
      boolean var8 = GsonHelper.getAsBoolean(var1, "stream", false);
      int var9 = GsonHelper.getAsInt(var1, "attenuation_distance", 16);
      return new Sound(var2, ConstantFloat.of(var4), ConstantFloat.of(var5), var6, var3, var8, var7, var9);
   }

   private Sound.Type getType(JsonObject var1, Sound.Type var2) {
      Sound.Type var3 = var2;
      if (var1.has("type")) {
         var3 = Sound.Type.getByName(GsonHelper.getAsString(var1, "type"));
         Validate.notNull(var3, "Invalid type", new Object[0]);
      }

      return var3;
   }

   // $FF: synthetic method
   public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      return this.deserialize(var1, var2, var3);
   }
}
