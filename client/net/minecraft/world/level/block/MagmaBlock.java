package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MagmaBlock extends Block {
   public static final MapCodec<MagmaBlock> CODEC = simpleCodec(MagmaBlock::new);
   private static final int BUBBLE_COLUMN_CHECK_DELAY = 20;

   @Override
   public MapCodec<MagmaBlock> codec() {
      return CODEC;
   }

   public MagmaBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   public void stepOn(Level var1, BlockPos var2, BlockState var3, Entity var4) {
      if (!var4.isSteppingCarefully() && var4 instanceof LivingEntity) {
         var4.hurt(var1.damageSources().hotFloor(), 1.0F);
      }

      super.stepOn(var1, var2, var3, var4);
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      BubbleColumnBlock.updateColumn(var2, var3.above(), var1);
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      if (var5 == Direction.UP && var7.is(Blocks.WATER)) {
         var3.scheduleTick(var4, this, 20);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      var2.scheduleTick(var3, this, 20);
   }
}
