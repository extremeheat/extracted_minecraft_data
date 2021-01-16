package io.netty.handler.codec.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;
import java.util.ArrayList;
import java.util.List;

public final class MqttDecoder extends ReplayingDecoder<MqttDecoder.DecoderState> {
   private static final int DEFAULT_MAX_BYTES_IN_MESSAGE = 8092;
   private MqttFixedHeader mqttFixedHeader;
   private Object variableHeader;
   private int bytesRemainingInVariablePart;
   private final int maxBytesInMessage;

   public MqttDecoder() {
      this(8092);
   }

   public MqttDecoder(int var1) {
      super(MqttDecoder.DecoderState.READ_FIXED_HEADER);
      this.maxBytesInMessage = var1;
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      MqttDecoder.Result var4;
      switch((MqttDecoder.DecoderState)this.state()) {
      case READ_FIXED_HEADER:
         try {
            this.mqttFixedHeader = decodeFixedHeader(var2);
            this.bytesRemainingInVariablePart = this.mqttFixedHeader.remainingLength();
            this.checkpoint(MqttDecoder.DecoderState.READ_VARIABLE_HEADER);
         } catch (Exception var8) {
            var3.add(this.invalidMessage(var8));
            return;
         }
      case READ_VARIABLE_HEADER:
         try {
            if (this.bytesRemainingInVariablePart > this.maxBytesInMessage) {
               throw new DecoderException("too large message: " + this.bytesRemainingInVariablePart + " bytes");
            }

            var4 = decodeVariableHeader(var2, this.mqttFixedHeader);
            this.variableHeader = var4.value;
            this.bytesRemainingInVariablePart -= var4.numberOfBytesConsumed;
            this.checkpoint(MqttDecoder.DecoderState.READ_PAYLOAD);
         } catch (Exception var7) {
            var3.add(this.invalidMessage(var7));
            return;
         }
      case READ_PAYLOAD:
         try {
            var4 = decodePayload(var2, this.mqttFixedHeader.messageType(), this.bytesRemainingInVariablePart, this.variableHeader);
            this.bytesRemainingInVariablePart -= var4.numberOfBytesConsumed;
            if (this.bytesRemainingInVariablePart != 0) {
               throw new DecoderException("non-zero remaining payload bytes: " + this.bytesRemainingInVariablePart + " (" + this.mqttFixedHeader.messageType() + ')');
            }

            this.checkpoint(MqttDecoder.DecoderState.READ_FIXED_HEADER);
            MqttMessage var5 = MqttMessageFactory.newMessage(this.mqttFixedHeader, this.variableHeader, var4.value);
            this.mqttFixedHeader = null;
            this.variableHeader = null;
            var3.add(var5);
            break;
         } catch (Exception var6) {
            var3.add(this.invalidMessage(var6));
            return;
         }
      case BAD_MESSAGE:
         var2.skipBytes(this.actualReadableBytes());
         break;
      default:
         throw new Error();
      }

   }

   private MqttMessage invalidMessage(Throwable var1) {
      this.checkpoint(MqttDecoder.DecoderState.BAD_MESSAGE);
      return MqttMessageFactory.newInvalidMessage(var1);
   }

   private static MqttFixedHeader decodeFixedHeader(ByteBuf var0) {
      short var1 = var0.readUnsignedByte();
      MqttMessageType var2 = MqttMessageType.valueOf(var1 >> 4);
      boolean var3 = (var1 & 8) == 8;
      int var4 = (var1 & 6) >> 1;
      boolean var5 = (var1 & 1) != 0;
      int var6 = 0;
      int var7 = 1;
      int var9 = 0;

      short var8;
      do {
         var8 = var0.readUnsignedByte();
         var6 += (var8 & 127) * var7;
         var7 *= 128;
         ++var9;
      } while((var8 & 128) != 0 && var9 < 4);

      if (var9 == 4 && (var8 & 128) != 0) {
         throw new DecoderException("remaining length exceeds 4 digits (" + var2 + ')');
      } else {
         MqttFixedHeader var10 = new MqttFixedHeader(var2, var3, MqttQoS.valueOf(var4), var5, var6);
         return MqttCodecUtil.validateFixedHeader(MqttCodecUtil.resetUnusedFields(var10));
      }
   }

   private static MqttDecoder.Result<?> decodeVariableHeader(ByteBuf var0, MqttFixedHeader var1) {
      switch(var1.messageType()) {
      case CONNECT:
         return decodeConnectionVariableHeader(var0);
      case CONNACK:
         return decodeConnAckVariableHeader(var0);
      case SUBSCRIBE:
      case UNSUBSCRIBE:
      case SUBACK:
      case UNSUBACK:
      case PUBACK:
      case PUBREC:
      case PUBCOMP:
      case PUBREL:
         return decodeMessageIdVariableHeader(var0);
      case PUBLISH:
         return decodePublishVariableHeader(var0, var1);
      case PINGREQ:
      case PINGRESP:
      case DISCONNECT:
         return new MqttDecoder.Result((Object)null, 0);
      default:
         return new MqttDecoder.Result((Object)null, 0);
      }
   }

