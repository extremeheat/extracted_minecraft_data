package io.netty.handler.codec.mqtt;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.StringUtil;

public class MqttMessage {
   private final MqttFixedHeader mqttFixedHeader;
   private final Object variableHeader;
   private final Object payload;
   private final DecoderResult decoderResult;

   public MqttMessage(MqttFixedHeader var1) {
      this(var1, (Object)null, (Object)null);
   }

   public MqttMessage(MqttFixedHeader var1, Object var2) {
      this(var1, var2, (Object)null);
   }

   public MqttMessage(MqttFixedHeader var1, Object var2, Object var3) {
      this(var1, var2, var3, DecoderResult.SUCCESS);
   }

   public MqttMessage(MqttFixedHeader var1, Object var2, Object var3, DecoderResult var4) {
      super();
      this.mqttFixedHeader = var1;
      this.variableHeader = var2;
      this.payload = var3;
      this.decoderResult = var4;
   }

   public MqttFixedHeader fixedHeader() {
      return this.mqttFixedHeader;
   }

   public Object variableHeader() {
      return this.variableHeader;
   }

   public Object payload() {
      return this.payload;
   }

   public DecoderResult decoderResult() {
      return this.decoderResult;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '[' + "fixedHeader=" + (this.fixedHeader() != null ? this.fixedHeader().toString() : "") + ", variableHeader=" + (this.variableHeader() != null ? this.variableHeader.toString() : "") + ", payload=" + (this.payload() != null ? this.payload.toString() : "") + ']';
   }
}
