package io.netty.handler.codec.mqtt;

public enum MqttMessageType {
   CONNECT(1),
   CONNACK(2),
   PUBLISH(3),
   PUBACK(4),
   PUBREC(5),
   PUBREL(6),
   PUBCOMP(7),
   SUBSCRIBE(8),
   SUBACK(9),
   UNSUBSCRIBE(10),
   UNSUBACK(11),
   PINGREQ(12),
   PINGRESP(13),
   DISCONNECT(14);

   private final int value;

   private MqttMessageType(int var3) {
      this.value = var3;
   }

   public int value() {
      return this.value;
   }

   public static MqttMessageType valueOf(int var0) {
      MqttMessageType[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         MqttMessageType var4 = var1[var3];
         if (var4.value == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("unknown message type: " + var0);
   }
}
