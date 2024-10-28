package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record LootItemRandomChanceCondition(NumberProvider chance) implements LootItemCondition {
   public static final MapCodec<LootItemRandomChanceCondition> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(NumberProviders.CODEC.fieldOf("chance").forGetter(LootItemRandomChanceCondition::chance)).apply(var0, LootItemRandomChanceCondition::new);
   });

   public LootItemRandomChanceCondition(NumberProvider var1) {
      super();
      this.chance = var1;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.RANDOM_CHANCE;
   }

   public boolean test(LootContext var1) {
      float var2 = this.chance.getFloat(var1);
      return var1.getRandom().nextFloat() < var2;
   }

   public static LootItemCondition.Builder randomChance(float var0) {
      return () -> {
         return new LootItemRandomChanceCondition(ConstantValue.exactly(var0));
      };
   }

   public static LootItemCondition.Builder randomChance(NumberProvider var0) {
      return () -> {
         return new LootItemRandomChanceCondition(var0);
      };
   }

   public NumberProvider chance() {
      return this.chance;
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((LootContext)var1);
   }
}
