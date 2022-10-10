package net.minecraft.enchantment;

import java.util.Iterator;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EnchantmentFrostWalker extends Enchantment {
   public EnchantmentFrostWalker(Enchantment.Rarity var1, EntityEquipmentSlot... var2) {
      super(var1, EnumEnchantmentType.ARMOR_FEET, var2);
   }

   public int func_77321_a(int var1) {
      return var1 * 10;
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 15;
   }

   public boolean func_185261_e() {
      return true;
   }

   public int func_77325_b() {
      return 2;
   }

   public static void func_185266_a(EntityLivingBase var0, World var1, BlockPos var2, int var3) {
      if (var0.field_70122_E) {
         IBlockState var4 = Blocks.field_185778_de.func_176223_P();
         float var5 = (float)Math.min(16, 2 + var3);
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos(0, 0, 0);
         Iterator var7 = BlockPos.func_177975_b(var2.func_177963_a((double)(-var5), -1.0D, (double)(-var5)), var2.func_177963_a((double)var5, -1.0D, (double)var5)).iterator();

         while(var7.hasNext()) {
            BlockPos.MutableBlockPos var8 = (BlockPos.MutableBlockPos)var7.next();
            if (var8.func_177957_d(var0.field_70165_t, var0.field_70163_u, var0.field_70161_v) <= (double)(var5 * var5)) {
               var6.func_181079_c(var8.func_177958_n(), var8.func_177956_o() + 1, var8.func_177952_p());
               IBlockState var9 = var1.func_180495_p(var6);
               if (var9.func_196958_f()) {
                  IBlockState var10 = var1.func_180495_p(var8);
                  if (var10.func_185904_a() == Material.field_151586_h && (Integer)var10.func_177229_b(BlockFlowingFluid.field_176367_b) == 0 && var4.func_196955_c(var1, var8) && var1.func_195584_a(var4, var8)) {
                     var1.func_175656_a(var8, var4);
                     var1.func_205220_G_().func_205360_a(var8.func_185334_h(), Blocks.field_185778_de, MathHelper.func_76136_a(var0.func_70681_au(), 60, 120));
                  }
               }
            }
         }

      }
   }

   public boolean func_77326_a(Enchantment var1) {
      return super.func_77326_a(var1) && var1 != Enchantments.field_185300_i;
   }
}
