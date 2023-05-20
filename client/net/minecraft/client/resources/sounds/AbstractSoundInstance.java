package net.minecraft.client.resources.sounds;

import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public abstract class AbstractSoundInstance implements SoundInstance {
   protected Sound sound;
   protected final SoundSource source;
   protected final ResourceLocation location;
   protected float volume = 1.0F;
   protected float pitch = 1.0F;
   protected double x;
   protected double y;
   protected double z;
   protected boolean looping;
   protected int delay;
   protected SoundInstance.Attenuation attenuation = SoundInstance.Attenuation.LINEAR;
   protected boolean relative;
   protected RandomSource random;

   protected AbstractSoundInstance(SoundEvent var1, SoundSource var2, RandomSource var3) {
      this(var1.getLocation(), var2, var3);
   }

   protected AbstractSoundInstance(ResourceLocation var1, SoundSource var2, RandomSource var3) {
      super();
      this.location = var1;
      this.source = var2;
      this.random = var3;
   }

   @Override
   public ResourceLocation getLocation() {
      return this.location;
   }

   @Override
   public WeighedSoundEvents resolve(SoundManager var1) {
      if (this.location.equals(SoundManager.INTENTIONALLY_EMPTY_SOUND_LOCATION)) {
         this.sound = SoundManager.INTENTIONALLY_EMPTY_SOUND;
         return SoundManager.INTENTIONALLY_EMPTY_SOUND_EVENT;
      } else {
         WeighedSoundEvents var2 = var1.getSoundEvent(this.location);
         if (var2 == null) {
            this.sound = SoundManager.EMPTY_SOUND;
         } else {
            this.sound = var2.getSound(this.random);
         }

         return var2;
      }
   }

   @Override
   public Sound getSound() {
      return this.sound;
   }

   @Override
   public SoundSource getSource() {
      return this.source;
   }

   @Override
   public boolean isLooping() {
      return this.looping;
   }

   @Override
   public int getDelay() {
      return this.delay;
   }

   @Override
   public float getVolume() {
      return this.volume * this.sound.getVolume().sample(this.random);
   }

   @Override
   public float getPitch() {
      return this.pitch * this.sound.getPitch().sample(this.random);
   }

   @Override
   public double getX() {
      return this.x;
   }

   @Override
   public double getY() {
      return this.y;
   }

   @Override
   public double getZ() {
      return this.z;
   }

   @Override
   public SoundInstance.Attenuation getAttenuation() {
      return this.attenuation;
   }

   @Override
   public boolean isRelative() {
      return this.relative;
   }

   @Override
   public String toString() {
      return "SoundInstance[" + this.location + "]";
   }
}
