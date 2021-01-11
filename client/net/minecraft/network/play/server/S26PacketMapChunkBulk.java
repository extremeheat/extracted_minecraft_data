package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;

public class S26PacketMapChunkBulk implements Packet<INetHandlerPlayClient> {
   private int[] field_149266_a;
   private int[] field_149264_b;
   private S21PacketChunkData.Extracted[] field_179755_c;
   private boolean field_149267_h;

   public S26PacketMapChunkBulk() {
      super();
   }

   public S26PacketMapChunkBulk(List<Chunk> var1) {
      super();
      int var2 = var1.size();
      this.field_149266_a = new int[var2];
      this.field_149264_b = new int[var2];
      this.field_179755_c = new S21PacketChunkData.Extracted[var2];
      this.field_149267_h = !((Chunk)var1.get(0)).func_177412_p().field_73011_w.func_177495_o();

      for(int var3 = 0; var3 < var2; ++var3) {
         Chunk var4 = (Chunk)var1.get(var3);
         S21PacketChunkData.Extracted var5 = S21PacketChunkData.func_179756_a(var4, true, this.field_149267_h, 65535);
         this.field_149266_a[var3] = var4.field_76635_g;
         this.field_149264_b[var3] = var4.field_76647_h;
         this.field_179755_c[var3] = var5;
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149267_h = var1.readBoolean();
      int var2 = var1.func_150792_a();
      this.field_149266_a = new int[var2];
      this.field_149264_b = new int[var2];
      this.field_179755_c = new S21PacketChunkData.Extracted[var2];

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         this.field_149266_a[var3] = var1.readInt();
         this.field_149264_b[var3] = var1.readInt();
         this.field_179755_c[var3] = new S21PacketChunkData.Extracted();
         this.field_179755_c[var3].field_150280_b = var1.readShort() & '\uffff';
         this.field_179755_c[var3].field_150282_a = new byte[S21PacketChunkData.func_180737_a(Integer.bitCount(this.field_179755_c[var3].field_150280_b), this.field_149267_h, true)];
      }

      for(var3 = 0; var3 < var2; ++var3) {
         var1.readBytes(this.field_179755_c[var3].field_150282_a);
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeBoolean(this.field_149267_h);
      var1.func_150787_b(this.field_179755_c.length);

      int var2;
      for(var2 = 0; var2 < this.field_149266_a.length; ++var2) {
         var1.writeInt(this.field_149266_a[var2]);
         var1.writeInt(this.field_149264_b[var2]);
         var1.writeShort((short)(this.field_179755_c[var2].field_150280_b & '\uffff'));
      }

      for(var2 = 0; var2 < this.field_149266_a.length; ++var2) {
         var1.writeBytes(this.field_179755_c[var2].field_150282_a);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147269_a(this);
   }

   public int func_149255_a(int var1) {
      return this.field_149266_a[var1];
   }

   public int func_149253_b(int var1) {
      return this.field_149264_b[var1];
   }

   public int func_149254_d() {
      return this.field_149266_a.length;
   }

   public byte[] func_149256_c(int var1) {
      return this.field_179755_c[var1].field_150282_a;
   }

   public int func_179754_d(int var1) {
      return this.field_179755_c[var1].field_150280_b;
   }
}
