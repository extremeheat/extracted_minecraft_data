package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public abstract class AbstractTickableSoundInstance extends AbstractSoundInstance implements TickableSoundInstance {
   private boolean stopped;

   protected AbstractTickableSoundInstance(SoundEvent var1, SoundSource var2) {
      super(var1, var2);
   }

   public boolean isStopped() {
      return this.stopped;
   }

   protected final void stop() {
      this.stopped = true;
      this.looping = false;
   }
}
