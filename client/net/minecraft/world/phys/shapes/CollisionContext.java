package net.minecraft.world.phys.shapes;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

public interface CollisionContext {
   static CollisionContext empty() {
      return EntityCollisionContext.Empty.WITHOUT_FLUID_COLLISIONS;
   }

   static CollisionContext emptyWithFluidCollisions() {
      return EntityCollisionContext.Empty.WITH_FLUID_COLLISIONS;
   }

   static CollisionContext of(final Entity entity) {
      Objects.requireNonNull(entity);
      byte var2 = 0;
      Object var10000;
      //$FF: var2->value
      //0->net/minecraft/world/entity/vehicle/minecart/AbstractMinecart
      switch (entity.typeSwitch<invokedynamic>(entity, var2)) {
         case 0:
            AbstractMinecart minecart = (AbstractMinecart)entity;
            var10000 = AbstractMinecart.useExperimentalMovement(minecart.level()) ? new MinecartCollisionContext(minecart, false) : new EntityCollisionContext(entity, false, false);
            break;
         default:
            var10000 = new EntityCollisionContext(entity, false, false);
      }

      return (CollisionContext)var10000;
   }

   static CollisionContext of(final Entity entity, final boolean alwaysCollideWithFluid) {
      return new EntityCollisionContext(entity, alwaysCollideWithFluid, false);
   }

   static CollisionContext placementContext(final @Nullable Player player) {
      return new EntityCollisionContext(player != null ? player.isDescending() : false, true, player != null ? player.getY() : -1.7976931348623157E308, player instanceof LivingEntity ? ((LivingEntity)player).getMainHandItem() : ItemStack.EMPTY, false, player);
   }

   static CollisionContext withPosition(final @Nullable Entity entity, final double position) {
      EntityCollisionContext var10000 = new EntityCollisionContext;
      boolean var10002 = entity != null ? entity.isDescending() : false;
      double var10004 = entity != null ? position : -1.7976931348623157E308;
      ItemStack var10005;
      if (entity instanceof LivingEntity livingEntity) {
         var10005 = livingEntity.getMainHandItem();
      } else {
         var10005 = ItemStack.EMPTY;
      }

      var10000.<init>(var10002, true, var10004, var10005, false, entity);
      return var10000;
   }

   boolean isDescending();

   boolean isAbove(final VoxelShape shape, final BlockPos pos, final boolean defaultValue);

   boolean isHoldingItem(final Item item);

   boolean alwaysCollideWithFluid();

   boolean canStandOnFluid(final FluidState fluidStateAbove, final FluidState fluid);

   VoxelShape getCollisionShape(BlockState state, CollisionGetter collisionGetter, BlockPos pos);

   default boolean isPlacement() {
      return false;
   }
}
