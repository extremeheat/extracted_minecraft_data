package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemBoat extends Item {
   public ItemBoat() {
      super();
      this.field_77777_bU = 1;
      this.func_77637_a(CreativeTabs.field_78029_e);
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      float var4 = 1.0F;
      float var5 = var3.field_70127_C + (var3.field_70125_A - var3.field_70127_C) * var4;
      float var6 = var3.field_70126_B + (var3.field_70177_z - var3.field_70126_B) * var4;
      double var7 = var3.field_70169_q + (var3.field_70165_t - var3.field_70169_q) * (double)var4;
      double var9 = var3.field_70167_r + (var3.field_70163_u - var3.field_70167_r) * (double)var4 + (double)var3.func_70047_e();
      double var11 = var3.field_70166_s + (var3.field_70161_v - var3.field_70166_s) * (double)var4;
      Vec3 var13 = new Vec3(var7, var9, var11);
      float var14 = MathHelper.func_76134_b(-var6 * 0.017453292F - 3.1415927F);
      float var15 = MathHelper.func_76126_a(-var6 * 0.017453292F - 3.1415927F);
      float var16 = -MathHelper.func_76134_b(-var5 * 0.017453292F);
      float var17 = MathHelper.func_76126_a(-var5 * 0.017453292F);
      float var18 = var15 * var16;
      float var20 = var14 * var16;
      double var21 = 5.0D;
      Vec3 var23 = var13.func_72441_c((double)var18 * var21, (double)var17 * var21, (double)var20 * var21);
      MovingObjectPosition var24 = var2.func_72901_a(var13, var23, true);
      if (var24 == null) {
         return var1;
      } else {
         Vec3 var25 = var3.func_70676_i(var4);
         boolean var26 = false;
         float var27 = 1.0F;
         List var28 = var2.func_72839_b(var3, var3.func_174813_aQ().func_72321_a(var25.field_72450_a * var21, var25.field_72448_b * var21, var25.field_72449_c * var21).func_72314_b((double)var27, (double)var27, (double)var27));

         for(int var29 = 0; var29 < var28.size(); ++var29) {
            Entity var30 = (Entity)var28.get(var29);
            if (var30.func_70067_L()) {
               float var31 = var30.func_70111_Y();
               AxisAlignedBB var32 = var30.func_174813_aQ().func_72314_b((double)var31, (double)var31, (double)var31);
               if (var32.func_72318_a(var13)) {
                  var26 = true;
               }
            }
         }

         if (var26) {
            return var1;
         } else {
            if (var24.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK) {
               BlockPos var33 = var24.func_178782_a();
               if (var2.func_180495_p(var33).func_177230_c() == Blocks.field_150431_aC) {
                  var33 = var33.func_177977_b();
               }

               EntityBoat var34 = new EntityBoat(var2, (double)((float)var33.func_177958_n() + 0.5F), (double)((float)var33.func_177956_o() + 1.0F), (double)((float)var33.func_177952_p() + 0.5F));
               var34.field_70177_z = (float)(((MathHelper.func_76128_c((double)(var3.field_70177_z * 4.0F / 360.0F) + 0.5D) & 3) - 1) * 90);
               if (!var2.func_72945_a(var34, var34.func_174813_aQ().func_72314_b(-0.1D, -0.1D, -0.1D)).isEmpty()) {
                  return var1;
               }

               if (!var2.field_72995_K) {
                  var2.func_72838_d(var34);
               }

               if (!var3.field_71075_bZ.field_75098_d) {
                  --var1.field_77994_a;
               }

               var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
            }

            return var1;
         }
      }
   }
}
