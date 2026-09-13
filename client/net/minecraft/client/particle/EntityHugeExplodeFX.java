package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EntityHugeExplodeFX extends EntityFX {
   private int field_70579_a;
   private int field_70580_aq = 8;

   public EntityHugeExplodeFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
   }

   @Override
   public void func_70539_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
   }

   @Override
   public void func_70071_h_() {
      for(int var1 = 0; var1 < 6; ++var1) {
         double var2 = this.field_70165_t + (this.field_70146_Z.nextDouble() - this.field_70146_Z.nextDouble()) * 4.0;
         double var4 = this.field_70163_u + (this.field_70146_Z.nextDouble() - this.field_70146_Z.nextDouble()) * 4.0;
         double var6 = this.field_70161_v + (this.field_70146_Z.nextDouble() - this.field_70146_Z.nextDouble()) * 4.0;
         this.field_70170_p.func_72869_a("largeexplode", var2, var4, var6, (double)((float)this.field_70579_a / (float)this.field_70580_aq), 0.0, 0.0);
      }

      ++this.field_70579_a;
      if (this.field_70579_a == this.field_70580_aq) {
         this.func_70106_y();
      }
   }

   @Override
   public int func_70537_b() {
      return 1;
   }
}
