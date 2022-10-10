package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemGlassBottle extends Item {
   public ItemGlassBottle(Item.Properties var1) {
      super(var1);
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      List var4 = var1.func_175647_a(EntityAreaEffectCloud.class, var2.func_174813_aQ().func_186662_g(2.0D), (var0) -> {
         return var0 != null && var0.func_70089_S() && var0.func_184494_w() instanceof EntityDragon;
      });
      ItemStack var5 = var2.func_184586_b(var3);
      if (!var4.isEmpty()) {
         EntityAreaEffectCloud var8 = (EntityAreaEffectCloud)var4.get(0);
         var8.func_184483_a(var8.func_184490_j() - 0.5F);
         var1.func_184148_a((EntityPlayer)null, var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, SoundEvents.field_187618_I, SoundCategory.NEUTRAL, 1.0F, 1.0F);
         return new ActionResult(EnumActionResult.SUCCESS, this.func_185061_a(var5, var2, new ItemStack(Items.field_185157_bK)));
      } else {
         RayTraceResult var6 = this.func_77621_a(var1, var2, true);
         if (var6 == null) {
            return new ActionResult(EnumActionResult.PASS, var5);
         } else {
            if (var6.field_72313_a == RayTraceResult.Type.BLOCK) {
               BlockPos var7 = var6.func_178782_a();
               if (!var1.func_175660_a(var2, var7)) {
                  return new ActionResult(EnumActionResult.PASS, var5);
               }

               if (var1.func_204610_c(var7).func_206884_a(FluidTags.field_206959_a)) {
                  var1.func_184148_a(var2, var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, SoundEvents.field_187615_H, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                  return new ActionResult(EnumActionResult.SUCCESS, this.func_185061_a(var5, var2, PotionUtils.func_185188_a(new ItemStack(Items.field_151068_bn), PotionTypes.field_185230_b)));
               }
            }

            return new ActionResult(EnumActionResult.PASS, var5);
         }
      }
   }

   protected ItemStack func_185061_a(ItemStack var1, EntityPlayer var2, ItemStack var3) {
      var1.func_190918_g(1);
      var2.func_71029_a(StatList.field_75929_E.func_199076_b(this));
      if (var1.func_190926_b()) {
         return var3;
      } else {
         if (!var2.field_71071_by.func_70441_a(var3)) {
            var2.func_71019_a(var3, false);
         }

         return var1;
      }
   }
}
