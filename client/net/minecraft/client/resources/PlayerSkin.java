package net.minecraft.client.resources;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record PlayerSkin(
   ResourceLocation texture,
   @Nullable String textureUrl,
   @Nullable ResourceLocation capeTexture,
   @Nullable ResourceLocation elytraTexture,
   PlayerSkin.Model model,
   boolean secure
) {
   public PlayerSkin(
      ResourceLocation texture,
      @Nullable String textureUrl,
      @Nullable ResourceLocation capeTexture,
      @Nullable ResourceLocation elytraTexture,
      PlayerSkin.Model model,
      boolean secure
   ) {
      super();
      this.texture = texture;
      this.textureUrl = textureUrl;
      this.capeTexture = capeTexture;
      this.elytraTexture = elytraTexture;
      this.model = model;
      this.secure = secure;
   }

   public static enum Model {
      SLIM("slim"),
      WIDE("default");

      private final String id;

      private Model(String var3) {
         this.id = var3;
      }

      public static PlayerSkin.Model byName(@Nullable String var0) {
         if (var0 == null) {
            return WIDE;
         } else {
            byte var2 = -1;
            switch (var0.hashCode()) {
               case 3533117:
                  if (var0.equals("slim")) {
                     var2 = 0;
                  }
               default:
                  return switch (var2) {
                     case 0 -> SLIM;
                     default -> WIDE;
                  };
            }
         }
      }

      public String id() {
         return this.id;
      }
   }
}
