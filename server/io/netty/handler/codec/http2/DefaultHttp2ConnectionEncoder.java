package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.CoalescingBufferQueue;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;

public class DefaultHttp2ConnectionEncoder implements Http2ConnectionEncoder {
   private final Http2FrameWriter frameWriter;
   private final Http2Connection connection;
   private Http2LifecycleManager lifecycleManager;
   private final ArrayDeque<Http2Settings> outstandingLocalSettingsQueue = new ArrayDeque(4);

   public DefaultHttp2ConnectionEncoder(Http2Connection var1, Http2FrameWriter var2) {
      super();
      this.connection = (Http2Connection)ObjectUtil.checkNotNull(var1, "connection");
      this.frameWriter = (Http2FrameWriter)ObjectUtil.checkNotNull(var2, "frameWriter");
      if (var1.remote().flowController() == null) {
         var1.remote().flowController(new DefaultHttp2RemoteFlowController(var1));
      }

   }

   public void lifecycleManager(Http2LifecycleManager var1) {
      this.lifecycleManager = (Http2LifecycleManager)ObjectUtil.checkNotNull(var1, "lifecycleManager");
   }

   public Http2FrameWriter frameWriter() {
      return this.frameWriter;
   }

   public Http2Connection connection() {
      return this.connection;
   }

   public final Http2RemoteFlowController flowController() {
      return (Http2RemoteFlowController)this.connection().remote().flowController();
   }

   public void remoteSettings(Http2Settings var1) throws Http2Exception {
      Boolean var2 = var1.pushEnabled();
      Http2FrameWriter.Configuration var3 = this.configuration();
      Http2HeadersEncoder.Configuration var4 = var3.headersConfiguration();
      Http2FrameSizePolicy var5 = var3.frameSizePolicy();
      if (var2 != null) {
         if (!this.connection.isServer() && var2) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Client received a value of ENABLE_PUSH specified to other than 0");
         }

         this.connection.remote().allowPushTo(var2);
      }

      Long var6 = var1.maxConcurrentStreams();
      if (var6 != null) {
         this.connection.local().maxActiveStreams((int)Math.min(var6, 2147483647L));
      }

      Long var7 = var1.headerTableSize();
      if (var7 != null) {
         var4.maxHeaderTableSize((long)((int)Math.min(var7, 2147483647L)));
      }

      Long var8 = var1.maxHeaderListSize();
      if (var8 != null) {
         var4.maxHeaderListSize(var8);
      }

      Integer var9 = var1.maxFrameSize();
      if (var9 != null) {
         var5.maxFrameSize(var9);
      }

