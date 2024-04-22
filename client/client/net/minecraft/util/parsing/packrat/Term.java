package net.minecraft.util.parsing.packrat;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.mutable.MutableBoolean;

public interface Term<S> {
   boolean parse(ParseState<S> var1, Scope var2, Control var3);

   static <S> Term<S> named(Atom<?> var0) {
      return new Term.Reference<>(var0);
   }

   static <S, T> Term<S> marker(Atom<T> var0, T var1) {
      return new Term.Marker<>(var0, var1);
   }

   @SafeVarargs
   static <S> Term<S> sequence(Term<S>... var0) {
      return new Term.Sequence<>(List.of(var0));
   }

   @SafeVarargs
   static <S> Term<S> alternative(Term<S>... var0) {
      return new Term.Alternative<>(List.of(var0));
   }

   static <S> Term<S> optional(Term<S> var0) {
      return new Term.Maybe<>(var0);
   }

   static <S> Term<S> cut() {
      return new Term<S>() {
         @Override
         public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
            var3.cut();
            return true;
         }

         @Override
         public String toString() {
            return "\u2191";
         }
      };
   }

   static <S> Term<S> empty() {
      return new Term<S>() {
         @Override
         public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
            return true;
         }

         @Override
         public String toString() {
            return "\u03b5";
         }
      };
   }

   public static record Alternative<S>(List<Term<S>> elements) implements Term<S> {
      public Alternative(List<Term<S>> elements) {
         super();
         this.elements = elements;
      }

      @Override
      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         MutableBoolean var4 = new MutableBoolean();
         Control var5 = var4::setTrue;
         int var6 = var1.mark();

         for (Term var8 : this.elements) {
            if (var4.isTrue()) {
               break;
            }

            Scope var9 = new Scope();
            if (var8.parse(var1, var9, var5)) {
               var2.putAll(var9);
               return true;
            }

            var1.restore(var6);
         }

         return false;
      }
   }

   public static record Marker<S, T>(Atom<T> name, T value) implements Term<S> {
      public Marker(Atom<T> name, T value) {
         super();
         this.name = name;
         this.value = (T)value;
      }

      @Override
      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         var2.put(this.name, this.value);
         return true;
      }
   }

   public static record Maybe<S>(Term<S> term) implements Term<S> {
      public Maybe(Term<S> term) {
         super();
         this.term = term;
      }

      @Override
      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         int var4 = var1.mark();
         if (!this.term.parse(var1, var2, var3)) {
            var1.restore(var4);
         }

         return true;
      }
   }

   public static record Reference<S, T>(Atom<T> name) implements Term<S> {
      public Reference(Atom<T> name) {
         super();
         this.name = name;
      }

      @Override
      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         Optional var4 = var1.parse(this.name);
         if (var4.isEmpty()) {
            return false;
         } else {
            var2.put(this.name, (T)var4.get());
            return true;
         }
      }
   }

   public static record Sequence<S>(List<Term<S>> elements) implements Term<S> {
      public Sequence(List<Term<S>> elements) {
         super();
         this.elements = elements;
      }

      @Override
      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         int var4 = var1.mark();

         for (Term var6 : this.elements) {
            if (!var6.parse(var1, var2, var3)) {
               var1.restore(var4);
               return false;
            }
         }

         return true;
      }
   }
}