package net.minecraft.world.phys.shapes;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public interface CollisionContext {
   static CollisionContext empty() {
      return EntityCollisionContext.Empty.WITHOUT_FLUID_COLLISIONS;
   }

   static CollisionContext emptyWithFluidCollisions() {
      return EntityCollisionContext.Empty.WITH_FLUID_COLLISIONS;
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
            var10000 = AbstractMinecart.useExperimentalMovement(var3.level()) ? new MinecartCollisionContext(var3, false) : new EntityCollisionContext(var0, false, false);
            break;
         default:
            var10000 = new EntityCollisionContext(var0, false, false);
      }

      return (CollisionContext)var10000;
   }

   static CollisionContext of(Entity var0, boolean var1) {
      return new EntityCollisionContext(var0, var1, false);
   }

   static CollisionContext placementContext(@Nullable Player var0) {
      return new EntityCollisionContext(var0 != null ? var0.isDescending() : false, true, var0 != null ? var0.getY() : -1.7976931348623157E308, var0 instanceof LivingEntity ? ((LivingEntity)var0).getMainHandItem() : ItemStack.EMPTY, false, var0);
   }

   static CollisionContext withPosition(@Nullable Entity var0, double var1) {
      EntityCollisionContext var10000 = new EntityCollisionContext;
      boolean var10002 = var0 != null ? var0.isDescending() : false;
      double var10004 = var0 != null ? var1 : -1.7976931348623157E308;
      ItemStack var10005;
      if (var0 instanceof LivingEntity var3) {
         var10005 = var3.getMainHandItem();
      } else {
         var10005 = ItemStack.EMPTY;
      }

      var10000.<init>(var10002, true, var10004, var10005, false, var0);
      return var10000;
   }

   boolean isDescending();

   boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3);

   boolean isHoldingItem(Item var1);

   boolean alwaysCollideWithFluid();

   boolean canStandOnFluid(FluidState var1, FluidState var2);

   VoxelShape getCollisionShape(BlockState var1, CollisionGetter var2, BlockPos var3);

   default boolean isPlacement() {
      return false;
   }
}
