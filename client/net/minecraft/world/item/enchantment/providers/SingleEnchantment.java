package net.minecraft.world.item.enchantment.providers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public record SingleEnchantment(Holder<Enchantment> enchantment, IntProvider level) implements EnchantmentProvider {
   public static final MapCodec<SingleEnchantment> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Enchantment.CODEC.fieldOf("enchantment").forGetter(SingleEnchantment::enchantment), IntProvider.CODEC.fieldOf("level").forGetter(SingleEnchantment::level)).apply(var0, SingleEnchantment::new);
   });

   public SingleEnchantment(Holder<Enchantment> enchantment, IntProvider level) {
      super();
      this.enchantment = enchantment;
      this.level = level;
   }

   public void enchant(ItemStack var1, ItemEnchantments.Mutable var2, RandomSource var3, Level var4, BlockPos var5) {
      var2.upgrade(this.enchantment, Mth.clamp(this.level.sample(var3), ((Enchantment)this.enchantment.value()).getMinLevel(), ((Enchantment)this.enchantment.value()).getMaxLevel()));
   }

   public MapCodec<SingleEnchantment> codec() {
      return CODEC;
   }

   public Holder<Enchantment> enchantment() {
      return this.enchantment;
   }

   public IntProvider level() {
      return this.level;
   }
}
