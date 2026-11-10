package com.mojang.realmsclient.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.Base64TextureProvider2d;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class RealmsTextureManager {
   private static final Map<String, RealmsTexture> TEXTURES = new HashMap();
   private static final Identifier TEMPLATE_ICON_LOCATION = Identifier.withDefaultNamespace("textures/gui/presets/isles.png");

   public RealmsTextureManager() {
      super();
   }

   public static Identifier worldTemplate(final String id, final @Nullable String image) {
      return image == null ? TEMPLATE_ICON_LOCATION : getTexture(id, image);
   }

   private static Identifier getTexture(final String id, final String encodedImage) {
      RealmsTexture texture = (RealmsTexture)TEXTURES.get(id);
      if (texture != null && texture.image().equals(encodedImage)) {
         return texture.textureId;
      } else {
         Identifier textureId = Identifier.fromNamespaceAndPath("realms", "dynamic/" + id);
         Minecraft.getInstance().getTextureManager().registerProvider(textureId, new Base64TextureProvider2d(encodedImage));
         TEXTURES.put(id, new RealmsTexture(encodedImage, textureId));
         return textureId;
      }
   }

   public static record RealmsTexture(String image, Identifier textureId) {
      public RealmsTexture {
         super();
      }
   }
}
