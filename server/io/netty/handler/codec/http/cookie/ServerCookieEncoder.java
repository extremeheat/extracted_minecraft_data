package io.netty.handler.codec.http.cookie;

import io.netty.handler.codec.DateFormatter;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ServerCookieEncoder extends CookieEncoder {
   public static final ServerCookieEncoder STRICT = new ServerCookieEncoder(true);
   public static final ServerCookieEncoder LAX = new ServerCookieEncoder(false);

   private ServerCookieEncoder(boolean var1) {
      super(var1);
   }

   public String encode(String var1, String var2) {
      return this.encode((Cookie)(new DefaultCookie(var1, var2)));
   }

   public String encode(Cookie var1) {
      String var2 = ((Cookie)ObjectUtil.checkNotNull(var1, "cookie")).name();
      String var3 = var1.value() != null ? var1.value() : "";
      this.validateCookie(var2, var3);
      StringBuilder var4 = CookieUtil.stringBuilder();
      if (var1.wrap()) {
         CookieUtil.addQuoted(var4, var2, var3);
      } else {
         CookieUtil.add(var4, var2, var3);
      }

      if (var1.maxAge() != -9223372036854775808L) {
         CookieUtil.add(var4, "Max-Age", var1.maxAge());
         Date var5 = new Date(var1.maxAge() * 1000L + System.currentTimeMillis());
         var4.append("Expires");
         var4.append('=');
         DateFormatter.append(var5, var4);
         var4.append(';');
         var4.append(' ');
      }

      if (var1.path() != null) {
         CookieUtil.add(var4, "Path", var1.path());
      }

      if (var1.domain() != null) {
         CookieUtil.add(var4, "Domain", var1.domain());
      }

      if (var1.isSecure()) {
         CookieUtil.add(var4, "Secure");
      }

      if (var1.isHttpOnly()) {
         CookieUtil.add(var4, "HTTPOnly");
      }

      return CookieUtil.stripTrailingSeparator(var4);
   }

   private static List<String> dedup(List<String> var0, Map<String, Integer> var1) {
      boolean[] var2 = new boolean[var0.size()];

      int var4;
      for(Iterator var3 = var1.values().iterator(); var3.hasNext(); var2[var4] = true) {
         var4 = (Integer)var3.next();
      }

      ArrayList var6 = new ArrayList(var1.size());
      var4 = 0;

      for(int var5 = var0.size(); var4 < var5; ++var4) {
         if (var2[var4]) {
            var6.add(var0.get(var4));
         }
      }

      return var6;
   }

   public List<String> encode(Cookie... var1) {
      if (((Cookie[])ObjectUtil.checkNotNull(var1, "cookies")).length == 0) {
         return Collections.emptyList();
      } else {
         ArrayList var2 = new ArrayList(var1.length);
         HashMap var3 = this.strict && var1.length > 1 ? new HashMap() : null;
         boolean var4 = false;

         for(int var5 = 0; var5 < var1.length; ++var5) {
            Cookie var6 = var1[var5];
            var2.add(this.encode(var6));
            if (var3 != null) {
               var4 |= var3.put(var6.name(), var5) != null;
            }
         }

         return (List)(var4 ? dedup(var2, var3) : var2);
      }
   }

   public List<String> encode(Collection<? extends Cookie> var1) {
      if (((Collection)ObjectUtil.checkNotNull(var1, "cookies")).isEmpty()) {
         return Collections.emptyList();
      } else {
         ArrayList var2 = new ArrayList(var1.size());
         HashMap var3 = this.strict && var1.size() > 1 ? new HashMap() : null;
         int var4 = 0;
         boolean var5 = false;
         Iterator var6 = var1.iterator();

         while(var6.hasNext()) {
            Cookie var7 = (Cookie)var6.next();
            var2.add(this.encode(var7));
            if (var3 != null) {
               var5 |= var3.put(var7.name(), var4++) != null;
            }
         }

         return (List)(var5 ? dedup(var2, var3) : var2);
      }
   }

   public List<String> encode(Iterable<? extends Cookie> var1) {
      Iterator var2 = ((Iterable)ObjectUtil.checkNotNull(var1, "cookies")).iterator();
      if (!var2.hasNext()) {
         return Collections.emptyList();
      } else {
         ArrayList var3 = new ArrayList();
         Cookie var4 = (Cookie)var2.next();
         HashMap var5 = this.strict && var2.hasNext() ? new HashMap() : null;
         int var6 = 0;
         var3.add(this.encode(var4));
         boolean var7 = var5 != null && var5.put(var4.name(), var6++) != null;

         while(var2.hasNext()) {
            Cookie var8 = (Cookie)var2.next();
            var3.add(this.encode(var8));
            if (var5 != null) {
               var7 |= var5.put(var8.name(), var6++) != null;
            }
         }

         return (List)(var7 ? dedup(var3, var5) : var3);
      }
   }
}
