package io.netty.handler.codec.mqtt;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum MqttConnectReturnCode {
   CONNECTION_ACCEPTED((byte)0),
   CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION((byte)1),
   CONNECTION_REFUSED_IDENTIFIER_REJECTED((byte)2),
   CONNECTION_REFUSED_SERVER_UNAVAILABLE((byte)3),
   CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD((byte)4),
   CONNECTION_REFUSED_NOT_AUTHORIZED((byte)5);

   private static final Map<Byte, MqttConnectReturnCode> VALUE_TO_CODE_MAP;
   private final byte byteValue;

   private MqttConnectReturnCode(byte var3) {
      this.byteValue = var3;
   }

   public byte byteValue() {
      return this.byteValue;
   }

   public static MqttConnectReturnCode valueOf(byte var0) {
      if (VALUE_TO_CODE_MAP.containsKey(var0)) {
         return (MqttConnectReturnCode)VALUE_TO_CODE_MAP.get(var0);
      } else {
         throw new IllegalArgumentException("unknown connect return code: " + (var0 & 255));
      }
   }

   static {
      HashMap var0 = new HashMap();
      MqttConnectReturnCode[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         MqttConnectReturnCode var4 = var1[var3];
         var0.put(var4.byteValue, var4);
      }

      VALUE_TO_CODE_MAP = Collections.unmodifiableMap(var0);
   }
}
