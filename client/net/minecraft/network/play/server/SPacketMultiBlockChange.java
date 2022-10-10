package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

public class SPacketMultiBlockChange implements Packet<INetHandlerPlayClient> {
   private ChunkPos field_148925_b;
   private SPacketMultiBlockChange.BlockUpdateData[] field_179845_b;

   public SPacketMultiBlockChange() {
      super();
   }

   public SPacketMultiBlockChange(int var1, short[] var2, Chunk var3) {
      super();
      this.field_148925_b = new ChunkPos(var3.field_76635_g, var3.field_76647_h);
      this.field_179845_b = new SPacketMultiBlockChange.BlockUpdateData[var1];

      for(int var4 = 0; var4 < this.field_179845_b.length; ++var4) {
         this.field_179845_b[var4] = new SPacketMultiBlockChange.BlockUpdateData(var2[var4], var3);
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_148925_b = new ChunkPos(var1.readInt(), var1.readInt());
      this.field_179845_b = new SPacketMultiBlockChange.BlockUpdateData[var1.func_150792_a()];

      for(int var2 = 0; var2 < this.field_179845_b.length; ++var2) {
         this.field_179845_b[var2] = new SPacketMultiBlockChange.BlockUpdateData(var1.readShort(), (IBlockState)Block.field_176229_d.func_148745_a(var1.func_150792_a()));
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeInt(this.field_148925_b.field_77276_a);
      var1.writeInt(this.field_148925_b.field_77275_b);
      var1.func_150787_b(this.field_179845_b.length);
      SPacketMultiBlockChange.BlockUpdateData[] var2 = this.field_179845_b;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SPacketMultiBlockChange.BlockUpdateData var5 = var2[var4];
         var1.writeShort(var5.func_180089_b());
         var1.func_150787_b(Block.func_196246_j(var5.func_180088_c()));
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147287_a(this);
   }

   public SPacketMultiBlockChange.BlockUpdateData[] func_179844_a() {
      return this.field_179845_b;
   }

   public class BlockUpdateData {
      private final short field_180091_b;
      private final IBlockState field_180092_c;

      public BlockUpdateData(short var2, IBlockState var3) {
         super();
         this.field_180091_b = var2;
         this.field_180092_c = var3;
      }

      public BlockUpdateData(short var2, Chunk var3) {
         super();
         this.field_180091_b = var2;
         this.field_180092_c = var3.func_180495_p(this.func_180090_a());
      }

      public BlockPos func_180090_a() {
         return new BlockPos(SPacketMultiBlockChange.this.field_148925_b.func_180331_a(this.field_180091_b >> 12 & 15, this.field_180091_b & 255, this.field_180091_b >> 8 & 15));
      }

      public short func_180089_b() {
         return this.field_180091_b;
      }

      public IBlockState func_180088_c() {
         return this.field_180092_c;
      }
   }
}
