package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class AbstractSelectionList<E extends Entry<E>> extends AbstractContainerWidget {
   protected static final int SCROLLBAR_WIDTH = 6;
   private static final ResourceLocation SCROLLER_SPRITE = new ResourceLocation("widget/scroller");
   private static final ResourceLocation SCROLLER_BACKGROUND_SPRITE = new ResourceLocation("widget/scroller_background");
   private static final ResourceLocation MENU_LIST_BACKGROUND = new ResourceLocation("textures/gui/menu_list_background.png");
   private static final ResourceLocation INWORLD_MENU_LIST_BACKGROUND = new ResourceLocation("textures/gui/inworld_menu_list_background.png");
   protected final Minecraft minecraft;
   protected final int itemHeight;
   private final List<E> children = new TrackedList();
   protected boolean centerListVertically = true;
   private double scrollAmount;
   private boolean renderHeader;
   protected int headerHeight;
   private boolean scrolling;
   @Nullable
   private E selected;
   @Nullable
   private E hovered;

   public AbstractSelectionList(Minecraft var1, int var2, int var3, int var4, int var5) {
      super(0, var4, var2, var3, CommonComponents.EMPTY);
      this.minecraft = var1;
      this.itemHeight = var5;
   }

   protected void setRenderHeader(boolean var1, int var2) {
      this.renderHeader = var1;
      this.headerHeight = var2;
      if (!var1) {
         this.headerHeight = 0;
      }

   }

   public int getRowWidth() {
      return 220;
   }

   @Nullable
   public E getSelected() {
      return this.selected;
   }

   public void setSelected(@Nullable E var1) {
      this.selected = var1;
   }

   public E getFirstElement() {
      return (Entry)this.children.get(0);
   }

   @Nullable
   public E getFocused() {
      return (Entry)super.getFocused();
   }

   public final List<E> children() {
      return this.children;
   }

   protected void clearEntries() {
      this.children.clear();
      this.selected = null;
   }

   protected void replaceEntries(Collection<E> var1) {
      this.clearEntries();
      this.children.addAll(var1);
   }

   protected E getEntry(int var1) {
      return (Entry)this.children().get(var1);
   }

   protected int addEntry(E var1) {
      this.children.add(var1);
      return this.children.size() - 1;
   }

   protected void addEntryToTop(E var1) {
      double var2 = (double)this.getMaxScroll() - this.getScrollAmount();
      this.children.add(0, var1);
      this.setScrollAmount((double)this.getMaxScroll() - var2);
   }

   protected boolean removeEntryFromTop(E var1) {
      double var2 = (double)this.getMaxScroll() - this.getScrollAmount();
      boolean var4 = this.removeEntry(var1);
      this.setScrollAmount((double)this.getMaxScroll() - var2);
      return var4;
   }

   protected int getItemCount() {
      return this.children().size();
   }

   protected boolean isSelectedItem(int var1) {
      return Objects.equals(this.getSelected(), this.children().get(var1));
   }

   @Nullable
   protected final E getEntryAtPosition(double var1, double var3) {
      int var5 = this.getRowWidth() / 2;
      int var6 = this.getX() + this.width / 2;
      int var7 = var6 - var5;
      int var8 = var6 + var5;
      int var9 = Mth.floor(var3 - (double)this.getY()) - this.headerHeight + (int)this.getScrollAmount() - 4;
      int var10 = var9 / this.itemHeight;
      return var1 >= (double)var7 && var1 <= (double)var8 && var10 >= 0 && var9 >= 0 && var10 < this.getItemCount() ? (Entry)this.children().get(var10) : null;
   }

   public void updateSize(int var1, HeaderAndFooterLayout var2) {
      this.updateSizeAndPosition(var1, var2.getContentHeight(), var2.getHeaderHeight());
   }

   public void updateSizeAndPosition(int var1, int var2, int var3) {
      this.setSize(var1, var2);
      this.setPosition(0, var3);
   }

   protected int getMaxPosition() {
      return this.getItemCount() * this.itemHeight + this.headerHeight;
   }

   protected boolean clickedHeader(int var1, int var2) {
      return false;
   }

   protected void renderHeader(GuiGraphics var1, int var2, int var3) {
   }

   protected void renderDecorations(GuiGraphics var1, int var2, int var3) {
   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      this.hovered = this.isMouseOver((double)var2, (double)var3) ? this.getEntryAtPosition((double)var2, (double)var3) : null;
      this.renderListBackground(var1);
      this.enableScissor(var1);
      int var5;
      int var6;
      if (this.renderHeader) {
         var5 = this.getRowLeft();
         var6 = this.getY() + 4 - (int)this.getScrollAmount();
         this.renderHeader(var1, var5, var6);
      }

      this.renderListItems(var1, var2, var3, var4);
      var1.disableScissor();
      this.renderListSeparators(var1);
      if (this.scrollbarVisible()) {
         var5 = this.getScrollbarPosition();
         var6 = (int)((float)(this.height * this.height) / (float)this.getMaxPosition());
         var6 = Mth.clamp(var6, 32, this.height - 8);
         int var7 = (int)this.getScrollAmount() * (this.height - var6) / this.getMaxScroll() + this.getY();
         if (var7 < this.getY()) {
            var7 = this.getY();
         }

         RenderSystem.enableBlend();
         var1.blitSprite(SCROLLER_BACKGROUND_SPRITE, var5, this.getY(), 6, this.getHeight());
         var1.blitSprite(SCROLLER_SPRITE, var5, var7, 6, var6);
         RenderSystem.disableBlend();
      }

      this.renderDecorations(var1, var2, var3);
      RenderSystem.disableBlend();
   }

   protected boolean scrollbarVisible() {
      return this.getMaxScroll() > 0;
   }

   protected void renderListSeparators(GuiGraphics var1) {
      RenderSystem.enableBlend();
      ResourceLocation var2 = this.minecraft.level == null ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
      ResourceLocation var3 = this.minecraft.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
      var1.blit(var2, this.getX(), this.getY() - 2, 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
      var1.blit(var3, this.getX(), this.getBottom(), 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
      RenderSystem.disableBlend();
   }

   protected void renderListBackground(GuiGraphics var1) {
      RenderSystem.enableBlend();
      ResourceLocation var2 = this.minecraft.level == null ? MENU_LIST_BACKGROUND : INWORLD_MENU_LIST_BACKGROUND;
      var1.blit(var2, this.getX(), this.getY(), (float)this.getRight(), (float)(this.getBottom() + (int)this.getScrollAmount()), this.getWidth(), this.getHeight(), 32, 32);
      RenderSystem.disableBlend();
   }

   protected void enableScissor(GuiGraphics var1) {
      var1.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
   }

   protected void centerScrollOn(E var1) {
      this.setScrollAmount((double)(this.children().indexOf(var1) * this.itemHeight + this.itemHeight / 2 - this.height / 2));
   }

   protected void ensureVisible(E var1) {
      int var2 = this.getRowTop(this.children().indexOf(var1));
      int var3 = var2 - this.getY() - 4 - this.itemHeight;
      if (var3 < 0) {
         this.scroll(var3);
      }

      int var4 = this.getBottom() - var2 - this.itemHeight - this.itemHeight;
      if (var4 < 0) {
         this.scroll(-var4);
      }

   }

   private void scroll(int var1) {
      this.setScrollAmount(this.getScrollAmount() + (double)var1);
   }

   public double getScrollAmount() {
      return this.scrollAmount;
   }

   public void setScrollAmount(double var1) {
      this.scrollAmount = Mth.clamp(var1, 0.0, (double)this.getMaxScroll());
   }

   public int getMaxScroll() {
      return Math.max(0, this.getMaxPosition() - (this.height - 4));
   }

   protected void updateScrollingState(double var1, double var3, int var5) {
      this.scrolling = var5 == 0 && var1 >= (double)this.getScrollbarPosition() && var1 < (double)(this.getScrollbarPosition() + 6);
   }

   protected int getScrollbarPosition() {
      return this.getDefaultScrollbarPosition();
   }

   protected int getDefaultScrollbarPosition() {
      return this.getRealRowRight() + this.getListOutlinePadding();
   }

   private int getListOutlinePadding() {
      return 10;
   }

   protected boolean isValidMouseClick(int var1) {
      return var1 == 0;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (!this.isValidMouseClick(var5)) {
         return false;
      } else {
         this.updateScrollingState(var1, var3, var5);
         if (!this.isMouseOver(var1, var3)) {
            return false;
         } else {
            Entry var6 = this.getEntryAtPosition(var1, var3);
            if (var6 != null) {
               if (var6.mouseClicked(var1, var3, var5)) {
                  Entry var7 = this.getFocused();
                  if (var7 != var6 && var7 instanceof ContainerEventHandler) {
                     ContainerEventHandler var8 = (ContainerEventHandler)var7;
                     var8.setFocused((GuiEventListener)null);
                  }

                  this.setFocused(var6);
                  this.setDragging(true);
                  return true;
               }
            } else if (this.clickedHeader((int)(var1 - (double)(this.getX() + this.width / 2 - this.getRowWidth() / 2)), (int)(var3 - (double)this.getY()) + (int)this.getScrollAmount() - 4)) {
               return true;
            }

            return this.scrolling;
         }
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (this.getFocused() != null) {
         this.getFocused().mouseReleased(var1, var3, var5);
      }

      return false;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (super.mouseDragged(var1, var3, var5, var6, var8)) {
         return true;
      } else if (var5 == 0 && this.scrolling) {
         if (var3 < (double)this.getY()) {
            this.setScrollAmount(0.0);
         } else if (var3 > (double)this.getBottom()) {
            this.setScrollAmount((double)this.getMaxScroll());
         } else {
            double var10 = (double)Math.max(1, this.getMaxScroll());
            int var12 = this.height;
            int var13 = Mth.clamp((int)((float)(var12 * var12) / (float)this.getMaxPosition()), 32, var12 - 8);
            double var14 = Math.max(1.0, var10 / (double)(var12 - var13));
            this.setScrollAmount(this.getScrollAmount() + var8 * var14);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      this.setScrollAmount(this.getScrollAmount() - var7 * (double)this.itemHeight / 2.0);
      return true;
   }

   public void setFocused(@Nullable GuiEventListener var1) {
      super.setFocused(var1);
      int var2 = this.children.indexOf(var1);
      if (var2 >= 0) {
         Entry var3 = (Entry)this.children.get(var2);
         this.setSelected(var3);
         if (this.minecraft.getLastInputType().isKeyboard()) {
            this.ensureVisible(var3);
         }
      }

   }

   @Nullable
   protected E nextEntry(ScreenDirection var1) {
      return this.nextEntry(var1, (var0) -> {
         return true;
      });
   }

   @Nullable
   protected E nextEntry(ScreenDirection var1, Predicate<E> var2) {
      return this.nextEntry(var1, var2, this.getSelected());
   }

   @Nullable
   protected E nextEntry(ScreenDirection var1, Predicate<E> var2, @Nullable E var3) {
      byte var10000;
      switch (var1) {
         case RIGHT:
         case LEFT:
            var10000 = 0;
            break;
         case UP:
            var10000 = -1;
            break;
         case DOWN:
            var10000 = 1;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      byte var4 = var10000;
      if (!this.children().isEmpty() && var4 != 0) {
         int var5;
         if (var3 == null) {
            var5 = var4 > 0 ? 0 : this.children().size() - 1;
         } else {
            var5 = this.children().indexOf(var3) + var4;
         }

         for(int var6 = var5; var6 >= 0 && var6 < this.children.size(); var6 += var4) {
            Entry var7 = (Entry)this.children().get(var6);
            if (var2.test(var7)) {
               return var7;
            }
         }
      }

      return null;
   }

   public boolean isMouseOver(double var1, double var3) {
      return var3 >= (double)this.getY() && var3 <= (double)this.getBottom() && var1 >= (double)this.getX() && var1 <= (double)this.getRight();
   }

   protected void renderListItems(GuiGraphics var1, int var2, int var3, float var4) {
      int var5 = this.getRowLeft();
      int var6 = this.getRowWidth();
      int var7 = this.itemHeight - 4;
      int var8 = this.getItemCount();

      for(int var9 = 0; var9 < var8; ++var9) {
         int var10 = this.getRowTop(var9);
         int var11 = this.getRowBottom(var9);
         if (var11 >= this.getY() && var10 <= this.getBottom()) {
            this.renderItem(var1, var2, var3, var4, var9, var5, var10, var6, var7);
         }
      }

   }

   protected void renderItem(GuiGraphics var1, int var2, int var3, float var4, int var5, int var6, int var7, int var8, int var9) {
      Entry var10 = this.getEntry(var5);
      var10.renderBack(var1, var5, var7, var6, var8, var9, var2, var3, Objects.equals(this.hovered, var10), var4);
      if (this.isSelectedItem(var5)) {
         int var11 = this.isFocused() ? -1 : -8355712;
         this.renderSelection(var1, var7, var8, var9, var11, -16777216);
      }

      var10.render(var1, var5, var7, var6, var8, var9, var2, var3, Objects.equals(this.hovered, var10), var4);
   }

   protected void renderSelection(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6) {
      int var7 = this.getX() + (this.width - var3) / 2;
      int var8 = this.getX() + (this.width + var3) / 2;
      var1.fill(var7, var2 - 2, var8, var2 + var4 + 2, var5);
      var1.fill(var7 + 1, var2 - 1, var8 - 1, var2 + var4 + 1, var6);
   }

   public int getRowLeft() {
      return this.getX() + this.width / 2 - this.getRowWidth() / 2 + 2;
   }

   private int getRealRowLeft() {
      return this.getX() + this.width / 2 - this.getRowWidth() / 2;
   }

   public int getRowRight() {
      return this.getRowLeft() + this.getRowWidth();
   }

   private int getRealRowRight() {
      return this.getRealRowLeft() + this.getRowWidth();
   }

   protected int getRowTop(int var1) {
      return this.getY() + 4 - (int)this.getScrollAmount() + var1 * this.itemHeight + this.headerHeight;
   }

   protected int getRowBottom(int var1) {
      return this.getRowTop(var1) + this.itemHeight;
   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      if (this.isFocused()) {
         return NarratableEntry.NarrationPriority.FOCUSED;
      } else {
         return this.hovered != null ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
      }
   }

   @Nullable
   protected E remove(int var1) {
      Entry var2 = (Entry)this.children.get(var1);
      return this.removeEntry((Entry)this.children.get(var1)) ? var2 : null;
   }

   protected boolean removeEntry(E var1) {
      boolean var2 = this.children.remove(var1);
      if (var2 && var1 == this.getSelected()) {
         this.setSelected((Entry)null);
      }

      return var2;
   }

   @Nullable
   protected E getHovered() {
      return this.hovered;
   }

   void bindEntryToSelf(Entry<E> var1) {
      var1.list = this;
   }

   protected void narrateListElementPosition(NarrationElementOutput var1, E var2) {
      List var3 = this.children();
      if (var3.size() > 1) {
         int var4 = var3.indexOf(var2);
         if (var4 != -1) {
            var1.add(NarratedElementType.POSITION, (Component)Component.translatable("narrator.position.list", var4 + 1, var3.size()));
         }
      }

   }

   // $FF: synthetic method
   @Nullable
   public GuiEventListener getFocused() {
      return this.getFocused();
   }

   private class TrackedList extends AbstractList<E> {
      private final List<E> delegate = Lists.newArrayList();

      TrackedList() {
         super();
      }

      public E get(int var1) {
         return (Entry)this.delegate.get(var1);
      }

      public int size() {
         return this.delegate.size();
      }

      public E set(int var1, E var2) {
         Entry var3 = (Entry)this.delegate.set(var1, var2);
         AbstractSelectionList.this.bindEntryToSelf(var2);
         return var3;
      }

      public void add(int var1, E var2) {
         this.delegate.add(var1, var2);
         AbstractSelectionList.this.bindEntryToSelf(var2);
      }

      public E remove(int var1) {
         return (Entry)this.delegate.remove(var1);
      }

      // $FF: synthetic method
      public Object remove(int var1) {
         return this.remove(var1);
      }

      // $FF: synthetic method
      public void add(int var1, Object var2) {
         this.add(var1, (Entry)var2);
      }

      // $FF: synthetic method
      public Object set(int var1, Object var2) {
         return this.set(var1, (Entry)var2);
      }

      // $FF: synthetic method
      public Object get(int var1) {
         return this.get(var1);
      }
   }

   protected abstract static class Entry<E extends Entry<E>> implements GuiEventListener {
      /** @deprecated */
      @Deprecated
      AbstractSelectionList<E> list;

      protected Entry() {
         super();
      }

      public void setFocused(boolean var1) {
      }

      public boolean isFocused() {
         return this.list.getFocused() == this;
      }

      public abstract void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);

      public void renderBack(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
      }

      public boolean isMouseOver(double var1, double var3) {
         return Objects.equals(this.list.getEntryAtPosition(var1, var3), this);
      }
   }
}
