package io.netty.handler.codec.mqtt;

public final class MqttPubAckMessage extends MqttMessage {
   public MqttPubAckMessage(MqttFixedHeader var1, MqttMessageIdVariableHeader var2) {
      super(var1, var2);
   }

   public MqttMessageIdVariableHeader variableHeader() {
      return (MqttMessageIdVariableHeader)super.variableHeader();
   }
}
