package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Rotations;
import net.minecraft.world.World;

public class ItemArmorStand extends Item {
   public ItemArmorStand(Item.Properties var1) {
      super(var1);
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      EnumFacing var2 = var1.func_196000_l();
      if (var2 == EnumFacing.DOWN) {
         return EnumActionResult.FAIL;
      } else {
         World var3 = var1.func_195991_k();
         BlockItemUseContext var4 = new BlockItemUseContext(var1);
         BlockPos var5 = var4.func_195995_a();
         BlockPos var6 = var5.func_177984_a();
         if (var4.func_196011_b() && var3.func_180495_p(var6).func_196953_a(var4)) {
            double var7 = (double)var5.func_177958_n();
            double var9 = (double)var5.func_177956_o();
            double var11 = (double)var5.func_177952_p();
            List var13 = var3.func_72839_b((Entity)null, new AxisAlignedBB(var7, var9, var11, var7 + 1.0D, var9 + 2.0D, var11 + 1.0D));
            if (!var13.isEmpty()) {
               return EnumActionResult.FAIL;
            } else {
               ItemStack var14 = var1.func_195996_i();
               if (!var3.field_72995_K) {
                  var3.func_175698_g(var5);
                  var3.func_175698_g(var6);
                  EntityArmorStand var15 = new EntityArmorStand(var3, var7 + 0.5D, var9, var11 + 0.5D);
                  float var16 = (float)MathHelper.func_76141_d((MathHelper.func_76142_g(var1.func_195990_h() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                  var15.func_70012_b(var7 + 0.5D, var9, var11 + 0.5D, var16, 0.0F);
                  this.func_179221_a(var15, var3.field_73012_v);
                  EntityType.func_208048_a(var3, var1.func_195999_j(), var15, var14.func_77978_p());
                  var3.func_72838_d(var15);
                  var3.func_184148_a((EntityPlayer)null, var15.field_70165_t, var15.field_70163_u, var15.field_70161_v, SoundEvents.field_187710_m, SoundCategory.BLOCKS, 0.75F, 0.8F);
               }

               var14.func_190918_g(1);
               return EnumActionResult.SUCCESS;
            }
         } else {
            return EnumActionResult.FAIL;
         }
      }
   }

   private void func_179221_a(EntityArmorStand var1, Random var2) {
      Rotations var3 = var1.func_175418_s();
      float var5 = var2.nextFloat() * 5.0F;
      float var6 = var2.nextFloat() * 20.0F - 10.0F;
      Rotations var4 = new Rotations(var3.func_179415_b() + var5, var3.func_179416_c() + var6, var3.func_179413_d());
      var1.func_175415_a(var4);
      var3 = var1.func_175408_t();
      var5 = var2.nextFloat() * 10.0F - 5.0F;
      var4 = new Rotations(var3.func_179415_b(), var3.func_179416_c() + var5, var3.func_179413_d());
      var1.func_175424_b(var4);
   }
}
