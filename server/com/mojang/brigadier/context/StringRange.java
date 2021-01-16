package com.mojang.brigadier.context;

import com.mojang.brigadier.ImmutableStringReader;
import java.util.Objects;

public class StringRange {
   private final int start;
   private final int end;

   public StringRange(int var1, int var2) {
      super();
      this.start = var1;
      this.end = var2;
   }

   public static StringRange at(int var0) {
      return new StringRange(var0, var0);
   }

   public static StringRange between(int var0, int var1) {
      return new StringRange(var0, var1);
   }

   public static StringRange encompassing(StringRange var0, StringRange var1) {
      return new StringRange(Math.min(var0.getStart(), var1.getStart()), Math.max(var0.getEnd(), var1.getEnd()));
   }

   public int getStart() {
      return this.start;
   }

   public int getEnd() {
      return this.end;
   }

   public String get(ImmutableStringReader var1) {
      return var1.getString().substring(this.start, this.end);
   }

   public String get(String var1) {
      return var1.substring(this.start, this.end);
   }

   public boolean isEmpty() {
      return this.start == this.end;
   }

   public int getLength() {
      return this.end - this.start;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof StringRange)) {
         return false;
      } else {
         StringRange var2 = (StringRange)var1;
         return this.start == var2.start && this.end == var2.end;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.start, this.end});
   }

   public String toString() {
      return "StringRange{start=" + this.start + ", end=" + this.end + '}';
   }
}
