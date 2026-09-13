package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderZombie extends RenderBiped {
   private static final ResourceLocation field_110866_o = new ResourceLocation("textures/entity/zombie_pigman.png");
   private static final ResourceLocation field_110865_p = new ResourceLocation("textures/entity/zombie/zombie.png");
   private static final ResourceLocation field_110864_q = new ResourceLocation("textures/entity/zombie/zombie_villager.png");
   private ModelBiped field_82434_o;
   private ModelZombieVillager field_82432_p;
   protected ModelBiped field_82437_k;
   protected ModelBiped field_82435_l;
   protected ModelBiped field_82436_m;
   protected ModelBiped field_82433_n;
   private int field_82431_q = 1;

   public RenderZombie() {
      super(new ModelZombie(), 0.5F, 1.0F);
      this.field_82434_o = this.field_77071_a;
      this.field_82432_p = new ModelZombieVillager();
   }

   @Override
   protected void func_82421_b() {
      this.field_82423_g = new ModelZombie(1.0F, true);
      this.field_82425_h = new ModelZombie(0.5F, true);
      this.field_82437_k = this.field_82423_g;
      this.field_82435_l = this.field_82425_h;
      this.field_82436_m = new ModelZombieVillager(1.0F, 0.0F, true);
      this.field_82433_n = new ModelZombieVillager(0.5F, 0.0F, true);
   }

   protected int func_77032_a(EntityZombie var1, int var2, float var3) {
      this.func_82427_a(var1);
      return super.func_77032_a((EntityLiving)var1, var2, var3);
   }

   public void func_76986_a(EntityZombie var1, double var2, double var4, double var6, float var8, float var9) {
      this.func_82427_a(var1);
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityZombie var1) {
      if (var1 instanceof EntityPigZombie) {
         return field_110866_o;
      } else {
         return var1.func_82231_m() ? field_110864_q : field_110865_p;
      }
   }

   protected void func_77029_c(EntityZombie var1, float var2) {
      this.func_82427_a(var1);
      super.func_77029_c((EntityLiving)var1, var2);
   }

   private void func_82427_a(EntityZombie var1) {
      if (var1.func_82231_m()) {
         if (this.field_82431_q != this.field_82432_p.func_82897_a()) {
            this.field_82432_p = new ModelZombieVillager();
            this.field_82431_q = this.field_82432_p.func_82897_a();
            this.field_82436_m = new ModelZombieVillager(1.0F, 0.0F, true);
            this.field_82433_n = new ModelZombieVillager(0.5F, 0.0F, true);
         }

         this.field_77045_g = this.field_82432_p;
         this.field_82423_g = this.field_82436_m;
         this.field_82425_h = this.field_82433_n;
      } else {
         this.field_77045_g = this.field_82434_o;
         this.field_82423_g = this.field_82437_k;
         this.field_82425_h = this.field_82435_l;
      }

      this.field_77071_a = (ModelBiped)this.field_77045_g;
   }

   protected void func_77043_a(EntityZombie var1, float var2, float var3, float var4) {
      if (var1.func_82230_o()) {
         var3 += (float)(Math.cos((double)var1.field_70173_aa * 3.25) * 3.141592653589793 * 0.25);
      }

      super.func_77043_a(var1, var2, var3, var4);
   }
}
