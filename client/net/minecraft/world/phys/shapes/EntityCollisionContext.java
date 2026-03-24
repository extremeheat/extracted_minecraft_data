package net.minecraft.world.phys.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

public class EntityCollisionContext implements CollisionContext {
   private final boolean descending;
   private final double entityBottom;
   private final boolean placement;
   private final ItemStack heldItem;
   private final boolean alwaysCollideWithFluid;
   private final @Nullable Entity entity;

   protected EntityCollisionContext(final boolean descending, final boolean placement, final double entityBottom, final ItemStack heldItem, final boolean alwaysCollideWithFluid, final @Nullable Entity entity) {
      super();
      this.descending = descending;
      this.placement = placement;
      this.entityBottom = entityBottom;
      this.heldItem = heldItem;
      this.alwaysCollideWithFluid = alwaysCollideWithFluid;
      this.entity = entity;
   }

   /** @deprecated */
   @Deprecated
   protected EntityCollisionContext(final Entity entity, final boolean alwaysCollideWithFluid, final boolean placement) {
      boolean var10001 = entity.isDescending();
      double var10003 = entity.getY();
      ItemStack var10004;
      if (entity instanceof LivingEntity livingEntity) {
         var10004 = livingEntity.getMainHandItem();
      } else {
         var10004 = ItemStack.EMPTY;
      }

      this(var10001, placement, var10003, var10004, alwaysCollideWithFluid, entity);
   }

   public boolean isHoldingItem(final Item item) {
      return this.heldItem.is(item);
   }

   public boolean alwaysCollideWithFluid() {
      return this.alwaysCollideWithFluid;
   }

   public boolean canStandOnFluid(final FluidState fluidStateAbove, final FluidState fluid) {
      Entity var4 = this.entity;
      if (!(var4 instanceof LivingEntity livingEntity)) {
         return false;
      } else {
         return livingEntity.canStandOnFluid(fluid) && !fluidStateAbove.getType().isSame(fluid.getType());
      }
   }

   public VoxelShape getCollisionShape(final BlockState state, final CollisionGetter collisionGetter, final BlockPos pos) {
      return state.getCollisionShape(collisionGetter, pos, this);
   }

   public boolean isDescending() {
      return this.descending;
   }

   public boolean isAbove(final VoxelShape shape, final BlockPos pos, final boolean defaultValue) {
      return this.entityBottom > (double)pos.getY() + shape.max(Direction.Axis.Y) - 9.999999747378752E-6;
   }

   public @Nullable Entity getEntity() {
      return this.entity;
   }

   public boolean isPlacement() {
      return this.placement;
   }

   protected static class Empty extends EntityCollisionContext {
      protected static final CollisionContext WITHOUT_FLUID_COLLISIONS = new Empty(false);
      protected static final CollisionContext WITH_FLUID_COLLISIONS = new Empty(true);

      public Empty(final boolean alwaysCollideWithFluid) {
         super(false, false, -1.7976931348623157E308, ItemStack.EMPTY, alwaysCollideWithFluid, (Entity)null);
      }

      public boolean isAbove(final VoxelShape shape, final BlockPos pos, final boolean defaultValue) {
         return defaultValue;
      }
   }
}
