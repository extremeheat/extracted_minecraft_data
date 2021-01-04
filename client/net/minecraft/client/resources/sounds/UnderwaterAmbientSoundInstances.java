package net.minecraft.client.resources.sounds;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class UnderwaterAmbientSoundInstances {
   public static class UnderwaterAmbientSoundInstance extends AbstractTickableSoundInstance {
      private final LocalPlayer player;
      private int fade;

      public UnderwaterAmbientSoundInstance(LocalPlayer var1) {
         super(SoundEvents.AMBIENT_UNDERWATER_LOOP, SoundSource.AMBIENT);
         this.player = var1;
         this.looping = true;
         this.delay = 0;
         this.volume = 1.0F;
         this.priority = true;
         this.relative = true;
      }

      public void tick() {
         if (!this.player.removed && this.fade >= 0) {
            if (this.player.isUnderWater()) {
               ++this.fade;
            } else {
               this.fade -= 2;
            }

            this.fade = Math.min(this.fade, 40);
            this.volume = Math.max(0.0F, Math.min((float)this.fade / 40.0F, 1.0F));
         } else {
            this.stopped = true;
         }
      }
   }

   public static class SubSound extends AbstractTickableSoundInstance {
      private final LocalPlayer player;

      protected SubSound(LocalPlayer var1, SoundEvent var2) {
         super(var2, SoundSource.AMBIENT);
         this.player = var1;
         this.looping = false;
         this.delay = 0;
         this.volume = 1.0F;
         this.priority = true;
         this.relative = true;
      }

      public void tick() {
         if (this.player.removed || !this.player.isUnderWater()) {
            this.stopped = true;
         }

      }
   }
}
