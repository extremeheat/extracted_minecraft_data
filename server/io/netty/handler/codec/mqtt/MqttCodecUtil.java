package io.netty.handler.codec.mqtt;

import io.netty.handler.codec.DecoderException;

final class MqttCodecUtil {
   private static final char[] TOPIC_WILDCARDS = new char[]{'#', '+'};
   private static final int MIN_CLIENT_ID_LENGTH = 1;
   private static final int MAX_CLIENT_ID_LENGTH = 23;

   static boolean isValidPublishTopicName(String var0) {
      char[] var1 = TOPIC_WILDCARDS;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = var1[var3];
         if (var0.indexOf(var4) >= 0) {
            return false;
         }
      }

      return true;
   }

   static boolean isValidMessageId(int var0) {
      return var0 != 0;
   }

   static boolean isValidClientId(MqttVersion var0, String var1) {
      if (var0 != MqttVersion.MQTT_3_1) {
         if (var0 == MqttVersion.MQTT_3_1_1) {
            return var1 != null;
         } else {
            throw new IllegalArgumentException(var0 + " is unknown mqtt version");
         }
      } else {
         return var1 != null && var1.length() >= 1 && var1.length() <= 23;
      }
   }

   static MqttFixedHeader validateFixedHeader(MqttFixedHeader var0) {
      switch(var0.messageType()) {
      case PUBREL:
      case SUBSCRIBE:
      case UNSUBSCRIBE:
         if (var0.qosLevel() != MqttQoS.AT_LEAST_ONCE) {
            throw new DecoderException(var0.messageType().name() + " message must have QoS 1");
         }
      default:
         return var0;
      }
   }

   static MqttFixedHeader resetUnusedFields(MqttFixedHeader var0) {
      switch(var0.messageType()) {
      case PUBREL:
      case SUBSCRIBE:
      case UNSUBSCRIBE:
         if (var0.isRetain()) {
            return new MqttFixedHeader(var0.messageType(), var0.isDup(), var0.qosLevel(), false, var0.remainingLength());
         }

         return var0;
      case CONNECT:
      case CONNACK:
      case PUBACK:
      case PUBREC:
      case PUBCOMP:
      case SUBACK:
      case UNSUBACK:
      case PINGREQ:
      case PINGRESP:
      case DISCONNECT:
         if (!var0.isDup() && var0.qosLevel() == MqttQoS.AT_MOST_ONCE && !var0.isRetain()) {
            return var0;
         }

         return new MqttFixedHeader(var0.messageType(), false, MqttQoS.AT_MOST_ONCE, false, var0.remainingLength());
      default:
         return var0;
      }
   }

   private MqttCodecUtil() {
      super();
   }
}
