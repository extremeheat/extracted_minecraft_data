package io.netty.handler.codec.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.util.IllegalReferenceCountException;

public class MqttPublishMessage extends MqttMessage implements ByteBufHolder {
   public MqttPublishMessage(MqttFixedHeader var1, MqttPublishVariableHeader var2, ByteBuf var3) {
      super(var1, var2, var3);
   }

   public MqttPublishVariableHeader variableHeader() {
      return (MqttPublishVariableHeader)super.variableHeader();
   }

   public ByteBuf payload() {
      return this.content();
   }

   public ByteBuf content() {
      ByteBuf var1 = (ByteBuf)super.payload();
      if (var1.refCnt() <= 0) {
         throw new IllegalReferenceCountException(var1.refCnt());
      } else {
         return var1;
      }
   }

   public MqttPublishMessage copy() {
      return this.replace(this.content().copy());
   }

   public MqttPublishMessage duplicate() {
      return this.replace(this.content().duplicate());
   }

   public MqttPublishMessage retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   public MqttPublishMessage replace(ByteBuf var1) {
      return new MqttPublishMessage(this.fixedHeader(), this.variableHeader(), var1);
   }

   public int refCnt() {
      return this.content().refCnt();
   }

   public MqttPublishMessage retain() {
      this.content().retain();
      return this;
   }

   public MqttPublishMessage retain(int var1) {
      this.content().retain(var1);
      return this;
   }

   public MqttPublishMessage touch() {
      this.content().touch();
      return this;
   }

   public MqttPublishMessage touch(Object var1) {
      this.content().touch(var1);
      return this;
   }

   public boolean release() {
      return this.content().release();
   }

   public boolean release(int var1) {
      return this.content().release(var1);
   }
}
