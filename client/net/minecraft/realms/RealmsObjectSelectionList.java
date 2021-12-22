package net.minecraft.realms;

import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;

public abstract class RealmsObjectSelectionList<E extends ObjectSelectionList.Entry<E>> extends ObjectSelectionList<E> {
   protected RealmsObjectSelectionList(int var1, int var2, int var3, int var4, int var5) {
      super(Minecraft.getInstance(), var1, var2, var3, var4, var5);
   }

   public void setSelectedItem(int var1) {
      if (var1 == -1) {
         this.setSelected((AbstractSelectionList.Entry)null);
      } else if (super.getItemCount() != 0) {
         this.setSelected((ObjectSelectionList.Entry)this.getEntry(var1));
      }

   }

   public void selectItem(int var1) {
      this.setSelectedItem(var1);
   }

   public void itemClicked(int var1, int var2, double var3, double var5, int var7) {
   }

   public int getMaxPosition() {
      return 0;
   }

   public int getScrollbarPosition() {
      return this.getRowLeft() + this.getRowWidth();
   }

   public int getRowWidth() {
      return (int)((double)this.width * 0.6D);
   }

   public void replaceEntries(Collection<E> var1) {
      super.replaceEntries(var1);
   }

   public int getItemCount() {
      return super.getItemCount();
   }

   public int getRowTop(int var1) {
      return super.getRowTop(var1);
   }

   public int getRowLeft() {
      return super.getRowLeft();
   }

   public int addEntry(E var1) {
      return super.addEntry(var1);
   }

   public void clear() {
      this.clearEntries();
   }

   // $FF: synthetic method
   public int addEntry(AbstractSelectionList.Entry var1) {
      return this.addEntry((ObjectSelectionList.Entry)var1);
   }
}
