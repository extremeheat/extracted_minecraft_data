package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jspecify.annotations.Nullable;

public abstract class AbstractSelectionList<E extends AbstractSelectionList.Entry<E>> extends AbstractContainerWidget {
   private static final ResourceLocation MENU_LIST_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/menu_list_background.png");
   private static final ResourceLocation INWORLD_MENU_LIST_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");
   private static final int SEPARATOR_HEIGHT = 2;
   protected final Minecraft minecraft;
   protected final int defaultEntryHeight;
   private final List<E> children = new TrackedList();
   protected boolean centerListVertically = true;
   private @Nullable E selected;
   private @Nullable E hovered;

   public AbstractSelectionList(Minecraft var1, int var2, int var3, int var4, int var5) {
      super(0, var4, var2, var3, CommonComponents.EMPTY);
      this.minecraft = var1;
      this.defaultEntryHeight = var5;
   }

   public @Nullable E getSelected() {
      return this.selected;
   }

   public void setSelected(@Nullable E var1) {
      this.selected = var1;
      if (var1 != null) {
         boolean var2 = var1.getContentY() < this.getY();
         boolean var3 = var1.getContentBottom() > this.getBottom();
         if (this.minecraft.getLastInputType().isKeyboard() || var2 || var3) {
            this.scrollToEntry(var1);
         }
      }

   }

   public @Nullable E getFocused() {
      return (E)(super.getFocused());
   }

   public final List<E> children() {
      return Collections.unmodifiableList(this.children);
   }

   protected void sort(Comparator<E> var1) {
      this.children.sort(var1);
      this.repositionEntries();
   }

   protected void swap(int var1, int var2) {
      Collections.swap(this.children, var1, var2);
      this.repositionEntries();
      this.scrollToEntry((Entry)this.children.get(var2));
   }

   protected void clearEntries() {
      this.children.clear();
      this.selected = null;
   }

   protected void clearEntriesExcept(E var1) {
      this.children.removeIf((var1x) -> var1x != var1);
      if (this.selected != var1) {
         this.setSelected((Entry)null);
      }

   }

   public void replaceEntries(Collection<E> var1) {
      this.clearEntries();

      for(Entry var3 : var1) {
         this.addEntry(var3);
      }

   }

   private int getFirstEntryY() {
      return this.getY() + 2;
   }

   public int getNextY() {
      int var1 = this.getFirstEntryY() - (int)this.scrollAmount();

      for(Entry var3 : this.children) {
         var1 += var3.getHeight();
      }

      return var1;
   }

   protected int addEntry(E var1) {
      return this.addEntry(var1, this.defaultEntryHeight);
   }

   protected int addEntry(E var1, int var2) {
      var1.setX(this.getRowLeft());
      var1.setWidth(this.getRowWidth());
      var1.setY(this.getNextY());
      var1.setHeight(var2);
      this.children.add(var1);
      return this.children.size() - 1;
   }

   protected void addEntryToTop(E var1) {
      this.addEntryToTop(var1, this.defaultEntryHeight);
   }

   protected void addEntryToTop(E var1, int var2) {
      double var3 = (double)this.maxScrollAmount() - this.scrollAmount();
      var1.setHeight(var2);
      this.children.addFirst(var1);
      this.repositionEntries();
      this.setScrollAmount((double)this.maxScrollAmount() - var3);
   }

   private void repositionEntries() {
      int var1 = this.getFirstEntryY() - (int)this.scrollAmount();

      for(Entry var3 : this.children) {
         var3.setY(var1);
         var1 += var3.getHeight();
         var3.setX(this.getRowLeft());
         var3.setWidth(this.getRowWidth());
      }

   }

   protected void removeEntryFromTop(E var1) {
      double var2 = (double)this.maxScrollAmount() - this.scrollAmount();
      this.removeEntry(var1);
      this.setScrollAmount((double)this.maxScrollAmount() - var2);
   }

   protected int getItemCount() {
      return this.children().size();
   }

   protected boolean entriesCanBeSelected() {
      return true;
   }

   protected final @Nullable E getEntryAtPosition(double var1, double var3) {
      for(Entry var6 : this.children) {
         if (var6.isMouseOver(var1, var3)) {
            return (E)var6;
         }
      }

      return null;
   }

