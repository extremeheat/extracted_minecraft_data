package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

public class RidingMinecartSoundInstance extends AbstractTickableSoundInstance {
   private static final float VOLUME_MIN = 0.0F;
   private static final float VOLUME_MAX = 0.75F;
   private final Player player;
   private final AbstractMinecart minecart;
   private final boolean underwaterSound;

   public RidingMinecartSoundInstance(Player var1, AbstractMinecart var2, boolean var3) {
      super(var3 ? SoundEvents.MINECART_INSIDE_UNDERWATER : SoundEvents.MINECART_INSIDE, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
      this.player = var1;
      this.minecart = var2;
      this.underwaterSound = var3;
      this.attenuation = SoundInstance.Attenuation.NONE;
      this.looping = true;
      this.delay = 0;
      this.volume = 0.0F;
   }

   public boolean canPlaySound() {
      return !this.minecart.isSilent();
   }

   public boolean canStartSilent() {
      return true;
   }

   public void tick() {
      if (!this.minecart.isRemoved() && this.player.isPassenger() && this.player.getVehicle() == this.minecart) {
         if (this.underwaterSound != this.player.isUnderWater()) {
            this.volume = 0.0F;
         } else {
            float var1 = (float)this.minecart.getDeltaMovement().horizontalDistance();
            if (var1 >= 0.01F) {
               this.volume = Mth.clampedLerp(0.0F, 0.75F, var1);
            } else {
               this.volume = 0.0F;
            }

         }
      } else {
         this.stop();
      }
   }
}
