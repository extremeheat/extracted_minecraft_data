package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFlowerPot extends BlockContainer {
   public static final PropertyInteger field_176444_a = PropertyInteger.func_177719_a("legacy_data", 0, 15);
   public static final PropertyEnum<BlockFlowerPot.EnumFlowerType> field_176443_b = PropertyEnum.func_177709_a("contents", BlockFlowerPot.EnumFlowerType.class);

   public BlockFlowerPot() {
      super(Material.field_151594_q);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176443_b, BlockFlowerPot.EnumFlowerType.EMPTY).func_177226_a(field_176444_a, 0));
      this.func_149683_g();
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a("item.flowerPot.name");
   }

   public void func_149683_g() {
      float var1 = 0.375F;
      float var2 = var1 / 2.0F;
      this.func_149676_a(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, var1, 0.5F + var2);
   }

   public boolean func_149662_c() {
      return false;
   }

   public int func_149645_b() {
      return 3;
   }

   public boolean func_149686_d() {
      return false;
   }

   public int func_180662_a(IBlockAccess var1, BlockPos var2, int var3) {
      TileEntity var4 = var1.func_175625_s(var2);
      if (var4 instanceof TileEntityFlowerPot) {
         Item var5 = ((TileEntityFlowerPot)var4).func_145965_a();
         if (var5 instanceof ItemBlock) {
            return Block.func_149634_a(var5).func_180662_a(var1, var2, var3);
         }
      }

      return 16777215;
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      ItemStack var9 = var4.field_71071_by.func_70448_g();
      if (var9 != null && var9.func_77973_b() instanceof ItemBlock) {
         TileEntityFlowerPot var10 = this.func_176442_d(var1, var2);
         if (var10 == null) {
            return false;
         } else if (var10.func_145965_a() != null) {
            return false;
         } else {
            Block var11 = Block.func_149634_a(var9.func_77973_b());
            if (!this.func_149928_a(var11, var9.func_77960_j())) {
               return false;
            } else {
               var10.func_145964_a(var9.func_77973_b(), var9.func_77960_j());
               var10.func_70296_d();
               var1.func_175689_h(var2);
               var4.func_71029_a(StatList.field_181736_T);
               if (!var4.field_71075_bZ.field_75098_d && --var9.field_77994_a <= 0) {
                  var4.field_71071_by.func_70299_a(var4.field_71071_by.field_70461_c, (ItemStack)null);
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   private boolean func_149928_a(Block var1, int var2) {
      if (var1 != Blocks.field_150327_N && var1 != Blocks.field_150328_O && var1 != Blocks.field_150434_aF && var1 != Blocks.field_150338_P && var1 != Blocks.field_150337_Q && var1 != Blocks.field_150345_g && var1 != Blocks.field_150330_I) {
         return var1 == Blocks.field_150329_H && var2 == BlockTallGrass.EnumType.FERN.func_177044_a();
      } else {
         return true;
      }
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      TileEntityFlowerPot var3 = this.func_176442_d(var1, var2);
      return var3 != null && var3.func_145965_a() != null ? var3.func_145965_a() : Items.field_151162_bE;
   }

   public int func_176222_j(World var1, BlockPos var2) {
      TileEntityFlowerPot var3 = this.func_176442_d(var1, var2);
      return var3 != null && var3.func_145965_a() != null ? var3.func_145966_b() : 0;
   }

   public boolean func_149648_K() {
      return true;
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return super.func_176196_c(var1, var2) && World.func_175683_a(var1, var2.func_177977_b());
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!World.func_175683_a(var1, var2.func_177977_b())) {
         this.func_176226_b(var1, var2, var3, 0);
         var1.func_175698_g(var2);
      }

   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      TileEntityFlowerPot var4 = this.func_176442_d(var1, var2);
      if (var4 != null && var4.func_145965_a() != null) {
         func_180635_a(var1, var2, new ItemStack(var4.func_145965_a(), 1, var4.func_145966_b()));
      }

      super.func_180663_b(var1, var2, var3);
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      super.func_176208_a(var1, var2, var3, var4);
      if (var4.field_71075_bZ.field_75098_d) {
         TileEntityFlowerPot var5 = this.func_176442_d(var1, var2);
         if (var5 != null) {
            var5.func_145964_a((Item)null, 0);
         }
      }

   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_151162_bE;
   }

   private TileEntityFlowerPot func_176442_d(World var1, BlockPos var2) {
      TileEntity var3 = var1.func_175625_s(var2);
      return var3 instanceof TileEntityFlowerPot ? (TileEntityFlowerPot)var3 : null;
   }

   public TileEntity func_149915_a(World var1, int var2) {
      Object var3 = null;
      int var4 = 0;
      switch(var2) {
      case 1:
         var3 = Blocks.field_150328_O;
         var4 = BlockFlower.EnumFlowerType.POPPY.func_176968_b();
         break;
      case 2:
         var3 = Blocks.field_150327_N;
         break;
      case 3:
         var3 = Blocks.field_150345_g;
         var4 = BlockPlanks.EnumType.OAK.func_176839_a();
         break;
      case 4:
         var3 = Blocks.field_150345_g;
         var4 = BlockPlanks.EnumType.SPRUCE.func_176839_a();
         break;
      case 5:
         var3 = Blocks.field_150345_g;
         var4 = BlockPlanks.EnumType.BIRCH.func_176839_a();
         break;
      case 6:
         var3 = Blocks.field_150345_g;
         var4 = BlockPlanks.EnumType.JUNGLE.func_176839_a();
         break;
      case 7:
         var3 = Blocks.field_150337_Q;
         break;
      case 8:
         var3 = Blocks.field_150338_P;
         break;
      case 9:
         var3 = Blocks.field_150434_aF;
         break;
      case 10:
         var3 = Blocks.field_150330_I;
         break;
      case 11:
         var3 = Blocks.field_150329_H;
         var4 = BlockTallGrass.EnumType.FERN.func_177044_a();
         break;
      case 12:
         var3 = Blocks.field_150345_g;
         var4 = BlockPlanks.EnumType.ACACIA.func_176839_a();
         break;
      case 13:
         var3 = Blocks.field_150345_g;
         var4 = BlockPlanks.EnumType.DARK_OAK.func_176839_a();
      }

      return new TileEntityFlowerPot(Item.func_150898_a((Block)var3), var4);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176443_b, field_176444_a});
   }

   public int func_176201_c(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176444_a);
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      BlockFlowerPot.EnumFlowerType var4 = BlockFlowerPot.EnumFlowerType.EMPTY;
      TileEntity var5 = var2.func_175625_s(var3);
      if (var5 instanceof TileEntityFlowerPot) {
         TileEntityFlowerPot var6 = (TileEntityFlowerPot)var5;
         Item var7 = var6.func_145965_a();
         if (var7 instanceof ItemBlock) {
            int var8 = var6.func_145966_b();
            Block var9 = Block.func_149634_a(var7);
            if (var9 == Blocks.field_150345_g) {
               switch(BlockPlanks.EnumType.func_176837_a(var8)) {
               case OAK:
                  var4 = BlockFlowerPot.EnumFlowerType.OAK_SAPLING;
                  break;
               case SPRUCE:
                  var4 = BlockFlowerPot.EnumFlowerType.SPRUCE_SAPLING;
                  break;
               case BIRCH:
                  var4 = BlockFlowerPot.EnumFlowerType.BIRCH_SAPLING;
                  break;
               case JUNGLE:
                  var4 = BlockFlowerPot.EnumFlowerType.JUNGLE_SAPLING;
                  break;
               case ACACIA:
                  var4 = BlockFlowerPot.EnumFlowerType.ACACIA_SAPLING;
                  break;
               case DARK_OAK:
                  var4 = BlockFlowerPot.EnumFlowerType.DARK_OAK_SAPLING;
                  break;
               default:
                  var4 = BlockFlowerPot.EnumFlowerType.EMPTY;
               }
            } else if (var9 == Blocks.field_150329_H) {
               switch(var8) {
               case 0:
                  var4 = BlockFlowerPot.EnumFlowerType.DEAD_BUSH;
                  break;
               case 2:
                  var4 = BlockFlowerPot.EnumFlowerType.FERN;
                  break;
               default:
                  var4 = BlockFlowerPot.EnumFlowerType.EMPTY;
               }
            } else if (var9 == Blocks.field_150327_N) {
               var4 = BlockFlowerPot.EnumFlowerType.DANDELION;
            } else if (var9 == Blocks.field_150328_O) {
               switch(BlockFlower.EnumFlowerType.func_176967_a(BlockFlower.EnumFlowerColor.RED, var8)) {
               case POPPY:
                  var4 = BlockFlowerPot.EnumFlowerType.POPPY;
                  break;
               case BLUE_ORCHID:
                  var4 = BlockFlowerPot.EnumFlowerType.BLUE_ORCHID;
                  break;
               case ALLIUM:
                  var4 = BlockFlowerPot.EnumFlowerType.ALLIUM;
                  break;
               case HOUSTONIA:
                  var4 = BlockFlowerPot.EnumFlowerType.HOUSTONIA;
                  break;
               case RED_TULIP:
                  var4 = BlockFlowerPot.EnumFlowerType.RED_TULIP;
                  break;
               case ORANGE_TULIP:
                  var4 = BlockFlowerPot.EnumFlowerType.ORANGE_TULIP;
                  break;
               case WHITE_TULIP:
                  var4 = BlockFlowerPot.EnumFlowerType.WHITE_TULIP;
                  break;
               case PINK_TULIP:
                  var4 = BlockFlowerPot.EnumFlowerType.PINK_TULIP;
                  break;
               case OXEYE_DAISY:
                  var4 = BlockFlowerPot.EnumFlowerType.OXEYE_DAISY;
                  break;
               default:
                  var4 = BlockFlowerPot.EnumFlowerType.EMPTY;
               }
            } else if (var9 == Blocks.field_150337_Q) {
               var4 = BlockFlowerPot.EnumFlowerType.MUSHROOM_RED;
            } else if (var9 == Blocks.field_150338_P) {
               var4 = BlockFlowerPot.EnumFlowerType.MUSHROOM_BROWN;
            } else if (var9 == Blocks.field_150330_I) {
               var4 = BlockFlowerPot.EnumFlowerType.DEAD_BUSH;
            } else if (var9 == Blocks.field_150434_aF) {
               var4 = BlockFlowerPot.EnumFlowerType.CACTUS;
            }
         }
      }

      return var1.func_177226_a(field_176443_b, var4);
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public static enum EnumFlowerType implements IStringSerializable {
      EMPTY("empty"),
      POPPY("rose"),
      BLUE_ORCHID("blue_orchid"),
      ALLIUM("allium"),
      HOUSTONIA("houstonia"),
      RED_TULIP("red_tulip"),
      ORANGE_TULIP("orange_tulip"),
      WHITE_TULIP("white_tulip"),
      PINK_TULIP("pink_tulip"),
      OXEYE_DAISY("oxeye_daisy"),
      DANDELION("dandelion"),
      OAK_SAPLING("oak_sapling"),
      SPRUCE_SAPLING("spruce_sapling"),
      BIRCH_SAPLING("birch_sapling"),
      JUNGLE_SAPLING("jungle_sapling"),
      ACACIA_SAPLING("acacia_sapling"),
      DARK_OAK_SAPLING("dark_oak_sapling"),
      MUSHROOM_RED("mushroom_red"),
      MUSHROOM_BROWN("mushroom_brown"),
      DEAD_BUSH("dead_bush"),
      FERN("fern"),
      CACTUS("cactus");

      private final String field_177006_w;

      private EnumFlowerType(String var3) {
         this.field_177006_w = var3;
      }

      public String toString() {
         return this.field_177006_w;
      }

      public String func_176610_l() {
         return this.field_177006_w;
      }
   }
}
