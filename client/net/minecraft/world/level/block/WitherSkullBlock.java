package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

public class WitherSkullBlock extends SkullBlock {
   @Nullable
   private static BlockPattern witherPatternFull;
   @Nullable
   private static BlockPattern witherPatternBase;

   protected WitherSkullBlock(BlockBehaviour.Properties var1) {
      super(SkullBlock.Types.WITHER_SKELETON, var1);
   }

   @Override
   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
      super.setPlacedBy(var1, var2, var3, var4, var5);
      BlockEntity var6 = var1.getBlockEntity(var2);
      if (var6 instanceof SkullBlockEntity) {
         checkSpawn(var1, var2, (SkullBlockEntity)var6);
      }
   }

   public static void checkSpawn(Level var0, BlockPos var1, SkullBlockEntity var2) {
      if (!var0.isClientSide) {
         BlockState var3 = var2.getBlockState();
         boolean var4 = var3.is(Blocks.WITHER_SKELETON_SKULL) || var3.is(Blocks.WITHER_SKELETON_WALL_SKULL);
         if (var4 && var1.getY() >= var0.getMinBuildHeight() && var0.getDifficulty() != Difficulty.PEACEFUL) {
            BlockPattern.BlockPatternMatch var5 = getOrCreateWitherFull().find(var0, var1);
            if (var5 != null) {
               WitherBoss var6 = EntityType.WITHER.create(var0);
               if (var6 != null) {
                  CarvedPumpkinBlock.clearPatternBlocks(var0, var5);
                  BlockPos var7 = var5.getBlock(1, 2, 0).getPos();
                  var6.moveTo(
                     (double)var7.getX() + 0.5,
                     (double)var7.getY() + 0.55,
                     (double)var7.getZ() + 0.5,
                     var5.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F,
                     0.0F
                  );
                  var6.yBodyRot = var5.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
                  var6.makeInvulnerable();

                  for(ServerPlayer var9 : var0.getEntitiesOfClass(ServerPlayer.class, var6.getBoundingBox().inflate(50.0))) {
                     CriteriaTriggers.SUMMONED_ENTITY.trigger(var9, var6);
                  }

                  var0.addFreshEntity(var6);
                  CarvedPumpkinBlock.updatePatternBlocks(var0, var5);
               }
            }
         }
      }
   }

   public static boolean canSpawnMob(Level var0, BlockPos var1, ItemStack var2) {
      if (var2.is(Items.WITHER_SKELETON_SKULL)
         && var1.getY() >= var0.getMinBuildHeight() + 2
         && var0.getDifficulty() != Difficulty.PEACEFUL
         && !var0.isClientSide) {
         return getOrCreateWitherBase().find(var0, var1) != null;
      } else {
         return false;
      }
   }

   private static BlockPattern getOrCreateWitherFull() {
      if (witherPatternFull == null) {
         witherPatternFull = BlockPatternBuilder.start()
            .aisle("^^^", "###", "~#~")
            .where('#', var0 -> var0.getState().is(BlockTags.WITHER_SUMMON_BASE_BLOCKS))
            .where(
               '^',
               BlockInWorld.hasState(
                  BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL))
               )
            )
            .where('~', var0 -> var0.getState().isAir())
            .build();
      }

      return witherPatternFull;
   }

   private static BlockPattern getOrCreateWitherBase() {
      if (witherPatternBase == null) {
         witherPatternBase = BlockPatternBuilder.start()
            .aisle("   ", "###", "~#~")
            .where('#', var0 -> var0.getState().is(BlockTags.WITHER_SUMMON_BASE_BLOCKS))
            .where('~', var0 -> var0.getState().isAir())
            .build();
      }

      return witherPatternBase;
   }
}
