package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public abstract class AbstractTickableSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
   protected boolean stopped;

   protected AbstractTickableSoundInstance(SoundEvent var1, SoundSource var2) {
      super(var1, var2);
   }

   public boolean isStopped() {
      return this.stopped;
   }
}
