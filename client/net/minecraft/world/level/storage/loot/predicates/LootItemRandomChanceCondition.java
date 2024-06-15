package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;

public record LootItemRandomChanceCondition(float probability) implements LootItemCondition {
   public static final MapCodec<LootItemRandomChanceCondition> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(Codec.FLOAT.fieldOf("chance").forGetter(LootItemRandomChanceCondition::probability)).apply(var0, LootItemRandomChanceCondition::new)
   );

   public LootItemRandomChanceCondition(float probability) {
      super();
      this.probability = probability;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.RANDOM_CHANCE;
   }

   public boolean test(LootContext var1) {
      return var1.getRandom().nextFloat() < this.probability;
   }

   public static LootItemCondition.Builder randomChance(float var0) {
      return () -> new LootItemRandomChanceCondition(var0);
   }
}
