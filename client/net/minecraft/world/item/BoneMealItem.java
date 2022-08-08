package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BoneMealItem extends Item {
   public static final int GRASS_SPREAD_WIDTH = 3;
   public static final int GRASS_SPREAD_HEIGHT = 1;
   public static final int GRASS_COUNT_MULTIPLIER = 3;

   public BoneMealItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockPos var4 = var3.relative(var1.getClickedFace());
      if (growCrop(var1.getItemInHand(), var2, var3)) {
         if (!var2.isClientSide) {
            var2.levelEvent(1505, var3, 0);
         }

         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         BlockState var5 = var2.getBlockState(var3);
         boolean var6 = var5.isFaceSturdy(var2, var3, var1.getClickedFace());
         if (var6 && growWaterPlant(var1.getItemInHand(), var2, var4, var1.getClickedFace())) {
            if (!var2.isClientSide) {
               var2.levelEvent(1505, var4, 0);
            }

            return InteractionResult.sidedSuccess(var2.isClientSide);
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
      if (var1.getBlockState(var2).is(Blocks.WATER) && var1.getFluidState(var2).getAmount() == 8) {
         if (!(var1 instanceof ServerLevel)) {
            return true;
         } else {
            RandomSource var4 = var1.getRandom();

            label78:
            for(int var5 = 0; var5 < 128; ++var5) {
               BlockPos var6 = var2;
               BlockState var7 = Blocks.SEAGRASS.defaultBlockState();

               for(int var8 = 0; var8 < var5 / 16; ++var8) {
                  var6 = var6.offset(var4.nextInt(3) - 1, (var4.nextInt(3) - 1) * var4.nextInt(3) / 2, var4.nextInt(3) - 1);
                  if (var1.getBlockState(var6).isCollisionShapeFullBlock(var1, var6)) {
                     continue label78;
                  }
               }

               Holder var10 = var1.getBiome(var6);
               if (var10.is(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL)) {
                  if (var5 == 0 && var3 != null && var3.getAxis().isHorizontal()) {
                     var7 = (BlockState)Registry.BLOCK.getTag(BlockTags.WALL_CORALS).flatMap((var1x) -> {
                        return var1x.getRandomElement(var1.random);
                     }).map((var0x) -> {
                        return ((Block)var0x.value()).defaultBlockState();
                     }).orElse(var7);
                     if (var7.hasProperty(BaseCoralWallFanBlock.FACING)) {
                        var7 = (BlockState)var7.setValue(BaseCoralWallFanBlock.FACING, var3);
                     }
                  } else if (var4.nextInt(4) == 0) {
                     var7 = (BlockState)Registry.BLOCK.getTag(BlockTags.UNDERWATER_BONEMEALS).flatMap((var1x) -> {
                        return var1x.getRandomElement(var1.random);
                     }).map((var0x) -> {
                        return ((Block)var0x.value()).defaultBlockState();
                     }).orElse(var7);
                  }
               }

               if (var7.is(BlockTags.WALL_CORALS, (var0x) -> {
                  return var0x.hasProperty(BaseCoralWallFanBlock.FACING);
               })) {
                  for(int var9 = 0; !var7.canSurvive(var1, var6) && var9 < 4; ++var9) {
                     var7 = (BlockState)var7.setValue(BaseCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(var4));
                  }
               }

               if (var7.canSurvive(var1, var6)) {
                  BlockState var11 = var1.getBlockState(var6);
                  if (var11.is(Blocks.WATER) && var1.getFluidState(var6).getAmount() == 8) {
                     var1.setBlock(var6, var7, 3);
                  } else if (var11.is(Blocks.SEAGRASS) && var4.nextInt(10) == 0) {
                     ((BonemealableBlock)Blocks.SEAGRASS).performBonemeal((ServerLevel)var1, var4, var6, var11);
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
         double var4 = 0.5;
         double var6;
         if (var3.is(Blocks.WATER)) {
            var2 *= 3;
            var6 = 1.0;
            var4 = 3.0;
         } else if (var3.isSolidRender(var0, var1)) {
            var1 = var1.above();
            var2 *= 3;
            var4 = 3.0;
            var6 = 1.0;
         } else {
            var6 = var3.getShape(var0, var1).max(Direction.Axis.Y);
         }

         var0.addParticle(ParticleTypes.HAPPY_VILLAGER, (double)var1.getX() + 0.5, (double)var1.getY() + 0.5, (double)var1.getZ() + 0.5, 0.0, 0.0, 0.0);
         RandomSource var8 = var0.getRandom();

         for(int var9 = 0; var9 < var2; ++var9) {
            double var10 = var8.nextGaussian() * 0.02;
            double var12 = var8.nextGaussian() * 0.02;
            double var14 = var8.nextGaussian() * 0.02;
            double var16 = 0.5 - var4;
            double var18 = (double)var1.getX() + var16 + var8.nextDouble() * var4 * 2.0;
            double var20 = (double)var1.getY() + var8.nextDouble() * var6;
            double var22 = (double)var1.getZ() + var16 + var8.nextDouble() * var4 * 2.0;
            if (!var0.getBlockState((new BlockPos(var18, var20, var22)).below()).isAir()) {
               var0.addParticle(ParticleTypes.HAPPY_VILLAGER, var18, var20, var22, var10, var12, var14);
            }
         }

      }
   }
}
