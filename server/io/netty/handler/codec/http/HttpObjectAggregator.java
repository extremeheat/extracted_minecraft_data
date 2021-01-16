package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageAggregator;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class HttpObjectAggregator extends MessageAggregator<HttpObject, HttpMessage, HttpContent, FullHttpMessage> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(HttpObjectAggregator.class);
   private static final FullHttpResponse CONTINUE;
   private static final FullHttpResponse EXPECTATION_FAILED;
   private static final FullHttpResponse TOO_LARGE_CLOSE;
   private static final FullHttpResponse TOO_LARGE;
   private final boolean closeOnExpectationFailed;

   public HttpObjectAggregator(int var1) {
      this(var1, false);
   }

   public HttpObjectAggregator(int var1, boolean var2) {
      super(var1);
      this.closeOnExpectationFailed = var2;
   }

   protected boolean isStartMessage(HttpObject var1) throws Exception {
      return var1 instanceof HttpMessage;
   }

   protected boolean isContentMessage(HttpObject var1) throws Exception {
      return var1 instanceof HttpContent;
   }

   protected boolean isLastContentMessage(HttpContent var1) throws Exception {
      return var1 instanceof LastHttpContent;
   }

   protected boolean isAggregated(HttpObject var1) throws Exception {
      return var1 instanceof FullHttpMessage;
   }

   protected boolean isContentLengthInvalid(HttpMessage var1, int var2) {
      try {
         return HttpUtil.getContentLength(var1, -1L) > (long)var2;
      } catch (NumberFormatException var4) {
         return false;
      }
   }

   private static Object continueResponse(HttpMessage var0, int var1, ChannelPipeline var2) {
      if (HttpUtil.isUnsupportedExpectation(var0)) {
         var2.fireUserEventTriggered(HttpExpectationFailedEvent.INSTANCE);
         return EXPECTATION_FAILED.retainedDuplicate();
      } else if (HttpUtil.is100ContinueExpected(var0)) {
         if (HttpUtil.getContentLength(var0, -1L) <= (long)var1) {
            return CONTINUE.retainedDuplicate();
         } else {
            var2.fireUserEventTriggered(HttpExpectationFailedEvent.INSTANCE);
            return TOO_LARGE.retainedDuplicate();
         }
      } else {
         return null;
      }
   }

   protected Object newContinueResponse(HttpMessage var1, int var2, ChannelPipeline var3) {
      Object var4 = continueResponse(var1, var2, var3);
      if (var4 != null) {
         var1.headers().remove((CharSequence)HttpHeaderNames.EXPECT);
      }

      return var4;
   }

   protected boolean closeAfterContinueResponse(Object var1) {
      return this.closeOnExpectationFailed && this.ignoreContentAfterContinueResponse(var1);
   }

   protected boolean ignoreContentAfterContinueResponse(Object var1) {
      if (var1 instanceof HttpResponse) {
         HttpResponse var2 = (HttpResponse)var1;
         return var2.status().codeClass().equals(HttpStatusClass.CLIENT_ERROR);
      } else {
         return false;
      }
   }

   protected FullHttpMessage beginAggregation(HttpMessage var1, ByteBuf var2) throws Exception {
      assert !(var1 instanceof FullHttpMessage);

      HttpUtil.setTransferEncodingChunked(var1, false);
      Object var3;
      if (var1 instanceof HttpRequest) {
         var3 = new HttpObjectAggregator.AggregatedFullHttpRequest((HttpRequest)var1, var2, (HttpHeaders)null);
      } else {
         if (!(var1 instanceof HttpResponse)) {
            throw new Error();
         }

         var3 = new HttpObjectAggregator.AggregatedFullHttpResponse((HttpResponse)var1, var2, (HttpHeaders)null);
      }

      return (FullHttpMessage)var3;
   }

   protected void aggregate(FullHttpMessage var1, HttpContent var2) throws Exception {
      if (var2 instanceof LastHttpContent) {
         ((HttpObjectAggregator.AggregatedFullHttpMessage)var1).setTrailingHeaders(((LastHttpContent)var2).trailingHeaders());
      }

   }

   protected void finishAggregation(FullHttpMessage var1) throws Exception {
      if (!HttpUtil.isContentLengthSet(var1)) {
         var1.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)String.valueOf(var1.content().readableBytes()));
      }

   }

   protected void handleOversizedMessage(final ChannelHandlerContext var1, HttpMessage var2) throws Exception {
      if (!(var2 instanceof HttpRequest)) {
         if (var2 instanceof HttpResponse) {
            var1.close();
            throw new TooLongFrameException("Response entity too large: " + var2);
         } else {
            throw new IllegalStateException();
         }
      } else {
         if (!(var2 instanceof FullHttpMessage) && (HttpUtil.is100ContinueExpected(var2) || HttpUtil.isKeepAlive(var2))) {
            var1.writeAndFlush(TOO_LARGE.retainedDuplicate()).addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1x) throws Exception {
                  if (!var1x.isSuccess()) {
                     HttpObjectAggregator.logger.debug("Failed to send a 413 Request Entity Too Large.", var1x.cause());
                     var1.close();
                  }

               }
            });
         } else {
            ChannelFuture var3 = var1.writeAndFlush(TOO_LARGE_CLOSE.retainedDuplicate());
            var3.addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1x) throws Exception {
                  if (!var1x.isSuccess()) {
                     HttpObjectAggregator.logger.debug("Failed to send a 413 Request Entity Too Large.", var1x.cause());
                  }

                  var1.close();
               }
            });
         }

         HttpObjectDecoder var4 = (HttpObjectDecoder)var1.pipeline().get(HttpObjectDecoder.class);
         if (var4 != null) {
            var4.reset();
         }

      }
   }

   static {
      CONTINUE = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE, Unpooled.EMPTY_BUFFER);
      EXPECTATION_FAILED = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.EXPECTATION_FAILED, Unpooled.EMPTY_BUFFER);
      TOO_LARGE_CLOSE = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER);
      TOO_LARGE = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER);
      EXPECTATION_FAILED.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (int)0);
      TOO_LARGE.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (int)0);
      TOO_LARGE_CLOSE.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (int)0);
      TOO_LARGE_CLOSE.headers().set((CharSequence)HttpHeaderNames.CONNECTION, (Object)HttpHeaderValues.CLOSE);
   }

   private static final class AggregatedFullHttpResponse extends HttpObjectAggregator.AggregatedFullHttpMessage implements FullHttpResponse {
      AggregatedFullHttpResponse(HttpResponse var1, ByteBuf var2, HttpHeaders var3) {
         super(var1, var2, var3);
      }

      public FullHttpResponse copy() {
         return this.replace(this.content().copy());
      }

      public FullHttpResponse duplicate() {
         return this.replace(this.content().duplicate());
      }

      public FullHttpResponse retainedDuplicate() {
         return this.replace(this.content().retainedDuplicate());
      }

      public FullHttpResponse replace(ByteBuf var1) {
         DefaultFullHttpResponse var2 = new DefaultFullHttpResponse(this.getProtocolVersion(), this.getStatus(), var1, this.headers().copy(), this.trailingHeaders().copy());
         var2.setDecoderResult(this.decoderResult());
         return var2;
      }

      public FullHttpResponse setStatus(HttpResponseStatus var1) {
         ((HttpResponse)this.message).setStatus(var1);
         return this;
      }

      public HttpResponseStatus getStatus() {
         return ((HttpResponse)this.message).status();
      }

      public HttpResponseStatus status() {
         return this.getStatus();
      }

      public FullHttpResponse setProtocolVersion(HttpVersion var1) {
         super.setProtocolVersion(var1);
         return this;
      }

      public FullHttpResponse retain(int var1) {
         super.retain(var1);
         return this;
      }

      public FullHttpResponse retain() {
         super.retain();
         return this;
      }

      public FullHttpResponse touch(Object var1) {
         super.touch(var1);
         return this;
      }

      public FullHttpResponse touch() {
         super.touch();
         return this;
      }

      public String toString() {
         return HttpMessageUtil.appendFullResponse(new StringBuilder(256), this).toString();
      }
   }

   private static final class AggregatedFullHttpRequest extends HttpObjectAggregator.AggregatedFullHttpMessage implements FullHttpRequest {
      AggregatedFullHttpRequest(HttpRequest var1, ByteBuf var2, HttpHeaders var3) {
         super(var1, var2, var3);
      }

      public FullHttpRequest copy() {
         return this.replace(this.content().copy());
      }

      public FullHttpRequest duplicate() {
         return this.replace(this.content().duplicate());
      }

      public FullHttpRequest retainedDuplicate() {
         return this.replace(this.content().retainedDuplicate());
      }

      public FullHttpRequest replace(ByteBuf var1) {
         DefaultFullHttpRequest var2 = new DefaultFullHttpRequest(this.protocolVersion(), this.method(), this.uri(), var1, this.headers().copy(), this.trailingHeaders().copy());
         var2.setDecoderResult(this.decoderResult());
         return var2;
      }

      public FullHttpRequest retain(int var1) {
         super.retain(var1);
         return this;
      }

      public FullHttpRequest retain() {
         super.retain();
         return this;
      }

      public FullHttpRequest touch() {
         super.touch();
         return this;
      }

      public FullHttpRequest touch(Object var1) {
         super.touch(var1);
         return this;
      }

      public FullHttpRequest setMethod(HttpMethod var1) {
         ((HttpRequest)this.message).setMethod(var1);
         return this;
      }

      public FullHttpRequest setUri(String var1) {
         ((HttpRequest)this.message).setUri(var1);
         return this;
      }

      public HttpMethod getMethod() {
         return ((HttpRequest)this.message).method();
      }

      public String getUri() {
         return ((HttpRequest)this.message).uri();
      }

      public HttpMethod method() {
         return this.getMethod();
      }

      public String uri() {
         return this.getUri();
      }

      public FullHttpRequest setProtocolVersion(HttpVersion var1) {
         super.setProtocolVersion(var1);
         return this;
      }

      public String toString() {
         return HttpMessageUtil.appendFullRequest(new StringBuilder(256), this).toString();
      }
   }

   private abstract static class AggregatedFullHttpMessage implements FullHttpMessage {
      protected final HttpMessage message;
      private final ByteBuf content;
      private HttpHeaders trailingHeaders;

      AggregatedFullHttpMessage(HttpMessage var1, ByteBuf var2, HttpHeaders var3) {
         super();
         this.message = var1;
         this.content = var2;
         this.trailingHeaders = var3;
      }

      public HttpHeaders trailingHeaders() {
         HttpHeaders var1 = this.trailingHeaders;
         return (HttpHeaders)(var1 == null ? EmptyHttpHeaders.INSTANCE : var1);
      }

      void setTrailingHeaders(HttpHeaders var1) {
         this.trailingHeaders = var1;
      }

      public HttpVersion getProtocolVersion() {
         return this.message.protocolVersion();
      }

      public HttpVersion protocolVersion() {
         return this.message.protocolVersion();
      }

      public FullHttpMessage setProtocolVersion(HttpVersion var1) {
         this.message.setProtocolVersion(var1);
         return this;
      }

      public HttpHeaders headers() {
         return this.message.headers();
      }

      public DecoderResult decoderResult() {
         return this.message.decoderResult();
      }

      public DecoderResult getDecoderResult() {
         return this.message.decoderResult();
      }

      public void setDecoderResult(DecoderResult var1) {
         this.message.setDecoderResult(var1);
      }

      public ByteBuf content() {
         return this.content;
      }

      public int refCnt() {
         return this.content.refCnt();
      }

      public FullHttpMessage retain() {
         this.content.retain();
         return this;
      }

      public FullHttpMessage retain(int var1) {
         this.content.retain(var1);
         return this;
      }

      public FullHttpMessage touch(Object var1) {
         this.content.touch(var1);
         return this;
      }

      public FullHttpMessage touch() {
         this.content.touch();
         return this;
      }

      public boolean release() {
         return this.content.release();
      }

      public boolean release(int var1) {
         return this.content.release(var1);
      }

      public abstract FullHttpMessage copy();

      public abstract FullHttpMessage duplicate();

      public abstract FullHttpMessage retainedDuplicate();
   }
}
