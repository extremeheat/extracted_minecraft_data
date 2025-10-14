package net.minecraft.world.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public record AmbientMoodSettings(Holder<SoundEvent> soundEvent, int tickDelay, int blockSearchExtent, double soundPositionOffset) {
   public static final Codec<AmbientMoodSettings> CODEC = RecordCodecBuilder.create((var0) -> var0.group(SoundEvent.CODEC.fieldOf("sound").forGetter((var0x) -> var0x.soundEvent), Codec.INT.fieldOf("tick_delay").forGetter((var0x) -> var0x.tickDelay), Codec.INT.fieldOf("block_search_extent").forGetter((var0x) -> var0x.blockSearchExtent), Codec.DOUBLE.fieldOf("offset").forGetter((var0x) -> var0x.soundPositionOffset)).apply(var0, AmbientMoodSettings::new));
   public static final AmbientMoodSettings LEGACY_CAVE_SETTINGS;

   public AmbientMoodSettings(Holder<SoundEvent> var1, int var2, int var3, double var4) {
      super();
      this.soundEvent = var1;
      this.tickDelay = var2;
      this.blockSearchExtent = var3;
      this.soundPositionOffset = var4;
   }

   static {
      LEGACY_CAVE_SETTINGS = new AmbientMoodSettings(SoundEvents.AMBIENT_CAVE, 6000, 8, 2.0);
   }
}
