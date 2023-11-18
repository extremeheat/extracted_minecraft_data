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

public class OptionsList extends ContainerObjectSelectionList<OptionsList.Entry> {
   public OptionsList(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.centerListVertically = false;
   }

   public int addBig(OptionInstance<?> var1) {
      return this.addEntry(OptionsList.Entry.big(this.minecraft.options, this.width, var1));
   }

   public void addSmall(OptionInstance<?> var1, @Nullable OptionInstance<?> var2) {
      this.addEntry(OptionsList.Entry.small(this.minecraft.options, this.width, var1, var2));
   }

   public void addSmall(OptionInstance<?>[] var1) {
      for(int var2 = 0; var2 < var1.length; var2 += 2) {
         this.addSmall(var1[var2], var2 < var1.length - 1 ? var1[var2 + 1] : null);
      }
   }

   @Override
   public int getRowWidth() {
      return 400;
   }

   @Override
   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 32;
   }

   @Nullable
   public AbstractWidget findOption(OptionInstance<?> var1) {
      for(OptionsList.Entry var3 : this.children()) {
         AbstractWidget var4 = var3.options.get(var1);
         if (var4 != null) {
            return var4;
         }
      }

      return null;
   }

   public Optional<AbstractWidget> getMouseOver(double var1, double var3) {
      for(OptionsList.Entry var6 : this.children()) {
         for(AbstractWidget var8 : var6.children) {
            if (var8.isMouseOver(var1, var3)) {
               return Optional.of(var8);
            }
         }
      }

      return Optional.empty();
   }

   protected static class Entry extends ContainerObjectSelectionList.Entry<OptionsList.Entry> {
      final Map<OptionInstance<?>, AbstractWidget> options;
      final List<AbstractWidget> children;

      private Entry(Map<OptionInstance<?>, AbstractWidget> var1) {
         super();
         this.options = var1;
         this.children = ImmutableList.copyOf(var1.values());
      }

      public static OptionsList.Entry big(Options var0, int var1, OptionInstance<?> var2) {
         return new OptionsList.Entry(ImmutableMap.of(var2, var2.createButton(var0, var1 / 2 - 155, 0, 310)));
      }

      public static OptionsList.Entry small(Options var0, int var1, OptionInstance<?> var2, @Nullable OptionInstance<?> var3) {
         AbstractWidget var4 = var2.createButton(var0, var1 / 2 - 155, 0, 150);
         return var3 == null
            ? new OptionsList.Entry(ImmutableMap.of(var2, var4))
            : new OptionsList.Entry(ImmutableMap.of(var2, var4, var3, var3.createButton(var0, var1 / 2 - 155 + 160, 0, 150)));
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.children.forEach(var5x -> {
            var5x.setY(var3);
            var5x.render(var1, var7, var8, var10);
         });
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
}
