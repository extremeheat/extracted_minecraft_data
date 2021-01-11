package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockPressurePlate extends BlockBasePressurePlate {
   public static final PropertyBool field_176580_a = PropertyBool.func_177716_a("powered");
   private final BlockPressurePlate.Sensitivity field_150069_a;

   protected BlockPressurePlate(Material var1, BlockPressurePlate.Sensitivity var2) {
      super(var1);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176580_a, false));
      this.field_150069_a = var2;
   }

   protected int func_176576_e(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_176580_a) ? 15 : 0;
   }

   protected IBlockState func_176575_a(IBlockState var1, int var2) {
      return var1.func_177226_a(field_176580_a, var2 > 0);
   }

   protected int func_180669_e(World var1, BlockPos var2) {
      AxisAlignedBB var3 = this.func_180667_a(var2);
      List var4;
      switch(this.field_150069_a) {
      case EVERYTHING:
         var4 = var1.func_72839_b((Entity)null, var3);
         break;
      case MOBS:
         var4 = var1.func_72872_a(EntityLivingBase.class, var3);
         break;
      default:
         return 0;
      }

      if (!var4.isEmpty()) {
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            Entity var6 = (Entity)var5.next();
            if (!var6.func_145773_az()) {
               return 15;
            }
         }
      }

      return 0;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176580_a, var1 == 1);
   }

   public int func_176201_c(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_176580_a) ? 1 : 0;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176580_a});
   }

   public static enum Sensitivity {
      EVERYTHING,
      MOBS;

      private Sensitivity() {
      }
   }
}
