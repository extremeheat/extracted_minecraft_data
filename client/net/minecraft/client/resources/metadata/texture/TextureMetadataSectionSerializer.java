package net.minecraft.client.resources.metadata.texture;

import com.google.gson.JsonObject;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;

public class TextureMetadataSectionSerializer implements MetadataSectionSerializer<TextureMetadataSection> {
   public TextureMetadataSectionSerializer() {
      super();
   }

   public TextureMetadataSection fromJson(JsonObject var1) {
      boolean var2 = GsonHelper.getAsBoolean(var1, "blur", false);
      boolean var3 = GsonHelper.getAsBoolean(var1, "clamp", false);
      return new TextureMetadataSection(var2, var3);
   }

   @Override
   public String getMetadataSectionName() {
      return "texture";
   }
}
