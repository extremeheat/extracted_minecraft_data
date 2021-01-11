package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class StructureVillagePieces {
   public static void func_143016_a() {
      MapGenStructureIO.func_143031_a(StructureVillagePieces.House1.class, "ViBH");
      MapGenStructureIO.func_143031_a(StructureVillagePieces.Field1.class, "ViDF");
      MapGenStructureIO.func_143031_a(StructureVillagePieces.Field2.class, "ViF");
      MapGenStructureIO.func_143031_a(StructureVillagePieces.Torch.class, "ViL");
      MapGenStructureIO.func_143031_a(StructureVillagePieces.Hall.class, "ViPH");
      MapGenStructureIO.func_143031_a(StructureVillagePieces.House4Garden.class, "ViSH");
      MapGenStructureIO.func_143031_a(StructureVillagePieces.WoodHut.class, "ViSmH");
      MapGenStructureIO.func_143031_a(StructureVillagePieces.Church.class, "ViST");
      MapGenStructureIO.func_143031_a(StructureVillagePieces.House2.class, "ViS");
      MapGenStructureIO.func_143031_a(StructureVillagePieces.Start.class, "ViStart");
      MapGenStructureIO.func_143031_a(StructureVillagePieces.Path.class, "ViSR");
      MapGenStructureIO.func_143031_a(StructureVillagePieces.House3.class, "ViTRH");
      MapGenStructureIO.func_143031_a(StructureVillagePieces.Well.class, "ViW");
   }

   public static List<StructureVillagePieces.PieceWeight> func_75084_a(Random var0, int var1) {
      ArrayList var2 = Lists.newArrayList();
      var2.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House4Garden.class, 4, MathHelper.func_76136_a(var0, 2 + var1, 4 + var1 * 2)));
      var2.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Church.class, 20, MathHelper.func_76136_a(var0, 0 + var1, 1 + var1)));
      var2.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House1.class, 20, MathHelper.func_76136_a(var0, 0 + var1, 2 + var1)));
      var2.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.WoodHut.class, 3, MathHelper.func_76136_a(var0, 2 + var1, 5 + var1 * 3)));
      var2.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Hall.class, 15, MathHelper.func_76136_a(var0, 0 + var1, 2 + var1)));
      var2.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Field1.class, 3, MathHelper.func_76136_a(var0, 1 + var1, 4 + var1)));
      var2.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Field2.class, 3, MathHelper.func_76136_a(var0, 2 + var1, 4 + var1 * 2)));
      var2.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House2.class, 15, MathHelper.func_76136_a(var0, 0, 1 + var1)));
      var2.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House3.class, 8, MathHelper.func_76136_a(var0, 0 + var1, 3 + var1 * 2)));
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         if (((StructureVillagePieces.PieceWeight)var3.next()).field_75087_d == 0) {
            var3.remove();
         }
      }

      return var2;
   }

   private static int func_75079_a(List<StructureVillagePieces.PieceWeight> var0) {
      boolean var1 = false;
      int var2 = 0;

      StructureVillagePieces.PieceWeight var4;
      for(Iterator var3 = var0.iterator(); var3.hasNext(); var2 += var4.field_75088_b) {
         var4 = (StructureVillagePieces.PieceWeight)var3.next();
         if (var4.field_75087_d > 0 && var4.field_75089_c < var4.field_75087_d) {
            var1 = true;
         }
      }

      return var1 ? var2 : -1;
   }

   private static StructureVillagePieces.Village func_176065_a(StructureVillagePieces.Start var0, StructureVillagePieces.PieceWeight var1, List<StructureComponent> var2, Random var3, int var4, int var5, int var6, EnumFacing var7, int var8) {
      Class var9 = var1.field_75090_a;
      Object var10 = null;
      if (var9 == StructureVillagePieces.House4Garden.class) {
         var10 = StructureVillagePieces.House4Garden.func_175858_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == StructureVillagePieces.Church.class) {
         var10 = StructureVillagePieces.Church.func_175854_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == StructureVillagePieces.House1.class) {
         var10 = StructureVillagePieces.House1.func_175850_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == StructureVillagePieces.WoodHut.class) {
         var10 = StructureVillagePieces.WoodHut.func_175853_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == StructureVillagePieces.Hall.class) {
         var10 = StructureVillagePieces.Hall.func_175857_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == StructureVillagePieces.Field1.class) {
         var10 = StructureVillagePieces.Field1.func_175851_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == StructureVillagePieces.Field2.class) {
         var10 = StructureVillagePieces.Field2.func_175852_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == StructureVillagePieces.House2.class) {
         var10 = StructureVillagePieces.House2.func_175855_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == StructureVillagePieces.House3.class) {
         var10 = StructureVillagePieces.House3.func_175849_a(var0, var2, var3, var4, var5, var6, var7, var8);
      }

      return (StructureVillagePieces.Village)var10;
   }

   private static StructureVillagePieces.Village func_176067_c(StructureVillagePieces.Start var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
      int var8 = func_75079_a(var0.field_74931_h);
      if (var8 <= 0) {
         return null;
      } else {
         int var9 = 0;

         while(var9 < 5) {
            ++var9;
            int var10 = var2.nextInt(var8);
            Iterator var11 = var0.field_74931_h.iterator();

            while(var11.hasNext()) {
               StructureVillagePieces.PieceWeight var12 = (StructureVillagePieces.PieceWeight)var11.next();
               var10 -= var12.field_75088_b;
               if (var10 < 0) {
                  if (!var12.func_75085_a(var7) || var12 == var0.field_74926_d && var0.field_74931_h.size() > 1) {
                     break;
                  }

                  StructureVillagePieces.Village var13 = func_176065_a(var0, var12, var1, var2, var3, var4, var5, var6, var7);
                  if (var13 != null) {
                     ++var12.field_75089_c;
                     var0.field_74926_d = var12;
                     if (!var12.func_75086_a()) {
                        var0.field_74931_h.remove(var12);
                     }

                     return var13;
                  }
               }
            }
         }

         StructureBoundingBox var14 = StructureVillagePieces.Torch.func_175856_a(var0, var1, var2, var3, var4, var5, var6);
         if (var14 != null) {
            return new StructureVillagePieces.Torch(var0, var7, var2, var14, var6);
         } else {
            return null;
         }
      }
   }

   private static StructureComponent func_176066_d(StructureVillagePieces.Start var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
      if (var7 > 50) {
         return null;
      } else if (Math.abs(var3 - var0.func_74874_b().field_78897_a) <= 112 && Math.abs(var5 - var0.func_74874_b().field_78896_c) <= 112) {
         StructureVillagePieces.Village var8 = func_176067_c(var0, var1, var2, var3, var4, var5, var6, var7 + 1);
         if (var8 != null) {
            int var9 = (var8.field_74887_e.field_78897_a + var8.field_74887_e.field_78893_d) / 2;
            int var10 = (var8.field_74887_e.field_78896_c + var8.field_74887_e.field_78892_f) / 2;
            int var11 = var8.field_74887_e.field_78893_d - var8.field_74887_e.field_78897_a;
            int var12 = var8.field_74887_e.field_78892_f - var8.field_74887_e.field_78896_c;
            int var13 = var11 > var12 ? var11 : var12;
            if (var0.func_74925_d().func_76940_a(var9, var10, var13 / 2 + 4, MapGenVillage.field_75055_e)) {
               var1.add(var8);
               var0.field_74932_i.add(var8);
               return var8;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static StructureComponent func_176069_e(StructureVillagePieces.Start var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
      if (var7 > 3 + var0.field_74928_c) {
         return null;
      } else if (Math.abs(var3 - var0.func_74874_b().field_78897_a) <= 112 && Math.abs(var5 - var0.func_74874_b().field_78896_c) <= 112) {
         StructureBoundingBox var8 = StructureVillagePieces.Path.func_175848_a(var0, var1, var2, var3, var4, var5, var6);
         if (var8 != null && var8.field_78895_b > 10) {
            StructureVillagePieces.Path var9 = new StructureVillagePieces.Path(var0, var7, var2, var8, var6);
            int var10 = (var9.field_74887_e.field_78897_a + var9.field_74887_e.field_78893_d) / 2;
            int var11 = (var9.field_74887_e.field_78896_c + var9.field_74887_e.field_78892_f) / 2;
            int var12 = var9.field_74887_e.field_78893_d - var9.field_74887_e.field_78897_a;
            int var13 = var9.field_74887_e.field_78892_f - var9.field_74887_e.field_78896_c;
            int var14 = var12 > var13 ? var12 : var13;
            if (var0.func_74925_d().func_76940_a(var10, var11, var14 / 2 + 4, MapGenVillage.field_75055_e)) {
               var1.add(var9);
               var0.field_74930_j.add(var9);
               return var9;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static class Torch extends StructureVillagePieces.Village {
      public Torch() {
         super();
      }

      public Torch(StructureVillagePieces.Start var1, int var2, Random var3, StructureBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.field_74885_f = var5;
         this.field_74887_e = var4;
      }

      public static StructureBoundingBox func_175856_a(StructureVillagePieces.Start var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 3, 4, 2, var6);
         return StructureComponent.func_74883_a(var1, var7) != null ? null : var7;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 4 - 1, 0);
         }

         this.func_175804_a(var1, var3, 0, 0, 0, 2, 3, 1, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 1, 0, 0, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 1, 1, 0, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 1, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_150325_L.func_176203_a(EnumDyeColor.WHITE.func_176767_b()), 1, 3, 0, var3);
         boolean var4 = this.field_74885_f == EnumFacing.EAST || this.field_74885_f == EnumFacing.NORTH;
         this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, this.field_74885_f.func_176746_e()), var4 ? 2 : 0, 3, 0, var3);
         this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, this.field_74885_f), 1, 3, 1, var3);
         this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, this.field_74885_f.func_176735_f()), var4 ? 0 : 2, 3, 0, var3);
         this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, this.field_74885_f.func_176734_d()), 1, 3, -1, var3);
         return true;
      }
   }

   public static class Field1 extends StructureVillagePieces.Village {
      private Block field_82679_b;
      private Block field_82680_c;
      private Block field_82678_d;
      private Block field_82681_h;

      public Field1() {
         super();
      }

      public Field1(StructureVillagePieces.Start var1, int var2, Random var3, StructureBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.field_74885_f = var5;
         this.field_74887_e = var4;
         this.field_82679_b = this.func_151559_a(var3);
         this.field_82680_c = this.func_151559_a(var3);
         this.field_82678_d = this.func_151559_a(var3);
         this.field_82681_h = this.func_151559_a(var3);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74768_a("CA", Block.field_149771_c.func_148757_b(this.field_82679_b));
         var1.func_74768_a("CB", Block.field_149771_c.func_148757_b(this.field_82680_c));
         var1.func_74768_a("CC", Block.field_149771_c.func_148757_b(this.field_82678_d));
         var1.func_74768_a("CD", Block.field_149771_c.func_148757_b(this.field_82681_h));
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_82679_b = Block.func_149729_e(var1.func_74762_e("CA"));
         this.field_82680_c = Block.func_149729_e(var1.func_74762_e("CB"));
         this.field_82678_d = Block.func_149729_e(var1.func_74762_e("CC"));
         this.field_82681_h = Block.func_149729_e(var1.func_74762_e("CD"));
      }

      private Block func_151559_a(Random var1) {
         switch(var1.nextInt(5)) {
         case 0:
            return Blocks.field_150459_bM;
         case 1:
            return Blocks.field_150469_bN;
         default:
            return Blocks.field_150464_aj;
         }
      }

      public static StructureVillagePieces.Field1 func_175851_a(StructureVillagePieces.Start var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 13, 4, 9, var6);
         return func_74895_a(var8) && StructureComponent.func_74883_a(var1, var8) == null ? new StructureVillagePieces.Field1(var0, var7, var2, var8, var6) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 4 - 1, 0);
         }

         this.func_175804_a(var1, var3, 0, 1, 0, 12, 4, 8, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 1, 2, 0, 7, Blocks.field_150458_ak.func_176223_P(), Blocks.field_150458_ak.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 0, 1, 5, 0, 7, Blocks.field_150458_ak.func_176223_P(), Blocks.field_150458_ak.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 0, 1, 8, 0, 7, Blocks.field_150458_ak.func_176223_P(), Blocks.field_150458_ak.func_176223_P(), false);
         this.func_175804_a(var1, var3, 10, 0, 1, 11, 0, 7, Blocks.field_150458_ak.func_176223_P(), Blocks.field_150458_ak.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 0, 0, 0, 8, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 0, 0, 6, 0, 8, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 12, 0, 0, 12, 0, 8, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 0, 11, 0, 0, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 8, 11, 0, 8, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 3, 0, 1, 3, 0, 7, Blocks.field_150355_j.func_176223_P(), Blocks.field_150355_j.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, 0, 1, 9, 0, 7, Blocks.field_150355_j.func_176223_P(), Blocks.field_150355_j.func_176223_P(), false);

         int var4;
         for(var4 = 1; var4 <= 7; ++var4) {
            this.func_175811_a(var1, this.field_82679_b.func_176203_a(MathHelper.func_76136_a(var2, 2, 7)), 1, 1, var4, var3);
            this.func_175811_a(var1, this.field_82679_b.func_176203_a(MathHelper.func_76136_a(var2, 2, 7)), 2, 1, var4, var3);
            this.func_175811_a(var1, this.field_82680_c.func_176203_a(MathHelper.func_76136_a(var2, 2, 7)), 4, 1, var4, var3);
            this.func_175811_a(var1, this.field_82680_c.func_176203_a(MathHelper.func_76136_a(var2, 2, 7)), 5, 1, var4, var3);
            this.func_175811_a(var1, this.field_82678_d.func_176203_a(MathHelper.func_76136_a(var2, 2, 7)), 7, 1, var4, var3);
            this.func_175811_a(var1, this.field_82678_d.func_176203_a(MathHelper.func_76136_a(var2, 2, 7)), 8, 1, var4, var3);
            this.func_175811_a(var1, this.field_82681_h.func_176203_a(MathHelper.func_76136_a(var2, 2, 7)), 10, 1, var4, var3);
            this.func_175811_a(var1, this.field_82681_h.func_176203_a(MathHelper.func_76136_a(var2, 2, 7)), 11, 1, var4, var3);
         }

         for(var4 = 0; var4 < 9; ++var4) {
            for(int var5 = 0; var5 < 13; ++var5) {
               this.func_74871_b(var1, var5, 4, var4, var3);
               this.func_175808_b(var1, Blocks.field_150346_d.func_176223_P(), var5, -1, var4, var3);
            }
         }

         return true;
      }
   }

   public static class Field2 extends StructureVillagePieces.Village {
      private Block field_82675_b;
      private Block field_82676_c;

      public Field2() {
         super();
      }

      public Field2(StructureVillagePieces.Start var1, int var2, Random var3, StructureBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.field_74885_f = var5;
         this.field_74887_e = var4;
         this.field_82675_b = this.func_151560_a(var3);
         this.field_82676_c = this.func_151560_a(var3);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74768_a("CA", Block.field_149771_c.func_148757_b(this.field_82675_b));
         var1.func_74768_a("CB", Block.field_149771_c.func_148757_b(this.field_82676_c));
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_82675_b = Block.func_149729_e(var1.func_74762_e("CA"));
         this.field_82676_c = Block.func_149729_e(var1.func_74762_e("CB"));
      }

      private Block func_151560_a(Random var1) {
         switch(var1.nextInt(5)) {
         case 0:
            return Blocks.field_150459_bM;
         case 1:
            return Blocks.field_150469_bN;
         default:
            return Blocks.field_150464_aj;
         }
      }

      public static StructureVillagePieces.Field2 func_175852_a(StructureVillagePieces.Start var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 7, 4, 9, var6);
         return func_74895_a(var8) && StructureComponent.func_74883_a(var1, var8) == null ? new StructureVillagePieces.Field2(var0, var7, var2, var8, var6) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 4 - 1, 0);
         }

         this.func_175804_a(var1, var3, 0, 1, 0, 6, 4, 8, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 1, 2, 0, 7, Blocks.field_150458_ak.func_176223_P(), Blocks.field_150458_ak.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 0, 1, 5, 0, 7, Blocks.field_150458_ak.func_176223_P(), Blocks.field_150458_ak.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 0, 0, 0, 8, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 0, 0, 6, 0, 8, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 0, 5, 0, 0, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 8, 5, 0, 8, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 3, 0, 1, 3, 0, 7, Blocks.field_150355_j.func_176223_P(), Blocks.field_150355_j.func_176223_P(), false);

         int var4;
         for(var4 = 1; var4 <= 7; ++var4) {
            this.func_175811_a(var1, this.field_82675_b.func_176203_a(MathHelper.func_76136_a(var2, 2, 7)), 1, 1, var4, var3);
            this.func_175811_a(var1, this.field_82675_b.func_176203_a(MathHelper.func_76136_a(var2, 2, 7)), 2, 1, var4, var3);
            this.func_175811_a(var1, this.field_82676_c.func_176203_a(MathHelper.func_76136_a(var2, 2, 7)), 4, 1, var4, var3);
            this.func_175811_a(var1, this.field_82676_c.func_176203_a(MathHelper.func_76136_a(var2, 2, 7)), 5, 1, var4, var3);
         }

         for(var4 = 0; var4 < 9; ++var4) {
            for(int var5 = 0; var5 < 7; ++var5) {
               this.func_74871_b(var1, var5, 4, var4, var3);
               this.func_175808_b(var1, Blocks.field_150346_d.func_176223_P(), var5, -1, var4, var3);
            }
         }

         return true;
      }
   }

   public static class House2 extends StructureVillagePieces.Village {
      private static final List<WeightedRandomChestContent> field_74918_a;
      private boolean field_74917_c;

      public House2() {
         super();
      }

      public House2(StructureVillagePieces.Start var1, int var2, Random var3, StructureBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.field_74885_f = var5;
         this.field_74887_e = var4;
      }

      public static StructureVillagePieces.House2 func_175855_a(StructureVillagePieces.Start var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 10, 6, 7, var6);
         return func_74895_a(var8) && StructureComponent.func_74883_a(var1, var8) == null ? new StructureVillagePieces.House2(var0, var7, var2, var8, var6) : null;
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Chest", this.field_74917_c);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_74917_c = var1.func_74767_n("Chest");
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 6 - 1, 0);
         }

         this.func_175804_a(var1, var3, 0, 1, 0, 9, 4, 6, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 0, 9, 0, 6, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 4, 0, 9, 4, 6, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 0, 9, 5, 6, Blocks.field_150333_U.func_176223_P(), Blocks.field_150333_U.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 5, 1, 8, 5, 5, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 1, 0, 2, 3, 0, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 1, 0, 0, 4, 0, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 3, 1, 0, 3, 4, 0, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 1, 6, 0, 4, 6, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 3, 3, 1, var3);
         this.func_175804_a(var1, var3, 3, 1, 2, 3, 3, 2, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 1, 3, 5, 3, 3, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 1, 1, 0, 3, 5, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 1, 6, 5, 3, 6, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 1, 0, 5, 3, 0, Blocks.field_180407_aO.func_176223_P(), Blocks.field_180407_aO.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, 1, 0, 9, 3, 0, Blocks.field_180407_aO.func_176223_P(), Blocks.field_180407_aO.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 1, 4, 9, 4, 6, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150356_k.func_176223_P(), 7, 1, 5, var3);
         this.func_175811_a(var1, Blocks.field_150356_k.func_176223_P(), 8, 1, 5, var3);
         this.func_175811_a(var1, Blocks.field_150411_aY.func_176223_P(), 9, 2, 5, var3);
         this.func_175811_a(var1, Blocks.field_150411_aY.func_176223_P(), 9, 2, 4, var3);
         this.func_175804_a(var1, var3, 7, 2, 4, 8, 2, 5, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 6, 1, 3, var3);
         this.func_175811_a(var1, Blocks.field_150460_al.func_176223_P(), 6, 2, 3, var3);
         this.func_175811_a(var1, Blocks.field_150460_al.func_176223_P(), 6, 3, 3, var3);
         this.func_175811_a(var1, Blocks.field_150334_T.func_176223_P(), 8, 1, 1, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 2, 4, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 2, 2, 6, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 4, 2, 6, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 2, 1, 4, var3);
         this.func_175811_a(var1, Blocks.field_150452_aw.func_176223_P(), 2, 2, 4, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 1, 1, 5, var3);
         this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(this.func_151555_a(Blocks.field_150476_ad, 3)), 2, 1, 5, var3);
         this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(this.func_151555_a(Blocks.field_150476_ad, 1)), 1, 1, 4, var3);
         if (!this.field_74917_c && var3.func_175898_b(new BlockPos(this.func_74865_a(5, 5), this.func_74862_a(1), this.func_74873_b(5, 5)))) {
            this.field_74917_c = true;
            this.func_180778_a(var1, var3, var2, 5, 1, 5, field_74918_a, 3 + var2.nextInt(6));
         }

         int var4;
         for(var4 = 6; var4 <= 8; ++var4) {
            if (this.func_175807_a(var1, var4, 0, -1, var3).func_177230_c().func_149688_o() == Material.field_151579_a && this.func_175807_a(var1, var4, -1, -1, var3).func_177230_c().func_149688_o() != Material.field_151579_a) {
               this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(this.func_151555_a(Blocks.field_150446_ar, 3)), var4, 0, -1, var3);
            }
         }

         for(var4 = 0; var4 < 7; ++var4) {
            for(int var5 = 0; var5 < 10; ++var5) {
               this.func_74871_b(var1, var5, 6, var4, var3);
               this.func_175808_b(var1, Blocks.field_150347_e.func_176223_P(), var5, -1, var4, var3);
            }
         }

         this.func_74893_a(var1, var3, 7, 1, 1, 1);
         return true;
      }

      protected int func_180779_c(int var1, int var2) {
         return 3;
      }

      static {
         field_74918_a = Lists.newArrayList(new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151045_i, 0, 1, 3, 3), new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 10), new WeightedRandomChestContent(Items.field_151043_k, 0, 1, 3, 5), new WeightedRandomChestContent(Items.field_151025_P, 0, 1, 3, 15), new WeightedRandomChestContent(Items.field_151034_e, 0, 1, 3, 15), new WeightedRandomChestContent(Items.field_151035_b, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151040_l, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151030_Z, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151028_Y, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151165_aa, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151167_ab, 0, 1, 1, 5), new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150343_Z), 0, 3, 7, 5), new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150345_g), 0, 3, 7, 5), new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 3), new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151136_bY, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151125_bZ, 0, 1, 1, 1)});
      }
   }

   public static class House3 extends StructureVillagePieces.Village {
      public House3() {
         super();
      }

      public House3(StructureVillagePieces.Start var1, int var2, Random var3, StructureBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.field_74885_f = var5;
         this.field_74887_e = var4;
      }

      public static StructureVillagePieces.House3 func_175849_a(StructureVillagePieces.Start var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 9, 7, 12, var6);
         return func_74895_a(var8) && StructureComponent.func_74883_a(var1, var8) == null ? new StructureVillagePieces.House3(var0, var7, var2, var8, var6) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 7 - 1, 0);
         }

         this.func_175804_a(var1, var3, 1, 1, 1, 7, 4, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 1, 6, 8, 4, 10, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 0, 5, 8, 0, 10, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 1, 7, 0, 4, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 0, 0, 3, 5, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 0, 0, 8, 3, 10, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 0, 7, 2, 0, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 5, 2, 1, 5, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 0, 6, 2, 3, 10, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 3, 0, 10, 7, 3, 10, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 0, 7, 3, 0, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 5, 2, 3, 5, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 4, 1, 8, 4, 1, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 4, 4, 3, 4, 4, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 2, 8, 5, 3, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 0, 4, 2, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 0, 4, 3, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 8, 4, 2, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 8, 4, 3, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 8, 4, 4, var3);
         int var4 = this.func_151555_a(Blocks.field_150476_ad, 3);
         int var5 = this.func_151555_a(Blocks.field_150476_ad, 2);

         int var6;
         int var7;
         for(var6 = -1; var6 <= 2; ++var6) {
            for(var7 = 0; var7 <= 8; ++var7) {
               this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(var4), var7, 4 + var6, var6, var3);
               if ((var6 > -1 || var7 <= 1) && (var6 > 0 || var7 <= 3) && (var6 > 1 || var7 <= 4 || var7 >= 6)) {
                  this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(var5), var7, 4 + var6, 5 - var6, var3);
               }
            }
         }

         this.func_175804_a(var1, var3, 3, 4, 5, 3, 4, 10, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 4, 2, 7, 4, 10, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 5, 4, 4, 5, 10, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 5, 4, 6, 5, 10, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 6, 3, 5, 6, 10, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         var6 = this.func_151555_a(Blocks.field_150476_ad, 0);

         int var8;
         for(var7 = 4; var7 >= 1; --var7) {
            this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), var7, 2 + var7, 7 - var7, var3);

            for(var8 = 8 - var7; var8 <= 10; ++var8) {
               this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(var6), var7, 2 + var7, var8, var3);
            }
         }

         var7 = this.func_151555_a(Blocks.field_150476_ad, 1);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 6, 6, 3, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 7, 5, 4, var3);
         this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(var7), 6, 6, 4, var3);

         int var9;
         for(var8 = 6; var8 <= 8; ++var8) {
            for(var9 = 5; var9 <= 10; ++var9) {
               this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(var7), var8, 12 - var8, var9, var3);
            }
         }

         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 0, 2, 1, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 0, 2, 4, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 2, 3, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 4, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 5, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 6, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 8, 2, 1, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 8, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 8, 2, 3, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 8, 2, 4, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 8, 2, 5, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 8, 2, 6, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 8, 2, 7, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 8, 2, 8, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 8, 2, 9, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 2, 2, 6, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 2, 2, 7, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 2, 2, 8, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 2, 2, 9, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 4, 4, 10, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 5, 4, 10, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 6, 4, 10, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 5, 5, 10, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 1, 0, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, this.field_74885_f), 2, 3, 1, var3);
         this.func_175810_a(var1, var3, var2, 2, 1, 0, EnumFacing.func_176731_b(this.func_151555_a(Blocks.field_180413_ao, 1)));
         this.func_175804_a(var1, var3, 1, 0, -1, 3, 2, -1, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         if (this.func_175807_a(var1, 2, 0, -1, var3).func_177230_c().func_149688_o() == Material.field_151579_a && this.func_175807_a(var1, 2, -1, -1, var3).func_177230_c().func_149688_o() != Material.field_151579_a) {
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(this.func_151555_a(Blocks.field_150446_ar, 3)), 2, 0, -1, var3);
         }

         for(var8 = 0; var8 < 5; ++var8) {
            for(var9 = 0; var9 < 9; ++var9) {
               this.func_74871_b(var1, var9, 7, var8, var3);
               this.func_175808_b(var1, Blocks.field_150347_e.func_176223_P(), var9, -1, var8, var3);
            }
         }

         for(var8 = 5; var8 < 11; ++var8) {
            for(var9 = 2; var9 < 9; ++var9) {
               this.func_74871_b(var1, var9, 7, var8, var3);
               this.func_175808_b(var1, Blocks.field_150347_e.func_176223_P(), var9, -1, var8, var3);
            }
         }

         this.func_74893_a(var1, var3, 4, 1, 2, 2);
         return true;
      }
   }

   public static class Hall extends StructureVillagePieces.Village {
      public Hall() {
         super();
      }

      public Hall(StructureVillagePieces.Start var1, int var2, Random var3, StructureBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.field_74885_f = var5;
         this.field_74887_e = var4;
      }

      public static StructureVillagePieces.Hall func_175857_a(StructureVillagePieces.Start var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 9, 7, 11, var6);
         return func_74895_a(var8) && StructureComponent.func_74883_a(var1, var8) == null ? new StructureVillagePieces.Hall(var0, var7, var2, var8, var6) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 7 - 1, 0);
         }

         this.func_175804_a(var1, var3, 1, 1, 1, 7, 4, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 1, 6, 8, 4, 10, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 0, 6, 8, 0, 10, Blocks.field_150346_d.func_176223_P(), Blocks.field_150346_d.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 6, 0, 6, var3);
         this.func_175804_a(var1, var3, 2, 1, 6, 2, 1, 10, Blocks.field_180407_aO.func_176223_P(), Blocks.field_180407_aO.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 1, 6, 8, 1, 10, Blocks.field_180407_aO.func_176223_P(), Blocks.field_180407_aO.func_176223_P(), false);
         this.func_175804_a(var1, var3, 3, 1, 10, 7, 1, 10, Blocks.field_180407_aO.func_176223_P(), Blocks.field_180407_aO.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 1, 7, 0, 4, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 0, 0, 3, 5, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 0, 0, 8, 3, 5, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 0, 7, 1, 0, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 5, 7, 1, 5, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 0, 7, 3, 0, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 5, 7, 3, 5, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 4, 1, 8, 4, 1, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 4, 4, 8, 4, 4, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 2, 8, 5, 3, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 0, 4, 2, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 0, 4, 3, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 8, 4, 2, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 8, 4, 3, var3);
         int var4 = this.func_151555_a(Blocks.field_150476_ad, 3);
         int var5 = this.func_151555_a(Blocks.field_150476_ad, 2);

         int var6;
         int var7;
         for(var6 = -1; var6 <= 2; ++var6) {
            for(var7 = 0; var7 <= 8; ++var7) {
               this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(var4), var7, 4 + var6, var6, var3);
               this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(var5), var7, 4 + var6, 5 - var6, var3);
            }
         }

         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 0, 2, 1, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 0, 2, 4, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 8, 2, 1, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 8, 2, 4, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 2, 3, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 8, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 8, 2, 3, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 2, 2, 5, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 3, 2, 5, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 5, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 6, 2, 5, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 2, 1, 3, var3);
         this.func_175811_a(var1, Blocks.field_150452_aw.func_176223_P(), 2, 2, 3, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 1, 1, 4, var3);
         this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(this.func_151555_a(Blocks.field_150476_ad, 3)), 2, 1, 4, var3);
         this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(this.func_151555_a(Blocks.field_150476_ad, 1)), 1, 1, 3, var3);
         this.func_175804_a(var1, var3, 5, 0, 1, 7, 0, 3, Blocks.field_150334_T.func_176223_P(), Blocks.field_150334_T.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150334_T.func_176223_P(), 6, 1, 1, var3);
         this.func_175811_a(var1, Blocks.field_150334_T.func_176223_P(), 6, 1, 2, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 1, 0, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, this.field_74885_f), 2, 3, 1, var3);
         this.func_175810_a(var1, var3, var2, 2, 1, 0, EnumFacing.func_176731_b(this.func_151555_a(Blocks.field_180413_ao, 1)));
         if (this.func_175807_a(var1, 2, 0, -1, var3).func_177230_c().func_149688_o() == Material.field_151579_a && this.func_175807_a(var1, 2, -1, -1, var3).func_177230_c().func_149688_o() != Material.field_151579_a) {
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(this.func_151555_a(Blocks.field_150446_ar, 3)), 2, 0, -1, var3);
         }

         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 6, 1, 5, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 6, 2, 5, var3);
         this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, this.field_74885_f.func_176734_d()), 6, 3, 4, var3);
         this.func_175810_a(var1, var3, var2, 6, 1, 5, EnumFacing.func_176731_b(this.func_151555_a(Blocks.field_180413_ao, 1)));

         for(var6 = 0; var6 < 5; ++var6) {
            for(var7 = 0; var7 < 9; ++var7) {
               this.func_74871_b(var1, var7, 7, var6, var3);
               this.func_175808_b(var1, Blocks.field_150347_e.func_176223_P(), var7, -1, var6, var3);
            }
         }

         this.func_74893_a(var1, var3, 4, 1, 2, 2);
         return true;
      }

      protected int func_180779_c(int var1, int var2) {
         return var1 == 0 ? 4 : super.func_180779_c(var1, var2);
      }
   }

   public static class WoodHut extends StructureVillagePieces.Village {
      private boolean field_74909_b;
      private int field_74910_c;

      public WoodHut() {
         super();
      }

      public WoodHut(StructureVillagePieces.Start var1, int var2, Random var3, StructureBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.field_74885_f = var5;
         this.field_74887_e = var4;
         this.field_74909_b = var3.nextBoolean();
         this.field_74910_c = var3.nextInt(3);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74768_a("T", this.field_74910_c);
         var1.func_74757_a("C", this.field_74909_b);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_74910_c = var1.func_74762_e("T");
         this.field_74909_b = var1.func_74767_n("C");
      }

      public static StructureVillagePieces.WoodHut func_175853_a(StructureVillagePieces.Start var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 4, 6, 5, var6);
         return func_74895_a(var8) && StructureComponent.func_74883_a(var1, var8) == null ? new StructureVillagePieces.WoodHut(var0, var7, var2, var8, var6) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 6 - 1, 0);
         }

         this.func_175804_a(var1, var3, 1, 1, 1, 3, 5, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 0, 3, 0, 4, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 1, 2, 0, 3, Blocks.field_150346_d.func_176223_P(), Blocks.field_150346_d.func_176223_P(), false);
         if (this.field_74909_b) {
            this.func_175804_a(var1, var3, 1, 4, 1, 2, 4, 3, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         } else {
            this.func_175804_a(var1, var3, 1, 5, 1, 2, 5, 3, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         }

         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 1, 4, 0, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 2, 4, 0, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 1, 4, 4, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 2, 4, 4, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 0, 4, 1, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 0, 4, 2, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 0, 4, 3, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 3, 4, 1, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 3, 4, 2, var3);
         this.func_175811_a(var1, Blocks.field_150364_r.func_176223_P(), 3, 4, 3, var3);
         this.func_175804_a(var1, var3, 0, 1, 0, 0, 3, 0, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 3, 1, 0, 3, 3, 0, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 1, 4, 0, 3, 4, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 3, 1, 4, 3, 3, 4, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 1, 1, 0, 3, 3, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 3, 1, 1, 3, 3, 3, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 1, 0, 2, 3, 0, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 1, 4, 2, 3, 4, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 3, 2, 2, var3);
         if (this.field_74910_c > 0) {
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), this.field_74910_c, 1, 3, var3);
            this.func_175811_a(var1, Blocks.field_150452_aw.func_176223_P(), this.field_74910_c, 2, 3, var3);
         }

         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 1, 1, 0, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 1, 2, 0, var3);
         this.func_175810_a(var1, var3, var2, 1, 1, 0, EnumFacing.func_176731_b(this.func_151555_a(Blocks.field_180413_ao, 1)));
         if (this.func_175807_a(var1, 1, 0, -1, var3).func_177230_c().func_149688_o() == Material.field_151579_a && this.func_175807_a(var1, 1, -1, -1, var3).func_177230_c().func_149688_o() != Material.field_151579_a) {
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(this.func_151555_a(Blocks.field_150446_ar, 3)), 1, 0, -1, var3);
         }

         for(int var4 = 0; var4 < 5; ++var4) {
            for(int var5 = 0; var5 < 4; ++var5) {
               this.func_74871_b(var1, var5, 6, var4, var3);
               this.func_175808_b(var1, Blocks.field_150347_e.func_176223_P(), var5, -1, var4, var3);
            }
         }

         this.func_74893_a(var1, var3, 1, 1, 2, 1);
         return true;
      }
   }

   public static class House1 extends StructureVillagePieces.Village {
      public House1() {
         super();
      }

      public House1(StructureVillagePieces.Start var1, int var2, Random var3, StructureBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.field_74885_f = var5;
         this.field_74887_e = var4;
      }

      public static StructureVillagePieces.House1 func_175850_a(StructureVillagePieces.Start var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 9, 9, 6, var6);
         return func_74895_a(var8) && StructureComponent.func_74883_a(var1, var8) == null ? new StructureVillagePieces.House1(var0, var7, var2, var8, var6) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 9 - 1, 0);
         }

         this.func_175804_a(var1, var3, 1, 1, 1, 7, 5, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 0, 8, 0, 5, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 0, 8, 5, 5, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 6, 1, 8, 6, 4, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 7, 2, 8, 7, 3, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         int var4 = this.func_151555_a(Blocks.field_150476_ad, 3);
         int var5 = this.func_151555_a(Blocks.field_150476_ad, 2);

         int var6;
         int var7;
         for(var6 = -1; var6 <= 2; ++var6) {
            for(var7 = 0; var7 <= 8; ++var7) {
               this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(var4), var7, 6 + var6, var6, var3);
               this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(var5), var7, 6 + var6, 5 - var6, var3);
            }
         }

         this.func_175804_a(var1, var3, 0, 1, 0, 0, 1, 5, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 1, 5, 8, 1, 5, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 1, 0, 8, 1, 4, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 1, 0, 7, 1, 0, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 0, 4, 0, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 5, 0, 4, 5, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 2, 5, 8, 4, 5, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 2, 0, 8, 4, 0, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 1, 0, 4, 4, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 5, 7, 4, 5, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 2, 1, 8, 4, 4, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 0, 7, 4, 0, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 4, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 5, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 6, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 4, 3, 0, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 5, 3, 0, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 6, 3, 0, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 2, 3, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 3, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 3, 3, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 8, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 8, 2, 3, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 8, 3, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 8, 3, 3, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 2, 2, 5, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 3, 2, 5, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 5, 2, 5, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 6, 2, 5, var3);
         this.func_175804_a(var1, var3, 1, 4, 1, 7, 4, 1, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 4, 4, 7, 4, 4, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 3, 4, 7, 3, 4, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 7, 1, 4, var3);
         this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(this.func_151555_a(Blocks.field_150476_ad, 0)), 7, 1, 3, var3);
         var6 = this.func_151555_a(Blocks.field_150476_ad, 3);
         this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(var6), 6, 1, 4, var3);
         this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(var6), 5, 1, 4, var3);
         this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(var6), 4, 1, 4, var3);
         this.func_175811_a(var1, Blocks.field_150476_ad.func_176203_a(var6), 3, 1, 4, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 6, 1, 3, var3);
         this.func_175811_a(var1, Blocks.field_150452_aw.func_176223_P(), 6, 2, 3, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 4, 1, 3, var3);
         this.func_175811_a(var1, Blocks.field_150452_aw.func_176223_P(), 4, 2, 3, var3);
         this.func_175811_a(var1, Blocks.field_150462_ai.func_176223_P(), 7, 1, 1, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 1, 1, 0, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 1, 2, 0, var3);
         this.func_175810_a(var1, var3, var2, 1, 1, 0, EnumFacing.func_176731_b(this.func_151555_a(Blocks.field_180413_ao, 1)));
         if (this.func_175807_a(var1, 1, 0, -1, var3).func_177230_c().func_149688_o() == Material.field_151579_a && this.func_175807_a(var1, 1, -1, -1, var3).func_177230_c().func_149688_o() != Material.field_151579_a) {
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(this.func_151555_a(Blocks.field_150446_ar, 3)), 1, 0, -1, var3);
         }

         for(var7 = 0; var7 < 6; ++var7) {
            for(int var8 = 0; var8 < 9; ++var8) {
               this.func_74871_b(var1, var8, 9, var7, var3);
               this.func_175808_b(var1, Blocks.field_150347_e.func_176223_P(), var8, -1, var7, var3);
            }
         }

         this.func_74893_a(var1, var3, 2, 1, 2, 1);
         return true;
      }

      protected int func_180779_c(int var1, int var2) {
         return 1;
      }
   }

   public static class Church extends StructureVillagePieces.Village {
      public Church() {
         super();
      }

      public Church(StructureVillagePieces.Start var1, int var2, Random var3, StructureBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.field_74885_f = var5;
         this.field_74887_e = var4;
      }

      public static StructureVillagePieces.Church func_175854_a(StructureVillagePieces.Start var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 5, 12, 9, var6);
         return func_74895_a(var8) && StructureComponent.func_74883_a(var1, var8) == null ? new StructureVillagePieces.Church(var0, var7, var2, var8, var6) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 12 - 1, 0);
         }

         this.func_175804_a(var1, var3, 1, 1, 1, 3, 3, 7, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 5, 1, 3, 9, 3, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 0, 3, 0, 8, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 1, 0, 3, 10, 0, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 1, 1, 0, 10, 3, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 1, 1, 4, 10, 3, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 4, 0, 4, 7, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 0, 4, 4, 4, 7, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 1, 8, 3, 4, 8, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 5, 4, 3, 10, 4, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 5, 5, 3, 5, 7, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 9, 0, 4, 9, 4, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 4, 0, 4, 4, 4, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 0, 11, 2, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 4, 11, 2, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 2, 11, 0, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 2, 11, 4, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 1, 1, 6, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 1, 1, 7, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 2, 1, 7, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 3, 1, 6, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 3, 1, 7, var3);
         this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(this.func_151555_a(Blocks.field_150446_ar, 3)), 1, 1, 5, var3);
         this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(this.func_151555_a(Blocks.field_150446_ar, 3)), 2, 1, 6, var3);
         this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(this.func_151555_a(Blocks.field_150446_ar, 3)), 3, 1, 5, var3);
         this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(this.func_151555_a(Blocks.field_150446_ar, 1)), 1, 2, 7, var3);
         this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(this.func_151555_a(Blocks.field_150446_ar, 0)), 3, 2, 7, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 3, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 4, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 4, 3, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 6, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 7, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 4, 6, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 4, 7, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 2, 6, 0, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 2, 7, 0, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 2, 6, 4, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 2, 7, 4, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 3, 6, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 4, 3, 6, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 2, 3, 8, var3);
         this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, this.field_74885_f.func_176734_d()), 2, 4, 7, var3);
         this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, this.field_74885_f.func_176746_e()), 1, 4, 6, var3);
         this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, this.field_74885_f.func_176735_f()), 3, 4, 6, var3);
         this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, this.field_74885_f), 2, 4, 5, var3);
         int var4 = this.func_151555_a(Blocks.field_150468_ap, 4);

         int var5;
         for(var5 = 1; var5 <= 9; ++var5) {
            this.func_175811_a(var1, Blocks.field_150468_ap.func_176203_a(var4), 3, var5, 3, var3);
         }

         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 1, 0, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 2, 0, var3);
         this.func_175810_a(var1, var3, var2, 2, 1, 0, EnumFacing.func_176731_b(this.func_151555_a(Blocks.field_180413_ao, 1)));
         if (this.func_175807_a(var1, 2, 0, -1, var3).func_177230_c().func_149688_o() == Material.field_151579_a && this.func_175807_a(var1, 2, -1, -1, var3).func_177230_c().func_149688_o() != Material.field_151579_a) {
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(this.func_151555_a(Blocks.field_150446_ar, 3)), 2, 0, -1, var3);
         }

         for(var5 = 0; var5 < 9; ++var5) {
            for(int var6 = 0; var6 < 5; ++var6) {
               this.func_74871_b(var1, var6, 12, var5, var3);
               this.func_175808_b(var1, Blocks.field_150347_e.func_176223_P(), var6, -1, var5, var3);
            }
         }

         this.func_74893_a(var1, var3, 2, 1, 2, 1);
         return true;
      }

      protected int func_180779_c(int var1, int var2) {
         return 2;
      }
   }

   public static class House4Garden extends StructureVillagePieces.Village {
      private boolean field_74913_b;

      public House4Garden() {
         super();
      }

      public House4Garden(StructureVillagePieces.Start var1, int var2, Random var3, StructureBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.field_74885_f = var5;
         this.field_74887_e = var4;
         this.field_74913_b = var3.nextBoolean();
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Terrace", this.field_74913_b);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_74913_b = var1.func_74767_n("Terrace");
      }

      public static StructureVillagePieces.House4Garden func_175858_a(StructureVillagePieces.Start var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 5, 6, 5, var6);
         return StructureComponent.func_74883_a(var1, var8) != null ? null : new StructureVillagePieces.House4Garden(var0, var7, var2, var8, var6);
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 6 - 1, 0);
         }

         this.func_175804_a(var1, var3, 0, 0, 0, 4, 0, 4, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 4, 0, 4, 4, 4, Blocks.field_150364_r.func_176223_P(), Blocks.field_150364_r.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 4, 1, 3, 4, 3, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 0, 1, 0, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 0, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 0, 3, 0, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 4, 1, 0, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 4, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 4, 3, 0, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 0, 1, 4, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 0, 2, 4, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 0, 3, 4, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 4, 1, 4, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 4, 2, 4, var3);
         this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 4, 3, 4, var3);
         this.func_175804_a(var1, var3, 0, 1, 1, 0, 3, 3, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 1, 1, 4, 3, 3, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 1, 4, 3, 3, 4, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 0, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 2, 2, 4, var3);
         this.func_175811_a(var1, Blocks.field_150410_aZ.func_176223_P(), 4, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 1, 1, 0, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 1, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 1, 3, 0, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 2, 3, 0, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 3, 3, 0, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 3, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 3, 1, 0, var3);
         if (this.func_175807_a(var1, 2, 0, -1, var3).func_177230_c().func_149688_o() == Material.field_151579_a && this.func_175807_a(var1, 2, -1, -1, var3).func_177230_c().func_149688_o() != Material.field_151579_a) {
            this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(this.func_151555_a(Blocks.field_150446_ar, 3)), 2, 0, -1, var3);
         }

         this.func_175804_a(var1, var3, 1, 1, 1, 3, 3, 3, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         if (this.field_74913_b) {
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 0, 5, 0, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 1, 5, 0, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 2, 5, 0, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 3, 5, 0, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 4, 5, 0, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 0, 5, 4, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 1, 5, 4, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 2, 5, 4, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 3, 5, 4, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 4, 5, 4, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 4, 5, 1, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 4, 5, 2, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 4, 5, 3, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 0, 5, 1, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 0, 5, 2, var3);
            this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 0, 5, 3, var3);
         }

         int var4;
         if (this.field_74913_b) {
            var4 = this.func_151555_a(Blocks.field_150468_ap, 3);
            this.func_175811_a(var1, Blocks.field_150468_ap.func_176203_a(var4), 3, 1, 3, var3);
            this.func_175811_a(var1, Blocks.field_150468_ap.func_176203_a(var4), 3, 2, 3, var3);
            this.func_175811_a(var1, Blocks.field_150468_ap.func_176203_a(var4), 3, 3, 3, var3);
            this.func_175811_a(var1, Blocks.field_150468_ap.func_176203_a(var4), 3, 4, 3, var3);
         }

         this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P().func_177226_a(BlockTorch.field_176596_a, this.field_74885_f), 2, 3, 1, var3);

         for(var4 = 0; var4 < 5; ++var4) {
            for(int var5 = 0; var5 < 5; ++var5) {
               this.func_74871_b(var1, var5, 6, var4, var3);
               this.func_175808_b(var1, Blocks.field_150347_e.func_176223_P(), var5, -1, var4, var3);
            }
         }

         this.func_74893_a(var1, var3, 1, 1, 2, 1);
         return true;
      }
   }

   public static class Path extends StructureVillagePieces.Road {
      private int field_74934_a;

      public Path() {
         super();
      }

      public Path(StructureVillagePieces.Start var1, int var2, Random var3, StructureBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.field_74885_f = var5;
         this.field_74887_e = var4;
         this.field_74934_a = Math.max(var4.func_78883_b(), var4.func_78880_d());
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74768_a("Length", this.field_74934_a);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_74934_a = var1.func_74762_e("Length");
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         boolean var4 = false;

         int var5;
         StructureComponent var6;
         for(var5 = var3.nextInt(5); var5 < this.field_74934_a - 8; var5 += 2 + var3.nextInt(5)) {
            var6 = this.func_74891_a((StructureVillagePieces.Start)var1, var2, var3, 0, var5);
            if (var6 != null) {
               var5 += Math.max(var6.field_74887_e.func_78883_b(), var6.field_74887_e.func_78880_d());
               var4 = true;
            }
         }

         for(var5 = var3.nextInt(5); var5 < this.field_74934_a - 8; var5 += 2 + var3.nextInt(5)) {
            var6 = this.func_74894_b((StructureVillagePieces.Start)var1, var2, var3, 0, var5);
            if (var6 != null) {
               var5 += Math.max(var6.field_74887_e.func_78883_b(), var6.field_74887_e.func_78880_d());
               var4 = true;
            }
         }

         if (var4 && var3.nextInt(3) > 0 && this.field_74885_f != null) {
            switch(this.field_74885_f) {
            case NORTH:
               StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, EnumFacing.WEST, this.func_74877_c());
               break;
            case SOUTH:
               StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f - 2, EnumFacing.WEST, this.func_74877_c());
               break;
            case WEST:
               StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c());
               break;
            case EAST:
               StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78893_d - 2, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c());
            }
         }

         if (var4 && var3.nextInt(3) > 0 && this.field_74885_f != null) {
            switch(this.field_74885_f) {
            case NORTH:
               StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, EnumFacing.EAST, this.func_74877_c());
               break;
            case SOUTH:
               StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f - 2, EnumFacing.EAST, this.func_74877_c());
               break;
            case WEST:
               StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c());
               break;
            case EAST:
               StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78893_d - 2, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c());
            }
         }

      }

      public static StructureBoundingBox func_175848_a(StructureVillagePieces.Start var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6) {
         for(int var7 = 7 * MathHelper.func_76136_a(var2, 3, 5); var7 >= 7; var7 -= 7) {
            StructureBoundingBox var8 = StructureBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 3, 3, var7, var6);
            if (StructureComponent.func_74883_a(var1, var8) == null) {
               return var8;
            }
         }

         return null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         IBlockState var4 = this.func_175847_a(Blocks.field_150351_n.func_176223_P());
         IBlockState var5 = this.func_175847_a(Blocks.field_150347_e.func_176223_P());

         for(int var6 = this.field_74887_e.field_78897_a; var6 <= this.field_74887_e.field_78893_d; ++var6) {
            for(int var7 = this.field_74887_e.field_78896_c; var7 <= this.field_74887_e.field_78892_f; ++var7) {
               BlockPos var8 = new BlockPos(var6, 64, var7);
               if (var3.func_175898_b(var8)) {
                  var8 = var1.func_175672_r(var8).func_177977_b();
                  var1.func_180501_a(var8, var4, 2);
                  var1.func_180501_a(var8.func_177977_b(), var5, 2);
               }
            }
         }

         return true;
      }
   }

   public abstract static class Road extends StructureVillagePieces.Village {
      public Road() {
         super();
      }

      protected Road(StructureVillagePieces.Start var1, int var2) {
         super(var1, var2);
      }
   }

   public static class Start extends StructureVillagePieces.Well {
      public WorldChunkManager field_74929_a;
      public boolean field_74927_b;
      public int field_74928_c;
      public StructureVillagePieces.PieceWeight field_74926_d;
      public List<StructureVillagePieces.PieceWeight> field_74931_h;
      public List<StructureComponent> field_74932_i = Lists.newArrayList();
      public List<StructureComponent> field_74930_j = Lists.newArrayList();

      public Start() {
         super();
      }

      public Start(WorldChunkManager var1, int var2, Random var3, int var4, int var5, List<StructureVillagePieces.PieceWeight> var6, int var7) {
         super((StructureVillagePieces.Start)null, 0, var3, var4, var5);
         this.field_74929_a = var1;
         this.field_74931_h = var6;
         this.field_74928_c = var7;
         BiomeGenBase var8 = var1.func_180300_a(new BlockPos(var4, 0, var5), BiomeGenBase.field_180279_ad);
         this.field_74927_b = var8 == BiomeGenBase.field_76769_d || var8 == BiomeGenBase.field_76786_s;
         this.func_175846_a(this.field_74927_b);
      }

      public WorldChunkManager func_74925_d() {
         return this.field_74929_a;
      }
   }

   public static class Well extends StructureVillagePieces.Village {
      public Well() {
         super();
      }

      public Well(StructureVillagePieces.Start var1, int var2, Random var3, int var4, int var5) {
         super(var1, var2);
         this.field_74885_f = EnumFacing.Plane.HORIZONTAL.func_179518_a(var3);
         switch(this.field_74885_f) {
         case NORTH:
         case SOUTH:
            this.field_74887_e = new StructureBoundingBox(var4, 64, var5, var4 + 6 - 1, 78, var5 + 6 - 1);
            break;
         default:
            this.field_74887_e = new StructureBoundingBox(var4, 64, var5, var4 + 6 - 1, 78, var5 + 6 - 1);
         }

      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78894_e - 4, this.field_74887_e.field_78896_c + 1, EnumFacing.WEST, this.func_74877_c());
         StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78894_e - 4, this.field_74887_e.field_78896_c + 1, EnumFacing.EAST, this.func_74877_c());
         StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78894_e - 4, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c());
         StructureVillagePieces.func_176069_e((StructureVillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78894_e - 4, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c());
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 3, 0);
         }

         this.func_175804_a(var1, var3, 1, 0, 1, 4, 12, 4, Blocks.field_150347_e.func_176223_P(), Blocks.field_150358_i.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 12, 2, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 3, 12, 2, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 12, 3, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 3, 12, 3, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 1, 13, 1, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 1, 14, 1, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 4, 13, 1, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 4, 14, 1, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 1, 13, 4, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 1, 14, 4, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 4, 13, 4, var3);
         this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 4, 14, 4, var3);
         this.func_175804_a(var1, var3, 1, 15, 1, 4, 15, 4, Blocks.field_150347_e.func_176223_P(), Blocks.field_150347_e.func_176223_P(), false);

         for(int var4 = 0; var4 <= 5; ++var4) {
            for(int var5 = 0; var5 <= 5; ++var5) {
               if (var5 == 0 || var5 == 5 || var4 == 0 || var4 == 5) {
                  this.func_175811_a(var1, Blocks.field_150351_n.func_176223_P(), var5, 11, var4, var3);
                  this.func_74871_b(var1, var5, 12, var4, var3);
               }
            }
         }

         return true;
      }
   }

   abstract static class Village extends StructureComponent {
      protected int field_143015_k = -1;
      private int field_74896_a;
      private boolean field_143014_b;

      public Village() {
         super();
      }

      protected Village(StructureVillagePieces.Start var1, int var2) {
         super(var2);
         if (var1 != null) {
            this.field_143014_b = var1.field_74927_b;
         }

      }

      protected void func_143012_a(NBTTagCompound var1) {
         var1.func_74768_a("HPos", this.field_143015_k);
         var1.func_74768_a("VCount", this.field_74896_a);
         var1.func_74757_a("Desert", this.field_143014_b);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         this.field_143015_k = var1.func_74762_e("HPos");
         this.field_74896_a = var1.func_74762_e("VCount");
         this.field_143014_b = var1.func_74767_n("Desert");
      }

      protected StructureComponent func_74891_a(StructureVillagePieces.Start var1, List<StructureComponent> var2, Random var3, int var4, int var5) {
         if (this.field_74885_f != null) {
            switch(this.field_74885_f) {
            case NORTH:
               return StructureVillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.WEST, this.func_74877_c());
            case SOUTH:
               return StructureVillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.WEST, this.func_74877_c());
            case WEST:
               return StructureVillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c());
            case EAST:
               return StructureVillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c());
            }
         }

         return null;
      }

      protected StructureComponent func_74894_b(StructureVillagePieces.Start var1, List<StructureComponent> var2, Random var3, int var4, int var5) {
         if (this.field_74885_f != null) {
            switch(this.field_74885_f) {
            case NORTH:
               return StructureVillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.EAST, this.func_74877_c());
            case SOUTH:
               return StructureVillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.EAST, this.func_74877_c());
            case WEST:
               return StructureVillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c());
            case EAST:
               return StructureVillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c());
            }
         }

         return null;
      }

      protected int func_74889_b(World var1, StructureBoundingBox var2) {
         int var3 = 0;
         int var4 = 0;
         BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

         for(int var6 = this.field_74887_e.field_78896_c; var6 <= this.field_74887_e.field_78892_f; ++var6) {
            for(int var7 = this.field_74887_e.field_78897_a; var7 <= this.field_74887_e.field_78893_d; ++var7) {
               var5.func_181079_c(var7, 64, var6);
               if (var2.func_175898_b(var5)) {
                  var3 += Math.max(var1.func_175672_r(var5).func_177956_o(), var1.field_73011_w.func_76557_i());
                  ++var4;
               }
            }
         }

         if (var4 == 0) {
            return -1;
         } else {
            return var3 / var4;
         }
      }

      protected static boolean func_74895_a(StructureBoundingBox var0) {
         return var0 != null && var0.field_78895_b > 10;
      }

      protected void func_74893_a(World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6) {
         if (this.field_74896_a < var6) {
            for(int var7 = this.field_74896_a; var7 < var6; ++var7) {
               int var8 = this.func_74865_a(var3 + var7, var5);
               int var9 = this.func_74862_a(var4);
               int var10 = this.func_74873_b(var3 + var7, var5);
               if (!var2.func_175898_b(new BlockPos(var8, var9, var10))) {
                  break;
               }

               ++this.field_74896_a;
               EntityVillager var11 = new EntityVillager(var1);
               var11.func_70012_b((double)var8 + 0.5D, (double)var9, (double)var10 + 0.5D, 0.0F, 0.0F);
               var11.func_180482_a(var1.func_175649_E(new BlockPos(var11)), (IEntityLivingData)null);
               var11.func_70938_b(this.func_180779_c(var7, var11.func_70946_n()));
               var1.func_72838_d(var11);
            }

         }
      }

      protected int func_180779_c(int var1, int var2) {
         return var2;
      }

      protected IBlockState func_175847_a(IBlockState var1) {
         if (this.field_143014_b) {
            if (var1.func_177230_c() == Blocks.field_150364_r || var1.func_177230_c() == Blocks.field_150363_s) {
               return Blocks.field_150322_A.func_176223_P();
            }

            if (var1.func_177230_c() == Blocks.field_150347_e) {
               return Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.DEFAULT.func_176675_a());
            }

            if (var1.func_177230_c() == Blocks.field_150344_f) {
               return Blocks.field_150322_A.func_176203_a(BlockSandStone.EnumType.SMOOTH.func_176675_a());
            }

            if (var1.func_177230_c() == Blocks.field_150476_ad) {
               return Blocks.field_150372_bz.func_176223_P().func_177226_a(BlockStairs.field_176309_a, var1.func_177229_b(BlockStairs.field_176309_a));
            }

            if (var1.func_177230_c() == Blocks.field_150446_ar) {
               return Blocks.field_150372_bz.func_176223_P().func_177226_a(BlockStairs.field_176309_a, var1.func_177229_b(BlockStairs.field_176309_a));
            }

            if (var1.func_177230_c() == Blocks.field_150351_n) {
               return Blocks.field_150322_A.func_176223_P();
            }
         }

         return var1;
      }

      protected void func_175811_a(World var1, IBlockState var2, int var3, int var4, int var5, StructureBoundingBox var6) {
         IBlockState var7 = this.func_175847_a(var2);
         super.func_175811_a(var1, var7, var3, var4, var5, var6);
      }

      protected void func_175804_a(World var1, StructureBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, IBlockState var9, IBlockState var10, boolean var11) {
         IBlockState var12 = this.func_175847_a(var9);
         IBlockState var13 = this.func_175847_a(var10);
         super.func_175804_a(var1, var2, var3, var4, var5, var6, var7, var8, var12, var13, var11);
      }

      protected void func_175808_b(World var1, IBlockState var2, int var3, int var4, int var5, StructureBoundingBox var6) {
         IBlockState var7 = this.func_175847_a(var2);
         super.func_175808_b(var1, var7, var3, var4, var5, var6);
      }

      protected void func_175846_a(boolean var1) {
         this.field_143014_b = var1;
      }
   }

   public static class PieceWeight {
      public Class<? extends StructureVillagePieces.Village> field_75090_a;
      public final int field_75088_b;
      public int field_75089_c;
      public int field_75087_d;

      public PieceWeight(Class<? extends StructureVillagePieces.Village> var1, int var2, int var3) {
         super();
         this.field_75090_a = var1;
         this.field_75088_b = var2;
         this.field_75087_d = var3;
      }

      public boolean func_75085_a(int var1) {
         return this.field_75087_d == 0 || this.field_75089_c < this.field_75087_d;
      }

      public boolean func_75086_a() {
         return this.field_75087_d == 0 || this.field_75089_c < this.field_75087_d;
      }
   }
}
