package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BlockBed extends BlockDirectional {
   public static final PropertyEnum<BlockBed.EnumPartType> field_176472_a = PropertyEnum.func_177709_a("part", BlockBed.EnumPartType.class);
   public static final PropertyBool field_176471_b = PropertyBool.func_177716_a("occupied");

   public BlockBed() {
      super(Material.field_151580_n);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176472_a, BlockBed.EnumPartType.FOOT).func_177226_a(field_176471_b, false));
      this.func_149978_e();
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.field_72995_K) {
         return true;
      } else {
         if (var3.func_177229_b(field_176472_a) != BlockBed.EnumPartType.HEAD) {
            var2 = var2.func_177972_a((EnumFacing)var3.func_177229_b(field_176387_N));
            var3 = var1.func_180495_p(var2);
            if (var3.func_177230_c() != this) {
               return true;
            }
         }

         if (var1.field_73011_w.func_76567_e() && var1.func_180494_b(var2) != BiomeGenBase.field_76778_j) {
            if ((Boolean)var3.func_177229_b(field_176471_b)) {
               EntityPlayer var10 = this.func_176470_e(var1, var2);
               if (var10 != null) {
                  var4.func_146105_b(new ChatComponentTranslation("tile.bed.occupied", new Object[0]));
                  return true;
               }

               var3 = var3.func_177226_a(field_176471_b, false);
               var1.func_180501_a(var2, var3, 4);
            }

            EntityPlayer.EnumStatus var11 = var4.func_180469_a(var2);
            if (var11 == EntityPlayer.EnumStatus.OK) {
               var3 = var3.func_177226_a(field_176471_b, true);
               var1.func_180501_a(var2, var3, 4);
               return true;
            } else {
               if (var11 == EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW) {
                  var4.func_146105_b(new ChatComponentTranslation("tile.bed.noSleep", new Object[0]));
               } else if (var11 == EntityPlayer.EnumStatus.NOT_SAFE) {
                  var4.func_146105_b(new ChatComponentTranslation("tile.bed.notSafe", new Object[0]));
               }

               return true;
            }
         } else {
            var1.func_175698_g(var2);
            BlockPos var9 = var2.func_177972_a(((EnumFacing)var3.func_177229_b(field_176387_N)).func_176734_d());
            if (var1.func_180495_p(var9).func_177230_c() == this) {
               var1.func_175698_g(var9);
            }

            var1.func_72885_a((Entity)null, (double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D, 5.0F, true, true);
            return true;
         }
      }
   }

   private EntityPlayer func_176470_e(World var1, BlockPos var2) {
      Iterator var3 = var1.field_73010_i.iterator();

      EntityPlayer var4;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         var4 = (EntityPlayer)var3.next();
      } while(!var4.func_70608_bn() || !var4.field_71081_bT.equals(var2));

      return var4;
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_149662_c() {
      return false;
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      this.func_149978_e();
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      EnumFacing var5 = (EnumFacing)var3.func_177229_b(field_176387_N);
      if (var3.func_177229_b(field_176472_a) == BlockBed.EnumPartType.HEAD) {
         if (var1.func_180495_p(var2.func_177972_a(var5.func_176734_d())).func_177230_c() != this) {
            var1.func_175698_g(var2);
         }
      } else if (var1.func_180495_p(var2.func_177972_a(var5)).func_177230_c() != this) {
         var1.func_175698_g(var2);
         if (!var1.field_72995_K) {
            this.func_176226_b(var1, var2, var3, 0);
         }
      }

   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return var1.func_177229_b(field_176472_a) == BlockBed.EnumPartType.HEAD ? null : Items.field_151104_aV;
   }

   private void func_149978_e() {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
   }

   public static BlockPos func_176468_a(World var0, BlockPos var1, int var2) {
      EnumFacing var3 = (EnumFacing)var0.func_180495_p(var1).func_177229_b(field_176387_N);
      int var4 = var1.func_177958_n();
      int var5 = var1.func_177956_o();
      int var6 = var1.func_177952_p();

      for(int var7 = 0; var7 <= 1; ++var7) {
         int var8 = var4 - var3.func_82601_c() * var7 - 1;
         int var9 = var6 - var3.func_82599_e() * var7 - 1;
         int var10 = var8 + 2;
         int var11 = var9 + 2;

         for(int var12 = var8; var12 <= var10; ++var12) {
            for(int var13 = var9; var13 <= var11; ++var13) {
               BlockPos var14 = new BlockPos(var12, var5, var13);
               if (func_176469_d(var0, var14)) {
                  if (var2 <= 0) {
                     return var14;
                  }

                  --var2;
               }
            }
         }
      }

      return null;
   }

   protected static boolean func_176469_d(World var0, BlockPos var1) {
      return World.func_175683_a(var0, var1.func_177977_b()) && !var0.func_180495_p(var1).func_177230_c().func_149688_o().func_76220_a() && !var0.func_180495_p(var1.func_177984_a()).func_177230_c().func_149688_o().func_76220_a();
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      if (var3.func_177229_b(field_176472_a) == BlockBed.EnumPartType.FOOT) {
         super.func_180653_a(var1, var2, var3, var4, 0);
      }

   }

   public int func_149656_h() {
      return 1;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Items.field_151104_aV;
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (var4.field_71075_bZ.field_75098_d && var3.func_177229_b(field_176472_a) == BlockBed.EnumPartType.HEAD) {
         BlockPos var5 = var2.func_177972_a(((EnumFacing)var3.func_177229_b(field_176387_N)).func_176734_d());
         if (var1.func_180495_p(var5).func_177230_c() == this) {
            var1.func_175698_g(var5);
         }
      }

   }

   public IBlockState func_176203_a(int var1) {
      EnumFacing var2 = EnumFacing.func_176731_b(var1);
      return (var1 & 8) > 0 ? this.func_176223_P().func_177226_a(field_176472_a, BlockBed.EnumPartType.HEAD).func_177226_a(field_176387_N, var2).func_177226_a(field_176471_b, (var1 & 4) > 0) : this.func_176223_P().func_177226_a(field_176472_a, BlockBed.EnumPartType.FOOT).func_177226_a(field_176387_N, var2);
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      if (var1.func_177229_b(field_176472_a) == BlockBed.EnumPartType.FOOT) {
         IBlockState var4 = var2.func_180495_p(var3.func_177972_a((EnumFacing)var1.func_177229_b(field_176387_N)));
         if (var4.func_177230_c() == this) {
            var1 = var1.func_177226_a(field_176471_b, var4.func_177229_b(field_176471_b));
         }
      }

      return var1;
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((EnumFacing)var1.func_177229_b(field_176387_N)).func_176736_b();
      if (var1.func_177229_b(field_176472_a) == BlockBed.EnumPartType.HEAD) {
         var3 |= 8;
         if ((Boolean)var1.func_177229_b(field_176471_b)) {
            var3 |= 4;
         }
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176387_N, field_176472_a, field_176471_b});
   }

   public static enum EnumPartType implements IStringSerializable {
      HEAD("head"),
      FOOT("foot");

      private final String field_177036_c;

      private EnumPartType(String var3) {
         this.field_177036_c = var3;
      }

      public String toString() {
         return this.field_177036_c;
      }

      public String func_176610_l() {
         return this.field_177036_c;
      }
   }
}
