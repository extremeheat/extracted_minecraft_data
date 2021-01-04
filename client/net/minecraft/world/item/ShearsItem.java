package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ShearsItem extends Item {
   public ShearsItem(Item.Properties var1) {
      super(var1);
   }

   public boolean mineBlock(ItemStack var1, Level var2, BlockState var3, BlockPos var4, LivingEntity var5) {
      if (!var2.isClientSide) {
         var1.hurtAndBreak(1, var5, (var0) -> {
            var0.broadcastBreakEvent(EquipmentSlot.MAINHAND);
         });
      }

      Block var6 = var3.getBlock();
      return !var3.is(BlockTags.LEAVES) && var6 != Blocks.COBWEB && var6 != Blocks.GRASS && var6 != Blocks.FERN && var6 != Blocks.DEAD_BUSH && var6 != Blocks.VINE && var6 != Blocks.TRIPWIRE && !var6.is(BlockTags.WOOL) ? super.mineBlock(var1, var2, var3, var4, var5) : true;
   }

   public boolean canDestroySpecial(BlockState var1) {
      Block var2 = var1.getBlock();
      return var2 == Blocks.COBWEB || var2 == Blocks.REDSTONE_WIRE || var2 == Blocks.TRIPWIRE;
   }

   public float getDestroySpeed(ItemStack var1, BlockState var2) {
      Block var3 = var2.getBlock();
      if (var3 != Blocks.COBWEB && !var2.is(BlockTags.LEAVES)) {
         return var3.is(BlockTags.WOOL) ? 5.0F : super.getDestroySpeed(var1, var2);
      } else {
         return 15.0F;
      }
   }
}
