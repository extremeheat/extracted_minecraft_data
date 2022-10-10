package net.minecraft.client.audio;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

public class MovingSoundMinecart extends MovingSound {
   private final EntityMinecart field_147670_k;
   private float field_147669_l = 0.0F;

   public MovingSoundMinecart(EntityMinecart var1) {
      super(SoundEvents.field_187782_ds, SoundCategory.NEUTRAL);
      this.field_147670_k = var1;
      this.field_147659_g = true;
      this.field_147665_h = 0;
      this.field_147662_b = 0.0F;
      this.field_147660_d = (float)var1.field_70165_t;
      this.field_147661_e = (float)var1.field_70163_u;
      this.field_147658_f = (float)var1.field_70161_v;
   }

   public boolean func_211503_n() {
      return true;
   }

   public void func_73660_a() {
      if (this.field_147670_k.field_70128_L) {
         this.field_147668_j = true;
      } else {
         this.field_147660_d = (float)this.field_147670_k.field_70165_t;
         this.field_147661_e = (float)this.field_147670_k.field_70163_u;
         this.field_147658_f = (float)this.field_147670_k.field_70161_v;
         float var1 = MathHelper.func_76133_a(this.field_147670_k.field_70159_w * this.field_147670_k.field_70159_w + this.field_147670_k.field_70179_y * this.field_147670_k.field_70179_y);
         if ((double)var1 >= 0.01D) {
            this.field_147669_l = MathHelper.func_76131_a(this.field_147669_l + 0.0025F, 0.0F, 1.0F);
            this.field_147662_b = 0.0F + MathHelper.func_76131_a(var1, 0.0F, 0.5F) * 0.7F;
         } else {
            this.field_147669_l = 0.0F;
            this.field_147662_b = 0.0F;
         }

      }
   }
}
