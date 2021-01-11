package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityComparator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneComparator extends BlockRedstoneDiode implements ITileEntityProvider {
   public static final PropertyBool field_176464_a = PropertyBool.func_177716_a("powered");
   public static final PropertyEnum<BlockRedstoneComparator.Mode> field_176463_b = PropertyEnum.func_177709_a("mode", BlockRedstoneComparator.Mode.class);

   public BlockRedstoneComparator(boolean var1) {
      super(var1);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176387_N, EnumFacing.NORTH).func_177226_a(field_176464_a, false).func_177226_a(field_176463_b, BlockRedstoneComparator.Mode.COMPARE));
      this.field_149758_A = true;
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a("item.comparator.name");
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_151132_bS;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Items.field_151132_bS;
   }

   protected int func_176403_d(IBlockState var1) {
      return 2;
   }

   protected IBlockState func_180674_e(IBlockState var1) {
      Boolean var2 = (Boolean)var1.func_177229_b(field_176464_a);
      BlockRedstoneComparator.Mode var3 = (BlockRedstoneComparator.Mode)var1.func_177229_b(field_176463_b);
      EnumFacing var4 = (EnumFacing)var1.func_177229_b(field_176387_N);
      return Blocks.field_150455_bV.func_176223_P().func_177226_a(field_176387_N, var4).func_177226_a(field_176464_a, var2).func_177226_a(field_176463_b, var3);
   }

   protected IBlockState func_180675_k(IBlockState var1) {
      Boolean var2 = (Boolean)var1.func_177229_b(field_176464_a);
      BlockRedstoneComparator.Mode var3 = (BlockRedstoneComparator.Mode)var1.func_177229_b(field_176463_b);
      EnumFacing var4 = (EnumFacing)var1.func_177229_b(field_176387_N);
      return Blocks.field_150441_bU.func_176223_P().func_177226_a(field_176387_N, var4).func_177226_a(field_176464_a, var2).func_177226_a(field_176463_b, var3);
   }

   protected boolean func_176406_l(IBlockState var1) {
      return this.field_149914_a || (Boolean)var1.func_177229_b(field_176464_a);
   }

   protected int func_176408_a(IBlockAccess var1, BlockPos var2, IBlockState var3) {
      TileEntity var4 = var1.func_175625_s(var2);
      return var4 instanceof TileEntityComparator ? ((TileEntityComparator)var4).func_145996_a() : 0;
   }

   private int func_176460_j(World var1, BlockPos var2, IBlockState var3) {
      return var3.func_177229_b(field_176463_b) == BlockRedstoneComparator.Mode.SUBTRACT ? Math.max(this.func_176397_f(var1, var2, var3) - this.func_176407_c(var1, var2, var3), 0) : this.func_176397_f(var1, var2, var3);
   }

   protected boolean func_176404_e(World var1, BlockPos var2, IBlockState var3) {
      int var4 = this.func_176397_f(var1, var2, var3);
      if (var4 >= 15) {
         return true;
      } else if (var4 == 0) {
         return false;
      } else {
         int var5 = this.func_176407_c(var1, var2, var3);
         if (var5 == 0) {
            return true;
         } else {
            return var4 >= var5;
         }
      }
   }

   protected int func_176397_f(World var1, BlockPos var2, IBlockState var3) {
      int var4 = super.func_176397_f(var1, var2, var3);
      EnumFacing var5 = (EnumFacing)var3.func_177229_b(field_176387_N);
      BlockPos var6 = var2.func_177972_a(var5);
      Block var7 = var1.func_180495_p(var6).func_177230_c();
      if (var7.func_149740_M()) {
         var4 = var7.func_180641_l(var1, var6);
      } else if (var4 < 15 && var7.func_149721_r()) {
         var6 = var6.func_177972_a(var5);
         var7 = var1.func_180495_p(var6).func_177230_c();
         if (var7.func_149740_M()) {
            var4 = var7.func_180641_l(var1, var6);
         } else if (var7.func_149688_o() == Material.field_151579_a) {
            EntityItemFrame var8 = this.func_176461_a(var1, var5, var6);
            if (var8 != null) {
               var4 = var8.func_174866_q();
            }
         }
      }

      return var4;
   }

   private EntityItemFrame func_176461_a(World var1, final EnumFacing var2, BlockPos var3) {
      List var4 = var1.func_175647_a(EntityItemFrame.class, new AxisAlignedBB((double)var3.func_177958_n(), (double)var3.func_177956_o(), (double)var3.func_177952_p(), (double)(var3.func_177958_n() + 1), (double)(var3.func_177956_o() + 1), (double)(var3.func_177952_p() + 1)), new Predicate<Entity>() {
         public boolean apply(Entity var1) {
            return var1 != null && var1.func_174811_aO() == var2;
         }

         // $FF: synthetic method
         public boolean apply(Object var1) {
            return this.apply((Entity)var1);
         }
      });
      return var4.size() == 1 ? (EntityItemFrame)var4.get(0) : null;
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (!var4.field_71075_bZ.field_75099_e) {
         return false;
      } else {
         var3 = var3.func_177231_a(field_176463_b);
         var1.func_72908_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D, "random.click", 0.3F, var3.func_177229_b(field_176463_b) == BlockRedstoneComparator.Mode.SUBTRACT ? 0.55F : 0.5F);
         var1.func_180501_a(var2, var3, 2);
         this.func_176462_k(var1, var2, var3);
         return true;
      }
   }

   protected void func_176398_g(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.func_175691_a(var2, this)) {
         int var4 = this.func_176460_j(var1, var2, var3);
         TileEntity var5 = var1.func_175625_s(var2);
         int var6 = var5 instanceof TileEntityComparator ? ((TileEntityComparator)var5).func_145996_a() : 0;
         if (var4 != var6 || this.func_176406_l(var3) != this.func_176404_e(var1, var2, var3)) {
            if (this.func_176402_i(var1, var2, var3)) {
               var1.func_175654_a(var2, this, 2, -1);
            } else {
               var1.func_175654_a(var2, this, 2, 0);
            }
         }

      }
   }

   private void func_176462_k(World var1, BlockPos var2, IBlockState var3) {
      int var4 = this.func_176460_j(var1, var2, var3);
      TileEntity var5 = var1.func_175625_s(var2);
      int var6 = 0;
      if (var5 instanceof TileEntityComparator) {
         TileEntityComparator var7 = (TileEntityComparator)var5;
         var6 = var7.func_145996_a();
         var7.func_145995_a(var4);
      }

      if (var6 != var4 || var3.func_177229_b(field_176463_b) == BlockRedstoneComparator.Mode.COMPARE) {
         boolean var9 = this.func_176404_e(var1, var2, var3);
         boolean var8 = this.func_176406_l(var3);
         if (var8 && !var9) {
            var1.func_180501_a(var2, var3.func_177226_a(field_176464_a, false), 2);
         } else if (!var8 && var9) {
            var1.func_180501_a(var2, var3.func_177226_a(field_176464_a, true), 2);
         }

         this.func_176400_h(var1, var2, var3);
      }

   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (this.field_149914_a) {
         var1.func_180501_a(var2, this.func_180675_k(var3).func_177226_a(field_176464_a, true), 4);
      }

      this.func_176462_k(var1, var2, var3);
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      super.func_176213_c(var1, var2, var3);
      var1.func_175690_a(var2, this.func_149915_a(var1, 0));
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      super.func_180663_b(var1, var2, var3);
      var1.func_175713_t(var2);
      this.func_176400_h(var1, var2, var3);
   }

   public boolean func_180648_a(World var1, BlockPos var2, IBlockState var3, int var4, int var5) {
      super.func_180648_a(var1, var2, var3, var4, var5);
      TileEntity var6 = var1.func_175625_s(var2);
      return var6 == null ? false : var6.func_145842_c(var4, var5);
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityComparator();
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176387_N, EnumFacing.func_176731_b(var1)).func_177226_a(field_176464_a, (var1 & 8) > 0).func_177226_a(field_176463_b, (var1 & 4) > 0 ? BlockRedstoneComparator.Mode.SUBTRACT : BlockRedstoneComparator.Mode.COMPARE);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((EnumFacing)var1.func_177229_b(field_176387_N)).func_176736_b();
      if ((Boolean)var1.func_177229_b(field_176464_a)) {
         var3 |= 8;
      }

      if (var1.func_177229_b(field_176463_b) == BlockRedstoneComparator.Mode.SUBTRACT) {
         var3 |= 4;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176387_N, field_176463_b, field_176464_a});
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.func_176223_P().func_177226_a(field_176387_N, var8.func_174811_aO().func_176734_d()).func_177226_a(field_176464_a, false).func_177226_a(field_176463_b, BlockRedstoneComparator.Mode.COMPARE);
   }

   public static enum Mode implements IStringSerializable {
      COMPARE("compare"),
      SUBTRACT("subtract");

      private final String field_177041_c;

      private Mode(String var3) {
         this.field_177041_c = var3;
      }

      public String toString() {
         return this.field_177041_c;
      }

      public String func_176610_l() {
         return this.field_177041_c;
      }
   }
}
