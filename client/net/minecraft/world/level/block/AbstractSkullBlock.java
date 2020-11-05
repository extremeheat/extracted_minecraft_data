package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public abstract class AbstractSkullBlock extends BaseEntityBlock implements Wearable {
   private final SkullBlock.Type type;

   public AbstractSkullBlock(SkullBlock.Type var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.type = var1;
   }

   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new SkullBlockEntity();
   }

   public SkullBlock.Type getType() {
      return this.type;
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }
}
