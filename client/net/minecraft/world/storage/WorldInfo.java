package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;

public class WorldInfo {
   private String field_186349_b;
   private int field_186350_c;
   private boolean field_186351_d;
   public static final EnumDifficulty field_176156_a;
   private long field_76100_a;
   private WorldType field_76098_b;
   private NBTTagCompound field_82576_c;
   @Nullable
   private String field_211931_h;
   private int field_76099_c;
   private int field_76096_d;
   private int field_76097_e;
   private long field_82575_g;
   private long field_76094_f;
   private long field_76095_g;
   private long field_76107_h;
   @Nullable
   private final DataFixer field_209226_o;
   private final int field_209227_p;
   private boolean field_209228_q;
   private NBTTagCompound field_76108_i;
   private int field_76105_j;
   private String field_76106_k;
   private int field_76103_l;
   private int field_176157_p;
   private boolean field_76104_m;
   private int field_76101_n;
   private boolean field_76102_o;
   private int field_76114_p;
   private GameType field_76113_q;
   private boolean field_76112_r;
   private boolean field_76111_s;
   private boolean field_76110_t;
   private boolean field_76109_u;
   private EnumDifficulty field_176158_z;
   private boolean field_176150_A;
   private double field_176151_B;
   private double field_176152_C;
   private double field_176146_D;
   private long field_176147_E;
   private double field_176148_F;
   private double field_176149_G;
   private double field_176153_H;
   private int field_176154_I;
   private int field_176155_J;
   private final Set<String> field_197721_N;
   private final Set<String> field_197722_O;
   private final Map<DimensionType, NBTTagCompound> field_186348_N;
   private NBTTagCompound field_201358_Q;
   private final GameRules field_82577_x;

   protected WorldInfo() {
      super();
      this.field_76098_b = WorldType.field_77137_b;
      this.field_82576_c = new NBTTagCompound();
      this.field_176146_D = 6.0E7D;
      this.field_176149_G = 5.0D;
      this.field_176153_H = 0.2D;
      this.field_176154_I = 5;
      this.field_176155_J = 15;
      this.field_197721_N = Sets.newHashSet();
      this.field_197722_O = Sets.newLinkedHashSet();
      this.field_186348_N = Maps.newIdentityHashMap();
      this.field_82577_x = new GameRules();
      this.field_209226_o = null;
      this.field_209227_p = 1631;
      this.func_212242_b(new NBTTagCompound());
   }

