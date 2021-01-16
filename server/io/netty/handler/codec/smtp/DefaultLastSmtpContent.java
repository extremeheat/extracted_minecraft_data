package io.netty.handler.codec.smtp;

import io.netty.buffer.ByteBuf;

public final class DefaultLastSmtpContent extends DefaultSmtpContent implements LastSmtpContent {
   public DefaultLastSmtpContent(ByteBuf var1) {
      super(var1);
   }

   public LastSmtpContent copy() {
      return (LastSmtpContent)super.copy();
   }

   public LastSmtpContent duplicate() {
      return (LastSmtpContent)super.duplicate();
   }

   public LastSmtpContent retainedDuplicate() {
      return (LastSmtpContent)super.retainedDuplicate();
   }

   public LastSmtpContent replace(ByteBuf var1) {
      return new DefaultLastSmtpContent(var1);
   }

   public DefaultLastSmtpContent retain() {
      super.retain();
      return this;
   }

   public DefaultLastSmtpContent retain(int var1) {
      super.retain(var1);
      return this;
   }

   public DefaultLastSmtpContent touch() {
      super.touch();
      return this;
   }

   public DefaultLastSmtpContent touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