   public void updateSize(int var1, HeaderAndFooterLayout var2) {
      this.updateSizeAndPosition(var1, var2.getContentHeight(), var2.getHeaderHeight());
   }

   public void updateSizeAndPosition(int var1, int var2, int var3) {
      this.updateSizeAndPosition(var1, var2, 0, var3);
   }

   public void updateSizeAndPosition(int var1, int var2, int var3, int var4) {
      this.setSize(var1, var2);
      this.setPosition(var3, var4);
      this.repositionEntries();
      if (this.getSelected() != null) {
         this.scrollToEntry(this.getSelected());
      }

      this.refreshScrollAmount();
   }

   protected int contentHeight() {
      int var1 = 0;

      for(Entry var3 : this.children) {
         var1 += var3.getHeight();
      }

      return var1 + 4;
   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      this.hovered = this.isMouseOver((double)var2, (double)var3) ? this.getEntryAtPosition((double)var2, (double)var3) : null;
      this.renderListBackground(var1);
      this.enableScissor(var1);
      this.renderListItems(var1, var2, var3, var4);
      var1.disableScissor();
      this.renderListSeparators(var1);
      this.renderScrollbar(var1, var2, var3);
   }

   protected void renderListSeparators(GuiGraphics var1) {
      ResourceLocation var2 = this.minecraft.level == null ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
      ResourceLocation var3 = this.minecraft.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
      var1.blit(RenderPipelines.GUI_TEXTURED, var2, this.getX(), this.getY() - 2, 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
      var1.blit(RenderPipelines.GUI_TEXTURED, var3, this.getX(), this.getBottom(), 0.0F, 0.0F, this.getWidth(), 2, 32, 2);
   }

   protected void renderListBackground(GuiGraphics var1) {
      ResourceLocation var2 = this.minecraft.level == null ? MENU_LIST_BACKGROUND : INWORLD_MENU_LIST_BACKGROUND;
      var1.blit(RenderPipelines.GUI_TEXTURED, var2, this.getX(), this.getY(), (float)this.getRight(), (float)(this.getBottom() + (int)this.scrollAmount()), this.getWidth(), this.getHeight(), 32, 32);
   }

   protected void enableScissor(GuiGraphics var1) {
      var1.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
   }

   protected void scrollToEntry(E var1) {
      int var2 = var1.getY() - this.getY() - 2;
      if (var2 < 0) {
         this.scroll(var2);
      }

      int var3 = this.getBottom() - var1.getY() - var1.getHeight() - 2;
      if (var3 < 0) {
         this.scroll(-var3);
      }

   }

   protected void centerScrollOn(E var1) {
      int var2 = 0;

      for(Entry var4 : this.children) {
         if (var4 == var1) {
            var2 += var4.getHeight() / 2;
            break;
         }

         var2 += var4.getHeight();
      }

      this.setScrollAmount((double)var2 - (double)this.height / 2.0);
   }

   private void scroll(int var1) {
      this.setScrollAmount(this.scrollAmount() + (double)var1);
   }

   public void setScrollAmount(double var1) {
      super.setScrollAmount(var1);
      this.repositionEntries();
   }

   protected double scrollRate() {
      return (double)this.defaultEntryHeight / 2.0;
   }

   protected int scrollBarX() {
      return this.getRowRight() + 6 + 2;
   }

   public Optional<GuiEventListener> getChildAt(double var1, double var3) {
      return Optional.ofNullable(this.getEntryAtPosition(var1, var3));
   }

   public void setFocused(boolean var1) {
      super.setFocused(var1);
      if (!var1) {
         this.setFocused((GuiEventListener)null);
      }

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
      }

   }

   protected @Nullable E nextEntry(ScreenDirection var1) {
      return (E)this.nextEntry(var1, (var0) -> true);
   }

   protected @Nullable E nextEntry(ScreenDirection var1, Predicate<E> var2) {
      return (E)this.nextEntry(var1, var2, this.getSelected());
   }

