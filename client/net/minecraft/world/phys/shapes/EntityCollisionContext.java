package net.minecraft.world.phys.shapes;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class EntityCollisionContext implements CollisionContext {
   protected static final CollisionContext EMPTY;
   private final boolean descending;
   private final double entityBottom;
   private final boolean placement;
   private final ItemStack heldItem;
   private final boolean alwaysCollideWithFluid;
   @Nullable
   private final Entity entity;

   protected EntityCollisionContext(boolean var1, boolean var2, double var3, ItemStack var5, boolean var6, @Nullable Entity var7) {
      super();
      this.descending = var1;
      this.placement = var2;
      this.entityBottom = var3;
      this.heldItem = var5;
      this.alwaysCollideWithFluid = var6;
      this.entity = var7;
   }

   /** @deprecated */
   @Deprecated
   protected EntityCollisionContext(Entity var1, boolean var2, boolean var3) {
      boolean var10001 = var1.isDescending();
      double var10003 = var1.getY();
      ItemStack var10004;
      if (var1 instanceof LivingEntity var4) {
         var10004 = var4.getMainHandItem();
      } else {
         var10004 = ItemStack.EMPTY;
      }

      this(var10001, var3, var10003, var10004, var2, var1);
   }

   public boolean isHoldingItem(Item var1) {
      return this.heldItem.is(var1);
   }

   public boolean alwaysCollideWithFluid() {
      return this.alwaysCollideWithFluid;
   }

   public boolean canStandOnFluid(FluidState var1, FluidState var2) {
      Entity var4 = this.entity;
      if (!(var4 instanceof LivingEntity var3)) {
         return false;
      } else {
         return var3.canStandOnFluid(var2) && !var1.getType().isSame(var2.getType());
      }
   }

   public VoxelShape getCollisionShape(BlockState var1, CollisionGetter var2, BlockPos var3) {
      return var1.getCollisionShape(var2, var3, this);
   }

   public boolean isDescending() {
      return this.descending;
   }

   public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3) {
      return this.entityBottom > (double)var2.getY() + var1.max(Direction.Axis.Y) - 9.999999747378752E-6;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   public boolean isPlacement() {
      return this.placement;
   }

   static {
      EMPTY = new EntityCollisionContext(false, false, -1.7976931348623157E308, ItemStack.EMPTY, false, (Entity)null) {
         public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3) {
            return var3;
         }
      };
   }
}
