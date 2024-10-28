package net.minecraft.client.gui.navigation;

public interface FocusNavigationEvent {
   ScreenDirection getVerticalDirectionForInitialFocus();

   public static record ArrowNavigation(ScreenDirection direction) implements FocusNavigationEvent {
      public ArrowNavigation(ScreenDirection direction) {
         super();
         this.direction = direction;
      }

      public ScreenDirection getVerticalDirectionForInitialFocus() {
         return this.direction.getAxis() == ScreenAxis.VERTICAL ? this.direction : ScreenDirection.DOWN;
      }

      public ScreenDirection direction() {
         return this.direction;
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

   public static record TabNavigation(boolean forward) implements FocusNavigationEvent {
      public TabNavigation(boolean forward) {
         super();
         this.forward = forward;
      }

      public ScreenDirection getVerticalDirectionForInitialFocus() {
         return this.forward ? ScreenDirection.DOWN : ScreenDirection.UP;
      }

      public boolean forward() {
         return this.forward;
      }
   }
}
