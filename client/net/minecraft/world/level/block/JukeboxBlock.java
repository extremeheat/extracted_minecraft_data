package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.component.TypedEntityData;
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
import org.jspecify.annotations.Nullable;

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
      TypedEntityData var6 = (TypedEntityData)var5.get(DataComponents.BLOCK_ENTITY_DATA);
      if (var6 != null && var6.contains("RecordItem")) {
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

   protected void affectNeighborsAfterRemoval(BlockState var1, ServerLevel var2, BlockPos var3, boolean var4) {
      Containers.updateNeighboursAfterDestroy(var1, var2, var3);
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

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3, Direction var4) {
      BlockEntity var6 = var2.getBlockEntity(var3);
      if (var6 instanceof JukeboxBlockEntity var5) {
         return var5.getComparatorOutput();
      } else {
         return 0;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HAS_RECORD);
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return (Boolean)var2.getValue(HAS_RECORD) ? createTickerHelper(var3, BlockEntityType.JUKEBOX, JukeboxBlockEntity::tick) : null;
   }

   static {
      HAS_RECORD = BlockStateProperties.HAS_RECORD;
   }
}
