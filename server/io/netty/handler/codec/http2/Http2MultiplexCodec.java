package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.DefaultMaxMessagesRecvByteBufAllocator;
import io.netty.channel.EventLoop;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.VoidChannelPromise;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ThrowableUtil;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;

public class Http2MultiplexCodec extends Http2FrameCodec {
   private static final ChannelFutureListener CHILD_CHANNEL_REGISTRATION_LISTENER = new ChannelFutureListener() {
      public void operationComplete(ChannelFuture var1) throws Exception {
         Http2MultiplexCodec.registerDone(var1);
      }
   };
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
   private static final ClosedChannelException CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), Http2MultiplexCodec.DefaultHttp2StreamChannel.Http2ChannelUnsafe.class, "write(...)");
   private static final int MIN_HTTP2_FRAME_SIZE = 9;
   private final ChannelHandler inboundStreamHandler;
   private int initialOutboundStreamWindow = 65535;
   private boolean parentReadInProgress;
   private int idCount;
   private Http2MultiplexCodec.DefaultHttp2StreamChannel head;
   private Http2MultiplexCodec.DefaultHttp2StreamChannel tail;
   volatile ChannelHandlerContext ctx;

   Http2MultiplexCodec(Http2ConnectionEncoder var1, Http2ConnectionDecoder var2, Http2Settings var3, ChannelHandler var4) {
      super(var1, var2, var3);
      this.inboundStreamHandler = var4;
   }

   private static void registerDone(ChannelFuture var0) {
      if (!var0.isSuccess()) {
         Channel var1 = var0.channel();
         if (var1.isRegistered()) {
            var1.close();
         } else {
            var1.unsafe().closeForcibly();
         }
      }

   }

   public final void handlerAdded0(ChannelHandlerContext var1) throws Exception {
      if (var1.executor() != var1.channel().eventLoop()) {
         throw new IllegalStateException("EventExecutor must be EventLoop of Channel");
      } else {
         this.ctx = var1;
      }
   }

   public final void handlerRemoved0(ChannelHandlerContext var1) throws Exception {
      super.handlerRemoved0(var1);

      Http2MultiplexCodec.DefaultHttp2StreamChannel var3;
      for(Http2MultiplexCodec.DefaultHttp2StreamChannel var2 = this.head; var2 != null; var3.next = null) {
         var3 = var2;
         var2 = var2.next;
      }

      this.head = this.tail = null;
   }

   Http2MultiplexCodec.Http2MultiplexCodecStream newStream() {
      return new Http2MultiplexCodec.Http2MultiplexCodecStream();
   }

   final void onHttp2Frame(ChannelHandlerContext var1, Http2Frame var2) {
      if (var2 instanceof Http2StreamFrame) {
         Http2StreamFrame var3 = (Http2StreamFrame)var2;
         this.onHttp2StreamFrame(((Http2MultiplexCodec.Http2MultiplexCodecStream)var3.stream()).channel, var3);
      } else if (var2 instanceof Http2GoAwayFrame) {
         this.onHttp2GoAwayFrame(var1, (Http2GoAwayFrame)var2);
         var1.fireChannelRead(var2);
      } else if (var2 instanceof Http2SettingsFrame) {
         Http2Settings var4 = ((Http2SettingsFrame)var2).settings();
         if (var4.initialWindowSize() != null) {
            this.initialOutboundStreamWindow = var4.initialWindowSize();
         }

         var1.fireChannelRead(var2);
      } else {
         var1.fireChannelRead(var2);
      }

   }

   final void onHttp2StreamStateChanged(ChannelHandlerContext var1, Http2FrameStream var2) {
      Http2MultiplexCodec.Http2MultiplexCodecStream var3 = (Http2MultiplexCodec.Http2MultiplexCodecStream)var2;
      switch(var2.state()) {
      case HALF_CLOSED_REMOTE:
      case OPEN:
         if (var3.channel == null) {
            ChannelFuture var4 = var1.channel().eventLoop().register(new Http2MultiplexCodec.DefaultHttp2StreamChannel(var3, false));
            if (var4.isDone()) {
               registerDone(var4);
            } else {
               var4.addListener(CHILD_CHANNEL_REGISTRATION_LISTENER);
            }
         }
         break;
      case CLOSED:
         Http2MultiplexCodec.DefaultHttp2StreamChannel var5 = var3.channel;
         if (var5 != null) {
            var5.streamClosed();
         }
      }

   }

   final void onHttp2StreamWritabilityChanged(ChannelHandlerContext var1, Http2FrameStream var2, boolean var3) {
      ((Http2MultiplexCodec.Http2MultiplexCodecStream)var2).channel.writabilityChanged(var3);
   }

   final Http2StreamChannel newOutboundStream() {
      return new Http2MultiplexCodec.DefaultHttp2StreamChannel(this.newStream(), true);
   }

   final void onHttp2FrameStreamException(ChannelHandlerContext var1, Http2FrameStreamException var2) {
      Http2FrameStream var3 = var2.stream();
      Http2MultiplexCodec.DefaultHttp2StreamChannel var4 = ((Http2MultiplexCodec.Http2MultiplexCodecStream)var3).channel;

      try {
         var4.pipeline().fireExceptionCaught(var2.getCause());
      } finally {
         var4.unsafe().closeForcibly();
      }

   }

   private void onHttp2StreamFrame(Http2MultiplexCodec.DefaultHttp2StreamChannel var1, Http2StreamFrame var2) {
      switch(var1.fireChildRead(var2)) {
      case READ_PROCESSED_BUT_STOP_READING:
         var1.fireChildReadComplete();
         break;
      case READ_PROCESSED_OK_TO_PROCESS_MORE:
         this.addChildChannelToReadPendingQueue(var1);
      case READ_IGNORED_CHANNEL_INACTIVE:
      case READ_QUEUED:
         break;
      default:
         throw new Error();
      }

   }

   final void addChildChannelToReadPendingQueue(Http2MultiplexCodec.DefaultHttp2StreamChannel var1) {
      if (!var1.fireChannelReadPending) {
         assert var1.next == null;

         if (this.tail == null) {
            assert this.head == null;

            this.tail = this.head = var1;
         } else {
            this.tail.next = var1;
            this.tail = var1;
         }

         var1.fireChannelReadPending = true;
      }

   }

   private void onHttp2GoAwayFrame(ChannelHandlerContext var1, final Http2GoAwayFrame var2) {
      try {
         this.forEachActiveStream(new Http2FrameStreamVisitor() {
            public boolean visit(Http2FrameStream var1) {
               int var2x = var1.id();
               Http2MultiplexCodec.DefaultHttp2StreamChannel var3 = ((Http2MultiplexCodec.Http2MultiplexCodecStream)var1).channel;
               if (var2x > var2.lastStreamId() && Http2MultiplexCodec.this.connection().local().isValidStreamId(var2x)) {
                  var3.pipeline().fireUserEventTriggered(var2.retainedDuplicate());
               }

               return true;
            }
         });
      } catch (Http2Exception var4) {
         var1.fireExceptionCaught(var4);
         var1.close();
      }

   }

   public final void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      this.parentReadInProgress = false;
      this.onChannelReadComplete(var1);
      this.channelReadComplete0(var1);
   }

   public final void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      this.parentReadInProgress = true;
      super.channelRead(var1, var2);
   }

   final void onChannelReadComplete(ChannelHandlerContext var1) {
      try {
         for(Http2MultiplexCodec.DefaultHttp2StreamChannel var2 = this.head; var2 != null; var2 = var2.next) {
            if (var2.fireChannelReadPending) {
               var2.fireChannelReadPending = false;
               var2.fireChildReadComplete();
            }

            var2.next = null;
         }
      } finally {
         this.tail = this.head = null;
         this.flush0(var1);
      }

   }

   void flush0(ChannelHandlerContext var1) {
      this.flush(var1);
   }

   boolean onBytesConsumed(ChannelHandlerContext var1, Http2FrameStream var2, int var3) throws Http2Exception {
      return this.consumeBytes(var2.id(), var3);
   }

   private boolean initialWritability(Http2FrameCodec.DefaultHttp2FrameStream var1) {
      return !Http2CodecUtil.isStreamIdValid(var1.id()) || this.isWritable(var1);
   }

   private final class DefaultHttp2StreamChannel extends DefaultAttributeMap implements Http2StreamChannel {
      private final Http2MultiplexCodec.DefaultHttp2StreamChannel.Http2StreamChannelConfig config = new Http2MultiplexCodec.DefaultHttp2StreamChannel.Http2StreamChannelConfig(this);
      private final Http2MultiplexCodec.DefaultHttp2StreamChannel.Http2ChannelUnsafe unsafe = new Http2MultiplexCodec.DefaultHttp2StreamChannel.Http2ChannelUnsafe();
      private final ChannelId channelId;
      private final ChannelPipeline pipeline;
      private final Http2FrameCodec.DefaultHttp2FrameStream stream;
      private final ChannelPromise closePromise;
      private final boolean outbound;
      private volatile boolean registered;
      private volatile boolean writable;
      private boolean outboundClosed;
      private boolean closePending;
      private boolean readInProgress;
      private Queue<Object> inboundBuffer;
      private boolean firstFrameWritten;
      private boolean streamClosedWithoutError;
      private boolean inFireChannelReadComplete;
      boolean fireChannelReadPending;
      Http2MultiplexCodec.DefaultHttp2StreamChannel next;

      DefaultHttp2StreamChannel(Http2FrameCodec.DefaultHttp2FrameStream var2, boolean var3) {
         super();
         this.stream = var2;
         this.outbound = var3;
         this.writable = Http2MultiplexCodec.this.initialWritability(var2);
         ((Http2MultiplexCodec.Http2MultiplexCodecStream)var2).channel = this;
         this.pipeline = new DefaultChannelPipeline(this) {
            protected void incrementPendingOutboundBytes(long var1) {
            }

            protected void decrementPendingOutboundBytes(long var1) {
            }
         };
         this.closePromise = this.pipeline.newPromise();
         this.channelId = new Http2StreamChannelId(this.parent().id(), ++Http2MultiplexCodec.this.idCount);
      }

      public Http2FrameStream stream() {
         return this.stream;
      }

      void streamClosed() {
         this.streamClosedWithoutError = true;
         if (this.readInProgress) {
            this.unsafe().closeForcibly();
         } else {
            this.closePending = true;
         }

      }

      public ChannelMetadata metadata() {
         return Http2MultiplexCodec.METADATA;
      }

      public ChannelConfig config() {
         return this.config;
      }

      public boolean isOpen() {
         return !this.closePromise.isDone();
      }

      public boolean isActive() {
         return this.isOpen();
      }

      public boolean isWritable() {
         return this.writable;
      }

      public ChannelId id() {
         return this.channelId;
      }

      public EventLoop eventLoop() {
         return this.parent().eventLoop();
      }

      public Channel parent() {
         return Http2MultiplexCodec.this.ctx.channel();
      }

      public boolean isRegistered() {
         return this.registered;
      }

      public SocketAddress localAddress() {
         return this.parent().localAddress();
      }

      public SocketAddress remoteAddress() {
         return this.parent().remoteAddress();
      }

      public ChannelFuture closeFuture() {
         return this.closePromise;
      }

      public long bytesBeforeUnwritable() {
         return (long)this.config().getWriteBufferHighWaterMark();
      }

      public long bytesBeforeWritable() {
         return 0L;
      }

      public Channel.Unsafe unsafe() {
         return this.unsafe;
      }

      public ChannelPipeline pipeline() {
         return this.pipeline;
      }

      public ByteBufAllocator alloc() {
         return this.config().getAllocator();
      }

      public Channel read() {
         this.pipeline().read();
         return this;
      }

      public Channel flush() {
         this.pipeline().flush();
         return this;
      }

      public ChannelFuture bind(SocketAddress var1) {
         return this.pipeline().bind(var1);
      }

      public ChannelFuture connect(SocketAddress var1) {
         return this.pipeline().connect(var1);
      }

      public ChannelFuture connect(SocketAddress var1, SocketAddress var2) {
         return this.pipeline().connect(var1, var2);
      }

      public ChannelFuture disconnect() {
         return this.pipeline().disconnect();
      }

      public ChannelFuture close() {
         return this.pipeline().close();
      }

      public ChannelFuture deregister() {
         return this.pipeline().deregister();
      }

      public ChannelFuture bind(SocketAddress var1, ChannelPromise var2) {
         return this.pipeline().bind(var1, var2);
      }

      public ChannelFuture connect(SocketAddress var1, ChannelPromise var2) {
         return this.pipeline().connect(var1, var2);
      }

      public ChannelFuture connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
         return this.pipeline().connect(var1, var2, var3);
      }

      public ChannelFuture disconnect(ChannelPromise var1) {
         return this.pipeline().disconnect(var1);
      }

      public ChannelFuture close(ChannelPromise var1) {
         return this.pipeline().close(var1);
      }

      public ChannelFuture deregister(ChannelPromise var1) {
         return this.pipeline().deregister(var1);
      }

      public ChannelFuture write(Object var1) {
         return this.pipeline().write(var1);
      }

      public ChannelFuture write(Object var1, ChannelPromise var2) {
         return this.pipeline().write(var1, var2);
      }

      public ChannelFuture writeAndFlush(Object var1, ChannelPromise var2) {
         return this.pipeline().writeAndFlush(var1, var2);
      }

      public ChannelFuture writeAndFlush(Object var1) {
         return this.pipeline().writeAndFlush(var1);
      }

      public ChannelPromise newPromise() {
         return this.pipeline().newPromise();
      }

      public ChannelProgressivePromise newProgressivePromise() {
         return this.pipeline().newProgressivePromise();
      }

      public ChannelFuture newSucceededFuture() {
         return this.pipeline().newSucceededFuture();
      }

      public ChannelFuture newFailedFuture(Throwable var1) {
         return this.pipeline().newFailedFuture(var1);
      }

      public ChannelPromise voidPromise() {
         return this.pipeline().voidPromise();
      }

      public int hashCode() {
         return this.id().hashCode();
      }

      public boolean equals(Object var1) {
         return this == var1;
      }

      public int compareTo(Channel var1) {
         return this == var1 ? 0 : this.id().compareTo(var1.id());
      }

      public String toString() {
         return this.parent().toString() + "(H2 - " + this.stream + ')';
      }

      void writabilityChanged(boolean var1) {
         assert this.eventLoop().inEventLoop();

         if (var1 != this.writable && this.isActive()) {
            this.writable = var1;
            this.pipeline().fireChannelWritabilityChanged();
         }

      }

      Http2MultiplexCodec.ReadState fireChildRead(Http2Frame var1) {
         assert this.eventLoop().inEventLoop();

         if (!this.isActive()) {
            ReferenceCountUtil.release(var1);
            return Http2MultiplexCodec.ReadState.READ_IGNORED_CHANNEL_INACTIVE;
         } else if (!this.readInProgress || this.inboundBuffer != null && !this.inboundBuffer.isEmpty()) {
            if (this.inboundBuffer == null) {
               this.inboundBuffer = new ArrayDeque(4);
            }

            this.inboundBuffer.add(var1);
            return Http2MultiplexCodec.ReadState.READ_QUEUED;
         } else {
            RecvByteBufAllocator.ExtendedHandle var2 = this.unsafe.recvBufAllocHandle();
            this.unsafe.doRead0(var1, var2);
            return var2.continueReading() ? Http2MultiplexCodec.ReadState.READ_PROCESSED_OK_TO_PROCESS_MORE : Http2MultiplexCodec.ReadState.READ_PROCESSED_BUT_STOP_READING;
         }
      }

      void fireChildReadComplete() {
         assert this.eventLoop().inEventLoop();

         try {
            if (this.readInProgress) {
               this.inFireChannelReadComplete = true;
               this.readInProgress = false;
               this.unsafe().recvBufAllocHandle().readComplete();
               this.pipeline().fireChannelReadComplete();
            }
         } finally {
            this.inFireChannelReadComplete = false;
         }

      }

      private final class Http2StreamChannelConfig extends DefaultChannelConfig {
         Http2StreamChannelConfig(Channel var2) {
            super(var2);
            this.setRecvByteBufAllocator(new Http2MultiplexCodec.Http2StreamChannelRecvByteBufAllocator());
         }

         public int getWriteBufferHighWaterMark() {
            return Math.min(DefaultHttp2StreamChannel.this.parent().config().getWriteBufferHighWaterMark(), Http2MultiplexCodec.this.initialOutboundStreamWindow);
         }

         public int getWriteBufferLowWaterMark() {
            return Math.min(DefaultHttp2StreamChannel.this.parent().config().getWriteBufferLowWaterMark(), Http2MultiplexCodec.this.initialOutboundStreamWindow);
         }

         public MessageSizeEstimator getMessageSizeEstimator() {
            return Http2MultiplexCodec.FlowControlledFrameSizeEstimator.INSTANCE;
         }

         public WriteBufferWaterMark getWriteBufferWaterMark() {
            int var1 = this.getWriteBufferHighWaterMark();
            return new WriteBufferWaterMark(var1, var1);
         }

         public ChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
            throw new UnsupportedOperationException();
         }

         /** @deprecated */
         @Deprecated
         public ChannelConfig setWriteBufferHighWaterMark(int var1) {
            throw new UnsupportedOperationException();
         }

         /** @deprecated */
         @Deprecated
         public ChannelConfig setWriteBufferLowWaterMark(int var1) {
            throw new UnsupportedOperationException();
         }

         public ChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
            throw new UnsupportedOperationException();
         }

         public ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
            if (!(var1.newHandle() instanceof RecvByteBufAllocator.ExtendedHandle)) {
               throw new IllegalArgumentException("allocator.newHandle() must return an object of type: " + RecvByteBufAllocator.ExtendedHandle.class);
            } else {
               super.setRecvByteBufAllocator(var1);
               return this;
            }
         }
      }

      private final class Http2ChannelUnsafe implements Channel.Unsafe {
         private final VoidChannelPromise unsafeVoidPromise;
         private RecvByteBufAllocator.ExtendedHandle recvHandle;
         private boolean writeDoneAndNoFlush;
         private boolean closeInitiated;

         private Http2ChannelUnsafe() {
            super();
            this.unsafeVoidPromise = new VoidChannelPromise(DefaultHttp2StreamChannel.this, false);
         }

         public void connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
            if (var3.setUncancellable()) {
               var3.setFailure(new UnsupportedOperationException());
            }
         }

         public RecvByteBufAllocator.ExtendedHandle recvBufAllocHandle() {
            if (this.recvHandle == null) {
               this.recvHandle = (RecvByteBufAllocator.ExtendedHandle)DefaultHttp2StreamChannel.this.config().getRecvByteBufAllocator().newHandle();
            }

            return this.recvHandle;
         }

         public SocketAddress localAddress() {
            return DefaultHttp2StreamChannel.this.parent().unsafe().localAddress();
         }

         public SocketAddress remoteAddress() {
            return DefaultHttp2StreamChannel.this.parent().unsafe().remoteAddress();
         }

         public void register(EventLoop var1, ChannelPromise var2) {
            if (var2.setUncancellable()) {
               if (DefaultHttp2StreamChannel.this.registered) {
                  throw new UnsupportedOperationException("Re-register is not supported");
               } else {
                  DefaultHttp2StreamChannel.this.registered = true;
                  if (!DefaultHttp2StreamChannel.this.outbound) {
                     DefaultHttp2StreamChannel.this.pipeline().addLast(Http2MultiplexCodec.this.inboundStreamHandler);
                  }

                  var2.setSuccess();
                  DefaultHttp2StreamChannel.this.pipeline().fireChannelRegistered();
                  if (DefaultHttp2StreamChannel.this.isActive()) {
                     DefaultHttp2StreamChannel.this.pipeline().fireChannelActive();
                  }

               }
            }
         }

         public void bind(SocketAddress var1, ChannelPromise var2) {
            if (var2.setUncancellable()) {
               var2.setFailure(new UnsupportedOperationException());
            }
         }

         public void disconnect(ChannelPromise var1) {
            this.close(var1);
         }

         public void close(final ChannelPromise var1) {
            if (var1.setUncancellable()) {
               if (this.closeInitiated) {
                  if (DefaultHttp2StreamChannel.this.closePromise.isDone()) {
                     var1.setSuccess();
                  } else if (!(var1 instanceof VoidChannelPromise)) {
                     DefaultHttp2StreamChannel.this.closePromise.addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture var1x) throws Exception {
                           var1.setSuccess();
                        }
                     });
                  }

               } else {
                  this.closeInitiated = true;
                  DefaultHttp2StreamChannel.this.closePending = false;
                  DefaultHttp2StreamChannel.this.fireChannelReadPending = false;
                  if (DefaultHttp2StreamChannel.this.parent().isActive() && !DefaultHttp2StreamChannel.this.streamClosedWithoutError && Http2CodecUtil.isStreamIdValid(DefaultHttp2StreamChannel.this.stream().id())) {
                     DefaultHttp2ResetFrame var2 = (new DefaultHttp2ResetFrame(Http2Error.CANCEL)).stream(DefaultHttp2StreamChannel.this.stream());
                     this.write(var2, DefaultHttp2StreamChannel.this.unsafe().voidPromise());
                     this.flush();
                  }

                  if (DefaultHttp2StreamChannel.this.inboundBuffer != null) {
                     while(true) {
                        Object var3 = DefaultHttp2StreamChannel.this.inboundBuffer.poll();
                        if (var3 == null) {
                           break;
                        }

                        ReferenceCountUtil.release(var3);
                     }
                  }

                  DefaultHttp2StreamChannel.this.outboundClosed = true;
                  DefaultHttp2StreamChannel.this.closePromise.setSuccess();
                  var1.setSuccess();
                  DefaultHttp2StreamChannel.this.pipeline().fireChannelInactive();
                  if (DefaultHttp2StreamChannel.this.isRegistered()) {
                     this.deregister(DefaultHttp2StreamChannel.this.unsafe().voidPromise());
                  }

               }
            }
         }

         public void closeForcibly() {
            this.close(DefaultHttp2StreamChannel.this.unsafe().voidPromise());
         }

         public void deregister(ChannelPromise var1) {
            if (var1.setUncancellable()) {
               if (DefaultHttp2StreamChannel.this.registered) {
                  DefaultHttp2StreamChannel.this.registered = true;
                  var1.setSuccess();
                  DefaultHttp2StreamChannel.this.pipeline().fireChannelUnregistered();
               } else {
                  var1.setFailure(new IllegalStateException("Not registered"));
               }

            }
         }

         public void beginRead() {
            if (!DefaultHttp2StreamChannel.this.readInProgress && DefaultHttp2StreamChannel.this.isActive()) {
               DefaultHttp2StreamChannel.this.readInProgress = true;
               RecvByteBufAllocator.Handle var1 = DefaultHttp2StreamChannel.this.unsafe().recvBufAllocHandle();
               var1.reset(DefaultHttp2StreamChannel.this.config());
               if (DefaultHttp2StreamChannel.this.inboundBuffer != null && !DefaultHttp2StreamChannel.this.inboundBuffer.isEmpty()) {
                  boolean var2;
                  do {
                     Object var3 = DefaultHttp2StreamChannel.this.inboundBuffer.poll();
                     if (var3 == null) {
                        var2 = false;
                        break;
                     }

                     this.doRead0((Http2Frame)var3, var1);
                  } while(var2 = var1.continueReading());

                  if (var2 && Http2MultiplexCodec.this.parentReadInProgress) {
                     Http2MultiplexCodec.this.addChildChannelToReadPendingQueue(DefaultHttp2StreamChannel.this);
                  } else {
                     DefaultHttp2StreamChannel.this.readInProgress = false;
                     var1.readComplete();
                     DefaultHttp2StreamChannel.this.pipeline().fireChannelReadComplete();
                     this.flush();
                     if (DefaultHttp2StreamChannel.this.closePending) {
                        DefaultHttp2StreamChannel.this.unsafe.closeForcibly();
                     }
                  }

               } else {
                  if (DefaultHttp2StreamChannel.this.closePending) {
                     DefaultHttp2StreamChannel.this.unsafe.closeForcibly();
                  }

               }
            }
         }

         void doRead0(Http2Frame var1, RecvByteBufAllocator.Handle var2) {
            int var3 = 0;
            if (var1 instanceof Http2DataFrame) {
               var3 = ((Http2DataFrame)var1).initialFlowControlledBytes();
               var2.lastBytesRead(var3);
            } else {
               var2.lastBytesRead(9);
            }

            var2.incMessagesRead(1);
            DefaultHttp2StreamChannel.this.pipeline().fireChannelRead(var1);
            if (var3 != 0) {
               try {
                  this.writeDoneAndNoFlush |= Http2MultiplexCodec.this.onBytesConsumed(Http2MultiplexCodec.this.ctx, DefaultHttp2StreamChannel.this.stream, var3);
               } catch (Http2Exception var5) {
                  DefaultHttp2StreamChannel.this.pipeline().fireExceptionCaught(var5);
               }
            }

         }

         public void write(Object var1, final ChannelPromise var2) {
            if (!var2.setUncancellable()) {
               ReferenceCountUtil.release(var1);
            } else if (!DefaultHttp2StreamChannel.this.isActive() || DefaultHttp2StreamChannel.this.outboundClosed && (var1 instanceof Http2HeadersFrame || var1 instanceof Http2DataFrame)) {
               ReferenceCountUtil.release(var1);
               var2.setFailure(Http2MultiplexCodec.CLOSED_CHANNEL_EXCEPTION);
            } else {
               try {
                  if (!(var1 instanceof Http2StreamFrame)) {
                     String var11 = var1.toString();
                     ReferenceCountUtil.release(var1);
                     var2.setFailure(new IllegalArgumentException("Message must be an " + StringUtil.simpleClassName(Http2StreamFrame.class) + ": " + var11));
                     return;
                  }

                  Http2StreamFrame var3 = this.validateStreamFrame((Http2StreamFrame)var1).stream(DefaultHttp2StreamChannel.this.stream());
                  if (!DefaultHttp2StreamChannel.this.firstFrameWritten && !Http2CodecUtil.isStreamIdValid(DefaultHttp2StreamChannel.this.stream().id())) {
                     if (!(var3 instanceof Http2HeadersFrame)) {
                        ReferenceCountUtil.release(var3);
                        var2.setFailure(new IllegalArgumentException("The first frame must be a headers frame. Was: " + var3.name()));
                        return;
                     }

                     DefaultHttp2StreamChannel.this.firstFrameWritten = true;
                     ChannelFuture var4 = this.write0(var3);
                     if (var4.isDone()) {
                        this.firstWriteComplete(var4, var2);
                     } else {
                        var4.addListener(new ChannelFutureListener() {
                           public void operationComplete(ChannelFuture var1) throws Exception {
                              Http2ChannelUnsafe.this.firstWriteComplete(var1, var2);
                           }
                        });
                     }

                     return;
                  }

                  ChannelFuture var10 = this.write0(var1);
                  if (var10.isDone()) {
                     this.writeComplete(var10, var2);
                  } else {
                     var10.addListener(new ChannelFutureListener() {
                        public void operationComplete(ChannelFuture var1) throws Exception {
                           Http2ChannelUnsafe.this.writeComplete(var1, var2);
                        }
                     });
                  }
               } catch (Throwable var8) {
                  var2.tryFailure(var8);
               } finally {
                  this.writeDoneAndNoFlush = true;
               }

            }
         }

         private void firstWriteComplete(ChannelFuture var1, ChannelPromise var2) {
            Throwable var3 = var1.cause();
            if (var3 == null) {
               DefaultHttp2StreamChannel.this.writabilityChanged(Http2MultiplexCodec.this.isWritable(DefaultHttp2StreamChannel.this.stream));
               var2.setSuccess();
            } else {
               var2.setFailure(this.wrapStreamClosedError(var3));
               this.closeForcibly();
            }

         }

         private void writeComplete(ChannelFuture var1, ChannelPromise var2) {
            Throwable var3 = var1.cause();
            if (var3 == null) {
               var2.setSuccess();
            } else {
               Throwable var4 = this.wrapStreamClosedError(var3);
               var2.setFailure(var4);
               if (var4 instanceof ClosedChannelException) {
                  if (DefaultHttp2StreamChannel.this.config.isAutoClose()) {
                     this.closeForcibly();
                  } else {
                     DefaultHttp2StreamChannel.this.outboundClosed = true;
                  }
               }
            }

         }

         private Throwable wrapStreamClosedError(Throwable var1) {
            return var1 instanceof Http2Exception && ((Http2Exception)var1).error() == Http2Error.STREAM_CLOSED ? (new ClosedChannelException()).initCause(var1) : var1;
         }

         private Http2StreamFrame validateStreamFrame(Http2StreamFrame var1) {
            if (var1.stream() != null && var1.stream() != DefaultHttp2StreamChannel.this.stream) {
               String var2 = var1.toString();
               ReferenceCountUtil.release(var1);
               throw new IllegalArgumentException("Stream " + var1.stream() + " must not be set on the frame: " + var2);
            } else {
               return var1;
            }
         }

         private ChannelFuture write0(Object var1) {
            ChannelPromise var2 = Http2MultiplexCodec.this.ctx.newPromise();
            Http2MultiplexCodec.this.write(Http2MultiplexCodec.this.ctx, var1, var2);
            return var2;
         }

         public void flush() {
            if (this.writeDoneAndNoFlush) {
               try {
                  if (!DefaultHttp2StreamChannel.this.inFireChannelReadComplete) {
                     Http2MultiplexCodec.this.flush0(Http2MultiplexCodec.this.ctx);
                  }
               } finally {
                  this.writeDoneAndNoFlush = false;
               }

            }
         }

         public ChannelPromise voidPromise() {
            return this.unsafeVoidPromise;
         }

         public ChannelOutboundBuffer outboundBuffer() {
            return null;
         }

         // $FF: synthetic method
         Http2ChannelUnsafe(Object var2) {
            this();
         }
      }
   }

   private static enum ReadState {
      READ_QUEUED,
      READ_IGNORED_CHANNEL_INACTIVE,
      READ_PROCESSED_BUT_STOP_READING,
      READ_PROCESSED_OK_TO_PROCESS_MORE;

      private ReadState() {
      }
   }

   static class Http2MultiplexCodecStream extends Http2FrameCodec.DefaultHttp2FrameStream {
      Http2MultiplexCodec.DefaultHttp2StreamChannel channel;

      Http2MultiplexCodecStream() {
         super();
      }
   }

   private static final class Http2StreamChannelRecvByteBufAllocator extends DefaultMaxMessagesRecvByteBufAllocator {
      private Http2StreamChannelRecvByteBufAllocator() {
         super();
      }

      public DefaultMaxMessagesRecvByteBufAllocator.MaxMessageHandle newHandle() {
         return new DefaultMaxMessagesRecvByteBufAllocator.MaxMessageHandle() {
            public int guess() {
               return 1024;
            }
         };
      }

      // $FF: synthetic method
      Http2StreamChannelRecvByteBufAllocator(Object var1) {
         this();
      }
   }

   private static final class FlowControlledFrameSizeEstimator implements MessageSizeEstimator {
      static final Http2MultiplexCodec.FlowControlledFrameSizeEstimator INSTANCE = new Http2MultiplexCodec.FlowControlledFrameSizeEstimator();
      static final MessageSizeEstimator.Handle HANDLE_INSTANCE = new MessageSizeEstimator.Handle() {
         public int size(Object var1) {
            return var1 instanceof Http2DataFrame ? (int)Math.min(2147483647L, (long)((Http2DataFrame)var1).initialFlowControlledBytes() + 9L) : 9;
         }
      };

      private FlowControlledFrameSizeEstimator() {
         super();
      }

      public MessageSizeEstimator.Handle newHandle() {
         return HANDLE_INSTANCE;
      }
   }
}
