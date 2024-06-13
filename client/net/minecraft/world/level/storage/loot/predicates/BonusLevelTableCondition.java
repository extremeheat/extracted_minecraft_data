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
   public static final MapCodec<BonusLevelTableCondition> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               Enchantment.CODEC.fieldOf("enchantment").forGetter(BonusLevelTableCondition::enchantment),
               ExtraCodecs.nonEmptyList(Codec.FLOAT.listOf()).fieldOf("chances").forGetter(BonusLevelTableCondition::values)
            )
            .apply(var0, BonusLevelTableCondition::new)
   );

   public BonusLevelTableCondition(Holder<Enchantment> enchantment, List<Float> values) {
      super();
      this.enchantment = enchantment;
      this.values = values;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.TABLE_BONUS;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.TOOL);
   }

   public boolean test(LootContext var1) {
      ItemStack var2 = var1.getParamOrNull(LootContextParams.TOOL);
      int var3 = var2 != null ? EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, var2) : 0;
      float var4 = this.values.get(Math.min(var3, this.values.size() - 1));
      return var1.getRandom().nextFloat() < var4;
   }

   public static LootItemCondition.Builder bonusLevelFlatChance(Holder<Enchantment> var0, float... var1) {
      ArrayList var2 = new ArrayList(var1.length);

      for (float var6 : var1) {
         var2.add(var6);
      }

      return () -> new BonusLevelTableCondition(var0, var2);
   }
}