   public WorldInfo(NBTTagCompound var1, DataFixer var2, int var3, @Nullable NBTTagCompound var4) {
      super();
      this.field_76098_b = WorldType.field_77137_b;
      this.field_82576_c = new NBTTagCompound();
      this.field_176146_D = 6.0E7D;
      this.field_176149_G = 5.0D;
      this.field_176153_H = 0.2D;
      this.field_176154_I = 5;
      this.field_176155_J = 15;
      this.field_197721_N = Sets.newHashSet();
      this.field_197722_O = Sets.newLinkedHashSet();
      this.field_186348_N = Maps.newIdentityHashMap();
      this.field_82577_x = new GameRules();
      this.field_209226_o = var2;
      NBTTagCompound var5;
      if (var1.func_150297_b("Version", 10)) {
         var5 = var1.func_74775_l("Version");
         this.field_186349_b = var5.func_74779_i("Name");
         this.field_186350_c = var5.func_74762_e("Id");
         this.field_186351_d = var5.func_74767_n("Snapshot");
      }

      this.field_76100_a = var1.func_74763_f("RandomSeed");
      if (var1.func_150297_b("generatorName", 8)) {
         String var9 = var1.func_74779_i("generatorName");
         this.field_76098_b = WorldType.func_77130_a(var9);
         if (this.field_76098_b == null) {
            this.field_76098_b = WorldType.field_77137_b;
         } else if (this.field_76098_b == WorldType.field_180271_f) {
            this.field_211931_h = var1.func_74779_i("generatorOptions");
         } else if (this.field_76098_b.func_77125_e()) {
            int var6 = 0;
            if (var1.func_150297_b("generatorVersion", 99)) {
               var6 = var1.func_74762_e("generatorVersion");
            }

            this.field_76098_b = this.field_76098_b.func_77132_a(var6);
         }

         this.func_212242_b(var1.func_74775_l("generatorOptions"));
      }

      this.field_76113_q = GameType.func_77146_a(var1.func_74762_e("GameType"));
      if (var1.func_150297_b("legacy_custom_options", 8)) {
         this.field_211931_h = var1.func_74779_i("legacy_custom_options");
      }

      if (var1.func_150297_b("MapFeatures", 99)) {
         this.field_76112_r = var1.func_74767_n("MapFeatures");
      } else {
         this.field_76112_r = true;
      }

      this.field_76099_c = var1.func_74762_e("SpawnX");
      this.field_76096_d = var1.func_74762_e("SpawnY");
      this.field_76097_e = var1.func_74762_e("SpawnZ");
      this.field_82575_g = var1.func_74763_f("Time");
      if (var1.func_150297_b("DayTime", 99)) {
         this.field_76094_f = var1.func_74763_f("DayTime");
      } else {
         this.field_76094_f = this.field_82575_g;
      }

      this.field_76095_g = var1.func_74763_f("LastPlayed");
      this.field_76107_h = var1.func_74763_f("SizeOnDisk");
      this.field_76106_k = var1.func_74779_i("LevelName");
      this.field_76103_l = var1.func_74762_e("version");
      this.field_176157_p = var1.func_74762_e("clearWeatherTime");
      this.field_76101_n = var1.func_74762_e("rainTime");
      this.field_76104_m = var1.func_74767_n("raining");
      this.field_76114_p = var1.func_74762_e("thunderTime");
      this.field_76102_o = var1.func_74767_n("thundering");
      this.field_76111_s = var1.func_74767_n("hardcore");
      if (var1.func_150297_b("initialized", 99)) {
         this.field_76109_u = var1.func_74767_n("initialized");
      } else {
         this.field_76109_u = true;
      }

      if (var1.func_150297_b("allowCommands", 99)) {
         this.field_76110_t = var1.func_74767_n("allowCommands");
      } else {
         this.field_76110_t = this.field_76113_q == GameType.CREATIVE;
      }

      this.field_209227_p = var3;
      if (var4 != null) {
         this.field_76108_i = var4;
      }

      if (var1.func_150297_b("GameRules", 10)) {
         this.field_82577_x.func_82768_a(var1.func_74775_l("GameRules"));
      }

      if (var1.func_150297_b("Difficulty", 99)) {
         this.field_176158_z = EnumDifficulty.func_151523_a(var1.func_74771_c("Difficulty"));
      }

      if (var1.func_150297_b("DifficultyLocked", 1)) {
         this.field_176150_A = var1.func_74767_n("DifficultyLocked");
      }

      if (var1.func_150297_b("BorderCenterX", 99)) {
         this.field_176151_B = var1.func_74769_h("BorderCenterX");
      }

      if (var1.func_150297_b("BorderCenterZ", 99)) {
         this.field_176152_C = var1.func_74769_h("BorderCenterZ");
      }

      if (var1.func_150297_b("BorderSize", 99)) {
         this.field_176146_D = var1.func_74769_h("BorderSize");
      }

      if (var1.func_150297_b("BorderSizeLerpTime", 99)) {
         this.field_176147_E = var1.func_74763_f("BorderSizeLerpTime");
      }

      if (var1.func_150297_b("BorderSizeLerpTarget", 99)) {
         this.field_176148_F = var1.func_74769_h("BorderSizeLerpTarget");
      }

      if (var1.func_150297_b("BorderSafeZone", 99)) {
         this.field_176149_G = var1.func_74769_h("BorderSafeZone");
      }

      if (var1.func_150297_b("BorderDamagePerBlock", 99)) {
         this.field_176153_H = var1.func_74769_h("BorderDamagePerBlock");
      }

      if (var1.func_150297_b("BorderWarningBlocks", 99)) {
         this.field_176154_I = var1.func_74762_e("BorderWarningBlocks");
      }

      if (var1.func_150297_b("BorderWarningTime", 99)) {
         this.field_176155_J = var1.func_74762_e("BorderWarningTime");
      }

      if (var1.func_150297_b("DimensionData", 10)) {
         var5 = var1.func_74775_l("DimensionData");
         Iterator var10 = var5.func_150296_c().iterator();

         while(var10.hasNext()) {
            String var7 = (String)var10.next();
            this.field_186348_N.put(DimensionType.func_186069_a(Integer.parseInt(var7)), var5.func_74775_l(var7));
         }
      }

      if (var1.func_150297_b("DataPacks", 10)) {
         var5 = var1.func_74775_l("DataPacks");
         NBTTagList var11 = var5.func_150295_c("Disabled", 8);

         for(int var12 = 0; var12 < var11.size(); ++var12) {
            this.field_197721_N.add(var11.func_150307_f(var12));
         }

         NBTTagList var13 = var5.func_150295_c("Enabled", 8);

         for(int var8 = 0; var8 < var13.size(); ++var8) {
            this.field_197722_O.add(var13.func_150307_f(var8));
         }
      }

      if (var1.func_150297_b("CustomBossEvents", 10)) {
         this.field_201358_Q = var1.func_74775_l("CustomBossEvents");
      }

   }

