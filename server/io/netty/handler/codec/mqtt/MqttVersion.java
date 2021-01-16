package io.netty.handler.codec.mqtt;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;

public enum MqttVersion {
   MQTT_3_1("MQIsdp", (byte)3),
   MQTT_3_1_1("MQTT", (byte)4);

   private final String name;
   private final byte level;

   private MqttVersion(String var3, byte var4) {
      this.name = (String)ObjectUtil.checkNotNull(var3, "protocolName");
      this.level = var4;
   }

   public String protocolName() {
      return this.name;
   }

   public byte[] protocolNameBytes() {
      return this.name.getBytes(CharsetUtil.UTF_8);
   }

   public byte protocolLevel() {
      return this.level;
   }

   public static MqttVersion fromProtocolNameAndLevel(String var0, byte var1) {
      MqttVersion[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         MqttVersion var5 = var2[var4];
         if (var5.name.equals(var0)) {
            if (var5.level == var1) {
               return var5;
            }

            throw new MqttUnacceptableProtocolVersionException(var0 + " and " + var1 + " are not match");
         }
      }

      throw new MqttUnacceptableProtocolVersionException(var0 + "is unknown protocol name");
   }
}
