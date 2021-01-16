package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;
import java.util.Deque;

public final class UniformStreamByteDistributor implements StreamByteDistributor {
   private final Http2Connection.PropertyKey stateKey;
   private final Deque<UniformStreamByteDistributor.State> queue = new ArrayDeque(4);
   private int minAllocationChunk = 1024;
   private long totalStreamableBytes;

   public UniformStreamByteDistributor(Http2Connection var1) {
      super();
      this.stateKey = var1.newKey();
      Http2Stream var2 = var1.connectionStream();
      var2.setProperty(this.stateKey, new UniformStreamByteDistributor.State(var2));
      var1.addListener(new Http2ConnectionAdapter() {
         public void onStreamAdded(Http2Stream var1) {
            var1.setProperty(UniformStreamByteDistributor.this.stateKey, UniformStreamByteDistributor.this.new State(var1));
         }

         public void onStreamClosed(Http2Stream var1) {
            UniformStreamByteDistributor.this.state(var1).close();
         }
      });
   }

   public void minAllocationChunk(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("minAllocationChunk must be > 0");
      } else {
         this.minAllocationChunk = var1;
      }
   }

   public void updateStreamableBytes(StreamByteDistributor.StreamState var1) {
      this.state(var1.stream()).updateStreamableBytes(Http2CodecUtil.streamableBytes(var1), var1.hasFrame(), var1.windowSize());
   }

   public void updateDependencyTree(int var1, int var2, short var3, boolean var4) {
   }

   public boolean distribute(int var1, StreamByteDistributor.Writer var2) throws Http2Exception {
      int var3 = this.queue.size();
      if (var3 == 0) {
         return this.totalStreamableBytes > 0L;
      } else {
         int var4 = Math.max(this.minAllocationChunk, var1 / var3);
         UniformStreamByteDistributor.State var5 = (UniformStreamByteDistributor.State)this.queue.pollFirst();

         do {
            var5.enqueued = false;
            if (!var5.windowNegative) {
               if (var1 == 0 && var5.streamableBytes > 0) {
                  this.queue.addFirst(var5);
                  var5.enqueued = true;
                  break;
               }

               int var6 = Math.min(var4, Math.min(var1, var5.streamableBytes));
               var1 -= var6;
               var5.write(var6, var2);
            }
         } while((var5 = (UniformStreamByteDistributor.State)this.queue.pollFirst()) != null);

         return this.totalStreamableBytes > 0L;
      }
   }

   private UniformStreamByteDistributor.State state(Http2Stream var1) {
      return (UniformStreamByteDistributor.State)((Http2Stream)ObjectUtil.checkNotNull(var1, "stream")).getProperty(this.stateKey);
   }

   private final class State {
      final Http2Stream stream;
      int streamableBytes;
      boolean windowNegative;
      boolean enqueued;
      boolean writing;

      State(Http2Stream var2) {
         super();
         this.stream = var2;
      }

      void updateStreamableBytes(int var1, boolean var2, int var3) {
         assert var2 || var1 == 0 : "hasFrame: " + var2 + " newStreamableBytes: " + var1;

         int var4 = var1 - this.streamableBytes;
         if (var4 != 0) {
            this.streamableBytes = var1;
            UniformStreamByteDistributor.this.totalStreamableBytes = UniformStreamByteDistributor.this.totalStreamableBytes + (long)var4;
         }

         this.windowNegative = var3 < 0;
         if (var2 && (var3 > 0 || var3 == 0 && !this.writing)) {
            this.addToQueue();
         }

      }

      void write(int var1, StreamByteDistributor.Writer var2) throws Http2Exception {
         this.writing = true;

         try {
            var2.write(this.stream, var1);
         } catch (Throwable var7) {
            throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, var7, "byte distribution write error");
         } finally {
            this.writing = false;
         }

      }

      void addToQueue() {
         if (!this.enqueued) {
            this.enqueued = true;
            UniformStreamByteDistributor.this.queue.addLast(this);
         }

      }

      void removeFromQueue() {
         if (this.enqueued) {
            this.enqueued = false;
            UniformStreamByteDistributor.this.queue.remove(this);
         }

      }

      void close() {
         this.removeFromQueue();
         this.updateStreamableBytes(0, false, 0);
      }
   }
}
