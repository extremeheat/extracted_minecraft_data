package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;

public record Instrument(SoundEvent b, int c, float d) {
   private final SoundEvent soundEvent;
   private final int useDuration;
   private final float range;
   public static final Codec<Instrument> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(SoundEvent.CODEC.fieldOf("sound_event").forGetter(Instrument::soundEvent), ExtraCodecs.POSITIVE_INT.fieldOf("use_duration").forGetter(Instrument::useDuration), ExtraCodecs.POSITIVE_FLOAT.fieldOf("range").forGetter(Instrument::range)).apply(var0, Instrument::new);
   });

   public Instrument(SoundEvent var1, int var2, float var3) {
      super();
      this.soundEvent = var1;
      this.useDuration = var2;
      this.range = var3;
   }

   public SoundEvent soundEvent() {
      return this.soundEvent;
   }

   public int useDuration() {
      return this.useDuration;
   }

   public float range() {
      return this.range;
   }
}
