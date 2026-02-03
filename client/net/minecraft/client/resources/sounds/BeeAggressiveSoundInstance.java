package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.bee.Bee;

public class BeeAggressiveSoundInstance extends BeeSoundInstance {
   public BeeAggressiveSoundInstance(final Bee bee) {
      super(bee, SoundEvents.BEE_LOOP_AGGRESSIVE, SoundSource.NEUTRAL);
      this.delay = 0;
   }

   protected AbstractTickableSoundInstance getAlternativeSoundInstance() {
      return new BeeFlyingSoundInstance(this.bee);
   }

   protected boolean shouldSwitchSounds() {
      return !this.bee.isAngry();
   }
}
