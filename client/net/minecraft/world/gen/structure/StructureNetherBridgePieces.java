package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureNetherBridgePieces {
   private static final StructureNetherBridgePieces.PieceWeight[] field_78742_a = new StructureNetherBridgePieces.PieceWeight[]{new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Straight.class, 30, 0, true), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing3.class, 10, 4), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing.class, 10, 4), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Stairs.class, 10, 3), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Throne.class, 5, 2), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Entrance.class, 5, 1)};
   private static final StructureNetherBridgePieces.PieceWeight[] field_78741_b = new StructureNetherBridgePieces.PieceWeight[]{new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor5.class, 25, 0, true), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Crossing2.class, 15, 5), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor2.class, 5, 10), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor.class, 5, 10), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor3.class, 10, 3, true), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.Corridor4.class, 7, 2), new StructureNetherBridgePieces.PieceWeight(StructureNetherBridgePieces.NetherStalkRoom.class, 5, 2)};

   public static void func_143049_a() {
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Crossing3.class, "NeBCr");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.End.class, "NeBEF");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Straight.class, "NeBS");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Corridor3.class, "NeCCS");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Corridor4.class, "NeCTB");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Entrance.class, "NeCE");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Crossing2.class, "NeSCSC");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Corridor.class, "NeSCLT");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Corridor5.class, "NeSC");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Corridor2.class, "NeSCRT");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.NetherStalkRoom.class, "NeCSR");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Throne.class, "NeMT");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Crossing.class, "NeRC");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Stairs.class, "NeSR");
      MapGenStructureIO.func_143031_a(StructureNetherBridgePieces.Start.class, "NeStart");
   }

   private static StructureNetherBridgePieces.Piece func_175887_b(StructureNetherBridgePieces.PieceWeight var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
      Class var8 = var0.field_78828_a;
      Object var9 = null;
      if (var8 == StructureNetherBridgePieces.Straight.class) {
         var9 = StructureNetherBridgePieces.Straight.func_175882_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces.Crossing3.class) {
         var9 = StructureNetherBridgePieces.Crossing3.func_175885_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces.Crossing.class) {
         var9 = StructureNetherBridgePieces.Crossing.func_175873_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces.Stairs.class) {
         var9 = StructureNetherBridgePieces.Stairs.func_175872_a(var1, var2, var3, var4, var5, var7, var6);
      } else if (var8 == StructureNetherBridgePieces.Throne.class) {
         var9 = StructureNetherBridgePieces.Throne.func_175874_a(var1, var2, var3, var4, var5, var7, var6);
      } else if (var8 == StructureNetherBridgePieces.Entrance.class) {
         var9 = StructureNetherBridgePieces.Entrance.func_175881_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces.Corridor5.class) {
         var9 = StructureNetherBridgePieces.Corridor5.func_175877_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces.Corridor2.class) {
         var9 = StructureNetherBridgePieces.Corridor2.func_175876_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces.Corridor.class) {
         var9 = StructureNetherBridgePieces.Corridor.func_175879_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces.Corridor3.class) {
         var9 = StructureNetherBridgePieces.Corridor3.func_175883_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces.Corridor4.class) {
         var9 = StructureNetherBridgePieces.Corridor4.func_175880_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces.Crossing2.class) {
         var9 = StructureNetherBridgePieces.Crossing2.func_175878_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var8 == StructureNetherBridgePieces.NetherStalkRoom.class) {
         var9 = StructureNetherBridgePieces.NetherStalkRoom.func_175875_a(var1, var2, var3, var4, var5, var6, var7);
      }

      return (StructureNetherBridgePieces.Piece)var9;
   }

   public static class Corridor4 extends StructureNetherBridgePieces.Piece {
      public Corridor4() {
         super();
      }

      public Corridor4(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         byte var4 = 1;
         if (this.field_74885_f == EnumFacing.WEST || this.field_74885_f == EnumFacing.NORTH) {
            var4 = 5;
         }

         this.func_74961_b((StructureNetherBridgePieces.Start)var1, var2, var3, 0, var4, var3.nextInt(8) > 0);
         this.func_74965_c((StructureNetherBridgePieces.Start)var1, var2, var3, 0, var4, var3.nextInt(8) > 0);
      }

      public static StructureNetherBridgePieces.Corridor4 func_175880_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -3, 0, 0, 9, 7, 9, var5);
         return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces.Corridor4(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         this.func_175804_a(var1, var3, 0, 0, 0, 8, 1, 8, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 8, 5, 8, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 6, 0, 8, 6, 5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 2, 5, 0, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 2, 0, 8, 5, 0, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 3, 0, 1, 4, 0, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 3, 0, 7, 4, 0, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 4, 8, 2, 8, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 1, 4, 2, 2, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 1, 4, 7, 2, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 3, 8, 8, 3, 8, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 3, 6, 0, 3, 7, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 3, 6, 8, 3, 7, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 3, 4, 0, 5, 5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 3, 4, 8, 5, 5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 3, 5, 2, 5, 5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 3, 5, 7, 5, 5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 4, 5, 1, 5, 5, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 4, 5, 7, 5, 5, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);

         for(int var4 = 0; var4 <= 5; ++var4) {
            for(int var5 = 0; var5 <= 8; ++var5) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var5, -1, var4, var3);
            }
         }

         return true;
      }
   }

   public static class Corridor3 extends StructureNetherBridgePieces.Piece {
      public Corridor3() {
         super();
      }

      public Corridor3(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74963_a((StructureNetherBridgePieces.Start)var1, var2, var3, 1, 0, true);
      }

      public static StructureNetherBridgePieces.Corridor3 func_175883_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -1, -7, 0, 5, 14, 10, var5);
         return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces.Corridor3(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         int var4 = this.func_151555_a(Blocks.field_150387_bl, 2);

         for(int var5 = 0; var5 <= 9; ++var5) {
            int var6 = Math.max(1, 7 - var5);
            int var7 = Math.min(Math.max(var6 + 5, 14 - var5), 13);
            int var8 = var5;
            this.func_175804_a(var1, var3, 0, 0, var5, 4, var6, var5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
            this.func_175804_a(var1, var3, 1, var6 + 1, var5, 3, var7 - 1, var5, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            if (var5 <= 6) {
               this.func_175811_a(var1, Blocks.field_150387_bl.func_176203_a(var4), 1, var6 + 1, var5, var3);
               this.func_175811_a(var1, Blocks.field_150387_bl.func_176203_a(var4), 2, var6 + 1, var5, var3);
               this.func_175811_a(var1, Blocks.field_150387_bl.func_176203_a(var4), 3, var6 + 1, var5, var3);
            }

            this.func_175804_a(var1, var3, 0, var7, var5, 4, var7, var5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
            this.func_175804_a(var1, var3, 0, var6 + 1, var5, 0, var7 - 1, var5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
            this.func_175804_a(var1, var3, 4, var6 + 1, var5, 4, var7 - 1, var5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
            if ((var5 & 1) == 0) {
               this.func_175804_a(var1, var3, 0, var6 + 2, var5, 0, var6 + 3, var5, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
               this.func_175804_a(var1, var3, 4, var6 + 2, var5, 4, var6 + 3, var5, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
            }

            for(int var9 = 0; var9 <= 4; ++var9) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var9, -1, var8, var3);
            }
         }

         return true;
      }
   }

   public static class Corridor extends StructureNetherBridgePieces.Piece {
      private boolean field_111021_b;

      public Corridor() {
         super();
      }

      public Corridor(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
         this.field_111021_b = var2.nextInt(3) == 0;
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_111021_b = var1.func_74767_n("Chest");
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Chest", this.field_111021_b);
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74961_b((StructureNetherBridgePieces.Start)var1, var2, var3, 0, 1, true);
      }

      public static StructureNetherBridgePieces.Corridor func_175879_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -1, 0, 0, 5, 7, 5, var5);
         return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces.Corridor(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         this.func_175804_a(var1, var3, 0, 0, 0, 4, 1, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 4, 5, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 2, 0, 4, 5, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 3, 1, 4, 4, 1, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 3, 3, 4, 4, 3, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 0, 5, 0, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 4, 3, 5, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 3, 4, 1, 4, 4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 3, 3, 4, 3, 4, 4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         if (this.field_111021_b && var3.func_175898_b(new BlockPos(this.func_74865_a(3, 3), this.func_74862_a(2), this.func_74873_b(3, 3)))) {
            this.field_111021_b = false;
            this.func_180778_a(var1, var3, var2, 3, 2, 3, field_111019_a, 2 + var2.nextInt(4));
         }

         this.func_175804_a(var1, var3, 0, 6, 0, 4, 6, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);

         for(int var4 = 0; var4 <= 4; ++var4) {
            for(int var5 = 0; var5 <= 4; ++var5) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var4, -1, var5, var3);
            }
         }

         return true;
      }
   }

   public static class Corridor2 extends StructureNetherBridgePieces.Piece {
      private boolean field_111020_b;

      public Corridor2() {
         super();
      }

      public Corridor2(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
         this.field_111020_b = var2.nextInt(3) == 0;
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_111020_b = var1.func_74767_n("Chest");
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Chest", this.field_111020_b);
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74965_c((StructureNetherBridgePieces.Start)var1, var2, var3, 0, 1, true);
      }

      public static StructureNetherBridgePieces.Corridor2 func_175876_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -1, 0, 0, 5, 7, 5, var5);
         return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces.Corridor2(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         this.func_175804_a(var1, var3, 0, 0, 0, 4, 1, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 4, 5, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 0, 5, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 3, 1, 0, 4, 1, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 3, 3, 0, 4, 3, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 2, 0, 4, 5, 0, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 4, 4, 5, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 3, 4, 1, 4, 4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 3, 3, 4, 3, 4, 4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         if (this.field_111020_b && var3.func_175898_b(new BlockPos(this.func_74865_a(1, 3), this.func_74862_a(2), this.func_74873_b(1, 3)))) {
            this.field_111020_b = false;
            this.func_180778_a(var1, var3, var2, 1, 2, 3, field_111019_a, 2 + var2.nextInt(4));
         }

         this.func_175804_a(var1, var3, 0, 6, 0, 4, 6, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);

         for(int var4 = 0; var4 <= 4; ++var4) {
            for(int var5 = 0; var5 <= 4; ++var5) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var4, -1, var5, var3);
            }
         }

         return true;
      }
   }

   public static class Crossing2 extends StructureNetherBridgePieces.Piece {
      public Crossing2() {
         super();
      }

      public Crossing2(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74963_a((StructureNetherBridgePieces.Start)var1, var2, var3, 1, 0, true);
         this.func_74961_b((StructureNetherBridgePieces.Start)var1, var2, var3, 0, 1, true);
         this.func_74965_c((StructureNetherBridgePieces.Start)var1, var2, var3, 0, 1, true);
      }

      public static StructureNetherBridgePieces.Crossing2 func_175878_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -1, 0, 0, 5, 7, 5, var5);
         return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces.Crossing2(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         this.func_175804_a(var1, var3, 0, 0, 0, 4, 1, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 4, 5, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 0, 5, 0, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 2, 0, 4, 5, 0, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 4, 0, 5, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 2, 4, 4, 5, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 6, 0, 4, 6, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);

         for(int var4 = 0; var4 <= 4; ++var4) {
            for(int var5 = 0; var5 <= 4; ++var5) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var4, -1, var5, var3);
            }
         }

         return true;
      }
   }

   public static class Corridor5 extends StructureNetherBridgePieces.Piece {
      public Corridor5() {
         super();
      }

      public Corridor5(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74963_a((StructureNetherBridgePieces.Start)var1, var2, var3, 1, 0, true);
      }

      public static StructureNetherBridgePieces.Corridor5 func_175877_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -1, 0, 0, 5, 7, 5, var5);
         return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces.Corridor5(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         this.func_175804_a(var1, var3, 0, 0, 0, 4, 1, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 4, 5, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 0, 5, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 2, 0, 4, 5, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 3, 1, 0, 4, 1, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 3, 3, 0, 4, 3, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 3, 1, 4, 4, 1, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 3, 3, 4, 4, 3, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 6, 0, 4, 6, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);

         for(int var4 = 0; var4 <= 4; ++var4) {
            for(int var5 = 0; var5 <= 4; ++var5) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var4, -1, var5, var3);
            }
         }

         return true;
      }
   }

   public static class NetherStalkRoom extends StructureNetherBridgePieces.Piece {
      public NetherStalkRoom() {
         super();
      }

      public NetherStalkRoom(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74963_a((StructureNetherBridgePieces.Start)var1, var2, var3, 5, 3, true);
         this.func_74963_a((StructureNetherBridgePieces.Start)var1, var2, var3, 5, 11, true);
      }

      public static StructureNetherBridgePieces.NetherStalkRoom func_175875_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -5, -3, 0, 13, 14, 13, var5);
         return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces.NetherStalkRoom(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         this.func_175804_a(var1, var3, 0, 3, 0, 12, 4, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 0, 12, 13, 12, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 0, 1, 12, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 11, 5, 0, 12, 12, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 11, 4, 12, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 5, 11, 10, 12, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 9, 11, 7, 12, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 0, 4, 12, 1, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 5, 0, 10, 12, 1, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 9, 0, 7, 12, 1, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 11, 2, 10, 12, 10, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);

         int var4;
         for(var4 = 1; var4 <= 11; var4 += 2) {
            this.func_175804_a(var1, var3, var4, 10, 0, var4, 11, 0, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
            this.func_175804_a(var1, var3, var4, 10, 12, var4, 11, 12, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
            this.func_175804_a(var1, var3, 0, 10, var4, 0, 11, var4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
            this.func_175804_a(var1, var3, 12, 10, var4, 12, 11, var4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
            this.func_175811_a(var1, Blocks.field_150385_bj.func_176223_P(), var4, 13, 0, var3);
            this.func_175811_a(var1, Blocks.field_150385_bj.func_176223_P(), var4, 13, 12, var3);
            this.func_175811_a(var1, Blocks.field_150385_bj.func_176223_P(), 0, 13, var4, var3);
            this.func_175811_a(var1, Blocks.field_150385_bj.func_176223_P(), 12, 13, var4, var3);
            this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), var4 + 1, 13, 0, var3);
            this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), var4 + 1, 13, 12, var3);
            this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), 0, 13, var4 + 1, var3);
            this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), 12, 13, var4 + 1, var3);
         }

         this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), 0, 13, 0, var3);
         this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), 0, 13, 12, var3);
         this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), 0, 13, 0, var3);
         this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), 12, 13, 0, var3);

         for(var4 = 3; var4 <= 9; var4 += 2) {
            this.func_175804_a(var1, var3, 1, 7, var4, 1, 8, var4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
            this.func_175804_a(var1, var3, 11, 7, var4, 11, 8, var4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         }

         var4 = this.func_151555_a(Blocks.field_150387_bl, 3);

         int var5;
         int var6;
         int var7;
         for(var5 = 0; var5 <= 6; ++var5) {
            var6 = var5 + 4;

            for(var7 = 5; var7 <= 7; ++var7) {
               this.func_175811_a(var1, Blocks.field_150387_bl.func_176203_a(var4), var7, 5 + var5, var6, var3);
            }

            if (var6 >= 5 && var6 <= 8) {
               this.func_175804_a(var1, var3, 5, 5, var6, 7, var5 + 4, var6, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
            } else if (var6 >= 9 && var6 <= 10) {
               this.func_175804_a(var1, var3, 5, 8, var6, 7, var5 + 4, var6, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
            }

            if (var5 >= 1) {
               this.func_175804_a(var1, var3, 5, 6 + var5, var6, 7, 9 + var5, var6, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            }
         }

         for(var5 = 5; var5 <= 7; ++var5) {
            this.func_175811_a(var1, Blocks.field_150387_bl.func_176203_a(var4), var5, 12, 11, var3);
         }

         this.func_175804_a(var1, var3, 5, 6, 7, 5, 7, 7, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 6, 7, 7, 7, 7, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 13, 12, 7, 13, 12, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 2, 3, 5, 3, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 9, 3, 5, 10, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 4, 2, 5, 8, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, 5, 2, 10, 5, 3, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, 5, 9, 10, 5, 10, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 10, 5, 4, 10, 5, 8, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         var5 = this.func_151555_a(Blocks.field_150387_bl, 0);
         var6 = this.func_151555_a(Blocks.field_150387_bl, 1);
         this.func_175811_a(var1, Blocks.field_150387_bl.func_176203_a(var6), 4, 5, 2, var3);
         this.func_175811_a(var1, Blocks.field_150387_bl.func_176203_a(var6), 4, 5, 3, var3);
         this.func_175811_a(var1, Blocks.field_150387_bl.func_176203_a(var6), 4, 5, 9, var3);
         this.func_175811_a(var1, Blocks.field_150387_bl.func_176203_a(var6), 4, 5, 10, var3);
         this.func_175811_a(var1, Blocks.field_150387_bl.func_176203_a(var5), 8, 5, 2, var3);
         this.func_175811_a(var1, Blocks.field_150387_bl.func_176203_a(var5), 8, 5, 3, var3);
         this.func_175811_a(var1, Blocks.field_150387_bl.func_176203_a(var5), 8, 5, 9, var3);
         this.func_175811_a(var1, Blocks.field_150387_bl.func_176203_a(var5), 8, 5, 10, var3);
         this.func_175804_a(var1, var3, 3, 4, 4, 4, 4, 8, Blocks.field_150425_aM.func_176223_P(), Blocks.field_150425_aM.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 4, 4, 9, 4, 8, Blocks.field_150425_aM.func_176223_P(), Blocks.field_150425_aM.func_176223_P(), false);
         this.func_175804_a(var1, var3, 3, 5, 4, 4, 5, 8, Blocks.field_150388_bm.func_176223_P(), Blocks.field_150388_bm.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 5, 4, 9, 5, 8, Blocks.field_150388_bm.func_176223_P(), Blocks.field_150388_bm.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 2, 0, 8, 2, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 4, 12, 2, 8, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 0, 0, 8, 1, 3, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 0, 9, 8, 1, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 4, 3, 1, 8, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, 0, 4, 12, 1, 8, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);

         int var8;
         for(var7 = 4; var7 <= 8; ++var7) {
            for(var8 = 0; var8 <= 2; ++var8) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var7, -1, var8, var3);
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var7, -1, 12 - var8, var3);
            }
         }

         for(var7 = 0; var7 <= 2; ++var7) {
            for(var8 = 4; var8 <= 8; ++var8) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var7, -1, var8, var3);
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), 12 - var7, -1, var8, var3);
            }
         }

         return true;
      }
   }

   public static class Entrance extends StructureNetherBridgePieces.Piece {
      public Entrance() {
         super();
      }

      public Entrance(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74963_a((StructureNetherBridgePieces.Start)var1, var2, var3, 5, 3, true);
      }

      public static StructureNetherBridgePieces.Entrance func_175881_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -5, -3, 0, 13, 14, 13, var5);
         return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces.Entrance(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         this.func_175804_a(var1, var3, 0, 3, 0, 12, 4, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 0, 12, 13, 12, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 0, 1, 12, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 11, 5, 0, 12, 12, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 11, 4, 12, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 5, 11, 10, 12, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 9, 11, 7, 12, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 0, 4, 12, 1, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 5, 0, 10, 12, 1, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 9, 0, 7, 12, 1, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 11, 2, 10, 12, 10, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 8, 0, 7, 8, 0, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);

         int var4;
         for(var4 = 1; var4 <= 11; var4 += 2) {
            this.func_175804_a(var1, var3, var4, 10, 0, var4, 11, 0, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
            this.func_175804_a(var1, var3, var4, 10, 12, var4, 11, 12, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
            this.func_175804_a(var1, var3, 0, 10, var4, 0, 11, var4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
            this.func_175804_a(var1, var3, 12, 10, var4, 12, 11, var4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
            this.func_175811_a(var1, Blocks.field_150385_bj.func_176223_P(), var4, 13, 0, var3);
            this.func_175811_a(var1, Blocks.field_150385_bj.func_176223_P(), var4, 13, 12, var3);
            this.func_175811_a(var1, Blocks.field_150385_bj.func_176223_P(), 0, 13, var4, var3);
            this.func_175811_a(var1, Blocks.field_150385_bj.func_176223_P(), 12, 13, var4, var3);
            this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), var4 + 1, 13, 0, var3);
            this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), var4 + 1, 13, 12, var3);
            this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), 0, 13, var4 + 1, var3);
            this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), 12, 13, var4 + 1, var3);
         }

         this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), 0, 13, 0, var3);
         this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), 0, 13, 12, var3);
         this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), 0, 13, 0, var3);
         this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), 12, 13, 0, var3);

         for(var4 = 3; var4 <= 9; var4 += 2) {
            this.func_175804_a(var1, var3, 1, 7, var4, 1, 8, var4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
            this.func_175804_a(var1, var3, 11, 7, var4, 11, 8, var4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         }

         this.func_175804_a(var1, var3, 4, 2, 0, 8, 2, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 4, 12, 2, 8, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 0, 0, 8, 1, 3, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 0, 9, 8, 1, 12, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 4, 3, 1, 8, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, 0, 4, 12, 1, 8, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);

         int var5;
         for(var4 = 4; var4 <= 8; ++var4) {
            for(var5 = 0; var5 <= 2; ++var5) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var4, -1, var5, var3);
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var4, -1, 12 - var5, var3);
            }
         }

         for(var4 = 0; var4 <= 2; ++var4) {
            for(var5 = 4; var5 <= 8; ++var5) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var4, -1, var5, var3);
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), 12 - var4, -1, var5, var3);
            }
         }

         this.func_175804_a(var1, var3, 5, 5, 5, 7, 5, 7, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 1, 6, 6, 4, 6, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150385_bj.func_176223_P(), 6, 0, 6, var3);
         this.func_175811_a(var1, Blocks.field_150356_k.func_176223_P(), 6, 5, 6, var3);
         BlockPos var6 = new BlockPos(this.func_74865_a(6, 6), this.func_74862_a(5), this.func_74873_b(6, 6));
         if (var3.func_175898_b(var6)) {
            var1.func_175637_a(Blocks.field_150356_k, var6, var2);
         }

         return true;
      }
   }

   public static class Throne extends StructureNetherBridgePieces.Piece {
      private boolean field_74976_a;

      public Throne() {
         super();
      }

      public Throne(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_74976_a = var1.func_74767_n("Mob");
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Mob", this.field_74976_a);
      }

      public static StructureNetherBridgePieces.Throne func_175874_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, int var5, EnumFacing var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -2, 0, 0, 7, 8, 9, var6);
         return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces.Throne(var5, var1, var7, var6) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         this.func_175804_a(var1, var3, 0, 2, 0, 6, 7, 7, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 0, 0, 5, 1, 7, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 1, 5, 2, 7, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 3, 2, 5, 3, 7, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 4, 3, 5, 4, 7, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 0, 1, 4, 2, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 2, 0, 5, 4, 2, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 5, 2, 1, 5, 3, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 5, 2, 5, 5, 3, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 3, 0, 5, 8, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 5, 3, 6, 5, 8, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 5, 8, 5, 5, 8, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), 1, 6, 3, var3);
         this.func_175811_a(var1, Blocks.field_150386_bk.func_176223_P(), 5, 6, 3, var3);
         this.func_175804_a(var1, var3, 0, 6, 3, 0, 6, 8, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 6, 3, 6, 6, 8, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 6, 8, 5, 7, 8, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 8, 8, 4, 8, 8, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         if (!this.field_74976_a) {
            BlockPos var4 = new BlockPos(this.func_74865_a(3, 5), this.func_74862_a(5), this.func_74873_b(3, 5));
            if (var3.func_175898_b(var4)) {
               this.field_74976_a = true;
               var1.func_180501_a(var4, Blocks.field_150474_ac.func_176223_P(), 2);
               TileEntity var5 = var1.func_175625_s(var4);
               if (var5 instanceof TileEntityMobSpawner) {
                  ((TileEntityMobSpawner)var5).func_145881_a().func_98272_a("Blaze");
               }
            }
         }

         for(int var6 = 0; var6 <= 6; ++var6) {
            for(int var7 = 0; var7 <= 6; ++var7) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var6, -1, var7, var3);
            }
         }

         return true;
      }
   }

   public static class Stairs extends StructureNetherBridgePieces.Piece {
      public Stairs() {
         super();
      }

      public Stairs(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74965_c((StructureNetherBridgePieces.Start)var1, var2, var3, 6, 2, false);
      }

      public static StructureNetherBridgePieces.Stairs func_175872_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, int var5, EnumFacing var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -2, 0, 0, 7, 11, 7, var6);
         return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces.Stairs(var5, var1, var7, var6) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         this.func_175804_a(var1, var3, 0, 0, 0, 6, 1, 6, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 6, 10, 6, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 1, 8, 0, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 2, 0, 6, 8, 0, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 1, 0, 8, 6, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 2, 1, 6, 8, 6, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 6, 5, 8, 6, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 3, 2, 0, 5, 4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 3, 2, 6, 5, 2, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 3, 4, 6, 5, 4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_150385_bj.func_176223_P(), 5, 2, 5, var3);
         this.func_175804_a(var1, var3, 4, 2, 5, 4, 3, 5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 3, 2, 5, 3, 4, 5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 2, 5, 2, 5, 5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 2, 5, 1, 6, 5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 7, 1, 5, 7, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 8, 2, 6, 8, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 6, 0, 4, 8, 0, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 0, 4, 5, 0, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);

         for(int var4 = 0; var4 <= 6; ++var4) {
            for(int var5 = 0; var5 <= 6; ++var5) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var4, -1, var5, var3);
            }
         }

         return true;
      }
   }

   public static class Crossing extends StructureNetherBridgePieces.Piece {
      public Crossing() {
         super();
      }

      public Crossing(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74963_a((StructureNetherBridgePieces.Start)var1, var2, var3, 2, 0, false);
         this.func_74961_b((StructureNetherBridgePieces.Start)var1, var2, var3, 0, 2, false);
         this.func_74965_c((StructureNetherBridgePieces.Start)var1, var2, var3, 0, 2, false);
      }

      public static StructureNetherBridgePieces.Crossing func_175873_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -2, 0, 0, 7, 9, 7, var5);
         return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces.Crossing(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         this.func_175804_a(var1, var3, 0, 0, 0, 6, 1, 6, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 6, 7, 6, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 1, 6, 0, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 6, 1, 6, 6, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 2, 0, 6, 6, 0, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 2, 6, 6, 6, 6, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 0, 6, 1, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 5, 0, 6, 6, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 2, 0, 6, 6, 1, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 2, 5, 6, 6, 6, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 6, 0, 4, 6, 0, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 0, 4, 5, 0, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 6, 6, 4, 6, 6, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 2, 5, 6, 4, 5, 6, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 6, 2, 0, 6, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 2, 0, 5, 4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 6, 2, 6, 6, 4, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 6, 5, 2, 6, 5, 4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);

         for(int var4 = 0; var4 <= 6; ++var4) {
            for(int var5 = 0; var5 <= 6; ++var5) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var4, -1, var5, var3);
            }
         }

         return true;
      }
   }

   public static class Crossing3 extends StructureNetherBridgePieces.Piece {
      public Crossing3() {
         super();
      }

      public Crossing3(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
      }

      protected Crossing3(Random var1, int var2, int var3) {
         super(0);
         this.field_74885_f = EnumFacing.Plane.HORIZONTAL.func_179518_a(var1);
         switch(this.field_74885_f) {
         case NORTH:
         case SOUTH:
            this.field_74887_e = new StructureBoundingBox(var2, 64, var3, var2 + 19 - 1, 73, var3 + 19 - 1);
            break;
         default:
            this.field_74887_e = new StructureBoundingBox(var2, 64, var3, var2 + 19 - 1, 73, var3 + 19 - 1);
         }

      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74963_a((StructureNetherBridgePieces.Start)var1, var2, var3, 8, 3, false);
         this.func_74961_b((StructureNetherBridgePieces.Start)var1, var2, var3, 3, 8, false);
         this.func_74965_c((StructureNetherBridgePieces.Start)var1, var2, var3, 3, 8, false);
      }

      public static StructureNetherBridgePieces.Crossing3 func_175885_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -8, -3, 0, 19, 10, 19, var5);
         return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces.Crossing3(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         this.func_175804_a(var1, var3, 7, 3, 0, 11, 4, 18, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 3, 7, 18, 4, 11, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 5, 0, 10, 7, 18, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 8, 18, 7, 10, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 5, 0, 7, 5, 7, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 5, 11, 7, 5, 18, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 11, 5, 0, 11, 5, 7, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 11, 5, 11, 11, 5, 18, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 7, 7, 5, 7, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 11, 5, 7, 18, 5, 7, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 11, 7, 5, 11, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 11, 5, 11, 18, 5, 11, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 2, 0, 11, 2, 5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 2, 13, 11, 2, 18, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 0, 0, 11, 1, 3, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 7, 0, 15, 11, 1, 18, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);

         int var4;
         int var5;
         for(var4 = 7; var4 <= 11; ++var4) {
            for(var5 = 0; var5 <= 2; ++var5) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var4, -1, var5, var3);
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var4, -1, 18 - var5, var3);
            }
         }

         this.func_175804_a(var1, var3, 0, 2, 7, 5, 2, 11, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 13, 2, 7, 18, 2, 11, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 7, 3, 1, 11, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 15, 0, 7, 18, 1, 11, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);

         for(var4 = 0; var4 <= 2; ++var4) {
            for(var5 = 7; var5 <= 11; ++var5) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var4, -1, var5, var3);
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), 18 - var4, -1, var5, var3);
            }
         }

         return true;
      }
   }

   public static class End extends StructureNetherBridgePieces.Piece {
      private int field_74972_a;

      public End() {
         super();
      }

      public End(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
         this.field_74972_a = var2.nextInt();
      }

      public static StructureNetherBridgePieces.End func_175884_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -1, -3, 0, 5, 10, 8, var5);
         return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces.End(var6, var1, var7, var5) : null;
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_74972_a = var1.func_74762_e("Seed");
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74768_a("Seed", this.field_74972_a);
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         Random var4 = new Random((long)this.field_74972_a);

         int var5;
         int var6;
         int var7;
         for(var5 = 0; var5 <= 4; ++var5) {
            for(var6 = 3; var6 <= 4; ++var6) {
               var7 = var4.nextInt(8);
               this.func_175804_a(var1, var3, var5, var6, 0, var5, var6, var7, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
            }
         }

         var5 = var4.nextInt(8);
         this.func_175804_a(var1, var3, 0, 5, 0, 0, 5, var5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         var5 = var4.nextInt(8);
         this.func_175804_a(var1, var3, 4, 5, 0, 4, 5, var5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);

         for(var5 = 0; var5 <= 4; ++var5) {
            var6 = var4.nextInt(5);
            this.func_175804_a(var1, var3, var5, 2, 0, var5, 2, var6, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         }

         for(var5 = 0; var5 <= 4; ++var5) {
            for(var6 = 0; var6 <= 1; ++var6) {
               var7 = var4.nextInt(3);
               this.func_175804_a(var1, var3, var5, var6, 0, var5, var6, var7, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
            }
         }

         return true;
      }
   }

   public static class Straight extends StructureNetherBridgePieces.Piece {
      public Straight() {
         super();
      }

      public Straight(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74963_a((StructureNetherBridgePieces.Start)var1, var2, var3, 1, 3, false);
      }

      public static StructureNetherBridgePieces.Straight func_175882_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -1, -3, 0, 5, 10, 19, var5);
         return func_74964_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureNetherBridgePieces.Straight(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         this.func_175804_a(var1, var3, 0, 3, 0, 4, 4, 18, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 5, 0, 3, 7, 18, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 5, 0, 0, 5, 18, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 5, 0, 4, 5, 18, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 0, 4, 2, 5, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 2, 13, 4, 2, 18, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 0, 4, 1, 3, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 0, 15, 4, 1, 18, Blocks.field_150385_bj.func_176223_P(), Blocks.field_150385_bj.func_176223_P(), false);

         for(int var4 = 0; var4 <= 4; ++var4) {
            for(int var5 = 0; var5 <= 2; ++var5) {
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var4, -1, var5, var3);
               this.func_175808_b(var1, Blocks.field_150385_bj.func_176223_P(), var4, -1, 18 - var5, var3);
            }
         }

         this.func_175804_a(var1, var3, 0, 1, 1, 0, 4, 1, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 3, 4, 0, 4, 4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 3, 14, 0, 4, 14, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 0, 1, 17, 0, 4, 17, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 1, 1, 4, 4, 1, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 3, 4, 4, 4, 4, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 3, 14, 4, 4, 14, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 1, 17, 4, 4, 17, Blocks.field_150386_bk.func_176223_P(), Blocks.field_150386_bk.func_176223_P(), false);
         return true;
      }
   }

   public static class Start extends StructureNetherBridgePieces.Crossing3 {
      public StructureNetherBridgePieces.PieceWeight field_74970_a;
      public List<StructureNetherBridgePieces.PieceWeight> field_74968_b;
      public List<StructureNetherBridgePieces.PieceWeight> field_74969_c;
      public List<StructureComponent> field_74967_d = Lists.newArrayList();

      public Start() {
         super();
      }

      public Start(Random var1, int var2, int var3) {
         super(var1, var2, var3);
         this.field_74968_b = Lists.newArrayList();
         StructureNetherBridgePieces.PieceWeight[] var4 = StructureNetherBridgePieces.field_78742_a;
         int var5 = var4.length;

         int var6;
         StructureNetherBridgePieces.PieceWeight var7;
         for(var6 = 0; var6 < var5; ++var6) {
            var7 = var4[var6];
            var7.field_78827_c = 0;
            this.field_74968_b.add(var7);
         }

         this.field_74969_c = Lists.newArrayList();
         var4 = StructureNetherBridgePieces.field_78741_b;
         var5 = var4.length;

         for(var6 = 0; var6 < var5; ++var6) {
            var7 = var4[var6];
            var7.field_78827_c = 0;
            this.field_74969_c.add(var7);
         }

      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
      }
   }

   abstract static class Piece extends StructureComponent {
      protected static final List<WeightedRandomChestContent> field_111019_a;

      public Piece() {
         super();
      }

      protected Piece(int var1) {
         super(var1);
      }

      protected void func_143011_b(NBTTagCompound var1) {
      }

      protected void func_143012_a(NBTTagCompound var1) {
      }

      private int func_74960_a(List<StructureNetherBridgePieces.PieceWeight> var1) {
         boolean var2 = false;
         int var3 = 0;

         StructureNetherBridgePieces.PieceWeight var5;
         for(Iterator var4 = var1.iterator(); var4.hasNext(); var3 += var5.field_78826_b) {
            var5 = (StructureNetherBridgePieces.PieceWeight)var4.next();
            if (var5.field_78824_d > 0 && var5.field_78827_c < var5.field_78824_d) {
               var2 = true;
            }
         }

         return var2 ? var3 : -1;
      }

      private StructureNetherBridgePieces.Piece func_175871_a(StructureNetherBridgePieces.Start var1, List<StructureNetherBridgePieces.PieceWeight> var2, List<StructureComponent> var3, Random var4, int var5, int var6, int var7, EnumFacing var8, int var9) {
         int var10 = this.func_74960_a(var2);
         boolean var11 = var10 > 0 && var9 <= 30;
         int var12 = 0;

         while(var12 < 5 && var11) {
            ++var12;
            int var13 = var4.nextInt(var10);
            Iterator var14 = var2.iterator();

            while(var14.hasNext()) {
               StructureNetherBridgePieces.PieceWeight var15 = (StructureNetherBridgePieces.PieceWeight)var14.next();
               var13 -= var15.field_78826_b;
               if (var13 < 0) {
                  if (!var15.func_78822_a(var9) || var15 == var1.field_74970_a && !var15.field_78825_e) {
                     break;
                  }

                  StructureNetherBridgePieces.Piece var16 = StructureNetherBridgePieces.func_175887_b(var15, var3, var4, var5, var6, var7, var8, var9);
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

         return StructureNetherBridgePieces.End.func_175884_a(var3, var4, var5, var6, var7, var8, var9);
      }

      private StructureComponent func_175870_a(StructureNetherBridgePieces.Start var1, List<StructureComponent> var2, Random var3, int var4, int var5, int var6, EnumFacing var7, int var8, boolean var9) {
         if (Math.abs(var4 - var1.func_74874_b().field_78897_a) <= 112 && Math.abs(var6 - var1.func_74874_b().field_78896_c) <= 112) {
            List var10 = var1.field_74968_b;
            if (var9) {
               var10 = var1.field_74969_c;
            }

            StructureNetherBridgePieces.Piece var11 = this.func_175871_a(var1, var10, var2, var3, var4, var5, var6, var7, var8 + 1);
            if (var11 != null) {
               var2.add(var11);
               var1.field_74967_d.add(var11);
            }

            return var11;
         } else {
            return StructureNetherBridgePieces.End.func_175884_a(var2, var3, var4, var5, var6, var7, var8);
         }
      }

      protected StructureComponent func_74963_a(StructureNetherBridgePieces.Start var1, List<StructureComponent> var2, Random var3, int var4, int var5, boolean var6) {
         if (this.field_74885_f != null) {
            switch(this.field_74885_f) {
            case NORTH:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78897_a + var4, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78896_c - 1, this.field_74885_f, this.func_74877_c(), var6);
            case SOUTH:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78897_a + var4, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78892_f + 1, this.field_74885_f, this.func_74877_c(), var6);
            case WEST:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78896_c + var4, this.field_74885_f, this.func_74877_c(), var6);
            case EAST:
               return this.func_175870_a(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78896_c + var4, this.field_74885_f, this.func_74877_c(), var6);
            }
         }

         return null;
      }

      protected StructureComponent func_74961_b(StructureNetherBridgePieces.Start var1, List<StructureComponent> var2, Random var3, int var4, int var5, boolean var6) {
         if (this.field_74885_f != null) {
            switch(this.field_74885_f) {
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

      protected StructureComponent func_74965_c(StructureNetherBridgePieces.Start var1, List<StructureComponent> var2, Random var3, int var4, int var5, boolean var6) {
         if (this.field_74885_f != null) {
            switch(this.field_74885_f) {
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

      protected static boolean func_74964_a(StructureBoundingBox var0) {
         return var0 != null && var0.field_78895_b > 10;
      }

      static {
         field_111019_a = Lists.newArrayList(new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151045_i, 0, 1, 3, 5), new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 5), new WeightedRandomChestContent(Items.field_151043_k, 0, 1, 3, 15), new WeightedRandomChestContent(Items.field_151010_B, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151171_ah, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151033_d, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151075_bm, 0, 3, 7, 5), new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 10), new WeightedRandomChestContent(Items.field_151136_bY, 0, 1, 1, 8), new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151125_bZ, 0, 1, 1, 3), new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150343_Z), 0, 2, 4, 2)});
      }
   }

   static class PieceWeight {
      public Class<? extends StructureNetherBridgePieces.Piece> field_78828_a;
      public final int field_78826_b;
      public int field_78827_c;
      public int field_78824_d;
      public boolean field_78825_e;

      public PieceWeight(Class<? extends StructureNetherBridgePieces.Piece> var1, int var2, int var3, boolean var4) {
         super();
         this.field_78828_a = var1;
         this.field_78826_b = var2;
         this.field_78824_d = var3;
         this.field_78825_e = var4;
      }

      public PieceWeight(Class<? extends StructureNetherBridgePieces.Piece> var1, int var2, int var3) {
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
