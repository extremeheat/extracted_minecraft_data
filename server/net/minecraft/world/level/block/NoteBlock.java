package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.BlockHitResult;

public class NoteBlock extends Block {
   public static final EnumProperty<NoteBlockInstrument> INSTRUMENT;
   public static final BooleanProperty POWERED;
   public static final IntegerProperty NOTE;

   public NoteBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(INSTRUMENT, NoteBlockInstrument.HARP)).setValue(NOTE, 0)).setValue(POWERED, false));
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(INSTRUMENT, NoteBlockInstrument.byState(var1.getLevel().getBlockState(var1.getClickedPos().below())));
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2 == Direction.DOWN ? (BlockState)var1.setValue(INSTRUMENT, NoteBlockInstrument.byState(var3)) : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      boolean var7 = var2.hasNeighborSignal(var3);
      if (var7 != (Boolean)var1.getValue(POWERED)) {
         if (var7) {
            this.playNote(var2, var3);
         }

         var2.setBlock(var3, (BlockState)var1.setValue(POWERED, var7), 3);
      }

   }

   private void playNote(Level var1, BlockPos var2) {
      if (var1.getBlockState(var2.above()).isAir()) {
         var1.blockEvent(var2, this, 0, 0);
      }

   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         var1 = (BlockState)var1.cycle(NOTE);
         var2.setBlock(var3, var1, 3);
         this.playNote(var2, var3);
         var4.awardStat(Stats.TUNE_NOTEBLOCK);
         return InteractionResult.CONSUME;
      }
   }

   public void attack(BlockState var1, Level var2, BlockPos var3, Player var4) {
      if (!var2.isClientSide) {
         this.playNote(var2, var3);
         var4.awardStat(Stats.PLAY_NOTEBLOCK);
      }
   }

   public boolean triggerEvent(BlockState var1, Level var2, BlockPos var3, int var4, int var5) {
      int var6 = (Integer)var1.getValue(NOTE);
      float var7 = (float)Math.pow(2.0D, (double)(var6 - 12) / 12.0D);
      var2.playSound((Player)null, (BlockPos)var3, ((NoteBlockInstrument)var1.getValue(INSTRUMENT)).getSoundEvent(), SoundSource.RECORDS, 3.0F, var7);
      var2.addParticle(ParticleTypes.NOTE, (double)var3.getX() + 0.5D, (double)var3.getY() + 1.2D, (double)var3.getZ() + 0.5D, (double)var6 / 24.0D, 0.0D, 0.0D);
      return true;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(INSTRUMENT, POWERED, NOTE);
   }

   static {
      INSTRUMENT = BlockStateProperties.NOTEBLOCK_INSTRUMENT;
      POWERED = BlockStateProperties.POWERED;
      NOTE = BlockStateProperties.NOTE;
   }
}
