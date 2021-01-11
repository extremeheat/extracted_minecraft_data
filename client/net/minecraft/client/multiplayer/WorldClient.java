package net.minecraft.client.multiplayer;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EntityFirework;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;

public class WorldClient extends World {
   private NetHandlerPlayClient field_73035_a;
   private ChunkProviderClient field_73033_b;
   private final Set<Entity> field_73032_d = Sets.newHashSet();
   private final Set<Entity> field_73036_L = Sets.newHashSet();
   private final Minecraft field_73037_M = Minecraft.func_71410_x();
   private final Set<ChunkCoordIntPair> field_73038_N = Sets.newHashSet();

   public WorldClient(NetHandlerPlayClient var1, WorldSettings var2, int var3, EnumDifficulty var4, Profiler var5) {
      super(new SaveHandlerMP(), new WorldInfo(var2, "MpServer"), WorldProvider.func_76570_a(var3), var5, true);
      this.field_73035_a = var1;
      this.func_72912_H().func_176144_a(var4);
      this.func_175652_B(new BlockPos(8, 64, 8));
      this.field_73011_w.func_76558_a(this);
      this.field_73020_y = this.func_72970_h();
      this.field_72988_C = new SaveDataMemoryStorage();
      this.func_72966_v();
      this.func_72947_a();
   }

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

