package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BoneMealItem extends Item {
   public BoneMealItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockPos var4 = var3.relative(var1.getClickedFace());
      if (growCrop(var1.getItemInHand(), var2, var3)) {
         if (!var2.isClientSide) {
            var2.levelEvent(2005, var3, 0);
         }

         return InteractionResult.SUCCESS;
      } else {
         BlockState var5 = var2.getBlockState(var3);
         boolean var6 = var5.isFaceSturdy(var2, var3, var1.getClickedFace());
         if (var6 && growWaterPlant(var1.getItemInHand(), var2, var4, var1.getClickedFace())) {
            if (!var2.isClientSide) {
               var2.levelEvent(2005, var4, 0);
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public static boolean growCrop(ItemStack var0, Level var1, BlockPos var2) {
      BlockState var3 = var1.getBlockState(var2);
      if (var3.getBlock() instanceof BonemealableBlock) {
         BonemealableBlock var4 = (BonemealableBlock)var3.getBlock();
         if (var4.isValidBonemealTarget(var1, var2, var3, var1.isClientSide)) {
            if (var1 instanceof ServerLevel) {
               if (var4.isBonemealSuccess(var1, var1.random, var2, var3)) {
                  var4.performBonemeal((ServerLevel)var1, var1.random, var2, var3);
               }

               var0.shrink(1);
            }

            return true;
         }
      }

      return false;
   }

   public static boolean growWaterPlant(ItemStack var0, Level var1, BlockPos var2, @Nullable Direction var3) {
      if (var1.getBlockState(var2).getBlock() == Blocks.WATER && var1.getFluidState(var2).getAmount() == 8) {
         if (!(var1 instanceof ServerLevel)) {
            return true;
         } else {
            label80:
            for(int var4 = 0; var4 < 128; ++var4) {
               BlockPos var5 = var2;
               Biome var6 = var1.getBiome(var2);
               BlockState var7 = Blocks.SEAGRASS.defaultBlockState();

               int var8;
               for(var8 = 0; var8 < var4 / 16; ++var8) {
                  var5 = var5.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                  var6 = var1.getBiome(var5);
                  if (var1.getBlockState(var5).isCollisionShapeFullBlock(var1, var5)) {
                     continue label80;
                  }
               }

               if (var6 == Biomes.WARM_OCEAN || var6 == Biomes.DEEP_WARM_OCEAN) {
                  if (var4 == 0 && var3 != null && var3.getAxis().isHorizontal()) {
                     var7 = (BlockState)((Block)BlockTags.WALL_CORALS.getRandomElement(var1.random)).defaultBlockState().setValue(BaseCoralWallFanBlock.FACING, var3);
                  } else if (random.nextInt(4) == 0) {
                     var7 = ((Block)BlockTags.UNDERWATER_BONEMEALS.getRandomElement(random)).defaultBlockState();
                  }
               }

               if (var7.getBlock().is(BlockTags.WALL_CORALS)) {
                  for(var8 = 0; !var7.canSurvive(var1, var5) && var8 < 4; ++var8) {
                     var7 = (BlockState)var7.setValue(BaseCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
                  }
               }

               if (var7.canSurvive(var1, var5)) {
                  BlockState var9 = var1.getBlockState(var5);
                  if (var9.getBlock() == Blocks.WATER && var1.getFluidState(var5).getAmount() == 8) {
                     var1.setBlock(var5, var7, 3);
                  } else if (var9.getBlock() == Blocks.SEAGRASS && random.nextInt(10) == 0) {
                     ((BonemealableBlock)Blocks.SEAGRASS).performBonemeal((ServerLevel)var1, random, var5, var9);
                  }
               }
            }

            var0.shrink(1);
            return true;
         }
      } else {
         return false;
      }
   }

   public static void addGrowthParticles(LevelAccessor var0, BlockPos var1, int var2) {
      if (var2 == 0) {
         var2 = 15;
      }

      BlockState var3 = var0.getBlockState(var1);
      if (!var3.isAir()) {
         for(int var4 = 0; var4 < var2; ++var4) {
            double var5 = random.nextGaussian() * 0.02D;
            double var7 = random.nextGaussian() * 0.02D;
            double var9 = random.nextGaussian() * 0.02D;
            var0.addParticle(ParticleTypes.HAPPY_VILLAGER, (double)((float)var1.getX() + random.nextFloat()), (double)var1.getY() + (double)random.nextFloat() * var3.getShape(var0, var1).max(Direction.Axis.Y), (double)((float)var1.getZ() + random.nextFloat()), var5, var7, var9);
         }

      }
   }
}
