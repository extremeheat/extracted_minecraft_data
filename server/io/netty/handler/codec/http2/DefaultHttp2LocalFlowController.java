package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;

public class DefaultHttp2LocalFlowController implements Http2LocalFlowController {
   public static final float DEFAULT_WINDOW_UPDATE_RATIO = 0.5F;
   private final Http2Connection connection;
   private final Http2Connection.PropertyKey stateKey;
   private Http2FrameWriter frameWriter;
   private ChannelHandlerContext ctx;
   private float windowUpdateRatio;
   private int initialWindowSize;
   private static final DefaultHttp2LocalFlowController.FlowState REDUCED_FLOW_STATE = new DefaultHttp2LocalFlowController.FlowState() {
      public int windowSize() {
         return 0;
      }

      public int initialWindowSize() {
         return 0;
      }

      public void window(int var1) {
         throw new UnsupportedOperationException();
      }

      public void incrementInitialStreamWindow(int var1) {
      }

      public boolean writeWindowUpdateIfNeeded() throws Http2Exception {
         throw new UnsupportedOperationException();
      }

      public boolean consumeBytes(int var1) throws Http2Exception {
         return false;
      }

      public int unconsumedBytes() {
         return 0;
      }

      public float windowUpdateRatio() {
         throw new UnsupportedOperationException();
      }

      public void windowUpdateRatio(float var1) {
         throw new UnsupportedOperationException();
      }

      public void receiveFlowControlledFrame(int var1) throws Http2Exception {
         throw new UnsupportedOperationException();
      }

      public void incrementFlowControlWindows(int var1) throws Http2Exception {
      }

      public void endOfStream(boolean var1) {
         throw new UnsupportedOperationException();
      }
   };

   public DefaultHttp2LocalFlowController(Http2Connection var1) {
      this(var1, 0.5F, false);
   }

   public DefaultHttp2LocalFlowController(Http2Connection var1, float var2, boolean var3) {
      super();
      this.initialWindowSize = 65535;
      this.connection = (Http2Connection)ObjectUtil.checkNotNull(var1, "connection");
      this.windowUpdateRatio(var2);
      this.stateKey = var1.newKey();
      Object var4 = var3 ? new DefaultHttp2LocalFlowController.AutoRefillState(var1.connectionStream(), this.initialWindowSize) : new DefaultHttp2LocalFlowController.DefaultState(var1.connectionStream(), this.initialWindowSize);
      var1.connectionStream().setProperty(this.stateKey, var4);
      var1.addListener(new Http2ConnectionAdapter() {
         public void onStreamAdded(Http2Stream var1) {
            var1.setProperty(DefaultHttp2LocalFlowController.this.stateKey, DefaultHttp2LocalFlowController.REDUCED_FLOW_STATE);
         }

         public void onStreamActive(Http2Stream var1) {
            var1.setProperty(DefaultHttp2LocalFlowController.this.stateKey, DefaultHttp2LocalFlowController.this.new DefaultState(var1, DefaultHttp2LocalFlowController.this.initialWindowSize));
         }

         public void onStreamClosed(Http2Stream var1) {
            try {
               DefaultHttp2LocalFlowController.FlowState var2 = DefaultHttp2LocalFlowController.this.state(var1);
               int var3 = var2.unconsumedBytes();
               if (DefaultHttp2LocalFlowController.this.ctx != null && var3 > 0) {
                  DefaultHttp2LocalFlowController.this.connectionState().consumeBytes(var3);
                  var2.consumeBytes(var3);
               }
            } catch (Http2Exception var7) {
               PlatformDependent.throwException(var7);
            } finally {
               var1.setProperty(DefaultHttp2LocalFlowController.this.stateKey, DefaultHttp2LocalFlowController.REDUCED_FLOW_STATE);
            }

         }
      });
   }

   public DefaultHttp2LocalFlowController frameWriter(Http2FrameWriter var1) {
      this.frameWriter = (Http2FrameWriter)ObjectUtil.checkNotNull(var1, "frameWriter");
      return this;
   }

   public void channelHandlerContext(ChannelHandlerContext var1) {
      this.ctx = (ChannelHandlerContext)ObjectUtil.checkNotNull(var1, "ctx");
   }

   public void initialWindowSize(int var1) throws Http2Exception {
      assert this.ctx == null || this.ctx.executor().inEventLoop();

      int var2 = var1 - this.initialWindowSize;
      this.initialWindowSize = var1;
      DefaultHttp2LocalFlowController.WindowUpdateVisitor var3 = new DefaultHttp2LocalFlowController.WindowUpdateVisitor(var2);
      this.connection.forEachActiveStream(var3);
      var3.throwIfError();
   }

   public int initialWindowSize() {
      return this.initialWindowSize;
   }

