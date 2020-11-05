package net.minecraft.client.gui.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class SpectatorMenu {
   private static final SpectatorMenuItem CLOSE_ITEM = new SpectatorMenu.CloseSpectatorItem();
   private static final SpectatorMenuItem SCROLL_LEFT = new SpectatorMenu.ScrollMenuItem(-1, true);
   private static final SpectatorMenuItem SCROLL_RIGHT_ENABLED = new SpectatorMenu.ScrollMenuItem(1, true);
   private static final SpectatorMenuItem SCROLL_RIGHT_DISABLED = new SpectatorMenu.ScrollMenuItem(1, false);
   private static final Component CLOSE_MENU_TEXT = new TranslatableComponent("spectatorMenu.close");
   private static final Component PREVIOUS_PAGE_TEXT = new TranslatableComponent("spectatorMenu.previous_page");
   private static final Component NEXT_PAGE_TEXT = new TranslatableComponent("spectatorMenu.next_page");
   public static final SpectatorMenuItem EMPTY_SLOT = new SpectatorMenuItem() {
      public void selectItem(SpectatorMenu var1) {
      }

      public Component getName() {
         return TextComponent.EMPTY;
      }

      public void renderIcon(PoseStack var1, float var2, int var3) {
      }

      public boolean isEnabled() {
         return false;
      }
   };
   private final SpectatorMenuListener listener;
   private SpectatorMenuCategory category = new RootSpectatorMenuCategory();
   private int selectedSlot = -1;
   private int page;

   public SpectatorMenu(SpectatorMenuListener var1) {
      super();
      this.listener = var1;
   }

   public SpectatorMenuItem getItem(int var1) {
      int var2 = var1 + this.page * 6;
      if (this.page > 0 && var1 == 0) {
         return SCROLL_LEFT;
      } else if (var1 == 7) {
         return var2 < this.category.getItems().size() ? SCROLL_RIGHT_ENABLED : SCROLL_RIGHT_DISABLED;
      } else if (var1 == 8) {
         return CLOSE_ITEM;
      } else {
         return var2 >= 0 && var2 < this.category.getItems().size() ? (SpectatorMenuItem)MoreObjects.firstNonNull(this.category.getItems().get(var2), EMPTY_SLOT) : EMPTY_SLOT;
      }
   }

   public List<SpectatorMenuItem> getItems() {
      ArrayList var1 = Lists.newArrayList();

      for(int var2 = 0; var2 <= 8; ++var2) {
         var1.add(this.getItem(var2));
      }

      return var1;
   }

   public SpectatorMenuItem getSelectedItem() {
      return this.getItem(this.selectedSlot);
   }

   public SpectatorMenuCategory getSelectedCategory() {
      return this.category;
   }

   public void selectSlot(int var1) {
      SpectatorMenuItem var2 = this.getItem(var1);
      if (var2 != EMPTY_SLOT) {
         if (this.selectedSlot == var1 && var2.isEnabled()) {
            var2.selectItem(this);
         } else {
            this.selectedSlot = var1;
         }
      }

   }

   public void exit() {
      this.listener.onSpectatorMenuClosed(this);
   }

   public int getSelectedSlot() {
      return this.selectedSlot;
   }

   public void selectCategory(SpectatorMenuCategory var1) {
      this.category = var1;
      this.selectedSlot = -1;
      this.page = 0;
   }

   public SpectatorPage getCurrentPage() {
      return new SpectatorPage(this.category, this.getItems(), this.selectedSlot);
   }

   static class ScrollMenuItem implements SpectatorMenuItem {
      private final int direction;
      private final boolean enabled;

      public ScrollMenuItem(int var1, boolean var2) {
         super();
         this.direction = var1;
         this.enabled = var2;
      }

      public void selectItem(SpectatorMenu var1) {
         var1.page = var1.page + this.direction;
      }

      public Component getName() {
         return this.direction < 0 ? SpectatorMenu.PREVIOUS_PAGE_TEXT : SpectatorMenu.NEXT_PAGE_TEXT;
      }

      public void renderIcon(PoseStack var1, float var2, int var3) {
         Minecraft.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_LOCATION);
         if (this.direction < 0) {
            GuiComponent.blit(var1, 0, 0, 144.0F, 0.0F, 16, 16, 256, 256);
         } else {
            GuiComponent.blit(var1, 0, 0, 160.0F, 0.0F, 16, 16, 256, 256);
         }

      }

      public boolean isEnabled() {
         return this.enabled;
      }
   }

   static class CloseSpectatorItem implements SpectatorMenuItem {
      private CloseSpectatorItem() {
         super();
      }

      public void selectItem(SpectatorMenu var1) {
         var1.exit();
      }

      public Component getName() {
         return SpectatorMenu.CLOSE_MENU_TEXT;
      }

      public void renderIcon(PoseStack var1, float var2, int var3) {
         Minecraft.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_LOCATION);
         GuiComponent.blit(var1, 0, 0, 128.0F, 0.0F, 16, 16, 256, 256);
      }

      public boolean isEnabled() {
         return true;
      }

      // $FF: synthetic method
      CloseSpectatorItem(Object var1) {
         this();
      }
   }
}
