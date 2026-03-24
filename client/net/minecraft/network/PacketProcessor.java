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
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Queue<ListenerAndPacket<?>> packetsToBeHandled = Queues.newConcurrentLinkedQueue();
   private final Thread runningThread;
   private boolean closed;

   public PacketProcessor(final Thread runningThread) {
      super();
      this.runningThread = runningThread;
   }

   public boolean isSameThread() {
      return Thread.currentThread() == this.runningThread;
   }

   public <T extends PacketListener> void scheduleIfPossible(final T listener, final Packet<T> packet) {
      if (this.closed) {
         throw new RejectedExecutionException("Server already shutting down");
      } else {
         this.packetsToBeHandled.add(new ListenerAndPacket(listener, packet));
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

   private static record ListenerAndPacket<T extends PacketListener>(T listener, Packet<T> packet) {
      private ListenerAndPacket {
         super();
      }

      public void handle() {
         if (this.listener.shouldHandleMessage(this.packet)) {
            try {
               this.packet.handle(this.listener);
            } catch (Exception var3) {
               if (var3 instanceof ReportedException) {
                  ReportedException re = (ReportedException)var3;
                  if (re.getCause() instanceof OutOfMemoryError) {
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
