package net.minecraft.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.storage.WorldInfo;

public final class WorldSettings {
   private final long field_77174_a;
   private final GameType field_77172_b;
   private final boolean field_77173_c;
   private final boolean field_77170_d;
   private final WorldType field_77171_e;
   private boolean field_77168_f;
   private boolean field_77169_g;
   private JsonElement field_82751_h;

   public WorldSettings(long var1, GameType var3, boolean var4, boolean var5, WorldType var6) {
      super();
      this.field_82751_h = new JsonObject();
      this.field_77174_a = var1;
      this.field_77172_b = var3;
      this.field_77173_c = var4;
      this.field_77170_d = var5;
      this.field_77171_e = var6;
   }

   public WorldSettings(WorldInfo var1) {
      this(var1.func_76063_b(), var1.func_76077_q(), var1.func_76089_r(), var1.func_76093_s(), var1.func_76067_t());
   }

   public WorldSettings func_77159_a() {
      this.field_77169_g = true;
      return this;
   }

   public WorldSettings func_77166_b() {
      this.field_77168_f = true;
      return this;
   }

   public WorldSettings func_205390_a(JsonElement var1) {
      this.field_82751_h = var1;
      return this;
   }

   public boolean func_77167_c() {
      return this.field_77169_g;
   }

   public long func_77160_d() {
      return this.field_77174_a;
   }

   public GameType func_77162_e() {
      return this.field_77172_b;
   }

   public boolean func_77158_f() {
      return this.field_77170_d;
   }

   public boolean func_77164_g() {
      return this.field_77173_c;
   }

   public WorldType func_77165_h() {
      return this.field_77171_e;
   }

   public boolean func_77163_i() {
      return this.field_77168_f;
   }

   public static GameType func_77161_a(int var0) {
      return GameType.func_77146_a(var0);
   }

   public JsonElement func_205391_j() {
      return this.field_82751_h;
   }
}
