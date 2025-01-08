package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LeavesBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<LeavesBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.POSITIVE_INT.fieldOf("leaf_particle_chance").forGetter((var0x) -> var0x.leafParticleChance), ParticleTypes.CODEC.fieldOf("leaf_particle").forGetter((var0x) -> var0x.leafParticle), propertiesCodec()).apply(var0, LeavesBlock::new));
   public static final int DECAY_DISTANCE = 7;
   public static final IntegerProperty DISTANCE;
   public static final BooleanProperty PERSISTENT;
   public static final BooleanProperty WATERLOGGED;
   protected final ParticleOptions leafParticle;
   protected final int leafParticleChance;
   private static final int TICK_DELAY = 1;

   public MapCodec<? extends LeavesBlock> codec() {
      return CODEC;
   }

   public LeavesBlock(int var1, ParticleOptions var2, BlockBehaviour.Properties var3) {
      super(var3);
      this.leafParticleChance = var1;
      this.leafParticle = var2;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(DISTANCE, 7)).setValue(PERSISTENT, false)).setValue(WATERLOGGED, false));
   }

   protected VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.empty();
   }

   protected boolean isRandomlyTicking(BlockState var1) {
      return (Integer)var1.getValue(DISTANCE) == 7 && !(Boolean)var1.getValue(PERSISTENT);
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (this.decaying(var1)) {
         dropResources(var1, var2, var3);
         var2.removeBlock(var3, false);
      }

   }

   protected boolean decaying(BlockState var1) {
      return !(Boolean)var1.getValue(PERSISTENT) && (Integer)var1.getValue(DISTANCE) == 7;
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      var2.setBlock(var3, updateDistance(var1, var2, var3), 3);
   }

   protected int getLightBlock(BlockState var1) {
      return 1;
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      int var9 = getDistanceAt(var7) + 1;
      if (var9 != 1 || (Integer)var1.getValue(DISTANCE) != var9) {
         var3.scheduleTick(var4, (Block)this, 1);
      }

      return var1;
   }

   private static BlockState updateDistance(BlockState var0, LevelAccessor var1, BlockPos var2) {
      int var3 = 7;
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();

      for(Direction var8 : Direction.values()) {
         var4.setWithOffset(var2, (Direction)var8);
         var3 = Math.min(var3, getDistanceAt(var1.getBlockState(var4)) + 1);
         if (var3 == 1) {
            break;
         }
      }

      return (BlockState)var0.setValue(DISTANCE, var3);
   }

   private static int getDistanceAt(BlockState var0) {
      return getOptionalDistanceAt(var0).orElse(7);
   }

   public static OptionalInt getOptionalDistanceAt(BlockState var0) {
      if (var0.is(BlockTags.LOGS)) {
         return OptionalInt.of(0);
      } else {
         return var0.hasProperty(DISTANCE) ? OptionalInt.of((Integer)var0.getValue(DISTANCE)) : OptionalInt.empty();
      }
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      super.animateTick(var1, var2, var3, var4);
      BlockPos var5 = var3.below();
      BlockState var6 = var2.getBlockState(var5);
      makeDrippingWaterParticles(var2, var3, var4, var6, var5);
      this.makeFallingLeavesParticles(var2, var3, var4, var6, var5);
   }

   private static void makeDrippingWaterParticles(Level var0, BlockPos var1, RandomSource var2, BlockState var3, BlockPos var4) {
      if (var0.isRainingAt(var1.above())) {
         if (var2.nextInt(15) == 1) {
            if (!var3.canOcclude() || !var3.isFaceSturdy(var0, var4, Direction.UP)) {
               ParticleUtils.spawnParticleBelow(var0, var1, var2, ParticleTypes.DRIPPING_WATER);
            }
         }
      }
   }

   private void makeFallingLeavesParticles(Level var1, BlockPos var2, RandomSource var3, BlockState var4, BlockPos var5) {
      if (var3.nextInt(this.leafParticleChance) == 0) {
         if (!isFaceFull(var4.getCollisionShape(var1, var5), Direction.UP)) {
            ParticleUtils.spawnParticleBelow(var1, var2, var3, this.leafParticle);
         }
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(DISTANCE, PERSISTENT, WATERLOGGED);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      BlockState var3 = (BlockState)((BlockState)this.defaultBlockState().setValue(PERSISTENT, true)).setValue(WATERLOGGED, var2.getType() == Fluids.WATER);
      return updateDistance(var3, var1.getLevel(), var1.getClickedPos());
   }

   static {
      DISTANCE = BlockStateProperties.DISTANCE;
      PERSISTENT = BlockStateProperties.PERSISTENT;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
   }
}
