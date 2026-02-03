package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public abstract class ObjectSelectionList<E extends ObjectSelectionList.Entry<E>> extends AbstractSelectionList<E> {
   private static final Component USAGE_NARRATION = Component.translatable("narration.selection.usage");

   public ObjectSelectionList(final Minecraft minecraft, final int width, final int height, final int y, final int itemHeight) {
      super(minecraft, width, height, y, itemHeight);
   }

   public @Nullable ComponentPath nextFocusPath(final FocusNavigationEvent navigationEvent) {
      if (this.getItemCount() == 0) {
         return null;
      } else if (this.isFocused() && navigationEvent instanceof FocusNavigationEvent.ArrowNavigation) {
         FocusNavigationEvent.ArrowNavigation arrowNavigation = (FocusNavigationEvent.ArrowNavigation)navigationEvent;
         E entry = (E)(this.nextEntry(arrowNavigation.direction()));
         if (entry != null) {
            return ComponentPath.path((ContainerEventHandler)this, (ComponentPath)ComponentPath.leaf(entry));
         } else {
            this.setFocused((GuiEventListener)null);
            this.setSelected((AbstractSelectionList.Entry)null);
            return null;
         }
      } else if (!this.isFocused()) {
         E entry = (E)(this.getSelected());
         if (entry == null) {
            entry = (E)(this.nextEntry(navigationEvent.getVerticalDirectionForInitialFocus()));
         }

         return entry == null ? null : ComponentPath.path((ContainerEventHandler)this, (ComponentPath)ComponentPath.leaf(entry));
      } else {
         return null;
      }
   }

   public void updateWidgetNarration(final NarrationElementOutput output) {
      E hovered = (E)(this.getHovered());
      if (hovered != null) {
         this.narrateListElementPosition(output.nest(), hovered);
         hovered.updateNarration(output);
      } else {
         E selected = (E)(this.getSelected());
         if (selected != null) {
            this.narrateListElementPosition(output.nest(), selected);
            selected.updateNarration(output);
         }
      }

      if (this.isFocused()) {
         output.add(NarratedElementType.USAGE, USAGE_NARRATION);
      }

   }

   public abstract static class Entry<E extends Entry<E>> extends AbstractSelectionList.Entry<E> implements NarrationSupplier {
      public Entry() {
         super();
      }

      public abstract Component getNarration();

      public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
         return true;
      }

      public void updateNarration(final NarrationElementOutput output) {
         output.add(NarratedElementType.TITLE, this.getNarration());
      }
   }
}
