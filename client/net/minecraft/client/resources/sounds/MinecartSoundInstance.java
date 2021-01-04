package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

public class MinecartSoundInstance extends AbstractTickableSoundInstance {
   private final AbstractMinecart minecart;
   private float pitch = 0.0F;

   public MinecartSoundInstance(AbstractMinecart var1) {
      super(SoundEvents.MINECART_RIDING, SoundSource.NEUTRAL);
      this.minecart = var1;
      this.looping = true;
      this.delay = 0;
      this.volume = 0.0F;
      this.x = (float)var1.x;
      this.y = (float)var1.y;
      this.z = (float)var1.z;
   }

   public boolean canStartSilent() {
      return true;
   }

   public void tick() {
      if (this.minecart.removed) {
         this.stopped = true;
      } else {
         this.x = (float)this.minecart.x;
         this.y = (float)this.minecart.y;
         this.z = (float)this.minecart.z;
         float var1 = Mth.sqrt(Entity.getHorizontalDistanceSqr(this.minecart.getDeltaMovement()));
         if ((double)var1 >= 0.01D) {
            this.pitch = Mth.clamp(this.pitch + 0.0025F, 0.0F, 1.0F);
            this.volume = Mth.lerp(Mth.clamp(var1, 0.0F, 0.5F), 0.0F, 0.7F);
         } else {
            this.pitch = 0.0F;
            this.volume = 0.0F;
         }

      }
   }
}
