package net.minecraft.client.resources.sounds;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.resources.ResourceLocation;

public class Sound implements Weighted {
   private final ResourceLocation location;
   private final float volume;
   private final float pitch;
   private final int weight;
   private final Sound.Type type;
   private final boolean stream;
   private final boolean preload;
   private final int attenuationDistance;

   public Sound(String var1, float var2, float var3, int var4, Sound.Type var5, boolean var6, boolean var7, int var8) {
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
      return new ResourceLocation(this.location.getNamespace(), "sounds/" + this.location.getPath() + ".ogg");
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public int getWeight() {
      return this.weight;
   }

   public Sound getSound() {
      return this;
   }

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

   // $FF: synthetic method
   public Object getSound() {
      return this.getSound();
   }

   public static enum Type {
      FILE("file"),
      SOUND_EVENT("event");

      private final String name;

      private Type(String var3) {
         this.name = var3;
      }

      public static Sound.Type getByName(String var0) {
         Sound.Type[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Sound.Type var4 = var1[var3];
            if (var4.name.equals(var0)) {
               return var4;
            }
         }

         return null;
      }
   }
}
