package net.minecraft.world.level.block;

import java.util.Iterator;
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
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.material.Material;

public class WitherSkullBlock extends SkullBlock {
   @Nullable
   private static BlockPattern witherPatternFull;
   @Nullable
   private static BlockPattern witherPatternBase;

   protected WitherSkullBlock(BlockBehaviour.Properties var1) {
      super(SkullBlock.Types.WITHER_SKELETON, var1);
   }

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
            BlockPattern var5 = getOrCreateWitherFull();
            BlockPattern.BlockPatternMatch var6 = var5.find(var0, var1);
            if (var6 != null) {
               for(int var7 = 0; var7 < var5.getWidth(); ++var7) {
                  for(int var8 = 0; var8 < var5.getHeight(); ++var8) {
                     BlockInWorld var9 = var6.getBlock(var7, var8, 0);
                     var0.setBlock(var9.getPos(), Blocks.AIR.defaultBlockState(), 2);
                     var0.levelEvent(2001, var9.getPos(), Block.getId(var9.getState()));
                  }
               }

               WitherBoss var11 = (WitherBoss)EntityType.WITHER.create(var0);
               BlockPos var12 = var6.getBlock(1, 2, 0).getPos();
               var11.moveTo((double)var12.getX() + 0.5D, (double)var12.getY() + 0.55D, (double)var12.getZ() + 0.5D, var6.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F, 0.0F);
               var11.yBodyRot = var6.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
               var11.makeInvulnerable();
               Iterator var13 = var0.getEntitiesOfClass(ServerPlayer.class, var11.getBoundingBox().inflate(50.0D)).iterator();

               while(var13.hasNext()) {
                  ServerPlayer var10 = (ServerPlayer)var13.next();
                  CriteriaTriggers.SUMMONED_ENTITY.trigger(var10, var11);
               }

               var0.addFreshEntity(var11);

               for(int var14 = 0; var14 < var5.getWidth(); ++var14) {
                  for(int var15 = 0; var15 < var5.getHeight(); ++var15) {
                     var0.blockUpdated(var6.getBlock(var14, var15, 0).getPos(), Blocks.AIR);
                  }
               }

            }
         }
      }
   }

   public static boolean canSpawnMob(Level var0, BlockPos var1, ItemStack var2) {
      if (var2.is(Items.WITHER_SKELETON_SKULL) && var1.getY() >= var0.getMinBuildHeight() + 2 && var0.getDifficulty() != Difficulty.PEACEFUL && !var0.isClientSide) {
         return getOrCreateWitherBase().find(var0, var1) != null;
      } else {
         return false;
      }
   }

   private static BlockPattern getOrCreateWitherFull() {
      if (witherPatternFull == null) {
         witherPatternFull = BlockPatternBuilder.start().aisle("^^^", "###", "~#~").where('#', (var0) -> {
            return var0.getState().is(BlockTags.WITHER_SUMMON_BASE_BLOCKS);
         }).where('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL)))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
      }

      return witherPatternFull;
   }

   private static BlockPattern getOrCreateWitherBase() {
      if (witherPatternBase == null) {
         witherPatternBase = BlockPatternBuilder.start().aisle("   ", "###", "~#~").where('#', (var0) -> {
            return var0.getState().is(BlockTags.WITHER_SUMMON_BASE_BLOCKS);
         }).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
      }

      return witherPatternBase;
   }
}
