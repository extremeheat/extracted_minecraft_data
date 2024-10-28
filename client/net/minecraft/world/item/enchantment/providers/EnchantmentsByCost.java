package net.minecraft.world.item.enchantment.providers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public record EnchantmentsByCost(HolderSet<Enchantment> enchantments, IntProvider cost) implements EnchantmentProvider {
   public static final MapCodec<EnchantmentsByCost> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).fieldOf("enchantments").forGetter(EnchantmentsByCost::enchantments), IntProvider.CODEC.fieldOf("cost").forGetter(EnchantmentsByCost::cost)).apply(var0, EnchantmentsByCost::new);
   });

   public EnchantmentsByCost(HolderSet<Enchantment> enchantments, IntProvider cost) {
      super();
      this.enchantments = enchantments;
      this.cost = cost;
   }

   public void enchant(ItemStack var1, ItemEnchantments.Mutable var2, RandomSource var3, Level var4, BlockPos var5) {
      List var6 = EnchantmentHelper.selectEnchantment(var3, var1, this.cost.sample(var3), this.enchantments.stream());
      Iterator var7 = var6.iterator();

      while(var7.hasNext()) {
         EnchantmentInstance var8 = (EnchantmentInstance)var7.next();
         var2.upgrade(var8.enchantment, var8.level);
      }

   }

   public MapCodec<EnchantmentsByCost> codec() {
      return CODEC;
   }

   public HolderSet<Enchantment> enchantments() {
      return this.enchantments;
   }

   public IntProvider cost() {
      return this.cost;
   }
}
