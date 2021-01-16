package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class Predicates {
   private static final Joiner COMMA_JOINER = Joiner.on(',');

   private Predicates() {
      super();
   }

   @GwtCompatible(
      serializable = true
   )
   public static <T> Predicate<T> alwaysTrue() {
      return Predicates.ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
   }

   @GwtCompatible(
      serializable = true
   )
   public static <T> Predicate<T> alwaysFalse() {
      return Predicates.ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
   }

   @GwtCompatible(
      serializable = true
   )
   public static <T> Predicate<T> isNull() {
      return Predicates.ObjectPredicate.IS_NULL.withNarrowedType();
   }

   @GwtCompatible(
      serializable = true
   )
   public static <T> Predicate<T> notNull() {
      return Predicates.ObjectPredicate.NOT_NULL.withNarrowedType();
   }

   public static <T> Predicate<T> not(Predicate<T> var0) {
      return new Predicates.NotPredicate(var0);
   }

   public static <T> Predicate<T> and(Iterable<? extends Predicate<? super T>> var0) {
      return new Predicates.AndPredicate(defensiveCopy(var0));
   }

   public static <T> Predicate<T> and(Predicate<? super T>... var0) {
      return new Predicates.AndPredicate(defensiveCopy((Object[])var0));
   }

   public static <T> Predicate<T> and(Predicate<? super T> var0, Predicate<? super T> var1) {
      return new Predicates.AndPredicate(asList((Predicate)Preconditions.checkNotNull(var0), (Predicate)Preconditions.checkNotNull(var1)));
   }

   public static <T> Predicate<T> or(Iterable<? extends Predicate<? super T>> var0) {
      return new Predicates.OrPredicate(defensiveCopy(var0));
   }

   public static <T> Predicate<T> or(Predicate<? super T>... var0) {
      return new Predicates.OrPredicate(defensiveCopy((Object[])var0));
   }

   public static <T> Predicate<T> or(Predicate<? super T> var0, Predicate<? super T> var1) {
      return new Predicates.OrPredicate(asList((Predicate)Preconditions.checkNotNull(var0), (Predicate)Preconditions.checkNotNull(var1)));
   }

   public static <T> Predicate<T> equalTo(@Nullable T var0) {
      return (Predicate)(var0 == null ? isNull() : new Predicates.IsEqualToPredicate(var0));
   }

   @GwtIncompatible
   public static Predicate<Object> instanceOf(Class<?> var0) {
      return new Predicates.InstanceOfPredicate(var0);
   }

   /** @deprecated */
   @Deprecated
   @GwtIncompatible
   @Beta
   public static Predicate<Class<?>> assignableFrom(Class<?> var0) {
      return subtypeOf(var0);
   }

   @GwtIncompatible
   @Beta
   public static Predicate<Class<?>> subtypeOf(Class<?> var0) {
      return new Predicates.SubtypeOfPredicate(var0);
   }

   public static <T> Predicate<T> in(Collection<? extends T> var0) {
      return new Predicates.InPredicate(var0);
   }

   public static <A, B> Predicate<A> compose(Predicate<B> var0, Function<A, ? extends B> var1) {
      return new Predicates.CompositionPredicate(var0, var1);
   }

   @GwtIncompatible
   public static Predicate<CharSequence> containsPattern(String var0) {
      return new Predicates.ContainsPatternFromStringPredicate(var0);
   }

   @GwtIncompatible("java.util.regex.Pattern")
   public static Predicate<CharSequence> contains(Pattern var0) {
      return new Predicates.ContainsPatternPredicate(new JdkPattern(var0));
   }

   private static <T> List<Predicate<? super T>> asList(Predicate<? super T> var0, Predicate<? super T> var1) {
      return Arrays.asList(var0, var1);
   }

   private static <T> List<T> defensiveCopy(T... var0) {
      return defensiveCopy((Iterable)Arrays.asList(var0));
   }

   static <T> List<T> defensiveCopy(Iterable<T> var0) {
      ArrayList var1 = new ArrayList();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         var1.add(Preconditions.checkNotNull(var3));
      }

      return var1;
   }

   @GwtIncompatible
   private static class ContainsPatternFromStringPredicate extends Predicates.ContainsPatternPredicate {
      private static final long serialVersionUID = 0L;

      ContainsPatternFromStringPredicate(String var1) {
         super(Platform.compilePattern(var1));
      }

      public String toString() {
         return "Predicates.containsPattern(" + this.pattern.pattern() + ")";
      }
   }

   @GwtIncompatible
   private static class ContainsPatternPredicate implements Predicate<CharSequence>, Serializable {
      final CommonPattern pattern;
      private static final long serialVersionUID = 0L;

      ContainsPatternPredicate(CommonPattern var1) {
         super();
         this.pattern = (CommonPattern)Preconditions.checkNotNull(var1);
      }

      public boolean apply(CharSequence var1) {
         return this.pattern.matcher(var1).find();
      }

      public int hashCode() {
         return Objects.hashCode(this.pattern.pattern(), this.pattern.flags());
      }

      public boolean equals(@Nullable Object var1) {
         if (!(var1 instanceof Predicates.ContainsPatternPredicate)) {
            return false;
         } else {
            Predicates.ContainsPatternPredicate var2 = (Predicates.ContainsPatternPredicate)var1;
            return Objects.equal(this.pattern.pattern(), var2.pattern.pattern()) && this.pattern.flags() == var2.pattern.flags();
         }
      }

      public String toString() {
         String var1 = MoreObjects.toStringHelper((Object)this.pattern).add("pattern", this.pattern.pattern()).add("pattern.flags", this.pattern.flags()).toString();
         return "Predicates.contains(" + var1 + ")";
      }
   }

   private static class CompositionPredicate<A, B> implements Predicate<A>, Serializable {
      final Predicate<B> p;
      final Function<A, ? extends B> f;
      private static final long serialVersionUID = 0L;

      private CompositionPredicate(Predicate<B> var1, Function<A, ? extends B> var2) {
         super();
         this.p = (Predicate)Preconditions.checkNotNull(var1);
         this.f = (Function)Preconditions.checkNotNull(var2);
      }

      public boolean apply(@Nullable A var1) {
         return this.p.apply(this.f.apply(var1));
      }

      public boolean equals(@Nullable Object var1) {
         if (!(var1 instanceof Predicates.CompositionPredicate)) {
            return false;
         } else {
            Predicates.CompositionPredicate var2 = (Predicates.CompositionPredicate)var1;
            return this.f.equals(var2.f) && this.p.equals(var2.p);
         }
      }

      public int hashCode() {
         return this.f.hashCode() ^ this.p.hashCode();
      }

      public String toString() {
         return this.p + "(" + this.f + ")";
      }

      // $FF: synthetic method
      CompositionPredicate(Predicate var1, Function var2, Object var3) {
         this(var1, var2);
      }
   }

   private static class InPredicate<T> implements Predicate<T>, Serializable {
      private final Collection<?> target;
      private static final long serialVersionUID = 0L;

      private InPredicate(Collection<?> var1) {
         super();
         this.target = (Collection)Preconditions.checkNotNull(var1);
      }

      public boolean apply(@Nullable T var1) {
         try {
            return this.target.contains(var1);
         } catch (NullPointerException var3) {
            return false;
         } catch (ClassCastException var4) {
            return false;
         }
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof Predicates.InPredicate) {
            Predicates.InPredicate var2 = (Predicates.InPredicate)var1;
            return this.target.equals(var2.target);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.target.hashCode();
      }

      public String toString() {
         return "Predicates.in(" + this.target + ")";
      }

      // $FF: synthetic method
      InPredicate(Collection var1, Object var2) {
         this(var1);
      }
   }

   @GwtIncompatible
   private static class SubtypeOfPredicate implements Predicate<Class<?>>, Serializable {
      private final Class<?> clazz;
      private static final long serialVersionUID = 0L;

      private SubtypeOfPredicate(Class<?> var1) {
         super();
         this.clazz = (Class)Preconditions.checkNotNull(var1);
      }

      public boolean apply(Class<?> var1) {
         return this.clazz.isAssignableFrom(var1);
      }

      public int hashCode() {
         return this.clazz.hashCode();
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof Predicates.SubtypeOfPredicate) {
            Predicates.SubtypeOfPredicate var2 = (Predicates.SubtypeOfPredicate)var1;
            return this.clazz == var2.clazz;
         } else {
            return false;
         }
      }

      public String toString() {
         return "Predicates.subtypeOf(" + this.clazz.getName() + ")";
      }

      // $FF: synthetic method
      SubtypeOfPredicate(Class var1, Object var2) {
         this(var1);
      }
   }

   @GwtIncompatible
   private static class InstanceOfPredicate implements Predicate<Object>, Serializable {
      private final Class<?> clazz;
      private static final long serialVersionUID = 0L;

      private InstanceOfPredicate(Class<?> var1) {
         super();
         this.clazz = (Class)Preconditions.checkNotNull(var1);
      }

      public boolean apply(@Nullable Object var1) {
         return this.clazz.isInstance(var1);
      }

      public int hashCode() {
         return this.clazz.hashCode();
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof Predicates.InstanceOfPredicate) {
            Predicates.InstanceOfPredicate var2 = (Predicates.InstanceOfPredicate)var1;
            return this.clazz == var2.clazz;
         } else {
            return false;
         }
      }

      public String toString() {
         return "Predicates.instanceOf(" + this.clazz.getName() + ")";
      }

      // $FF: synthetic method
      InstanceOfPredicate(Class var1, Object var2) {
         this(var1);
      }
   }

   private static class IsEqualToPredicate<T> implements Predicate<T>, Serializable {
      private final T target;
      private static final long serialVersionUID = 0L;

      private IsEqualToPredicate(T var1) {
         super();
         this.target = var1;
      }

      public boolean apply(T var1) {
         return this.target.equals(var1);
      }

      public int hashCode() {
         return this.target.hashCode();
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof Predicates.IsEqualToPredicate) {
            Predicates.IsEqualToPredicate var2 = (Predicates.IsEqualToPredicate)var1;
            return this.target.equals(var2.target);
         } else {
            return false;
         }
      }

      public String toString() {
         return "Predicates.equalTo(" + this.target + ")";
      }

      // $FF: synthetic method
      IsEqualToPredicate(Object var1, Object var2) {
         this(var1);
      }
   }

   private static class OrPredicate<T> implements Predicate<T>, Serializable {
      private final List<? extends Predicate<? super T>> components;
      private static final long serialVersionUID = 0L;

      private OrPredicate(List<? extends Predicate<? super T>> var1) {
         super();
         this.components = var1;
      }

      public boolean apply(@Nullable T var1) {
         for(int var2 = 0; var2 < this.components.size(); ++var2) {
            if (((Predicate)this.components.get(var2)).apply(var1)) {
               return true;
            }
         }

         return false;
      }

      public int hashCode() {
         return this.components.hashCode() + 87855567;
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof Predicates.OrPredicate) {
            Predicates.OrPredicate var2 = (Predicates.OrPredicate)var1;
            return this.components.equals(var2.components);
         } else {
            return false;
         }
      }

      public String toString() {
         return "Predicates.or(" + Predicates.COMMA_JOINER.join((Iterable)this.components) + ")";
      }

      // $FF: synthetic method
      OrPredicate(List var1, Object var2) {
         this(var1);
      }
   }

   private static class AndPredicate<T> implements Predicate<T>, Serializable {
      private final List<? extends Predicate<? super T>> components;
      private static final long serialVersionUID = 0L;

      private AndPredicate(List<? extends Predicate<? super T>> var1) {
         super();
         this.components = var1;
      }

      public boolean apply(@Nullable T var1) {
         for(int var2 = 0; var2 < this.components.size(); ++var2) {
            if (!((Predicate)this.components.get(var2)).apply(var1)) {
               return false;
            }
         }

         return true;
      }

      public int hashCode() {
         return this.components.hashCode() + 306654252;
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof Predicates.AndPredicate) {
            Predicates.AndPredicate var2 = (Predicates.AndPredicate)var1;
            return this.components.equals(var2.components);
         } else {
            return false;
         }
      }

      public String toString() {
         return "Predicates.and(" + Predicates.COMMA_JOINER.join((Iterable)this.components) + ")";
      }

      // $FF: synthetic method
      AndPredicate(List var1, Object var2) {
         this(var1);
      }
   }

   private static class NotPredicate<T> implements Predicate<T>, Serializable {
      final Predicate<T> predicate;
      private static final long serialVersionUID = 0L;

      NotPredicate(Predicate<T> var1) {
         super();
         this.predicate = (Predicate)Preconditions.checkNotNull(var1);
      }

      public boolean apply(@Nullable T var1) {
         return !this.predicate.apply(var1);
      }

      public int hashCode() {
         return ~this.predicate.hashCode();
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof Predicates.NotPredicate) {
            Predicates.NotPredicate var2 = (Predicates.NotPredicate)var1;
            return this.predicate.equals(var2.predicate);
         } else {
            return false;
         }
      }

      public String toString() {
         return "Predicates.not(" + this.predicate + ")";
      }
   }

   static enum ObjectPredicate implements Predicate<Object> {
      ALWAYS_TRUE {
         public boolean apply(@Nullable Object var1) {
            return true;
         }

         public String toString() {
            return "Predicates.alwaysTrue()";
         }
      },
      ALWAYS_FALSE {
         public boolean apply(@Nullable Object var1) {
            return false;
         }

         public String toString() {
            return "Predicates.alwaysFalse()";
         }
      },
      IS_NULL {
         public boolean apply(@Nullable Object var1) {
            return var1 == null;
         }

         public String toString() {
            return "Predicates.isNull()";
         }
      },
      NOT_NULL {
         public boolean apply(@Nullable Object var1) {
            return var1 != null;
         }

         public String toString() {
            return "Predicates.notNull()";
         }
      };

      private ObjectPredicate() {
      }

      <T> Predicate<T> withNarrowedType() {
         return this;
      }

      // $FF: synthetic method
      ObjectPredicate(Object var3) {
         this();
      }
   }
}
