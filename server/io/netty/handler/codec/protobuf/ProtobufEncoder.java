package io.netty.handler.codec.protobuf;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.MessageLite.Builder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

@ChannelHandler.Sharable
public class ProtobufEncoder extends MessageToMessageEncoder<MessageLiteOrBuilder> {
   public ProtobufEncoder() {
      super();
   }

   protected void encode(ChannelHandlerContext var1, MessageLiteOrBuilder var2, List<Object> var3) throws Exception {
      if (var2 instanceof MessageLite) {
         var3.add(Unpooled.wrappedBuffer(((MessageLite)var2).toByteArray()));
      } else {
         if (var2 instanceof Builder) {
            var3.add(Unpooled.wrappedBuffer(((Builder)var2).build().toByteArray()));
         }

      }
   }
}
