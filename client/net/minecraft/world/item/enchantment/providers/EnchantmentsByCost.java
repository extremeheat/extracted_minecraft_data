package net.minecraft.world.item.enchantment.providers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public record EnchantmentsByCost(HolderSet<Enchantment> enchantments, IntProvider cost) implements EnchantmentProvider {
   public static final MapCodec<EnchantmentsByCost> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).fieldOf("enchantments").forGetter(EnchantmentsByCost::enchantments),
               IntProvider.CODEC.fieldOf("cost").forGetter(EnchantmentsByCost::cost)
            )
            .apply(var0, EnchantmentsByCost::new)
   );

   public EnchantmentsByCost(HolderSet<Enchantment> enchantments, IntProvider cost) {
      super();
      this.enchantments = enchantments;
      this.cost = cost;
   }

   @Override
   public void enchant(ItemStack var1, ItemEnchantments.Mutable var2, RandomSource var3, DifficultyInstance var4) {
      for (EnchantmentInstance var7 : EnchantmentHelper.selectEnchantment(var3, var1, this.cost.sample(var3), this.enchantments.stream())) {
         var2.upgrade(var7.enchantment, var7.level);
      }
   }

   @Override
   public MapCodec<EnchantmentsByCost> codec() {
      return CODEC;
   }
}
