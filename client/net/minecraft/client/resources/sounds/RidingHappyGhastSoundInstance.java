package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.entity.player.Player;

public class RidingHappyGhastSoundInstance extends AbstractTickableSoundInstance {
   private static final float VOLUME_MIN = 0.0F;
   private static final float VOLUME_MAX = 1.0F;
   private final Player player;
   private final HappyGhast happyGhast;

   public RidingHappyGhastSoundInstance(Player var1, HappyGhast var2) {
      super(SoundEvents.HAPPY_GHAST_RIDING, var2.getSoundSource(), SoundInstance.createUnseededRandom());
      this.player = var1;
      this.happyGhast = var2;
      this.attenuation = SoundInstance.Attenuation.NONE;
      this.looping = true;
      this.delay = 0;
      this.volume = 0.0F;
   }

   public boolean canStartSilent() {
      return true;
   }

   public void tick() {
      if (!this.happyGhast.isRemoved() && this.player.isPassenger() && this.player.getVehicle() == this.happyGhast) {
         float var1 = (float)this.happyGhast.getDeltaMovement().length();
         if (var1 >= 0.01F) {
            this.volume = 5.0F * Mth.clampedLerp(0.0F, 1.0F, var1);
         } else {
            this.volume = 0.0F;
         }

      } else {
         this.stop();
      }
   }
}
