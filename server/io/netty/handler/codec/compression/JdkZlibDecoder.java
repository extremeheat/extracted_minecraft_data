package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class JdkZlibDecoder extends ZlibDecoder {
   private static final int FHCRC = 2;
   private static final int FEXTRA = 4;
   private static final int FNAME = 8;
   private static final int FCOMMENT = 16;
   private static final int FRESERVED = 224;
   private Inflater inflater;
   private final byte[] dictionary;
   private final ByteBufChecksum crc;
   private final boolean decompressConcatenated;
   private JdkZlibDecoder.GzipState gzipState;
   private int flags;
   private int xlen;
   private volatile boolean finished;
   private boolean decideZlibOrNone;

   public JdkZlibDecoder() {
      this(ZlibWrapper.ZLIB, (byte[])null, false);
   }

   public JdkZlibDecoder(byte[] var1) {
      this(ZlibWrapper.ZLIB, var1, false);
   }

   public JdkZlibDecoder(ZlibWrapper var1) {
      this(var1, (byte[])null, false);
   }

   public JdkZlibDecoder(ZlibWrapper var1, boolean var2) {
      this(var1, (byte[])null, var2);
   }

   public JdkZlibDecoder(boolean var1) {
      this(ZlibWrapper.GZIP, (byte[])null, var1);
   }

   private JdkZlibDecoder(ZlibWrapper var1, byte[] var2, boolean var3) {
      super();
      this.gzipState = JdkZlibDecoder.GzipState.HEADER_START;
      this.flags = -1;
      this.xlen = -1;
      if (var1 == null) {
         throw new NullPointerException("wrapper");
      } else {
         this.decompressConcatenated = var3;
         switch(var1) {
         case GZIP:
            this.inflater = new Inflater(true);
            this.crc = ByteBufChecksum.wrapChecksum(new CRC32());
            break;
         case NONE:
            this.inflater = new Inflater(true);
            this.crc = null;
            break;
         case ZLIB:
            this.inflater = new Inflater();
            this.crc = null;
            break;
         case ZLIB_OR_NONE:
            this.decideZlibOrNone = true;
            this.crc = null;
            break;
         default:
            throw new IllegalArgumentException("Only GZIP or ZLIB is supported, but you used " + var1);
         }

         this.dictionary = var2;
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
            if (this.decideZlibOrNone) {
               if (var4 < 2) {
                  return;
               }

               boolean var5 = !looksLikeZlib(var2.getShort(var2.readerIndex()));
               this.inflater = new Inflater(var5);
               this.decideZlibOrNone = false;
            }

            if (this.crc != null) {
               switch(this.gzipState) {
               case FOOTER_START:
                  if (this.readGZIPFooter(var2)) {
                     this.finished = true;
                  }

                  return;
               default:
                  if (this.gzipState != JdkZlibDecoder.GzipState.HEADER_END && !this.readGZIPHeader(var2)) {
                     return;
                  }

                  var4 = var2.readableBytes();
               }
            }

            if (var2.hasArray()) {
               this.inflater.setInput(var2.array(), var2.arrayOffset() + var2.readerIndex(), var4);
            } else {
               byte[] var16 = new byte[var4];
               var2.getBytes(var2.readerIndex(), var16);
               this.inflater.setInput(var16);
            }

            ByteBuf var17 = var1.alloc().heapBuffer(this.inflater.getRemaining() << 1);

            try {
               boolean var6 = false;

               while(true) {
                  if (!this.inflater.needsInput()) {
                     byte[] var7 = var17.array();
                     int var8 = var17.writerIndex();
                     int var9 = var17.arrayOffset() + var8;
                     int var10 = this.inflater.inflate(var7, var9, var17.writableBytes());
                     if (var10 > 0) {
                        var17.writerIndex(var8 + var10);
                        if (this.crc != null) {
                           this.crc.update(var7, var9, var10);
                        }
                     } else if (this.inflater.needsDictionary()) {
                        if (this.dictionary == null) {
                           throw new DecompressionException("decompression failure, unable to set dictionary as non was specified");
                        }

                        this.inflater.setDictionary(this.dictionary);
                     }

                     if (!this.inflater.finished()) {
                        var17.ensureWritable(this.inflater.getRemaining() << 1);
                        continue;
                     }

                     if (this.crc == null) {
                        this.finished = true;
                     } else {
                        var6 = true;
                     }
                  }

                  var2.skipBytes(var4 - this.inflater.getRemaining());
                  if (!var6) {
                     break;
                  }

                  this.gzipState = JdkZlibDecoder.GzipState.FOOTER_START;
                  if (this.readGZIPFooter(var2)) {
                     this.finished = !this.decompressConcatenated;
                     if (!this.finished) {
                        this.inflater.reset();
                        this.crc.reset();
                        this.gzipState = JdkZlibDecoder.GzipState.HEADER_START;
                     }
                  }
                  break;
               }
            } catch (DataFormatException var14) {
               throw new DecompressionException("decompression failure", var14);
            } finally {
               if (var17.isReadable()) {
                  var3.add(var17);
               } else {
                  var17.release();
               }

            }

         }
      }
   }

   protected void handlerRemoved0(ChannelHandlerContext var1) throws Exception {
      super.handlerRemoved0(var1);
      if (this.inflater != null) {
         this.inflater.end();
      }

   }

   private boolean readGZIPHeader(ByteBuf var1) {
      short var5;
      switch(this.gzipState) {
      case HEADER_START:
         if (var1.readableBytes() < 10) {
            return false;
         }

         byte var2 = var1.readByte();
         byte var3 = var1.readByte();
         if (var2 != 31) {
            throw new DecompressionException("Input is not in the GZIP format");
         }

         this.crc.update(var2);
         this.crc.update(var3);
         short var4 = var1.readUnsignedByte();
         if (var4 != 8) {
            throw new DecompressionException("Unsupported compression method " + var4 + " in the GZIP header");
         }

         this.crc.update(var4);
         this.flags = var1.readUnsignedByte();
         this.crc.update(this.flags);
         if ((this.flags & 224) != 0) {
            throw new DecompressionException("Reserved flags are set in the GZIP header");
         }

         this.crc.update(var1, var1.readerIndex(), 4);
         var1.skipBytes(4);
         this.crc.update(var1.readUnsignedByte());
         this.crc.update(var1.readUnsignedByte());
         this.gzipState = JdkZlibDecoder.GzipState.FLG_READ;
      case FLG_READ:
         if ((this.flags & 4) != 0) {
            if (var1.readableBytes() < 2) {
               return false;
            }

            var5 = var1.readUnsignedByte();
            short var6 = var1.readUnsignedByte();
            this.crc.update(var5);
            this.crc.update(var6);
            this.xlen |= var5 << 8 | var6;
         }

         this.gzipState = JdkZlibDecoder.GzipState.XLEN_READ;
      case XLEN_READ:
         if (this.xlen != -1) {
            if (var1.readableBytes() < this.xlen) {
               return false;
            }

            this.crc.update(var1, var1.readerIndex(), this.xlen);
            var1.skipBytes(this.xlen);
         }

         this.gzipState = JdkZlibDecoder.GzipState.SKIP_FNAME;
      case SKIP_FNAME:
         if ((this.flags & 8) != 0) {
            if (!var1.isReadable()) {
               return false;
            }

            do {
               var5 = var1.readUnsignedByte();
               this.crc.update(var5);
            } while(var5 != 0 && var1.isReadable());
         }

         this.gzipState = JdkZlibDecoder.GzipState.SKIP_COMMENT;
      case SKIP_COMMENT:
         if ((this.flags & 16) != 0) {
            if (!var1.isReadable()) {
               return false;
            }

            do {
               var5 = var1.readUnsignedByte();
               this.crc.update(var5);
            } while(var5 != 0 && var1.isReadable());
         }

         this.gzipState = JdkZlibDecoder.GzipState.PROCESS_FHCRC;
      case PROCESS_FHCRC:
         break;
      case HEADER_END:
         return true;
      default:
         throw new IllegalStateException();
      }

      if ((this.flags & 2) != 0) {
         if (var1.readableBytes() < 4) {
            return false;
         }

         this.verifyCrc(var1);
      }

      this.crc.reset();
      this.gzipState = JdkZlibDecoder.GzipState.HEADER_END;
      return true;
   }

   private boolean readGZIPFooter(ByteBuf var1) {
      if (var1.readableBytes() < 8) {
         return false;
      } else {
         this.verifyCrc(var1);
         int var2 = 0;

         int var3;
         for(var3 = 0; var3 < 4; ++var3) {
            var2 |= var1.readUnsignedByte() << var3 * 8;
         }

         var3 = this.inflater.getTotalOut();
         if (var2 != var3) {
            throw new DecompressionException("Number of bytes mismatch. Expected: " + var2 + ", Got: " + var3);
         } else {
            return true;
         }
      }
   }

   private void verifyCrc(ByteBuf var1) {
      long var2 = 0L;

      for(int var4 = 0; var4 < 4; ++var4) {
         var2 |= (long)var1.readUnsignedByte() << var4 * 8;
      }

      long var6 = this.crc.getValue();
      if (var2 != var6) {
         throw new DecompressionException("CRC value mismatch. Expected: " + var2 + ", Got: " + var6);
      }
   }

   private static boolean looksLikeZlib(short var0) {
      return (var0 & 30720) == 30720 && var0 % 31 == 0;
   }

   private static enum GzipState {
      HEADER_START,
      HEADER_END,
      FLG_READ,
      XLEN_READ,
      SKIP_FNAME,
      SKIP_COMMENT,
      PROCESS_FHCRC,
      FOOTER_START;

      private GzipState() {
      }
   }
}
