package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallHangingSignBlock extends SignBlock {
   public static final MapCodec<WallHangingSignBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(WoodType.CODEC.fieldOf("wood_type").forGetter(SignBlock::type), propertiesCodec()).apply(var0, WallHangingSignBlock::new);
   });
   public static final EnumProperty<Direction> FACING;
   public static final VoxelShape PLANK_NORTHSOUTH;
   public static final VoxelShape PLANK_EASTWEST;
   public static final VoxelShape SHAPE_NORTHSOUTH;
   public static final VoxelShape SHAPE_EASTWEST;
   private static final Map<Direction, VoxelShape> AABBS;

   public MapCodec<WallHangingSignBlock> codec() {
      return CODEC;
   }

   public WallHangingSignBlock(WoodType var1, BlockBehaviour.Properties var2) {
      super(var1, var2.sound(var1.hangingSignSoundType()));
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false));
   }

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      BlockEntity var9 = var3.getBlockEntity(var4);
      if (var9 instanceof SignBlockEntity var8) {
         if (this.shouldTryToChainAnotherHangingSign(var2, var5, var7, var8, var1)) {
            return InteractionResult.PASS;
         }
      }

      return super.useItemOn(var1, var2, var3, var4, var5, var6, var7);
   }

   private boolean shouldTryToChainAnotherHangingSign(BlockState var1, Player var2, BlockHitResult var3, SignBlockEntity var4, ItemStack var5) {
      return !var4.canExecuteClickCommands(var4.isFacingFrontText(var2), var2) && var5.getItem() instanceof HangingSignItem && !this.isHittingEditableSide(var3, var1);
   }

   private boolean isHittingEditableSide(BlockHitResult var1, BlockState var2) {
      return var1.getDirection().getAxis() == ((Direction)var2.getValue(FACING)).getAxis();
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)AABBS.get(var1.getValue(FACING));
   }

   protected VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return this.getShape(var1, var2, var3, CollisionContext.empty());
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch ((Direction)var1.getValue(FACING)) {
         case EAST:
         case WEST:
            return PLANK_EASTWEST;
         default:
            return PLANK_NORTHSOUTH;
      }
   }

   public boolean canPlace(BlockState var1, LevelReader var2, BlockPos var3) {
      Direction var4 = ((Direction)var1.getValue(FACING)).getClockWise();
      Direction var5 = ((Direction)var1.getValue(FACING)).getCounterClockWise();
      return this.canAttachTo(var2, var1, var3.relative(var4), var5) || this.canAttachTo(var2, var1, var3.relative(var5), var4);
   }

   public boolean canAttachTo(LevelReader var1, BlockState var2, BlockPos var3, Direction var4) {
      BlockState var5 = var1.getBlockState(var3);
      return var5.is(BlockTags.WALL_HANGING_SIGNS) ? ((Direction)var5.getValue(FACING)).getAxis().test((Direction)var2.getValue(FACING)) : var5.isFaceSturdy(var1, var3, var4, SupportType.FULL);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = this.defaultBlockState();
      FluidState var3 = var1.getLevel().getFluidState(var1.getClickedPos());
      Level var4 = var1.getLevel();
      BlockPos var5 = var1.getClickedPos();
      Direction[] var6 = var1.getNearestLookingDirections();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction var9 = var6[var8];
         if (var9.getAxis().isHorizontal() && !var9.getAxis().test(var1.getClickedFace())) {
            Direction var10 = var9.getOpposite();
            var2 = (BlockState)var2.setValue(FACING, var10);
            if (var2.canSurvive(var4, var5) && this.canPlace(var2, var4, var5)) {
               return (BlockState)var2.setValue(WATERLOGGED, var3.getType() == Fluids.WATER);
            }
         }
      }

      return null;
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      return var5.getAxis() == ((Direction)var1.getValue(FACING)).getClockWise().getAxis() && !var1.canSurvive(var2, var4) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public float getYRotationDegrees(BlockState var1) {
      return ((Direction)var1.getValue(FACING)).toYRot();
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, WATERLOGGED);
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new HangingSignBlockEntity(var1, var2);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createTickerHelper(var3, BlockEntityType.HANGING_SIGN, SignBlockEntity::tick);
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      PLANK_NORTHSOUTH = Block.box(0.0, 14.0, 6.0, 16.0, 16.0, 10.0);
      PLANK_EASTWEST = Block.box(6.0, 14.0, 0.0, 10.0, 16.0, 16.0);
      SHAPE_NORTHSOUTH = Shapes.or(PLANK_NORTHSOUTH, Block.box(1.0, 0.0, 7.0, 15.0, 10.0, 9.0));
      SHAPE_EASTWEST = Shapes.or(PLANK_EASTWEST, Block.box(7.0, 0.0, 1.0, 9.0, 10.0, 15.0));
      AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, SHAPE_NORTHSOUTH, Direction.SOUTH, SHAPE_NORTHSOUTH, Direction.EAST, SHAPE_EASTWEST, Direction.WEST, SHAPE_EASTWEST));
   }
}
