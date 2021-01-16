package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpPostStandardRequestDecoder implements InterfaceHttpPostRequestDecoder {
   private final HttpDataFactory factory;
   private final HttpRequest request;
   private final Charset charset;
   private boolean isLastChunk;
   private final List<InterfaceHttpData> bodyListHttpData;
   private final Map<String, List<InterfaceHttpData>> bodyMapHttpData;
   private ByteBuf undecodedChunk;
   private int bodyListHttpDataRank;
   private HttpPostRequestDecoder.MultiPartStatus currentStatus;
   private Attribute currentAttribute;
   private boolean destroyed;
   private int discardThreshold;

   public HttpPostStandardRequestDecoder(HttpRequest var1) {
      this(new DefaultHttpDataFactory(16384L), var1, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostStandardRequestDecoder(HttpDataFactory var1, HttpRequest var2) {
      this(var1, var2, HttpConstants.DEFAULT_CHARSET);
   }

   public HttpPostStandardRequestDecoder(HttpDataFactory var1, HttpRequest var2, Charset var3) {
      super();
      this.bodyListHttpData = new ArrayList();
      this.bodyMapHttpData = new TreeMap(CaseIgnoringComparator.INSTANCE);
      this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
      this.discardThreshold = 10485760;
      this.request = (HttpRequest)ObjectUtil.checkNotNull(var2, "request");
      this.charset = (Charset)ObjectUtil.checkNotNull(var3, "charset");
      this.factory = (HttpDataFactory)ObjectUtil.checkNotNull(var1, "factory");
      if (var2 instanceof HttpContent) {
         this.offer((HttpContent)var2);
      } else {
         this.undecodedChunk = Unpooled.buffer();
         this.parseBody();
      }

   }

   private void checkDestroyed() {
      if (this.destroyed) {
         throw new IllegalStateException(HttpPostStandardRequestDecoder.class.getSimpleName() + " was destroyed already");
      }
   }

   public boolean isMultipart() {
      this.checkDestroyed();
      return false;
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

   public HttpPostStandardRequestDecoder offer(HttpContent var1) {
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
      return this.currentAttribute;
   }

   private void parseBody() {
      if (this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE && this.currentStatus != HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
         this.parseBodyAttributes();
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

   private void parseBodyAttributesStandard() {
      int var1 = this.undecodedChunk.readerIndex();
      int var2 = var1;
      if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
      }

      boolean var5 = true;

      try {
         while(this.undecodedChunk.isReadable() && var5) {
            char var6 = (char)this.undecodedChunk.readUnsignedByte();
            ++var2;
            int var4;
            switch(this.currentStatus) {
            case DISPOSITION:
               String var7;
               if (var6 == '=') {
                  this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                  int var3 = var2 - 1;
                  var7 = decodeAttribute(this.undecodedChunk.toString(var1, var3 - var1, this.charset), this.charset);
                  this.currentAttribute = this.factory.createAttribute(this.request, var7);
                  var1 = var2;
               } else if (var6 == '&') {
                  this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                  var4 = var2 - 1;
                  var7 = decodeAttribute(this.undecodedChunk.toString(var1, var4 - var1, this.charset), this.charset);
                  this.currentAttribute = this.factory.createAttribute(this.request, var7);
                  this.currentAttribute.setValue("");
                  this.addHttpData(this.currentAttribute);
                  this.currentAttribute = null;
                  var1 = var2;
                  var5 = true;
               }
               break;
            case FIELD:
               if (var6 == '&') {
                  this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                  var4 = var2 - 1;
                  this.setFinalBuffer(this.undecodedChunk.copy(var1, var4 - var1));
                  var1 = var2;
                  var5 = true;
               } else if (var6 == '\r') {
                  if (this.undecodedChunk.isReadable()) {
                     var6 = (char)this.undecodedChunk.readUnsignedByte();
                     ++var2;
                     if (var6 != '\n') {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
                     }

                     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                     var4 = var2 - 2;
                     this.setFinalBuffer(this.undecodedChunk.copy(var1, var4 - var1));
                     var1 = var2;
                     var5 = false;
                  } else {
                     --var2;
                  }
               } else if (var6 == '\n') {
                  this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                  var4 = var2 - 1;
                  this.setFinalBuffer(this.undecodedChunk.copy(var1, var4 - var1));
                  var1 = var2;
                  var5 = false;
               }
               break;
            default:
               var5 = false;
            }
         }

         if (this.isLastChunk && this.currentAttribute != null) {
            if (var2 > var1) {
               this.setFinalBuffer(this.undecodedChunk.copy(var1, var2 - var1));
            } else if (!this.currentAttribute.isCompleted()) {
               this.setFinalBuffer(Unpooled.EMPTY_BUFFER);
            }

            var1 = var2;
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
         } else if (var5 && this.currentAttribute != null && this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
            this.currentAttribute.addContent(this.undecodedChunk.copy(var1, var2 - var1), false);
            var1 = var2;
         }

         this.undecodedChunk.readerIndex(var1);
      } catch (HttpPostRequestDecoder.ErrorDataDecoderException var8) {
         this.undecodedChunk.readerIndex(var1);
         throw var8;
      } catch (IOException var9) {
         this.undecodedChunk.readerIndex(var1);
         throw new HttpPostRequestDecoder.ErrorDataDecoderException(var9);
      }
   }

   private void parseBodyAttributes() {
      if (!this.undecodedChunk.hasArray()) {
         this.parseBodyAttributesStandard();
      } else {
         HttpPostBodyUtil.SeekAheadOptimize var1 = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
         int var2 = this.undecodedChunk.readerIndex();
         int var3 = var2;
         if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
         }

         boolean var6 = true;

         try {
            label81:
            while(var1.pos < var1.limit) {
               char var7 = (char)(var1.bytes[var1.pos++] & 255);
               ++var3;
               int var5;
               switch(this.currentStatus) {
               case DISPOSITION:
                  String var8;
                  if (var7 == '=') {
                     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                     int var4 = var3 - 1;
                     var8 = decodeAttribute(this.undecodedChunk.toString(var2, var4 - var2, this.charset), this.charset);
                     this.currentAttribute = this.factory.createAttribute(this.request, var8);
                     var2 = var3;
                  } else if (var7 == '&') {
                     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                     var5 = var3 - 1;
                     var8 = decodeAttribute(this.undecodedChunk.toString(var2, var5 - var2, this.charset), this.charset);
                     this.currentAttribute = this.factory.createAttribute(this.request, var8);
                     this.currentAttribute.setValue("");
                     this.addHttpData(this.currentAttribute);
                     this.currentAttribute = null;
                     var2 = var3;
                     var6 = true;
                  }
                  break;
               case FIELD:
                  if (var7 == '&') {
                     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                     var5 = var3 - 1;
                     this.setFinalBuffer(this.undecodedChunk.copy(var2, var5 - var2));
                     var2 = var3;
                     var6 = true;
                  } else if (var7 == '\r') {
                     if (var1.pos < var1.limit) {
                        var7 = (char)(var1.bytes[var1.pos++] & 255);
                        ++var3;
                        if (var7 != '\n') {
                           var1.setReadPosition(0);
                           throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
                        }

                        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                        var5 = var3 - 2;
                        var1.setReadPosition(0);
                        this.setFinalBuffer(this.undecodedChunk.copy(var2, var5 - var2));
                        var2 = var3;
                        var6 = false;
                        break label81;
                     }

                     if (var1.limit > 0) {
                        --var3;
                     }
                  } else {
                     if (var7 != '\n') {
                        continue;
                     }

                     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                     var5 = var3 - 1;
                     var1.setReadPosition(0);
                     this.setFinalBuffer(this.undecodedChunk.copy(var2, var5 - var2));
                     var2 = var3;
                     var6 = false;
                     break label81;
                  }
                  break;
               default:
                  var1.setReadPosition(0);
                  var6 = false;
                  break label81;
               }
            }

            if (this.isLastChunk && this.currentAttribute != null) {
               if (var3 > var2) {
                  this.setFinalBuffer(this.undecodedChunk.copy(var2, var3 - var2));
               } else if (!this.currentAttribute.isCompleted()) {
                  this.setFinalBuffer(Unpooled.EMPTY_BUFFER);
               }

               var2 = var3;
               this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
            } else if (var6 && this.currentAttribute != null && this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
               this.currentAttribute.addContent(this.undecodedChunk.copy(var2, var3 - var2), false);
               var2 = var3;
            }

            this.undecodedChunk.readerIndex(var2);
         } catch (HttpPostRequestDecoder.ErrorDataDecoderException var9) {
            this.undecodedChunk.readerIndex(var2);
            throw var9;
         } catch (IOException var10) {
            this.undecodedChunk.readerIndex(var2);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var10);
         } catch (IllegalArgumentException var11) {
            this.undecodedChunk.readerIndex(var2);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(var11);
         }
      }
   }

   private void setFinalBuffer(ByteBuf var1) throws IOException {
      this.currentAttribute.addContent(var1, true);
      String var2 = decodeAttribute(this.currentAttribute.getByteBuf().toString(this.charset), this.charset);
      this.currentAttribute.setValue(var2);
      this.addHttpData(this.currentAttribute);
      this.currentAttribute = null;
   }

   private static String decodeAttribute(String var0, Charset var1) {
      try {
         return QueryStringDecoder.decodeComponent(var0, var1);
      } catch (IllegalArgumentException var3) {
         throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad string: '" + var0 + '\'', var3);
      }
   }

   public void destroy() {
      this.cleanFiles();
      this.destroyed = true;
      if (this.undecodedChunk != null && this.undecodedChunk.refCnt() > 0) {
         this.undecodedChunk.release();
         this.undecodedChunk = null;
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
}
