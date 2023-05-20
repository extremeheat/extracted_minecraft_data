package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CeilingHangingSignBlock extends SignBlock {
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   protected static final float AABB_OFFSET = 5.0F;
   protected static final VoxelShape SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
   private static final Map<Integer, VoxelShape> AABBS = Maps.newHashMap(
      ImmutableMap.of(
         0,
         Block.box(1.0, 0.0, 7.0, 15.0, 10.0, 9.0),
         4,
         Block.box(7.0, 0.0, 1.0, 9.0, 10.0, 15.0),
         8,
         Block.box(1.0, 0.0, 7.0, 15.0, 10.0, 9.0),
         12,
         Block.box(7.0, 0.0, 1.0, 9.0, 10.0, 15.0)
      )
   );

   public CeilingHangingSignBlock(BlockBehaviour.Properties var1, WoodType var2) {
      super(var1.sound(var2.hangingSignSoundType()), var2);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(ROTATION, Integer.valueOf(0))
            .setValue(ATTACHED, Boolean.valueOf(false))
            .setValue(WATERLOGGED, Boolean.valueOf(false))
      );
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
   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return var2.getBlockState(var3.above()).isFaceSturdy(var2, var3.above(), Direction.DOWN, SupportType.CENTER);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      FluidState var3 = var2.getFluidState(var1.getClickedPos());
      BlockPos var4 = var1.getClickedPos().above();
      BlockState var5 = var2.getBlockState(var4);
      boolean var6 = var5.is(BlockTags.ALL_HANGING_SIGNS);
      Direction var7 = Direction.fromYRot((double)var1.getRotation());
      boolean var8 = !Block.isFaceFull(var5.getCollisionShape(var2, var4), Direction.DOWN) || var1.isSecondaryUseActive();
      if (var6 && !var1.isSecondaryUseActive()) {
         if (var5.hasProperty(WallHangingSignBlock.FACING)) {
            Direction var9 = var5.getValue(WallHangingSignBlock.FACING);
            if (var9.getAxis().test(var7)) {
               var8 = false;
            }
         } else if (var5.hasProperty(ROTATION)) {
            Optional var10 = RotationSegment.convertToDirection(var5.getValue(ROTATION));
            if (var10.isPresent() && ((Direction)var10.get()).getAxis().test(var7)) {
               var8 = false;
            }
         }
      }

      int var11 = !var8 ? RotationSegment.convertToSegment(var7.getOpposite()) : RotationSegment.convertToSegment(var1.getRotation() + 180.0F);
      return this.defaultBlockState()
         .setValue(ATTACHED, Boolean.valueOf(var8))
         .setValue(ROTATION, Integer.valueOf(var11))
         .setValue(WATERLOGGED, Boolean.valueOf(var3.getType() == Fluids.WATER));
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      VoxelShape var5 = AABBS.get(var1.getValue(ROTATION));
      return var5 == null ? SHAPE : var5;
   }

   @Override
   public VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return this.getShape(var1, var2, var3, CollisionContext.empty());
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2 == Direction.UP && !this.canSurvive(var1, var4, var5)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(ROTATION, Integer.valueOf(var2.rotate(var1.getValue(ROTATION), 16)));
   }

   @Override
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.setValue(ROTATION, Integer.valueOf(var2.mirror(var1.getValue(ROTATION), 16)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(ROTATION, ATTACHED, WATERLOGGED);
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new HangingSignBlockEntity(var1, var2);
   }
}
