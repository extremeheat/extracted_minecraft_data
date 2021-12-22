package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WitherWallSkullBlock extends WallSkullBlock {
   protected WitherWallSkullBlock(BlockBehaviour.Properties var1) {
      super(SkullBlock.Types.WITHER_SKELETON, var1);
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
      Blocks.WITHER_SKELETON_SKULL.setPlacedBy(var1, var2, var3, var4, var5);
   }
}
