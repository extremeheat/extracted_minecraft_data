package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class OreBlock extends Block {
   public OreBlock(Block.Properties var1) {
      super(var1);
   }

   protected int xpOnDrop(Random var1) {
      if (this == Blocks.COAL_ORE) {
         return Mth.nextInt(var1, 0, 2);
      } else if (this == Blocks.DIAMOND_ORE) {
         return Mth.nextInt(var1, 3, 7);
      } else if (this == Blocks.EMERALD_ORE) {
         return Mth.nextInt(var1, 3, 7);
      } else if (this == Blocks.LAPIS_ORE) {
         return Mth.nextInt(var1, 2, 5);
      } else {
         return this == Blocks.NETHER_QUARTZ_ORE ? Mth.nextInt(var1, 2, 5) : 0;
      }
   }

   public void spawnAfterBreak(BlockState var1, Level var2, BlockPos var3, ItemStack var4) {
      super.spawnAfterBreak(var1, var2, var3, var4);
      if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, var4) == 0) {
         int var5 = this.xpOnDrop(var2.random);
         if (var5 > 0) {
            this.popExperience(var2, var3, var5);
         }
      }

   }
}
