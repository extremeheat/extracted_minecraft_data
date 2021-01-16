package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.NoSuchElementException;

@GwtCompatible
public abstract class DiscreteDomain<C extends Comparable> {
   public static DiscreteDomain<Integer> integers() {
      return DiscreteDomain.IntegerDomain.INSTANCE;
   }

   public static DiscreteDomain<Long> longs() {
      return DiscreteDomain.LongDomain.INSTANCE;
   }

   public static DiscreteDomain<BigInteger> bigIntegers() {
      return DiscreteDomain.BigIntegerDomain.INSTANCE;
   }

   protected DiscreteDomain() {
      super();
   }

   public abstract C next(C var1);

   public abstract C previous(C var1);

   public abstract long distance(C var1, C var2);

   @CanIgnoreReturnValue
   public C minValue() {
      throw new NoSuchElementException();
   }

   @CanIgnoreReturnValue
   public C maxValue() {
      throw new NoSuchElementException();
   }

   private static final class BigIntegerDomain extends DiscreteDomain<BigInteger> implements Serializable {
      private static final DiscreteDomain.BigIntegerDomain INSTANCE = new DiscreteDomain.BigIntegerDomain();
      private static final BigInteger MIN_LONG = BigInteger.valueOf(-9223372036854775808L);
      private static final BigInteger MAX_LONG = BigInteger.valueOf(9223372036854775807L);
      private static final long serialVersionUID = 0L;

      private BigIntegerDomain() {
         super();
      }

      public BigInteger next(BigInteger var1) {
         return var1.add(BigInteger.ONE);
      }

      public BigInteger previous(BigInteger var1) {
         return var1.subtract(BigInteger.ONE);
      }

      public long distance(BigInteger var1, BigInteger var2) {
         return var2.subtract(var1).max(MIN_LONG).min(MAX_LONG).longValue();
      }

      private Object readResolve() {
         return INSTANCE;
      }

      public String toString() {
         return "DiscreteDomain.bigIntegers()";
      }
   }

   private static final class LongDomain extends DiscreteDomain<Long> implements Serializable {
      private static final DiscreteDomain.LongDomain INSTANCE = new DiscreteDomain.LongDomain();
      private static final long serialVersionUID = 0L;

      private LongDomain() {
         super();
      }

      public Long next(Long var1) {
         long var2 = var1;
         return var2 == 9223372036854775807L ? null : var2 + 1L;
      }

      public Long previous(Long var1) {
         long var2 = var1;
         return var2 == -9223372036854775808L ? null : var2 - 1L;
      }

      public long distance(Long var1, Long var2) {
         long var3 = var2 - var1;
         if (var2 > var1 && var3 < 0L) {
            return 9223372036854775807L;
         } else {
            return var2 < var1 && var3 > 0L ? -9223372036854775808L : var3;
         }
      }

      public Long minValue() {
         return -9223372036854775808L;
      }

      public Long maxValue() {
         return 9223372036854775807L;
      }

      private Object readResolve() {
         return INSTANCE;
      }

      public String toString() {
         return "DiscreteDomain.longs()";
      }
   }

   private static final class IntegerDomain extends DiscreteDomain<Integer> implements Serializable {
      private static final DiscreteDomain.IntegerDomain INSTANCE = new DiscreteDomain.IntegerDomain();
      private static final long serialVersionUID = 0L;

      private IntegerDomain() {
         super();
      }

      public Integer next(Integer var1) {
         int var2 = var1;
         return var2 == 2147483647 ? null : var2 + 1;
      }

      public Integer previous(Integer var1) {
         int var2 = var1;
         return var2 == -2147483648 ? null : var2 - 1;
      }

      public long distance(Integer var1, Integer var2) {
         return (long)var2 - (long)var1;
      }

      public Integer minValue() {
         return -2147483648;
      }

      public Integer maxValue() {
         return 2147483647;
      }

      private Object readResolve() {
         return INSTANCE;
      }

      public String toString() {
         return "DiscreteDomain.integers()";
      }
   }
}
