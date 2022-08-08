package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Bee;

public class BeeFlyingSoundInstance extends BeeSoundInstance {
   public BeeFlyingSoundInstance(Bee var1) {
      super(var1, SoundEvents.BEE_LOOP, SoundSource.NEUTRAL);
   }

   protected AbstractTickableSoundInstance getAlternativeSoundInstance() {
      return new BeeAggressiveSoundInstance(this.bee);
   }

   protected boolean shouldSwitchSounds() {
      return this.bee.isAngry();
   }
}
