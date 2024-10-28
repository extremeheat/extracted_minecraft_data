package net.minecraft.util.parsing.packrat;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.mutable.MutableBoolean;

public interface Term<S> {
   boolean parse(ParseState<S> var1, Scope var2, Control var3);

   static <S> Term<S> named(Atom<?> var0) {
      return new Reference(var0);
   }

   static <S, T> Term<S> marker(Atom<T> var0, T var1) {
      return new Marker(var0, var1);
   }

   @SafeVarargs
   static <S> Term<S> sequence(Term<S>... var0) {
      return new Sequence(List.of(var0));
   }

   @SafeVarargs
   static <S> Term<S> alternative(Term<S>... var0) {
      return new Alternative(List.of(var0));
   }

   static <S> Term<S> optional(Term<S> var0) {
      return new Maybe(var0);
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

   public static record Reference<S, T>(Atom<T> name) implements Term<S> {
      public Reference(Atom<T> var1) {
         super();
         this.name = var1;
      }

      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         Optional var4 = var1.parse(this.name);
         if (var4.isEmpty()) {
            return false;
         } else {
            var2.put(this.name, var4.get());
            return true;
         }
      }

      public Atom<T> name() {
         return this.name;
      }
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

      public Atom<T> name() {
         return this.name;
      }

      public T value() {
         return this.value;
      }
   }

   public static record Sequence<S>(List<Term<S>> elements) implements Term<S> {
      public Sequence(List<Term<S>> var1) {
         super();
         this.elements = var1;
      }

      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         int var4 = var1.mark();
         Iterator var5 = this.elements.iterator();

         Term var6;
         do {
            if (!var5.hasNext()) {
               return true;
            }

            var6 = (Term)var5.next();
         } while(var6.parse(var1, var2, var3));

         var1.restore(var4);
         return false;
      }

      public List<Term<S>> elements() {
         return this.elements;
      }
   }

   public static record Alternative<S>(List<Term<S>> elements) implements Term<S> {
      public Alternative(List<Term<S>> var1) {
         super();
         this.elements = var1;
      }

      public boolean parse(ParseState<S> var1, Scope var2, Control var3) {
         MutableBoolean var4 = new MutableBoolean();
         Objects.requireNonNull(var4);
         Control var5 = var4::setTrue;
         int var6 = var1.mark();
         Iterator var7 = this.elements.iterator();

         while(var7.hasNext()) {
            Term var8 = (Term)var7.next();
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

      public List<Term<S>> elements() {
         return this.elements;
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

      public Term<S> term() {
         return this.term;
      }
   }
}
