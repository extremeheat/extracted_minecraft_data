package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CreakingHeartBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class CreakingHeartBlock extends BaseEntityBlock {
   public static final MapCodec<CreakingHeartBlock> CODEC = simpleCodec(CreakingHeartBlock::new);
   public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
   public static final EnumProperty<CreakingHeartBlock.CreakingHeartState> CREAKING = BlockStateProperties.CREAKING;

   @Override
   public MapCodec<CreakingHeartBlock> codec() {
      return CODEC;
   }

   protected CreakingHeartBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y).setValue(CREAKING, CreakingHeartBlock.CreakingHeartState.DISABLED));
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new CreakingHeartBlockEntity(var1, var2);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      if (var1.isClientSide) {
         return null;
      } else {
         return var2.getValue(CREAKING) != CreakingHeartBlock.CreakingHeartState.DISABLED
            ? createTickerHelper(var3, BlockEntityType.CREAKING_HEART, CreakingHeartBlockEntity::serverTick)
            : null;
      }
   }

   public static boolean canSummonCreaking(Level var0) {
      return var0.dimensionType().natural() && var0.isNight();
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (canSummonCreaking(var2)) {
         if (var1.getValue(CREAKING) != CreakingHeartBlock.CreakingHeartState.DISABLED) {
            if (var4.nextInt(16) == 0 && isSurroundedByLogs(var2, var3)) {
               var2.playLocalSound(
                  (double)(var3.getX() + var2.random.nextIntBetweenInclusive(-16, 16)),
                  (double)(var3.getY() + var2.random.nextIntBetweenInclusive(-14, 2)),
                  (double)(var3.getZ() + var2.random.nextIntBetweenInclusive(-16, 16)),
                  SoundEvents.CREAKING_HEART_IDLE,
                  SoundSource.BLOCKS,
                  1.0F,
                  1.0F,
                  false
               );
            }
         }
      }
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      BlockState var9 = super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
      return updateState(var9, var2, var4);
   }

   private static BlockState updateState(BlockState var0, LevelReader var1, BlockPos var2) {
      boolean var3 = hasRequiredLogs(var0, var1, var2);
      CreakingHeartBlock.CreakingHeartState var4 = var0.getValue(CREAKING);
      return var3 && var4 == CreakingHeartBlock.CreakingHeartState.DISABLED ? var0.setValue(CREAKING, CreakingHeartBlock.CreakingHeartState.DORMANT) : var0;
   }

   public static boolean hasRequiredLogs(BlockState var0, LevelReader var1, BlockPos var2) {
      Direction.Axis var3 = var0.getValue(AXIS);

      for (Direction var7 : var3.getDirections()) {
         BlockState var8 = var1.getBlockState(var2.relative(var7));
         if (!var8.is(BlockTags.PALE_OAK_LOGS) || var8.getValue(AXIS) != var3) {
            return false;
         }
      }

      return true;
   }

   private static boolean isSurroundedByLogs(LevelAccessor var0, BlockPos var1) {
      for (Direction var5 : Direction.values()) {
         BlockPos var6 = var1.relative(var5);
         BlockState var7 = var0.getBlockState(var6);
         if (!var7.is(BlockTags.LOGS)) {
            return false;
         }
      }

      return true;
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return updateState(this.defaultBlockState().setValue(AXIS, var1.getClickedFace().getAxis()), var1.getLevel(), var1.getClickedPos());
   }

   @Override
   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return RotatedPillarBlock.rotatePillar(var1, var2);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AXIS, CREAKING);
   }

   @Override
   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var2.getBlockEntity(var3) instanceof CreakingHeartBlockEntity var6) {
         var6.removeProtector(null);
      }

      super.onRemove(var1, var2, var3, var4, var5);
   }

   @Override
   public BlockState playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      if (var1.getBlockEntity(var2) instanceof CreakingHeartBlockEntity var5) {
         var5.removeProtector(var4.damageSources().playerAttack(var4));
      }

      return super.playerWillDestroy(var1, var2, var3, var4);
   }

   public static enum CreakingHeartState implements StringRepresentable {
      DISABLED("disabled"),
      DORMANT("dormant"),
      ACTIVE("active");

      private final String name;

      private CreakingHeartState(final String nullxx) {
         this.name = nullxx;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
