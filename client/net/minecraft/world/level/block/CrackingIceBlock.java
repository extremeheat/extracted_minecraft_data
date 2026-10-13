package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.Nullable;

public abstract class CrackingIceBlock extends IceBlock {
   public static final IntegerProperty AGE;
   public static final int MAX_AGE = 3;

   public CrackingIceBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   protected void propagateMelting(final ServerLevel level, final BlockPos pos, final RandomSource random) {
      BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();

      for(Direction direction : Direction.values()) {
         neighborPos.setWithOffset(pos, (Direction)direction);
         BlockState neighbour = level.getBlockState(neighborPos);
         if (neighbour.is(this) && !this.slightlyMelt(neighbour, level, neighborPos)) {
            level.scheduleTick(neighborPos, this, this.getTickDelay(random));
         }
      }

   }

   protected boolean slightlyMelt(final BlockState state, final Level level, final BlockPos pos) {
      int age = (Integer)state.getValue(AGE);
      if (age < 3) {
         level.setBlock(pos, (BlockState)state.setValue(AGE, age + 1), 2);
         SoundEvent crackingSound = this.getCrackingIceSound();
         if (crackingSound != null) {
            level.playSound((Entity)null, (BlockPos)pos, crackingSound, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         this.playCrackingIceParticle(state, level, pos);
         level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
         return false;
      } else {
         this.melt(state, level, pos);
         return true;
      }
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(AGE);
   }

   protected abstract int getTickDelay(final RandomSource random);

   protected void playCrackingIceParticle(final BlockState state, final Level level, final BlockPos pos) {
   }

   protected @Nullable SoundEvent getCrackingIceSound() {
      return null;
   }

   static {
      AGE = BlockStateProperties.AGE_3;
   }
}
