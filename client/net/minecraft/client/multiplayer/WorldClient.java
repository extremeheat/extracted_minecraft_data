package net.minecraft.client.multiplayer;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EntityFireworkStarterFX;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveHandlerMP;

public class WorldClient extends World {
   private NetHandlerPlayClient field_73035_a;
   private ChunkProviderClient field_73033_b;
   private IntHashMap field_73034_c = new IntHashMap();
   private Set field_73032_d = new HashSet();
   private Set field_73036_L = new HashSet();
   private final Minecraft field_73037_M = Minecraft.func_71410_x();
   private final Set field_73038_N = new HashSet();

   public WorldClient(NetHandlerPlayClient var1, WorldSettings var2, int var3, EnumDifficulty var4, Profiler var5) {
      super(new SaveHandlerMP(), "MpServer", WorldProvider.func_76570_a(var3), var2, var5);
      this.field_73035_a = var1;
      this.field_73013_u = var4;
      this.func_72950_A(8, 64, 8);
      this.field_72988_C = var1.field_147305_a;
   }

   @Override
   public void func_72835_b() {
      super.func_72835_b();
      this.func_82738_a(this.func_82737_E() + 1L);
      if (this.func_82736_K().func_82766_b("doDaylightCycle")) {
         this.func_72877_b(this.func_72820_D() + 1L);
      }

      this.field_72984_F.func_76320_a("reEntryProcessing");

      for(int var1 = 0; var1 < 10 && !this.field_73036_L.isEmpty(); ++var1) {
         Entity var2 = (Entity)this.field_73036_L.iterator().next();
         this.field_73036_L.remove(var2);
         if (!this.field_72996_f.contains(var2)) {
            this.func_72838_d(var2);
         }
      }

      this.field_72984_F.func_76318_c("connection");
      this.field_73035_a.func_147233_a();
      this.field_72984_F.func_76318_c("chunkCache");
      this.field_73033_b.func_73156_b();
      this.field_72984_F.func_76318_c("blocks");
      this.func_147456_g();
      this.field_72984_F.func_76319_b();
   }

   public void func_73031_a(int var1, int var2, int var3, int var4, int var5, int var6) {
   }

   @Override
   protected IChunkProvider func_72970_h() {
      this.field_73033_b = new ChunkProviderClient(this);
      return this.field_73033_b;
   }

   @Override
   protected void func_147456_g() {
      super.func_147456_g();
      this.field_73038_N.retainAll(this.field_72993_I);
      if (this.field_73038_N.size() == this.field_72993_I.size()) {
         this.field_73038_N.clear();
      }

      int var1 = 0;

      for(ChunkCoordIntPair var3 : this.field_72993_I) {
         if (!this.field_73038_N.contains(var3)) {
            int var4 = var3.field_77276_a * 16;
            int var5 = var3.field_77275_b * 16;
            this.field_72984_F.func_76320_a("getChunk");
            Chunk var6 = this.func_72964_e(var3.field_77276_a, var3.field_77275_b);
            this.func_147467_a(var4, var5, var6);
            this.field_72984_F.func_76319_b();
            this.field_73038_N.add(var3);
            if (++var1 >= 10) {
               return;
            }
         }
      }
   }

   public void func_73025_a(int var1, int var2, boolean var3) {
      if (var3) {
         this.field_73033_b.func_73158_c(var1, var2);
      } else {
         this.field_73033_b.func_73234_b(var1, var2);
      }

      if (!var3) {
         this.func_147458_c(var1 * 16, 0, var2 * 16, var1 * 16 + 15, 256, var2 * 16 + 15);
      }
   }

   @Override
   public boolean func_72838_d(Entity var1) {
      boolean var2 = super.func_72838_d(var1);
      this.field_73032_d.add(var1);
      if (!var2) {
         this.field_73036_L.add(var1);
      } else if (var1 instanceof EntityMinecart) {
         this.field_73037_M.func_147118_V().func_147682_a(new MovingSoundMinecart((EntityMinecart)var1));
      }

      return var2;
   }

   @Override
   public void func_72900_e(Entity var1) {
      super.func_72900_e(var1);
      this.field_73032_d.remove(var1);
   }

   @Override
   protected void func_72923_a(Entity var1) {
      super.func_72923_a(var1);
      if (this.field_73036_L.contains(var1)) {
         this.field_73036_L.remove(var1);
      }
   }

   @Override
   protected void func_72847_b(Entity var1) {
      super.func_72847_b(var1);
      boolean var2 = false;
      if (this.field_73032_d.contains(var1)) {
         if (var1.func_70089_S()) {
            this.field_73036_L.add(var1);
            var2 = true;
         } else {
            this.field_73032_d.remove(var1);
         }
      }

      if (RenderManager.field_78727_a.func_78713_a(var1).func_147905_a() && !var2) {
         this.field_73037_M.field_71438_f.func_147584_b();
      }
   }

   public void func_73027_a(int var1, Entity var2) {
      Entity var3 = this.func_73045_a(var1);
      if (var3 != null) {
         this.func_72900_e(var3);
      }

      this.field_73032_d.add(var2);
      var2.func_145769_d(var1);
      if (!this.func_72838_d(var2)) {
         this.field_73036_L.add(var2);
      }

      this.field_73034_c.func_76038_a(var1, var2);
      if (RenderManager.field_78727_a.func_78713_a(var2).func_147905_a()) {
         this.field_73037_M.field_71438_f.func_147584_b();
      }
   }

   @Override
   public Entity func_73045_a(int var1) {
      return (Entity)(var1 == this.field_73037_M.field_71439_g.func_145782_y()
         ? this.field_73037_M.field_71439_g
         : (Entity)this.field_73034_c.func_76041_a(var1));
   }

