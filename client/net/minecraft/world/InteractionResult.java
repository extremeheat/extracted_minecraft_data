package net.minecraft.world;

import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public sealed interface InteractionResult {
   Success SUCCESS = new Success(InteractionResult.SwingSource.CLIENT, InteractionResult.ItemContext.DEFAULT);
   Success SUCCESS_SERVER = new Success(InteractionResult.SwingSource.SERVER, InteractionResult.ItemContext.DEFAULT);
   Success CONSUME = new Success(InteractionResult.SwingSource.NONE, InteractionResult.ItemContext.DEFAULT);
   Fail FAIL = new Fail();
   Pass PASS = new Pass();
   TryEmptyHandInteraction TRY_WITH_EMPTY_HAND = new TryEmptyHandInteraction();

   default boolean consumesAction() {
      return false;
   }

   public static enum SwingSource {
      NONE,
      CLIENT,
      SERVER;

      private SwingSource() {
      }

      // $FF: synthetic method
      private static SwingSource[] $values() {
         return new SwingSource[]{NONE, CLIENT, SERVER};
      }
   }

   public static record Success(SwingSource swingSource, ItemContext itemContext) implements InteractionResult {
      public Success {
         super();
      }

      public boolean consumesAction() {
         return true;
      }

      public Success heldItemTransformedTo(final ItemStack itemStack) {
         return new Success(this.swingSource, new ItemContext(true, itemStack));
      }

      public Success withoutItem() {
         return new Success(this.swingSource, InteractionResult.ItemContext.NONE);
      }

      public boolean wasItemInteraction() {
         return this.itemContext.wasItemInteraction;
      }

      public @Nullable ItemStack heldItemTransformedTo() {
         return this.itemContext.heldItemTransformedTo;
      }
   }

   public static record ItemContext(boolean wasItemInteraction, @Nullable ItemStack heldItemTransformedTo) {
      public static final ItemContext NONE = new ItemContext(false, (ItemStack)null);
      public static final ItemContext DEFAULT = new ItemContext(true, (ItemStack)null);

      public ItemContext {
         super();
      }
   }

   public static record Fail() implements InteractionResult {
      public Fail() {
         super();
      }
   }

   public static record Pass() implements InteractionResult {
      public Pass() {
         super();
      }
   }

   public static record TryEmptyHandInteraction() implements InteractionResult {
      public TryEmptyHandInteraction() {
         super();
      }
   }
}
