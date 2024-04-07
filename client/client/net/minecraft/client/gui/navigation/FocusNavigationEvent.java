package net.minecraft.client.gui.navigation;

public interface FocusNavigationEvent {
   ScreenDirection getVerticalDirectionForInitialFocus();

   public static record ArrowNavigation(ScreenDirection direction) implements FocusNavigationEvent {
      public ArrowNavigation(ScreenDirection direction) {
         super();
         this.direction = direction;
      }

      @Override
      public ScreenDirection getVerticalDirectionForInitialFocus() {
         return this.direction.getAxis() == ScreenAxis.VERTICAL ? this.direction : ScreenDirection.DOWN;
      }
   }

   public static class InitialFocus implements FocusNavigationEvent {
      public InitialFocus() {
         super();
      }

      @Override
      public ScreenDirection getVerticalDirectionForInitialFocus() {
         return ScreenDirection.DOWN;
      }
   }

   public static record TabNavigation(boolean forward) implements FocusNavigationEvent {
      public TabNavigation(boolean forward) {
         super();
         this.forward = forward;
      }

      @Override
      public ScreenDirection getVerticalDirectionForInitialFocus() {
         return this.forward ? ScreenDirection.DOWN : ScreenDirection.UP;
      }
   }
}
