package io.netty.handler.codec.http;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/** @deprecated */
@Deprecated
public class DefaultCookie extends io.netty.handler.codec.http.cookie.DefaultCookie implements Cookie {
   private String comment;
   private String commentUrl;
   private boolean discard;
   private Set<Integer> ports = Collections.emptySet();
   private Set<Integer> unmodifiablePorts;
   private int version;

   public DefaultCookie(String var1, String var2) {
      super(var1, var2);
      this.unmodifiablePorts = this.ports;
   }

   /** @deprecated */
   @Deprecated
   public String getName() {
      return this.name();
   }

   /** @deprecated */
   @Deprecated
   public String getValue() {
      return this.value();
   }

   /** @deprecated */
   @Deprecated
   public String getDomain() {
      return this.domain();
   }

   /** @deprecated */
   @Deprecated
   public String getPath() {
      return this.path();
   }

   /** @deprecated */
   @Deprecated
   public String getComment() {
      return this.comment();
   }

   /** @deprecated */
   @Deprecated
   public String comment() {
      return this.comment;
   }

   /** @deprecated */
   @Deprecated
   public void setComment(String var1) {
      this.comment = this.validateValue("comment", var1);
   }

   /** @deprecated */
   @Deprecated
   public String getCommentUrl() {
      return this.commentUrl();
   }

   /** @deprecated */
   @Deprecated
   public String commentUrl() {
      return this.commentUrl;
   }

   /** @deprecated */
   @Deprecated
   public void setCommentUrl(String var1) {
      this.commentUrl = this.validateValue("commentUrl", var1);
   }

   /** @deprecated */
   @Deprecated
   public boolean isDiscard() {
      return this.discard;
   }

   /** @deprecated */
   @Deprecated
   public void setDiscard(boolean var1) {
      this.discard = var1;
   }

   /** @deprecated */
   @Deprecated
   public Set<Integer> getPorts() {
      return this.ports();
   }

   /** @deprecated */
   @Deprecated
   public Set<Integer> ports() {
      if (this.unmodifiablePorts == null) {
         this.unmodifiablePorts = Collections.unmodifiableSet(this.ports);
      }

      return this.unmodifiablePorts;
   }

   /** @deprecated */
   @Deprecated
   public void setPorts(int... var1) {
      if (var1 == null) {
         throw new NullPointerException("ports");
      } else {
         int[] var2 = (int[])var1.clone();
         if (var2.length == 0) {
            this.unmodifiablePorts = this.ports = Collections.emptySet();
         } else {
            TreeSet var3 = new TreeSet();
            int[] var4 = var2;
            int var5 = var2.length;
            int var6 = 0;

            while(true) {
               if (var6 >= var5) {
                  this.ports = var3;
                  this.unmodifiablePorts = null;
                  break;
               }

               int var7 = var4[var6];
               if (var7 <= 0 || var7 > 65535) {
                  throw new IllegalArgumentException("port out of range: " + var7);
               }

               var3.add(var7);
               ++var6;
            }
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public void setPorts(Iterable<Integer> var1) {
      TreeSet var2 = new TreeSet();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         int var4 = (Integer)var3.next();
         if (var4 <= 0 || var4 > 65535) {
            throw new IllegalArgumentException("port out of range: " + var4);
         }

         var2.add(var4);
      }

      if (var2.isEmpty()) {
         this.unmodifiablePorts = this.ports = Collections.emptySet();
      } else {
         this.ports = var2;
         this.unmodifiablePorts = null;
      }

   }

   /** @deprecated */
   @Deprecated
   public long getMaxAge() {
      return this.maxAge();
   }

   /** @deprecated */
   @Deprecated
   public int getVersion() {
      return this.version();
   }

   /** @deprecated */
   @Deprecated
   public int version() {
      return this.version;
   }

   /** @deprecated */
   @Deprecated
   public void setVersion(int var1) {
      this.version = var1;
   }
}
