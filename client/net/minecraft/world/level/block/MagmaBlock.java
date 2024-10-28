package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MagmaBlock extends Block {
   public static final MapCodec<MagmaBlock> CODEC = simpleCodec(MagmaBlock::new);
   private static final int BUBBLE_COLUMN_CHECK_DELAY = 20;

   public MapCodec<MagmaBlock> codec() {
      return CODEC;
   }

   public MagmaBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public void stepOn(Level var1, BlockPos var2, BlockState var3, Entity var4) {
      if (!var4.isSteppingCarefully() && var4 instanceof LivingEntity) {
         var4.hurt(var1.damageSources().hotFloor(), 1.0F);
      }

      super.stepOn(var1, var2, var3, var4);
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      BubbleColumnBlock.updateColumn(var2, var3.above(), var1);
   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == Direction.UP && var3.is(Blocks.WATER)) {
         var4.scheduleTick(var5, (Block)this, 20);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      var2.scheduleTick(var3, this, 20);
   }
}