   public WorldInfo(WorldSettings var1, String var2) {
      super();
      this.field_76098_b = WorldType.field_77137_b;
      this.field_82576_c = new NBTTagCompound();
      this.field_176146_D = 6.0E7D;
      this.field_176149_G = 5.0D;
      this.field_176153_H = 0.2D;
      this.field_176154_I = 5;
      this.field_176155_J = 15;
      this.field_197721_N = Sets.newHashSet();
      this.field_197722_O = Sets.newLinkedHashSet();
      this.field_186348_N = Maps.newIdentityHashMap();
      this.field_82577_x = new GameRules();
      this.field_209226_o = null;
      this.field_209227_p = 1631;
      this.func_176127_a(var1);
      this.field_76106_k = var2;
      this.field_176158_z = field_176156_a;
      this.field_76109_u = false;
   }

   public void func_176127_a(WorldSettings var1) {
      this.field_76100_a = var1.func_77160_d();
      this.field_76113_q = var1.func_77162_e();
      this.field_76112_r = var1.func_77164_g();
      this.field_76111_s = var1.func_77158_f();
      this.field_76098_b = var1.func_77165_h();
      this.func_212242_b((NBTTagCompound)Dynamic.convert(JsonOps.INSTANCE, NBTDynamicOps.field_210820_a, var1.func_205391_j()));
      this.field_76110_t = var1.func_77163_i();
   }

   public NBTTagCompound func_76082_a(@Nullable NBTTagCompound var1) {
      this.func_209225_Q();
      if (var1 == null) {
         var1 = this.field_76108_i;
      }

      NBTTagCompound var2 = new NBTTagCompound();
      this.func_76064_a(var2, var1);
      return var2;
   }

