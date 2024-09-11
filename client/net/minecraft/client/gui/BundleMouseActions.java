package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.ScrollWheelHandler;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ServerboundSelectBundleItemPacket;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Vector2i;

public class BundleMouseActions implements ItemSlotMouseAction {
   private final Minecraft minecraft;
   private final ScrollWheelHandler scrollWheelHandler;

   public BundleMouseActions(Minecraft var1) {
      super();
      this.minecraft = var1;
      this.scrollWheelHandler = new ScrollWheelHandler();
   }

   @Override
   public boolean matches(Slot var1) {
      return var1.getItem().is(Items.BUNDLE);
   }

   @Override
   public boolean onMouseScrolled(double var1, double var3, int var5, ItemStack var6) {
      int var7 = BundleItem.getNumberOfItemsToShow(var6);
      if (var7 == 0) {
         return false;
      } else {
         Vector2i var8 = this.scrollWheelHandler.onMouseScroll(var1, var3);
         int var9 = var8.y == 0 ? -var8.x : var8.y;
         if (var9 != 0) {
            int var10 = BundleItem.getSelectedItem(var6);
            var10 = ScrollWheelHandler.getNextScrollWheelSelection((double)var9, var10, var7);
            this.setSelectedBundleItem(var6, var5, var10);
         }

         return true;
      }
   }

   @Override
   public void onStopHovering(Slot var1) {
      this.unselectedBundleItem(var1.getItem(), var1.index);
   }

   @Override
   public void onSlotClicked(Slot var1, ClickType var2) {
      if (var2 == ClickType.QUICK_MOVE) {
         this.unselectedBundleItem(var1.getItem(), var1.index);
      }
   }

   private void setSelectedBundleItem(ItemStack var1, int var2, int var3) {
      if (this.minecraft.getConnection() != null && var3 < BundleItem.getNumberOfItemsToShow(var1)) {
         ClientPacketListener var4 = this.minecraft.getConnection();
         BundleItem.toggleSelectedItem(var1, var3);
         var4.send(new ServerboundSelectBundleItemPacket(var2, var3));
      }
   }

   public void unselectedBundleItem(ItemStack var1, int var2) {
      this.setSelectedBundleItem(var1, var2, -1);
   }
}
