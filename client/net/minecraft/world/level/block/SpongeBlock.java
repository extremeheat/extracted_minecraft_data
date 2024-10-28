package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SpongeBlock extends Block {
   public static final MapCodec<SpongeBlock> CODEC = simpleCodec(SpongeBlock::new);
   public static final int MAX_DEPTH = 6;
   public static final int MAX_COUNT = 64;
   private static final Direction[] ALL_DIRECTIONS = Direction.values();

   public MapCodec<SpongeBlock> codec() {
      return CODEC;
   }

   protected SpongeBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock())) {
         this.tryAbsorbWater(var2, var3);
      }
   }

   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      this.tryAbsorbWater(var2, var3);
      super.neighborChanged(var1, var2, var3, var4, var5, var6);
   }

   protected void tryAbsorbWater(Level var1, BlockPos var2) {
      if (this.removeWaterBreadthFirstSearch(var1, var2)) {
         var1.setBlock(var2, Blocks.WET_SPONGE.defaultBlockState(), 2);
         var1.playSound((Player)null, (BlockPos)var2, SoundEvents.SPONGE_ABSORB, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

   }

   private boolean removeWaterBreadthFirstSearch(Level var1, BlockPos var2) {
      return BlockPos.breadthFirstTraversal(var2, 6, 65, (var0, var1x) -> {
         Direction[] var2 = ALL_DIRECTIONS;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Direction var5 = var2[var4];
            var1x.accept(var0.relative(var5));
         }

      }, (var2x) -> {
         if (var2x.equals(var2)) {
            return true;
         } else {
            BlockState var3 = var1.getBlockState(var2x);
            FluidState var4 = var1.getFluidState(var2x);
            if (!var4.is(FluidTags.WATER)) {
               return false;
            } else {
               Block var6 = var3.getBlock();
               if (var6 instanceof BucketPickup) {
                  BucketPickup var5 = (BucketPickup)var6;
                  if (!var5.pickupBlock((Player)null, var1, var2x, var3).isEmpty()) {
                     return true;
                  }
               }

               if (var3.getBlock() instanceof LiquidBlock) {
                  var1.setBlock(var2x, Blocks.AIR.defaultBlockState(), 3);
               } else {
                  if (!var3.is(Blocks.KELP) && !var3.is(Blocks.KELP_PLANT) && !var3.is(Blocks.SEAGRASS) && !var3.is(Blocks.TALL_SEAGRASS)) {
                     return false;
                  }

                  BlockEntity var7 = var3.hasBlockEntity() ? var1.getBlockEntity(var2x) : null;
                  dropResources(var3, var1, var2x, var7);
                  var1.setBlock(var2x, Blocks.AIR.defaultBlockState(), 3);
               }

               return true;
            }
         }
      }) > 1;
   }
}
