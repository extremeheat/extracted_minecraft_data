package net.minecraft.client.resources;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record PlayerSkin(ResourceLocation texture, @Nullable String textureUrl, @Nullable ResourceLocation capeTexture, @Nullable ResourceLocation elytraTexture, Model model, boolean secure) {
   public PlayerSkin(ResourceLocation var1, @Nullable String var2, @Nullable ResourceLocation var3, @Nullable ResourceLocation var4, Model var5, boolean var6) {
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

      private Model(final String var3) {
         this.id = var3;
      }

      public static Model byName(@Nullable String var0) {
         if (var0 == null) {
            return WIDE;
         } else {
            Model var10000;
            switch (var0) {
               case "slim" -> var10000 = SLIM;
               default -> var10000 = WIDE;
            }

            return var10000;
         }
      }

      public String id() {
         return this.id;
      }

      // $FF: synthetic method
      private static Model[] $values() {
         return new Model[]{SLIM, WIDE};
      }
   }
}
