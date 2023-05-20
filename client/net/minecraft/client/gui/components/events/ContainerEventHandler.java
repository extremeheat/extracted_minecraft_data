package net.minecraft.client.gui.components.events;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.joml.Vector2i;

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

   @Override
   default void setFocused(boolean var1) {
   }

   @Override
   default boolean isFocused() {
      return this.getFocused() != null;
   }

   @Nullable
   @Override
   default ComponentPath getCurrentFocusPath() {
      GuiEventListener var1 = this.getFocused();
      return var1 != null ? ComponentPath.path(this, var1.getCurrentFocusPath()) : null;
   }

   default void magicalSpecialHackyFocus(@Nullable GuiEventListener var1) {
      this.setFocused(var1);
   }

   @Nullable
   @Override
   default ComponentPath nextFocusPath(FocusNavigationEvent var1) {
      GuiEventListener var2 = this.getFocused();
      if (var2 != null) {
         ComponentPath var3 = var2.nextFocusPath(var1);
         if (var3 != null) {
            return ComponentPath.path(this, var3);
         }
      }

      if (var1 instanceof FocusNavigationEvent.TabNavigation var5) {
         return this.handleTabNavigation((FocusNavigationEvent.TabNavigation)var5);
      } else {
         return var1 instanceof FocusNavigationEvent.ArrowNavigation var4 ? this.handleArrowNavigation(var4) : null;
      }
   }

   @Nullable
   private ComponentPath handleTabNavigation(FocusNavigationEvent.TabNavigation var1) {
      boolean var2 = var1.forward();
      GuiEventListener var3 = this.getFocused();
      ArrayList var4 = new ArrayList<>(this.children());
      Collections.sort(var4, Comparator.comparingInt(var0 -> var0.getTabOrderGroup()));
      int var6 = var4.indexOf(var3);
      int var5;
      if (var3 != null && var6 >= 0) {
         var5 = var6 + (var2 ? 1 : 0);
      } else if (var2) {
         var5 = 0;
      } else {
         var5 = var4.size();
      }

      ListIterator var7 = var4.listIterator(var5);
      BooleanSupplier var8 = var2 ? var7::hasNext : var7::hasPrevious;
      Supplier var9 = var2 ? var7::next : var7::previous;

      while(var8.getAsBoolean()) {
         GuiEventListener var10 = (GuiEventListener)var9.get();
         ComponentPath var11 = var10.nextFocusPath(var1);
         if (var11 != null) {
            return ComponentPath.path(this, var11);
         }
      }

      return null;
   }

   @Nullable
   private ComponentPath handleArrowNavigation(FocusNavigationEvent.ArrowNavigation var1) {
      GuiEventListener var2 = this.getFocused();
      if (var2 == null) {
         ScreenDirection var5 = var1.direction();
         ScreenRectangle var4 = this.getRectangle().getBorder(var5.getOpposite());
         return ComponentPath.path(this, this.nextFocusPathInDirection(var4, var5, null, var1));
      } else {
         ScreenRectangle var3 = var2.getRectangle();
         return ComponentPath.path(this, this.nextFocusPathInDirection(var3, var1.direction(), var2, var1));
      }
   }

   @Nullable
   private ComponentPath nextFocusPathInDirection(ScreenRectangle var1, ScreenDirection var2, @Nullable GuiEventListener var3, FocusNavigationEvent var4) {
      ScreenAxis var5 = var2.getAxis();
      ScreenAxis var6 = var5.orthogonal();
      ScreenDirection var7 = var6.getPositive();
      int var8 = var1.getBoundInDirection(var2.getOpposite());
      ArrayList var9 = new ArrayList();

      for(GuiEventListener var11 : this.children()) {
         if (var11 != var3) {
            ScreenRectangle var12 = var11.getRectangle();
            if (var12.overlapsInAxis(var1, var6)) {
               int var13 = var12.getBoundInDirection(var2.getOpposite());
               if (var2.isAfter(var13, var8)) {
                  var9.add(var11);
               } else if (var13 == var8 && var2.isAfter(var12.getBoundInDirection(var2), var1.getBoundInDirection(var2))) {
                  var9.add(var11);
               }
            }
         }
      }

      Comparator var15 = Comparator.comparing(var1x -> var1x.getRectangle().getBoundInDirection(var2.getOpposite()), var2.coordinateValueComparator());
      Comparator var16 = Comparator.comparing(var1x -> var1x.getRectangle().getBoundInDirection(var7.getOpposite()), var7.coordinateValueComparator());
      var9.sort(var15.thenComparing(var16));

      for(GuiEventListener var18 : var9) {
         ComponentPath var14 = var18.nextFocusPath(var4);
         if (var14 != null) {
            return var14;
         }
      }

      return this.nextFocusPathVaguelyInDirection(var1, var2, var3, var4);
   }

   @Nullable
   private ComponentPath nextFocusPathVaguelyInDirection(
      ScreenRectangle var1, ScreenDirection var2, @Nullable GuiEventListener var3, FocusNavigationEvent var4
   ) {
      ScreenAxis var5 = var2.getAxis();
      ScreenAxis var6 = var5.orthogonal();
      ArrayList var7 = new ArrayList();
      ScreenPosition var8 = ScreenPosition.of(var5, var1.getBoundInDirection(var2), var1.getCenterInAxis(var6));

      for(GuiEventListener var10 : this.children()) {
         if (var10 != var3) {
            ScreenRectangle var11 = var10.getRectangle();
            ScreenPosition var12 = ScreenPosition.of(var5, var11.getBoundInDirection(var2.getOpposite()), var11.getCenterInAxis(var6));
            if (var2.isAfter(var12.getCoordinate(var5), var8.getCoordinate(var5))) {
               long var13 = Vector2i.distanceSquared(var8.x(), var8.y(), var12.x(), var12.y());
               var7.add(Pair.of(var10, var13));
            }
         }
      }

      var7.sort(Comparator.comparingDouble(Pair::getSecond));

      for(Pair var16 : var7) {
         ComponentPath var17 = ((GuiEventListener)var16.getFirst()).nextFocusPath(var4);
         if (var17 != null) {
            return var17;
         }
      }

      return null;
   }
}