   private void func_76064_a(NBTTagCompound var1, NBTTagCompound var2) {
      NBTTagCompound var3 = new NBTTagCompound();
      var3.func_74778_a("Name", "1.13.2");
      var3.func_74768_a("Id", 1631);
      var3.func_74757_a("Snapshot", false);
      var1.func_74782_a("Version", var3);
      var1.func_74768_a("DataVersion", 1631);
      var1.func_74772_a("RandomSeed", this.field_76100_a);
      var1.func_74778_a("generatorName", this.field_76098_b.func_211889_b());
      var1.func_74768_a("generatorVersion", this.field_76098_b.func_77131_c());
      if (!this.field_82576_c.isEmpty()) {
         var1.func_74782_a("generatorOptions", this.field_82576_c);
      }

      if (this.field_211931_h != null) {
         var1.func_74778_a("legacy_custom_options", this.field_211931_h);
      }

      var1.func_74768_a("GameType", this.field_76113_q.func_77148_a());
      var1.func_74757_a("MapFeatures", this.field_76112_r);
      var1.func_74768_a("SpawnX", this.field_76099_c);
      var1.func_74768_a("SpawnY", this.field_76096_d);
      var1.func_74768_a("SpawnZ", this.field_76097_e);
      var1.func_74772_a("Time", this.field_82575_g);
      var1.func_74772_a("DayTime", this.field_76094_f);
      var1.func_74772_a("SizeOnDisk", this.field_76107_h);
      var1.func_74772_a("LastPlayed", Util.func_211179_d());
      var1.func_74778_a("LevelName", this.field_76106_k);
      var1.func_74768_a("version", this.field_76103_l);
      var1.func_74768_a("clearWeatherTime", this.field_176157_p);
      var1.func_74768_a("rainTime", this.field_76101_n);
      var1.func_74757_a("raining", this.field_76104_m);
      var1.func_74768_a("thunderTime", this.field_76114_p);
      var1.func_74757_a("thundering", this.field_76102_o);
      var1.func_74757_a("hardcore", this.field_76111_s);
      var1.func_74757_a("allowCommands", this.field_76110_t);
      var1.func_74757_a("initialized", this.field_76109_u);
      var1.func_74780_a("BorderCenterX", this.field_176151_B);
      var1.func_74780_a("BorderCenterZ", this.field_176152_C);
      var1.func_74780_a("BorderSize", this.field_176146_D);
      var1.func_74772_a("BorderSizeLerpTime", this.field_176147_E);
      var1.func_74780_a("BorderSafeZone", this.field_176149_G);
      var1.func_74780_a("BorderDamagePerBlock", this.field_176153_H);
      var1.func_74780_a("BorderSizeLerpTarget", this.field_176148_F);
      var1.func_74780_a("BorderWarningBlocks", (double)this.field_176154_I);
      var1.func_74780_a("BorderWarningTime", (double)this.field_176155_J);
      if (this.field_176158_z != null) {
         var1.func_74774_a("Difficulty", (byte)this.field_176158_z.func_151525_a());
      }

      var1.func_74757_a("DifficultyLocked", this.field_176150_A);
      var1.func_74782_a("GameRules", this.field_82577_x.func_82770_a());
      NBTTagCompound var4 = new NBTTagCompound();
      Iterator var5 = this.field_186348_N.entrySet().iterator();

      while(var5.hasNext()) {
         Entry var6 = (Entry)var5.next();
         var4.func_74782_a(String.valueOf(((DimensionType)var6.getKey()).func_186068_a()), (INBTBase)var6.getValue());
      }

      var1.func_74782_a("DimensionData", var4);
      if (var2 != null) {
         var1.func_74782_a("Player", var2);
      }

      NBTTagCompound var10 = new NBTTagCompound();
      NBTTagList var11 = new NBTTagList();
      Iterator var7 = this.field_197722_O.iterator();

      while(var7.hasNext()) {
         String var8 = (String)var7.next();
         var11.add((INBTBase)(new NBTTagString(var8)));
      }

      var10.func_74782_a("Enabled", var11);
      NBTTagList var12 = new NBTTagList();
      Iterator var13 = this.field_197721_N.iterator();

      while(var13.hasNext()) {
         String var9 = (String)var13.next();
         var12.add((INBTBase)(new NBTTagString(var9)));
      }

      var10.func_74782_a("Disabled", var12);
      var1.func_74782_a("DataPacks", var10);
      if (this.field_201358_Q != null) {
         var1.func_74782_a("CustomBossEvents", this.field_201358_Q);
      }

   }

   public long func_76063_b() {
      return this.field_76100_a;
   }

   public int func_76079_c() {
      return this.field_76099_c;
   }

   public int func_76075_d() {
      return this.field_76096_d;
   }

   public int func_76074_e() {
      return this.field_76097_e;
   }

   public long func_82573_f() {
      return this.field_82575_g;
   }

   public long func_76073_f() {
      return this.field_76094_f;
   }

   public long func_76092_g() {
      return this.field_76107_h;
   }

