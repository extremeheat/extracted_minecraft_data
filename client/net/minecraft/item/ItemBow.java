package net.minecraft.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemBow extends Item {
   public ItemBow(Item.Properties var1) {
      super(var1);
      this.func_185043_a(new ResourceLocation("pull"), (var0, var1x, var2) -> {
         if (var2 == null) {
            return 0.0F;
         } else {
            return var2.func_184607_cu().func_77973_b() != Items.field_151031_f ? 0.0F : (float)(var0.func_77988_m() - var2.func_184605_cv()) / 20.0F;
         }
      });
      this.func_185043_a(new ResourceLocation("pulling"), (var0, var1x, var2) -> {
         return var2 != null && var2.func_184587_cr() && var2.func_184607_cu() == var0 ? 1.0F : 0.0F;
      });
   }

   private ItemStack func_185060_a(EntityPlayer var1) {
      if (this.func_185058_h_(var1.func_184586_b(EnumHand.OFF_HAND))) {
         return var1.func_184586_b(EnumHand.OFF_HAND);
      } else if (this.func_185058_h_(var1.func_184586_b(EnumHand.MAIN_HAND))) {
         return var1.func_184586_b(EnumHand.MAIN_HAND);
      } else {
         for(int var2 = 0; var2 < var1.field_71071_by.func_70302_i_(); ++var2) {
            ItemStack var3 = var1.field_71071_by.func_70301_a(var2);
            if (this.func_185058_h_(var3)) {
               return var3;
            }
         }

         return ItemStack.field_190927_a;
      }
   }

   protected boolean func_185058_h_(ItemStack var1) {
      return var1.func_77973_b() instanceof ItemArrow;
   }

   public void func_77615_a(ItemStack var1, World var2, EntityLivingBase var3, int var4) {
      if (var3 instanceof EntityPlayer) {
         EntityPlayer var5 = (EntityPlayer)var3;
         boolean var6 = var5.field_71075_bZ.field_75098_d || EnchantmentHelper.func_77506_a(Enchantments.field_185312_x, var1) > 0;
         ItemStack var7 = this.func_185060_a(var5);
         if (!var7.func_190926_b() || var6) {
            if (var7.func_190926_b()) {
               var7 = new ItemStack(Items.field_151032_g);
            }

            int var8 = this.func_77626_a(var1) - var4;
            float var9 = func_185059_b(var8);
            if ((double)var9 >= 0.1D) {
               boolean var10 = var6 && var7.func_77973_b() == Items.field_151032_g;
               if (!var2.field_72995_K) {
                  ItemArrow var11 = (ItemArrow)((ItemArrow)(var7.func_77973_b() instanceof ItemArrow ? var7.func_77973_b() : Items.field_151032_g));
                  EntityArrow var12 = var11.func_200887_a(var2, var7, var5);
                  var12.func_184547_a(var5, var5.field_70125_A, var5.field_70177_z, 0.0F, var9 * 3.0F, 1.0F);
                  if (var9 == 1.0F) {
                     var12.func_70243_d(true);
                  }

                  int var13 = EnchantmentHelper.func_77506_a(Enchantments.field_185309_u, var1);
                  if (var13 > 0) {
                     var12.func_70239_b(var12.func_70242_d() + (double)var13 * 0.5D + 0.5D);
                  }

                  int var14 = EnchantmentHelper.func_77506_a(Enchantments.field_185310_v, var1);
                  if (var14 > 0) {
                     var12.func_70240_a(var14);
                  }

                  if (EnchantmentHelper.func_77506_a(Enchantments.field_185311_w, var1) > 0) {
                     var12.func_70015_d(100);
                  }

                  var1.func_77972_a(1, var5);
                  if (var10 || var5.field_71075_bZ.field_75098_d && (var7.func_77973_b() == Items.field_185166_h || var7.func_77973_b() == Items.field_185167_i)) {
                     var12.field_70251_a = EntityArrow.PickupStatus.CREATIVE_ONLY;
                  }

                  var2.func_72838_d(var12);
               }

               var2.func_184148_a((EntityPlayer)null, var5.field_70165_t, var5.field_70163_u, var5.field_70161_v, SoundEvents.field_187737_v, SoundCategory.PLAYERS, 1.0F, 1.0F / (field_77697_d.nextFloat() * 0.4F + 1.2F) + var9 * 0.5F);
               if (!var10 && !var5.field_71075_bZ.field_75098_d) {
                  var7.func_190918_g(1);
                  if (var7.func_190926_b()) {
                     var5.field_71071_by.func_184437_d(var7);
                  }
               }

               var5.func_71029_a(StatList.field_75929_E.func_199076_b(this));
            }
         }
      }
   }

   public static float func_185059_b(int var0) {
      float var1 = (float)var0 / 20.0F;
      var1 = (var1 * var1 + var1 * 2.0F) / 3.0F;
      if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      return var1;
   }

   public int func_77626_a(ItemStack var1) {
      return 72000;
   }

   public EnumAction func_77661_b(ItemStack var1) {
      return EnumAction.BOW;
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      boolean var5 = !this.func_185060_a(var2).func_190926_b();
      if (!var2.field_71075_bZ.field_75098_d && !var5) {
         return var5 ? new ActionResult(EnumActionResult.PASS, var4) : new ActionResult(EnumActionResult.FAIL, var4);
      } else {
         var2.func_184598_c(var3);
         return new ActionResult(EnumActionResult.SUCCESS, var4);
      }
   }

   public int func_77619_b() {
      return 1;
   }
}
