package io.netty.handler.codec.http;

import java.util.Set;

/** @deprecated */
@Deprecated
public interface Cookie extends io.netty.handler.codec.http.cookie.Cookie {
   /** @deprecated */
   @Deprecated
   String getName();

   /** @deprecated */
   @Deprecated
   String getValue();

   /** @deprecated */
   @Deprecated
   String getDomain();

   /** @deprecated */
   @Deprecated
   String getPath();

   /** @deprecated */
   @Deprecated
   String getComment();

   /** @deprecated */
   @Deprecated
   String comment();

   /** @deprecated */
   @Deprecated
   void setComment(String var1);

   /** @deprecated */
   @Deprecated
   long getMaxAge();

   /** @deprecated */
   @Deprecated
   long maxAge();

   /** @deprecated */
   @Deprecated
   void setMaxAge(long var1);

   /** @deprecated */
   @Deprecated
   int getVersion();

   /** @deprecated */
   @Deprecated
   int version();

   /** @deprecated */
   @Deprecated
   void setVersion(int var1);

   /** @deprecated */
   @Deprecated
   String getCommentUrl();

   /** @deprecated */
   @Deprecated
   String commentUrl();

   /** @deprecated */
   @Deprecated
   void setCommentUrl(String var1);

   /** @deprecated */
   @Deprecated
   boolean isDiscard();

   /** @deprecated */
   @Deprecated
   void setDiscard(boolean var1);

   /** @deprecated */
   @Deprecated
   Set<Integer> getPorts();

   /** @deprecated */
   @Deprecated
   Set<Integer> ports();

   /** @deprecated */
   @Deprecated
   void setPorts(int... var1);

   /** @deprecated */
   @Deprecated
   void setPorts(Iterable<Integer> var1);
}
