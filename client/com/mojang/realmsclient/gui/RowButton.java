package com.mojang.realmsclient.gui;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.realms.RealmsObjectSelectionList;

public abstract class RowButton {
   public final int width;
   public final int height;
   public final int xOffset;
   public final int yOffset;

   public RowButton(int var1, int var2, int var3, int var4) {
      super();
      this.width = var1;
      this.height = var2;
      this.xOffset = var3;
      this.yOffset = var4;
   }

   public void drawForRowAt(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      int var6 = var2 + this.xOffset;
      int var7 = var3 + this.yOffset;
      boolean var8 = var4 >= var6 && var4 <= var6 + this.width && var5 >= var7 && var5 <= var7 + this.height;
      this.draw(var1, var6, var7, var8);
   }

   protected abstract void draw(GuiGraphics var1, int var2, int var3, boolean var4);

   public int getRight() {
      return this.xOffset + this.width;
   }

   public int getBottom() {
      return this.yOffset + this.height;
   }

   public abstract void onClick(int var1);

   public static void drawButtonsInRow(GuiGraphics var0, List<RowButton> var1, RealmsObjectSelectionList<?> var2, int var3, int var4, int var5, int var6) {
      Iterator var7 = var1.iterator();

      while(var7.hasNext()) {
         RowButton var8 = (RowButton)var7.next();
         if (var2.getRowWidth() > var8.getRight()) {
            var8.drawForRowAt(var0, var3, var4, var5, var6);
         }
      }

   }

   public static void rowButtonMouseClicked(RealmsObjectSelectionList<?> var0, ObjectSelectionList.Entry<?> var1, List<RowButton> var2, int var3, double var4, double var6) {
      int var8 = var0.children().indexOf(var1);
      if (var8 > -1) {
         var0.selectItem(var8);
         int var9 = var0.getRowLeft();
         int var10 = var0.getRowTop(var8);
         int var11 = (int)(var4 - (double)var9);
         int var12 = (int)(var6 - (double)var10);
         Iterator var13 = var2.iterator();

         while(var13.hasNext()) {
            RowButton var14 = (RowButton)var13.next();
            if (var11 >= var14.xOffset && var11 <= var14.getRight() && var12 >= var14.yOffset && var12 <= var14.getBottom()) {
               var14.onClick(var8);
            }
         }
      }

   }
}
