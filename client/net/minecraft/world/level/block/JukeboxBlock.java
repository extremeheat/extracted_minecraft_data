package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class JukeboxBlock extends BaseEntityBlock {
   public static final BooleanProperty HAS_RECORD;

   protected JukeboxBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HAS_RECORD, false));
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
      super.setPlacedBy(var1, var2, var3, var4, var5);
      CompoundTag var6 = var5.getOrCreateTag();
      if (var6.contains("BlockEntityTag")) {
         CompoundTag var7 = var6.getCompound("BlockEntityTag");
         if (var7.contains("RecordItem")) {
            var1.setBlock(var2, (BlockState)var3.setValue(HAS_RECORD, true), 2);
         }
      }

   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if ((Boolean)var1.getValue(HAS_RECORD)) {
         this.dropRecording(var2, var3);
         var1 = (BlockState)var1.setValue(HAS_RECORD, false);
         var2.setBlock(var3, var1, 2);
         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public void setRecord(LevelAccessor var1, BlockPos var2, BlockState var3, ItemStack var4) {
      BlockEntity var5 = var1.getBlockEntity(var2);
      if (var5 instanceof JukeboxBlockEntity) {
         ((JukeboxBlockEntity)var5).setRecord(var4.copy());
         var1.setBlock(var2, (BlockState)var3.setValue(HAS_RECORD, true), 2);
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
               double var7 = (double)(var1.random.nextFloat() * 0.7F) + 0.15000000596046448D;
               double var9 = (double)(var1.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
               double var11 = (double)(var1.random.nextFloat() * 0.7F) + 0.15000000596046448D;
               ItemStack var13 = var5.copy();
               ItemEntity var14 = new ItemEntity(var1, (double)var2.getX() + var7, (double)var2.getY() + var9, (double)var2.getZ() + var11, var13);
               var14.setDefaultPickUpDelay();
               var1.addFreshEntity(var14);
            }
         }
      }
   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         this.dropRecording(var2, var3);
         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new JukeboxBlockEntity(var1, var2);
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

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

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HAS_RECORD);
   }

   static {
      HAS_RECORD = BlockStateProperties.HAS_RECORD;
   }
}
