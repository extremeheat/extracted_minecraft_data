package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

public class OptionsList extends ContainerObjectSelectionList<OptionsList.Entry> {
   public OptionsList(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.centerListVertically = false;
   }

   public int addBig(Option var1) {
      return this.addEntry(OptionsList.Entry.big(this.minecraft.options, this.width, var1));
   }

   public void addSmall(Option var1, @Nullable Option var2) {
      this.addEntry(OptionsList.Entry.small(this.minecraft.options, this.width, var1, var2));
   }

   public void addSmall(Option[] var1) {
      for(int var2 = 0; var2 < var1.length; var2 += 2) {
         this.addSmall(var1[var2], var2 < var1.length - 1 ? var1[var2 + 1] : null);
      }

   }

   public int getRowWidth() {
      return 400;
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 32;
   }

   @Nullable
   public AbstractWidget findOption(Option var1) {
      Iterator var2 = this.children().iterator();

      AbstractWidget var4;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         OptionsList.Entry var3 = (OptionsList.Entry)var2.next();
         var4 = (AbstractWidget)var3.options.get(var1);
      } while(var4 == null);

      return var4;
   }

   public Optional<AbstractWidget> getMouseOver(double var1, double var3) {
      Iterator var5 = this.children().iterator();

      while(var5.hasNext()) {
         OptionsList.Entry var6 = (OptionsList.Entry)var5.next();
         Iterator var7 = var6.children.iterator();

         while(var7.hasNext()) {
            AbstractWidget var8 = (AbstractWidget)var7.next();
            if (var8.isMouseOver(var1, var3)) {
               return Optional.of(var8);
            }
         }
      }

      return Optional.empty();
   }

   protected static class Entry extends ContainerObjectSelectionList.Entry<OptionsList.Entry> {
      final Map<Option, AbstractWidget> options;
      final List<AbstractWidget> children;

      private Entry(Map<Option, AbstractWidget> var1) {
         super();
         this.options = var1;
         this.children = ImmutableList.copyOf(var1.values());
      }

      public static OptionsList.Entry big(Options var0, int var1, Option var2) {
         return new OptionsList.Entry(ImmutableMap.of(var2, var2.createButton(var0, var1 / 2 - 155, 0, 310)));
      }

      public static OptionsList.Entry small(Options var0, int var1, Option var2, @Nullable Option var3) {
         AbstractWidget var4 = var2.createButton(var0, var1 / 2 - 155, 0, 150);
         return var3 == null ? new OptionsList.Entry(ImmutableMap.of(var2, var4)) : new OptionsList.Entry(ImmutableMap.of(var2, var4, var3, var3.createButton(var0, var1 / 2 - 155 + 160, 0, 150)));
      }

      public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.children.forEach((var5x) -> {
            var5x.field_36 = var3;
            var5x.render(var1, var7, var8, var10);
         });
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }
   }
}
