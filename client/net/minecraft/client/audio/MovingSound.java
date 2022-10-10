package net.minecraft.client.audio;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public abstract class MovingSound extends AbstractSound implements ITickableSound {
   protected boolean field_147668_j;

   protected MovingSound(SoundEvent var1, SoundCategory var2) {
      super(var1, var2);
   }

   public boolean func_147667_k() {
      return this.field_147668_j;
   }
}
