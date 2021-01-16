package net.minecraft.world.level.block;

import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class AbstractChestBlock<E extends BlockEntity> extends BaseEntityBlock {
   protected final Supplier<BlockEntityType<? extends E>> blockEntityType;

   protected AbstractChestBlock(BlockBehaviour.Properties var1, Supplier<BlockEntityType<? extends E>> var2) {
      super(var1);
      this.blockEntityType = var2;
   }
}
