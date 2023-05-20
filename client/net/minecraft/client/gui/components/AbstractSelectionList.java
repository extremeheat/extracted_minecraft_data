package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class AbstractSelectionList<E extends AbstractSelectionList.Entry<E>>
   extends AbstractContainerEventHandler
   implements Renderable,
   NarratableEntry {
   protected final Minecraft minecraft;
   protected final int itemHeight;
   private final List<E> children = new AbstractSelectionList.TrackedList();
   protected int width;
   protected int height;
   protected int y0;
   protected int y1;
   protected int x1;
   protected int x0;
   protected boolean centerListVertically = true;
   private double scrollAmount;
   private boolean renderSelection = true;
   private boolean renderHeader;
   protected int headerHeight;
   private boolean scrolling;
   @Nullable
   private E selected;
   private boolean renderBackground = true;
   private boolean renderTopAndBottom = true;
   @Nullable
   private E hovered;

   public AbstractSelectionList(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      super();
      this.minecraft = var1;
      this.width = var2;
      this.height = var3;
      this.y0 = var4;
      this.y1 = var5;
      this.itemHeight = var6;
      this.x0 = 0;
      this.x1 = var2;
   }

   public void setRenderSelection(boolean var1) {
      this.renderSelection = var1;
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
      return this.children.get(0);
   }

   public void setRenderBackground(boolean var1) {
      this.renderBackground = var1;
   }

   public void setRenderTopAndBottom(boolean var1) {
      this.renderTopAndBottom = var1;
   }

   @Nullable
   public E getFocused() {
      return (E)super.getFocused();
   }

   @Override
   public final List<E> children() {
      return this.children;
   }

   protected final void clearEntries() {
      this.children.clear();
      this.selected = null;
   }

   protected void replaceEntries(Collection<E> var1) {
      this.clearEntries();
      this.children.addAll(var1);
   }

   protected E getEntry(int var1) {
      return this.children().get(var1);
   }

   protected int addEntry(E var1) {
      this.children.add((E)var1);
      return this.children.size() - 1;
   }

   protected void addEntryToTop(E var1) {
      double var2 = (double)this.getMaxScroll() - this.getScrollAmount();
      this.children.add(0, (E)var1);
      this.setScrollAmount((double)this.getMaxScroll() - var2);
   }

   protected boolean removeEntryFromTop(E var1) {
      double var2 = (double)this.getMaxScroll() - this.getScrollAmount();
      boolean var4 = this.removeEntry((E)var1);
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
      int var6 = this.x0 + this.width / 2;
      int var7 = var6 - var5;
      int var8 = var6 + var5;
      int var9 = Mth.floor(var3 - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
      int var10 = var9 / this.itemHeight;
      return var1 < (double)this.getScrollbarPosition()
            && var1 >= (double)var7
            && var1 <= (double)var8
            && var10 >= 0
            && var9 >= 0
            && var10 < this.getItemCount()
         ? this.children().get(var10)
         : null;
   }

   public void updateSize(int var1, int var2, int var3, int var4) {
      this.width = var1;
      this.height = var2;
      this.y0 = var3;
      this.y1 = var4;
      this.x0 = 0;
      this.x1 = var1;
   }

   public void setLeftPos(int var1) {
      this.x0 = var1;
      this.x1 = var1 + this.width;
   }

   protected int getMaxPosition() {
      return this.getItemCount() * this.itemHeight + this.headerHeight;
   }

   protected void clickedHeader(int var1, int var2) {
   }

   protected void renderHeader(PoseStack var1, int var2, int var3) {
   }

   protected void renderBackground(PoseStack var1) {
   }

   protected void renderDecorations(PoseStack var1, int var2, int var3) {
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      int var5 = this.getScrollbarPosition();
      int var6 = var5 + 6;
      this.hovered = this.isMouseOver((double)var2, (double)var3) ? this.getEntryAtPosition((double)var2, (double)var3) : null;
      if (this.renderBackground) {
         RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
         RenderSystem.setShaderColor(0.125F, 0.125F, 0.125F, 1.0F);
         boolean var7 = true;
         blit(var1, this.x0, this.y0, (float)this.x1, (float)(this.y1 + (int)this.getScrollAmount()), this.x1 - this.x0, this.y1 - this.y0, 32, 32);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }

      int var12 = this.getRowLeft();
      int var8 = this.y0 + 4 - (int)this.getScrollAmount();
      this.enableScissor();
      if (this.renderHeader) {
         this.renderHeader(var1, var12, var8);
      }

      this.renderList(var1, var2, var3, var4);
      disableScissor();
      if (this.renderTopAndBottom) {
         RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
         boolean var9 = true;
         RenderSystem.setShaderColor(0.25F, 0.25F, 0.25F, 1.0F);
         blit(var1, this.x0, 0, 0.0F, 0.0F, this.width, this.y0, 32, 32);
         blit(var1, this.x0, this.y1, 0.0F, (float)this.y1, this.width, this.height - this.y1, 32, 32);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         boolean var10 = true;
         fillGradient(var1, this.x0, this.y0, this.x1, this.y0 + 4, -16777216, 0);
         fillGradient(var1, this.x0, this.y1 - 4, this.x1, this.y1, 0, -16777216);
      }

      int var13 = this.getMaxScroll();
      if (var13 > 0) {
         int var14 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
         var14 = Mth.clamp(var14, 32, this.y1 - this.y0 - 8);
         int var11 = (int)this.getScrollAmount() * (this.y1 - this.y0 - var14) / var13 + this.y0;
         if (var11 < this.y0) {
            var11 = this.y0;
         }

         fill(var1, var5, this.y0, var6, this.y1, -16777216);
         fill(var1, var5, var11, var6, var11 + var14, -8355712);
         fill(var1, var5, var11, var6 - 1, var11 + var14 - 1, -4144960);
      }

      this.renderDecorations(var1, var2, var3);
      RenderSystem.disableBlend();
   }

   protected void enableScissor() {
      enableScissor(this.x0, this.y0, this.x1, this.y1);
   }

   protected void centerScrollOn(E var1) {
      this.setScrollAmount((double)(this.children().indexOf(var1) * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2));
   }

   protected void ensureVisible(E var1) {
      int var2 = this.getRowTop(this.children().indexOf(var1));
      int var3 = var2 - this.y0 - 4 - this.itemHeight;
      if (var3 < 0) {
         this.scroll(var3);
      }

      int var4 = this.y1 - var2 - this.itemHeight - this.itemHeight;
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
      return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
   }

   public int getScrollBottom() {
      return (int)this.getScrollAmount() - this.height - this.headerHeight;
   }

   protected void updateScrollingState(double var1, double var3, int var5) {
      this.scrolling = var5 == 0 && var1 >= (double)this.getScrollbarPosition() && var1 < (double)(this.getScrollbarPosition() + 6);
   }

   protected int getScrollbarPosition() {
      return this.width / 2 + 124;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      this.updateScrollingState(var1, var3, var5);
      if (!this.isMouseOver(var1, var3)) {
         return false;
      } else {
         AbstractSelectionList.Entry var6 = this.getEntryAtPosition(var1, var3);
         if (var6 != null) {
            if (var6.mouseClicked(var1, var3, var5)) {
               AbstractSelectionList.Entry var7 = this.getFocused();
               if (var7 != var6 && var7 instanceof ContainerEventHandler var8) {
                  var8.setFocused(null);
               }

               this.setFocused(var6);
               this.setDragging(true);
               return true;
            }
         } else if (var5 == 0) {
            this.clickedHeader(
               (int)(var1 - (double)(this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(var3 - (double)this.y0) + (int)this.getScrollAmount() - 4
            );
            return true;
         }

         return this.scrolling;
      }
   }

   @Override
   public boolean mouseReleased(double var1, double var3, int var5) {
      if (this.getFocused() != null) {
         this.getFocused().mouseReleased(var1, var3, var5);
      }

      return false;
   }

   @Override
   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (super.mouseDragged(var1, var3, var5, var6, var8)) {
         return true;
      } else if (var5 == 0 && this.scrolling) {
         if (var3 < (double)this.y0) {
            this.setScrollAmount(0.0);
         } else if (var3 > (double)this.y1) {
            this.setScrollAmount((double)this.getMaxScroll());
         } else {
            double var10 = (double)Math.max(1, this.getMaxScroll());
            int var12 = this.y1 - this.y0;
            int var13 = Mth.clamp((int)((float)(var12 * var12) / (float)this.getMaxPosition()), 32, var12 - 8);
            double var14 = Math.max(1.0, var10 / (double)(var12 - var13));
            this.setScrollAmount(this.getScrollAmount() + var8 * var14);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5) {
      this.setScrollAmount(this.getScrollAmount() - var5 * (double)this.itemHeight / 2.0);
      return true;
   }

   @Override
   public void setFocused(@Nullable GuiEventListener var1) {
      super.setFocused(var1);
      int var2 = this.children.indexOf(var1);
      if (var2 >= 0) {
         AbstractSelectionList.Entry var3 = this.children.get(var2);
         this.setSelected((E)var3);
         if (this.minecraft.getLastInputType().isKeyboard()) {
            this.ensureVisible((E)var3);
         }
      }
   }

   @Nullable
   protected E nextEntry(ScreenDirection var1) {
      return this.nextEntry(var1, var0 -> true);
   }

   @Nullable
   protected E nextEntry(ScreenDirection var1, Predicate<E> var2) {
      return this.nextEntry(var1, var2, this.getSelected());
   }

   @Nullable
   protected E nextEntry(ScreenDirection var1, Predicate<E> var2, @Nullable E var3) {
      byte var4 = switch(var1) {
         case RIGHT, LEFT -> 0;
         case UP -> -1;
         case DOWN -> 1;
      };
      if (!this.children().isEmpty() && var4 != 0) {
         int var5;
         if (var3 == null) {
            var5 = var4 > 0 ? 0 : this.children().size() - 1;
         } else {
            var5 = this.children().indexOf(var3) + var4;
         }

         for(int var6 = var5; var6 >= 0 && var6 < this.children.size(); var6 += var4) {
            AbstractSelectionList.Entry var7 = this.children().get(var6);
            if (var2.test(var7)) {
               return (E)var7;
            }
         }
      }

      return null;
   }

   @Override
   public boolean isMouseOver(double var1, double var3) {
      return var3 >= (double)this.y0 && var3 <= (double)this.y1 && var1 >= (double)this.x0 && var1 <= (double)this.x1;
   }

   protected void renderList(PoseStack var1, int var2, int var3, float var4) {
      int var5 = this.getRowLeft();
      int var6 = this.getRowWidth();
      int var7 = this.itemHeight - 4;
      int var8 = this.getItemCount();

      for(int var9 = 0; var9 < var8; ++var9) {
         int var10 = this.getRowTop(var9);
         int var11 = this.getRowBottom(var9);
         if (var11 >= this.y0 && var10 <= this.y1) {
            this.renderItem(var1, var2, var3, var4, var9, var5, var10, var6, var7);
         }
      }
   }

   protected void renderItem(PoseStack var1, int var2, int var3, float var4, int var5, int var6, int var7, int var8, int var9) {
      AbstractSelectionList.Entry var10 = this.getEntry(var5);
      var10.renderBack(var1, var5, var7, var6, var8, var9, var2, var3, Objects.equals(this.hovered, var10), var4);
      if (this.renderSelection && this.isSelectedItem(var5)) {
         int var11 = this.isFocused() ? -1 : -8355712;
         this.renderSelection(var1, var7, var8, var9, var11, -16777216);
      }

      var10.render(var1, var5, var7, var6, var8, var9, var2, var3, Objects.equals(this.hovered, var10), var4);
   }

   protected void renderSelection(PoseStack var1, int var2, int var3, int var4, int var5, int var6) {
      int var7 = this.x0 + (this.width - var3) / 2;
      int var8 = this.x0 + (this.width + var3) / 2;
      fill(var1, var7, var2 - 2, var8, var2 + var4 + 2, var5);
      fill(var1, var7 + 1, var2 - 1, var8 - 1, var2 + var4 + 1, var6);
   }

   public int getRowLeft() {
      return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
   }

   public int getRowRight() {
      return this.getRowLeft() + this.getRowWidth();
   }

   protected int getRowTop(int var1) {
      return this.y0 + 4 - (int)this.getScrollAmount() + var1 * this.itemHeight + this.headerHeight;
   }

   protected int getRowBottom(int var1) {
      return this.getRowTop(var1) + this.itemHeight;
   }

   @Override
   public NarratableEntry.NarrationPriority narrationPriority() {
      if (this.isFocused()) {
         return NarratableEntry.NarrationPriority.FOCUSED;
      } else {
         return this.hovered != null ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
      }
   }

   @Nullable
   protected E remove(int var1) {
      AbstractSelectionList.Entry var2 = this.children.get(var1);
      return (E)(this.removeEntry(this.children.get(var1)) ? var2 : null);
   }

   protected boolean removeEntry(E var1) {
      boolean var2 = this.children.remove(var1);
      if (var2 && var1 == this.getSelected()) {
         this.setSelected((E)null);
      }

      return var2;
   }

   @Nullable
   protected E getHovered() {
      return this.hovered;
   }

   void bindEntryToSelf(AbstractSelectionList.Entry<E> var1) {
      var1.list = this;
   }

   protected void narrateListElementPosition(NarrationElementOutput var1, E var2) {
      List var3 = this.children();
      if (var3.size() > 1) {
         int var4 = var3.indexOf(var2);
         if (var4 != -1) {
            var1.add(NarratedElementType.POSITION, Component.translatable("narrator.position.list", var4 + 1, var3.size()));
         }
      }
   }

   @Override
   public ScreenRectangle getRectangle() {
      return new ScreenRectangle(this.x0, this.y0, this.x1 - this.x0, this.y1 - this.y0);
   }

   protected abstract static class Entry<E extends AbstractSelectionList.Entry<E>> implements GuiEventListener {
      @Deprecated
      AbstractSelectionList<E> list;

      protected Entry() {
         super();
      }

      @Override
      public void setFocused(boolean var1) {
      }

      @Override
      public boolean isFocused() {
         return this.list.getFocused() == this;
      }

      public abstract void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);

      public void renderBack(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
      }

      @Override
      public boolean isMouseOver(double var1, double var3) {
         return Objects.equals(this.list.getEntryAtPosition(var1, var3), this);
      }
   }

   class TrackedList extends AbstractList<E> {
      private final List<E> delegate = Lists.newArrayList();

      TrackedList() {
         super();
      }

      public E get(int var1) {
         return this.delegate.get(var1);
      }

      @Override
      public int size() {
         return this.delegate.size();
      }

      public E set(int var1, E var2) {
         AbstractSelectionList.Entry var3 = this.delegate.set(var1, (E)var2);
         AbstractSelectionList.this.bindEntryToSelf(var2);
         return (E)var3;
      }

      public void add(int var1, E var2) {
         this.delegate.add(var1, (E)var2);
         AbstractSelectionList.this.bindEntryToSelf(var2);
      }

      public E remove(int var1) {
         return this.delegate.remove(var1);
      }
   }
}
