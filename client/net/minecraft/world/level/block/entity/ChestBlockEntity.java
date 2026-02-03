package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ChestBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
   private static final int EVENT_SET_OPEN_COUNT = 1;
   private static final Component DEFAULT_NAME = Component.translatable("container.chest");
   private NonNullList<ItemStack> items;
   private final ContainerOpenersCounter openersCounter;
   private final ChestLidController chestLidController;

   protected ChestBlockEntity(final BlockEntityType<?> type, final BlockPos worldPosition, final BlockState blockState) {
      super(type, worldPosition, blockState);
      this.items = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
      this.openersCounter = new ContainerOpenersCounter() {
         {
            Objects.requireNonNull(ChestBlockEntity.this);
         }

         protected void onOpen(final Level level, final BlockPos pos, final BlockState blockState) {
            Block var5 = blockState.getBlock();
            if (var5 instanceof ChestBlock chestBlock) {
               ChestBlockEntity.playSound(level, pos, blockState, chestBlock.getOpenChestSound());
            }

         }

         protected void onClose(final Level level, final BlockPos pos, final BlockState blockState) {
            Block var5 = blockState.getBlock();
            if (var5 instanceof ChestBlock chestBlock) {
               ChestBlockEntity.playSound(level, pos, blockState, chestBlock.getCloseChestSound());
            }

         }

         protected void openerCountChanged(final Level level, final BlockPos pos, final BlockState blockState, final int previous, final int current) {
            ChestBlockEntity.this.signalOpenCount(level, pos, blockState, previous, current);
         }

         public boolean isOwnContainer(final Player player) {
            if (!(player.containerMenu instanceof ChestMenu)) {
               return false;
            } else {
               Container container = ((ChestMenu)player.containerMenu).getContainer();
               return container == ChestBlockEntity.this || container instanceof CompoundContainer && ((CompoundContainer)container).contains(ChestBlockEntity.this);
            }
         }
      };
      this.chestLidController = new ChestLidController();
   }

   public ChestBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      this(BlockEntityType.CHEST, worldPosition, blockState);
   }

   public int getContainerSize() {
      return 27;
   }

   protected Component getDefaultName() {
      return DEFAULT_NAME;
   }

   protected void loadAdditional(final ValueInput input) {
      super.loadAdditional(input);
      this.items = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(input)) {
         ContainerHelper.loadAllItems(input, this.items);
      }

   }

   protected void saveAdditional(final ValueOutput output) {
      super.saveAdditional(output);
      if (!this.trySaveLootTable(output)) {
         ContainerHelper.saveAllItems(output, this.items);
      }

   }

   public static void lidAnimateTick(final Level level, final BlockPos pos, final BlockState state, final ChestBlockEntity entity) {
      entity.chestLidController.tickLid();
   }

   private static void playSound(final Level level, final BlockPos worldPosition, final BlockState blockState, final SoundEvent event) {
      ChestType type = (ChestType)blockState.getValue(ChestBlock.TYPE);
      if (type != ChestType.LEFT) {
         double x = (double)worldPosition.getX() + 0.5;
         double y = (double)worldPosition.getY() + 0.5;
         double z = (double)worldPosition.getZ() + 0.5;
         if (type == ChestType.RIGHT) {
            Direction direction = ChestBlock.getConnectedDirection(blockState);
            x += (double)direction.getStepX() * 0.5;
            z += (double)direction.getStepZ() * 0.5;
         }

         level.playSound((Entity)null, x, y, z, event, SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
      }
   }

   public boolean triggerEvent(final int b0, final int b1) {
      if (b0 == 1) {
         this.chestLidController.shouldBeOpen(b1 > 0);
         return true;
      } else {
         return super.triggerEvent(b0, b1);
      }
   }

   public void startOpen(final ContainerUser containerUser) {
      if (!this.remove && !containerUser.getLivingEntity().isSpectator()) {
         this.openersCounter.incrementOpeners(containerUser.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState(), containerUser.getContainerInteractionRange());
      }

   }

   public void stopOpen(final ContainerUser containerUser) {
      if (!this.remove && !containerUser.getLivingEntity().isSpectator()) {
         this.openersCounter.decrementOpeners(containerUser.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState());
      }

   }

   public List<ContainerUser> getEntitiesWithContainerOpen() {
      return this.openersCounter.getEntitiesWithContainerOpen(this.getLevel(), this.getBlockPos());
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(final NonNullList<ItemStack> items) {
      this.items = items;
   }

   public float getOpenNess(final float a) {
      return this.chestLidController.getOpenness(a);
   }

   public static int getOpenCount(final BlockGetter level, final BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      if (state.hasBlockEntity()) {
         BlockEntity blockEntity = level.getBlockEntity(pos);
         if (blockEntity instanceof ChestBlockEntity) {
            return ((ChestBlockEntity)blockEntity).openersCounter.getOpenerCount();
         }
      }

      return 0;
   }

   public static void swapContents(final ChestBlockEntity one, final ChestBlockEntity two) {
      NonNullList<ItemStack> items = one.getItems();
      one.setItems(two.getItems());
      two.setItems(items);
   }

   protected AbstractContainerMenu createMenu(final int containerId, final Inventory inventory) {
      return ChestMenu.threeRows(containerId, inventory, this);
   }

   public void recheckOpen() {
      if (!this.remove) {
         this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
      }

   }

   protected void signalOpenCount(final Level level, final BlockPos pos, final BlockState blockState, final int previous, final int current) {
      Block block = blockState.getBlock();
      level.blockEvent(pos, block, 1, current);
   }
}
