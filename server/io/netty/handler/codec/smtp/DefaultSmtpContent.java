package io.netty.handler.codec.smtp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;

public class DefaultSmtpContent extends DefaultByteBufHolder implements SmtpContent {
   public DefaultSmtpContent(ByteBuf var1) {
      super(var1);
   }

   public SmtpContent copy() {
      return (SmtpContent)super.copy();
   }

   public SmtpContent duplicate() {
      return (SmtpContent)super.duplicate();
   }

   public SmtpContent retainedDuplicate() {
      return (SmtpContent)super.retainedDuplicate();
   }

   public SmtpContent replace(ByteBuf var1) {
      return new DefaultSmtpContent(var1);
   }

   public SmtpContent retain() {
      super.retain();
      return this;
   }

   public SmtpContent retain(int var1) {
      super.retain(var1);
      return this;
   }

   public SmtpContent touch() {
      super.touch();
      return this;
   }

   public SmtpContent touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
