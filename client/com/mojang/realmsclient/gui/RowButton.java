package com.mojang.realmsclient.gui;

import java.util.Iterator;
import java.util.List;
import net.minecraft.realms.RealmListEntry;
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

   public void drawForRowAt(int var1, int var2, int var3, int var4) {
      int var5 = var1 + this.xOffset;
      int var6 = var2 + this.yOffset;
      boolean var7 = false;
      if (var3 >= var5 && var3 <= var5 + this.width && var4 >= var6 && var4 <= var6 + this.height) {
         var7 = true;
      }

      this.draw(var5, var6, var7);
   }

   protected abstract void draw(int var1, int var2, boolean var3);

   public int getRight() {
      return this.xOffset + this.width;
   }

   public int getBottom() {
      return this.yOffset + this.height;
   }

   public abstract void onClick(int var1);

   public static void drawButtonsInRow(List<RowButton> var0, RealmsObjectSelectionList var1, int var2, int var3, int var4, int var5) {
      Iterator var6 = var0.iterator();

      while(var6.hasNext()) {
         RowButton var7 = (RowButton)var6.next();
         if (var1.getRowWidth() > var7.getRight()) {
            var7.drawForRowAt(var2, var3, var4, var5);
         }
      }

   }

   public static void rowButtonMouseClicked(RealmsObjectSelectionList var0, RealmListEntry var1, List<RowButton> var2, int var3, double var4, double var6) {
      if (var3 == 0) {
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
}
