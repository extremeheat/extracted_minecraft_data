package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BlockSapling extends BlockBush implements IGrowable {
   public static final PropertyEnum<BlockPlanks.EnumType> field_176480_a = PropertyEnum.func_177709_a("type", BlockPlanks.EnumType.class);
   public static final PropertyInteger field_176479_b = PropertyInteger.func_177719_a("stage", 0, 1);

   protected BlockSapling() {
      super();
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176480_a, BlockPlanks.EnumType.OAK).func_177226_a(field_176479_b, 0));
      float var1 = 0.4F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, var1 * 2.0F, 0.5F + var1);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a(this.func_149739_a() + "." + BlockPlanks.EnumType.OAK.func_176840_c() + ".name");
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (!var1.field_72995_K) {
         super.func_180650_b(var1, var2, var3, var4);
         if (var1.func_175671_l(var2.func_177984_a()) >= 9 && var4.nextInt(7) == 0) {
            this.func_176478_d(var1, var2, var3, var4);
         }

      }
   }

   public void func_176478_d(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if ((Integer)var3.func_177229_b(field_176479_b) == 0) {
         var1.func_180501_a(var2, var3.func_177231_a(field_176479_b), 4);
      } else {
         this.func_176476_e(var1, var2, var3, var4);
      }

   }

   public void func_176476_e(World var1, BlockPos var2, IBlockState var3, Random var4) {
      Object var5 = var4.nextInt(10) == 0 ? new WorldGenBigTree(true) : new WorldGenTrees(true);
      int var6 = 0;
      int var7 = 0;
      boolean var8 = false;
      IBlockState var9;
      switch((BlockPlanks.EnumType)var3.func_177229_b(field_176480_a)) {
      case SPRUCE:
         label68:
         for(var6 = 0; var6 >= -1; --var6) {
            for(var7 = 0; var7 >= -1; --var7) {
               if (this.func_181624_a(var1, var2, var6, var7, BlockPlanks.EnumType.SPRUCE)) {
                  var5 = new WorldGenMegaPineTree(false, var4.nextBoolean());
                  var8 = true;
                  break label68;
               }
            }
         }

         if (!var8) {
            var7 = 0;
            var6 = 0;
            var5 = new WorldGenTaiga2(true);
         }
         break;
      case BIRCH:
         var5 = new WorldGenForest(true, false);
         break;
      case JUNGLE:
         var9 = Blocks.field_150364_r.func_176223_P().func_177226_a(BlockOldLog.field_176301_b, BlockPlanks.EnumType.JUNGLE);
         IBlockState var10 = Blocks.field_150362_t.func_176223_P().func_177226_a(BlockOldLeaf.field_176239_P, BlockPlanks.EnumType.JUNGLE).func_177226_a(BlockLeaves.field_176236_b, false);

         label82:
         for(var6 = 0; var6 >= -1; --var6) {
            for(var7 = 0; var7 >= -1; --var7) {
               if (this.func_181624_a(var1, var2, var6, var7, BlockPlanks.EnumType.JUNGLE)) {
                  var5 = new WorldGenMegaJungle(true, 10, 20, var9, var10);
                  var8 = true;
                  break label82;
               }
            }
         }

         if (!var8) {
            var7 = 0;
            var6 = 0;
            var5 = new WorldGenTrees(true, 4 + var4.nextInt(7), var9, var10, false);
         }
         break;
      case ACACIA:
         var5 = new WorldGenSavannaTree(true);
         break;
      case DARK_OAK:
         label96:
         for(var6 = 0; var6 >= -1; --var6) {
            for(var7 = 0; var7 >= -1; --var7) {
               if (this.func_181624_a(var1, var2, var6, var7, BlockPlanks.EnumType.DARK_OAK)) {
                  var5 = new WorldGenCanopyTree(true);
                  var8 = true;
                  break label96;
               }
            }
         }

         if (!var8) {
            return;
         }
      case OAK:
      }

      var9 = Blocks.field_150350_a.func_176223_P();
      if (var8) {
         var1.func_180501_a(var2.func_177982_a(var6, 0, var7), var9, 4);
         var1.func_180501_a(var2.func_177982_a(var6 + 1, 0, var7), var9, 4);
         var1.func_180501_a(var2.func_177982_a(var6, 0, var7 + 1), var9, 4);
         var1.func_180501_a(var2.func_177982_a(var6 + 1, 0, var7 + 1), var9, 4);
      } else {
         var1.func_180501_a(var2, var9, 4);
      }

      if (!((WorldGenerator)var5).func_180709_b(var1, var4, var2.func_177982_a(var6, 0, var7))) {
         if (var8) {
            var1.func_180501_a(var2.func_177982_a(var6, 0, var7), var3, 4);
            var1.func_180501_a(var2.func_177982_a(var6 + 1, 0, var7), var3, 4);
            var1.func_180501_a(var2.func_177982_a(var6, 0, var7 + 1), var3, 4);
            var1.func_180501_a(var2.func_177982_a(var6 + 1, 0, var7 + 1), var3, 4);
         } else {
            var1.func_180501_a(var2, var3, 4);
         }
      }

   }

   private boolean func_181624_a(World var1, BlockPos var2, int var3, int var4, BlockPlanks.EnumType var5) {
      return this.func_176477_a(var1, var2.func_177982_a(var3, 0, var4), var5) && this.func_176477_a(var1, var2.func_177982_a(var3 + 1, 0, var4), var5) && this.func_176477_a(var1, var2.func_177982_a(var3, 0, var4 + 1), var5) && this.func_176477_a(var1, var2.func_177982_a(var3 + 1, 0, var4 + 1), var5);
   }

   public boolean func_176477_a(World var1, BlockPos var2, BlockPlanks.EnumType var3) {
      IBlockState var4 = var1.func_180495_p(var2);
      return var4.func_177230_c() == this && var4.func_177229_b(field_176480_a) == var3;
   }

   public int func_180651_a(IBlockState var1) {
      return ((BlockPlanks.EnumType)var1.func_177229_b(field_176480_a)).func_176839_a();
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      BlockPlanks.EnumType[] var4 = BlockPlanks.EnumType.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         BlockPlanks.EnumType var7 = var4[var6];
         var3.add(new ItemStack(var1, 1, var7.func_176839_a()));
      }

   }

   public boolean func_176473_a(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return true;
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return (double)var1.field_73012_v.nextFloat() < 0.45D;
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      this.func_176478_d(var1, var3, var4, var2);
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176480_a, BlockPlanks.EnumType.func_176837_a(var1 & 7)).func_177226_a(field_176479_b, (var1 & 8) >> 3);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((BlockPlanks.EnumType)var1.func_177229_b(field_176480_a)).func_176839_a();
      var3 |= (Integer)var1.func_177229_b(field_176479_b) << 3;
      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176480_a, field_176479_b});
   }
}
