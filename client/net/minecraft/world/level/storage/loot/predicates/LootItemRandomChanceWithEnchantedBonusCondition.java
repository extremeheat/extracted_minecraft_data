package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record LootItemRandomChanceWithEnchantedBonusCondition(float unenchantedChance, LevelBasedValue enchantedChance, Holder<Enchantment> enchantment) implements LootItemCondition {
   public static final MapCodec<LootItemRandomChanceWithEnchantedBonusCondition> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.floatRange(0.0F, 1.0F).fieldOf("unenchanted_chance").forGetter(LootItemRandomChanceWithEnchantedBonusCondition::unenchantedChance), LevelBasedValue.CODEC.fieldOf("enchanted_chance").forGetter(LootItemRandomChanceWithEnchantedBonusCondition::enchantedChance), Enchantment.CODEC.fieldOf("enchantment").forGetter(LootItemRandomChanceWithEnchantedBonusCondition::enchantment)).apply(var0, LootItemRandomChanceWithEnchantedBonusCondition::new);
   });

   public LootItemRandomChanceWithEnchantedBonusCondition(float var1, LevelBasedValue var2, Holder<Enchantment> var3) {
      super();
      this.unenchantedChance = var1;
      this.enchantedChance = var2;
      this.enchantment = var3;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.RANDOM_CHANCE_WITH_ENCHANTED_BONUS;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.ATTACKING_ENTITY);
   }

   public boolean test(LootContext var1) {
      Entity var2 = (Entity)var1.getOptionalParameter(LootContextParams.ATTACKING_ENTITY);
      int var10000;
      if (var2 instanceof LivingEntity var4) {
         var10000 = EnchantmentHelper.getEnchantmentLevel(this.enchantment, var4);
      } else {
         var10000 = 0;
      }

      int var3 = var10000;
      float var5 = var3 > 0 ? this.enchantedChance.calculate(var3) : this.unenchantedChance;
      return var1.getRandom().nextFloat() < var5;
   }

   public static LootItemCondition.Builder randomChanceAndLootingBoost(HolderLookup.Provider var0, float var1, float var2) {
      HolderLookup.RegistryLookup var3 = var0.lookupOrThrow(Registries.ENCHANTMENT);
      return () -> {
         return new LootItemRandomChanceWithEnchantedBonusCondition(var1, new LevelBasedValue.Linear(var1 + var2, var2), var3.getOrThrow(Enchantments.LOOTING));
      };
   }

   public float unenchantedChance() {
      return this.unenchantedChance;
   }

   public LevelBasedValue enchantedChance() {
      return this.enchantedChance;
   }

   public Holder<Enchantment> enchantment() {
      return this.enchantment;
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((LootContext)var1);
   }
}