   public int windowSize(Http2Stream var1) {
      return this.state(var1).windowSize();
   }

   public int initialWindowSize(Http2Stream var1) {
      return this.state(var1).initialWindowSize();
   }

   public void incrementWindowSize(Http2Stream var1, int var2) throws Http2Exception {
      assert this.ctx != null && this.ctx.executor().inEventLoop();

      DefaultHttp2LocalFlowController.FlowState var3 = this.state(var1);
      var3.incrementInitialStreamWindow(var2);
      var3.writeWindowUpdateIfNeeded();
   }

   public boolean consumeBytes(Http2Stream var1, int var2) throws Http2Exception {
      assert this.ctx != null && this.ctx.executor().inEventLoop();

      if (var2 < 0) {
         throw new IllegalArgumentException("numBytes must not be negative");
      } else if (var2 == 0) {
         return false;
      } else if (var1 != null && !isClosed(var1)) {
         if (var1.id() == 0) {
            throw new UnsupportedOperationException("Returning bytes for the connection window is not supported");
         } else {
            boolean var3 = this.connectionState().consumeBytes(var2);
            var3 |= this.state(var1).consumeBytes(var2);
            return var3;
         }
      } else {
         return false;
      }
   }

   public int unconsumedBytes(Http2Stream var1) {
      return this.state(var1).unconsumedBytes();
   }

   private static void checkValidRatio(float var0) {
      if (Double.compare((double)var0, 0.0D) <= 0 || Double.compare((double)var0, 1.0D) >= 0) {
         throw new IllegalArgumentException("Invalid ratio: " + var0);
      }
   }

   public void windowUpdateRatio(float var1) {
      assert this.ctx == null || this.ctx.executor().inEventLoop();

      checkValidRatio(var1);
      this.windowUpdateRatio = var1;
   }

   public float windowUpdateRatio() {
      return this.windowUpdateRatio;
   }

   public void windowUpdateRatio(Http2Stream var1, float var2) throws Http2Exception {
      assert this.ctx != null && this.ctx.executor().inEventLoop();

      checkValidRatio(var2);
      DefaultHttp2LocalFlowController.FlowState var3 = this.state(var1);
      var3.windowUpdateRatio(var2);
      var3.writeWindowUpdateIfNeeded();
   }

   public float windowUpdateRatio(Http2Stream var1) throws Http2Exception {
      return this.state(var1).windowUpdateRatio();
   }

   public void receiveFlowControlledFrame(Http2Stream var1, ByteBuf var2, int var3, boolean var4) throws Http2Exception {
      assert this.ctx != null && this.ctx.executor().inEventLoop();

      int var5 = var2.readableBytes() + var3;
      DefaultHttp2LocalFlowController.FlowState var6 = this.connectionState();
      var6.receiveFlowControlledFrame(var5);
      if (var1 != null && !isClosed(var1)) {
         DefaultHttp2LocalFlowController.FlowState var7 = this.state(var1);
         var7.endOfStream(var4);
         var7.receiveFlowControlledFrame(var5);
      } else if (var5 > 0) {
         var6.consumeBytes(var5);
      }

   }

   private DefaultHttp2LocalFlowController.FlowState connectionState() {
      return (DefaultHttp2LocalFlowController.FlowState)this.connection.connectionStream().getProperty(this.stateKey);
   }

   private DefaultHttp2LocalFlowController.FlowState state(Http2Stream var1) {
      return (DefaultHttp2LocalFlowController.FlowState)var1.getProperty(this.stateKey);
   }

   private static boolean isClosed(Http2Stream var0) {
      return var0.state() == Http2Stream.State.CLOSED;
   }

   private final class WindowUpdateVisitor implements Http2StreamVisitor {
      private Http2Exception.CompositeStreamException compositeException;
      private final int delta;

      public WindowUpdateVisitor(int var2) {
         super();
         this.delta = var2;
      }

      public boolean visit(Http2Stream var1) throws Http2Exception {
         try {
            DefaultHttp2LocalFlowController.FlowState var2 = DefaultHttp2LocalFlowController.this.state(var1);
            var2.incrementFlowControlWindows(this.delta);
            var2.incrementInitialStreamWindow(this.delta);
         } catch (Http2Exception.StreamException var3) {
            if (this.compositeException == null) {
               this.compositeException = new Http2Exception.CompositeStreamException(var3.error(), 4);
            }

            this.compositeException.add(var3);
         }

         return true;
      }

      public void throwIfError() throws Http2Exception.CompositeStreamException {
         if (this.compositeException != null) {
            throw this.compositeException;
         }
      }
   }

   private interface FlowState {
      int windowSize();

      int initialWindowSize();

      void window(int var1);

      void incrementInitialStreamWindow(int var1);

      boolean writeWindowUpdateIfNeeded() throws Http2Exception;

