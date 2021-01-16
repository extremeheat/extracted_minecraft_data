package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;

public final class MqttMessageIdVariableHeader {
   private final int messageId;

   public static MqttMessageIdVariableHeader from(int var0) {
      if (var0 >= 1 && var0 <= 65535) {
         return new MqttMessageIdVariableHeader(var0);
      } else {
         throw new IllegalArgumentException("messageId: " + var0 + " (expected: 1 ~ 65535)");
      }
   }

   private MqttMessageIdVariableHeader(int var1) {
      super();
      this.messageId = var1;
   }

   public int messageId() {
      return this.messageId;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '[' + "messageId=" + this.messageId + ']';
   }
}
