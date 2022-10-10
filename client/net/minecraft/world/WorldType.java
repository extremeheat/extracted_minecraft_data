package net.minecraft.world;

public class WorldType {
   public static final WorldType[] field_77139_a = new WorldType[16];
   public static final WorldType field_77137_b = (new WorldType(0, "default", 1)).func_77129_f();
   public static final WorldType field_77138_c = (new WorldType(1, "flat")).func_205392_a(true);
   public static final WorldType field_77135_d = new WorldType(2, "largeBiomes");
   public static final WorldType field_151360_e = (new WorldType(3, "amplified")).func_151358_j();
   public static final WorldType field_180271_f = (new WorldType(4, "customized", "normal", 0)).func_205392_a(true).func_77124_a(false);
   public static final WorldType field_205394_h = (new WorldType(5, "buffet")).func_205392_a(true);
   public static final WorldType field_180272_g = new WorldType(6, "debug_all_block_states");
   public static final WorldType field_77136_e = (new WorldType(8, "default_1_1", 0)).func_77124_a(false);
   private final int field_82748_f;
   private final String field_77133_f;
   private final String field_211890_l;
   private final int field_77134_g;
   private boolean field_77140_h;
   private boolean field_77141_i;
   private boolean field_151361_l;
   private boolean field_205395_p;

   private WorldType(int var1, String var2) {
      this(var1, var2, var2, 0);
   }

   private WorldType(int var1, String var2, int var3) {
      this(var1, var2, var2, var3);
   }

   private WorldType(int var1, String var2, String var3, int var4) {
      super();
      this.field_77133_f = var2;
      this.field_211890_l = var3;
      this.field_77134_g = var4;
      this.field_77140_h = true;
      this.field_82748_f = var1;
      field_77139_a[var1] = this;
   }

   public String func_211888_a() {
      return this.field_77133_f;
   }

   public String func_211889_b() {
      return this.field_211890_l;
   }

   public String func_77128_b() {
      return "generator." + this.field_77133_f;
   }

   public String func_151359_c() {
      return this.func_77128_b() + ".info";
   }

   public int func_77131_c() {
      return this.field_77134_g;
   }

   public WorldType func_77132_a(int var1) {
      return this == field_77137_b && var1 == 0 ? field_77136_e : this;
   }

   public boolean func_205393_e() {
      return this.field_205395_p;
   }

   public WorldType func_205392_a(boolean var1) {
      this.field_205395_p = var1;
      return this;
   }

   private WorldType func_77124_a(boolean var1) {
      this.field_77140_h = var1;
      return this;
   }

   public boolean func_77126_d() {
      return this.field_77140_h;
   }

   private WorldType func_77129_f() {
      this.field_77141_i = true;
      return this;
   }

   public boolean func_77125_e() {
      return this.field_77141_i;
   }

   public static WorldType func_77130_a(String var0) {
      WorldType[] var1 = field_77139_a;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         WorldType var4 = var1[var3];
         if (var4 != null && var4.field_77133_f.equalsIgnoreCase(var0)) {
            return var4;
         }
      }

      return null;
   }

   public int func_82747_f() {
      return this.field_82748_f;
   }

   public boolean func_151357_h() {
      return this.field_151361_l;
   }

   private WorldType func_151358_j() {
      this.field_151361_l = true;
      return this;
   }
}
