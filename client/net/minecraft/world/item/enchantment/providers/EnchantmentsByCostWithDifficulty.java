package net.minecraft.world.item.enchantment.providers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public record EnchantmentsByCostWithDifficulty(HolderSet<Enchantment> enchantments, int minCost, int maxCostSpan) implements EnchantmentProvider {
   public static final MapCodec<EnchantmentsByCostWithDifficulty> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).fieldOf("enchantments").forGetter(EnchantmentsByCostWithDifficulty::enchantments),
               ExtraCodecs.POSITIVE_INT.fieldOf("min_cost").forGetter(EnchantmentsByCostWithDifficulty::minCost),
               ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_cost_span").forGetter(EnchantmentsByCostWithDifficulty::maxCostSpan)
            )
            .apply(var0, EnchantmentsByCostWithDifficulty::new)
   );

   public EnchantmentsByCostWithDifficulty(HolderSet<Enchantment> enchantments, int minCost, int maxCostSpan) {
      super();
      this.enchantments = enchantments;
      this.minCost = minCost;
      this.maxCostSpan = maxCostSpan;
   }

   @Override
   public void enchant(ItemStack var1, ItemEnchantments.Mutable var2, RandomSource var3, DifficultyInstance var4) {
      float var5 = var4.getSpecialMultiplier();
      int var6 = Mth.randomBetweenInclusive(var3, this.minCost, this.minCost + (int)(var5 * (float)this.maxCostSpan));

      for (EnchantmentInstance var9 : EnchantmentHelper.selectEnchantment(var3, var1, var6, this.enchantments.stream())) {
         var2.upgrade(var9.enchantment, var9.level);
      }
   }

   @Override
   public MapCodec<EnchantmentsByCostWithDifficulty> codec() {
      return CODEC;
   }
}
