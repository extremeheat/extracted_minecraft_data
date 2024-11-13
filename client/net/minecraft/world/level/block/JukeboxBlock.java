package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class JukeboxBlock extends BaseEntityBlock {
   public static final MapCodec<JukeboxBlock> CODEC = simpleCodec(JukeboxBlock::new);
   public static final BooleanProperty HAS_RECORD;

   public MapCodec<JukeboxBlock> codec() {
      return CODEC;
   }

   protected JukeboxBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HAS_RECORD, false));
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
      super.setPlacedBy(var1, var2, var3, var4, var5);
      CustomData var6 = (CustomData)var5.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
      if (var6.contains("RecordItem")) {
         var1.setBlock(var2, (BlockState)var3.setValue(HAS_RECORD, true), 2);
      }

   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if ((Boolean)var1.getValue(HAS_RECORD)) {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity var6 = (JukeboxBlockEntity)var7;
            var6.popOutTheItem();
            return InteractionResult.SUCCESS;
         }
      }

      return InteractionResult.PASS;
   }

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if ((Boolean)var2.getValue(HAS_RECORD)) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      } else {
         ItemStack var8 = var5.getItemInHand(var6);
         InteractionResult var9 = JukeboxPlayable.tryInsertIntoJukebox(var3, var4, var8, var5);
         return (InteractionResult)(!var9.consumesAction() ? InteractionResult.TRY_WITH_EMPTY_HAND : var9);
      }
   }

   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof JukeboxBlockEntity) {
            JukeboxBlockEntity var6 = (JukeboxBlockEntity)var7;
            var6.popOutTheItem();
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new JukeboxBlockEntity(var1, var2);
   }

   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      BlockEntity var6 = var2.getBlockEntity(var3);
      if (var6 instanceof JukeboxBlockEntity var5) {
         if (var5.getSongPlayer().isPlaying()) {
            return 15;
         }
      }

      return 0;
   }

   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (var5 instanceof JukeboxBlockEntity var4) {
         return var4.getComparatorOutput();
      } else {
         return 0;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HAS_RECORD);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return (Boolean)var2.getValue(HAS_RECORD) ? createTickerHelper(var3, BlockEntityType.JUKEBOX, JukeboxBlockEntity::tick) : null;
   }

   static {
      HAS_RECORD = BlockStateProperties.HAS_RECORD;
   }
}
