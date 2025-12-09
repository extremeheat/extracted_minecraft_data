package net.minecraft.client.resources.sounds;

import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public abstract class AbstractSoundInstance implements SoundInstance {
   protected @Nullable Sound sound;
   protected final SoundSource source;
   protected final Identifier identifier;
   protected float volume;
   protected float pitch;
   protected double x;
   protected double y;
   protected double z;
   protected boolean looping;
   protected int delay;
   protected SoundInstance.Attenuation attenuation;
   protected boolean relative;
   protected RandomSource random;

   protected AbstractSoundInstance(SoundEvent var1, SoundSource var2, RandomSource var3) {
      this(var1.location(), var2, var3);
   }

   protected AbstractSoundInstance(Identifier var1, SoundSource var2, RandomSource var3) {
      super();
      this.volume = 1.0F;
      this.pitch = 1.0F;
      this.attenuation = SoundInstance.Attenuation.LINEAR;
      this.identifier = var1;
      this.source = var2;
      this.random = var3;
   }

   public Identifier getIdentifier() {
      return this.identifier;
   }

   public @Nullable WeighedSoundEvents resolve(SoundManager var1) {
      if (this.identifier.equals(SoundManager.INTENTIONALLY_EMPTY_SOUND_LOCATION)) {
         this.sound = SoundManager.INTENTIONALLY_EMPTY_SOUND;
         return SoundManager.INTENTIONALLY_EMPTY_SOUND_EVENT;
      } else {
         WeighedSoundEvents var2 = var1.getSoundEvent(this.identifier);
         if (var2 == null) {
            this.sound = SoundManager.EMPTY_SOUND;
         } else {
            this.sound = var2.getSound(this.random);
         }

         return var2;
      }
   }

   public @Nullable Sound getSound() {
      return this.sound;
   }

   public SoundSource getSource() {
      return this.source;
   }

   public boolean isLooping() {
      return this.looping;
   }

   public int getDelay() {
      return this.delay;
   }

   public float getVolume() {
      return this.volume * this.sound.getVolume().sample(this.random);
   }

   public float getPitch() {
      return this.pitch * this.sound.getPitch().sample(this.random);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public SoundInstance.Attenuation getAttenuation() {
      return this.attenuation;
   }

   public boolean isRelative() {
      return this.relative;
   }

   public String toString() {
      return "SoundInstance[" + String.valueOf(this.identifier) + "]";
   }
}
