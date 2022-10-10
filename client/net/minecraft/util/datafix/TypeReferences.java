package net.minecraft.util.datafix;

import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DSL.TypeReference;

public class TypeReferences {
   public static final TypeReference field_211285_a;
   public static final TypeReference field_211286_b;
   public static final TypeReference field_211287_c;
   public static final TypeReference field_211288_d;
   public static final TypeReference field_211289_e;
   public static final TypeReference field_211290_f;
   public static final TypeReference field_211291_g;
   public static final TypeReference field_211292_h;
   public static final TypeReference field_211293_i;
   public static final TypeReference field_211294_j;
   public static final TypeReference field_211295_k;
   public static final TypeReference field_211296_l;
   public static final TypeReference field_211297_m;
   public static final TypeReference field_211298_n;
   public static final TypeReference field_211299_o;
   public static final TypeReference field_211300_p;
   public static final TypeReference field_211301_q;
   public static final TypeReference field_211302_r;
   public static final TypeReference field_211303_s;
   public static final TypeReference field_211873_t;
   public static final TypeReference field_211874_u;
   public static final TypeReference field_211304_t;
   public static final TypeReference field_211305_u;

   static {
      field_211285_a = DataFixTypes.LEVEL;
      field_211286_b = DataFixTypes.PLAYER;
      field_211287_c = DataFixTypes.CHUNK;
      field_211288_d = DataFixTypes.HOTBAR;
      field_211289_e = DataFixTypes.OPTIONS;
      field_211290_f = DataFixTypes.STRUCTURE;
      field_211291_g = DataFixTypes.STATS;
      field_211292_h = DataFixTypes.SAVED_DATA;
      field_211293_i = DataFixTypes.ADVANCEMENTS;
      field_211294_j = () -> {
         return "block_entity";
      };
      field_211295_k = () -> {
         return "item_stack";
      };
      field_211296_l = () -> {
         return "block_state";
      };
      field_211297_m = () -> {
         return "entity_name";
      };
      field_211298_n = () -> {
         return "entity_tree";
      };
      field_211299_o = () -> {
         return "entity";
      };
      field_211300_p = () -> {
         return "block_name";
      };
      field_211301_q = () -> {
         return "item_name";
      };
      field_211302_r = () -> {
         return "untagged_spawner";
      };
      field_211303_s = () -> {
         return "structure_feature";
      };
      field_211873_t = () -> {
         return "objective";
      };
      field_211874_u = () -> {
         return "team";
      };
      field_211304_t = () -> {
         return "recipe";
      };
      field_211305_u = () -> {
         return "biome";
      };
   }
}
