package io.netty.handler.codec.smtp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

public interface SmtpContent extends ByteBufHolder {
   SmtpContent copy();

   SmtpContent duplicate();

   SmtpContent retainedDuplicate();

   SmtpContent replace(ByteBuf var1);

   SmtpContent retain();

   SmtpContent retain(int var1);

   SmtpContent touch();

   SmtpContent touch(Object var1);
}
