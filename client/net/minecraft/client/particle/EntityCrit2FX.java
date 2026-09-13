package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityCrit2FX extends EntityFX {
   private Entity field_70557_a;
   private int field_70560_aq;
   private int field_70559_ar;
   private String field_70558_as;

   public EntityCrit2FX(World var1, Entity var2) {
      this(var1, var2, "crit");
   }

   public EntityCrit2FX(World var1, Entity var2, String var3) {
      super(
         var1,
         var2.field_70165_t,
         var2.field_70121_D.field_72338_b + (double)(var2.field_70131_O / 2.0F),
         var2.field_70161_v,
         var2.field_70159_w,
         var2.field_70181_x,
         var2.field_70179_y
      );
      this.field_70557_a = var2;
      this.field_70559_ar = 3;
      this.field_70558_as = var3;
      this.func_70071_h_();
   }

   @Override
   public void func_70539_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
   }

   @Override
   public void func_70071_h_() {
      for(int var1 = 0; var1 < 16; ++var1) {
         double var2 = (double)(this.field_70146_Z.nextFloat() * 2.0F - 1.0F);
         double var4 = (double)(this.field_70146_Z.nextFloat() * 2.0F - 1.0F);
         double var6 = (double)(this.field_70146_Z.nextFloat() * 2.0F - 1.0F);
         if (!(var2 * var2 + var4 * var4 + var6 * var6 > 1.0)) {
            double var8 = this.field_70557_a.field_70165_t + var2 * (double)this.field_70557_a.field_70130_N / 4.0;
            double var10 = this.field_70557_a.field_70121_D.field_72338_b
               + (double)(this.field_70557_a.field_70131_O / 2.0F)
               + var4 * (double)this.field_70557_a.field_70131_O / 4.0;
            double var12 = this.field_70557_a.field_70161_v + var6 * (double)this.field_70557_a.field_70130_N / 4.0;
            this.field_70170_p.func_72869_a(this.field_70558_as, var8, var10, var12, var2, var4 + 0.2, var6);
         }
      }

      ++this.field_70560_aq;
      if (this.field_70560_aq >= this.field_70559_ar) {
         this.func_70106_y();
      }
   }

   @Override
   public int func_70537_b() {
      return 3;
   }
}
