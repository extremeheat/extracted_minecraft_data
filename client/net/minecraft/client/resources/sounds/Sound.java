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
   private final Type type;
   private final boolean stream;
   private final boolean preload;
   private final int attenuationDistance;

   public Sound(String var1, SampledFloat var2, SampledFloat var3, int var4, Type var5, boolean var6, boolean var7, int var8) {
      super();
      this.location = new ResourceLocation(var1);
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

   public int getWeight() {
      return this.weight;
   }

   public Sound getSound(RandomSource var1) {
      return this;
   }

   public void preloadIfRequired(SoundEngine var1) {
      if (this.preload) {
         var1.requestPreload(this);
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

   // $FF: synthetic method
   public Object getSound(RandomSource var1) {
      return this.getSound(var1);
   }

   public static enum Type {
      FILE("file"),
      SOUND_EVENT("event");

      private final String name;

      private Type(String var3) {
         this.name = var3;
      }

      @Nullable
      public static Type getByName(String var0) {
         Type[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Type var4 = var1[var3];
            if (var4.name.equals(var0)) {
               return var4;
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