      this.field_72984_F.func_76318_c("chunkCache");
      this.field_73033_b.func_73156_b();
      this.field_72984_F.func_76318_c("blocks");
      this.func_147456_g();
      this.field_72984_F.func_76319_b();
   }

   public void func_73031_a(int var1, int var2, int var3, int var4, int var5, int var6) {
   }

   protected IChunkProvider func_72970_h() {
      this.field_73033_b = new ChunkProviderClient(this);
      return this.field_73033_b;
   }

   protected void func_147456_g() {
      super.func_147456_g();
      this.field_73038_N.retainAll(this.field_72993_I);
      if (this.field_73038_N.size() == this.field_72993_I.size()) {
         this.field_73038_N.clear();
      }

      int var1 = 0;
      Iterator var2 = this.field_72993_I.iterator();

      while(var2.hasNext()) {
         ChunkCoordIntPair var3 = (ChunkCoordIntPair)var2.next();
         if (!this.field_73038_N.contains(var3)) {
            int var4 = var3.field_77276_a * 16;
            int var5 = var3.field_77275_b * 16;
            this.field_72984_F.func_76320_a("getChunk");
            Chunk var6 = this.func_72964_e(var3.field_77276_a, var3.field_77275_b);
            this.func_147467_a(var4, var5, var6);
            this.field_72984_F.func_76319_b();
            this.field_73038_N.add(var3);
            ++var1;
            if (var1 >= 10) {
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

   public void func_72900_e(Entity var1) {
      super.func_72900_e(var1);
      this.field_73032_d.remove(var1);
   }

   protected void func_72923_a(Entity var1) {
      super.func_72923_a(var1);
      if (this.field_73036_L.contains(var1)) {
         this.field_73036_L.remove(var1);
      }

   }

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

      this.field_175729_l.func_76038_a(var1, var2);
   }

   public Entity func_73045_a(int var1) {
      return (Entity)(var1 == this.field_73037_M.field_71439_g.func_145782_y() ? this.field_73037_M.field_71439_g : super.func_73045_a(var1));
   }

   public Entity func_73028_b(int var1) {
      Entity var2 = (Entity)this.field_175729_l.func_76049_d(var1);
      if (var2 != null) {
         this.field_73032_d.remove(var2);
         this.func_72900_e(var2);
      }

      return var2;
   }

   public boolean func_180503_b(BlockPos var1, IBlockState var2) {
      int var3 = var1.func_177958_n();
      int var4 = var1.func_177956_o();
      int var5 = var1.func_177952_p();
      this.func_73031_a(var3, var4, var5, var3, var4, var5);
      return super.func_180501_a(var1, var2, 3);
   }

   public void func_72882_A() {
      this.field_73035_a.func_147298_b().func_150718_a(new ChatComponentText("Quitting"));
   }

   protected void func_72979_l() {
   }

   protected int func_152379_p() {
      return this.field_73037_M.field_71474_y.field_151451_c;
   }

   public void func_73029_E(int var1, int var2, int var3) {
      byte var4 = 16;
      Random var5 = new Random();
      ItemStack var6 = this.field_73037_M.field_71439_g.func_70694_bm();
      boolean var7 = this.field_73037_M.field_71442_b.func_178889_l() == WorldSettings.GameType.CREATIVE && var6 != null && Block.func_149634_a(var6.func_77973_b()) == Blocks.field_180401_cv;
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();

      for(int var9 = 0; var9 < 1000; ++var9) {
         int var10 = var1 + this.field_73012_v.nextInt(var4) - this.field_73012_v.nextInt(var4);
         int var11 = var2 + this.field_73012_v.nextInt(var4) - this.field_73012_v.nextInt(var4);
         int var12 = var3 + this.field_73012_v.nextInt(var4) - this.field_73012_v.nextInt(var4);
         var8.func_181079_c(var10, var11, var12);
         IBlockState var13 = this.func_180495_p(var8);
         var13.func_177230_c().func_180655_c(this, var8, var13, var5);
         if (var7 && var13.func_177230_c() == Blocks.field_180401_cv) {
            this.func_175688_a(EnumParticleTypes.BARRIER, (double)((float)var10 + 0.5F), (double)((float)var11 + 0.5F), (double)((float)var12 + 0.5F), 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   public void func_73022_a() {
      this.field_72996_f.removeAll(this.field_72997_g);

      int var1;
      Entity var2;
      int var3;
      int var4;
      for(var1 = 0; var1 < this.field_72997_g.size(); ++var1) {
         var2 = (Entity)this.field_72997_g.get(var1);
         var3 = var2.field_70176_ah;
         var4 = var2.field_70164_aj;
         if (var2.field_70175_ag && this.func_175680_a(var3, var4, true)) {
            this.func_72964_e(var3, var4).func_76622_b(var2);
         }
      }

      for(var1 = 0; var1 < this.field_72997_g.size(); ++var1) {
         this.func_72847_b((Entity)this.field_72997_g.get(var1));
      }

      this.field_72997_g.clear();

      for(var1 = 0; var1 < this.field_72996_f.size(); ++var1) {
         var2 = (Entity)this.field_72996_f.get(var1);
         if (var2.field_70154_o != null) {
            if (!var2.field_70154_o.field_70128_L && var2.field_70154_o.field_70153_n == var2) {
               continue;
            }

            var2.field_70154_o.field_70153_n = null;
            var2.field_70154_o = null;
         }

         if (var2.field_70128_L) {
            var3 = var2.field_70176_ah;
            var4 = var2.field_70164_aj;
            if (var2.field_70175_ag && this.func_175680_a(var3, var4, true)) {
               this.func_72964_e(var3, var4).func_76622_b(var2);
            }

            this.field_72996_f.remove(var1--);
            this.func_72847_b(var2);
         }
      }

   }

   public CrashReportCategory func_72914_a(CrashReport var1) {
      CrashReportCategory var2 = super.func_72914_a(var1);
      var2.func_71500_a("Forced entities", new Callable<String>() {
         public String call() {
            return WorldClient.this.field_73032_d.size() + " total; " + WorldClient.this.field_73032_d.toString();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var2.func_71500_a("Retry entities", new Callable<String>() {
         public String call() {
            return WorldClient.this.field_73036_L.size() + " total; " + WorldClient.this.field_73036_L.toString();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var2.func_71500_a("Server brand", new Callable<String>() {
         public String call() throws Exception {
            return WorldClient.this.field_73037_M.field_71439_g.func_142021_k();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var2.func_71500_a("Server type", new Callable<String>() {
         public String call() throws Exception {
            return WorldClient.this.field_73037_M.func_71401_C() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      return var2;
   }

   public void func_175731_a(BlockPos var1, String var2, float var3, float var4, boolean var5) {
      this.func_72980_b((double)var1.func_177958_n() + 0.5D, (double)var1.func_177956_o() + 0.5D, (double)var1.func_177952_p() + 0.5D, var2, var3, var4, var5);
   }

   public void func_72980_b(double var1, double var3, double var5, String var7, float var8, float var9, boolean var10) {
      double var11 = this.field_73037_M.func_175606_aa().func_70092_e(var1, var3, var5);
      PositionedSoundRecord var13 = new PositionedSoundRecord(new ResourceLocation(var7), var8, var9, (float)var1, (float)var3, (float)var5);
      if (var10 && var11 > 100.0D) {
         double var14 = Math.sqrt(var11) / 40.0D;
         this.field_73037_M.func_147118_V().func_147681_a(var13, (int)(var14 * 20.0D));
      } else {
         this.field_73037_M.func_147118_V().func_147682_a(var13);
      }

   }

   public void func_92088_a(double var1, double var3, double var5, double var7, double var9, double var11, NBTTagCompound var13) {
      this.field_73037_M.field_71452_i.func_78873_a(new EntityFirework.StarterFX(this, var1, var3, var5, var7, var9, var11, this.field_73037_M.field_71452_i, var13));
   }

   public void func_96443_a(Scoreboard var1) {
      this.field_96442_D = var1;
   }

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
