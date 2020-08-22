package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ChorusFlowerBlock extends Block {
   public static final IntegerProperty AGE;
   private final ChorusPlantBlock plant;

   protected ChorusFlowerBlock(ChorusPlantBlock var1, Block.Properties var2) {
      super(var2);
      this.plant = var1;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if (!var1.canSurvive(var2, var3)) {
         var2.destroyBlock(var3, true);
      } else {
         BlockPos var5 = var3.above();
         if (var2.isEmptyBlock(var5) && var5.getY() < 256) {
            int var6 = (Integer)var1.getValue(AGE);
            if (var6 < 5) {
               boolean var7 = false;
               boolean var8 = false;
               BlockState var9 = var2.getBlockState(var3.below());
               Block var10 = var9.getBlock();
               int var11;
               if (var10 == Blocks.END_STONE) {
                  var7 = true;
               } else if (var10 == this.plant) {
                  var11 = 1;

                  for(int var12 = 0; var12 < 4; ++var12) {
                     Block var13 = var2.getBlockState(var3.below(var11 + 1)).getBlock();
                     if (var13 != this.plant) {
                        if (var13 == Blocks.END_STONE) {
                           var8 = true;
                        }
                        break;
                     }

                     ++var11;
                  }

                  if (var11 < 2 || var11 <= var4.nextInt(var8 ? 5 : 4)) {
                     var7 = true;
                  }
               } else if (var9.isAir()) {
                  var7 = true;
               }

               if (var7 && allNeighborsEmpty(var2, var5, (Direction)null) && var2.isEmptyBlock(var3.above(2))) {
                  var2.setBlock(var3, this.plant.getStateForPlacement(var2, var3), 2);
                  this.placeGrownFlower(var2, var5, var6);
               } else if (var6 < 4) {
                  var11 = var4.nextInt(4);
                  if (var8) {
                     ++var11;
                  }

                  boolean var16 = false;

                  for(int var17 = 0; var17 < var11; ++var17) {
                     Direction var14 = Direction.Plane.HORIZONTAL.getRandomDirection(var4);
                     BlockPos var15 = var3.relative(var14);
                     if (var2.isEmptyBlock(var15) && var2.isEmptyBlock(var15.below()) && allNeighborsEmpty(var2, var15, var14.getOpposite())) {
                        this.placeGrownFlower(var2, var15, var6 + 1);
                        var16 = true;
                     }
                  }

                  if (var16) {
                     var2.setBlock(var3, this.plant.getStateForPlacement(var2, var3), 2);
                  } else {
                     this.placeDeadFlower(var2, var3);
                  }
               } else {
                  this.placeDeadFlower(var2, var3);
               }

            }
         }
      }
   }

   private void placeGrownFlower(Level var1, BlockPos var2, int var3) {
      var1.setBlock(var2, (BlockState)this.defaultBlockState().setValue(AGE, var3), 2);
      var1.levelEvent(1033, var2, 0);
   }

   private void placeDeadFlower(Level var1, BlockPos var2) {
      var1.setBlock(var2, (BlockState)this.defaultBlockState().setValue(AGE, 5), 2);
      var1.levelEvent(1034, var2, 0);
   }

   private static boolean allNeighborsEmpty(LevelReader var0, BlockPos var1, @Nullable Direction var2) {
      Iterator var3 = Direction.Plane.HORIZONTAL.iterator();

      Direction var4;
      do {
         if (!var3.hasNext()) {
            return true;
         }

         var4 = (Direction)var3.next();
      } while(var4 == var2 || var0.isEmptyBlock(var1.relative(var4)));

      return false;
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 != Direction.UP && !var1.canSurvive(var4, var5)) {
         var4.getBlockTicks().scheduleTick(var5, this, 1);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.below());
      Block var5 = var4.getBlock();
      if (var5 != this.plant && var5 != Blocks.END_STONE) {
         if (!var4.isAir()) {
            return false;
         } else {
            boolean var6 = false;
            Iterator var7 = Direction.Plane.HORIZONTAL.iterator();

            while(var7.hasNext()) {
               Direction var8 = (Direction)var7.next();
               BlockState var9 = var2.getBlockState(var3.relative(var8));
               if (var9.getBlock() == this.plant) {
                  if (var6) {
                     return false;
                  }

                  var6 = true;
               } else if (!var9.isAir()) {
                  return false;
               }
            }

            return var6;
         }
      } else {
         return true;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder var1) {
      var1.add(AGE);
   }

   public static void generatePlant(LevelAccessor var0, BlockPos var1, Random var2, int var3) {
      var0.setBlock(var1, ((ChorusPlantBlock)Blocks.CHORUS_PLANT).getStateForPlacement(var0, var1), 2);
      growTreeRecursive(var0, var1, var2, var1, var3, 0);
   }

   private static void growTreeRecursive(LevelAccessor var0, BlockPos var1, Random var2, BlockPos var3, int var4, int var5) {
      ChorusPlantBlock var6 = (ChorusPlantBlock)Blocks.CHORUS_PLANT;
      int var7 = var2.nextInt(4) + 1;
      if (var5 == 0) {
         ++var7;
      }

      for(int var8 = 0; var8 < var7; ++var8) {
         BlockPos var9 = var1.above(var8 + 1);
         if (!allNeighborsEmpty(var0, var9, (Direction)null)) {
            return;
         }

         var0.setBlock(var9, var6.getStateForPlacement(var0, var9), 2);
         var0.setBlock(var9.below(), var6.getStateForPlacement(var0, var9.below()), 2);
      }

      boolean var13 = false;
      if (var5 < 4) {
         int var14 = var2.nextInt(4);
         if (var5 == 0) {
            ++var14;
         }

         for(int var10 = 0; var10 < var14; ++var10) {
            Direction var11 = Direction.Plane.HORIZONTAL.getRandomDirection(var2);
            BlockPos var12 = var1.above(var7).relative(var11);
            if (Math.abs(var12.getX() - var3.getX()) < var4 && Math.abs(var12.getZ() - var3.getZ()) < var4 && var0.isEmptyBlock(var12) && var0.isEmptyBlock(var12.below()) && allNeighborsEmpty(var0, var12, var11.getOpposite())) {
               var13 = true;
               var0.setBlock(var12, var6.getStateForPlacement(var0, var12), 2);
               var0.setBlock(var12.relative(var11.getOpposite()), var6.getStateForPlacement(var0, var12.relative(var11.getOpposite())), 2);
               growTreeRecursive(var0, var12, var2, var3, var4, var5 + 1);
            }
         }
      }

      if (!var13) {
         var0.setBlock(var1.above(var7), (BlockState)Blocks.CHORUS_FLOWER.defaultBlockState().setValue(AGE, 5), 2);
      }

   }

   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Entity var4) {
      BlockPos var5 = var3.getBlockPos();
      var1.destroyBlock(var5, true, var4);
   }

   static {
      AGE = BlockStateProperties.AGE_5;
   }
}
