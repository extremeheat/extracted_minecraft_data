package net.minecraft.network;

import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import net.minecraft.ReportedException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import org.slf4j.Logger;

public class PacketProcessor implements AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   private final Queue<ListenerAndPacket<?>> packetsToBeHandled = Queues.newConcurrentLinkedQueue();
   private final Thread runningThread;
   private boolean closed;

   public PacketProcessor(Thread var1) {
      super();
      this.runningThread = var1;
   }

   public boolean isSameThread() {
      return Thread.currentThread() == this.runningThread;
   }

   public <T extends PacketListener> void scheduleIfPossible(T var1, Packet<T> var2) {
      if (this.closed) {
         throw new RejectedExecutionException("Server already shutting down");
      } else {
         this.packetsToBeHandled.add(new ListenerAndPacket(var1, var2));
      }
   }

   public void processQueuedPackets() {
      if (!this.closed) {
         while(!this.packetsToBeHandled.isEmpty()) {
            ((ListenerAndPacket)this.packetsToBeHandled.poll()).handle();
         }
      }

   }

   public void close() {
      this.closed = true;
   }

   static record ListenerAndPacket<T extends PacketListener>(T listener, Packet<T> packet) {
      ListenerAndPacket(T var1, Packet<T> var2) {
         super();
         this.listener = var1;
         this.packet = var2;
      }

      public void handle() {
         if (this.listener.shouldHandleMessage(this.packet)) {
            try {
               this.packet.handle(this.listener);
            } catch (Exception var3) {
               if (var3 instanceof ReportedException) {
                  ReportedException var2 = (ReportedException)var3;
                  if (var2.getCause() instanceof OutOfMemoryError) {
                     throw PacketUtils.makeReportedException(var3, this.packet, this.listener);
                  }
               }

               this.listener.onPacketError(this.packet, var3);
            }
         } else {
            PacketProcessor.LOGGER.debug("Ignoring packet due to disconnection: {}", this.packet);
         }

      }
   }
}
