package net.minecraft.commands.arguments.selector.options;

public class InvertableSetOptionState {
   private boolean hasPositiveValue;
   private boolean hasNegativeValue;

   public InvertableSetOptionState() {
      super();
   }

   public boolean canParsePositive() {
      return !this.hasPositiveValue && !this.hasNegativeValue;
   }

   public boolean canParseNegative() {
      return !this.hasPositiveValue;
   }

   public boolean canParseAny() {
      return this.canParsePositive() || this.canParseNegative();
   }

   public boolean canParse(final boolean inverted) {
      return inverted ? this.canParseNegative() : this.canParsePositive();
   }

   public void markParsedPositive() {
      this.hasPositiveValue = true;
   }

   public void markParsedNegative() {
      this.hasNegativeValue = true;
   }

   public void markParsed(final boolean inverted) {
      if (inverted) {
         this.markParsedNegative();
      } else {
         this.markParsedPositive();
      }

   }
}
