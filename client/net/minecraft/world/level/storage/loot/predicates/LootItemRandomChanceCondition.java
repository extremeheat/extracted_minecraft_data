package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import org.jspecify.annotations.Nullable;

public record LootItemRandomChanceCondition(Holder<ContextFloatProvider> chance) implements LootItemCondition {
   public static final MapCodec<LootItemRandomChanceCondition> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ContextFloatProviders.CODEC.fieldOf("chance").forGetter(LootItemRandomChanceCondition::chance)).apply(i, LootItemRandomChanceCondition::new));

   public LootItemRandomChanceCondition {
      super();
   }

   public MapCodec<LootItemRandomChanceCondition> codec() {
      return MAP_CODEC;
   }

   public boolean test(final LootContext context) {
      float probability = ((ContextFloatProvider)this.chance.value()).getFloat(context);
      return context.getRandom().nextFloat() < probability;
   }

   public @Nullable Boolean constantValue(final LootItemCondition.ConstantConditionEvaluationContext context) {
      return null;
   }

   public void validate(final ValidationContext context) {
   }

   public static LootItemCondition.Builder randomChance(final float probability) {
      return () -> new LootItemRandomChanceCondition(ContextFloatProviders.exactly(probability));
   }

   public static LootItemCondition.Builder randomChance(final Holder<ContextFloatProvider> probability) {
      return () -> new LootItemRandomChanceCondition(probability);
   }
}
