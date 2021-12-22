package net.minecraft.world.item.context;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class DirectionalPlaceContext extends BlockPlaceContext {
   private final Direction direction;

   public DirectionalPlaceContext(Level var1, BlockPos var2, Direction var3, ItemStack var4, Direction var5) {
      super(var1, (Player)null, InteractionHand.MAIN_HAND, var4, new BlockHitResult(Vec3.atBottomCenterOf(var2), var5, var2, false));
      this.direction = var3;
   }

   public BlockPos getClickedPos() {
      return this.getHitResult().getBlockPos();
   }

   public boolean canPlace() {
      return this.getLevel().getBlockState(this.getHitResult().getBlockPos()).canBeReplaced(this);
   }

   public boolean replacingClickedOnBlock() {
      return this.canPlace();
   }

   public Direction getNearestLookingDirection() {
      return Direction.DOWN;
   }

   public Direction[] getNearestLookingDirections() {
      switch(this.direction) {
      case DOWN:
      default:
         return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.field_526};
      case field_526:
         return new Direction[]{Direction.DOWN, Direction.field_526, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
      case NORTH:
         return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.field_526, Direction.SOUTH};
      case SOUTH:
         return new Direction[]{Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.field_526, Direction.NORTH};
      case WEST:
         return new Direction[]{Direction.DOWN, Direction.WEST, Direction.SOUTH, Direction.field_526, Direction.NORTH, Direction.EAST};
      case EAST:
         return new Direction[]{Direction.DOWN, Direction.EAST, Direction.SOUTH, Direction.field_526, Direction.NORTH, Direction.WEST};
      }
   }

   public Direction getHorizontalDirection() {
      return this.direction.getAxis() == Direction.Axis.field_501 ? Direction.NORTH : this.direction;
   }

   public boolean isSecondaryUseActive() {
      return false;
   }

   public float getRotation() {
      return (float)(this.direction.get2DDataValue() * 90);
   }
}
