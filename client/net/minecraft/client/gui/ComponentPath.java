package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

public interface ComponentPath {
   static ComponentPath leaf(GuiEventListener var0) {
      return new ComponentPath.Leaf(var0);
   }

   @Nullable
   static ComponentPath path(ContainerEventHandler var0, @Nullable ComponentPath var1) {
      return var1 == null ? null : new ComponentPath.Path(var0, var1);
   }

   static ComponentPath path(GuiEventListener var0, ContainerEventHandler... var1) {
      ComponentPath var2 = leaf(var0);

      for(ContainerEventHandler var6 : var1) {
         var2 = path(var6, var2);
      }

      return var2;
   }

   GuiEventListener component();

   void applyFocus(boolean var1);

   public static record Leaf(GuiEventListener a) implements ComponentPath {
      private final GuiEventListener component;

      public Leaf(GuiEventListener var1) {
         super();
         this.component = var1;
      }

      @Override
      public void applyFocus(boolean var1) {
         this.component.setFocused(var1);
      }
   }

   public static record Path(ContainerEventHandler a, ComponentPath b) implements ComponentPath {
      private final ContainerEventHandler component;
      private final ComponentPath childPath;

      public Path(ContainerEventHandler var1, ComponentPath var2) {
         super();
         this.component = var1;
         this.childPath = var2;
      }

      @Override
      public void applyFocus(boolean var1) {
         if (!var1) {
            this.component.setFocused(null);
         } else {
            this.component.setFocused(this.childPath.component());
         }

         this.childPath.applyFocus(var1);
      }
   }
}
