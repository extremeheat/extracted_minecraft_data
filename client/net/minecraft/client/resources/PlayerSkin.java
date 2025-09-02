package net.minecraft.client.resources;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelType;

public record PlayerSkin(ResourceLocation texture, @Nullable String textureUrl, @Nullable ResourceLocation capeTexture, @Nullable ResourceLocation elytraTexture, PlayerModelType model, boolean secure) {
   public PlayerSkin(ResourceLocation var1, @Nullable String var2, @Nullable ResourceLocation var3, @Nullable ResourceLocation var4, PlayerModelType var5, boolean var6) {
      super();
      this.texture = var1;
      this.textureUrl = var2;
      this.capeTexture = var3;
      this.elytraTexture = var4;
      this.model = var5;
      this.secure = var6;
   }
}
