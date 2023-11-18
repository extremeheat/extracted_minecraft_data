package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class CompressionDecoder extends ByteToMessageDecoder {
   public static final int MAXIMUM_COMPRESSED_LENGTH = 2097152;
   public static final int MAXIMUM_UNCOMPRESSED_LENGTH = 8388608;
   private final Inflater inflater;
   private int threshold;
   private boolean validateDecompressed;

   public CompressionDecoder(int var1, boolean var2) {
      super();
      this.threshold = var1;
      this.validateDecompressed = var2;
      this.inflater = new Inflater();
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (var2.readableBytes() != 0) {
         int var4 = VarInt.read(var2);
         if (var4 == 0) {
            var3.add(var2.readBytes(var2.readableBytes()));
         } else {
            if (this.validateDecompressed) {
               if (var4 < this.threshold) {
                  throw new DecoderException("Badly compressed packet - size of " + var4 + " is below server threshold of " + this.threshold);
               }

               if (var4 > 8388608) {
                  throw new DecoderException("Badly compressed packet - size of " + var4 + " is larger than protocol maximum of 8388608");
               }
            }

            this.setupInflaterInput(var2);
            ByteBuf var5 = this.inflate(var1, var4);
            this.inflater.reset();
            var3.add(var5);
         }
      }
   }

   private void setupInflaterInput(ByteBuf var1) {
      ByteBuffer var2;
      if (var1.nioBufferCount() > 0) {
         var2 = var1.nioBuffer();
         var1.skipBytes(var1.readableBytes());
      } else {
         var2 = ByteBuffer.allocateDirect(var1.readableBytes());
         var1.readBytes(var2);
         var2.flip();
      }

      this.inflater.setInput(var2);
   }

   private ByteBuf inflate(ChannelHandlerContext var1, int var2) throws DataFormatException {
      ByteBuf var3 = var1.alloc().directBuffer(var2);

      try {
         ByteBuffer var4 = var3.internalNioBuffer(0, var2);
         int var5 = var4.position();
         this.inflater.inflate(var4);
         int var6 = var4.position() - var5;
         if (var6 != var2) {
            throw new DecoderException("Badly compressed packet - actual length of uncompressed payload " + var6 + " is does not match declared size " + var2);
         } else {
            var3.writerIndex(var3.writerIndex() + var6);
            return var3;
         }
      } catch (Exception var7) {
         var3.release();
         throw var7;
      }
   }

   public void setThreshold(int var1, boolean var2) {
      this.threshold = var1;
      this.validateDecompressed = var2;
   }
}
