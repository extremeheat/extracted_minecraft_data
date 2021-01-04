package net.minecraft.world.phys.shapes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class EntityCollisionContext implements CollisionContext {
   protected static final CollisionContext EMPTY;
   private final boolean sneaking;
   private final double entityBottom;
   private final Item heldItem;

   protected EntityCollisionContext(boolean var1, double var2, Item var4) {
      super();
      this.sneaking = var1;
      this.entityBottom = var2;
      this.heldItem = var4;
   }

   @Deprecated
   protected EntityCollisionContext(Entity var1) {
      this(var1.isSneaking(), var1.getBoundingBox().minY, var1 instanceof LivingEntity ? ((LivingEntity)var1).getMainHandItem().getItem() : Items.AIR);
   }

   public boolean isHoldingItem(Item var1) {
      return this.heldItem == var1;
   }

   public boolean isSneaking() {
      return this.sneaking;
   }

   public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3) {
      return this.entityBottom > (double)var2.getY() + var1.max(Direction.Axis.Y) - 9.999999747378752E-6D;
   }

   static {
      EMPTY = new EntityCollisionContext(false, -1.7976931348623157E308D, Items.AIR) {
         public boolean isAbove(VoxelShape var1, BlockPos var2, boolean var3) {
            return var3;
         }
      };
   }
}
