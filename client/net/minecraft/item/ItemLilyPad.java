package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemLilyPad extends ItemBlock {
   public ItemLilyPad(Block var1, Item.Properties var2) {
      super(var1, var2);
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      return EnumActionResult.PASS;
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      RayTraceResult var5 = this.func_77621_a(var1, var2, true);
      if (var5 == null) {
         return new ActionResult(EnumActionResult.PASS, var4);
      } else {
         if (var5.field_72313_a == RayTraceResult.Type.BLOCK) {
            BlockPos var6 = var5.func_178782_a();
            if (!var1.func_175660_a(var2, var6) || !var2.func_175151_a(var6.func_177972_a(var5.field_178784_b), var5.field_178784_b, var4)) {
               return new ActionResult(EnumActionResult.FAIL, var4);
            }

            BlockPos var7 = var6.func_177984_a();
            IBlockState var8 = var1.func_180495_p(var6);
            Material var9 = var8.func_185904_a();
            IFluidState var10 = var1.func_204610_c(var6);
            if ((var10.func_206886_c() == Fluids.field_204546_a || var9 == Material.field_151588_w) && var1.func_175623_d(var7)) {
               var1.func_180501_a(var7, Blocks.field_196651_dG.func_176223_P(), 11);
               if (var2 instanceof EntityPlayerMP) {
                  CriteriaTriggers.field_193137_x.func_193173_a((EntityPlayerMP)var2, var7, var4);
               }

               if (!var2.field_71075_bZ.field_75098_d) {
                  var4.func_190918_g(1);
               }

               var2.func_71029_a(StatList.field_75929_E.func_199076_b(this));
               var1.func_184133_a(var2, var6, SoundEvents.field_187916_gp, SoundCategory.BLOCKS, 1.0F, 1.0F);
               return new ActionResult(EnumActionResult.SUCCESS, var4);
            }
         }

         return new ActionResult(EnumActionResult.FAIL, var4);
      }
   }
}
