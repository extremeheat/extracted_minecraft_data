package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.INpc;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BonusChestFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataCallableSave;
import net.minecraft.world.storage.WorldSavedDataStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServer extends World implements IThreadListener {
   private static final Logger field_147491_a = LogManager.getLogger();
   private final MinecraftServer field_73061_a;
   private final EntityTracker field_73062_L;
   private final PlayerChunkMap field_73063_M;
   private final Map<UUID, Entity> field_175741_N = Maps.newHashMap();
   public boolean field_73058_d;
   private boolean field_73068_P;
   private int field_80004_Q;
   private final Teleporter field_85177_Q;
   private final WorldEntitySpawner field_175742_R = new WorldEntitySpawner();
   private final ServerTickList<Block> field_94579_S;
   private final ServerTickList<Fluid> field_205342_P;
   protected final VillageSiege field_175740_d;
   ObjectLinkedOpenHashSet<BlockEventData> field_147490_S;
   private boolean field_211159_Q;

   public WorldServer(MinecraftServer var1, ISaveHandler var2, WorldSavedDataStorage var3, WorldInfo var4, DimensionType var5, Profiler var6) {
      super(var2, var3, var4, var5.func_186070_d(), var6, false);
      this.field_94579_S = new ServerTickList(this, (var0) -> {
         return var0 == null || var0.func_176223_P().func_196958_f();
      }, IRegistry.field_212618_g::func_177774_c, IRegistry.field_212618_g::func_82594_a, this::func_205338_b);
      this.field_205342_P = new ServerTickList(this, (var0) -> {
         return var0 == null || var0 == Fluids.field_204541_a;
      }, IRegistry.field_212619_h::func_177774_c, IRegistry.field_212619_h::func_82594_a, this::func_205339_a);
      this.field_175740_d = new VillageSiege(this);
      this.field_147490_S = new ObjectLinkedOpenHashSet();
      this.field_73061_a = var1;
      this.field_73062_L = new EntityTracker(this);
      this.field_73063_M = new PlayerChunkMap(this);
      this.field_73011_w.func_76558_a(this);
      this.field_73020_y = this.func_72970_h();
      this.field_85177_Q = new Teleporter(this);
      this.func_72966_v();
      this.func_72947_a();
      this.func_175723_af().func_177725_a(var1.func_175580_aG());
   }

   public WorldServer func_212251_i__() {
      String var1 = VillageCollection.func_176062_a(this.field_73011_w);
      VillageCollection var2 = (VillageCollection)this.func_212411_a(DimensionType.OVERWORLD, VillageCollection::new, var1);
      if (var2 == null) {
         this.field_72982_D = new VillageCollection(this);
         this.func_212409_a(DimensionType.OVERWORLD, var1, this.field_72982_D);
      } else {
         this.field_72982_D = var2;
         this.field_72982_D.func_82566_a(this);
      }

      ScoreboardSaveData var3 = (ScoreboardSaveData)this.func_212411_a(DimensionType.OVERWORLD, ScoreboardSaveData::new, "scoreboard");
      if (var3 == null) {
         var3 = new ScoreboardSaveData();
         this.func_212409_a(DimensionType.OVERWORLD, "scoreboard", var3);
      }

      var3.func_96499_a(this.field_73061_a.func_200251_aP());
      this.field_73061_a.func_200251_aP().func_186684_a(new WorldSavedDataCallableSave(var3));
      this.func_175723_af().func_177739_c(this.field_72986_A.func_176120_C(), this.field_72986_A.func_176126_D());
      this.func_175723_af().func_177744_c(this.field_72986_A.func_176140_I());
      this.func_175723_af().func_177724_b(this.field_72986_A.func_176138_H());
      this.func_175723_af().func_177747_c(this.field_72986_A.func_176131_J());
      this.func_175723_af().func_177723_b(this.field_72986_A.func_176139_K());
      if (this.field_72986_A.func_176134_F() > 0L) {
         this.func_175723_af().func_177738_a(this.field_72986_A.func_176137_E(), this.field_72986_A.func_176132_G(), this.field_72986_A.func_176134_F());
      } else {
         this.func_175723_af().func_177750_a(this.field_72986_A.func_176137_E());
      }

      return this;
   }

   public void func_72835_b(BooleanSupplier var1) {
      this.field_211159_Q = true;
      super.func_72835_b(var1);
      if (this.func_72912_H().func_76093_s() && this.func_175659_aa() != EnumDifficulty.HARD) {
         this.func_72912_H().func_176144_a(EnumDifficulty.HARD);
      }

      this.field_73020_y.func_201711_g().func_202090_b().func_73660_a();
      if (this.func_73056_e()) {
         if (this.func_82736_K().func_82766_b("doDaylightCycle")) {
            long var2 = this.field_72986_A.func_76073_f() + 24000L;
            this.field_72986_A.func_76068_b(var2 - var2 % 24000L);
         }

         this.func_73053_d();
      }

      this.field_72984_F.func_76320_a("spawner");
      if (this.func_82736_K().func_82766_b("doMobSpawning") && this.field_72986_A.func_76067_t() != WorldType.field_180272_g) {
         this.field_175742_R.func_77192_a(this, this.field_72985_G, this.field_72992_H, this.field_72986_A.func_82573_f() % 400L == 0L);
         this.func_72863_F().func_203082_a(this, this.field_72985_G, this.field_72992_H);
      }

      this.field_72984_F.func_76318_c("chunkSource");
      this.field_73020_y.func_73156_b(var1);
      int var4 = this.func_72967_a(1.0F);
      if (var4 != this.func_175657_ab()) {
         this.func_175692_b(var4);
      }

      this.field_72986_A.func_82572_b(this.field_72986_A.func_82573_f() + 1L);
      if (this.func_82736_K().func_82766_b("doDaylightCycle")) {
         this.field_72986_A.func_76068_b(this.field_72986_A.func_76073_f() + 1L);
      }

      this.field_72984_F.func_76318_c("tickPending");
      this.func_72955_a();
      this.field_72984_F.func_76318_c("tickBlocks");
      this.func_147456_g();
      this.field_72984_F.func_76318_c("chunkMap");
      this.field_73063_M.func_72693_b();
      this.field_72984_F.func_76318_c("village");
      this.field_72982_D.func_75544_a();
      this.field_175740_d.func_75528_a();
      this.field_72984_F.func_76318_c("portalForcer");
      this.field_85177_Q.func_85189_a(this.func_82737_E());
      this.field_72984_F.func_76319_b();
      this.func_147488_Z();
      this.field_211159_Q = false;
   }

   public boolean func_211158_j_() {
      return this.field_211159_Q;
   }

   @Nullable
   public Biome.SpawnListEntry func_175734_a(EnumCreatureType var1, BlockPos var2) {
      List var3 = this.func_72863_F().func_177458_a(var1, var2);
      return var3.isEmpty() ? null : (Biome.SpawnListEntry)WeightedRandom.func_76271_a(this.field_73012_v, var3);
   }

   public boolean func_175732_a(EnumCreatureType var1, Biome.SpawnListEntry var2, BlockPos var3) {
      List var4 = this.func_72863_F().func_177458_a(var1, var3);
      return var4 != null && !var4.isEmpty() ? var4.contains(var2) : false;
   }

   public void func_72854_c() {
      this.field_73068_P = false;
      if (!this.field_73010_i.isEmpty()) {
         int var1 = 0;
         int var2 = 0;
         Iterator var3 = this.field_73010_i.iterator();

         while(var3.hasNext()) {
            EntityPlayer var4 = (EntityPlayer)var3.next();
            if (var4.func_175149_v()) {
               ++var1;
            } else if (var4.func_70608_bn()) {
               ++var2;
            }
         }

         this.field_73068_P = var2 > 0 && var2 >= this.field_73010_i.size() - var1;
      }

   }

   public ServerScoreboard func_96441_U() {
      return this.field_73061_a.func_200251_aP();
   }

   protected void func_73053_d() {
      this.field_73068_P = false;
      List var1 = (List)this.field_73010_i.stream().filter(EntityPlayer::func_70608_bn).collect(Collectors.toList());
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         EntityPlayer var3 = (EntityPlayer)var2.next();
         var3.func_70999_a(false, false, true);
      }

      if (this.func_82736_K().func_82766_b("doWeatherCycle")) {
         this.func_73051_P();
      }

   }

   private void func_73051_P() {
      this.field_72986_A.func_76080_g(0);
      this.field_72986_A.func_76084_b(false);
      this.field_72986_A.func_76090_f(0);
      this.field_72986_A.func_76069_a(false);
   }

   public boolean func_73056_e() {
      if (this.field_73068_P && !this.field_72995_K) {
         Iterator var1 = this.field_73010_i.iterator();

         EntityPlayer var2;
         do {
            if (!var1.hasNext()) {
               return true;
            }

            var2 = (EntityPlayer)var1.next();
         } while(var2.func_175149_v() || var2.func_71026_bH());

         return false;
      } else {
         return false;
      }
   }

   public void func_72974_f() {
      if (this.field_72986_A.func_76075_d() <= 0) {
         this.field_72986_A.func_76056_b(this.func_181545_F() + 1);
      }

      int var1 = this.field_72986_A.func_76079_c();
      int var2 = this.field_72986_A.func_76074_e();
      int var3 = 0;

      while(this.func_184141_c(new BlockPos(var1, 0, var2)).func_196958_f()) {
         var1 += this.field_73012_v.nextInt(8) - this.field_73012_v.nextInt(8);
         var2 += this.field_73012_v.nextInt(8) - this.field_73012_v.nextInt(8);
         ++var3;
         if (var3 == 10000) {
            break;
         }
      }

      this.field_72986_A.func_76058_a(var1);
      this.field_72986_A.func_76087_c(var2);
   }

   public boolean func_175680_a(int var1, int var2, boolean var3) {
      return this.func_201697_a(var1, var2);
   }

   public boolean func_201697_a(int var1, int var2) {
      return this.func_72863_F().func_73149_a(var1, var2);
   }

   protected void func_184162_i() {
      this.field_72984_F.func_76320_a("playerCheckLight");
      if (!this.field_73010_i.isEmpty()) {
         int var1 = this.field_73012_v.nextInt(this.field_73010_i.size());
         EntityPlayer var2 = (EntityPlayer)this.field_73010_i.get(var1);
         int var3 = MathHelper.func_76128_c(var2.field_70165_t) + this.field_73012_v.nextInt(11) - 5;
         int var4 = MathHelper.func_76128_c(var2.field_70163_u) + this.field_73012_v.nextInt(11) - 5;
         int var5 = MathHelper.func_76128_c(var2.field_70161_v) + this.field_73012_v.nextInt(11) - 5;
         this.func_175664_x(new BlockPos(var3, var4, var5));
      }

      this.field_72984_F.func_76319_b();
   }

   protected void func_147456_g() {
      this.func_184162_i();
      if (this.field_72986_A.func_76067_t() == WorldType.field_180272_g) {
         Iterator var19 = this.field_73063_M.func_187300_b();

         while(var19.hasNext()) {
            ((Chunk)var19.next()).func_150804_b(false);
         }

      } else {
         int var1 = this.func_82736_K().func_180263_c("randomTickSpeed");
         boolean var2 = this.func_72896_J();
         boolean var3 = this.func_72911_I();
         this.field_72984_F.func_76320_a("pollingChunks");

         for(Iterator var4 = this.field_73063_M.func_187300_b(); var4.hasNext(); this.field_72984_F.func_76319_b()) {
            this.field_72984_F.func_76320_a("getChunk");
            Chunk var5 = (Chunk)var4.next();
            int var6 = var5.field_76635_g * 16;
            int var7 = var5.field_76647_h * 16;
            this.field_72984_F.func_76318_c("checkNextLight");
            var5.func_76594_o();
            this.field_72984_F.func_76318_c("tickChunk");
            var5.func_150804_b(false);
            this.field_72984_F.func_76318_c("thunder");
            int var8;
            BlockPos var9;
            if (var2 && var3 && this.field_73012_v.nextInt(100000) == 0) {
               this.field_73005_l = this.field_73005_l * 3 + 1013904223;
               var8 = this.field_73005_l >> 2;
               var9 = this.func_175736_a(new BlockPos(var6 + (var8 & 15), 0, var7 + (var8 >> 8 & 15)));
               if (this.func_175727_C(var9)) {
                  DifficultyInstance var10 = this.func_175649_E(var9);
                  boolean var11 = this.func_82736_K().func_82766_b("doMobSpawning") && this.field_73012_v.nextDouble() < (double)var10.func_180168_b() * 0.01D;
                  if (var11) {
                     EntitySkeletonHorse var12 = new EntitySkeletonHorse(this);
                     var12.func_190691_p(true);
                     var12.func_70873_a(0);
                     var12.func_70107_b((double)var9.func_177958_n(), (double)var9.func_177956_o(), (double)var9.func_177952_p());
                     this.func_72838_d(var12);
                  }

                  this.func_72942_c(new EntityLightningBolt(this, (double)var9.func_177958_n() + 0.5D, (double)var9.func_177956_o(), (double)var9.func_177952_p() + 0.5D, var11));
               }
            }

            this.field_72984_F.func_76318_c("iceandsnow");
            if (this.field_73012_v.nextInt(16) == 0) {
               this.field_73005_l = this.field_73005_l * 3 + 1013904223;
               var8 = this.field_73005_l >> 2;
               var9 = this.func_205770_a(Heightmap.Type.MOTION_BLOCKING, new BlockPos(var6 + (var8 & 15), 0, var7 + (var8 >> 8 & 15)));
               BlockPos var22 = var9.func_177977_b();
               Biome var24 = this.func_180494_b(var9);
               if (var24.func_201848_a(this, var22)) {
                  this.func_175656_a(var22, Blocks.field_150432_aD.func_176223_P());
               }

               if (var2 && var24.func_201850_b(this, var9)) {
                  this.func_175656_a(var9, Blocks.field_150433_aE.func_176223_P());
               }

               if (var2 && this.func_180494_b(var22).func_201851_b() == Biome.RainType.RAIN) {
                  this.func_180495_p(var22).func_177230_c().func_176224_k(this, var22);
               }
            }

            this.field_72984_F.func_76318_c("tickBlocks");
            if (var1 > 0) {
               ChunkSection[] var20 = var5.func_76587_i();
               int var21 = var20.length;

               for(int var23 = 0; var23 < var21; ++var23) {
                  ChunkSection var25 = var20[var23];
                  if (var25 != Chunk.field_186036_a && var25.func_206915_b()) {
                     for(int var26 = 0; var26 < var1; ++var26) {
                        this.field_73005_l = this.field_73005_l * 3 + 1013904223;
                        int var13 = this.field_73005_l >> 2;
                        int var14 = var13 & 15;
                        int var15 = var13 >> 8 & 15;
                        int var16 = var13 >> 16 & 15;
                        IBlockState var17 = var25.func_177485_a(var14, var16, var15);
                        IFluidState var18 = var25.func_206914_b(var14, var16, var15);
                        this.field_72984_F.func_76320_a("randomTick");
                        if (var17.func_204519_t()) {
                           var17.func_196944_b(this, new BlockPos(var14 + var6, var16 + var25.func_76662_d(), var15 + var7), this.field_73012_v);
                        }

                        if (var18.func_206890_h()) {
                           var18.func_206891_b(this, new BlockPos(var14 + var6, var16 + var25.func_76662_d(), var15 + var7), this.field_73012_v);
                        }

                        this.field_72984_F.func_76319_b();
                     }
                  }
               }
            }
         }

         this.field_72984_F.func_76319_b();
      }
   }

   protected BlockPos func_175736_a(BlockPos var1) {
      BlockPos var2 = this.func_205770_a(Heightmap.Type.MOTION_BLOCKING, var1);
      AxisAlignedBB var3 = (new AxisAlignedBB(var2, new BlockPos(var2.func_177958_n(), this.func_72800_K(), var2.func_177952_p()))).func_186662_g(3.0D);
      List var4 = this.func_175647_a(EntityLivingBase.class, var3, (var1x) -> {
         return var1x != null && var1x.func_70089_S() && this.func_175678_i(var1x.func_180425_c());
      });
      if (!var4.isEmpty()) {
         return ((EntityLivingBase)var4.get(this.field_73012_v.nextInt(var4.size()))).func_180425_c();
      } else {
         if (var2.func_177956_o() == -1) {
            var2 = var2.func_177981_b(2);
         }

         return var2;
      }
   }

   public void func_72939_s() {
      if (this.field_73010_i.isEmpty()) {
         if (this.field_80004_Q++ >= 300) {
            return;
         }
      } else {
         this.func_82742_i();
      }

      this.field_73011_w.func_186059_r();
      super.func_72939_s();
   }

   protected void func_184147_l() {
      super.func_184147_l();
      this.field_72984_F.func_76318_c("players");

      for(int var1 = 0; var1 < this.field_73010_i.size(); ++var1) {
         Entity var2 = (Entity)this.field_73010_i.get(var1);
         Entity var3 = var2.func_184187_bx();
         if (var3 != null) {
            if (!var3.field_70128_L && var3.func_184196_w(var2)) {
               continue;
            }

            var2.func_184210_p();
         }

         this.field_72984_F.func_76320_a("tick");
         if (!var2.field_70128_L) {
            try {
               this.func_72870_g(var2);
            } catch (Throwable var7) {
               CrashReport var5 = CrashReport.func_85055_a(var7, "Ticking player");
               CrashReportCategory var6 = var5.func_85058_a("Player being ticked");
               var2.func_85029_a(var6);
               throw new ReportedException(var5);
            }
         }

         this.field_72984_F.func_76319_b();
         this.field_72984_F.func_76320_a("remove");
         if (var2.field_70128_L) {
            int var4 = var2.field_70176_ah;
            int var8 = var2.field_70164_aj;
            if (var2.field_70175_ag && this.func_175680_a(var4, var8, true)) {
               this.func_72964_e(var4, var8).func_76622_b(var2);
            }

            this.field_72996_f.remove(var2);
            this.func_72847_b(var2);
         }

         this.field_72984_F.func_76319_b();
      }

   }

   public void func_82742_i() {
      this.field_80004_Q = 0;
   }

   public void func_72955_a() {
      if (this.field_72986_A.func_76067_t() != WorldType.field_180272_g) {
         this.field_94579_S.func_205365_a();
         this.field_205342_P.func_205365_a();
      }
   }

   private void func_205339_a(NextTickListEntry<Fluid> var1) {
      IFluidState var2 = this.func_204610_c(var1.field_180282_a);
      if (var2.func_206886_c() == var1.func_151351_a()) {
         var2.func_206880_a(this, var1.field_180282_a);
      }

   }

   private void func_205338_b(NextTickListEntry<Block> var1) {
      IBlockState var2 = this.func_180495_p(var1.field_180282_a);
      if (var2.func_177230_c() == var1.func_151351_a()) {
         var2.func_196940_a(this, var1.field_180282_a, this.field_73012_v);
      }

   }

   public void func_72866_a(Entity var1, boolean var2) {
      if (!this.func_175735_ai() && (var1 instanceof EntityAnimal || var1 instanceof EntityWaterMob)) {
         var1.func_70106_y();
      }

      if (!this.func_175738_ah() && var1 instanceof INpc) {
         var1.func_70106_y();
      }

      super.func_72866_a(var1, var2);
   }

   private boolean func_175738_ah() {
      return this.field_73061_a.func_71220_V();
   }

   private boolean func_175735_ai() {
      return this.field_73061_a.func_71268_U();
   }

   protected IChunkProvider func_72970_h() {
      IChunkLoader var1 = this.field_73019_z.func_75763_a(this.field_73011_w);
      return new ChunkProviderServer(this, var1, this.field_73011_w.func_186060_c(), this.field_73061_a);
   }

   public boolean func_175660_a(EntityPlayer var1, BlockPos var2) {
      return !this.field_73061_a.func_175579_a(this, var2, var1) && this.func_175723_af().func_177746_a(var2);
   }

   public void func_72963_a(WorldSettings var1) {
      if (!this.field_72986_A.func_76070_v()) {
         try {
            this.func_73052_b(var1);
            if (this.field_72986_A.func_76067_t() == WorldType.field_180272_g) {
               this.func_175737_aj();
            }

            super.func_72963_a(var1);
         } catch (Throwable var6) {
            CrashReport var3 = CrashReport.func_85055_a(var6, "Exception initializing level");

            try {
               this.func_72914_a(var3);
            } catch (Throwable var5) {
            }

            throw new ReportedException(var3);
         }

         this.field_72986_A.func_76091_d(true);
      }

   }

   private void func_175737_aj() {
      this.field_72986_A.func_176128_f(false);
      this.field_72986_A.func_176121_c(true);
      this.field_72986_A.func_76084_b(false);
      this.field_72986_A.func_76069_a(false);
      this.field_72986_A.func_176142_i(1000000000);
      this.field_72986_A.func_76068_b(6000L);
      this.field_72986_A.func_76060_a(GameType.SPECTATOR);
      this.field_72986_A.func_176119_g(false);
      this.field_72986_A.func_176144_a(EnumDifficulty.PEACEFUL);
      this.field_72986_A.func_180783_e(true);
      this.func_82736_K().func_82764_b("doDaylightCycle", "false", this.field_73061_a);
   }

   private void func_73052_b(WorldSettings var1) {
      if (!this.field_73011_w.func_76567_e()) {
         this.field_72986_A.func_176143_a(BlockPos.field_177992_a.func_177981_b(this.field_73020_y.func_201711_g().func_205470_d()));
      } else if (this.field_72986_A.func_76067_t() == WorldType.field_180272_g) {
         this.field_72986_A.func_176143_a(BlockPos.field_177992_a.func_177984_a());
      } else {
         BiomeProvider var2 = this.field_73020_y.func_201711_g().func_202090_b();
         List var3 = var2.func_76932_a();
         Random var4 = new Random(this.func_72905_C());
         BlockPos var5 = var2.func_180630_a(0, 0, 256, var3, var4);
         ChunkPos var6 = var5 == null ? new ChunkPos(0, 0) : new ChunkPos(var5);
         if (var5 == null) {
            field_147491_a.warn("Unable to find spawn biome");
         }

         boolean var7 = false;
         Iterator var8 = BlockTags.field_205599_H.func_199885_a().iterator();

         while(var8.hasNext()) {
            Block var9 = (Block)var8.next();
            if (var2.func_205706_b().contains(var9.func_176223_P())) {
               var7 = true;
               break;
            }
         }

         this.field_72986_A.func_176143_a(var6.func_206849_h().func_177982_a(8, this.field_73020_y.func_201711_g().func_205470_d(), 8));
         int var15 = 0;
         int var16 = 0;
         int var10 = 0;
         int var11 = -1;
         boolean var12 = true;

         for(int var13 = 0; var13 < 1024; ++var13) {
            if (var15 > -16 && var15 <= 16 && var16 > -16 && var16 <= 16) {
               BlockPos var14 = this.field_73011_w.func_206920_a(new ChunkPos(var6.field_77276_a + var15, var6.field_77275_b + var16), var7);
               if (var14 != null) {
                  this.field_72986_A.func_176143_a(var14);
                  break;
               }
            }

            if (var15 == var16 || var15 < 0 && var15 == -var16 || var15 > 0 && var15 == 1 - var16) {
               int var17 = var10;
               var10 = -var11;
               var11 = var17;
            }

            var15 += var10;
            var16 += var11;
         }

         if (var1.func_77167_c()) {
            this.func_73047_i();
         }

      }
   }

   protected void func_73047_i() {
      BonusChestFeature var1 = new BonusChestFeature();

      for(int var2 = 0; var2 < 10; ++var2) {
         int var3 = this.field_72986_A.func_76079_c() + this.field_73012_v.nextInt(6) - this.field_73012_v.nextInt(6);
         int var4 = this.field_72986_A.func_76074_e() + this.field_73012_v.nextInt(6) - this.field_73012_v.nextInt(6);
         BlockPos var5 = this.func_205770_a(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(var3, 0, var4)).func_177984_a();
         if (var1.func_212245_a(this, this.field_73020_y.func_201711_g(), this.field_73012_v, var5, (NoFeatureConfig)IFeatureConfig.field_202429_e)) {
            break;
         }
      }

   }

   @Nullable
   public BlockPos func_180504_m() {
      return this.field_73011_w.func_177496_h();
   }

   public void func_73044_a(boolean var1, @Nullable IProgressUpdate var2) throws SessionLockException {
      ChunkProviderServer var3 = this.func_72863_F();
      if (var3.func_73157_c()) {
         if (var2 != null) {
            var2.func_200210_a(new TextComponentTranslation("menu.savingLevel", new Object[0]));
         }

         this.func_73042_a();
         if (var2 != null) {
            var2.func_200209_c(new TextComponentTranslation("menu.savingChunks", new Object[0]));
         }

         var3.func_186027_a(var1);
         ArrayList var4 = Lists.newArrayList(var3.func_189548_a());
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            Chunk var6 = (Chunk)var5.next();
            if (var6 != null && !this.field_73063_M.func_152621_a(var6.field_76635_g, var6.field_76647_h)) {
               var3.func_189549_a(var6);
            }
         }

      }
   }

   public void func_104140_m() {
      ChunkProviderServer var1 = this.func_72863_F();
      if (var1.func_73157_c()) {
         var1.func_104112_b();
      }
   }

   protected void func_73042_a() throws SessionLockException {
      this.func_72906_B();
      Iterator var1 = this.field_73061_a.func_212370_w().iterator();

      while(var1.hasNext()) {
         WorldServer var2 = (WorldServer)var1.next();
         if (var2 instanceof WorldServerMulti) {
            ((WorldServerMulti)var2).func_184166_c();
         }
      }

      this.field_72986_A.func_176145_a(this.func_175723_af().func_177741_h());
      this.field_72986_A.func_176124_d(this.func_175723_af().func_177731_f());
      this.field_72986_A.func_176141_c(this.func_175723_af().func_177721_g());
      this.field_72986_A.func_176129_e(this.func_175723_af().func_177742_m());
      this.field_72986_A.func_176125_f(this.func_175723_af().func_177727_n());
      this.field_72986_A.func_176122_j(this.func_175723_af().func_177748_q());
      this.field_72986_A.func_176136_k(this.func_175723_af().func_177740_p());
      this.field_72986_A.func_176118_b(this.func_175723_af().func_177751_j());
      this.field_72986_A.func_176135_e(this.func_175723_af().func_177732_i());
      this.field_72986_A.func_201356_c(this.field_73061_a.func_201300_aS().func_201380_c());
      this.field_73019_z.func_75755_a(this.field_72986_A, this.field_73061_a.func_184103_al().func_72378_q());
      this.func_175693_T().func_75744_a();
   }

   public boolean func_72838_d(Entity var1) {
      return this.func_184165_i(var1) ? super.func_72838_d(var1) : false;
   }

   public void func_212420_a(Stream<Entity> var1) {
      var1.forEach((var1x) -> {
         if (this.func_184165_i(var1x)) {
            this.field_72996_f.add(var1x);
            this.func_72923_a(var1x);
         }

      });
   }

   private boolean func_184165_i(Entity var1) {
      if (var1.field_70128_L) {
         field_147491_a.warn("Tried to add entity {} but it was marked as removed already", EntityType.func_200718_a(var1.func_200600_R()));
         return false;
      } else {
         UUID var2 = var1.func_110124_au();
         if (this.field_175741_N.containsKey(var2)) {
            Entity var3 = (Entity)this.field_175741_N.get(var2);
            if (this.field_72997_g.contains(var3)) {
               this.field_72997_g.remove(var3);
            } else {
               if (!(var1 instanceof EntityPlayer)) {
                  field_147491_a.warn("Keeping entity {} that already exists with UUID {}", EntityType.func_200718_a(var3.func_200600_R()), var2.toString());
                  return false;
               }

               field_147491_a.warn("Force-added player with duplicate UUID {}", var2.toString());
            }

            this.func_72973_f(var3);
         }

         return true;
      }
   }

   protected void func_72923_a(Entity var1) {
      super.func_72923_a(var1);
      this.field_175729_l.func_76038_a(var1.func_145782_y(), var1);
      this.field_175741_N.put(var1.func_110124_au(), var1);
      Entity[] var2 = var1.func_70021_al();
      if (var2 != null) {
         Entity[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Entity var6 = var3[var5];
            this.field_175729_l.func_76038_a(var6.func_145782_y(), var6);
         }
      }

   }

   protected void func_72847_b(Entity var1) {
      super.func_72847_b(var1);
      this.field_175729_l.func_76049_d(var1.func_145782_y());
      this.field_175741_N.remove(var1.func_110124_au());
      Entity[] var2 = var1.func_70021_al();
      if (var2 != null) {
         Entity[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Entity var6 = var3[var5];
            this.field_175729_l.func_76049_d(var6.func_145782_y());
         }
      }

   }

   public boolean func_72942_c(Entity var1) {
      if (super.func_72942_c(var1)) {
         this.field_73061_a.func_184103_al().func_148543_a((EntityPlayer)null, var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, 512.0D, this.field_73011_w.func_186058_p(), new SPacketSpawnGlobalEntity(var1));
         return true;
      } else {
         return false;
      }
   }

   public void func_72960_a(Entity var1, byte var2) {
      this.func_73039_n().func_151248_b(var1, new SPacketEntityStatus(var1, var2));
   }

   public ChunkProviderServer func_72863_F() {
      return (ChunkProviderServer)super.func_72863_F();
   }

   public Explosion func_211529_a(@Nullable Entity var1, DamageSource var2, double var3, double var5, double var7, float var9, boolean var10, boolean var11) {
      Explosion var12 = new Explosion(this, var1, var3, var5, var7, var9, var10, var11);
      if (var2 != null) {
         var12.func_199592_a(var2);
      }

      var12.func_77278_a();
      var12.func_77279_a(false);
      if (!var11) {
         var12.func_180342_d();
      }

      Iterator var13 = this.field_73010_i.iterator();

      while(var13.hasNext()) {
         EntityPlayer var14 = (EntityPlayer)var13.next();
         if (var14.func_70092_e(var3, var5, var7) < 4096.0D) {
            ((EntityPlayerMP)var14).field_71135_a.func_147359_a(new SPacketExplosion(var3, var5, var7, var9, var12.func_180343_e(), (Vec3d)var12.func_77277_b().get(var14)));
         }
      }

      return var12;
   }

   public void func_175641_c(BlockPos var1, Block var2, int var3, int var4) {
      this.field_147490_S.add(new BlockEventData(var1, var2, var3, var4));
   }

   private void func_147488_Z() {
      while(!this.field_147490_S.isEmpty()) {
         BlockEventData var1 = (BlockEventData)this.field_147490_S.removeFirst();
         if (this.func_147485_a(var1)) {
            this.field_73061_a.func_184103_al().func_148543_a((EntityPlayer)null, (double)var1.func_180328_a().func_177958_n(), (double)var1.func_180328_a().func_177956_o(), (double)var1.func_180328_a().func_177952_p(), 64.0D, this.field_73011_w.func_186058_p(), new SPacketBlockAction(var1.func_180328_a(), var1.func_151337_f(), var1.func_151339_d(), var1.func_151338_e()));
         }
      }

   }

   private boolean func_147485_a(BlockEventData var1) {
      IBlockState var2 = this.func_180495_p(var1.func_180328_a());
      return var2.func_177230_c() == var1.func_151337_f() ? var2.func_189547_a(this, var1.func_180328_a(), var1.func_151339_d(), var1.func_151338_e()) : false;
   }

   public void close() {
      this.field_73019_z.func_75759_a();
      super.close();
   }

   protected void func_72979_l() {
      boolean var1 = this.func_72896_J();
      super.func_72979_l();
      if (this.field_73003_n != this.field_73004_o) {
         this.field_73061_a.func_184103_al().func_148537_a(new SPacketChangeGameState(7, this.field_73004_o), this.field_73011_w.func_186058_p());
      }

      if (this.field_73018_p != this.field_73017_q) {
         this.field_73061_a.func_184103_al().func_148537_a(new SPacketChangeGameState(8, this.field_73017_q), this.field_73011_w.func_186058_p());
      }

      if (var1 != this.func_72896_J()) {
         if (var1) {
            this.field_73061_a.func_184103_al().func_148540_a(new SPacketChangeGameState(2, 0.0F));
         } else {
            this.field_73061_a.func_184103_al().func_148540_a(new SPacketChangeGameState(1, 0.0F));
         }

         this.field_73061_a.func_184103_al().func_148540_a(new SPacketChangeGameState(7, this.field_73004_o));
         this.field_73061_a.func_184103_al().func_148540_a(new SPacketChangeGameState(8, this.field_73017_q));
      }

   }

   public ServerTickList<Block> func_205220_G_() {
      return this.field_94579_S;
   }

   public ServerTickList<Fluid> func_205219_F_() {
      return this.field_205342_P;
   }

   @Nonnull
   public MinecraftServer func_73046_m() {
      return this.field_73061_a;
   }

   public EntityTracker func_73039_n() {
      return this.field_73062_L;
   }

   public PlayerChunkMap func_184164_w() {
      return this.field_73063_M;
   }

   public Teleporter func_85176_s() {
      return this.field_85177_Q;
   }

   public TemplateManager func_184163_y() {
      return this.field_73019_z.func_186340_h();
   }

   public <T extends IParticleData> int func_195598_a(T var1, double var2, double var4, double var6, int var8, double var9, double var11, double var13, double var15) {
      SPacketParticles var17 = new SPacketParticles(var1, false, (float)var2, (float)var4, (float)var6, (float)var9, (float)var11, (float)var13, (float)var15, var8);
      int var18 = 0;

      for(int var19 = 0; var19 < this.field_73010_i.size(); ++var19) {
         EntityPlayerMP var20 = (EntityPlayerMP)this.field_73010_i.get(var19);
         if (this.func_195601_a(var20, false, var2, var4, var6, var17)) {
            ++var18;
         }
      }

      return var18;
   }

   public <T extends IParticleData> boolean func_195600_a(EntityPlayerMP var1, T var2, boolean var3, double var4, double var6, double var8, int var10, double var11, double var13, double var15, double var17) {
      SPacketParticles var19 = new SPacketParticles(var2, var3, (float)var4, (float)var6, (float)var8, (float)var11, (float)var13, (float)var15, (float)var17, var10);
      return this.func_195601_a(var1, var3, var4, var6, var8, var19);
   }

   private boolean func_195601_a(EntityPlayerMP var1, boolean var2, double var3, double var5, double var7, Packet<?> var9) {
      if (var1.func_71121_q() != this) {
         return false;
      } else {
         BlockPos var10 = var1.func_180425_c();
         double var11 = var10.func_177954_c(var3, var5, var7);
         if (var11 > 1024.0D && (!var2 || var11 > 262144.0D)) {
            return false;
         } else {
            var1.field_71135_a.func_147359_a(var9);
            return true;
         }
      }
   }

   @Nullable
   public Entity func_175733_a(UUID var1) {
      return (Entity)this.field_175741_N.get(var1);
   }

   public ListenableFuture<Object> func_152344_a(Runnable var1) {
      return this.field_73061_a.func_152344_a(var1);
   }

   public boolean func_152345_ab() {
      return this.field_73061_a.func_152345_ab();
   }

   @Nullable
   public BlockPos func_211157_a(String var1, BlockPos var2, int var3, boolean var4) {
      return this.func_72863_F().func_211268_a(this, var1, var2, var3, var4);
   }

   public RecipeManager func_199532_z() {
      return this.field_73061_a.func_199529_aN();
   }

   public NetworkTagManager func_205772_D() {
      return this.field_73061_a.func_199731_aO();
   }

   // $FF: synthetic method
   public Scoreboard func_96441_U() {
      return this.func_96441_U();
   }

   // $FF: synthetic method
   public IChunkProvider func_72863_F() {
      return this.func_72863_F();
   }

   // $FF: synthetic method
   public ITickList func_205219_F_() {
      return this.func_205219_F_();
   }

   // $FF: synthetic method
   public ITickList func_205220_G_() {
      return this.func_205220_G_();
   }
}
