package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedInput;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class HttpPostRequestEncoder implements ChunkedInput<HttpContent> {
   private static final Entry[] percentEncodings = new Entry[]{new SimpleImmutableEntry(Pattern.compile("\\*"), "%2A"), new SimpleImmutableEntry(Pattern.compile("\\+"), "%20"), new SimpleImmutableEntry(Pattern.compile("~"), "%7E")};
   private final HttpDataFactory factory;
   private final HttpRequest request;
   private final Charset charset;
   private boolean isChunked;
   private final List<InterfaceHttpData> bodyListDatas;
   final List<InterfaceHttpData> multipartHttpDatas;
   private final boolean isMultipart;
   String multipartDataBoundary;
   String multipartMixedBoundary;
   private boolean headerFinalized;
   private final HttpPostRequestEncoder.EncoderMode encoderMode;
   private boolean isLastChunk;
   private boolean isLastChunkSent;
   private FileUpload currentFileUpload;
   private boolean duringMixedMode;
   private long globalBodySize;
   private long globalProgress;
   private ListIterator<InterfaceHttpData> iterator;
   private ByteBuf currentBuffer;
   private InterfaceHttpData currentData;
   private boolean isKey;

   public HttpPostRequestEncoder(HttpRequest var1, boolean var2) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      this(new DefaultHttpDataFactory(16384L), var1, var2, HttpConstants.DEFAULT_CHARSET, HttpPostRequestEncoder.EncoderMode.RFC1738);
   }

   public HttpPostRequestEncoder(HttpDataFactory var1, HttpRequest var2, boolean var3) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      this(var1, var2, var3, HttpConstants.DEFAULT_CHARSET, HttpPostRequestEncoder.EncoderMode.RFC1738);
   }

   public HttpPostRequestEncoder(HttpDataFactory var1, HttpRequest var2, boolean var3, Charset var4, HttpPostRequestEncoder.EncoderMode var5) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      super();
      this.isKey = true;
      this.request = (HttpRequest)ObjectUtil.checkNotNull(var2, "request");
      this.charset = (Charset)ObjectUtil.checkNotNull(var4, "charset");
      this.factory = (HttpDataFactory)ObjectUtil.checkNotNull(var1, "factory");
      if (HttpMethod.TRACE.equals(var2.method())) {
         throw new HttpPostRequestEncoder.ErrorDataEncoderException("Cannot create a Encoder if request is a TRACE");
      } else {
         this.bodyListDatas = new ArrayList();
         this.isLastChunk = false;
         this.isLastChunkSent = false;
         this.isMultipart = var3;
         this.multipartHttpDatas = new ArrayList();
         this.encoderMode = var5;
         if (this.isMultipart) {
            this.initDataMultipart();
         }

      }
   }

   public void cleanFiles() {
      this.factory.cleanRequestHttpData(this.request);
   }

   public boolean isMultipart() {
      return this.isMultipart;
   }

   private void initDataMultipart() {
      this.multipartDataBoundary = getNewMultipartDelimiter();
   }

   private void initMixedMultipart() {
      this.multipartMixedBoundary = getNewMultipartDelimiter();
   }

   private static String getNewMultipartDelimiter() {
      return Long.toHexString(PlatformDependent.threadLocalRandom().nextLong());
   }

   public List<InterfaceHttpData> getBodyListAttributes() {
      return this.bodyListDatas;
   }

   public void setBodyHttpDatas(List<InterfaceHttpData> var1) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (var1 == null) {
         throw new NullPointerException("datas");
      } else {
         this.globalBodySize = 0L;
         this.bodyListDatas.clear();
         this.currentFileUpload = null;
         this.duringMixedMode = false;
         this.multipartHttpDatas.clear();
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            InterfaceHttpData var3 = (InterfaceHttpData)var2.next();
            this.addBodyHttpData(var3);
         }

      }
   }

   public void addBodyAttribute(String var1, String var2) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      String var3 = var2 != null ? var2 : "";
      Attribute var4 = this.factory.createAttribute(this.request, (String)ObjectUtil.checkNotNull(var1, "name"), var3);
      this.addBodyHttpData(var4);
   }

   public void addBodyFileUpload(String var1, File var2, String var3, boolean var4) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      this.addBodyFileUpload(var1, var2.getName(), var2, var3, var4);
   }

   public void addBodyFileUpload(String var1, String var2, File var3, String var4, boolean var5) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      ObjectUtil.checkNotNull(var1, "name");
      ObjectUtil.checkNotNull(var3, "file");
      if (var2 == null) {
         var2 = "";
      }

      String var6 = var4;
      String var7 = null;
      if (var4 == null) {
         if (var5) {
            var6 = "text/plain";
         } else {
            var6 = "application/octet-stream";
         }
      }

      if (!var5) {
         var7 = HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value();
      }

      FileUpload var8 = this.factory.createFileUpload(this.request, var1, var2, var6, var7, (Charset)null, var3.length());

      try {
         var8.setContent(var3);
      } catch (IOException var10) {
         throw new HttpPostRequestEncoder.ErrorDataEncoderException(var10);
      }

      this.addBodyHttpData(var8);
   }

   public void addBodyFileUploads(String var1, File[] var2, String[] var3, boolean[] var4) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (var2.length != var3.length && var2.length != var4.length) {
         throw new IllegalArgumentException("Different array length");
      } else {
         for(int var5 = 0; var5 < var2.length; ++var5) {
            this.addBodyFileUpload(var1, var2[var5], var3[var5], var4[var5]);
         }

      }
   }

   public void addBodyHttpData(InterfaceHttpData var1) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (this.headerFinalized) {
         throw new HttpPostRequestEncoder.ErrorDataEncoderException("Cannot add value once finalized");
      } else {
         this.bodyListDatas.add(ObjectUtil.checkNotNull(var1, "data"));
         FileUpload var8;
         if (!this.isMultipart) {
            String var12;
            Attribute var14;
            String var15;
            if (var1 instanceof Attribute) {
               Attribute var9 = (Attribute)var1;

               try {
                  var12 = this.encodeAttribute(var9.getName(), this.charset);
                  var15 = this.encodeAttribute(var9.getValue(), this.charset);
                  var14 = this.factory.createAttribute(this.request, var12, var15);
                  this.multipartHttpDatas.add(var14);
                  this.globalBodySize += (long)(var14.getName().length() + 1) + var14.length() + 1L;
               } catch (IOException var7) {
                  throw new HttpPostRequestEncoder.ErrorDataEncoderException(var7);
               }
            } else if (var1 instanceof FileUpload) {
               var8 = (FileUpload)var1;
               var12 = this.encodeAttribute(var8.getName(), this.charset);
               var15 = this.encodeAttribute(var8.getFilename(), this.charset);
               var14 = this.factory.createAttribute(this.request, var12, var15);
               this.multipartHttpDatas.add(var14);
               this.globalBodySize += (long)(var14.getName().length() + 1) + var14.length() + 1L;
            }

         } else {
            if (var1 instanceof Attribute) {
               InternalAttribute var2;
               if (this.duringMixedMode) {
                  var2 = new InternalAttribute(this.charset);
                  var2.addValue("\r\n--" + this.multipartMixedBoundary + "--");
                  this.multipartHttpDatas.add(var2);
                  this.multipartMixedBoundary = null;
                  this.currentFileUpload = null;
                  this.duringMixedMode = false;
               }

               var2 = new InternalAttribute(this.charset);
               if (!this.multipartHttpDatas.isEmpty()) {
                  var2.addValue("\r\n");
               }

               var2.addValue("--" + this.multipartDataBoundary + "\r\n");
               Attribute var3 = (Attribute)var1;
               var2.addValue(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.FORM_DATA + "; " + HttpHeaderValues.NAME + "=\"" + var3.getName() + "\"\r\n");
               var2.addValue(HttpHeaderNames.CONTENT_LENGTH + ": " + var3.length() + "\r\n");
               Charset var4 = var3.getCharset();
               if (var4 != null) {
                  var2.addValue(HttpHeaderNames.CONTENT_TYPE + ": " + "text/plain" + "; " + HttpHeaderValues.CHARSET + '=' + var4.name() + "\r\n");
               }

               var2.addValue("\r\n");
               this.multipartHttpDatas.add(var2);
               this.multipartHttpDatas.add(var1);
               this.globalBodySize += var3.length() + (long)var2.size();
            } else if (var1 instanceof FileUpload) {
               var8 = (FileUpload)var1;
               InternalAttribute var10 = new InternalAttribute(this.charset);
               if (!this.multipartHttpDatas.isEmpty()) {
                  var10.addValue("\r\n");
               }

               boolean var11;
               if (this.duringMixedMode) {
                  if (this.currentFileUpload != null && this.currentFileUpload.getName().equals(var8.getName())) {
                     var11 = true;
                  } else {
                     var10.addValue("--" + this.multipartMixedBoundary + "--");
                     this.multipartHttpDatas.add(var10);
                     this.multipartMixedBoundary = null;
                     var10 = new InternalAttribute(this.charset);
                     var10.addValue("\r\n");
                     var11 = false;
                     this.currentFileUpload = var8;
                     this.duringMixedMode = false;
                  }
               } else if (this.encoderMode != HttpPostRequestEncoder.EncoderMode.HTML5 && this.currentFileUpload != null && this.currentFileUpload.getName().equals(var8.getName())) {
                  this.initMixedMultipart();
                  InternalAttribute var5 = (InternalAttribute)this.multipartHttpDatas.get(this.multipartHttpDatas.size() - 2);
                  this.globalBodySize -= (long)var5.size();
                  StringBuilder var6 = (new StringBuilder(139 + this.multipartDataBoundary.length() + this.multipartMixedBoundary.length() * 2 + var8.getFilename().length() + var8.getName().length())).append("--").append(this.multipartDataBoundary).append("\r\n").append(HttpHeaderNames.CONTENT_DISPOSITION).append(": ").append(HttpHeaderValues.FORM_DATA).append("; ").append(HttpHeaderValues.NAME).append("=\"").append(var8.getName()).append("\"\r\n").append(HttpHeaderNames.CONTENT_TYPE).append(": ").append(HttpHeaderValues.MULTIPART_MIXED).append("; ").append(HttpHeaderValues.BOUNDARY).append('=').append(this.multipartMixedBoundary).append("\r\n\r\n").append("--").append(this.multipartMixedBoundary).append("\r\n").append(HttpHeaderNames.CONTENT_DISPOSITION).append(": ").append(HttpHeaderValues.ATTACHMENT);
                  if (!var8.getFilename().isEmpty()) {
                     var6.append("; ").append(HttpHeaderValues.FILENAME).append("=\"").append(var8.getFilename()).append('"');
                  }

                  var6.append("\r\n");
                  var5.setValue(var6.toString(), 1);
                  var5.setValue("", 2);
                  this.globalBodySize += (long)var5.size();
                  var11 = true;
                  this.duringMixedMode = true;
               } else {
                  var11 = false;
                  this.currentFileUpload = var8;
                  this.duringMixedMode = false;
               }

               if (var11) {
                  var10.addValue("--" + this.multipartMixedBoundary + "\r\n");
                  if (var8.getFilename().isEmpty()) {
                     var10.addValue(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.ATTACHMENT + "\r\n");
                  } else {
                     var10.addValue(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.ATTACHMENT + "; " + HttpHeaderValues.FILENAME + "=\"" + var8.getFilename() + "\"\r\n");
                  }
               } else {
                  var10.addValue("--" + this.multipartDataBoundary + "\r\n");
                  if (var8.getFilename().isEmpty()) {
                     var10.addValue(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.FORM_DATA + "; " + HttpHeaderValues.NAME + "=\"" + var8.getName() + "\"\r\n");
                  } else {
                     var10.addValue(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.FORM_DATA + "; " + HttpHeaderValues.NAME + "=\"" + var8.getName() + "\"; " + HttpHeaderValues.FILENAME + "=\"" + var8.getFilename() + "\"\r\n");
                  }
               }

               var10.addValue(HttpHeaderNames.CONTENT_LENGTH + ": " + var8.length() + "\r\n");
               var10.addValue(HttpHeaderNames.CONTENT_TYPE + ": " + var8.getContentType());
               String var13 = var8.getContentTransferEncoding();
               if (var13 != null && var13.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) {
                  var10.addValue("\r\n" + HttpHeaderNames.CONTENT_TRANSFER_ENCODING + ": " + HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value() + "\r\n\r\n");
               } else if (var8.getCharset() != null) {
                  var10.addValue("; " + HttpHeaderValues.CHARSET + '=' + var8.getCharset().name() + "\r\n\r\n");
               } else {
                  var10.addValue("\r\n\r\n");
               }

               this.multipartHttpDatas.add(var10);
               this.multipartHttpDatas.add(var1);
               this.globalBodySize += var8.length() + (long)var10.size();
            }

         }
      }
   }

   public HttpRequest finalizeRequest() throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (!this.headerFinalized) {
         if (this.isMultipart) {
            InternalAttribute var1 = new InternalAttribute(this.charset);
            if (this.duringMixedMode) {
               var1.addValue("\r\n--" + this.multipartMixedBoundary + "--");
            }

            var1.addValue("\r\n--" + this.multipartDataBoundary + "--\r\n");
            this.multipartHttpDatas.add(var1);
            this.multipartMixedBoundary = null;
            this.currentFileUpload = null;
            this.duringMixedMode = false;
            this.globalBodySize += (long)var1.size();
         }

         this.headerFinalized = true;
         HttpHeaders var9 = this.request.headers();
         List var2 = var9.getAll((CharSequence)HttpHeaderNames.CONTENT_TYPE);
         List var3 = var9.getAll((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
         if (var2 != null) {
            var9.remove((CharSequence)HttpHeaderNames.CONTENT_TYPE);
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               String var5 = (String)var4.next();
               String var6 = var5.toLowerCase();
               if (!var6.startsWith(HttpHeaderValues.MULTIPART_FORM_DATA.toString()) && !var6.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())) {
                  var9.add((CharSequence)HttpHeaderNames.CONTENT_TYPE, (Object)var5);
               }
            }
         }

         if (this.isMultipart) {
            String var10 = HttpHeaderValues.MULTIPART_FORM_DATA + "; " + HttpHeaderValues.BOUNDARY + '=' + this.multipartDataBoundary;
            var9.add((CharSequence)HttpHeaderNames.CONTENT_TYPE, (Object)var10);
         } else {
            var9.add((CharSequence)HttpHeaderNames.CONTENT_TYPE, (Object)HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED);
         }

         long var11 = this.globalBodySize;
         if (!this.isMultipart) {
            --var11;
         }

         this.iterator = this.multipartHttpDatas.listIterator();
         var9.set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)String.valueOf(var11));
         if (var11 <= 8096L && !this.isMultipart) {
            HttpContent var13 = this.nextChunk();
            if (this.request instanceof FullHttpRequest) {
               FullHttpRequest var14 = (FullHttpRequest)this.request;
               ByteBuf var8 = var13.content();
               if (var14.content() != var8) {
                  var14.content().clear().writeBytes(var8);
                  var8.release();
               }

               return var14;
            } else {
               return new HttpPostRequestEncoder.WrappedFullHttpRequest(this.request, var13);
            }
         } else {
            this.isChunked = true;
            if (var3 != null) {
               var9.remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
               Iterator var12 = var3.iterator();

               while(var12.hasNext()) {
                  CharSequence var7 = (CharSequence)var12.next();
                  if (!HttpHeaderValues.CHUNKED.contentEqualsIgnoreCase(var7)) {
                     var9.add((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, (Object)var7);
                  }
               }
            }

            HttpUtil.setTransferEncodingChunked(this.request, true);
            return new HttpPostRequestEncoder.WrappedHttpRequest(this.request);
         }
      } else {
         throw new HttpPostRequestEncoder.ErrorDataEncoderException("Header already encoded");
      }
   }

   public boolean isChunked() {
      return this.isChunked;
   }

   private String encodeAttribute(String var1, Charset var2) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (var1 == null) {
         return "";
      } else {
         try {
            String var3 = URLEncoder.encode(var1, var2.name());
            if (this.encoderMode == HttpPostRequestEncoder.EncoderMode.RFC3986) {
               Entry[] var4 = percentEncodings;
               int var5 = var4.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  Entry var7 = var4[var6];
                  String var8 = (String)var7.getValue();
                  var3 = ((Pattern)var7.getKey()).matcher(var3).replaceAll(var8);
               }
            }

            return var3;
         } catch (UnsupportedEncodingException var9) {
            throw new HttpPostRequestEncoder.ErrorDataEncoderException(var2.name(), var9);
         }
      }
   }

   private ByteBuf fillByteBuf() {
      int var1 = this.currentBuffer.readableBytes();
      if (var1 > 8096) {
         return this.currentBuffer.readRetainedSlice(8096);
      } else {
         ByteBuf var2 = this.currentBuffer;
         this.currentBuffer = null;
         return var2;
      }
   }

   private HttpContent encodeNextChunkMultipart(int var1) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (this.currentData == null) {
         return null;
      } else {
         ByteBuf var2;
         if (this.currentData instanceof InternalAttribute) {
            var2 = ((InternalAttribute)this.currentData).toByteBuf();
            this.currentData = null;
         } else {
            try {
               var2 = ((HttpData)this.currentData).getChunk(var1);
            } catch (IOException var4) {
               throw new HttpPostRequestEncoder.ErrorDataEncoderException(var4);
            }

            if (var2.capacity() == 0) {
               this.currentData = null;
               return null;
            }
         }

         if (this.currentBuffer == null) {
            this.currentBuffer = var2;
         } else {
            this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, var2);
         }

         if (this.currentBuffer.readableBytes() < 8096) {
            this.currentData = null;
            return null;
         } else {
            var2 = this.fillByteBuf();
            return new DefaultHttpContent(var2);
         }
      }
   }

   private HttpContent encodeNextChunkUrlEncoded(int var1) throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (this.currentData == null) {
         return null;
      } else {
         int var2 = var1;
         ByteBuf var3;
         if (this.isKey) {
            String var4 = this.currentData.getName();
            var3 = Unpooled.wrappedBuffer(var4.getBytes());
            this.isKey = false;
            if (this.currentBuffer == null) {
               this.currentBuffer = Unpooled.wrappedBuffer(var3, Unpooled.wrappedBuffer("=".getBytes()));
            } else {
               this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, var3, Unpooled.wrappedBuffer("=".getBytes()));
            }

            var2 = var1 - (var3.readableBytes() + 1);
            if (this.currentBuffer.readableBytes() >= 8096) {
               var3 = this.fillByteBuf();
               return new DefaultHttpContent(var3);
            }
         }

         try {
            var3 = ((HttpData)this.currentData).getChunk(var2);
         } catch (IOException var5) {
            throw new HttpPostRequestEncoder.ErrorDataEncoderException(var5);
         }

         ByteBuf var6 = null;
         if (var3.readableBytes() < var2) {
            this.isKey = true;
            var6 = this.iterator.hasNext() ? Unpooled.wrappedBuffer("&".getBytes()) : null;
         }

         if (var3.capacity() == 0) {
            this.currentData = null;
            if (this.currentBuffer == null) {
               this.currentBuffer = var6;
            } else if (var6 != null) {
               this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, var6);
            }

            if (this.currentBuffer.readableBytes() >= 8096) {
               var3 = this.fillByteBuf();
               return new DefaultHttpContent(var3);
            } else {
               return null;
            }
         } else {
            if (this.currentBuffer == null) {
               if (var6 != null) {
                  this.currentBuffer = Unpooled.wrappedBuffer(var3, var6);
               } else {
                  this.currentBuffer = var3;
               }
            } else if (var6 != null) {
               this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, var3, var6);
            } else {
               this.currentBuffer = Unpooled.wrappedBuffer(this.currentBuffer, var3);
            }

            if (this.currentBuffer.readableBytes() < 8096) {
               this.currentData = null;
               this.isKey = true;
               return null;
            } else {
               var3 = this.fillByteBuf();
               return new DefaultHttpContent(var3);
            }
         }
      }
   }

   public void close() throws Exception {
   }

   /** @deprecated */
   @Deprecated
   public HttpContent readChunk(ChannelHandlerContext var1) throws Exception {
      return this.readChunk(var1.alloc());
   }

   public HttpContent readChunk(ByteBufAllocator var1) throws Exception {
      if (this.isLastChunkSent) {
         return null;
      } else {
         HttpContent var2 = this.nextChunk();
         this.globalProgress += (long)var2.content().readableBytes();
         return var2;
      }
   }

   private HttpContent nextChunk() throws HttpPostRequestEncoder.ErrorDataEncoderException {
      if (this.isLastChunk) {
         this.isLastChunkSent = true;
         return LastHttpContent.EMPTY_LAST_CONTENT;
      } else {
         int var1 = this.calculateRemainingSize();
         if (var1 <= 0) {
            ByteBuf var3 = this.fillByteBuf();
            return new DefaultHttpContent(var3);
         } else {
            HttpContent var2;
            if (this.currentData != null) {
               if (this.isMultipart) {
                  var2 = this.encodeNextChunkMultipart(var1);
               } else {
                  var2 = this.encodeNextChunkUrlEncoded(var1);
               }

               if (var2 != null) {
                  return var2;
               }

               var1 = this.calculateRemainingSize();
            }

            if (!this.iterator.hasNext()) {
               return this.lastChunk();
            } else {
               while(var1 > 0 && this.iterator.hasNext()) {
                  this.currentData = (InterfaceHttpData)this.iterator.next();
                  if (this.isMultipart) {
                     var2 = this.encodeNextChunkMultipart(var1);
                  } else {
                     var2 = this.encodeNextChunkUrlEncoded(var1);
                  }

                  if (var2 != null) {
                     return var2;
                  }

                  var1 = this.calculateRemainingSize();
               }

               return this.lastChunk();
            }
         }
      }
   }

   private int calculateRemainingSize() {
      int var1 = 8096;
      if (this.currentBuffer != null) {
         var1 -= this.currentBuffer.readableBytes();
      }

      return var1;
   }

   private HttpContent lastChunk() {
      this.isLastChunk = true;
      if (this.currentBuffer == null) {
         this.isLastChunkSent = true;
         return LastHttpContent.EMPTY_LAST_CONTENT;
      } else {
         ByteBuf var1 = this.currentBuffer;
         this.currentBuffer = null;
         return new DefaultHttpContent(var1);
      }
   }

   public boolean isEndOfInput() throws Exception {
      return this.isLastChunkSent;
   }

   public long length() {
      return this.isMultipart ? this.globalBodySize : this.globalBodySize - 1L;
   }

   public long progress() {
      return this.globalProgress;
   }

   private static final class WrappedFullHttpRequest extends HttpPostRequestEncoder.WrappedHttpRequest implements FullHttpRequest {
      private final HttpContent content;

      private WrappedFullHttpRequest(HttpRequest var1, HttpContent var2) {
         super(var1);
         this.content = var2;
      }

      public FullHttpRequest setProtocolVersion(HttpVersion var1) {
         super.setProtocolVersion(var1);
         return this;
      }

      public FullHttpRequest setMethod(HttpMethod var1) {
         super.setMethod(var1);
         return this;
      }

      public FullHttpRequest setUri(String var1) {
         super.setUri(var1);
         return this;
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
         DefaultFullHttpRequest var2 = new DefaultFullHttpRequest(this.protocolVersion(), this.method(), this.uri(), var1);
         var2.headers().set(this.headers());
         var2.trailingHeaders().set(this.trailingHeaders());
         return var2;
      }

      public FullHttpRequest retain(int var1) {
         this.content.retain(var1);
         return this;
      }

      public FullHttpRequest retain() {
         this.content.retain();
         return this;
      }

      public FullHttpRequest touch() {
         this.content.touch();
         return this;
      }

      public FullHttpRequest touch(Object var1) {
         this.content.touch(var1);
         return this;
      }

      public ByteBuf content() {
         return this.content.content();
      }

      public HttpHeaders trailingHeaders() {
         return (HttpHeaders)(this.content instanceof LastHttpContent ? ((LastHttpContent)this.content).trailingHeaders() : EmptyHttpHeaders.INSTANCE);
      }

      public int refCnt() {
         return this.content.refCnt();
      }

      public boolean release() {
         return this.content.release();
      }

      public boolean release(int var1) {
         return this.content.release(var1);
      }

      // $FF: synthetic method
      WrappedFullHttpRequest(HttpRequest var1, HttpContent var2, Object var3) {
         this(var1, var2);
      }
   }

   private static class WrappedHttpRequest implements HttpRequest {
      private final HttpRequest request;

      WrappedHttpRequest(HttpRequest var1) {
         super();
         this.request = var1;
      }

      public HttpRequest setProtocolVersion(HttpVersion var1) {
         this.request.setProtocolVersion(var1);
         return this;
      }

      public HttpRequest setMethod(HttpMethod var1) {
         this.request.setMethod(var1);
         return this;
      }

      public HttpRequest setUri(String var1) {
         this.request.setUri(var1);
         return this;
      }

      public HttpMethod getMethod() {
         return this.request.method();
      }

      public HttpMethod method() {
         return this.request.method();
      }

      public String getUri() {
         return this.request.uri();
      }

      public String uri() {
         return this.request.uri();
      }

      public HttpVersion getProtocolVersion() {
         return this.request.protocolVersion();
      }

      public HttpVersion protocolVersion() {
         return this.request.protocolVersion();
      }

      public HttpHeaders headers() {
         return this.request.headers();
      }

      public DecoderResult decoderResult() {
         return this.request.decoderResult();
      }

      /** @deprecated */
      @Deprecated
      public DecoderResult getDecoderResult() {
         return this.request.getDecoderResult();
      }

      public void setDecoderResult(DecoderResult var1) {
         this.request.setDecoderResult(var1);
      }
   }

   public static class ErrorDataEncoderException extends Exception {
      private static final long serialVersionUID = 5020247425493164465L;

      public ErrorDataEncoderException() {
         super();
      }

      public ErrorDataEncoderException(String var1) {
         super(var1);
      }

      public ErrorDataEncoderException(Throwable var1) {
         super(var1);
      }

      public ErrorDataEncoderException(String var1, Throwable var2) {
         super(var1, var2);
      }
   }

   public static enum EncoderMode {
      RFC1738,
      RFC3986,
      HTML5;

      private EncoderMode() {
      }
   }
}
