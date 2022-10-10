package net.minecraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;

public class TileEntityEnderChest extends TileEntity implements IChestLid, ITickable {
   public float field_145972_a;
   public float field_145975_i;
   public int field_145973_j;
   private int field_145974_k;

   public TileEntityEnderChest() {
      super(TileEntityType.field_200974_e);
   }

   public void func_73660_a() {
      if (++this.field_145974_k % 20 * 4 == 0) {
         this.field_145850_b.func_175641_c(this.field_174879_c, Blocks.field_150477_bB, 1, this.field_145973_j);
      }

      this.field_145975_i = this.field_145972_a;
      int var1 = this.field_174879_c.func_177958_n();
      int var2 = this.field_174879_c.func_177956_o();
      int var3 = this.field_174879_c.func_177952_p();
      float var4 = 0.1F;
      double var7;
      if (this.field_145973_j > 0 && this.field_145972_a == 0.0F) {
         double var5 = (double)var1 + 0.5D;
         var7 = (double)var3 + 0.5D;
         this.field_145850_b.func_184148_a((EntityPlayer)null, var5, (double)var2 + 0.5D, var7, SoundEvents.field_187520_aJ, SoundCategory.BLOCKS, 0.5F, this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F);
      }

      if (this.field_145973_j == 0 && this.field_145972_a > 0.0F || this.field_145973_j > 0 && this.field_145972_a < 1.0F) {
         float var11 = this.field_145972_a;
         if (this.field_145973_j > 0) {
            this.field_145972_a += 0.1F;
         } else {
            this.field_145972_a -= 0.1F;
         }

         if (this.field_145972_a > 1.0F) {
            this.field_145972_a = 1.0F;
         }

         float var6 = 0.5F;
         if (this.field_145972_a < 0.5F && var11 >= 0.5F) {
            var7 = (double)var1 + 0.5D;
            double var9 = (double)var3 + 0.5D;
            this.field_145850_b.func_184148_a((EntityPlayer)null, var7, (double)var2 + 0.5D, var9, SoundEvents.field_187519_aI, SoundCategory.BLOCKS, 0.5F, this.field_145850_b.field_73012_v.nextFloat() * 0.1F + 0.9F);
         }

         if (this.field_145972_a < 0.0F) {
            this.field_145972_a = 0.0F;
         }
      }

   }

   public boolean func_145842_c(int var1, int var2) {
      if (var1 == 1) {
         this.field_145973_j = var2;
         return true;
      } else {
         return super.func_145842_c(var1, var2);
      }
   }

   public void func_145843_s() {
      this.func_145836_u();
      super.func_145843_s();
   }

   public void func_145969_a() {
      ++this.field_145973_j;
      this.field_145850_b.func_175641_c(this.field_174879_c, Blocks.field_150477_bB, 1, this.field_145973_j);
   }

   public void func_145970_b() {
      --this.field_145973_j;
      this.field_145850_b.func_175641_c(this.field_174879_c, Blocks.field_150477_bB, 1, this.field_145973_j);
   }

   public boolean func_145971_a(EntityPlayer var1) {
      if (this.field_145850_b.func_175625_s(this.field_174879_c) != this) {
         return false;
      } else {
         return var1.func_70092_e((double)this.field_174879_c.func_177958_n() + 0.5D, (double)this.field_174879_c.func_177956_o() + 0.5D, (double)this.field_174879_c.func_177952_p() + 0.5D) <= 64.0D;
      }
   }

   public float func_195480_a(float var1) {
      return this.field_145975_i + (this.field_145972_a - this.field_145975_i) * var1;
   }
}
