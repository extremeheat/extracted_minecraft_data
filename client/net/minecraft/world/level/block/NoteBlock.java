package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class NoteBlock extends Block {
   public static final EnumProperty<NoteBlockInstrument> INSTRUMENT = BlockStateProperties.NOTEBLOCK_INSTRUMENT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final IntegerProperty NOTE = BlockStateProperties.NOTE;

   public NoteBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(INSTRUMENT, NoteBlockInstrument.HARP)
            .setValue(NOTE, Integer.valueOf(0))
            .setValue(POWERED, Boolean.valueOf(false))
      );
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState().setValue(INSTRUMENT, NoteBlockInstrument.byState(var1.getLevel().getBlockState(var1.getClickedPos().below())));
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2 == Direction.DOWN ? var1.setValue(INSTRUMENT, NoteBlockInstrument.byState(var3)) : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      boolean var7 = var2.hasNeighborSignal(var3);
      if (var7 != var1.getValue(POWERED)) {
         if (var7) {
            this.playNote(null, var2, var3);
         }

         var2.setBlock(var3, var1.setValue(POWERED, Boolean.valueOf(var7)), 3);
      }
   }

   private void playNote(@Nullable Entity var1, Level var2, BlockPos var3) {
      if (var2.getBlockState(var3.above()).isAir()) {
         var2.blockEvent(var3, this, 0, 0);
         var2.gameEvent(var1, GameEvent.NOTE_BLOCK_PLAY, var3);
      }
   }

   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         var1 = var1.cycle(NOTE);
         var2.setBlock(var3, var1, 3);
         this.playNote(var4, var2, var3);
         var4.awardStat(Stats.TUNE_NOTEBLOCK);
         return InteractionResult.CONSUME;
      }
   }

   @Override
   public void attack(BlockState var1, Level var2, BlockPos var3, Player var4) {
      if (!var2.isClientSide) {
         this.playNote(var4, var2, var3);
         var4.awardStat(Stats.PLAY_NOTEBLOCK);
      }
   }

   @Override
   public boolean triggerEvent(BlockState var1, Level var2, BlockPos var3, int var4, int var5) {
      int var6 = var1.getValue(NOTE);
      float var7 = (float)Math.pow(2.0, (double)(var6 - 12) / 12.0);
      var2.playSound(null, var3, var1.getValue(INSTRUMENT).getSoundEvent(), SoundSource.RECORDS, 3.0F, var7);
      var2.addParticle(ParticleTypes.NOTE, (double)var3.getX() + 0.5, (double)var3.getY() + 1.2, (double)var3.getZ() + 0.5, (double)var6 / 24.0, 0.0, 0.0);
      return true;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(INSTRUMENT, POWERED, NOTE);
   }
}
