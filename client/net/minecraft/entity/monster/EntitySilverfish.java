package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class EntitySilverfish extends EntityMob {
   private int field_70843_d;

   public EntitySilverfish(World var1) {
      super(var1);
      this.func_70105_a(0.3F, 0.7F);
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(8.0);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.6000000238418579);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(1.0);
   }

   @Override
   protected boolean func_70041_e_() {
      return false;
   }

   @Override
   protected Entity func_70782_k() {
      double var1 = 8.0;
      return this.field_70170_p.func_72856_b(this, var1);
   }

   @Override
   protected String func_70639_aQ() {
      return "mob.silverfish.say";
   }

   @Override
   protected String func_70621_aR() {
      return "mob.silverfish.hit";
   }

   @Override
   protected String func_70673_aS() {
      return "mob.silverfish.kill";
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_85032_ar()) {
         return false;
      } else {
         if (this.field_70843_d <= 0 && (var1 instanceof EntityDamageSource || var1 == DamageSource.field_76376_m)) {
            this.field_70843_d = 20;
         }

         return super.func_70097_a(var1, var2);
      }
   }

   @Override
   protected void func_70785_a(Entity var1, float var2) {
      if (this.field_70724_aR <= 0
         && var2 < 1.2F
         && var1.field_70121_D.field_72337_e > this.field_70121_D.field_72338_b
         && var1.field_70121_D.field_72338_b < this.field_70121_D.field_72337_e) {
         this.field_70724_aR = 20;
         this.func_70652_k(var1);
      }
   }

   @Override
   protected void func_145780_a(int var1, int var2, int var3, Block var4) {
      this.func_85030_a("mob.silverfish.step", 0.15F, 1.0F);
   }

   @Override
   protected Item func_146068_u() {
      return Item.func_150899_d(0);
   }

   @Override
   public void func_70071_h_() {
      this.field_70761_aq = this.field_70177_z;
      super.func_70071_h_();
   }

   @Override
   protected void func_70626_be() {
      super.func_70626_be();
      if (!this.field_70170_p.field_72995_K) {
         if (this.field_70843_d > 0) {
            --this.field_70843_d;
            if (this.field_70843_d == 0) {
               int var1 = MathHelper.func_76128_c(this.field_70165_t);
               int var2 = MathHelper.func_76128_c(this.field_70163_u);
               int var3 = MathHelper.func_76128_c(this.field_70161_v);
               boolean var4 = false;

               for(int var5 = 0; !var4 && var5 <= 5 && var5 >= -5; var5 = var5 <= 0 ? 1 - var5 : 0 - var5) {
                  for(int var6 = 0; !var4 && var6 <= 10 && var6 >= -10; var6 = var6 <= 0 ? 1 - var6 : 0 - var6) {
                     for(int var7 = 0; !var4 && var7 <= 10 && var7 >= -10; var7 = var7 <= 0 ? 1 - var7 : 0 - var7) {
                        if (this.field_70170_p.func_147439_a(var1 + var6, var2 + var5, var3 + var7) == Blocks.field_150418_aU) {
                           if (!this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
                              int var8 = this.field_70170_p.func_72805_g(var1 + var6, var2 + var5, var3 + var7);
                              ImmutablePair var9 = BlockSilverfish.func_150197_b(var8);
                              this.field_70170_p.func_147465_d(var1 + var6, var2 + var5, var3 + var7, (Block)var9.getLeft(), var9.getRight(), 3);
                           } else {
                              this.field_70170_p.func_147480_a(var1 + var6, var2 + var5, var3 + var7, false);
                           }

                           Blocks.field_150418_aU.func_149664_b(this.field_70170_p, var1 + var6, var2 + var5, var3 + var7, 0);
                           if (this.field_70146_Z.nextBoolean()) {
                              var4 = true;
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }

         if (this.field_70789_a == null && !this.func_70781_l()) {
            int var10 = MathHelper.func_76128_c(this.field_70165_t);
            int var11 = MathHelper.func_76128_c(this.field_70163_u + 0.5);
            int var12 = MathHelper.func_76128_c(this.field_70161_v);
            int var13 = this.field_70146_Z.nextInt(6);
            Block var14 = this.field_70170_p
               .func_147439_a(var10 + Facing.field_71586_b[var13], var11 + Facing.field_71587_c[var13], var12 + Facing.field_71585_d[var13]);
            int var15 = this.field_70170_p
               .func_72805_g(var10 + Facing.field_71586_b[var13], var11 + Facing.field_71587_c[var13], var12 + Facing.field_71585_d[var13]);
            if (BlockSilverfish.func_150196_a(var14)) {
               this.field_70170_p
                  .func_147465_d(
                     var10 + Facing.field_71586_b[var13],
                     var11 + Facing.field_71587_c[var13],
                     var12 + Facing.field_71585_d[var13],
                     Blocks.field_150418_aU,
                     BlockSilverfish.func_150195_a(var14, var15),
                     3
                  );
               this.func_70656_aK();
               this.func_70106_y();
            } else {
               this.func_70779_j();
            }
         } else if (this.field_70789_a != null && !this.func_70781_l()) {
            this.field_70789_a = null;
         }
      }
   }

   @Override
   public float func_70783_a(int var1, int var2, int var3) {
      return this.field_70170_p.func_147439_a(var1, var2 - 1, var3) == Blocks.field_150348_b ? 10.0F : super.func_70783_a(var1, var2, var3);
   }

   @Override
   protected boolean func_70814_o() {
      return true;
   }

   @Override
   public boolean func_70601_bi() {
      if (super.func_70601_bi()) {
         EntityPlayer var1 = this.field_70170_p.func_72890_a(this, 5.0);
         return var1 == null;
      } else {
         return false;
      }
   }

   @Override
   public EnumCreatureAttribute func_70668_bt() {
      return EnumCreatureAttribute.ARTHROPOD;
   }
}
