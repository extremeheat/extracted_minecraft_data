package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.AppendableCharSequence;
import java.util.List;

public abstract class HttpObjectDecoder extends ByteToMessageDecoder {
   private static final String EMPTY_VALUE = "";
   private final int maxChunkSize;
   private final boolean chunkedSupported;
   protected final boolean validateHeaders;
   private final HttpObjectDecoder.HeaderParser headerParser;
   private final HttpObjectDecoder.LineParser lineParser;
   private HttpMessage message;
   private long chunkSize;
   private long contentLength;
   private volatile boolean resetRequested;
   private CharSequence name;
   private CharSequence value;
   private LastHttpContent trailer;
   private HttpObjectDecoder.State currentState;

   protected HttpObjectDecoder() {
      this(4096, 8192, 8192, true);
   }

   protected HttpObjectDecoder(int var1, int var2, int var3, boolean var4) {
      this(var1, var2, var3, var4, true);
   }

   protected HttpObjectDecoder(int var1, int var2, int var3, boolean var4, boolean var5) {
      this(var1, var2, var3, var4, var5, 128);
   }

   protected HttpObjectDecoder(int var1, int var2, int var3, boolean var4, boolean var5, int var6) {
      super();
      this.contentLength = -9223372036854775808L;
      this.currentState = HttpObjectDecoder.State.SKIP_CONTROL_CHARS;
      if (var1 <= 0) {
         throw new IllegalArgumentException("maxInitialLineLength must be a positive integer: " + var1);
      } else if (var2 <= 0) {
         throw new IllegalArgumentException("maxHeaderSize must be a positive integer: " + var2);
      } else if (var3 <= 0) {
         throw new IllegalArgumentException("maxChunkSize must be a positive integer: " + var3);
      } else {
         AppendableCharSequence var7 = new AppendableCharSequence(var6);
         this.lineParser = new HttpObjectDecoder.LineParser(var7, var1);
         this.headerParser = new HttpObjectDecoder.HeaderParser(var7, var2);
         this.maxChunkSize = var3;
         this.chunkedSupported = var4;
         this.validateHeaders = var5;
      }
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (this.resetRequested) {
         this.resetNow();
      }

      int var4;
      int var5;
      AppendableCharSequence var12;
      switch(this.currentState) {
      case SKIP_CONTROL_CHARS:
         if (!skipControlCharacters(var2)) {
            return;
         }

         this.currentState = HttpObjectDecoder.State.READ_INITIAL;
      case READ_INITIAL:
         try {
            var12 = this.lineParser.parse(var2);
            if (var12 == null) {
               return;
            }

            String[] var17 = splitInitialLine(var12);
            if (var17.length < 3) {
               this.currentState = HttpObjectDecoder.State.SKIP_CONTROL_CHARS;
               return;
            }

            this.message = this.createMessage(var17);
            this.currentState = HttpObjectDecoder.State.READ_HEADER;
         } catch (Exception var9) {
            var3.add(this.invalidMessage(var2, var9));
            return;
         }
      case READ_HEADER:
         try {
            HttpObjectDecoder.State var16 = this.readHeaders(var2);
            if (var16 == null) {
               return;
            }

            this.currentState = var16;
            switch(var16) {
            case SKIP_CONTROL_CHARS:
               var3.add(this.message);
               var3.add(LastHttpContent.EMPTY_LAST_CONTENT);
               this.resetNow();
               return;
            case READ_CHUNK_SIZE:
               if (!this.chunkedSupported) {
                  throw new IllegalArgumentException("Chunked messages not supported");
               }

               var3.add(this.message);
               return;
            default:
               long var18 = this.contentLength();
               if (var18 != 0L && (var18 != -1L || !this.isDecodingRequest())) {
                  assert var16 == HttpObjectDecoder.State.READ_FIXED_LENGTH_CONTENT || var16 == HttpObjectDecoder.State.READ_VARIABLE_LENGTH_CONTENT;

                  var3.add(this.message);
                  if (var16 == HttpObjectDecoder.State.READ_FIXED_LENGTH_CONTENT) {
                     this.chunkSize = var18;
                  }

                  return;
               }

               var3.add(this.message);
               var3.add(LastHttpContent.EMPTY_LAST_CONTENT);
               this.resetNow();
               return;
            }
         } catch (Exception var10) {
            var3.add(this.invalidMessage(var2, var10));
            return;
         }
      case READ_CHUNK_SIZE:
         try {
            var12 = this.lineParser.parse(var2);
            if (var12 == null) {
               return;
            }

            var5 = getChunkSize(var12.toString());
            this.chunkSize = (long)var5;
            if (var5 == 0) {
               this.currentState = HttpObjectDecoder.State.READ_CHUNK_FOOTER;
               return;
            }

            this.currentState = HttpObjectDecoder.State.READ_CHUNKED_CONTENT;
         } catch (Exception var8) {
            var3.add(this.invalidChunk(var2, var8));
            return;
         }
      case READ_CHUNKED_CONTENT:
         assert this.chunkSize <= 2147483647L;

         var4 = Math.min((int)this.chunkSize, this.maxChunkSize);
         var4 = Math.min(var4, var2.readableBytes());
         if (var4 == 0) {
            return;
         }

         DefaultHttpContent var15 = new DefaultHttpContent(var2.readRetainedSlice(var4));
         this.chunkSize -= (long)var4;
         var3.add(var15);
         if (this.chunkSize != 0L) {
            return;
         }

         this.currentState = HttpObjectDecoder.State.READ_CHUNK_DELIMITER;
      case READ_CHUNK_DELIMITER:
         var4 = var2.writerIndex();
         var5 = var2.readerIndex();

         while(var4 > var5) {
            byte var14 = var2.getByte(var5++);
            if (var14 == 10) {
               this.currentState = HttpObjectDecoder.State.READ_CHUNK_SIZE;
               break;
            }
         }

         var2.readerIndex(var5);
         return;
      case READ_VARIABLE_LENGTH_CONTENT:
         var4 = Math.min(var2.readableBytes(), this.maxChunkSize);
         if (var4 > 0) {
            ByteBuf var13 = var2.readRetainedSlice(var4);
            var3.add(new DefaultHttpContent(var13));
         }

         return;
      case READ_FIXED_LENGTH_CONTENT:
         var4 = var2.readableBytes();
         if (var4 == 0) {
            return;
         }

         var5 = Math.min(var4, this.maxChunkSize);
         if ((long)var5 > this.chunkSize) {
            var5 = (int)this.chunkSize;
         }

         ByteBuf var6 = var2.readRetainedSlice(var5);
         this.chunkSize -= (long)var5;
         if (this.chunkSize == 0L) {
            var3.add(new DefaultLastHttpContent(var6, this.validateHeaders));
            this.resetNow();
         } else {
            var3.add(new DefaultHttpContent(var6));
         }

         return;
      case READ_CHUNK_FOOTER:
         try {
            LastHttpContent var11 = this.readTrailingHeaders(var2);
            if (var11 == null) {
               return;
            }

            var3.add(var11);
            this.resetNow();
            return;
         } catch (Exception var7) {
            var3.add(this.invalidChunk(var2, var7));
            return;
         }
      case BAD_MESSAGE:
         var2.skipBytes(var2.readableBytes());
         break;
      case UPGRADED:
         var4 = var2.readableBytes();
         if (var4 > 0) {
            var3.add(var2.readBytes(var4));
         }
      }

   }

