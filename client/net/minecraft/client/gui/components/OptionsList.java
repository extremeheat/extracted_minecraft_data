package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;

public class OptionsList extends ContainerObjectSelectionList<OptionsList.Entry> {
   private static final int BIG_BUTTON_WIDTH = 310;
   private static final int DEFAULT_ITEM_HEIGHT = 25;
   private final OptionsSubScreen screen;

   public OptionsList(Minecraft var1, int var2, int var3, OptionsSubScreen var4) {
      super(var1, var2, var4.layout.getContentHeight(), var4.layout.getHeaderHeight(), 25);
      this.centerListVertically = false;
      this.screen = var4;
   }

   public void addBig(OptionInstance<?> var1) {
      this.addEntry(OptionsList.OptionEntry.big(this.minecraft.options, var1, this.screen));
   }

   public void addSmall(OptionInstance<?>... var1) {
      for (byte var2 = 0; var2 < var1.length; var2 += 2) {
         OptionInstance var3 = var2 < var1.length - 1 ? var1[var2 + 1] : null;
         this.addEntry(OptionsList.OptionEntry.small(this.minecraft.options, var1[var2], var3, this.screen));
      }
   }

   public void addSmall(List<AbstractWidget> var1) {
      for (byte var2 = 0; var2 < var1.size(); var2 += 2) {
         this.addSmall((AbstractWidget)var1.get(var2), var2 < var1.size() - 1 ? (AbstractWidget)var1.get(var2 + 1) : null);
      }
   }

   public void addSmall(AbstractWidget var1, @Nullable AbstractWidget var2) {
      this.addEntry(OptionsList.Entry.small(var1, var2, this.screen));
   }

   @Override
   public int getRowWidth() {
      return 310;
   }

   @Nullable
   public AbstractWidget findOption(OptionInstance<?> var1) {
      for (OptionsList.Entry var3 : this.children()) {
         if (var3 instanceof OptionsList.OptionEntry var4) {
            AbstractWidget var5 = var4.options.get(var1);
            if (var5 != null) {
               return var5;
            }
         }
      }

      return null;
   }

   public void applyUnsavedChanges() {
      for (OptionsList.Entry var2 : this.children()) {
         if (var2 instanceof OptionsList.OptionEntry) {
            OptionsList.OptionEntry var3 = (OptionsList.OptionEntry)var2;

            for (AbstractWidget var5 : var3.options.values()) {
               if (var5 instanceof OptionInstance.OptionInstanceSliderButton var6) {
                  var6.applyUnsavedValue();
               }
            }
         }
      }
   }

   public Optional<GuiEventListener> getMouseOver(double var1, double var3) {
      for (OptionsList.Entry var6 : this.children()) {
         for (GuiEventListener var8 : var6.children()) {
            if (var8.isMouseOver(var1, var3)) {
               return Optional.of(var8);
            }
         }
      }

      return Optional.empty();
   }

   protected static class Entry extends ContainerObjectSelectionList.Entry<OptionsList.Entry> {
      private final List<AbstractWidget> children;
      private final Screen screen;
      private static final int X_OFFSET = 160;

      Entry(List<AbstractWidget> var1, Screen var2) {
         super();
         this.children = ImmutableList.copyOf(var1);
         this.screen = var2;
      }

      public static OptionsList.Entry big(List<AbstractWidget> var0, Screen var1) {
         return new OptionsList.Entry(var0, var1);
      }

      public static OptionsList.Entry small(AbstractWidget var0, @Nullable AbstractWidget var1, Screen var2) {
         return var1 == null ? new OptionsList.Entry(ImmutableList.of(var0), var2) : new OptionsList.Entry(ImmutableList.of(var0, var1), var2);
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         short var11 = 0;
         int var12 = this.screen.width / 2 - 155;

         for (AbstractWidget var14 : this.children) {
            var14.setPosition(var12 + var11, var3);
            var14.render(var1, var7, var8, var10);
            var11 += 160;
         }
      }

      @Override
      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      @Override
      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }
   }

   protected static class OptionEntry extends OptionsList.Entry {
      final Map<OptionInstance<?>, AbstractWidget> options;

      private OptionEntry(Map<OptionInstance<?>, AbstractWidget> var1, OptionsSubScreen var2) {
         super(ImmutableList.copyOf(var1.values()), var2);
         this.options = var1;
      }

      public static OptionsList.OptionEntry big(Options var0, OptionInstance<?> var1, OptionsSubScreen var2) {
         return new OptionsList.OptionEntry(ImmutableMap.of(var1, var1.createButton(var0, 0, 0, 310)), var2);
      }

      public static OptionsList.OptionEntry small(Options var0, OptionInstance<?> var1, @Nullable OptionInstance<?> var2, OptionsSubScreen var3) {
         AbstractWidget var4 = var1.createButton(var0);
         return var2 == null
            ? new OptionsList.OptionEntry(ImmutableMap.of(var1, var4), var3)
            : new OptionsList.OptionEntry(ImmutableMap.of(var1, var4, var2, var2.createButton(var0)), var3);
      }
   }
}
