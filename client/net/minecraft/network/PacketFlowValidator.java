package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import java.util.List;
import net.minecraft.network.protocol.Packet;
import org.slf4j.Logger;

public class PacketFlowValidator extends MessageToMessageCodec<Packet<?>, Packet<?>> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final AttributeKey<ConnectionProtocol.CodecData<?>> decoderKey;
   private final AttributeKey<ConnectionProtocol.CodecData<?>> encoderKey;

   public PacketFlowValidator(AttributeKey<ConnectionProtocol.CodecData<?>> var1, AttributeKey<ConnectionProtocol.CodecData<?>> var2) {
      super();
      this.decoderKey = var1;
      this.encoderKey = var2;
   }

   private static void validatePacket(ChannelHandlerContext var0, Packet<?> var1, List<Object> var2, AttributeKey<ConnectionProtocol.CodecData<?>> var3) {
      Attribute var4 = var0.channel().attr(var3);
      ConnectionProtocol.CodecData var5 = (ConnectionProtocol.CodecData)var4.get();
      if (!var5.isValidPacketType(var1)) {
         LOGGER.error("Unrecognized packet in pipeline {}:{} - {}", new Object[]{var5.protocol().id(), var5.flow(), var1});
      }

      ReferenceCountUtil.retain(var1);
      var2.add(var1);
      ProtocolSwapHandler.swapProtocolIfNeeded(var4, var1);
   }

   protected void decode(ChannelHandlerContext var1, Packet<?> var2, List<Object> var3) throws Exception {
      validatePacket(var1, var2, var3, this.decoderKey);
   }

   protected void encode(ChannelHandlerContext var1, Packet<?> var2, List<Object> var3) throws Exception {
      validatePacket(var1, var2, var3, this.encoderKey);
   }
}
