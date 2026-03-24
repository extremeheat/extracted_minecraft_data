package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

public class EntityBoundSoundInstance extends AbstractTickableSoundInstance {
   private final Entity entity;

   public EntityBoundSoundInstance(final SoundEvent event, final SoundSource source, final float volume, final float pitch, final Entity entity, final long seed) {
      super(event, source, RandomSource.create(seed));
      this.volume = volume;
      this.pitch = pitch;
      this.entity = entity;
      this.x = (double)((float)this.entity.getX());
      this.y = (double)((float)this.entity.getY());
      this.z = (double)((float)this.entity.getZ());
   }

   public boolean canPlaySound() {
      return !this.entity.isSilent();
   }

   public void tick() {
      if (this.entity.isRemoved()) {
         this.stop();
      } else {
         this.x = (double)((float)this.entity.getX());
         this.y = (double)((float)this.entity.getY());
         this.z = (double)((float)this.entity.getZ());
      }
   }
}
