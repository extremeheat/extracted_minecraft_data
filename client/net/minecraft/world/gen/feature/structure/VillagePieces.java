package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockGlassPane;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorchWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class VillagePieces {
   public static void func_143016_a() {
      StructureIO.func_143031_a(VillagePieces.House1.class, "ViBH");
      StructureIO.func_143031_a(VillagePieces.Field1.class, "ViDF");
      StructureIO.func_143031_a(VillagePieces.Field2.class, "ViF");
      StructureIO.func_143031_a(VillagePieces.Torch.class, "ViL");
      StructureIO.func_143031_a(VillagePieces.Hall.class, "ViPH");
      StructureIO.func_143031_a(VillagePieces.House4Garden.class, "ViSH");
      StructureIO.func_143031_a(VillagePieces.WoodHut.class, "ViSmH");
      StructureIO.func_143031_a(VillagePieces.Church.class, "ViST");
      StructureIO.func_143031_a(VillagePieces.House2.class, "ViS");
      StructureIO.func_143031_a(VillagePieces.Start.class, "ViStart");
      StructureIO.func_143031_a(VillagePieces.Path.class, "ViSR");
      StructureIO.func_143031_a(VillagePieces.House3.class, "ViTRH");
      StructureIO.func_143031_a(VillagePieces.Well.class, "ViW");
   }

   public static List<VillagePieces.PieceWeight> func_75084_a(Random var0, int var1) {
      ArrayList var2 = Lists.newArrayList();
      var2.add(new VillagePieces.PieceWeight(VillagePieces.House4Garden.class, 4, MathHelper.func_76136_a(var0, 2 + var1, 4 + var1 * 2)));
      var2.add(new VillagePieces.PieceWeight(VillagePieces.Church.class, 20, MathHelper.func_76136_a(var0, 0 + var1, 1 + var1)));
      var2.add(new VillagePieces.PieceWeight(VillagePieces.House1.class, 20, MathHelper.func_76136_a(var0, 0 + var1, 2 + var1)));
      var2.add(new VillagePieces.PieceWeight(VillagePieces.WoodHut.class, 3, MathHelper.func_76136_a(var0, 2 + var1, 5 + var1 * 3)));
      var2.add(new VillagePieces.PieceWeight(VillagePieces.Hall.class, 15, MathHelper.func_76136_a(var0, 0 + var1, 2 + var1)));
      var2.add(new VillagePieces.PieceWeight(VillagePieces.Field1.class, 3, MathHelper.func_76136_a(var0, 1 + var1, 4 + var1)));
      var2.add(new VillagePieces.PieceWeight(VillagePieces.Field2.class, 3, MathHelper.func_76136_a(var0, 2 + var1, 4 + var1 * 2)));
      var2.add(new VillagePieces.PieceWeight(VillagePieces.House2.class, 15, MathHelper.func_76136_a(var0, 0, 1 + var1)));
      var2.add(new VillagePieces.PieceWeight(VillagePieces.House3.class, 8, MathHelper.func_76136_a(var0, 0 + var1, 3 + var1 * 2)));
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         if (((VillagePieces.PieceWeight)var3.next()).field_75087_d == 0) {
            var3.remove();
         }
      }

      return var2;
   }

   private static int func_75079_a(List<VillagePieces.PieceWeight> var0) {
      boolean var1 = false;
      int var2 = 0;

      VillagePieces.PieceWeight var4;
      for(Iterator var3 = var0.iterator(); var3.hasNext(); var2 += var4.field_75088_b) {
         var4 = (VillagePieces.PieceWeight)var3.next();
         if (var4.field_75087_d > 0 && var4.field_75089_c < var4.field_75087_d) {
            var1 = true;
         }
      }

      return var1 ? var2 : -1;
   }

   private static VillagePieces.Village func_176065_a(VillagePieces.Start var0, VillagePieces.PieceWeight var1, List<StructurePiece> var2, Random var3, int var4, int var5, int var6, EnumFacing var7, int var8) {
      Class var9 = var1.field_75090_a;
      Object var10 = null;
      if (var9 == VillagePieces.House4Garden.class) {
         var10 = VillagePieces.House4Garden.func_175858_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == VillagePieces.Church.class) {
         var10 = VillagePieces.Church.func_175854_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == VillagePieces.House1.class) {
         var10 = VillagePieces.House1.func_175850_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == VillagePieces.WoodHut.class) {
         var10 = VillagePieces.WoodHut.func_175853_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == VillagePieces.Hall.class) {
         var10 = VillagePieces.Hall.func_175857_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == VillagePieces.Field1.class) {
         var10 = VillagePieces.Field1.func_175851_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == VillagePieces.Field2.class) {
         var10 = VillagePieces.Field2.func_175852_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == VillagePieces.House2.class) {
         var10 = VillagePieces.House2.func_175855_a(var0, var2, var3, var4, var5, var6, var7, var8);
      } else if (var9 == VillagePieces.House3.class) {
         var10 = VillagePieces.House3.func_175849_a(var0, var2, var3, var4, var5, var6, var7, var8);
      }

      return (VillagePieces.Village)var10;
   }

   private static VillagePieces.Village func_176067_c(VillagePieces.Start var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
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
               VillagePieces.PieceWeight var12 = (VillagePieces.PieceWeight)var11.next();
               var10 -= var12.field_75088_b;
               if (var10 < 0) {
                  if (!var12.func_75085_a(var7) || var12 == var0.field_74926_d && var0.field_74931_h.size() > 1) {
                     break;
                  }

                  VillagePieces.Village var13 = func_176065_a(var0, var12, var1, var2, var3, var4, var5, var6, var7);
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

         MutableBoundingBox var14 = VillagePieces.Torch.func_175856_a(var0, var1, var2, var3, var4, var5, var6);
         if (var14 != null) {
            return new VillagePieces.Torch(var0, var7, var2, var14, var6);
         } else {
            return null;
         }
      }
   }

   private static StructurePiece func_176066_d(VillagePieces.Start var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
      if (var7 > 50) {
         return null;
      } else if (Math.abs(var3 - var0.func_74874_b().field_78897_a) <= 112 && Math.abs(var5 - var0.func_74874_b().field_78896_c) <= 112) {
         VillagePieces.Village var8 = func_176067_c(var0, var1, var2, var3, var4, var5, var6, var7 + 1);
         if (var8 != null) {
            var1.add(var8);
            var0.field_74932_i.add(var8);
            return var8;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static StructurePiece func_176069_e(VillagePieces.Start var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
      if (var7 > 3 + var0.field_74928_c) {
         return null;
      } else if (Math.abs(var3 - var0.func_74874_b().field_78897_a) <= 112 && Math.abs(var5 - var0.func_74874_b().field_78896_c) <= 112) {
         MutableBoundingBox var8 = VillagePieces.Path.func_175848_a(var0, var1, var2, var3, var4, var5, var6);
         if (var8 != null && var8.field_78895_b > 10) {
            VillagePieces.Path var9 = new VillagePieces.Path(var0, var7, var2, var8, var6);
            var1.add(var9);
            var0.field_74930_j.add(var9);
            return var9;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static class Torch extends VillagePieces.Village {
      public Torch() {
         super();
      }

      public Torch(VillagePieces.Start var1, int var2, Random var3, MutableBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.func_186164_a(var5);
         this.field_74887_e = var4;
      }

      public static MutableBoundingBox func_175856_a(VillagePieces.Start var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 3, 4, 2, var6);
         return StructurePiece.func_74883_a(var1, var7) != null ? null : var7;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 4 - 1, 0);
         }

         IBlockState var5 = this.func_175847_a(Blocks.field_180407_aO.func_176223_P());
         this.func_175804_a(var1, var3, 0, 0, 0, 2, 3, 1, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175811_a(var1, var5, 1, 0, 0, var3);
         this.func_175811_a(var1, var5, 1, 1, 0, var3);
         this.func_175811_a(var1, var5, 1, 2, 0, var3);
         this.func_175811_a(var1, Blocks.field_196602_ba.func_176223_P(), 1, 3, 0, var3);
         this.func_189926_a(var1, EnumFacing.EAST, 2, 3, 0, var3);
         this.func_189926_a(var1, EnumFacing.NORTH, 1, 3, 1, var3);
         this.func_189926_a(var1, EnumFacing.WEST, 0, 3, 0, var3);
         this.func_189926_a(var1, EnumFacing.SOUTH, 1, 3, -1, var3);
         return true;
      }
   }

   public static class Field1 extends VillagePieces.Village {
      private IBlockState field_82679_b;
      private IBlockState field_82680_c;
      private IBlockState field_82678_d;
      private IBlockState field_82681_h;

      public Field1() {
         super();
      }

      public Field1(VillagePieces.Start var1, int var2, Random var3, MutableBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.func_186164_a(var5);
         this.field_74887_e = var4;
         this.field_82679_b = VillagePieces.Field2.func_197529_b(var3);
         this.field_82680_c = VillagePieces.Field2.func_197529_b(var3);
         this.field_82678_d = VillagePieces.Field2.func_197529_b(var3);
         this.field_82681_h = VillagePieces.Field2.func_197529_b(var3);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74782_a("CA", NBTUtil.func_190009_a(this.field_82679_b));
         var1.func_74782_a("CB", NBTUtil.func_190009_a(this.field_82680_c));
         var1.func_74782_a("CC", NBTUtil.func_190009_a(this.field_82678_d));
         var1.func_74782_a("CD", NBTUtil.func_190009_a(this.field_82681_h));
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_82679_b = NBTUtil.func_190008_d(var1.func_74775_l("CA"));
         this.field_82680_c = NBTUtil.func_190008_d(var1.func_74775_l("CB"));
         this.field_82678_d = NBTUtil.func_190008_d(var1.func_74775_l("CC"));
         this.field_82681_h = NBTUtil.func_190008_d(var1.func_74775_l("CD"));
         if (!(this.field_82679_b.func_177230_c() instanceof BlockCrops)) {
            this.field_82679_b = Blocks.field_150464_aj.func_176223_P();
         }

         if (!(this.field_82680_c.func_177230_c() instanceof BlockCrops)) {
            this.field_82680_c = Blocks.field_150459_bM.func_176223_P();
         }

         if (!(this.field_82678_d.func_177230_c() instanceof BlockCrops)) {
            this.field_82678_d = Blocks.field_150469_bN.func_176223_P();
         }

         if (!(this.field_82681_h.func_177230_c() instanceof BlockCrops)) {
            this.field_82681_h = Blocks.field_185773_cZ.func_176223_P();
         }

      }

      public static VillagePieces.Field1 func_175851_a(VillagePieces.Start var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         MutableBoundingBox var8 = MutableBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 13, 4, 9, var6);
         return func_74895_a(var8) && StructurePiece.func_74883_a(var1, var8) == null ? new VillagePieces.Field1(var0, var7, var2, var8, var6) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 4 - 1, 0);
         }

         IBlockState var5 = this.func_175847_a(Blocks.field_196617_K.func_176223_P());
         this.func_175804_a(var1, var3, 0, 1, 0, 12, 4, 8, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 1, 2, 0, 7, Blocks.field_150458_ak.func_176223_P(), Blocks.field_150458_ak.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 0, 1, 5, 0, 7, Blocks.field_150458_ak.func_176223_P(), Blocks.field_150458_ak.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 0, 1, 8, 0, 7, Blocks.field_150458_ak.func_176223_P(), Blocks.field_150458_ak.func_176223_P(), false);
         this.func_175804_a(var1, var3, 10, 0, 1, 11, 0, 7, Blocks.field_150458_ak.func_176223_P(), Blocks.field_150458_ak.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 0, 0, 0, 8, var5, var5, false);
         this.func_175804_a(var1, var3, 6, 0, 0, 6, 0, 8, var5, var5, false);
         this.func_175804_a(var1, var3, 12, 0, 0, 12, 0, 8, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 0, 0, 11, 0, 0, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 0, 8, 11, 0, 8, var5, var5, false);
         this.func_175804_a(var1, var3, 3, 0, 1, 3, 0, 7, Blocks.field_150355_j.func_176223_P(), Blocks.field_150355_j.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, 0, 1, 9, 0, 7, Blocks.field_150355_j.func_176223_P(), Blocks.field_150355_j.func_176223_P(), false);

         int var6;
         for(var6 = 1; var6 <= 7; ++var6) {
            BlockCrops var7 = (BlockCrops)this.field_82679_b.func_177230_c();
            int var8 = var7.func_185526_g();
            int var9 = var8 / 3;
            this.func_175811_a(var1, (IBlockState)this.field_82679_b.func_206870_a(var7.func_185524_e(), MathHelper.func_76136_a(var2, var9, var8)), 1, 1, var6, var3);
            this.func_175811_a(var1, (IBlockState)this.field_82679_b.func_206870_a(var7.func_185524_e(), MathHelper.func_76136_a(var2, var9, var8)), 2, 1, var6, var3);
            var7 = (BlockCrops)this.field_82680_c.func_177230_c();
            int var10 = var7.func_185526_g();
            int var11 = var10 / 3;
            this.func_175811_a(var1, (IBlockState)this.field_82680_c.func_206870_a(var7.func_185524_e(), MathHelper.func_76136_a(var2, var11, var10)), 4, 1, var6, var3);
            this.func_175811_a(var1, (IBlockState)this.field_82680_c.func_206870_a(var7.func_185524_e(), MathHelper.func_76136_a(var2, var11, var10)), 5, 1, var6, var3);
            var7 = (BlockCrops)this.field_82678_d.func_177230_c();
            int var12 = var7.func_185526_g();
            int var13 = var12 / 3;
            this.func_175811_a(var1, (IBlockState)this.field_82678_d.func_206870_a(var7.func_185524_e(), MathHelper.func_76136_a(var2, var13, var12)), 7, 1, var6, var3);
            this.func_175811_a(var1, (IBlockState)this.field_82678_d.func_206870_a(var7.func_185524_e(), MathHelper.func_76136_a(var2, var13, var12)), 8, 1, var6, var3);
            var7 = (BlockCrops)this.field_82681_h.func_177230_c();
            int var14 = var7.func_185526_g();
            int var15 = var14 / 3;
            this.func_175811_a(var1, (IBlockState)this.field_82681_h.func_206870_a(var7.func_185524_e(), MathHelper.func_76136_a(var2, var15, var14)), 10, 1, var6, var3);
            this.func_175811_a(var1, (IBlockState)this.field_82681_h.func_206870_a(var7.func_185524_e(), MathHelper.func_76136_a(var2, var15, var14)), 11, 1, var6, var3);
         }

         for(var6 = 0; var6 < 9; ++var6) {
            for(int var16 = 0; var16 < 13; ++var16) {
               this.func_74871_b(var1, var16, 4, var6, var3);
               this.func_175808_b(var1, Blocks.field_150346_d.func_176223_P(), var16, -1, var6, var3);
            }
         }

         return true;
      }
   }

   public static class Field2 extends VillagePieces.Village {
      private IBlockState field_82675_b;
      private IBlockState field_82676_c;

      public Field2() {
         super();
      }

      public Field2(VillagePieces.Start var1, int var2, Random var3, MutableBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.func_186164_a(var5);
         this.field_74887_e = var4;
         this.field_82675_b = func_197529_b(var3);
         this.field_82676_c = func_197529_b(var3);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74782_a("CA", NBTUtil.func_190009_a(this.field_82675_b));
         var1.func_74782_a("CB", NBTUtil.func_190009_a(this.field_82676_c));
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_82675_b = NBTUtil.func_190008_d(var1.func_74775_l("CA"));
         this.field_82676_c = NBTUtil.func_190008_d(var1.func_74775_l("CB"));
      }

      private static IBlockState func_197529_b(Random var0) {
         switch(var0.nextInt(10)) {
         case 0:
         case 1:
            return Blocks.field_150459_bM.func_176223_P();
         case 2:
         case 3:
            return Blocks.field_150469_bN.func_176223_P();
         case 4:
            return Blocks.field_185773_cZ.func_176223_P();
         default:
            return Blocks.field_150464_aj.func_176223_P();
         }
      }

      public static VillagePieces.Field2 func_175852_a(VillagePieces.Start var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         MutableBoundingBox var8 = MutableBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 7, 4, 9, var6);
         return func_74895_a(var8) && StructurePiece.func_74883_a(var1, var8) == null ? new VillagePieces.Field2(var0, var7, var2, var8, var6) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 4 - 1, 0);
         }

         IBlockState var5 = this.func_175847_a(Blocks.field_196617_K.func_176223_P());
         this.func_175804_a(var1, var3, 0, 1, 0, 6, 4, 8, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 1, 2, 0, 7, Blocks.field_150458_ak.func_176223_P(), Blocks.field_150458_ak.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 0, 1, 5, 0, 7, Blocks.field_150458_ak.func_176223_P(), Blocks.field_150458_ak.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 0, 0, 0, 8, var5, var5, false);
         this.func_175804_a(var1, var3, 6, 0, 0, 6, 0, 8, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 0, 0, 5, 0, 0, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 0, 8, 5, 0, 8, var5, var5, false);
         this.func_175804_a(var1, var3, 3, 0, 1, 3, 0, 7, Blocks.field_150355_j.func_176223_P(), Blocks.field_150355_j.func_176223_P(), false);

         int var6;
         for(var6 = 1; var6 <= 7; ++var6) {
            BlockCrops var7 = (BlockCrops)this.field_82675_b.func_177230_c();
            int var8 = var7.func_185526_g();
            int var9 = var8 / 3;
            this.func_175811_a(var1, (IBlockState)this.field_82675_b.func_206870_a(var7.func_185524_e(), MathHelper.func_76136_a(var2, var9, var8)), 1, 1, var6, var3);
            this.func_175811_a(var1, (IBlockState)this.field_82675_b.func_206870_a(var7.func_185524_e(), MathHelper.func_76136_a(var2, var9, var8)), 2, 1, var6, var3);
            var7 = (BlockCrops)this.field_82676_c.func_177230_c();
            int var10 = var7.func_185526_g();
            int var11 = var10 / 3;
            this.func_175811_a(var1, (IBlockState)this.field_82676_c.func_206870_a(var7.func_185524_e(), MathHelper.func_76136_a(var2, var11, var10)), 4, 1, var6, var3);
            this.func_175811_a(var1, (IBlockState)this.field_82676_c.func_206870_a(var7.func_185524_e(), MathHelper.func_76136_a(var2, var11, var10)), 5, 1, var6, var3);
         }

         for(var6 = 0; var6 < 9; ++var6) {
            for(int var12 = 0; var12 < 7; ++var12) {
               this.func_74871_b(var1, var12, 4, var6, var3);
               this.func_175808_b(var1, Blocks.field_150346_d.func_176223_P(), var12, -1, var6, var3);
            }
         }

         return true;
      }
   }

   public static class House2 extends VillagePieces.Village {
      private boolean field_74917_c;

      public House2() {
         super();
      }

      public House2(VillagePieces.Start var1, int var2, Random var3, MutableBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.func_186164_a(var5);
         this.field_74887_e = var4;
      }

      public static VillagePieces.House2 func_175855_a(VillagePieces.Start var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         MutableBoundingBox var8 = MutableBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 10, 6, 7, var6);
         return func_74895_a(var8) && StructurePiece.func_74883_a(var1, var8) == null ? new VillagePieces.House2(var0, var7, var2, var8, var6) : null;
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Chest", this.field_74917_c);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_74917_c = var1.func_74767_n("Chest");
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 6 - 1, 0);
         }

         IBlockState var5 = Blocks.field_150347_e.func_176223_P();
         IBlockState var6 = this.func_175847_a((IBlockState)Blocks.field_150476_ad.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.NORTH));
         IBlockState var7 = this.func_175847_a((IBlockState)Blocks.field_150476_ad.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.WEST));
         IBlockState var8 = this.func_175847_a(Blocks.field_196662_n.func_176223_P());
         IBlockState var9 = this.func_175847_a((IBlockState)Blocks.field_196659_cl.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.NORTH));
         IBlockState var10 = this.func_175847_a(Blocks.field_196617_K.func_176223_P());
         IBlockState var11 = this.func_175847_a(Blocks.field_180407_aO.func_176223_P());
         this.func_175804_a(var1, var3, 0, 1, 0, 9, 4, 6, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 0, 9, 0, 6, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 4, 0, 9, 4, 6, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 5, 0, 9, 5, 6, Blocks.field_150333_U.func_176223_P(), Blocks.field_150333_U.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 5, 1, 8, 5, 5, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 1, 0, 2, 3, 0, var8, var8, false);
         this.func_175804_a(var1, var3, 0, 1, 0, 0, 4, 0, var10, var10, false);
         this.func_175804_a(var1, var3, 3, 1, 0, 3, 4, 0, var10, var10, false);
         this.func_175804_a(var1, var3, 0, 1, 6, 0, 4, 6, var10, var10, false);
         this.func_175811_a(var1, var8, 3, 3, 1, var3);
         this.func_175804_a(var1, var3, 3, 1, 2, 3, 3, 2, var8, var8, false);
         this.func_175804_a(var1, var3, 4, 1, 3, 5, 3, 3, var8, var8, false);
         this.func_175804_a(var1, var3, 0, 1, 1, 0, 3, 5, var8, var8, false);
         this.func_175804_a(var1, var3, 1, 1, 6, 5, 3, 6, var8, var8, false);
         this.func_175804_a(var1, var3, 5, 1, 0, 5, 3, 0, var11, var11, false);
         this.func_175804_a(var1, var3, 9, 1, 0, 9, 3, 0, var11, var11, false);
         this.func_175804_a(var1, var3, 6, 1, 4, 9, 4, 6, var5, var5, false);
         this.func_175811_a(var1, Blocks.field_150353_l.func_176223_P(), 7, 1, 5, var3);
         this.func_175811_a(var1, Blocks.field_150353_l.func_176223_P(), 8, 1, 5, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196409_a, true)).func_206870_a(BlockPane.field_196413_c, true), 9, 2, 5, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196409_a, true), 9, 2, 4, var3);
         this.func_175804_a(var1, var3, 7, 2, 4, 8, 2, 5, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175811_a(var1, var5, 6, 1, 3, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150460_al.func_176223_P().func_206870_a(BlockFurnace.field_176447_a, EnumFacing.SOUTH), 6, 2, 3, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150460_al.func_176223_P().func_206870_a(BlockFurnace.field_176447_a, EnumFacing.SOUTH), 6, 3, 3, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150333_U.func_176223_P().func_206870_a(BlockSlab.field_196505_a, SlabType.DOUBLE), 8, 1, 1, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 2, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 2, 4, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 2, 2, 6, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 4, 2, 6, var3);
         this.func_175811_a(var1, var11, 2, 1, 4, var3);
         this.func_175811_a(var1, Blocks.field_196663_cq.func_176223_P(), 2, 2, 4, var3);
         this.func_175811_a(var1, var8, 1, 1, 5, var3);
         this.func_175811_a(var1, var6, 2, 1, 5, var3);
         this.func_175811_a(var1, var7, 1, 1, 4, var3);
         if (!this.field_74917_c && var3.func_175898_b(new BlockPos(this.func_74865_a(5, 5), this.func_74862_a(1), this.func_74873_b(5, 5)))) {
            this.field_74917_c = true;
            this.func_186167_a(var1, var3, var2, 5, 1, 5, LootTableList.field_186423_e);
         }

         int var12;
         for(var12 = 6; var12 <= 8; ++var12) {
            if (this.func_175807_a(var1, var12, 0, -1, var3).func_196958_f() && !this.func_175807_a(var1, var12, -1, -1, var3).func_196958_f()) {
               this.func_175811_a(var1, var9, var12, 0, -1, var3);
               if (this.func_175807_a(var1, var12, -1, -1, var3).func_177230_c() == Blocks.field_185774_da) {
                  this.func_175811_a(var1, Blocks.field_196658_i.func_176223_P(), var12, -1, -1, var3);
               }
            }
         }

         for(var12 = 0; var12 < 7; ++var12) {
            for(int var13 = 0; var13 < 10; ++var13) {
               this.func_74871_b(var1, var13, 6, var12, var3);
               this.func_175808_b(var1, var5, var13, -1, var12, var3);
            }
         }

         this.func_74893_a(var1, var3, 7, 1, 1, 1);
         return true;
      }

      protected int func_180779_c(int var1, int var2) {
         return 3;
      }
   }

   public static class House3 extends VillagePieces.Village {
      public House3() {
         super();
      }

      public House3(VillagePieces.Start var1, int var2, Random var3, MutableBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.func_186164_a(var5);
         this.field_74887_e = var4;
      }

      public static VillagePieces.House3 func_175849_a(VillagePieces.Start var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         MutableBoundingBox var8 = MutableBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 9, 7, 12, var6);
         return func_74895_a(var8) && StructurePiece.func_74883_a(var1, var8) == null ? new VillagePieces.House3(var0, var7, var2, var8, var6) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 7 - 1, 0);
         }

         IBlockState var5 = this.func_175847_a(Blocks.field_150347_e.func_176223_P());
         IBlockState var6 = this.func_175847_a((IBlockState)Blocks.field_150476_ad.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.NORTH));
         IBlockState var7 = this.func_175847_a((IBlockState)Blocks.field_150476_ad.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.SOUTH));
         IBlockState var8 = this.func_175847_a((IBlockState)Blocks.field_150476_ad.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.EAST));
         IBlockState var9 = this.func_175847_a((IBlockState)Blocks.field_150476_ad.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.WEST));
         IBlockState var10 = this.func_175847_a(Blocks.field_196662_n.func_176223_P());
         IBlockState var11 = this.func_175847_a(Blocks.field_196617_K.func_176223_P());
         this.func_175804_a(var1, var3, 1, 1, 1, 7, 4, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 1, 6, 8, 4, 10, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 0, 5, 8, 0, 10, var10, var10, false);
         this.func_175804_a(var1, var3, 1, 0, 1, 7, 0, 4, var10, var10, false);
         this.func_175804_a(var1, var3, 0, 0, 0, 0, 3, 5, var5, var5, false);
         this.func_175804_a(var1, var3, 8, 0, 0, 8, 3, 10, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 0, 0, 7, 2, 0, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 0, 5, 2, 1, 5, var5, var5, false);
         this.func_175804_a(var1, var3, 2, 0, 6, 2, 3, 10, var5, var5, false);
         this.func_175804_a(var1, var3, 3, 0, 10, 7, 3, 10, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 2, 0, 7, 3, 0, var10, var10, false);
         this.func_175804_a(var1, var3, 1, 2, 5, 2, 3, 5, var10, var10, false);
         this.func_175804_a(var1, var3, 0, 4, 1, 8, 4, 1, var10, var10, false);
         this.func_175804_a(var1, var3, 0, 4, 4, 3, 4, 4, var10, var10, false);
         this.func_175804_a(var1, var3, 0, 5, 2, 8, 5, 3, var10, var10, false);
         this.func_175811_a(var1, var10, 0, 4, 2, var3);
         this.func_175811_a(var1, var10, 0, 4, 3, var3);
         this.func_175811_a(var1, var10, 8, 4, 2, var3);
         this.func_175811_a(var1, var10, 8, 4, 3, var3);
         this.func_175811_a(var1, var10, 8, 4, 4, var3);
         IBlockState var12 = var6;
         IBlockState var13 = var7;
         IBlockState var14 = var9;
         IBlockState var15 = var8;

         int var16;
         int var17;
         for(var16 = -1; var16 <= 2; ++var16) {
            for(var17 = 0; var17 <= 8; ++var17) {
               this.func_175811_a(var1, var12, var17, 4 + var16, var16, var3);
               if ((var16 > -1 || var17 <= 1) && (var16 > 0 || var17 <= 3) && (var16 > 1 || var17 <= 4 || var17 >= 6)) {
                  this.func_175811_a(var1, var13, var17, 4 + var16, 5 - var16, var3);
               }
            }
         }

         this.func_175804_a(var1, var3, 3, 4, 5, 3, 4, 10, var10, var10, false);
         this.func_175804_a(var1, var3, 7, 4, 2, 7, 4, 10, var10, var10, false);
         this.func_175804_a(var1, var3, 4, 5, 4, 4, 5, 10, var10, var10, false);
         this.func_175804_a(var1, var3, 6, 5, 4, 6, 5, 10, var10, var10, false);
         this.func_175804_a(var1, var3, 5, 6, 3, 5, 6, 10, var10, var10, false);

         for(var16 = 4; var16 >= 1; --var16) {
            this.func_175811_a(var1, var10, var16, 2 + var16, 7 - var16, var3);

            for(var17 = 8 - var16; var17 <= 10; ++var17) {
               this.func_175811_a(var1, var15, var16, 2 + var16, var17, var3);
            }
         }

         this.func_175811_a(var1, var10, 6, 6, 3, var3);
         this.func_175811_a(var1, var10, 7, 5, 4, var3);
         this.func_175811_a(var1, var9, 6, 6, 4, var3);

         for(var16 = 6; var16 <= 8; ++var16) {
            for(var17 = 5; var17 <= 10; ++var17) {
               this.func_175811_a(var1, var14, var16, 12 - var16, var17, var3);
            }
         }

         this.func_175811_a(var1, var11, 0, 2, 1, var3);
         this.func_175811_a(var1, var11, 0, 2, 4, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 2, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 2, 3, var3);
         this.func_175811_a(var1, var11, 4, 2, 0, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 5, 2, 0, var3);
         this.func_175811_a(var1, var11, 6, 2, 0, var3);
         this.func_175811_a(var1, var11, 8, 2, 1, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 8, 2, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 8, 2, 3, var3);
         this.func_175811_a(var1, var11, 8, 2, 4, var3);
         this.func_175811_a(var1, var10, 8, 2, 5, var3);
         this.func_175811_a(var1, var11, 8, 2, 6, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 8, 2, 7, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 8, 2, 8, var3);
         this.func_175811_a(var1, var11, 8, 2, 9, var3);
         this.func_175811_a(var1, var11, 2, 2, 6, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 2, 2, 7, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 2, 2, 8, var3);
         this.func_175811_a(var1, var11, 2, 2, 9, var3);
         this.func_175811_a(var1, var11, 4, 4, 10, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 5, 4, 10, var3);
         this.func_175811_a(var1, var11, 6, 4, 10, var3);
         this.func_175811_a(var1, var10, 5, 5, 10, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 1, 0, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 2, 0, var3);
         this.func_189926_a(var1, EnumFacing.NORTH, 2, 3, 1, var3);
         this.func_189927_a(var1, var3, var2, 2, 1, 0, EnumFacing.NORTH);
         this.func_175804_a(var1, var3, 1, 0, -1, 3, 2, -1, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         if (this.func_175807_a(var1, 2, 0, -1, var3).func_196958_f() && !this.func_175807_a(var1, 2, -1, -1, var3).func_196958_f()) {
            this.func_175811_a(var1, var12, 2, 0, -1, var3);
            if (this.func_175807_a(var1, 2, -1, -1, var3).func_177230_c() == Blocks.field_185774_da) {
               this.func_175811_a(var1, Blocks.field_196658_i.func_176223_P(), 2, -1, -1, var3);
            }
         }

         for(var16 = 0; var16 < 5; ++var16) {
            for(var17 = 0; var17 < 9; ++var17) {
               this.func_74871_b(var1, var17, 7, var16, var3);
               this.func_175808_b(var1, var5, var17, -1, var16, var3);
            }
         }

         for(var16 = 5; var16 < 11; ++var16) {
            for(var17 = 2; var17 < 9; ++var17) {
               this.func_74871_b(var1, var17, 7, var16, var3);
               this.func_175808_b(var1, var5, var17, -1, var16, var3);
            }
         }

         this.func_74893_a(var1, var3, 4, 1, 2, 2);
         return true;
      }
   }

   public static class Hall extends VillagePieces.Village {
      public Hall() {
         super();
      }

      public Hall(VillagePieces.Start var1, int var2, Random var3, MutableBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.func_186164_a(var5);
         this.field_74887_e = var4;
      }

      public static VillagePieces.Hall func_175857_a(VillagePieces.Start var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         MutableBoundingBox var8 = MutableBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 9, 7, 11, var6);
         return func_74895_a(var8) && StructurePiece.func_74883_a(var1, var8) == null ? new VillagePieces.Hall(var0, var7, var2, var8, var6) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 7 - 1, 0);
         }

         IBlockState var5 = this.func_175847_a(Blocks.field_150347_e.func_176223_P());
         IBlockState var6 = this.func_175847_a((IBlockState)Blocks.field_150476_ad.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.NORTH));
         IBlockState var7 = this.func_175847_a((IBlockState)Blocks.field_150476_ad.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.SOUTH));
         IBlockState var8 = this.func_175847_a((IBlockState)Blocks.field_150476_ad.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.WEST));
         IBlockState var9 = this.func_175847_a(Blocks.field_196662_n.func_176223_P());
         IBlockState var10 = this.func_175847_a(Blocks.field_196617_K.func_176223_P());
         IBlockState var11 = this.func_175847_a(Blocks.field_180407_aO.func_176223_P());
         this.func_175804_a(var1, var3, 1, 1, 1, 7, 4, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 1, 6, 8, 4, 10, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 0, 6, 8, 0, 10, Blocks.field_150346_d.func_176223_P(), Blocks.field_150346_d.func_176223_P(), false);
         this.func_175811_a(var1, var5, 6, 0, 6, var3);
         IBlockState var12 = (IBlockState)((IBlockState)var11.func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196413_c, true);
         IBlockState var13 = (IBlockState)((IBlockState)var11.func_206870_a(BlockFence.field_196414_y, true)).func_206870_a(BlockFence.field_196411_b, true);
         this.func_175804_a(var1, var3, 2, 1, 6, 2, 1, 9, var12, var12, false);
         this.func_175811_a(var1, (IBlockState)((IBlockState)var11.func_206870_a(BlockFence.field_196413_c, true)).func_206870_a(BlockFence.field_196411_b, true), 2, 1, 10, var3);
         this.func_175804_a(var1, var3, 8, 1, 6, 8, 1, 9, var12, var12, false);
         this.func_175811_a(var1, (IBlockState)((IBlockState)var11.func_206870_a(BlockFence.field_196413_c, true)).func_206870_a(BlockFence.field_196414_y, true), 8, 1, 10, var3);
         this.func_175804_a(var1, var3, 3, 1, 10, 7, 1, 10, var13, var13, false);
         this.func_175804_a(var1, var3, 1, 0, 1, 7, 0, 4, var9, var9, false);
         this.func_175804_a(var1, var3, 0, 0, 0, 0, 3, 5, var5, var5, false);
         this.func_175804_a(var1, var3, 8, 0, 0, 8, 3, 5, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 0, 0, 7, 1, 0, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 0, 5, 7, 1, 5, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 2, 0, 7, 3, 0, var9, var9, false);
         this.func_175804_a(var1, var3, 1, 2, 5, 7, 3, 5, var9, var9, false);
         this.func_175804_a(var1, var3, 0, 4, 1, 8, 4, 1, var9, var9, false);
         this.func_175804_a(var1, var3, 0, 4, 4, 8, 4, 4, var9, var9, false);
         this.func_175804_a(var1, var3, 0, 5, 2, 8, 5, 3, var9, var9, false);
         this.func_175811_a(var1, var9, 0, 4, 2, var3);
         this.func_175811_a(var1, var9, 0, 4, 3, var3);
         this.func_175811_a(var1, var9, 8, 4, 2, var3);
         this.func_175811_a(var1, var9, 8, 4, 3, var3);
         IBlockState var14 = var6;
         IBlockState var15 = var7;

         int var18;
         for(int var17 = -1; var17 <= 2; ++var17) {
            for(var18 = 0; var18 <= 8; ++var18) {
               this.func_175811_a(var1, var14, var18, 4 + var17, var17, var3);
               this.func_175811_a(var1, var15, var18, 4 + var17, 5 - var17, var3);
            }
         }

         this.func_175811_a(var1, var10, 0, 2, 1, var3);
         this.func_175811_a(var1, var10, 0, 2, 4, var3);
         this.func_175811_a(var1, var10, 8, 2, 1, var3);
         this.func_175811_a(var1, var10, 8, 2, 4, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 2, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 2, 3, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 8, 2, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 8, 2, 3, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 2, 2, 5, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 3, 2, 5, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 5, 2, 0, var3);
         this.func_175811_a(var1, var11, 2, 1, 3, var3);
         this.func_175811_a(var1, Blocks.field_196663_cq.func_176223_P(), 2, 2, 3, var3);
         this.func_175811_a(var1, var9, 1, 1, 4, var3);
         this.func_175811_a(var1, var14, 2, 1, 4, var3);
         this.func_175811_a(var1, var8, 1, 1, 3, var3);
         IBlockState var20 = (IBlockState)Blocks.field_150333_U.func_176223_P().func_206870_a(BlockSlab.field_196505_a, SlabType.DOUBLE);
         this.func_175804_a(var1, var3, 5, 0, 1, 7, 0, 3, var20, var20, false);
         this.func_175811_a(var1, var20, 6, 1, 1, var3);
         this.func_175811_a(var1, var20, 6, 1, 2, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 1, 0, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 2, 0, var3);
         this.func_189926_a(var1, EnumFacing.NORTH, 2, 3, 1, var3);
         this.func_189927_a(var1, var3, var2, 2, 1, 0, EnumFacing.NORTH);
         if (this.func_175807_a(var1, 2, 0, -1, var3).func_196958_f() && !this.func_175807_a(var1, 2, -1, -1, var3).func_196958_f()) {
            this.func_175811_a(var1, var14, 2, 0, -1, var3);
            if (this.func_175807_a(var1, 2, -1, -1, var3).func_177230_c() == Blocks.field_185774_da) {
               this.func_175811_a(var1, Blocks.field_196658_i.func_176223_P(), 2, -1, -1, var3);
            }
         }

         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 6, 1, 5, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 6, 2, 5, var3);
         this.func_189926_a(var1, EnumFacing.SOUTH, 6, 3, 4, var3);
         this.func_189927_a(var1, var3, var2, 6, 1, 5, EnumFacing.SOUTH);

         for(var18 = 0; var18 < 5; ++var18) {
            for(int var19 = 0; var19 < 9; ++var19) {
               this.func_74871_b(var1, var19, 7, var18, var3);
               this.func_175808_b(var1, var5, var19, -1, var18, var3);
            }
         }

         this.func_74893_a(var1, var3, 4, 1, 2, 2);
         return true;
      }

      protected int func_180779_c(int var1, int var2) {
         return var1 == 0 ? 4 : super.func_180779_c(var1, var2);
      }
   }

   public static class WoodHut extends VillagePieces.Village {
      private boolean field_74909_b;
      private int field_74910_c;

      public WoodHut() {
         super();
      }

      public WoodHut(VillagePieces.Start var1, int var2, Random var3, MutableBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.func_186164_a(var5);
         this.field_74887_e = var4;
         this.field_74909_b = var3.nextBoolean();
         this.field_74910_c = var3.nextInt(3);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74768_a("T", this.field_74910_c);
         var1.func_74757_a("C", this.field_74909_b);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_74910_c = var1.func_74762_e("T");
         this.field_74909_b = var1.func_74767_n("C");
      }

      public static VillagePieces.WoodHut func_175853_a(VillagePieces.Start var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         MutableBoundingBox var8 = MutableBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 4, 6, 5, var6);
         return func_74895_a(var8) && StructurePiece.func_74883_a(var1, var8) == null ? new VillagePieces.WoodHut(var0, var7, var2, var8, var6) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 6 - 1, 0);
         }

         IBlockState var5 = this.func_175847_a(Blocks.field_150347_e.func_176223_P());
         IBlockState var6 = this.func_175847_a(Blocks.field_196662_n.func_176223_P());
         IBlockState var7 = this.func_175847_a((IBlockState)Blocks.field_196659_cl.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.NORTH));
         IBlockState var8 = this.func_175847_a(Blocks.field_196617_K.func_176223_P());
         IBlockState var9 = this.func_175847_a(Blocks.field_180407_aO.func_176223_P());
         this.func_175804_a(var1, var3, 1, 1, 1, 3, 5, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 0, 3, 0, 4, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 0, 1, 2, 0, 3, Blocks.field_150346_d.func_176223_P(), Blocks.field_150346_d.func_176223_P(), false);
         if (this.field_74909_b) {
            this.func_175804_a(var1, var3, 1, 4, 1, 2, 4, 3, var8, var8, false);
         } else {
            this.func_175804_a(var1, var3, 1, 5, 1, 2, 5, 3, var8, var8, false);
         }

         this.func_175811_a(var1, var8, 1, 4, 0, var3);
         this.func_175811_a(var1, var8, 2, 4, 0, var3);
         this.func_175811_a(var1, var8, 1, 4, 4, var3);
         this.func_175811_a(var1, var8, 2, 4, 4, var3);
         this.func_175811_a(var1, var8, 0, 4, 1, var3);
         this.func_175811_a(var1, var8, 0, 4, 2, var3);
         this.func_175811_a(var1, var8, 0, 4, 3, var3);
         this.func_175811_a(var1, var8, 3, 4, 1, var3);
         this.func_175811_a(var1, var8, 3, 4, 2, var3);
         this.func_175811_a(var1, var8, 3, 4, 3, var3);
         this.func_175804_a(var1, var3, 0, 1, 0, 0, 3, 0, var8, var8, false);
         this.func_175804_a(var1, var3, 3, 1, 0, 3, 3, 0, var8, var8, false);
         this.func_175804_a(var1, var3, 0, 1, 4, 0, 3, 4, var8, var8, false);
         this.func_175804_a(var1, var3, 3, 1, 4, 3, 3, 4, var8, var8, false);
         this.func_175804_a(var1, var3, 0, 1, 1, 0, 3, 3, var6, var6, false);
         this.func_175804_a(var1, var3, 3, 1, 1, 3, 3, 3, var6, var6, false);
         this.func_175804_a(var1, var3, 1, 1, 0, 2, 3, 0, var6, var6, false);
         this.func_175804_a(var1, var3, 1, 1, 4, 2, 3, 4, var6, var6, false);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 2, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 3, 2, 2, var3);
         if (this.field_74910_c > 0) {
            this.func_175811_a(var1, (IBlockState)((IBlockState)var9.func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(this.field_74910_c == 1 ? BlockFence.field_196414_y : BlockFence.field_196411_b, true), this.field_74910_c, 1, 3, var3);
            this.func_175811_a(var1, Blocks.field_196663_cq.func_176223_P(), this.field_74910_c, 2, 3, var3);
         }

         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 1, 1, 0, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 1, 2, 0, var3);
         this.func_189927_a(var1, var3, var2, 1, 1, 0, EnumFacing.NORTH);
         if (this.func_175807_a(var1, 1, 0, -1, var3).func_196958_f() && !this.func_175807_a(var1, 1, -1, -1, var3).func_196958_f()) {
            this.func_175811_a(var1, var7, 1, 0, -1, var3);
            if (this.func_175807_a(var1, 1, -1, -1, var3).func_177230_c() == Blocks.field_185774_da) {
               this.func_175811_a(var1, Blocks.field_196658_i.func_176223_P(), 1, -1, -1, var3);
            }
         }

         for(int var10 = 0; var10 < 5; ++var10) {
            for(int var11 = 0; var11 < 4; ++var11) {
               this.func_74871_b(var1, var11, 6, var10, var3);
               this.func_175808_b(var1, var5, var11, -1, var10, var3);
            }
         }

         this.func_74893_a(var1, var3, 1, 1, 2, 1);
         return true;
      }
   }

   public static class House1 extends VillagePieces.Village {
      public House1() {
         super();
      }

      public House1(VillagePieces.Start var1, int var2, Random var3, MutableBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.func_186164_a(var5);
         this.field_74887_e = var4;
      }

      public static VillagePieces.House1 func_175850_a(VillagePieces.Start var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         MutableBoundingBox var8 = MutableBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 9, 9, 6, var6);
         return func_74895_a(var8) && StructurePiece.func_74883_a(var1, var8) == null ? new VillagePieces.House1(var0, var7, var2, var8, var6) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 9 - 1, 0);
         }

         IBlockState var5 = this.func_175847_a(Blocks.field_150347_e.func_176223_P());
         IBlockState var6 = this.func_175847_a((IBlockState)Blocks.field_150476_ad.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.NORTH));
         IBlockState var7 = this.func_175847_a((IBlockState)Blocks.field_150476_ad.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.SOUTH));
         IBlockState var8 = this.func_175847_a((IBlockState)Blocks.field_150476_ad.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.EAST));
         IBlockState var9 = this.func_175847_a(Blocks.field_196662_n.func_176223_P());
         IBlockState var10 = this.func_175847_a((IBlockState)Blocks.field_196659_cl.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.NORTH));
         IBlockState var11 = this.func_175847_a(Blocks.field_180407_aO.func_176223_P());
         this.func_175804_a(var1, var3, 1, 1, 1, 7, 5, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 0, 8, 0, 5, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 5, 0, 8, 5, 5, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 6, 1, 8, 6, 4, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 7, 2, 8, 7, 3, var5, var5, false);

         int var13;
         for(int var12 = -1; var12 <= 2; ++var12) {
            for(var13 = 0; var13 <= 8; ++var13) {
               this.func_175811_a(var1, var6, var13, 6 + var12, var12, var3);
               this.func_175811_a(var1, var7, var13, 6 + var12, 5 - var12, var3);
            }
         }

         this.func_175804_a(var1, var3, 0, 1, 0, 0, 1, 5, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 1, 5, 8, 1, 5, var5, var5, false);
         this.func_175804_a(var1, var3, 8, 1, 0, 8, 1, 4, var5, var5, false);
         this.func_175804_a(var1, var3, 2, 1, 0, 7, 1, 0, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 2, 0, 0, 4, 0, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 2, 5, 0, 4, 5, var5, var5, false);
         this.func_175804_a(var1, var3, 8, 2, 5, 8, 4, 5, var5, var5, false);
         this.func_175804_a(var1, var3, 8, 2, 0, 8, 4, 0, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 2, 1, 0, 4, 4, var9, var9, false);
         this.func_175804_a(var1, var3, 1, 2, 5, 7, 4, 5, var9, var9, false);
         this.func_175804_a(var1, var3, 8, 2, 1, 8, 4, 4, var9, var9, false);
         this.func_175804_a(var1, var3, 1, 2, 0, 7, 4, 0, var9, var9, false);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 4, 2, 0, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 5, 2, 0, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 6, 2, 0, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 4, 3, 0, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 5, 3, 0, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 6, 3, 0, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 2, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 2, 3, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 3, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 3, 3, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 8, 2, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 8, 2, 3, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 8, 3, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 8, 3, 3, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 2, 2, 5, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 3, 2, 5, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 5, 2, 5, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 6, 2, 5, var3);
         this.func_175804_a(var1, var3, 1, 4, 1, 7, 4, 1, var9, var9, false);
         this.func_175804_a(var1, var3, 1, 4, 4, 7, 4, 4, var9, var9, false);
         this.func_175804_a(var1, var3, 1, 3, 4, 7, 3, 4, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
         this.func_175811_a(var1, var9, 7, 1, 4, var3);
         this.func_175811_a(var1, var8, 7, 1, 3, var3);
         this.func_175811_a(var1, var6, 6, 1, 4, var3);
         this.func_175811_a(var1, var6, 5, 1, 4, var3);
         this.func_175811_a(var1, var6, 4, 1, 4, var3);
         this.func_175811_a(var1, var6, 3, 1, 4, var3);
         this.func_175811_a(var1, var11, 6, 1, 3, var3);
         this.func_175811_a(var1, Blocks.field_196663_cq.func_176223_P(), 6, 2, 3, var3);
         this.func_175811_a(var1, var11, 4, 1, 3, var3);
         this.func_175811_a(var1, Blocks.field_196663_cq.func_176223_P(), 4, 2, 3, var3);
         this.func_175811_a(var1, Blocks.field_150462_ai.func_176223_P(), 7, 1, 1, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 1, 1, 0, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 1, 2, 0, var3);
         this.func_189927_a(var1, var3, var2, 1, 1, 0, EnumFacing.NORTH);
         if (this.func_175807_a(var1, 1, 0, -1, var3).func_196958_f() && !this.func_175807_a(var1, 1, -1, -1, var3).func_196958_f()) {
            this.func_175811_a(var1, var10, 1, 0, -1, var3);
            if (this.func_175807_a(var1, 1, -1, -1, var3).func_177230_c() == Blocks.field_185774_da) {
               this.func_175811_a(var1, Blocks.field_196658_i.func_176223_P(), 1, -1, -1, var3);
            }
         }

         for(var13 = 0; var13 < 6; ++var13) {
            for(int var14 = 0; var14 < 9; ++var14) {
               this.func_74871_b(var1, var14, 9, var13, var3);
               this.func_175808_b(var1, var5, var14, -1, var13, var3);
            }
         }

         this.func_74893_a(var1, var3, 2, 1, 2, 1);
         return true;
      }

      protected int func_180779_c(int var1, int var2) {
         return 1;
      }
   }

   public static class Church extends VillagePieces.Village {
      public Church() {
         super();
      }

      public Church(VillagePieces.Start var1, int var2, Random var3, MutableBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.func_186164_a(var5);
         this.field_74887_e = var4;
      }

      public static VillagePieces.Church func_175854_a(VillagePieces.Start var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         MutableBoundingBox var8 = MutableBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 5, 12, 9, var6);
         return func_74895_a(var8) && StructurePiece.func_74883_a(var1, var8) == null ? new VillagePieces.Church(var0, var7, var2, var8, var6) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 12 - 1, 0);
         }

         IBlockState var5 = Blocks.field_150347_e.func_176223_P();
         IBlockState var6 = this.func_175847_a((IBlockState)Blocks.field_196659_cl.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.NORTH));
         IBlockState var7 = this.func_175847_a((IBlockState)Blocks.field_196659_cl.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.WEST));
         IBlockState var8 = this.func_175847_a((IBlockState)Blocks.field_196659_cl.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.EAST));
         this.func_175804_a(var1, var3, 1, 1, 1, 3, 3, 7, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 5, 1, 3, 9, 3, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 0, 3, 0, 8, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 1, 0, 3, 10, 0, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 1, 1, 0, 10, 3, var5, var5, false);
         this.func_175804_a(var1, var3, 4, 1, 1, 4, 10, 3, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 0, 4, 0, 4, 7, var5, var5, false);
         this.func_175804_a(var1, var3, 4, 0, 4, 4, 4, 7, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 1, 8, 3, 4, 8, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 5, 4, 3, 10, 4, var5, var5, false);
         this.func_175804_a(var1, var3, 1, 5, 5, 3, 5, 7, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 9, 0, 4, 9, 4, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 4, 0, 4, 4, 4, var5, var5, false);
         this.func_175811_a(var1, var5, 0, 11, 2, var3);
         this.func_175811_a(var1, var5, 4, 11, 2, var3);
         this.func_175811_a(var1, var5, 2, 11, 0, var3);
         this.func_175811_a(var1, var5, 2, 11, 4, var3);
         this.func_175811_a(var1, var5, 1, 1, 6, var3);
         this.func_175811_a(var1, var5, 1, 1, 7, var3);
         this.func_175811_a(var1, var5, 2, 1, 7, var3);
         this.func_175811_a(var1, var5, 3, 1, 6, var3);
         this.func_175811_a(var1, var5, 3, 1, 7, var3);
         this.func_175811_a(var1, var6, 1, 1, 5, var3);
         this.func_175811_a(var1, var6, 2, 1, 6, var3);
         this.func_175811_a(var1, var6, 3, 1, 5, var3);
         this.func_175811_a(var1, var7, 1, 2, 7, var3);
         this.func_175811_a(var1, var8, 3, 2, 7, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 2, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 3, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 4, 2, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 4, 3, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 6, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 7, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 4, 6, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 4, 7, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 2, 6, 0, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 2, 7, 0, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 2, 6, 4, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 2, 7, 4, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 3, 6, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 4, 3, 6, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 2, 3, 8, var3);
         this.func_189926_a(var1, EnumFacing.SOUTH, 2, 4, 7, var3);
         this.func_189926_a(var1, EnumFacing.EAST, 1, 4, 6, var3);
         this.func_189926_a(var1, EnumFacing.WEST, 3, 4, 6, var3);
         this.func_189926_a(var1, EnumFacing.NORTH, 2, 4, 5, var3);
         IBlockState var9 = (IBlockState)Blocks.field_150468_ap.func_176223_P().func_206870_a(BlockLadder.field_176382_a, EnumFacing.WEST);

         int var10;
         for(var10 = 1; var10 <= 9; ++var10) {
            this.func_175811_a(var1, var9, 3, var10, 3, var3);
         }

         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 1, 0, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 2, 0, var3);
         this.func_189927_a(var1, var3, var2, 2, 1, 0, EnumFacing.NORTH);
         if (this.func_175807_a(var1, 2, 0, -1, var3).func_196958_f() && !this.func_175807_a(var1, 2, -1, -1, var3).func_196958_f()) {
            this.func_175811_a(var1, var6, 2, 0, -1, var3);
            if (this.func_175807_a(var1, 2, -1, -1, var3).func_177230_c() == Blocks.field_185774_da) {
               this.func_175811_a(var1, Blocks.field_196658_i.func_176223_P(), 2, -1, -1, var3);
            }
         }

         for(var10 = 0; var10 < 9; ++var10) {
            for(int var11 = 0; var11 < 5; ++var11) {
               this.func_74871_b(var1, var11, 12, var10, var3);
               this.func_175808_b(var1, var5, var11, -1, var10, var3);
            }
         }

         this.func_74893_a(var1, var3, 2, 1, 2, 1);
         return true;
      }

      protected int func_180779_c(int var1, int var2) {
         return 2;
      }
   }

   public static class House4Garden extends VillagePieces.Village {
      private boolean field_74913_b;

      public House4Garden() {
         super();
      }

      public House4Garden(VillagePieces.Start var1, int var2, Random var3, MutableBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.func_186164_a(var5);
         this.field_74887_e = var4;
         this.field_74913_b = var3.nextBoolean();
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Terrace", this.field_74913_b);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_74913_b = var1.func_74767_n("Terrace");
      }

      public static VillagePieces.House4Garden func_175858_a(VillagePieces.Start var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
         MutableBoundingBox var8 = MutableBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 5, 6, 5, var6);
         return StructurePiece.func_74883_a(var1, var8) != null ? null : new VillagePieces.House4Garden(var0, var7, var2, var8, var6);
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 6 - 1, 0);
         }

         IBlockState var5 = this.func_175847_a(Blocks.field_150347_e.func_176223_P());
         IBlockState var6 = this.func_175847_a(Blocks.field_196662_n.func_176223_P());
         IBlockState var7 = this.func_175847_a((IBlockState)Blocks.field_196659_cl.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.NORTH));
         IBlockState var8 = this.func_175847_a(Blocks.field_196617_K.func_176223_P());
         IBlockState var9 = this.func_175847_a(Blocks.field_180407_aO.func_176223_P());
         this.func_175804_a(var1, var3, 0, 0, 0, 4, 0, 4, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 4, 0, 4, 4, 4, var8, var8, false);
         this.func_175804_a(var1, var3, 1, 4, 1, 3, 4, 3, var6, var6, false);
         this.func_175811_a(var1, var5, 0, 1, 0, var3);
         this.func_175811_a(var1, var5, 0, 2, 0, var3);
         this.func_175811_a(var1, var5, 0, 3, 0, var3);
         this.func_175811_a(var1, var5, 4, 1, 0, var3);
         this.func_175811_a(var1, var5, 4, 2, 0, var3);
         this.func_175811_a(var1, var5, 4, 3, 0, var3);
         this.func_175811_a(var1, var5, 0, 1, 4, var3);
         this.func_175811_a(var1, var5, 0, 2, 4, var3);
         this.func_175811_a(var1, var5, 0, 3, 4, var3);
         this.func_175811_a(var1, var5, 4, 1, 4, var3);
         this.func_175811_a(var1, var5, 4, 2, 4, var3);
         this.func_175811_a(var1, var5, 4, 3, 4, var3);
         this.func_175804_a(var1, var3, 0, 1, 1, 0, 3, 3, var6, var6, false);
         this.func_175804_a(var1, var3, 4, 1, 1, 4, 3, 3, var6, var6, false);
         this.func_175804_a(var1, var3, 1, 1, 4, 3, 3, 4, var6, var6, false);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 0, 2, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196411_b, true)).func_206870_a(BlockGlassPane.field_196414_y, true), 2, 2, 4, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150410_aZ.func_176223_P().func_206870_a(BlockGlassPane.field_196413_c, true)).func_206870_a(BlockGlassPane.field_196409_a, true), 4, 2, 2, var3);
         this.func_175811_a(var1, var6, 1, 1, 0, var3);
         this.func_175811_a(var1, var6, 1, 2, 0, var3);
         this.func_175811_a(var1, var6, 1, 3, 0, var3);
         this.func_175811_a(var1, var6, 2, 3, 0, var3);
         this.func_175811_a(var1, var6, 3, 3, 0, var3);
         this.func_175811_a(var1, var6, 3, 2, 0, var3);
         this.func_175811_a(var1, var6, 3, 1, 0, var3);
         if (this.func_175807_a(var1, 2, 0, -1, var3).func_196958_f() && !this.func_175807_a(var1, 2, -1, -1, var3).func_196958_f()) {
            this.func_175811_a(var1, var7, 2, 0, -1, var3);
            if (this.func_175807_a(var1, 2, -1, -1, var3).func_177230_c() == Blocks.field_185774_da) {
               this.func_175811_a(var1, Blocks.field_196658_i.func_176223_P(), 2, -1, -1, var3);
            }
         }

         this.func_175804_a(var1, var3, 1, 1, 1, 3, 3, 3, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         if (this.field_74913_b) {
            boolean var10 = false;
            boolean var11 = true;

            for(int var12 = 0; var12 <= 4; ++var12) {
               for(int var13 = 0; var13 <= 4; ++var13) {
                  boolean var14 = var12 == 0 || var12 == 4;
                  boolean var15 = var13 == 0 || var13 == 4;
                  if (var14 || var15) {
                     boolean var16 = var12 == 0 || var12 == 4;
                     boolean var17 = var13 == 0 || var13 == 4;
                     IBlockState var18 = (IBlockState)((IBlockState)((IBlockState)((IBlockState)var9.func_206870_a(BlockFence.field_196413_c, var16 && var13 != 0)).func_206870_a(BlockFence.field_196409_a, var16 && var13 != 4)).func_206870_a(BlockFence.field_196414_y, var17 && var12 != 0)).func_206870_a(BlockFence.field_196411_b, var17 && var12 != 4);
                     this.func_175811_a(var1, var18, var12, 5, var13, var3);
                  }
               }
            }
         }

         if (this.field_74913_b) {
            IBlockState var19 = (IBlockState)Blocks.field_150468_ap.func_176223_P().func_206870_a(BlockLadder.field_176382_a, EnumFacing.SOUTH);
            this.func_175811_a(var1, var19, 3, 1, 3, var3);
            this.func_175811_a(var1, var19, 3, 2, 3, var3);
            this.func_175811_a(var1, var19, 3, 3, 3, var3);
            this.func_175811_a(var1, var19, 3, 4, 3, var3);
         }

         this.func_189926_a(var1, EnumFacing.NORTH, 2, 3, 1, var3);

         for(int var20 = 0; var20 < 5; ++var20) {
            for(int var21 = 0; var21 < 5; ++var21) {
               this.func_74871_b(var1, var21, 6, var20, var3);
               this.func_175808_b(var1, var5, var21, -1, var20, var3);
            }
         }

         this.func_74893_a(var1, var3, 1, 1, 2, 1);
         return true;
      }
   }

   public static class Path extends VillagePieces.Road {
      private int field_74934_a;

      public Path() {
         super();
      }

      public Path(VillagePieces.Start var1, int var2, Random var3, MutableBoundingBox var4, EnumFacing var5) {
         super(var1, var2);
         this.func_186164_a(var5);
         this.field_74887_e = var4;
         this.field_74934_a = Math.max(var4.func_78883_b(), var4.func_78880_d());
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74768_a("Length", this.field_74934_a);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_74934_a = var1.func_74762_e("Length");
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         boolean var4 = false;

         int var5;
         StructurePiece var6;
         for(var5 = var3.nextInt(5); var5 < this.field_74934_a - 8; var5 += 2 + var3.nextInt(5)) {
            var6 = this.func_74891_a((VillagePieces.Start)var1, var2, var3, 0, var5);
            if (var6 != null) {
               var5 += Math.max(var6.field_74887_e.func_78883_b(), var6.field_74887_e.func_78880_d());
               var4 = true;
            }
         }

         for(var5 = var3.nextInt(5); var5 < this.field_74934_a - 8; var5 += 2 + var3.nextInt(5)) {
            var6 = this.func_74894_b((VillagePieces.Start)var1, var2, var3, 0, var5);
            if (var6 != null) {
               var5 += Math.max(var6.field_74887_e.func_78883_b(), var6.field_74887_e.func_78880_d());
               var4 = true;
            }
         }

         EnumFacing var7 = this.func_186165_e();
         if (var4 && var3.nextInt(3) > 0 && var7 != null) {
            switch(var7) {
            case NORTH:
            default:
               VillagePieces.func_176069_e((VillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, EnumFacing.WEST, this.func_74877_c());
               break;
            case SOUTH:
               VillagePieces.func_176069_e((VillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f - 2, EnumFacing.WEST, this.func_74877_c());
               break;
            case WEST:
               VillagePieces.func_176069_e((VillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c());
               break;
            case EAST:
               VillagePieces.func_176069_e((VillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78893_d - 2, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c());
            }
         }

         if (var4 && var3.nextInt(3) > 0 && var7 != null) {
            switch(var7) {
            case NORTH:
            default:
               VillagePieces.func_176069_e((VillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78896_c, EnumFacing.EAST, this.func_74877_c());
               break;
            case SOUTH:
               VillagePieces.func_176069_e((VillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f - 2, EnumFacing.EAST, this.func_74877_c());
               break;
            case WEST:
               VillagePieces.func_176069_e((VillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78897_a, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c());
               break;
            case EAST:
               VillagePieces.func_176069_e((VillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78893_d - 2, this.field_74887_e.field_78895_b, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c());
            }
         }

      }

      public static MutableBoundingBox func_175848_a(VillagePieces.Start var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6) {
         for(int var7 = 7 * MathHelper.func_76136_a(var2, 3, 5); var7 >= 7; var7 -= 7) {
            MutableBoundingBox var8 = MutableBoundingBox.func_175897_a(var3, var4, var5, 0, 0, 0, 3, 3, var7, var6);
            if (StructurePiece.func_74883_a(var1, var8) == null) {
               return var8;
            }
         }

         return null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         IBlockState var5 = this.func_175847_a(Blocks.field_185774_da.func_176223_P());
         IBlockState var6 = this.func_175847_a(Blocks.field_196662_n.func_176223_P());
         IBlockState var7 = this.func_175847_a(Blocks.field_150351_n.func_176223_P());
         IBlockState var8 = this.func_175847_a(Blocks.field_150347_e.func_176223_P());
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();
         this.field_74887_e.field_78895_b = 1000;
         this.field_74887_e.field_78894_e = 0;

         for(int var10 = this.field_74887_e.field_78897_a; var10 <= this.field_74887_e.field_78893_d; ++var10) {
            for(int var11 = this.field_74887_e.field_78896_c; var11 <= this.field_74887_e.field_78892_f; ++var11) {
               var9.func_181079_c(var10, 64, var11);
               if (var3.func_175898_b(var9)) {
                  int var12 = var1.func_201676_a(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, var9.func_177958_n(), var9.func_177952_p());
                  var9.func_181079_c(var9.func_177958_n(), var12, var9.func_177952_p()).func_189536_c(EnumFacing.DOWN);
                  if (var9.func_177956_o() < var1.func_181545_F()) {
                     var9.func_185336_p(var1.func_181545_F() - 1);
                  }

                  while(var9.func_177956_o() >= var1.func_181545_F() - 1) {
                     IBlockState var13 = var1.func_180495_p(var9);
                     Block var14 = var13.func_177230_c();
                     if (var14 == Blocks.field_196658_i && var1.func_175623_d(var9.func_177984_a())) {
                        var1.func_180501_a(var9, var5, 2);
                        break;
                     }

                     if (var13.func_185904_a().func_76224_d()) {
                        var1.func_180501_a(new BlockPos(var9), var6, 2);
                        break;
                     }

                     if (var14 == Blocks.field_150354_m || var14 == Blocks.field_196611_F || var14 == Blocks.field_150322_A || var14 == Blocks.field_196583_aj || var14 == Blocks.field_196585_ak || var14 == Blocks.field_180395_cM || var14 == Blocks.field_196583_aj || var14 == Blocks.field_196585_ak) {
                        var1.func_180501_a(var9, var7, 2);
                        var1.func_180501_a(var9.func_177977_b(), var8, 2);
                        break;
                     }

                     var9.func_189536_c(EnumFacing.DOWN);
                  }

                  this.field_74887_e.field_78895_b = Math.min(this.field_74887_e.field_78895_b, var9.func_177956_o());
                  this.field_74887_e.field_78894_e = Math.max(this.field_74887_e.field_78894_e, var9.func_177956_o());
               }
            }
         }

         return true;
      }
   }

   public abstract static class Road extends VillagePieces.Village {
      public Road() {
         super();
      }

      protected Road(VillagePieces.Start var1, int var2) {
         super(var1, var2);
      }
   }

   public static class Start extends VillagePieces.Well {
      public int field_74928_c;
      public VillagePieces.PieceWeight field_74926_d;
      public List<VillagePieces.PieceWeight> field_74931_h;
      public List<StructurePiece> field_74932_i = Lists.newArrayList();
      public List<StructurePiece> field_74930_j = Lists.newArrayList();

      public Start() {
         super();
      }

      public Start(int var1, Random var2, int var3, int var4, List<VillagePieces.PieceWeight> var5, VillageConfig var6) {
         super((VillagePieces.Start)null, 0, var2, var3, var4);
         this.field_74931_h = var5;
         this.field_74928_c = var6.field_202461_a;
         this.field_189928_h = var6.field_202462_b;
         this.func_202579_a(this.field_189928_h);
         this.field_189929_i = var2.nextInt(50) == 0;
      }
   }

   public static class Well extends VillagePieces.Village {
      public Well() {
         super();
      }

      public Well(VillagePieces.Start var1, int var2, Random var3, int var4, int var5) {
         super(var1, var2);
         this.func_186164_a(EnumFacing.Plane.HORIZONTAL.func_179518_a(var3));
         if (this.func_186165_e().func_176740_k() == EnumFacing.Axis.Z) {
            this.field_74887_e = new MutableBoundingBox(var4, 64, var5, var4 + 6 - 1, 78, var5 + 6 - 1);
         } else {
            this.field_74887_e = new MutableBoundingBox(var4, 64, var5, var4 + 6 - 1, 78, var5 + 6 - 1);
         }

      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         VillagePieces.func_176069_e((VillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78894_e - 4, this.field_74887_e.field_78896_c + 1, EnumFacing.WEST, this.func_74877_c());
         VillagePieces.func_176069_e((VillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78894_e - 4, this.field_74887_e.field_78896_c + 1, EnumFacing.EAST, this.func_74877_c());
         VillagePieces.func_176069_e((VillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78894_e - 4, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c());
         VillagePieces.func_176069_e((VillagePieces.Start)var1, var2, var3, this.field_74887_e.field_78897_a + 1, this.field_74887_e.field_78894_e - 4, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c());
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.field_143015_k < 0) {
            this.field_143015_k = this.func_74889_b(var1, var3);
            if (this.field_143015_k < 0) {
               return true;
            }

            this.field_74887_e.func_78886_a(0, this.field_143015_k - this.field_74887_e.field_78894_e + 3, 0);
         }

         IBlockState var5 = this.func_175847_a(Blocks.field_150347_e.func_176223_P());
         IBlockState var6 = this.func_175847_a(Blocks.field_180407_aO.func_176223_P());
         this.func_175804_a(var1, var3, 1, 0, 1, 4, 12, 4, var5, Blocks.field_150355_j.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 12, 2, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 3, 12, 2, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, 12, 3, var3);
         this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 3, 12, 3, var3);
         this.func_175811_a(var1, var6, 1, 13, 1, var3);
         this.func_175811_a(var1, var6, 1, 14, 1, var3);
         this.func_175811_a(var1, var6, 4, 13, 1, var3);
         this.func_175811_a(var1, var6, 4, 14, 1, var3);
         this.func_175811_a(var1, var6, 1, 13, 4, var3);
         this.func_175811_a(var1, var6, 1, 14, 4, var3);
         this.func_175811_a(var1, var6, 4, 13, 4, var3);
         this.func_175811_a(var1, var6, 4, 14, 4, var3);
         this.func_175804_a(var1, var3, 1, 15, 1, 4, 15, 4, var5, var5, false);

         for(int var7 = 0; var7 <= 5; ++var7) {
            for(int var8 = 0; var8 <= 5; ++var8) {
               if (var8 == 0 || var8 == 5 || var7 == 0 || var7 == 5) {
                  this.func_175811_a(var1, var5, var8, 11, var7, var3);
                  this.func_74871_b(var1, var8, 12, var7, var3);
               }
            }
         }

         return true;
      }
   }

   abstract static class Village extends StructurePiece {
      protected int field_143015_k = -1;
      private int field_74896_a;
      protected VillagePieces.Type field_189928_h;
      protected boolean field_189929_i;

      public Village() {
         super();
      }

      protected Village(VillagePieces.Start var1, int var2) {
         super(var2);
         if (var1 != null) {
            this.field_189928_h = var1.field_189928_h;
            this.field_189929_i = var1.field_189929_i;
         }

      }

      protected void func_143012_a(NBTTagCompound var1) {
         var1.func_74768_a("HPos", this.field_143015_k);
         var1.func_74768_a("VCount", this.field_74896_a);
         var1.func_74774_a("Type", (byte)this.field_189928_h.func_202604_a());
         var1.func_74757_a("Zombie", this.field_189929_i);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         this.field_143015_k = var1.func_74762_e("HPos");
         this.field_74896_a = var1.func_74762_e("VCount");
         this.field_189928_h = VillagePieces.Type.func_202603_a(var1.func_74771_c("Type"));
         if (var1.func_74767_n("Desert")) {
            this.field_189928_h = VillagePieces.Type.SANDSTONE;
         }

         this.field_189929_i = var1.func_74767_n("Zombie");
      }

      @Nullable
      protected StructurePiece func_74891_a(VillagePieces.Start var1, List<StructurePiece> var2, Random var3, int var4, int var5) {
         EnumFacing var6 = this.func_186165_e();
         if (var6 != null) {
            switch(var6) {
            case NORTH:
            default:
               return VillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.WEST, this.func_74877_c());
            case SOUTH:
               return VillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.WEST, this.func_74877_c());
            case WEST:
               return VillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c());
            case EAST:
               return VillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c());
            }
         } else {
            return null;
         }
      }

      @Nullable
      protected StructurePiece func_74894_b(VillagePieces.Start var1, List<StructurePiece> var2, Random var3, int var4, int var5) {
         EnumFacing var6 = this.func_186165_e();
         if (var6 != null) {
            switch(var6) {
            case NORTH:
            default:
               return VillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.EAST, this.func_74877_c());
            case SOUTH:
               return VillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.EAST, this.func_74877_c());
            case WEST:
               return VillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c());
            case EAST:
               return VillagePieces.func_176066_d(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c());
            }
         } else {
            return null;
         }
      }

      protected int func_74889_b(IWorld var1, MutableBoundingBox var2) {
         int var3 = 0;
         int var4 = 0;
         BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

         for(int var6 = this.field_74887_e.field_78896_c; var6 <= this.field_74887_e.field_78892_f; ++var6) {
            for(int var7 = this.field_74887_e.field_78897_a; var7 <= this.field_74887_e.field_78893_d; ++var7) {
               var5.func_181079_c(var7, 64, var6);
               if (var2.func_175898_b(var5)) {
                  var3 += var1.func_205770_a(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, var5).func_177956_o();
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

      protected static boolean func_74895_a(MutableBoundingBox var0) {
         return var0 != null && var0.field_78895_b > 10;
      }

      protected void func_74893_a(IWorld var1, MutableBoundingBox var2, int var3, int var4, int var5, int var6) {
         if (this.field_74896_a < var6) {
            for(int var7 = this.field_74896_a; var7 < var6; ++var7) {
               int var8 = this.func_74865_a(var3 + var7, var5);
               int var9 = this.func_74862_a(var4);
               int var10 = this.func_74873_b(var3 + var7, var5);
               if (!var2.func_175898_b(new BlockPos(var8, var9, var10))) {
                  break;
               }

               ++this.field_74896_a;
               if (this.field_189929_i) {
                  EntityZombieVillager var11 = new EntityZombieVillager(var1.func_201672_e());
                  var11.func_70012_b((double)var8 + 0.5D, (double)var9, (double)var10 + 0.5D, 0.0F, 0.0F);
                  var11.func_204210_a(var1.func_175649_E(new BlockPos(var11)), (IEntityLivingData)null, (NBTTagCompound)null);
                  var11.func_190733_a(this.func_180779_c(var7, 0));
                  var11.func_110163_bv();
                  var1.func_72838_d(var11);
               } else {
                  EntityVillager var12 = new EntityVillager(var1.func_201672_e());
                  var12.func_70012_b((double)var8 + 0.5D, (double)var9, (double)var10 + 0.5D, 0.0F, 0.0F);
                  var12.func_70938_b(this.func_180779_c(var7, var1.func_201674_k().nextInt(6)));
                  var12.func_190672_a(var1.func_175649_E(new BlockPos(var12)), (IEntityLivingData)null, (NBTTagCompound)null, false);
                  var1.func_72838_d(var12);
               }
            }

         }
      }

      protected int func_180779_c(int var1, int var2) {
         return var2;
      }

      protected IBlockState func_175847_a(IBlockState var1) {
         Block var2 = var1.func_177230_c();
         if (this.field_189928_h == VillagePieces.Type.SANDSTONE) {
            if (var2.func_203417_a(BlockTags.field_200031_h) || var2 == Blocks.field_150347_e) {
               return Blocks.field_150322_A.func_176223_P();
            }

            if (var2.func_203417_a(BlockTags.field_199898_b)) {
               return Blocks.field_196585_ak.func_176223_P();
            }

            if (var2 == Blocks.field_150476_ad) {
               return (IBlockState)Blocks.field_150372_bz.func_176223_P().func_206870_a(BlockStairs.field_176309_a, var1.func_177229_b(BlockStairs.field_176309_a));
            }

            if (var2 == Blocks.field_196659_cl) {
               return (IBlockState)Blocks.field_150372_bz.func_176223_P().func_206870_a(BlockStairs.field_176309_a, var1.func_177229_b(BlockStairs.field_176309_a));
            }

            if (var2 == Blocks.field_150351_n) {
               return Blocks.field_150322_A.func_176223_P();
            }

            if (var2 == Blocks.field_196663_cq) {
               return Blocks.field_196667_cs.func_176223_P();
            }
         } else if (this.field_189928_h == VillagePieces.Type.SPRUCE) {
            if (var2.func_203417_a(BlockTags.field_200031_h)) {
               return (IBlockState)Blocks.field_196618_L.func_176223_P().func_206870_a(BlockLog.field_176298_M, var1.func_177229_b(BlockLog.field_176298_M));
            }

            if (var2.func_203417_a(BlockTags.field_199898_b)) {
               return Blocks.field_196664_o.func_176223_P();
            }

            if (var2 == Blocks.field_150476_ad) {
               return (IBlockState)Blocks.field_150485_bF.func_176223_P().func_206870_a(BlockStairs.field_176309_a, var1.func_177229_b(BlockStairs.field_176309_a));
            }

            if (var2 == Blocks.field_180407_aO) {
               return Blocks.field_180408_aP.func_176223_P();
            }

            if (var2 == Blocks.field_196663_cq) {
               return Blocks.field_196665_cr.func_176223_P();
            }
         } else if (this.field_189928_h == VillagePieces.Type.ACACIA) {
            if (var2.func_203417_a(BlockTags.field_200031_h)) {
               return (IBlockState)Blocks.field_196621_O.func_176223_P().func_206870_a(BlockLog.field_176298_M, var1.func_177229_b(BlockLog.field_176298_M));
            }

            if (var2.func_203417_a(BlockTags.field_199898_b)) {
               return Blocks.field_196670_r.func_176223_P();
            }

            if (var2 == Blocks.field_150476_ad) {
               return (IBlockState)Blocks.field_150400_ck.func_176223_P().func_206870_a(BlockStairs.field_176309_a, var1.func_177229_b(BlockStairs.field_176309_a));
            }

            if (var2 == Blocks.field_150347_e) {
               return (IBlockState)Blocks.field_196621_O.func_176223_P().func_206870_a(BlockLog.field_176298_M, EnumFacing.Axis.Y);
            }

            if (var2 == Blocks.field_180407_aO) {
               return Blocks.field_180405_aT.func_176223_P();
            }

            if (var2 == Blocks.field_196663_cq) {
               return Blocks.field_196671_cu.func_176223_P();
            }
         }

         return var1;
      }

      protected BlockDoor func_189925_i() {
         if (this.field_189928_h == VillagePieces.Type.ACACIA) {
            return (BlockDoor)Blocks.field_180410_as;
         } else {
            return this.field_189928_h == VillagePieces.Type.SPRUCE ? (BlockDoor)Blocks.field_180414_ap : (BlockDoor)Blocks.field_180413_ao;
         }
      }

      protected void func_189927_a(IWorld var1, MutableBoundingBox var2, Random var3, int var4, int var5, int var6, EnumFacing var7) {
         if (!this.field_189929_i) {
            this.func_189915_a(var1, var2, var3, var4, var5, var6, EnumFacing.NORTH, this.func_189925_i());
         }

      }

      protected void func_189926_a(IWorld var1, EnumFacing var2, int var3, int var4, int var5, MutableBoundingBox var6) {
         if (!this.field_189929_i) {
            this.func_175811_a(var1, (IBlockState)Blocks.field_196591_bQ.func_176223_P().func_206870_a(BlockTorchWall.field_196532_a, var2), var3, var4, var5, var6);
         }

      }

      protected void func_175808_b(IWorld var1, IBlockState var2, int var3, int var4, int var5, MutableBoundingBox var6) {
         IBlockState var7 = this.func_175847_a(var2);
         super.func_175808_b(var1, var7, var3, var4, var5, var6);
      }

      protected void func_202579_a(VillagePieces.Type var1) {
         this.field_189928_h = var1;
      }
   }

   public static class PieceWeight {
      public Class<? extends VillagePieces.Village> field_75090_a;
      public final int field_75088_b;
      public int field_75089_c;
      public int field_75087_d;

      public PieceWeight(Class<? extends VillagePieces.Village> var1, int var2, int var3) {
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

   public static enum Type {
      OAK(0),
      SANDSTONE(1),
      ACACIA(2),
      SPRUCE(3);

      private final int field_202605_e;

      private Type(int var3) {
         this.field_202605_e = var3;
      }

      public int func_202604_a() {
         return this.field_202605_e;
      }

      public static VillagePieces.Type func_202603_a(int var0) {
         VillagePieces.Type[] var1 = values();
         return var0 >= 0 && var0 < var1.length ? var1[var0] : OAK;
      }
   }
}
