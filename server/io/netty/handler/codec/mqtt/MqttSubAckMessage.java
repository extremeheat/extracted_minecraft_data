package io.netty.handler.codec.mqtt;

public final class MqttSubAckMessage extends MqttMessage {
   public MqttSubAckMessage(MqttFixedHeader var1, MqttMessageIdVariableHeader var2, MqttSubAckPayload var3) {
      super(var1, var2, var3);
   }

   public MqttMessageIdVariableHeader variableHeader() {
      return (MqttMessageIdVariableHeader)super.variableHeader();
   }

   public MqttSubAckPayload payload() {
      return (MqttSubAckPayload)super.payload();
   }
}
