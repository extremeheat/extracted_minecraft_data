package io.netty.handler.codec.mqtt;

public final class MqttConnectMessage extends MqttMessage {
   public MqttConnectMessage(MqttFixedHeader var1, MqttConnectVariableHeader var2, MqttConnectPayload var3) {
      super(var1, var2, var3);
   }

   public MqttConnectVariableHeader variableHeader() {
      return (MqttConnectVariableHeader)super.variableHeader();
   }

   public MqttConnectPayload payload() {
      return (MqttConnectPayload)super.payload();
   }
}
