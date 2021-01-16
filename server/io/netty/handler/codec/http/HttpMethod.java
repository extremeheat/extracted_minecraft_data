package io.netty.handler.codec.http;

import io.netty.util.AsciiString;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;

public class HttpMethod implements Comparable<HttpMethod> {
   public static final HttpMethod OPTIONS = new HttpMethod("OPTIONS");
   public static final HttpMethod GET = new HttpMethod("GET");
   public static final HttpMethod HEAD = new HttpMethod("HEAD");
   public static final HttpMethod POST = new HttpMethod("POST");
   public static final HttpMethod PUT = new HttpMethod("PUT");
   public static final HttpMethod PATCH = new HttpMethod("PATCH");
   public static final HttpMethod DELETE = new HttpMethod("DELETE");
   public static final HttpMethod TRACE = new HttpMethod("TRACE");
   public static final HttpMethod CONNECT = new HttpMethod("CONNECT");
   private static final HttpMethod.EnumNameMap<HttpMethod> methodMap;
   private final AsciiString name;

   public static HttpMethod valueOf(String var0) {
      HttpMethod var1 = (HttpMethod)methodMap.get(var0);
      return var1 != null ? var1 : new HttpMethod(var0);
   }

   public HttpMethod(String var1) {
      super();
      var1 = ((String)ObjectUtil.checkNotNull(var1, "name")).trim();
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("empty name");
      } else {
         for(int var2 = 0; var2 < var1.length(); ++var2) {
            char var3 = var1.charAt(var2);
            if (Character.isISOControl(var3) || Character.isWhitespace(var3)) {
               throw new IllegalArgumentException("invalid character in name");
            }
         }

         this.name = AsciiString.cached(var1);
      }
   }

   public String name() {
      return this.name.toString();
   }

   public AsciiString asciiName() {
      return this.name;
   }

   public int hashCode() {
      return this.name().hashCode();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof HttpMethod)) {
         return false;
      } else {
         HttpMethod var2 = (HttpMethod)var1;
         return this.name().equals(var2.name());
      }
   }

   public String toString() {
      return this.name.toString();
   }

   public int compareTo(HttpMethod var1) {
      return this.name().compareTo(var1.name());
   }

   static {
      methodMap = new HttpMethod.EnumNameMap(new HttpMethod.EnumNameMap.Node[]{new HttpMethod.EnumNameMap.Node(OPTIONS.toString(), OPTIONS), new HttpMethod.EnumNameMap.Node(GET.toString(), GET), new HttpMethod.EnumNameMap.Node(HEAD.toString(), HEAD), new HttpMethod.EnumNameMap.Node(POST.toString(), POST), new HttpMethod.EnumNameMap.Node(PUT.toString(), PUT), new HttpMethod.EnumNameMap.Node(PATCH.toString(), PATCH), new HttpMethod.EnumNameMap.Node(DELETE.toString(), DELETE), new HttpMethod.EnumNameMap.Node(TRACE.toString(), TRACE), new HttpMethod.EnumNameMap.Node(CONNECT.toString(), CONNECT)});
   }

   private static final class EnumNameMap<T> {
      private final HttpMethod.EnumNameMap.Node<T>[] values;
      private final int valuesMask;

      EnumNameMap(HttpMethod.EnumNameMap.Node<T>... var1) {
         super();
         this.values = (HttpMethod.EnumNameMap.Node[])(new HttpMethod.EnumNameMap.Node[MathUtil.findNextPositivePowerOfTwo(var1.length)]);
         this.valuesMask = this.values.length - 1;
         HttpMethod.EnumNameMap.Node[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            HttpMethod.EnumNameMap.Node var5 = var2[var4];
            int var6 = hashCode(var5.key) & this.valuesMask;
            if (this.values[var6] != null) {
               throw new IllegalArgumentException("index " + var6 + " collision between values: [" + this.values[var6].key + ", " + var5.key + ']');
            }

            this.values[var6] = var5;
         }

      }

      T get(String var1) {
         HttpMethod.EnumNameMap.Node var2 = this.values[hashCode(var1) & this.valuesMask];
         return var2 != null && var2.key.equals(var1) ? var2.value : null;
      }

      private static int hashCode(String var0) {
         return var0.hashCode() >>> 6;
      }

      private static final class Node<T> {
         final String key;
         final T value;

         Node(String var1, T var2) {
            super();
            this.key = var1;
            this.value = var2;
         }
      }
   }
}
