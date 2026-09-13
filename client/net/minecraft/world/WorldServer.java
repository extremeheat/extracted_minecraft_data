package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.INpc;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase$SpawnListEntry;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;
import net.minecraft.world.storage.ISaveHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServer extends World {
   private static final Logger field_147491_a = LogManager.getLogger();
   private final MinecraftServer field_73061_a;
   private final EntityTracker field_73062_L;
   private final PlayerManager field_73063_M;
   private Set field_73064_N;
   private TreeSet field_73065_O;
   public ChunkProviderServer field_73059_b;
   public boolean field_73058_d;
   private boolean field_73068_P;
   private int field_80004_Q;
   private final Teleporter field_85177_Q;
   private final SpawnerAnimals field_135059_Q = new SpawnerAnimals();
   private WorldServer$ServerBlockEventList[] field_147490_S = new WorldServer$ServerBlockEventList[]{
      new WorldServer$ServerBlockEventList(null), new WorldServer$ServerBlockEventList(null)
   };
   private int field_147489_T;
   private static final WeightedRandomChestContent[] field_73069_S = new WeightedRandomChestContent[]{
      new WeightedRandomChestContent(Items.field_151055_y, 0, 1, 3, 10),
      new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150344_f), 0, 1, 3, 10),
      new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150364_r), 0, 1, 3, 10),
      new WeightedRandomChestContent(Items.field_151049_t, 0, 1, 1, 3),
      new WeightedRandomChestContent(Items.field_151053_p, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151050_s, 0, 1, 1, 3),
      new WeightedRandomChestContent(Items.field_151039_o, 0, 1, 1, 5),
      new WeightedRandomChestContent(Items.field_151034_e, 0, 2, 3, 5),
      new WeightedRandomChestContent(Items.field_151025_P, 0, 2, 3, 3),
      new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150363_s), 0, 1, 3, 10)
   };
   private List field_94579_S = new ArrayList();
   private IntHashMap field_73066_T;

   public WorldServer(MinecraftServer var1, ISaveHandler var2, String var3, int var4, WorldSettings var5, Profiler var6) {
      super(var2, var3, var5, WorldProvider.func_76570_a(var4), var6);
      this.field_73061_a = var1;
      this.field_73062_L = new EntityTracker(this);
      this.field_73063_M = new PlayerManager(this);
      if (this.field_73066_T == null) {
         this.field_73066_T = new IntHashMap();
      }

      if (this.field_73064_N == null) {
         this.field_73064_N = new HashSet();
      }

      if (this.field_73065_O == null) {
         this.field_73065_O = new TreeSet();
      }

      this.field_85177_Q = new Teleporter(this);
      this.field_96442_D = new ServerScoreboard(var1);
      ScoreboardSaveData var7 = (ScoreboardSaveData)this.field_72988_C.func_75742_a(ScoreboardSaveData.class, "scoreboard");
      if (var7 == null) {
         var7 = new ScoreboardSaveData();
         this.field_72988_C.func_75745_a("scoreboard", var7);
      }

      var7.func_96499_a(this.field_96442_D);
      ((ServerScoreboard)this.field_96442_D).func_96547_a(var7);
   }

   @Override
   public void func_72835_b() {
      super.func_72835_b();
      if (this.func_72912_H().func_76093_s() && this.field_73013_u != EnumDifficulty.HARD) {
         this.field_73013_u = EnumDifficulty.HARD;
      }

      this.field_73011_w.field_76578_c.func_76938_b();
      if (this.func_73056_e()) {
         if (this.func_82736_K().func_82766_b("doDaylightCycle")) {
            long var1 = this.field_72986_A.func_76073_f() + 24000L;
            this.field_72986_A.func_76068_b(var1 - var1 % 24000L);
         }

         this.func_73053_d();
      }

      this.field_72984_F.func_76320_a("mobSpawner");
      if (this.func_82736_K().func_82766_b("doMobSpawning")) {
         this.field_135059_Q.func_77192_a(this, this.field_72985_G, this.field_72992_H, this.field_72986_A.func_82573_f() % 400L == 0L);
      }

      this.field_72984_F.func_76318_c("chunkSource");
      this.field_73020_y.func_73156_b();
      int var3 = this.func_72967_a(1.0F);
      if (var3 != this.field_73008_k) {
         this.field_73008_k = var3;
      }

      this.field_72986_A.func_82572_b(this.field_72986_A.func_82573_f() + 1L);
      if (this.func_82736_K().func_82766_b("doDaylightCycle")) {
         this.field_72986_A.func_76068_b(this.field_72986_A.func_76073_f() + 1L);
      }

      this.field_72984_F.func_76318_c("tickPending");
      this.func_72955_a(false);
      this.field_72984_F.func_76318_c("tickBlocks");
      this.func_147456_g();
      this.field_72984_F.func_76318_c("chunkMap");
      this.field_73063_M.func_72693_b();
      this.field_72984_F.func_76318_c("village");
      this.field_72982_D.func_75544_a();
      this.field_72983_E.func_75528_a();
      this.field_72984_F.func_76318_c("portalForcer");
      this.field_85177_Q.func_85189_a(this.func_82737_E());
      this.field_72984_F.func_76319_b();
      this.func_147488_Z();
   }

   public BiomeGenBase$SpawnListEntry func_73057_a(EnumCreatureType var1, int var2, int var3, int var4) {
      List var5 = this.func_72863_F().func_73155_a(var1, var2, var3, var4);
      return var5 != null && !var5.isEmpty() ? (BiomeGenBase$SpawnListEntry)WeightedRandom.func_76271_a(this.field_73012_v, var5) : null;
   }

   @Override
   public void func_72854_c() {
      this.field_73068_P = !this.field_73010_i.isEmpty();

      for(EntityPlayer var2 : this.field_73010_i) {
         if (!var2.func_70608_bn()) {
            this.field_73068_P = false;
            break;
         }
      }
   }

   protected void func_73053_d() {
      this.field_73068_P = false;

      for(EntityPlayer var2 : this.field_73010_i) {
         if (var2.func_70608_bn()) {
            var2.func_70999_a(false, false, true);
         }
      }

      this.func_73051_P();
   }

   private void func_73051_P() {
      this.field_72986_A.func_76080_g(0);
      this.field_72986_A.func_76084_b(false);
      this.field_72986_A.func_76090_f(0);
      this.field_72986_A.func_76069_a(false);
   }

   public boolean func_73056_e() {
      if (this.field_73068_P && !this.field_72995_K) {
         for(EntityPlayer var2 : this.field_73010_i) {
            if (!var2.func_71026_bH()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void func_72974_f() {
      if (this.field_72986_A.func_76075_d() <= 0) {
         this.field_72986_A.func_76056_b(64);
      }

      int var1 = this.field_72986_A.func_76079_c();
      int var2 = this.field_72986_A.func_76074_e();
      int var3 = 0;

      while(this.func_147474_b(var1, var2).func_149688_o() == Material.field_151579_a) {
         var1 += this.field_73012_v.nextInt(8) - this.field_73012_v.nextInt(8);
         var2 += this.field_73012_v.nextInt(8) - this.field_73012_v.nextInt(8);
         if (++var3 == 10000) {
            break;
         }
      }

      this.field_72986_A.func_76058_a(var1);
      this.field_72986_A.func_76087_c(var2);
   }

   @Override
   protected void func_147456_g() {
      super.func_147456_g();
      int var1 = 0;
      int var2 = 0;

      for(ChunkCoordIntPair var4 : this.field_72993_I) {
         int var5 = var4.field_77276_a * 16;
         int var6 = var4.field_77275_b * 16;
         this.field_72984_F.func_76320_a("getChunk");
         Chunk var7 = this.func_72964_e(var4.field_77276_a, var4.field_77275_b);
         this.func_147467_a(var5, var6, var7);
         this.field_72984_F.func_76318_c("tickChunk");
         var7.func_150804_b(false);
         this.field_72984_F.func_76318_c("thunder");
         if (this.field_73012_v.nextInt(100000) == 0 && this.func_72896_J() && this.func_72911_I()) {
            this.field_73005_l = this.field_73005_l * 3 + 1013904223;
            int var8 = this.field_73005_l >> 2;
            int var9 = var5 + (var8 & 15);
            int var10 = var6 + (var8 >> 8 & 15);
            int var11 = this.func_72874_g(var9, var10);
            if (this.func_72951_B(var9, var11, var10)) {
               this.func_72942_c(new EntityLightningBolt(this, (double)var9, (double)var11, (double)var10));
            }
         }

         this.field_72984_F.func_76318_c("iceandsnow");
         if (this.field_73012_v.nextInt(16) == 0) {
            this.field_73005_l = this.field_73005_l * 3 + 1013904223;
            int var18 = this.field_73005_l >> 2;
            int var20 = var18 & 15;
            int var22 = var18 >> 8 & 15;
            int var24 = this.func_72874_g(var20 + var5, var22 + var6);
            if (this.func_72850_v(var20 + var5, var24 - 1, var22 + var6)) {
               this.func_147449_b(var20 + var5, var24 - 1, var22 + var6, Blocks.field_150432_aD);
            }

            if (this.func_72896_J() && this.func_147478_e(var20 + var5, var24, var22 + var6, true)) {
               this.func_147449_b(var20 + var5, var24, var22 + var6, Blocks.field_150431_aC);
            }

            if (this.func_72896_J()) {
               BiomeGenBase var12 = this.func_72807_a(var20 + var5, var22 + var6);
               if (var12.func_76738_d()) {
                  this.func_147439_a(var20 + var5, var24 - 1, var22 + var6).func_149639_l(this, var20 + var5, var24 - 1, var22 + var6);
               }
            }
         }

         this.field_72984_F.func_76318_c("tickBlocks");

         for(ExtendedBlockStorage var25 : var7.func_76587_i()) {
            if (var25 != null && var25.func_76675_b()) {
               for(int var26 = 0; var26 < 3; ++var26) {
                  this.field_73005_l = this.field_73005_l * 3 + 1013904223;
                  int var13 = this.field_73005_l >> 2;
                  int var14 = var13 & 15;
                  int var15 = var13 >> 8 & 15;
                  int var16 = var13 >> 16 & 15;
                  ++var2;
                  Block var17 = var25.func_150819_a(var14, var16, var15);
                  if (var17.func_149653_t()) {
                     ++var1;
                     var17.func_149674_a(this, var14 + var5, var16 + var25.func_76662_d(), var15 + var6, this.field_73012_v);
                  }
               }
            }
         }

         this.field_72984_F.func_76319_b();
      }
   }

   @Override
   public boolean func_147477_a(int var1, int var2, int var3, Block var4) {
      NextTickListEntry var5 = new NextTickListEntry(var1, var2, var3, var4);
      return this.field_94579_S.contains(var5);
   }

   @Override
   public void func_147464_a(int var1, int var2, int var3, Block var4, int var5) {
      this.func_147454_a(var1, var2, var3, var4, var5, 0);
   }

   @Override
   public void func_147454_a(int var1, int var2, int var3, Block var4, int var5, int var6) {
      NextTickListEntry var7 = new NextTickListEntry(var1, var2, var3, var4);
      byte var8 = 0;
      if (this.field_72999_e && var4.func_149688_o() != Material.field_151579_a) {
         if (var4.func_149698_L()) {
            var8 = 8;
            if (this.func_72904_c(
               var7.field_77183_a - var8,
               var7.field_77181_b - var8,
               var7.field_77182_c - var8,
               var7.field_77183_a + var8,
               var7.field_77181_b + var8,
               var7.field_77182_c + var8
            )) {
               Block var9 = this.func_147439_a(var7.field_77183_a, var7.field_77181_b, var7.field_77182_c);
               if (var9.func_149688_o() != Material.field_151579_a && var9 == var7.func_151351_a()) {
                  var9.func_149674_a(this, var7.field_77183_a, var7.field_77181_b, var7.field_77182_c, this.field_73012_v);
               }
            }

            return;
         }

         var5 = 1;
      }

      if (this.func_72904_c(var1 - var8, var2 - var8, var3 - var8, var1 + var8, var2 + var8, var3 + var8)) {
         if (var4.func_149688_o() != Material.field_151579_a) {
            var7.func_77176_a((long)var5 + this.field_72986_A.func_82573_f());
            var7.func_82753_a(var6);
         }

         if (!this.field_73064_N.contains(var7)) {
            this.field_73064_N.add(var7);
            this.field_73065_O.add(var7);
         }
      }
   }

   @Override
   public void func_147446_b(int var1, int var2, int var3, Block var4, int var5, int var6) {
      NextTickListEntry var7 = new NextTickListEntry(var1, var2, var3, var4);
      var7.func_82753_a(var6);
      if (var4.func_149688_o() != Material.field_151579_a) {
         var7.func_77176_a((long)var5 + this.field_72986_A.func_82573_f());
      }

      if (!this.field_73064_N.contains(var7)) {
         this.field_73064_N.add(var7);
         this.field_73065_O.add(var7);
      }
   }

   @Override
   public void func_72939_s() {
      if (this.field_73010_i.isEmpty()) {
         if (this.field_80004_Q++ >= 1200) {
            return;
         }
      } else {
         this.func_82742_i();
      }

      super.func_72939_s();
   }

   public void func_82742_i() {
      this.field_80004_Q = 0;
   }

   @Override
   public boolean func_72955_a(boolean var1) {
      int var2 = this.field_73065_O.size();
      if (var2 != this.field_73064_N.size()) {
         throw new IllegalStateException("TickNextTick list out of synch");
      } else {
         if (var2 > 1000) {
            var2 = 1000;
         }

         this.field_72984_F.func_76320_a("cleaning");

         for(int var3 = 0; var3 < var2; ++var3) {
            NextTickListEntry var4 = (NextTickListEntry)this.field_73065_O.first();
            if (!var1 && var4.field_77180_e > this.field_72986_A.func_82573_f()) {
               break;
            }

            this.field_73065_O.remove(var4);
            this.field_73064_N.remove(var4);
            this.field_94579_S.add(var4);
         }

         this.field_72984_F.func_76319_b();
         this.field_72984_F.func_76320_a("ticking");
         Iterator var14 = this.field_94579_S.iterator();

         while(var14.hasNext()) {
            NextTickListEntry var15 = (NextTickListEntry)var14.next();
            var14.remove();
            byte var5 = 0;
            if (this.func_72904_c(
               var15.field_77183_a - var5,
               var15.field_77181_b - var5,
               var15.field_77182_c - var5,
               var15.field_77183_a + var5,
               var15.field_77181_b + var5,
               var15.field_77182_c + var5
            )) {
               Block var6 = this.func_147439_a(var15.field_77183_a, var15.field_77181_b, var15.field_77182_c);
               if (var6.func_149688_o() != Material.field_151579_a && Block.func_149680_a(var6, var15.func_151351_a())) {
                  try {
                     var6.func_149674_a(this, var15.field_77183_a, var15.field_77181_b, var15.field_77182_c, this.field_73012_v);
                  } catch (Throwable var13) {
                     CrashReport var8 = CrashReport.func_85055_a(var13, "Exception while ticking a block");
                     CrashReportCategory var9 = var8.func_85058_a("Block being ticked");

                     int var10;
                     try {
                        var10 = this.func_72805_g(var15.field_77183_a, var15.field_77181_b, var15.field_77182_c);
                     } catch (Throwable var12) {
                        var10 = -1;
                     }

                     CrashReportCategory.func_147153_a(var9, var15.field_77183_a, var15.field_77181_b, var15.field_77182_c, var6, var10);
                     throw new ReportedException(var8);
                  }
               }
            } else {
               this.func_147464_a(var15.field_77183_a, var15.field_77181_b, var15.field_77182_c, var15.func_151351_a(), 0);
            }
         }

         this.field_72984_F.func_76319_b();
         this.field_94579_S.clear();
         return !this.field_73065_O.isEmpty();
      }
   }

   @Override
   public List func_72920_a(Chunk var1, boolean var2) {
      ArrayList var3 = null;
      ChunkCoordIntPair var4 = var1.func_76632_l();
      int var5 = (var4.field_77276_a << 4) - 2;
      int var6 = var5 + 16 + 2;
      int var7 = (var4.field_77275_b << 4) - 2;
      int var8 = var7 + 16 + 2;

      for(int var9 = 0; var9 < 2; ++var9) {
         Iterator var10;
         if (var9 == 0) {
            var10 = this.field_73065_O.iterator();
         } else {
            var10 = this.field_94579_S.iterator();
            if (!this.field_94579_S.isEmpty()) {
               field_147491_a.debug("toBeTicked = " + this.field_94579_S.size());
            }
         }

         while(var10.hasNext()) {
            NextTickListEntry var11 = (NextTickListEntry)var10.next();
            if (var11.field_77183_a >= var5 && var11.field_77183_a < var6 && var11.field_77182_c >= var7 && var11.field_77182_c < var8) {
               if (var2) {
                  this.field_73064_N.remove(var11);
                  var10.remove();
               }

               if (var3 == null) {
                  var3 = new ArrayList();
               }

               var3.add(var11);
            }
         }
      }

      return var3;
   }

   @Override
   public void func_72866_a(Entity var1, boolean var2) {
      if (!this.field_73061_a.func_71268_U() && (var1 instanceof EntityAnimal || var1 instanceof EntityWaterMob)) {
         var1.func_70106_y();
      }

      if (!this.field_73061_a.func_71220_V() && var1 instanceof INpc) {
         var1.func_70106_y();
      }

      super.func_72866_a(var1, var2);
   }

   @Override
   protected IChunkProvider func_72970_h() {
      IChunkLoader var1 = this.field_73019_z.func_75763_a(this.field_73011_w);
      this.field_73059_b = new ChunkProviderServer(this, var1, this.field_73011_w.func_76555_c());
      return this.field_73059_b;
   }

   public List func_147486_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      ArrayList var7 = new ArrayList();

      for(int var8 = 0; var8 < this.field_147482_g.size(); ++var8) {
         TileEntity var9 = (TileEntity)this.field_147482_g.get(var8);
         if (var9.field_145851_c >= var1
            && var9.field_145848_d >= var2
            && var9.field_145849_e >= var3
            && var9.field_145851_c < var4
            && var9.field_145848_d < var5
            && var9.field_145849_e < var6) {
            var7.add(var9);
         }
      }

      return var7;
   }

   @Override
   public boolean func_72962_a(EntityPlayer var1, int var2, int var3, int var4) {
      return !this.field_73061_a.func_96290_a(this, var2, var3, var4, var1);
   }

   @Override
   protected void func_72963_a(WorldSettings var1) {
      if (this.field_73066_T == null) {
         this.field_73066_T = new IntHashMap();
      }

      if (this.field_73064_N == null) {
         this.field_73064_N = new HashSet();
      }

      if (this.field_73065_O == null) {
         this.field_73065_O = new TreeSet();
      }

      this.func_73052_b(var1);
      super.func_72963_a(var1);
   }

   protected void func_73052_b(WorldSettings var1) {
      if (!this.field_73011_w.func_76567_e()) {
         this.field_72986_A.func_76081_a(0, this.field_73011_w.func_76557_i(), 0);
      } else {
         this.field_72987_B = true;
         WorldChunkManager var2 = this.field_73011_w.field_76578_c;
         List var3 = var2.func_76932_a();
         Random var4 = new Random(this.func_72905_C());
         ChunkPosition var5 = var2.func_150795_a(0, 0, 256, var3, var4);
         int var6 = 0;
         int var7 = this.field_73011_w.func_76557_i();
         int var8 = 0;
         if (var5 != null) {
            var6 = var5.field_151329_a;
            var8 = var5.field_151328_c;
         } else {
            field_147491_a.warn("Unable to find spawn biome");
         }

         int var9 = 0;

         while(!this.field_73011_w.func_76566_a(var6, var8)) {
            var6 += var4.nextInt(64) - var4.nextInt(64);
            var8 += var4.nextInt(64) - var4.nextInt(64);
            if (++var9 == 1000) {
               break;
            }
         }

         this.field_72986_A.func_76081_a(var6, var7, var8);
         this.field_72987_B = false;
         if (var1.func_77167_c()) {
            this.func_73047_i();
         }
      }
   }

   protected void func_73047_i() {
      WorldGeneratorBonusChest var1 = new WorldGeneratorBonusChest(field_73069_S, 10);

      for(int var2 = 0; var2 < 10; ++var2) {
         int var3 = this.field_72986_A.func_76079_c() + this.field_73012_v.nextInt(6) - this.field_73012_v.nextInt(6);
         int var4 = this.field_72986_A.func_76074_e() + this.field_73012_v.nextInt(6) - this.field_73012_v.nextInt(6);
         int var5 = this.func_72825_h(var3, var4) + 1;
         if (var1.func_76484_a(this, this.field_73012_v, var3, var5, var4)) {
            break;
         }
      }
   }

   public ChunkCoordinates func_73054_j() {
      return this.field_73011_w.func_76554_h();
   }

   public void func_73044_a(boolean var1, IProgressUpdate var2) {
      if (this.field_73020_y.func_73157_c()) {
         if (var2 != null) {
            var2.func_73720_a("Saving level");
         }

         this.func_73042_a();
         if (var2 != null) {
            var2.func_73719_c("Saving chunks");
         }

         this.field_73020_y.func_73151_a(var1, var2);

         for(Chunk var5 : Lists.newArrayList(this.field_73059_b.func_152380_a())) {
            if (var5 != null && !this.field_73063_M.func_152621_a(var5.field_76635_g, var5.field_76647_h)) {
               this.field_73059_b.func_73241_b(var5.field_76635_g, var5.field_76647_h);
            }
         }
      }
   }

   public void func_104140_m() {
      if (this.field_73020_y.func_73157_c()) {
         this.field_73020_y.func_104112_b();
      }
   }

   protected void func_73042_a() {
      this.func_72906_B();
      this.field_73019_z.func_75755_a(this.field_72986_A, this.field_73061_a.func_71203_ab().func_72378_q());
      this.field_72988_C.func_75744_a();
   }

   @Override
   protected void func_72923_a(Entity var1) {
      super.func_72923_a(var1);
      this.field_73066_T.func_76038_a(var1.func_145782_y(), var1);
      Entity[] var2 = var1.func_70021_al();
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            this.field_73066_T.func_76038_a(var2[var3].func_145782_y(), var2[var3]);
         }
      }
   }

   @Override
   protected void func_72847_b(Entity var1) {
      super.func_72847_b(var1);
      this.field_73066_T.func_76049_d(var1.func_145782_y());
      Entity[] var2 = var1.func_70021_al();
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            this.field_73066_T.func_76049_d(var2[var3].func_145782_y());
         }
      }
   }

   @Override
   public Entity func_73045_a(int var1) {
      return (Entity)this.field_73066_T.func_76041_a(var1);
   }

   @Override
   public boolean func_72942_c(Entity var1) {
      if (super.func_72942_c(var1)) {
         this.field_73061_a
            .func_71203_ab()
            .func_148541_a(
               var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, 512.0, this.field_73011_w.field_76574_g, new S2CPacketSpawnGlobalEntity(var1)
            );
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void func_72960_a(Entity var1, byte var2) {
      this.func_73039_n().func_151248_b(var1, new S19PacketEntityStatus(var1, var2));
   }

   @Override
   public Explosion func_72885_a(Entity var1, double var2, double var4, double var6, float var8, boolean var9, boolean var10) {
      Explosion var11 = new Explosion(this, var1, var2, var4, var6, var8);
      var11.field_77286_a = var9;
      var11.field_82755_b = var10;
      var11.func_77278_a();
      var11.func_77279_a(false);
      if (!var10) {
         var11.field_77281_g.clear();
      }

      for(EntityPlayer var13 : this.field_73010_i) {
         if (var13.func_70092_e(var2, var4, var6) < 4096.0) {
            ((EntityPlayerMP)var13)
               .field_71135_a
               .func_147359_a(new S27PacketExplosion(var2, var4, var6, var8, var11.field_77281_g, (Vec3)var11.func_77277_b().get(var13)));
         }
      }

      return var11;
   }

   @Override
   public void func_147452_c(int var1, int var2, int var3, Block var4, int var5, int var6) {
      BlockEventData var7 = new BlockEventData(var1, var2, var3, var4, var5, var6);

      for(BlockEventData var9 : this.field_147490_S[this.field_147489_T]) {
         if (var9.equals(var7)) {
            return;
         }
      }

      this.field_147490_S[this.field_147489_T].add(var7);
   }

   private void func_147488_Z() {
      while(!this.field_147490_S[this.field_147489_T].isEmpty()) {
         int var1 = this.field_147489_T;
         this.field_147489_T ^= 1;

         for(BlockEventData var3 : this.field_147490_S[var1]) {
            if (this.func_147485_a(var3)) {
               this.field_73061_a
                  .func_71203_ab()
                  .func_148541_a(
                     (double)var3.func_151340_a(),
                     (double)var3.func_151342_b(),
                     (double)var3.func_151341_c(),
                     64.0,
                     this.field_73011_w.field_76574_g,
                     new S24PacketBlockAction(
                        var3.func_151340_a(), var3.func_151342_b(), var3.func_151341_c(), var3.func_151337_f(), var3.func_151339_d(), var3.func_151338_e()
                     )
                  );
            }
         }

         this.field_147490_S[var1].clear();
      }
   }

   private boolean func_147485_a(BlockEventData var1) {
      Block var2 = this.func_147439_a(var1.func_151340_a(), var1.func_151342_b(), var1.func_151341_c());
      return var2 == var1.func_151337_f()
         ? var2.func_149696_a(this, var1.func_151340_a(), var1.func_151342_b(), var1.func_151341_c(), var1.func_151339_d(), var1.func_151338_e())
         : false;
   }

   public void func_73041_k() {
      this.field_73019_z.func_75759_a();
   }

   @Override
   protected void func_72979_l() {
      boolean var1 = this.func_72896_J();
      super.func_72979_l();
      if (this.field_73003_n != this.field_73004_o) {
         this.field_73061_a.func_71203_ab().func_148537_a(new S2BPacketChangeGameState(7, this.field_73004_o), this.field_73011_w.field_76574_g);
      }

      if (this.field_73018_p != this.field_73017_q) {
         this.field_73061_a.func_71203_ab().func_148537_a(new S2BPacketChangeGameState(8, this.field_73017_q), this.field_73011_w.field_76574_g);
      }

      if (var1 != this.func_72896_J()) {
         if (var1) {
            this.field_73061_a.func_71203_ab().func_148540_a(new S2BPacketChangeGameState(2, 0.0F));
         } else {
            this.field_73061_a.func_71203_ab().func_148540_a(new S2BPacketChangeGameState(1, 0.0F));
         }

         this.field_73061_a.func_71203_ab().func_148540_a(new S2BPacketChangeGameState(7, this.field_73004_o));
         this.field_73061_a.func_71203_ab().func_148540_a(new S2BPacketChangeGameState(8, this.field_73017_q));
      }
   }

   @Override
   protected int func_152379_p() {
      return this.field_73061_a.func_71203_ab().func_72395_o();
   }

   public MinecraftServer func_73046_m() {
      return this.field_73061_a;
   }

   public EntityTracker func_73039_n() {
      return this.field_73062_L;
   }

   public PlayerManager func_73040_p() {
      return this.field_73063_M;
   }

   public Teleporter func_85176_s() {
      return this.field_85177_Q;
   }

   public void func_147487_a(String var1, double var2, double var4, double var6, int var8, double var9, double var11, double var13, double var15) {
      S2APacketParticles var17 = new S2APacketParticles(
         var1, (float)var2, (float)var4, (float)var6, (float)var9, (float)var11, (float)var13, (float)var15, var8
      );

      for(int var18 = 0; var18 < this.field_73010_i.size(); ++var18) {
         EntityPlayerMP var19 = (EntityPlayerMP)this.field_73010_i.get(var18);
         ChunkCoordinates var20 = var19.func_82114_b();
         double var21 = var2 - (double)var20.field_71574_a;
         double var23 = var4 - (double)var20.field_71572_b;
         double var25 = var6 - (double)var20.field_71573_c;
         double var27 = var21 * var21 + var23 * var23 + var25 * var25;
         if (var27 <= 256.0) {
            var19.field_71135_a.func_147359_a(var17);
         }
      }
   }
}
