package net.minecraft.client.resources;

import java.util.stream.Stream;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class MobEffectTextureManager extends TextureAtlasHolder {
   public MobEffectTextureManager(TextureManager var1) {
      super(var1, new ResourceLocation("textures/atlas/mob_effects.png"), "mob_effect");
   }

   protected Stream<ResourceLocation> getResourcesToLoad() {
      return Registry.MOB_EFFECT.keySet().stream();
   }

   public TextureAtlasSprite get(MobEffect var1) {
      return this.getSprite(Registry.MOB_EFFECT.getKey(var1));
   }
}
