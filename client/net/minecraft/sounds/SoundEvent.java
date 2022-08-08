package net.minecraft.sounds;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public class SoundEvent {
   public static final Codec<SoundEvent> CODEC;
   private final ResourceLocation location;
   private final float range;
   private final boolean newSystem;

   public SoundEvent(ResourceLocation var1) {
      this(var1, 16.0F, false);
   }

   public SoundEvent(ResourceLocation var1, float var2) {
      this(var1, var2, true);
   }

   private SoundEvent(ResourceLocation var1, float var2, boolean var3) {
      super();
      this.location = var1;
      this.range = var2;
      this.newSystem = var3;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }

   public float getRange(float var1) {
      if (this.newSystem) {
         return this.range;
      } else {
         return var1 > 1.0F ? 16.0F * var1 : 16.0F;
      }
   }

   static {
      CODEC = ResourceLocation.CODEC.xmap(SoundEvent::new, (var0) -> {
         return var0.location;
      });
   }
}
