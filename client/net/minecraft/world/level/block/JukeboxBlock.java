package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
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
   public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;

   @Override
   public MapCodec<JukeboxBlock> codec() {
      return CODEC;
   }

   protected JukeboxBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(HAS_RECORD, Boolean.valueOf(false)));
   }

   @Override
   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
      super.setPlacedBy(var1, var2, var3, var4, var5);
      CustomData var6 = var5.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
      if (var6.contains("RecordItem")) {
         var1.setBlock(var2, var3.setValue(HAS_RECORD, Boolean.valueOf(true)), 2);
      }
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var1.getValue(HAS_RECORD)) {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof JukeboxBlockEntity var6) {
            var6.popOutRecord();
            return InteractionResult.sidedSuccess(var2.isClientSide);
         }
      }

      return InteractionResult.PASS;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof JukeboxBlockEntity var6) {
            var6.popOutRecord();
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new JukeboxBlockEntity(var1, var2);
   }

   @Override
   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   @Override
   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      BlockEntity var6 = var2.getBlockEntity(var3);
      if (var6 instanceof JukeboxBlockEntity var5 && var5.isRecordPlaying()) {
         return 15;
      }

      return 0;
   }

   @Override
   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      BlockEntity var6 = var2.getBlockEntity(var3);
      if (var6 instanceof JukeboxBlockEntity var4) {
         Item var7 = var4.getTheItem().getItem();
         if (var7 instanceof RecordItem var5) {
            return var5.getAnalogOutput();
         }
      }

      return 0;
   }

   @Override
   protected RenderShape getRenderShape(BlockState var1) {
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
