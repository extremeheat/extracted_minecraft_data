package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityHugeExplodeFX extends EntityFX {
   private int field_70579_a;
   private int field_70580_aq = 8;

   protected EntityHugeExplodeFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
   }

   public void func_180434_a(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
   }

   public void func_70071_h_() {
      for(int var1 = 0; var1 < 6; ++var1) {
         double var2 = this.field_70165_t + (this.field_70146_Z.nextDouble() - this.field_70146_Z.nextDouble()) * 4.0D;
         double var4 = this.field_70163_u + (this.field_70146_Z.nextDouble() - this.field_70146_Z.nextDouble()) * 4.0D;
         double var6 = this.field_70161_v + (this.field_70146_Z.nextDouble() - this.field_70146_Z.nextDouble()) * 4.0D;
         this.field_70170_p.func_175688_a(EnumParticleTypes.EXPLOSION_LARGE, var2, var4, var6, (double)((float)this.field_70579_a / (float)this.field_70580_aq), 0.0D, 0.0D);
      }

      ++this.field_70579_a;
      if (this.field_70579_a == this.field_70580_aq) {
         this.func_70106_y();
      }

   }

   public int func_70537_b() {
      return 1;
   }

   public static class Factory implements IParticleFactory {
      public Factory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityHugeExplodeFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
