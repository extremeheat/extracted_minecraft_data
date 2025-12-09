package net.minecraft.world.entity.variant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;

public record MoonBrightnessCheck(MinMaxBounds.Doubles range) implements SpawnCondition {
   public static final MapCodec<MoonBrightnessCheck> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(MinMaxBounds.Doubles.CODEC.fieldOf("range").forGetter(MoonBrightnessCheck::range)).apply(var0, MoonBrightnessCheck::new));

   public MoonBrightnessCheck(MinMaxBounds.Doubles var1) {
      super();
      this.range = var1;
   }

   public boolean test(SpawnContext var1) {
      MoonPhase var2 = (MoonPhase)var1.environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, Vec3.atCenterOf(var1.pos()));
      float var3 = DimensionType.MOON_BRIGHTNESS_PER_PHASE[var2.index()];
      return this.range.matches((double)var3);
   }

   public MapCodec<MoonBrightnessCheck> codec() {
      return MAP_CODEC;
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((SpawnContext)var1);
   }
}
