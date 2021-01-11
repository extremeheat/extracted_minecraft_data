package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureStrongholdPieces {
   private static final StructureStrongholdPieces.PieceWeight[] field_75205_b = new StructureStrongholdPieces.PieceWeight[]{new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Straight.class, 40, 0), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Prison.class, 5, 5), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.LeftTurn.class, 20, 0), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.RightTurn.class, 20, 0), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.RoomCrossing.class, 10, 6), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.StairsStraight.class, 5, 5), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Stairs.class, 5, 5), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Crossing.class, 5, 4), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.ChestCorridor.class, 5, 4), new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.Library.class, 10, 2) {
      public boolean func_75189_a(int var1) {
         return super.func_75189_a(var1) && var1 > 4;
      }
   }, new StructureStrongholdPieces.PieceWeight(StructureStrongholdPieces.PortalRoom.class, 20, 1) {
      public boolean func_75189_a(int var1) {
         return super.func_75189_a(var1) && var1 > 5;
      }
   }};
   private static List<StructureStrongholdPieces.PieceWeight> field_75206_c;
   private static Class<? extends StructureStrongholdPieces.Stronghold> field_75203_d;
   static int field_75207_a;
   private static final StructureStrongholdPieces.Stones field_75204_e = new StructureStrongholdPieces.Stones();

   public static void func_143046_a() {
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces.ChestCorridor.class, "SHCC");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces.Corridor.class, "SHFC");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces.Crossing.class, "SH5C");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces.LeftTurn.class, "SHLT");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces.Library.class, "SHLi");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces.PortalRoom.class, "SHPR");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces.Prison.class, "SHPH");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces.RightTurn.class, "SHRT");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces.RoomCrossing.class, "SHRC");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces.Stairs.class, "SHSD");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces.Stairs2.class, "SHStart");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces.Straight.class, "SHS");
      MapGenStructureIO.func_143031_a(StructureStrongholdPieces.StairsStraight.class, "SHSSD");
   }

   public static void func_75198_a() {
      field_75206_c = Lists.newArrayList();
      StructureStrongholdPieces.PieceWeight[] var0 = field_75205_b;
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         StructureStrongholdPieces.PieceWeight var3 = var0[var2];
         var3.field_75193_c = 0;
         field_75206_c.add(var3);
      }

      field_75203_d = null;
   }

   private static boolean func_75202_c() {
      boolean var0 = false;
      field_75207_a = 0;

      StructureStrongholdPieces.PieceWeight var2;
      for(Iterator var1 = field_75206_c.iterator(); var1.hasNext(); field_75207_a += var2.field_75192_b) {
         var2 = (StructureStrongholdPieces.PieceWeight)var1.next();
         if (var2.field_75191_d > 0 && var2.field_75193_c < var2.field_75191_d) {
            var0 = true;
         }
      }

      return var0;
   }

   private static StructureStrongholdPieces.Stronghold func_175954_a(Class<? extends StructureStrongholdPieces.Stronghold> var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
      Object var8 = null;
      if (var0 == StructureStrongholdPieces.Straight.class) {
         var8 = StructureStrongholdPieces.Straight.func_175862_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.Prison.class) {
         var8 = StructureStrongholdPieces.Prison.func_175860_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.LeftTurn.class) {
         var8 = StructureStrongholdPieces.LeftTurn.func_175867_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.RightTurn.class) {
         var8 = StructureStrongholdPieces.RightTurn.func_175867_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.RoomCrossing.class) {
         var8 = StructureStrongholdPieces.RoomCrossing.func_175859_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.StairsStraight.class) {
         var8 = StructureStrongholdPieces.StairsStraight.func_175861_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.Stairs.class) {
         var8 = StructureStrongholdPieces.Stairs.func_175863_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.Crossing.class) {
         var8 = StructureStrongholdPieces.Crossing.func_175866_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.ChestCorridor.class) {
         var8 = StructureStrongholdPieces.ChestCorridor.func_175868_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.Library.class) {
         var8 = StructureStrongholdPieces.Library.func_175864_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StructureStrongholdPieces.PortalRoom.class) {
         var8 = StructureStrongholdPieces.PortalRoom.func_175865_a(var1, var2, var3, var4, var5, var6, var7);
      }

      return (StructureStrongholdPieces.Stronghold)var8;
   }

   private static StructureStrongholdPieces.Stronghold func_175955_b(StructureStrongholdPieces.Stairs2 var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
      if (!func_75202_c()) {
         return null;
      } else {
         if (field_75203_d != null) {
            StructureStrongholdPieces.Stronghold var8 = func_175954_a(field_75203_d, var1, var2, var3, var4, var5, var6, var7);
            field_75203_d = null;
            if (var8 != null) {
               return var8;
            }
         }

         int var13 = 0;

         while(var13 < 5) {
            ++var13;
            int var9 = var2.nextInt(field_75207_a);
            Iterator var10 = field_75206_c.iterator();

            while(var10.hasNext()) {
               StructureStrongholdPieces.PieceWeight var11 = (StructureStrongholdPieces.PieceWeight)var10.next();
               var9 -= var11.field_75192_b;
               if (var9 < 0) {
                  if (!var11.func_75189_a(var7) || var11 == var0.field_75027_a) {
                     break;
                  }

                  StructureStrongholdPieces.Stronghold var12 = func_175954_a(var11.field_75194_a, var1, var2, var3, var4, var5, var6, var7);
                  if (var12 != null) {
                     ++var11.field_75193_c;
                     var0.field_75027_a = var11;
                     if (!var11.func_75190_a()) {
                        field_75206_c.remove(var11);
                     }

                     return var12;
                  }
               }
            }
         }

         StructureBoundingBox var14 = StructureStrongholdPieces.Corridor.func_175869_a(var1, var2, var3, var4, var5, var6);
         if (var14 != null && var14.field_78895_b > 1) {
            return new StructureStrongholdPieces.Corridor(var7, var2, var14, var6);
         } else {
            return null;
         }
      }
   }

   private static StructureComponent func_175953_c(StructureStrongholdPieces.Stairs2 var0, List<StructureComponent> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
      if (var7 > 50) {
         return null;
      } else if (Math.abs(var3 - var0.func_74874_b().field_78897_a) <= 112 && Math.abs(var5 - var0.func_74874_b().field_78896_c) <= 112) {
         StructureStrongholdPieces.Stronghold var8 = func_175955_b(var0, var1, var2, var3, var4, var5, var6, var7 + 1);
         if (var8 != null) {
            var1.add(var8);
            var0.field_75026_c.add(var8);
         }

         return var8;
      } else {
         return null;
      }
   }

   static class Stones extends StructureComponent.BlockSelector {
      private Stones() {
         super();
      }

      public void func_75062_a(Random var1, int var2, int var3, int var4, boolean var5) {
         if (var5) {
            float var6 = var1.nextFloat();
            if (var6 < 0.2F) {
               this.field_151562_a = Blocks.field_150417_aV.func_176203_a(BlockStoneBrick.field_176251_N);
            } else if (var6 < 0.5F) {
               this.field_151562_a = Blocks.field_150417_aV.func_176203_a(BlockStoneBrick.field_176250_M);
            } else if (var6 < 0.55F) {
               this.field_151562_a = Blocks.field_150418_aU.func_176203_a(BlockSilverfish.EnumType.STONEBRICK.func_176881_a());
            } else {
               this.field_151562_a = Blocks.field_150417_aV.func_176223_P();
            }
         } else {
            this.field_151562_a = Blocks.field_150350_a.func_176223_P();
         }

      }

      // $FF: synthetic method
      Stones(Object var1) {
         this();
      }
   }

   public static class PortalRoom extends StructureStrongholdPieces.Stronghold {
      private boolean field_75005_a;

      public PortalRoom() {
         super();
      }

      public PortalRoom(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Mob", this.field_75005_a);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_75005_a = var1.func_74767_n("Mob");
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         if (var1 != null) {
            ((StructureStrongholdPieces.Stairs2)var1).field_75025_b = this;
         }

      }

      public static StructureStrongholdPieces.PortalRoom func_175865_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -4, -1, 0, 11, 8, 16, var5);
         return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureStrongholdPieces.PortalRoom(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         this.func_74882_a(var1, var3, 0, 0, 0, 10, 7, 15, false, var2, StructureStrongholdPieces.field_75204_e);
         this.func_74990_a(var1, var2, var3, StructureStrongholdPieces.Stronghold.Door.GRATES, 4, 1, 0);
         byte var4 = 6;
         this.func_74882_a(var1, var3, 1, var4, 1, 1, var4, 14, false, var2, StructureStrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 9, var4, 1, 9, var4, 14, false, var2, StructureStrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 2, var4, 1, 8, var4, 2, false, var2, StructureStrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 2, var4, 14, 8, var4, 14, false, var2, StructureStrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 1, 1, 1, 2, 1, 4, false, var2, StructureStrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 8, 1, 1, 9, 1, 4, false, var2, StructureStrongholdPieces.field_75204_e);
         this.func_175804_a(var1, var3, 1, 1, 1, 1, 1, 3, Blocks.field_150356_k.func_176223_P(), Blocks.field_150356_k.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, 1, 1, 9, 1, 3, Blocks.field_150356_k.func_176223_P(), Blocks.field_150356_k.func_176223_P(), false);
         this.func_74882_a(var1, var3, 3, 1, 8, 7, 1, 12, false, var2, StructureStrongholdPieces.field_75204_e);
         this.func_175804_a(var1, var3, 4, 1, 9, 6, 1, 11, Blocks.field_150356_k.func_176223_P(), Blocks.field_150356_k.func_176223_P(), false);

         int var5;
         for(var5 = 3; var5 < 14; var5 += 2) {
            this.func_175804_a(var1, var3, 0, 3, var5, 0, 4, var5, Blocks.field_150411_aY.func_176223_P(), Blocks.field_150411_aY.func_176223_P(), false);
            this.func_175804_a(var1, var3, 10, 3, var5, 10, 4, var5, Blocks.field_150411_aY.func_176223_P(), Blocks.field_150411_aY.func_176223_P(), false);
         }

         for(var5 = 2; var5 < 9; var5 += 2) {
            this.func_175804_a(var1, var3, var5, 3, 15, var5, 4, 15, Blocks.field_150411_aY.func_176223_P(), Blocks.field_150411_aY.func_176223_P(), false);
         }

         var5 = this.func_151555_a(Blocks.field_150390_bg, 3);
         this.func_74882_a(var1, var3, 4, 1, 5, 6, 1, 7, false, var2, StructureStrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 4, 2, 6, 6, 2, 7, false, var2, StructureStrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 4, 3, 7, 6, 3, 7, false, var2, StructureStrongholdPieces.field_75204_e);

         int var6;
         for(var6 = 4; var6 <= 6; ++var6) {
            this.func_175811_a(var1, Blocks.field_150390_bg.func_176203_a(var5), var6, 1, 4, var3);
            this.func_175811_a(var1, Blocks.field_150390_bg.func_176203_a(var5), var6, 2, 5, var3);
            this.func_175811_a(var1, Blocks.field_150390_bg.func_176203_a(var5), var6, 3, 6, var3);
         }

         var6 = EnumFacing.NORTH.func_176736_b();
         int var7 = EnumFacing.SOUTH.func_176736_b();
         int var8 = EnumFacing.EAST.func_176736_b();
         int var9 = EnumFacing.WEST.func_176736_b();
         if (this.field_74885_f != null) {
            switch(this.field_74885_f) {
            case SOUTH:
               var6 = EnumFacing.SOUTH.func_176736_b();
               var7 = EnumFacing.NORTH.func_176736_b();
               break;
            case WEST:
               var6 = EnumFacing.WEST.func_176736_b();
               var7 = EnumFacing.EAST.func_176736_b();
               var8 = EnumFacing.SOUTH.func_176736_b();
               var9 = EnumFacing.NORTH.func_176736_b();
               break;
            case EAST:
               var6 = EnumFacing.EAST.func_176736_b();
               var7 = EnumFacing.WEST.func_176736_b();
               var8 = EnumFacing.SOUTH.func_176736_b();
               var9 = EnumFacing.NORTH.func_176736_b();
            }
         }

         this.func_175811_a(var1, Blocks.field_150378_br.func_176203_a(var6).func_177226_a(BlockEndPortalFrame.field_176507_b, var2.nextFloat() > 0.9F), 4, 3, 8, var3);
         this.func_175811_a(var1, Blocks.field_150378_br.func_176203_a(var6).func_177226_a(BlockEndPortalFrame.field_176507_b, var2.nextFloat() > 0.9F), 5, 3, 8, var3);
         this.func_175811_a(var1, Blocks.field_150378_br.func_176203_a(var6).func_177226_a(BlockEndPortalFrame.field_176507_b, var2.nextFloat() > 0.9F), 6, 3, 8, var3);
         this.func_175811_a(var1, Blocks.field_150378_br.func_176203_a(var7).func_177226_a(BlockEndPortalFrame.field_176507_b, var2.nextFloat() > 0.9F), 4, 3, 12, var3);
         this.func_175811_a(var1, Blocks.field_150378_br.func_176203_a(var7).func_177226_a(BlockEndPortalFrame.field_176507_b, var2.nextFloat() > 0.9F), 5, 3, 12, var3);
         this.func_175811_a(var1, Blocks.field_150378_br.func_176203_a(var7).func_177226_a(BlockEndPortalFrame.field_176507_b, var2.nextFloat() > 0.9F), 6, 3, 12, var3);
         this.func_175811_a(var1, Blocks.field_150378_br.func_176203_a(var8).func_177226_a(BlockEndPortalFrame.field_176507_b, var2.nextFloat() > 0.9F), 3, 3, 9, var3);
         this.func_175811_a(var1, Blocks.field_150378_br.func_176203_a(var8).func_177226_a(BlockEndPortalFrame.field_176507_b, var2.nextFloat() > 0.9F), 3, 3, 10, var3);
         this.func_175811_a(var1, Blocks.field_150378_br.func_176203_a(var8).func_177226_a(BlockEndPortalFrame.field_176507_b, var2.nextFloat() > 0.9F), 3, 3, 11, var3);
         this.func_175811_a(var1, Blocks.field_150378_br.func_176203_a(var9).func_177226_a(BlockEndPortalFrame.field_176507_b, var2.nextFloat() > 0.9F), 7, 3, 9, var3);
         this.func_175811_a(var1, Blocks.field_150378_br.func_176203_a(var9).func_177226_a(BlockEndPortalFrame.field_176507_b, var2.nextFloat() > 0.9F), 7, 3, 10, var3);
         this.func_175811_a(var1, Blocks.field_150378_br.func_176203_a(var9).func_177226_a(BlockEndPortalFrame.field_176507_b, var2.nextFloat() > 0.9F), 7, 3, 11, var3);
         if (!this.field_75005_a) {
            int var12 = this.func_74862_a(3);
            BlockPos var10 = new BlockPos(this.func_74865_a(5, 6), var12, this.func_74873_b(5, 6));
            if (var3.func_175898_b(var10)) {
               this.field_75005_a = true;
               var1.func_180501_a(var10, Blocks.field_150474_ac.func_176223_P(), 2);
               TileEntity var11 = var1.func_175625_s(var10);
               if (var11 instanceof TileEntityMobSpawner) {
                  ((TileEntityMobSpawner)var11).func_145881_a().func_98272_a("Silverfish");
               }
            }
         }

         return true;
      }
   }

   public static class Crossing extends StructureStrongholdPieces.Stronghold {
      private boolean field_74996_b;
      private boolean field_74997_c;
      private boolean field_74995_d;
      private boolean field_74999_h;

      public Crossing() {
         super();
      }

      public Crossing(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
         this.field_74996_b = var2.nextBoolean();
         this.field_74997_c = var2.nextBoolean();
         this.field_74995_d = var2.nextBoolean();
         this.field_74999_h = var2.nextInt(3) > 0;
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("leftLow", this.field_74996_b);
         var1.func_74757_a("leftHigh", this.field_74997_c);
         var1.func_74757_a("rightLow", this.field_74995_d);
         var1.func_74757_a("rightHigh", this.field_74999_h);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_74996_b = var1.func_74767_n("leftLow");
         this.field_74997_c = var1.func_74767_n("leftHigh");
         this.field_74995_d = var1.func_74767_n("rightLow");
         this.field_74999_h = var1.func_74767_n("rightHigh");
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         int var4 = 3;
         int var5 = 5;
         if (this.field_74885_f == EnumFacing.WEST || this.field_74885_f == EnumFacing.NORTH) {
            var4 = 8 - var4;
            var5 = 8 - var5;
         }

         this.func_74986_a((StructureStrongholdPieces.Stairs2)var1, var2, var3, 5, 1);
         if (this.field_74996_b) {
            this.func_74989_b((StructureStrongholdPieces.Stairs2)var1, var2, var3, var4, 1);
         }

         if (this.field_74997_c) {
            this.func_74989_b((StructureStrongholdPieces.Stairs2)var1, var2, var3, var5, 7);
         }

         if (this.field_74995_d) {
            this.func_74987_c((StructureStrongholdPieces.Stairs2)var1, var2, var3, var4, 1);
         }

         if (this.field_74999_h) {
            this.func_74987_c((StructureStrongholdPieces.Stairs2)var1, var2, var3, var5, 7);
         }

      }

      public static StructureStrongholdPieces.Crossing func_175866_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -4, -3, 0, 10, 9, 11, var5);
         return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureStrongholdPieces.Crossing(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            this.func_74882_a(var1, var3, 0, 0, 0, 9, 8, 10, true, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74990_a(var1, var2, var3, this.field_143013_d, 4, 3, 0);
            if (this.field_74996_b) {
               this.func_175804_a(var1, var3, 0, 3, 1, 0, 5, 3, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            }

            if (this.field_74995_d) {
               this.func_175804_a(var1, var3, 9, 3, 1, 9, 5, 3, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            }

            if (this.field_74997_c) {
               this.func_175804_a(var1, var3, 0, 5, 7, 0, 7, 9, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            }

            if (this.field_74999_h) {
               this.func_175804_a(var1, var3, 9, 5, 7, 9, 7, 9, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            }

            this.func_175804_a(var1, var3, 5, 1, 10, 7, 3, 10, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            this.func_74882_a(var1, var3, 1, 2, 1, 8, 2, 6, false, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74882_a(var1, var3, 4, 1, 5, 4, 4, 9, false, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74882_a(var1, var3, 8, 1, 5, 8, 4, 9, false, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74882_a(var1, var3, 1, 4, 7, 3, 4, 9, false, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74882_a(var1, var3, 1, 3, 5, 3, 3, 6, false, var2, StructureStrongholdPieces.field_75204_e);
            this.func_175804_a(var1, var3, 1, 3, 4, 3, 3, 4, Blocks.field_150333_U.func_176223_P(), Blocks.field_150333_U.func_176223_P(), false);
            this.func_175804_a(var1, var3, 1, 4, 6, 3, 4, 6, Blocks.field_150333_U.func_176223_P(), Blocks.field_150333_U.func_176223_P(), false);
            this.func_74882_a(var1, var3, 5, 1, 7, 7, 1, 8, false, var2, StructureStrongholdPieces.field_75204_e);
            this.func_175804_a(var1, var3, 5, 1, 9, 7, 1, 9, Blocks.field_150333_U.func_176223_P(), Blocks.field_150333_U.func_176223_P(), false);
            this.func_175804_a(var1, var3, 5, 2, 7, 7, 2, 7, Blocks.field_150333_U.func_176223_P(), Blocks.field_150333_U.func_176223_P(), false);
            this.func_175804_a(var1, var3, 4, 5, 7, 4, 5, 9, Blocks.field_150333_U.func_176223_P(), Blocks.field_150333_U.func_176223_P(), false);
            this.func_175804_a(var1, var3, 8, 5, 7, 8, 5, 9, Blocks.field_150333_U.func_176223_P(), Blocks.field_150333_U.func_176223_P(), false);
            this.func_175804_a(var1, var3, 5, 5, 7, 7, 5, 9, Blocks.field_150334_T.func_176223_P(), Blocks.field_150334_T.func_176223_P(), false);
            this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P(), 6, 5, 6, var3);
            return true;
         }
      }
   }

   public static class Library extends StructureStrongholdPieces.Stronghold {
      private static final List<WeightedRandomChestContent> field_75007_b;
      private boolean field_75008_c;

      public Library() {
         super();
      }

      public Library(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
         this.field_75008_c = var3.func_78882_c() > 6;
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Tall", this.field_75008_c);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_75008_c = var1.func_74767_n("Tall");
      }

      public static StructureStrongholdPieces.Library func_175864_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -4, -1, 0, 14, 11, 15, var5);
         if (!func_74991_a(var7) || StructureComponent.func_74883_a(var0, var7) != null) {
            var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -4, -1, 0, 14, 6, 15, var5);
            if (!func_74991_a(var7) || StructureComponent.func_74883_a(var0, var7) != null) {
               return null;
            }
         }

         return new StructureStrongholdPieces.Library(var6, var1, var7, var5);
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            byte var4 = 11;
            if (!this.field_75008_c) {
               var4 = 6;
            }

            this.func_74882_a(var1, var3, 0, 0, 0, 13, var4 - 1, 14, true, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74990_a(var1, var2, var3, this.field_143013_d, 4, 1, 0);
            this.func_175805_a(var1, var3, var2, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.field_150321_G.func_176223_P(), Blocks.field_150321_G.func_176223_P(), false);
            boolean var5 = true;
            boolean var6 = true;

            int var7;
            for(var7 = 1; var7 <= 13; ++var7) {
               if ((var7 - 1) % 4 == 0) {
                  this.func_175804_a(var1, var3, 1, 1, var7, 1, 4, var7, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
                  this.func_175804_a(var1, var3, 12, 1, var7, 12, 4, var7, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
                  this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P(), 2, 3, var7, var3);
                  this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P(), 11, 3, var7, var3);
                  if (this.field_75008_c) {
                     this.func_175804_a(var1, var3, 1, 6, var7, 1, 9, var7, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
                     this.func_175804_a(var1, var3, 12, 6, var7, 12, 9, var7, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
                  }
               } else {
                  this.func_175804_a(var1, var3, 1, 1, var7, 1, 4, var7, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
                  this.func_175804_a(var1, var3, 12, 1, var7, 12, 4, var7, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
                  if (this.field_75008_c) {
                     this.func_175804_a(var1, var3, 1, 6, var7, 1, 9, var7, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
                     this.func_175804_a(var1, var3, 12, 6, var7, 12, 9, var7, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
                  }
               }
            }

            for(var7 = 3; var7 < 12; var7 += 2) {
               this.func_175804_a(var1, var3, 3, 1, var7, 4, 3, var7, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
               this.func_175804_a(var1, var3, 6, 1, var7, 7, 3, var7, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
               this.func_175804_a(var1, var3, 9, 1, var7, 10, 3, var7, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
            }

            if (this.field_75008_c) {
               this.func_175804_a(var1, var3, 1, 5, 1, 3, 5, 13, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
               this.func_175804_a(var1, var3, 10, 5, 1, 12, 5, 13, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
               this.func_175804_a(var1, var3, 4, 5, 1, 9, 5, 2, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
               this.func_175804_a(var1, var3, 4, 5, 12, 9, 5, 13, Blocks.field_150344_f.func_176223_P(), Blocks.field_150344_f.func_176223_P(), false);
               this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 9, 5, 11, var3);
               this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 8, 5, 11, var3);
               this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 9, 5, 10, var3);
               this.func_175804_a(var1, var3, 3, 6, 2, 3, 6, 12, Blocks.field_180407_aO.func_176223_P(), Blocks.field_180407_aO.func_176223_P(), false);
               this.func_175804_a(var1, var3, 10, 6, 2, 10, 6, 10, Blocks.field_180407_aO.func_176223_P(), Blocks.field_180407_aO.func_176223_P(), false);
               this.func_175804_a(var1, var3, 4, 6, 2, 9, 6, 2, Blocks.field_180407_aO.func_176223_P(), Blocks.field_180407_aO.func_176223_P(), false);
               this.func_175804_a(var1, var3, 4, 6, 12, 8, 6, 12, Blocks.field_180407_aO.func_176223_P(), Blocks.field_180407_aO.func_176223_P(), false);
               this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 9, 6, 11, var3);
               this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 8, 6, 11, var3);
               this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), 9, 6, 10, var3);
               var7 = this.func_151555_a(Blocks.field_150468_ap, 3);
               this.func_175811_a(var1, Blocks.field_150468_ap.func_176203_a(var7), 10, 1, 13, var3);
               this.func_175811_a(var1, Blocks.field_150468_ap.func_176203_a(var7), 10, 2, 13, var3);
               this.func_175811_a(var1, Blocks.field_150468_ap.func_176203_a(var7), 10, 3, 13, var3);
               this.func_175811_a(var1, Blocks.field_150468_ap.func_176203_a(var7), 10, 4, 13, var3);
               this.func_175811_a(var1, Blocks.field_150468_ap.func_176203_a(var7), 10, 5, 13, var3);
               this.func_175811_a(var1, Blocks.field_150468_ap.func_176203_a(var7), 10, 6, 13, var3);
               this.func_175811_a(var1, Blocks.field_150468_ap.func_176203_a(var7), 10, 7, 13, var3);
               byte var8 = 7;
               byte var9 = 7;
               this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), var8 - 1, 9, var9, var3);
               this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), var8, 9, var9, var3);
               this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), var8 - 1, 8, var9, var3);
               this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), var8, 8, var9, var3);
               this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), var8 - 1, 7, var9, var3);
               this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), var8, 7, var9, var3);
               this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), var8 - 2, 7, var9, var3);
               this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), var8 + 1, 7, var9, var3);
               this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), var8 - 1, 7, var9 - 1, var3);
               this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), var8 - 1, 7, var9 + 1, var3);
               this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), var8, 7, var9 - 1, var3);
               this.func_175811_a(var1, Blocks.field_180407_aO.func_176223_P(), var8, 7, var9 + 1, var3);
               this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P(), var8 - 2, 8, var9, var3);
               this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P(), var8 + 1, 8, var9, var3);
               this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P(), var8 - 1, 8, var9 - 1, var3);
               this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P(), var8 - 1, 8, var9 + 1, var3);
               this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P(), var8, 8, var9 - 1, var3);
               this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P(), var8, 8, var9 + 1, var3);
            }

            this.func_180778_a(var1, var3, var2, 3, 3, 5, WeightedRandomChestContent.func_177629_a(field_75007_b, Items.field_151134_bR.func_92112_a(var2, 1, 5, 2)), 1 + var2.nextInt(4));
            if (this.field_75008_c) {
               this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 12, 9, 1, var3);
               this.func_180778_a(var1, var3, var2, 12, 8, 1, WeightedRandomChestContent.func_177629_a(field_75007_b, Items.field_151134_bR.func_92112_a(var2, 1, 5, 2)), 1 + var2.nextInt(4));
            }

            return true;
         }
      }

      static {
         field_75007_b = Lists.newArrayList(new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151122_aG, 0, 1, 3, 20), new WeightedRandomChestContent(Items.field_151121_aF, 0, 2, 7, 20), new WeightedRandomChestContent(Items.field_151148_bJ, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151111_aL, 0, 1, 1, 1)});
      }
   }

   public static class Prison extends StructureStrongholdPieces.Stronghold {
      public Prison() {
         super();
      }

      public Prison(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74986_a((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
      }

      public static StructureStrongholdPieces.Prison func_175860_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -1, -1, 0, 9, 5, 11, var5);
         return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureStrongholdPieces.Prison(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            this.func_74882_a(var1, var3, 0, 0, 0, 8, 4, 10, true, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 1, 0);
            this.func_175804_a(var1, var3, 1, 1, 10, 3, 3, 10, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            this.func_74882_a(var1, var3, 4, 1, 1, 4, 3, 1, false, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74882_a(var1, var3, 4, 1, 3, 4, 3, 3, false, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74882_a(var1, var3, 4, 1, 7, 4, 3, 7, false, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74882_a(var1, var3, 4, 1, 9, 4, 3, 9, false, var2, StructureStrongholdPieces.field_75204_e);
            this.func_175804_a(var1, var3, 4, 1, 4, 4, 3, 6, Blocks.field_150411_aY.func_176223_P(), Blocks.field_150411_aY.func_176223_P(), false);
            this.func_175804_a(var1, var3, 5, 1, 5, 7, 3, 5, Blocks.field_150411_aY.func_176223_P(), Blocks.field_150411_aY.func_176223_P(), false);
            this.func_175811_a(var1, Blocks.field_150411_aY.func_176223_P(), 4, 3, 2, var3);
            this.func_175811_a(var1, Blocks.field_150411_aY.func_176223_P(), 4, 3, 8, var3);
            this.func_175811_a(var1, Blocks.field_150454_av.func_176203_a(this.func_151555_a(Blocks.field_150454_av, 3)), 4, 1, 2, var3);
            this.func_175811_a(var1, Blocks.field_150454_av.func_176203_a(this.func_151555_a(Blocks.field_150454_av, 3) + 8), 4, 2, 2, var3);
            this.func_175811_a(var1, Blocks.field_150454_av.func_176203_a(this.func_151555_a(Blocks.field_150454_av, 3)), 4, 1, 8, var3);
            this.func_175811_a(var1, Blocks.field_150454_av.func_176203_a(this.func_151555_a(Blocks.field_150454_av, 3) + 8), 4, 2, 8, var3);
            return true;
         }
      }
   }

   public static class RoomCrossing extends StructureStrongholdPieces.Stronghold {
      private static final List<WeightedRandomChestContent> field_75014_c;
      protected int field_75013_b;

      public RoomCrossing() {
         super();
      }

      public RoomCrossing(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
         this.field_75013_b = var2.nextInt(5);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74768_a("Type", this.field_75013_b);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_75013_b = var1.func_74762_e("Type");
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74986_a((StructureStrongholdPieces.Stairs2)var1, var2, var3, 4, 1);
         this.func_74989_b((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 4);
         this.func_74987_c((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 4);
      }

      public static StructureStrongholdPieces.RoomCrossing func_175859_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -4, -1, 0, 11, 7, 11, var5);
         return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureStrongholdPieces.RoomCrossing(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            this.func_74882_a(var1, var3, 0, 0, 0, 10, 6, 10, true, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74990_a(var1, var2, var3, this.field_143013_d, 4, 1, 0);
            this.func_175804_a(var1, var3, 4, 1, 10, 6, 3, 10, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            this.func_175804_a(var1, var3, 0, 1, 4, 0, 3, 6, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            this.func_175804_a(var1, var3, 10, 1, 4, 10, 3, 6, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            int var4;
            switch(this.field_75013_b) {
            case 0:
               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 5, 1, 5, var3);
               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 5, 2, 5, var3);
               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 5, 3, 5, var3);
               this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P(), 4, 3, 5, var3);
               this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P(), 6, 3, 5, var3);
               this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P(), 5, 3, 4, var3);
               this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P(), 5, 3, 6, var3);
               this.func_175811_a(var1, Blocks.field_150333_U.func_176223_P(), 4, 1, 4, var3);
               this.func_175811_a(var1, Blocks.field_150333_U.func_176223_P(), 4, 1, 5, var3);
               this.func_175811_a(var1, Blocks.field_150333_U.func_176223_P(), 4, 1, 6, var3);
               this.func_175811_a(var1, Blocks.field_150333_U.func_176223_P(), 6, 1, 4, var3);
               this.func_175811_a(var1, Blocks.field_150333_U.func_176223_P(), 6, 1, 5, var3);
               this.func_175811_a(var1, Blocks.field_150333_U.func_176223_P(), 6, 1, 6, var3);
               this.func_175811_a(var1, Blocks.field_150333_U.func_176223_P(), 5, 1, 4, var3);
               this.func_175811_a(var1, Blocks.field_150333_U.func_176223_P(), 5, 1, 6, var3);
               break;
            case 1:
               for(var4 = 0; var4 < 5; ++var4) {
                  this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 3, 1, 3 + var4, var3);
                  this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 7, 1, 3 + var4, var3);
                  this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 3 + var4, 1, 3, var3);
                  this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 3 + var4, 1, 7, var3);
               }

               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 5, 1, 5, var3);
               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 5, 2, 5, var3);
               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 5, 3, 5, var3);
               this.func_175811_a(var1, Blocks.field_150358_i.func_176223_P(), 5, 4, 5, var3);
               break;
            case 2:
               for(var4 = 1; var4 <= 9; ++var4) {
                  this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 1, 3, var4, var3);
                  this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 9, 3, var4, var3);
               }

               for(var4 = 1; var4 <= 9; ++var4) {
                  this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), var4, 3, 1, var3);
                  this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), var4, 3, 9, var3);
               }

               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 5, 1, 4, var3);
               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 5, 1, 6, var3);
               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 5, 3, 4, var3);
               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 5, 3, 6, var3);
               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 4, 1, 5, var3);
               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 6, 1, 5, var3);
               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 4, 3, 5, var3);
               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 6, 3, 5, var3);

               for(var4 = 1; var4 <= 3; ++var4) {
                  this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 4, var4, 4, var3);
                  this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 6, var4, 4, var3);
                  this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 4, var4, 6, var3);
                  this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 6, var4, 6, var3);
               }

               this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P(), 5, 3, 5, var3);

               for(var4 = 2; var4 <= 8; ++var4) {
                  this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 2, 3, var4, var3);
                  this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 3, 3, var4, var3);
                  if (var4 <= 3 || var4 >= 7) {
                     this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 4, 3, var4, var3);
                     this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 5, 3, var4, var3);
                     this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 6, 3, var4, var3);
                  }

                  this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 7, 3, var4, var3);
                  this.func_175811_a(var1, Blocks.field_150344_f.func_176223_P(), 8, 3, var4, var3);
               }

               this.func_175811_a(var1, Blocks.field_150468_ap.func_176203_a(this.func_151555_a(Blocks.field_150468_ap, EnumFacing.WEST.func_176745_a())), 9, 1, 3, var3);
               this.func_175811_a(var1, Blocks.field_150468_ap.func_176203_a(this.func_151555_a(Blocks.field_150468_ap, EnumFacing.WEST.func_176745_a())), 9, 2, 3, var3);
               this.func_175811_a(var1, Blocks.field_150468_ap.func_176203_a(this.func_151555_a(Blocks.field_150468_ap, EnumFacing.WEST.func_176745_a())), 9, 3, 3, var3);
               this.func_180778_a(var1, var3, var2, 3, 4, 8, WeightedRandomChestContent.func_177629_a(field_75014_c, Items.field_151134_bR.func_92114_b(var2)), 1 + var2.nextInt(4));
            }

            return true;
         }
      }

      static {
         field_75014_c = Lists.newArrayList(new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 10), new WeightedRandomChestContent(Items.field_151043_k, 0, 1, 3, 5), new WeightedRandomChestContent(Items.field_151137_ax, 0, 4, 9, 5), new WeightedRandomChestContent(Items.field_151044_h, 0, 3, 8, 10), new WeightedRandomChestContent(Items.field_151025_P, 0, 1, 3, 15), new WeightedRandomChestContent(Items.field_151034_e, 0, 1, 3, 15), new WeightedRandomChestContent(Items.field_151035_b, 0, 1, 1, 1)});
      }
   }

   public static class RightTurn extends StructureStrongholdPieces.LeftTurn {
      public RightTurn() {
         super();
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         if (this.field_74885_f != EnumFacing.NORTH && this.field_74885_f != EnumFacing.EAST) {
            this.func_74989_b((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
         } else {
            this.func_74987_c((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
         }

      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            this.func_74882_a(var1, var3, 0, 0, 0, 4, 4, 4, true, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 1, 0);
            if (this.field_74885_f != EnumFacing.NORTH && this.field_74885_f != EnumFacing.EAST) {
               this.func_175804_a(var1, var3, 0, 1, 1, 0, 3, 3, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            } else {
               this.func_175804_a(var1, var3, 4, 1, 1, 4, 3, 3, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            }

            return true;
         }
      }
   }

   public static class LeftTurn extends StructureStrongholdPieces.Stronghold {
      public LeftTurn() {
         super();
      }

      public LeftTurn(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         if (this.field_74885_f != EnumFacing.NORTH && this.field_74885_f != EnumFacing.EAST) {
            this.func_74987_c((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
         } else {
            this.func_74989_b((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
         }

      }

      public static StructureStrongholdPieces.LeftTurn func_175867_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -1, -1, 0, 5, 5, 5, var5);
         return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureStrongholdPieces.LeftTurn(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            this.func_74882_a(var1, var3, 0, 0, 0, 4, 4, 4, true, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 1, 0);
            if (this.field_74885_f != EnumFacing.NORTH && this.field_74885_f != EnumFacing.EAST) {
               this.func_175804_a(var1, var3, 4, 1, 1, 4, 3, 3, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            } else {
               this.func_175804_a(var1, var3, 0, 1, 1, 0, 3, 3, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            }

            return true;
         }
      }
   }

   public static class StairsStraight extends StructureStrongholdPieces.Stronghold {
      public StairsStraight() {
         super();
      }

      public StairsStraight(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74986_a((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
      }

      public static StructureStrongholdPieces.StairsStraight func_175861_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -1, -7, 0, 5, 11, 8, var5);
         return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureStrongholdPieces.StairsStraight(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            this.func_74882_a(var1, var3, 0, 0, 0, 4, 10, 7, true, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 7, 0);
            this.func_74990_a(var1, var2, var3, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 7);
            int var4 = this.func_151555_a(Blocks.field_150446_ar, 2);

            for(int var5 = 0; var5 < 6; ++var5) {
               this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var4), 1, 6 - var5, 1 + var5, var3);
               this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var4), 2, 6 - var5, 1 + var5, var3);
               this.func_175811_a(var1, Blocks.field_150446_ar.func_176203_a(var4), 3, 6 - var5, 1 + var5, var3);
               if (var5 < 5) {
                  this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 1, 5 - var5, 1 + var5, var3);
                  this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 2, 5 - var5, 1 + var5, var3);
                  this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 3, 5 - var5, 1 + var5, var3);
               }
            }

            return true;
         }
      }
   }

   public static class ChestCorridor extends StructureStrongholdPieces.Stronghold {
      private static final List<WeightedRandomChestContent> field_75003_a;
      private boolean field_75002_c;

      public ChestCorridor() {
         super();
      }

      public ChestCorridor(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Chest", this.field_75002_c);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_75002_c = var1.func_74767_n("Chest");
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74986_a((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
      }

      public static StructureStrongholdPieces.ChestCorridor func_175868_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -1, -1, 0, 5, 5, 7, var5);
         return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureStrongholdPieces.ChestCorridor(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            this.func_74882_a(var1, var3, 0, 0, 0, 4, 4, 6, true, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 1, 0);
            this.func_74990_a(var1, var2, var3, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
            this.func_175804_a(var1, var3, 3, 1, 2, 3, 1, 4, Blocks.field_150417_aV.func_176223_P(), Blocks.field_150417_aV.func_176223_P(), false);
            this.func_175811_a(var1, Blocks.field_150333_U.func_176203_a(BlockStoneSlab.EnumType.SMOOTHBRICK.func_176624_a()), 3, 1, 1, var3);
            this.func_175811_a(var1, Blocks.field_150333_U.func_176203_a(BlockStoneSlab.EnumType.SMOOTHBRICK.func_176624_a()), 3, 1, 5, var3);
            this.func_175811_a(var1, Blocks.field_150333_U.func_176203_a(BlockStoneSlab.EnumType.SMOOTHBRICK.func_176624_a()), 3, 2, 2, var3);
            this.func_175811_a(var1, Blocks.field_150333_U.func_176203_a(BlockStoneSlab.EnumType.SMOOTHBRICK.func_176624_a()), 3, 2, 4, var3);

            for(int var4 = 2; var4 <= 4; ++var4) {
               this.func_175811_a(var1, Blocks.field_150333_U.func_176203_a(BlockStoneSlab.EnumType.SMOOTHBRICK.func_176624_a()), 2, 1, var4, var3);
            }

            if (!this.field_75002_c && var3.func_175898_b(new BlockPos(this.func_74865_a(3, 3), this.func_74862_a(2), this.func_74873_b(3, 3)))) {
               this.field_75002_c = true;
               this.func_180778_a(var1, var3, var2, 3, 2, 3, WeightedRandomChestContent.func_177629_a(field_75003_a, Items.field_151134_bR.func_92114_b(var2)), 2 + var2.nextInt(2));
            }

            return true;
         }
      }

      static {
         field_75003_a = Lists.newArrayList(new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151079_bi, 0, 1, 1, 10), new WeightedRandomChestContent(Items.field_151045_i, 0, 1, 3, 3), new WeightedRandomChestContent(Items.field_151042_j, 0, 1, 5, 10), new WeightedRandomChestContent(Items.field_151043_k, 0, 1, 3, 5), new WeightedRandomChestContent(Items.field_151137_ax, 0, 4, 9, 5), new WeightedRandomChestContent(Items.field_151025_P, 0, 1, 3, 15), new WeightedRandomChestContent(Items.field_151034_e, 0, 1, 3, 15), new WeightedRandomChestContent(Items.field_151035_b, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151040_l, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151030_Z, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151028_Y, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151165_aa, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151167_ab, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151153_ao, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151141_av, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151138_bX, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151136_bY, 0, 1, 1, 1), new WeightedRandomChestContent(Items.field_151125_bZ, 0, 1, 1, 1)});
      }
   }

   public static class Straight extends StructureStrongholdPieces.Stronghold {
      private boolean field_75019_b;
      private boolean field_75020_c;

      public Straight() {
         super();
      }

      public Straight(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
         this.field_75019_b = var2.nextInt(2) == 0;
         this.field_75020_c = var2.nextInt(2) == 0;
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Left", this.field_75019_b);
         var1.func_74757_a("Right", this.field_75020_c);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_75019_b = var1.func_74767_n("Left");
         this.field_75020_c = var1.func_74767_n("Right");
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         this.func_74986_a((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
         if (this.field_75019_b) {
            this.func_74989_b((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 2);
         }

         if (this.field_75020_c) {
            this.func_74987_c((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 2);
         }

      }

      public static StructureStrongholdPieces.Straight func_175862_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -1, -1, 0, 5, 5, 7, var5);
         return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureStrongholdPieces.Straight(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            this.func_74882_a(var1, var3, 0, 0, 0, 4, 4, 6, true, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 1, 0);
            this.func_74990_a(var1, var2, var3, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
            this.func_175809_a(var1, var3, var2, 0.1F, 1, 2, 1, Blocks.field_150478_aa.func_176223_P());
            this.func_175809_a(var1, var3, var2, 0.1F, 3, 2, 1, Blocks.field_150478_aa.func_176223_P());
            this.func_175809_a(var1, var3, var2, 0.1F, 1, 2, 5, Blocks.field_150478_aa.func_176223_P());
            this.func_175809_a(var1, var3, var2, 0.1F, 3, 2, 5, Blocks.field_150478_aa.func_176223_P());
            if (this.field_75019_b) {
               this.func_175804_a(var1, var3, 0, 1, 2, 0, 3, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            }

            if (this.field_75020_c) {
               this.func_175804_a(var1, var3, 4, 1, 2, 4, 3, 4, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            }

            return true;
         }
      }
   }

   public static class Stairs2 extends StructureStrongholdPieces.Stairs {
      public StructureStrongholdPieces.PieceWeight field_75027_a;
      public StructureStrongholdPieces.PortalRoom field_75025_b;
      public List<StructureComponent> field_75026_c = Lists.newArrayList();

      public Stairs2() {
         super();
      }

      public Stairs2(int var1, Random var2, int var3, int var4) {
         super(0, var2, var3, var4);
      }

      public BlockPos func_180776_a() {
         return this.field_75025_b != null ? this.field_75025_b.func_180776_a() : super.func_180776_a();
      }
   }

   public static class Stairs extends StructureStrongholdPieces.Stronghold {
      private boolean field_75024_a;

      public Stairs() {
         super();
      }

      public Stairs(int var1, Random var2, int var3, int var4) {
         super(var1);
         this.field_75024_a = true;
         this.field_74885_f = EnumFacing.Plane.HORIZONTAL.func_179518_a(var2);
         this.field_143013_d = StructureStrongholdPieces.Stronghold.Door.OPENING;
         switch(this.field_74885_f) {
         case NORTH:
         case SOUTH:
            this.field_74887_e = new StructureBoundingBox(var3, 64, var4, var3 + 5 - 1, 74, var4 + 5 - 1);
            break;
         default:
            this.field_74887_e = new StructureBoundingBox(var3, 64, var4, var3 + 5 - 1, 74, var4 + 5 - 1);
         }

      }

      public Stairs(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_75024_a = false;
         this.field_74885_f = var4;
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Source", this.field_75024_a);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_75024_a = var1.func_74767_n("Source");
      }

      public void func_74861_a(StructureComponent var1, List<StructureComponent> var2, Random var3) {
         if (this.field_75024_a) {
            StructureStrongholdPieces.field_75203_d = StructureStrongholdPieces.Crossing.class;
         }

         this.func_74986_a((StructureStrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
      }

      public static StructureStrongholdPieces.Stairs func_175863_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -1, -7, 0, 5, 11, 5, var5);
         return func_74991_a(var7) && StructureComponent.func_74883_a(var0, var7) == null ? new StructureStrongholdPieces.Stairs(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            this.func_74882_a(var1, var3, 0, 0, 0, 4, 10, 4, true, var2, StructureStrongholdPieces.field_75204_e);
            this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 7, 0);
            this.func_74990_a(var1, var2, var3, StructureStrongholdPieces.Stronghold.Door.OPENING, 1, 1, 4);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 2, 6, 1, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 1, 5, 1, var3);
            this.func_175811_a(var1, Blocks.field_150333_U.func_176203_a(BlockStoneSlab.EnumType.STONE.func_176624_a()), 1, 6, 1, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 1, 5, 2, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 1, 4, 3, var3);
            this.func_175811_a(var1, Blocks.field_150333_U.func_176203_a(BlockStoneSlab.EnumType.STONE.func_176624_a()), 1, 5, 3, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 2, 4, 3, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 3, 3, 3, var3);
            this.func_175811_a(var1, Blocks.field_150333_U.func_176203_a(BlockStoneSlab.EnumType.STONE.func_176624_a()), 3, 4, 3, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 3, 3, 2, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 3, 2, 1, var3);
            this.func_175811_a(var1, Blocks.field_150333_U.func_176203_a(BlockStoneSlab.EnumType.STONE.func_176624_a()), 3, 3, 1, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 2, 2, 1, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 1, 1, 1, var3);
            this.func_175811_a(var1, Blocks.field_150333_U.func_176203_a(BlockStoneSlab.EnumType.STONE.func_176624_a()), 1, 2, 1, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 1, 1, 2, var3);
            this.func_175811_a(var1, Blocks.field_150333_U.func_176203_a(BlockStoneSlab.EnumType.STONE.func_176624_a()), 1, 1, 3, var3);
            return true;
         }
      }
   }

   public static class Corridor extends StructureStrongholdPieces.Stronghold {
      private int field_74993_a;

      public Corridor() {
         super();
      }

      public Corridor(int var1, Random var2, StructureBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_74885_f = var4;
         this.field_74887_e = var3;
         this.field_74993_a = var4 != EnumFacing.NORTH && var4 != EnumFacing.SOUTH ? var3.func_78883_b() : var3.func_78880_d();
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74768_a("Steps", this.field_74993_a);
      }

      protected void func_143011_b(NBTTagCompound var1) {
         super.func_143011_b(var1);
         this.field_74993_a = var1.func_74762_e("Steps");
      }

      public static StructureBoundingBox func_175869_a(List<StructureComponent> var0, Random var1, int var2, int var3, int var4, EnumFacing var5) {
         boolean var6 = true;
         StructureBoundingBox var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -1, -1, 0, 5, 5, 4, var5);
         StructureComponent var8 = StructureComponent.func_74883_a(var0, var7);
         if (var8 == null) {
            return null;
         } else {
            if (var8.func_74874_b().field_78895_b == var7.field_78895_b) {
               for(int var9 = 3; var9 >= 1; --var9) {
                  var7 = StructureBoundingBox.func_175897_a(var2, var3, var4, -1, -1, 0, 5, 5, var9 - 1, var5);
                  if (!var8.func_74874_b().func_78884_a(var7)) {
                     return StructureBoundingBox.func_175897_a(var2, var3, var4, -1, -1, 0, 5, 5, var9, var5);
                  }
               }
            }

            return null;
         }
      }

      public boolean func_74875_a(World var1, Random var2, StructureBoundingBox var3) {
         if (this.func_74860_a(var1, var3)) {
            return false;
         } else {
            for(int var4 = 0; var4 < this.field_74993_a; ++var4) {
               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 0, 0, var4, var3);
               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 1, 0, var4, var3);
               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 2, 0, var4, var3);
               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 3, 0, var4, var3);
               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 4, 0, var4, var3);

               for(int var5 = 1; var5 <= 3; ++var5) {
                  this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 0, var5, var4, var3);
                  this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 1, var5, var4, var3);
                  this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 2, var5, var4, var3);
                  this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), 3, var5, var4, var3);
                  this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 4, var5, var4, var3);
               }

               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 0, 4, var4, var3);
               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 1, 4, var4, var3);
               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 2, 4, var4, var3);
               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 3, 4, var4, var3);
               this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), 4, 4, var4, var3);
            }

            return true;
         }
      }
   }

   abstract static class Stronghold extends StructureComponent {
      protected StructureStrongholdPieces.Stronghold.Door field_143013_d;

      public Stronghold() {
         super();
         this.field_143013_d = StructureStrongholdPieces.Stronghold.Door.OPENING;
      }

      protected Stronghold(int var1) {
         super(var1);
         this.field_143013_d = StructureStrongholdPieces.Stronghold.Door.OPENING;
      }

      protected void func_143012_a(NBTTagCompound var1) {
         var1.func_74778_a("EntryDoor", this.field_143013_d.name());
      }

      protected void func_143011_b(NBTTagCompound var1) {
         this.field_143013_d = StructureStrongholdPieces.Stronghold.Door.valueOf(var1.func_74779_i("EntryDoor"));
      }

      protected void func_74990_a(World var1, Random var2, StructureBoundingBox var3, StructureStrongholdPieces.Stronghold.Door var4, int var5, int var6, int var7) {
         switch(var4) {
         case OPENING:
         default:
            this.func_175804_a(var1, var3, var5, var6, var7, var5 + 3 - 1, var6 + 3 - 1, var7, Blocks.field_150350_a.func_176223_P(), Blocks.field_150350_a.func_176223_P(), false);
            break;
         case WOOD_DOOR:
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), var5, var6, var7, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), var5, var6 + 1, var7, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), var5, var6 + 2, var7, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), var5 + 1, var6 + 2, var7, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), var5 + 2, var6 + 2, var7, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), var5 + 2, var6 + 1, var7, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), var5 + 2, var6, var7, var3);
            this.func_175811_a(var1, Blocks.field_180413_ao.func_176223_P(), var5 + 1, var6, var7, var3);
            this.func_175811_a(var1, Blocks.field_180413_ao.func_176203_a(8), var5 + 1, var6 + 1, var7, var3);
            break;
         case GRATES:
            this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), var5 + 1, var6, var7, var3);
            this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), var5 + 1, var6 + 1, var7, var3);
            this.func_175811_a(var1, Blocks.field_150411_aY.func_176223_P(), var5, var6, var7, var3);
            this.func_175811_a(var1, Blocks.field_150411_aY.func_176223_P(), var5, var6 + 1, var7, var3);
            this.func_175811_a(var1, Blocks.field_150411_aY.func_176223_P(), var5, var6 + 2, var7, var3);
            this.func_175811_a(var1, Blocks.field_150411_aY.func_176223_P(), var5 + 1, var6 + 2, var7, var3);
            this.func_175811_a(var1, Blocks.field_150411_aY.func_176223_P(), var5 + 2, var6 + 2, var7, var3);
            this.func_175811_a(var1, Blocks.field_150411_aY.func_176223_P(), var5 + 2, var6 + 1, var7, var3);
            this.func_175811_a(var1, Blocks.field_150411_aY.func_176223_P(), var5 + 2, var6, var7, var3);
            break;
         case IRON_DOOR:
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), var5, var6, var7, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), var5, var6 + 1, var7, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), var5, var6 + 2, var7, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), var5 + 1, var6 + 2, var7, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), var5 + 2, var6 + 2, var7, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), var5 + 2, var6 + 1, var7, var3);
            this.func_175811_a(var1, Blocks.field_150417_aV.func_176223_P(), var5 + 2, var6, var7, var3);
            this.func_175811_a(var1, Blocks.field_150454_av.func_176223_P(), var5 + 1, var6, var7, var3);
            this.func_175811_a(var1, Blocks.field_150454_av.func_176203_a(8), var5 + 1, var6 + 1, var7, var3);
            this.func_175811_a(var1, Blocks.field_150430_aB.func_176203_a(this.func_151555_a(Blocks.field_150430_aB, 4)), var5 + 2, var6 + 1, var7 + 1, var3);
            this.func_175811_a(var1, Blocks.field_150430_aB.func_176203_a(this.func_151555_a(Blocks.field_150430_aB, 3)), var5 + 2, var6 + 1, var7 - 1, var3);
         }

      }

      protected StructureStrongholdPieces.Stronghold.Door func_74988_a(Random var1) {
         int var2 = var1.nextInt(5);
         switch(var2) {
         case 0:
         case 1:
         default:
            return StructureStrongholdPieces.Stronghold.Door.OPENING;
         case 2:
            return StructureStrongholdPieces.Stronghold.Door.WOOD_DOOR;
         case 3:
            return StructureStrongholdPieces.Stronghold.Door.GRATES;
         case 4:
            return StructureStrongholdPieces.Stronghold.Door.IRON_DOOR;
         }
      }

      protected StructureComponent func_74986_a(StructureStrongholdPieces.Stairs2 var1, List<StructureComponent> var2, Random var3, int var4, int var5) {
         if (this.field_74885_f != null) {
            switch(this.field_74885_f) {
            case NORTH:
               return StructureStrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a + var4, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78896_c - 1, this.field_74885_f, this.func_74877_c());
            case SOUTH:
               return StructureStrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a + var4, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78892_f + 1, this.field_74885_f, this.func_74877_c());
            case WEST:
               return StructureStrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78896_c + var4, this.field_74885_f, this.func_74877_c());
            case EAST:
               return StructureStrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78896_c + var4, this.field_74885_f, this.func_74877_c());
            }
         }

         return null;
      }

      protected StructureComponent func_74989_b(StructureStrongholdPieces.Stairs2 var1, List<StructureComponent> var2, Random var3, int var4, int var5) {
         if (this.field_74885_f != null) {
            switch(this.field_74885_f) {
            case NORTH:
               return StructureStrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.WEST, this.func_74877_c());
            case SOUTH:
               return StructureStrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.WEST, this.func_74877_c());
            case WEST:
               return StructureStrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c());
            case EAST:
               return StructureStrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c());
            }
         }

         return null;
      }

      protected StructureComponent func_74987_c(StructureStrongholdPieces.Stairs2 var1, List<StructureComponent> var2, Random var3, int var4, int var5) {
         if (this.field_74885_f != null) {
            switch(this.field_74885_f) {
            case NORTH:
               return StructureStrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.EAST, this.func_74877_c());
            case SOUTH:
               return StructureStrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.EAST, this.func_74877_c());
            case WEST:
               return StructureStrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c());
            case EAST:
               return StructureStrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c());
            }
         }

         return null;
      }

      protected static boolean func_74991_a(StructureBoundingBox var0) {
         return var0 != null && var0.field_78895_b > 10;
      }

      public static enum Door {
         OPENING,
         WOOD_DOOR,
         GRATES,
         IRON_DOOR;

         private Door() {
         }
      }
   }

   static class PieceWeight {
      public Class<? extends StructureStrongholdPieces.Stronghold> field_75194_a;
      public final int field_75192_b;
      public int field_75193_c;
      public int field_75191_d;

      public PieceWeight(Class<? extends StructureStrongholdPieces.Stronghold> var1, int var2, int var3) {
         super();
         this.field_75194_a = var1;
         this.field_75192_b = var2;
         this.field_75191_d = var3;
      }

      public boolean func_75189_a(int var1) {
         return this.field_75191_d == 0 || this.field_75193_c < this.field_75191_d;
      }

      public boolean func_75190_a() {
         return this.field_75191_d == 0 || this.field_75193_c < this.field_75191_d;
      }
   }
}
