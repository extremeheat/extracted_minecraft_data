package io.netty.handler.codec.compression;

import com.jcraft.jzlib.Inflater;
import com.jcraft.jzlib.JZlib;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;

public class JZlibDecoder extends ZlibDecoder {
   private final Inflater z;
   private byte[] dictionary;
   private volatile boolean finished;

   public JZlibDecoder() {
      this(ZlibWrapper.ZLIB);
   }

   public JZlibDecoder(ZlibWrapper var1) {
      super();
      this.z = new Inflater();
      if (var1 == null) {
         throw new NullPointerException("wrapper");
      } else {
         int var2 = this.z.init(ZlibUtil.convertWrapperType(var1));
         if (var2 != 0) {
            ZlibUtil.fail(this.z, "initialization failure", var2);
         }

      }
   }

   public JZlibDecoder(byte[] var1) {
      super();
      this.z = new Inflater();
      if (var1 == null) {
         throw new NullPointerException("dictionary");
      } else {
         this.dictionary = var1;
         int var2 = this.z.inflateInit(JZlib.W_ZLIB);
         if (var2 != 0) {
            ZlibUtil.fail(this.z, "initialization failure", var2);
         }

      }
   }

   public boolean isClosed() {
      return this.finished;
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (this.finished) {
         var2.skipBytes(var2.readableBytes());
      } else {
         int var4 = var2.readableBytes();
         if (var4 != 0) {
            try {
               this.z.avail_in = var4;
               if (var2.hasArray()) {
                  this.z.next_in = var2.array();
                  this.z.next_in_index = var2.arrayOffset() + var2.readerIndex();
               } else {
                  byte[] var5 = new byte[var4];
                  var2.getBytes(var2.readerIndex(), var5);
                  this.z.next_in = var5;
                  this.z.next_in_index = 0;
               }

               int var18 = this.z.next_in_index;
               ByteBuf var6 = var1.alloc().heapBuffer(var4 << 1);

               try {
                  while(true) {
                     var6.ensureWritable(this.z.avail_in << 1);
                     this.z.avail_out = var6.writableBytes();
                     this.z.next_out = var6.array();
                     this.z.next_out_index = var6.arrayOffset() + var6.writerIndex();
                     int var7 = this.z.next_out_index;
                     int var8 = this.z.inflate(2);
                     int var9 = this.z.next_out_index - var7;
                     if (var9 > 0) {
                        var6.writerIndex(var6.writerIndex() + var9);
                     }

                     switch(var8) {
                     case -5:
                        if (this.z.avail_in <= 0) {
                           return;
                        }
                        break;
                     case -4:
                     case -3:
                     case -2:
                     case -1:
                     default:
                        ZlibUtil.fail(this.z, "decompression failure", var8);
                     case 0:
                        break;
                     case 1:
                        this.finished = true;
                        this.z.inflateEnd();
                        return;
                     case 2:
                        if (this.dictionary == null) {
                           ZlibUtil.fail(this.z, "decompression failure", var8);
                        } else {
                           var8 = this.z.inflateSetDictionary(this.dictionary, this.dictionary.length);
                           if (var8 != 0) {
                              ZlibUtil.fail(this.z, "failed to set the dictionary", var8);
                           }
                        }
                     }
                  }
               } finally {
                  var2.skipBytes(this.z.next_in_index - var18);
                  if (var6.isReadable()) {
                     var3.add(var6);
                  } else {
                     var6.release();
                  }

               }
            } finally {
               this.z.next_in = null;
               this.z.next_out = null;
            }
         }
      }
   }
}
