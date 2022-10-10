package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;

public class ModelArmorStandArmor extends ModelBiped {
   public ModelArmorStandArmor() {
      this(0.0F);
   }

   public ModelArmorStandArmor(float var1) {
      this(var1, 64, 32);
   }

   protected ModelArmorStandArmor(float var1, int var2, int var3) {
      super(var1, 0.0F, var2, var3);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      if (var7 instanceof EntityArmorStand) {
         EntityArmorStand var8 = (EntityArmorStand)var7;
         this.field_78116_c.field_78795_f = 0.017453292F * var8.func_175418_s().func_179415_b();
         this.field_78116_c.field_78796_g = 0.017453292F * var8.func_175418_s().func_179416_c();
         this.field_78116_c.field_78808_h = 0.017453292F * var8.func_175418_s().func_179413_d();
         this.field_78116_c.func_78793_a(0.0F, 1.0F, 0.0F);
         this.field_78115_e.field_78795_f = 0.017453292F * var8.func_175408_t().func_179415_b();
         this.field_78115_e.field_78796_g = 0.017453292F * var8.func_175408_t().func_179416_c();
         this.field_78115_e.field_78808_h = 0.017453292F * var8.func_175408_t().func_179413_d();
         this.field_178724_i.field_78795_f = 0.017453292F * var8.func_175404_u().func_179415_b();
         this.field_178724_i.field_78796_g = 0.017453292F * var8.func_175404_u().func_179416_c();
         this.field_178724_i.field_78808_h = 0.017453292F * var8.func_175404_u().func_179413_d();
         this.field_178723_h.field_78795_f = 0.017453292F * var8.func_175411_v().func_179415_b();
         this.field_178723_h.field_78796_g = 0.017453292F * var8.func_175411_v().func_179416_c();
         this.field_178723_h.field_78808_h = 0.017453292F * var8.func_175411_v().func_179413_d();
         this.field_178722_k.field_78795_f = 0.017453292F * var8.func_175403_w().func_179415_b();
         this.field_178722_k.field_78796_g = 0.017453292F * var8.func_175403_w().func_179416_c();
         this.field_178722_k.field_78808_h = 0.017453292F * var8.func_175403_w().func_179413_d();
         this.field_178722_k.func_78793_a(1.9F, 11.0F, 0.0F);
         this.field_178721_j.field_78795_f = 0.017453292F * var8.func_175407_x().func_179415_b();
         this.field_178721_j.field_78796_g = 0.017453292F * var8.func_175407_x().func_179416_c();
         this.field_178721_j.field_78808_h = 0.017453292F * var8.func_175407_x().func_179413_d();
         this.field_178721_j.func_78793_a(-1.9F, 11.0F, 0.0F);
         func_178685_a(this.field_78116_c, this.field_178720_f);
      }
   }
}
