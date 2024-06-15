package net.minecraft.client.gui.layouts;

import com.mojang.math.Divisor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.util.Mth;

public class GridLayout extends AbstractLayout {
   private final List<LayoutElement> children = new ArrayList<>();
   private final List<GridLayout.CellInhabitant> cellInhabitants = new ArrayList<>();
   private final LayoutSettings defaultCellSettings = LayoutSettings.defaults();
   private int rowSpacing = 0;
   private int columnSpacing = 0;

   public GridLayout() {
      this(0, 0);
   }

   public GridLayout(int var1, int var2) {
      super(var1, var2, 0, 0);
   }

   @Override
   public void arrangeElements() {
      super.arrangeElements();
      int var1 = 0;
      int var2 = 0;

      for (GridLayout.CellInhabitant var4 : this.cellInhabitants) {
         var1 = Math.max(var4.getLastOccupiedRow(), var1);
         var2 = Math.max(var4.getLastOccupiedColumn(), var2);
      }

      int[] var12 = new int[var2 + 1];
      int[] var13 = new int[var1 + 1];

      for (GridLayout.CellInhabitant var6 : this.cellInhabitants) {
         int var7 = var6.getHeight() - (var6.occupiedRows - 1) * this.rowSpacing;
         Divisor var8 = new Divisor(var7, var6.occupiedRows);

         for (int var9 = var6.row; var9 <= var6.getLastOccupiedRow(); var9++) {
            var13[var9] = Math.max(var13[var9], var8.nextInt());
         }

         int var20 = var6.getWidth() - (var6.occupiedColumns - 1) * this.columnSpacing;
         Divisor var10 = new Divisor(var20, var6.occupiedColumns);

         for (int var11 = var6.column; var11 <= var6.getLastOccupiedColumn(); var11++) {
            var12[var11] = Math.max(var12[var11], var10.nextInt());
         }
      }

      int[] var14 = new int[var2 + 1];
      int[] var15 = new int[var1 + 1];
      var14[0] = 0;

      for (int var16 = 1; var16 <= var2; var16++) {
         var14[var16] = var14[var16 - 1] + var12[var16 - 1] + this.columnSpacing;
      }

      var15[0] = 0;

      for (int var17 = 1; var17 <= var1; var17++) {
         var15[var17] = var15[var17 - 1] + var13[var17 - 1] + this.rowSpacing;
      }

      for (GridLayout.CellInhabitant var19 : this.cellInhabitants) {
         int var21 = 0;

         for (int var23 = var19.column; var23 <= var19.getLastOccupiedColumn(); var23++) {
            var21 += var12[var23];
         }

         var21 += this.columnSpacing * (var19.occupiedColumns - 1);
         var19.setX(this.getX() + var14[var19.column], var21);
         int var24 = 0;

         for (int var26 = var19.row; var26 <= var19.getLastOccupiedRow(); var26++) {
            var24 += var13[var26];
         }

         var24 += this.rowSpacing * (var19.occupiedRows - 1);
         var19.setY(this.getY() + var15[var19.row], var24);
      }

      this.width = var14[var2] + var12[var2];
      this.height = var15[var1] + var13[var1];
   }

   public <T extends LayoutElement> T addChild(T var1, int var2, int var3) {
      return this.addChild((T)var1, var2, var3, this.newCellSettings());
   }

   public <T extends LayoutElement> T addChild(T var1, int var2, int var3, LayoutSettings var4) {
      return this.addChild((T)var1, var2, var3, 1, 1, var4);
   }

   public <T extends LayoutElement> T addChild(T var1, int var2, int var3, Consumer<LayoutSettings> var4) {
      return this.addChild((T)var1, var2, var3, 1, 1, Util.make(this.newCellSettings(), var4));
   }

   public <T extends LayoutElement> T addChild(T var1, int var2, int var3, int var4, int var5) {
      return this.addChild((T)var1, var2, var3, var4, var5, this.newCellSettings());
   }

   public <T extends LayoutElement> T addChild(T var1, int var2, int var3, int var4, int var5, LayoutSettings var6) {
      if (var4 < 1) {
         throw new IllegalArgumentException("Occupied rows must be at least 1");
      } else if (var5 < 1) {
         throw new IllegalArgumentException("Occupied columns must be at least 1");
      } else {
         this.cellInhabitants.add(new GridLayout.CellInhabitant(var1, var2, var3, var4, var5, var6));
         this.children.add(var1);
         return (T)var1;
      }
   }

   public <T extends LayoutElement> T addChild(T var1, int var2, int var3, int var4, int var5, Consumer<LayoutSettings> var6) {
      return this.addChild((T)var1, var2, var3, var4, var5, Util.make(this.newCellSettings(), var6));
   }

   public GridLayout columnSpacing(int var1) {
      this.columnSpacing = var1;
      return this;
   }

   public GridLayout rowSpacing(int var1) {
      this.rowSpacing = var1;
      return this;
   }

   public GridLayout spacing(int var1) {
      return this.columnSpacing(var1).rowSpacing(var1);
   }

   @Override
   public void visitChildren(Consumer<LayoutElement> var1) {
      this.children.forEach(var1);
   }

   public LayoutSettings newCellSettings() {
      return this.defaultCellSettings.copy();
   }

   public LayoutSettings defaultCellSetting() {
      return this.defaultCellSettings;
   }

   public GridLayout.RowHelper createRowHelper(int var1) {
      return new GridLayout.RowHelper(var1);
   }

   static class CellInhabitant extends AbstractLayout.AbstractChildWrapper {
      final int row;
      final int column;
      final int occupiedRows;
      final int occupiedColumns;

      CellInhabitant(LayoutElement var1, int var2, int var3, int var4, int var5, LayoutSettings var6) {
         super(var1, var6.getExposed());
         this.row = var2;
         this.column = var3;
         this.occupiedRows = var4;
         this.occupiedColumns = var5;
      }

      public int getLastOccupiedRow() {
         return this.row + this.occupiedRows - 1;
      }

      public int getLastOccupiedColumn() {
         return this.column + this.occupiedColumns - 1;
      }
   }

   public final class RowHelper {
      private final int columns;
      private int index;

      RowHelper(final int nullx) {
         super();
         this.columns = nullx;
      }

      public <T extends LayoutElement> T addChild(T var1) {
         return this.addChild((T)var1, 1);
      }

      public <T extends LayoutElement> T addChild(T var1, int var2) {
         return this.addChild((T)var1, var2, this.defaultCellSetting());
      }

      public <T extends LayoutElement> T addChild(T var1, LayoutSettings var2) {
         return this.addChild((T)var1, 1, var2);
      }

      public <T extends LayoutElement> T addChild(T var1, int var2, LayoutSettings var3) {
         int var4 = this.index / this.columns;
         int var5 = this.index % this.columns;
         if (var5 + var2 > this.columns) {
            var4++;
            var5 = 0;
            this.index = Mth.roundToward(this.index, this.columns);
         }

         this.index += var2;
         return GridLayout.this.addChild((T)var1, var4, var5, 1, var2, var3);
      }

      public GridLayout getGrid() {
         return GridLayout.this;
      }

      public LayoutSettings newCellSettings() {
         return GridLayout.this.newCellSettings();
      }

      public LayoutSettings defaultCellSetting() {
         return GridLayout.this.defaultCellSetting();
      }
   }
}
