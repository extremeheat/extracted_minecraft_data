package net.minecraft.client.audio;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class UnderwaterAmbientSounds {
   public static class UnderWaterSound extends MovingSound {
      private final EntityPlayerSP field_204203_n;
      private int field_204204_o;

      public UnderWaterSound(EntityPlayerSP var1) {
         super(SoundEvents.field_204323_b, SoundCategory.AMBIENT);
         this.field_204203_n = var1;
         this.field_147659_g = true;
         this.field_147665_h = 0;
         this.field_147662_b = 1.0F;
         this.field_204201_l = true;
      }

      public void func_73660_a() {
         if (!this.field_204203_n.field_70128_L && this.field_204204_o >= 0) {
            this.field_147660_d = (float)this.field_204203_n.field_70165_t;
            this.field_147661_e = (float)this.field_204203_n.field_70163_u;
            this.field_147658_f = (float)this.field_204203_n.field_70161_v;
            if (this.field_204203_n.func_204231_K()) {
               ++this.field_204204_o;
            } else {
               this.field_204204_o -= 2;
            }

            this.field_204204_o = Math.min(this.field_204204_o, 40);
            this.field_147662_b = Math.max(0.0F, Math.min((float)this.field_204204_o / 40.0F, 1.0F));
         } else {
            this.field_147668_j = true;
         }
      }
   }

   public static class SubSound extends MovingSound {
      private final EntityPlayerSP field_204202_n;

      protected SubSound(EntityPlayerSP var1, SoundEvent var2) {
         super(var2, SoundCategory.AMBIENT);
         this.field_204202_n = var1;
         this.field_147659_g = false;
         this.field_147665_h = 0;
         this.field_147662_b = 1.0F;
         this.field_204201_l = true;
      }

      public void func_73660_a() {
         if (!this.field_204202_n.field_70128_L && this.field_204202_n.func_204231_K()) {
            this.field_147660_d = (float)this.field_204202_n.field_70165_t;
            this.field_147661_e = (float)this.field_204202_n.field_70163_u;
            this.field_147658_f = (float)this.field_204202_n.field_70161_v;
         } else {
            this.field_147668_j = true;
         }
      }
   }
}
