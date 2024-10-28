package net.minecraft.client.resources;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public record PlayerSkin(ResourceLocation texture, @Nullable String textureUrl, @Nullable ResourceLocation capeTexture, @Nullable ResourceLocation elytraTexture, Model model, boolean secure) {
   public PlayerSkin(ResourceLocation texture, @Nullable String textureUrl, @Nullable ResourceLocation capeTexture, @Nullable ResourceLocation elytraTexture, Model model, boolean secure) {
      super();
      this.texture = texture;
      this.textureUrl = textureUrl;
      this.capeTexture = capeTexture;
      this.elytraTexture = elytraTexture;
      this.model = model;
      this.secure = secure;
   }

   public ResourceLocation texture() {
      return this.texture;
   }

   @Nullable
   public String textureUrl() {
      return this.textureUrl;
   }

   @Nullable
   public ResourceLocation capeTexture() {
      return this.capeTexture;
   }

   @Nullable
   public ResourceLocation elytraTexture() {
      return this.elytraTexture;
   }

   public Model model() {
      return this.model;
   }

   public boolean secure() {
      return this.secure;
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
