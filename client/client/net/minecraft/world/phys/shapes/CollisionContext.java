package net.minecraft.world.phys.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.FluidState;

public interface CollisionContext {
   static CollisionContext empty() {
      return EntityCollisionContext.EMPTY;
   }

   static CollisionContext of(Entity var0) {
      return new EntityCollisionContext(var0);
   }

   boolean isDescending();

   boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3);

   boolean isHoldingItem(Item var1);

   boolean canStandOnFluid(FluidState var1, FluidState var2);
}
