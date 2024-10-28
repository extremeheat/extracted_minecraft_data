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
      byte var2 = 0;
      Object var10000;
      //$FF: var2->value
      //0->net/minecraft/world/entity/vehicle/AbstractMinecart
      switch (var0.typeSwitch<invokedynamic>(var0, var2)) {
         case 0:
            AbstractMinecart var3 = (AbstractMinecart)var0;
            var10000 = AbstractMinecart.useExperimentalMovement(var3.level()) ? new MinecartCollisionContext(var3, false) : new EntityCollisionContext(var0, false);
            break;
         default:
            var10000 = new EntityCollisionContext(var0, false);
      }

      return (CollisionContext)var10000;
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
