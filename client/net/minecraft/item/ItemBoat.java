package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition$MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemBoat extends Item {
   public ItemBoat() {
      super();
      this.field_77777_bU = 1;
      this.func_77637_a(CreativeTabs.field_78029_e);
   }

   @Override
   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      float var4 = 1.0F;
      float var5 = var3.field_70127_C + (var3.field_70125_A - var3.field_70127_C) * var4;
      float var6 = var3.field_70126_B + (var3.field_70177_z - var3.field_70126_B) * var4;
      double var7 = var3.field_70169_q + (var3.field_70165_t - var3.field_70169_q) * (double)var4;
      double var9 = var3.field_70167_r + (var3.field_70163_u - var3.field_70167_r) * (double)var4 + 1.62 - (double)var3.field_70129_M;
      double var11 = var3.field_70166_s + (var3.field_70161_v - var3.field_70166_s) * (double)var4;
      Vec3 var13 = Vec3.func_72443_a(var7, var9, var11);
      float var14 = MathHelper.func_76134_b(-var6 * 0.017453292F - 3.1415927F);
      float var15 = MathHelper.func_76126_a(-var6 * 0.017453292F - 3.1415927F);
      float var16 = -MathHelper.func_76134_b(-var5 * 0.017453292F);
      float var17 = MathHelper.func_76126_a(-var5 * 0.017453292F);
      float var18 = var15 * var16;
      float var20 = var14 * var16;
      double var21 = 5.0;
      Vec3 var23 = var13.func_72441_c((double)var18 * var21, (double)var17 * var21, (double)var20 * var21);
      MovingObjectPosition var24 = var2.func_72901_a(var13, var23, true);
      if (var24 == null) {
         return var1;
      } else {
         Vec3 var25 = var3.func_70676_i(var4);
         boolean var26 = false;
         float var27 = 1.0F;
         List var28 = var2.func_72839_b(
            var3,
            var3.field_70121_D
               .func_72321_a(var25.field_72450_a * var21, var25.field_72448_b * var21, var25.field_72449_c * var21)
               .func_72314_b((double)var27, (double)var27, (double)var27)
         );

         for(int var29 = 0; var29 < var28.size(); ++var29) {
            Entity var30 = (Entity)var28.get(var29);
            if (var30.func_70067_L()) {
               float var31 = var30.func_70111_Y();
               AxisAlignedBB var32 = var30.field_70121_D.func_72314_b((double)var31, (double)var31, (double)var31);
               if (var32.func_72318_a(var13)) {
                  var26 = true;
               }
            }
         }

         if (var26) {
            return var1;
         } else {
            if (var24.field_72313_a == MovingObjectPosition$MovingObjectType.BLOCK) {
               int var33 = var24.field_72311_b;
               int var34 = var24.field_72312_c;
               int var35 = var24.field_72309_d;
               if (var2.func_147439_a(var33, var34, var35) == Blocks.field_150431_aC) {
                  --var34;
               }

               EntityBoat var36 = new EntityBoat(var2, (double)((float)var33 + 0.5F), (double)((float)var34 + 1.0F), (double)((float)var35 + 0.5F));
               var36.field_70177_z = (float)(((MathHelper.func_76128_c((double)(var3.field_70177_z * 4.0F / 360.0F) + 0.5) & 3) - 1) * 90);
               if (!var2.func_72945_a(var36, var36.field_70121_D.func_72314_b(-0.1, -0.1, -0.1)).isEmpty()) {
                  return var1;
               }

               if (!var2.field_72995_K) {
                  var2.func_72838_d(var36);
               }

               if (!var3.field_71075_bZ.field_75098_d) {
                  --var1.field_77994_a;
               }
            }

            return var1;
         }
      }
   }
}
