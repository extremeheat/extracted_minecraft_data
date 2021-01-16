package io.netty.handler.codec.http2;

import io.netty.channel.ChannelId;

final class Http2StreamChannelId implements ChannelId {
   private static final long serialVersionUID = -6642338822166867585L;
   private final int id;
   private final ChannelId parentId;

   Http2StreamChannelId(ChannelId var1, int var2) {
      super();
      this.parentId = var1;
      this.id = var2;
   }

   public String asShortText() {
      return this.parentId.asShortText() + '/' + this.id;
   }

   public String asLongText() {
      return this.parentId.asLongText() + '/' + this.id;
   }

   public int compareTo(ChannelId var1) {
      if (var1 instanceof Http2StreamChannelId) {
         Http2StreamChannelId var2 = (Http2StreamChannelId)var1;
         int var3 = this.parentId.compareTo(var2.parentId);
         return var3 == 0 ? this.id - var2.id : var3;
      } else {
         return this.parentId.compareTo(var1);
      }
   }

   public int hashCode() {
      return this.id * 31 + this.parentId.hashCode();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Http2StreamChannelId)) {
         return false;
      } else {
         Http2StreamChannelId var2 = (Http2StreamChannelId)var1;
         return this.id == var2.id && this.parentId.equals(var2.parentId);
      }
   }

   public String toString() {
      return this.asShortText();
   }
}
