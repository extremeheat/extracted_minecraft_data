package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ServerboundSelectBundleItemPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;

public class BundleMouseActions implements ItemSlotMouseAction {
   private final Minecraft minecraft;
   private final ScrollWheelHandler scrollWheelHandler;

   public BundleMouseActions(Minecraft var1) {
      super();
      this.minecraft = var1;
      this.scrollWheelHandler = new ScrollWheelHandler();
   }

   public boolean matches(Slot var1) {
      return var1.getItem().is(ItemTags.BUNDLES);
   }

   public boolean onMouseScrolled(double var1, double var3, int var5, ItemStack var6) {
      int var7 = BundleItem.getNumberOfItemsToShow(var6);
      if (var7 == 0) {
         return false;
      } else {
         Vector2i var8 = this.scrollWheelHandler.onMouseScroll(var1, var3);
         int var9 = var8.y == 0 ? -var8.x : var8.y;
         if (var9 != 0) {
            int var10 = BundleItem.getSelectedItem(var6);
            int var11 = ScrollWheelHandler.getNextScrollWheelSelection((double)var9, var10, var7);
            if (var10 != var11) {
               this.toggleSelectedBundleItem(var6, var5, var11);
            }
         }

         return true;
      }
   }

   public void onStopHovering(Slot var1) {
      this.unselectedBundleItem(var1.getItem(), var1.index);
   }

   public void onSlotClicked(Slot var1, ClickType var2) {
      if (var2 == ClickType.QUICK_MOVE || var2 == ClickType.SWAP || var2 == ClickType.PICKUP) {
         this.unselectedBundleItem(var1.getItem(), var1.index);
      }

   }

   private void toggleSelectedBundleItem(ItemStack var1, int var2, int var3) {
      if (this.minecraft.getConnection() != null && var3 < BundleItem.getNumberOfItemsToShow(var1)) {
         ClientPacketListener var4 = this.minecraft.getConnection();
         BundleItem.toggleSelectedItem(var1, var3);
         var4.send(new ServerboundSelectBundleItemPacket(var2, var3));
      }

   }

   public void unselectedBundleItem(ItemStack var1, int var2) {
      this.toggleSelectedBundleItem(var1, var2, -1);
   }
}
