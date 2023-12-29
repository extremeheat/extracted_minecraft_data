package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

public class DaylightDetectorBlock extends BaseEntityBlock {
   public static final MapCodec<DaylightDetectorBlock> CODEC = simpleCodec(DaylightDetectorBlock::new);
   public static final IntegerProperty POWER = BlockStateProperties.POWER;
   public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;
   protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);

   @Override
   public MapCodec<DaylightDetectorBlock> codec() {
      return CODEC;
   }

   public DaylightDetectorBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(POWER, Integer.valueOf(0)).setValue(INVERTED, Boolean.valueOf(false)));
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   public boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   @Override
   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var1.getValue(POWER);
   }

   private static void updateSignalStrength(BlockState var0, Level var1, BlockPos var2) {
      int var3 = var1.getBrightness(LightLayer.SKY, var2) - var1.getSkyDarken();
      float var4 = var1.getSunAngle(1.0F);
      boolean var5 = var0.getValue(INVERTED);
      if (var5) {
         var3 = 15 - var3;
      } else if (var3 > 0) {
         float var6 = var4 < 3.1415927F ? 0.0F : 6.2831855F;
         var4 += (var6 - var4) * 0.2F;
         var3 = Math.round((float)var3 * Mth.cos(var4));
      }

      var3 = Mth.clamp(var3, 0, 15);
      if (var0.getValue(POWER) != var3) {
         var1.setBlock(var2, var0.setValue(POWER, Integer.valueOf(var3)), 3);
      }
   }

   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var4.mayBuild()) {
         if (var2.isClientSide) {
            return InteractionResult.SUCCESS;
         } else {
            BlockState var7 = var1.cycle(INVERTED);
            var2.setBlock(var3, var7, 2);
            var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var4, var7));
            updateSignalStrength(var7, var2, var3);
            return InteractionResult.CONSUME;
         }
      } else {
         return super.use(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Override
   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new DaylightDetectorBlockEntity(var1, var2);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return !var1.isClientSide && var1.dimensionType().hasSkyLight()
         ? createTickerHelper(var3, BlockEntityType.DAYLIGHT_DETECTOR, DaylightDetectorBlock::tickEntity)
         : null;
   }

   private static void tickEntity(Level var0, BlockPos var1, BlockState var2, DaylightDetectorBlockEntity var3) {
      if (var0.getGameTime() % 20L == 0L) {
         updateSignalStrength(var2, var0, var1);
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(POWER, INVERTED);
   }
}
