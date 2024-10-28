package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record BonusLevelTableCondition(Holder<Enchantment> enchantment, List<Float> values) implements LootItemCondition {
   public static final MapCodec<BonusLevelTableCondition> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Enchantment.CODEC.fieldOf("enchantment").forGetter(BonusLevelTableCondition::enchantment), ExtraCodecs.nonEmptyList(Codec.FLOAT.listOf()).fieldOf("chances").forGetter(BonusLevelTableCondition::values)).apply(var0, BonusLevelTableCondition::new);
   });

   public BonusLevelTableCondition(Holder<Enchantment> enchantment, List<Float> values) {
      super();
      this.enchantment = enchantment;
      this.values = values;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.TABLE_BONUS;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.TOOL);
   }

   public boolean test(LootContext var1) {
      ItemStack var2 = (ItemStack)var1.getParamOrNull(LootContextParams.TOOL);
      int var3 = var2 != null ? EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, var2) : 0;
      float var4 = (Float)this.values.get(Math.min(var3, this.values.size() - 1));
      return var1.getRandom().nextFloat() < var4;
   }

   public static LootItemCondition.Builder bonusLevelFlatChance(Holder<Enchantment> var0, float... var1) {
      ArrayList var2 = new ArrayList(var1.length);
      float[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         float var6 = var3[var5];
         var2.add(var6);
      }

      return () -> {
         return new BonusLevelTableCondition(var0, var2);
      };
   }

   public Holder<Enchantment> enchantment() {
      return this.enchantment;
   }

   public List<Float> values() {
      return this.values;
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((LootContext)var1);
   }
}
