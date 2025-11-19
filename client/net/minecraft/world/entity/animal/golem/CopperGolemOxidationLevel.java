package net.minecraft.world.entity.animal.golem;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public record CopperGolemOxidationLevel(SoundEvent spinHeadSound, SoundEvent hurtSound, SoundEvent deathSound, SoundEvent stepSound, Identifier texture, Identifier eyeTexture) {
   public CopperGolemOxidationLevel(SoundEvent var1, SoundEvent var2, SoundEvent var3, SoundEvent var4, Identifier var5, Identifier var6) {
      super();
      this.spinHeadSound = var1;
      this.hurtSound = var2;
      this.deathSound = var3;
      this.stepSound = var4;
      this.texture = var5;
      this.eyeTexture = var6;
   }
}
