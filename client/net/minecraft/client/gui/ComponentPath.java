package net.minecraft.client.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

public interface ComponentPath {
   static ComponentPath leaf(GuiEventListener var0) {
      return new Leaf(var0);
   }

   @Nullable
   static ComponentPath path(ContainerEventHandler var0, @Nullable ComponentPath var1) {
      return var1 == null ? null : new Path(var0, var1);
   }

   static ComponentPath path(GuiEventListener var0, ContainerEventHandler... var1) {
      ComponentPath var2 = leaf(var0);
      ContainerEventHandler[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ContainerEventHandler var6 = var3[var5];
         var2 = path(var6, var2);
      }

      return var2;
   }

   GuiEventListener component();

   void applyFocus(boolean var1);

   public static record Leaf(GuiEventListener component) implements ComponentPath {
      public Leaf(GuiEventListener var1) {
         super();
         this.component = var1;
      }

      public void applyFocus(boolean var1) {
         this.component.setFocused(var1);
      }

      public GuiEventListener component() {
         return this.component;
      }
   }

   public static record Path(ContainerEventHandler component, ComponentPath childPath) implements ComponentPath {
      public Path(ContainerEventHandler var1, ComponentPath var2) {
         super();
         this.component = var1;
         this.childPath = var2;
      }

      public void applyFocus(boolean var1) {
         if (!var1) {
            this.component.setFocused((GuiEventListener)null);
         } else {
            this.component.setFocused(this.childPath.component());
         }

         this.childPath.applyFocus(var1);
      }

      public ContainerEventHandler component() {
         return this.component;
      }

      public ComponentPath childPath() {
         return this.childPath;
      }

      // $FF: synthetic method
      public GuiEventListener component() {
         return this.component();
      }
   }
}
