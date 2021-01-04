package net.minecraft.client.resources;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class MobEffectTextureManager extends TextureAtlasHolder {
   public MobEffectTextureManager(TextureManager var1) {
      super(var1, TextureAtlas.LOCATION_MOB_EFFECTS, "textures/mob_effect");
   }

   protected Iterable<ResourceLocation> getResourcesToLoad() {
      return Registry.MOB_EFFECT.keySet();
   }

   public TextureAtlasSprite get(MobEffect var1) {
      return this.getSprite(Registry.MOB_EFFECT.getKey(var1));
   }
}
