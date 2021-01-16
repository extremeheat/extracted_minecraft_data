package io.netty.handler.codec.protobuf;

import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

@ChannelHandler.Sharable
public class ProtobufEncoderNano extends MessageToMessageEncoder<MessageNano> {
   public ProtobufEncoderNano() {
      super();
   }

   protected void encode(ChannelHandlerContext var1, MessageNano var2, List<Object> var3) throws Exception {
      int var4 = var2.getSerializedSize();
      ByteBuf var5 = var1.alloc().heapBuffer(var4, var4);
      byte[] var6 = var5.array();
      CodedOutputByteBufferNano var7 = CodedOutputByteBufferNano.newInstance(var6, var5.arrayOffset(), var5.capacity());
      var2.writeTo(var7);
      var5.writerIndex(var4);
      var3.add(var5);
   }
}
