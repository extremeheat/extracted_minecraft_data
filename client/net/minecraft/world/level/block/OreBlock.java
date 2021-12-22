package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class OreBlock extends Block {
   private final UniformInt xpRange;

   public OreBlock(BlockBehaviour.Properties var1) {
      this(var1, UniformInt.method_45(0, 0));
   }

   public OreBlock(BlockBehaviour.Properties var1, UniformInt var2) {
      super(var1);
      this.xpRange = var2;
   }

   public void spawnAfterBreak(BlockState var1, ServerLevel var2, BlockPos var3, ItemStack var4) {
      super.spawnAfterBreak(var1, var2, var3, var4);
      if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, var4) == 0) {
         int var5 = this.xpRange.sample(var2.random);
         if (var5 > 0) {
            this.popExperience(var2, var3, var5);
         }
      }

   }
}
