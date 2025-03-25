package net.minecraft.util.parsing.packrat;

import java.util.ArrayList;
import java.util.List;

public interface Term<S> {
   boolean parse(ParseState<S> var1, Scope var2, Control var3);

   static <S, T> Term<S> marker(Atom<T> var0, T var1) {
      return new Marker(var0, var1);
   }

   @SafeVarargs
   static <S> Term<S> sequence(Term<S>... var0) {
      return new Sequence<S>(var0);
   }

   @SafeVarargs
   static <S> Term<S> alternative(Term<S>... var0) {
      return new Alternative<S>(var0);
   }

   static <S> Term<S> optional(Term<S> var0) {
      return new Maybe<S>(var0);
   }

   static <S, T> Term<S> repeated(NamedRule<S, T> var0, Atom<List<T>> var1) {
      return repeated(var0, var1, 0);
   }

   static <S, T> Term<S> repeated(NamedRule<S, T> var0, Atom<List<T>> var1, int var2) {
      return new Repeated(var0, var1, var2);
   }

   static <S, T> Term<S> repeatedWithTrailingSeparator(NamedRule<S, T> var0, Atom<List<T>> var1, Term<S> var2) {
      return repeatedWithTrailingSeparator(var0, var1, var2, 0);
   }

   static <S, T> Term<S> repeatedWithTrailingSeparator(NamedRule<S, T> var0, Atom<List<T>> var1, Term<S> var2, int var3) {
      return new RepeatedWithSeparator(var0, var1, var2, var3, true);
   }

   static <S, T> Term<S> repeatedWithoutTrailingSeparator(NamedRule<S, T> var0, Atom<List<T>> var1, Term<S> var2) {
      return repeatedWithoutTrailingSeparator(var0, var1, var2, 0);
   }

   static <S, T> Term<S> repeatedWithoutTrailingSeparator(NamedRule<S, T> var0, Atom<List<T>> var1, Term<S> var2, int var3) {
      return new RepeatedWithSeparator(var0, var1, var2, var3, false);
   }

   static <S> Term<S> positiveLookahead(Term<S> var0) {
      return new LookAhead<S>(var0, true);
   }

   static <S> Term<S> negativeLookahead(Term<S> var0) {
      return new LookAhead<S>(var0, false);
   }

   static <S> Term<S> cut() {
      return new Term<S>() {
         public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
            var3.cut();
            return true;
         }

         public String toString() {
            return "\u2191";
         }
      };
   }

   static <S> Term<S> empty() {
      return new Term<S>() {
         public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
            return true;
         }

         public String toString() {
            return "\u03b5";
         }
      };
   }

   static <S> Term<S> fail(final Object var0) {
      return new Term<S>() {
         public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
            var1.errorCollector().store(var1.mark(), var0);
            return false;
         }

         public String toString() {
            return "fail";
         }
      };
   }

   public static record Marker<S, T>(Atom<T> name, T value) implements Term<S> {
      public Marker(Atom<T> var1, T var2) {
         super();
         this.name = var1;
         this.value = var2;
      }

      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         var2.put(this.name, this.value);
         return true;
      }
   }

   public static record Sequence<S>(Term<S>[] elements) implements Term<S> {
      public Sequence(Term<S>[] var1) {
         super();
         this.elements = var1;
      }

      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         int var4 = var1.mark();

         for(Term var8 : this.elements) {
            if (!var8.parse(var1, var2, var3)) {
               var1.restore(var4);
               return false;
            }
         }

         return true;
      }
   }

   public static record Alternative<S>(Term<S>[] elements) implements Term<S> {
      public Alternative(Term<S>[] var1) {
         super();
         this.elements = var1;
      }

      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         Control var4 = var1.acquireControl();

         try {
            int var5 = var1.mark();
            var2.splitFrame();

            for(Term var9 : this.elements) {
               if (var9.parse(var1, var2, var4)) {
                  var2.mergeFrame();
                  boolean var10 = true;
                  return var10;
               }

               var2.clearFrameValues();
               var1.restore(var5);
               if (var4.hasCut()) {
                  break;
               }
            }

            var2.popFrame();
            boolean var14 = false;
            return var14;
         } finally {
            var1.releaseControl();
         }
      }
   }

   public static record Maybe<S>(Term<S> term) implements Term<S> {
      public Maybe(Term<S> var1) {
         super();
         this.term = var1;
      }

      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         int var4 = var1.mark();
         if (!this.term.parse(var1, var2, var3)) {
            var1.restore(var4);
         }

         return true;
      }
   }

   public static record Repeated<S, T>(NamedRule<S, T> element, Atom<List<T>> listName, int minRepetitions) implements Term<S> {
      public Repeated(NamedRule<S, T> var1, Atom<List<T>> var2, int var3) {
         super();
         this.element = var1;
         this.listName = var2;
         this.minRepetitions = var3;
      }

      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         int var4 = var1.mark();
         ArrayList var5 = new ArrayList(this.minRepetitions);

         while(true) {
            int var6 = var1.mark();
            Object var7 = var1.parse(this.element);
            if (var7 == null) {
               var1.restore(var6);
               if (var5.size() < this.minRepetitions) {
                  var1.restore(var4);
                  return false;
               } else {
                  var2.put(this.listName, var5);
                  return true;
               }
            }

            var5.add(var7);
         }
      }
   }

   public static record RepeatedWithSeparator<S, T>(NamedRule<S, T> element, Atom<List<T>> listName, Term<S> separator, int minRepetitions, boolean allowTrailingSeparator) implements Term<S> {
      public RepeatedWithSeparator(NamedRule<S, T> var1, Atom<List<T>> var2, Term<S> var3, int var4, boolean var5) {
         super();
         this.element = var1;
         this.listName = var2;
         this.separator = var3;
         this.minRepetitions = var4;
         this.allowTrailingSeparator = var5;
      }

      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         int var4 = var1.mark();
         ArrayList var5 = new ArrayList(this.minRepetitions);
         boolean var6 = true;

         while(true) {
            int var7 = var1.mark();
            if (!var6 && !this.separator.parse(var1, var2, var3)) {
               var1.restore(var7);
               break;
            }

            int var8 = var1.mark();
            Object var9 = var1.parse(this.element);
            if (var9 == null) {
               if (var6) {
                  var1.restore(var8);
               } else {
                  if (!this.allowTrailingSeparator) {
                     var1.restore(var4);
                     return false;
                  }

                  var1.restore(var8);
               }
               break;
            }

            var5.add(var9);
            var6 = false;
         }

         if (var5.size() < this.minRepetitions) {
            var1.restore(var4);
            return false;
         } else {
            var2.put(this.listName, var5);
            return true;
         }
      }
   }

   public static record LookAhead<S>(Term<S> term, boolean positive) implements Term<S> {
      public LookAhead(Term<S> var1, boolean var2) {
         super();
         this.term = var1;
         this.positive = var2;
      }

      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         int var4 = var1.mark();
         boolean var5 = this.term.parse(var1.silent(), var2, var3);
         var1.restore(var4);
         return this.positive == var5;
      }
   }
}
