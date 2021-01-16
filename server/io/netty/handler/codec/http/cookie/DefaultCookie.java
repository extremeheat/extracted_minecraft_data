package io.netty.handler.codec.http.cookie;

import io.netty.util.internal.ObjectUtil;

public class DefaultCookie implements Cookie {
   private final String name;
   private String value;
   private boolean wrap;
   private String domain;
   private String path;
   private long maxAge = -9223372036854775808L;
   private boolean secure;
   private boolean httpOnly;

   public DefaultCookie(String var1, String var2) {
      super();
      var1 = ((String)ObjectUtil.checkNotNull(var1, "name")).trim();
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("empty name");
      } else {
         this.name = var1;
         this.setValue(var2);
      }
   }

   public String name() {
      return this.name;
   }

   public String value() {
      return this.value;
   }

   public void setValue(String var1) {
      this.value = (String)ObjectUtil.checkNotNull(var1, "value");
   }

   public boolean wrap() {
      return this.wrap;
   }

   public void setWrap(boolean var1) {
      this.wrap = var1;
   }

   public String domain() {
      return this.domain;
   }

   public void setDomain(String var1) {
      this.domain = CookieUtil.validateAttributeValue("domain", var1);
   }

   public String path() {
      return this.path;
   }

   public void setPath(String var1) {
      this.path = CookieUtil.validateAttributeValue("path", var1);
   }

   public long maxAge() {
      return this.maxAge;
   }

   public void setMaxAge(long var1) {
      this.maxAge = var1;
   }

   public boolean isSecure() {
      return this.secure;
   }

   public void setSecure(boolean var1) {
      this.secure = var1;
   }

   public boolean isHttpOnly() {
      return this.httpOnly;
   }

   public void setHttpOnly(boolean var1) {
      this.httpOnly = var1;
   }

   public int hashCode() {
      return this.name().hashCode();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Cookie)) {
         return false;
      } else {
         Cookie var2 = (Cookie)var1;
         if (!this.name().equals(var2.name())) {
            return false;
         } else {
            if (this.path() == null) {
               if (var2.path() != null) {
                  return false;
               }
            } else {
               if (var2.path() == null) {
                  return false;
               }

               if (!this.path().equals(var2.path())) {
                  return false;
               }
            }

            if (this.domain() == null) {
               return var2.domain() == null;
            } else {
               return this.domain().equalsIgnoreCase(var2.domain());
            }
         }
      }
   }

   public int compareTo(Cookie var1) {
      int var2 = this.name().compareTo(var1.name());
      if (var2 != 0) {
         return var2;
      } else {
         if (this.path() == null) {
            if (var1.path() != null) {
               return -1;
            }
         } else {
            if (var1.path() == null) {
               return 1;
            }

            var2 = this.path().compareTo(var1.path());
            if (var2 != 0) {
               return var2;
            }
         }

         if (this.domain() == null) {
            return var1.domain() != null ? -1 : 0;
         } else if (var1.domain() == null) {
            return 1;
         } else {
            var2 = this.domain().compareToIgnoreCase(var1.domain());
            return var2;
         }
      }
   }

   /** @deprecated */
   @Deprecated
   protected String validateValue(String var1, String var2) {
      return CookieUtil.validateAttributeValue(var1, var2);
   }

   public String toString() {
      StringBuilder var1 = CookieUtil.stringBuilder().append(this.name()).append('=').append(this.value());
      if (this.domain() != null) {
         var1.append(", domain=").append(this.domain());
      }

      if (this.path() != null) {
         var1.append(", path=").append(this.path());
      }

      if (this.maxAge() >= 0L) {
         var1.append(", maxAge=").append(this.maxAge()).append('s');
      }

      if (this.isSecure()) {
         var1.append(", secure");
      }

      if (this.isHttpOnly()) {
         var1.append(", HTTPOnly");
      }

      return var1.toString();
   }
}
