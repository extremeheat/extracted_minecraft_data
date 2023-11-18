package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChorusFlowerBlock extends Block {
   public static final int DEAD_AGE = 5;
   public static final IntegerProperty AGE = BlockStateProperties.AGE_5;
   protected static final VoxelShape BLOCK_SUPPORT_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
   private final ChorusPlantBlock plant;

   protected ChorusFlowerBlock(ChorusPlantBlock var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.plant = var1;
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   @Override
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!var1.canSurvive(var2, var3)) {
         var2.destroyBlock(var3, true);
      }
   }

   @Override
   public boolean isRandomlyTicking(BlockState var1) {
      return var1.getValue(AGE) < 5;
   }

   @Override
   public VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return BLOCK_SUPPORT_SHAPE;
   }

   @Override
   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      BlockPos var5 = var3.above();
      if (var2.isEmptyBlock(var5) && var5.getY() < var2.getMaxBuildHeight()) {
         int var6 = var1.getValue(AGE);
         if (var6 < 5) {
            boolean var7 = false;
            boolean var8 = false;
            BlockState var9 = var2.getBlockState(var3.below());
            if (var9.is(Blocks.END_STONE)) {
               var7 = true;
            } else if (var9.is(this.plant)) {
               int var10 = 1;

               for(int var11 = 0; var11 < 4; ++var11) {
                  BlockState var12 = var2.getBlockState(var3.below(var10 + 1));
                  if (!var12.is(this.plant)) {
                     if (var12.is(Blocks.END_STONE)) {
                        var8 = true;
                     }
                     break;
                  }

                  ++var10;
               }

               if (var10 < 2 || var10 <= var4.nextInt(var8 ? 5 : 4)) {
                  var7 = true;
               }
            } else if (var9.isAir()) {
               var7 = true;
            }

            if (var7 && allNeighborsEmpty(var2, var5, null) && var2.isEmptyBlock(var3.above(2))) {
               var2.setBlock(var3, this.plant.getStateForPlacement(var2, var3), 2);
               this.placeGrownFlower(var2, var5, var6);
            } else if (var6 < 4) {
               int var15 = var4.nextInt(4);
               if (var8) {
                  ++var15;
               }

               boolean var16 = false;

               for(int var17 = 0; var17 < var15; ++var17) {
                  Direction var13 = Direction.Plane.HORIZONTAL.getRandomDirection(var4);
                  BlockPos var14 = var3.relative(var13);
                  if (var2.isEmptyBlock(var14) && var2.isEmptyBlock(var14.below()) && allNeighborsEmpty(var2, var14, var13.getOpposite())) {
                     this.placeGrownFlower(var2, var14, var6 + 1);
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

   private void placeGrownFlower(Level var1, BlockPos var2, int var3) {
      var1.setBlock(var2, this.defaultBlockState().setValue(AGE, Integer.valueOf(var3)), 2);
      var1.levelEvent(1033, var2, 0);
   }

   private void placeDeadFlower(Level var1, BlockPos var2) {
      var1.setBlock(var2, this.defaultBlockState().setValue(AGE, Integer.valueOf(5)), 2);
      var1.levelEvent(1034, var2, 0);
   }

   private static boolean allNeighborsEmpty(LevelReader var0, BlockPos var1, @Nullable Direction var2) {
      for(Direction var4 : Direction.Plane.HORIZONTAL) {
         if (var4 != var2 && !var0.isEmptyBlock(var1.relative(var4))) {
            return false;
         }
      }

      return true;
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 != Direction.UP && !var1.canSurvive(var4, var5)) {
         var4.scheduleTick(var5, this, 1);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.below());
      if (!var4.is(this.plant) && !var4.is(Blocks.END_STONE)) {
         if (!var4.isAir()) {
            return false;
         } else {
            boolean var5 = false;

            for(Direction var7 : Direction.Plane.HORIZONTAL) {
               BlockState var8 = var2.getBlockState(var3.relative(var7));
               if (var8.is(this.plant)) {
                  if (var5) {
                     return false;
                  }

                  var5 = true;
               } else if (!var8.isAir()) {
                  return false;
               }
            }

            return var5;
         }
      } else {
         return true;
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE);
   }

   public static void generatePlant(LevelAccessor var0, BlockPos var1, RandomSource var2, int var3) {
      var0.setBlock(var1, ((ChorusPlantBlock)Blocks.CHORUS_PLANT).getStateForPlacement(var0, var1), 2);
      growTreeRecursive(var0, var1, var2, var1, var3, 0);
   }

   private static void growTreeRecursive(LevelAccessor var0, BlockPos var1, RandomSource var2, BlockPos var3, int var4, int var5) {
      ChorusPlantBlock var6 = (ChorusPlantBlock)Blocks.CHORUS_PLANT;
      int var7 = var2.nextInt(4) + 1;
      if (var5 == 0) {
         ++var7;
      }

      for(int var8 = 0; var8 < var7; ++var8) {
         BlockPos var9 = var1.above(var8 + 1);
         if (!allNeighborsEmpty(var0, var9, null)) {
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
            if (Math.abs(var12.getX() - var3.getX()) < var4
               && Math.abs(var12.getZ() - var3.getZ()) < var4
               && var0.isEmptyBlock(var12)
               && var0.isEmptyBlock(var12.below())
               && allNeighborsEmpty(var0, var12, var11.getOpposite())) {
               var13 = true;
               var0.setBlock(var12, var6.getStateForPlacement(var0, var12), 2);
               var0.setBlock(var12.relative(var11.getOpposite()), var6.getStateForPlacement(var0, var12.relative(var11.getOpposite())), 2);
               growTreeRecursive(var0, var12, var2, var3, var4, var5 + 1);
            }
         }
      }

      if (!var13) {
         var0.setBlock(var1.above(var7), Blocks.CHORUS_FLOWER.defaultBlockState().setValue(AGE, Integer.valueOf(5)), 2);
      }
   }

   @Override
   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      BlockPos var5 = var3.getBlockPos();
      if (!var1.isClientSide && var4.mayInteract(var1, var5) && var4.getType().is(EntityTypeTags.IMPACT_PROJECTILES)) {
         var1.destroyBlock(var5, true, var4);
      }
   }
}
