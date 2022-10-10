package net.minecraft.stats;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class StatList {
   public static final StatType<Block> field_188065_ae;
   public static final StatType<Item> field_188066_af;
   public static final StatType<Item> field_75929_E;
   public static final StatType<Item> field_199088_e;
   public static final StatType<Item> field_199089_f;
   public static final StatType<Item> field_188068_aj;
   public static final StatType<EntityType<?>> field_199090_h;
   public static final StatType<EntityType<?>> field_199091_i;
   public static final StatType<ResourceLocation> field_199092_j;
   public static final ResourceLocation field_75947_j;
   public static final ResourceLocation field_188097_g;
   public static final ResourceLocation field_188098_h;
   public static final ResourceLocation field_203284_n;
   public static final ResourceLocation field_188099_i;
   public static final ResourceLocation field_188100_j;
   public static final ResourceLocation field_188101_k;
   public static final ResourceLocation field_188102_l;
   public static final ResourceLocation field_211755_s;
   public static final ResourceLocation field_75943_n;
   public static final ResourceLocation field_188103_o;
   public static final ResourceLocation field_188104_p;
   public static final ResourceLocation field_211756_w;
   public static final ResourceLocation field_188106_r;
   public static final ResourceLocation field_188107_s;
   public static final ResourceLocation field_188108_t;
   public static final ResourceLocation field_188109_u;
   public static final ResourceLocation field_188110_v;
   public static final ResourceLocation field_75946_m;
   public static final ResourceLocation field_75953_u;
   public static final ResourceLocation field_75952_v;
   public static final ResourceLocation field_188111_y;
   public static final ResourceLocation field_212735_F;
   public static final ResourceLocation field_212736_G;
   public static final ResourceLocation field_188112_z;
   public static final ResourceLocation field_212737_I;
   public static final ResourceLocation field_212738_J;
   public static final ResourceLocation field_212739_K;
   public static final ResourceLocation field_188069_A;
   public static final ResourceLocation field_188070_B;
   public static final ResourceLocation field_151186_x;
   public static final ResourceLocation field_75932_A;
   public static final ResourceLocation field_188071_E;
   public static final ResourceLocation field_188074_H;
   public static final ResourceLocation field_188075_I;
   public static final ResourceLocation field_188076_J;
   public static final ResourceLocation field_188077_K;
   public static final ResourceLocation field_188078_L;
   public static final ResourceLocation field_188079_M;
   public static final ResourceLocation field_188080_N;
   public static final ResourceLocation field_212740_X;
   public static final ResourceLocation field_188081_O;
   public static final ResourceLocation field_188082_P;
   public static final ResourceLocation field_188083_Q;
   public static final ResourceLocation field_188084_R;
   public static final ResourceLocation field_188085_S;
   public static final ResourceLocation field_188086_T;
   public static final ResourceLocation field_188087_U;
   public static final ResourceLocation field_188088_V;
   public static final ResourceLocation field_188089_W;
   public static final ResourceLocation field_188090_X;
   public static final ResourceLocation field_188091_Y;
   public static final ResourceLocation field_188092_Z;
   public static final ResourceLocation field_188061_aa;
   public static final ResourceLocation field_188062_ab;
   public static final ResourceLocation field_188063_ac;
   public static final ResourceLocation field_188064_ad;
   public static final ResourceLocation field_191272_ae;

   public static void func_212734_a() {
   }

   private static ResourceLocation func_199084_a(String var0, IStatFormater var1) {
      ResourceLocation var2 = new ResourceLocation(var0);
      IRegistry.field_212623_l.func_82595_a(var2, var2);
      field_199092_j.func_199077_a(var2, var1);
      return var2;
   }

   private static <T> StatType<T> func_199085_a(String var0, IRegistry<T> var1) {
      StatType var2 = new StatType(var1);
      IRegistry.field_212634_w.func_82595_a(new ResourceLocation(var0), var2);
      return var2;
   }

   static {
      field_188065_ae = func_199085_a("mined", IRegistry.field_212618_g);
      field_188066_af = func_199085_a("crafted", IRegistry.field_212630_s);
      field_75929_E = func_199085_a("used", IRegistry.field_212630_s);
      field_199088_e = func_199085_a("broken", IRegistry.field_212630_s);
      field_199089_f = func_199085_a("picked_up", IRegistry.field_212630_s);
      field_188068_aj = func_199085_a("dropped", IRegistry.field_212630_s);
      field_199090_h = func_199085_a("killed", IRegistry.field_212629_r);
      field_199091_i = func_199085_a("killed_by", IRegistry.field_212629_r);
      field_199092_j = func_199085_a("custom", IRegistry.field_212623_l);
      field_75947_j = func_199084_a("leave_game", IStatFormater.DEFAULT);
      field_188097_g = func_199084_a("play_one_minute", IStatFormater.TIME);
      field_188098_h = func_199084_a("time_since_death", IStatFormater.TIME);
      field_203284_n = func_199084_a("time_since_rest", IStatFormater.TIME);
      field_188099_i = func_199084_a("sneak_time", IStatFormater.TIME);
      field_188100_j = func_199084_a("walk_one_cm", IStatFormater.DISTANCE);
      field_188101_k = func_199084_a("crouch_one_cm", IStatFormater.DISTANCE);
      field_188102_l = func_199084_a("sprint_one_cm", IStatFormater.DISTANCE);
      field_211755_s = func_199084_a("walk_on_water_one_cm", IStatFormater.DISTANCE);
      field_75943_n = func_199084_a("fall_one_cm", IStatFormater.DISTANCE);
      field_188103_o = func_199084_a("climb_one_cm", IStatFormater.DISTANCE);
      field_188104_p = func_199084_a("fly_one_cm", IStatFormater.DISTANCE);
      field_211756_w = func_199084_a("walk_under_water_one_cm", IStatFormater.DISTANCE);
      field_188106_r = func_199084_a("minecart_one_cm", IStatFormater.DISTANCE);
      field_188107_s = func_199084_a("boat_one_cm", IStatFormater.DISTANCE);
      field_188108_t = func_199084_a("pig_one_cm", IStatFormater.DISTANCE);
      field_188109_u = func_199084_a("horse_one_cm", IStatFormater.DISTANCE);
      field_188110_v = func_199084_a("aviate_one_cm", IStatFormater.DISTANCE);
      field_75946_m = func_199084_a("swim_one_cm", IStatFormater.DISTANCE);
      field_75953_u = func_199084_a("jump", IStatFormater.DEFAULT);
      field_75952_v = func_199084_a("drop", IStatFormater.DEFAULT);
      field_188111_y = func_199084_a("damage_dealt", IStatFormater.DIVIDE_BY_TEN);
      field_212735_F = func_199084_a("damage_dealt_absorbed", IStatFormater.DIVIDE_BY_TEN);
      field_212736_G = func_199084_a("damage_dealt_resisted", IStatFormater.DIVIDE_BY_TEN);
      field_188112_z = func_199084_a("damage_taken", IStatFormater.DIVIDE_BY_TEN);
      field_212737_I = func_199084_a("damage_blocked_by_shield", IStatFormater.DIVIDE_BY_TEN);
      field_212738_J = func_199084_a("damage_absorbed", IStatFormater.DIVIDE_BY_TEN);
      field_212739_K = func_199084_a("damage_resisted", IStatFormater.DIVIDE_BY_TEN);
      field_188069_A = func_199084_a("deaths", IStatFormater.DEFAULT);
      field_188070_B = func_199084_a("mob_kills", IStatFormater.DEFAULT);
      field_151186_x = func_199084_a("animals_bred", IStatFormater.DEFAULT);
      field_75932_A = func_199084_a("player_kills", IStatFormater.DEFAULT);
      field_188071_E = func_199084_a("fish_caught", IStatFormater.DEFAULT);
      field_188074_H = func_199084_a("talked_to_villager", IStatFormater.DEFAULT);
      field_188075_I = func_199084_a("traded_with_villager", IStatFormater.DEFAULT);
      field_188076_J = func_199084_a("eat_cake_slice", IStatFormater.DEFAULT);
      field_188077_K = func_199084_a("fill_cauldron", IStatFormater.DEFAULT);
      field_188078_L = func_199084_a("use_cauldron", IStatFormater.DEFAULT);
      field_188079_M = func_199084_a("clean_armor", IStatFormater.DEFAULT);
      field_188080_N = func_199084_a("clean_banner", IStatFormater.DEFAULT);
      field_212740_X = func_199084_a("clean_shulker_box", IStatFormater.DEFAULT);
      field_188081_O = func_199084_a("interact_with_brewingstand", IStatFormater.DEFAULT);
      field_188082_P = func_199084_a("interact_with_beacon", IStatFormater.DEFAULT);
      field_188083_Q = func_199084_a("inspect_dropper", IStatFormater.DEFAULT);
      field_188084_R = func_199084_a("inspect_hopper", IStatFormater.DEFAULT);
      field_188085_S = func_199084_a("inspect_dispenser", IStatFormater.DEFAULT);
      field_188086_T = func_199084_a("play_noteblock", IStatFormater.DEFAULT);
      field_188087_U = func_199084_a("tune_noteblock", IStatFormater.DEFAULT);
      field_188088_V = func_199084_a("pot_flower", IStatFormater.DEFAULT);
      field_188089_W = func_199084_a("trigger_trapped_chest", IStatFormater.DEFAULT);
      field_188090_X = func_199084_a("open_enderchest", IStatFormater.DEFAULT);
      field_188091_Y = func_199084_a("enchant_item", IStatFormater.DEFAULT);
      field_188092_Z = func_199084_a("play_record", IStatFormater.DEFAULT);
      field_188061_aa = func_199084_a("interact_with_furnace", IStatFormater.DEFAULT);
      field_188062_ab = func_199084_a("interact_with_crafting_table", IStatFormater.DEFAULT);
      field_188063_ac = func_199084_a("open_chest", IStatFormater.DEFAULT);
      field_188064_ad = func_199084_a("sleep_in_bed", IStatFormater.DEFAULT);
      field_191272_ae = func_199084_a("open_shulker_box", IStatFormater.DEFAULT);
   }
}