      boolean consumeBytes(int var1) throws Http2Exception;

      int unconsumedBytes();

      float windowUpdateRatio();

      void windowUpdateRatio(float var1);

      void receiveFlowControlledFrame(int var1) throws Http2Exception;

      void incrementFlowControlWindows(int var1) throws Http2Exception;

      void endOfStream(boolean var1);
   }

   private class DefaultState implements DefaultHttp2LocalFlowController.FlowState {
      private final Http2Stream stream;
      private int window;
      private int processedWindow;
      private int initialStreamWindowSize;
      private float streamWindowUpdateRatio;
      private int lowerBound;
      private boolean endOfStream;

      public DefaultState(Http2Stream var2, int var3) {
         super();
         this.stream = var2;
         this.window(var3);
         this.streamWindowUpdateRatio = DefaultHttp2LocalFlowController.this.windowUpdateRatio;
      }

      public void window(int var1) {
         assert DefaultHttp2LocalFlowController.this.ctx == null || DefaultHttp2LocalFlowController.this.ctx.executor().inEventLoop();

         this.window = this.processedWindow = this.initialStreamWindowSize = var1;
      }

      public int windowSize() {
         return this.window;
      }

      public int initialWindowSize() {
         return this.initialStreamWindowSize;
      }

      public void endOfStream(boolean var1) {
         this.endOfStream = var1;
      }

      public float windowUpdateRatio() {
         return this.streamWindowUpdateRatio;
      }

      public void windowUpdateRatio(float var1) {
         assert DefaultHttp2LocalFlowController.this.ctx == null || DefaultHttp2LocalFlowController.this.ctx.executor().inEventLoop();

         this.streamWindowUpdateRatio = var1;
      }

      public void incrementInitialStreamWindow(int var1) {
         int var2 = (int)Math.min(2147483647L, Math.max(0L, (long)this.initialStreamWindowSize + (long)var1));
         var1 = var2 - this.initialStreamWindowSize;
         this.initialStreamWindowSize += var1;
      }

      public void incrementFlowControlWindows(int var1) throws Http2Exception {
         if (var1 > 0 && this.window > 2147483647 - var1) {
            throw Http2Exception.streamError(this.stream.id(), Http2Error.FLOW_CONTROL_ERROR, "Flow control window overflowed for stream: %d", this.stream.id());
         } else {
            this.window += var1;
            this.processedWindow += var1;
            this.lowerBound = var1 < 0 ? var1 : 0;
         }
      }

      public void receiveFlowControlledFrame(int var1) throws Http2Exception {
         assert var1 >= 0;

         this.window -= var1;
         if (this.window < this.lowerBound) {
            throw Http2Exception.streamError(this.stream.id(), Http2Error.FLOW_CONTROL_ERROR, "Flow control window exceeded for stream: %d", this.stream.id());
         }
      }

      private void returnProcessedBytes(int var1) throws Http2Exception {
         if (this.processedWindow - var1 < this.window) {
            throw Http2Exception.streamError(this.stream.id(), Http2Error.INTERNAL_ERROR, "Attempting to return too many bytes for stream %d", this.stream.id());
         } else {
            this.processedWindow -= var1;
         }
      }

      public boolean consumeBytes(int var1) throws Http2Exception {
         this.returnProcessedBytes(var1);
         return this.writeWindowUpdateIfNeeded();
      }

      public int unconsumedBytes() {
         return this.processedWindow - this.window;
      }

      public boolean writeWindowUpdateIfNeeded() throws Http2Exception {
         if (!this.endOfStream && this.initialStreamWindowSize > 0) {
            int var1 = (int)((float)this.initialStreamWindowSize * this.streamWindowUpdateRatio);
            if (this.processedWindow <= var1) {
               this.writeWindowUpdate();
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      private void writeWindowUpdate() throws Http2Exception {
         int var1 = this.initialStreamWindowSize - this.processedWindow;

         try {
            this.incrementFlowControlWindows(var1);
         } catch (Throwable var3) {
            throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, var3, "Attempting to return too many bytes for stream %d", this.stream.id());
         }

         DefaultHttp2LocalFlowController.this.frameWriter.writeWindowUpdate(DefaultHttp2LocalFlowController.this.ctx, this.stream.id(), var1, DefaultHttp2LocalFlowController.this.ctx.newPromise());
      }
   }

   private final class AutoRefillState extends DefaultHttp2LocalFlowController.DefaultState {
      public AutoRefillState(Http2Stream var2, int var3) {
         super(var2, var3);
      }

      public void receiveFlowControlledFrame(int var1) throws Http2Exception {
         super.receiveFlowControlledFrame(var1);
         super.consumeBytes(var1);
      }

      public boolean consumeBytes(int var1) throws Http2Exception {
         return false;
      }
   }
}
