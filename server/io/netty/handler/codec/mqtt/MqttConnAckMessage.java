package io.netty.handler.codec.mqtt;

public final class MqttConnAckMessage extends MqttMessage {
   public MqttConnAckMessage(MqttFixedHeader var1, MqttConnAckVariableHeader var2) {
      super(var1, var2);
   }

   public MqttConnAckVariableHeader variableHeader() {
      return (MqttConnAckVariableHeader)super.variableHeader();
   }
}
