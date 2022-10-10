package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ItemBucket extends Item {
   private final Fluid field_77876_a;

   public ItemBucket(Fluid var1, Item.Properties var2) {
      super(var2);
      this.field_77876_a = var1;
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      RayTraceResult var5 = this.func_77621_a(var1, var2, this.field_77876_a == Fluids.field_204541_a);
      if (var5 == null) {
         return new ActionResult(EnumActionResult.PASS, var4);
      } else if (var5.field_72313_a == RayTraceResult.Type.BLOCK) {
         BlockPos var6 = var5.func_178782_a();
         if (var1.func_175660_a(var2, var6) && var2.func_175151_a(var6, var5.field_178784_b, var4)) {
            IBlockState var7;
            if (this.field_77876_a == Fluids.field_204541_a) {
               var7 = var1.func_180495_p(var6);
               if (var7.func_177230_c() instanceof IBucketPickupHandler) {
                  Fluid var10 = ((IBucketPickupHandler)var7.func_177230_c()).func_204508_a(var1, var6, var7);
                  if (var10 != Fluids.field_204541_a) {
                     var2.func_71029_a(StatList.field_75929_E.func_199076_b(this));
                     var2.func_184185_a(var10.func_207185_a(FluidTags.field_206960_b) ? SoundEvents.field_187633_N : SoundEvents.field_187630_M, 1.0F, 1.0F);
                     ItemStack var9 = this.func_150910_a(var4, var2, var10.func_204524_b());
                     if (!var1.field_72995_K) {
                        CriteriaTriggers.field_204813_j.func_204817_a((EntityPlayerMP)var2, new ItemStack(var10.func_204524_b()));
                     }

                     return new ActionResult(EnumActionResult.SUCCESS, var9);
                  }
               }

               return new ActionResult(EnumActionResult.FAIL, var4);
            } else {
               var7 = var1.func_180495_p(var6);
               BlockPos var8 = this.func_210768_a(var7, var6, var5);
               if (this.func_180616_a(var2, var1, var8, var5)) {
                  this.func_203792_a(var1, var4, var8);
                  if (var2 instanceof EntityPlayerMP) {
                     CriteriaTriggers.field_193137_x.func_193173_a((EntityPlayerMP)var2, var8, var4);
                  }

                  var2.func_71029_a(StatList.field_75929_E.func_199076_b(this));
                  return new ActionResult(EnumActionResult.SUCCESS, this.func_203790_a(var4, var2));
               } else {
                  return new ActionResult(EnumActionResult.FAIL, var4);
               }
            }
         } else {
            return new ActionResult(EnumActionResult.FAIL, var4);
         }
      } else {
         return new ActionResult(EnumActionResult.PASS, var4);
      }
   }

   private BlockPos func_210768_a(IBlockState var1, BlockPos var2, RayTraceResult var3) {
      return var1.func_177230_c() instanceof ILiquidContainer ? var2 : var3.func_178782_a().func_177972_a(var3.field_178784_b);
   }

   protected ItemStack func_203790_a(ItemStack var1, EntityPlayer var2) {
      return !var2.field_71075_bZ.field_75098_d ? new ItemStack(Items.field_151133_ar) : var1;
   }

   public void func_203792_a(World var1, ItemStack var2, BlockPos var3) {
   }

   private ItemStack func_150910_a(ItemStack var1, EntityPlayer var2, Item var3) {
      if (var2.field_71075_bZ.field_75098_d) {
         return var1;
      } else {
         var1.func_190918_g(1);
         if (var1.func_190926_b()) {
            return new ItemStack(var3);
         } else {
            if (!var2.field_71071_by.func_70441_a(new ItemStack(var3))) {
               var2.func_71019_a(new ItemStack(var3), false);
            }

            return var1;
         }
      }
   }

   public boolean func_180616_a(@Nullable EntityPlayer var1, World var2, BlockPos var3, @Nullable RayTraceResult var4) {
      if (!(this.field_77876_a instanceof FlowingFluid)) {
         return false;
      } else {
         IBlockState var5 = var2.func_180495_p(var3);
         Material var6 = var5.func_185904_a();
         boolean var7 = !var6.func_76220_a();
         boolean var8 = var6.func_76222_j();
         if (var2.func_175623_d(var3) || var7 || var8 || var5.func_177230_c() instanceof ILiquidContainer && ((ILiquidContainer)var5.func_177230_c()).func_204510_a(var2, var3, var5, this.field_77876_a)) {
            if (var2.field_73011_w.func_177500_n() && this.field_77876_a.func_207185_a(FluidTags.field_206959_a)) {
               int var9 = var3.func_177958_n();
               int var10 = var3.func_177956_o();
               int var11 = var3.func_177952_p();
               var2.func_184133_a(var1, var3, SoundEvents.field_187646_bt, SoundCategory.BLOCKS, 0.5F, 2.6F + (var2.field_73012_v.nextFloat() - var2.field_73012_v.nextFloat()) * 0.8F);

               for(int var12 = 0; var12 < 8; ++var12) {
                  var2.func_195594_a(Particles.field_197594_E, (double)var9 + Math.random(), (double)var10 + Math.random(), (double)var11 + Math.random(), 0.0D, 0.0D, 0.0D);
               }
            } else if (var5.func_177230_c() instanceof ILiquidContainer) {
               if (((ILiquidContainer)var5.func_177230_c()).func_204509_a(var2, var3, var5, ((FlowingFluid)this.field_77876_a).func_207204_a(false))) {
                  this.func_203791_b(var1, var2, var3);
               }
            } else {
               if (!var2.field_72995_K && (var7 || var8) && !var6.func_76224_d()) {
                  var2.func_175655_b(var3, true);
               }

               this.func_203791_b(var1, var2, var3);
               var2.func_180501_a(var3, this.field_77876_a.func_207188_f().func_206883_i(), 11);
            }

            return true;
         } else {
            return var4 == null ? false : this.func_180616_a(var1, var2, var4.func_178782_a().func_177972_a(var4.field_178784_b), (RayTraceResult)null);
         }
      }
   }

   protected void func_203791_b(@Nullable EntityPlayer var1, IWorld var2, BlockPos var3) {
      SoundEvent var4 = this.field_77876_a.func_207185_a(FluidTags.field_206960_b) ? SoundEvents.field_187627_L : SoundEvents.field_187624_K;
      var2.func_184133_a(var1, var3, var4, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }
}
