package net.minecraft.server.management;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerChunkMapEntry {
   private static final Logger field_187281_a = LogManager.getLogger();
   private final PlayerChunkMap field_187282_b;
   private final List<EntityPlayerMP> field_187283_c = Lists.newArrayList();
   private final ChunkPos field_187284_d;
   private final short[] field_187285_e = new short[64];
   @Nullable
   private Chunk field_187286_f;
   private int field_187287_g;
   private int field_187288_h;
   private long field_187289_i;
   private boolean field_187290_j;

   public PlayerChunkMapEntry(PlayerChunkMap var1, int var2, int var3) {
      super();
      this.field_187282_b = var1;
      this.field_187284_d = new ChunkPos(var2, var3);
      ChunkProviderServer var4 = var1.func_72688_a().func_72863_F();
      var4.func_212469_a(var2, var3);
      this.field_187286_f = var4.func_186025_d(var2, var3, true, false);
   }

   public ChunkPos func_187264_a() {
      return this.field_187284_d;
   }

   public void func_187276_a(EntityPlayerMP var1) {
      if (this.field_187283_c.contains(var1)) {
         field_187281_a.debug("Failed to add player. {} already is in chunk {}, {}", var1, this.field_187284_d.field_77276_a, this.field_187284_d.field_77275_b);
      } else {
         if (this.field_187283_c.isEmpty()) {
            this.field_187289_i = this.field_187282_b.func_72688_a().func_82737_E();
         }

         this.field_187283_c.add(var1);
         if (this.field_187290_j) {
            this.func_187278_c(var1);
         }

      }
   }

   public void func_187277_b(EntityPlayerMP var1) {
      if (this.field_187283_c.contains(var1)) {
         if (this.field_187290_j) {
            var1.field_71135_a.func_147359_a(new SPacketUnloadChunk(this.field_187284_d.field_77276_a, this.field_187284_d.field_77275_b));
         }

         this.field_187283_c.remove(var1);
         if (this.field_187283_c.isEmpty()) {
            this.field_187282_b.func_187305_b(this);
         }

      }
   }

   public boolean func_187268_a(boolean var1) {
      if (this.field_187286_f != null) {
         return true;
      } else {
         this.field_187286_f = this.field_187282_b.func_72688_a().func_72863_F().func_186025_d(this.field_187284_d.field_77276_a, this.field_187284_d.field_77275_b, true, var1);
         return this.field_187286_f != null;
      }
   }

   public boolean func_187272_b() {
      if (this.field_187290_j) {
         return true;
      } else if (this.field_187286_f == null) {
         return false;
      } else if (!this.field_187286_f.func_150802_k()) {
         return false;
      } else {
         this.field_187287_g = 0;
         this.field_187288_h = 0;
         this.field_187290_j = true;
         if (!this.field_187283_c.isEmpty()) {
            SPacketChunkData var1 = new SPacketChunkData(this.field_187286_f, 65535);
            Iterator var2 = this.field_187283_c.iterator();

            while(var2.hasNext()) {
               EntityPlayerMP var3 = (EntityPlayerMP)var2.next();
               var3.field_71135_a.func_147359_a(var1);
               this.field_187282_b.func_72688_a().func_73039_n().func_85172_a(var3, this.field_187286_f);
            }
         }

         return true;
      }
   }

   public void func_187278_c(EntityPlayerMP var1) {
      if (this.field_187290_j) {
         var1.field_71135_a.func_147359_a(new SPacketChunkData(this.field_187286_f, 65535));
         this.field_187282_b.func_72688_a().func_73039_n().func_85172_a(var1, this.field_187286_f);
      }
   }

   public void func_187279_c() {
      long var1 = this.field_187282_b.func_72688_a().func_82737_E();
      if (this.field_187286_f != null) {
         this.field_187286_f.func_177415_c(this.field_187286_f.func_177416_w() + var1 - this.field_187289_i);
      }

      this.field_187289_i = var1;
   }

   public void func_187265_a(int var1, int var2, int var3) {
      if (this.field_187290_j) {
         if (this.field_187287_g == 0) {
            this.field_187282_b.func_187304_a(this);
         }

         this.field_187288_h |= 1 << (var2 >> 4);
         if (this.field_187287_g < 64) {
            short var4 = (short)(var1 << 12 | var3 << 8 | var2);

            for(int var5 = 0; var5 < this.field_187287_g; ++var5) {
               if (this.field_187285_e[var5] == var4) {
                  return;
               }
            }

            this.field_187285_e[this.field_187287_g++] = var4;
         }

      }
   }

   public void func_187267_a(Packet<?> var1) {
      if (this.field_187290_j) {
         for(int var2 = 0; var2 < this.field_187283_c.size(); ++var2) {
            ((EntityPlayerMP)this.field_187283_c.get(var2)).field_71135_a.func_147359_a(var1);
         }

      }
   }

   public void func_187280_d() {
      if (this.field_187290_j && this.field_187286_f != null) {
         if (this.field_187287_g != 0) {
            int var1;
            int var2;
            int var3;
            if (this.field_187287_g == 1) {
               var1 = (this.field_187285_e[0] >> 12 & 15) + this.field_187284_d.field_77276_a * 16;
               var2 = this.field_187285_e[0] & 255;
               var3 = (this.field_187285_e[0] >> 8 & 15) + this.field_187284_d.field_77275_b * 16;
               BlockPos var4 = new BlockPos(var1, var2, var3);
               this.func_187267_a(new SPacketBlockChange(this.field_187282_b.func_72688_a(), var4));
               if (this.field_187282_b.func_72688_a().func_180495_p(var4).func_177230_c().func_149716_u()) {
                  this.func_187273_a(this.field_187282_b.func_72688_a().func_175625_s(var4));
               }
            } else if (this.field_187287_g == 64) {
               this.func_187267_a(new SPacketChunkData(this.field_187286_f, this.field_187288_h));
            } else {
               this.func_187267_a(new SPacketMultiBlockChange(this.field_187287_g, this.field_187285_e, this.field_187286_f));

               for(var1 = 0; var1 < this.field_187287_g; ++var1) {
                  var2 = (this.field_187285_e[var1] >> 12 & 15) + this.field_187284_d.field_77276_a * 16;
                  var3 = this.field_187285_e[var1] & 255;
                  int var6 = (this.field_187285_e[var1] >> 8 & 15) + this.field_187284_d.field_77275_b * 16;
                  BlockPos var5 = new BlockPos(var2, var3, var6);
                  if (this.field_187282_b.func_72688_a().func_180495_p(var5).func_177230_c().func_149716_u()) {
                     this.func_187273_a(this.field_187282_b.func_72688_a().func_175625_s(var5));
                  }
               }
            }

            this.field_187287_g = 0;
            this.field_187288_h = 0;
         }
      }
   }

   private void func_187273_a(@Nullable TileEntity var1) {
      if (var1 != null) {
         SPacketUpdateTileEntity var2 = var1.func_189518_D_();
         if (var2 != null) {
            this.func_187267_a(var2);
         }
      }

   }

   public boolean func_187275_d(EntityPlayerMP var1) {
      return this.field_187283_c.contains(var1);
   }

   public boolean func_187269_a(Predicate<EntityPlayerMP> var1) {
      return this.field_187283_c.stream().anyMatch(var1);
   }

   public boolean func_187271_a(double var1, Predicate<EntityPlayerMP> var3) {
      int var4 = 0;

      for(int var5 = this.field_187283_c.size(); var4 < var5; ++var4) {
         EntityPlayerMP var6 = (EntityPlayerMP)this.field_187283_c.get(var4);
         if (var3.test(var6) && this.field_187284_d.func_185327_a(var6) < var1 * var1) {
            return true;
         }
      }

      return false;
   }

   public boolean func_187274_e() {
      return this.field_187290_j;
   }

   @Nullable
   public Chunk func_187266_f() {
      return this.field_187286_f;
   }

   public double func_187270_g() {
      double var1 = 1.7976931348623157E308D;
      Iterator var3 = this.field_187283_c.iterator();

      while(var3.hasNext()) {
         EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
         double var5 = this.field_187284_d.func_185327_a(var4);
         if (var5 < var1) {
            var1 = var5;
         }
      }

      return var1;
   }
}
