package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record LootItemRandomChanceWithLootingCondition(float b, float c) implements LootItemCondition {
   private final float percent;
   private final float lootingMultiplier;
   public static final Codec<LootItemRandomChanceWithLootingCondition> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.FLOAT.fieldOf("chance").forGetter(LootItemRandomChanceWithLootingCondition::percent),
               Codec.FLOAT.fieldOf("looting_multiplier").forGetter(LootItemRandomChanceWithLootingCondition::lootingMultiplier)
            )
            .apply(var0, LootItemRandomChanceWithLootingCondition::new)
   );

   public LootItemRandomChanceWithLootingCondition(float var1, float var2) {
      super();
      this.percent = var1;
      this.lootingMultiplier = var2;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.RANDOM_CHANCE_WITH_LOOTING;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.KILLER_ENTITY);
   }

   public boolean test(LootContext var1) {
      Entity var2 = var1.getParamOrNull(LootContextParams.KILLER_ENTITY);
      int var3 = 0;
      if (var2 instanceof LivingEntity) {
         var3 = EnchantmentHelper.getMobLooting((LivingEntity)var2);
      }

      return var1.getRandom().nextFloat() < this.percent + (float)var3 * this.lootingMultiplier;
   }

   public static LootItemCondition.Builder randomChanceAndLootingBoost(float var0, float var1) {
      return () -> new LootItemRandomChanceWithLootingCondition(var0, var1);
   }
}
