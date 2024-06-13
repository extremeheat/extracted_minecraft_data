package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
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
   public static final MapCodec<NoteBlock> CODEC = simpleCodec(NoteBlock::new);
   public static final EnumProperty<NoteBlockInstrument> INSTRUMENT = BlockStateProperties.NOTEBLOCK_INSTRUMENT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final IntegerProperty NOTE = BlockStateProperties.NOTE;
   public static final int NOTE_VOLUME = 3;

   @Override
   public MapCodec<NoteBlock> codec() {
      return CODEC;
   }

   public NoteBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition.any().setValue(INSTRUMENT, NoteBlockInstrument.HARP).setValue(NOTE, Integer.valueOf(0)).setValue(POWERED, Boolean.valueOf(false))
      );
   }

   private BlockState setInstrument(LevelAccessor var1, BlockPos var2, BlockState var3) {
      NoteBlockInstrument var4 = var1.getBlockState(var2.above()).instrument();
      if (var4.worksAboveNoteBlock()) {
         return var3.setValue(INSTRUMENT, var4);
      } else {
         NoteBlockInstrument var5 = var1.getBlockState(var2.below()).instrument();
         NoteBlockInstrument var6 = var5.worksAboveNoteBlock() ? NoteBlockInstrument.HARP : var5;
         return var3.setValue(INSTRUMENT, var6);
      }
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.setInstrument(var1.getLevel(), var1.getClickedPos(), this.defaultBlockState());
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      boolean var7 = var2.getAxis() == Direction.Axis.Y;
      return var7 ? this.setInstrument(var4, var5, var1) : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      boolean var7 = var2.hasNeighborSignal(var3);
      if (var7 != var1.getValue(POWERED)) {
         if (var7) {
            this.playNote(null, var1, var2, var3);
         }

         var2.setBlock(var3, var1.setValue(POWERED, Boolean.valueOf(var7)), 3);
      }
   }

   private void playNote(@Nullable Entity var1, BlockState var2, Level var3, BlockPos var4) {
      if (var2.getValue(INSTRUMENT).worksAboveNoteBlock() || var3.getBlockState(var4.above()).isAir()) {
         var3.blockEvent(var4, this, 0, 0);
         var3.gameEvent(var1, GameEvent.NOTE_BLOCK_PLAY, var4);
      }
   }

   @Override
   protected ItemInteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      return var1.is(ItemTags.NOTE_BLOCK_TOP_INSTRUMENTS) && var7.getDirection() == Direction.UP
         ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
         : super.useItemOn(var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         var1 = var1.cycle(NOTE);
         var2.setBlock(var3, var1, 3);
         this.playNote(var4, var1, var2, var3);
         var4.awardStat(Stats.TUNE_NOTEBLOCK);
         return InteractionResult.CONSUME;
      }
   }

   @Override
   protected void attack(BlockState var1, Level var2, BlockPos var3, Player var4) {
      if (!var2.isClientSide) {
         this.playNote(var4, var1, var2, var3);
         var4.awardStat(Stats.PLAY_NOTEBLOCK);
      }
   }

   public static float getPitchFromNote(int var0) {
      return (float)Math.pow(2.0, (double)(var0 - 12) / 12.0);
   }

   @Override
   protected boolean triggerEvent(BlockState var1, Level var2, BlockPos var3, int var4, int var5) {
      NoteBlockInstrument var7 = var1.getValue(INSTRUMENT);
      float var6;
      if (var7.isTunable()) {
         int var8 = var1.getValue(NOTE);
         var6 = getPitchFromNote(var8);
         var2.addParticle(ParticleTypes.NOTE, (double)var3.getX() + 0.5, (double)var3.getY() + 1.2, (double)var3.getZ() + 0.5, (double)var8 / 24.0, 0.0, 0.0);
      } else {
         var6 = 1.0F;
      }

      Holder var10;
      if (var7.hasCustomSound()) {
         ResourceLocation var9 = this.getCustomSoundId(var2, var3);
         if (var9 == null) {
            return false;
         }

         var10 = Holder.direct(SoundEvent.createVariableRangeEvent(var9));
      } else {
         var10 = var7.getSoundEvent();
      }

      var2.playSeededSound(
         null, (double)var3.getX() + 0.5, (double)var3.getY() + 0.5, (double)var3.getZ() + 0.5, var10, SoundSource.RECORDS, 3.0F, var6, var2.random.nextLong()
      );
      return true;
   }

   @Nullable
   private ResourceLocation getCustomSoundId(Level var1, BlockPos var2) {
      return var1.getBlockEntity(var2.above()) instanceof SkullBlockEntity var3 ? var3.getNoteBlockSound() : null;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(INSTRUMENT, POWERED, NOTE);
   }
}
