package io.netty.handler.codec.string;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

@ChannelHandler.Sharable
public class LineEncoder extends MessageToMessageEncoder<CharSequence> {
   private final Charset charset;
   private final byte[] lineSeparator;

   public LineEncoder() {
      this(LineSeparator.DEFAULT, CharsetUtil.UTF_8);
   }

   public LineEncoder(LineSeparator var1) {
      this(var1, CharsetUtil.UTF_8);
   }

   public LineEncoder(Charset var1) {
      this(LineSeparator.DEFAULT, var1);
   }

   public LineEncoder(LineSeparator var1, Charset var2) {
      super();
      this.charset = (Charset)ObjectUtil.checkNotNull(var2, "charset");
      this.lineSeparator = ((LineSeparator)ObjectUtil.checkNotNull(var1, "lineSeparator")).value().getBytes(var2);
   }

   protected void encode(ChannelHandlerContext var1, CharSequence var2, List<Object> var3) throws Exception {
      ByteBuf var4 = ByteBufUtil.encodeString(var1.alloc(), CharBuffer.wrap(var2), this.charset, this.lineSeparator.length);
      var4.writeBytes(this.lineSeparator);
      var3.add(var4);
   }
}
