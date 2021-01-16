package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;

public class DefaultSpdySynReplyFrame extends DefaultSpdyHeadersFrame implements SpdySynReplyFrame {
   public DefaultSpdySynReplyFrame(int var1) {
      super(var1);
   }

   public DefaultSpdySynReplyFrame(int var1, boolean var2) {
      super(var1, var2);
   }

   public SpdySynReplyFrame setStreamId(int var1) {
      super.setStreamId(var1);
      return this;
   }

   public SpdySynReplyFrame setLast(boolean var1) {
      super.setLast(var1);
      return this;
   }

   public SpdySynReplyFrame setInvalid() {
      super.setInvalid();
      return this;
   }

   public String toString() {
      StringBuilder var1 = (new StringBuilder()).append(StringUtil.simpleClassName((Object)this)).append("(last: ").append(this.isLast()).append(')').append(StringUtil.NEWLINE).append("--> Stream-ID = ").append(this.streamId()).append(StringUtil.NEWLINE).append("--> Headers:").append(StringUtil.NEWLINE);
      this.appendHeaders(var1);
      var1.setLength(var1.length() - StringUtil.NEWLINE.length());
      return var1.toString();
   }
}
