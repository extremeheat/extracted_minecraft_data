package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class FrostedIceBlock extends IceBlock {
   public static final IntegerProperty AGE;

   public FrostedIceBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   public void tick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if ((var4.nextInt(3) == 0 || this.fewerNeigboursThan(var2, var3, 4)) && var2.getMaxLocalRawBrightness(var3) > 11 - (Integer)var1.getValue(AGE) - var1.getLightBlock(var2, var3) && this.slightlyMelt(var1, var2, var3)) {
         BlockPos.PooledMutableBlockPos var5 = BlockPos.PooledMutableBlockPos.acquire();
         Throwable var6 = null;

         try {
            Direction[] var7 = Direction.values();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Direction var10 = var7[var9];
               var5.set((Vec3i)var3).move(var10);
               BlockState var11 = var2.getBlockState(var5);
               if (var11.getBlock() == this && !this.slightlyMelt(var11, var2, var5)) {
                  var2.getBlockTicks().scheduleTick(var5, this, Mth.nextInt(var4, 20, 40));
               }
            }
         } catch (Throwable var19) {
            var6 = var19;
            throw var19;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var18) {
                     var6.addSuppressed(var18);
                  }
               } else {
                  var5.close();
               }
            }

         }

      } else {
         var2.getBlockTicks().scheduleTick(var3, this, Mth.nextInt(var4, 20, 40));
      }
   }

   private boolean slightlyMelt(BlockState var1, Level var2, BlockPos var3) {
      int var4 = (Integer)var1.getValue(AGE);
      if (var4 < 3) {
         var2.setBlock(var3, (BlockState)var1.setValue(AGE, var4 + 1), 2);
         return false;
      } else {
         this.melt(var1, var2, var3);
         return true;
      }
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (var4 == this && this.fewerNeigboursThan(var2, var3, 2)) {
         this.melt(var1, var2, var3);
      }

      super.neighborChanged(var1, var2, var3, var4, var5, var6);
   }

   private boolean fewerNeigboursThan(BlockGetter var1, BlockPos var2, int var3) {
      int var4 = 0;
      BlockPos.PooledMutableBlockPos var5 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var6 = null;

      try {
         Direction[] var7 = Direction.values();
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Direction var10 = var7[var9];
            var5.set((Vec3i)var2).move(var10);
            if (var1.getBlockState(var5).getBlock() == this) {
               ++var4;
               if (var4 >= var3) {
                  boolean var11 = false;
                  return var11;
               }
            }
         }
      } catch (Throwable var21) {
         var6 = var21;
         throw var21;
      } finally {
         if (var5 != null) {
            if (var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var20) {
                  var6.addSuppressed(var20);
               }
            } else {
               var5.close();
            }
         }

      }

      return true;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE);
   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return ItemStack.EMPTY;
   }

   static {
      AGE = BlockStateProperties.AGE_3;
   }
}
