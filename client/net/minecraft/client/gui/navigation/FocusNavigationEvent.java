package net.minecraft.client.gui.navigation;

public interface FocusNavigationEvent {
   ScreenDirection getVerticalDirectionForInitialFocus();

   public static record TabNavigation(boolean forward) implements FocusNavigationEvent {
      public TabNavigation(boolean var1) {
         super();
         this.forward = var1;
      }

      public ScreenDirection getVerticalDirectionForInitialFocus() {
         return this.forward ? ScreenDirection.DOWN : ScreenDirection.UP;
      }
   }

   public static class InitialFocus implements FocusNavigationEvent {
      public InitialFocus() {
         super();
      }

      public ScreenDirection getVerticalDirectionForInitialFocus() {
         return ScreenDirection.DOWN;
      }
   }

   public static record ArrowNavigation(ScreenDirection direction) implements FocusNavigationEvent {
      public ArrowNavigation(ScreenDirection var1) {
         super();
         this.direction = var1;
      }

      public ScreenDirection getVerticalDirectionForInitialFocus() {
         return this.direction.getAxis() == ScreenAxis.VERTICAL ? this.direction : ScreenDirection.DOWN;
      }
   }
}
