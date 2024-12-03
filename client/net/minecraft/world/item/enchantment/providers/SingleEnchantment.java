package net.minecraft.world.item.enchantment.providers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public record SingleEnchantment(Holder<Enchantment> enchantment, IntProvider level) implements EnchantmentProvider {
   public static final MapCodec<SingleEnchantment> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Enchantment.CODEC.fieldOf("enchantment").forGetter(SingleEnchantment::enchantment), IntProvider.CODEC.fieldOf("level").forGetter(SingleEnchantment::level)).apply(var0, SingleEnchantment::new));

   public SingleEnchantment(Holder<Enchantment> var1, IntProvider var2) {
      super();
      this.enchantment = var1;
      this.level = var2;
   }

   public void enchant(ItemStack var1, ItemEnchantments.Mutable var2, RandomSource var3, DifficultyInstance var4) {
      var2.upgrade(this.enchantment, Mth.clamp(this.level.sample(var3), ((Enchantment)this.enchantment.value()).getMinLevel(), ((Enchantment)this.enchantment.value()).getMaxLevel()));
   }

   public MapCodec<SingleEnchantment> codec() {
      return CODEC;
   }
}
