package net.minecraft.world.item.context;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class BlockPlaceContext extends UseOnContext {
   private final BlockPos relativePos;
   protected boolean replaceClicked = true;

   public BlockPlaceContext(Player var1, InteractionHand var2, ItemStack var3, BlockHitResult var4) {
      this(var1.level, var1, var2, var3, var4);
   }

   public BlockPlaceContext(UseOnContext var1) {
      this(var1.getLevel(), var1.getPlayer(), var1.getHand(), var1.getItemInHand(), var1.getHitResult());
   }

   protected BlockPlaceContext(Level var1, @Nullable Player var2, InteractionHand var3, ItemStack var4, BlockHitResult var5) {
      super(var1, var2, var3, var4, var5);
      this.relativePos = var5.getBlockPos().relative(var5.getDirection());
      this.replaceClicked = var1.getBlockState(var5.getBlockPos()).canBeReplaced(this);
   }

   public static BlockPlaceContext at(BlockPlaceContext var0, BlockPos var1, Direction var2) {
      return new BlockPlaceContext(
         var0.getLevel(),
         var0.getPlayer(),
         var0.getHand(),
         var0.getItemInHand(),
         new BlockHitResult(
            new Vec3(
               (double)var1.getX() + 0.5 + (double)var2.getStepX() * 0.5,
               (double)var1.getY() + 0.5 + (double)var2.getStepY() * 0.5,
               (double)var1.getZ() + 0.5 + (double)var2.getStepZ() * 0.5
            ),
            var2,
            var1,
            false
         )
      );
   }

   @Override
   public BlockPos getClickedPos() {
      return this.replaceClicked ? super.getClickedPos() : this.relativePos;
   }

   public boolean canPlace() {
      return this.replaceClicked || this.getLevel().getBlockState(this.getClickedPos()).canBeReplaced(this);
   }

   public boolean replacingClickedOnBlock() {
      return this.replaceClicked;
   }

   public Direction getNearestLookingDirection() {
      return Direction.orderedByNearest(this.getPlayer())[0];
   }

   public Direction getNearestLookingVerticalDirection() {
      return Direction.getFacingAxis(this.getPlayer(), Direction.Axis.Y);
   }

   public Direction[] getNearestLookingDirections() {
      Direction[] var1 = Direction.orderedByNearest(this.getPlayer());
      if (this.replaceClicked) {
         return var1;
      } else {
         Direction var2 = this.getClickedFace();
         int var3 = 0;

         while(var3 < var1.length && var1[var3] != var2.getOpposite()) {
            ++var3;
         }

         if (var3 > 0) {
            System.arraycopy(var1, 0, var1, 1, var3);
            var1[0] = var2.getOpposite();
         }

         return var1;
      }
   }
}
