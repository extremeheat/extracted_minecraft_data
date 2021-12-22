package net.minecraft.client.gui.components.events;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public interface ContainerEventHandler extends GuiEventListener {
   List<? extends GuiEventListener> children();

   default Optional<GuiEventListener> getChildAt(double var1, double var3) {
      Iterator var5 = this.children().iterator();

      GuiEventListener var6;
      do {
         if (!var5.hasNext()) {
            return Optional.empty();
         }

         var6 = (GuiEventListener)var5.next();
      } while(!var6.isMouseOver(var1, var3));

      return Optional.of(var6);
   }

   default boolean mouseClicked(double var1, double var3, int var5) {
      Iterator var6 = this.children().iterator();

      GuiEventListener var7;
      do {
         if (!var6.hasNext()) {
            return false;
         }

         var7 = (GuiEventListener)var6.next();
      } while(!var7.mouseClicked(var1, var3, var5));

      this.setFocused(var7);
      if (var5 == 0) {
         this.setDragging(true);
      }

      return true;
   }

   default boolean mouseReleased(double var1, double var3, int var5) {
      this.setDragging(false);
      return this.getChildAt(var1, var3).filter((var5x) -> {
         return var5x.mouseReleased(var1, var3, var5);
      }).isPresent();
   }

   default boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.getFocused() != null && this.isDragging() && var5 == 0 ? this.getFocused().mouseDragged(var1, var3, var5, var6, var8) : false;
   }

   boolean isDragging();

   void setDragging(boolean var1);

   default boolean mouseScrolled(double var1, double var3, double var5) {
      return this.getChildAt(var1, var3).filter((var6) -> {
         return var6.mouseScrolled(var1, var3, var5);
      }).isPresent();
   }

   default boolean keyPressed(int var1, int var2, int var3) {
      return this.getFocused() != null && this.getFocused().keyPressed(var1, var2, var3);
   }

   default boolean keyReleased(int var1, int var2, int var3) {
      return this.getFocused() != null && this.getFocused().keyReleased(var1, var2, var3);
   }

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
         BooleanSupplier var10000;
         if (var1) {
            Objects.requireNonNull(var7);
            var10000 = var7::hasNext;
         } else {
            Objects.requireNonNull(var7);
            var10000 = var7::hasPrevious;
         }

         BooleanSupplier var8 = var10000;
         Supplier var11;
         if (var1) {
            Objects.requireNonNull(var7);
            var11 = var7::next;
         } else {
            Objects.requireNonNull(var7);
            var11 = var7::previous;
         }

         Supplier var9 = var11;

         GuiEventListener var10;
         do {
            if (!var8.getAsBoolean()) {
               this.setFocused((GuiEventListener)null);
               return false;
            }

            var10 = (GuiEventListener)var9.get();
         } while(!var10.changeFocus(var1));

         this.setFocused(var10);
         return true;
      }
   }
}
