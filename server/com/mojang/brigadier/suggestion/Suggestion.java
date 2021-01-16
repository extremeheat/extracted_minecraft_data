package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import java.util.Objects;

public class Suggestion implements Comparable<Suggestion> {
   private final StringRange range;
   private final String text;
   private final Message tooltip;

   public Suggestion(StringRange var1, String var2) {
      this(var1, var2, (Message)null);
   }

   public Suggestion(StringRange var1, String var2, Message var3) {
      super();
      this.range = var1;
      this.text = var2;
      this.tooltip = var3;
   }

   public StringRange getRange() {
      return this.range;
   }

   public String getText() {
      return this.text;
   }

   public Message getTooltip() {
      return this.tooltip;
   }

   public String apply(String var1) {
      if (this.range.getStart() == 0 && this.range.getEnd() == var1.length()) {
         return this.text;
      } else {
         StringBuilder var2 = new StringBuilder();
         if (this.range.getStart() > 0) {
            var2.append(var1.substring(0, this.range.getStart()));
         }

         var2.append(this.text);
         if (this.range.getEnd() < var1.length()) {
            var2.append(var1.substring(this.range.getEnd()));
         }

         return var2.toString();
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Suggestion)) {
         return false;
      } else {
         Suggestion var2 = (Suggestion)var1;
         return Objects.equals(this.range, var2.range) && Objects.equals(this.text, var2.text) && Objects.equals(this.tooltip, var2.tooltip);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.range, this.text, this.tooltip});
   }

   public String toString() {
      return "Suggestion{range=" + this.range + ", text='" + this.text + '\'' + ", tooltip='" + this.tooltip + '\'' + '}';
   }

   public int compareTo(Suggestion var1) {
      return this.text.compareTo(var1.text);
   }

   public int compareToIgnoreCase(Suggestion var1) {
      return this.text.compareToIgnoreCase(var1.text);
   }

   public Suggestion expand(String var1, StringRange var2) {
      if (var2.equals(this.range)) {
         return this;
      } else {
         StringBuilder var3 = new StringBuilder();
         if (var2.getStart() < this.range.getStart()) {
            var3.append(var1.substring(var2.getStart(), this.range.getStart()));
         }

         var3.append(this.text);
         if (var2.getEnd() > this.range.getEnd()) {
            var3.append(var1.substring(this.range.getEnd(), var2.getEnd()));
         }

         return new Suggestion(var2, var3.toString(), this.tooltip);
      }
   }
}
