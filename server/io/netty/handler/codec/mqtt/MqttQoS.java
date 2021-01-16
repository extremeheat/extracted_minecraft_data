package io.netty.handler.codec.mqtt;

public enum MqttQoS {
   AT_MOST_ONCE(0),
   AT_LEAST_ONCE(1),
   EXACTLY_ONCE(2),
   FAILURE(128);

   private final int value;

   private MqttQoS(int var3) {
      this.value = var3;
   }

   public int value() {
      return this.value;
   }

   public static MqttQoS valueOf(int var0) {
      MqttQoS[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         MqttQoS var4 = var1[var3];
         if (var4.value == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException("invalid QoS: " + var0);
   }
}
