package net.minecraft.client.resources;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class MobEffectTextureManager extends TextureAtlasHolder {
   public MobEffectTextureManager(TextureManager var1) {
      super(var1, ResourceLocation.withDefaultNamespace("textures/atlas/mob_effects.png"), ResourceLocation.withDefaultNamespace("mob_effects"));
   }

   public TextureAtlasSprite get(Holder<MobEffect> var1) {
      return this.getSprite((ResourceLocation)var1.unwrapKey().map(ResourceKey::location).orElseGet(MissingTextureAtlasSprite::getLocation));
   }
}