   protected void decodeLast(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      super.decodeLast(var1, var2, var3);
      if (this.resetRequested) {
         this.resetNow();
      }

      if (this.message != null) {
         boolean var4 = HttpUtil.isTransferEncodingChunked(this.message);
         if (this.currentState == HttpObjectDecoder.State.READ_VARIABLE_LENGTH_CONTENT && !var2.isReadable() && !var4) {
            var3.add(LastHttpContent.EMPTY_LAST_CONTENT);
            this.resetNow();
            return;
         }

         if (this.currentState == HttpObjectDecoder.State.READ_HEADER) {
            var3.add(this.invalidMessage(Unpooled.EMPTY_BUFFER, new PrematureChannelClosureException("Connection closed before received headers")));
            this.resetNow();
            return;
         }

         boolean var5;
         if (!this.isDecodingRequest() && !var4) {
            var5 = this.contentLength() > 0L;
         } else {
            var5 = true;
         }

         if (!var5) {
            var3.add(LastHttpContent.EMPTY_LAST_CONTENT);
         }

         this.resetNow();
      }

   }

   public void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof HttpExpectationFailedEvent) {
         switch(this.currentState) {
         case READ_CHUNK_SIZE:
         case READ_VARIABLE_LENGTH_CONTENT:
         case READ_FIXED_LENGTH_CONTENT:
            this.reset();
         case READ_INITIAL:
         case READ_HEADER:
         }
      }

      super.userEventTriggered(var1, var2);
   }

   protected boolean isContentAlwaysEmpty(HttpMessage var1) {
      if (var1 instanceof HttpResponse) {
         HttpResponse var2 = (HttpResponse)var1;
         int var3 = var2.status().code();
         if (var3 >= 100 && var3 < 200) {
            return var3 != 101 || var2.headers().contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ACCEPT) || !var2.headers().contains((CharSequence)HttpHeaderNames.UPGRADE, (CharSequence)HttpHeaderValues.WEBSOCKET, true);
         }

         switch(var3) {
         case 204:
         case 304:
            return true;
         }
      }

      return false;
   }

   protected boolean isSwitchingToNonHttp1Protocol(HttpResponse var1) {
      if (var1.status().code() != HttpResponseStatus.SWITCHING_PROTOCOLS.code()) {
         return false;
      } else {
         String var2 = var1.headers().get((CharSequence)HttpHeaderNames.UPGRADE);
         return var2 == null || !var2.contains(HttpVersion.HTTP_1_0.text()) && !var2.contains(HttpVersion.HTTP_1_1.text());
      }
   }

   public void reset() {
      this.resetRequested = true;
   }

   private void resetNow() {
      HttpMessage var1 = this.message;
      this.message = null;
      this.name = null;
      this.value = null;
      this.contentLength = -9223372036854775808L;
      this.lineParser.reset();
      this.headerParser.reset();
      this.trailer = null;
      if (!this.isDecodingRequest()) {
         HttpResponse var2 = (HttpResponse)var1;
         if (var2 != null && this.isSwitchingToNonHttp1Protocol(var2)) {
            this.currentState = HttpObjectDecoder.State.UPGRADED;
            return;
         }
      }

      this.resetRequested = false;
      this.currentState = HttpObjectDecoder.State.SKIP_CONTROL_CHARS;
   }

   private HttpMessage invalidMessage(ByteBuf var1, Exception var2) {
      this.currentState = HttpObjectDecoder.State.BAD_MESSAGE;
      var1.skipBytes(var1.readableBytes());
      if (this.message == null) {
         this.message = this.createInvalidMessage();
      }

      this.message.setDecoderResult(DecoderResult.failure(var2));
      HttpMessage var3 = this.message;
      this.message = null;
      return var3;
   }

   private HttpContent invalidChunk(ByteBuf var1, Exception var2) {
      this.currentState = HttpObjectDecoder.State.BAD_MESSAGE;
      var1.skipBytes(var1.readableBytes());
      DefaultLastHttpContent var3 = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
      var3.setDecoderResult(DecoderResult.failure(var2));
      this.message = null;
      this.trailer = null;
      return var3;
   }

   private static boolean skipControlCharacters(ByteBuf var0) {
      boolean var1 = false;
      int var2 = var0.writerIndex();
      int var3 = var0.readerIndex();

      while(var2 > var3) {
         short var4 = var0.getUnsignedByte(var3++);
         if (!Character.isISOControl(var4) && !Character.isWhitespace(var4)) {
            --var3;
            var1 = true;
            break;
         }
      }

      var0.readerIndex(var3);
      return var1;
   }

   private HttpObjectDecoder.State readHeaders(ByteBuf var1) {
      HttpMessage var2 = this.message;
      HttpHeaders var3 = var2.headers();
      AppendableCharSequence var4 = this.headerParser.parse(var1);
      if (var4 == null) {
         return null;
      } else {
         if (var4.length() > 0) {
            do {
               char var5 = var4.charAt(0);
               if (this.name != null && (var5 == ' ' || var5 == '\t')) {
                  String var6 = var4.toString().trim();
                  String var7 = String.valueOf(this.value);
                  this.value = var7 + ' ' + var6;
               } else {
                  if (this.name != null) {
                     var3.add((CharSequence)this.name, (Object)this.value);
                  }

                  this.splitHeader(var4);
               }

               var4 = this.headerParser.parse(var1);
               if (var4 == null) {
                  return null;
               }
            } while(var4.length() > 0);
         }

         if (this.name != null) {
            var3.add((CharSequence)this.name, (Object)this.value);
         }

         this.name = null;
         this.value = null;
         HttpObjectDecoder.State var8;
         if (this.isContentAlwaysEmpty(var2)) {
            HttpUtil.setTransferEncodingChunked(var2, false);
            var8 = HttpObjectDecoder.State.SKIP_CONTROL_CHARS;
         } else if (HttpUtil.isTransferEncodingChunked(var2)) {
            var8 = HttpObjectDecoder.State.READ_CHUNK_SIZE;
         } else if (this.contentLength() >= 0L) {
            var8 = HttpObjectDecoder.State.READ_FIXED_LENGTH_CONTENT;
         } else {
            var8 = HttpObjectDecoder.State.READ_VARIABLE_LENGTH_CONTENT;
         }

         return var8;
      }
   }

   private long contentLength() {
      if (this.contentLength == -9223372036854775808L) {
         this.contentLength = HttpUtil.getContentLength(this.message, -1L);
      }

      return this.contentLength;
   }

   private LastHttpContent readTrailingHeaders(ByteBuf var1) {
      AppendableCharSequence var2 = this.headerParser.parse(var1);
      if (var2 == null) {
         return null;
      } else {
         CharSequence var3 = null;
         if (var2.length() <= 0) {
            return LastHttpContent.EMPTY_LAST_CONTENT;
         } else {
            LastHttpContent var4 = this.trailer;
            if (var4 == null) {
               var4 = this.trailer = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER, this.validateHeaders);
            }

            do {
               char var5 = var2.charAt(0);
               if (var3 == null || var5 != ' ' && var5 != '\t') {
                  this.splitHeader(var2);
                  CharSequence var10 = this.name;
                  if (!HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(var10) && !HttpHeaderNames.TRANSFER_ENCODING.contentEqualsIgnoreCase(var10) && !HttpHeaderNames.TRAILER.contentEqualsIgnoreCase(var10)) {
                     var4.trailingHeaders().add((CharSequence)var10, (Object)this.value);
                  }

                  var3 = this.name;
                  this.name = null;
                  this.value = null;
               } else {
                  List var6 = var4.trailingHeaders().getAll(var3);
                  if (!var6.isEmpty()) {
                     int var7 = var6.size() - 1;
                     String var8 = var2.toString().trim();
                     String var9 = (String)var6.get(var7);
                     var6.set(var7, var9 + var8);
                  }
               }

               var2 = this.headerParser.parse(var1);
               if (var2 == null) {
                  return null;
               }
            } while(var2.length() > 0);

            this.trailer = null;
            return var4;
         }
      }
   }

   protected abstract boolean isDecodingRequest();

   protected abstract HttpMessage createMessage(String[] var1) throws Exception;

   protected abstract HttpMessage createInvalidMessage();

   private static int getChunkSize(String var0) {
      var0 = var0.trim();

      for(int var1 = 0; var1 < var0.length(); ++var1) {
         char var2 = var0.charAt(var1);
         if (var2 == ';' || Character.isWhitespace(var2) || Character.isISOControl(var2)) {
            var0 = var0.substring(0, var1);
            break;
         }
      }

      return Integer.parseInt(var0, 16);
   }

   private static String[] splitInitialLine(AppendableCharSequence var0) {
      int var1 = findNonWhitespace(var0, 0);
      int var2 = findWhitespace(var0, var1);
      int var3 = findNonWhitespace(var0, var2);
      int var4 = findWhitespace(var0, var3);
      int var5 = findNonWhitespace(var0, var4);
      int var6 = findEndOfString(var0);
      return new String[]{var0.subStringUnsafe(var1, var2), var0.subStringUnsafe(var3, var4), var5 < var6 ? var0.subStringUnsafe(var5, var6) : ""};
   }

   private void splitHeader(AppendableCharSequence var1) {
      int var2 = var1.length();
      int var3 = findNonWhitespace(var1, 0);

      int var4;
      for(var4 = var3; var4 < var2; ++var4) {
         char var8 = var1.charAt(var4);
         if (var8 == ':' || Character.isWhitespace(var8)) {
            break;
         }
      }

      int var5;
      for(var5 = var4; var5 < var2; ++var5) {
         if (var1.charAt(var5) == ':') {
            ++var5;
            break;
         }
      }

      this.name = var1.subStringUnsafe(var3, var4);
      int var6 = findNonWhitespace(var1, var5);
      if (var6 == var2) {
         this.value = "";
      } else {
         int var7 = findEndOfString(var1);
         this.value = var1.subStringUnsafe(var6, var7);
      }

   }

   private static int findNonWhitespace(AppendableCharSequence var0, int var1) {
      for(int var2 = var1; var2 < var0.length(); ++var2) {
         if (!Character.isWhitespace(var0.charAtUnsafe(var2))) {
            return var2;
         }
      }

      return var0.length();
   }

   private static int findWhitespace(AppendableCharSequence var0, int var1) {
      for(int var2 = var1; var2 < var0.length(); ++var2) {
         if (Character.isWhitespace(var0.charAtUnsafe(var2))) {
            return var2;
         }
      }

      return var0.length();
   }

   private static int findEndOfString(AppendableCharSequence var0) {
      for(int var1 = var0.length() - 1; var1 > 0; --var1) {
         if (!Character.isWhitespace(var0.charAtUnsafe(var1))) {
            return var1 + 1;
         }
      }

      return 0;
   }

   private static final class LineParser extends HttpObjectDecoder.HeaderParser {
      LineParser(AppendableCharSequence var1, int var2) {
         super(var1, var2);
      }

      public AppendableCharSequence parse(ByteBuf var1) {
         this.reset();
         return super.parse(var1);
      }

      protected TooLongFrameException newException(int var1) {
         return new TooLongFrameException("An HTTP line is larger than " + var1 + " bytes.");
      }
   }

   private static class HeaderParser implements ByteProcessor {
      private final AppendableCharSequence seq;
      private final int maxLength;
      private int size;

      HeaderParser(AppendableCharSequence var1, int var2) {
         super();
         this.seq = var1;
         this.maxLength = var2;
      }

      public AppendableCharSequence parse(ByteBuf var1) {
         int var2 = this.size;
         this.seq.reset();
         int var3 = var1.forEachByte(this);
         if (var3 == -1) {
            this.size = var2;
            return null;
         } else {
            var1.readerIndex(var3 + 1);
            return this.seq;
         }
      }

      public void reset() {
         this.size = 0;
      }

      public boolean process(byte var1) throws Exception {
         char var2 = (char)(var1 & 255);
         if (var2 == '\r') {
            return true;
         } else if (var2 == '\n') {
            return false;
         } else if (++this.size > this.maxLength) {
            throw this.newException(this.maxLength);
         } else {
            this.seq.append(var2);
            return true;
         }
      }

      protected TooLongFrameException newException(int var1) {
         return new TooLongFrameException("HTTP header is larger than " + var1 + " bytes.");
      }
   }

   private static enum State {
      SKIP_CONTROL_CHARS,
      READ_INITIAL,
      READ_HEADER,
      READ_VARIABLE_LENGTH_CONTENT,
      READ_FIXED_LENGTH_CONTENT,
      READ_CHUNK_SIZE,
      READ_CHUNKED_CONTENT,
      READ_CHUNK_DELIMITER,
      READ_CHUNK_FOOTER,
      BAD_MESSAGE,
      UPGRADED;

      private State() {
      }
   }
}
