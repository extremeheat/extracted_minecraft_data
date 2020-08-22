package net.minecraft.sounds;

import net.minecraft.resources.ResourceLocation;

public class SoundEvent {
   private final ResourceLocation location;

   public SoundEvent(ResourceLocation var1) {
      this.location = var1;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }
}
