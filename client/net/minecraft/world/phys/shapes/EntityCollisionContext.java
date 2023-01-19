package net.minecraft.world.phys.shapes;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;

public class EntityCollisionContext implements CollisionContext {
   protected static final CollisionContext EMPTY = new EntityCollisionContext(false, -1.7976931348623157E308, ItemStack.EMPTY, var0 -> false, null) {
      @Override
      public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3) {
         return var3;
      }
   };
   private final boolean descending;
   private final double entityBottom;
   private final ItemStack heldItem;
   private final Predicate<FluidState> canStandOnFluid;
   @Nullable
   private final Entity entity;

   protected EntityCollisionContext(boolean var1, double var2, ItemStack var4, Predicate<FluidState> var5, @Nullable Entity var6) {
      super();
      this.descending = var1;
      this.entityBottom = var2;
      this.heldItem = var4;
      this.canStandOnFluid = var5;
      this.entity = var6;
   }

   @Deprecated
   protected EntityCollisionContext(Entity var1) {
      this(
         var1.isDescending(),
         var1.getY(),
         var1 instanceof LivingEntity ? ((LivingEntity)var1).getMainHandItem() : ItemStack.EMPTY,
         var1 instanceof LivingEntity ? ((LivingEntity)var1)::canStandOnFluid : var0 -> false,
         var1
      );
   }

   @Override
   public boolean isHoldingItem(Item var1) {
      return this.heldItem.is(var1);
   }

   @Override
   public boolean canStandOnFluid(FluidState var1, FluidState var2) {
      return this.canStandOnFluid.test(var2) && !var1.getType().isSame(var2.getType());
   }

   @Override
   public boolean isDescending() {
      return this.descending;
   }

   @Override
   public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3) {
      return this.entityBottom > (double)var2.getY() + var1.max(Direction.Axis.Y) - 9.999999747378752E-6;
   }

   @Nullable
   public Entity getEntity() {
      return this.entity;
   }
}
