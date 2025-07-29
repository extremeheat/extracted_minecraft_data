package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.IntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CopperGolemStatueBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<CopperGolemStatueBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(CopperGolemStatueBlock::getWeatheringState), propertiesCodec()).apply(var0, CopperGolemStatueBlock::new));
   public static final EnumProperty<Direction> FACING;
   public static final EnumProperty<Pose> POSE;
   public static final BooleanProperty WATERLOGGED;
   private static final VoxelShape SHAPE;
   private final WeatheringCopper.WeatherState weatheringState;

   public MapCodec<? extends CopperGolemStatueBlock> codec() {
      return CODEC;
   }

   public CopperGolemStatueBlock(WeatheringCopper.WeatherState var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.weatheringState = var1;
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH)).setValue(POSE, CopperGolemStatueBlock.Pose.STANDING)).setValue(WATERLOGGED, false));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      super.createBlockStateDefinition(var1);
      var1.add(FACING, POSE, WATERLOGGED);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite())).setValue(WATERLOGGED, var2.getType() == Fluids.WATER);
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public WeatheringCopper.WeatherState getWeatheringState() {
      return this.weatheringState;
   }

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if (var1.is(ItemTags.AXES)) {
         return InteractionResult.PASS;
      } else {
         this.updatePose(var3, var2, var4, var5);
         return InteractionResult.SUCCESS;
      }
   }

   void updatePose(Level var1, BlockState var2, BlockPos var3, Player var4) {
      var1.playSound((Entity)null, var3, SoundEvents.COPPER_GOLEM_BECOME_STATUE, SoundSource.BLOCKS);
      var1.setBlock(var3, (BlockState)var2.setValue(BlockStateProperties.COPPER_GOLEM_POSE, ((Pose)var2.getValue(BlockStateProperties.COPPER_GOLEM_POSE)).getNextPose()), 3);
      var1.gameEvent(var4, GameEvent.BLOCK_CHANGE, var3);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return var2 == PathComputationType.WATER && var1.getFluidState().is(FluidTags.WATER);
   }

   public @Nullable BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new CopperGolemStatueBlockEntity(var1, var2);
   }

   public boolean shouldChangedStateKeepBlockEntity(BlockState var1) {
      return var1.is(BlockTags.COPPER_GOLEM_STATUES);
   }

   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3, Direction var4) {
      return ((Pose)var1.getValue(BlockStateProperties.COPPER_GOLEM_POSE)).ordinal() + 1;
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   static {
      FACING = BlockStateProperties.HORIZONTAL_FACING;
      POSE = BlockStateProperties.COPPER_GOLEM_POSE;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE = Block.cube(10.0, 14.0, 10.0);
   }

   public static enum Pose implements StringRepresentable {
      STANDING("standing"),
      SITTING("sitting"),
      RUNNING("running"),
      STAR("star");

      public static final IntFunction<Pose> BY_ID = ByIdMap.<Pose>continuous(Enum::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final Codec<Pose> CODEC = StringRepresentable.<Pose>fromEnum(Pose::values);
      private final String name;

      private Pose(final String var3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
      }

      public Pose getNextPose() {
         return (Pose)BY_ID.apply(this.ordinal() + 1);
      }

      // $FF: synthetic method
      private static Pose[] $values() {
         return new Pose[]{STANDING, SITTING, RUNNING, STAR};
      }
   }
}
