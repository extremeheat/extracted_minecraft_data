package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class Converter<A, B> implements Function<A, B> {
   private final boolean handleNullAutomatically;
   @LazyInit
   private transient Converter<B, A> reverse;

   protected Converter() {
      this(true);
   }

   Converter(boolean var1) {
      super();
      this.handleNullAutomatically = var1;
   }

   protected abstract B doForward(A var1);

   protected abstract A doBackward(B var1);

   @Nullable
   @CanIgnoreReturnValue
   public final B convert(@Nullable A var1) {
      return this.correctedDoForward(var1);
   }

   @Nullable
   B correctedDoForward(@Nullable A var1) {
      if (this.handleNullAutomatically) {
         return var1 == null ? null : Preconditions.checkNotNull(this.doForward(var1));
      } else {
         return this.doForward(var1);
      }
   }

   @Nullable
   A correctedDoBackward(@Nullable B var1) {
      if (this.handleNullAutomatically) {
         return var1 == null ? null : Preconditions.checkNotNull(this.doBackward(var1));
      } else {
         return this.doBackward(var1);
      }
   }

   @CanIgnoreReturnValue
   public Iterable<B> convertAll(final Iterable<? extends A> var1) {
      Preconditions.checkNotNull(var1, "fromIterable");
      return new Iterable<B>() {
         public Iterator<B> iterator() {
            return new Iterator<B>() {
               private final Iterator<? extends A> fromIterator = var1.iterator();

               public boolean hasNext() {
                  return this.fromIterator.hasNext();
               }

               public B next() {
                  return Converter.this.convert(this.fromIterator.next());
               }

               public void remove() {
                  this.fromIterator.remove();
               }
            };
         }
      };
   }

   @CanIgnoreReturnValue
   public Converter<B, A> reverse() {
      Converter var1 = this.reverse;
      return var1 == null ? (this.reverse = new Converter.ReverseConverter(this)) : var1;
   }

   public final <C> Converter<A, C> andThen(Converter<B, C> var1) {
      return this.doAndThen(var1);
   }

   <C> Converter<A, C> doAndThen(Converter<B, C> var1) {
      return new Converter.ConverterComposition(this, (Converter)Preconditions.checkNotNull(var1));
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   @CanIgnoreReturnValue
   public final B apply(@Nullable A var1) {
      return this.convert(var1);
   }

   public boolean equals(@Nullable Object var1) {
      return super.equals(var1);
   }

   public static <A, B> Converter<A, B> from(Function<? super A, ? extends B> var0, Function<? super B, ? extends A> var1) {
      return new Converter.FunctionBasedConverter(var0, var1);
   }

   public static <T> Converter<T, T> identity() {
      return Converter.IdentityConverter.INSTANCE;
   }

   private static final class IdentityConverter<T> extends Converter<T, T> implements Serializable {
      static final Converter.IdentityConverter INSTANCE = new Converter.IdentityConverter();
      private static final long serialVersionUID = 0L;

      private IdentityConverter() {
         super();
      }

      protected T doForward(T var1) {
         return var1;
      }

      protected T doBackward(T var1) {
         return var1;
      }

      public Converter.IdentityConverter<T> reverse() {
         return this;
      }

      <S> Converter<T, S> doAndThen(Converter<T, S> var1) {
         return (Converter)Preconditions.checkNotNull(var1, "otherConverter");
      }

      public String toString() {
         return "Converter.identity()";
      }

      private Object readResolve() {
         return INSTANCE;
      }
   }

   private static final class FunctionBasedConverter<A, B> extends Converter<A, B> implements Serializable {
      private final Function<? super A, ? extends B> forwardFunction;
      private final Function<? super B, ? extends A> backwardFunction;

      private FunctionBasedConverter(Function<? super A, ? extends B> var1, Function<? super B, ? extends A> var2) {
         super();
         this.forwardFunction = (Function)Preconditions.checkNotNull(var1);
         this.backwardFunction = (Function)Preconditions.checkNotNull(var2);
      }

      protected B doForward(A var1) {
         return this.forwardFunction.apply(var1);
      }

      protected A doBackward(B var1) {
         return this.backwardFunction.apply(var1);
      }

      public boolean equals(@Nullable Object var1) {
         if (!(var1 instanceof Converter.FunctionBasedConverter)) {
            return false;
         } else {
            Converter.FunctionBasedConverter var2 = (Converter.FunctionBasedConverter)var1;
            return this.forwardFunction.equals(var2.forwardFunction) && this.backwardFunction.equals(var2.backwardFunction);
         }
      }

      public int hashCode() {
         return this.forwardFunction.hashCode() * 31 + this.backwardFunction.hashCode();
      }

      public String toString() {
         return "Converter.from(" + this.forwardFunction + ", " + this.backwardFunction + ")";
      }

      // $FF: synthetic method
      FunctionBasedConverter(Function var1, Function var2, Object var3) {
         this(var1, var2);
      }
   }

   private static final class ConverterComposition<A, B, C> extends Converter<A, C> implements Serializable {
      final Converter<A, B> first;
      final Converter<B, C> second;
      private static final long serialVersionUID = 0L;

      ConverterComposition(Converter<A, B> var1, Converter<B, C> var2) {
         super();
         this.first = var1;
         this.second = var2;
      }

      protected C doForward(A var1) {
         throw new AssertionError();
      }

      protected A doBackward(C var1) {
         throw new AssertionError();
      }

      @Nullable
      C correctedDoForward(@Nullable A var1) {
         return this.second.correctedDoForward(this.first.correctedDoForward(var1));
      }

      @Nullable
      A correctedDoBackward(@Nullable C var1) {
         return this.first.correctedDoBackward(this.second.correctedDoBackward(var1));
      }

      public boolean equals(@Nullable Object var1) {
         if (!(var1 instanceof Converter.ConverterComposition)) {
            return false;
         } else {
            Converter.ConverterComposition var2 = (Converter.ConverterComposition)var1;
            return this.first.equals(var2.first) && this.second.equals(var2.second);
         }
      }

      public int hashCode() {
         return 31 * this.first.hashCode() + this.second.hashCode();
      }

      public String toString() {
         return this.first + ".andThen(" + this.second + ")";
      }
   }

   private static final class ReverseConverter<A, B> extends Converter<B, A> implements Serializable {
      final Converter<A, B> original;
      private static final long serialVersionUID = 0L;

      ReverseConverter(Converter<A, B> var1) {
         super();
         this.original = var1;
      }

      protected A doForward(B var1) {
         throw new AssertionError();
      }

      protected B doBackward(A var1) {
         throw new AssertionError();
      }

      @Nullable
      A correctedDoForward(@Nullable B var1) {
         return this.original.correctedDoBackward(var1);
      }

      @Nullable
      B correctedDoBackward(@Nullable A var1) {
         return this.original.correctedDoForward(var1);
      }

      public Converter<A, B> reverse() {
         return this.original;
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof Converter.ReverseConverter) {
            Converter.ReverseConverter var2 = (Converter.ReverseConverter)var1;
            return this.original.equals(var2.original);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return ~this.original.hashCode();
      }

      public String toString() {
         return this.original + ".reverse()";
      }
   }
}
