package net.minecraft.server;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ICommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.ServerPackFinder;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.server.management.UserListWhitelist;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ITickable;
import net.minecraft.util.Util;
import net.minecraft.util.WorldOptimizer;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.ForcedChunksSaveData;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.ServerWorldEventHandler;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;
import net.minecraft.world.storage.loot.LootTableManager;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer implements IThreadListener, ISnooperInfo, ICommandSource, Runnable {
   private static final Logger field_147145_h = LogManager.getLogger();
   public static final File field_152367_a = new File("usercache.json");
   private final ISaveFormat field_71310_m;
   private final Snooper field_71307_n = new Snooper("server", this, Util.func_211177_b());
   private final File field_71308_o;
   private final List<ITickable> field_71322_p = Lists.newArrayList();
   public final Profiler field_71304_b = new Profiler();
   private final NetworkSystem field_147144_o;
   private final ServerStatusResponse field_147147_p = new ServerStatusResponse();
   private final Random field_147146_q = new Random();
   private final DataFixer field_184112_s;
   private String field_71320_r;
   private int field_71319_s = -1;
   private final Map<DimensionType, WorldServer> field_71305_c = Maps.newIdentityHashMap();
   private PlayerList field_71318_t;
   private boolean field_71317_u = true;
   private boolean field_71316_v;
   private int field_71315_w;
   protected final Proxy field_110456_c;
   private ITextComponent field_71302_d;
   private int field_71303_e;
   private boolean field_71325_x;
   private boolean field_190519_A;
   private boolean field_71324_y;
   private boolean field_71323_z;
   private boolean field_71284_A;
   private boolean field_71285_B;
   private String field_71286_C;
   private int field_71280_D;
   private int field_143008_E;
   public final long[] field_71311_j = new long[100];
   protected final Map<DimensionType, long[]> field_71312_k = Maps.newIdentityHashMap();
   private KeyPair field_71292_I;
   private String field_71293_J;
   private String field_71294_K;
   private String field_71287_L;
   private boolean field_71288_M;
   private boolean field_71289_N;
   private String field_147141_M = "";
   private String field_175588_P = "";
   private boolean field_71296_Q;
   private long field_71299_R;
   private ITextComponent field_71298_S;
   private boolean field_71295_T;
   private boolean field_104057_T;
   private final YggdrasilAuthenticationService field_152364_T;
   private final MinecraftSessionService field_147143_S;
   private final GameProfileRepository field_152365_W;
   private final PlayerProfileCache field_152366_X;
   private long field_147142_T;
   public final Queue<FutureTask<?>> field_175589_i = Queues.newConcurrentLinkedQueue();
   private Thread field_175590_aa;
   private long field_211151_aa = Util.func_211177_b();
   private boolean field_184111_ab;
   private final IReloadableResourceManager field_195576_ac;
   private final ResourcePackList<ResourcePackInfo> field_195577_ad;
   private FolderPackFinder field_195578_ae;
   private final Commands field_195579_af;
   private final RecipeManager field_199530_ag;
   private final NetworkTagManager field_199736_ah;
   private final ServerScoreboard field_200255_ai;
   private final CustomBossEvents field_201301_aj;
   private final LootTableManager field_200256_aj;
   private final AdvancementManager field_200257_ak;
   private final FunctionManager field_200258_al;
   private boolean field_205745_an;
   private boolean field_212205_ao;
   private float field_211152_ao;

   public MinecraftServer(@Nullable File var1, Proxy var2, DataFixer var3, Commands var4, YggdrasilAuthenticationService var5, MinecraftSessionService var6, GameProfileRepository var7, PlayerProfileCache var8) {
      super();
      this.field_195576_ac = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA);
      this.field_195577_ad = new ResourcePackList(ResourcePackInfo::new);
      this.field_199530_ag = new RecipeManager();
      this.field_199736_ah = new NetworkTagManager();
      this.field_200255_ai = new ServerScoreboard(this);
      this.field_201301_aj = new CustomBossEvents(this);
      this.field_200256_aj = new LootTableManager();
      this.field_200257_ak = new AdvancementManager();
      this.field_200258_al = new FunctionManager(this);
      this.field_110456_c = var2;
      this.field_195579_af = var4;
      this.field_152364_T = var5;
      this.field_147143_S = var6;
      this.field_152365_W = var7;
      this.field_152366_X = var8;
      this.field_71308_o = var1;
      this.field_147144_o = var1 == null ? null : new NetworkSystem(this);
      this.field_71310_m = var1 == null ? null : new AnvilSaveConverter(var1.toPath(), var1.toPath().resolve("../backups"), var3);
      this.field_184112_s = var3;
      this.field_195576_ac.func_199006_a(this.field_199736_ah);
      this.field_195576_ac.func_199006_a(this.field_199530_ag);
      this.field_195576_ac.func_199006_a(this.field_200256_aj);
      this.field_195576_ac.func_199006_a(this.field_200258_al);
      this.field_195576_ac.func_199006_a(this.field_200257_ak);
   }

   public abstract boolean func_71197_b() throws IOException;

   public void func_71237_c(String var1) {
      if (this.func_71254_M().func_75801_b(var1)) {
         field_147145_h.info("Converting map!");
         this.func_200245_b(new TextComponentTranslation("menu.convertingLevel", new Object[0]));
         this.func_71254_M().func_75805_a(var1, new IProgressUpdate() {
            private long field_96245_b = Util.func_211177_b();

            public void func_200210_a(ITextComponent var1) {
            }

            public void func_200211_b(ITextComponent var1) {
            }

            public void func_73718_a(int var1) {
               if (Util.func_211177_b() - this.field_96245_b >= 1000L) {
                  this.field_96245_b = Util.func_211177_b();
                  MinecraftServer.field_147145_h.info("Converting... {}%", var1);
               }

            }

            public void func_146586_a() {
            }

            public void func_200209_c(ITextComponent var1) {
            }
         });
      }

      if (this.field_212205_ao) {
         field_147145_h.info("Forcing world upgrade!");
         WorldInfo var2 = this.func_71254_M().func_75803_c(this.func_71270_I());
         if (var2 != null) {
            WorldOptimizer var3 = new WorldOptimizer(this.func_71270_I(), this.func_71254_M(), var2);
            ITextComponent var4 = null;

            while(!var3.func_212218_b()) {
               ITextComponent var5 = var3.func_212215_m();
               if (var4 != var5) {
                  var4 = var5;
                  field_147145_h.info(var3.func_212215_m().getString());
               }

               int var6 = var3.func_212211_j();
               if (var6 > 0) {
                  int var7 = var3.func_212208_k() + var3.func_212209_l();
                  field_147145_h.info("{}% completed ({} / {} chunks)...", MathHelper.func_76141_d((float)var7 / (float)var6 * 100.0F), var7, var6);
               }

               if (this.func_71241_aa()) {
                  var3.func_212217_a();
               } else {
                  try {
                     Thread.sleep(1000L);
                  } catch (InterruptedException var8) {
                  }
               }
            }
         }
      }

   }

   protected synchronized void func_200245_b(ITextComponent var1) {
      this.field_71298_S = var1;
   }

   @Nullable
   public synchronized ITextComponent func_200253_h_() {
      return this.field_71298_S;
   }

   public void func_71247_a(String var1, String var2, long var3, WorldType var5, JsonElement var6) {
      this.func_71237_c(var1);
      this.func_200245_b(new TextComponentTranslation("menu.loadingLevel", new Object[0]));
      ISaveHandler var7 = this.func_71254_M().func_197715_a(var1, this);
      this.func_175584_a(this.func_71270_I(), var7);
      WorldInfo var9 = var7.func_75757_d();
      WorldSettings var8;
      if (var9 == null) {
         if (this.func_71242_L()) {
            var8 = WorldServerDemo.field_73071_a;
         } else {
            var8 = new WorldSettings(var3, this.func_71265_f(), this.func_71225_e(), this.func_71199_h(), var5);
            var8.func_205390_a(var6);
            if (this.field_71289_N) {
               var8.func_77159_a();
            }
         }

         var9 = new WorldInfo(var8, var2);
      } else {
         var9.func_76062_a(var2);
         var8 = new WorldSettings(var9);
      }

      this.func_195560_a(var7.func_75765_b(), var9);
      WorldSavedDataStorage var10 = new WorldSavedDataStorage(var7);
      this.func_212369_a(var7, var10, var9, var8);
      this.func_147139_a(this.func_147135_j());
      this.func_71222_d(var10);
   }

   public void func_212369_a(ISaveHandler var1, WorldSavedDataStorage var2, WorldInfo var3, WorldSettings var4) {
      if (this.func_71242_L()) {
         this.field_71305_c.put(DimensionType.OVERWORLD, (new WorldServerDemo(this, var1, var2, var3, DimensionType.OVERWORLD, this.field_71304_b)).func_212251_i__());
      } else {
         this.field_71305_c.put(DimensionType.OVERWORLD, (new WorldServer(this, var1, var2, var3, DimensionType.OVERWORLD, this.field_71304_b)).func_212251_i__());
      }

      WorldServer var5 = this.func_71218_a(DimensionType.OVERWORLD);
      var5.func_72963_a(var4);
      var5.func_72954_a(new ServerWorldEventHandler(this, var5));
      if (!this.func_71264_H()) {
         var5.func_72912_H().func_76060_a(this.func_71265_f());
      }

      WorldServerMulti var6 = (new WorldServerMulti(this, var1, DimensionType.NETHER, var5, this.field_71304_b)).func_212251_i__();
      this.field_71305_c.put(DimensionType.NETHER, var6);
      var6.func_72954_a(new ServerWorldEventHandler(this, var6));
      if (!this.func_71264_H()) {
         var6.func_72912_H().func_76060_a(this.func_71265_f());
      }

      WorldServerMulti var7 = (new WorldServerMulti(this, var1, DimensionType.THE_END, var5, this.field_71304_b)).func_212251_i__();
      this.field_71305_c.put(DimensionType.THE_END, var7);
      var7.func_72954_a(new ServerWorldEventHandler(this, var7));
      if (!this.func_71264_H()) {
         var7.func_72912_H().func_76060_a(this.func_71265_f());
      }

      this.func_184103_al().func_212504_a(var5);
      if (var3.func_201357_P() != null) {
         this.func_201300_aS().func_201381_a(var3.func_201357_P());
      }

   }

   public void func_195560_a(File var1, WorldInfo var2) {
      this.field_195577_ad.func_198982_a(new ServerPackFinder());
      this.field_195578_ae = new FolderPackFinder(new File(var1, "datapacks"));
      this.field_195577_ad.func_198982_a(this.field_195578_ae);
      this.field_195577_ad.func_198983_a();
      ArrayList var3 = Lists.newArrayList();
      Iterator var4 = var2.func_197720_O().iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         ResourcePackInfo var6 = this.field_195577_ad.func_198981_a(var5);
         if (var6 != null) {
            var3.add(var6);
         } else {
            field_147145_h.warn("Missing data pack {}", var5);
         }
      }

      this.field_195577_ad.func_198985_a(var3);
      this.func_195568_a(var2);
   }

   public void func_71222_d(WorldSavedDataStorage var1) {
      boolean var2 = true;
      boolean var3 = true;
      boolean var4 = true;
      boolean var5 = true;
      boolean var6 = true;
      this.func_200245_b(new TextComponentTranslation("menu.generatingTerrain", new Object[0]));
      WorldServer var7 = this.func_71218_a(DimensionType.OVERWORLD);
      field_147145_h.info("Preparing start region for dimension " + DimensionType.func_212678_a(var7.field_73011_w.func_186058_p()));
      BlockPos var8 = var7.func_175694_M();
      ArrayList var9 = Lists.newArrayList();
      Set var10 = Sets.newConcurrentHashSet();
      Stopwatch var11 = Stopwatch.createStarted();

      for(int var12 = -192; var12 <= 192 && this.func_71278_l(); var12 += 16) {
         for(int var13 = -192; var13 <= 192 && this.func_71278_l(); var13 += 16) {
            var9.add(new ChunkPos(var8.func_177958_n() + var12 >> 4, var8.func_177952_p() + var13 >> 4));
         }

         CompletableFuture var24 = var7.func_72863_F().func_201720_a(var9, (var1x) -> {
            var10.add(var1x.func_76632_l());
         });

         while(!var24.isDone()) {
            try {
               var24.get(1L, TimeUnit.SECONDS);
            } catch (InterruptedException var20) {
               throw new RuntimeException(var20);
            } catch (ExecutionException var21) {
               if (var21.getCause() instanceof RuntimeException) {
                  throw (RuntimeException)var21.getCause();
               }

               throw new RuntimeException(var21.getCause());
            } catch (TimeoutException var22) {
               this.func_200250_a(new TextComponentTranslation("menu.preparingSpawn", new Object[0]), var10.size() * 100 / 625);
            }
         }

         this.func_200250_a(new TextComponentTranslation("menu.preparingSpawn", new Object[0]), var10.size() * 100 / 625);
      }

      field_147145_h.info("Time elapsed: {} ms", var11.elapsed(TimeUnit.MILLISECONDS));
      Iterator var23 = DimensionType.func_212681_b().iterator();

      while(true) {
         ForcedChunksSaveData var14;
         DimensionType var25;
         do {
            if (!var23.hasNext()) {
               this.func_71243_i();
               return;
            }

            var25 = (DimensionType)var23.next();
            var14 = (ForcedChunksSaveData)var1.func_212426_a(var25, ForcedChunksSaveData::new, "chunks");
         } while(var14 == null);

         WorldServer var15 = this.func_71218_a(var25);
         LongIterator var16 = var14.func_212438_a().iterator();

         while(var16.hasNext()) {
            this.func_200250_a(new TextComponentTranslation("menu.loadingForcedChunks", new Object[]{var25}), var14.func_212438_a().size() * 100 / 625);
            long var17 = var16.nextLong();
            ChunkPos var19 = new ChunkPos(var17);
            var15.func_72863_F().func_186025_d(var19.field_77276_a, var19.field_77275_b, true, true);
         }
      }
   }

   public void func_175584_a(String var1, ISaveHandler var2) {
      File var3 = new File(var2.func_75765_b(), "resources.zip");
      if (var3.isFile()) {
         try {
            this.func_180507_a_("level://" + URLEncoder.encode(var1, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
         } catch (UnsupportedEncodingException var5) {
            field_147145_h.warn("Something went wrong url encoding {}", var1);
         }
      }

   }

   public abstract boolean func_71225_e();

   public abstract GameType func_71265_f();

   public abstract EnumDifficulty func_147135_j();

   public abstract boolean func_71199_h();

   public abstract int func_110455_j();

   public abstract boolean func_195569_l();

   protected void func_200250_a(ITextComponent var1, int var2) {
      this.field_71302_d = var1;
      this.field_71303_e = var2;
      field_147145_h.info("{}: {}%", var1.getString(), var2);
   }

   protected void func_71243_i() {
      this.field_71302_d = null;
      this.field_71303_e = 0;
   }

   public void func_71267_a(boolean var1) {
      Iterator var2 = this.func_212370_w().iterator();

      while(var2.hasNext()) {
         WorldServer var3 = (WorldServer)var2.next();
         if (var3 != null) {
            if (!var1) {
               field_147145_h.info("Saving chunks for level '{}'/{}", var3.func_72912_H().func_76065_j(), DimensionType.func_212678_a(var3.field_73011_w.func_186058_p()));
            }

            try {
               var3.func_73044_a(true, (IProgressUpdate)null);
            } catch (SessionLockException var5) {
               field_147145_h.warn(var5.getMessage());
            }
         }
      }

   }

   public void func_71260_j() {
      field_147145_h.info("Stopping server");
      if (this.func_147137_ag() != null) {
         this.func_147137_ag().func_151268_b();
      }

      if (this.field_71318_t != null) {
         field_147145_h.info("Saving players");
         this.field_71318_t.func_72389_g();
         this.field_71318_t.func_72392_r();
      }

      field_147145_h.info("Saving worlds");
      Iterator var1 = this.func_212370_w().iterator();

      WorldServer var2;
      while(var1.hasNext()) {
         var2 = (WorldServer)var1.next();
         if (var2 != null) {
            var2.field_73058_d = false;
         }
      }

      this.func_71267_a(false);
      var1 = this.func_212370_w().iterator();

      while(var1.hasNext()) {
         var2 = (WorldServer)var1.next();
         if (var2 != null) {
            var2.close();
         }
      }

      if (this.field_71307_n.func_76468_d()) {
         this.field_71307_n.func_76470_e();
      }

   }

   public String func_71211_k() {
      return this.field_71320_r;
   }

   public void func_71189_e(String var1) {
      this.field_71320_r = var1;
   }

   public boolean func_71278_l() {
      return this.field_71317_u;
   }

   public void func_71263_m() {
      this.field_71317_u = false;
   }

   private boolean func_212379_aT() {
      return Util.func_211177_b() < this.field_211151_aa;
   }

   public void run() {
      try {
         if (this.func_71197_b()) {
            this.field_211151_aa = Util.func_211177_b();
            this.field_147147_p.func_151315_a(new TextComponentString(this.field_71286_C));
            this.field_147147_p.func_151321_a(new ServerStatusResponse.Version("1.13.2", 404));
            this.func_184107_a(this.field_147147_p);

            while(this.field_71317_u) {
               long var1 = Util.func_211177_b() - this.field_211151_aa;
               if (var1 > 2000L && this.field_211151_aa - this.field_71299_R >= 15000L) {
                  long var46 = var1 / 50L;
                  field_147145_h.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", var1, var46);
                  this.field_211151_aa += var46 * 50L;
                  this.field_71299_R = this.field_211151_aa;
               }

               this.func_71217_p(this::func_212379_aT);
               this.field_211151_aa += 50L;

               while(this.func_212379_aT()) {
                  Thread.sleep(1L);
               }

               this.field_71296_Q = true;
            }
         } else {
            this.func_71228_a((CrashReport)null);
         }
      } catch (Throwable var44) {
         field_147145_h.error("Encountered an unexpected exception", var44);
         CrashReport var2;
         if (var44 instanceof ReportedException) {
            var2 = this.func_71230_b(((ReportedException)var44).func_71575_a());
         } else {
            var2 = this.func_71230_b(new CrashReport("Exception in server tick loop", var44));
         }

         File var3 = new File(new File(this.func_71238_n(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
         if (var2.func_147149_a(var3)) {
            field_147145_h.error("This crash report has been saved to: {}", var3.getAbsolutePath());
         } else {
            field_147145_h.error("We were unable to save this crash report to disk.");
         }

         this.func_71228_a(var2);
      } finally {
         try {
            this.field_71316_v = true;
            this.func_71260_j();
         } catch (Throwable var42) {
            field_147145_h.error("Exception stopping the server", var42);
         } finally {
            this.func_71240_o();
         }

      }

   }

   public void func_184107_a(ServerStatusResponse var1) {
      File var2 = this.func_71209_f("server-icon.png");
      if (!var2.exists()) {
         var2 = this.func_71254_M().func_186352_b(this.func_71270_I(), "icon.png");
      }

      if (var2.isFile()) {
         ByteBuf var3 = Unpooled.buffer();

         try {
            BufferedImage var4 = ImageIO.read(var2);
            Validate.validState(var4.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
            Validate.validState(var4.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
            ImageIO.write(var4, "PNG", new ByteBufOutputStream(var3));
            ByteBuffer var5 = Base64.getEncoder().encode(var3.nioBuffer());
            var1.func_151320_a("data:image/png;base64," + StandardCharsets.UTF_8.decode(var5));
         } catch (Exception var9) {
            field_147145_h.error("Couldn't load server icon", var9);
         } finally {
            var3.release();
         }
      }

   }

   public boolean func_184106_y() {
      this.field_184111_ab = this.field_184111_ab || this.func_184109_z().isFile();
      return this.field_184111_ab;
   }

   public File func_184109_z() {
      return this.func_71254_M().func_186352_b(this.func_71270_I(), "icon.png");
   }

   public File func_71238_n() {
      return new File(".");
   }

   public void func_71228_a(CrashReport var1) {
   }

   public void func_71240_o() {
   }

   public void func_71217_p(BooleanSupplier var1) {
      long var2 = Util.func_211178_c();
      ++this.field_71315_w;
      if (this.field_71295_T) {
         this.field_71295_T = false;
         this.field_71304_b.func_199095_a(this.field_71315_w);
      }

      this.field_71304_b.func_76320_a("root");
      this.func_71190_q(var1);
      if (var2 - this.field_147142_T >= 5000000000L) {
         this.field_147142_T = var2;
         this.field_147147_p.func_151319_a(new ServerStatusResponse.Players(this.func_71275_y(), this.func_71233_x()));
         GameProfile[] var4 = new GameProfile[Math.min(this.func_71233_x(), 12)];
         int var5 = MathHelper.func_76136_a(this.field_147146_q, 0, this.func_71233_x() - var4.length);

         for(int var6 = 0; var6 < var4.length; ++var6) {
            var4[var6] = ((EntityPlayerMP)this.field_71318_t.func_181057_v().get(var5 + var6)).func_146103_bH();
         }

         Collections.shuffle(Arrays.asList(var4));
         this.field_147147_p.func_151318_b().func_151330_a(var4);
      }

      if (this.field_71315_w % 900 == 0) {
         this.field_71304_b.func_76320_a("save");
         this.field_71318_t.func_72389_g();
         this.func_71267_a(true);
         this.field_71304_b.func_76319_b();
      }

      this.field_71304_b.func_76320_a("snooper");
      if (!this.field_71307_n.func_76468_d() && this.field_71315_w > 100) {
         this.field_71307_n.func_76463_a();
      }

      if (this.field_71315_w % 6000 == 0) {
         this.field_71307_n.func_76471_b();
      }

      this.field_71304_b.func_76319_b();
      this.field_71304_b.func_76320_a("tallying");
      long var7 = this.field_71311_j[this.field_71315_w % 100] = Util.func_211178_c() - var2;
      this.field_211152_ao = this.field_211152_ao * 0.8F + (float)var7 / 1000000.0F * 0.19999999F;
      this.field_71304_b.func_76319_b();
      this.field_71304_b.func_76319_b();
   }

   public void func_71190_q(BooleanSupplier var1) {
      this.field_71304_b.func_76320_a("jobs");

      FutureTask var2;
      while((var2 = (FutureTask)this.field_175589_i.poll()) != null) {
         Util.func_181617_a(var2, field_147145_h);
      }

      this.field_71304_b.func_76318_c("commandFunctions");
      this.func_193030_aL().func_73660_a();
      this.field_71304_b.func_76318_c("levels");

      WorldServer var4;
      long var5;
      for(Iterator var3 = this.func_212370_w().iterator(); var3.hasNext(); ((long[])this.field_71312_k.computeIfAbsent(var4.field_73011_w.func_186058_p(), (var0) -> {
         return new long[100];
      }))[this.field_71315_w % 100] = Util.func_211178_c() - var5) {
         var4 = (WorldServer)var3.next();
         var5 = Util.func_211178_c();
         if (var4.field_73011_w.func_186058_p() == DimensionType.OVERWORLD || this.func_71255_r()) {
            this.field_71304_b.func_194340_a(() -> {
               return "dim-" + var4.field_73011_w.func_186058_p().func_186068_a();
            });
            if (this.field_71315_w % 20 == 0) {
               this.field_71304_b.func_76320_a("timeSync");
               this.field_71318_t.func_148537_a(new SPacketTimeUpdate(var4.func_82737_E(), var4.func_72820_D(), var4.func_82736_K().func_82766_b("doDaylightCycle")), var4.field_73011_w.func_186058_p());
               this.field_71304_b.func_76319_b();
            }

            this.field_71304_b.func_76320_a("tick");

            CrashReport var8;
            try {
               var4.func_72835_b(var1);
            } catch (Throwable var10) {
               var8 = CrashReport.func_85055_a(var10, "Exception ticking world");
               var4.func_72914_a(var8);
               throw new ReportedException(var8);
            }

            try {
               var4.func_72939_s();
            } catch (Throwable var9) {
               var8 = CrashReport.func_85055_a(var9, "Exception ticking world entities");
               var4.func_72914_a(var8);
               throw new ReportedException(var8);
            }

            this.field_71304_b.func_76319_b();
            this.field_71304_b.func_76320_a("tracker");
            var4.func_73039_n().func_72788_a();
            this.field_71304_b.func_76319_b();
            this.field_71304_b.func_76319_b();
         }
      }

      this.field_71304_b.func_76318_c("connection");
      this.func_147137_ag().func_151269_c();
      this.field_71304_b.func_76318_c("players");
      this.field_71318_t.func_72374_b();
      this.field_71304_b.func_76318_c("tickables");

      for(int var11 = 0; var11 < this.field_71322_p.size(); ++var11) {
         ((ITickable)this.field_71322_p.get(var11)).func_73660_a();
      }

      this.field_71304_b.func_76319_b();
   }

   public boolean func_71255_r() {
      return true;
   }

   public void func_82010_a(ITickable var1) {
      this.field_71322_p.add(var1);
   }

   public static void main(String[] var0) {
      Bootstrap.func_151354_b();

      try {
         boolean var1 = true;
         String var2 = null;
         String var3 = ".";
         String var4 = null;
         boolean var5 = false;
         boolean var6 = false;
         boolean var7 = false;
         int var8 = -1;

         for(int var9 = 0; var9 < var0.length; ++var9) {
            String var10 = var0[var9];
            String var11 = var9 == var0.length - 1 ? null : var0[var9 + 1];
            boolean var12 = false;
            if (!"nogui".equals(var10) && !"--nogui".equals(var10)) {
               if ("--port".equals(var10) && var11 != null) {
                  var12 = true;

                  try {
                     var8 = Integer.parseInt(var11);
                  } catch (NumberFormatException var15) {
                  }
               } else if ("--singleplayer".equals(var10) && var11 != null) {
                  var12 = true;
                  var2 = var11;
               } else if ("--universe".equals(var10) && var11 != null) {
                  var12 = true;
                  var3 = var11;
               } else if ("--world".equals(var10) && var11 != null) {
                  var12 = true;
                  var4 = var11;
               } else if ("--demo".equals(var10)) {
                  var5 = true;
               } else if ("--bonusChest".equals(var10)) {
                  var6 = true;
               } else if ("--forceUpgrade".equals(var10)) {
                  var7 = true;
               }
            } else {
               var1 = false;
            }

            if (var12) {
               ++var9;
            }
         }

         YggdrasilAuthenticationService var17 = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
         MinecraftSessionService var18 = var17.createMinecraftSessionService();
         GameProfileRepository var19 = var17.createProfileRepository();
         PlayerProfileCache var20 = new PlayerProfileCache(var19, new File(var3, field_152367_a.getName()));
         final DedicatedServer var13 = new DedicatedServer(new File(var3), DataFixesManager.func_210901_a(), var17, var18, var19, var20);
         if (var2 != null) {
            var13.func_71224_l(var2);
         }

         if (var4 != null) {
            var13.func_71261_m(var4);
         }

         if (var8 >= 0) {
            var13.func_71208_b(var8);
         }

         if (var5) {
            var13.func_71204_b(true);
         }

         if (var6) {
            var13.func_71194_c(true);
         }

         if (var1 && !GraphicsEnvironment.isHeadless()) {
            var13.func_120011_ar();
         }

         if (var7) {
            var13.func_212204_b(true);
         }

         var13.func_71256_s();
         Thread var14 = new Thread("Server Shutdown Thread") {
            public void run() {
               var13.func_71260_j();
            }
         };
         var14.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(field_147145_h));
         Runtime.getRuntime().addShutdownHook(var14);
      } catch (Exception var16) {
         field_147145_h.fatal("Failed to start the minecraft server", var16);
      }

   }

   protected void func_212204_b(boolean var1) {
      this.field_212205_ao = var1;
   }

   public void func_71256_s() {
      this.field_175590_aa = new Thread(this, "Server thread");
      this.field_175590_aa.setUncaughtExceptionHandler((var0, var1) -> {
         field_147145_h.error(var1);
      });
      this.field_175590_aa.start();
   }

   public File func_71209_f(String var1) {
      return new File(this.func_71238_n(), var1);
   }

   public void func_71244_g(String var1) {
      field_147145_h.info(var1);
   }

   public void func_71236_h(String var1) {
      field_147145_h.warn(var1);
   }

   public WorldServer func_71218_a(DimensionType var1) {
      return (WorldServer)this.field_71305_c.get(var1);
   }

   public Iterable<WorldServer> func_212370_w() {
      return this.field_71305_c.values();
   }

   public String func_71249_w() {
      return "1.13.2";
   }

   public int func_71233_x() {
      return this.field_71318_t.func_72394_k();
   }

   public int func_71275_y() {
      return this.field_71318_t.func_72352_l();
   }

   public String[] func_71213_z() {
      return this.field_71318_t.func_72369_d();
   }

   public boolean func_71239_B() {
      return false;
   }

   public void func_71201_j(String var1) {
      field_147145_h.error(var1);
   }

   public void func_71198_k(String var1) {
      if (this.func_71239_B()) {
         field_147145_h.info(var1);
      }

   }

   public String getServerModName() {
      return "vanilla";
   }

   public CrashReport func_71230_b(CrashReport var1) {
      var1.func_85056_g().func_189529_a("Profiler Position", () -> {
         return this.field_71304_b.func_199094_a() ? this.field_71304_b.func_76322_c() : "N/A (disabled)";
      });
      if (this.field_71318_t != null) {
         var1.func_85056_g().func_189529_a("Player Count", () -> {
            return this.field_71318_t.func_72394_k() + " / " + this.field_71318_t.func_72352_l() + "; " + this.field_71318_t.func_181057_v();
         });
      }

      var1.func_85056_g().func_189529_a("Data Packs", () -> {
         StringBuilder var1 = new StringBuilder();
         Iterator var2 = this.field_195577_ad.func_198980_d().iterator();

         while(var2.hasNext()) {
            ResourcePackInfo var3 = (ResourcePackInfo)var2.next();
            if (var1.length() > 0) {
               var1.append(", ");
            }

            var1.append(var3.func_195790_f());
            if (!var3.func_195791_d().func_198968_a()) {
               var1.append(" (incompatible)");
            }
         }

         return var1.toString();
      });
      return var1;
   }

   public boolean func_175578_N() {
      return this.field_71308_o != null;
   }

   public void func_145747_a(ITextComponent var1) {
      field_147145_h.info(var1.getString());
   }

   public KeyPair func_71250_E() {
      return this.field_71292_I;
   }

   public int func_71215_F() {
      return this.field_71319_s;
   }

   public void func_71208_b(int var1) {
      this.field_71319_s = var1;
   }

   public String func_71214_G() {
      return this.field_71293_J;
   }

   public void func_71224_l(String var1) {
      this.field_71293_J = var1;
   }

   public boolean func_71264_H() {
      return this.field_71293_J != null;
   }

   public String func_71270_I() {
      return this.field_71294_K;
   }

   public void func_71261_m(String var1) {
      this.field_71294_K = var1;
   }

   public void func_71246_n(String var1) {
      this.field_71287_L = var1;
   }

   public String func_71221_J() {
      return this.field_71287_L;
   }

   public void func_71253_a(KeyPair var1) {
      this.field_71292_I = var1;
   }

   public void func_147139_a(EnumDifficulty var1) {
      Iterator var2 = this.func_212370_w().iterator();

      while(var2.hasNext()) {
         WorldServer var3 = (WorldServer)var2.next();
         if (var3.func_72912_H().func_76093_s()) {
            var3.func_72912_H().func_176144_a(EnumDifficulty.HARD);
            var3.func_72891_a(true, true);
         } else if (this.func_71264_H()) {
            var3.func_72912_H().func_176144_a(var1);
            var3.func_72891_a(var3.func_175659_aa() != EnumDifficulty.PEACEFUL, true);
         } else {
            var3.func_72912_H().func_176144_a(var1);
            var3.func_72891_a(this.func_71193_K(), this.field_71324_y);
         }
      }

   }

   public boolean func_71193_K() {
      return true;
   }

   public boolean func_71242_L() {
      return this.field_71288_M;
   }

   public void func_71204_b(boolean var1) {
      this.field_71288_M = var1;
   }

   public void func_71194_c(boolean var1) {
      this.field_71289_N = var1;
   }

   public ISaveFormat func_71254_M() {
      return this.field_71310_m;
   }

   public String func_147133_T() {
      return this.field_147141_M;
   }

   public String func_175581_ab() {
      return this.field_175588_P;
   }

   public void func_180507_a_(String var1, String var2) {
      this.field_147141_M = var1;
      this.field_175588_P = var2;
   }

   public void func_70000_a(Snooper var1) {
      var1.func_152768_a("whitelist_enabled", false);
      var1.func_152768_a("whitelist_count", 0);
      if (this.field_71318_t != null) {
         var1.func_152768_a("players_current", this.func_71233_x());
         var1.func_152768_a("players_max", this.func_71275_y());
         var1.func_152768_a("players_seen", this.field_71318_t.func_72373_m().length);
      }

      var1.func_152768_a("uses_auth", this.field_71325_x);
      var1.func_152768_a("gui_state", this.func_71279_ae() ? "enabled" : "disabled");
      var1.func_152768_a("run_time", (Util.func_211177_b() - var1.func_130105_g()) / 60L * 1000L);
      var1.func_152768_a("avg_tick_ms", (int)(MathHelper.func_76127_a(this.field_71311_j) * 1.0E-6D));
      int var2 = 0;
      Iterator var3 = this.func_212370_w().iterator();

      while(var3.hasNext()) {
         WorldServer var4 = (WorldServer)var3.next();
         if (var4 != null) {
            WorldInfo var5 = var4.func_72912_H();
            var1.func_152768_a("world[" + var2 + "][dimension]", var4.field_73011_w.func_186058_p());
            var1.func_152768_a("world[" + var2 + "][mode]", var5.func_76077_q());
            var1.func_152768_a("world[" + var2 + "][difficulty]", var4.func_175659_aa());
            var1.func_152768_a("world[" + var2 + "][hardcore]", var5.func_76093_s());
            var1.func_152768_a("world[" + var2 + "][generator_name]", var5.func_76067_t().func_211888_a());
            var1.func_152768_a("world[" + var2 + "][generator_version]", var5.func_76067_t().func_77131_c());
            var1.func_152768_a("world[" + var2 + "][height]", this.field_71280_D);
            var1.func_152768_a("world[" + var2 + "][chunks_loaded]", var4.func_72863_F().func_73152_e());
            ++var2;
         }
      }

      var1.func_152768_a("worlds", var2);
   }

   public boolean func_70002_Q() {
      return true;
   }

   public abstract boolean func_71262_S();

   public boolean func_71266_T() {
      return this.field_71325_x;
   }

   public void func_71229_d(boolean var1) {
      this.field_71325_x = var1;
   }

   public boolean func_190518_ac() {
      return this.field_190519_A;
   }

   public void func_190517_e(boolean var1) {
      this.field_190519_A = var1;
   }

   public boolean func_71268_U() {
      return this.field_71324_y;
   }

   public void func_71251_e(boolean var1) {
      this.field_71324_y = var1;
   }

   public boolean func_71220_V() {
      return this.field_71323_z;
   }

   public abstract boolean func_181035_ah();

   public void func_71257_f(boolean var1) {
      this.field_71323_z = var1;
   }

   public boolean func_71219_W() {
      return this.field_71284_A;
   }

   public void func_71188_g(boolean var1) {
      this.field_71284_A = var1;
   }

   public boolean func_71231_X() {
      return this.field_71285_B;
   }

   public void func_71245_h(boolean var1) {
      this.field_71285_B = var1;
   }

   public abstract boolean func_82356_Z();

   public String func_71273_Y() {
      return this.field_71286_C;
   }

   public void func_71205_p(String var1) {
      this.field_71286_C = var1;
   }

   public int func_71207_Z() {
      return this.field_71280_D;
   }

   public void func_71191_d(int var1) {
      this.field_71280_D = var1;
   }

   public boolean func_71241_aa() {
      return this.field_71316_v;
   }

   public PlayerList func_184103_al() {
      return this.field_71318_t;
   }

   public void func_184105_a(PlayerList var1) {
      this.field_71318_t = var1;
   }

   public abstract boolean func_71344_c();

   public void func_71235_a(GameType var1) {
      Iterator var2 = this.func_212370_w().iterator();

      while(var2.hasNext()) {
         WorldServer var3 = (WorldServer)var2.next();
         var3.func_72912_H().func_76060_a(var1);
      }

   }

   public NetworkSystem func_147137_ag() {
      return this.field_147144_o;
   }

   public boolean func_71200_ad() {
      return this.field_71296_Q;
   }

   public boolean func_71279_ae() {
      return false;
   }

   public abstract boolean func_195565_a(GameType var1, boolean var2, int var3);

   public int func_71259_af() {
      return this.field_71315_w;
   }

   public void func_71223_ag() {
      this.field_71295_T = true;
   }

   public Snooper func_80003_ah() {
      return this.field_71307_n;
   }

   public int func_82357_ak() {
      return 16;
   }

   public boolean func_175579_a(World var1, BlockPos var2, EntityPlayer var3) {
      return false;
   }

   public void func_104055_i(boolean var1) {
      this.field_104057_T = var1;
   }

   public boolean func_104056_am() {
      return this.field_104057_T;
   }

   public int func_143007_ar() {
      return this.field_143008_E;
   }

   public void func_143006_e(int var1) {
      this.field_143008_E = var1;
   }

   public MinecraftSessionService func_147130_as() {
      return this.field_147143_S;
   }

   public GameProfileRepository func_152359_aw() {
      return this.field_152365_W;
   }

   public PlayerProfileCache func_152358_ax() {
      return this.field_152366_X;
   }

   public ServerStatusResponse func_147134_at() {
      return this.field_147147_p;
   }

   public void func_147132_au() {
      this.field_147142_T = 0L;
   }

   public int func_175580_aG() {
      return 29999984;
   }

   public <V> ListenableFuture<V> func_175586_a(Callable<V> var1) {
      Validate.notNull(var1);
      if (!this.func_152345_ab() && !this.func_71241_aa()) {
         ListenableFutureTask var2 = ListenableFutureTask.create(var1);
         this.field_175589_i.add(var2);
         return var2;
      } else {
         try {
            return Futures.immediateFuture(var1.call());
         } catch (Exception var3) {
            return Futures.immediateFailedCheckedFuture(var3);
         }
      }
   }

   public ListenableFuture<Object> func_152344_a(Runnable var1) {
      Validate.notNull(var1);
      return this.func_175586_a(Executors.callable(var1));
   }

   public boolean func_152345_ab() {
      return Thread.currentThread() == this.field_175590_aa;
   }

   public int func_175577_aI() {
      return 256;
   }

   public long func_211150_az() {
      return this.field_211151_aa;
   }

   public Thread func_175583_aK() {
      return this.field_175590_aa;
   }

   public DataFixer func_195563_aC() {
      return this.field_184112_s;
   }

   public int func_184108_a(@Nullable WorldServer var1) {
      return var1 != null ? var1.func_82736_K().func_180263_c("spawnRadius") : 10;
   }

   public AdvancementManager func_191949_aK() {
      return this.field_200257_ak;
   }

   public FunctionManager func_193030_aL() {
      return this.field_200258_al;
   }

   public void func_193031_aM() {
      if (!this.func_152345_ab()) {
         this.func_152344_a(this::func_193031_aM);
      } else {
         this.func_184103_al().func_72389_g();
         this.field_195577_ad.func_198983_a();
         this.func_195568_a(this.func_71218_a(DimensionType.OVERWORLD).func_72912_H());
         this.func_184103_al().func_193244_w();
      }
   }

   private void func_195568_a(WorldInfo var1) {
      ArrayList var2 = Lists.newArrayList(this.field_195577_ad.func_198980_d());
      Iterator var3 = this.field_195577_ad.func_198978_b().iterator();

      while(var3.hasNext()) {
         ResourcePackInfo var4 = (ResourcePackInfo)var3.next();
         if (!var1.func_197719_N().contains(var4.func_195790_f()) && !var2.contains(var4)) {
            field_147145_h.info("Found new data pack {}, loading it automatically", var4.func_195790_f());
            var4.func_195792_i().func_198993_a(var2, var4, (var0) -> {
               return var0;
            }, false);
         }
      }

      this.field_195577_ad.func_198985_a(var2);
      ArrayList var5 = Lists.newArrayList();
      this.field_195577_ad.func_198980_d().forEach((var1x) -> {
         var5.add(var1x.func_195796_e());
      });
      this.field_195576_ac.func_199005_a(var5);
      var1.func_197720_O().clear();
      var1.func_197719_N().clear();
      this.field_195577_ad.func_198980_d().forEach((var1x) -> {
         var1.func_197720_O().add(var1x.func_195790_f());
      });
      this.field_195577_ad.func_198978_b().forEach((var2x) -> {
         if (!this.field_195577_ad.func_198980_d().contains(var2x)) {
            var1.func_197719_N().add(var2x.func_195790_f());
         }

      });
   }

   public void func_205743_a(CommandSource var1) {
      if (this.func_205744_aT()) {
         PlayerList var2 = var1.func_197028_i().func_184103_al();
         UserListWhitelist var3 = var2.func_152599_k();
         if (var3.func_152689_b()) {
            ArrayList var4 = Lists.newArrayList(var2.func_181057_v());
            Iterator var5 = var4.iterator();

            while(var5.hasNext()) {
               EntityPlayerMP var6 = (EntityPlayerMP)var5.next();
               if (!var3.func_152705_a(var6.func_146103_bH())) {
                  var6.field_71135_a.func_194028_b(new TextComponentTranslation("multiplayer.disconnect.not_whitelisted", new Object[0]));
               }
            }

         }
      }
   }

   public IReloadableResourceManager func_195570_aG() {
      return this.field_195576_ac;
   }

   public ResourcePackList<ResourcePackInfo> func_195561_aH() {
      return this.field_195577_ad;
   }

   public ITextComponent func_200246_aJ() {
      return this.field_71302_d;
   }

   public int func_195566_aK() {
      return this.field_71303_e;
   }

   public Commands func_195571_aL() {
      return this.field_195579_af;
   }

   public CommandSource func_195573_aM() {
      return new CommandSource(this, this.func_71218_a(DimensionType.OVERWORLD) == null ? Vec3d.field_186680_a : new Vec3d(this.func_71218_a(DimensionType.OVERWORLD).func_175694_M()), Vec2f.field_189974_a, this.func_71218_a(DimensionType.OVERWORLD), 4, "Server", new TextComponentString("Server"), this, (Entity)null);
   }

   public boolean func_195039_a() {
      return true;
   }

   public boolean func_195040_b() {
      return true;
   }

   public RecipeManager func_199529_aN() {
      return this.field_199530_ag;
   }

   public NetworkTagManager func_199731_aO() {
      return this.field_199736_ah;
   }

   public ServerScoreboard func_200251_aP() {
      return this.field_200255_ai;
   }

   public LootTableManager func_200249_aQ() {
      return this.field_200256_aj;
   }

   public GameRules func_200252_aR() {
      return this.func_71218_a(DimensionType.OVERWORLD).func_82736_K();
   }

   public CustomBossEvents func_201300_aS() {
      return this.field_201301_aj;
   }

   public boolean func_205744_aT() {
      return this.field_205745_an;
   }

   public void func_205741_k(boolean var1) {
      this.field_205745_an = var1;
   }

   public float func_211149_aT() {
      return this.field_211152_ao;
   }

   public int func_211833_a(GameProfile var1) {
      if (this.func_184103_al().func_152596_g(var1)) {
         UserListOpsEntry var2 = (UserListOpsEntry)this.func_184103_al().func_152603_m().func_152683_b(var1);
         if (var2 != null) {
            return var2.func_152644_a();
         } else if (this.func_71264_H()) {
            if (this.func_71214_G().equals(var1.getName())) {
               return 4;
            } else {
               return this.func_184103_al().func_206257_x() ? 4 : 0;
            }
         } else {
            return this.func_110455_j();
         }
      } else {
         return 0;
      }
   }
}
