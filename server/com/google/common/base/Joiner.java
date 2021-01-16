package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
public class Joiner {
   private final String separator;

   public static Joiner on(String var0) {
      return new Joiner(var0);
   }

   public static Joiner on(char var0) {
      return new Joiner(String.valueOf(var0));
   }

   private Joiner(String var1) {
      super();
      this.separator = (String)Preconditions.checkNotNull(var1);
   }

   private Joiner(Joiner var1) {
      super();
      this.separator = var1.separator;
   }

   @CanIgnoreReturnValue
   public <A extends Appendable> A appendTo(A var1, Iterable<?> var2) throws IOException {
      return this.appendTo(var1, var2.iterator());
   }

   @CanIgnoreReturnValue
   public <A extends Appendable> A appendTo(A var1, Iterator<?> var2) throws IOException {
      Preconditions.checkNotNull(var1);
      if (var2.hasNext()) {
         var1.append(this.toString(var2.next()));

         while(var2.hasNext()) {
            var1.append(this.separator);
            var1.append(this.toString(var2.next()));
         }
      }

      return var1;
   }

   @CanIgnoreReturnValue
   public final <A extends Appendable> A appendTo(A var1, Object[] var2) throws IOException {
      return this.appendTo((Appendable)var1, (Iterable)Arrays.asList(var2));
   }

   @CanIgnoreReturnValue
   public final <A extends Appendable> A appendTo(A var1, @Nullable Object var2, @Nullable Object var3, Object... var4) throws IOException {
      return this.appendTo(var1, iterable(var2, var3, var4));
   }

   @CanIgnoreReturnValue
   public final StringBuilder appendTo(StringBuilder var1, Iterable<?> var2) {
      return this.appendTo(var1, var2.iterator());
   }

   @CanIgnoreReturnValue
   public final StringBuilder appendTo(StringBuilder var1, Iterator<?> var2) {
      try {
         this.appendTo((Appendable)var1, (Iterator)var2);
         return var1;
      } catch (IOException var4) {
         throw new AssertionError(var4);
      }
   }

   @CanIgnoreReturnValue
   public final StringBuilder appendTo(StringBuilder var1, Object[] var2) {
      return this.appendTo((StringBuilder)var1, (Iterable)Arrays.asList(var2));
   }

   @CanIgnoreReturnValue
   public final StringBuilder appendTo(StringBuilder var1, @Nullable Object var2, @Nullable Object var3, Object... var4) {
      return this.appendTo(var1, iterable(var2, var3, var4));
   }

   public final String join(Iterable<?> var1) {
      return this.join(var1.iterator());
   }

   public final String join(Iterator<?> var1) {
      return this.appendTo(new StringBuilder(), var1).toString();
   }

   public final String join(Object[] var1) {
      return this.join((Iterable)Arrays.asList(var1));
   }

   public final String join(@Nullable Object var1, @Nullable Object var2, Object... var3) {
      return this.join(iterable(var1, var2, var3));
   }

   public Joiner useForNull(final String var1) {
      Preconditions.checkNotNull(var1);
      return new Joiner(this) {
         CharSequence toString(@Nullable Object var1x) {
            return (CharSequence)(var1x == null ? var1 : Joiner.this.toString(var1x));
         }

         public Joiner useForNull(String var1x) {
            throw new UnsupportedOperationException("already specified useForNull");
         }

         public Joiner skipNulls() {
            throw new UnsupportedOperationException("already specified useForNull");
         }
      };
   }

   public Joiner skipNulls() {
      return new Joiner(this) {
         public <A extends Appendable> A appendTo(A var1, Iterator<?> var2) throws IOException {
            Preconditions.checkNotNull(var1, "appendable");
            Preconditions.checkNotNull(var2, "parts");

            Object var3;
            while(var2.hasNext()) {
               var3 = var2.next();
               if (var3 != null) {
                  var1.append(Joiner.this.toString(var3));
                  break;
               }
            }

            while(var2.hasNext()) {
               var3 = var2.next();
               if (var3 != null) {
                  var1.append(Joiner.this.separator);
                  var1.append(Joiner.this.toString(var3));
               }
            }

            return var1;
         }

         public Joiner useForNull(String var1) {
            throw new UnsupportedOperationException("already specified skipNulls");
         }

         public Joiner.MapJoiner withKeyValueSeparator(String var1) {
            throw new UnsupportedOperationException("can't use .skipNulls() with maps");
         }
      };
   }

