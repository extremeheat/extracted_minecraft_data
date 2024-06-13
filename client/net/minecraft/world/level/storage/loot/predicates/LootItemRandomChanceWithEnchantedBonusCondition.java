package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record LootItemRandomChanceWithEnchantedBonusCondition(LevelBasedValue chance, Holder<Enchantment> enchantment) implements LootItemCondition {
   public static final MapCodec<LootItemRandomChanceWithEnchantedBonusCondition> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               LevelBasedValue.CODEC.fieldOf("chance").forGetter(LootItemRandomChanceWithEnchantedBonusCondition::chance),
               Enchantment.CODEC.fieldOf("enchantment").forGetter(LootItemRandomChanceWithEnchantedBonusCondition::enchantment)
            )
            .apply(var0, LootItemRandomChanceWithEnchantedBonusCondition::new)
   );

   public LootItemRandomChanceWithEnchantedBonusCondition(LevelBasedValue chance, Holder<Enchantment> enchantment) {
      super();
      this.chance = chance;
      this.enchantment = enchantment;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.RANDOM_CHANCE_WITH_ENCHANTED_BONUS;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.ATTACKING_ENTITY);
   }

   public boolean test(LootContext var1) {
      Entity var2 = var1.getParamOrNull(LootContextParams.ATTACKING_ENTITY);
      int var3;
      if (var2 instanceof LivingEntity var4) {
         var3 = EnchantmentHelper.getEnchantmentLevel(this.enchantment, var4);
      } else {
         var3 = 0;
      }

      return var1.getRandom().nextFloat() < this.chance.calculate(var3);
   }

   public static LootItemCondition.Builder randomChanceAndLootingBoost(HolderLookup.Provider var0, float var1, float var2) {
      HolderLookup.RegistryLookup var3 = var0.lookupOrThrow(Registries.ENCHANTMENT);
      return () -> new LootItemRandomChanceWithEnchantedBonusCondition(new LevelBasedValue.Linear(var1 + var2, var2), var3.getOrThrow(Enchantments.LOOTING));
   }
}
