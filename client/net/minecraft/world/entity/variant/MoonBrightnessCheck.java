package net.minecraft.world.entity.variant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;

public record MoonBrightnessCheck(MinMaxBounds.Doubles range) implements SpawnCondition {
   public static final MapCodec<MoonBrightnessCheck> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(MinMaxBounds.Doubles.CODEC.fieldOf("range").forGetter(MoonBrightnessCheck::range)).apply(var0, MoonBrightnessCheck::new));

   public MoonBrightnessCheck(MinMaxBounds.Doubles var1) {
      super();
      this.range = var1;
   }

   public boolean test(SpawnContext var1) {
      return this.range.matches((double)var1.level().getLevel().getMoonBrightness());
   }

   public MapCodec<MoonBrightnessCheck> codec() {
      return MAP_CODEC;
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((SpawnContext)var1);
   }
}
