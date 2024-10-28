package net.minecraft.client.resources;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.flag.FeatureFlags;

public class MobEffectTextureManager extends TextureAtlasHolder {
   public MobEffectTextureManager(TextureManager var1) {
      super(var1, new ResourceLocation("textures/atlas/mob_effects.png"), new ResourceLocation("mob_effects"));
   }

   public TextureAtlasSprite get(Holder<MobEffect> var1) {
      if (var1 == MobEffects.BAD_OMEN) {
         ClientLevel var2 = Minecraft.getInstance().level;
         if (var2 != null && var2.enabledFeatures().contains(FeatureFlags.UPDATE_1_21)) {
            return this.getSprite(new ResourceLocation("bad_omen_121"));
         }
      }

      return this.getSprite((ResourceLocation)var1.unwrapKey().map(ResourceKey::location).orElseGet(MissingTextureAtlasSprite::getLocation));
   }
}
