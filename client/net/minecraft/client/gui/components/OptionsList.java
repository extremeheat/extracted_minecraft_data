package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
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

public class OptionsList extends ContainerObjectSelectionList<Entry> {
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

   public int getRowWidth() {
      return 310;
   }

   @Nullable
   public AbstractWidget findOption(OptionInstance<?> var1) {
      Iterator var2 = this.children().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
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
      Iterator var1 = this.children().iterator();

      while(true) {
         Entry var2;
         do {
            if (!var1.hasNext()) {
               return;
            }

            var2 = (Entry)var1.next();
         } while(!(var2 instanceof OptionEntry));

         OptionEntry var3 = (OptionEntry)var2;
         Iterator var4 = var3.options.values().iterator();

         while(var4.hasNext()) {
            AbstractWidget var5 = (AbstractWidget)var4.next();
            if (var5 instanceof OptionInstance.OptionInstanceSliderButton var6) {
               var6.applyUnsavedValue();
            }
         }
      }
   }

   public Optional<GuiEventListener> getMouseOver(double var1, double var3) {
      Iterator var5 = this.children().iterator();

      while(var5.hasNext()) {
         Entry var6 = (Entry)var5.next();
         Iterator var7 = var6.children().iterator();

         while(var7.hasNext()) {
            GuiEventListener var8 = (GuiEventListener)var7.next();
            if (var8.isMouseOver(var1, var3)) {
               return Optional.of(var8);
            }
         }
      }

      return Optional.empty();
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

   protected static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
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

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         int var11 = 0;
         int var12 = this.screen.width / 2 - 155;

         for(Iterator var13 = this.children.iterator(); var13.hasNext(); var11 += 160) {
            AbstractWidget var14 = (AbstractWidget)var13.next();
            var14.setPosition(var12 + var11, var3);
            var14.render(var1, var7, var8, var10);
         }

      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }
   }
}
