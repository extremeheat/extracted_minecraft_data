package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneRepeater extends BlockRedstoneDiode {
   public static final PropertyBool field_176411_a = PropertyBool.func_177716_a("locked");
   public static final PropertyInteger field_176410_b = PropertyInteger.func_177719_a("delay", 1, 4);

   protected BlockRedstoneRepeater(boolean var1) {
      super(var1);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176387_N, EnumFacing.NORTH).func_177226_a(field_176410_b, 1).func_177226_a(field_176411_a, false));
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a("item.diode.name");
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1.func_177226_a(field_176411_a, this.func_176405_b(var2, var3, var1));
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (!var4.field_71075_bZ.field_75099_e) {
         return false;
      } else {
         var1.func_180501_a(var2, var3.func_177231_a(field_176410_b), 3);
         return true;
      }
   }

   protected int func_176403_d(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176410_b) * 2;
   }

   protected IBlockState func_180674_e(IBlockState var1) {
      Integer var2 = (Integer)var1.func_177229_b(field_176410_b);
      Boolean var3 = (Boolean)var1.func_177229_b(field_176411_a);
      EnumFacing var4 = (EnumFacing)var1.func_177229_b(field_176387_N);
      return Blocks.field_150416_aS.func_176223_P().func_177226_a(field_176387_N, var4).func_177226_a(field_176410_b, var2).func_177226_a(field_176411_a, var3);
   }

   protected IBlockState func_180675_k(IBlockState var1) {
      Integer var2 = (Integer)var1.func_177229_b(field_176410_b);
      Boolean var3 = (Boolean)var1.func_177229_b(field_176411_a);
      EnumFacing var4 = (EnumFacing)var1.func_177229_b(field_176387_N);
      return Blocks.field_150413_aR.func_176223_P().func_177226_a(field_176387_N, var4).func_177226_a(field_176410_b, var2).func_177226_a(field_176411_a, var3);
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_151107_aW;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Items.field_151107_aW;
   }

   public boolean func_176405_b(IBlockAccess var1, BlockPos var2, IBlockState var3) {
      return this.func_176407_c(var1, var2, var3) > 0;
   }

   protected boolean func_149908_a(Block var1) {
      return func_149909_d(var1);
   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (this.field_149914_a) {
         EnumFacing var5 = (EnumFacing)var3.func_177229_b(field_176387_N);
         double var6 = (double)((float)var2.func_177958_n() + 0.5F) + (double)(var4.nextFloat() - 0.5F) * 0.2D;
         double var8 = (double)((float)var2.func_177956_o() + 0.4F) + (double)(var4.nextFloat() - 0.5F) * 0.2D;
         double var10 = (double)((float)var2.func_177952_p() + 0.5F) + (double)(var4.nextFloat() - 0.5F) * 0.2D;
         float var12 = -5.0F;
         if (var4.nextBoolean()) {
            var12 = (float)((Integer)var3.func_177229_b(field_176410_b) * 2 - 1);
         }

         var12 /= 16.0F;
         double var13 = (double)(var12 * (float)var5.func_82601_c());
         double var15 = (double)(var12 * (float)var5.func_82599_e());
         var1.func_175688_a(EnumParticleTypes.REDSTONE, var6 + var13, var8, var10 + var15, 0.0D, 0.0D, 0.0D);
      }
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      super.func_180663_b(var1, var2, var3);
      this.func_176400_h(var1, var2, var3);
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176387_N, EnumFacing.func_176731_b(var1)).func_177226_a(field_176411_a, false).func_177226_a(field_176410_b, 1 + (var1 >> 2));
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((EnumFacing)var1.func_177229_b(field_176387_N)).func_176736_b();
      var3 |= (Integer)var1.func_177229_b(field_176410_b) - 1 << 2;
      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176387_N, field_176410_b, field_176411_a});
   }
}
