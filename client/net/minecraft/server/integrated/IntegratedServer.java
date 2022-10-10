package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.FutureTask;
import java.util.function.BooleanSupplier;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.command.Commands;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.profiler.Snooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.CryptManager;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegratedServer extends MinecraftServer {
   private static final Logger field_147148_h = LogManager.getLogger();
   private final Minecraft field_71349_l;
   private final WorldSettings field_71350_m;
   private boolean field_71348_o;
   private int field_195580_l = -1;
   private ThreadLanServerPing field_71345_q;
   private UUID field_211528_n;

   public IntegratedServer(Minecraft var1, String var2, String var3, WorldSettings var4, YggdrasilAuthenticationService var5, MinecraftSessionService var6, GameProfileRepository var7, PlayerProfileCache var8) {
      super(new File(var1.field_71412_D, "saves"), var1.func_110437_J(), var1.func_184126_aj(), new Commands(false), var5, var6, var7, var8);
      this.func_71224_l(var1.func_110432_I().func_111285_a());
      this.func_71261_m(var2);
      this.func_71246_n(var3);
      this.func_71204_b(var1.func_71355_q());
      this.func_71194_c(var4.func_77167_c());
      this.func_71191_d(256);
      this.func_184105_a(new IntegratedPlayerList(this));
      this.field_71349_l = var1;
      this.field_71350_m = this.func_71242_L() ? WorldServerDemo.field_73071_a : var4;
   }

   protected void func_71247_a(String var1, String var2, long var3, WorldType var5, JsonElement var6) {
      this.func_71237_c(var1);
      ISaveHandler var7 = this.func_71254_M().func_197715_a(var1, this);
      this.func_175584_a(this.func_71270_I(), var7);
      WorldInfo var8 = var7.func_75757_d();
      if (var8 == null) {
         var8 = new WorldInfo(this.field_71350_m, var2);
      } else {
         var8.func_76062_a(var2);
      }

      this.func_195560_a(var7.func_75765_b(), var8);
      WorldSavedDataStorage var9 = new WorldSavedDataStorage(var7);
      this.func_212369_a(var7, var9, var8, this.field_71350_m);
      if (this.func_71218_a(DimensionType.OVERWORLD).func_72912_H().func_176130_y() == null) {
         this.func_147139_a(this.field_71349_l.field_71474_y.field_74318_M);
      }

      this.func_71222_d(var9);
   }

   protected boolean func_71197_b() throws IOException {
      field_147148_h.info("Starting integrated minecraft server version 1.13.2");
      this.func_71229_d(true);
      this.func_71251_e(true);
      this.func_71257_f(true);
      this.func_71188_g(true);
      this.func_71245_h(true);
      field_147148_h.info("Generating keypair");
      this.func_71253_a(CryptManager.func_75891_b());
      this.func_71247_a(this.func_71270_I(), this.func_71221_J(), this.field_71350_m.func_77160_d(), this.field_71350_m.func_77165_h(), this.field_71350_m.func_205391_j());
      this.func_71205_p(this.func_71214_G() + " - " + this.func_71218_a(DimensionType.OVERWORLD).func_72912_H().func_76065_j());
      return true;
   }

   protected void func_71217_p(BooleanSupplier var1) {
      boolean var2 = this.field_71348_o;
      this.field_71348_o = Minecraft.func_71410_x().func_147114_u() != null && Minecraft.func_71410_x().func_147113_T();
      if (!var2 && this.field_71348_o) {
         field_147148_h.info("Saving and pausing game...");
         this.func_184103_al().func_72389_g();
         this.func_71267_a(false);
      }

      FutureTask var7;
      if (this.field_71348_o) {
         while((var7 = (FutureTask)this.field_175589_i.poll()) != null) {
            Util.func_181617_a(var7, field_147148_h);
         }
      } else {
         super.func_71217_p(var1);
         if (this.field_71349_l.field_71474_y.field_151451_c != this.func_184103_al().func_72395_o()) {
            field_147148_h.info("Changing view distance to {}, from {}", this.field_71349_l.field_71474_y.field_151451_c, this.func_184103_al().func_72395_o());
            this.func_184103_al().func_152611_a(this.field_71349_l.field_71474_y.field_151451_c);
         }

         if (this.field_71349_l.field_71441_e != null) {
            WorldInfo var3 = this.func_71218_a(DimensionType.OVERWORLD).func_72912_H();
            WorldInfo var4 = this.field_71349_l.field_71441_e.func_72912_H();
            if (!var3.func_176123_z() && var4.func_176130_y() != var3.func_176130_y()) {
               field_147148_h.info("Changing difficulty to {}, from {}", var4.func_176130_y(), var3.func_176130_y());
               this.func_147139_a(var4.func_176130_y());
            } else if (var4.func_176123_z() && !var3.func_176123_z()) {
               field_147148_h.info("Locking difficulty to {}", var4.func_176130_y());
               Iterator var5 = this.func_212370_w().iterator();

               while(var5.hasNext()) {
                  WorldServer var6 = (WorldServer)var5.next();
                  if (var6 != null) {
                     var6.func_72912_H().func_180783_e(true);
                  }
               }
            }
         }
      }

   }

   public boolean func_71225_e() {
      return false;
   }

   public GameType func_71265_f() {
      return this.field_71350_m.func_77162_e();
   }

   public EnumDifficulty func_147135_j() {
      return this.field_71349_l.field_71441_e.func_72912_H().func_176130_y();
   }

   public boolean func_71199_h() {
      return this.field_71350_m.func_77158_f();
   }

   public boolean func_195569_l() {
      return true;
   }

   public boolean func_195041_r_() {
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
      var1.func_85056_g().func_71507_a("Type", "Integrated Server (map_client.txt)");
      var1.func_85056_g().func_189529_a("Is Modded", () -> {
         String var1 = ClientBrandRetriever.getClientModName();
         if (!var1.equals("vanilla")) {
            return "Definitely; Client brand changed to '" + var1 + "'";
         } else {
            var1 = this.getServerModName();
            if (!"vanilla".equals(var1)) {
               return "Definitely; Server brand changed to '" + var1 + "'";
            } else {
               return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and both client + server brands are untouched.";
            }
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

   public void func_70000_a(Snooper var1) {
      super.func_70000_a(var1);
      var1.func_152768_a("snooper_partner", this.field_71349_l.func_71378_E().func_80006_f());
   }

   public boolean func_70002_Q() {
      return Minecraft.func_71410_x().func_70002_Q();
   }

   public boolean func_195565_a(GameType var1, boolean var2, int var3) {
      try {
         this.func_147137_ag().func_151265_a((InetAddress)null, var3);
         field_147148_h.info("Started serving on {}", var3);
         this.field_195580_l = var3;
         this.field_71345_q = new ThreadLanServerPing(this.func_71273_Y(), var3 + "");
         this.field_71345_q.start();
         this.func_184103_al().func_152604_a(var1);
         this.func_184103_al().func_72387_b(var2);
         int var4 = this.func_211833_a(this.field_71349_l.field_71439_g.func_146103_bH());
         this.field_71349_l.field_71439_g.func_184839_n(var4);
         Iterator var5 = this.func_184103_al().func_181057_v().iterator();

         while(var5.hasNext()) {
            EntityPlayerMP var6 = (EntityPlayerMP)var5.next();
            this.func_195571_aL().func_197051_a(var6);
         }

         return true;
      } catch (IOException var7) {
         return false;
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
      Futures.getUnchecked(this.func_152344_a(() -> {
         ArrayList var1 = Lists.newArrayList(this.func_184103_al().func_181057_v());
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            EntityPlayerMP var3 = (EntityPlayerMP)var2.next();
            if (!var3.func_110124_au().equals(this.field_211528_n)) {
               this.func_184103_al().func_72367_e(var3);
            }
         }

      }));
      super.func_71263_m();
      if (this.field_71345_q != null) {
         this.field_71345_q.interrupt();
         this.field_71345_q = null;
      }

   }

   public boolean func_71344_c() {
      return this.field_195580_l > -1;
   }

   public int func_71215_F() {
      return this.field_195580_l;
   }

   public void func_71235_a(GameType var1) {
      super.func_71235_a(var1);
      this.func_184103_al().func_152604_a(var1);
   }

   public boolean func_82356_Z() {
      return true;
   }

   public int func_110455_j() {
      return 2;
   }

   public void func_211527_b(UUID var1) {
      this.field_211528_n = var1;
   }
}
