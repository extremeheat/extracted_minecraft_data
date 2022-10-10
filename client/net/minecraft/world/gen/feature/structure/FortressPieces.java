package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class FortressPieces {
   private static final FortressPieces.PieceWeight[] field_78742_a = new FortressPieces.PieceWeight[]{new FortressPieces.PieceWeight(FortressPieces.Straight.class, 30, 0, true), new FortressPieces.PieceWeight(FortressPieces.Crossing3.class, 10, 4), new FortressPieces.PieceWeight(FortressPieces.Crossing.class, 10, 4), new FortressPieces.PieceWeight(FortressPieces.Stairs.class, 10, 3), new FortressPieces.PieceWeight(FortressPieces.Throne.class, 5, 2), new FortressPieces.PieceWeight(FortressPieces.Entrance.class, 5, 1)};
   private static final FortressPieces.PieceWeight[] field_78741_b = new FortressPieces.PieceWeight[]{new FortressPieces.PieceWeight(FortressPieces.Corridor5.class, 25, 0, true), new FortressPieces.PieceWeight(FortressPieces.Crossing2.class, 15, 5), new FortressPieces.PieceWeight(FortressPieces.Corridor2.class, 5, 10), new FortressPieces.PieceWeight(FortressPieces.Corridor.class, 5, 10), new FortressPieces.PieceWeight(FortressPieces.Corridor3.class, 10, 3, true), new FortressPieces.PieceWeight(FortressPieces.Corridor4.class, 7, 2), new FortressPieces.PieceWeight(FortressPieces.NetherStalkRoom.class, 5, 2)};

   public static void func_143049_a() {
      StructureIO.func_143031_a(FortressPieces.Crossing3.class, "NeBCr");
      StructureIO.func_143031_a(FortressPieces.End.class, "NeBEF");
      StructureIO.func_143031_a(FortressPieces.Straight.class, "NeBS");
      StructureIO.func_143031_a(FortressPieces.Corridor3.class, "NeCCS");
      StructureIO.func_143031_a(FortressPieces.Corridor4.class, "NeCTB");
      StructureIO.func_143031_a(FortressPieces.Entrance.class, "NeCE");
      StructureIO.func_143031_a(FortressPieces.Crossing2.class, "NeSCSC");
      StructureIO.func_143031_a(FortressPieces.Corridor.class, "NeSCLT");
      StructureIO.func_143031_a(FortressPieces.Corridor5.class, "NeSC");
      StructureIO.func_143031_a(FortressPieces.Corridor2.class, "NeSCRT");
      StructureIO.func_143031_a(FortressPieces.NetherStalkRoom.class, "NeCSR");
      StructureIO.func_143031_a(FortressPieces.Throne.class, "NeMT");
      StructureIO.func_143031_a(FortressPieces.Crossing.class, "NeRC");
      StructureIO.func_143031_a(FortressPieces.Stairs.class, "NeSR");
      StructureIO.func_143031_a(FortressPieces.Start.class, "NeStart");
   }

   private static FortressPieces.Piece func_175887_b(FortressPieces.PieceWeight var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
      Class var8 = var0.field_78828_a;
      Object var9 = null;
      if (var8 == FortressPieces.Straight.class) {
         var9 = FortressPieces.Straight.func_175882_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == FortressPieces.Crossing3.class) {
         var9 = FortressPieces.Crossing3.func_175885_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == FortressPieces.Crossing.class) {
         var9 = FortressPieces.Crossing.func_175873_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == FortressPieces.Stairs.class) {
         var9 = FortressPieces.Stairs.func_175872_a(var1, var2, var3, var4, var5, var7, var6);
      } else if (var8 == FortressPieces.Throne.class) {
         var9 = FortressPieces.Throne.func_175874_a(var1, var2, var3, var4, var5, var7, var6);
      } else if (var8 == FortressPieces.Entrance.class) {
         var9 = FortressPieces.Entrance.func_175881_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == FortressPieces.Corridor5.class) {
         var9 = FortressPieces.Corridor5.func_175877_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == FortressPieces.Corridor2.class) {
         var9 = FortressPieces.Corridor2.func_175876_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == FortressPieces.Corridor.class) {
         var9 = FortressPieces.Corridor.func_175879_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == FortressPieces.Corridor3.class) {
         var9 = FortressPieces.Corridor3.func_175883_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == FortressPieces.Corridor4.class) {
         var9 = FortressPieces.Corridor4.func_175880_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == FortressPieces.Crossing2.class) {
         var9 = FortressPieces.Crossing2.func_175878_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == FortressPieces.NetherStalkRoom.class) {
         var9 = FortressPieces.NetherStalkRoom.func_175875_a(var1, var2, var3, var4, var5, var6, var7);
      }

      return (FortressPieces.Piece)var9;
   }

   public static class Corridor4 extends FortressPieces.Piece {
      public Corridor4() {
         super();
      }

      public Corridor4(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         byte var4 = 1;
         EnumFacing var5 = this.func_186165_e();
         if (var5 == EnumFacing.WEST || var5 == EnumFacing.NORTH) {
            var4 = 5;
         }

         this.func_74961_b((FortressPieces.Start)var1, var2, var3, 0, var4, var3.nextInt(8) > 0);
         this.func_74965_c((FortressPieces.Start)var1, var2, var3, 0, var4, var3.nextInt(8) > 0);
      }

      public static FortressPieces.Corridor4 func_175880_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -3, 0, 0, 9, 7, 9, var5);
         return func_74964_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new FortressPieces.Corridor4(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         IBlockState var5 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196413_c, true);
         IBlockState var6 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196414_y, true)).func_206870_a(BlockFence.field_196411_b, true);
         this.func_175804_a(var1, var3, 0, 0, 0, 8, 1, 8, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 8, 5, 8, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 6, 0, 8, 6, 5, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 2, 5, 0, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 2, 0, 8, 5, 0, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 3, 0, 1, 4, 0, var6, var6, false);
         this.func_175804_a(var1, var3, 7, 3, 0, 7, 4, 0, var6, var6, false);
         this.func_175804_a(var1, var3, 0, 2, 4, 8, 2, 8, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 1, 4, 2, 2, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 1, 4, 7, 2, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 3, 8, 7, 3, 8, var6, var6, false);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196411_b, true)).func_206870_a(BlockFence.field_196413_c, true), 0, 3, 8, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196414_y, true)).func_206870_a(BlockFence.field_196413_c, true), 8, 3, 8, var3);
         this.func_175804_a(var1, var3, 0, 3, 6, 0, 3, 7, var5, var5, false);
         this.func_175804_a(var1, var3, 8, 3, 6, 8, 3, 7, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 3, 4, 0, 5, 5, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 3, 4, 8, 5, 5, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 3, 5, 2, 5, 5, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 3, 5, 7, 5, 5, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 4, 5, 1, 5, 5, var6, var6, false);
         this.func_175804_a(var1, var3, 7, 4, 5, 7, 5, 5, var6, var6, false);

         for(int var7 = 0; var7 <= 5; ++var7) {
            for(int var8 = 0; var8 <= 8; ++var8) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var8, -1, var7, var3);
            }
         }

         return true;
      }
   }

   public static class Corridor3 extends FortressPieces.Piece {
      public Corridor3() {
         super();
      }

      public Corridor3(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74963_a((FortressPieces.Start)var1, var2, var3, 1, 0, true);
      }

      public static FortressPieces.Corridor3 func_175883_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -1, -7, 0, 5, 14, 10, var5);
         return func_74964_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new FortressPieces.Corridor3(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         IBlockState var5 = (IBlockState)Blocks.field_150387_bl.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.SOUTH);
         IBlockState var6 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196413_c, true);

         for(int var7 = 0; var7 <= 9; ++var7) {
            int var8 = Math.max(1, 7 - var7);
            int var9 = Math.min(Math.max(var8 + 5, 14 - var7), 13);
            int var10 = var7;
            this.func_175804_a(var1, var3, 0, 0, var7, 4, var8, var7, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
            this.func_175804_a(var1, var3, 1, var8 + 1, var7, 3, var9 - 1, var7, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            if (var7 <= 6) {
               this.func_175811_a(var1, var5, 1, var8 + 1, var7, var3);
               this.func_175811_a(var1, var5, 2, var8 + 1, var7, var3);
               this.func_175811_a(var1, var5, 3, var8 + 1, var7, var3);
            }

            this.func_175804_a(var1, var3, 0, var9, var7, 4, var9, var7, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
            this.func_175804_a(var1, var3, 0, var8 + 1, var7, 0, var9 - 1, var7, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
            this.func_175804_a(var1, var3, 4, var8 + 1, var7, 4, var9 - 1, var7, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
            if ((var7 & 1) == 0) {
               this.func_175804_a(var1, var3, 0, var8 + 2, var7, 0, var8 + 3, var7, var6, var6, false);
               this.func_175804_a(var1, var3, 4, var8 + 2, var7, 4, var8 + 3, var7, var6, var6, false);
            }

            for(int var11 = 0; var11 <= 4; ++var11) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var11, -1, var10, var3);
            }
         }

         return true;
      }
   }

   public static class Corridor extends FortressPieces.Piece {
      private boolean field_111021_b;

      public Corridor() {
         super();
      }

      public Corridor(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
         this.field_111021_b = var2.nextInt(3) == 0;
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_111021_b = var1.func_74767_n("Chest");
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Chest", this.field_111021_b);
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74961_b((FortressPieces.Start)var1, var2, var3, 0, 1, true);
      }

      public static FortressPieces.Corridor func_175879_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -1, 0, 0, 5, 7, 5, var5);
         return func_74964_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new FortressPieces.Corridor(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_175804_a(var1, var3, 0, 0, 0, 4, 1, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 4, 5, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         IBlockState var5 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196414_y, true)).func_206870_a(BlockFence.field_196411_b, true);
         IBlockState var6 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196413_c, true);
         this.func_175804_a(var1, var3, 4, 2, 0, 4, 5, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 3, 1, 4, 4, 1, var6, var6, false);
         this.func_175804_a(var1, var3, 4, 3, 3, 4, 4, 3, var6, var6, false);
         this.func_175804_a(var1, var3, 0, 2, 0, 0, 5, 0, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 4, 3, 5, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 3, 4, 1, 4, 4, var5, var5, false);
         this.func_175804_a(var1, var3, 3, 3, 4, 3, 4, 4, var5, var5, false);
         if (this.field_111021_b && var3.func_175898_b(new BlockPos(this.func_74865_a(3, 3), this.func_74862_a(2), this.func_74873_b(3, 3)))) {
            this.field_111021_b = false;
            this.func_186167_a(var1, var3, var2, 3, 2, 3, LootTableList.field_186425_g);
         }

         this.func_175804_a(var1, var3, 0, 6, 0, 4, 6, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);

         for(int var7 = 0; var7 <= 4; ++var7) {
            for(int var8 = 0; var8 <= 4; ++var8) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var7, -1, var8, var3);
            }
         }

         return true;
      }
   }

   public static class Corridor2 extends FortressPieces.Piece {
      private boolean field_111020_b;

      public Corridor2() {
         super();
      }

      public Corridor2(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
         this.field_111020_b = var2.nextInt(3) == 0;
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_111020_b = var1.func_74767_n("Chest");
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Chest", this.field_111020_b);
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74965_c((FortressPieces.Start)var1, var2, var3, 0, 1, true);
      }

      public static FortressPieces.Corridor2 func_175876_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -1, 0, 0, 5, 7, 5, var5);
         return func_74964_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new FortressPieces.Corridor2(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_175804_a(var1, var3, 0, 0, 0, 4, 1, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 4, 5, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         IBlockState var5 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196414_y, true)).func_206870_a(BlockFence.field_196411_b, true);
         IBlockState var6 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196413_c, true);
         this.func_175804_a(var1, var3, 0, 2, 0, 0, 5, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 3, 1, 0, 4, 1, var6, var6, false);
         this.func_175804_a(var1, var3, 0, 3, 3, 0, 4, 3, var6, var6, false);
         this.func_175804_a(var1, var3, 4, 2, 0, 4, 5, 0, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 4, 4, 5, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 3, 4, 1, 4, 4, var5, var5, false);
         this.func_175804_a(var1, var3, 3, 3, 4, 3, 4, 4, var5, var5, false);
         if (this.field_111020_b && var3.func_175898_b(new BlockPos(this.func_74865_a(1, 3), this.func_74862_a(2), this.func_74873_b(1, 3)))) {
            this.field_111020_b = false;
            this.func_186167_a(var1, var3, var2, 1, 2, 3, LootTableList.field_186425_g);
         }

         this.func_175804_a(var1, var3, 0, 6, 0, 4, 6, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);

         for(int var7 = 0; var7 <= 4; ++var7) {
            for(int var8 = 0; var8 <= 4; ++var8) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var7, -1, var8, var3);
            }
         }

         return true;
      }
   }

   public static class Crossing2 extends FortressPieces.Piece {
      public Crossing2() {
         super();
      }

      public Crossing2(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74963_a((FortressPieces.Start)var1, var2, var3, 1, 0, true);
         this.func_74961_b((FortressPieces.Start)var1, var2, var3, 0, 1, true);
         this.func_74965_c((FortressPieces.Start)var1, var2, var3, 0, 1, true);
      }

      public static FortressPieces.Crossing2 func_175878_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -1, 0, 0, 5, 7, 5, var5);
         return func_74964_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new FortressPieces.Crossing2(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_175804_a(var1, var3, 0, 0, 0, 4, 1, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 4, 5, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 0, 5, 0, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 2, 0, 4, 5, 0, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 4, 0, 5, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 2, 4, 4, 5, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 6, 0, 4, 6, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);

         for(int var5 = 0; var5 <= 4; ++var5) {
            for(int var6 = 0; var6 <= 4; ++var6) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var5, -1, var6, var3);
            }
         }

         return true;
      }
   }

   public static class Corridor5 extends FortressPieces.Piece {
      public Corridor5() {
         super();
      }

      public Corridor5(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74963_a((FortressPieces.Start)var1, var2, var3, 1, 0, true);
      }

      public static FortressPieces.Corridor5 func_175877_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -1, 0, 0, 5, 7, 5, var5);
         return func_74964_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new FortressPieces.Corridor5(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_175804_a(var1, var3, 0, 0, 0, 4, 1, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 4, 5, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         IBlockState var5 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196413_c, true);
         this.func_175804_a(var1, var3, 0, 2, 0, 0, 5, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 2, 0, 4, 5, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 3, 1, 0, 4, 1, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 3, 3, 0, 4, 3, var5, var5, false);
         this.func_175804_a(var1, var3, 4, 3, 1, 4, 4, 1, var5, var5, false);
         this.func_175804_a(var1, var3, 4, 3, 3, 4, 4, 3, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 6, 0, 4, 6, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);

         for(int var6 = 0; var6 <= 4; ++var6) {
            for(int var7 = 0; var7 <= 4; ++var7) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var6, -1, var7, var3);
            }
         }

         return true;
      }
   }

   public static class NetherStalkRoom extends FortressPieces.Piece {
      public NetherStalkRoom() {
         super();
      }

      public NetherStalkRoom(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74963_a((FortressPieces.Start)var1, var2, var3, 5, 3, true);
         this.func_74963_a((FortressPieces.Start)var1, var2, var3, 5, 11, true);
      }

      public static FortressPieces.NetherStalkRoom func_175875_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -5, -3, 0, 13, 14, 13, var5);
         return func_74964_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new FortressPieces.NetherStalkRoom(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_175804_a(var1, var3, 0, 3, 0, 12, 4, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 0, 12, 13, 12, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 0, 1, 12, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 11, 5, 0, 12, 12, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 11, 4, 12, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 5, 11, 10, 12, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 9, 11, 7, 12, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 0, 4, 12, 1, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 5, 0, 10, 12, 1, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 9, 0, 7, 12, 1, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 11, 2, 10, 12, 10, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         IBlockState var5 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196414_y, true)).func_206870_a(BlockFence.field_196411_b, true);
         IBlockState var6 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196413_c, true);
         IBlockState var7 = (IBlockState)var6.func_206870_a(BlockFence.field_196414_y, true);
         IBlockState var8 = (IBlockState)var6.func_206870_a(BlockFence.field_196411_b, true);

         int var9;
         for(var9 = 1; var9 <= 11; var9 += 2) {
            this.func_175804_a(var1, var3, var9, 10, 0, var9, 11, 0, var5, var5, false);
            this.func_175804_a(var1, var3, var9, 10, 12, var9, 11, 12, var5, var5, false);
            this.func_175804_a(var1, var3, 0, 10, var9, 0, 11, var9, var6, var6, false);
            this.func_175804_a(var1, var3, 12, 10, var9, 12, 11, var9, var6, var6, false);
            this.func_175811_a(var1, Blocks.field_196653_dH.func_176223_P(), var9, 13, 0, var3);
            this.func_175811_a(var1, Blocks.field_196653_dH.func_176223_P(), var9, 13, 12, var3);
            this.func_175811_a(var1, Blocks.field_196653_dH.func_176223_P(), 0, 13, var9, var3);
            this.func_175811_a(var1, Blocks.field_196653_dH.func_176223_P(), 12, 13, var9, var3);
            if (var9 != 11) {
               this.func_175811_a(var1, var5, var9 + 1, 13, 0, var3);
               this.func_175811_a(var1, var5, var9 + 1, 13, 12, var3);
               this.func_175811_a(var1, var6, 0, 13, var9 + 1, var3);
               this.func_175811_a(var1, var6, 12, 13, var9 + 1, var3);
            }
         }

         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196411_b, true), 0, 13, 0, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196413_c, true)).func_206870_a(BlockFence.field_196411_b, true), 0, 13, 12, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196413_c, true)).func_206870_a(BlockFence.field_196414_y, true), 12, 13, 12, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196414_y, true), 12, 13, 0, var3);

         for(var9 = 3; var9 <= 9; var9 += 2) {
            this.func_175804_a(var1, var3, 1, 7, var9, 1, 8, var9, var7, var7, false);
            this.func_175804_a(var1, var3, 11, 7, var9, 11, 8, var9, var8, var8, false);
         }

         IBlockState var14 = (IBlockState)Blocks.field_150387_bl.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.NORTH);

         int var10;
         int var12;
         for(var10 = 0; var10 <= 6; ++var10) {
            int var11 = var10 + 4;

            for(var12 = 5; var12 <= 7; ++var12) {
               this.func_175811_a(var1, var14, var12, 5 + var10, var11, var3);
            }

            if (var11 >= 5 && var11 <= 8) {
               this.func_175804_a(var1, var3, 5, 5, var11, 7, var10 + 4, var11, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
            } else if (var11 >= 9 && var11 <= 10) {
               this.func_175804_a(var1, var3, 5, 8, var11, 7, var10 + 4, var11, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
            }

            if (var10 >= 1) {
               this.func_175804_a(var1, var3, 5, 6 + var10, var11, 7, 9 + var10, var11, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            }
         }

         for(var10 = 5; var10 <= 7; ++var10) {
            this.func_175811_a(var1, var14, var10, 12, 11, var3);
         }

         this.func_175804_a(var1, var3, 5, 6, 7, 5, 7, 7, var8, var8, false);
         this.func_175804_a(var1, var3, 7, 6, 7, 7, 7, 7, var7, var7, false);
         this.func_175804_a(var1, var3, 5, 13, 12, 7, 13, 12, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 2, 3, 5, 3, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 9, 3, 5, 10, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 4, 2, 5, 8, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, 5, 2, 10, 5, 3, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, 5, 9, 10, 5, 10, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 10, 5, 4, 10, 5, 8, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         IBlockState var15 = (IBlockState)var14.func_206870_a(BlockStairs.field_176309_a, EnumFacing.EAST);
         IBlockState var16 = (IBlockState)var14.func_206870_a(BlockStairs.field_176309_a, EnumFacing.WEST);
         this.func_175811_a(var1, var16, 4, 5, 2, var3);
         this.func_175811_a(var1, var16, 4, 5, 3, var3);
         this.func_175811_a(var1, var16, 4, 5, 9, var3);
         this.func_175811_a(var1, var16, 4, 5, 10, var3);
         this.func_175811_a(var1, var15, 8, 5, 2, var3);
         this.func_175811_a(var1, var15, 8, 5, 3, var3);
         this.func_175811_a(var1, var15, 8, 5, 9, var3);
         this.func_175811_a(var1, var15, 8, 5, 10, var3);
         this.func_175804_a(var1, var3, 3, 4, 4, 4, 4, 8, Blocks.field_150425_aM.func_176223_P(), Blocks.field_150425_aM.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 4, 4, 9, 4, 8, Blocks.field_150425_aM.func_176223_P(), Blocks.field_150425_aM.func_176223_P(), false);
         this.func_175804_a(var1, var3, 3, 5, 4, 4, 5, 8, Blocks.field_150388_bm.func_176223_P(), Blocks.field_150388_bm.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 5, 4, 9, 5, 8, Blocks.field_150388_bm.func_176223_P(), Blocks.field_150388_bm.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 2, 0, 8, 2, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 4, 12, 2, 8, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 0, 0, 8, 1, 3, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 0, 9, 8, 1, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 4, 3, 1, 8, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, 0, 4, 12, 1, 8, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);

         int var13;
         for(var12 = 4; var12 <= 8; ++var12) {
            for(var13 = 0; var13 <= 2; ++var13) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var12, -1, var13, var3);
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var12, -1, 12 - var13, var3);
            }
         }

         for(var12 = 0; var12 <= 2; ++var12) {
            for(var13 = 4; var13 <= 8; ++var13) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var12, -1, var13, var3);
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), 12 - var12, -1, var13, var3);
            }
         }

         return true;
      }
   }

   public static class Entrance extends FortressPieces.Piece {
      public Entrance() {
         super();
      }

      public Entrance(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74963_a((FortressPieces.Start)var1, var2, var3, 5, 3, true);
      }

      public static FortressPieces.Entrance func_175881_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -5, -3, 0, 13, 14, 13, var5);
         return func_74964_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new FortressPieces.Entrance(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_175804_a(var1, var3, 0, 3, 0, 12, 4, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 0, 12, 13, 12, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 0, 1, 12, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 11, 5, 0, 12, 12, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 11, 4, 12, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 5, 11, 10, 12, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 9, 11, 7, 12, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 0, 4, 12, 1, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 5, 0, 10, 12, 1, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 9, 0, 7, 12, 1, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 11, 2, 10, 12, 10, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 8, 0, 7, 8, 0, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         IBlockState var5 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196414_y, true)).func_206870_a(BlockFence.field_196411_b, true);
         IBlockState var6 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196413_c, true);

         int var7;
         for(var7 = 1; var7 <= 11; var7 += 2) {
            this.func_175804_a(var1, var3, var7, 10, 0, var7, 11, 0, var5, var5, false);
            this.func_175804_a(var1, var3, var7, 10, 12, var7, 11, 12, var5, var5, false);
            this.func_175804_a(var1, var3, 0, 10, var7, 0, 11, var7, var6, var6, false);
            this.func_175804_a(var1, var3, 12, 10, var7, 12, 11, var7, var6, var6, false);
            this.func_175811_a(var1, Blocks.field_196653_dH.func_176223_P(), var7, 13, 0, var3);
            this.func_175811_a(var1, Blocks.field_196653_dH.func_176223_P(), var7, 13, 12, var3);
            this.func_175811_a(var1, Blocks.field_196653_dH.func_176223_P(), 0, 13, var7, var3);
            this.func_175811_a(var1, Blocks.field_196653_dH.func_176223_P(), 12, 13, var7, var3);
            if (var7 != 11) {
               this.func_175811_a(var1, var5, var7 + 1, 13, 0, var3);
               this.func_175811_a(var1, var5, var7 + 1, 13, 12, var3);
               this.func_175811_a(var1, var6, 0, 13, var7 + 1, var3);
               this.func_175811_a(var1, var6, 12, 13, var7 + 1, var3);
            }
         }

         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196411_b, true), 0, 13, 0, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196413_c, true)).func_206870_a(BlockFence.field_196411_b, true), 0, 13, 12, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196413_c, true)).func_206870_a(BlockFence.field_196414_y, true), 12, 13, 12, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196414_y, true), 12, 13, 0, var3);

         for(var7 = 3; var7 <= 9; var7 += 2) {
            this.func_175804_a(var1, var3, 1, 7, var7, 1, 8, var7, (IBlockState)var6.func_206870_a(BlockFence.field_196414_y, true), (IBlockState)var6.func_206870_a(BlockFence.field_196414_y, true), false);
            this.func_175804_a(var1, var3, 11, 7, var7, 11, 8, var7, (IBlockState)var6.func_206870_a(BlockFence.field_196411_b, true), (IBlockState)var6.func_206870_a(BlockFence.field_196411_b, true), false);
         }

         this.func_175804_a(var1, var3, 4, 2, 0, 8, 2, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 4, 12, 2, 8, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 0, 0, 8, 1, 3, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 0, 9, 8, 1, 12, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 4, 3, 1, 8, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, 0, 4, 12, 1, 8, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);

         int var8;
         for(var7 = 4; var7 <= 8; ++var7) {
            for(var8 = 0; var8 <= 2; ++var8) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var7, -1, var8, var3);
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var7, -1, 12 - var8, var3);
            }
         }

         for(var7 = 0; var7 <= 2; ++var7) {
            for(var8 = 4; var8 <= 8; ++var8) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var7, -1, var8, var3);
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), 12 - var7, -1, var8, var3);
            }
         }

         this.func_175804_a(var1, var3, 5, 5, 5, 7, 5, 7, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 1, 6, 6, 4, 6, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_196653_dH.func_176223_P(), 6, 0, 6, var3);
         this.func_175811_a(var1, Blocks.field_150353_l.func_176223_P(), 6, 5, 6, var3);
         BlockPos var9 = new BlockPos(this.func_74865_a(6, 6), this.func_74862_a(5), this.func_74873_b(6, 6));
         if (var3.func_175898_b(var9)) {
            var1.func_205219_F_().func_205360_a(var9, Fluids.field_204547_b, 0);
         }

         return true;
      }
   }

   public static class Throne extends FortressPieces.Piece {
      private boolean field_74976_a;

      public Throne() {
         super();
      }

      public Throne(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_74976_a = var1.func_74767_n("Mob");
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Mob", this.field_74976_a);
      }

      public static FortressPieces.Throne func_175874_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, int var5, EnumFacing var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -2, 0, 0, 7, 8, 9, var6);
         return func_74964_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new FortressPieces.Throne(var5, var1, var7, var6) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_175804_a(var1, var3, 0, 2, 0, 6, 7, 7, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 0, 5, 1, 7, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 1, 5, 2, 7, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 3, 2, 5, 3, 7, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 4, 3, 5, 4, 7, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 0, 1, 4, 2, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 2, 0, 5, 4, 2, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 5, 2, 1, 5, 3, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 5, 2, 5, 5, 3, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 3, 0, 5, 8, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 5, 3, 6, 5, 8, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 5, 8, 5, 5, 8, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         IBlockState var5 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196414_y, true)).func_206870_a(BlockFence.field_196411_b, true);
         IBlockState var6 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196413_c, true);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196414_y, true), 1, 6, 3, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196411_b, true), 5, 6, 3, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196411_b, true)).func_206870_a(BlockFence.field_196409_a, true), 0, 6, 3, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196414_y, true)).func_206870_a(BlockFence.field_196409_a, true), 6, 6, 3, var3);
         this.func_175804_a(var1, var3, 0, 6, 4, 0, 6, 7, var6, var6, false);
         this.func_175804_a(var1, var3, 6, 6, 4, 6, 6, 7, var6, var6, false);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196411_b, true)).func_206870_a(BlockFence.field_196413_c, true), 0, 6, 8, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196414_y, true)).func_206870_a(BlockFence.field_196413_c, true), 6, 6, 8, var3);
         this.func_175804_a(var1, var3, 1, 6, 8, 5, 6, 8, var5, var5, false);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196411_b, true), 1, 7, 8, var3);
         this.func_175804_a(var1, var3, 2, 7, 8, 4, 7, 8, var5, var5, false);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196414_y, true), 5, 7, 8, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196411_b, true), 2, 8, 8, var3);
         this.func_175811_a(var1, var5, 3, 8, 8, var3);
         this.func_175811_a(var1, (IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196414_y, true), 4, 8, 8, var3);
         if (!this.field_74976_a) {
            BlockPos var7 = new BlockPos(this.func_74865_a(3, 5), this.func_74862_a(5), this.func_74873_b(3, 5));
            if (var3.func_175898_b(var7)) {
               this.field_74976_a = true;
               var1.func_180501_a(var7, Blocks.field_150474_ac.func_176223_P(), 2);
               TileEntity var8 = var1.func_175625_s(var7);
               if (var8 instanceof TileEntityMobSpawner) {
                  ((TileEntityMobSpawner)var8).func_145881_a().func_200876_a(EntityType.field_200792_f);
               }
            }
         }

         for(int var9 = 0; var9 <= 6; ++var9) {
            for(int var10 = 0; var10 <= 6; ++var10) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var9, -1, var10, var3);
            }
         }

         return true;
      }
   }

   public static class Stairs extends FortressPieces.Piece {
      public Stairs() {
         super();
      }

      public Stairs(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74965_c((FortressPieces.Start)var1, var2, var3, 6, 2, false);
      }

      public static FortressPieces.Stairs func_175872_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, int var5, EnumFacing var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -2, 0, 0, 7, 11, 7, var6);
         return func_74964_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new FortressPieces.Stairs(var5, var1, var7, var6) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_175804_a(var1, var3, 0, 0, 0, 6, 1, 6, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 6, 10, 6, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 1, 8, 0, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 2, 0, 6, 8, 0, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 1, 0, 8, 6, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 2, 1, 6, 8, 6, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 6, 5, 8, 6, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         IBlockState var5 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196414_y, true)).func_206870_a(BlockFence.field_196411_b, true);
         IBlockState var6 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196413_c, true);
         this.func_175804_a(var1, var3, 0, 3, 2, 0, 5, 4, var6, var6, false);
         this.func_175804_a(var1, var3, 6, 3, 2, 6, 5, 2, var6, var6, false);
         this.func_175804_a(var1, var3, 6, 3, 4, 6, 5, 4, var6, var6, false);
         this.func_175811_a(var1, Blocks.field_196653_dH.func_176223_P(), 5, 2, 5, var3);
         this.func_175804_a(var1, var3, 4, 2, 5, 4, 3, 5, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 3, 2, 5, 3, 4, 5, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 2, 5, 2, 5, 5, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 5, 1, 6, 5, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 7, 1, 5, 7, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 8, 2, 6, 8, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 6, 0, 4, 8, 0, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 0, 4, 5, 0, var5, var5, false);

         for(int var7 = 0; var7 <= 6; ++var7) {
            for(int var8 = 0; var8 <= 6; ++var8) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var7, -1, var8, var3);
            }
         }

         return true;
      }
   }

   public static class Crossing extends FortressPieces.Piece {
      public Crossing() {
         super();
      }

      public Crossing(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74963_a((FortressPieces.Start)var1, var2, var3, 2, 0, false);
         this.func_74961_b((FortressPieces.Start)var1, var2, var3, 0, 2, false);
         this.func_74965_c((FortressPieces.Start)var1, var2, var3, 0, 2, false);
      }

      public static FortressPieces.Crossing func_175873_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -2, 0, 0, 7, 9, 7, var5);
         return func_74964_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new FortressPieces.Crossing(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_175804_a(var1, var3, 0, 0, 0, 6, 1, 6, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 6, 7, 6, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 1, 6, 0, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 6, 1, 6, 6, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 2, 0, 6, 6, 0, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 2, 6, 6, 6, 6, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 0, 6, 1, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 5, 0, 6, 6, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 2, 0, 6, 6, 1, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 2, 5, 6, 6, 6, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         IBlockState var5 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196414_y, true)).func_206870_a(BlockFence.field_196411_b, true);
         IBlockState var6 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196413_c, true);
         this.func_175804_a(var1, var3, 2, 6, 0, 4, 6, 0, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 0, 4, 5, 0, var5, var5, false);
         this.func_175804_a(var1, var3, 2, 6, 6, 4, 6, 6, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 6, 4, 5, 6, var5, var5, false);
         this.func_175804_a(var1, var3, 0, 6, 2, 0, 6, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 2, 0, 5, 4, var6, var6, false);
         this.func_175804_a(var1, var3, 6, 6, 2, 6, 6, 4, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 5, 2, 6, 5, 4, var6, var6, false);

         for(int var7 = 0; var7 <= 6; ++var7) {
            for(int var8 = 0; var8 <= 6; ++var8) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var7, -1, var8, var3);
            }
         }

         return true;
      }
   }

   public static class Crossing3 extends FortressPieces.Piece {
      public Crossing3() {
         super();
      }

      public Crossing3(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
      }

      protected Crossing3(Random var1, int var2, int var3) {
         super(0);
         this.func_186164_a(EnumFacing.Plane.HORIZONTAL.func_179518_a(var1));
         if (this.func_186165_e().func_176740_k() == EnumFacing.Axis.Z) {
            this.field_74887_e = new MutableBoundingBox(var2, 64, var3, var2 + 19 - 1, 73, var3 + 19 - 1);
         } else {
            this.field_74887_e = new MutableBoundingBox(var2, 64, var3, var2 + 19 - 1, 73, var3 + 19 - 1);
         }

      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74963_a((FortressPieces.Start)var1, var2, var3, 8, 3, false);
         this.func_74961_b((FortressPieces.Start)var1, var2, var3, 3, 8, false);
         this.func_74965_c((FortressPieces.Start)var1, var2, var3, 3, 8, false);
      }

      public static FortressPieces.Crossing3 func_175885_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -8, -3, 0, 19, 10, 19, var5);
         return func_74964_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new FortressPieces.Crossing3(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_175804_a(var1, var3, 7, 3, 0, 11, 4, 18, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 3, 7, 18, 4, 11, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 5, 0, 10, 7, 18, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 8, 18, 7, 10, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 5, 0, 7, 5, 7, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 5, 11, 7, 5, 18, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 11, 5, 0, 11, 5, 7, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 11, 5, 11, 11, 5, 18, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 7, 7, 5, 7, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 11, 5, 7, 18, 5, 7, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 11, 7, 5, 11, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 11, 5, 11, 18, 5, 11, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 2, 0, 11, 2, 5, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 2, 13, 11, 2, 18, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 0, 0, 11, 1, 3, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 0, 15, 11, 1, 18, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);

         int var5;
         int var6;
         for(var5 = 7; var5 <= 11; ++var5) {
            for(var6 = 0; var6 <= 2; ++var6) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var5, -1, var6, var3);
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var5, -1, 18 - var6, var3);
            }
         }

         this.func_175804_a(var1, var3, 0, 2, 7, 5, 2, 11, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 13, 2, 7, 18, 2, 11, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 7, 3, 1, 11, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 15, 0, 7, 18, 1, 11, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);

         for(var5 = 0; var5 <= 2; ++var5) {
            for(var6 = 7; var6 <= 11; ++var6) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var5, -1, var6, var3);
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), 18 - var5, -1, var6, var3);
            }
         }

         return true;
      }
   }

   public static class End extends FortressPieces.Piece {
      private int field_74972_a;

      public End() {
         super();
      }

      public End(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
         this.field_74972_a = var2.nextInt();
      }

      public static FortressPieces.End func_175884_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -1, -3, 0, 5, 10, 8, var5);
         return func_74964_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new FortressPieces.End(var6, var1, var7, var5) : null;
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_74972_a = var1.func_74762_e("Seed");
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74768_a("Seed", this.field_74972_a);
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         Random var5 = new Random((long)this.field_74972_a);

         int var6;
         int var7;
         int var8;
         for(var6 = 0; var6 <= 4; ++var6) {
            for(var7 = 3; var7 <= 4; ++var7) {
               var8 = var5.nextInt(8);
               this.func_175804_a(var1, var3, var6, var7, 0, var6, var7, var8, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
            }
         }

         var6 = var5.nextInt(8);
         this.func_175804_a(var1, var3, 0, 5, 0, 0, 5, var6, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         var6 = var5.nextInt(8);
         this.func_175804_a(var1, var3, 4, 5, 0, 4, 5, var6, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);

         for(var6 = 0; var6 <= 4; ++var6) {
            var7 = var5.nextInt(5);
            this.func_175804_a(var1, var3, var6, 2, 0, var6, 2, var7, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         }

         for(var6 = 0; var6 <= 4; ++var6) {
            for(var7 = 0; var7 <= 1; ++var7) {
               var8 = var5.nextInt(3);
               this.func_175804_a(var1, var3, var6, var7, 0, var6, var7, var8, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
            }
         }

         return true;
      }
   }

   public static class Straight extends FortressPieces.Piece {
      public Straight() {
         super();
      }

      public Straight(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74963_a((FortressPieces.Start)var1, var2, var3, 1, 3, false);
      }

      public static FortressPieces.Straight func_175882_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -1, -3, 0, 5, 10, 19, var5);
         return func_74964_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new FortressPieces.Straight(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_175804_a(var1, var3, 0, 3, 0, 4, 4, 18, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 5, 0, 3, 7, 18, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 0, 0, 5, 18, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 5, 0, 4, 5, 18, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 4, 2, 5, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 13, 4, 2, 18, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 0, 4, 1, 3, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 15, 4, 1, 18, Blocks.field_196653_dH.func_176223_P(), Blocks.field_196653_dH.func_176223_P(), false);

         for(int var5 = 0; var5 <= 4; ++var5) {
            for(int var6 = 0; var6 <= 2; ++var6) {
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var5, -1, var6, var3);
               this.func_175808_b(var1, Blocks.field_196653_dH.func_176223_P(), var5, -1, 18 - var6, var3);
            }
         }

         IBlockState var8 = (IBlockState)((IBlockState)Blocks.field_150386_bk.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196413_c, true);
         IBlockState var9 = (IBlockState)var8.func_206870_a(BlockFence.field_196411_b, true);
         IBlockState var7 = (IBlockState)var8.func_206870_a(BlockFence.field_196414_y, true);
         this.func_175804_a(var1, var3, 0, 1, 1, 0, 4, 1, var9, var9, false);
         this.func_175804_a(var1, var3, 0, 3, 4, 0, 4, 4, var9, var9, false);
         this.func_175804_a(var1, var3, 0, 3, 14, 0, 4, 14, var9, var9, false);
         this.func_175804_a(var1, var3, 0, 1, 17, 0, 4, 17, var9, var9, false);
         this.func_175804_a(var1, var3, 4, 1, 1, 4, 4, 1, var7, var7, false);
         this.func_175804_a(var1, var3, 4, 3, 4, 4, 4, 4, var7, var7, false);
         this.func_175804_a(var1, var3, 4, 3, 14, 4, 4, 14, var7, var7, false);
         this.func_175804_a(var1, var3, 4, 1, 17, 4, 4, 17, var7, var7, false);
         return true;
      }
   }

   public static class Start extends FortressPieces.Crossing3 {
      public FortressPieces.PieceWeight field_74970_a;
      public List<FortressPieces.PieceWeight> field_74968_b;
      public List<FortressPieces.PieceWeight> field_74969_c;
      public List<StructurePiece> field_74967_d = Lists.newArrayList();

      public Start() {
         super();
      }

      public Start(Random var1, int var2, int var3) {
         super(var1, var2, var3);
         this.field_74968_b = Lists.newArrayList();
         FortressPieces.PieceWeight[] var4 = FortressPieces.field_78742_a;
         int var5 = var4.length;

         int var6;
         FortressPieces.PieceWeight var7;
         for(var6 = 0; var6 < var5; ++var6) {
            var7 = var4[var6];
            var7.field_78827_c = 0;
            this.field_74968_b.add(var7);
         }

         this.field_74969_c = Lists.newArrayList();
         var4 = FortressPieces.field_78741_b;
         var5 = var4.length;

         for(var6 = 0; var6 < var5; ++var6) {
            var7 = var4[var6];
            var7.field_78827_c = 0;
            this.field_74969_c.add(var7);
         }

      }
   }

   abstract static class Piece extends StructurePiece {
      public Piece() {
         super();
      }

      protected Piece(int var1) {
         super(var1);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
      }

      protected void func_143012_a(NBTTagCompound var1) {
      }

      private int func_74960_a(List<FortressPieces.PieceWeight> var1) {
         boolean var2 = false;
         int var3 = 0;

         FortressPieces.PieceWeight var5;
         for(Iterator var4 = var1.iterator(); var4.hasNext(); var3 += var5.field_78826_b) {
            var5 = (FortressPieces.PieceWeight)var4.next();
            if (var5.field_78824_d > 0 && var5.field_78827_c < var5.field_78824_d) {
               var2 = true;
            }
         }

         return var2 ? var3 : -1;
      }

      private FortressPieces.Piece func_175871_a(FortressPieces.Start var1, List<FortressPieces.PieceWeight> var2, List<StructurePiece> var3, Random var4, int var5, int var6, int var7, EnumFacing var8, int var9) {
         int var10 = this.func_74960_a(var2);
         boolean var11 = var10 > 0 && var9 <= 30;
         int var12 = 0;

         while(var12 < 5 && var11) {
            ++var12;
            int var13 = var4.nextInt(var10);
            Iterator var14 = var2.iterator();

            while(var14.hasNext()) {
               FortressPieces.PieceWeight var15 = (FortressPieces.PieceWeight)var14.next();
               var13 -= var15.field_78826_b;
               if (var13 < 0) {
                  if (!var15.func_78822_a(var9) || var15 == var1.field_74970_a && !var15.field_78825_e) {
                     break;
                  }

                  FortressPieces.Piece var16 = FortressPieces.func_175887_b(var15, var3, var4, var5, var6, var7, var8, var9);
                  if (var16 != null) {
                     ++var15.field_78827_c;
                     var1.field_74970_a = var15;
                     if (!var15.func_78823_a()) {
                        var2.remove(var15);
                     }

                     return var16;
                  }
               }
            }
         }

         return FortressPieces.End.func_175884_a(var3, var4, var5, var6, var7, var8, var9);
      }

      private StructurePiece func_175870_a(FortressPieces.Start var1, List<StructurePiece> var2, Random var3, int var4, int var5, int var6, @Nullable EnumFacing var7, int var8, boolean var9) {
         if (Math.abs(var4 - var1.func_74874_b().field_78897_a) <= 112 && Math.abs(var6 - var1.func_74874_b().field_78896_c) <= 112) {
            List var10 = var1.field_74968_b;
            if (var9) {
               var10 = var1.field_74969_c;
            }

            FortressPieces.Piece var11 = this.func_175871_a(var1, var10, var2, var3, var4, var5, var6, var7, var8 + 1);
            if (var11 != null) {
               var2.add(var11);
               var1.field_74967_d.add(var11);
            }

            return var11;
         } else {
            return FortressPieces.End.func_175884_a(var2, var3, var4, var5, var6, var7, var8);
         }
      }

      @Nullable
      protected StructurePiece func_74963_a(FortressPieces.Start var1, List<StructurePiece> var2, Random var3, int var4, int var5, boolean var6) {
         EnumFacing var7 = this.func_186165_e();
         if (var7 != null) {
            switch(var7) {
            case NORTH:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78897_a + var4, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78896_c - 1, var7, this.func_74877_c(), var6);
            case SOUTH:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78897_a + var4, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78892_f + 1, var7, this.func_74877_c(), var6);
            case WEST:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78896_c + var4, var7, this.func_74877_c(), var6);
            case EAST:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78896_c + var4, var7, this.func_74877_c(), var6);
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece func_74961_b(FortressPieces.Start var1, List<StructurePiece> var2, Random var3, int var4, int var5, boolean var6) {
         EnumFacing var7 = this.func_186165_e();
         if (var7 != null) {
            switch(var7) {
            case NORTH:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.WEST, this.func_74877_c(), var6);
            case SOUTH:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.WEST, this.func_74877_c(), var6);
            case WEST:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c(), var6);
            case EAST:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c(), var6);
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece func_74965_c(FortressPieces.Start var1, List<StructurePiece> var2, Random var3, int var4, int var5, boolean var6) {
         EnumFacing var7 = this.func_186165_e();
         if (var7 != null) {
            switch(var7) {
            case NORTH:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.EAST, this.func_74877_c(), var6);
            case SOUTH:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.EAST, this.func_74877_c(), var6);
            case WEST:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c(), var6);
            case EAST:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c(), var6);
            }
         }

         return null;
      }

      protected static boolean func_74964_a(MutableBoundingBox var0) {
         return var0 != null && var0.field_78895_b > 10;
      }
   }

   static class PieceWeight {
      public Class<? extends FortressPieces.Piece> field_78828_a;
      public final int field_78826_b;
      public int field_78827_c;
      public int field_78824_d;
      public boolean field_78825_e;

      public PieceWeight(Class<? extends FortressPieces.Piece> var1, int var2, int var3, boolean var4) {
         super();
         this.field_78828_a = var1;
         this.field_78826_b = var2;
         this.field_78824_d = var3;
         this.field_78825_e = var4;
      }

      public PieceWeight(Class<? extends FortressPieces.Piece> var1, int var2, int var3) {
         this(var1, var2, var3, false);
      }

      public boolean func_78822_a(int var1) {
         return this.field_78824_d == 0 || this.field_78827_c < this.field_78824_d;
      }

      public boolean func_78823_a() {
         return this.field_78824_d == 0 || this.field_78827_c < this.field_78824_d;
      }
   }
}
