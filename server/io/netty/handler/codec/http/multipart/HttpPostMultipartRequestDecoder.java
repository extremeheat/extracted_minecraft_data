package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpPostMultipartRequestDecoder implements InterfaceHttpPostRequestDecoder {
   private final HttpDataFactory factory;
   private final HttpRequest request;
   private Charset charset;
   private boolean isLastChunk;
   private final List<InterfaceHttpData> bodyListHttpData;
   private final Map<String, List<InterfaceHttpData>> bodyMapHttpData;
   private ByteBuf undecodedChunk;
   private int bodyListHttpDataRank;
   private String multipartDataBoundary;
   private String multipartMixedBoundary;
   private HttpPostRequestDecoder.MultiPartStatus currentStatus;
   private Map<CharSequence, Attribute> currentFieldAttributes;
   private FileUpload currentFileUpload;
   private Attribute currentAttribute;
   private boolean destroyed;
   private int discardThreshold;
   private static final String FILENAME_ENCODED;

   public HttpPostMultipartRequestDecoder(HttpRequest var1) {
      this(new DefaultHttpDataFactory(16384L), var1, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostMultipartRequestDecoder(HttpDataFactory var1, HttpRequest var2) {
      this(var1, var2, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostMultipartRequestDecoder(HttpDataFactory var1, HttpRequest var2, Charset var3) {
      super();
      this.bodyListHttpData = new ArrayList();
      this.bodyMapHttpData = new TreeMap(CaseIgnoringComparator.INSTANCE);
      this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
      this.discardThreshold = 10485760;
      this.request = (HttpRequest)ObjectUtil.checkNotNull(var2, "request");
      this.charset = (Charset)ObjectUtil.checkNotNull(var3, "charset");
      this.factory = (HttpDataFactory)ObjectUtil.checkNotNull(var1, "factory");
      this.setMultipart(this.request.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE));
      if (var2 instanceof HttpContent) {
         this.offer((HttpContent)var2);
      } else {
         this.undecodedChunk = Unpooled.buffer();
         this.parseBody();
      }

   }

   private void setMultipart(String var1) {
      String[] var2 = HttpPostRequestDecoder.getMultipartDataBoundary(var1);
      if (var2 != null) {
         this.multipartDataBoundary = var2[0];
         if (var2.length > 1 && var2[1] != null) {
            this.charset = Charset.forName(var2[1]);
         }
      } else {
         this.multipartDataBoundary = null;
      }

      this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
   }

   private void checkDestroyed() {
      if (this.destroyed) {
         throw new IllegalStateException(HttpPostMultipartRequestDecoder.class.getSimpleName() + " was destroyed already");
      }
   }

   public boolean isMultipart() {
      this.checkDestroyed();
      return true;
   }

   public void setDiscardThreshold(int var1) {
      this.discardThreshold = ObjectUtil.checkPositiveOrZero(var1, "discardThreshold");
   }

   public int getDiscardThreshold() {
      return this.discardThreshold;
   }

   public List<InterfaceHttpData> getBodyHttpDatas() {
      this.checkDestroyed();
      if (!this.isLastChunk) {
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      } else {
         return this.bodyListHttpData;
      }
   }

   public List<InterfaceHttpData> getBodyHttpDatas(String var1) {
      this.checkDestroyed();
      if (!this.isLastChunk) {
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      } else {
         return (List)this.bodyMapHttpData.get(var1);
      }
   }

   public InterfaceHttpData getBodyHttpData(String var1) {
      this.checkDestroyed();
      if (!this.isLastChunk) {
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      } else {
         List var2 = (List)this.bodyMapHttpData.get(var1);
         return var2 != null ? (InterfaceHttpData)var2.get(0) : null;
      }
   }

   public HttpPostMultipartRequestDecoder offer(HttpContent var1) {
      this.checkDestroyed();
      ByteBuf var2 = var1.content();
      if (this.undecodedChunk == null) {
         this.undecodedChunk = var2.copy();
      } else {
         this.undecodedChunk.writeBytes(var2);
      }

      if (var1 instanceof LastHttpContent) {
         this.isLastChunk = true;
      }

      this.parseBody();
      if (this.undecodedChunk != null && this.undecodedChunk.writerIndex() > this.discardThreshold) {
         this.undecodedChunk.discardReadBytes();
      }

      return this;
   }

   public boolean hasNext() {
      this.checkDestroyed();
      if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE && this.bodyListHttpDataRank >= this.bodyListHttpData.size()) {
         throw new HttpPostRequestDecoder.EndOfDataDecoderException();
      } else {
         return !this.bodyListHttpData.isEmpty() && this.bodyListHttpDataRank < this.bodyListHttpData.size();
      }
   }

   public InterfaceHttpData next() {
      this.checkDestroyed();
      return this.hasNext() ? (InterfaceHttpData)this.bodyListHttpData.get(this.bodyListHttpDataRank++) : null;
   }

   public InterfaceHttpData currentPartialHttpData() {
      return (InterfaceHttpData)(this.currentFileUpload != null ? this.currentFileUpload : this.currentAttribute);
   }

   private void parseBody() {
      if (this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE && this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
         this.parseBodyMultipart();
      } else {
         if (this.isLastChunk) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
         }

      }
   }

   protected void addHttpData(InterfaceHttpData var1) {
      if (var1 != null) {
         Object var2 = (List)this.bodyMapHttpData.get(var1.getName());
         if (var2 == null) {
            var2 = new ArrayList(1);
            this.bodyMapHttpData.put(var1.getName(), var2);
         }

         ((List)var2).add(var1);
         this.bodyListHttpData.add(var1);
      }
   }

   private void parseBodyMultipart() {
      if (this.undecodedChunk != null && this.undecodedChunk.readableBytes() != 0) {
         for(InterfaceHttpData var1 = this.decodeMultipart(this.currentStatus); var1 != null; var1 = this.decodeMultipart(this.currentStatus)) {
            this.addHttpData(var1);
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE || this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
               break;
            }
         }

      }
   }

   private InterfaceHttpData decodeMultipart(HttpPostRequestDecoder.MultiPartStatus var1) {
      switch(var1) {
      case NOTSTARTED:
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
      case PREAMBLE:
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
      case HEADERDELIMITER:
         return this.findMultipartDelimiter(this.multipartDataBoundary, HttpPostRequestDecoder.MultiPartStatus.DISPOSITION, HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE);
      case DISPOSITION:
         return this.findMultipartDisposition();
      case FIELD:
         Charset var2 = null;
         Attribute var3 = (Attribute)this.currentFieldAttributes.get(HttpHeaderValues.CHARSET);
         if (var3 != null) {
            try {
               var2 = Charset.forName(var3.getValue());
            } catch (IOException var14) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var14);
            } catch (UnsupportedCharsetException var15) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var15);
            }
         }

         Attribute var4 = (Attribute)this.currentFieldAttributes.get(HttpHeaderValues.NAME);
         Attribute var5;
         if (this.currentAttribute == null) {
            var5 = (Attribute)this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_LENGTH);

            long var6;
            try {
               var6 = var5 != null ? Long.parseLong(var5.getValue()) : 0L;
            } catch (IOException var12) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var12);
            } catch (NumberFormatException var13) {
               var6 = 0L;
            }

            try {
               if (var6 > 0L) {
                  this.currentAttribute = this.factory.createAttribute(this.request, cleanString(var4.getValue()), var6);
               } else {
                  this.currentAttribute = this.factory.createAttribute(this.request, cleanString(var4.getValue()));
               }
            } catch (NullPointerException var9) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var9);
            } catch (IllegalArgumentException var10) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var10);
            } catch (IOException var11) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException(var11);
            }

            if (var2 != null) {
               this.currentAttribute.setCharset(var2);
            }
         }

         if (!loadDataMultipart(this.undecodedChunk, this.multipartDataBoundary, this.currentAttribute)) {
            return null;
         }

         var5 = this.currentAttribute;
         this.currentAttribute = null;
         this.currentFieldAttributes = null;
         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
         return var5;
      case FILEUPLOAD:
         return this.getFileUpload(this.multipartDataBoundary);
      case MIXEDDELIMITER:
         return this.findMultipartDelimiter(this.multipartMixedBoundary, HttpPostRequestDecoder.MultiPartStatus.MIXEDDISPOSITION, HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
      case MIXEDDISPOSITION:
         return this.findMultipartDisposition();
      case MIXEDFILEUPLOAD:
         return this.getFileUpload(this.multipartMixedBoundary);
      case PREEPILOGUE:
         return null;
      case EPILOGUE:
         return null;
      default:
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("Shouldn't reach here.");
      }
   }

   private static void skipControlCharacters(ByteBuf var0) {
      if (!var0.hasArray()) {
         try {
            skipControlCharactersStandard(var0);
         } catch (IndexOutOfBoundsException var3) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var3);
         }
      } else {
         HttpPostBodyUtil.SeekAheadOptimize var1 = new HttpPostBodyUtil.SeekAheadOptimize(var0);

         char var2;
         do {
            if (var1.pos >= var1.limit) {
               throw new HttpPostRequestDecoder.NotEnoughDataDecoderException("Access out of bounds");
            }

            var2 = (char)(var1.bytes[var1.pos++] & 255);
         } while(Character.isISOControl(var2) || Character.isWhitespace(var2));

         var1.setReadPosition(1);
      }
   }

   private static void skipControlCharactersStandard(ByteBuf var0) {
      char var1;
      do {
         var1 = (char)var0.readUnsignedByte();
      } while(Character.isISOControl(var1) || Character.isWhitespace(var1));

      var0.readerIndex(var0.readerIndex() - 1);
   }

   private InterfaceHttpData findMultipartDelimiter(String var1, HttpPostRequestDecoder.MultiPartStatus var2, HttpPostRequestDecoder.MultiPartStatus var3) {
      int var4 = this.undecodedChunk.readerIndex();

      try {
         skipControlCharacters(this.undecodedChunk);
      } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException var8) {
         this.undecodedChunk.readerIndex(var4);
         return null;
      }

      this.skipOneLine();

      String var5;
      try {
         var5 = readDelimiter(this.undecodedChunk, var1);
      } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException var7) {
         this.undecodedChunk.readerIndex(var4);
         return null;
      }

      if (var5.equals(var1)) {
         this.currentStatus = var2;
         return this.decodeMultipart(var2);
      } else if (var5.equals(var1 + "--")) {
         this.currentStatus = var3;
         if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER) {
            this.currentFieldAttributes = null;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
         } else {
            return null;
         }
      } else {
         this.undecodedChunk.readerIndex(var4);
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("No Multipart delimiter found");
      }
   }

   private InterfaceHttpData findMultipartDisposition() {
      int var1 = this.undecodedChunk.readerIndex();
      if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
         this.currentFieldAttributes = new TreeMap(CaseIgnoringComparator.INSTANCE);
      }

      while(!this.skipOneLine()) {
         String var2;
         try {
            skipControlCharacters(this.undecodedChunk);
            var2 = readLine(this.undecodedChunk, this.charset);
         } catch (HttpPostRequestDecoder.NotEnoughDataDecoderException var19) {
            this.undecodedChunk.readerIndex(var1);
            return null;
         }

         String[] var3 = splitMultipartHeader(var2);
         Attribute var7;
         if (!HttpHeaderNames.CONTENT_DISPOSITION.contentEqualsIgnoreCase(var3[0])) {
            Attribute var24;
            if (HttpHeaderNames.CONTENT_TRANSFER_ENCODING.contentEqualsIgnoreCase(var3[0])) {
               try {
                  var24 = this.factory.createAttribute(this.request, HttpHeaderNames.CONTENT_TRANSFER_ENCODING.toString(), cleanString(var3[1]));
               } catch (NullPointerException var15) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var15);
               } catch (IllegalArgumentException var16) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var16);
               }

               this.currentFieldAttributes.put(HttpHeaderNames.CONTENT_TRANSFER_ENCODING, var24);
            } else if (HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(var3[0])) {
               try {
                  var24 = this.factory.createAttribute(this.request, HttpHeaderNames.CONTENT_LENGTH.toString(), cleanString(var3[1]));
               } catch (NullPointerException var13) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var13);
               } catch (IllegalArgumentException var14) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException(var14);
               }

               this.currentFieldAttributes.put(HttpHeaderNames.CONTENT_LENGTH, var24);
            } else {
               if (!HttpHeaderNames.CONTENT_TYPE.contentEqualsIgnoreCase(var3[0])) {
                  throw new HttpPostRequestDecoder.ErrorDataDecoderException("Unknown Params: " + var2);
               }

               if (HttpHeaderValues.MULTIPART_MIXED.contentEqualsIgnoreCase(var3[1])) {
                  if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
                     String var22 = StringUtil.substringAfter(var3[2], '=');
                     this.multipartMixedBoundary = "--" + var22;
                     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
                     return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER);
                  }

                  throw new HttpPostRequestDecoder.ErrorDataDecoderException("Mixed Multipart found in a previous Mixed Multipart");
               }

               for(int var21 = 1; var21 < var3.length; ++var21) {
                  String var23 = HttpHeaderValues.CHARSET.toString();
                  if (var3[var21].regionMatches(true, 0, var23, 0, var23.length())) {
                     String var25 = StringUtil.substringAfter(var3[var21], '=');

                     try {
                        var7 = this.factory.createAttribute(this.request, var23, cleanString(var25));
                     } catch (NullPointerException var11) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var11);
                     } catch (IllegalArgumentException var12) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var12);
                     }

                     this.currentFieldAttributes.put(HttpHeaderValues.CHARSET, var7);
                  } else {
                     Attribute var26;
                     try {
                        var26 = this.factory.createAttribute(this.request, cleanString(var3[0]), var3[var21]);
                     } catch (NullPointerException var9) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var9);
                     } catch (IllegalArgumentException var10) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(var10);
                     }

                     this.currentFieldAttributes.put(var26.getName(), var26);
                  }
               }
            }
         } else {
            boolean var4;
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
               var4 = HttpHeaderValues.FORM_DATA.contentEqualsIgnoreCase(var3[1]);
            } else {
               var4 = HttpHeaderValues.ATTACHMENT.contentEqualsIgnoreCase(var3[1]) || HttpHeaderValues.FILE.contentEqualsIgnoreCase(var3[1]);
            }

            if (var4) {
               for(int var5 = 2; var5 < var3.length; ++var5) {
                  String[] var6 = var3[var5].split("=", 2);

                  try {
                     var7 = this.getContentDispositionAttribute(var6);
                  } catch (NullPointerException var17) {
                     throw new HttpPostRequestDecoder.ErrorDataDecoderException(var17);
                  } catch (IllegalArgumentException var18) {
                     throw new HttpPostRequestDecoder.ErrorDataDecoderException(var18);
                  }

                  this.currentFieldAttributes.put(var7.getName(), var7);
               }
            }
         }
      }

      Attribute var20 = (Attribute)this.currentFieldAttributes.get(HttpHeaderValues.FILENAME);
      if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
         if (var20 != null) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD);
         } else {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FIELD);
         }
      } else if (var20 != null) {
         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD;
         return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD);
      } else {
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("Filename not found");
      }
   }

   private Attribute getContentDispositionAttribute(String... var1) {
      String var2 = cleanString(var1[0]);
      String var3 = var1[1];
      if (HttpHeaderValues.FILENAME.contentEquals(var2)) {
         int var4 = var3.length() - 1;
         if (var4 > 0 && var3.charAt(0) == '"' && var3.charAt(var4) == '"') {
            var3 = var3.substring(1, var4);
         }
      } else if (FILENAME_ENCODED.equals(var2)) {
         try {
            var2 = HttpHeaderValues.FILENAME.toString();
            String[] var7 = var3.split("'", 3);
            var3 = QueryStringDecoder.decodeComponent(var7[2], Charset.forName(var7[0]));
         } catch (ArrayIndexOutOfBoundsException var5) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var5);
         } catch (UnsupportedCharsetException var6) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var6);
         }
      } else {
         var3 = cleanString(var3);
      }

      return this.factory.createAttribute(this.request, var2, var3);
   }

   protected InterfaceHttpData getFileUpload(String var1) {
      Attribute var2 = (Attribute)this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
      Charset var3 = this.charset;
      HttpPostBodyUtil.TransferEncodingMechanism var4 = HttpPostBodyUtil.TransferEncodingMechanism.BIT7;
      if (var2 != null) {
         String var5;
         try {
            var5 = var2.getValue().toLowerCase();
         } catch (IOException var20) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var20);
         }

         if (var5.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT7.value())) {
            var3 = CharsetUtil.US_ASCII;
         } else if (var5.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT8.value())) {
            var3 = CharsetUtil.ISO_8859_1;
            var4 = HttpPostBodyUtil.TransferEncodingMechanism.BIT8;
         } else {
            if (!var5.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) {
               throw new HttpPostRequestDecoder.ErrorDataDecoderException("TransferEncoding Unknown: " + var5);
            }

            var4 = HttpPostBodyUtil.TransferEncodingMechanism.BINARY;
         }
      }

      Attribute var21 = (Attribute)this.currentFieldAttributes.get(HttpHeaderValues.CHARSET);
      if (var21 != null) {
         try {
            var3 = Charset.forName(var21.getValue());
         } catch (IOException var18) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var18);
         } catch (UnsupportedCharsetException var19) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var19);
         }
      }

      if (this.currentFileUpload == null) {
         Attribute var6 = (Attribute)this.currentFieldAttributes.get(HttpHeaderValues.FILENAME);
         Attribute var7 = (Attribute)this.currentFieldAttributes.get(HttpHeaderValues.NAME);
         Attribute var8 = (Attribute)this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_TYPE);
         Attribute var9 = (Attribute)this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_LENGTH);

         long var10;
         try {
            var10 = var9 != null ? Long.parseLong(var9.getValue()) : 0L;
         } catch (IOException var16) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var16);
         } catch (NumberFormatException var17) {
            var10 = 0L;
         }

         try {
            String var12;
            if (var8 != null) {
               var12 = var8.getValue();
            } else {
               var12 = "application/octet-stream";
            }

            this.currentFileUpload = this.factory.createFileUpload(this.request, cleanString(var7.getValue()), cleanString(var6.getValue()), var12, var4.value(), var3, var10);
         } catch (NullPointerException var13) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var13);
         } catch (IllegalArgumentException var14) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var14);
         } catch (IOException var15) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var15);
         }
      }

      if (!loadDataMultipart(this.undecodedChunk, var1, this.currentFileUpload)) {
         return null;
      } else if (this.currentFileUpload.isCompleted()) {
         if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
            this.currentFieldAttributes = null;
         } else {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
            this.cleanMixedAttributes();
         }

         FileUpload var22 = this.currentFileUpload;
         this.currentFileUpload = null;
         return var22;
      } else {
         return null;
      }
   }

   public void destroy() {
      this.checkDestroyed();
      this.cleanFiles();
      this.destroyed = true;
      if (this.undecodedChunk != null && this.undecodedChunk.refCnt() > 0) {
         this.undecodedChunk.release();
         this.undecodedChunk = null;
      }

      for(int var1 = this.bodyListHttpDataRank; var1 < this.bodyListHttpData.size(); ++var1) {
         ((InterfaceHttpData)this.bodyListHttpData.get(var1)).release();
      }

   }

   public void cleanFiles() {
      this.checkDestroyed();
      this.factory.cleanRequestHttpData(this.request);
   }

   public void removeHttpDataFromClean(InterfaceHttpData var1) {
      this.checkDestroyed();
      this.factory.removeHttpDataFromClean(this.request, var1);
   }

   private void cleanMixedAttributes() {
      this.currentFieldAttributes.remove(HttpHeaderValues.CHARSET);
      this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_LENGTH);
      this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
      this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_TYPE);
      this.currentFieldAttributes.remove(HttpHeaderValues.FILENAME);
   }

   private static String readLineStandard(ByteBuf var0, Charset var1) {
      int var2 = var0.readerIndex();

      try {
         ByteBuf var3 = Unpooled.buffer(64);

         while(var0.isReadable()) {
            byte var4 = var0.readByte();
            if (var4 == 13) {
               var4 = var0.getByte(var0.readerIndex());
               if (var4 == 10) {
                  var0.readByte();
                  return var3.toString(var1);
               }

               var3.writeByte(13);
            } else {
               if (var4 == 10) {
                  return var3.toString(var1);
               }

               var3.writeByte(var4);
            }
         }
      } catch (IndexOutOfBoundsException var5) {
         var0.readerIndex(var2);
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var5);
      }

      var0.readerIndex(var2);
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
   }

   private static String readLine(ByteBuf var0, Charset var1) {
      if (!var0.hasArray()) {
         return readLineStandard(var0, var1);
      } else {
         HttpPostBodyUtil.SeekAheadOptimize var2 = new HttpPostBodyUtil.SeekAheadOptimize(var0);
         int var3 = var0.readerIndex();

         try {
            ByteBuf var4 = Unpooled.buffer(64);

            while(var2.pos < var2.limit) {
               byte var5 = var2.bytes[var2.pos++];
               if (var5 == 13) {
                  if (var2.pos < var2.limit) {
                     var5 = var2.bytes[var2.pos++];
                     if (var5 == 10) {
                        var2.setReadPosition(0);
                        return var4.toString(var1);
                     }

                     --var2.pos;
                     var4.writeByte(13);
                  } else {
                     var4.writeByte(var5);
                  }
               } else {
                  if (var5 == 10) {
                     var2.setReadPosition(0);
                     return var4.toString(var1);
                  }

                  var4.writeByte(var5);
               }
            }
         } catch (IndexOutOfBoundsException var6) {
            var0.readerIndex(var3);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var6);
         }

         var0.readerIndex(var3);
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      }
   }

   private static String readDelimiterStandard(ByteBuf var0, String var1) {
      int var2 = var0.readerIndex();

      try {
         StringBuilder var3 = new StringBuilder(64);
         int var4 = 0;
         int var5 = var1.length();

         byte var6;
         while(var0.isReadable() && var4 < var5) {
            var6 = var0.readByte();
            if (var6 != var1.charAt(var4)) {
               var0.readerIndex(var2);
               throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }

            ++var4;
            var3.append((char)var6);
         }

         if (var0.isReadable()) {
            var6 = var0.readByte();
            if (var6 == 13) {
               var6 = var0.readByte();
               if (var6 == 10) {
                  return var3.toString();
               }

               var0.readerIndex(var2);
               throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }

            if (var6 == 10) {
               return var3.toString();
            }

            if (var6 == 45) {
               var3.append('-');
               var6 = var0.readByte();
               if (var6 == 45) {
                  var3.append('-');
                  if (var0.isReadable()) {
                     var6 = var0.readByte();
                     if (var6 == 13) {
                        var6 = var0.readByte();
                        if (var6 == 10) {
                           return var3.toString();
                        }

                        var0.readerIndex(var2);
                        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                     }

                     if (var6 == 10) {
                        return var3.toString();
                     }

                     var0.readerIndex(var0.readerIndex() - 1);
                     return var3.toString();
                  }

                  return var3.toString();
               }
            }
         }
      } catch (IndexOutOfBoundsException var7) {
         var0.readerIndex(var2);
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var7);
      }

      var0.readerIndex(var2);
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
   }

   private static String readDelimiter(ByteBuf var0, String var1) {
      if (!var0.hasArray()) {
         return readDelimiterStandard(var0, var1);
      } else {
         HttpPostBodyUtil.SeekAheadOptimize var2 = new HttpPostBodyUtil.SeekAheadOptimize(var0);
         int var3 = var0.readerIndex();
         int var4 = 0;
         int var5 = var1.length();

         try {
            StringBuilder var6 = new StringBuilder(64);

            while(true) {
               byte var7;
               if (var2.pos >= var2.limit || var4 >= var5) {
                  if (var2.pos >= var2.limit) {
                     break;
                  }

                  var7 = var2.bytes[var2.pos++];
                  if (var7 == 13) {
                     if (var2.pos < var2.limit) {
                        var7 = var2.bytes[var2.pos++];
                        if (var7 == 10) {
                           var2.setReadPosition(0);
                           return var6.toString();
                        }

                        var0.readerIndex(var3);
                        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                     }

                     var0.readerIndex(var3);
                     throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                  }

                  if (var7 == 10) {
                     var2.setReadPosition(0);
                     return var6.toString();
                  }

                  if (var7 == 45) {
                     var6.append('-');
                     if (var2.pos < var2.limit) {
                        var7 = var2.bytes[var2.pos++];
                        if (var7 == 45) {
                           var6.append('-');
                           if (var2.pos < var2.limit) {
                              var7 = var2.bytes[var2.pos++];
                              if (var7 == 13) {
                                 if (var2.pos < var2.limit) {
                                    var7 = var2.bytes[var2.pos++];
                                    if (var7 == 10) {
                                       var2.setReadPosition(0);
                                       return var6.toString();
                                    }

                                    var0.readerIndex(var3);
                                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                                 }

                                 var0.readerIndex(var3);
                                 throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                              }

                              if (var7 == 10) {
                                 var2.setReadPosition(0);
                                 return var6.toString();
                              }

                              var2.setReadPosition(1);
                              return var6.toString();
                           }

                           var2.setReadPosition(0);
                           return var6.toString();
                        }
                     }
                  }
                  break;
               }

               var7 = var2.bytes[var2.pos++];
               if (var7 != var1.charAt(var4)) {
                  var0.readerIndex(var3);
                  throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
               }

               ++var4;
               var6.append((char)var7);
            }
         } catch (IndexOutOfBoundsException var8) {
            var0.readerIndex(var3);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(var8);
         }

         var0.readerIndex(var3);
         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
      }
   }

   private static boolean loadDataMultipartStandard(ByteBuf var0, String var1, HttpData var2) {
      int var3 = var0.readerIndex();
      int var4 = var1.length();
      int var5 = 0;
      int var6 = var3;
      byte var7 = 10;
      boolean var8 = false;

      while(var0.isReadable()) {
         byte var9 = var0.readByte();
         if (var7 == 10 && var9 == var1.codePointAt(var5)) {
            ++var5;
            if (var4 == var5) {
               var8 = true;
               break;
            }
         } else {
            var6 = var0.readerIndex();
            if (var9 == 10) {
               var5 = 0;
               var6 -= var7 == 13 ? 2 : 1;
            }

            var7 = var9;
         }
      }

      if (var7 == 13) {
         --var6;
      }

      ByteBuf var12 = var0.copy(var3, var6 - var3);

      try {
         var2.addContent(var12, var8);
      } catch (IOException var11) {
         throw new HttpPostRequestDecoder.ErrorDataDecoderException(var11);
      }

      var0.readerIndex(var6);
      return var8;
   }

   private static boolean loadDataMultipart(ByteBuf var0, String var1, HttpData var2) {
      if (!var0.hasArray()) {
         return loadDataMultipartStandard(var0, var1, var2);
      } else {
         HttpPostBodyUtil.SeekAheadOptimize var3 = new HttpPostBodyUtil.SeekAheadOptimize(var0);
         int var4 = var0.readerIndex();
         int var5 = var1.length();
         int var6 = 0;
         int var7 = var3.pos;
         byte var8 = 10;
         boolean var9 = false;

         while(var3.pos < var3.limit) {
            byte var10 = var3.bytes[var3.pos++];
            if (var8 == 10 && var10 == var1.codePointAt(var6)) {
               ++var6;
               if (var5 == var6) {
                  var9 = true;
                  break;
               }
            } else {
               var7 = var3.pos;
               if (var10 == 10) {
                  var6 = 0;
                  var7 -= var8 == 13 ? 2 : 1;
               }

               var8 = var10;
            }
         }

         if (var8 == 13) {
            --var7;
         }

         int var14 = var3.getReadPosition(var7);
         ByteBuf var11 = var0.copy(var4, var14 - var4);

         try {
            var2.addContent(var11, var9);
         } catch (IOException var13) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var13);
         }

         var0.readerIndex(var14);
         return var9;
      }
   }

   private static String cleanString(String var0) {
      int var1 = var0.length();
      StringBuilder var2 = new StringBuilder(var1);

      for(int var3 = 0; var3 < var1; ++var3) {
         char var4 = var0.charAt(var3);
         switch(var4) {
         case '\t':
         case ',':
         case ':':
         case ';':
         case '=':
            var2.append(' ');
         case '"':
            break;
         default:
            var2.append(var4);
         }
      }

      return var2.toString().trim();
   }

   private boolean skipOneLine() {
      if (!this.undecodedChunk.isReadable()) {
         return false;
      } else {
         byte var1 = this.undecodedChunk.readByte();
         if (var1 == 13) {
            if (!this.undecodedChunk.isReadable()) {
               this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
               return false;
            } else {
               var1 = this.undecodedChunk.readByte();
               if (var1 == 10) {
                  return true;
               } else {
                  this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 2);
                  return false;
               }
            }
         } else if (var1 == 10) {
            return true;
         } else {
            this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
            return false;
         }
      }
   }

   private static String[] splitMultipartHeader(String var0) {
      ArrayList var1 = new ArrayList(1);
      int var2 = HttpPostBodyUtil.findNonWhitespace(var0, 0);

      int var3;
      for(var3 = var2; var3 < var0.length(); ++var3) {
         char var7 = var0.charAt(var3);
         if (var7 == ':' || Character.isWhitespace(var7)) {
            break;
         }
      }

      int var4;
      for(var4 = var3; var4 < var0.length(); ++var4) {
         if (var0.charAt(var4) == ':') {
            ++var4;
            break;
         }
      }

      int var5 = HttpPostBodyUtil.findNonWhitespace(var0, var4);
      int var6 = HttpPostBodyUtil.findEndOfString(var0);
      var1.add(var0.substring(var2, var3));
      String var13 = var5 >= var6 ? "" : var0.substring(var5, var6);
      String[] var8;
      if (var13.indexOf(59) >= 0) {
         var8 = splitMultipartHeaderValues(var13);
      } else {
         var8 = var13.split(",");
      }

      String[] var9 = var8;
      int var10 = var8.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         String var12 = var9[var11];
         var1.add(var12.trim());
      }

      var9 = new String[var1.size()];

      for(var10 = 0; var10 < var1.size(); ++var10) {
         var9[var10] = (String)var1.get(var10);
      }

      return var9;
   }

   private static String[] splitMultipartHeaderValues(String var0) {
      ArrayList var1 = InternalThreadLocalMap.get().arrayList(1);
      boolean var2 = false;
      boolean var3 = false;
      int var4 = 0;

      for(int var5 = 0; var5 < var0.length(); ++var5) {
         char var6 = var0.charAt(var5);
         if (var2) {
            if (var3) {
               var3 = false;
            } else if (var6 == '\\') {
               var3 = true;
            } else if (var6 == '"') {
               var2 = false;
            }
         } else if (var6 == '"') {
            var2 = true;
         } else if (var6 == ';') {
            var1.add(var0.substring(var4, var5));
            var4 = var5 + 1;
         }
      }

      var1.add(var0.substring(var4));
      return (String[])var1.toArray(new String[var1.size()]);
   }

   static {
      FILENAME_ENCODED = HttpHeaderValues.FILENAME.toString() + '*';
   }
}
