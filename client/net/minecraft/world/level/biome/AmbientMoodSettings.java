package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class AmbientMoodSettings {
   public static final Codec<AmbientMoodSettings> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(SoundEvent.CODEC.fieldOf("sound").forGetter((var0x) -> {
         return var0x.soundEvent;
      }), Codec.INT.fieldOf("tick_delay").forGetter((var0x) -> {
         return var0x.tickDelay;
      }), Codec.INT.fieldOf("block_search_extent").forGetter((var0x) -> {
         return var0x.blockSearchExtent;
      }), Codec.DOUBLE.fieldOf("offset").forGetter((var0x) -> {
         return var0x.soundPositionOffset;
      })).apply(var0, AmbientMoodSettings::new);
   });
   public static final AmbientMoodSettings LEGACY_CAVE_SETTINGS;
   private final SoundEvent soundEvent;
   private final int tickDelay;
   private final int blockSearchExtent;
   private final double soundPositionOffset;

   public AmbientMoodSettings(SoundEvent var1, int var2, int var3, double var4) {
      super();
      this.soundEvent = var1;
      this.tickDelay = var2;
      this.blockSearchExtent = var3;
      this.soundPositionOffset = var4;
   }

   public SoundEvent getSoundEvent() {
      return this.soundEvent;
   }

   public int getTickDelay() {
      return this.tickDelay;
   }

   public int getBlockSearchExtent() {
      return this.blockSearchExtent;
   }

   public double getSoundPositionOffset() {
      return this.soundPositionOffset;
   }

   static {
      LEGACY_CAVE_SETTINGS = new AmbientMoodSettings(SoundEvents.AMBIENT_CAVE, 6000, 8, 2.0D);
   }
}
