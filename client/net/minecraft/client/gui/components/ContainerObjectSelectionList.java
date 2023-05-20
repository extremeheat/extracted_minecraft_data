package net.minecraft.client.gui.components;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public abstract class ContainerObjectSelectionList<E extends ContainerObjectSelectionList.Entry<E>> extends AbstractSelectionList<E> {
   public ContainerObjectSelectionList(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   @Nullable
   @Override
   public ComponentPath nextFocusPath(FocusNavigationEvent var1) {
      if (this.getItemCount() == 0) {
         return null;
      } else if (!(var1 instanceof FocusNavigationEvent.ArrowNavigation)) {
         return super.nextFocusPath(var1);
      } else {
         FocusNavigationEvent.ArrowNavigation var2 = (FocusNavigationEvent.ArrowNavigation)var1;
         ContainerObjectSelectionList.Entry var3 = this.getFocused();
         if (var2.direction().getAxis() == ScreenAxis.HORIZONTAL && var3 != null) {
            return ComponentPath.path(this, var3.nextFocusPath(var1));
         } else {
            int var4 = -1;
            ScreenDirection var5 = var2.direction();
            if (var3 != null) {
               var4 = var3.children().indexOf(var3.getFocused());
            }

            if (var4 == -1) {
               switch(var5) {
                  case LEFT:
                     var4 = 2147483647;
                     var5 = ScreenDirection.DOWN;
                     break;
                  case RIGHT:
                     var4 = 0;
                     var5 = ScreenDirection.DOWN;
                     break;
                  default:
                     var4 = 0;
               }
            }

            ContainerObjectSelectionList.Entry var6 = var3;

            ComponentPath var7;
            do {
               var6 = this.nextEntry(var5, var0 -> !var0.children().isEmpty(), (E)var6);
               if (var6 == null) {
                  return null;
               }

               var7 = var6.focusPathAtIndex(var2, var4);
            } while(var7 == null);

            return ComponentPath.path(this, var7);
         }
      }
   }

   @Override
   public void setFocused(@Nullable GuiEventListener var1) {
      super.setFocused(var1);
      if (var1 == null) {
         this.setSelected((E)null);
      }
   }

   @Override
   public NarratableEntry.NarrationPriority narrationPriority() {
      return this.isFocused() ? NarratableEntry.NarrationPriority.FOCUSED : super.narrationPriority();
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
      public boolean mouseClicked(double var1, double var3, int var5) {
         return ContainerEventHandler.super.mouseClicked(var1, var3, var5);
      }

      @Override
      public void setFocused(@Nullable GuiEventListener var1) {
         if (this.focused != null) {
            this.focused.setFocused(false);
         }

         if (var1 != null) {
            var1.setFocused(true);
         }

         this.focused = var1;
      }

      @Nullable
      @Override
      public GuiEventListener getFocused() {
         return this.focused;
      }

      @Nullable
      public ComponentPath focusPathAtIndex(FocusNavigationEvent var1, int var2) {
         if (this.children().isEmpty()) {
            return null;
         } else {
            ComponentPath var3 = this.children().get(Math.min(var2, this.children().size() - 1)).nextFocusPath(var1);
            return ComponentPath.path(this, var3);
         }
      }

      @Nullable
      @Override
      public ComponentPath nextFocusPath(FocusNavigationEvent var1) {
         if (var1 instanceof FocusNavigationEvent.ArrowNavigation var2) {
            byte var3 = switch(var2.direction()) {
               case LEFT -> -1;
               case RIGHT -> 1;
               case UP, DOWN -> 0;
            };
            if (var3 == 0) {
               return null;
            }

            int var4 = Mth.clamp(var3 + this.children().indexOf(this.getFocused()), 0, this.children().size() - 1);

            for(int var5 = var4; var5 >= 0 && var5 < this.children().size(); var5 += var3) {
               GuiEventListener var6 = this.children().get(var5);
               ComponentPath var7 = var6.nextFocusPath(var1);
               if (var7 != null) {
                  return ComponentPath.path(this, var7);
               }
            }
         }

         return ContainerEventHandler.super.nextFocusPath(var1);
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
