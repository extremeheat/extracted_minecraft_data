package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAITempt extends EntityAIBase {
   private final EntityCreature field_75284_a;
   private final double field_75282_b;
   private double field_75283_c;
   private double field_75280_d;
   private double field_75281_e;
   private double field_75278_f;
   private double field_75279_g;
   private EntityPlayer field_75289_h;
   private int field_75290_i;
   private boolean field_75287_j;
   private final Ingredient field_151484_k;
   private final boolean field_75285_l;

   public EntityAITempt(EntityCreature var1, double var2, Ingredient var4, boolean var5) {
      this(var1, var2, var5, var4);
   }

   public EntityAITempt(EntityCreature var1, double var2, boolean var4, Ingredient var5) {
      super();
      this.field_75284_a = var1;
      this.field_75282_b = var2;
      this.field_151484_k = var5;
      this.field_75285_l = var4;
      this.func_75248_a(3);
      if (!(var1.func_70661_as() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
      }
   }

   public boolean func_75250_a() {
      if (this.field_75290_i > 0) {
         --this.field_75290_i;
         return false;
      } else {
         this.field_75289_h = this.field_75284_a.field_70170_p.func_72890_a(this.field_75284_a, 10.0D);
         if (this.field_75289_h == null) {
            return false;
         } else {
            return this.func_188508_a(this.field_75289_h.func_184614_ca()) || this.func_188508_a(this.field_75289_h.func_184592_cb());
         }
      }
   }

   protected boolean func_188508_a(ItemStack var1) {
      return this.field_151484_k.test(var1);
   }

   public boolean func_75253_b() {
      if (this.field_75285_l) {
         if (this.field_75284_a.func_70068_e(this.field_75289_h) < 36.0D) {
            if (this.field_75289_h.func_70092_e(this.field_75283_c, this.field_75280_d, this.field_75281_e) > 0.010000000000000002D) {
               return false;
            }

            if (Math.abs((double)this.field_75289_h.field_70125_A - this.field_75278_f) > 5.0D || Math.abs((double)this.field_75289_h.field_70177_z - this.field_75279_g) > 5.0D) {
               return false;
            }
         } else {
            this.field_75283_c = this.field_75289_h.field_70165_t;
            this.field_75280_d = this.field_75289_h.field_70163_u;
            this.field_75281_e = this.field_75289_h.field_70161_v;
         }

         this.field_75278_f = (double)this.field_75289_h.field_70125_A;
         this.field_75279_g = (double)this.field_75289_h.field_70177_z;
      }

      return this.func_75250_a();
   }

   public void func_75249_e() {
      this.field_75283_c = this.field_75289_h.field_70165_t;
      this.field_75280_d = this.field_75289_h.field_70163_u;
      this.field_75281_e = this.field_75289_h.field_70161_v;
      this.field_75287_j = true;
   }

   public void func_75251_c() {
      this.field_75289_h = null;
      this.field_75284_a.func_70661_as().func_75499_g();
      this.field_75290_i = 100;
      this.field_75287_j = false;
   }

   public void func_75246_d() {
      this.field_75284_a.func_70671_ap().func_75651_a(this.field_75289_h, (float)(this.field_75284_a.func_184649_cE() + 20), (float)this.field_75284_a.func_70646_bf());
      if (this.field_75284_a.func_70068_e(this.field_75289_h) < 6.25D) {
         this.field_75284_a.func_70661_as().func_75499_g();
      } else {
         this.field_75284_a.func_70661_as().func_75497_a(this.field_75289_h, this.field_75282_b);
      }

   }

   public boolean func_75277_f() {
      return this.field_75287_j;
   }
}
