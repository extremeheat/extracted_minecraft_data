package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ShelfBlockEntity extends BlockEntity implements ItemOwner, ListBackedContainer {
   public static final int MAX_ITEMS = 3;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String ALIGN_ITEMS_TO_BOTTOM_TAG = "align_items_to_bottom";
   private final NonNullList<ItemStack> items;
   private boolean alignItemsToBottom;

   public ShelfBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SHELF, var1, var2);
      this.items = NonNullList.<ItemStack>withSize(3, ItemStack.EMPTY);
   }

   protected void loadAdditional(ValueInput var1) {
      super.loadAdditional(var1);
      this.items.clear();
      ContainerHelper.loadAllItems(var1, this.items);
      this.alignItemsToBottom = var1.getBooleanOr("align_items_to_bottom", false);
   }

   protected void saveAdditional(ValueOutput var1) {
      super.saveAdditional(var1);
      ContainerHelper.saveAllItems(var1, this.items, true);
      var1.putBoolean("align_items_to_bottom", this.alignItemsToBottom);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      try (ProblemReporter.ScopedCollector var2 = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
         TagValueOutput var3 = TagValueOutput.createWithContext(var2, var1);
         ContainerHelper.saveAllItems(var3, this.items, true);
         var3.putBoolean("align_items_to_bottom", this.alignItemsToBottom);
         return var3.buildResult();
      }
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   public boolean stillValid(Player var1) {
      return Container.stillValidBlockEntity(this, var1);
   }

   public ItemStack swapItemNoUpdate(int var1, ItemStack var2) {
      ItemStack var3 = this.removeItemNoUpdate(var1);
      this.setItemNoUpdate(var1, var2);
      return var3;
   }

   public void setChanged(Holder.Reference<GameEvent> var1) {
      super.setChanged();
      if (this.level != null) {
         this.level.gameEvent(var1, this.worldPosition, GameEvent.Context.of(this.getBlockState()));
         this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }

   }

   public void setChanged() {
      this.setChanged(GameEvent.BLOCK_ACTIVATE);
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      super.applyImplicitComponents(var1);
      ((ItemContainerContents)var1.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)).copyInto(this.items);
   }

   protected void collectImplicitComponents(DataComponentMap.Builder var1) {
      super.collectImplicitComponents(var1);
      var1.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
   }

   public void removeComponentsFromTag(ValueOutput var1) {
      var1.discard("Items");
   }

   public Level level() {
      return this.level;
   }

   public Vec3 position() {
      return this.getBlockPos().getCenter();
   }

   public float getVisualRotationYInDegrees() {
      return ((Direction)this.getBlockState().getValue(ShelfBlock.FACING)).getOpposite().toYRot();
   }

   public boolean getAlignItemsToBottom() {
      return this.alignItemsToBottom;
   }

   // $FF: synthetic method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}