      Integer var10 = var1.initialWindowSize();
      if (var10 != null) {
         this.flowController().initialWindowSize(var10);
      }

   }

   public ChannelFuture writeData(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, boolean var5, ChannelPromise var6) {
      Http2Stream var7;
      try {
         var7 = this.requireStream(var2);
         switch(var7.state()) {
         case OPEN:
         case HALF_CLOSED_REMOTE:
            break;
         default:
            throw new IllegalStateException("Stream " + var7.id() + " in unexpected state " + var7.state());
         }
      } catch (Throwable var9) {
         var3.release();
         return var6.setFailure(var9);
      }

      this.flowController().addFlowControlled(var7, new DefaultHttp2ConnectionEncoder.FlowControlledData(var7, var3, var4, var5, var6));
      return var6;
   }

   public ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5, ChannelPromise var6) {
      return this.writeHeaders(var1, var2, var3, 0, (short)16, false, var4, var5, var6);
   }

   private static boolean validateHeadersSentState(Http2Stream var0, Http2Headers var1, boolean var2, boolean var3) {
      boolean var4 = var2 && HttpStatusClass.valueOf(var1.status()) == HttpStatusClass.INFORMATIONAL;
      if ((!var4 && var3 || !var0.isHeadersSent()) && !var0.isTrailersSent()) {
         return var4;
      } else {
         throw new IllegalStateException("Stream " + var0.id() + " sent too many headers EOS: " + var3);
      }
   }

   public ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8, ChannelPromise var9) {
      try {
         final Http2Stream var10 = this.connection.stream(var2);
         if (var10 == null) {
            try {
               var10 = this.connection.local().createStream(var2, var8);
            } catch (Http2Exception var15) {
               if (this.connection.remote().mayHaveCreatedStream(var2)) {
                  var9.tryFailure(new IllegalStateException("Stream no longer exists: " + var2, var15));
                  return var9;
               }

               throw var15;
            }
         } else {
            switch(var10.state()) {
            case OPEN:
            case HALF_CLOSED_REMOTE:
               break;
            case RESERVED_LOCAL:
               var10.open(var8);
               break;
            default:
               throw new IllegalStateException("Stream " + var10.id() + " in unexpected state " + var10.state());
            }
         }

         Http2RemoteFlowController var11 = this.flowController();
         if (var8 && var11.hasFlowControlled(var10)) {
            var11.addFlowControlled(var10, new DefaultHttp2ConnectionEncoder.FlowControlledHeaders(var10, var3, var4, var5, var6, var7, true, var9));
            return var9;
         } else {
            boolean var12 = validateHeadersSentState(var10, var3, this.connection.isServer(), var8);
            if (var8) {
               ChannelFutureListener var14 = new ChannelFutureListener() {
                  public void operationComplete(ChannelFuture var1) throws Exception {
                     DefaultHttp2ConnectionEncoder.this.lifecycleManager.closeStreamLocal(var10, var1);
                  }
               };
               var9 = var9.unvoid().addListener(var14);
            }

            ChannelFuture var13 = this.frameWriter.writeHeaders(var1, var2, var3, var4, var5, var6, var7, var8, var9);
            Throwable var17 = var13.cause();
            if (var17 == null) {
               var10.headersSent(var12);
               if (!var13.isSuccess()) {
                  this.notifyLifecycleManagerOnError(var13, var1);
               }
            } else {
               this.lifecycleManager.onError(var1, true, var17);
            }

            return var13;
         }
      } catch (Throwable var16) {
         this.lifecycleManager.onError(var1, true, var16);
         var9.tryFailure(var16);
         return var9;
      }
   }

   public ChannelFuture writePriority(ChannelHandlerContext var1, int var2, int var3, short var4, boolean var5, ChannelPromise var6) {
      return this.frameWriter.writePriority(var1, var2, var3, var4, var5, var6);
   }

   public ChannelFuture writeRstStream(ChannelHandlerContext var1, int var2, long var3, ChannelPromise var5) {
      return this.lifecycleManager.resetStream(var1, var2, var3, var5);
   }

   public ChannelFuture writeSettings(ChannelHandlerContext var1, Http2Settings var2, ChannelPromise var3) {
      this.outstandingLocalSettingsQueue.add(var2);

      try {
         Boolean var4 = var2.pushEnabled();
         if (var4 != null && this.connection.isServer()) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Server sending SETTINGS frame with ENABLE_PUSH specified");
         }
      } catch (Throwable var5) {
         return var3.setFailure(var5);
      }

      return this.frameWriter.writeSettings(var1, var2, var3);
   }

   public ChannelFuture writeSettingsAck(ChannelHandlerContext var1, ChannelPromise var2) {
      return this.frameWriter.writeSettingsAck(var1, var2);
   }

   public ChannelFuture writePing(ChannelHandlerContext var1, boolean var2, long var3, ChannelPromise var5) {
      return this.frameWriter.writePing(var1, var2, var3, var5);
   }

   public ChannelFuture writePushPromise(ChannelHandlerContext var1, int var2, int var3, Http2Headers var4, int var5, ChannelPromise var6) {
      try {
         if (this.connection.goAwayReceived()) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Sending PUSH_PROMISE after GO_AWAY received.");
         } else {
            Http2Stream var7 = this.requireStream(var2);
            this.connection.local().reservePushStream(var3, var7);
            ChannelFuture var8 = this.frameWriter.writePushPromise(var1, var2, var3, var4, var5, var6);
            Throwable var9 = var8.cause();
            if (var9 == null) {
               var7.pushPromiseSent();
               if (!var8.isSuccess()) {
                  this.notifyLifecycleManagerOnError(var8, var1);
               }
            } else {
               this.lifecycleManager.onError(var1, true, var9);
            }

            return var8;
         }
      } catch (Throwable var10) {
         this.lifecycleManager.onError(var1, true, var10);
         var6.tryFailure(var10);
         return var6;
      }
   }

   public ChannelFuture writeGoAway(ChannelHandlerContext var1, int var2, long var3, ByteBuf var5, ChannelPromise var6) {
      return this.lifecycleManager.goAway(var1, var2, var3, var5, var6);
   }

   public ChannelFuture writeWindowUpdate(ChannelHandlerContext var1, int var2, int var3, ChannelPromise var4) {
      return var4.setFailure(new UnsupportedOperationException("Use the Http2[Inbound|Outbound]FlowController objects to control window sizes"));
   }

   public ChannelFuture writeFrame(ChannelHandlerContext var1, byte var2, int var3, Http2Flags var4, ByteBuf var5, ChannelPromise var6) {
      return this.frameWriter.writeFrame(var1, var2, var3, var4, var5, var6);
   }

   public void close() {
      this.frameWriter.close();
   }

   public Http2Settings pollSentSettings() {
      return (Http2Settings)this.outstandingLocalSettingsQueue.poll();
   }

   public Http2FrameWriter.Configuration configuration() {
      return this.frameWriter.configuration();
   }

   private Http2Stream requireStream(int var1) {
      Http2Stream var2 = this.connection.stream(var1);
      if (var2 == null) {
         String var3;
         if (this.connection.streamMayHaveExisted(var1)) {
            var3 = "Stream no longer exists: " + var1;
         } else {
            var3 = "Stream does not exist: " + var1;
         }

         throw new IllegalArgumentException(var3);
      } else {
         return var2;
      }
   }

   private void notifyLifecycleManagerOnError(ChannelFuture var1, final ChannelHandlerContext var2) {
      var1.addListener(new ChannelFutureListener() {
         public void operationComplete(ChannelFuture var1) throws Exception {
            Throwable var2x = var1.cause();
            if (var2x != null) {
               DefaultHttp2ConnectionEncoder.this.lifecycleManager.onError(var2, true, var2x);
            }

         }
      });
   }

   public abstract class FlowControlledBase implements Http2RemoteFlowController.FlowControlled, ChannelFutureListener {
      protected final Http2Stream stream;
      protected ChannelPromise promise;
      protected boolean endOfStream;
      protected int padding;

      FlowControlledBase(Http2Stream var2, int var3, boolean var4, ChannelPromise var5) {
         super();
         if (var3 < 0) {
            throw new IllegalArgumentException("padding must be >= 0");
         } else {
            this.padding = var3;
            this.endOfStream = var4;
            this.stream = var2;
            this.promise = var5;
         }
      }

      public void writeComplete() {
         if (this.endOfStream) {
            DefaultHttp2ConnectionEncoder.this.lifecycleManager.closeStreamLocal(this.stream, this.promise);
         }

      }

      public void operationComplete(ChannelFuture var1) throws Exception {
         if (!var1.isSuccess()) {
            this.error(DefaultHttp2ConnectionEncoder.this.flowController().channelHandlerContext(), var1.cause());
         }

      }
   }

   private final class FlowControlledHeaders extends DefaultHttp2ConnectionEncoder.FlowControlledBase {
      private final Http2Headers headers;
      private final int streamDependency;
      private final short weight;
      private final boolean exclusive;

      FlowControlledHeaders(Http2Stream var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8, ChannelPromise var9) {
         super(var2, var7, var8, var9);
         this.headers = var3;
         this.streamDependency = var4;
         this.weight = var5;
         this.exclusive = var6;
      }

      public int size() {
         return 0;
      }

      public void error(ChannelHandlerContext var1, Throwable var2) {
         if (var1 != null) {
            DefaultHttp2ConnectionEncoder.this.lifecycleManager.onError(var1, true, var2);
         }

         this.promise.tryFailure(var2);
      }

      public void write(ChannelHandlerContext var1, int var2) {
         boolean var3 = DefaultHttp2ConnectionEncoder.validateHeadersSentState(this.stream, this.headers, DefaultHttp2ConnectionEncoder.this.connection.isServer(), this.endOfStream);
         if (this.promise.isVoid()) {
            this.promise = var1.newPromise();
         }

         this.promise.addListener(this);
         ChannelFuture var4 = DefaultHttp2ConnectionEncoder.this.frameWriter.writeHeaders(var1, this.stream.id(), this.headers, this.streamDependency, this.weight, this.exclusive, this.padding, this.endOfStream, this.promise);
         Throwable var5 = var4.cause();
         if (var5 == null) {
            this.stream.headersSent(var3);
         }

      }

      public boolean merge(ChannelHandlerContext var1, Http2RemoteFlowController.FlowControlled var2) {
         return false;
      }
   }

   private final class FlowControlledData extends DefaultHttp2ConnectionEncoder.FlowControlledBase {
      private final CoalescingBufferQueue queue;
      private int dataSize;

      FlowControlledData(Http2Stream var2, ByteBuf var3, int var4, boolean var5, ChannelPromise var6) {
         super(var2, var4, var5, var6);
         this.queue = new CoalescingBufferQueue(var6.channel());
         this.queue.add(var3, var6);
         this.dataSize = this.queue.readableBytes();
      }

      public int size() {
         return this.dataSize + this.padding;
      }

      public void error(ChannelHandlerContext var1, Throwable var2) {
         this.queue.releaseAndFailAll(var2);
         DefaultHttp2ConnectionEncoder.this.lifecycleManager.onError(var1, true, var2);
      }

      public void write(ChannelHandlerContext var1, int var2) {
         int var3 = this.queue.readableBytes();
         if (!this.endOfStream) {
            if (var3 == 0) {
               ChannelPromise var8 = var1.newPromise().addListener(this);
               var1.write(this.queue.remove(0, var8), var8);
               return;
            }

            if (var2 == 0) {
               return;
            }
         }

         int var4 = Math.min(var3, var2);
         ChannelPromise var5 = var1.newPromise().addListener(this);
         ByteBuf var6 = this.queue.remove(var4, var5);
         this.dataSize = this.queue.readableBytes();
         int var7 = Math.min(var2 - var4, this.padding);
         this.padding -= var7;
         DefaultHttp2ConnectionEncoder.this.frameWriter().writeData(var1, this.stream.id(), var6, var7, this.endOfStream && this.size() == 0, var5);
      }

      public boolean merge(ChannelHandlerContext var1, Http2RemoteFlowController.FlowControlled var2) {
         DefaultHttp2ConnectionEncoder.FlowControlledData var3;
         if (DefaultHttp2ConnectionEncoder.FlowControlledData.class == var2.getClass() && 2147483647 - (var3 = (DefaultHttp2ConnectionEncoder.FlowControlledData)var2).size() >= this.size()) {
            var3.queue.copyTo(this.queue);
            this.dataSize = this.queue.readableBytes();
            this.padding = Math.max(this.padding, var3.padding);
            this.endOfStream = var3.endOfStream;
            return true;
         } else {
            return false;
         }
      }
   }
}
