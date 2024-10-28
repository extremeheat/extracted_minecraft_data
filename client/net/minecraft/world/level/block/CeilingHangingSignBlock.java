package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
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
   public static final MapCodec<CeilingHangingSignBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(WoodType.CODEC.fieldOf("wood_type").forGetter(SignBlock::type), propertiesCodec()).apply(var0, CeilingHangingSignBlock::new);
   });
   public static final IntegerProperty ROTATION;
   public static final BooleanProperty ATTACHED;
   protected static final float AABB_OFFSET = 5.0F;
   protected static final VoxelShape SHAPE;
   private static final Map<Integer, VoxelShape> AABBS;

   public MapCodec<CeilingHangingSignBlock> codec() {
      return CODEC;
   }

   public CeilingHangingSignBlock(WoodType var1, BlockBehaviour.Properties var2) {
      super(var1, var2.sound(var1.hangingSignSoundType()));
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, 0)).setValue(ATTACHED, false)).setValue(WATERLOGGED, false));
   }

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      BlockEntity var9 = var3.getBlockEntity(var4);
      if (var9 instanceof SignBlockEntity var8) {
         if (this.shouldTryToChainAnotherHangingSign(var5, var7, var8, var1)) {
            return InteractionResult.PASS;
         }
      }

      return super.useItemOn(var1, var2, var3, var4, var5, var6, var7);
   }

   private boolean shouldTryToChainAnotherHangingSign(Player var1, BlockHitResult var2, SignBlockEntity var3, ItemStack var4) {
      return !var3.canExecuteClickCommands(var3.isFacingFrontText(var1), var1) && var4.getItem() instanceof HangingSignItem && var2.getDirection().equals(Direction.DOWN);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return var2.getBlockState(var3.above()).isFaceSturdy(var2, var3.above(), Direction.DOWN, SupportType.CENTER);
   }

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
            Direction var9 = (Direction)var5.getValue(WallHangingSignBlock.FACING);
            if (var9.getAxis().test(var7)) {
               var8 = false;
            }
         } else if (var5.hasProperty(ROTATION)) {
            Optional var10 = RotationSegment.convertToDirection((Integer)var5.getValue(ROTATION));
            if (var10.isPresent() && ((Direction)var10.get()).getAxis().test(var7)) {
               var8 = false;
            }
         }
      }

      int var11 = !var8 ? RotationSegment.convertToSegment(var7.getOpposite()) : RotationSegment.convertToSegment(var1.getRotation() + 180.0F);
      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(ATTACHED, var8)).setValue(ROTATION, var11)).setValue(WATERLOGGED, var3.getType() == Fluids.WATER);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      VoxelShape var5 = (VoxelShape)AABBS.get(var1.getValue(ROTATION));
      return var5 == null ? SHAPE : var5;
   }

   protected VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return this.getShape(var1, var2, var3, CollisionContext.empty());
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      return var5 == Direction.UP && !this.canSurvive(var1, var2, var4) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public float getYRotationDegrees(BlockState var1) {
      return RotationSegment.convertToDegrees((Integer)var1.getValue(ROTATION));
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(ROTATION, var2.rotate((Integer)var1.getValue(ROTATION), 16));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return (BlockState)var1.setValue(ROTATION, var2.mirror((Integer)var1.getValue(ROTATION), 16));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(ROTATION, ATTACHED, WATERLOGGED);
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new HangingSignBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createTickerHelper(var3, BlockEntityType.HANGING_SIGN, SignBlockEntity::tick);
   }

   static {
      ROTATION = BlockStateProperties.ROTATION_16;
      ATTACHED = BlockStateProperties.ATTACHED;
      SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
      AABBS = Maps.newHashMap(ImmutableMap.of(0, Block.box(1.0, 0.0, 7.0, 15.0, 10.0, 9.0), 4, Block.box(7.0, 0.0, 1.0, 9.0, 10.0, 15.0), 8, Block.box(1.0, 0.0, 7.0, 15.0, 10.0, 9.0), 12, Block.box(7.0, 0.0, 1.0, 9.0, 10.0, 15.0)));
   }
}
