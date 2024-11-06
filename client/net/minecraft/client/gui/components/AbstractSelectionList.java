package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class AbstractSelectionList<E extends Entry<E>> extends AbstractContainerWidget {
   private static final ResourceLocation MENU_LIST_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/menu_list_background.png");
   private static final ResourceLocation INWORLD_MENU_LIST_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");
   protected final Minecraft minecraft;
   protected final int itemHeight;
   private final List<E> children;
   protected boolean centerListVertically;
   private boolean renderHeader;
   protected int headerHeight;
   @Nullable
   private E selected;
   @Nullable
   private E hovered;

   public AbstractSelectionList(Minecraft var1, int var2, int var3, int var4, int var5) {
      super(0, var4, var2, var3, CommonComponents.EMPTY);
      this.children = new TrackedList();
      this.centerListVertically = true;
      this.minecraft = var1;
      this.itemHeight = var5;
   }

   public AbstractSelectionList(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      this(var1, var2, var3, var4, var5);
      this.renderHeader = true;
      this.headerHeight = var6;
   }

   @Nullable
   public E getSelected() {
      return this.selected;
   }

   public void setSelectedIndex(int var1) {
      if (var1 == -1) {
         this.setSelected((Entry)null);
      } else if (this.getItemCount() != 0) {
         this.setSelected(this.getEntry(var1));
      }

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

   public void replaceEntries(Collection<E> var1) {
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
      double var2 = (double)this.maxScrollAmount() - this.scrollAmount();
      this.children.add(0, var1);
      this.setScrollAmount((double)this.maxScrollAmount() - var2);
   }

   protected boolean removeEntryFromTop(E var1) {
      double var2 = (double)this.maxScrollAmount() - this.scrollAmount();
      boolean var4 = this.removeEntry(var1);
      this.setScrollAmount((double)this.maxScrollAmount() - var2);
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
      int var9 = Mth.floor(var3 - (double)this.getY()) - this.headerHeight + (int)this.scrollAmount() - 4;
      int var10 = var9 / this.itemHeight;
      return var1 >= (double)var7 && var1 <= (double)var8 && var10 >= 0 && var9 >= 0 && var10 < this.getItemCount() ? (Entry)this.children().get(var10) : null;
   }

   public void updateSize(int var1, HeaderAndFooterLayout var2) {
      this.updateSizeAndPosition(var1, var2.getContentHeight(), var2.getHeaderHeight());
   }

   public void updateSizeAndPosition(int var1, int var2, int var3) {
      this.setSize(var1, var2);
      this.setPosition(0, var3);
      this.refreshScrollAmount();
   }

   protected int contentHeight() {
      return this.getItemCount() * this.itemHeight + this.headerHeight + 4;
   }

   protected void renderHeader(GuiGraphics var1, int var2, int var3) {
   }

   protected void renderDecorations(GuiGraphics var1, int var2, int var3) {
   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      this.hovered = this.isMouseOver((double)var2, (double)var3) ? this.getEntryAtPosition((double)var2, (double)var3) : null;
      this.renderListBackground(var1);
      this.enableScissor(var1);
      if (this.renderHeader) {
         int var5 = this.getRowLeft();
         int var6 = this.getY() + 4 - (int)this.scrollAmount();
         this.renderHeader(var1, var5, var6);
      }

      this.renderListItems(var1, var2, var3, var4);
      var1.disableScissor();
      this.renderListSeparators(var1);
      this.renderScrollbar(var1);
      this.renderDecorations(var1, var2, var3);
   }

   protected void renderListSeparators(GuiGraphics var1) {
      ResourceLocation var2 = this.minecraft.level == null ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
      ResourceLocation var3 = this.minecraft.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
      var1.blit(RenderType::guiTextured, var2, this.getX(), this.getY() - 2, 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
      var1.blit(RenderType::guiTextured, var3, this.getX(), this.getBottom(), 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
   }

   protected void renderListBackground(GuiGraphics var1) {
      ResourceLocation var2 = this.minecraft.level == null ? MENU_LIST_BACKGROUND : INWORLD_MENU_LIST_BACKGROUND;
      var1.blit(RenderType::guiTextured, var2, this.getX(), this.getY(), (float)this.getRight(), (float)(this.getBottom() + (int)this.scrollAmount()), this.getWidth(), this.getHeight(), 32, 32);
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
      this.setScrollAmount(this.scrollAmount() + (double)var1);
   }

   protected double scrollRate() {
      return (double)this.itemHeight / 2.0;
   }

   protected int scrollBarX() {
      return this.getRowRight() + 6 + 2;
   }

   public Optional<GuiEventListener> getChildAt(double var1, double var3) {
      return Optional.ofNullable(this.getEntryAtPosition(var1, var3));
   }

   public void setFocused(@Nullable GuiEventListener var1) {
      Entry var2 = this.getFocused();
      if (var2 != var1 && var2 instanceof ContainerEventHandler var3) {
         var3.setFocused((GuiEventListener)null);
      }

      super.setFocused(var1);
      int var5 = this.children.indexOf(var1);
      if (var5 >= 0) {
         Entry var4 = (Entry)this.children.get(var5);
         this.setSelected(var4);
         if (this.minecraft.getLastInputType().isKeyboard()) {
            this.ensureVisible(var4);
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

   public int getRowRight() {
      return this.getRowLeft() + this.getRowWidth();
   }

   public int getRowTop(int var1) {
      return this.getY() + 4 - (int)this.scrollAmount() + var1 * this.itemHeight + this.headerHeight;
   }

   public int getRowBottom(int var1) {
      return this.getRowTop(var1) + this.itemHeight;
   }

   public int getRowWidth() {
      return 220;
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
      public Object remove(final int var1) {
         return this.remove(var1);
      }

      // $FF: synthetic method
      public void add(final int var1, final Object var2) {
         this.add(var1, (Entry)var2);
      }

      // $FF: synthetic method
      public Object set(final int var1, final Object var2) {
         return this.set(var1, (Entry)var2);
      }

      // $FF: synthetic method
      public Object get(final int var1) {
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
