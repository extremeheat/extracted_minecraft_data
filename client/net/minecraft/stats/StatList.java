package net.minecraft.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList$EntityEggInfo;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ChatComponentTranslation;

public class StatList {
   protected static Map field_75942_a = new HashMap();
   public static List field_75940_b = new ArrayList();
   public static List field_75941_c = new ArrayList();
   public static List field_75938_d = new ArrayList();
   public static List field_75939_e = new ArrayList();
   public static StatBase field_75947_j = new StatBasic("stat.leaveGame", new ChatComponentTranslation("stat.leaveGame")).func_75966_h().func_75971_g();
   public static StatBase field_75948_k = new StatBasic("stat.playOneMinute", new ChatComponentTranslation("stat.playOneMinute"), StatBase.field_75981_i)
      .func_75966_h()
      .func_75971_g();
   public static StatBase field_75945_l = new StatBasic("stat.walkOneCm", new ChatComponentTranslation("stat.walkOneCm"), StatBase.field_75979_j)
      .func_75966_h()
      .func_75971_g();
   public static StatBase field_75946_m = new StatBasic("stat.swimOneCm", new ChatComponentTranslation("stat.swimOneCm"), StatBase.field_75979_j)
      .func_75966_h()
      .func_75971_g();
   public static StatBase field_75943_n = new StatBasic("stat.fallOneCm", new ChatComponentTranslation("stat.fallOneCm"), StatBase.field_75979_j)
      .func_75966_h()
      .func_75971_g();
   public static StatBase field_75944_o = new StatBasic("stat.climbOneCm", new ChatComponentTranslation("stat.climbOneCm"), StatBase.field_75979_j)
      .func_75966_h()
      .func_75971_g();
   public static StatBase field_75958_p = new StatBasic("stat.flyOneCm", new ChatComponentTranslation("stat.flyOneCm"), StatBase.field_75979_j)
      .func_75966_h()
      .func_75971_g();
   public static StatBase field_75957_q = new StatBasic("stat.diveOneCm", new ChatComponentTranslation("stat.diveOneCm"), StatBase.field_75979_j)
      .func_75966_h()
      .func_75971_g();
   public static StatBase field_75956_r = new StatBasic("stat.minecartOneCm", new ChatComponentTranslation("stat.minecartOneCm"), StatBase.field_75979_j)
      .func_75966_h()
      .func_75971_g();
   public static StatBase field_75955_s = new StatBasic("stat.boatOneCm", new ChatComponentTranslation("stat.boatOneCm"), StatBase.field_75979_j)
      .func_75966_h()
      .func_75971_g();
   public static StatBase field_75954_t = new StatBasic("stat.pigOneCm", new ChatComponentTranslation("stat.pigOneCm"), StatBase.field_75979_j)
      .func_75966_h()
      .func_75971_g();
   public static StatBase field_151185_q = new StatBasic("stat.horseOneCm", new ChatComponentTranslation("stat.horseOneCm"), StatBase.field_75979_j)
      .func_75966_h()
      .func_75971_g();
   public static StatBase field_75953_u = new StatBasic("stat.jump", new ChatComponentTranslation("stat.jump")).func_75966_h().func_75971_g();
   public static StatBase field_75952_v = new StatBasic("stat.drop", new ChatComponentTranslation("stat.drop")).func_75966_h().func_75971_g();
   public static StatBase field_75951_w = new StatBasic("stat.damageDealt", new ChatComponentTranslation("stat.damageDealt"), StatBase.field_111202_k)
      .func_75971_g();
   public static StatBase field_75961_x = new StatBasic("stat.damageTaken", new ChatComponentTranslation("stat.damageTaken"), StatBase.field_111202_k)
      .func_75971_g();
   public static StatBase field_75960_y = new StatBasic("stat.deaths", new ChatComponentTranslation("stat.deaths")).func_75971_g();
   public static StatBase field_75959_z = new StatBasic("stat.mobKills", new ChatComponentTranslation("stat.mobKills")).func_75971_g();
   public static StatBase field_151186_x = new StatBasic("stat.animalsBred", new ChatComponentTranslation("stat.animalsBred")).func_75971_g();
   public static StatBase field_75932_A = new StatBasic("stat.playerKills", new ChatComponentTranslation("stat.playerKills")).func_75971_g();
   public static StatBase field_75933_B = new StatBasic("stat.fishCaught", new ChatComponentTranslation("stat.fishCaught")).func_75971_g();
   public static StatBase field_151183_A = new StatBasic("stat.junkFished", new ChatComponentTranslation("stat.junkFished")).func_75971_g();
   public static StatBase field_151184_B = new StatBasic("stat.treasureFished", new ChatComponentTranslation("stat.treasureFished")).func_75971_g();
   public static final StatBase[] field_75934_C = new StatBase[4096];
   public static final StatBase[] field_75928_D = new StatBase[32000];
   public static final StatBase[] field_75929_E = new StatBase[32000];
   public static final StatBase[] field_75930_F = new StatBase[32000];

   public static void func_151178_a() {
      func_151181_c();
      func_75925_c();
      func_151179_e();
      func_75918_d();
      AchievementList.func_75997_a();
      EntityList.func_151514_a();
   }

