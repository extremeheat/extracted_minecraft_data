package io.netty.channel.socket;

import java.io.IOException;

public final class ChannelOutputShutdownException extends IOException {
   private static final long serialVersionUID = 6712549938359321378L;

   public ChannelOutputShutdownException(String var1) {
      super(var1);
   }

   public ChannelOutputShutdownException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
