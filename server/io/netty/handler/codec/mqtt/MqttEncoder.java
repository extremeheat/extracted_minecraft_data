package io.netty.handler.codec.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.EmptyArrays;
import java.util.Iterator;
import java.util.List;

@ChannelHandler.Sharable
public final class MqttEncoder extends MessageToMessageEncoder<MqttMessage> {
   public static final MqttEncoder INSTANCE = new MqttEncoder();

   private MqttEncoder() {
      super();
   }

   protected void encode(ChannelHandlerContext var1, MqttMessage var2, List<Object> var3) throws Exception {
      var3.add(doEncode(var1.alloc(), var2));
   }

   static ByteBuf doEncode(ByteBufAllocator var0, MqttMessage var1) {
      switch(var1.fixedHeader().messageType()) {
      case CONNECT:
         return encodeConnectMessage(var0, (MqttConnectMessage)var1);
      case CONNACK:
         return encodeConnAckMessage(var0, (MqttConnAckMessage)var1);
      case PUBLISH:
         return encodePublishMessage(var0, (MqttPublishMessage)var1);
      case SUBSCRIBE:
         return encodeSubscribeMessage(var0, (MqttSubscribeMessage)var1);
      case UNSUBSCRIBE:
         return encodeUnsubscribeMessage(var0, (MqttUnsubscribeMessage)var1);
      case SUBACK:
         return encodeSubAckMessage(var0, (MqttSubAckMessage)var1);
      case UNSUBACK:
      case PUBACK:
      case PUBREC:
      case PUBREL:
      case PUBCOMP:
         return encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(var0, var1);
      case PINGREQ:
      case PINGRESP:
      case DISCONNECT:
         return encodeMessageWithOnlySingleByteFixedHeader(var0, var1);
      default:
         throw new IllegalArgumentException("Unknown message type: " + var1.fixedHeader().messageType().value());
      }
   }

   private static ByteBuf encodeConnectMessage(ByteBufAllocator var0, MqttConnectMessage var1) {
      byte var2 = 0;
      MqttFixedHeader var3 = var1.fixedHeader();
      MqttConnectVariableHeader var4 = var1.variableHeader();
      MqttConnectPayload var5 = var1.payload();
      MqttVersion var6 = MqttVersion.fromProtocolNameAndLevel(var4.name(), (byte)var4.version());
      if (!var4.hasUserName() && var4.hasPassword()) {
         throw new DecoderException("Without a username, the password MUST be not set");
      } else {
         String var7 = var5.clientIdentifier();
         if (!MqttCodecUtil.isValidClientId(var6, var7)) {
            throw new MqttIdentifierRejectedException("invalid clientIdentifier: " + var7);
         } else {
            byte[] var8 = encodeStringUtf8(var7);
            int var22 = var2 + 2 + var8.length;
            String var9 = var5.willTopic();
            byte[] var10 = var9 != null ? encodeStringUtf8(var9) : EmptyArrays.EMPTY_BYTES;
            byte[] var11 = var5.willMessageInBytes();
            byte[] var12 = var11 != null ? var11 : EmptyArrays.EMPTY_BYTES;
            if (var4.isWillFlag()) {
               var22 += 2 + var10.length;
               var22 += 2 + var12.length;
            }

            String var13 = var5.userName();
            byte[] var14 = var13 != null ? encodeStringUtf8(var13) : EmptyArrays.EMPTY_BYTES;
            if (var4.hasUserName()) {
               var22 += 2 + var14.length;
            }

            byte[] var15 = var5.passwordInBytes();
            byte[] var16 = var15 != null ? var15 : EmptyArrays.EMPTY_BYTES;
            if (var4.hasPassword()) {
               var22 += 2 + var16.length;
            }

            byte[] var17 = var6.protocolNameBytes();
            int var18 = 2 + var17.length + 4;
            int var19 = var18 + var22;
            int var20 = 1 + getVariableLengthInt(var19);
            ByteBuf var21 = var0.buffer(var20 + var19);
            var21.writeByte(getFixedHeaderByte1(var3));
            writeVariableLengthInt(var21, var19);
            var21.writeShort(var17.length);
            var21.writeBytes(var17);
            var21.writeByte(var4.version());
            var21.writeByte(getConnVariableHeaderFlag(var4));
            var21.writeShort(var4.keepAliveTimeSeconds());
            var21.writeShort(var8.length);
            var21.writeBytes((byte[])var8, 0, var8.length);
            if (var4.isWillFlag()) {
               var21.writeShort(var10.length);
               var21.writeBytes((byte[])var10, 0, var10.length);
               var21.writeShort(var12.length);
               var21.writeBytes((byte[])var12, 0, var12.length);
            }

            if (var4.hasUserName()) {
               var21.writeShort(var14.length);
               var21.writeBytes((byte[])var14, 0, var14.length);
            }

            if (var4.hasPassword()) {
               var21.writeShort(var16.length);
               var21.writeBytes((byte[])var16, 0, var16.length);
            }

            return var21;
         }
      }
   }

