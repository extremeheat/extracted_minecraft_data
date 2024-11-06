package net.minecraft.world.inventory;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class BeaconMenu extends AbstractContainerMenu {
   private static final int PAYMENT_SLOT = 0;
   private static final int SLOT_COUNT = 1;
   private static final int DATA_COUNT = 3;
   private static final int INV_SLOT_START = 1;
   private static final int INV_SLOT_END = 28;
   private static final int USE_ROW_SLOT_START = 28;
   private static final int USE_ROW_SLOT_END = 37;
   private static final int NO_EFFECT = 0;
   private final Container beacon;
   private final PaymentSlot paymentSlot;
   private final ContainerLevelAccess access;
   private final ContainerData beaconData;

   public BeaconMenu(int var1, Container var2) {
      this(var1, var2, new SimpleContainerData(3), ContainerLevelAccess.NULL);
   }

   public BeaconMenu(int var1, Container var2, ContainerData var3, ContainerLevelAccess var4) {
      super(MenuType.BEACON, var1);
      this.beacon = new SimpleContainer(this, 1) {
         public boolean canPlaceItem(int var1, ItemStack var2) {
            return var2.is(ItemTags.BEACON_PAYMENT_ITEMS);
         }

         public int getMaxStackSize() {
            return 1;
         }
      };
      checkContainerDataCount(var3, 3);
      this.beaconData = var3;
      this.access = var4;
      this.paymentSlot = new PaymentSlot(this.beacon, 0, 136, 110);
      this.addSlot(this.paymentSlot);
      this.addDataSlots(var3);
      this.addStandardInventorySlots(var2, 36, 137);
   }

   public void removed(Player var1) {
      super.removed(var1);
      if (!var1.level().isClientSide) {
         ItemStack var2 = this.paymentSlot.remove(this.paymentSlot.getMaxStackSize());
         if (!var2.isEmpty()) {
            var1.drop(var2, false);
         }

      }
   }

   public boolean stillValid(Player var1) {
      return stillValid(this.access, var1, Blocks.BEACON);
   }

   public void setData(int var1, int var2) {
      super.setData(var1, var2);
      this.broadcastChanges();
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 == 0) {
            if (!this.moveItemStackTo(var5, 1, 37, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
         } else if (!this.paymentSlot.hasItem() && this.paymentSlot.mayPlace(var5) && var5.getCount() == 1) {
            if (!this.moveItemStackTo(var5, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var2 >= 1 && var2 < 28) {
            if (!this.moveItemStackTo(var5, 28, 37, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var2 >= 28 && var2 < 37) {
            if (!this.moveItemStackTo(var5, 1, 28, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(var5, 1, 37, false)) {
            return ItemStack.EMPTY;
         }

         if (var5.isEmpty()) {
            var4.setByPlayer(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }

         if (var5.getCount() == var3.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(var1, var5);
      }

      return var3;
   }

   public int getLevels() {
      return this.beaconData.get(0);
   }

   public static int encodeEffect(@Nullable Holder<MobEffect> var0) {
      return var0 == null ? 0 : BuiltInRegistries.MOB_EFFECT.asHolderIdMap().getId(var0) + 1;
   }

   @Nullable
   public static Holder<MobEffect> decodeEffect(int var0) {
      return var0 == 0 ? null : (Holder)BuiltInRegistries.MOB_EFFECT.asHolderIdMap().byId(var0 - 1);
   }

   @Nullable
   public Holder<MobEffect> getPrimaryEffect() {
      return decodeEffect(this.beaconData.get(1));
   }

   @Nullable
   public Holder<MobEffect> getSecondaryEffect() {
      return decodeEffect(this.beaconData.get(2));
   }

   public void updateEffects(Optional<Holder<MobEffect>> var1, Optional<Holder<MobEffect>> var2) {
      if (this.paymentSlot.hasItem()) {
         this.beaconData.set(1, encodeEffect((Holder)var1.orElse((Object)null)));
         this.beaconData.set(2, encodeEffect((Holder)var2.orElse((Object)null)));
         this.paymentSlot.remove(1);
         this.access.execute(Level::blockEntityChanged);
      }

   }

   public boolean hasPayment() {
      return !this.beacon.getItem(0).isEmpty();
   }

   private static class PaymentSlot extends Slot {
      public PaymentSlot(Container var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      public boolean mayPlace(ItemStack var1) {
         return var1.is(ItemTags.BEACON_PAYMENT_ITEMS);
      }

      public int getMaxStackSize() {
         return 1;
      }
   }
}
