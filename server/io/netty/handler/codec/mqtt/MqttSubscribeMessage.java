package io.netty.handler.codec.mqtt;

public final class MqttSubscribeMessage extends MqttMessage {
   public MqttSubscribeMessage(MqttFixedHeader var1, MqttMessageIdVariableHeader var2, MqttSubscribePayload var3) {
      super(var1, var2, var3);
   }

   public MqttMessageIdVariableHeader variableHeader() {
      return (MqttMessageIdVariableHeader)super.variableHeader();
   }

   public MqttSubscribePayload payload() {
      return (MqttSubscribePayload)super.payload();
   }
}