   public Entity func_73028_b(int var1) {
      Entity var2 = (Entity)this.field_73034_c.func_76049_d(var1);
      if (var2 != null) {
         this.field_73032_d.remove(var2);
         this.func_72900_e(var2);
      }

      return var2;
   }

   public boolean func_147492_c(int var1, int var2, int var3, Block var4, int var5) {
      this.func_73031_a(var1, var2, var3, var1, var2, var3);
      return super.func_147465_d(var1, var2, var3, var4, var5, 3);
   }

   @Override
   public void func_72882_A() {
      this.field_73035_a.func_147298_b().func_150718_a(new ChatComponentText("Quitting"));
   }

   @Override
   protected void func_72979_l() {
      if (!this.field_73011_w.field_76576_e) {
         ;
      }
   }

   @Override
   protected int func_152379_p() {
      return this.field_73037_M.field_71474_y.field_151451_c;
   }

   public void func_73029_E(int var1, int var2, int var3) {
      byte var4 = 16;
      Random var5 = new Random();

      for(int var6 = 0; var6 < 1000; ++var6) {
         int var7 = var1 + this.field_73012_v.nextInt(var4) - this.field_73012_v.nextInt(var4);
         int var8 = var2 + this.field_73012_v.nextInt(var4) - this.field_73012_v.nextInt(var4);
         int var9 = var3 + this.field_73012_v.nextInt(var4) - this.field_73012_v.nextInt(var4);
         Block var10 = this.func_147439_a(var7, var8, var9);
         if (var10.func_149688_o() == Material.field_151579_a) {
            if (this.field_73012_v.nextInt(8) > var8 && this.field_73011_w.func_76564_j()) {
               this.func_72869_a(
                  "depthsuspend",
                  (double)((float)var7 + this.field_73012_v.nextFloat()),
                  (double)((float)var8 + this.field_73012_v.nextFloat()),
                  (double)((float)var9 + this.field_73012_v.nextFloat()),
                  0.0,
                  0.0,
                  0.0
               );
            }
         } else {
            var10.func_149734_b(this, var7, var8, var9, var5);
         }
      }
   }

   public void func_73022_a() {
      this.field_72996_f.removeAll(this.field_72997_g);

      for(int var1 = 0; var1 < this.field_72997_g.size(); ++var1) {
         Entity var2 = (Entity)this.field_72997_g.get(var1);
         int var3 = var2.field_70176_ah;
         int var4 = var2.field_70164_aj;
         if (var2.field_70175_ag && this.func_72916_c(var3, var4)) {
            this.func_72964_e(var3, var4).func_76622_b(var2);
         }
      }

      for(int var5 = 0; var5 < this.field_72997_g.size(); ++var5) {
         this.func_72847_b((Entity)this.field_72997_g.get(var5));
      }

      this.field_72997_g.clear();

      for(int var6 = 0; var6 < this.field_72996_f.size(); ++var6) {
         Entity var7 = (Entity)this.field_72996_f.get(var6);
         if (var7.field_70154_o != null) {
            if (!var7.field_70154_o.field_70128_L && var7.field_70154_o.field_70153_n == var7) {
               continue;
            }

            var7.field_70154_o.field_70153_n = null;
            var7.field_70154_o = null;
         }

         if (var7.field_70128_L) {
            int var8 = var7.field_70176_ah;
            int var9 = var7.field_70164_aj;
            if (var7.field_70175_ag && this.func_72916_c(var8, var9)) {
               this.func_72964_e(var8, var9).func_76622_b(var7);
            }

            this.field_72996_f.remove(var6--);
            this.func_72847_b(var7);
         }
      }
   }

   @Override
   public CrashReportCategory func_72914_a(CrashReport var1) {
      CrashReportCategory var2 = super.func_72914_a(var1);
      var2.func_71500_a("Forced entities", new WorldClient$1(this));
      var2.func_71500_a("Retry entities", new WorldClient$2(this));
      var2.func_71500_a("Server brand", new WorldClient$3(this));
      var2.func_71500_a("Server type", new WorldClient$4(this));
      return var2;
   }

   @Override
   public void func_72980_b(double var1, double var3, double var5, String var7, float var8, float var9, boolean var10) {
      double var11 = this.field_73037_M.field_71451_h.func_70092_e(var1, var3, var5);
      PositionedSoundRecord var13 = new PositionedSoundRecord(new ResourceLocation(var7), var8, var9, (float)var1, (float)var3, (float)var5);
      if (var10 && var11 > 100.0) {
         double var14 = Math.sqrt(var11) / 40.0;
         this.field_73037_M.func_147118_V().func_147681_a(var13, (int)(var14 * 20.0));
      } else {
         this.field_73037_M.func_147118_V().func_147682_a(var13);
      }
   }

   @Override
   public void func_92088_a(double var1, double var3, double var5, double var7, double var9, double var11, NBTTagCompound var13) {
      this.field_73037_M
         .field_71452_i
         .func_78873_a(new EntityFireworkStarterFX(this, var1, var3, var5, var7, var9, var11, this.field_73037_M.field_71452_i, var13));
   }

   public void func_96443_a(Scoreboard var1) {
      this.field_96442_D = var1;
   }

   @Override
   public void func_72877_b(long var1) {
      if (var1 < 0L) {
         var1 = -var1;
         this.func_82736_K().func_82764_b("doDaylightCycle", "false");
      } else {
         this.func_82736_K().func_82764_b("doDaylightCycle", "true");
      }

      super.func_72877_b(var1);
   }
}
