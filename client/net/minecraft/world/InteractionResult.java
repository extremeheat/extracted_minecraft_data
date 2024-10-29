package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;

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

   public static record Success(SwingSource swingSource, ItemContext itemContext) implements InteractionResult {
      public Success(SwingSource var1, ItemContext var2) {
         super();
         this.swingSource = var1;
         this.itemContext = var2;
      }

      public boolean consumesAction() {
         return true;
      }

      public Success heldItemTransformedTo(ItemStack var1) {
         return new Success(this.swingSource, new ItemContext(true, var1));
      }

      public Success withoutItem() {
         return new Success(this.swingSource, InteractionResult.ItemContext.NONE);
      }

      public boolean wasItemInteraction() {
         return this.itemContext.wasItemInteraction;
      }

      @Nullable
      public ItemStack heldItemTransformedTo() {
         return this.itemContext.heldItemTransformedTo;
      }

      public SwingSource swingSource() {
         return this.swingSource;
      }

      public ItemContext itemContext() {
         return this.itemContext;
      }
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

   public static record ItemContext(boolean wasItemInteraction, @Nullable ItemStack heldItemTransformedTo) {
      final boolean wasItemInteraction;
      @Nullable
      final ItemStack heldItemTransformedTo;
      static ItemContext NONE = new ItemContext(false, (ItemStack)null);
      static ItemContext DEFAULT = new ItemContext(true, (ItemStack)null);

      public ItemContext(boolean var1, @Nullable ItemStack var2) {
         super();
         this.wasItemInteraction = var1;
         this.heldItemTransformedTo = var2;
      }

      public boolean wasItemInteraction() {
         return this.wasItemInteraction;
      }

      @Nullable
      public ItemStack heldItemTransformedTo() {
         return this.heldItemTransformedTo;
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
