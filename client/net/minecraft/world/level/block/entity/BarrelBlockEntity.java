package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BarrelBlockEntity extends RandomizableContainerBlockEntity {
   private static final Component DEFAULT_NAME = Component.translatable("container.barrel");
   private NonNullList<ItemStack> items;
   private final ContainerOpenersCounter openersCounter;

   public BarrelBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      super(BlockEntityTypes.BARREL, worldPosition, blockState);
      this.items = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
      this.openersCounter = new ContainerOpenersCounter() {
         {
            Objects.requireNonNull(BarrelBlockEntity.this);
         }

         protected void onOpen(final Level level, final BlockPos pos, final BlockState state) {
            BarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_OPEN);
            BarrelBlockEntity.this.updateBlockState(state, true);
         }

         protected void onClose(final Level level, final BlockPos pos, final BlockState state) {
            BarrelBlockEntity.this.playSound(state, SoundEvents.BARREL_CLOSE);
            BarrelBlockEntity.this.updateBlockState(state, false);
         }

         protected void openerCountChanged(final Level level, final BlockPos pos, final BlockState blockState, final int previous, final int current) {
         }

         public boolean isOwnContainer(final Player player) {
            if (player.containerMenu instanceof ChestMenu) {
               Container container = ((ChestMenu)player.containerMenu).getContainer();
               return container == BarrelBlockEntity.this;
            } else {
               return false;
            }
         }
      };
   }

   protected void saveAdditional(final ValueOutput output) {
      super.saveAdditional(output);
      if (!this.trySaveLootTable(output)) {
         ContainerHelper.saveAllItems(output, this.items);
      }

   }

   protected void loadAdditional(final ValueInput input) {
      super.loadAdditional(input);
      this.items = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(input)) {
         ContainerHelper.loadAllItems(input, this.items);
      }

   }

   public int getContainerSize() {
      return 27;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(final NonNullList<ItemStack> items) {
      this.items = items;
   }

   protected Component getDefaultName() {
      return DEFAULT_NAME;
   }

   protected AbstractContainerMenu createMenu(final int containerId, final Inventory inventory) {
      return ChestMenu.threeRows(containerId, inventory, this);
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

   public void recheckOpen() {
      if (!this.remove) {
         this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
      }

   }

   private void updateBlockState(final BlockState state, final boolean isOpen) {
      this.level.setBlockAndUpdate(this.getBlockPos(), (BlockState)state.setValue(BarrelBlock.OPEN, isOpen));
   }

   private void playSound(final BlockState state, final SoundEvent event) {
      Vec3i direction = ((Direction)state.getValue(BarrelBlock.FACING)).getUnitVec3i();
      double x = (double)this.worldPosition.getX() + 0.5 + (double)direction.getX() / 2.0;
      double y = (double)this.worldPosition.getY() + 0.5 + (double)direction.getY() / 2.0;
      double z = (double)this.worldPosition.getZ() + 0.5 + (double)direction.getZ() / 2.0;
      this.level.playSound((Entity)null, x, y, z, event, SoundSource.BLOCKS, 0.5F, this.level.getRandom().nextFloat() * 0.1F + 0.9F);
   }
}
