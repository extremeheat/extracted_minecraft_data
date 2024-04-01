package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PotatoSproutsBlock extends BushBlock implements BonemealableBlock {
   public static final MapCodec<PotatoSproutsBlock> CODEC = simpleCodec(PotatoSproutsBlock::new);
   protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

   @Override
   public MapCodec<PotatoSproutsBlock> codec() {
      return CODEC;
   }

   public PotatoSproutsBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Vec3 var5 = var1.getOffset(var2, var3);
      return SHAPE.move(var5.x, var5.y, var5.z);
   }

   @Override
   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(Blocks.CORRUPTED_PEELGRASS_BLOCK) || var1.is(Blocks.PEELGRASS_BLOCK);
   }

   @Override
   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return true;
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return (double)var1.random.nextFloat() < 0.25;
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      TreeGrower.POTATO.growTree(var1, var1.getChunkSource().getGenerator(), var3, var4, var2);
   }
}
