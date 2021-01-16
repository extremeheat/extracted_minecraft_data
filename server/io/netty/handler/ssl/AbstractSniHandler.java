package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.List;
import java.util.Locale;

public abstract class AbstractSniHandler<T> extends ByteToMessageDecoder implements ChannelOutboundHandler {
   private static final int MAX_SSL_RECORDS = 4;
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractSniHandler.class);
   private boolean handshakeFailed;
   private boolean suppressRead;
   private boolean readPending;

   public AbstractSniHandler() {
      super();
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (!this.suppressRead && !this.handshakeFailed) {
         int var4 = var2.writerIndex();

         try {
            int var5 = 0;

            label105:
            while(var5 < 4) {
               int var6 = var2.readerIndex();
               int var7 = var4 - var6;
               if (var7 < 5) {
                  return;
               }

               short var8 = var2.getUnsignedByte(var6);
               switch(var8) {
               case 20:
               case 21:
                  int var9 = SslUtils.getEncryptedPacketLength(var2, var6);
                  if (var9 == -2) {
                     this.handshakeFailed = true;
                     NotSslRecordException var28 = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(var2));
                     var2.skipBytes(var2.readableBytes());
                     var1.fireUserEventTriggered(new SniCompletionEvent(var28));
                     SslUtils.handleHandshakeFailure(var1, var28, true);
                     throw var28;
                  }

                  if (var9 != -1 && var4 - var6 - 5 >= var9) {
                     var2.skipBytes(var9);
                     ++var5;
                     break;
                  }

                  return;
               case 22:
                  short var10 = var2.getUnsignedByte(var6 + 1);
                  if (var10 == 3) {
                     int var11 = var2.getUnsignedShort(var6 + 3) + 5;
                     if (var7 < var11) {
                        return;
                     }

                     int var12 = var6 + var11;
                     int var13 = var6 + 43;
                     if (var12 - var13 >= 6) {
                        short var14 = var2.getUnsignedByte(var13);
                        var13 += var14 + 1;
                        int var15 = var2.getUnsignedShort(var13);
                        var13 += var15 + 2;
                        short var16 = var2.getUnsignedByte(var13);
                        var13 += var16 + 1;
                        int var17 = var2.getUnsignedShort(var13);
                        var13 += 2;
                        int var18 = var13 + var17;
                        if (var18 <= var12) {
                           while(var18 - var13 >= 4) {
                              int var19 = var2.getUnsignedShort(var13);
                              var13 += 2;
                              int var20 = var2.getUnsignedShort(var13);
                              var13 += 2;
                              if (var18 - var13 < var20) {
                                 break label105;
                              }

                              if (var19 == 0) {
                                 var13 += 2;
                                 if (var18 - var13 >= 3) {
                                    short var21 = var2.getUnsignedByte(var13);
                                    ++var13;
                                    if (var21 == 0) {
                                       int var22 = var2.getUnsignedShort(var13);
                                       var13 += 2;
                                       if (var18 - var13 >= var22) {
                                          String var23 = var2.toString(var13, var22, CharsetUtil.US_ASCII);

                                          try {
                                             this.select(var1, var23.toLowerCase(Locale.US));
                                          } catch (Throwable var25) {
                                             PlatformDependent.throwException(var25);
                                          }

                                          return;
                                       }
                                    }
                                 }
                                 break label105;
                              }

                              var13 += var20;
                           }
                        }
                     }
                  }
               default:
                  break label105;
               }
            }
         } catch (NotSslRecordException var26) {
            throw var26;
         } catch (Exception var27) {
            if (logger.isDebugEnabled()) {
               logger.debug("Unexpected client hello packet: " + ByteBufUtil.hexDump(var2), (Throwable)var27);
            }
         }

         this.select(var1, (String)null);
      }

   }

   private void select(final ChannelHandlerContext var1, final String var2) throws Exception {
      Future var3 = this.lookup(var1, var2);
      if (var3.isDone()) {
         this.fireSniCompletionEvent(var1, var2, var3);
         this.onLookupComplete(var1, var2, var3);
      } else {
         this.suppressRead = true;
         var3.addListener(new FutureListener<T>() {
            public void operationComplete(Future<T> var1x) throws Exception {
               try {
                  AbstractSniHandler.this.suppressRead = false;

                  try {
                     AbstractSniHandler.this.fireSniCompletionEvent(var1, var2, var1x);
                     AbstractSniHandler.this.onLookupComplete(var1, var2, var1x);
                  } catch (DecoderException var8) {
                     var1.fireExceptionCaught(var8);
                  } catch (Exception var9) {
                     var1.fireExceptionCaught(new DecoderException(var9));
                  } catch (Throwable var10) {
                     var1.fireExceptionCaught(var10);
                  }
               } finally {
                  if (AbstractSniHandler.this.readPending) {
                     AbstractSniHandler.this.readPending = false;
                     var1.read();
                  }

               }

            }
         });
      }

   }

   private void fireSniCompletionEvent(ChannelHandlerContext var1, String var2, Future<T> var3) {
      Throwable var4 = var3.cause();
      if (var4 == null) {
         var1.fireUserEventTriggered(new SniCompletionEvent(var2));
      } else {
         var1.fireUserEventTriggered(new SniCompletionEvent(var2, var4));
      }

   }

   protected abstract Future<T> lookup(ChannelHandlerContext var1, String var2) throws Exception;

   protected abstract void onLookupComplete(ChannelHandlerContext var1, String var2, Future<T> var3) throws Exception;

   public void read(ChannelHandlerContext var1) throws Exception {
      if (this.suppressRead) {
         this.readPending = true;
      } else {
         var1.read();
      }

   }

   public void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) throws Exception {
      var1.bind(var2, var3);
   }

   public void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception {
      var1.connect(var2, var3, var4);
   }

   public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.disconnect(var2);
   }

   public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.close(var2);
   }

   public void deregister(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.deregister(var2);
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      var1.write(var2, var3);
   }

   public void flush(ChannelHandlerContext var1) throws Exception {
      var1.flush();
   }
}
