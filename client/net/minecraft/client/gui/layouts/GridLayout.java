package net.minecraft.client.gui.layouts;

import com.mojang.math.Divisor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

public class GridLayout extends AbstractLayout {
   private final List<ChildContainer> children;
   private final LayoutSettings defaultCellSettings;
   private int rowSpacing;
   private int columnSpacing;

   public GridLayout() {
      this(0, 0);
   }

   public GridLayout(final int x, final int y) {
      super(x, y, 0, 0);
      this.children = new ArrayList();
      this.defaultCellSettings = LayoutSettings.defaults();
      this.rowSpacing = 0;
      this.columnSpacing = 0;
   }

   public void arrangeElements() {
      super.arrangeElements();
      int maxRow = 0;
      int maxColumn = 0;

      for(ChildContainer child : this.children) {
         maxRow = Math.max(child.getLastOccupiedRow(), maxRow);
         maxColumn = Math.max(child.getLastOccupiedColumn(), maxColumn);
      }

      int[] maxColumnWidths = new int[maxColumn + 1];
      int[] maxRowHeights = new int[maxRow + 1];

      for(ChildContainer child : this.children) {
         int childHeight = child.getHeight() - (child.occupiedRows - 1) * this.rowSpacing;
         Divisor heightDivisor = new Divisor(childHeight, child.occupiedRows);

         for(int row = child.row; row <= child.getLastOccupiedRow(); ++row) {
            maxRowHeights[row] = Math.max(maxRowHeights[row], heightDivisor.nextInt());
         }

         int childWidth = child.getWidth() - (child.occupiedColumns - 1) * this.columnSpacing;
         Divisor widthDivisor = new Divisor(childWidth, child.occupiedColumns);

         for(int column = child.column; column <= child.getLastOccupiedColumn(); ++column) {
            maxColumnWidths[column] = Math.max(maxColumnWidths[column], widthDivisor.nextInt());
         }
      }

      int[] columnXOffsets = new int[maxColumn + 1];
      int[] rowYOffsets = new int[maxRow + 1];
      columnXOffsets[0] = 0;

      for(int column = 1; column <= maxColumn; ++column) {
         columnXOffsets[column] = columnXOffsets[column - 1] + maxColumnWidths[column - 1] + this.columnSpacing;
      }

      rowYOffsets[0] = 0;

      for(int row = 1; row <= maxRow; ++row) {
         rowYOffsets[row] = rowYOffsets[row - 1] + maxRowHeights[row - 1] + this.rowSpacing;
      }

      for(ChildContainer child : this.children) {
         int availableWidth = 0;

         for(int column = child.column; column <= child.getLastOccupiedColumn(); ++column) {
            availableWidth += maxColumnWidths[column];
         }

         availableWidth += this.columnSpacing * (child.occupiedColumns - 1);
         child.setX(this.getX() + columnXOffsets[child.column], availableWidth);
         int availableHeight = 0;

         for(int row = child.row; row <= child.getLastOccupiedRow(); ++row) {
            availableHeight += maxRowHeights[row];
         }

         availableHeight += this.rowSpacing * (child.occupiedRows - 1);
         child.setY(this.getY() + rowYOffsets[child.row], availableHeight);
      }

      this.width = columnXOffsets[maxColumn] + maxColumnWidths[maxColumn];
      this.height = rowYOffsets[maxRow] + maxRowHeights[maxRow];
   }

   public <T extends LayoutElement> T addChild(final T child, final int row, final int column) {
      return (T)this.addChild(child, row, column, this.newCellSettings());
   }

   public <T extends LayoutElement> T addChild(final T child, final int row, final int column, final LayoutSettings cellSettings) {
      return (T)this.addChild(child, row, column, 1, 1, cellSettings);
   }

   public <T extends LayoutElement> T addChild(final T child, final int row, final int column, final Consumer<LayoutSettings> layoutSettingsAdjustments) {
      return (T)this.addChild(child, row, column, 1, 1, (LayoutSettings)Util.make(this.newCellSettings(), layoutSettingsAdjustments));
   }

   public <T extends LayoutElement> T addChild(final T child, final int row, final int column, final int rows, final int columns) {
      return (T)this.addChild(child, row, column, rows, columns, this.newCellSettings());
   }

   public <T extends LayoutElement> T addChild(final T child, final int row, final int column, final int rows, final int columns, final LayoutSettings cellSettings) {
      if (rows < 1) {
         throw new IllegalArgumentException("Occupied rows must be at least 1");
      } else if (columns < 1) {
         throw new IllegalArgumentException("Occupied columns must be at least 1");
      } else {
         this.children.add(new ChildContainer(child, row, column, rows, columns, cellSettings));
         return child;
      }
   }

   public <T extends LayoutElement> T addChild(final T child, final int row, final int column, final int rows, final int columns, final Consumer<LayoutSettings> layoutSettingsAdjustments) {
      return (T)this.addChild(child, row, column, rows, columns, (LayoutSettings)Util.make(this.newCellSettings(), layoutSettingsAdjustments));
   }

   public GridLayout columnSpacing(final int columnSpacing) {
      this.columnSpacing = columnSpacing;
      return this;
   }

   public GridLayout rowSpacing(final int rowSpacing) {
      this.rowSpacing = rowSpacing;
      return this;
   }

   public GridLayout spacing(final int spacing) {
      return this.columnSpacing(spacing).rowSpacing(spacing);
   }

   public void visitChildren(final Consumer<LayoutElement> layoutElementVisitor) {
      this.children.forEach((child) -> layoutElementVisitor.accept(child.child));
   }

   public LayoutSettings newCellSettings() {
      return this.defaultCellSettings.copy();
   }

   public LayoutSettings defaultCellSetting() {
      return this.defaultCellSettings;
   }

   public RowHelper createRowHelper(final int columns) {
      return new RowHelper(columns);
   }

   private static class ChildContainer extends AbstractLayout.AbstractChildWrapper {
      private final int row;
      private final int column;
      private final int occupiedRows;
      private final int occupiedColumns;

      private ChildContainer(final LayoutElement widget, final int row, final int column, final int occupiedRows, final int occupiedColumns, final LayoutSettings cellSettings) {
         super(widget, cellSettings.getExposed());
         this.row = row;
         this.column = column;
         this.occupiedRows = occupiedRows;
         this.occupiedColumns = occupiedColumns;
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

      private RowHelper(final int columns) {
         Objects.requireNonNull(GridLayout.this);
         super();
         this.columns = columns;
      }

      public <T extends LayoutElement> T addChild(final T widget) {
         return (T)this.addChild(widget, 1);
      }

      public <T extends LayoutElement> T addChild(final T widget, final int columnWidth) {
         return (T)this.addChild(widget, columnWidth, this.defaultCellSetting());
      }

      public <T extends LayoutElement> T addChild(final T widget, final LayoutSettings layoutSettings) {
         return (T)this.addChild(widget, 1, layoutSettings);
      }

      public <T extends LayoutElement> T addChild(final T widget, final int columnWidth, final LayoutSettings layoutSettings) {
         int row = this.index / this.columns;
         int columnBegin = this.index % this.columns;
         if (columnBegin + columnWidth > this.columns) {
            ++row;
            columnBegin = 0;
            this.index = Mth.roundToward(this.index, this.columns);
         }

         this.index += columnWidth;
         return (T)GridLayout.this.addChild(widget, row, columnBegin, 1, columnWidth, layoutSettings);
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
