package org.apache.logging.log4j.core.jmx;

import com.lmax.disruptor.RingBuffer;
import javax.management.ObjectName;

public class RingBufferAdmin implements RingBufferAdminMBean {
   private final RingBuffer<?> ringBuffer;
   private final ObjectName objectName;

   public static RingBufferAdmin forAsyncLogger(RingBuffer<?> var0, String var1) {
      String var2 = Server.escape(var1);
      String var3 = String.format("org.apache.logging.log4j2:type=%s,component=AsyncLoggerRingBuffer", var2);
      return new RingBufferAdmin(var0, var3);
   }

   public static RingBufferAdmin forAsyncLoggerConfig(RingBuffer<?> var0, String var1, String var2) {
      String var3 = Server.escape(var1);
      String var4 = Server.escape(var2);
      String var5 = String.format("org.apache.logging.log4j2:type=%s,component=Loggers,name=%s,subtype=RingBuffer", var3, var4);
      return new RingBufferAdmin(var0, var5);
   }

   protected RingBufferAdmin(RingBuffer<?> var1, String var2) {
      super();
      this.ringBuffer = var1;

      try {
         this.objectName = new ObjectName(var2);
      } catch (Exception var4) {
         throw new IllegalStateException(var4);
      }
   }

   public long getBufferSize() {
      return this.ringBuffer == null ? 0L : (long)this.ringBuffer.getBufferSize();
   }

   public long getRemainingCapacity() {
      return this.ringBuffer == null ? 0L : this.ringBuffer.remainingCapacity();
   }

   public ObjectName getObjectName() {
      return this.objectName;
   }
}
