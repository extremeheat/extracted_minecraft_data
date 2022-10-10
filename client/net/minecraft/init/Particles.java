package net.minecraft.init;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public class Particles {
   public static final BasicParticleType field_197608_a;
   public static final BasicParticleType field_197609_b;
   public static final BasicParticleType field_197610_c;
   public static final ParticleType<BlockParticleData> field_197611_d;
   public static final BasicParticleType field_197612_e;
   public static final BasicParticleType field_203220_f;
   public static final BasicParticleType field_197613_f;
   public static final BasicParticleType field_197614_g;
   public static final BasicParticleType field_197615_h;
   public static final BasicParticleType field_197616_i;
   public static final BasicParticleType field_197617_j;
   public static final BasicParticleType field_197618_k;
   public static final ParticleType<RedstoneParticleData> field_197619_l;
   public static final BasicParticleType field_197620_m;
   public static final BasicParticleType field_197621_n;
   public static final BasicParticleType field_197622_o;
   public static final BasicParticleType field_197623_p;
   public static final BasicParticleType field_197624_q;
   public static final BasicParticleType field_197625_r;
   public static final BasicParticleType field_197626_s;
   public static final BasicParticleType field_197627_t;
   public static final ParticleType<BlockParticleData> field_197628_u;
   public static final BasicParticleType field_197629_v;
   public static final BasicParticleType field_197630_w;
   public static final BasicParticleType field_197631_x;
   public static final BasicParticleType field_197632_y;
   public static final BasicParticleType field_197633_z;
   public static final BasicParticleType field_197590_A;
   public static final ParticleType<ItemParticleData> field_197591_B;
   public static final BasicParticleType field_197592_C;
   public static final BasicParticleType field_197593_D;
   public static final BasicParticleType field_197594_E;
   public static final BasicParticleType field_197595_F;
   public static final BasicParticleType field_197596_G;
   public static final BasicParticleType field_197597_H;
   public static final BasicParticleType field_197598_I;
   public static final BasicParticleType field_197599_J;
   public static final BasicParticleType field_197600_K;
   public static final BasicParticleType field_197601_L;
   public static final BasicParticleType field_197602_M;
   public static final BasicParticleType field_197603_N;
   public static final BasicParticleType field_197604_O;
   public static final BasicParticleType field_197605_P;
   public static final BasicParticleType field_197606_Q;
   public static final BasicParticleType field_197607_R;
   public static final BasicParticleType field_203217_T;
   public static final BasicParticleType field_203218_U;
   public static final BasicParticleType field_203219_V;
   public static final BasicParticleType field_205167_W;
   public static final BasicParticleType field_206864_X;

   private static <T extends ParticleType<?>> T func_197589_a(String var0) {
      ParticleType var1 = (ParticleType)IRegistry.field_212632_u.func_212608_b(new ResourceLocation(var0));
      if (var1 == null) {
         throw new IllegalStateException("Invalid or unknown particle type: " + var0);
      } else {
         return var1;
      }
   }

   static {
      if (!Bootstrap.func_179869_a()) {
         throw new RuntimeException("Accessed particles before Bootstrap!");
      } else {
         field_197608_a = (BasicParticleType)func_197589_a("ambient_entity_effect");
         field_197609_b = (BasicParticleType)func_197589_a("angry_villager");
         field_197610_c = (BasicParticleType)func_197589_a("barrier");
         field_197611_d = func_197589_a("block");
         field_197612_e = (BasicParticleType)func_197589_a("bubble");
         field_203220_f = (BasicParticleType)func_197589_a("bubble_column_up");
         field_197613_f = (BasicParticleType)func_197589_a("cloud");
         field_197614_g = (BasicParticleType)func_197589_a("crit");
         field_197615_h = (BasicParticleType)func_197589_a("damage_indicator");
         field_197616_i = (BasicParticleType)func_197589_a("dragon_breath");
         field_197617_j = (BasicParticleType)func_197589_a("dripping_lava");
         field_197618_k = (BasicParticleType)func_197589_a("dripping_water");
         field_197619_l = func_197589_a("dust");
         field_197620_m = (BasicParticleType)func_197589_a("effect");
         field_197621_n = (BasicParticleType)func_197589_a("elder_guardian");
         field_197622_o = (BasicParticleType)func_197589_a("enchanted_hit");
         field_197623_p = (BasicParticleType)func_197589_a("enchant");
         field_197624_q = (BasicParticleType)func_197589_a("end_rod");
         field_197625_r = (BasicParticleType)func_197589_a("entity_effect");
         field_197626_s = (BasicParticleType)func_197589_a("explosion_emitter");
         field_197627_t = (BasicParticleType)func_197589_a("explosion");
         field_197628_u = func_197589_a("falling_dust");
         field_197629_v = (BasicParticleType)func_197589_a("firework");
         field_197630_w = (BasicParticleType)func_197589_a("fishing");
         field_197631_x = (BasicParticleType)func_197589_a("flame");
         field_197632_y = (BasicParticleType)func_197589_a("happy_villager");
         field_197633_z = (BasicParticleType)func_197589_a("heart");
         field_197590_A = (BasicParticleType)func_197589_a("instant_effect");
         field_197591_B = func_197589_a("item");
         field_197592_C = (BasicParticleType)func_197589_a("item_slime");
         field_197593_D = (BasicParticleType)func_197589_a("item_snowball");
         field_197594_E = (BasicParticleType)func_197589_a("large_smoke");
         field_197595_F = (BasicParticleType)func_197589_a("lava");
         field_197596_G = (BasicParticleType)func_197589_a("mycelium");
         field_197597_H = (BasicParticleType)func_197589_a("note");
         field_197598_I = (BasicParticleType)func_197589_a("poof");
         field_197599_J = (BasicParticleType)func_197589_a("portal");
         field_197600_K = (BasicParticleType)func_197589_a("rain");
         field_197601_L = (BasicParticleType)func_197589_a("smoke");
         field_197602_M = (BasicParticleType)func_197589_a("spit");
         field_197603_N = (BasicParticleType)func_197589_a("sweep_attack");
         field_197604_O = (BasicParticleType)func_197589_a("totem_of_undying");
         field_197605_P = (BasicParticleType)func_197589_a("underwater");
         field_197606_Q = (BasicParticleType)func_197589_a("splash");
         field_197607_R = (BasicParticleType)func_197589_a("witch");
         field_203217_T = (BasicParticleType)func_197589_a("bubble_pop");
         field_203218_U = (BasicParticleType)func_197589_a("current_down");
         field_203219_V = (BasicParticleType)func_197589_a("squid_ink");
         field_205167_W = (BasicParticleType)func_197589_a("nautilus");
         field_206864_X = (BasicParticleType)func_197589_a("dolphin");
      }
   }
}
