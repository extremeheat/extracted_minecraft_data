package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.TreeMap;
import java.util.Map.Entry;

public class StreamBufferingEncoder extends DecoratingHttp2ConnectionEncoder {
   private final TreeMap<Integer, StreamBufferingEncoder.PendingStream> pendingStreams;
   private int maxConcurrentStreams;
   private boolean closed;

   public StreamBufferingEncoder(Http2ConnectionEncoder var1) {
      this(var1, 100);
   }

   public StreamBufferingEncoder(Http2ConnectionEncoder var1, int var2) {
      super(var1);
      this.pendingStreams = new TreeMap();
      this.maxConcurrentStreams = var2;
      this.connection().addListener(new Http2ConnectionAdapter() {
         public void onGoAwayReceived(int var1, long var2, ByteBuf var4) {
            StreamBufferingEncoder.this.cancelGoAwayStreams(var1, var2, var4);
         }

         public void onStreamClosed(Http2Stream var1) {
            StreamBufferingEncoder.this.tryCreatePendingStreams();
         }
      });
   }

   public int numBufferedStreams() {
      return this.pendingStreams.size();
   }

   public ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5, ChannelPromise var6) {
      return this.writeHeaders(var1, var2, var3, 0, (short)16, false, var4, var5, var6);
   }

   public ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8, ChannelPromise var9) {
      if (this.closed) {
         return var9.setFailure(new StreamBufferingEncoder.Http2ChannelClosedException());
      } else if (!this.isExistingStream(var2) && !this.connection().goAwayReceived()) {
         if (this.canCreateStream()) {
            return super.writeHeaders(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         } else {
            StreamBufferingEncoder.PendingStream var10 = (StreamBufferingEncoder.PendingStream)this.pendingStreams.get(var2);
            if (var10 == null) {
               var10 = new StreamBufferingEncoder.PendingStream(var1, var2);
               this.pendingStreams.put(var2, var10);
            }

            var10.frames.add(new StreamBufferingEncoder.HeadersFrame(var3, var4, var5, var6, var7, var8, var9));
            return var9;
         }
      } else {
         return super.writeHeaders(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   public ChannelFuture writeRstStream(ChannelHandlerContext var1, int var2, long var3, ChannelPromise var5) {
      if (this.isExistingStream(var2)) {
         return super.writeRstStream(var1, var2, var3, var5);
      } else {
         StreamBufferingEncoder.PendingStream var6 = (StreamBufferingEncoder.PendingStream)this.pendingStreams.remove(var2);
         if (var6 != null) {
            var6.close((Throwable)null);
            var5.setSuccess();
         } else {
            var5.setFailure(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Stream does not exist %d", var2));
         }

         return var5;
      }
   }

   public ChannelFuture writeData(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, boolean var5, ChannelPromise var6) {
      if (this.isExistingStream(var2)) {
         return super.writeData(var1, var2, var3, var4, var5, var6);
      } else {
         StreamBufferingEncoder.PendingStream var7 = (StreamBufferingEncoder.PendingStream)this.pendingStreams.get(var2);
         if (var7 != null) {
            var7.frames.add(new StreamBufferingEncoder.DataFrame(var3, var4, var5, var6));
         } else {
            ReferenceCountUtil.safeRelease(var3);
            var6.setFailure(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Stream does not exist %d", var2));
         }

         return var6;
      }
   }

   public void remoteSettings(Http2Settings var1) throws Http2Exception {
      super.remoteSettings(var1);
      this.maxConcurrentStreams = this.connection().local().maxActiveStreams();
      this.tryCreatePendingStreams();
   }

   public void close() {
      try {
         if (!this.closed) {
            this.closed = true;
            StreamBufferingEncoder.Http2ChannelClosedException var1 = new StreamBufferingEncoder.Http2ChannelClosedException();

            while(!this.pendingStreams.isEmpty()) {
               StreamBufferingEncoder.PendingStream var2 = (StreamBufferingEncoder.PendingStream)this.pendingStreams.pollFirstEntry().getValue();
               var2.close(var1);
            }
         }
      } finally {
         super.close();
      }

   }

   private void tryCreatePendingStreams() {
      while(!this.pendingStreams.isEmpty() && this.canCreateStream()) {
         Entry var1 = this.pendingStreams.pollFirstEntry();
         StreamBufferingEncoder.PendingStream var2 = (StreamBufferingEncoder.PendingStream)var1.getValue();

         try {
            var2.sendFrames();
         } catch (Throwable var4) {
            var2.close(var4);
         }
      }

   }

   private void cancelGoAwayStreams(int var1, long var2, ByteBuf var4) {
      Iterator var5 = this.pendingStreams.values().iterator();
      StreamBufferingEncoder.Http2GoAwayException var6 = new StreamBufferingEncoder.Http2GoAwayException(var1, var2, ByteBufUtil.getBytes(var4));

      while(var5.hasNext()) {
         StreamBufferingEncoder.PendingStream var7 = (StreamBufferingEncoder.PendingStream)var5.next();
         if (var7.streamId > var1) {
            var5.remove();
            var7.close(var6);
         }
      }

   }

   private boolean canCreateStream() {
      return this.connection().local().numActiveStreams() < this.maxConcurrentStreams;
   }

   private boolean isExistingStream(int var1) {
      return var1 <= this.connection().local().lastStreamCreated();
   }

   private final class DataFrame extends StreamBufferingEncoder.Frame {
      final ByteBuf data;
      final int padding;
      final boolean endOfStream;

      DataFrame(ByteBuf var2, int var3, boolean var4, ChannelPromise var5) {
         super(var5);
         this.data = var2;
         this.padding = var3;
         this.endOfStream = var4;
      }

      void release(Throwable var1) {
         super.release(var1);
         ReferenceCountUtil.safeRelease(this.data);
      }

      void send(ChannelHandlerContext var1, int var2) {
         StreamBufferingEncoder.this.writeData(var1, var2, this.data, this.padding, this.endOfStream, this.promise);
      }
   }

   private final class HeadersFrame extends StreamBufferingEncoder.Frame {
      final Http2Headers headers;
      final int streamDependency;
      final short weight;
      final boolean exclusive;
      final int padding;
      final boolean endOfStream;

      HeadersFrame(Http2Headers var2, int var3, short var4, boolean var5, int var6, boolean var7, ChannelPromise var8) {
         super(var8);
         this.headers = var2;
         this.streamDependency = var3;
         this.weight = var4;
         this.exclusive = var5;
         this.padding = var6;
         this.endOfStream = var7;
      }

      void send(ChannelHandlerContext var1, int var2) {
         StreamBufferingEncoder.this.writeHeaders(var1, var2, this.headers, this.streamDependency, this.weight, this.exclusive, this.padding, this.endOfStream, this.promise);
      }
   }

   private abstract static class Frame {
      final ChannelPromise promise;

      Frame(ChannelPromise var1) {
         super();
         this.promise = var1;
      }

      void release(Throwable var1) {
         if (var1 == null) {
            this.promise.setSuccess();
         } else {
            this.promise.setFailure(var1);
         }

      }

      abstract void send(ChannelHandlerContext var1, int var2);
   }

   private static final class PendingStream {
      final ChannelHandlerContext ctx;
      final int streamId;
      final Queue<StreamBufferingEncoder.Frame> frames = new ArrayDeque(2);

      PendingStream(ChannelHandlerContext var1, int var2) {
         super();
         this.ctx = var1;
         this.streamId = var2;
      }

      void sendFrames() {
         Iterator var1 = this.frames.iterator();

         while(var1.hasNext()) {
            StreamBufferingEncoder.Frame var2 = (StreamBufferingEncoder.Frame)var1.next();
            var2.send(this.ctx, this.streamId);
         }

      }

      void close(Throwable var1) {
         Iterator var2 = this.frames.iterator();

         while(var2.hasNext()) {
            StreamBufferingEncoder.Frame var3 = (StreamBufferingEncoder.Frame)var2.next();
            var3.release(var1);
         }

      }
   }

   public static final class Http2GoAwayException extends Http2Exception {
      private static final long serialVersionUID = 1326785622777291198L;
      private final int lastStreamId;
      private final long errorCode;
      private final byte[] debugData;

      public Http2GoAwayException(int var1, long var2, byte[] var4) {
         super(Http2Error.STREAM_CLOSED);
         this.lastStreamId = var1;
         this.errorCode = var2;
         this.debugData = var4;
      }

      public int lastStreamId() {
         return this.lastStreamId;
      }

      public long errorCode() {
         return this.errorCode;
      }

      public byte[] debugData() {
         return this.debugData;
      }
   }

   public static final class Http2ChannelClosedException extends Http2Exception {
      private static final long serialVersionUID = 4768543442094476971L;

      public Http2ChannelClosedException() {
         super(Http2Error.REFUSED_STREAM, "Connection closed");
      }
   }
}
