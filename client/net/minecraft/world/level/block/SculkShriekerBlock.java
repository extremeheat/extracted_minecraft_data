package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SculkShriekerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<SculkShriekerBlock> CODEC = simpleCodec(SculkShriekerBlock::new);
   public static final BooleanProperty SHRIEKING;
   public static final BooleanProperty WATERLOGGED;
   public static final BooleanProperty CAN_SUMMON;
   protected static final VoxelShape COLLIDER;
   public static final double TOP_Y;

   public MapCodec<SculkShriekerBlock> codec() {
      return CODEC;
   }

   public SculkShriekerBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(SHRIEKING, false)).setValue(WATERLOGGED, false)).setValue(CAN_SUMMON, false));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(SHRIEKING);
      var1.add(WATERLOGGED);
      var1.add(CAN_SUMMON);
   }

   public void stepOn(Level var1, BlockPos var2, BlockState var3, Entity var4) {
      if (var1 instanceof ServerLevel var5) {
         ServerPlayer var6 = SculkShriekerBlockEntity.tryGetPlayer(var4);
         if (var6 != null) {
            var5.getBlockEntity(var2, BlockEntityType.SCULK_SHRIEKER).ifPresent((var2x) -> {
               var2x.tryShriek(var5, var6);
            });
         }
      }

      super.stepOn(var1, var2, var3, var4);
   }

   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var2 instanceof ServerLevel var6) {
         if ((Boolean)var1.getValue(SHRIEKING) && !var1.is(var4.getBlock())) {
            var6.getBlockEntity(var3, BlockEntityType.SCULK_SHRIEKER).ifPresent((var1x) -> {
               var1x.tryRespond(var6);
            });
         }
      }

      super.onRemove(var1, var2, var3, var4, var5);
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if ((Boolean)var1.getValue(SHRIEKING)) {
         var2.setBlock(var3, (BlockState)var1.setValue(SHRIEKING, false), 3);
         var2.getBlockEntity(var3, BlockEntityType.SCULK_SHRIEKER).ifPresent((var1x) -> {
            var1x.tryRespond(var2);
         });
      }

   }

   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return COLLIDER;
   }

   protected VoxelShape getOcclusionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return COLLIDER;
   }

   protected boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new SculkShriekerBlockEntity(var1, var2);
   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, var1.getLevel().getFluidState(var1.getClickedPos()).getType() == Fluids.WATER);
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   protected void spawnAfterBreak(BlockState var1, ServerLevel var2, BlockPos var3, ItemStack var4, boolean var5) {
      super.spawnAfterBreak(var1, var2, var3, var4, var5);
      if (var5) {
         this.tryDropExperience(var2, var3, var4, ConstantInt.of(5));
      }

   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return !var1.isClientSide ? BaseEntityBlock.createTickerHelper(var3, BlockEntityType.SCULK_SHRIEKER, (var0, var1x, var2x, var3x) -> {
         VibrationSystem.Ticker.tick(var0, var3x.getVibrationData(), var3x.getVibrationUser());
      }) : null;
   }

   static {
      SHRIEKING = BlockStateProperties.SHRIEKING;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      CAN_SUMMON = BlockStateProperties.CAN_SUMMON;
      COLLIDER = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
      TOP_Y = COLLIDER.max(Direction.Axis.Y);
   }
}