   private static MqttDecoder.Result<MqttConnectVariableHeader> decodeConnectionVariableHeader(ByteBuf var0) {
      MqttDecoder.Result var1 = decodeString(var0);
      int var2 = var1.numberOfBytesConsumed;
      byte var3 = var0.readByte();
      ++var2;
      MqttVersion var4 = MqttVersion.fromProtocolNameAndLevel((String)var1.value, var3);
      short var5 = var0.readUnsignedByte();
      ++var2;
      MqttDecoder.Result var6 = decodeMsbLsb(var0);
      var2 += var6.numberOfBytesConsumed;
      boolean var7 = (var5 & 128) == 128;
      boolean var8 = (var5 & 64) == 64;
      boolean var9 = (var5 & 32) == 32;
      int var10 = (var5 & 24) >> 3;
      boolean var11 = (var5 & 4) == 4;
      boolean var12 = (var5 & 2) == 2;
      if (var4 == MqttVersion.MQTT_3_1_1) {
         boolean var13 = (var5 & 1) == 0;
         if (!var13) {
            throw new DecoderException("non-zero reserved flag");
         }
      }

      MqttConnectVariableHeader var14 = new MqttConnectVariableHeader(var4.protocolName(), var4.protocolLevel(), var7, var8, var9, var10, var11, var12, (Integer)var6.value);
      return new MqttDecoder.Result(var14, var2);
   }

   private static MqttDecoder.Result<MqttConnAckVariableHeader> decodeConnAckVariableHeader(ByteBuf var0) {
      boolean var1 = (var0.readUnsignedByte() & 1) == 1;
      byte var2 = var0.readByte();
      boolean var3 = true;
      MqttConnAckVariableHeader var4 = new MqttConnAckVariableHeader(MqttConnectReturnCode.valueOf(var2), var1);
      return new MqttDecoder.Result(var4, 2);
   }

   private static MqttDecoder.Result<MqttMessageIdVariableHeader> decodeMessageIdVariableHeader(ByteBuf var0) {
      MqttDecoder.Result var1 = decodeMessageId(var0);
      return new MqttDecoder.Result(MqttMessageIdVariableHeader.from((Integer)var1.value), var1.numberOfBytesConsumed);
   }

   private static MqttDecoder.Result<MqttPublishVariableHeader> decodePublishVariableHeader(ByteBuf var0, MqttFixedHeader var1) {
      MqttDecoder.Result var2 = decodeString(var0);
      if (!MqttCodecUtil.isValidPublishTopicName((String)var2.value)) {
         throw new DecoderException("invalid publish topic name: " + (String)var2.value + " (contains wildcards)");
      } else {
         int var3 = var2.numberOfBytesConsumed;
         int var4 = -1;
         if (var1.qosLevel().value() > 0) {
            MqttDecoder.Result var5 = decodeMessageId(var0);
            var4 = (Integer)var5.value;
            var3 += var5.numberOfBytesConsumed;
         }

         MqttPublishVariableHeader var6 = new MqttPublishVariableHeader((String)var2.value, var4);
         return new MqttDecoder.Result(var6, var3);
      }
   }

   private static MqttDecoder.Result<Integer> decodeMessageId(ByteBuf var0) {
      MqttDecoder.Result var1 = decodeMsbLsb(var0);
      if (!MqttCodecUtil.isValidMessageId((Integer)var1.value)) {
         throw new DecoderException("invalid messageId: " + var1.value);
      } else {
         return var1;
      }
   }

   private static MqttDecoder.Result<?> decodePayload(ByteBuf var0, MqttMessageType var1, int var2, Object var3) {
      switch(var1) {
      case CONNECT:
         return decodeConnectionPayload(var0, (MqttConnectVariableHeader)var3);
      case CONNACK:
      case UNSUBACK:
      case PUBACK:
      case PUBREC:
      case PUBCOMP:
      case PUBREL:
      default:
         return new MqttDecoder.Result((Object)null, 0);
      case SUBSCRIBE:
         return decodeSubscribePayload(var0, var2);
      case UNSUBSCRIBE:
         return decodeUnsubscribePayload(var0, var2);
      case SUBACK:
         return decodeSubackPayload(var0, var2);
      case PUBLISH:
         return decodePublishPayload(var0, var2);
      }
   }