   public Joiner.MapJoiner withKeyValueSeparator(char var1) {
      return this.withKeyValueSeparator(String.valueOf(var1));
   }

   public Joiner.MapJoiner withKeyValueSeparator(String var1) {
      return new Joiner.MapJoiner(this, var1);
   }

   CharSequence toString(Object var1) {
      Preconditions.checkNotNull(var1);
      return (CharSequence)(var1 instanceof CharSequence ? (CharSequence)var1 : var1.toString());
   }

   private static Iterable<Object> iterable(final Object var0, final Object var1, final Object[] var2) {
      Preconditions.checkNotNull(var2);
      return new AbstractList<Object>() {
         public int size() {
            return var2.length + 2;
         }

         public Object get(int var1x) {
            switch(var1x) {
            case 0:
               return var0;
            case 1:
               return var1;
            default:
               return var2[var1x - 2];
            }
         }
      };
   }

   // $FF: synthetic method
   Joiner(Joiner var1, Object var2) {
      this(var1);
   }

   public static final class MapJoiner {
      private final Joiner joiner;
      private final String keyValueSeparator;

      private MapJoiner(Joiner var1, String var2) {
         super();
         this.joiner = var1;
         this.keyValueSeparator = (String)Preconditions.checkNotNull(var2);
      }

      @CanIgnoreReturnValue
      public <A extends Appendable> A appendTo(A var1, Map<?, ?> var2) throws IOException {
         return this.appendTo((Appendable)var1, (Iterable)var2.entrySet());
      }

      @CanIgnoreReturnValue
      public StringBuilder appendTo(StringBuilder var1, Map<?, ?> var2) {
         return this.appendTo((StringBuilder)var1, (Iterable)var2.entrySet());
      }

      public String join(Map<?, ?> var1) {
         return this.join((Iterable)var1.entrySet());
      }

      @Beta
      @CanIgnoreReturnValue
      public <A extends Appendable> A appendTo(A var1, Iterable<? extends Entry<?, ?>> var2) throws IOException {
         return this.appendTo(var1, var2.iterator());
      }

      @Beta
      @CanIgnoreReturnValue
      public <A extends Appendable> A appendTo(A var1, Iterator<? extends Entry<?, ?>> var2) throws IOException {
         Preconditions.checkNotNull(var1);
         if (var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            var1.append(this.joiner.toString(var3.getKey()));
            var1.append(this.keyValueSeparator);
            var1.append(this.joiner.toString(var3.getValue()));

            while(var2.hasNext()) {
               var1.append(this.joiner.separator);
               Entry var4 = (Entry)var2.next();
               var1.append(this.joiner.toString(var4.getKey()));
               var1.append(this.keyValueSeparator);
               var1.append(this.joiner.toString(var4.getValue()));
            }
         }

         return var1;
      }

      @Beta
      @CanIgnoreReturnValue
      public StringBuilder appendTo(StringBuilder var1, Iterable<? extends Entry<?, ?>> var2) {
         return this.appendTo(var1, var2.iterator());
      }

      @Beta
      @CanIgnoreReturnValue
      public StringBuilder appendTo(StringBuilder var1, Iterator<? extends Entry<?, ?>> var2) {
         try {
            this.appendTo((Appendable)var1, (Iterator)var2);
            return var1;
         } catch (IOException var4) {
            throw new AssertionError(var4);
         }
      }

      @Beta
      public String join(Iterable<? extends Entry<?, ?>> var1) {
         return this.join(var1.iterator());
      }

      @Beta
      public String join(Iterator<? extends Entry<?, ?>> var1) {
         return this.appendTo(new StringBuilder(), var1).toString();
      }

      public Joiner.MapJoiner useForNull(String var1) {
         return new Joiner.MapJoiner(this.joiner.useForNull(var1), this.keyValueSeparator);
      }

      // $FF: synthetic method
      MapJoiner(Joiner var1, String var2, Object var3) {
         this(var1, var2);
      }
   }
}
