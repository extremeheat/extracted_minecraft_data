package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityParticleEmitter extends EntityFX {
   private Entity field_174851_a;
   private int field_174852_ax;
   private int field_174850_ay;
   private EnumParticleTypes field_174849_az;

   public EntityParticleEmitter(World var1, Entity var2, EnumParticleTypes var3) {
      super(var1, var2.field_70165_t, var2.func_174813_aQ().field_72338_b + (double)(var2.field_70131_O / 2.0F), var2.field_70161_v, var2.field_70159_w, var2.field_70181_x, var2.field_70179_y);
      this.field_174851_a = var2;
      this.field_174850_ay = 3;
      this.field_174849_az = var3;
      this.func_70071_h_();
   }

   public void func_180434_a(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
   }

   public void func_70071_h_() {
      for(int var1 = 0; var1 < 16; ++var1) {
         double var2 = (double)(this.field_70146_Z.nextFloat() * 2.0F - 1.0F);
         double var4 = (double)(this.field_70146_Z.nextFloat() * 2.0F - 1.0F);
         double var6 = (double)(this.field_70146_Z.nextFloat() * 2.0F - 1.0F);
         if (var2 * var2 + var4 * var4 + var6 * var6 <= 1.0D) {
            double var8 = this.field_174851_a.field_70165_t + var2 * (double)this.field_174851_a.field_70130_N / 4.0D;
            double var10 = this.field_174851_a.func_174813_aQ().field_72338_b + (double)(this.field_174851_a.field_70131_O / 2.0F) + var4 * (double)this.field_174851_a.field_70131_O / 4.0D;
            double var12 = this.field_174851_a.field_70161_v + var6 * (double)this.field_174851_a.field_70130_N / 4.0D;
            this.field_70170_p.func_175682_a(this.field_174849_az, false, var8, var10, var12, var2, var4 + 0.2D, var6);
         }
      }

      ++this.field_174852_ax;
      if (this.field_174852_ax >= this.field_174850_ay) {
         this.func_70106_y();
      }

   }

   public int func_70537_b() {
      return 3;
   }
}
