package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;

public final class MqttConnAckVariableHeader {
   private final MqttConnectReturnCode connectReturnCode;
   private final boolean sessionPresent;

   public MqttConnAckVariableHeader(MqttConnectReturnCode var1, boolean var2) {
      super();
      this.connectReturnCode = var1;
      this.sessionPresent = var2;
   }

   public MqttConnectReturnCode connectReturnCode() {
      return this.connectReturnCode;
   }

   public boolean isSessionPresent() {
      return this.sessionPresent;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '[' + "connectReturnCode=" + this.connectReturnCode + ", sessionPresent=" + this.sessionPresent + ']';
   }
}
