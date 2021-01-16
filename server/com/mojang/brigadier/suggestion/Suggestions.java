package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.context.StringRange;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Suggestions {
   private static final Suggestions EMPTY = new Suggestions(StringRange.at(0), new ArrayList());
   private final StringRange range;
   private final List<Suggestion> suggestions;

   public Suggestions(StringRange var1, List<Suggestion> var2) {
      super();
      this.range = var1;
      this.suggestions = var2;
   }

   public StringRange getRange() {
      return this.range;
   }

   public List<Suggestion> getList() {
      return this.suggestions;
   }

   public boolean isEmpty() {
      return this.suggestions.isEmpty();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Suggestions)) {
         return false;
      } else {
         Suggestions var2 = (Suggestions)var1;
         return Objects.equals(this.range, var2.range) && Objects.equals(this.suggestions, var2.suggestions);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.range, this.suggestions});
   }

   public String toString() {
      return "Suggestions{range=" + this.range + ", suggestions=" + this.suggestions + '}';
   }

   public static CompletableFuture<Suggestions> empty() {
      return CompletableFuture.completedFuture(EMPTY);
   }

   public static Suggestions merge(String var0, Collection<Suggestions> var1) {
      if (var1.isEmpty()) {
         return EMPTY;
      } else if (var1.size() == 1) {
         return (Suggestions)var1.iterator().next();
      } else {
         HashSet var2 = new HashSet();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Suggestions var4 = (Suggestions)var3.next();
            var2.addAll(var4.getList());
         }

         return create(var0, var2);
      }
   }

   public static Suggestions create(String var0, Collection<Suggestion> var1) {
      if (var1.isEmpty()) {
         return EMPTY;
      } else {
         int var2 = 2147483647;
         int var3 = -2147483648;

         Suggestion var5;
         for(Iterator var4 = var1.iterator(); var4.hasNext(); var3 = Math.max(var5.getRange().getEnd(), var3)) {
            var5 = (Suggestion)var4.next();
            var2 = Math.min(var5.getRange().getStart(), var2);
         }

         StringRange var8 = new StringRange(var2, var3);
         HashSet var9 = new HashSet();
         Iterator var6 = var1.iterator();

         while(var6.hasNext()) {
            Suggestion var7 = (Suggestion)var6.next();
            var9.add(var7.expand(var0, var8));
         }

         ArrayList var10 = new ArrayList(var9);
         var10.sort((var0x, var1x) -> {
            return var0x.compareToIgnoreCase(var1x);
         });
         return new Suggestions(var8, var10);
      }
   }
}
