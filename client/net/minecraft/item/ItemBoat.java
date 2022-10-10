package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemBoat extends Item {
   private final EntityBoat.Type field_185057_a;

   public ItemBoat(EntityBoat.Type var1, Item.Properties var2) {
      super(var2);
      this.field_185057_a = var1;
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      float var5 = 1.0F;
      float var6 = var2.field_70127_C + (var2.field_70125_A - var2.field_70127_C) * 1.0F;
      float var7 = var2.field_70126_B + (var2.field_70177_z - var2.field_70126_B) * 1.0F;
      double var8 = var2.field_70169_q + (var2.field_70165_t - var2.field_70169_q) * 1.0D;
      double var10 = var2.field_70167_r + (var2.field_70163_u - var2.field_70167_r) * 1.0D + (double)var2.func_70047_e();
      double var12 = var2.field_70166_s + (var2.field_70161_v - var2.field_70166_s) * 1.0D;
      Vec3d var14 = new Vec3d(var8, var10, var12);
      float var15 = MathHelper.func_76134_b(-var7 * 0.017453292F - 3.1415927F);
      float var16 = MathHelper.func_76126_a(-var7 * 0.017453292F - 3.1415927F);
      float var17 = -MathHelper.func_76134_b(-var6 * 0.017453292F);
      float var18 = MathHelper.func_76126_a(-var6 * 0.017453292F);
      float var19 = var16 * var17;
      float var21 = var15 * var17;
      double var22 = 5.0D;
      Vec3d var24 = var14.func_72441_c((double)var19 * 5.0D, (double)var18 * 5.0D, (double)var21 * 5.0D);
      RayTraceResult var25 = var1.func_200260_a(var14, var24, RayTraceFluidMode.ALWAYS);
      if (var25 == null) {
         return new ActionResult(EnumActionResult.PASS, var4);
      } else {
         Vec3d var26 = var2.func_70676_i(1.0F);
         boolean var27 = false;
         List var28 = var1.func_72839_b(var2, var2.func_174813_aQ().func_72321_a(var26.field_72450_a * 5.0D, var26.field_72448_b * 5.0D, var26.field_72449_c * 5.0D).func_186662_g(1.0D));

         for(int var29 = 0; var29 < var28.size(); ++var29) {
            Entity var30 = (Entity)var28.get(var29);
            if (var30.func_70067_L()) {
               AxisAlignedBB var31 = var30.func_174813_aQ().func_186662_g((double)var30.func_70111_Y());
               if (var31.func_72318_a(var14)) {
                  var27 = true;
               }
            }
         }

         if (var27) {
            return new ActionResult(EnumActionResult.PASS, var4);
         } else if (var25.field_72313_a == RayTraceResult.Type.BLOCK) {
            BlockPos var32 = var25.func_178782_a();
            Block var33 = var1.func_180495_p(var32).func_177230_c();
            EntityBoat var34 = new EntityBoat(var1, var25.field_72307_f.field_72450_a, var25.field_72307_f.field_72448_b, var25.field_72307_f.field_72449_c);
            var34.func_184458_a(this.field_185057_a);
            var34.field_70177_z = var2.field_70177_z;
            if (!var1.func_195586_b(var34, var34.func_174813_aQ().func_186662_g(-0.1D))) {
               return new ActionResult(EnumActionResult.FAIL, var4);
            } else {
               if (!var1.field_72995_K) {
                  var1.func_72838_d(var34);
               }

               if (!var2.field_71075_bZ.field_75098_d) {
                  var4.func_190918_g(1);
               }

               var2.func_71029_a(StatList.field_75929_E.func_199076_b(this));
               return new ActionResult(EnumActionResult.SUCCESS, var4);
            }
         } else {
            return new ActionResult(EnumActionResult.PASS, var4);
         }
      }
   }
}
