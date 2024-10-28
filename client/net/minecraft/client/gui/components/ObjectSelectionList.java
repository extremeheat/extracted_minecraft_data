package net.minecraft.client.gui.components;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;

public abstract class ObjectSelectionList<E extends Entry<E>> extends AbstractSelectionList<E> {
   private static final Component USAGE_NARRATION = Component.translatable("narration.selection.usage");

   public ObjectSelectionList(Minecraft var1, int var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent var1) {
      if (this.getItemCount() == 0) {
         return null;
      } else if (this.isFocused() && var1 instanceof FocusNavigationEvent.ArrowNavigation) {
         FocusNavigationEvent.ArrowNavigation var4 = (FocusNavigationEvent.ArrowNavigation)var1;
         Entry var3 = (Entry)this.nextEntry(var4.direction());
         return var3 != null ? ComponentPath.path((ContainerEventHandler)this, (ComponentPath)ComponentPath.leaf(var3)) : null;
      } else if (!this.isFocused()) {
         Entry var2 = (Entry)this.getSelected();
         if (var2 == null) {
            var2 = (Entry)this.nextEntry(var1.getVerticalDirectionForInitialFocus());
         }

         return var2 == null ? null : ComponentPath.path((ContainerEventHandler)this, (ComponentPath)ComponentPath.leaf(var2));
      } else {
         return null;
      }
   }

   public void updateWidgetNarration(NarrationElementOutput var1) {
      Entry var2 = (Entry)this.getHovered();
      if (var2 != null) {
         this.narrateListElementPosition(var1.nest(), var2);
         var2.updateNarration(var1);
      } else {
         Entry var3 = (Entry)this.getSelected();
         if (var3 != null) {
            this.narrateListElementPosition(var1.nest(), var3);
            var3.updateNarration(var1);
         }
      }

      if (this.isFocused()) {
         var1.add(NarratedElementType.USAGE, USAGE_NARRATION);
      }

   }

   public abstract static class Entry<E extends Entry<E>> extends AbstractSelectionList.Entry<E> implements NarrationSupplier {
      public Entry() {
         super();
      }

      public abstract Component getNarration();

      public boolean mouseClicked(double var1, double var3, int var5) {
         return true;
      }

      public void updateNarration(NarrationElementOutput var1) {
         var1.add(NarratedElementType.TITLE, this.getNarration());
      }
   }
}
