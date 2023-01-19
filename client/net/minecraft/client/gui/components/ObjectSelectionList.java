package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.network.chat.Component;

public abstract class ObjectSelectionList<E extends ObjectSelectionList.Entry<E>> extends AbstractSelectionList<E> {
   private static final Component USAGE_NARRATION = Component.translatable("narration.selection.usage");
   private boolean inFocus;

   public ObjectSelectionList(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean changeFocus(boolean var1) {
      if (!this.inFocus && this.getItemCount() == 0) {
         return false;
      } else {
         this.inFocus = !this.inFocus;
         if (this.inFocus && this.getSelected() == null && this.getItemCount() > 0) {
            this.moveSelection(AbstractSelectionList.SelectionDirection.DOWN);
         } else if (this.inFocus && this.getSelected() != null) {
            this.refreshSelection();
         }

         return this.inFocus;
      }
   }

   @Override
   public void updateNarration(NarrationElementOutput var1) {
      ObjectSelectionList.Entry var2 = this.getHovered();
      if (var2 != null) {
         this.narrateListElementPosition(var1.nest(), (E)var2);
         var2.updateNarration(var1);
      } else {
         ObjectSelectionList.Entry var3 = this.getSelected();
         if (var3 != null) {
            this.narrateListElementPosition(var1.nest(), (E)var3);
            var3.updateNarration(var1);
         }
      }

      if (this.isFocused()) {
         var1.add(NarratedElementType.USAGE, USAGE_NARRATION);
      }
   }

   public abstract static class Entry<E extends ObjectSelectionList.Entry<E>> extends AbstractSelectionList.Entry<E> implements NarrationSupplier {
      public Entry() {
         super();
      }

      @Override
      public boolean changeFocus(boolean var1) {
         return false;
      }

      public abstract Component getNarration();

      @Override
      public void updateNarration(NarrationElementOutput var1) {
         var1.add(NarratedElementType.TITLE, this.getNarration());
      }
   }
}
