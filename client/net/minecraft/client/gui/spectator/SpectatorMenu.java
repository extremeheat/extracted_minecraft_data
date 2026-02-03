package net.minecraft.client.gui.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class SpectatorMenu {
   private static final Identifier CLOSE_SPRITE = Identifier.withDefaultNamespace("spectator/close");
   private static final Identifier SCROLL_LEFT_SPRITE = Identifier.withDefaultNamespace("spectator/scroll_left");
   private static final Identifier SCROLL_RIGHT_SPRITE = Identifier.withDefaultNamespace("spectator/scroll_right");
   private static final SpectatorMenuItem CLOSE_ITEM = new CloseSpectatorItem();
   private static final SpectatorMenuItem SCROLL_LEFT = new ScrollMenuItem(-1, true);
   private static final SpectatorMenuItem SCROLL_RIGHT_ENABLED = new ScrollMenuItem(1, true);
   private static final SpectatorMenuItem SCROLL_RIGHT_DISABLED = new ScrollMenuItem(1, false);
   private static final int MAX_PER_PAGE = 8;
   private static final Component CLOSE_MENU_TEXT = Component.translatable("spectatorMenu.close");
   private static final Component PREVIOUS_PAGE_TEXT = Component.translatable("spectatorMenu.previous_page");
   private static final Component NEXT_PAGE_TEXT = Component.translatable("spectatorMenu.next_page");
   public static final SpectatorMenuItem EMPTY_SLOT = new SpectatorMenuItem() {
      public void selectItem(final SpectatorMenu menu) {
      }

      public Component getName() {
         return CommonComponents.EMPTY;
      }

      public void renderIcon(final GuiGraphics graphics, final float brightness, final float alpha) {
      }

      public boolean isEnabled() {
         return false;
      }
   };
   private final SpectatorMenuListener listener;
   private SpectatorMenuCategory category = new RootSpectatorMenuCategory();
   private int selectedSlot = -1;
   private int page;

   public SpectatorMenu(final SpectatorMenuListener listener) {
      super();
      this.listener = listener;
   }

   public SpectatorMenuItem getItem(final int slot) {
      int index = slot + this.page * 6;
      if (this.page > 0 && slot == 0) {
         return SCROLL_LEFT;
      } else if (slot == 7) {
         return index < this.category.getItems().size() ? SCROLL_RIGHT_ENABLED : SCROLL_RIGHT_DISABLED;
      } else if (slot == 8) {
         return CLOSE_ITEM;
      } else {
         return index >= 0 && index < this.category.getItems().size() ? (SpectatorMenuItem)MoreObjects.firstNonNull((SpectatorMenuItem)this.category.getItems().get(index), EMPTY_SLOT) : EMPTY_SLOT;
      }
   }

   public List<SpectatorMenuItem> getItems() {
      List<SpectatorMenuItem> items = Lists.newArrayList();

      for(int i = 0; i <= 8; ++i) {
         items.add(this.getItem(i));
      }

      return items;
   }

   public SpectatorMenuItem getSelectedItem() {
      return this.getItem(this.selectedSlot);
   }

   public SpectatorMenuCategory getSelectedCategory() {
      return this.category;
   }

   public void selectSlot(final int slot) {
      SpectatorMenuItem item = this.getItem(slot);
      if (item != EMPTY_SLOT) {
         if (this.selectedSlot == slot && item.isEnabled()) {
            item.selectItem(this);
         } else {
            this.selectedSlot = slot;
         }
      }

   }

   public void exit() {
      this.listener.onSpectatorMenuClosed(this);
   }

   public int getSelectedSlot() {
      return this.selectedSlot;
   }

   public void selectCategory(final SpectatorMenuCategory category) {
      this.category = category;
      this.selectedSlot = -1;
      this.page = 0;
   }

   public SpectatorPage getCurrentPage() {
      return new SpectatorPage(this.getItems(), this.selectedSlot);
   }

   private static class CloseSpectatorItem implements SpectatorMenuItem {
      private CloseSpectatorItem() {
         super();
      }

      public void selectItem(final SpectatorMenu menu) {
         menu.exit();
      }

      public Component getName() {
         return SpectatorMenu.CLOSE_MENU_TEXT;
      }

      public void renderIcon(final GuiGraphics graphics, final float brightness, final float alpha) {
         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)SpectatorMenu.CLOSE_SPRITE, 0, 0, 16, 16, ARGB.colorFromFloat(alpha, brightness, brightness, brightness));
      }

      public boolean isEnabled() {
         return true;
      }
   }

   private static class ScrollMenuItem implements SpectatorMenuItem {
      private final int direction;
      private final boolean enabled;

      public ScrollMenuItem(final int direction, final boolean enabled) {
         super();
         this.direction = direction;
         this.enabled = enabled;
      }

      public void selectItem(final SpectatorMenu menu) {
         menu.page += this.direction;
      }

      public Component getName() {
         return this.direction < 0 ? SpectatorMenu.PREVIOUS_PAGE_TEXT : SpectatorMenu.NEXT_PAGE_TEXT;
      }

      public void renderIcon(final GuiGraphics graphics, final float brightness, final float alpha) {
         int color = ARGB.colorFromFloat(alpha, brightness, brightness, brightness);
         if (this.direction < 0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)SpectatorMenu.SCROLL_LEFT_SPRITE, 0, 0, 16, 16, color);
         } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)SpectatorMenu.SCROLL_RIGHT_SPRITE, 0, 0, 16, 16, color);
         }

      }

      public boolean isEnabled() {
         return this.enabled;
      }
   }
}
