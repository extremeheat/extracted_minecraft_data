package net.minecraft.world.item.enchantment.providers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

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
   public void enchant(ItemStack var1, ItemEnchantments.Mutable var2, RandomSource var3, Level var4, BlockPos var5) {
      float var6 = var4.getCurrentDifficultyAt(var5).getSpecialMultiplier();
      int var7 = Mth.randomBetweenInclusive(var3, this.minCost, this.minCost + (int)(var6 * (float)this.maxCostSpan));

      for (EnchantmentInstance var10 : EnchantmentHelper.selectEnchantment(var3, var1, var7, this.enchantments.stream())) {
         var2.upgrade(var10.enchantment, var10.level);
      }
   }

   @Override
   public MapCodec<EnchantmentsByCostWithDifficulty> codec() {
      return CODEC;
   }
}
