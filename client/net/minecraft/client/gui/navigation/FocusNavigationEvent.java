package net.minecraft.client.gui.navigation;

public interface FocusNavigationEvent {
   ScreenDirection getVerticalDirectionForInitialFocus();

   public static record ArrowNavigation(ScreenDirection a) implements FocusNavigationEvent {
      private final ScreenDirection direction;

      public ArrowNavigation(ScreenDirection var1) {
         super();
         this.direction = var1;
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

   public static record TabNavigation(boolean a) implements FocusNavigationEvent {
      private final boolean forward;

      public TabNavigation(boolean var1) {
         super();
         this.forward = var1;
      }

      @Override
      public ScreenDirection getVerticalDirectionForInitialFocus() {
         return this.forward ? ScreenDirection.DOWN : ScreenDirection.UP;
      }
   }
}
