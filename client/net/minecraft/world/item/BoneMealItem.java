package net.minecraft.world.item;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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

         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         BlockState var5 = var2.getBlockState(var3);
         boolean var6 = var5.isFaceSturdy(var2, var3, var1.getClickedFace());
         if (var6 && growWaterPlant(var1.getItemInHand(), var2, var4, var1.getClickedFace())) {
            if (!var2.isClientSide) {
               var2.levelEvent(2005, var4, 0);
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
            label80:
            for(int var4 = 0; var4 < 128; ++var4) {
               BlockPos var5 = var2;
               BlockState var6 = Blocks.SEAGRASS.defaultBlockState();

               for(int var7 = 0; var7 < var4 / 16; ++var7) {
                  var5 = var5.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                  if (var1.getBlockState(var5).isCollisionShapeFullBlock(var1, var5)) {
                     continue label80;
                  }
               }

               Optional var9 = var1.getBiomeName(var5);
               if (Objects.equals(var9, Optional.of(Biomes.WARM_OCEAN)) || Objects.equals(var9, Optional.of(Biomes.DEEP_WARM_OCEAN))) {
                  if (var4 == 0 && var3 != null && var3.getAxis().isHorizontal()) {
                     var6 = (BlockState)((Block)BlockTags.WALL_CORALS.getRandomElement(var1.random)).defaultBlockState().setValue(BaseCoralWallFanBlock.FACING, var3);
                  } else if (random.nextInt(4) == 0) {
                     var6 = ((Block)BlockTags.UNDERWATER_BONEMEALS.getRandomElement(random)).defaultBlockState();
                  }
               }

               if (var6.getBlock().is((Tag)BlockTags.WALL_CORALS)) {
                  for(int var8 = 0; !var6.canSurvive(var1, var5) && var8 < 4; ++var8) {
                     var6 = (BlockState)var6.setValue(BaseCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random));
                  }
               }

               if (var6.canSurvive(var1, var5)) {
                  BlockState var10 = var1.getBlockState(var5);
                  if (var10.is(Blocks.WATER) && var1.getFluidState(var5).getAmount() == 8) {
                     var1.setBlock(var5, var6, 3);
                  } else if (var10.is(Blocks.SEAGRASS) && random.nextInt(10) == 0) {
                     ((BonemealableBlock)Blocks.SEAGRASS).performBonemeal((ServerLevel)var1, random, var5, var10);
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
         double var4 = 0.5D;
         double var6;
         if (var3.is(Blocks.WATER)) {
            var2 *= 3;
            var6 = 1.0D;
            var4 = 3.0D;
         } else if (var3.isSolidRender(var0, var1)) {
            var1 = var1.above();
            var2 *= 3;
            var4 = 3.0D;
            var6 = 1.0D;
         } else {
            var6 = var3.getShape(var0, var1).max(Direction.Axis.Y);
         }

         var0.addParticle(ParticleTypes.HAPPY_VILLAGER, (double)var1.getX() + 0.5D, (double)var1.getY() + 0.5D, (double)var1.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);

         for(int var8 = 0; var8 < var2; ++var8) {
            double var9 = random.nextGaussian() * 0.02D;
            double var11 = random.nextGaussian() * 0.02D;
            double var13 = random.nextGaussian() * 0.02D;
            double var15 = 0.5D - var4;
            double var17 = (double)var1.getX() + var15 + random.nextDouble() * var4 * 2.0D;
            double var19 = (double)var1.getY() + random.nextDouble() * var6;
            double var21 = (double)var1.getZ() + var15 + random.nextDouble() * var4 * 2.0D;
            if (!var0.getBlockState((new BlockPos(var17, var19, var21)).below()).isAir()) {
               var0.addParticle(ParticleTypes.HAPPY_VILLAGER, var17, var19, var21, var9, var11, var13);
            }
         }

      }
   }
}
