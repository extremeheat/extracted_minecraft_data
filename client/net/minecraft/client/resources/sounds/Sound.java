package net.minecraft.client.resources.sounds;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.SampledFloat;
import org.jspecify.annotations.Nullable;

public class Sound implements Weighted<Sound> {
   public static final FileToIdConverter SOUND_LISTER = new FileToIdConverter("sounds", ".ogg");
   private final Identifier location;
   private final SampledFloat volume;
   private final SampledFloat pitch;
   private final int weight;
   private final Type type;
   private final boolean stream;
   private final boolean preload;
   private final int attenuationDistance;

   public Sound(final Identifier location, final SampledFloat volume, final SampledFloat pitch, final int weight, final Type type, final boolean stream, final boolean preload, final int attenuationDistance) {
      super();
      this.location = location;
      this.volume = volume;
      this.pitch = pitch;
      this.weight = weight;
      this.type = type;
      this.stream = stream;
      this.preload = preload;
      this.attenuationDistance = attenuationDistance;
   }

   public Identifier getLocation() {
      return this.location;
   }

   public Identifier getPath() {
      return SOUND_LISTER.idToFile(this.location);
   }

   public SampledFloat getVolume() {
      return this.volume;
   }

   public SampledFloat getPitch() {
      return this.pitch;
   }

   public int getWeight() {
      return this.weight;
   }

   public Sound getSound(final RandomSource random) {
      return this;
   }

   public void preloadIfRequired(final SoundEngine soundEngine) {
      if (this.preload) {
         soundEngine.requestPreload(this);
      }

   }

   public Type getType() {
      return this.type;
   }

   public boolean shouldStream() {
      return this.stream;
   }

   public boolean shouldPreload() {
      return this.preload;
   }

   public int getAttenuationDistance() {
      return this.attenuationDistance;
   }

   public String toString() {
      return "Sound[" + String.valueOf(this.location) + "]";
   }

   public static enum Type {
      FILE("file"),
      SOUND_EVENT("event");

      private final String name;

      private Type(final String name) {
         this.name = name;
      }

      public static @Nullable Type getByName(final String name) {
         for(Type type : values()) {
            if (type.name.equals(name)) {
               return type;
            }
         }

         return null;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{FILE, SOUND_EVENT};
      }
   }
}
