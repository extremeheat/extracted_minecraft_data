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

public class SaplingBlock extends VegetationBlock implements BonemealableBlock {
   public static final MapCodec<SaplingBlock> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(TreeGrower.CODEC.fieldOf("tree").forGetter((b) -> b.treeGrower), propertiesCodec()).apply(i, SaplingBlock::new));
   public static final IntegerProperty STAGE;
   public static final int BRIGHTNESS_FOR_SAPLING_GROWTH = 9;
   public static final int TICK_CHANCE_FOR_SAPLING_GROWTH = 7;
   private static final VoxelShape SHAPE;
   protected final TreeGrower treeGrower;

   public MapCodec<? extends SaplingBlock> codec() {
      return CODEC;
   }

   protected SaplingBlock(final TreeGrower treeGrower, final BlockBehaviour.Properties properties) {
      super(properties);
      this.treeGrower = treeGrower;
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(STAGE, 0));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (level.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(7) == 0) {
         this.advanceTree(level, pos, state, random);
      }

   }

   public void advanceTree(final ServerLevel level, final BlockPos pos, final BlockState state, final RandomSource random) {
      if ((Integer)state.getValue(STAGE) == 0) {
         level.setBlock(pos, (BlockState)state.cycle(STAGE), 260);
      } else {
         this.treeGrower.growTree(level, level.getChunkSource().getGenerator(), pos, state, random);
      }

   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state) {
      if (level instanceof ServerLevel serverLevel) {
         if (!this.treeGrower.canGrow(serverLevel, pos, state)) {
            return false;
         } else {
            int heightOffset = this.treeGrower.getMinimumHeight(serverLevel).orElse(0);
            return level.isInsideBuildHeight(pos.above(heightOffset));
         }
      } else {
         return false;
      }
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state) {
      return (double)level.getRandom().nextFloat() < 0.45;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state) {
      this.advanceTree(level, pos, state, random);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(STAGE);
   }

   static {
      STAGE = BlockStateProperties.STAGE;
      SHAPE = Block.column(12.0, 0.0, 12.0);
   }
}
