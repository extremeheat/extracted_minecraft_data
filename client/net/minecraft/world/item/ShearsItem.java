package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ShearsItem extends Item {
   public ShearsItem(Item.Properties var1) {
      super(var1);
   }

   public boolean mineBlock(ItemStack var1, Level var2, BlockState var3, BlockPos var4, LivingEntity var5) {
      if (!var2.isClientSide && !var3.getBlock().is((Tag)BlockTags.FIRE)) {
         var1.hurtAndBreak(1, var5, (var0) -> {
            var0.broadcastBreakEvent(EquipmentSlot.MAINHAND);
         });
      }

      return !var3.is(BlockTags.LEAVES) && !var3.is(Blocks.COBWEB) && !var3.is(Blocks.GRASS) && !var3.is(Blocks.FERN) && !var3.is(Blocks.DEAD_BUSH) && !var3.is(Blocks.VINE) && !var3.is(Blocks.TRIPWIRE) && !var3.is(BlockTags.WOOL) ? super.mineBlock(var1, var2, var3, var4, var5) : true;
   }

   public boolean isCorrectToolForDrops(BlockState var1) {
      return var1.is(Blocks.COBWEB) || var1.is(Blocks.REDSTONE_WIRE) || var1.is(Blocks.TRIPWIRE);
   }

   public float getDestroySpeed(ItemStack var1, BlockState var2) {
      if (!var2.is(Blocks.COBWEB) && !var2.is(BlockTags.LEAVES)) {
         return var2.is(BlockTags.WOOL) ? 5.0F : super.getDestroySpeed(var1, var2);
      } else {
         return 15.0F;
      }
   }
}
