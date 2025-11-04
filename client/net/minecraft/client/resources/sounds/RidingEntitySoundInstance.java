package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class RidingEntitySoundInstance extends AbstractTickableSoundInstance {
   private final Player player;
   private final Entity entity;
   private final boolean underwaterSound;
   private final float volumeMin;
   private final float volumeMax;
   private final float volumeAmplifier;

   public RidingEntitySoundInstance(Player var1, Entity var2, boolean var3, SoundEvent var4, SoundSource var5, float var6, float var7, float var8) {
      super(var4, var5, SoundInstance.createUnseededRandom());
      this.player = var1;
      this.entity = var2;
      this.underwaterSound = var3;
      this.volumeMin = var6;
      this.volumeMax = var7;
      this.volumeAmplifier = var8;
      this.attenuation = SoundInstance.Attenuation.NONE;
      this.looping = true;
      this.delay = 0;
      this.volume = var6;
   }

   public boolean canPlaySound() {
      return !this.entity.isSilent();
   }

   public boolean canStartSilent() {
      return true;
   }

   protected boolean shouldNotPlayUnderwaterSound() {
      return this.underwaterSound != this.entity.isUnderWater();
   }

   protected float getEntitySpeed() {
      return (float)this.entity.getDeltaMovement().length();
   }

   protected boolean shoudlPlaySound() {
      return true;
   }

   public void tick() {
      if (!this.entity.isRemoved() && this.player.isPassenger() && this.player.getVehicle() == this.entity) {
         if (this.shouldNotPlayUnderwaterSound()) {
            this.volume = this.volumeMin;
         } else {
            float var1 = this.getEntitySpeed();
            if (var1 >= 0.01F && this.shoudlPlaySound()) {
               this.volume = this.volumeAmplifier * Mth.clampedLerp(var1, this.volumeMin, this.volumeMax);
            } else {
               this.volume = this.volumeMin;
            }

         }
      } else {
         this.stop();
      }
   }
}
