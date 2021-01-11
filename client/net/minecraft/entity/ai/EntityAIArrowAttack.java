package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.util.MathHelper;

public class EntityAIArrowAttack extends EntityAIBase {
   private final EntityLiving field_75322_b;
   private final IRangedAttackMob field_82641_b;
   private EntityLivingBase field_75323_c;
   private int field_75320_d;
   private double field_75321_e;
   private int field_75318_f;
   private int field_96561_g;
   private int field_75325_h;
   private float field_96562_i;
   private float field_82642_h;

   public EntityAIArrowAttack(IRangedAttackMob var1, double var2, int var4, float var5) {
      this(var1, var2, var4, var4, var5);
   }

   public EntityAIArrowAttack(IRangedAttackMob var1, double var2, int var4, int var5, float var6) {
      super();
      this.field_75320_d = -1;
      if (!(var1 instanceof EntityLivingBase)) {
         throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
      } else {
         this.field_82641_b = var1;
         this.field_75322_b = (EntityLiving)var1;
         this.field_75321_e = var2;
         this.field_96561_g = var4;
         this.field_75325_h = var5;
         this.field_96562_i = var6;
         this.field_82642_h = var6 * var6;
         this.func_75248_a(3);
      }
   }

   public boolean func_75250_a() {
      EntityLivingBase var1 = this.field_75322_b.func_70638_az();
      if (var1 == null) {
         return false;
      } else {
         this.field_75323_c = var1;
         return true;
      }
   }

   public boolean func_75253_b() {
      return this.func_75250_a() || !this.field_75322_b.func_70661_as().func_75500_f();
   }

   public void func_75251_c() {
      this.field_75323_c = null;
      this.field_75318_f = 0;
      this.field_75320_d = -1;
   }

   public void func_75246_d() {
      double var1 = this.field_75322_b.func_70092_e(this.field_75323_c.field_70165_t, this.field_75323_c.func_174813_aQ().field_72338_b, this.field_75323_c.field_70161_v);
      boolean var3 = this.field_75322_b.func_70635_at().func_75522_a(this.field_75323_c);
      if (var3) {
         ++this.field_75318_f;
      } else {
         this.field_75318_f = 0;
      }

      if (var1 <= (double)this.field_82642_h && this.field_75318_f >= 20) {
         this.field_75322_b.func_70661_as().func_75499_g();
      } else {
         this.field_75322_b.func_70661_as().func_75497_a(this.field_75323_c, this.field_75321_e);
      }

      this.field_75322_b.func_70671_ap().func_75651_a(this.field_75323_c, 30.0F, 30.0F);
      float var4;
      if (--this.field_75320_d == 0) {
         if (var1 > (double)this.field_82642_h || !var3) {
            return;
         }

         var4 = MathHelper.func_76133_a(var1) / this.field_96562_i;
         float var5 = MathHelper.func_76131_a(var4, 0.1F, 1.0F);
         this.field_82641_b.func_82196_d(this.field_75323_c, var5);
         this.field_75320_d = MathHelper.func_76141_d(var4 * (float)(this.field_75325_h - this.field_96561_g) + (float)this.field_96561_g);
      } else if (this.field_75320_d < 0) {
         var4 = MathHelper.func_76133_a(var1) / this.field_96562_i;
         this.field_75320_d = MathHelper.func_76141_d(var4 * (float)(this.field_75325_h - this.field_96561_g) + (float)this.field_96561_g);
      }

   }
}
