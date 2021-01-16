package io.netty.handler.codec.mqtt;

public final class MqttUnsubscribeMessage extends MqttMessage {
   public MqttUnsubscribeMessage(MqttFixedHeader var1, MqttMessageIdVariableHeader var2, MqttUnsubscribePayload var3) {
      super(var1, var2, var3);
   }

   public MqttMessageIdVariableHeader variableHeader() {
      return (MqttMessageIdVariableHeader)super.variableHeader();
   }

   public MqttUnsubscribePayload payload() {
      return (MqttUnsubscribePayload)super.payload();
   }
}
