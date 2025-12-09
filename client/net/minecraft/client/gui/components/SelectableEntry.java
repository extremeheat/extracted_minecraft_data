package net.minecraft.client.gui.components;

public interface SelectableEntry {
   default boolean mouseOverIcon(int var1, int var2, int var3) {
      return var1 >= 0 && var1 < var3 && var2 >= 0 && var2 < var3;
   }

   default boolean mouseOverLeftHalf(int var1, int var2, int var3) {
      return var1 >= 0 && var1 < var3 / 2 && var2 >= 0 && var2 < var3;
   }

   default boolean mouseOverRightHalf(int var1, int var2, int var3) {
      return var1 >= var3 / 2 && var1 < var3 && var2 >= 0 && var2 < var3;
   }

   default boolean mouseOverTopRightQuarter(int var1, int var2, int var3) {
      return var1 >= var3 / 2 && var1 < var3 && var2 >= 0 && var2 < var3 / 2;
   }

   default boolean mouseOverBottomRightQuarter(int var1, int var2, int var3) {
      return var1 >= var3 / 2 && var1 < var3 && var2 >= var3 / 2 && var2 < var3;
   }

   default boolean mouseOverTopLeftQuarter(int var1, int var2, int var3) {
      return var1 >= 0 && var1 < var3 / 2 && var2 >= 0 && var2 < var3 / 2;
   }

   default boolean mouseOverBottomLeftQuarter(int var1, int var2, int var3) {
      return var1 >= 0 && var1 < var3 / 2 && var2 >= var3 / 2 && var2 < var3;
   }
}
