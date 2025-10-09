package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class OptionsList extends ContainerObjectSelectionList<AbstractEntry> {
   private static final int BIG_BUTTON_WIDTH = 310;
   private static final int DEFAULT_ITEM_HEIGHT = 25;
   private final OptionsSubScreen screen;

   public OptionsList(Minecraft var1, int var2, OptionsSubScreen var3) {
      super(var1, var2, var3.layout.getContentHeight(), var3.layout.getHeaderHeight(), 25);
      this.centerListVertically = false;
      this.screen = var3;
   }

   public void addBig(OptionInstance<?> var1) {
      this.addEntry(OptionsList.OptionEntry.big(this.minecraft.options, var1, this.screen));
   }

   public void addSmall(OptionInstance<?>... var1) {
      for(int var2 = 0; var2 < var1.length; var2 += 2) {
         OptionInstance var3 = var2 < var1.length - 1 ? var1[var2 + 1] : null;
         this.addEntry(OptionsList.OptionEntry.small(this.minecraft.options, var1[var2], var3, this.screen));
      }

   }

   public void addSmall(List<AbstractWidget> var1) {
      for(int var2 = 0; var2 < var1.size(); var2 += 2) {
         this.addSmall((AbstractWidget)var1.get(var2), var2 < var1.size() - 1 ? (AbstractWidget)var1.get(var2 + 1) : null);
      }

   }

   public void addSmall(AbstractWidget var1, @Nullable AbstractWidget var2) {
      this.addEntry(OptionsList.Entry.small(var1, var2, this.screen));
   }

   public void addHeader(Component var1) {
      Objects.requireNonNull(this.minecraft.font);
      byte var2 = 9;
      int var3 = this.children().isEmpty() ? 0 : var2 * 2;
      this.addEntry(new HeaderEntry(this.screen, var1, var3), var3 + var2 + 4);
   }

   public int getRowWidth() {
      return 310;
   }

   @Nullable
   public AbstractWidget findOption(OptionInstance<?> var1) {
      for(AbstractEntry var3 : this.children()) {
         if (var3 instanceof OptionEntry var4) {
            AbstractWidget var5 = (AbstractWidget)var4.options.get(var1);
            if (var5 != null) {
               return var5;
            }
         }
      }

      return null;
   }

   public void applyUnsavedChanges() {
      for(AbstractEntry var2 : this.children()) {
         if (var2 instanceof OptionEntry var3) {
            for(AbstractWidget var5 : var3.options.values()) {
               if (var5 instanceof OptionInstance.OptionInstanceSliderButton var6) {
                  var6.applyUnsavedValue();
               }
            }
         }
      }

   }

   public void resetOption(OptionInstance<?> var1) {
      for(AbstractEntry var3 : this.children()) {
         if (var3 instanceof OptionEntry var4) {
            AbstractWidget var5 = (AbstractWidget)var4.options.get(var1);
            if (var5 instanceof ResettableOptionWidget var6) {
               var6.resetValue();
            }
         }
      }

   }

   protected abstract static class AbstractEntry extends ContainerObjectSelectionList.Entry<AbstractEntry> {
      protected AbstractEntry() {
         super();
      }
   }

   protected static class HeaderEntry extends AbstractEntry {
      private final Screen screen;
      private final int paddingTop;
      private final StringWidget widget;

      protected HeaderEntry(Screen var1, Component var2, int var3) {
         super();
         this.screen = var1;
         this.paddingTop = var3;
         this.widget = new StringWidget(var2, var1.getFont());
      }

      public List<? extends NarratableEntry> narratables() {
         return List.of(this.widget);
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         this.widget.setPosition(this.screen.width / 2 - 155, this.getContentY() + this.paddingTop);
         this.widget.render(var1, var2, var3, var5);
      }

      public List<? extends GuiEventListener> children() {
         return List.of(this.widget);
      }
   }

   protected static class Entry extends AbstractEntry {
      private final List<AbstractWidget> children;
      private final Screen screen;
      private static final int X_OFFSET = 160;

      Entry(List<AbstractWidget> var1, Screen var2) {
         super();
         this.children = ImmutableList.copyOf(var1);
         this.screen = var2;
      }

      public static Entry big(List<AbstractWidget> var0, Screen var1) {
         return new Entry(var0, var1);
      }

      public static Entry small(AbstractWidget var0, @Nullable AbstractWidget var1, Screen var2) {
         return var1 == null ? new Entry(ImmutableList.of(var0), var2) : new Entry(ImmutableList.of(var0, var1), var2);
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         int var6 = 0;
         int var7 = this.screen.width / 2 - 155;

         for(AbstractWidget var9 : this.children) {
            var9.setPosition(var7 + var6, this.getContentY());
            var9.render(var1, var2, var3, var5);
            var6 += 160;
         }

      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }
   }

   protected static class OptionEntry extends Entry {
      final Map<OptionInstance<?>, AbstractWidget> options;

      private OptionEntry(Map<OptionInstance<?>, AbstractWidget> var1, OptionsSubScreen var2) {
         super(ImmutableList.copyOf(var1.values()), var2);
         this.options = var1;
      }

      public static OptionEntry big(Options var0, OptionInstance<?> var1, OptionsSubScreen var2) {
         return new OptionEntry(ImmutableMap.of(var1, var1.createButton(var0, 0, 0, 310)), var2);
      }

      public static OptionEntry small(Options var0, OptionInstance<?> var1, @Nullable OptionInstance<?> var2, OptionsSubScreen var3) {
         AbstractWidget var4 = var1.createButton(var0);
         return var2 == null ? new OptionEntry(ImmutableMap.of(var1, var4), var3) : new OptionEntry(ImmutableMap.of(var1, var4, var2, var2.createButton(var0)), var3);
      }
   }
}
