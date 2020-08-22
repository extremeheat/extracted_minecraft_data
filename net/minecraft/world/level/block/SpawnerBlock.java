package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerBlock extends BaseEntityBlock {
   protected SpawnerBlock(Block.Properties var1) {
      super(var1);
   }

   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new SpawnerBlockEntity();
   }

   public void spawnAfterBreak(BlockState var1, Level var2, BlockPos var3, ItemStack var4) {
      super.spawnAfterBreak(var1, var2, var3, var4);
      int var5 = 15 + var2.random.nextInt(15) + var2.random.nextInt(15);
      this.popExperience(var2, var3, var5);
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return ItemStack.EMPTY;
   }
}
