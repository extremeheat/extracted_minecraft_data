package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemEnderEye extends Item {
   public ItemEnderEye(Item.Properties var1) {
      super(var1);
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      IBlockState var4 = var2.func_180495_p(var3);
      if (var4.func_177230_c() == Blocks.field_150378_br && !(Boolean)var4.func_177229_b(BlockEndPortalFrame.field_176507_b)) {
         if (var2.field_72995_K) {
            return EnumActionResult.SUCCESS;
         } else {
            IBlockState var5 = (IBlockState)var4.func_206870_a(BlockEndPortalFrame.field_176507_b, true);
            Block.func_199601_a(var4, var5, var2, var3);
            var2.func_180501_a(var3, var5, 2);
            var2.func_175666_e(var3, Blocks.field_150378_br);
            var1.func_195996_i().func_190918_g(1);

            for(int var6 = 0; var6 < 16; ++var6) {
               double var7 = (double)((float)var3.func_177958_n() + (5.0F + field_77697_d.nextFloat() * 6.0F) / 16.0F);
               double var9 = (double)((float)var3.func_177956_o() + 0.8125F);
               double var11 = (double)((float)var3.func_177952_p() + (5.0F + field_77697_d.nextFloat() * 6.0F) / 16.0F);
               double var13 = 0.0D;
               double var15 = 0.0D;
               double var17 = 0.0D;
               var2.func_195594_a(Particles.field_197601_L, var7, var9, var11, 0.0D, 0.0D, 0.0D);
            }

            var2.func_184133_a((EntityPlayer)null, var3, SoundEvents.field_193781_bp, SoundCategory.BLOCKS, 1.0F, 1.0F);
            BlockPattern.PatternHelper var19 = BlockEndPortalFrame.func_185661_e().func_177681_a(var2, var3);
            if (var19 != null) {
               BlockPos var20 = var19.func_181117_a().func_177982_a(-3, 0, -3);

               for(int var8 = 0; var8 < 3; ++var8) {
                  for(int var21 = 0; var21 < 3; ++var21) {
                     var2.func_180501_a(var20.func_177982_a(var8, 0, var21), Blocks.field_150384_bq.func_176223_P(), 2);
                  }
               }

               var2.func_175669_a(1038, var20.func_177982_a(1, 0, 1), 0);
            }

            return EnumActionResult.SUCCESS;
         }
      } else {
         return EnumActionResult.PASS;
      }
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      RayTraceResult var5 = this.func_77621_a(var1, var2, false);
      if (var5 != null && var5.field_72313_a == RayTraceResult.Type.BLOCK && var1.func_180495_p(var5.func_178782_a()).func_177230_c() == Blocks.field_150378_br) {
         return new ActionResult(EnumActionResult.PASS, var4);
      } else {
         var2.func_184598_c(var3);
         if (!var1.field_72995_K) {
            BlockPos var6 = ((WorldServer)var1).func_72863_F().func_211268_a(var1, "Stronghold", new BlockPos(var2), 100, false);
            if (var6 != null) {
               EntityEnderEye var7 = new EntityEnderEye(var1, var2.field_70165_t, var2.field_70163_u + (double)(var2.field_70131_O / 2.0F), var2.field_70161_v);
               var7.func_180465_a(var6);
               var1.func_72838_d(var7);
               if (var2 instanceof EntityPlayerMP) {
                  CriteriaTriggers.field_192132_l.func_192239_a((EntityPlayerMP)var2, var6);
               }

               var1.func_184148_a((EntityPlayer)null, var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, SoundEvents.field_187528_aR, SoundCategory.NEUTRAL, 0.5F, 0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F));
               var1.func_180498_a((EntityPlayer)null, 1003, new BlockPos(var2), 0);
               if (!var2.field_71075_bZ.field_75098_d) {
                  var4.func_190918_g(1);
               }

               var2.func_71029_a(StatList.field_75929_E.func_199076_b(this));
               return new ActionResult(EnumActionResult.SUCCESS, var4);
            }
         }

         return new ActionResult(EnumActionResult.SUCCESS, var4);
      }
   }
}