   protected @Nullable E nextEntry(ScreenDirection var1, Predicate<E> var2, @Nullable E var3) {
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
               return (E)var7;
            }
         }
      }

      return null;
   }

   protected void renderListItems(GuiGraphics var1, int var2, int var3, float var4) {
      for(Entry var6 : this.children) {
         if (var6.getY() + var6.getHeight() >= this.getY() && var6.getY() <= this.getBottom()) {
            this.renderItem(var1, var2, var3, var4, var6);
         }
      }

   }

   protected void renderItem(GuiGraphics var1, int var2, int var3, float var4, E var5) {
      if (this.entriesCanBeSelected() && this.getSelected() == var5) {
         int var6 = this.isFocused() ? -1 : -8355712;
         this.renderSelection(var1, var5, var6);
      }

      var5.renderContent(var1, var2, var3, Objects.equals(this.hovered, var5), var4);
   }

   protected void renderSelection(GuiGraphics var1, E var2, int var3) {
      int var4 = var2.getX();
      int var5 = var2.getY();
      int var6 = var4 + var2.getWidth();
      int var7 = var5 + var2.getHeight();
      var1.fill(var4, var5, var6, var7, var3);
      var1.fill(var4 + 1, var5 + 1, var6 - 1, var7 - 1, -16777216);
   }

   public int getRowLeft() {
      return this.getX() + this.width / 2 - this.getRowWidth() / 2;
   }

   public int getRowRight() {
      return this.getRowLeft() + this.getRowWidth();
   }

   public int getRowTop(int var1) {
      return ((Entry)this.children.get(var1)).getY();
   }

   public int getRowBottom(int var1) {
      Entry var2 = (Entry)this.children.get(var1);
      return var2.getY() + var2.getHeight();
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

   protected void removeEntries(List<E> var1) {
      var1.forEach(this::removeEntry);
   }

   protected void removeEntry(E var1) {
      boolean var2 = this.children.remove(var1);
      if (var2) {
         this.repositionEntries();
         if (var1 == this.getSelected()) {
            this.setSelected((Entry)null);
         }
      }

   }

   protected @Nullable E getHovered() {
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
   public @Nullable GuiEventListener getFocused() {
      return this.getFocused();
   }

   protected abstract static class Entry<E extends Entry<E>> implements GuiEventListener, LayoutElement {
      public static final int CONTENT_PADDING = 2;
      private int x = 0;
      private int y = 0;
      private int width = 0;
      private int height;
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

      public abstract void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5);

      public boolean isMouseOver(double var1, double var3) {
         return this.getRectangle().containsPoint((int)var1, (int)var3);
      }

      public void setX(int var1) {
         this.x = var1;
      }

      public void setY(int var1) {
         this.y = var1;
      }

      public void setWidth(int var1) {
         this.width = var1;
      }

      public void setHeight(int var1) {
         this.height = var1;
      }

      public int getContentX() {
         return this.getX() + 2;
      }

      public int getContentY() {
         return this.getY() + 2;
      }

      public int getContentHeight() {
         return this.getHeight() - 4;
      }

      public int getContentYMiddle() {
         return this.getContentY() + this.getContentHeight() / 2;
      }

      public int getContentBottom() {
         return this.getContentY() + this.getContentHeight();
      }

      public int getContentWidth() {
         return this.getWidth() - 4;
      }

      public int getContentXMiddle() {
         return this.getContentX() + this.getContentWidth() / 2;
      }

      public int getContentRight() {
         return this.getContentX() + this.getContentWidth();
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public void visitWidgets(Consumer<AbstractWidget> var1) {
      }

      public ScreenRectangle getRectangle() {
         return LayoutElement.super.getRectangle();
      }
   }

   class TrackedList extends AbstractList<E> {
      private final List<E> delegate = Lists.newArrayList();

      TrackedList() {
         super();
      }

      public E get(int var1) {
         return (E)((Entry)this.delegate.get(var1));
      }

      public int size() {
         return this.delegate.size();
      }

      public E set(int var1, E var2) {
         Entry var3 = (Entry)this.delegate.set(var1, var2);
         AbstractSelectionList.this.bindEntryToSelf(var2);
         return (E)var3;
      }

      public void add(int var1, E var2) {
         this.delegate.add(var1, var2);
         AbstractSelectionList.this.bindEntryToSelf(var2);
      }

      public E remove(int var1) {
         return (E)((Entry)this.delegate.remove(var1));
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
}
