package net.minecraft.client.resources.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;

public abstract class BeeSoundInstance extends AbstractTickableSoundInstance {
   protected final Bee bee;
   private boolean hasSwitched;

   public BeeSoundInstance(Bee var1, SoundEvent var2, SoundSource var3) {
      super(var2, var3);
      this.bee = var1;
      this.x = (float)var1.getX();
      this.y = (float)var1.getY();
      this.z = (float)var1.getZ();
      this.looping = true;
      this.delay = 0;
      this.volume = 0.0F;
   }

   public void tick() {
      boolean var1 = this.shouldSwitchSounds();
      if (var1 && !this.stopped) {
         Minecraft.getInstance().getSoundManager().queueTickingSound(this.getAlternativeSoundInstance());
         this.hasSwitched = true;
      }

      if (!this.bee.removed && !this.hasSwitched) {
         this.x = (float)this.bee.getX();
         this.y = (float)this.bee.getY();
         this.z = (float)this.bee.getZ();
         float var2 = Mth.sqrt(Entity.getHorizontalDistanceSqr(this.bee.getDeltaMovement()));
         if ((double)var2 >= 0.01D) {
            this.pitch = Mth.lerp(Mth.clamp(var2, this.getMinPitch(), this.getMaxPitch()), this.getMinPitch(), this.getMaxPitch());
            this.volume = Mth.lerp(Mth.clamp(var2, 0.0F, 0.5F), 0.0F, 1.2F);
         } else {
            this.pitch = 0.0F;
            this.volume = 0.0F;
         }

      } else {
         this.stopped = true;
      }
   }

   private float getMinPitch() {
      return this.bee.isBaby() ? 1.1F : 0.7F;
   }

   private float getMaxPitch() {
      return this.bee.isBaby() ? 1.5F : 1.1F;
   }

   public boolean canStartSilent() {
      return true;
   }

   protected abstract AbstractTickableSoundInstance getAlternativeSoundInstance();

   protected abstract boolean shouldSwitchSounds();
}
