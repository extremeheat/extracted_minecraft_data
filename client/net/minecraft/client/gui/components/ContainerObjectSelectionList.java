package net.minecraft.client.gui.components;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class ContainerObjectSelectionList<E extends ContainerObjectSelectionList.Entry<E>> extends AbstractSelectionList<E> {
   private boolean hasFocus;

   public ContainerObjectSelectionList(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean changeFocus(boolean var1) {
      this.hasFocus = super.changeFocus(var1);
      if (this.hasFocus) {
         this.ensureVisible(this.getFocused());
      }

      return this.hasFocus;
   }

   @Override
   public NarratableEntry.NarrationPriority narrationPriority() {
      return this.hasFocus ? NarratableEntry.NarrationPriority.FOCUSED : super.narrationPriority();
   }

   @Override
   protected boolean isSelectedItem(int var1) {
      return false;
   }

   @Override
   public void updateNarration(NarrationElementOutput var1) {
      ContainerObjectSelectionList.Entry var2 = this.getHovered();
      if (var2 != null) {
         var2.updateNarration(var1.nest());
         this.narrateListElementPosition(var1, (E)var2);
      } else {
         ContainerObjectSelectionList.Entry var3 = this.getFocused();
         if (var3 != null) {
            var3.updateNarration(var1.nest());
            this.narrateListElementPosition(var1, (E)var3);
         }
      }

      var1.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
   }

   public abstract static class Entry<E extends ContainerObjectSelectionList.Entry<E>> extends AbstractSelectionList.Entry<E> implements ContainerEventHandler {
      @Nullable
      private GuiEventListener focused;
      @Nullable
      private NarratableEntry lastNarratable;
      private boolean dragging;

      public Entry() {
         super();
      }

      @Override
      public boolean isDragging() {
         return this.dragging;
      }

      @Override
      public void setDragging(boolean var1) {
         this.dragging = var1;
      }

      @Override
      public void setFocused(@Nullable GuiEventListener var1) {
         this.focused = var1;
      }

      @Nullable
      @Override
      public GuiEventListener getFocused() {
         return this.focused;
      }

      public abstract List<? extends NarratableEntry> narratables();

      void updateNarration(NarrationElementOutput var1) {
         List var2 = this.narratables();
         Screen.NarratableSearchResult var3 = Screen.findNarratableWidget(var2, this.lastNarratable);
         if (var3 != null) {
            if (var3.priority.isTerminal()) {
               this.lastNarratable = var3.entry;
            }

            if (var2.size() > 1) {
               var1.add(NarratedElementType.POSITION, Component.translatable("narrator.position.object_list", var3.index + 1, var2.size()));
               if (var3.priority == NarratableEntry.NarrationPriority.FOCUSED) {
                  var1.add(NarratedElementType.USAGE, Component.translatable("narration.component_list.usage"));
               }
            }

            var3.entry.updateNarration(var1.nest());
         }
      }
   }
}
