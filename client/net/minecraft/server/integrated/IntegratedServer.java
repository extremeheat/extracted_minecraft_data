package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.CryptManager;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegratedServer extends MinecraftServer {
   private static final Logger field_147148_h = LogManager.getLogger();
   private final Minecraft field_71349_l;
   private final WorldSettings field_71350_m;
   private boolean field_71348_o;
   private boolean field_71346_p;
   private ThreadLanServerPing field_71345_q;

   public IntegratedServer(Minecraft var1) {
      super(var1.func_110437_J(), new File(var1.field_71412_D, field_152367_a.getName()));
      this.field_71349_l = var1;
      this.field_71350_m = null;
   }

   public IntegratedServer(Minecraft var1, String var2, String var3, WorldSettings var4) {
      super(new File(var1.field_71412_D, "saves"), var1.func_110437_J(), new File(var1.field_71412_D, field_152367_a.getName()));
      this.func_71224_l(var1.func_110432_I().func_111285_a());
      this.func_71261_m(var2);
      this.func_71246_n(var3);
      this.func_71204_b(var1.func_71355_q());
      this.func_71194_c(var4.func_77167_c());
      this.func_71191_d(256);
      this.func_152361_a(new IntegratedPlayerList(this));
      this.field_71349_l = var1;
      this.field_71350_m = this.func_71242_L() ? DemoWorldServer.field_73071_a : var4;
   }

   protected ServerCommandManager func_175582_h() {
      return new IntegratedServerCommandManager();
   }

   protected void func_71247_a(String var1, String var2, long var3, WorldType var5, String var6) {
      this.func_71237_c(var1);
      this.field_71305_c = new WorldServer[3];
      this.field_71312_k = new long[this.field_71305_c.length][100];
      ISaveHandler var7 = this.func_71254_M().func_75804_a(var1, true);
      this.func_175584_a(this.func_71270_I(), var7);
      WorldInfo var8 = var7.func_75757_d();
      if (var8 == null) {
         var8 = new WorldInfo(this.field_71350_m, var2);
      } else {
         var8.func_76062_a(var2);
      }

      for(int var9 = 0; var9 < this.field_71305_c.length; ++var9) {
         byte var10 = 0;
         if (var9 == 1) {
            var10 = -1;
         }

         if (var9 == 2) {
            var10 = 1;
         }

         if (var9 == 0) {
            if (this.func_71242_L()) {
               this.field_71305_c[var9] = (WorldServer)(new DemoWorldServer(this, var7, var8, var10, this.field_71304_b)).func_175643_b();
            } else {
               this.field_71305_c[var9] = (WorldServer)(new WorldServer(this, var7, var8, var10, this.field_71304_b)).func_175643_b();
            }

            this.field_71305_c[var9].func_72963_a(this.field_71350_m);
         } else {
            this.field_71305_c[var9] = (WorldServer)(new WorldServerMulti(this, var7, var10, this.field_71305_c[0], this.field_71304_b)).func_175643_b();
         }

         this.field_71305_c[var9].func_72954_a(new WorldManager(this, this.field_71305_c[var9]));
      }

      this.func_71203_ab().func_72364_a(this.field_71305_c);
      if (this.field_71305_c[0].func_72912_H().func_176130_y() == null) {
         this.func_147139_a(this.field_71349_l.field_71474_y.field_74318_M);
      }

      this.func_71222_d();
   }

   protected boolean func_71197_b() throws IOException {
      field_147148_h.info("Starting integrated minecraft server version 1.8.9");
      this.func_71229_d(true);
      this.func_71251_e(true);
      this.func_71257_f(true);
      this.func_71188_g(true);
      this.func_71245_h(true);
      field_147148_h.info("Generating keypair");
      this.func_71253_a(CryptManager.func_75891_b());
      this.func_71247_a(this.func_71270_I(), this.func_71221_J(), this.field_71350_m.func_77160_d(), this.field_71350_m.func_77165_h(), this.field_71350_m.func_82749_j());
      this.func_71205_p(this.func_71214_G() + " - " + this.field_71305_c[0].func_72912_H().func_76065_j());
      return true;
   }

   protected void func_71217_p() {
      boolean var1 = this.field_71348_o;
      this.field_71348_o = Minecraft.func_71410_x().func_147114_u() != null && Minecraft.func_71410_x().func_147113_T();
      if (!var1 && this.field_71348_o) {
         field_147148_h.info("Saving and pausing game...");
         this.func_71203_ab().func_72389_g();
         this.func_71267_a(false);
      }

      if (this.field_71348_o) {
         synchronized(this.field_175589_i) {
            while(!this.field_175589_i.isEmpty()) {
               Util.func_181617_a((FutureTask)this.field_175589_i.poll(), field_147148_h);
            }
         }
      } else {
         super.func_71217_p();
         if (this.field_71349_l.field_71474_y.field_151451_c != this.func_71203_ab().func_72395_o()) {
            field_147148_h.info("Changing view distance to {}, from {}", new Object[]{this.field_71349_l.field_71474_y.field_151451_c, this.func_71203_ab().func_72395_o()});
            this.func_71203_ab().func_152611_a(this.field_71349_l.field_71474_y.field_151451_c);
         }

         if (this.field_71349_l.field_71441_e != null) {
            WorldInfo var2 = this.field_71305_c[0].func_72912_H();
            WorldInfo var3 = this.field_71349_l.field_71441_e.func_72912_H();
            if (!var2.func_176123_z() && var3.func_176130_y() != var2.func_176130_y()) {
               field_147148_h.info("Changing difficulty to {}, from {}", new Object[]{var3.func_176130_y(), var2.func_176130_y()});
               this.func_147139_a(var3.func_176130_y());
            } else if (var3.func_176123_z() && !var2.func_176123_z()) {
               field_147148_h.info("Locking difficulty to {}", new Object[]{var3.func_176130_y()});
               WorldServer[] var4 = this.field_71305_c;
               int var5 = var4.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  WorldServer var7 = var4[var6];
                  if (var7 != null) {
                     var7.func_72912_H().func_180783_e(true);
                  }
               }
            }
         }
      }

   }

   public boolean func_71225_e() {
      return false;
   }

   public WorldSettings.GameType func_71265_f() {
      return this.field_71350_m.func_77162_e();
   }

   public EnumDifficulty func_147135_j() {
      return this.field_71349_l.field_71441_e.func_72912_H().func_176130_y();
   }

   public boolean func_71199_h() {
      return this.field_71350_m.func_77158_f();
   }

   public boolean func_181034_q() {
      return true;
   }

   public boolean func_183002_r() {
      return true;
   }

   public File func_71238_n() {
      return this.field_71349_l.field_71412_D;
   }

   public boolean func_71262_S() {
      return false;
   }

   public boolean func_181035_ah() {
      return false;
   }

   protected void func_71228_a(CrashReport var1) {
      this.field_71349_l.func_71404_a(var1);
   }

   public CrashReport func_71230_b(CrashReport var1) {
      var1 = super.func_71230_b(var1);
      var1.func_85056_g().func_71500_a("Type", new Callable<String>() {
         public String call() throws Exception {
            return "Integrated Server (map_client.txt)";
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var1.func_85056_g().func_71500_a("Is Modded", new Callable<String>() {
         public String call() throws Exception {
            String var1 = ClientBrandRetriever.getClientModName();
            if (!var1.equals("vanilla")) {
               return "Definitely; Client brand changed to '" + var1 + "'";
            } else {
               var1 = IntegratedServer.this.getServerModName();
               if (!var1.equals("vanilla")) {
                  return "Definitely; Server brand changed to '" + var1 + "'";
               } else {
                  return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and both client + server brands are untouched.";
               }
            }
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      return var1;
   }

   public void func_147139_a(EnumDifficulty var1) {
      super.func_147139_a(var1);
      if (this.field_71349_l.field_71441_e != null) {
         this.field_71349_l.field_71441_e.func_72912_H().func_176144_a(var1);
      }

   }

   public void func_70000_a(PlayerUsageSnooper var1) {
      super.func_70000_a(var1);
      var1.func_152768_a("snooper_partner", this.field_71349_l.func_71378_E().func_80006_f());
   }

   public boolean func_70002_Q() {
      return Minecraft.func_71410_x().func_70002_Q();
   }

   public String func_71206_a(WorldSettings.GameType var1, boolean var2) {
      try {
         int var3 = -1;

         try {
            var3 = HttpUtil.func_76181_a();
         } catch (IOException var5) {
         }

         if (var3 <= 0) {
            var3 = 25564;
         }

         this.func_147137_ag().func_151265_a((InetAddress)null, var3);
         field_147148_h.info("Started on " + var3);
         this.field_71346_p = true;
         this.field_71345_q = new ThreadLanServerPing(this.func_71273_Y(), var3 + "");
         this.field_71345_q.start();
         this.func_71203_ab().func_152604_a(var1);
         this.func_71203_ab().func_72387_b(var2);
         return var3 + "";
      } catch (IOException var6) {
         return null;
      }
   }

   public void func_71260_j() {
      super.func_71260_j();
      if (this.field_71345_q != null) {
         this.field_71345_q.interrupt();
         this.field_71345_q = null;
      }

   }

   public void func_71263_m() {
      Futures.getUnchecked(this.func_152344_a(new Runnable() {
         public void run() {
            ArrayList var1 = Lists.newArrayList(IntegratedServer.this.func_71203_ab().func_181057_v());
            Iterator var2 = var1.iterator();

            while(var2.hasNext()) {
               EntityPlayerMP var3 = (EntityPlayerMP)var2.next();
               IntegratedServer.this.func_71203_ab().func_72367_e(var3);
            }

         }
      }));
      super.func_71263_m();
      if (this.field_71345_q != null) {
         this.field_71345_q.interrupt();
         this.field_71345_q = null;
      }

   }

   public void func_175592_a() {
      this.func_175585_v();
   }

   public boolean func_71344_c() {
      return this.field_71346_p;
   }

   public void func_71235_a(WorldSettings.GameType var1) {
      this.func_71203_ab().func_152604_a(var1);
   }

   public boolean func_82356_Z() {
      return true;
   }

   public int func_110455_j() {
      return 4;
   }
}
