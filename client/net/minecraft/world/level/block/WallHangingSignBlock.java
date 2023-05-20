package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallHangingSignBlock extends SignBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final VoxelShape PLANK_NORTHSOUTH = Block.box(0.0, 14.0, 6.0, 16.0, 16.0, 10.0);
   public static final VoxelShape PLANK_EASTWEST = Block.box(6.0, 14.0, 0.0, 10.0, 16.0, 16.0);
   public static final VoxelShape SHAPE_NORTHSOUTH = Shapes.or(PLANK_NORTHSOUTH, Block.box(1.0, 0.0, 7.0, 15.0, 10.0, 9.0));
   public static final VoxelShape SHAPE_EASTWEST = Shapes.or(PLANK_EASTWEST, Block.box(7.0, 0.0, 1.0, 9.0, 10.0, 15.0));
   private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(
      ImmutableMap.of(Direction.NORTH, SHAPE_NORTHSOUTH, Direction.SOUTH, SHAPE_NORTHSOUTH, Direction.EAST, SHAPE_EASTWEST, Direction.WEST, SHAPE_EASTWEST)
   );

   public WallHangingSignBlock(BlockBehaviour.Properties var1, WoodType var2) {
      super(var1.sound(var2.hangingSignSoundType()), var2);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      BlockEntity var8 = var2.getBlockEntity(var3);
      if (var8 instanceof SignBlockEntity var7) {
         ItemStack var9 = var4.getItemInHand(var5);
         if (!var7.hasAnyClickCommands(var4) && var9.getItem() instanceof BlockItem) {
            return InteractionResult.PASS;
         }
      }

      return super.use(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return AABBS.get(var1.getValue(FACING));
   }

   @Override
   public VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return this.getShape(var1, var2, var3, CollisionContext.empty());
   }

   @Override
   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch((Direction)var1.getValue(FACING)) {
         case EAST:
         case WEST:
            return PLANK_EASTWEST;
         default:
            return PLANK_NORTHSOUTH;
      }
   }

   public boolean canPlace(BlockState var1, LevelReader var2, BlockPos var3) {
      Direction var4 = var1.getValue(FACING).getClockWise();
      Direction var5 = var1.getValue(FACING).getCounterClockWise();
      return this.canAttachTo(var2, var1, var3.relative(var4), var5) || this.canAttachTo(var2, var1, var3.relative(var5), var4);
   }

   public boolean canAttachTo(LevelReader var1, BlockState var2, BlockPos var3, Direction var4) {
      BlockState var5 = var1.getBlockState(var3);
      return var5.is(BlockTags.WALL_HANGING_SIGNS)
         ? var5.getValue(FACING).getAxis().test(var2.getValue(FACING))
         : var5.isFaceSturdy(var1, var3, var4, SupportType.FULL);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = this.defaultBlockState();
      FluidState var3 = var1.getLevel().getFluidState(var1.getClickedPos());
      Level var4 = var1.getLevel();
      BlockPos var5 = var1.getClickedPos();

      for(Direction var9 : var1.getNearestLookingDirections()) {
         if (var9.getAxis().isHorizontal() && !var9.getAxis().test(var1.getClickedFace())) {
            Direction var10 = var9.getOpposite();
            var2 = var2.setValue(FACING, var10);
            if (var2.canSurvive(var4, var5) && this.canPlace(var2, var4, var5)) {
               return var2.setValue(WATERLOGGED, Boolean.valueOf(var3.getType() == Fluids.WATER));
            }
         }
      }

      return null;
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2.getAxis() == var1.getValue(FACING).getClockWise().getAxis() && !var1.canSurvive(var4, var5)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, WATERLOGGED);
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new HangingSignBlockEntity(var1, var2);
   }

   @Override
   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }
}