   private void func_209225_Q() {
      if (!this.field_209228_q && this.field_76108_i != null) {
         if (this.field_209227_p < 1631) {
            if (this.field_209226_o == null) {
               throw new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.");
            }

            this.field_76108_i = NBTUtil.func_210822_a(this.field_209226_o, DataFixTypes.PLAYER, this.field_76108_i, this.field_209227_p);
         }

         this.field_76105_j = this.field_76108_i.func_74762_e("Dimension");
         this.field_209228_q = true;
      }
   }

   public NBTTagCompound func_76072_h() {
      this.func_209225_Q();
      return this.field_76108_i;
   }

   public int func_202836_i() {
      this.func_209225_Q();
      return this.field_76105_j;
   }

   public void func_76058_a(int var1) {
      this.field_76099_c = var1;
   }

   public void func_76056_b(int var1) {
      this.field_76096_d = var1;
   }

   public void func_76087_c(int var1) {
      this.field_76097_e = var1;
   }

   public void func_82572_b(long var1) {
      this.field_82575_g = var1;
   }

   public void func_76068_b(long var1) {
      this.field_76094_f = var1;
   }

   public void func_176143_a(BlockPos var1) {
      this.field_76099_c = var1.func_177958_n();
      this.field_76096_d = var1.func_177956_o();
      this.field_76097_e = var1.func_177952_p();
   }

   public String func_76065_j() {
      return this.field_76106_k;
   }

   public void func_76062_a(String var1) {
      this.field_76106_k = var1;
   }

   public int func_76088_k() {
      return this.field_76103_l;
   }

   public void func_76078_e(int var1) {
      this.field_76103_l = var1;
   }

   public long func_76057_l() {
      return this.field_76095_g;
   }

   public int func_176133_A() {
      return this.field_176157_p;
   }

   public void func_176142_i(int var1) {
      this.field_176157_p = var1;
   }

   public boolean func_76061_m() {
      return this.field_76102_o;
   }

   public void func_76069_a(boolean var1) {
      this.field_76102_o = var1;
   }

   public int func_76071_n() {
      return this.field_76114_p;
   }

   public void func_76090_f(int var1) {
      this.field_76114_p = var1;
   }

   public boolean func_76059_o() {
      return this.field_76104_m;
   }

   public void func_76084_b(boolean var1) {
      this.field_76104_m = var1;
   }

   public int func_76083_p() {
      return this.field_76101_n;
   }

   public void func_76080_g(int var1) {
      this.field_76101_n = var1;
   }

   public GameType func_76077_q() {
      return this.field_76113_q;
   }

   public boolean func_76089_r() {
      return this.field_76112_r;
   }

   public void func_176128_f(boolean var1) {
      this.field_76112_r = var1;
   }

   public void func_76060_a(GameType var1) {
      this.field_76113_q = var1;
   }

   public boolean func_76093_s() {
      return this.field_76111_s;
   }

   public void func_176119_g(boolean var1) {
      this.field_76111_s = var1;
   }

   public WorldType func_76067_t() {
      return this.field_76098_b;
   }

   public void func_76085_a(WorldType var1) {
      this.field_76098_b = var1;
   }

   public NBTTagCompound func_211027_A() {
      return this.field_82576_c;
   }

   public void func_212242_b(NBTTagCompound var1) {
      this.field_82576_c = var1;
   }

   public boolean func_76086_u() {
      return this.field_76110_t;
   }

   public void func_176121_c(boolean var1) {
      this.field_76110_t = var1;
   }

   public boolean func_76070_v() {
      return this.field_76109_u;
   }

   public void func_76091_d(boolean var1) {
      this.field_76109_u = var1;
   }

   public GameRules func_82574_x() {
      return this.field_82577_x;
   }

   public double func_176120_C() {
      return this.field_176151_B;
   }

   public double func_176126_D() {
      return this.field_176152_C;
   }

   public double func_176137_E() {
      return this.field_176146_D;
   }

   public void func_176145_a(double var1) {
      this.field_176146_D = var1;
   }

   public long func_176134_F() {
      return this.field_176147_E;
   }

   public void func_176135_e(long var1) {
      this.field_176147_E = var1;
   }

