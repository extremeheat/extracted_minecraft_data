package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class TargetBlock extends Block {
   private static final IntegerProperty OUTPUT_POWER;
   private static final int ACTIVATION_TICKS_ARROWS = 20;
   private static final int ACTIVATION_TICKS_OTHER = 8;

   public TargetBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(OUTPUT_POWER, 0));
   }

   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      int var5 = updateRedstoneOutput(var1, var2, var3, var4);
      Entity var6 = var4.getOwner();
      if (var6 instanceof ServerPlayer) {
         ServerPlayer var7 = (ServerPlayer)var6;
         var7.awardStat(Stats.TARGET_HIT);
         CriteriaTriggers.TARGET_BLOCK_HIT.trigger(var7, var4, var3.getLocation(), var5);
      }

   }

   private static int updateRedstoneOutput(LevelAccessor var0, BlockState var1, BlockHitResult var2, Entity var3) {
      int var4 = getRedstoneStrength(var2, var2.getLocation());
      int var5 = var3 instanceof AbstractArrow ? 20 : 8;
      if (!var0.getBlockTicks().hasScheduledTick(var2.getBlockPos(), var1.getBlock())) {
         setOutputPower(var0, var1, var4, var2.getBlockPos(), var5);
      }

      return var4;
   }

   private static int getRedstoneStrength(BlockHitResult var0, Vec3 var1) {
      Direction var2 = var0.getDirection();
      double var3 = Math.abs(Mth.frac(var1.field_414) - 0.5D);
      double var5 = Math.abs(Mth.frac(var1.field_415) - 0.5D);
      double var7 = Math.abs(Mth.frac(var1.field_416) - 0.5D);
      Direction.Axis var11 = var2.getAxis();
      double var9;
      if (var11 == Direction.Axis.field_501) {
         var9 = Math.max(var3, var7);
      } else if (var11 == Direction.Axis.field_502) {
         var9 = Math.max(var3, var5);
      } else {
         var9 = Math.max(var5, var7);
      }

      return Math.max(1, Mth.ceil(15.0D * Mth.clamp((0.5D - var9) / 0.5D, 0.0D, 1.0D)));
   }

   private static void setOutputPower(LevelAccessor var0, BlockState var1, int var2, BlockPos var3, int var4) {
      var0.setBlock(var3, (BlockState)var1.setValue(OUTPUT_POWER, var2), 3);
      var0.scheduleTick(var3, var1.getBlock(), var4);
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if ((Integer)var1.getValue(OUTPUT_POWER) != 0) {
         var2.setBlock(var3, (BlockState)var1.setValue(OUTPUT_POWER, 0), 3);
      }

   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Integer)var1.getValue(OUTPUT_POWER);
   }

   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(OUTPUT_POWER);
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var2.isClientSide() && !var1.is(var4.getBlock())) {
         if ((Integer)var1.getValue(OUTPUT_POWER) > 0 && !var2.getBlockTicks().hasScheduledTick(var3, this)) {
            var2.setBlock(var3, (BlockState)var1.setValue(OUTPUT_POWER, 0), 18);
         }

      }
   }

   static {
      OUTPUT_POWER = BlockStateProperties.POWER;
   }
}
