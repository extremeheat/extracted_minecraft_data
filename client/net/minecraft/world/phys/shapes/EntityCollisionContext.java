package net.minecraft.world.phys.shapes;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class EntityCollisionContext implements CollisionContext {
   protected static final CollisionContext EMPTY;
   private final boolean descending;
   private final double entityBottom;
   private final ItemStack heldItem;
   private final Predicate<Fluid> canStandOnFluid;
   @Nullable
   private final Entity entity;

   protected EntityCollisionContext(boolean var1, double var2, ItemStack var4, Predicate<Fluid> var5, @Nullable Entity var6) {
      super();
      this.descending = var1;
      this.entityBottom = var2;
      this.heldItem = var4;
      this.canStandOnFluid = var5;
      this.entity = var6;
   }

   /** @deprecated */
   @Deprecated
   protected EntityCollisionContext(Entity var1) {
      boolean var10001 = var1.isDescending();
      double var10002 = var1.getY();
      ItemStack var10003 = var1 instanceof LivingEntity ? ((LivingEntity)var1).getMainHandItem() : ItemStack.EMPTY;
      Predicate var2;
      if (var1 instanceof LivingEntity) {
         LivingEntity var10004 = (LivingEntity)var1;
         Objects.requireNonNull((LivingEntity)var1);
         var2 = var10004::canStandOnFluid;
      } else {
         var2 = (var0) -> {
            return false;
         };
      }

      this(var10001, var10002, var10003, var2, var1);
   }

   public boolean isHoldingItem(Item var1) {
      return this.heldItem.method_87(var1);
   }

   public boolean canStandOnFluid(FluidState var1, FlowingFluid var2) {
      return this.canStandOnFluid.test(var2) && !var1.getType().isSame(var2);
   }

   public boolean isDescending() {
      return this.descending;
   }

   public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3) {
      return this.entityBottom > (double)var2.getY() + var1.max(Direction.Axis.field_501) - 9.999999747378752E-6D;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }

   static {
      EMPTY = new EntityCollisionContext(false, -1.7976931348623157E308D, ItemStack.EMPTY, (var0) -> {
         return false;
      }, (Entity)null) {
         public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3) {
            return var3;
         }
      };
   }
}
