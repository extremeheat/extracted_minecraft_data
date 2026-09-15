package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.FloatRangePredicate;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;

public record FloatValueCheck(Holder<ContextFloatProvider> value, FloatRangePredicate range) implements LootItemCondition {
   public static final MapCodec<FloatValueCheck> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ContextFloatProviders.CODEC.fieldOf("value").forGetter(FloatValueCheck::value), FloatRangePredicate.CODEC.fieldOf("test").forGetter(FloatValueCheck::range)).apply(i, FloatValueCheck::new));

   public FloatValueCheck {
      super();
   }

   public MapCodec<FloatValueCheck> codec() {
      return MAP_CODEC;
   }

   public void validate(final ValidationContext context) {
      LootItemCondition.super.validate(context);
      Validatable.validateHolder(context, "value", this.value);
      Validatable.validate(context, "range", this.range);
   }

   public boolean test(final LootContext context) {
      return this.range.test(context, this.value.value());
   }

   public static LootItemCondition.Builder hasValue(final Holder<ContextFloatProvider> value, final FloatRangePredicate range) {
      return () -> new FloatValueCheck(value, range);
   }
}
