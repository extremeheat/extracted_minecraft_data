package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTrident;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ItemTrident extends Item {
   public ItemTrident(Item.Properties var1) {
      super(var1);
      this.func_185043_a(new ResourceLocation("throwing"), (var0, var1x, var2) -> {
         return var2 != null && var2.func_184587_cr() && var2.func_184607_cu() == var0 ? 1.0F : 0.0F;
      });
   }

   public boolean func_195938_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4) {
      return !var4.func_184812_l_();
   }

   public EnumAction func_77661_b(ItemStack var1) {
      return EnumAction.SPEAR;
   }

   public int func_77626_a(ItemStack var1) {
      return 72000;
   }

   public boolean func_77636_d(ItemStack var1) {
      return false;
   }

   public void func_77615_a(ItemStack var1, World var2, EntityLivingBase var3, int var4) {
      if (var3 instanceof EntityPlayer) {
         EntityPlayer var5 = (EntityPlayer)var3;
         int var6 = this.func_77626_a(var1) - var4;
         if (var6 >= 10) {
            int var7 = EnchantmentHelper.func_203190_g(var1);
            if (var7 <= 0 || var5.func_70026_G()) {
               if (!var2.field_72995_K) {
                  var1.func_77972_a(1, var5);
                  if (var7 == 0) {
                     EntityTrident var8 = new EntityTrident(var2, var5, var1);
                     var8.func_184547_a(var5, var5.field_70125_A, var5.field_70177_z, 0.0F, 2.5F + (float)var7 * 0.5F, 1.0F);
                     if (var5.field_71075_bZ.field_75098_d) {
                        var8.field_70251_a = EntityArrow.PickupStatus.CREATIVE_ONLY;
                     }

                     var2.func_72838_d(var8);
                     if (!var5.field_71075_bZ.field_75098_d) {
                        var5.field_71071_by.func_184437_d(var1);
                     }
                  }
               }

               var5.func_71029_a(StatList.field_75929_E.func_199076_b(this));
               SoundEvent var17 = SoundEvents.field_203274_ip;
               if (var7 > 0) {
                  float var9 = var5.field_70177_z;
                  float var10 = var5.field_70125_A;
                  float var11 = -MathHelper.func_76126_a(var9 * 0.017453292F) * MathHelper.func_76134_b(var10 * 0.017453292F);
                  float var12 = -MathHelper.func_76126_a(var10 * 0.017453292F);
                  float var13 = MathHelper.func_76134_b(var9 * 0.017453292F) * MathHelper.func_76134_b(var10 * 0.017453292F);
                  float var14 = MathHelper.func_76129_c(var11 * var11 + var12 * var12 + var13 * var13);
                  float var15 = 3.0F * ((1.0F + (float)var7) / 4.0F);
                  var11 *= var15 / var14;
                  var12 *= var15 / var14;
                  var13 *= var15 / var14;
                  var5.func_70024_g((double)var11, (double)var12, (double)var13);
                  if (var7 >= 3) {
                     var17 = SoundEvents.field_203273_io;
                  } else if (var7 == 2) {
                     var17 = SoundEvents.field_203272_in;
                  } else {
                     var17 = SoundEvents.field_203271_im;
                  }

                  var5.func_204803_n(20);
                  if (var5.field_70122_E) {
                     float var16 = 1.1999999F;
                     var5.func_70091_d(MoverType.SELF, 0.0D, 1.1999999284744263D, 0.0D);
                  }
               }

               var2.func_184148_a((EntityPlayer)null, var5.field_70165_t, var5.field_70163_u, var5.field_70161_v, var17, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
         }
      }
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      if (var4.func_77952_i() >= var4.func_77958_k()) {
         return new ActionResult(EnumActionResult.FAIL, var4);
      } else if (EnchantmentHelper.func_203190_g(var4) > 0 && !var2.func_70026_G()) {
         return new ActionResult(EnumActionResult.FAIL, var4);
      } else {
         var2.func_184598_c(var3);
         return new ActionResult(EnumActionResult.SUCCESS, var4);
      }
   }

   public boolean func_77644_a(ItemStack var1, EntityLivingBase var2, EntityLivingBase var3) {
      var1.func_77972_a(1, var3);
      return true;
   }

   public boolean func_179218_a(ItemStack var1, World var2, IBlockState var3, BlockPos var4, EntityLivingBase var5) {
      if ((double)var3.func_185887_b(var2, var4) != 0.0D) {
         var1.func_77972_a(2, var5);
      }

      return true;
   }

   public Multimap<String, AttributeModifier> func_111205_h(EntityEquipmentSlot var1) {
      Multimap var2 = super.func_111205_h(var1);
      if (var1 == EntityEquipmentSlot.MAINHAND) {
         var2.put(SharedMonsterAttributes.field_111264_e.func_111108_a(), new AttributeModifier(field_111210_e, "Tool modifier", 8.0D, 0));
         var2.put(SharedMonsterAttributes.field_188790_f.func_111108_a(), new AttributeModifier(field_185050_h, "Tool modifier", -2.9000000953674316D, 0));
      }

      return var2;
   }

   public int func_77619_b() {
      return 1;
   }
}
