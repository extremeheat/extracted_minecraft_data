package net.minecraft.world.phys.shapes;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public interface CollisionContext {
   static CollisionContext empty() {
      return EntityCollisionContext.EMPTY;
   }

   static CollisionContext of(Entity var0) {
      Objects.requireNonNull(var0);

      return (CollisionContext)(switch (var0) {
         case AbstractMinecart var3 -> AbstractMinecart.useExperimentalMovement(var3.level())
         ? new MinecartCollisionContext(var3, false)
         : new EntityCollisionContext((Entity)var0, false);
         default -> new EntityCollisionContext((Entity)var0, false);
      });
   }

   static CollisionContext of(Entity var0, boolean var1) {
      return new EntityCollisionContext(var0, var1);
   }

   boolean isDescending();

   boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3);

   boolean isHoldingItem(Item var1);

   boolean canStandOnFluid(FluidState var1, FluidState var2);

   VoxelShape getCollisionShape(BlockState var1, CollisionGetter var2, BlockPos var3);
}
