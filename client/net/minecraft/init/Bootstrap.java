package net.minecraft.init;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFire;
import net.minecraft.item.Item;
import net.minecraft.stats.StatList;

public class Bootstrap {
   private static boolean field_151355_a = false;

   static void func_151353_a() {
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151032_g, new Bootstrap$1());
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151110_aK, new Bootstrap$2());
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151126_ay, new Bootstrap$3());
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151062_by, new Bootstrap$4());
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151068_bn, new Bootstrap$5());
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151063_bx, new Bootstrap$6());
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151152_bP, new Bootstrap$7());
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151059_bz, new Bootstrap$8());
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151124_az, new Bootstrap$9());
      Bootstrap$10 var0 = new Bootstrap$10();
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151129_at, var0);
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151131_as, var0);
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151133_ar, new Bootstrap$11());
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151033_d, new Bootstrap$12());
      BlockDispenser.field_149943_a.func_82595_a(Items.field_151100_aR, new Bootstrap$13());
      BlockDispenser.field_149943_a.func_82595_a(Item.func_150898_a(Blocks.field_150335_W), new Bootstrap$14());
   }

   public static void func_151354_b() {
      if (!field_151355_a) {
         field_151355_a = true;
         Block.func_149671_p();
         BlockFire.func_149843_e();
         Item.func_150900_l();
         StatList.func_151178_a();
         func_151353_a();
      }
   }
}
