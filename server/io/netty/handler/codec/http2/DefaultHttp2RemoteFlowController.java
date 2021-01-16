package io.netty.handler.codec.http2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.ArrayDeque;
import java.util.Deque;

public class DefaultHttp2RemoteFlowController implements Http2RemoteFlowController {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultHttp2RemoteFlowController.class);
   private static final int MIN_WRITABLE_CHUNK = 32768;
   private final Http2Connection connection;
   private final Http2Connection.PropertyKey stateKey;
   private final StreamByteDistributor streamByteDistributor;
   private final DefaultHttp2RemoteFlowController.FlowState connectionState;
   private int initialWindowSize;
   private DefaultHttp2RemoteFlowController.WritabilityMonitor monitor;
   private ChannelHandlerContext ctx;

   public DefaultHttp2RemoteFlowController(Http2Connection var1) {
      this(var1, (Http2RemoteFlowController.Listener)null);
   }

   public DefaultHttp2RemoteFlowController(Http2Connection var1, StreamByteDistributor var2) {
      this(var1, var2, (Http2RemoteFlowController.Listener)null);
   }

   public DefaultHttp2RemoteFlowController(Http2Connection var1, Http2RemoteFlowController.Listener var2) {
      this(var1, new WeightedFairQueueByteDistributor(var1), var2);
   }

   public DefaultHttp2RemoteFlowController(Http2Connection var1, StreamByteDistributor var2, Http2RemoteFlowController.Listener var3) {
      super();
      this.initialWindowSize = 65535;
      this.connection = (Http2Connection)ObjectUtil.checkNotNull(var1, "connection");
      this.streamByteDistributor = (StreamByteDistributor)ObjectUtil.checkNotNull(var2, "streamWriteDistributor");
      this.stateKey = var1.newKey();
      this.connectionState = new DefaultHttp2RemoteFlowController.FlowState(var1.connectionStream());
      var1.connectionStream().setProperty(this.stateKey, this.connectionState);
      this.listener(var3);
      this.monitor.windowSize(this.connectionState, this.initialWindowSize);
      var1.addListener(new Http2ConnectionAdapter() {
         public void onStreamAdded(Http2Stream var1) {
            var1.setProperty(DefaultHttp2RemoteFlowController.this.stateKey, DefaultHttp2RemoteFlowController.this.new FlowState(var1));
         }

         public void onStreamActive(Http2Stream var1) {
            DefaultHttp2RemoteFlowController.this.monitor.windowSize(DefaultHttp2RemoteFlowController.this.state(var1), DefaultHttp2RemoteFlowController.this.initialWindowSize);
         }

         public void onStreamClosed(Http2Stream var1) {
            DefaultHttp2RemoteFlowController.this.state(var1).cancel(Http2Error.STREAM_CLOSED, (Throwable)null);
         }

         public void onStreamHalfClosed(Http2Stream var1) {
            if (Http2Stream.State.HALF_CLOSED_LOCAL == var1.state()) {
               DefaultHttp2RemoteFlowController.this.state(var1).cancel(Http2Error.STREAM_CLOSED, (Throwable)null);
            }

         }
      });
   }

   public void channelHandlerContext(ChannelHandlerContext var1) throws Http2Exception {
      this.ctx = (ChannelHandlerContext)ObjectUtil.checkNotNull(var1, "ctx");
      this.channelWritabilityChanged();
      if (this.isChannelWritable()) {
         this.writePendingBytes();
      }

   }

   public ChannelHandlerContext channelHandlerContext() {
      return this.ctx;
   }

   public void initialWindowSize(int var1) throws Http2Exception {
      assert this.ctx == null || this.ctx.executor().inEventLoop();

      this.monitor.initialWindowSize(var1);
   }

   public int initialWindowSize() {
      return this.initialWindowSize;
   }

   public int windowSize(Http2Stream var1) {
      return this.state(var1).windowSize();
   }

   public boolean isWritable(Http2Stream var1) {
      return this.monitor.isWritable(this.state(var1));
   }

   public void channelWritabilityChanged() throws Http2Exception {
      this.monitor.channelWritabilityChange();
   }

   public void updateDependencyTree(int var1, int var2, short var3, boolean var4) {
      assert var3 >= 1 && var3 <= 256 : "Invalid weight";

      assert var1 != var2 : "A stream cannot depend on itself";

      assert var1 > 0 && var2 >= 0 : "childStreamId must be > 0. parentStreamId must be >= 0.";

      this.streamByteDistributor.updateDependencyTree(var1, var2, var3, var4);
   }

   private boolean isChannelWritable() {
      return this.ctx != null && this.isChannelWritable0();
   }

   private boolean isChannelWritable0() {
      return this.ctx.channel().isWritable();
   }

   public void listener(Http2RemoteFlowController.Listener var1) {
      this.monitor = (DefaultHttp2RemoteFlowController.WritabilityMonitor)(var1 == null ? new DefaultHttp2RemoteFlowController.WritabilityMonitor() : new DefaultHttp2RemoteFlowController.ListenerWritabilityMonitor(var1));
   }

   public void incrementWindowSize(Http2Stream var1, int var2) throws Http2Exception {
      assert this.ctx == null || this.ctx.executor().inEventLoop();

      this.monitor.incrementWindowSize(this.state(var1), var2);
   }

   public void addFlowControlled(Http2Stream var1, Http2RemoteFlowController.FlowControlled var2) {
      assert this.ctx == null || this.ctx.executor().inEventLoop();

      ObjectUtil.checkNotNull(var2, "frame");

      try {
         this.monitor.enqueueFrame(this.state(var1), var2);
      } catch (Throwable var4) {
         var2.error(this.ctx, var4);
      }

   }

   public boolean hasFlowControlled(Http2Stream var1) {
      return this.state(var1).hasFrame();
   }

   private DefaultHttp2RemoteFlowController.FlowState state(Http2Stream var1) {
      return (DefaultHttp2RemoteFlowController.FlowState)var1.getProperty(this.stateKey);
   }

   private int connectionWindowSize() {
      return this.connectionState.windowSize();
   }

   private int minUsableChannelBytes() {
      return Math.max(this.ctx.channel().config().getWriteBufferLowWaterMark(), 32768);
   }

   private int maxUsableChannelBytes() {
      int var1 = (int)Math.min(2147483647L, this.ctx.channel().bytesBeforeUnwritable());
      int var2 = var1 > 0 ? Math.max(var1, this.minUsableChannelBytes()) : 0;
      return Math.min(this.connectionState.windowSize(), var2);
   }

   private int writableBytes() {
      return Math.min(this.connectionWindowSize(), this.maxUsableChannelBytes());
   }

   public void writePendingBytes() throws Http2Exception {
      this.monitor.writePendingBytes();
   }

   private final class ListenerWritabilityMonitor extends DefaultHttp2RemoteFlowController.WritabilityMonitor implements Http2StreamVisitor {
      private final Http2RemoteFlowController.Listener listener;

      ListenerWritabilityMonitor(Http2RemoteFlowController.Listener var2) {
         super(null);
         this.listener = var2;
      }

      public boolean visit(Http2Stream var1) throws Http2Exception {
         DefaultHttp2RemoteFlowController.FlowState var2 = DefaultHttp2RemoteFlowController.this.state(var1);
         if (this.isWritable(var2) != var2.markedWritability()) {
            this.notifyWritabilityChanged(var2);
         }

         return true;
      }

      void windowSize(DefaultHttp2RemoteFlowController.FlowState var1, int var2) {
         super.windowSize(var1, var2);

         try {
            this.checkStateWritability(var1);
         } catch (Http2Exception var4) {
            throw new RuntimeException("Caught unexpected exception from window", var4);
         }
      }

      void incrementWindowSize(DefaultHttp2RemoteFlowController.FlowState var1, int var2) throws Http2Exception {
         super.incrementWindowSize(var1, var2);
         this.checkStateWritability(var1);
      }

      void initialWindowSize(int var1) throws Http2Exception {
         super.initialWindowSize(var1);
         if (this.isWritableConnection()) {
            this.checkAllWritabilityChanged();
         }

      }

      void enqueueFrame(DefaultHttp2RemoteFlowController.FlowState var1, Http2RemoteFlowController.FlowControlled var2) throws Http2Exception {
         super.enqueueFrame(var1, var2);
         this.checkConnectionThenStreamWritabilityChanged(var1);
      }

      void stateCancelled(DefaultHttp2RemoteFlowController.FlowState var1) {
         try {
            this.checkConnectionThenStreamWritabilityChanged(var1);
         } catch (Http2Exception var3) {
            throw new RuntimeException("Caught unexpected exception from checkAllWritabilityChanged", var3);
         }
      }

      void channelWritabilityChange() throws Http2Exception {
         if (DefaultHttp2RemoteFlowController.this.connectionState.markedWritability() != DefaultHttp2RemoteFlowController.this.isChannelWritable()) {
            this.checkAllWritabilityChanged();
         }

      }

      private void checkStateWritability(DefaultHttp2RemoteFlowController.FlowState var1) throws Http2Exception {
         if (this.isWritable(var1) != var1.markedWritability()) {
            if (var1 == DefaultHttp2RemoteFlowController.this.connectionState) {
               this.checkAllWritabilityChanged();
            } else {
               this.notifyWritabilityChanged(var1);
            }
         }

      }

      private void notifyWritabilityChanged(DefaultHttp2RemoteFlowController.FlowState var1) {
         var1.markedWritability(!var1.markedWritability());

         try {
            this.listener.writabilityChanged(var1.stream);
         } catch (Throwable var3) {
            DefaultHttp2RemoteFlowController.logger.error("Caught Throwable from listener.writabilityChanged", var3);
         }

      }

      private void checkConnectionThenStreamWritabilityChanged(DefaultHttp2RemoteFlowController.FlowState var1) throws Http2Exception {
         if (this.isWritableConnection() != DefaultHttp2RemoteFlowController.this.connectionState.markedWritability()) {
            this.checkAllWritabilityChanged();
         } else if (this.isWritable(var1) != var1.markedWritability()) {
            this.notifyWritabilityChanged(var1);
         }

      }

      private void checkAllWritabilityChanged() throws Http2Exception {
         DefaultHttp2RemoteFlowController.this.connectionState.markedWritability(this.isWritableConnection());
         DefaultHttp2RemoteFlowController.this.connection.forEachActiveStream(this);
      }
   }

   private class WritabilityMonitor implements StreamByteDistributor.Writer {
      private boolean inWritePendingBytes;
      private long totalPendingBytes;

      private WritabilityMonitor() {
         super();
      }

      public final void write(Http2Stream var1, int var2) {
         DefaultHttp2RemoteFlowController.this.state(var1).writeAllocatedBytes(var2);
      }

      void channelWritabilityChange() throws Http2Exception {
      }

      void stateCancelled(DefaultHttp2RemoteFlowController.FlowState var1) {
      }

      void windowSize(DefaultHttp2RemoteFlowController.FlowState var1, int var2) {
         var1.windowSize(var2);
      }

      void incrementWindowSize(DefaultHttp2RemoteFlowController.FlowState var1, int var2) throws Http2Exception {
         var1.incrementStreamWindow(var2);
      }

      void enqueueFrame(DefaultHttp2RemoteFlowController.FlowState var1, Http2RemoteFlowController.FlowControlled var2) throws Http2Exception {
         var1.enqueueFrame(var2);
      }

      final void incrementPendingBytes(int var1) {
         this.totalPendingBytes += (long)var1;
      }

      final boolean isWritable(DefaultHttp2RemoteFlowController.FlowState var1) {
         return this.isWritableConnection() && var1.isWritable();
      }

      final void writePendingBytes() throws Http2Exception {
         if (!this.inWritePendingBytes) {
            this.inWritePendingBytes = true;

            try {
               int var1 = DefaultHttp2RemoteFlowController.this.writableBytes();

               while(DefaultHttp2RemoteFlowController.this.streamByteDistributor.distribute(var1, this) && (var1 = DefaultHttp2RemoteFlowController.this.writableBytes()) > 0 && DefaultHttp2RemoteFlowController.this.isChannelWritable0()) {
               }
            } finally {
               this.inWritePendingBytes = false;
            }

         }
      }

      void initialWindowSize(int var1) throws Http2Exception {
         if (var1 < 0) {
            throw new IllegalArgumentException("Invalid initial window size: " + var1);
         } else {
            final int var2 = var1 - DefaultHttp2RemoteFlowController.this.initialWindowSize;
            DefaultHttp2RemoteFlowController.this.initialWindowSize = var1;
            DefaultHttp2RemoteFlowController.this.connection.forEachActiveStream(new Http2StreamVisitor() {
               public boolean visit(Http2Stream var1) throws Http2Exception {
                  DefaultHttp2RemoteFlowController.this.state(var1).incrementStreamWindow(var2);
                  return true;
               }
            });
            if (var2 > 0 && DefaultHttp2RemoteFlowController.this.isChannelWritable()) {
               this.writePendingBytes();
            }

         }
      }

      final boolean isWritableConnection() {
         return (long)DefaultHttp2RemoteFlowController.this.connectionState.windowSize() - this.totalPendingBytes > 0L && DefaultHttp2RemoteFlowController.this.isChannelWritable();
      }

      // $FF: synthetic method
      WritabilityMonitor(Object var2) {
         this();
      }
   }

   private final class FlowState implements StreamByteDistributor.StreamState {
      private final Http2Stream stream;
      private final Deque<Http2RemoteFlowController.FlowControlled> pendingWriteQueue;
      private int window;
      private long pendingBytes;
      private boolean markedWritable;
      private boolean writing;
      private boolean cancelled;

      FlowState(Http2Stream var2) {
         super();
         this.stream = var2;
         this.pendingWriteQueue = new ArrayDeque(2);
      }

      boolean isWritable() {
         return (long)this.windowSize() > this.pendingBytes() && !this.cancelled;
      }

      public Http2Stream stream() {
         return this.stream;
      }

      boolean markedWritability() {
         return this.markedWritable;
      }

      void markedWritability(boolean var1) {
         this.markedWritable = var1;
      }

      public int windowSize() {
         return this.window;
      }

      void windowSize(int var1) {
         this.window = var1;
      }

      int writeAllocatedBytes(int var1) {
         Throwable var4 = null;

         int var3;
         byte var20;
         try {
            assert !this.writing;

            this.writing = true;
            boolean var6 = false;

            Http2RemoteFlowController.FlowControlled var5;
            while(!this.cancelled && (var5 = this.peek()) != null) {
               int var7 = Math.min(var1, this.writableWindow());
               if (var7 <= 0 && var5.size() > 0) {
                  break;
               }

               var6 = true;
               int var8 = var5.size();

               try {
                  var5.write(DefaultHttp2RemoteFlowController.this.ctx, Math.max(0, var7));
                  if (var5.size() == 0) {
                     this.pendingWriteQueue.remove();
                     var5.writeComplete();
                  }
               } finally {
                  var1 -= var8 - var5.size();
               }
            }

            if (var6) {
               return var3;
            }

            var20 = -1;
         } catch (Throwable var18) {
            this.cancelled = true;
            var4 = var18;
            return var3;
         } finally {
            this.writing = false;
            var3 = var1 - var1;
            this.decrementPendingBytes(var3, false);
            this.decrementFlowControlWindow(var3);
            if (this.cancelled) {
               this.cancel(Http2Error.INTERNAL_ERROR, var4);
            }

         }

         return var20;
      }

      int incrementStreamWindow(int var1) throws Http2Exception {
         if (var1 > 0 && 2147483647 - var1 < this.window) {
            throw Http2Exception.streamError(this.stream.id(), Http2Error.FLOW_CONTROL_ERROR, "Window size overflow for stream: %d", this.stream.id());
         } else {
            this.window += var1;
            DefaultHttp2RemoteFlowController.this.streamByteDistributor.updateStreamableBytes(this);
            return this.window;
         }
      }

      private int writableWindow() {
         return Math.min(this.window, DefaultHttp2RemoteFlowController.this.connectionWindowSize());
      }

      public long pendingBytes() {
         return this.pendingBytes;
      }

      void enqueueFrame(Http2RemoteFlowController.FlowControlled var1) {
         Http2RemoteFlowController.FlowControlled var2 = (Http2RemoteFlowController.FlowControlled)this.pendingWriteQueue.peekLast();
         if (var2 == null) {
            this.enqueueFrameWithoutMerge(var1);
         } else {
            int var3 = var2.size();
            if (var2.merge(DefaultHttp2RemoteFlowController.this.ctx, var1)) {
               this.incrementPendingBytes(var2.size() - var3, true);
            } else {
               this.enqueueFrameWithoutMerge(var1);
            }
         }
      }

      private void enqueueFrameWithoutMerge(Http2RemoteFlowController.FlowControlled var1) {
         this.pendingWriteQueue.offer(var1);
         this.incrementPendingBytes(var1.size(), true);
      }

      public boolean hasFrame() {
         return !this.pendingWriteQueue.isEmpty();
      }

      private Http2RemoteFlowController.FlowControlled peek() {
         return (Http2RemoteFlowController.FlowControlled)this.pendingWriteQueue.peek();
      }

      void cancel(Http2Error var1, Throwable var2) {
         this.cancelled = true;
         if (!this.writing) {
            Http2RemoteFlowController.FlowControlled var3 = (Http2RemoteFlowController.FlowControlled)this.pendingWriteQueue.poll();
            if (var3 != null) {
               Http2Exception var4 = Http2Exception.streamError(this.stream.id(), var1, var2, "Stream closed before write could take place");

               do {
                  this.writeError(var3, var4);
                  var3 = (Http2RemoteFlowController.FlowControlled)this.pendingWriteQueue.poll();
               } while(var3 != null);
            }

            DefaultHttp2RemoteFlowController.this.streamByteDistributor.updateStreamableBytes(this);
            DefaultHttp2RemoteFlowController.this.monitor.stateCancelled(this);
         }
      }

      private void incrementPendingBytes(int var1, boolean var2) {
         this.pendingBytes += (long)var1;
         DefaultHttp2RemoteFlowController.this.monitor.incrementPendingBytes(var1);
         if (var2) {
            DefaultHttp2RemoteFlowController.this.streamByteDistributor.updateStreamableBytes(this);
         }

      }

      private void decrementPendingBytes(int var1, boolean var2) {
         this.incrementPendingBytes(-var1, var2);
      }

      private void decrementFlowControlWindow(int var1) {
         try {
            int var2 = -var1;
            DefaultHttp2RemoteFlowController.this.connectionState.incrementStreamWindow(var2);
            this.incrementStreamWindow(var2);
         } catch (Http2Exception var3) {
            throw new IllegalStateException("Invalid window state when writing frame: " + var3.getMessage(), var3);
         }
      }

      private void writeError(Http2RemoteFlowController.FlowControlled var1, Http2Exception var2) {
         assert DefaultHttp2RemoteFlowController.this.ctx != null;

         this.decrementPendingBytes(var1.size(), true);
         var1.error(DefaultHttp2RemoteFlowController.this.ctx, var2);
      }
   }
}
