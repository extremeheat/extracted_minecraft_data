package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class ChestBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
   private static final int EVENT_SET_OPEN_COUNT = 1;
   private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
   private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
      @Override
      protected void onOpen(Level var1, BlockPos var2, BlockState var3) {
         ChestBlockEntity.playSound(var1, var2, var3, SoundEvents.CHEST_OPEN);
      }

      @Override
      protected void onClose(Level var1, BlockPos var2, BlockState var3) {
         ChestBlockEntity.playSound(var1, var2, var3, SoundEvents.CHEST_CLOSE);
      }

      @Override
      protected void openerCountChanged(Level var1, BlockPos var2, BlockState var3, int var4, int var5) {
         ChestBlockEntity.this.signalOpenCount(var1, var2, var3, var4, var5);
      }

      @Override
      protected boolean isOwnContainer(Player var1) {
         if (!(var1.containerMenu instanceof ChestMenu)) {
            return false;
         } else {
            Container var2 = ((ChestMenu)var1.containerMenu).getContainer();
            return var2 == ChestBlockEntity.this || var2 instanceof CompoundContainer && ((CompoundContainer)var2).contains(ChestBlockEntity.this);
         }
      }
   };
   private final ChestLidController chestLidController = new ChestLidController();

   protected ChestBlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3) {
      super(var1, var2, var3);
   }

   public ChestBlockEntity(BlockPos var1, BlockState var2) {
      this(BlockEntityType.CHEST, var1, var2);
   }

   @Override
   public int getContainerSize() {
      return 27;
   }

   @Override
   protected Component getDefaultName() {
      return Component.translatable("container.chest");
   }

   @Override
   public void load(CompoundTag var1, HolderLookup.Provider var2) {
      super.load(var1, var2);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(var1)) {
         ContainerHelper.loadAllItems(var1, this.items, var2);
      }
   }

   @Override
   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      if (!this.trySaveLootTable(var1)) {
         ContainerHelper.saveAllItems(var1, this.items, var2);
      }
   }

   public static void lidAnimateTick(Level var0, BlockPos var1, BlockState var2, ChestBlockEntity var3) {
      var3.chestLidController.tickLid();
   }

   static void playSound(Level var0, BlockPos var1, BlockState var2, SoundEvent var3) {
      ChestType var4 = var2.getValue(ChestBlock.TYPE);
      if (var4 != ChestType.LEFT) {
         double var5 = (double)var1.getX() + 0.5;
         double var7 = (double)var1.getY() + 0.5;
         double var9 = (double)var1.getZ() + 0.5;
         if (var4 == ChestType.RIGHT) {
            Direction var11 = ChestBlock.getConnectedDirection(var2);
            var5 += (double)var11.getStepX() * 0.5;
            var9 += (double)var11.getStepZ() * 0.5;
         }

         var0.playSound(null, var5, var7, var9, var3, SoundSource.BLOCKS, 0.5F, var0.random.nextFloat() * 0.1F + 0.9F);
      }
   }

   @Override
   public boolean triggerEvent(int var1, int var2) {
      if (var1 == 1) {
         this.chestLidController.shouldBeOpen(var2 > 0);
         return true;
      } else {
         return super.triggerEvent(var1, var2);
      }
   }

   @Override
   public void startOpen(Player var1) {
      if (!this.remove && !var1.isSpectator()) {
         this.openersCounter.incrementOpeners(var1, this.getLevel(), this.getBlockPos(), this.getBlockState());
      }
   }

   @Override
   public void stopOpen(Player var1) {
      if (!this.remove && !var1.isSpectator()) {
         this.openersCounter.decrementOpeners(var1, this.getLevel(), this.getBlockPos(), this.getBlockState());
      }
   }

   @Override
   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   @Override
   protected void setItems(NonNullList<ItemStack> var1) {
      this.items = var1;
   }

   @Override
   public float getOpenNess(float var1) {
      return this.chestLidController.getOpenness(var1);
   }

   public static int getOpenCount(BlockGetter var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      if (var2.hasBlockEntity()) {
         BlockEntity var3 = var0.getBlockEntity(var1);
         if (var3 instanceof ChestBlockEntity) {
            return ((ChestBlockEntity)var3).openersCounter.getOpenerCount();
         }
      }

      return 0;
   }

   public static void swapContents(ChestBlockEntity var0, ChestBlockEntity var1) {
      NonNullList var2 = var0.getItems();
      var0.setItems(var1.getItems());
      var1.setItems(var2);
   }

   @Override
   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return ChestMenu.threeRows(var1, var2, this);
   }

   public void recheckOpen() {
      if (!this.remove) {
         this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
      }
   }

   protected void signalOpenCount(Level var1, BlockPos var2, BlockState var3, int var4, int var5) {
      Block var6 = var3.getBlock();
      var1.blockEvent(var2, var6, 1, var5);
   }
}
