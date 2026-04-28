package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class DaylightDetectorBlock extends BaseEntityBlock {
   public static final MapCodec<DaylightDetectorBlock> CODEC = simpleCodec(DaylightDetectorBlock::new);
   public static final IntegerProperty POWER;
   public static final BooleanProperty INVERTED;
   private static final VoxelShape SHAPE;

   public MapCodec<DaylightDetectorBlock> codec() {
      return CODEC;
   }

   public DaylightDetectorBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWER, 0)).setValue(INVERTED, false));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   protected boolean useShapeForLightOcclusion(final BlockState state) {
      return true;
   }

   private static void updateSignalStrength(final BlockState state, final Level level, final BlockPos pos) {
      int target = level.getEffectiveSkyBrightness(pos);
      float sunAngle = (Float)level.environmentAttributes().getValue(EnvironmentAttributes.SUN_ANGLE, pos) * 0.017453292F;
      boolean isInverted = (Boolean)state.getValue(INVERTED);
      if (isInverted) {
         target = 15 - target;
      } else if (target > 0) {
         float offset = sunAngle < 3.1415927F ? 0.0F : 6.2831855F;
         sunAngle += (offset - sunAngle) * 0.2F;
         target = Math.round((float)target * Mth.cos((double)sunAngle));
      }

      target = Mth.clamp(target, 0, 15);
      if ((Integer)state.getValue(POWER) != target) {
         level.setBlock(pos, (BlockState)state.setValue(POWER, target), 3);
      }

   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (!player.mayBuild()) {
         return super.useWithoutItem(state, level, pos, player, hitResult);
      } else {
         if (!level.isClientSide()) {
            BlockState newState = (BlockState)state.cycle(INVERTED);
            level.setBlock(pos, newState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
            updateSignalStrength(newState, level, pos);
         }

         return InteractionResult.SUCCESS;
      }
   }

   protected boolean isSignalSource(final BlockState state) {
      return true;
   }

   protected int ownSignal(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return (Integer)state.getValue(POWER);
   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new DaylightDetectorBlockEntity(worldPosition, blockState);
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
      return !level.isClientSide() && level.dimensionType().hasSkyLight() ? createTickerHelper(type, BlockEntityTypes.DAYLIGHT_DETECTOR, DaylightDetectorBlock::tickEntity) : null;
   }

   private static void tickEntity(final Level level, final BlockPos blockPos, final BlockState blockState, final DaylightDetectorBlockEntity blockEntity) {
      if (level.getGameTime() % 20L == 0L) {
         updateSignalStrength(blockState, level, blockPos);
      }

   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(POWER, INVERTED);
   }

   static {
      POWER = BlockStateProperties.POWER;
      INVERTED = BlockStateProperties.INVERTED;
      SHAPE = Block.column(16.0, 0.0, 6.0);
   }
}
