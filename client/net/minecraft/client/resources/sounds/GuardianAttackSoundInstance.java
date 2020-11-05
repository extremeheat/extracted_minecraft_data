package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.monster.Guardian;

public class GuardianAttackSoundInstance extends AbstractTickableSoundInstance {
   private final Guardian guardian;

   public GuardianAttackSoundInstance(Guardian var1) {
      super(SoundEvents.GUARDIAN_ATTACK, SoundSource.HOSTILE);
      this.guardian = var1;
      this.attenuation = SoundInstance.Attenuation.NONE;
      this.looping = true;
      this.delay = 0;
   }

   public boolean canPlaySound() {
      return !this.guardian.isSilent();
   }

   public void tick() {
      if (!this.guardian.removed && this.guardian.getTarget() == null) {
         this.x = (double)((float)this.guardian.getX());
         this.y = (double)((float)this.guardian.getY());
         this.z = (double)((float)this.guardian.getZ());
         float var1 = this.guardian.getAttackAnimationScale(0.0F);
         this.volume = 0.0F + 1.0F * var1 * var1;
         this.pitch = 0.7F + 0.5F * var1;
      } else {
         this.stop();
      }
   }
}
