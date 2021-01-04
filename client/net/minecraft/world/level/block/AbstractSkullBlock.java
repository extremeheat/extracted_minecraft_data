package net.minecraft.world.level.block;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractSkullBlock extends BaseEntityBlock {
   private final SkullBlock.Type type;

   public AbstractSkullBlock(SkullBlock.Type var1, Block.Properties var2) {
      super(var2);
      this.type = var1;
   }

   public boolean hasCustomBreakingProgress(BlockState var1) {
      return true;
   }

   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new SkullBlockEntity();
   }

   public SkullBlock.Type getType() {
      return this.type;
   }
}
