package net.minecraft.client.resources;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record PlayerSkin(ResourceLocation a, @Nullable String b, @Nullable ResourceLocation c, @Nullable ResourceLocation d, PlayerSkin.Model e, boolean f) {
   private final ResourceLocation texture;
   @Nullable
   private final String textureUrl;
   @Nullable
   private final ResourceLocation capeTexture;
   @Nullable
   private final ResourceLocation elytraTexture;
   private final PlayerSkin.Model model;
   private final boolean secure;

   public PlayerSkin(
      ResourceLocation var1, @Nullable String var2, @Nullable ResourceLocation var3, @Nullable ResourceLocation var4, PlayerSkin.Model var5, boolean var6
   ) {
      super();
      this.texture = var1;
      this.textureUrl = var2;
      this.capeTexture = var3;
      this.elytraTexture = var4;
      this.model = var5;
      this.secure = var6;
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
            switch(var0.hashCode()) {
               case 3533117:
                  if (var0.equals("slim")) {
                     var2 = 0;
                  }
               default:
                  return switch(var2) {
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
