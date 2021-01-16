package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.util.Constants;

public abstract class AbstractOutputStreamAppender<M extends OutputStreamManager> extends AbstractAppender {
   private final boolean immediateFlush;
   private final M manager;

   protected AbstractOutputStreamAppender(String var1, Layout<? extends Serializable> var2, Filter var3, boolean var4, boolean var5, M var6) {
      super(var1, var3, var2, var4);
      this.manager = var6;
      this.immediateFlush = var5;
   }

   public boolean getImmediateFlush() {
      return this.immediateFlush;
   }

   public M getManager() {
      return this.manager;
   }

   public void start() {
      if (this.getLayout() == null) {
         LOGGER.error("No layout set for the appender named [" + this.getName() + "].");
      }

      if (this.manager == null) {
         LOGGER.error("No OutputStreamManager set for the appender named [" + this.getName() + "].");
      }

      super.start();
   }

   public boolean stop(long var1, TimeUnit var3) {
      return this.stop(var1, var3, true);
   }

   protected boolean stop(long var1, TimeUnit var3, boolean var4) {
      boolean var5 = super.stop(var1, var3, var4);
      var5 &= this.manager.stop(var1, var3);
      if (var4) {
         this.setStopped();
      }

      LOGGER.debug((String)"Appender {} stopped with status {}", (Object)this.getName(), (Object)var5);
      return var5;
   }

   public void append(LogEvent var1) {
      try {
         this.tryAppend(var1);
      } catch (AppenderLoggingException var3) {
         this.error("Unable to write to stream " + this.manager.getName() + " for appender " + this.getName() + ": " + var3);
         throw var3;
      }
   }

   private void tryAppend(LogEvent var1) {
      if (Constants.ENABLE_DIRECT_ENCODERS) {
         this.directEncodeEvent(var1);
      } else {
         this.writeByteArrayToManager(var1);
      }

   }

   protected void directEncodeEvent(LogEvent var1) {
      this.getLayout().encode(var1, this.manager);
      if (this.immediateFlush || var1.isEndOfBatch()) {
         this.manager.flush();
      }

   }

   protected void writeByteArrayToManager(LogEvent var1) {
      byte[] var2 = this.getLayout().toByteArray(var1);
      if (var2 != null && var2.length > 0) {
         this.manager.write(var2, this.immediateFlush || var1.isEndOfBatch());
      }

   }

   public abstract static class Builder<B extends AbstractOutputStreamAppender.Builder<B>> extends AbstractAppender.Builder<B> {
      @PluginBuilderAttribute
      private boolean bufferedIo = true;
      @PluginBuilderAttribute
      private int bufferSize;
      @PluginBuilderAttribute
      private boolean immediateFlush;

      public Builder() {
         super();
         this.bufferSize = Constants.ENCODER_BYTE_BUFFER_SIZE;
         this.immediateFlush = true;
      }

      public int getBufferSize() {
         return this.bufferSize;
      }

      public boolean isBufferedIo() {
         return this.bufferedIo;
      }

      public boolean isImmediateFlush() {
         return this.immediateFlush;
      }

      public B withImmediateFlush(boolean var1) {
         this.immediateFlush = var1;
         return (AbstractOutputStreamAppender.Builder)this.asBuilder();
      }

      public B withBufferedIo(boolean var1) {
         this.bufferedIo = var1;
         return (AbstractOutputStreamAppender.Builder)this.asBuilder();
      }

      public B withBufferSize(int var1) {
         this.bufferSize = var1;
         return (AbstractOutputStreamAppender.Builder)this.asBuilder();
      }
   }
}
