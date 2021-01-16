package io.netty.handler.codec.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;

public final class MqttMessageFactory {
   public static MqttMessage newMessage(MqttFixedHeader var0, Object var1, Object var2) {
      switch(var0.messageType()) {
      case CONNECT:
         return new MqttConnectMessage(var0, (MqttConnectVariableHeader)var1, (MqttConnectPayload)var2);
      case CONNACK:
         return new MqttConnAckMessage(var0, (MqttConnAckVariableHeader)var1);
      case SUBSCRIBE:
         return new MqttSubscribeMessage(var0, (MqttMessageIdVariableHeader)var1, (MqttSubscribePayload)var2);
      case SUBACK:
         return new MqttSubAckMessage(var0, (MqttMessageIdVariableHeader)var1, (MqttSubAckPayload)var2);
      case UNSUBACK:
         return new MqttUnsubAckMessage(var0, (MqttMessageIdVariableHeader)var1);
      case UNSUBSCRIBE:
         return new MqttUnsubscribeMessage(var0, (MqttMessageIdVariableHeader)var1, (MqttUnsubscribePayload)var2);
      case PUBLISH:
         return new MqttPublishMessage(var0, (MqttPublishVariableHeader)var1, (ByteBuf)var2);
      case PUBACK:
         return new MqttPubAckMessage(var0, (MqttMessageIdVariableHeader)var1);
      case PUBREC:
      case PUBREL:
      case PUBCOMP:
         return new MqttMessage(var0, var1);
      case PINGREQ:
      case PINGRESP:
      case DISCONNECT:
         return new MqttMessage(var0);
      default:
         throw new IllegalArgumentException("unknown message type: " + var0.messageType());
      }
   }

   public static MqttMessage newInvalidMessage(Throwable var0) {
      return new MqttMessage((MqttFixedHeader)null, (Object)null, (Object)null, DecoderResult.failure(var0));
   }

   private MqttMessageFactory() {
      super();
   }
}
