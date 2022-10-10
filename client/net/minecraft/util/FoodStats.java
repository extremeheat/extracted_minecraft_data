package net.minecraft.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;

public class FoodStats {
   private int field_75127_a = 20;
   private float field_75125_b = 5.0F;
   private float field_75126_c;
   private int field_75123_d;
   private int field_75124_e = 20;

   public FoodStats() {
      super();
   }

   public void func_75122_a(int var1, float var2) {
      this.field_75127_a = Math.min(var1 + this.field_75127_a, 20);
      this.field_75125_b = Math.min(this.field_75125_b + (float)var1 * var2 * 2.0F, (float)this.field_75127_a);
   }

   public void func_151686_a(ItemFood var1, ItemStack var2) {
      this.func_75122_a(var1.func_150905_g(var2), var1.func_150906_h(var2));
   }

   public void func_75118_a(EntityPlayer var1) {
      EnumDifficulty var2 = var1.field_70170_p.func_175659_aa();
      this.field_75124_e = this.field_75127_a;
      if (this.field_75126_c > 4.0F) {
         this.field_75126_c -= 4.0F;
         if (this.field_75125_b > 0.0F) {
            this.field_75125_b = Math.max(this.field_75125_b - 1.0F, 0.0F);
         } else if (var2 != EnumDifficulty.PEACEFUL) {
            this.field_75127_a = Math.max(this.field_75127_a - 1, 0);
         }
      }

      boolean var3 = var1.field_70170_p.func_82736_K().func_82766_b("naturalRegeneration");
      if (var3 && this.field_75125_b > 0.0F && var1.func_70996_bM() && this.field_75127_a >= 20) {
         ++this.field_75123_d;
         if (this.field_75123_d >= 10) {
            float var4 = Math.min(this.field_75125_b, 6.0F);
            var1.func_70691_i(var4 / 6.0F);
            this.func_75113_a(var4);
            this.field_75123_d = 0;
         }
      } else if (var3 && this.field_75127_a >= 18 && var1.func_70996_bM()) {
         ++this.field_75123_d;
         if (this.field_75123_d >= 80) {
            var1.func_70691_i(1.0F);
            this.func_75113_a(6.0F);
            this.field_75123_d = 0;
         }
      } else if (this.field_75127_a <= 0) {
         ++this.field_75123_d;
         if (this.field_75123_d >= 80) {
            if (var1.func_110143_aJ() > 10.0F || var2 == EnumDifficulty.HARD || var1.func_110143_aJ() > 1.0F && var2 == EnumDifficulty.NORMAL) {
               var1.func_70097_a(DamageSource.field_76366_f, 1.0F);
            }

            this.field_75123_d = 0;
         }
      } else {
         this.field_75123_d = 0;
      }

   }

   public void func_75112_a(NBTTagCompound var1) {
      if (var1.func_150297_b("foodLevel", 99)) {
         this.field_75127_a = var1.func_74762_e("foodLevel");
         this.field_75123_d = var1.func_74762_e("foodTickTimer");
         this.field_75125_b = var1.func_74760_g("foodSaturationLevel");
         this.field_75126_c = var1.func_74760_g("foodExhaustionLevel");
      }

   }

   public void func_75117_b(NBTTagCompound var1) {
      var1.func_74768_a("foodLevel", this.field_75127_a);
      var1.func_74768_a("foodTickTimer", this.field_75123_d);
      var1.func_74776_a("foodSaturationLevel", this.field_75125_b);
      var1.func_74776_a("foodExhaustionLevel", this.field_75126_c);
   }

   public int func_75116_a() {
      return this.field_75127_a;
   }

   public boolean func_75121_c() {
      return this.field_75127_a < 20;
   }

   public void func_75113_a(float var1) {
      this.field_75126_c = Math.min(this.field_75126_c + var1, 40.0F);
   }

   public float func_75115_e() {
      return this.field_75125_b;
   }

   public void func_75114_a(int var1) {
      this.field_75127_a = var1;
   }

   public void func_75119_b(float var1) {
      this.field_75125_b = var1;
   }
}