   private static void func_75918_d() {
      HashSet var0 = new HashSet();

      for(IRecipe var2 : CraftingManager.func_77594_a().func_77592_b()) {
         if (var2.func_77571_b() != null) {
            var0.add(var2.func_77571_b().func_77973_b());
         }
      }

      for(ItemStack var6 : FurnaceRecipes.func_77602_a().func_77599_b().values()) {
         var0.add(var6.func_77973_b());
      }

      for(Item var7 : var0) {
         if (var7 != null) {
            int var3 = Item.func_150891_b(var7);
            field_75928_D[var3] = new StatCrafting(
                  "stat.craftItem." + var3, new ChatComponentTranslation("stat.craftItem", new ItemStack(var7).func_151000_E()), var7
               )
               .func_75971_g();
         }
      }

      func_75924_a(field_75928_D);
   }

   private static void func_151181_c() {
      for(Block var1 : Block.field_149771_c) {
         if (Item.func_150898_a(var1) != null) {
            int var2 = Block.func_149682_b(var1);
            if (var1.func_149652_G()) {
               field_75934_C[var2] = new StatCrafting(
                     "stat.mineBlock." + var2, new ChatComponentTranslation("stat.mineBlock", new ItemStack(var1).func_151000_E()), Item.func_150898_a(var1)
                  )
                  .func_75971_g();
               field_75939_e.add((StatCrafting)field_75934_C[var2]);
            }
         }
      }

      func_75924_a(field_75934_C);
   }

   private static void func_75925_c() {
      for(Item var1 : Item.field_150901_e) {
         if (var1 != null) {
            int var2 = Item.func_150891_b(var1);
            field_75929_E[var2] = new StatCrafting(
                  "stat.useItem." + var2, new ChatComponentTranslation("stat.useItem", new ItemStack(var1).func_151000_E()), var1
               )
               .func_75971_g();
            if (!(var1 instanceof ItemBlock)) {
               field_75938_d.add((StatCrafting)field_75929_E[var2]);
            }
         }
      }

      func_75924_a(field_75929_E);
   }

   private static void func_151179_e() {
      for(Item var1 : Item.field_150901_e) {
         if (var1 != null) {
            int var2 = Item.func_150891_b(var1);
            if (var1.func_77645_m()) {
               field_75930_F[var2] = new StatCrafting(
                     "stat.breakItem." + var2, new ChatComponentTranslation("stat.breakItem", new ItemStack(var1).func_151000_E()), var1
                  )
                  .func_75971_g();
            }
         }
      }

      func_75924_a(field_75930_F);
   }

   private static void func_75924_a(StatBase[] var0) {
      func_151180_a(var0, Blocks.field_150355_j, Blocks.field_150358_i);
      func_151180_a(var0, Blocks.field_150353_l, Blocks.field_150356_k);
      func_151180_a(var0, Blocks.field_150428_aP, Blocks.field_150423_aK);
      func_151180_a(var0, Blocks.field_150470_am, Blocks.field_150460_al);
      func_151180_a(var0, Blocks.field_150439_ay, Blocks.field_150450_ax);
      func_151180_a(var0, Blocks.field_150416_aS, Blocks.field_150413_aR);
      func_151180_a(var0, Blocks.field_150455_bV, Blocks.field_150441_bU);
      func_151180_a(var0, Blocks.field_150429_aA, Blocks.field_150437_az);
      func_151180_a(var0, Blocks.field_150374_bv, Blocks.field_150379_bu);
      func_151180_a(var0, Blocks.field_150337_Q, Blocks.field_150338_P);
      func_151180_a(var0, Blocks.field_150334_T, Blocks.field_150333_U);
      func_151180_a(var0, Blocks.field_150373_bw, Blocks.field_150376_bx);
      func_151180_a(var0, Blocks.field_150349_c, Blocks.field_150346_d);
      func_151180_a(var0, Blocks.field_150458_ak, Blocks.field_150346_d);
   }

   private static void func_151180_a(StatBase[] var0, Block var1, Block var2) {
      int var3 = Block.func_149682_b(var1);
      int var4 = Block.func_149682_b(var2);
      if (var0[var3] != null && var0[var4] == null) {
         var0[var4] = var0[var3];
      } else {
         field_75940_b.remove(var0[var3]);
         field_75939_e.remove(var0[var3]);
         field_75941_c.remove(var0[var3]);
         var0[var3] = var0[var4];
      }
   }

   public static StatBase func_151182_a(EntityList$EntityEggInfo var0) {
      String var1 = EntityList.func_75617_a(var0.field_75613_a);
      return var1 == null
         ? null
         : new StatBase("stat.killEntity." + var1, new ChatComponentTranslation("stat.entityKill", new ChatComponentTranslation("entity." + var1 + ".name")))
            .func_75971_g();
   }

   public static StatBase func_151176_b(EntityList$EntityEggInfo var0) {
      String var1 = EntityList.func_75617_a(var0.field_75613_a);
      return var1 == null
         ? null
         : new StatBase(
               "stat.entityKilledBy." + var1, new ChatComponentTranslation("stat.entityKilledBy", new ChatComponentTranslation("entity." + var1 + ".name"))
            )
            .func_75971_g();
   }

   public static StatBase func_151177_a(String var0) {
      return (StatBase)field_75942_a.get(var0);
   }
}
