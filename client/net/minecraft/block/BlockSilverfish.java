package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockSilverfish extends Block {
   public static final PropertyEnum<BlockSilverfish.EnumType> field_176378_a = PropertyEnum.func_177709_a("variant", BlockSilverfish.EnumType.class);

   public BlockSilverfish() {
      super(Material.field_151571_B);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176378_a, BlockSilverfish.EnumType.STONE));
      this.func_149711_c(0.0F);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   public int func_149745_a(Random var1) {
      return 0;
   }

   public static boolean func_176377_d(IBlockState var0) {
      Block var1 = var0.func_177230_c();
      return var0 == Blocks.field_150348_b.func_176223_P().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.STONE) || var1 == Blocks.field_150347_e || var1 == Blocks.field_150417_aV;
   }

   protected ItemStack func_180643_i(IBlockState var1) {
      switch((BlockSilverfish.EnumType)var1.func_177229_b(field_176378_a)) {
      case COBBLESTONE:
         return new ItemStack(Blocks.field_150347_e);
      case STONEBRICK:
         return new ItemStack(Blocks.field_150417_aV);
      case MOSSY_STONEBRICK:
         return new ItemStack(Blocks.field_150417_aV, 1, BlockStoneBrick.EnumType.MOSSY.func_176612_a());
      case CRACKED_STONEBRICK:
         return new ItemStack(Blocks.field_150417_aV, 1, BlockStoneBrick.EnumType.CRACKED.func_176612_a());
      case CHISELED_STONEBRICK:
         return new ItemStack(Blocks.field_150417_aV, 1, BlockStoneBrick.EnumType.CHISELED.func_176612_a());
      default:
         return new ItemStack(Blocks.field_150348_b);
      }
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      if (!var1.field_72995_K && var1.func_82736_K().func_82766_b("doTileDrops")) {
         EntitySilverfish var6 = new EntitySilverfish(var1);
         var6.func_70012_b((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o(), (double)var2.func_177952_p() + 0.5D, 0.0F, 0.0F);
         var1.func_72838_d(var6);
         var6.func_70656_aK();
      }

   }

   public int func_176222_j(World var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      return var3.func_177230_c().func_176201_c(var3);
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      BlockSilverfish.EnumType[] var4 = BlockSilverfish.EnumType.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         BlockSilverfish.EnumType var7 = var4[var6];
         var3.add(new ItemStack(var1, 1, var7.func_176881_a()));
      }

   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176378_a, BlockSilverfish.EnumType.func_176879_a(var1));
   }

   public int func_176201_c(IBlockState var1) {
      return ((BlockSilverfish.EnumType)var1.func_177229_b(field_176378_a)).func_176881_a();
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176378_a});
   }

   public static enum EnumType implements IStringSerializable {
      STONE(0, "stone") {
         public IBlockState func_176883_d() {
            return Blocks.field_150348_b.func_176223_P().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.STONE);
         }
      },
      COBBLESTONE(1, "cobblestone", "cobble") {
         public IBlockState func_176883_d() {
            return Blocks.field_150347_e.func_176223_P();
         }
      },
      STONEBRICK(2, "stone_brick", "brick") {
         public IBlockState func_176883_d() {
            return Blocks.field_150417_aV.func_176223_P().func_177226_a(BlockStoneBrick.field_176249_a, BlockStoneBrick.EnumType.DEFAULT);
         }
      },
      MOSSY_STONEBRICK(3, "mossy_brick", "mossybrick") {
         public IBlockState func_176883_d() {
            return Blocks.field_150417_aV.func_176223_P().func_177226_a(BlockStoneBrick.field_176249_a, BlockStoneBrick.EnumType.MOSSY);
         }
      },
      CRACKED_STONEBRICK(4, "cracked_brick", "crackedbrick") {
         public IBlockState func_176883_d() {
            return Blocks.field_150417_aV.func_176223_P().func_177226_a(BlockStoneBrick.field_176249_a, BlockStoneBrick.EnumType.CRACKED);
         }
      },
      CHISELED_STONEBRICK(5, "chiseled_brick", "chiseledbrick") {
         public IBlockState func_176883_d() {
            return Blocks.field_150417_aV.func_176223_P().func_177226_a(BlockStoneBrick.field_176249_a, BlockStoneBrick.EnumType.CHISELED);
         }
      };

      private static final BlockSilverfish.EnumType[] field_176885_g = new BlockSilverfish.EnumType[values().length];
      private final int field_176893_h;
      private final String field_176894_i;
      private final String field_176891_j;

      private EnumType(int var3, String var4) {
         this(var3, var4, var4);
      }

      private EnumType(int var3, String var4, String var5) {
         this.field_176893_h = var3;
         this.field_176894_i = var4;
         this.field_176891_j = var5;
      }

      public int func_176881_a() {
         return this.field_176893_h;
      }

      public String toString() {
         return this.field_176894_i;
      }

      public static BlockSilverfish.EnumType func_176879_a(int var0) {
         if (var0 < 0 || var0 >= field_176885_g.length) {
            var0 = 0;
         }

         return field_176885_g[var0];
      }

      public String func_176610_l() {
         return this.field_176894_i;
      }

      public String func_176882_c() {
         return this.field_176891_j;
      }

      public abstract IBlockState func_176883_d();

      public static BlockSilverfish.EnumType func_176878_a(IBlockState var0) {
         BlockSilverfish.EnumType[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            BlockSilverfish.EnumType var4 = var1[var3];
            if (var0 == var4.func_176883_d()) {
               return var4;
            }
         }

         return STONE;
      }

      // $FF: synthetic method
      EnumType(int var3, String var4, Object var5) {
         this(var3, var4);
      }

      // $FF: synthetic method
      EnumType(int var3, String var4, String var5, Object var6) {
         this(var3, var4, var5);
      }

      static {
         BlockSilverfish.EnumType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockSilverfish.EnumType var3 = var0[var2];
            field_176885_g[var3.func_176881_a()] = var3;
         }

      }
   }
}
