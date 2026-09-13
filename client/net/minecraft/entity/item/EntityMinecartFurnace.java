package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMinecartFurnace extends EntityMinecart {
   private int field_94110_c;
   public double field_94111_a;
   public double field_94109_b;

   public EntityMinecartFurnace(World var1) {
      super(var1);
   }

   public EntityMinecartFurnace(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public int func_94087_l() {
      return 2;
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, new Byte((byte)0));
   }

   @Override
   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_94110_c > 0) {
         --this.field_94110_c;
      }

      if (this.field_94110_c <= 0) {
         this.field_94111_a = this.field_94109_b = 0.0;
      }

      this.func_94107_f(this.field_94110_c > 0);
      if (this.func_94108_c() && this.field_70146_Z.nextInt(4) == 0) {
         this.field_70170_p.func_72869_a("largesmoke", this.field_70165_t, this.field_70163_u + 0.8, this.field_70161_v, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public void func_94095_a(DamageSource var1) {
      super.func_94095_a(var1);
      if (!var1.func_94541_c()) {
         this.func_70099_a(new ItemStack(Blocks.field_150460_al, 1), 0.0F);
      }
   }

   @Override
   protected void func_145821_a(int var1, int var2, int var3, double var4, double var6, Block var8, int var9) {
      super.func_145821_a(var1, var2, var3, var4, var6, var8, var9);
      double var10 = this.field_94111_a * this.field_94111_a + this.field_94109_b * this.field_94109_b;
      if (var10 > 1.0E-4 && this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y > 0.001) {
         var10 = (double)MathHelper.func_76133_a(var10);
         this.field_94111_a /= var10;
         this.field_94109_b /= var10;
         if (this.field_94111_a * this.field_70159_w + this.field_94109_b * this.field_70179_y < 0.0) {
            this.field_94111_a = 0.0;
            this.field_94109_b = 0.0;
         } else {
            this.field_94111_a = this.field_70159_w;
            this.field_94109_b = this.field_70179_y;
         }
      }
   }

   @Override
   protected void func_94101_h() {
      double var1 = this.field_94111_a * this.field_94111_a + this.field_94109_b * this.field_94109_b;
      if (var1 > 1.0E-4) {
         var1 = (double)MathHelper.func_76133_a(var1);
         this.field_94111_a /= var1;
         this.field_94109_b /= var1;
         double var3 = 0.05;
         this.field_70159_w *= 0.800000011920929;
         this.field_70181_x *= 0.0;
         this.field_70179_y *= 0.800000011920929;
         this.field_70159_w += this.field_94111_a * var3;
         this.field_70179_y += this.field_94109_b * var3;
      } else {
         this.field_70159_w *= 0.9800000190734863;
         this.field_70181_x *= 0.0;
         this.field_70179_y *= 0.9800000190734863;
      }

      super.func_94101_h();
   }

   @Override
   public boolean func_130002_c(EntityPlayer var1) {
      ItemStack var2 = var1.field_71071_by.func_70448_g();
      if (var2 != null && var2.func_77973_b() == Items.field_151044_h) {
         if (!var1.field_71075_bZ.field_75098_d && --var2.field_77994_a == 0) {
            var1.field_71071_by.func_70299_a(var1.field_71071_by.field_70461_c, null);
         }

         this.field_94110_c += 3600;
      }

      this.field_94111_a = this.field_70165_t - var1.field_70165_t;
      this.field_94109_b = this.field_70161_v - var1.field_70161_v;
      return true;
   }

   @Override
   protected void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74780_a("PushX", this.field_94111_a);
      var1.func_74780_a("PushZ", this.field_94109_b);
      var1.func_74777_a("Fuel", (short)this.field_94110_c);
   }

   @Override
   protected void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_94111_a = var1.func_74769_h("PushX");
      this.field_94109_b = var1.func_74769_h("PushZ");
      this.field_94110_c = var1.func_74765_d("Fuel");
   }

   protected boolean func_94108_c() {
      return (this.field_70180_af.func_75683_a(16) & 1) != 0;
   }

   protected void func_94107_f(boolean var1) {
      if (var1) {
         this.field_70180_af.func_75692_b(16, (byte)(this.field_70180_af.func_75683_a(16) | 1));
      } else {
         this.field_70180_af.func_75692_b(16, (byte)(this.field_70180_af.func_75683_a(16) & -2));
      }
   }

   @Override
   public Block func_145817_o() {
      return Blocks.field_150470_am;
   }

   @Override
   public int func_94097_p() {
      return 2;
   }
}
