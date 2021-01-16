package io.netty.handler.codec.http;

import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.AsciiString;
import io.netty.util.HashingStrategy;
import io.netty.util.internal.StringUtil;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class CombinedHttpHeaders extends DefaultHttpHeaders {
   public CombinedHttpHeaders(boolean var1) {
      super(new CombinedHttpHeaders.CombinedHttpHeadersImpl(AsciiString.CASE_INSENSITIVE_HASHER, valueConverter(var1), nameValidator(var1)));
   }

   public boolean containsValue(CharSequence var1, CharSequence var2, boolean var3) {
      return super.containsValue(var1, StringUtil.trimOws(var2), var3);
   }

   private static final class CombinedHttpHeadersImpl extends DefaultHeaders<CharSequence, CharSequence, CombinedHttpHeaders.CombinedHttpHeadersImpl> {
      private static final int VALUE_LENGTH_ESTIMATE = 10;
      private CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<Object> objectEscaper;
      private CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<CharSequence> charSequenceEscaper;

      private CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<Object> objectEscaper() {
         if (this.objectEscaper == null) {
            this.objectEscaper = new CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<Object>() {
               public CharSequence escape(Object var1) {
                  return StringUtil.escapeCsv((CharSequence)CombinedHttpHeadersImpl.this.valueConverter().convertObject(var1), true);
               }
            };
         }

         return this.objectEscaper;
      }

      private CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<CharSequence> charSequenceEscaper() {
         if (this.charSequenceEscaper == null) {
            this.charSequenceEscaper = new CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<CharSequence>() {
               public CharSequence escape(CharSequence var1) {
                  return StringUtil.escapeCsv(var1, true);
               }
            };
         }

         return this.charSequenceEscaper;
      }

      public CombinedHttpHeadersImpl(HashingStrategy<CharSequence> var1, ValueConverter<CharSequence> var2, DefaultHeaders.NameValidator<CharSequence> var3) {
         super(var1, var2, var3);
      }

      public Iterator<CharSequence> valueIterator(CharSequence var1) {
         Iterator var2 = super.valueIterator(var1);
         if (!var2.hasNext()) {
            return var2;
         } else {
            Iterator var3 = StringUtil.unescapeCsvFields((CharSequence)var2.next()).iterator();
            if (var2.hasNext()) {
               throw new IllegalStateException("CombinedHttpHeaders should only have one value");
            } else {
               return var3;
            }
         }
      }

      public List<CharSequence> getAll(CharSequence var1) {
         List var2 = super.getAll(var1);
         if (var2.isEmpty()) {
            return var2;
         } else if (var2.size() != 1) {
            throw new IllegalStateException("CombinedHttpHeaders should only have one value");
         } else {
            return StringUtil.unescapeCsvFields((CharSequence)var2.get(0));
         }
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl add(Headers<? extends CharSequence, ? extends CharSequence, ?> var1) {
         if (var1 == this) {
            throw new IllegalArgumentException("can't add to itself.");
         } else {
            Iterator var2;
            Entry var3;
            if (var1 instanceof CombinedHttpHeaders.CombinedHttpHeadersImpl) {
               if (this.isEmpty()) {
                  this.addImpl(var1);
               } else {
                  var2 = var1.iterator();

                  while(var2.hasNext()) {
                     var3 = (Entry)var2.next();
                     this.addEscapedValue((CharSequence)var3.getKey(), (CharSequence)var3.getValue());
                  }
               }
            } else {
               var2 = var1.iterator();

               while(var2.hasNext()) {
                  var3 = (Entry)var2.next();
                  this.add((CharSequence)var3.getKey(), (CharSequence)var3.getValue());
               }
            }

            return this;
         }
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl set(Headers<? extends CharSequence, ? extends CharSequence, ?> var1) {
         if (var1 == this) {
            return this;
         } else {
            this.clear();
            return this.add(var1);
         }
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl setAll(Headers<? extends CharSequence, ? extends CharSequence, ?> var1) {
         if (var1 == this) {
            return this;
         } else {
            Iterator var2 = var1.names().iterator();

            while(var2.hasNext()) {
               CharSequence var3 = (CharSequence)var2.next();
               this.remove(var3);
            }

            return this.add(var1);
         }
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl add(CharSequence var1, CharSequence var2) {
         return this.addEscapedValue(var1, this.charSequenceEscaper().escape(var2));
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl add(CharSequence var1, CharSequence... var2) {
         return this.addEscapedValue(var1, commaSeparate(this.charSequenceEscaper(), (Object[])var2));
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl add(CharSequence var1, Iterable<? extends CharSequence> var2) {
         return this.addEscapedValue(var1, commaSeparate(this.charSequenceEscaper(), var2));
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl addObject(CharSequence var1, Object var2) {
         return this.addEscapedValue(var1, commaSeparate(this.objectEscaper(), var2));
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl addObject(CharSequence var1, Iterable<?> var2) {
         return this.addEscapedValue(var1, commaSeparate(this.objectEscaper(), var2));
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl addObject(CharSequence var1, Object... var2) {
         return this.addEscapedValue(var1, commaSeparate(this.objectEscaper(), var2));
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl set(CharSequence var1, CharSequence... var2) {
         super.set(var1, (Object)commaSeparate(this.charSequenceEscaper(), (Object[])var2));
         return this;
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl set(CharSequence var1, Iterable<? extends CharSequence> var2) {
         super.set(var1, (Object)commaSeparate(this.charSequenceEscaper(), var2));
         return this;
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl setObject(CharSequence var1, Object var2) {
         super.set(var1, (Object)commaSeparate(this.objectEscaper(), var2));
         return this;
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl setObject(CharSequence var1, Object... var2) {
         super.set(var1, (Object)commaSeparate(this.objectEscaper(), var2));
         return this;
      }

      public CombinedHttpHeaders.CombinedHttpHeadersImpl setObject(CharSequence var1, Iterable<?> var2) {
         super.set(var1, (Object)commaSeparate(this.objectEscaper(), var2));
         return this;
      }

      private CombinedHttpHeaders.CombinedHttpHeadersImpl addEscapedValue(CharSequence var1, CharSequence var2) {
         CharSequence var3 = (CharSequence)super.get(var1);
         if (var3 == null) {
            super.add(var1, (Object)var2);
         } else {
            super.set(var1, (Object)commaSeparateEscapedValues(var3, var2));
         }

         return this;
      }

      private static <T> CharSequence commaSeparate(CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<T> var0, T... var1) {
         StringBuilder var2 = new StringBuilder(var1.length * 10);
         if (var1.length > 0) {
            int var3 = var1.length - 1;

            for(int var4 = 0; var4 < var3; ++var4) {
               var2.append(var0.escape(var1[var4])).append(',');
            }

            var2.append(var0.escape(var1[var3]));
         }

         return var2;
      }

      private static <T> CharSequence commaSeparate(CombinedHttpHeaders.CombinedHttpHeadersImpl.CsvValueEscaper<T> var0, Iterable<? extends T> var1) {
         StringBuilder var2 = var1 instanceof Collection ? new StringBuilder(((Collection)var1).size() * 10) : new StringBuilder();
         Iterator var3 = var1.iterator();
         if (var3.hasNext()) {
            Object var4;
            for(var4 = var3.next(); var3.hasNext(); var4 = var3.next()) {
               var2.append(var0.escape(var4)).append(',');
            }

            var2.append(var0.escape(var4));
         }

         return var2;
      }

      private static CharSequence commaSeparateEscapedValues(CharSequence var0, CharSequence var1) {
         return (new StringBuilder(var0.length() + 1 + var1.length())).append(var0).append(',').append(var1);
      }

      private interface CsvValueEscaper<T> {
         CharSequence escape(T var1);
      }
   }
}
