package net.minecraft.entity.ai;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIEatGrass extends EntityAIBase {
   private static final Predicate<IBlockState> field_179505_b;
   private final EntityLiving field_151500_b;
   private final World field_151501_c;
   private int field_151502_a;

   public EntityAIEatGrass(EntityLiving var1) {
      super();
      this.field_151500_b = var1;
      this.field_151501_c = var1.field_70170_p;
      this.func_75248_a(7);
   }

   public boolean func_75250_a() {
      if (this.field_151500_b.func_70681_au().nextInt(this.field_151500_b.func_70631_g_() ? 50 : 1000) != 0) {
         return false;
      } else {
         BlockPos var1 = new BlockPos(this.field_151500_b.field_70165_t, this.field_151500_b.field_70163_u, this.field_151500_b.field_70161_v);
         if (field_179505_b.test(this.field_151501_c.func_180495_p(var1))) {
            return true;
         } else {
            return this.field_151501_c.func_180495_p(var1.func_177977_b()).func_177230_c() == Blocks.field_196658_i;
         }
      }
   }

   public void func_75249_e() {
      this.field_151502_a = 40;
      this.field_151501_c.func_72960_a(this.field_151500_b, (byte)10);
      this.field_151500_b.func_70661_as().func_75499_g();
   }

   public void func_75251_c() {
      this.field_151502_a = 0;
   }

   public boolean func_75253_b() {
      return this.field_151502_a > 0;
   }

   public int func_151499_f() {
      return this.field_151502_a;
   }

   public void func_75246_d() {
      this.field_151502_a = Math.max(0, this.field_151502_a - 1);
      if (this.field_151502_a == 4) {
         BlockPos var1 = new BlockPos(this.field_151500_b.field_70165_t, this.field_151500_b.field_70163_u, this.field_151500_b.field_70161_v);
         if (field_179505_b.test(this.field_151501_c.func_180495_p(var1))) {
            if (this.field_151501_c.func_82736_K().func_82766_b("mobGriefing")) {
               this.field_151501_c.func_175655_b(var1, false);
            }

            this.field_151500_b.func_70615_aA();
         } else {
            BlockPos var2 = var1.func_177977_b();
            if (this.field_151501_c.func_180495_p(var2).func_177230_c() == Blocks.field_196658_i) {
               if (this.field_151501_c.func_82736_K().func_82766_b("mobGriefing")) {
                  this.field_151501_c.func_175718_b(2001, var2, Block.func_196246_j(Blocks.field_196658_i.func_176223_P()));
                  this.field_151501_c.func_180501_a(var2, Blocks.field_150346_d.func_176223_P(), 2);
               }

               this.field_151500_b.func_70615_aA();
            }
         }

      }
   }

   static {
      field_179505_b = BlockStateMatcher.func_177638_a(Blocks.field_150349_c);
   }
}
