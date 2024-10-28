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
import net.minecraft.world.damagesource.DamageSource;
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
   public static final EnumProperty<Direction.Axis> AXIS;
   public static final EnumProperty<CreakingHeartState> CREAKING;

   public MapCodec<CreakingHeartBlock> codec() {
      return CODEC;
   }

   protected CreakingHeartBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(AXIS, Direction.Axis.Y)).setValue(CREAKING, CreakingHeartBlock.CreakingHeartState.DISABLED));
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new CreakingHeartBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      if (var1.isClientSide) {
         return null;
      } else {
         return var2.getValue(CREAKING) != CreakingHeartBlock.CreakingHeartState.DISABLED ? createTickerHelper(var3, BlockEntityType.CREAKING_HEART, CreakingHeartBlockEntity::serverTick) : null;
      }
   }

   public static boolean canSummonCreaking(Level var0) {
      return var0.dimensionType().natural() && var0.isNight();
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (canSummonCreaking(var2)) {
         if (var1.getValue(CREAKING) != CreakingHeartBlock.CreakingHeartState.DISABLED) {
            if (var4.nextInt(16) == 0 && isSurroundedByLogs(var2, var3)) {
               var2.playLocalSound((double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), SoundEvents.CREAKING_HEART_IDLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

         }
      }
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      BlockState var9 = super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
      return updateState(var9, var2, var4);
   }

   private static BlockState updateState(BlockState var0, LevelReader var1, BlockPos var2) {
      boolean var3 = hasRequiredLogs(var0, var1, var2);
      CreakingHeartState var4 = (CreakingHeartState)var0.getValue(CREAKING);
      return var3 && var4 == CreakingHeartBlock.CreakingHeartState.DISABLED ? (BlockState)var0.setValue(CREAKING, CreakingHeartBlock.CreakingHeartState.DORMANT) : var0;
   }

   public static boolean hasRequiredLogs(BlockState var0, LevelReader var1, BlockPos var2) {
      Direction.Axis var3 = (Direction.Axis)var0.getValue(AXIS);
      Direction[] var4 = var3.getDirections();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction var7 = var4[var6];
         BlockState var8 = var1.getBlockState(var2.relative(var7));
         if (!var8.is(BlockTags.PALE_OAK_LOGS) || var8.getValue(AXIS) != var3) {
            return false;
         }
      }

      return true;
   }

   private static boolean isSurroundedByLogs(LevelAccessor var0, BlockPos var1) {
      Direction[] var2 = Direction.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction var5 = var2[var4];
         BlockPos var6 = var1.relative(var5);
         BlockState var7 = var0.getBlockState(var6);
         if (!var7.is(BlockTags.PALE_OAK_LOGS)) {
            return false;
         }
      }

      return true;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return updateState((BlockState)this.defaultBlockState().setValue(AXIS, var1.getClickedFace().getAxis()), var1.getLevel(), var1.getClickedPos());
   }

   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return RotatedPillarBlock.rotatePillar(var1, var2);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AXIS, CREAKING);
   }

   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      BlockEntity var7 = var2.getBlockEntity(var3);
      if (var7 instanceof CreakingHeartBlockEntity var6) {
         var6.removeProtector((DamageSource)null);
      }

      super.onRemove(var1, var2, var3, var4, var5);
   }

   public BlockState playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      BlockEntity var6 = var1.getBlockEntity(var2);
      if (var6 instanceof CreakingHeartBlockEntity var5) {
         var5.removeProtector(var4.damageSources().playerAttack(var4));
      }

      return super.playerWillDestroy(var1, var2, var3, var4);
   }

   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      if (var1.getValue(CREAKING) != CreakingHeartBlock.CreakingHeartState.ACTIVE) {
         return 0;
      } else {
         BlockEntity var5 = var2.getBlockEntity(var3);
         if (var5 instanceof CreakingHeartBlockEntity) {
            CreakingHeartBlockEntity var4 = (CreakingHeartBlockEntity)var5;
            return var4.getAnalogOutputSignal();
         } else {
            return 0;
         }
      }
   }

   static {
      AXIS = BlockStateProperties.AXIS;
      CREAKING = BlockStateProperties.CREAKING;
   }

   public static enum CreakingHeartState implements StringRepresentable {
      DISABLED("disabled"),
      DORMANT("dormant"),
      ACTIVE("active");

      private final String name;

      private CreakingHeartState(final String var3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static CreakingHeartState[] $values() {
         return new CreakingHeartState[]{DISABLED, DORMANT, ACTIVE};
      }
   }
}
