package net.minecraft.realms;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;

public class RealmsObjectSelectionListProxy extends ObjectSelectionList {
   private final RealmsObjectSelectionList realmsObjectSelectionList;

   public RealmsObjectSelectionListProxy(RealmsObjectSelectionList var1, int var2, int var3, int var4, int var5, int var6) {
      super(Minecraft.getInstance(), var2, var3, var4, var5, var6);
      this.realmsObjectSelectionList = var1;
   }

   public int getItemCount() {
      return super.getItemCount();
   }

   public void clear() {
      super.clearEntries();
   }

   public boolean isFocused() {
      return this.realmsObjectSelectionList.isFocused();
   }

   protected void setSelectedItem(int var1) {
      if (var1 == -1) {
         super.setSelected((AbstractSelectionList.Entry)null);
      } else if (super.getItemCount() != 0) {
         ObjectSelectionList.Entry var2 = (ObjectSelectionList.Entry)super.getEntry(var1);
         super.setSelected(var2);
      }

   }

   public void setSelected(@Nullable ObjectSelectionList.Entry var1) {
      super.setSelected(var1);
      this.realmsObjectSelectionList.selectItem(super.children().indexOf(var1));
   }

   public void renderBackground() {
      this.realmsObjectSelectionList.renderBackground();
   }

   public int getWidth() {
      return this.width;
   }

   public int getMaxPosition() {
      return this.realmsObjectSelectionList.getMaxPosition();
   }

   public int getScrollbarPosition() {
      return this.realmsObjectSelectionList.getScrollbarPosition();
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.realmsObjectSelectionList.mouseScrolled(var1, var3, var5) ? true : super.mouseScrolled(var1, var3, var5);
   }

   public int getRowWidth() {
      return this.realmsObjectSelectionList.getRowWidth();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.realmsObjectSelectionList.mouseClicked(var1, var3, var5) ? true : access$001(this, var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return this.realmsObjectSelectionList.mouseReleased(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.realmsObjectSelectionList.mouseDragged(var1, var3, var5, var6, var8) ? true : super.mouseDragged(var1, var3, var5, var6, var8);
   }

   protected final int addEntry(ObjectSelectionList.Entry var1) {
      return super.addEntry(var1);
   }

   public ObjectSelectionList.Entry remove(int var1) {
      return (ObjectSelectionList.Entry)super.remove(var1);
   }

   public boolean removeEntry(ObjectSelectionList.Entry var1) {
      return super.removeEntry(var1);
   }

   public void setScrollAmount(double var1) {
      super.setScrollAmount(var1);
   }

   public int y0() {
      return this.y0;
   }

   public int y1() {
      return this.y1;
   }

   public int headerHeight() {
      return this.headerHeight;
   }

   public int itemHeight() {
      return this.itemHeight;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return super.keyPressed(var1, var2, var3) ? true : this.realmsObjectSelectionList.keyPressed(var1, var2, var3);
   }

   public void replaceEntries(Collection var1) {
      super.replaceEntries(var1);
   }

   public int getRowTop(int var1) {
      return super.getRowTop(var1);
   }

   public int getRowLeft() {
      return super.getRowLeft();
   }

   // $FF: synthetic method
   static boolean access$001(RealmsObjectSelectionListProxy var0, double var1, double var3, int var5) {
      return var0.mouseClicked(var1, var3, var5);
   }
}
