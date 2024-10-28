package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
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
import net.minecraft.world.level.gameevent.GameEvent;

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
            var1.getPlayer().gameEvent(GameEvent.ITEM_INTERACT_FINISH);
            var2.levelEvent(1505, var3, 15);
         }

         return InteractionResult.SUCCESS;
      } else {
         BlockState var5 = var2.getBlockState(var3);
         boolean var6 = var5.isFaceSturdy(var2, var3, var1.getClickedFace());
         if (var6 && growWaterPlant(var1.getItemInHand(), var2, var4, var1.getClickedFace())) {
            if (!var2.isClientSide) {
               var1.getPlayer().gameEvent(GameEvent.ITEM_INTERACT_FINISH);
               var2.levelEvent(1505, var4, 15);
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public static boolean growCrop(ItemStack var0, Level var1, BlockPos var2) {
      BlockState var3 = var1.getBlockState(var2);
      Block var5 = var3.getBlock();
      if (var5 instanceof BonemealableBlock var4) {
         if (var4.isValidBonemealTarget(var1, var2, var3)) {
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

            label80:
            for(int var5 = 0; var5 < 128; ++var5) {
               BlockPos var6 = var2;
               BlockState var7 = Blocks.SEAGRASS.defaultBlockState();

               for(int var8 = 0; var8 < var5 / 16; ++var8) {
                  var6 = var6.offset(var4.nextInt(3) - 1, (var4.nextInt(3) - 1) * var4.nextInt(3) / 2, var4.nextInt(3) - 1);
                  if (var1.getBlockState(var6).isCollisionShapeFullBlock(var1, var6)) {
                     continue label80;
                  }
               }

               Holder var10 = var1.getBiome(var6);
               if (var10.is(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL)) {
                  if (var5 == 0 && var3 != null && var3.getAxis().isHorizontal()) {
                     var7 = (BlockState)BuiltInRegistries.BLOCK.getRandomElementOf(BlockTags.WALL_CORALS, var1.random).map((var0x) -> {
                        return ((Block)var0x.value()).defaultBlockState();
                     }).orElse(var7);
                     if (var7.hasProperty(BaseCoralWallFanBlock.FACING)) {
                        var7 = (BlockState)var7.setValue(BaseCoralWallFanBlock.FACING, var3);
                     }
                  } else if (var4.nextInt(4) == 0) {
                     var7 = (BlockState)BuiltInRegistries.BLOCK.getRandomElementOf(BlockTags.UNDERWATER_BONEMEALS, var1.random).map((var0x) -> {
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
                  } else if (var11.is(Blocks.SEAGRASS) && ((BonemealableBlock)Blocks.SEAGRASS).isValidBonemealTarget(var1, var6, var11) && var4.nextInt(10) == 0) {
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
      BlockState var3 = var0.getBlockState(var1);
      Block var5 = var3.getBlock();
      if (var5 instanceof BonemealableBlock) {
         BonemealableBlock var4 = (BonemealableBlock)var5;
         BlockPos var6 = var4.getParticlePos(var1);
         switch (var4.getType()) {
            case NEIGHBOR_SPREADER -> ParticleUtils.spawnParticles(var0, var6, var2 * 3, 3.0, 1.0, false, ParticleTypes.HAPPY_VILLAGER);
            case GROWER -> ParticleUtils.spawnParticleInBlock(var0, var6, var2, ParticleTypes.HAPPY_VILLAGER);
         }
      } else if (var3.is(Blocks.WATER)) {
         ParticleUtils.spawnParticles(var0, var1, var2 * 3, 3.0, 1.0, false, ParticleTypes.HAPPY_VILLAGER);
      }

   }
}