   private static int getConnVariableHeaderFlag(MqttConnectVariableHeader var0) {
      int var1 = 0;
      if (var0.hasUserName()) {
         var1 |= 128;
      }

      if (var0.hasPassword()) {
         var1 |= 64;
      }

      if (var0.isWillRetain()) {
         var1 |= 32;
      }

      var1 |= (var0.willQos() & 3) << 3;
      if (var0.isWillFlag()) {
         var1 |= 4;
      }

      if (var0.isCleanSession()) {
         var1 |= 2;
      }

      return var1;
   }

   private static ByteBuf encodeConnAckMessage(ByteBufAllocator var0, MqttConnAckMessage var1) {
      ByteBuf var2 = var0.buffer(4);
      var2.writeByte(getFixedHeaderByte1(var1.fixedHeader()));
      var2.writeByte(2);
      var2.writeByte(var1.variableHeader().isSessionPresent() ? 1 : 0);
      var2.writeByte(var1.variableHeader().connectReturnCode().byteValue());
      return var2;
   }

   private static ByteBuf encodeSubscribeMessage(ByteBufAllocator var0, MqttSubscribeMessage var1) {
      byte var2 = 2;
      int var3 = 0;
      MqttFixedHeader var4 = var1.fixedHeader();
      MqttMessageIdVariableHeader var5 = var1.variableHeader();
      MqttSubscribePayload var6 = var1.payload();

      for(Iterator var7 = var6.topicSubscriptions().iterator(); var7.hasNext(); ++var3) {
         MqttTopicSubscription var8 = (MqttTopicSubscription)var7.next();
         String var9 = var8.topicName();
         byte[] var10 = encodeStringUtf8(var9);
         var3 += 2 + var10.length;
      }

      int var15 = var2 + var3;
      int var16 = 1 + getVariableLengthInt(var15);
      ByteBuf var17 = var0.buffer(var16 + var15);
      var17.writeByte(getFixedHeaderByte1(var4));
      writeVariableLengthInt(var17, var15);
      int var18 = var5.messageId();
      var17.writeShort(var18);
      Iterator var11 = var6.topicSubscriptions().iterator();

      while(var11.hasNext()) {
         MqttTopicSubscription var12 = (MqttTopicSubscription)var11.next();
         String var13 = var12.topicName();
         byte[] var14 = encodeStringUtf8(var13);
         var17.writeShort(var14.length);
         var17.writeBytes((byte[])var14, 0, var14.length);
         var17.writeByte(var12.qualityOfService().value());
      }

      return var17;
   }

   private static ByteBuf encodeUnsubscribeMessage(ByteBufAllocator var0, MqttUnsubscribeMessage var1) {
      byte var2 = 2;
      int var3 = 0;
      MqttFixedHeader var4 = var1.fixedHeader();
      MqttMessageIdVariableHeader var5 = var1.variableHeader();
      MqttUnsubscribePayload var6 = var1.payload();

      byte[] var9;
      for(Iterator var7 = var6.topics().iterator(); var7.hasNext(); var3 += 2 + var9.length) {
         String var8 = (String)var7.next();
         var9 = encodeStringUtf8(var8);
      }

      int var14 = var2 + var3;
      int var15 = 1 + getVariableLengthInt(var14);
      ByteBuf var16 = var0.buffer(var15 + var14);
      var16.writeByte(getFixedHeaderByte1(var4));
      writeVariableLengthInt(var16, var14);
      int var10 = var5.messageId();
      var16.writeShort(var10);
      Iterator var11 = var6.topics().iterator();

      while(var11.hasNext()) {
         String var12 = (String)var11.next();
         byte[] var13 = encodeStringUtf8(var12);
         var16.writeShort(var13.length);
         var16.writeBytes((byte[])var13, 0, var13.length);
      }

      return var16;
   }

