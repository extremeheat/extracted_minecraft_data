package net.minecraft.server.integrated;

import java.io.File;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.crash.CrashReport;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.CryptManager;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegratedServer extends MinecraftServer {
   private static final Logger field_147148_h = LogManager.getLogger();
   private final Minecraft field_71349_l;
   private final WorldSettings field_71350_m;
   private boolean field_71348_o;
   private boolean field_71346_p;
   private ThreadLanServerPing field_71345_q;

   public IntegratedServer(Minecraft var1, String var2, String var3, WorldSettings var4) {
      super(new File(var1.field_71412_D, "saves"), var1.func_110437_J());
      this.func_71224_l(var1.func_110432_I().func_111285_a());
      this.func_71261_m(var2);
      this.func_71246_n(var3);
      this.func_71204_b(var1.func_71355_q());
      this.func_71194_c(var4.func_77167_c());
      this.func_71191_d(256);
      this.func_152361_a(new IntegratedPlayerList(this));
      this.field_71349_l = var1;
      this.field_71350_m = var4;
   }

   @Override
   protected void func_71247_a(String var1, String var2, long var3, WorldType var5, String var6) {
      this.func_71237_c(var1);
      this.field_71305_c = new WorldServer[3];
      this.field_71312_k = new long[this.field_71305_c.length][100];
      ISaveHandler var7 = this.func_71254_M().func_75804_a(var1, true);

      for(int var8 = 0; var8 < this.field_71305_c.length; ++var8) {
         byte var9 = 0;
         if (var8 == 1) {
            var9 = -1;
         }

         if (var8 == 2) {
            var9 = 1;
         }

         if (var8 == 0) {
            if (this.func_71242_L()) {
               this.field_71305_c[var8] = new DemoWorldServer(this, var7, var2, var9, this.field_71304_b);
            } else {
               this.field_71305_c[var8] = new WorldServer(this, var7, var2, var9, this.field_71350_m, this.field_71304_b);
            }
         } else {
            this.field_71305_c[var8] = new WorldServerMulti(this, var7, var2, var9, this.field_71350_m, this.field_71305_c[0], this.field_71304_b);
         }

         this.field_71305_c[var8].func_72954_a(new WorldManager(this, this.field_71305_c[var8]));
         this.func_71203_ab().func_72364_a(this.field_71305_c);
      }

      this.func_147139_a(this.func_147135_j());
      this.func_71222_d();
   }

   @Override
   protected boolean func_71197_b() {
      field_147148_h.info("Starting integrated minecraft server version 1.7.10");
      this.func_71229_d(true);
      this.func_71251_e(true);
      this.func_71257_f(true);
      this.func_71188_g(true);
      this.func_71245_h(true);
      field_147148_h.info("Generating keypair");
      this.func_71253_a(CryptManager.func_75891_b());
      this.func_71247_a(
         this.func_71270_I(), this.func_71221_J(), this.field_71350_m.func_77160_d(), this.field_71350_m.func_77165_h(), this.field_71350_m.func_82749_j()
      );
      this.func_71205_p(this.func_71214_G() + " - " + this.field_71305_c[0].func_72912_H().func_76065_j());
      return true;
   }

   @Override
   protected void func_71217_p() {
      boolean var1 = this.field_71348_o;
      this.field_71348_o = Minecraft.func_71410_x().func_147114_u() != null && Minecraft.func_71410_x().func_147113_T();
      if (!var1 && this.field_71348_o) {
         field_147148_h.info("Saving and pausing game...");
         this.func_71203_ab().func_72389_g();
         this.func_71267_a(false);
      }

      if (!this.field_71348_o) {
         super.func_71217_p();
         if (this.field_71349_l.field_71474_y.field_151451_c != this.func_71203_ab().func_72395_o()) {
            field_147148_h.info(
               "Changing view distance to {}, from {}", new Object[]{this.field_71349_l.field_71474_y.field_151451_c, this.func_71203_ab().func_72395_o()}
            );
            this.func_71203_ab().func_152611_a(this.field_71349_l.field_71474_y.field_151451_c);
         }
      }
   }

   @Override
   public boolean func_71225_e() {
      return false;
   }

   @Override
   public WorldSettings$GameType func_71265_f() {
      return this.field_71350_m.func_77162_e();
   }

   @Override
   public EnumDifficulty func_147135_j() {
      return this.field_71349_l.field_71474_y.field_74318_M;
   }

   @Override
   public boolean func_71199_h() {
      return this.field_71350_m.func_77158_f();
   }

   @Override
   public boolean func_152363_m() {
      return false;
   }

   @Override
   protected File func_71238_n() {
      return this.field_71349_l.field_71412_D;
   }

   @Override
   public boolean func_71262_S() {
      return false;
   }

   @Override
   protected void func_71228_a(CrashReport var1) {
      this.field_71349_l.func_71404_a(var1);
   }

   @Override
   public CrashReport func_71230_b(CrashReport var1) {
      var1 = super.func_71230_b(var1);
      var1.func_85056_g().func_71500_a("Type", new IntegratedServer$1(this));
      var1.func_85056_g().func_71500_a("Is Modded", new IntegratedServer$2(this));
      return var1;
   }

   @Override
   public void func_70000_a(PlayerUsageSnooper var1) {
      super.func_70000_a(var1);
      var1.func_152768_a("snooper_partner", this.field_71349_l.func_71378_E().func_80006_f());
   }

   @Override
   public boolean func_70002_Q() {
      return Minecraft.func_71410_x().func_70002_Q();
   }

   @Override
   public String func_71206_a(WorldSettings$GameType var1, boolean var2) {
      try {
         int var3 = -1;

         try {
            var3 = HttpUtil.func_76181_a();
         } catch (IOException var5) {
         }

         if (var3 <= 0) {
            var3 = 25564;
         }

         this.func_147137_ag().func_151265_a(null, var3);
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

   @Override
   public void func_71260_j() {
      super.func_71260_j();
      if (this.field_71345_q != null) {
         this.field_71345_q.interrupt();
         this.field_71345_q = null;
      }
   }

   @Override
   public void func_71263_m() {
      super.func_71263_m();
      if (this.field_71345_q != null) {
         this.field_71345_q.interrupt();
         this.field_71345_q = null;
      }
   }

   public boolean func_71344_c() {
      return this.field_71346_p;
   }

   @Override
   public void func_71235_a(WorldSettings$GameType var1) {
      this.func_71203_ab().func_152604_a(var1);
   }

   @Override
   public boolean func_82356_Z() {
      return true;
   }

   @Override
   public int func_110455_j() {
      return 4;
   }
}
