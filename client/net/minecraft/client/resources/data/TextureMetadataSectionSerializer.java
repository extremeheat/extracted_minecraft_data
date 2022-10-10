package net.minecraft.client.resources.data;

import com.google.gson.JsonObject;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JsonUtils;

public class TextureMetadataSectionSerializer implements IMetadataSectionSerializer<TextureMetadataSection> {
   public TextureMetadataSectionSerializer() {
      super();
   }

   public TextureMetadataSection func_195812_a(JsonObject var1) {
      boolean var2 = JsonUtils.func_151209_a(var1, "blur", false);
      boolean var3 = JsonUtils.func_151209_a(var1, "clamp", false);
      return new TextureMetadataSection(var2, var3);
   }

   public String func_110483_a() {
      return "texture";
   }

   // $FF: synthetic method
   public Object func_195812_a(JsonObject var1) {
      return this.func_195812_a(var1);
   }
}
