package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record BonusLevelTableCondition(Holder<Enchantment> b, List<Float> c) implements LootItemCondition {
   private final Holder<Enchantment> enchantment;
   private final List<Float> values;
   public static final Codec<BonusLevelTableCondition> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               BuiltInRegistries.ENCHANTMENT.holderByNameCodec().fieldOf("enchantment").forGetter(BonusLevelTableCondition::enchantment),
               Codec.FLOAT.listOf().fieldOf("chances").forGetter(BonusLevelTableCondition::values)
            )
            .apply(var0, BonusLevelTableCondition::new)
   );

   public BonusLevelTableCondition(Holder<Enchantment> var1, List<Float> var2) {
      super();
      this.enchantment = var1;
      this.values = var2;
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
      int var3 = var2 != null ? EnchantmentHelper.getItemEnchantmentLevel(this.enchantment.value(), var2) : 0;
      float var4 = this.values.get(Math.min(var3, this.values.size() - 1));
      return var1.getRandom().nextFloat() < var4;
   }

   public static LootItemCondition.Builder bonusLevelFlatChance(Enchantment var0, float... var1) {
      ArrayList var2 = new ArrayList(var1.length);

      for(float var6 : var1) {
         var2.add(var6);
      }

      return () -> new BonusLevelTableCondition(var0.builtInRegistryHolder(), var2);
   }
}
