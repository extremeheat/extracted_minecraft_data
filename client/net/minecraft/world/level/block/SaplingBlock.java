package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SaplingBlock extends BushBlock implements BonemealableBlock {
   public static final MapCodec<SaplingBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(TreeGrower.CODEC.fieldOf("tree").forGetter(var0x -> var0x.treeGrower), propertiesCodec()).apply(var0, SaplingBlock::new)
   );
   public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
   protected static final float AABB_OFFSET = 6.0F;
   protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
   protected final TreeGrower treeGrower;

   @Override
   public MapCodec<? extends SaplingBlock> codec() {
      return CODEC;
   }

   protected SaplingBlock(TreeGrower var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.treeGrower = var1;
      this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, Integer.valueOf(0)));
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var2.getMaxLocalRawBrightness(var3.above()) >= 9 && var4.nextInt(7) == 0) {
         this.advanceTree(var2, var3, var1, var4);
      }
   }

   public void advanceTree(ServerLevel var1, BlockPos var2, BlockState var3, RandomSource var4) {
      if (var3.getValue(STAGE) == 0) {
         var1.setBlock(var2, var3.cycle(STAGE), 4);
      } else {
         this.treeGrower.growTree(var1, var1.getChunkSource().getGenerator(), var2, var3, var4);
      }
   }

   @Override
   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return true;
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return (double)var1.random.nextFloat() < 0.45;
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      this.advanceTree(var1, var3, var4, var2);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(STAGE);
   }
}
