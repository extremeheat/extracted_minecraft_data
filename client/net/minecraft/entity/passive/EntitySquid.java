package net.minecraft.entity.passive;

import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySquid extends EntityWaterMob {
   public float field_70861_d;
   public float field_70862_e;
   public float field_70859_f;
   public float field_70860_g;
   public float field_70867_h;
   public float field_70868_i;
   public float field_70866_j;
   public float field_70865_by;
   private float field_70863_bz;
   private float field_70864_bA;
   private float field_70871_bB;
   private float field_70872_bC;
   private float field_70869_bD;
   private float field_70870_bE;

   public EntitySquid(World var1) {
      super(var1);
      this.func_70105_a(0.95F, 0.95F);
      this.field_70864_bA = 1.0F / (this.field_70146_Z.nextFloat() + 1.0F) * 0.2F;
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(10.0);
   }

   @Override
   protected String func_70639_aQ() {
      return null;
   }

   @Override
   protected String func_70621_aR() {
      return null;
   }

   @Override
   protected String func_70673_aS() {
      return null;
   }

   @Override
   protected float func_70599_aP() {
      return 0.4F;
   }

   @Override
   protected Item func_146068_u() {
      return Item.func_150899_d(0);
   }

   @Override
   protected boolean func_70041_e_() {
      return false;
   }

   @Override
   protected void func_70628_a(boolean var1, int var2) {
      int var3 = this.field_70146_Z.nextInt(3 + var2) + 1;

      for(int var4 = 0; var4 < var3; ++var4) {
         this.func_70099_a(new ItemStack(Items.field_151100_aR, 1, 0), 0.0F);
      }
   }

   @Override
   public boolean func_70090_H() {
      return this.field_70170_p.func_72918_a(this.field_70121_D.func_72314_b(0.0, -0.6000000238418579, 0.0), Material.field_151586_h, this);
   }

   @Override
   public void func_70636_d() {
      super.func_70636_d();
      this.field_70862_e = this.field_70861_d;
      this.field_70860_g = this.field_70859_f;
      this.field_70868_i = this.field_70867_h;
      this.field_70865_by = this.field_70866_j;
      this.field_70867_h += this.field_70864_bA;
      if (this.field_70867_h > 6.2831855F) {
         this.field_70867_h -= 6.2831855F;
         if (this.field_70146_Z.nextInt(10) == 0) {
            this.field_70864_bA = 1.0F / (this.field_70146_Z.nextFloat() + 1.0F) * 0.2F;
         }
      }

      if (this.func_70090_H()) {
         if (this.field_70867_h < 3.1415927F) {
            float var1 = this.field_70867_h / 3.1415927F;
            this.field_70866_j = MathHelper.func_76126_a(var1 * var1 * 3.1415927F) * 3.1415927F * 0.25F;
            if ((double)var1 > 0.75) {
               this.field_70863_bz = 1.0F;
               this.field_70871_bB = 1.0F;
            } else {
               this.field_70871_bB *= 0.8F;
            }
         } else {
            this.field_70866_j = 0.0F;
            this.field_70863_bz *= 0.9F;
            this.field_70871_bB *= 0.99F;
         }

         if (!this.field_70170_p.field_72995_K) {
            this.field_70159_w = (double)(this.field_70872_bC * this.field_70863_bz);
            this.field_70181_x = (double)(this.field_70869_bD * this.field_70863_bz);
            this.field_70179_y = (double)(this.field_70870_bE * this.field_70863_bz);
         }

         float var2 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         this.field_70761_aq += (-((float)Math.atan2(this.field_70159_w, this.field_70179_y)) * 180.0F / 3.1415927F - this.field_70761_aq) * 0.1F;
         this.field_70177_z = this.field_70761_aq;
         this.field_70859_f += 3.1415927F * this.field_70871_bB * 1.5F;
         this.field_70861_d += (-((float)Math.atan2((double)var2, this.field_70181_x)) * 180.0F / 3.1415927F - this.field_70861_d) * 0.1F;
      } else {
         this.field_70866_j = MathHelper.func_76135_e(MathHelper.func_76126_a(this.field_70867_h)) * 3.1415927F * 0.25F;
         if (!this.field_70170_p.field_72995_K) {
            this.field_70159_w = 0.0;
            this.field_70181_x -= 0.08;
            this.field_70181_x *= 0.9800000190734863;
            this.field_70179_y = 0.0;
         }

         this.field_70861_d = (float)((double)this.field_70861_d + (double)(-90.0F - this.field_70861_d) * 0.02);
      }
   }

   @Override
   public void func_70612_e(float var1, float var2) {
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
   }

   @Override
   protected void func_70626_be() {
      ++this.field_70708_bq;
      if (this.field_70708_bq > 100) {
         this.field_70872_bC = this.field_70869_bD = this.field_70870_bE = 0.0F;
      } else if (this.field_70146_Z.nextInt(50) == 0
         || !this.field_70171_ac
         || this.field_70872_bC == 0.0F && this.field_70869_bD == 0.0F && this.field_70870_bE == 0.0F) {
         float var1 = this.field_70146_Z.nextFloat() * 3.1415927F * 2.0F;
         this.field_70872_bC = MathHelper.func_76134_b(var1) * 0.2F;
         this.field_70869_bD = -0.1F + this.field_70146_Z.nextFloat() * 0.2F;
         this.field_70870_bE = MathHelper.func_76126_a(var1) * 0.2F;
      }

      this.func_70623_bb();
   }

   @Override
   public boolean func_70601_bi() {
      return this.field_70163_u > 45.0 && this.field_70163_u < 63.0 && super.func_70601_bi();
   }
}
