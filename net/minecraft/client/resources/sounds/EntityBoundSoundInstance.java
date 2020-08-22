package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class EntityBoundSoundInstance extends AbstractTickableSoundInstance {
   private final Entity entity;

   public EntityBoundSoundInstance(SoundEvent var1, SoundSource var2, Entity var3) {
      this(var1, var2, 1.0F, 1.0F, var3);
   }

   public EntityBoundSoundInstance(SoundEvent var1, SoundSource var2, float var3, float var4, Entity var5) {
      super(var1, var2);
      this.volume = var3;
      this.pitch = var4;
      this.entity = var5;
      this.x = (float)this.entity.getX();
      this.y = (float)this.entity.getY();
      this.z = (float)this.entity.getZ();
   }

   public void tick() {
      if (this.entity.removed) {
         this.stopped = true;
      } else {
         this.x = (float)this.entity.getX();
         this.y = (float)this.entity.getY();
         this.z = (float)this.entity.getZ();
      }
   }
}
