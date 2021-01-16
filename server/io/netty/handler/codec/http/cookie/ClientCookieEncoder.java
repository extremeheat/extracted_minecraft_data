package io.netty.handler.codec.http.cookie;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public final class ClientCookieEncoder extends CookieEncoder {
   public static final ClientCookieEncoder STRICT = new ClientCookieEncoder(true);
   public static final ClientCookieEncoder LAX = new ClientCookieEncoder(false);
   private static final Comparator<Cookie> COOKIE_COMPARATOR = new Comparator<Cookie>() {
      public int compare(Cookie var1, Cookie var2) {
         String var3 = var1.path();
         String var4 = var2.path();
         int var5 = var3 == null ? 2147483647 : var3.length();
         int var6 = var4 == null ? 2147483647 : var4.length();
         int var7 = var6 - var5;
         return var7 != 0 ? var7 : -1;
      }
   };

   private ClientCookieEncoder(boolean var1) {
      super(var1);
   }

   public String encode(String var1, String var2) {
      return this.encode((Cookie)(new DefaultCookie(var1, var2)));
   }

   public String encode(Cookie var1) {
      StringBuilder var2 = CookieUtil.stringBuilder();
      this.encode(var2, (Cookie)ObjectUtil.checkNotNull(var1, "cookie"));
      return CookieUtil.stripTrailingSeparator(var2);
   }

   public String encode(Cookie... var1) {
      if (((Cookie[])ObjectUtil.checkNotNull(var1, "cookies")).length == 0) {
         return null;
      } else {
         StringBuilder var2 = CookieUtil.stringBuilder();
         Cookie[] var3;
         int var5;
         if (this.strict) {
            if (var1.length == 1) {
               this.encode(var2, var1[0]);
            } else {
               var3 = (Cookie[])Arrays.copyOf(var1, var1.length);
               Arrays.sort(var3, COOKIE_COMPARATOR);
               Cookie[] var4 = var3;
               var5 = var3.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  Cookie var7 = var4[var6];
                  this.encode(var2, var7);
               }
            }
         } else {
            var3 = var1;
            int var8 = var1.length;

            for(var5 = 0; var5 < var8; ++var5) {
               Cookie var9 = var3[var5];
               this.encode(var2, var9);
            }
         }

         return CookieUtil.stripTrailingSeparatorOrNull(var2);
      }
   }

   public String encode(Collection<? extends Cookie> var1) {
      if (((Collection)ObjectUtil.checkNotNull(var1, "cookies")).isEmpty()) {
         return null;
      } else {
         StringBuilder var2 = CookieUtil.stringBuilder();
         if (this.strict) {
            if (var1.size() == 1) {
               this.encode(var2, (Cookie)var1.iterator().next());
            } else {
               Cookie[] var3 = (Cookie[])var1.toArray(new Cookie[var1.size()]);
               Arrays.sort(var3, COOKIE_COMPARATOR);
               Cookie[] var4 = var3;
               int var5 = var3.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  Cookie var7 = var4[var6];
                  this.encode(var2, var7);
               }
            }
         } else {
            Iterator var8 = var1.iterator();

            while(var8.hasNext()) {
               Cookie var9 = (Cookie)var8.next();
               this.encode(var2, var9);
            }
         }

         return CookieUtil.stripTrailingSeparatorOrNull(var2);
      }
   }

   public String encode(Iterable<? extends Cookie> var1) {
      Iterator var2 = ((Iterable)ObjectUtil.checkNotNull(var1, "cookies")).iterator();
      if (!var2.hasNext()) {
         return null;
      } else {
         StringBuilder var3 = CookieUtil.stringBuilder();
         if (this.strict) {
            Cookie var4 = (Cookie)var2.next();
            if (!var2.hasNext()) {
               this.encode(var3, var4);
            } else {
               ArrayList var5 = InternalThreadLocalMap.get().arrayList();
               var5.add(var4);

               while(var2.hasNext()) {
                  var5.add(var2.next());
               }

               Cookie[] var6 = (Cookie[])var5.toArray(new Cookie[var5.size()]);
               Arrays.sort(var6, COOKIE_COMPARATOR);
               Cookie[] var7 = var6;
               int var8 = var6.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  Cookie var10 = var7[var9];
                  this.encode(var3, var10);
               }
            }
         } else {
            while(var2.hasNext()) {
               this.encode(var3, (Cookie)var2.next());
            }
         }

         return CookieUtil.stripTrailingSeparatorOrNull(var3);
      }
   }

   private void encode(StringBuilder var1, Cookie var2) {
      String var3 = var2.name();
      String var4 = var2.value() != null ? var2.value() : "";
      this.validateCookie(var3, var4);
      if (var2.wrap()) {
         CookieUtil.addQuoted(var1, var3, var4);
      } else {
         CookieUtil.add(var1, var3, var4);
      }

   }
}
