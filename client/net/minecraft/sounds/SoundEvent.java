package net.minecraft.sounds;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public class SoundEvent {
   public static final Codec<SoundEvent> CODEC;
   private final ResourceLocation location;

   public SoundEvent(ResourceLocation var1) {
      super();
      this.location = var1;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }

   static {
      CODEC = ResourceLocation.CODEC.xmap(SoundEvent::new, (var0) -> {
         return var0.location;
      });
   }
}
