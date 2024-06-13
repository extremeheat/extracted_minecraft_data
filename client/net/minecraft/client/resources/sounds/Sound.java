package net.minecraft.client.resources.sounds;

import javax.annotation.Nullable;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.SampledFloat;

public class Sound implements Weighted<Sound> {
   public static final FileToIdConverter SOUND_LISTER = new FileToIdConverter("sounds", ".ogg");
   private final ResourceLocation location;
   private final SampledFloat volume;
   private final SampledFloat pitch;
   private final int weight;
   private final Sound.Type type;
   private final boolean stream;
   private final boolean preload;
   private final int attenuationDistance;

   public Sound(ResourceLocation var1, SampledFloat var2, SampledFloat var3, int var4, Sound.Type var5, boolean var6, boolean var7, int var8) {
      super();
      this.location = var1;
      this.volume = var2;
      this.pitch = var3;
      this.weight = var4;
      this.type = var5;
      this.stream = var6;
      this.preload = var7;
      this.attenuationDistance = var8;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }

   public ResourceLocation getPath() {
      return SOUND_LISTER.idToFile(this.location);
   }

   public SampledFloat getVolume() {
      return this.volume;
   }

   public SampledFloat getPitch() {
      return this.pitch;
   }

   @Override
   public int getWeight() {
      return this.weight;
   }

   public Sound getSound(RandomSource var1) {
      return this;
   }

   @Override
   public void preloadIfRequired(SoundEngine var1) {
      if (this.preload) {
         var1.requestPreload(this);
      }
   }

   public Sound.Type getType() {
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

   @Override
   public String toString() {
      return "Sound[" + this.location + "]";
   }

   public static enum Type {
      FILE("file"),
      SOUND_EVENT("event");

      private final String name;

      private Type(final String nullxx) {
         this.name = nullxx;
      }

      @Nullable
      public static Sound.Type getByName(String var0) {
         for (Sound.Type var4 : values()) {
            if (var4.name.equals(var0)) {
               return var4;
            }
         }

         return null;
      }
   }
}
