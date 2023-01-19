package net.minecraft.client.gui.components.events;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public interface ContainerEventHandler extends GuiEventListener {
   List<? extends GuiEventListener> children();

   default Optional<GuiEventListener> getChildAt(double var1, double var3) {
      for(GuiEventListener var6 : this.children()) {
         if (var6.isMouseOver(var1, var3)) {
            return Optional.of(var6);
         }
      }

      return Optional.empty();
   }

   @Override
   default boolean mouseClicked(double var1, double var3, int var5) {
      for(GuiEventListener var7 : this.children()) {
         if (var7.mouseClicked(var1, var3, var5)) {
            this.setFocused(var7);
            if (var5 == 0) {
               this.setDragging(true);
            }

            return true;
         }
      }

      return false;
   }

   @Override
   default boolean mouseReleased(double var1, double var3, int var5) {
      this.setDragging(false);
      return this.getChildAt(var1, var3).filter(var5x -> var5x.mouseReleased(var1, var3, var5)).isPresent();
   }

   @Override
   default boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.getFocused() != null && this.isDragging() && var5 == 0 ? this.getFocused().mouseDragged(var1, var3, var5, var6, var8) : false;
   }

   boolean isDragging();

   void setDragging(boolean var1);

   @Override
   default boolean mouseScrolled(double var1, double var3, double var5) {
      return this.getChildAt(var1, var3).filter(var6 -> var6.mouseScrolled(var1, var3, var5)).isPresent();
   }

   @Override
   default boolean keyPressed(int var1, int var2, int var3) {
      return this.getFocused() != null && this.getFocused().keyPressed(var1, var2, var3);
   }

   @Override
   default boolean keyReleased(int var1, int var2, int var3) {
      return this.getFocused() != null && this.getFocused().keyReleased(var1, var2, var3);
   }

   @Override
   default boolean charTyped(char var1, int var2) {
      return this.getFocused() != null && this.getFocused().charTyped(var1, var2);
   }

   @Nullable
   GuiEventListener getFocused();

   void setFocused(@Nullable GuiEventListener var1);

   default void setInitialFocus(@Nullable GuiEventListener var1) {
      this.setFocused(var1);
      var1.changeFocus(true);
   }

   default void magicalSpecialHackyFocus(@Nullable GuiEventListener var1) {
      this.setFocused(var1);
   }

   @Override
   default boolean changeFocus(boolean var1) {
      GuiEventListener var2 = this.getFocused();
      boolean var3 = var2 != null;
      if (var3 && var2.changeFocus(var1)) {
         return true;
      } else {
         List var4 = this.children();
         int var6 = var4.indexOf(var2);
         int var5;
         if (var3 && var6 >= 0) {
            var5 = var6 + (var1 ? 1 : 0);
         } else if (var1) {
            var5 = 0;
         } else {
            var5 = var4.size();
         }

         ListIterator var7 = var4.listIterator(var5);
         BooleanSupplier var8 = var1 ? var7::hasNext : var7::hasPrevious;
         Supplier var9 = var1 ? var7::next : var7::previous;

         while(var8.getAsBoolean()) {
            GuiEventListener var10 = (GuiEventListener)var9.get();
            if (var10.changeFocus(var1)) {
               this.setFocused(var10);
               return true;
            }
         }

         this.setFocused(null);
         return false;
      }
   }
}