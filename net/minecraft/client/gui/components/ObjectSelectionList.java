package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;

public abstract class ObjectSelectionList extends AbstractSelectionList {
   private boolean inFocus;

   public ObjectSelectionList(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public boolean changeFocus(boolean var1) {
      if (!this.inFocus && this.getItemCount() == 0) {
         return false;
      } else {
         this.inFocus = !this.inFocus;
         if (this.inFocus && this.getSelected() == null && this.getItemCount() > 0) {
            this.moveSelection(1);
         } else if (this.inFocus && this.getSelected() != null) {
            this.moveSelection(0);
         }

         return this.inFocus;
      }
   }

   public abstract static class Entry extends AbstractSelectionList.Entry {
      public boolean changeFocus(boolean var1) {
         return false;
      }
   }
}
