package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public class AmbientAdditionsSettings {
   public static final Codec<AmbientAdditionsSettings> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(SoundEvent.CODEC.fieldOf("sound").forGetter((var0x) -> {
         return var0x.soundEvent;
      }), Codec.DOUBLE.fieldOf("tick_chance").forGetter((var0x) -> {
         return var0x.tickChance;
      })).apply(var0, AmbientAdditionsSettings::new);
   });
   private final Holder<SoundEvent> soundEvent;
   private final double tickChance;

   public AmbientAdditionsSettings(Holder<SoundEvent> var1, double var2) {
      super();
      this.soundEvent = var1;
      this.tickChance = var2;
   }

   public Holder<SoundEvent> getSoundEvent() {
      return this.soundEvent;
   }

   public double getTickChance() {
      return this.tickChance;
   }
}
