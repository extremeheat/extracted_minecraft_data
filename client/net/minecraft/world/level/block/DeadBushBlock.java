package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DeadBushBlock extends VegetationBlock {
   public static final MapCodec<DeadBushBlock> CODEC = simpleCodec(DeadBushBlock::new);
   private static final VoxelShape SHAPE = Block.column(12.0, 0.0, 13.0);
   private static final int IDLE_SOUND_CHANCE = 150;
   private static final int IDLE_SOUND_BADLANDS_DECREASED_CHANCE = 5;

   public MapCodec<DeadBushBlock> codec() {
      return CODEC;
   }

   protected DeadBushBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(BlockTags.DEAD_BUSH_MAY_PLACE_ON);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var4.nextInt(150) == 0) {
         BlockState var5 = var2.getBlockState(var3.below());
         if ((var5.is(Blocks.RED_SAND) || var5.is(BlockTags.TERRACOTTA)) && var4.nextInt(5) != 0) {
            return;
         }

         BlockState var6 = var2.getBlockState(var3.below(2));
         if (var5.is(BlockTags.PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS) && var6.is(BlockTags.PLAYS_AMBIENT_DESERT_BLOCK_SOUNDS)) {
            var2.playLocalSound((double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), SoundEvents.DEAD_BUSH_IDLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }
      }

   }
}