   private static MqttDecoder.Result<MqttConnectPayload> decodeConnectionPayload(ByteBuf var0, MqttConnectVariableHeader var1) {
      MqttDecoder.Result var2 = decodeString(var0);
      String var3 = (String)var2.value;
      MqttVersion var4 = MqttVersion.fromProtocolNameAndLevel(var1.name(), (byte)var1.version());
      if (!MqttCodecUtil.isValidClientId(var4, var3)) {
         throw new MqttIdentifierRejectedException("invalid clientIdentifier: " + var3);
      } else {
         int var5 = var2.numberOfBytesConsumed;
         MqttDecoder.Result var6 = null;
         MqttDecoder.Result var7 = null;
         if (var1.isWillFlag()) {
            var6 = decodeString(var0, 0, 32767);
            var5 += var6.numberOfBytesConsumed;
            var7 = decodeByteArray(var0);
            var5 += var7.numberOfBytesConsumed;
         }

         MqttDecoder.Result var8 = null;
         MqttDecoder.Result var9 = null;
         if (var1.hasUserName()) {
            var8 = decodeString(var0);
            var5 += var8.numberOfBytesConsumed;
         }

         if (var1.hasPassword()) {
            var9 = decodeByteArray(var0);
            var5 += var9.numberOfBytesConsumed;
         }

         MqttConnectPayload var10 = new MqttConnectPayload((String)var2.value, var6 != null ? (String)var6.value : null, var7 != null ? (byte[])var7.value : null, var8 != null ? (String)var8.value : null, var9 != null ? (byte[])var9.value : null);
         return new MqttDecoder.Result(var10, var5);
      }
   }

   private static MqttDecoder.Result<MqttSubscribePayload> decodeSubscribePayload(ByteBuf var0, int var1) {
      ArrayList var2 = new ArrayList();
      int var3 = 0;

      while(var3 < var1) {
         MqttDecoder.Result var4 = decodeString(var0);
         var3 += var4.numberOfBytesConsumed;
         int var5 = var0.readUnsignedByte() & 3;
         ++var3;
         var2.add(new MqttTopicSubscription((String)var4.value, MqttQoS.valueOf(var5)));
      }

      return new MqttDecoder.Result(new MqttSubscribePayload(var2), var3);
   }

   private static MqttDecoder.Result<MqttSubAckPayload> decodeSubackPayload(ByteBuf var0, int var1) {
      ArrayList var2 = new ArrayList();
      int var3 = 0;

      while(var3 < var1) {
         int var4 = var0.readUnsignedByte();
         if (var4 != MqttQoS.FAILURE.value()) {
            var4 &= 3;
         }

         ++var3;
         var2.add(var4);
      }

      return new MqttDecoder.Result(new MqttSubAckPayload(var2), var3);
   }

   private static MqttDecoder.Result<MqttUnsubscribePayload> decodeUnsubscribePayload(ByteBuf var0, int var1) {
      ArrayList var2 = new ArrayList();
      int var3 = 0;

      while(var3 < var1) {
         MqttDecoder.Result var4 = decodeString(var0);
         var3 += var4.numberOfBytesConsumed;
         var2.add(var4.value);
      }

      return new MqttDecoder.Result(new MqttUnsubscribePayload(var2), var3);
   }

   private static MqttDecoder.Result<ByteBuf> decodePublishPayload(ByteBuf var0, int var1) {
      ByteBuf var2 = var0.readRetainedSlice(var1);
      return new MqttDecoder.Result(var2, var1);
   }

   private static MqttDecoder.Result<String> decodeString(ByteBuf var0) {
      return decodeString(var0, 0, 2147483647);
   }

   private static MqttDecoder.Result<String> decodeString(ByteBuf var0, int var1, int var2) {
      MqttDecoder.Result var3 = decodeMsbLsb(var0);
      int var4 = (Integer)var3.value;
      int var5 = var3.numberOfBytesConsumed;
      if (var4 >= var1 && var4 <= var2) {
         String var6 = var0.toString(var0.readerIndex(), var4, CharsetUtil.UTF_8);
         var0.skipBytes(var4);
         var5 += var4;
         return new MqttDecoder.Result(var6, var5);
      } else {
         var0.skipBytes(var4);
         var5 += var4;
         return new MqttDecoder.Result((Object)null, var5);
      }
   }

   private static MqttDecoder.Result<byte[]> decodeByteArray(ByteBuf var0) {
      MqttDecoder.Result var1 = decodeMsbLsb(var0);
      int var2 = (Integer)var1.value;
      byte[] var3 = new byte[var2];
      var0.readBytes(var3);
      return new MqttDecoder.Result(var3, var1.numberOfBytesConsumed + var2);
   }

   private static MqttDecoder.Result<Integer> decodeMsbLsb(ByteBuf var0) {
      return decodeMsbLsb(var0, 0, 65535);
   }

   private static MqttDecoder.Result<Integer> decodeMsbLsb(ByteBuf var0, int var1, int var2) {
      short var3 = var0.readUnsignedByte();
      short var4 = var0.readUnsignedByte();
      boolean var5 = true;
      int var6 = var3 << 8 | var4;
      if (var6 < var1 || var6 > var2) {
         var6 = -1;
      }

      return new MqttDecoder.Result(var6, 2);
   }

   private static final class Result<T> {
      private final T value;
      private final int numberOfBytesConsumed;

      Result(T var1, int var2) {
         super();
         this.value = var1;
         this.numberOfBytesConsumed = var2;
      }
   }

   static enum DecoderState {
      READ_FIXED_HEADER,
      READ_VARIABLE_HEADER,
      READ_PAYLOAD,
      BAD_MESSAGE;

      private DecoderState() {
      }
   }
}
