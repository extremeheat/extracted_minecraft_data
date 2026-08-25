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
   protected @Nullable WeighedSoundEvents soundEvent;
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
   protected final RandomSource random;

   protected AbstractSoundInstance(final SoundEvent event, final SoundSource source, final RandomSource random) {
      this(event.location(), source, random);
   }

   protected AbstractSoundInstance(final Identifier identifier, final SoundSource source, final RandomSource random) {
      super();
      this.volume = 1.0F;
      this.pitch = 1.0F;
      this.attenuation = SoundInstance.Attenuation.LINEAR;
      this.identifier = identifier;
      this.source = source;
      this.random = random;
   }

   public Identifier getIdentifier() {
      return this.identifier;
   }

   public @Nullable WeighedSoundEvents getOrResolve(final SoundManager soundManager) {
      if (this.identifier.equals(SoundManager.INTENTIONALLY_EMPTY_SOUND_LOCATION)) {
         this.sound = SoundManager.INTENTIONALLY_EMPTY_SOUND;
         return SoundManager.INTENTIONALLY_EMPTY_SOUND_EVENT;
      } else {
         this.soundEvent = soundManager.getSoundEvent(this.identifier);
         if (this.soundEvent == null) {
            this.sound = SoundManager.EMPTY_SOUND;
         } else {
            this.sound = this.soundEvent.getSound(this.random);
         }

         return this.soundEvent;
      }
   }

   public @Nullable Sound getSound() {
      return this.sound;
   }

   public @Nullable WeighedSoundEvents getSoundEvent() {
      return this.soundEvent;
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
