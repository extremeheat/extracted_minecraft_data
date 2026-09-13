package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIEatGrass extends EntityAIBase {
   private EntityLiving field_151500_b;
   private World field_151501_c;
   int field_151502_a;

   public EntityAIEatGrass(EntityLiving var1) {
      super();
      this.field_151500_b = var1;
      this.field_151501_c = var1.field_70170_p;
      this.func_75248_a(7);
   }

   @Override
   public boolean func_75250_a() {
      if (this.field_151500_b.func_70681_au().nextInt(this.field_151500_b.func_70631_g_() ? 50 : 1000) != 0) {
         return false;
      } else {
         int var1 = MathHelper.func_76128_c(this.field_151500_b.field_70165_t);
         int var2 = MathHelper.func_76128_c(this.field_151500_b.field_70163_u);
         int var3 = MathHelper.func_76128_c(this.field_151500_b.field_70161_v);
         if (this.field_151501_c.func_147439_a(var1, var2, var3) == Blocks.field_150329_H && this.field_151501_c.func_72805_g(var1, var2, var3) == 1) {
            return true;
         } else {
            return this.field_151501_c.func_147439_a(var1, var2 - 1, var3) == Blocks.field_150349_c;
         }
      }
   }

   @Override
   public void func_75249_e() {
      this.field_151502_a = 40;
      this.field_151501_c.func_72960_a(this.field_151500_b, (byte)10);
      this.field_151500_b.func_70661_as().func_75499_g();
   }

   @Override
   public void func_75251_c() {
      this.field_151502_a = 0;
   }

   @Override
   public boolean func_75253_b() {
      return this.field_151502_a > 0;
   }

   public int func_151499_f() {
      return this.field_151502_a;
   }

   @Override
   public void func_75246_d() {
      this.field_151502_a = Math.max(0, this.field_151502_a - 1);
      if (this.field_151502_a == 4) {
         int var1 = MathHelper.func_76128_c(this.field_151500_b.field_70165_t);
         int var2 = MathHelper.func_76128_c(this.field_151500_b.field_70163_u);
         int var3 = MathHelper.func_76128_c(this.field_151500_b.field_70161_v);
         if (this.field_151501_c.func_147439_a(var1, var2, var3) == Blocks.field_150329_H) {
            if (this.field_151501_c.func_82736_K().func_82766_b("mobGriefing")) {
               this.field_151501_c.func_147480_a(var1, var2, var3, false);
            }

            this.field_151500_b.func_70615_aA();
         } else if (this.field_151501_c.func_147439_a(var1, var2 - 1, var3) == Blocks.field_150349_c) {
            if (this.field_151501_c.func_82736_K().func_82766_b("mobGriefing")) {
               this.field_151501_c.func_72926_e(2001, var1, var2 - 1, var3, Block.func_149682_b(Blocks.field_150349_c));
               this.field_151501_c.func_147465_d(var1, var2 - 1, var3, Blocks.field_150346_d, 0, 2);
            }

            this.field_151500_b.func_70615_aA();
         }
      }
   }
}
