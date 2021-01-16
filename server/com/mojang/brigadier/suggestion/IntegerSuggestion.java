package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import java.util.Objects;

public class IntegerSuggestion extends Suggestion {
   private int value;

   public IntegerSuggestion(StringRange var1, int var2) {
      this(var1, var2, (Message)null);
   }

   public IntegerSuggestion(StringRange var1, int var2, Message var3) {
      super(var1, Integer.toString(var2), var3);
      this.value = var2;
   }

   public int getValue() {
      return this.value;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntegerSuggestion)) {
         return false;
      } else {
         IntegerSuggestion var2 = (IntegerSuggestion)var1;
         return this.value == var2.value && super.equals(var1);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{super.hashCode(), this.value});
   }

   public String toString() {
      return "IntegerSuggestion{value=" + this.value + ", range=" + this.getRange() + ", text='" + this.getText() + '\'' + ", tooltip='" + this.getTooltip() + '\'' + '}';
   }

   public int compareTo(Suggestion var1) {
      return var1 instanceof IntegerSuggestion ? Integer.compare(this.value, ((IntegerSuggestion)var1).value) : super.compareTo(var1);
   }

   public int compareToIgnoreCase(Suggestion var1) {
      return this.compareTo(var1);
   }
}
