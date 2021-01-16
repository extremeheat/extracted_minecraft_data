package io.netty.handler.codec.compression;

import com.ning.compress.BufferRecycler;
import com.ning.compress.lzf.ChunkEncoder;
import com.ning.compress.lzf.LZFEncoder;
import com.ning.compress.lzf.util.ChunkEncoderFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class LzfEncoder extends MessageToByteEncoder<ByteBuf> {
   private static final int MIN_BLOCK_TO_COMPRESS = 16;
   private final ChunkEncoder encoder;
   private final BufferRecycler recycler;

   public LzfEncoder() {
      this(false, 65535);
   }

   public LzfEncoder(boolean var1) {
      this(var1, 65535);
   }

   public LzfEncoder(int var1) {
      this(false, var1);
   }

   public LzfEncoder(boolean var1, int var2) {
      super(false);
      if (var2 >= 16 && var2 <= 65535) {
         this.encoder = var1 ? ChunkEncoderFactory.safeNonAllocatingInstance(var2) : ChunkEncoderFactory.optimalNonAllocatingInstance(var2);
         this.recycler = BufferRecycler.instance();
      } else {
         throw new IllegalArgumentException("totalLength: " + var2 + " (expected: " + 16 + '-' + '\uffff' + ')');
      }
   }

   protected void encode(ChannelHandlerContext var1, ByteBuf var2, ByteBuf var3) throws Exception {
      int var4 = var2.readableBytes();
      int var5 = var2.readerIndex();
      byte[] var6;
      int var7;
      if (var2.hasArray()) {
         var6 = var2.array();
         var7 = var2.arrayOffset() + var5;
      } else {
         var6 = this.recycler.allocInputBuffer(var4);
         var2.getBytes(var5, (byte[])var6, 0, var4);
         var7 = 0;
      }

      int var8 = LZFEncoder.estimateMaxWorkspaceSize(var4);
      var3.ensureWritable(var8);
      byte[] var9 = var3.array();
      int var10 = var3.arrayOffset() + var3.writerIndex();
      int var11 = LZFEncoder.appendEncoded(this.encoder, var6, var7, var4, var9, var10) - var10;
      var3.writerIndex(var3.writerIndex() + var11);
      var2.skipBytes(var4);
      if (!var2.hasArray()) {
         this.recycler.releaseInputBuffer(var6);
      }

   }
}