   private static ByteBuf encodeSubAckMessage(ByteBufAllocator var0, MqttSubAckMessage var1) {
      byte var2 = 2;
      int var3 = var1.payload().grantedQoSLevels().size();
      int var4 = var2 + var3;
      int var5 = 1 + getVariableLengthInt(var4);
      ByteBuf var6 = var0.buffer(var5 + var4);
      var6.writeByte(getFixedHeaderByte1(var1.fixedHeader()));
      writeVariableLengthInt(var6, var4);
      var6.writeShort(var1.variableHeader().messageId());
      Iterator var7 = var1.payload().grantedQoSLevels().iterator();

      while(var7.hasNext()) {
         int var8 = (Integer)var7.next();
         var6.writeByte(var8);
      }

      return var6;
   }

   private static ByteBuf encodePublishMessage(ByteBufAllocator var0, MqttPublishMessage var1) {
      MqttFixedHeader var2 = var1.fixedHeader();
      MqttPublishVariableHeader var3 = var1.variableHeader();
      ByteBuf var4 = var1.payload().duplicate();
      String var5 = var3.topicName();
      byte[] var6 = encodeStringUtf8(var5);
      int var7 = 2 + var6.length + (var2.qosLevel().value() > 0 ? 2 : 0);
      int var8 = var4.readableBytes();
      int var9 = var7 + var8;
      int var10 = 1 + getVariableLengthInt(var9);
      ByteBuf var11 = var0.buffer(var10 + var9);
      var11.writeByte(getFixedHeaderByte1(var2));
      writeVariableLengthInt(var11, var9);
      var11.writeShort(var6.length);
      var11.writeBytes(var6);
      if (var2.qosLevel().value() > 0) {
         var11.writeShort(var3.messageId());
      }

      var11.writeBytes(var4);
      return var11;
   }

   private static ByteBuf encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(ByteBufAllocator var0, MqttMessage var1) {
      MqttFixedHeader var2 = var1.fixedHeader();
      MqttMessageIdVariableHeader var3 = (MqttMessageIdVariableHeader)var1.variableHeader();
      int var4 = var3.messageId();
      byte var5 = 2;
      int var6 = 1 + getVariableLengthInt(var5);
      ByteBuf var7 = var0.buffer(var6 + var5);
      var7.writeByte(getFixedHeaderByte1(var2));
      writeVariableLengthInt(var7, var5);
      var7.writeShort(var4);
      return var7;
   }

   private static ByteBuf encodeMessageWithOnlySingleByteFixedHeader(ByteBufAllocator var0, MqttMessage var1) {
      MqttFixedHeader var2 = var1.fixedHeader();
      ByteBuf var3 = var0.buffer(2);
      var3.writeByte(getFixedHeaderByte1(var2));
      var3.writeByte(0);
      return var3;
   }

   private static int getFixedHeaderByte1(MqttFixedHeader var0) {
      byte var1 = 0;
      int var2 = var1 | var0.messageType().value() << 4;
      if (var0.isDup()) {
         var2 |= 8;
      }

      var2 |= var0.qosLevel().value() << 1;
      if (var0.isRetain()) {
         var2 |= 1;
      }

      return var2;
   }

   private static void writeVariableLengthInt(ByteBuf var0, int var1) {
      do {
         int var2 = var1 % 128;
         var1 /= 128;
         if (var1 > 0) {
            var2 |= 128;
         }

         var0.writeByte(var2);
      } while(var1 > 0);

   }

   private static int getVariableLengthInt(int var0) {
      int var1 = 0;

      do {
         var0 /= 128;
         ++var1;
      } while(var0 > 0);

      return var1;
   }

   private static byte[] encodeStringUtf8(String var0) {
      return var0.getBytes(CharsetUtil.UTF_8);
   }
}
