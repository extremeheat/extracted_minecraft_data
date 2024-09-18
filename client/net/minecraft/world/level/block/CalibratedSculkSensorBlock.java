package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CalibratedSculkSensorBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;

public class CalibratedSculkSensorBlock extends SculkSensorBlock {
   public static final MapCodec<CalibratedSculkSensorBlock> CODEC = simpleCodec(CalibratedSculkSensorBlock::new);
   public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

   @Override
   public MapCodec<CalibratedSculkSensorBlock> codec() {
      return CODEC;
   }

   public CalibratedSculkSensorBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new CalibratedSculkSensorBlockEntity(var1, var2);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return !var1.isClientSide
         ? createTickerHelper(
            var3,
            BlockEntityType.CALIBRATED_SCULK_SENSOR,
            (var0, var1x, var2x, var3x) -> VibrationSystem.Ticker.tick(var0, var3x.getVibrationData(), var3x.getVibrationUser())
         )
         : null;
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return super.getStateForPlacement(var1).setValue(FACING, var1.getHorizontalDirection());
   }

   @Override
   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var4 != var1.getValue(FACING) ? super.getSignal(var1, var2, var3, var4) : 0;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      super.createBlockStateDefinition(var1);
      var1.add(FACING);
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
   public int getActiveTicks() {
      return 10;
   }
}
