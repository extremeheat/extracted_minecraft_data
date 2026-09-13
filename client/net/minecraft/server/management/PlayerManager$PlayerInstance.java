package net.minecraft.server.management;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;

class PlayerManager$PlayerInstance {
   private final List field_73263_b;
   private final ChunkCoordIntPair field_73264_c;
   private short[] field_151254_d;
   private int field_73262_e;
   private int field_73260_f;
   private long field_111198_g;

   public PlayerManager$PlayerInstance(PlayerManager var1, int var2, int var3) {
      super();
      this.field_73265_a = var1;
      this.field_73263_b = new ArrayList();
      this.field_151254_d = new short[64];
      this.field_73264_c = new ChunkCoordIntPair(var2, var3);
      var1.func_72688_a().field_73059_b.func_73158_c(var2, var3);
   }

   public void func_73255_a(EntityPlayerMP var1) {
      if (this.field_73263_b.contains(var1)) {
         PlayerManager.access$000()
            .debug(
               "Failed to add player. {} already is in chunk {}, {}", new Object[]{var1, this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b}
            );
      } else {
         if (this.field_73263_b.isEmpty()) {
            this.field_111198_g = PlayerManager.access$100(this.field_73265_a).func_82737_E();
         }

         this.field_73263_b.add(var1);
         var1.field_71129_f.add(this.field_73264_c);
      }
   }

   public void func_73252_b(EntityPlayerMP var1) {
      if (this.field_73263_b.contains(var1)) {
         Chunk var2 = PlayerManager.access$100(this.field_73265_a).func_72964_e(this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b);
         if (var2.func_150802_k()) {
            var1.field_71135_a.func_147359_a(new S21PacketChunkData(var2, true, 0));
         }

         this.field_73263_b.remove(var1);
         var1.field_71129_f.remove(this.field_73264_c);
         if (this.field_73263_b.isEmpty()) {
            long var3 = (long)this.field_73264_c.field_77276_a + 2147483647L | (long)this.field_73264_c.field_77275_b + 2147483647L << 32;
            this.func_111196_a(var2);
            PlayerManager.access$200(this.field_73265_a).func_76159_d(var3);
            PlayerManager.access$300(this.field_73265_a).remove(this);
            if (this.field_73262_e > 0) {
               PlayerManager.access$400(this.field_73265_a).remove(this);
            }

            this.field_73265_a.func_72688_a().field_73059_b.func_73241_b(this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b);
         }
      }
   }

   public void func_111194_a() {
      this.func_111196_a(PlayerManager.access$100(this.field_73265_a).func_72964_e(this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b));
   }

   private void func_111196_a(Chunk var1) {
      var1.field_111204_q += PlayerManager.access$100(this.field_73265_a).func_82737_E() - this.field_111198_g;
      this.field_111198_g = PlayerManager.access$100(this.field_73265_a).func_82737_E();
   }

   public void func_151253_a(int var1, int var2, int var3) {
      if (this.field_73262_e == 0) {
         PlayerManager.access$400(this.field_73265_a).add(this);
      }

      this.field_73260_f |= 1 << (var2 >> 4);
      if (this.field_73262_e < 64) {
         short var4 = (short)(var1 << 12 | var3 << 8 | var2);

         for(int var5 = 0; var5 < this.field_73262_e; ++var5) {
            if (this.field_151254_d[var5] == var4) {
               return;
            }
         }

         this.field_151254_d[this.field_73262_e++] = var4;
      }
   }

   public void func_151251_a(Packet var1) {
      for(int var2 = 0; var2 < this.field_73263_b.size(); ++var2) {
         EntityPlayerMP var3 = (EntityPlayerMP)this.field_73263_b.get(var2);
         if (!var3.field_71129_f.contains(this.field_73264_c)) {
            var3.field_71135_a.func_147359_a(var1);
         }
      }
   }

   public void func_73254_a() {
      if (this.field_73262_e != 0) {
         if (this.field_73262_e == 1) {
            int var1 = this.field_73264_c.field_77276_a * 16 + (this.field_151254_d[0] >> 12 & 15);
            int var2 = this.field_151254_d[0] & 255;
            int var3 = this.field_73264_c.field_77275_b * 16 + (this.field_151254_d[0] >> 8 & 15);
            this.func_151251_a(new S23PacketBlockChange(var1, var2, var3, PlayerManager.access$100(this.field_73265_a)));
            if (PlayerManager.access$100(this.field_73265_a).func_147439_a(var1, var2, var3).func_149716_u()) {
               this.func_151252_a(PlayerManager.access$100(this.field_73265_a).func_147438_o(var1, var2, var3));
            }
         } else if (this.field_73262_e == 64) {
            int var7 = this.field_73264_c.field_77276_a * 16;
            int var9 = this.field_73264_c.field_77275_b * 16;
            this.func_151251_a(
               new S21PacketChunkData(
                  PlayerManager.access$100(this.field_73265_a).func_72964_e(this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b),
                  false,
                  this.field_73260_f
               )
            );

            for(int var11 = 0; var11 < 16; ++var11) {
               if ((this.field_73260_f & 1 << var11) != 0) {
                  int var4 = var11 << 4;
                  List var5 = PlayerManager.access$100(this.field_73265_a).func_147486_a(var7, var4, var9, var7 + 16, var4 + 16, var9 + 16);

                  for(int var6 = 0; var6 < var5.size(); ++var6) {
                     this.func_151252_a((TileEntity)var5.get(var6));
                  }
               }
            }
         } else {
            this.func_151251_a(
               new S22PacketMultiBlockChange(
                  this.field_73262_e,
                  this.field_151254_d,
                  PlayerManager.access$100(this.field_73265_a).func_72964_e(this.field_73264_c.field_77276_a, this.field_73264_c.field_77275_b)
               )
            );

            for(int var8 = 0; var8 < this.field_73262_e; ++var8) {
               int var10 = this.field_73264_c.field_77276_a * 16 + (this.field_151254_d[var8] >> 12 & 15);
               int var12 = this.field_151254_d[var8] & 255;
               int var13 = this.field_73264_c.field_77275_b * 16 + (this.field_151254_d[var8] >> 8 & 15);
               if (PlayerManager.access$100(this.field_73265_a).func_147439_a(var10, var12, var13).func_149716_u()) {
                  this.func_151252_a(PlayerManager.access$100(this.field_73265_a).func_147438_o(var10, var12, var13));
               }
            }
         }

         this.field_73262_e = 0;
         this.field_73260_f = 0;
      }
   }

   private void func_151252_a(TileEntity var1) {
      if (var1 != null) {
         Packet var2 = var1.func_145844_m();
         if (var2 != null) {
            this.func_151251_a(var2);
         }
      }
   }
}
