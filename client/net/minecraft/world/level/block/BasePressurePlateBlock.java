package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BasePressurePlateBlock extends Block {
   protected static final VoxelShape PRESSED_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 0.5, 15.0);
   protected static final VoxelShape AABB = Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);
   protected static final AABB TOUCH_AABB = new AABB(0.0625, 0.0, 0.0625, 0.9375, 0.25, 0.9375);
   protected final BlockSetType type;

   protected BasePressurePlateBlock(BlockBehaviour.Properties var1, BlockSetType var2) {
      super(var1.sound(var2.soundType()));
      this.type = var2;
   }

   @Override
   protected abstract MapCodec<? extends BasePressurePlateBlock> codec();

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getSignalForState(var1) > 0 ? PRESSED_AABB : AABB;
   }

   protected int getPressedTime() {
      return 20;
   }

   @Override
   public boolean isPossibleToRespawnInThis(BlockState var1) {
      return true;
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      return var5 == Direction.DOWN && !var1.canSurvive(var2, var4)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      return canSupportRigidBlock(var2, var4) || canSupportCenter(var2, var4, Direction.UP);
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      int var5 = this.getSignalForState(var1);
      if (var5 > 0) {
         this.checkPressed(null, var2, var3, var1, var5);
      }
   }

   @Override
   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var2.isClientSide) {
         int var5 = this.getSignalForState(var1);
         if (var5 == 0) {
            this.checkPressed(var4, var2, var3, var1, var5);
         }
      }
   }

   private void checkPressed(@Nullable Entity var1, Level var2, BlockPos var3, BlockState var4, int var5) {
      int var6 = this.getSignalStrength(var2, var3);
      boolean var7 = var5 > 0;
      boolean var8 = var6 > 0;
      if (var5 != var6) {
         BlockState var9 = this.setSignalForState(var4, var6);
         var2.setBlock(var3, var9, 2);
         this.updateNeighbours(var2, var3);
         var2.setBlocksDirty(var3, var4, var9);
      }

      if (!var8 && var7) {
         var2.playSound(null, var3, this.type.pressurePlateClickOff(), SoundSource.BLOCKS);
         var2.gameEvent(var1, GameEvent.BLOCK_DEACTIVATE, var3);
      } else if (var8 && !var7) {
         var2.playSound(null, var3, this.type.pressurePlateClickOn(), SoundSource.BLOCKS);
         var2.gameEvent(var1, GameEvent.BLOCK_ACTIVATE, var3);
      }

      if (var8) {
         var2.scheduleTick(new BlockPos(var3), this, this.getPressedTime());
      }
   }

   @Override
   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5 && !var1.is(var4.getBlock())) {
         if (this.getSignalForState(var1) > 0) {
            this.updateNeighbours(var2, var3);
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   protected void updateNeighbours(Level var1, BlockPos var2) {
      var1.updateNeighborsAt(var2, this);
      var1.updateNeighborsAt(var2.below(), this);
   }

   @Override
   protected int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return this.getSignalForState(var1);
   }

   @Override
   protected int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var4 == Direction.UP ? this.getSignalForState(var1) : 0;
   }

   @Override
   protected boolean isSignalSource(BlockState var1) {
      return true;
   }

   protected static int getEntityCount(Level var0, AABB var1, Class<? extends Entity> var2) {
      return var0.getEntitiesOfClass(var2, var1, EntitySelector.NO_SPECTATORS.and(var0x -> !var0x.isIgnoringBlockTriggers())).size();
   }

   protected abstract int getSignalStrength(Level var1, BlockPos var2);

   protected abstract int getSignalForState(BlockState var1);

   protected abstract BlockState setSignalForState(BlockState var1, int var2);
}
