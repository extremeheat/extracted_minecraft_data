package net.minecraft.client.gui.components;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.events.GuiEventListener;

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

   public static class Entry extends ContainerObjectSelectionList.Entry<OptionsList.Entry> {
      private final List<AbstractWidget> children;

      private Entry(List<AbstractWidget> var1) {
         super();
         this.children = var1;
      }

      public static OptionsList.Entry big(Options var0, int var1, Option var2) {
         return new OptionsList.Entry(ImmutableList.of(var2.createButton(var0, var1 / 2 - 155, 0, 310)));
      }

      public static OptionsList.Entry small(Options var0, int var1, Option var2, @Nullable Option var3) {
         AbstractWidget var4 = var2.createButton(var0, var1 / 2 - 155, 0, 150);
         return var3 == null ? new OptionsList.Entry(ImmutableList.of(var4)) : new OptionsList.Entry(ImmutableList.of(var4, var3.createButton(var0, var1 / 2 - 155 + 160, 0, 150)));
      }

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         this.children.forEach((var4x) -> {
            var4x.y = var2;
            var4x.render(var6, var7, var9);
         });
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }
   }
}