   public double func_176132_G() {
      return this.field_176148_F;
   }

   public void func_176118_b(double var1) {
      this.field_176148_F = var1;
   }

   public void func_176141_c(double var1) {
      this.field_176152_C = var1;
   }

   public void func_176124_d(double var1) {
      this.field_176151_B = var1;
   }

   public double func_176138_H() {
      return this.field_176149_G;
   }

   public void func_176129_e(double var1) {
      this.field_176149_G = var1;
   }

   public double func_176140_I() {
      return this.field_176153_H;
   }

   public void func_176125_f(double var1) {
      this.field_176153_H = var1;
   }

   public int func_176131_J() {
      return this.field_176154_I;
   }

   public int func_176139_K() {
      return this.field_176155_J;
   }

   public void func_176122_j(int var1) {
      this.field_176154_I = var1;
   }

   public void func_176136_k(int var1) {
      this.field_176155_J = var1;
   }

   public EnumDifficulty func_176130_y() {
      return this.field_176158_z;
   }

   public void func_176144_a(EnumDifficulty var1) {
      this.field_176158_z = var1;
   }

   public boolean func_176123_z() {
      return this.field_176150_A;
   }

   public void func_180783_e(boolean var1) {
      this.field_176150_A = var1;
   }

   public void func_85118_a(CrashReportCategory var1) {
      var1.func_189529_a("Level seed", () -> {
         return String.valueOf(this.func_76063_b());
      });
      var1.func_189529_a("Level generator", () -> {
         return String.format("ID %02d - %s, ver %d. Features enabled: %b", this.field_76098_b.func_82747_f(), this.field_76098_b.func_211888_a(), this.field_76098_b.func_77131_c(), this.field_76112_r);
      });
      var1.func_189529_a("Level generator options", () -> {
         return this.field_82576_c.toString();
      });
      var1.func_189529_a("Level spawn location", () -> {
         return CrashReportCategory.func_184876_a(this.field_76099_c, this.field_76096_d, this.field_76097_e);
      });
      var1.func_189529_a("Level time", () -> {
         return String.format("%d game time, %d day time", this.field_82575_g, this.field_76094_f);
      });
      var1.func_189529_a("Level dimension", () -> {
         return String.valueOf(this.field_76105_j);
      });
      var1.func_189529_a("Level storage version", () -> {
         String var1 = "Unknown?";

         try {
            switch(this.field_76103_l) {
            case 19132:
               var1 = "McRegion";
               break;
            case 19133:
               var1 = "Anvil";
            }
         } catch (Throwable var3) {
         }

         return String.format("0x%05X - %s", this.field_76103_l, var1);
      });
      var1.func_189529_a("Level weather", () -> {
         return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.field_76101_n, this.field_76104_m, this.field_76114_p, this.field_76102_o);
      });
      var1.func_189529_a("Level game mode", () -> {
         return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.field_76113_q.func_77149_b(), this.field_76113_q.func_77148_a(), this.field_76111_s, this.field_76110_t);
      });
   }

   public NBTTagCompound func_186347_a(DimensionType var1) {
      NBTTagCompound var2 = (NBTTagCompound)this.field_186348_N.get(var1);
      return var2 == null ? new NBTTagCompound() : var2;
   }

   public void func_186345_a(DimensionType var1, NBTTagCompound var2) {
      this.field_186348_N.put(var1, var2);
   }

   public int func_186344_K() {
      return this.field_186350_c;
   }

   public boolean func_186343_L() {
      return this.field_186351_d;
   }

   public String func_186346_M() {
      return this.field_186349_b;
   }

   public Set<String> func_197719_N() {
      return this.field_197721_N;
   }

   public Set<String> func_197720_O() {
      return this.field_197722_O;
   }

   @Nullable
   public NBTTagCompound func_201357_P() {
      return this.field_201358_Q;
   }

   public void func_201356_c(@Nullable NBTTagCompound var1) {
      this.field_201358_Q = var1;
   }

   static {
      field_176156_a = EnumDifficulty.NORMAL;
   }
}
