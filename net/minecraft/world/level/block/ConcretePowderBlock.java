package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class ConcretePowderBlock extends FallingBlock {
   private final BlockState concrete;

   public ConcretePowderBlock(Block var1, Block.Properties var2) {
      super(var2);
      this.concrete = var1.defaultBlockState();
   }

   public void onLand(Level var1, BlockPos var2, BlockState var3, BlockState var4) {
      if (canSolidify(var4)) {
         var1.setBlock(var2, this.concrete, 3);
      }

   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      return !canSolidify(var2.getBlockState(var3)) && !touchesLiquid(var2, var3) ? super.getStateForPlacement(var1) : this.concrete;
   }

   private static boolean touchesLiquid(BlockGetter var0, BlockPos var1) {
      boolean var2 = false;
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos(var1);
      Direction[] var4 = Direction.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction var7 = var4[var6];
         BlockState var8 = var0.getBlockState(var3);
         if (var7 != Direction.DOWN || canSolidify(var8)) {
            var3.set((Vec3i)var1).move(var7);
            var8 = var0.getBlockState(var3);
            if (canSolidify(var8) && !var8.isFaceSturdy(var0, var1, var7.getOpposite())) {
               var2 = true;
               break;
            }
         }
      }

      return var2;
   }

   private static boolean canSolidify(BlockState var0) {
      return var0.getFluidState().is(FluidTags.WATER);
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return touchesLiquid(var4, var5) ? this.concrete : super.updateShape(var1, var2, var3, var4, var5, var6);
   }
}
