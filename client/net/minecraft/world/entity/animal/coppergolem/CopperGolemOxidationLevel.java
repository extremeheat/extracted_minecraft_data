package net.minecraft.world.entity.animal.coppergolem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public record CopperGolemOxidationLevel(SoundEvent spinHeadSound, SoundEvent hurtSound, SoundEvent deathSound, SoundEvent stepSound, ResourceLocation texture, ResourceLocation eyeTexture) {
   public CopperGolemOxidationLevel(SoundEvent var1, SoundEvent var2, SoundEvent var3, SoundEvent var4, ResourceLocation var5, ResourceLocation var6) {
      super();
      this.spinHeadSound = var1;
      this.hurtSound = var2;
      this.deathSound = var3;
      this.stepSound = var4;
      this.texture = var5;
      this.eyeTexture = var6;
   }
}
