package io.netty.handler.codec.spdy;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.ThrowableUtil;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class SpdySessionHandler extends ChannelDuplexHandler {
   private static final SpdyProtocolException PROTOCOL_EXCEPTION = (SpdyProtocolException)ThrowableUtil.unknownStackTrace(new SpdyProtocolException(), SpdySessionHandler.class, "handleOutboundMessage(...)");
   private static final SpdyProtocolException STREAM_CLOSED = (SpdyProtocolException)ThrowableUtil.unknownStackTrace(new SpdyProtocolException("Stream closed"), SpdySessionHandler.class, "removeStream(...)");
   private static final int DEFAULT_WINDOW_SIZE = 65536;
   private int initialSendWindowSize = 65536;
   private int initialReceiveWindowSize = 65536;
   private volatile int initialSessionReceiveWindowSize = 65536;
   private final SpdySession spdySession;
   private int lastGoodStreamId;
   private static final int DEFAULT_MAX_CONCURRENT_STREAMS = 2147483647;
   private int remoteConcurrentStreams;
   private int localConcurrentStreams;
   private final AtomicInteger pings;
   private boolean sentGoAwayFrame;
   private boolean receivedGoAwayFrame;
   private ChannelFutureListener closeSessionFutureListener;
   private final boolean server;
   private final int minorVersion;

   public SpdySessionHandler(SpdyVersion var1, boolean var2) {
      super();
      this.spdySession = new SpdySession(this.initialSendWindowSize, this.initialReceiveWindowSize);
      this.remoteConcurrentStreams = 2147483647;
      this.localConcurrentStreams = 2147483647;
      this.pings = new AtomicInteger();
      if (var1 == null) {
         throw new NullPointerException("version");
      } else {
         this.server = var2;
         this.minorVersion = var1.getMinorVersion();
      }
   }

   public void setSessionReceiveWindowSize(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("sessionReceiveWindowSize");
      } else {
         this.initialSessionReceiveWindowSize = var1;
      }
   }

   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      int var4;
      int var5;
      int var6;
      if (var2 instanceof SpdyDataFrame) {
         SpdyDataFrame var16 = (SpdyDataFrame)var2;
         var4 = var16.streamId();
         var5 = -1 * var16.content().readableBytes();
         var6 = this.spdySession.updateReceiveWindowSize(0, var5);
         if (var6 < 0) {
            this.issueSessionError(var1, SpdySessionStatus.PROTOCOL_ERROR);
            return;
         }

         int var19;
         if (var6 <= this.initialSessionReceiveWindowSize / 2) {
            var19 = this.initialSessionReceiveWindowSize - var6;
            this.spdySession.updateReceiveWindowSize(0, var19);
            DefaultSpdyWindowUpdateFrame var8 = new DefaultSpdyWindowUpdateFrame(0, var19);
            var1.writeAndFlush(var8);
         }

         if (!this.spdySession.isActiveStream(var4)) {
            var16.release();
            if (var4 <= this.lastGoodStreamId) {
               this.issueStreamError(var1, var4, SpdyStreamStatus.PROTOCOL_ERROR);
            } else if (!this.sentGoAwayFrame) {
               this.issueStreamError(var1, var4, SpdyStreamStatus.INVALID_STREAM);
            }

            return;
         }

         if (this.spdySession.isRemoteSideClosed(var4)) {
            var16.release();
            this.issueStreamError(var1, var4, SpdyStreamStatus.STREAM_ALREADY_CLOSED);
            return;
         }

         if (!this.isRemoteInitiatedId(var4) && !this.spdySession.hasReceivedReply(var4)) {
            var16.release();
            this.issueStreamError(var1, var4, SpdyStreamStatus.PROTOCOL_ERROR);
            return;
         }

         var19 = this.spdySession.updateReceiveWindowSize(var4, var5);
         if (var19 < this.spdySession.getReceiveWindowSizeLowerBound(var4)) {
            var16.release();
            this.issueStreamError(var1, var4, SpdyStreamStatus.FLOW_CONTROL_ERROR);
            return;
         }

         if (var19 < 0) {
            while(var16.content().readableBytes() > this.initialReceiveWindowSize) {
               DefaultSpdyDataFrame var20 = new DefaultSpdyDataFrame(var4, var16.content().readRetainedSlice(this.initialReceiveWindowSize));
               var1.writeAndFlush(var20);
            }
         }

         if (var19 <= this.initialReceiveWindowSize / 2 && !var16.isLast()) {
            int var21 = this.initialReceiveWindowSize - var19;
            this.spdySession.updateReceiveWindowSize(var4, var21);
            DefaultSpdyWindowUpdateFrame var9 = new DefaultSpdyWindowUpdateFrame(var4, var21);
            var1.writeAndFlush(var9);
         }

         if (var16.isLast()) {
            this.halfCloseStream(var4, true, var1.newSucceededFuture());
         }
      } else if (var2 instanceof SpdySynStreamFrame) {
         SpdySynStreamFrame var15 = (SpdySynStreamFrame)var2;
         var4 = var15.streamId();
         if (var15.isInvalid() || !this.isRemoteInitiatedId(var4) || this.spdySession.isActiveStream(var4)) {
            this.issueStreamError(var1, var4, SpdyStreamStatus.PROTOCOL_ERROR);
            return;
         }

         if (var4 <= this.lastGoodStreamId) {
            this.issueSessionError(var1, SpdySessionStatus.PROTOCOL_ERROR);
            return;
         }

         byte var17 = var15.priority();
         boolean var18 = var15.isLast();
         boolean var7 = var15.isUnidirectional();
         if (!this.acceptStream(var4, var17, var18, var7)) {
            this.issueStreamError(var1, var4, SpdyStreamStatus.REFUSED_STREAM);
            return;
         }
      } else if (var2 instanceof SpdySynReplyFrame) {
         SpdySynReplyFrame var14 = (SpdySynReplyFrame)var2;
         var4 = var14.streamId();
         if (var14.isInvalid() || this.isRemoteInitiatedId(var4) || this.spdySession.isRemoteSideClosed(var4)) {
            this.issueStreamError(var1, var4, SpdyStreamStatus.INVALID_STREAM);
            return;
         }

         if (this.spdySession.hasReceivedReply(var4)) {
            this.issueStreamError(var1, var4, SpdyStreamStatus.STREAM_IN_USE);
            return;
         }

         this.spdySession.receivedReply(var4);
         if (var14.isLast()) {
            this.halfCloseStream(var4, true, var1.newSucceededFuture());
         }
      } else if (var2 instanceof SpdyRstStreamFrame) {
         SpdyRstStreamFrame var3 = (SpdyRstStreamFrame)var2;
         this.removeStream(var3.streamId(), var1.newSucceededFuture());
      } else if (var2 instanceof SpdySettingsFrame) {
         SpdySettingsFrame var10 = (SpdySettingsFrame)var2;
         var4 = var10.getValue(0);
         if (var4 >= 0 && var4 != this.minorVersion) {
            this.issueSessionError(var1, SpdySessionStatus.PROTOCOL_ERROR);
            return;
         }

         var5 = var10.getValue(4);
         if (var5 >= 0) {
            this.remoteConcurrentStreams = var5;
         }

         if (var10.isPersisted(7)) {
            var10.removeValue(7);
         }

         var10.setPersistValue(7, false);
         var6 = var10.getValue(7);
         if (var6 >= 0) {
            this.updateInitialSendWindowSize(var6);
         }
      } else if (var2 instanceof SpdyPingFrame) {
         SpdyPingFrame var11 = (SpdyPingFrame)var2;
         if (this.isRemoteInitiatedId(var11.id())) {
            var1.writeAndFlush(var11);
            return;
         }

         if (this.pings.get() == 0) {
            return;
         }

         this.pings.getAndDecrement();
      } else if (var2 instanceof SpdyGoAwayFrame) {
         this.receivedGoAwayFrame = true;
      } else if (var2 instanceof SpdyHeadersFrame) {
         SpdyHeadersFrame var12 = (SpdyHeadersFrame)var2;
         var4 = var12.streamId();
         if (var12.isInvalid()) {
            this.issueStreamError(var1, var4, SpdyStreamStatus.PROTOCOL_ERROR);
            return;
         }

         if (this.spdySession.isRemoteSideClosed(var4)) {
            this.issueStreamError(var1, var4, SpdyStreamStatus.INVALID_STREAM);
            return;
         }

         if (var12.isLast()) {
            this.halfCloseStream(var4, true, var1.newSucceededFuture());
         }
      } else if (var2 instanceof SpdyWindowUpdateFrame) {
         SpdyWindowUpdateFrame var13 = (SpdyWindowUpdateFrame)var2;
         var4 = var13.streamId();
         var5 = var13.deltaWindowSize();
         if (var4 != 0 && this.spdySession.isLocalSideClosed(var4)) {
            return;
         }

         if (this.spdySession.getSendWindowSize(var4) > 2147483647 - var5) {
            if (var4 == 0) {
               this.issueSessionError(var1, SpdySessionStatus.PROTOCOL_ERROR);
            } else {
               this.issueStreamError(var1, var4, SpdyStreamStatus.FLOW_CONTROL_ERROR);
            }

            return;
         }

         this.updateSendWindowSize(var1, var4, var5);
      }

      var1.fireChannelRead(var2);
   }

   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      Iterator var2 = this.spdySession.activeStreams().keySet().iterator();

      while(var2.hasNext()) {
         Integer var3 = (Integer)var2.next();
         this.removeStream(var3, var1.newSucceededFuture());
      }

      var1.fireChannelInactive();
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      if (var2 instanceof SpdyProtocolException) {
         this.issueSessionError(var1, SpdySessionStatus.PROTOCOL_ERROR);
      }

      var1.fireExceptionCaught(var2);
   }

   public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      this.sendGoAwayFrame(var1, var2);
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (!(var2 instanceof SpdyDataFrame) && !(var2 instanceof SpdySynStreamFrame) && !(var2 instanceof SpdySynReplyFrame) && !(var2 instanceof SpdyRstStreamFrame) && !(var2 instanceof SpdySettingsFrame) && !(var2 instanceof SpdyPingFrame) && !(var2 instanceof SpdyGoAwayFrame) && !(var2 instanceof SpdyHeadersFrame) && !(var2 instanceof SpdyWindowUpdateFrame)) {
         var1.write(var2, var3);
      } else {
         this.handleOutboundMessage(var1, var2, var3);
      }

   }

   private void handleOutboundMessage(final ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      int var5;
      int var6;
      int var7;
      if (var2 instanceof SpdyDataFrame) {
         SpdyDataFrame var16 = (SpdyDataFrame)var2;
         var5 = var16.streamId();
         if (this.spdySession.isLocalSideClosed(var5)) {
            var16.release();
            var3.setFailure(PROTOCOL_EXCEPTION);
            return;
         }

         var6 = var16.content().readableBytes();
         var7 = this.spdySession.getSendWindowSize(var5);
         int var19 = this.spdySession.getSendWindowSize(0);
         var7 = Math.min(var7, var19);
         if (var7 <= 0) {
            this.spdySession.putPendingWrite(var5, new SpdySession.PendingWrite(var16, var3));
            return;
         }

         if (var7 < var6) {
            this.spdySession.updateSendWindowSize(var5, -1 * var7);
            this.spdySession.updateSendWindowSize(0, -1 * var7);
            DefaultSpdyDataFrame var9 = new DefaultSpdyDataFrame(var5, var16.content().readRetainedSlice(var7));
            this.spdySession.putPendingWrite(var5, new SpdySession.PendingWrite(var16, var3));
            var1.write(var9).addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1x) throws Exception {
                  if (!var1x.isSuccess()) {
                     SpdySessionHandler.this.issueSessionError(var1, SpdySessionStatus.INTERNAL_ERROR);
                  }

               }
            });
            return;
         }

         this.spdySession.updateSendWindowSize(var5, -1 * var6);
         this.spdySession.updateSendWindowSize(0, -1 * var6);
         var3.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) throws Exception {
               if (!var1x.isSuccess()) {
                  SpdySessionHandler.this.issueSessionError(var1, SpdySessionStatus.INTERNAL_ERROR);
               }

            }
         });
         if (var16.isLast()) {
            this.halfCloseStream(var5, false, var3);
         }
      } else if (var2 instanceof SpdySynStreamFrame) {
         SpdySynStreamFrame var15 = (SpdySynStreamFrame)var2;
         var5 = var15.streamId();
         if (this.isRemoteInitiatedId(var5)) {
            var3.setFailure(PROTOCOL_EXCEPTION);
            return;
         }

         byte var17 = var15.priority();
         boolean var18 = var15.isUnidirectional();
         boolean var8 = var15.isLast();
         if (!this.acceptStream(var5, var17, var18, var8)) {
            var3.setFailure(PROTOCOL_EXCEPTION);
            return;
         }
      } else if (var2 instanceof SpdySynReplyFrame) {
         SpdySynReplyFrame var14 = (SpdySynReplyFrame)var2;
         var5 = var14.streamId();
         if (!this.isRemoteInitiatedId(var5) || this.spdySession.isLocalSideClosed(var5)) {
            var3.setFailure(PROTOCOL_EXCEPTION);
            return;
         }

         if (var14.isLast()) {
            this.halfCloseStream(var5, false, var3);
         }
      } else if (var2 instanceof SpdyRstStreamFrame) {
         SpdyRstStreamFrame var4 = (SpdyRstStreamFrame)var2;
         this.removeStream(var4.streamId(), var3);
      } else if (var2 instanceof SpdySettingsFrame) {
         SpdySettingsFrame var11 = (SpdySettingsFrame)var2;
         var5 = var11.getValue(0);
         if (var5 >= 0 && var5 != this.minorVersion) {
            var3.setFailure(PROTOCOL_EXCEPTION);
            return;
         }

         var6 = var11.getValue(4);
         if (var6 >= 0) {
            this.localConcurrentStreams = var6;
         }

         if (var11.isPersisted(7)) {
            var11.removeValue(7);
         }

         var11.setPersistValue(7, false);
         var7 = var11.getValue(7);
         if (var7 >= 0) {
            this.updateInitialReceiveWindowSize(var7);
         }
      } else if (var2 instanceof SpdyPingFrame) {
         SpdyPingFrame var12 = (SpdyPingFrame)var2;
         if (this.isRemoteInitiatedId(var12.id())) {
            var1.fireExceptionCaught(new IllegalArgumentException("invalid PING ID: " + var12.id()));
            return;
         }

         this.pings.getAndIncrement();
      } else {
         if (var2 instanceof SpdyGoAwayFrame) {
            var3.setFailure(PROTOCOL_EXCEPTION);
            return;
         }

         if (var2 instanceof SpdyHeadersFrame) {
            SpdyHeadersFrame var13 = (SpdyHeadersFrame)var2;
            var5 = var13.streamId();
            if (this.spdySession.isLocalSideClosed(var5)) {
               var3.setFailure(PROTOCOL_EXCEPTION);
               return;
            }

            if (var13.isLast()) {
               this.halfCloseStream(var5, false, var3);
            }
         } else if (var2 instanceof SpdyWindowUpdateFrame) {
            var3.setFailure(PROTOCOL_EXCEPTION);
            return;
         }
      }

      var1.write(var2, var3);
   }

   private void issueSessionError(ChannelHandlerContext var1, SpdySessionStatus var2) {
      this.sendGoAwayFrame(var1, var2).addListener(new SpdySessionHandler.ClosingChannelFutureListener(var1, var1.newPromise()));
   }

   private void issueStreamError(ChannelHandlerContext var1, int var2, SpdyStreamStatus var3) {
      boolean var4 = !this.spdySession.isRemoteSideClosed(var2);
      ChannelPromise var5 = var1.newPromise();
      this.removeStream(var2, var5);
      DefaultSpdyRstStreamFrame var6 = new DefaultSpdyRstStreamFrame(var2, var3);
      var1.writeAndFlush(var6, var5);
      if (var4) {
         var1.fireChannelRead(var6);
      }

   }

   private boolean isRemoteInitiatedId(int var1) {
      boolean var2 = SpdyCodecUtil.isServerId(var1);
      return this.server && !var2 || !this.server && var2;
   }

   private void updateInitialSendWindowSize(int var1) {
      int var2 = var1 - this.initialSendWindowSize;
      this.initialSendWindowSize = var1;
      this.spdySession.updateAllSendWindowSizes(var2);
   }

   private void updateInitialReceiveWindowSize(int var1) {
      int var2 = var1 - this.initialReceiveWindowSize;
      this.initialReceiveWindowSize = var1;
      this.spdySession.updateAllReceiveWindowSizes(var2);
   }

   private boolean acceptStream(int var1, byte var2, boolean var3, boolean var4) {
      if (!this.receivedGoAwayFrame && !this.sentGoAwayFrame) {
         boolean var5 = this.isRemoteInitiatedId(var1);
         int var6 = var5 ? this.localConcurrentStreams : this.remoteConcurrentStreams;
         if (this.spdySession.numActiveStreams(var5) >= var6) {
            return false;
         } else {
            this.spdySession.acceptStream(var1, var2, var3, var4, this.initialSendWindowSize, this.initialReceiveWindowSize, var5);
            if (var5) {
               this.lastGoodStreamId = var1;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private void halfCloseStream(int var1, boolean var2, ChannelFuture var3) {
      if (var2) {
         this.spdySession.closeRemoteSide(var1, this.isRemoteInitiatedId(var1));
      } else {
         this.spdySession.closeLocalSide(var1, this.isRemoteInitiatedId(var1));
      }

      if (this.closeSessionFutureListener != null && this.spdySession.noActiveStreams()) {
         var3.addListener(this.closeSessionFutureListener);
      }

   }

   private void removeStream(int var1, ChannelFuture var2) {
      this.spdySession.removeStream(var1, STREAM_CLOSED, this.isRemoteInitiatedId(var1));
      if (this.closeSessionFutureListener != null && this.spdySession.noActiveStreams()) {
         var2.addListener(this.closeSessionFutureListener);
      }

   }

   private void updateSendWindowSize(final ChannelHandlerContext var1, int var2, int var3) {
      this.spdySession.updateSendWindowSize(var2, var3);

      while(true) {
         SpdySession.PendingWrite var4 = this.spdySession.getPendingWrite(var2);
         if (var4 == null) {
            return;
         }

         SpdyDataFrame var5 = var4.spdyDataFrame;
         int var6 = var5.content().readableBytes();
         int var7 = var5.streamId();
         int var8 = this.spdySession.getSendWindowSize(var7);
         int var9 = this.spdySession.getSendWindowSize(0);
         var8 = Math.min(var8, var9);
         if (var8 <= 0) {
            return;
         }

         if (var8 < var6) {
            this.spdySession.updateSendWindowSize(var7, -1 * var8);
            this.spdySession.updateSendWindowSize(0, -1 * var8);
            DefaultSpdyDataFrame var10 = new DefaultSpdyDataFrame(var7, var5.content().readRetainedSlice(var8));
            var1.writeAndFlush(var10).addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1x) throws Exception {
                  if (!var1x.isSuccess()) {
                     SpdySessionHandler.this.issueSessionError(var1, SpdySessionStatus.INTERNAL_ERROR);
                  }

               }
            });
         } else {
            this.spdySession.removePendingWrite(var7);
            this.spdySession.updateSendWindowSize(var7, -1 * var6);
            this.spdySession.updateSendWindowSize(0, -1 * var6);
            if (var5.isLast()) {
               this.halfCloseStream(var7, false, var4.promise);
            }

            var1.writeAndFlush(var5, var4.promise).addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1x) throws Exception {
                  if (!var1x.isSuccess()) {
                     SpdySessionHandler.this.issueSessionError(var1, SpdySessionStatus.INTERNAL_ERROR);
                  }

               }
            });
         }
      }
   }

   private void sendGoAwayFrame(ChannelHandlerContext var1, ChannelPromise var2) {
      if (!var1.channel().isActive()) {
         var1.close(var2);
      } else {
         ChannelFuture var3 = this.sendGoAwayFrame(var1, SpdySessionStatus.OK);
         if (this.spdySession.noActiveStreams()) {
            var3.addListener(new SpdySessionHandler.ClosingChannelFutureListener(var1, var2));
         } else {
            this.closeSessionFutureListener = new SpdySessionHandler.ClosingChannelFutureListener(var1, var2);
         }

      }
   }

   private ChannelFuture sendGoAwayFrame(ChannelHandlerContext var1, SpdySessionStatus var2) {
      if (!this.sentGoAwayFrame) {
         this.sentGoAwayFrame = true;
         DefaultSpdyGoAwayFrame var3 = new DefaultSpdyGoAwayFrame(this.lastGoodStreamId, var2);
         return var1.writeAndFlush(var3);
      } else {
         return var1.newSucceededFuture();
      }
   }

   private static final class ClosingChannelFutureListener implements ChannelFutureListener {
      private final ChannelHandlerContext ctx;
      private final ChannelPromise promise;

      ClosingChannelFutureListener(ChannelHandlerContext var1, ChannelPromise var2) {
         super();
         this.ctx = var1;
         this.promise = var2;
      }

      public void operationComplete(ChannelFuture var1) throws Exception {
         this.ctx.close(this.promise);
      }
   }
}
