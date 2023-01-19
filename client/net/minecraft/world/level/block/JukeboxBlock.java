package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class JukeboxBlock extends BaseEntityBlock {
   public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;

   protected JukeboxBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(HAS_RECORD, Boolean.valueOf(false)));
   }

   @Override
   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
      super.setPlacedBy(var1, var2, var3, var4, var5);
      CompoundTag var6 = BlockItem.getBlockEntityData(var5);
      if (var6 != null && var6.contains("RecordItem")) {
         var1.setBlock(var2, var3.setValue(HAS_RECORD, Boolean.valueOf(true)), 2);
      }
   }

   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var1.getValue(HAS_RECORD)) {
         this.dropRecording(var2, var3);
         var1 = var1.setValue(HAS_RECORD, Boolean.valueOf(false));
         var2.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, var3, GameEvent.Context.of(var1));
         var2.setBlock(var3, var1, 2);
         var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var4, var1));
         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void setRecord(@Nullable Entity var1, LevelAccessor var2, BlockPos var3, BlockState var4, ItemStack var5) {
      BlockEntity var6 = var2.getBlockEntity(var3);
      if (var6 instanceof JukeboxBlockEntity var7) {
         var7.setRecord(var5.copy());
         var7.playRecord();
         var2.setBlock(var3, var4.setValue(HAS_RECORD, Boolean.valueOf(true)), 2);
         var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var1, var4));
      }
   }

   private void dropRecording(Level var1, BlockPos var2) {
      if (!var1.isClientSide) {
         BlockEntity var3 = var1.getBlockEntity(var2);
         if (var3 instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity var4 = (JukeboxBlockEntity)var3;
            ItemStack var5 = var4.getRecord();
            if (!var5.isEmpty()) {
               var1.levelEvent(1010, var2, 0);
               var4.clearContent();
               float var6 = 0.7F;
               double var7 = (double)(var1.random.nextFloat() * 0.7F) + 0.15000000596046448;
               double var9 = (double)(var1.random.nextFloat() * 0.7F) + 0.06000000238418579 + 0.6;
               double var11 = (double)(var1.random.nextFloat() * 0.7F) + 0.15000000596046448;
               ItemStack var13 = var5.copy();
               ItemEntity var14 = new ItemEntity(var1, (double)var2.getX() + var7, (double)var2.getY() + var9, (double)var2.getZ() + var11, var13);
               var14.setDefaultPickUpDelay();
               var1.addFreshEntity(var14);
            }
         }
      }
   }

   @Override
   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         this.dropRecording(var2, var3);
         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new JukeboxBlockEntity(var1, var2);
   }

   @Override
   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   @Override
   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      BlockEntity var4 = var2.getBlockEntity(var3);
      if (var4 instanceof JukeboxBlockEntity) {
         Item var5 = ((JukeboxBlockEntity)var4).getRecord().getItem();
         if (var5 instanceof RecordItem) {
            return ((RecordItem)var5).getAnalogOutput();
         }
      }

      return 0;
   }

   @Override
   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HAS_RECORD);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var2.getValue(HAS_RECORD) ? createTickerHelper(var3, BlockEntityType.JUKEBOX, JukeboxBlockEntity::playRecordTick) : null;
   }
}
