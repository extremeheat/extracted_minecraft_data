package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorchWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class StrongholdPieces {
   private static final StrongholdPieces.PieceWeight[] field_75205_b = new StrongholdPieces.PieceWeight[]{new StrongholdPieces.PieceWeight(StrongholdPieces.Straight.class, 40, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.Prison.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.LeftTurn.class, 20, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.RightTurn.class, 20, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.RoomCrossing.class, 10, 6), new StrongholdPieces.PieceWeight(StrongholdPieces.StairsStraight.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.Stairs.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.Crossing.class, 5, 4), new StrongholdPieces.PieceWeight(StrongholdPieces.ChestCorridor.class, 5, 4), new StrongholdPieces.PieceWeight(StrongholdPieces.Library.class, 10, 2) {
      public boolean func_75189_a(int var1) {
         return super.func_75189_a(var1) && var1 > 4;
      }
   }, new StrongholdPieces.PieceWeight(StrongholdPieces.PortalRoom.class, 20, 1) {
      public boolean func_75189_a(int var1) {
         return super.func_75189_a(var1) && var1 > 5;
      }
   }};
   private static List<StrongholdPieces.PieceWeight> field_75206_c;
   private static Class<? extends StrongholdPieces.Stronghold> field_75203_d;
   private static int field_75207_a;
   private static final StrongholdPieces.Stones field_75204_e = new StrongholdPieces.Stones();

   public static void func_143046_a() {
      StructureIO.func_143031_a(StrongholdPieces.ChestCorridor.class, "SHCC");
      StructureIO.func_143031_a(StrongholdPieces.Corridor.class, "SHFC");
      StructureIO.func_143031_a(StrongholdPieces.Crossing.class, "SH5C");
      StructureIO.func_143031_a(StrongholdPieces.LeftTurn.class, "SHLT");
      StructureIO.func_143031_a(StrongholdPieces.Library.class, "SHLi");
      StructureIO.func_143031_a(StrongholdPieces.PortalRoom.class, "SHPR");
      StructureIO.func_143031_a(StrongholdPieces.Prison.class, "SHPH");
      StructureIO.func_143031_a(StrongholdPieces.RightTurn.class, "SHRT");
      StructureIO.func_143031_a(StrongholdPieces.RoomCrossing.class, "SHRC");
      StructureIO.func_143031_a(StrongholdPieces.Stairs.class, "SHSD");
      StructureIO.func_143031_a(StrongholdPieces.Stairs2.class, "SHStart");
      StructureIO.func_143031_a(StrongholdPieces.Straight.class, "SHS");
      StructureIO.func_143031_a(StrongholdPieces.StairsStraight.class, "SHSSD");
   }

   public static void func_75198_a() {
      field_75206_c = Lists.newArrayList();
      StrongholdPieces.PieceWeight[] var0 = field_75205_b;
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         StrongholdPieces.PieceWeight var3 = var0[var2];
         var3.field_75193_c = 0;
         field_75206_c.add(var3);
      }

      field_75203_d = null;
   }

   private static boolean func_75202_c() {
      boolean var0 = false;
      field_75207_a = 0;

      StrongholdPieces.PieceWeight var2;
      for(Iterator var1 = field_75206_c.iterator(); var1.hasNext(); field_75207_a += var2.field_75192_b) {
         var2 = (StrongholdPieces.PieceWeight)var1.next();
         if (var2.field_75191_d > 0 && var2.field_75193_c < var2.field_75191_d) {
            var0 = true;
         }
      }

      return var0;
   }

   private static StrongholdPieces.Stronghold func_175954_a(Class<? extends StrongholdPieces.Stronghold> var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, @Nullable EnumFacing var6, int var7) {
      Object var8 = null;
      if (var0 == StrongholdPieces.Straight.class) {
         var8 = StrongholdPieces.Straight.func_175862_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.Prison.class) {
         var8 = StrongholdPieces.Prison.func_175860_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.LeftTurn.class) {
         var8 = StrongholdPieces.LeftTurn.func_175867_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.RightTurn.class) {
         var8 = StrongholdPieces.RightTurn.func_175867_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.RoomCrossing.class) {
         var8 = StrongholdPieces.RoomCrossing.func_175859_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.StairsStraight.class) {
         var8 = StrongholdPieces.StairsStraight.func_175861_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.Stairs.class) {
         var8 = StrongholdPieces.Stairs.func_175863_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.Crossing.class) {
         var8 = StrongholdPieces.Crossing.func_175866_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.ChestCorridor.class) {
         var8 = StrongholdPieces.ChestCorridor.func_175868_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.Library.class) {
         var8 = StrongholdPieces.Library.func_175864_a(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.PortalRoom.class) {
         var8 = StrongholdPieces.PortalRoom.func_175865_a(var1, var2, var3, var4, var5, var6, var7);
      }

      return (StrongholdPieces.Stronghold)var8;
   }

   private static StrongholdPieces.Stronghold func_175955_b(StrongholdPieces.Stairs2 var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, EnumFacing var6, int var7) {
      if (!func_75202_c()) {
         return null;
      } else {
         if (field_75203_d != null) {
            StrongholdPieces.Stronghold var8 = func_175954_a(field_75203_d, var1, var2, var3, var4, var5, var6, var7);
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
               StrongholdPieces.PieceWeight var11 = (StrongholdPieces.PieceWeight)var10.next();
               var9 -= var11.field_75192_b;
               if (var9 < 0) {
                  if (!var11.func_75189_a(var7) || var11 == var0.field_75027_a) {
                     break;
                  }

                  StrongholdPieces.Stronghold var12 = func_175954_a(var11.field_75194_a, var1, var2, var3, var4, var5, var6, var7);
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

         MutableBoundingBox var14 = StrongholdPieces.Corridor.func_175869_a(var1, var2, var3, var4, var5, var6);
         if (var14 != null && var14.field_78895_b > 1) {
            return new StrongholdPieces.Corridor(var7, var2, var14, var6);
         } else {
            return null;
         }
      }
   }

   private static StructurePiece func_175953_c(StrongholdPieces.Stairs2 var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, @Nullable EnumFacing var6, int var7) {
      if (var7 > 50) {
         return null;
      } else if (Math.abs(var3 - var0.func_74874_b().field_78897_a) <= 112 && Math.abs(var5 - var0.func_74874_b().field_78896_c) <= 112) {
         StrongholdPieces.Stronghold var8 = func_175955_b(var0, var1, var2, var3, var4, var5, var6, var7 + 1);
         if (var8 != null) {
            var1.add(var8);
            var0.field_75026_c.add(var8);
         }

         return var8;
      } else {
         return null;
      }
   }

   static class Stones extends StructurePiece.BlockSelector {
      private Stones() {
         super();
      }

      public void func_75062_a(Random var1, int var2, int var3, int var4, boolean var5) {
         if (var5) {
            float var6 = var1.nextFloat();
            if (var6 < 0.2F) {
               this.field_151562_a = Blocks.field_196700_dk.func_176223_P();
            } else if (var6 < 0.5F) {
               this.field_151562_a = Blocks.field_196698_dj.func_176223_P();
            } else if (var6 < 0.55F) {
               this.field_151562_a = Blocks.field_196688_de.func_176223_P();
            } else {
               this.field_151562_a = Blocks.field_196696_di.func_176223_P();
            }
         } else {
            this.field_151562_a = Blocks.field_201941_jj.func_176223_P();
         }

      }

      // $FF: synthetic method
      Stones(Object var1) {
         this();
      }
   }

   public static class PortalRoom extends StrongholdPieces.Stronghold {
      private boolean field_75005_a;

      public PortalRoom() {
         super();
      }

      public PortalRoom(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Mob", this.field_75005_a);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_75005_a = var1.func_74767_n("Mob");
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         if (var1 != null) {
            ((StrongholdPieces.Stairs2)var1).field_75025_b = this;
         }

      }

      public static StrongholdPieces.PortalRoom func_175865_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -4, -1, 0, 11, 8, 16, var5);
         return func_74991_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new StrongholdPieces.PortalRoom(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_74882_a(var1, var3, 0, 0, 0, 10, 7, 15, false, var2, StrongholdPieces.field_75204_e);
         this.func_74990_a(var1, var2, var3, StrongholdPieces.Stronghold.Door.GRATES, 4, 1, 0);
         byte var5 = 6;
         this.func_74882_a(var1, var3, 1, var5, 1, 1, var5, 14, false, var2, StrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 9, var5, 1, 9, var5, 14, false, var2, StrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 2, var5, 1, 8, var5, 2, false, var2, StrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 2, var5, 14, 8, var5, 14, false, var2, StrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 1, 1, 1, 2, 1, 4, false, var2, StrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 8, 1, 1, 9, 1, 4, false, var2, StrongholdPieces.field_75204_e);
         this.func_175804_a(var1, var3, 1, 1, 1, 1, 1, 3, Blocks.field_150353_l.func_176223_P(), Blocks.field_150353_l.func_176223_P(), false);
         this.func_175804_a(var1, var3, 9, 1, 1, 9, 1, 3, Blocks.field_150353_l.func_176223_P(), Blocks.field_150353_l.func_176223_P(), false);
         this.func_74882_a(var1, var3, 3, 1, 8, 7, 1, 12, false, var2, StrongholdPieces.field_75204_e);
         this.func_175804_a(var1, var3, 4, 1, 9, 6, 1, 11, Blocks.field_150353_l.func_176223_P(), Blocks.field_150353_l.func_176223_P(), false);
         IBlockState var6 = (IBlockState)((IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196409_a, true)).func_206870_a(BlockPane.field_196413_c, true);
         IBlockState var7 = (IBlockState)((IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196414_y, true)).func_206870_a(BlockPane.field_196411_b, true);

         int var8;
         for(var8 = 3; var8 < 14; var8 += 2) {
            this.func_175804_a(var1, var3, 0, 3, var8, 0, 4, var8, var6, var6, false);
            this.func_175804_a(var1, var3, 10, 3, var8, 10, 4, var8, var6, var6, false);
         }

         for(var8 = 2; var8 < 9; var8 += 2) {
            this.func_175804_a(var1, var3, var8, 3, 15, var8, 4, 15, var7, var7, false);
         }

         IBlockState var18 = (IBlockState)Blocks.field_150390_bg.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.NORTH);
         this.func_74882_a(var1, var3, 4, 1, 5, 6, 1, 7, false, var2, StrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 4, 2, 6, 6, 2, 7, false, var2, StrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 4, 3, 7, 6, 3, 7, false, var2, StrongholdPieces.field_75204_e);

         for(int var9 = 4; var9 <= 6; ++var9) {
            this.func_175811_a(var1, var18, var9, 1, 4, var3);
            this.func_175811_a(var1, var18, var9, 2, 5, var3);
            this.func_175811_a(var1, var18, var9, 3, 6, var3);
         }

         IBlockState var19 = (IBlockState)Blocks.field_150378_br.func_176223_P().func_206870_a(BlockEndPortalFrame.field_176508_a, EnumFacing.NORTH);
         IBlockState var10 = (IBlockState)Blocks.field_150378_br.func_176223_P().func_206870_a(BlockEndPortalFrame.field_176508_a, EnumFacing.SOUTH);
         IBlockState var11 = (IBlockState)Blocks.field_150378_br.func_176223_P().func_206870_a(BlockEndPortalFrame.field_176508_a, EnumFacing.EAST);
         IBlockState var12 = (IBlockState)Blocks.field_150378_br.func_176223_P().func_206870_a(BlockEndPortalFrame.field_176508_a, EnumFacing.WEST);
         boolean var13 = true;
         boolean[] var14 = new boolean[12];

         for(int var15 = 0; var15 < var14.length; ++var15) {
            var14[var15] = var2.nextFloat() > 0.9F;
            var13 &= var14[var15];
         }

         this.func_175811_a(var1, (IBlockState)var19.func_206870_a(BlockEndPortalFrame.field_176507_b, var14[0]), 4, 3, 8, var3);
         this.func_175811_a(var1, (IBlockState)var19.func_206870_a(BlockEndPortalFrame.field_176507_b, var14[1]), 5, 3, 8, var3);
         this.func_175811_a(var1, (IBlockState)var19.func_206870_a(BlockEndPortalFrame.field_176507_b, var14[2]), 6, 3, 8, var3);
         this.func_175811_a(var1, (IBlockState)var10.func_206870_a(BlockEndPortalFrame.field_176507_b, var14[3]), 4, 3, 12, var3);
         this.func_175811_a(var1, (IBlockState)var10.func_206870_a(BlockEndPortalFrame.field_176507_b, var14[4]), 5, 3, 12, var3);
         this.func_175811_a(var1, (IBlockState)var10.func_206870_a(BlockEndPortalFrame.field_176507_b, var14[5]), 6, 3, 12, var3);
         this.func_175811_a(var1, (IBlockState)var11.func_206870_a(BlockEndPortalFrame.field_176507_b, var14[6]), 3, 3, 9, var3);
         this.func_175811_a(var1, (IBlockState)var11.func_206870_a(BlockEndPortalFrame.field_176507_b, var14[7]), 3, 3, 10, var3);
         this.func_175811_a(var1, (IBlockState)var11.func_206870_a(BlockEndPortalFrame.field_176507_b, var14[8]), 3, 3, 11, var3);
         this.func_175811_a(var1, (IBlockState)var12.func_206870_a(BlockEndPortalFrame.field_176507_b, var14[9]), 7, 3, 9, var3);
         this.func_175811_a(var1, (IBlockState)var12.func_206870_a(BlockEndPortalFrame.field_176507_b, var14[10]), 7, 3, 10, var3);
         this.func_175811_a(var1, (IBlockState)var12.func_206870_a(BlockEndPortalFrame.field_176507_b, var14[11]), 7, 3, 11, var3);
         if (var13) {
            IBlockState var20 = Blocks.field_150384_bq.func_176223_P();
            this.func_175811_a(var1, var20, 4, 3, 9, var3);
            this.func_175811_a(var1, var20, 5, 3, 9, var3);
            this.func_175811_a(var1, var20, 6, 3, 9, var3);
            this.func_175811_a(var1, var20, 4, 3, 10, var3);
            this.func_175811_a(var1, var20, 5, 3, 10, var3);
            this.func_175811_a(var1, var20, 6, 3, 10, var3);
            this.func_175811_a(var1, var20, 4, 3, 11, var3);
            this.func_175811_a(var1, var20, 5, 3, 11, var3);
            this.func_175811_a(var1, var20, 6, 3, 11, var3);
         }

         if (!this.field_75005_a) {
            int var17 = this.func_74862_a(3);
            BlockPos var21 = new BlockPos(this.func_74865_a(5, 6), var17, this.func_74873_b(5, 6));
            if (var3.func_175898_b(var21)) {
               this.field_75005_a = true;
               var1.func_180501_a(var21, Blocks.field_150474_ac.func_176223_P(), 2);
               TileEntity var16 = var1.func_175625_s(var21);
               if (var16 instanceof TileEntityMobSpawner) {
                  ((TileEntityMobSpawner)var16).func_145881_a().func_200876_a(EntityType.field_200740_af);
               }
            }
         }

         return true;
      }
   }

   public static class Crossing extends StrongholdPieces.Stronghold {
      private boolean field_74996_b;
      private boolean field_74997_c;
      private boolean field_74995_d;
      private boolean field_74999_h;

      public Crossing() {
         super();
      }

      public Crossing(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
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

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_74996_b = var1.func_74767_n("leftLow");
         this.field_74997_c = var1.func_74767_n("leftHigh");
         this.field_74995_d = var1.func_74767_n("rightLow");
         this.field_74999_h = var1.func_74767_n("rightHigh");
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         int var4 = 3;
         int var5 = 5;
         EnumFacing var6 = this.func_186165_e();
         if (var6 == EnumFacing.WEST || var6 == EnumFacing.NORTH) {
            var4 = 8 - var4;
            var5 = 8 - var5;
         }

         this.func_74986_a((StrongholdPieces.Stairs2)var1, var2, var3, 5, 1);
         if (this.field_74996_b) {
            this.func_74989_b((StrongholdPieces.Stairs2)var1, var2, var3, var4, 1);
         }

         if (this.field_74997_c) {
            this.func_74989_b((StrongholdPieces.Stairs2)var1, var2, var3, var5, 7);
         }

         if (this.field_74995_d) {
            this.func_74987_c((StrongholdPieces.Stairs2)var1, var2, var3, var4, 1);
         }

         if (this.field_74999_h) {
            this.func_74987_c((StrongholdPieces.Stairs2)var1, var2, var3, var5, 7);
         }

      }

      public static StrongholdPieces.Crossing func_175866_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -4, -3, 0, 10, 9, 11, var5);
         return func_74991_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new StrongholdPieces.Crossing(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_74882_a(var1, var3, 0, 0, 0, 9, 8, 10, true, var2, StrongholdPieces.field_75204_e);
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 4, 3, 0);
         if (this.field_74996_b) {
            this.func_175804_a(var1, var3, 0, 3, 1, 0, 5, 3, field_202556_l, field_202556_l, false);
         }

         if (this.field_74995_d) {
            this.func_175804_a(var1, var3, 9, 3, 1, 9, 5, 3, field_202556_l, field_202556_l, false);
         }

         if (this.field_74997_c) {
            this.func_175804_a(var1, var3, 0, 5, 7, 0, 7, 9, field_202556_l, field_202556_l, false);
         }

         if (this.field_74999_h) {
            this.func_175804_a(var1, var3, 9, 5, 7, 9, 7, 9, field_202556_l, field_202556_l, false);
         }

         this.func_175804_a(var1, var3, 5, 1, 10, 7, 3, 10, field_202556_l, field_202556_l, false);
         this.func_74882_a(var1, var3, 1, 2, 1, 8, 2, 6, false, var2, StrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 4, 1, 5, 4, 4, 9, false, var2, StrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 8, 1, 5, 8, 4, 9, false, var2, StrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 1, 4, 7, 3, 4, 9, false, var2, StrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 1, 3, 5, 3, 3, 6, false, var2, StrongholdPieces.field_75204_e);
         this.func_175804_a(var1, var3, 1, 3, 4, 3, 3, 4, Blocks.field_150333_U.func_176223_P(), Blocks.field_150333_U.func_176223_P(), false);
         this.func_175804_a(var1, var3, 1, 4, 6, 3, 4, 6, Blocks.field_150333_U.func_176223_P(), Blocks.field_150333_U.func_176223_P(), false);
         this.func_74882_a(var1, var3, 5, 1, 7, 7, 1, 8, false, var2, StrongholdPieces.field_75204_e);
         this.func_175804_a(var1, var3, 5, 1, 9, 7, 1, 9, Blocks.field_150333_U.func_176223_P(), Blocks.field_150333_U.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 2, 7, 7, 2, 7, Blocks.field_150333_U.func_176223_P(), Blocks.field_150333_U.func_176223_P(), false);
         this.func_175804_a(var1, var3, 4, 5, 7, 4, 5, 9, Blocks.field_150333_U.func_176223_P(), Blocks.field_150333_U.func_176223_P(), false);
         this.func_175804_a(var1, var3, 8, 5, 7, 8, 5, 9, Blocks.field_150333_U.func_176223_P(), Blocks.field_150333_U.func_176223_P(), false);
         this.func_175804_a(var1, var3, 5, 5, 7, 7, 5, 9, (IBlockState)Blocks.field_150333_U.func_176223_P().func_206870_a(BlockSlab.field_196505_a, SlabType.DOUBLE), (IBlockState)Blocks.field_150333_U.func_176223_P().func_206870_a(BlockSlab.field_196505_a, SlabType.DOUBLE), false);
         this.func_175811_a(var1, (IBlockState)Blocks.field_196591_bQ.func_176223_P().func_206870_a(BlockTorchWall.field_196532_a, EnumFacing.SOUTH), 6, 5, 6, var3);
         return true;
      }
   }

   public static class Library extends StrongholdPieces.Stronghold {
      private boolean field_75008_c;

      public Library() {
         super();
      }

      public Library(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
         this.field_75008_c = var3.func_78882_c() > 6;
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Tall", this.field_75008_c);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_75008_c = var1.func_74767_n("Tall");
      }

      public static StrongholdPieces.Library func_175864_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -4, -1, 0, 14, 11, 15, var5);
         if (!func_74991_a(var7) || StructurePiece.func_74883_a(var0, var7) != null) {
            var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -4, -1, 0, 14, 6, 15, var5);
            if (!func_74991_a(var7) || StructurePiece.func_74883_a(var0, var7) != null) {
               return null;
            }
         }

         return new StrongholdPieces.Library(var6, var1, var7, var5);
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         byte var5 = 11;
         if (!this.field_75008_c) {
            var5 = 6;
         }

         this.func_74882_a(var1, var3, 0, 0, 0, 13, var5 - 1, 14, true, var2, StrongholdPieces.field_75204_e);
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 4, 1, 0);
         this.func_189914_a(var1, var3, var2, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.field_196553_aF.func_176223_P(), Blocks.field_196553_aF.func_176223_P(), false, false);
         boolean var6 = true;
         boolean var7 = true;

         int var8;
         for(var8 = 1; var8 <= 13; ++var8) {
            if ((var8 - 1) % 4 == 0) {
               this.func_175804_a(var1, var3, 1, 1, var8, 1, 4, var8, Blocks.field_196662_n.func_176223_P(), Blocks.field_196662_n.func_176223_P(), false);
               this.func_175804_a(var1, var3, 12, 1, var8, 12, 4, var8, Blocks.field_196662_n.func_176223_P(), Blocks.field_196662_n.func_176223_P(), false);
               this.func_175811_a(var1, (IBlockState)Blocks.field_196591_bQ.func_176223_P().func_206870_a(BlockTorchWall.field_196532_a, EnumFacing.EAST), 2, 3, var8, var3);
               this.func_175811_a(var1, (IBlockState)Blocks.field_196591_bQ.func_176223_P().func_206870_a(BlockTorchWall.field_196532_a, EnumFacing.WEST), 11, 3, var8, var3);
               if (this.field_75008_c) {
                  this.func_175804_a(var1, var3, 1, 6, var8, 1, 9, var8, Blocks.field_196662_n.func_176223_P(), Blocks.field_196662_n.func_176223_P(), false);
                  this.func_175804_a(var1, var3, 12, 6, var8, 12, 9, var8, Blocks.field_196662_n.func_176223_P(), Blocks.field_196662_n.func_176223_P(), false);
               }
            } else {
               this.func_175804_a(var1, var3, 1, 1, var8, 1, 4, var8, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
               this.func_175804_a(var1, var3, 12, 1, var8, 12, 4, var8, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
               if (this.field_75008_c) {
                  this.func_175804_a(var1, var3, 1, 6, var8, 1, 9, var8, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
                  this.func_175804_a(var1, var3, 12, 6, var8, 12, 9, var8, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
               }
            }
         }

         for(var8 = 3; var8 < 12; var8 += 2) {
            this.func_175804_a(var1, var3, 3, 1, var8, 4, 3, var8, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
            this.func_175804_a(var1, var3, 6, 1, var8, 7, 3, var8, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
            this.func_175804_a(var1, var3, 9, 1, var8, 10, 3, var8, Blocks.field_150342_X.func_176223_P(), Blocks.field_150342_X.func_176223_P(), false);
         }

         if (this.field_75008_c) {
            this.func_175804_a(var1, var3, 1, 5, 1, 3, 5, 13, Blocks.field_196662_n.func_176223_P(), Blocks.field_196662_n.func_176223_P(), false);
            this.func_175804_a(var1, var3, 10, 5, 1, 12, 5, 13, Blocks.field_196662_n.func_176223_P(), Blocks.field_196662_n.func_176223_P(), false);
            this.func_175804_a(var1, var3, 4, 5, 1, 9, 5, 2, Blocks.field_196662_n.func_176223_P(), Blocks.field_196662_n.func_176223_P(), false);
            this.func_175804_a(var1, var3, 4, 5, 12, 9, 5, 13, Blocks.field_196662_n.func_176223_P(), Blocks.field_196662_n.func_176223_P(), false);
            this.func_175811_a(var1, Blocks.field_196662_n.func_176223_P(), 9, 5, 11, var3);
            this.func_175811_a(var1, Blocks.field_196662_n.func_176223_P(), 8, 5, 11, var3);
            this.func_175811_a(var1, Blocks.field_196662_n.func_176223_P(), 9, 5, 10, var3);
            IBlockState var17 = (IBlockState)((IBlockState)Blocks.field_180407_aO.func_176223_P().func_206870_a(BlockFence.field_196414_y, true)).func_206870_a(BlockFence.field_196411_b, true);
            IBlockState var9 = (IBlockState)((IBlockState)Blocks.field_180407_aO.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196413_c, true);
            this.func_175804_a(var1, var3, 3, 6, 3, 3, 6, 11, var9, var9, false);
            this.func_175804_a(var1, var3, 10, 6, 3, 10, 6, 9, var9, var9, false);
            this.func_175804_a(var1, var3, 4, 6, 2, 9, 6, 2, var17, var17, false);
            this.func_175804_a(var1, var3, 4, 6, 12, 7, 6, 12, var17, var17, false);
            this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_180407_aO.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196411_b, true), 3, 6, 2, var3);
            this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_180407_aO.func_176223_P().func_206870_a(BlockFence.field_196413_c, true)).func_206870_a(BlockFence.field_196411_b, true), 3, 6, 12, var3);
            this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_180407_aO.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196414_y, true), 10, 6, 2, var3);

            for(int var10 = 0; var10 <= 2; ++var10) {
               this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_180407_aO.func_176223_P().func_206870_a(BlockFence.field_196413_c, true)).func_206870_a(BlockFence.field_196414_y, true), 8 + var10, 6, 12 - var10, var3);
               if (var10 != 2) {
                  this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_180407_aO.func_176223_P().func_206870_a(BlockFence.field_196409_a, true)).func_206870_a(BlockFence.field_196411_b, true), 8 + var10, 6, 11 - var10, var3);
               }
            }

            IBlockState var18 = (IBlockState)Blocks.field_150468_ap.func_176223_P().func_206870_a(BlockLadder.field_176382_a, EnumFacing.SOUTH);
            this.func_175811_a(var1, var18, 10, 1, 13, var3);
            this.func_175811_a(var1, var18, 10, 2, 13, var3);
            this.func_175811_a(var1, var18, 10, 3, 13, var3);
            this.func_175811_a(var1, var18, 10, 4, 13, var3);
            this.func_175811_a(var1, var18, 10, 5, 13, var3);
            this.func_175811_a(var1, var18, 10, 6, 13, var3);
            this.func_175811_a(var1, var18, 10, 7, 13, var3);
            boolean var11 = true;
            boolean var12 = true;
            IBlockState var13 = (IBlockState)Blocks.field_180407_aO.func_176223_P().func_206870_a(BlockFence.field_196411_b, true);
            this.func_175811_a(var1, var13, 6, 9, 7, var3);
            IBlockState var14 = (IBlockState)Blocks.field_180407_aO.func_176223_P().func_206870_a(BlockFence.field_196414_y, true);
            this.func_175811_a(var1, var14, 7, 9, 7, var3);
            this.func_175811_a(var1, var13, 6, 8, 7, var3);
            this.func_175811_a(var1, var14, 7, 8, 7, var3);
            IBlockState var15 = (IBlockState)((IBlockState)var9.func_206870_a(BlockFence.field_196414_y, true)).func_206870_a(BlockFence.field_196411_b, true);
            this.func_175811_a(var1, var15, 6, 7, 7, var3);
            this.func_175811_a(var1, var15, 7, 7, 7, var3);
            this.func_175811_a(var1, var13, 5, 7, 7, var3);
            this.func_175811_a(var1, var14, 8, 7, 7, var3);
            this.func_175811_a(var1, (IBlockState)var13.func_206870_a(BlockFence.field_196409_a, true), 6, 7, 6, var3);
            this.func_175811_a(var1, (IBlockState)var13.func_206870_a(BlockFence.field_196413_c, true), 6, 7, 8, var3);
            this.func_175811_a(var1, (IBlockState)var14.func_206870_a(BlockFence.field_196409_a, true), 7, 7, 6, var3);
            this.func_175811_a(var1, (IBlockState)var14.func_206870_a(BlockFence.field_196413_c, true), 7, 7, 8, var3);
            IBlockState var16 = Blocks.field_150478_aa.func_176223_P();
            this.func_175811_a(var1, var16, 5, 8, 7, var3);
            this.func_175811_a(var1, var16, 8, 8, 7, var3);
            this.func_175811_a(var1, var16, 6, 8, 6, var3);
            this.func_175811_a(var1, var16, 6, 8, 8, var3);
            this.func_175811_a(var1, var16, 7, 8, 6, var3);
            this.func_175811_a(var1, var16, 7, 8, 8, var3);
         }

         this.func_186167_a(var1, var3, var2, 3, 3, 5, LootTableList.field_186426_h);
         if (this.field_75008_c) {
            this.func_175811_a(var1, field_202556_l, 12, 9, 1, var3);
            this.func_186167_a(var1, var3, var2, 12, 8, 1, LootTableList.field_186426_h);
         }

         return true;
      }
   }

   public static class Prison extends StrongholdPieces.Stronghold {
      public Prison() {
         super();
      }

      public Prison(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74986_a((StrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
      }

      public static StrongholdPieces.Prison func_175860_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -1, -1, 0, 9, 5, 11, var5);
         return func_74991_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new StrongholdPieces.Prison(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_74882_a(var1, var3, 0, 0, 0, 8, 4, 10, true, var2, StrongholdPieces.field_75204_e);
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 1, 0);
         this.func_175804_a(var1, var3, 1, 1, 10, 3, 3, 10, field_202556_l, field_202556_l, false);
         this.func_74882_a(var1, var3, 4, 1, 1, 4, 3, 1, false, var2, StrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 4, 1, 3, 4, 3, 3, false, var2, StrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 4, 1, 7, 4, 3, 7, false, var2, StrongholdPieces.field_75204_e);
         this.func_74882_a(var1, var3, 4, 1, 9, 4, 3, 9, false, var2, StrongholdPieces.field_75204_e);

         for(int var5 = 1; var5 <= 3; ++var5) {
            this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196409_a, true)).func_206870_a(BlockPane.field_196413_c, true), 4, var5, 4, var3);
            this.func_175811_a(var1, (IBlockState)((IBlockState)((IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196409_a, true)).func_206870_a(BlockPane.field_196413_c, true)).func_206870_a(BlockPane.field_196411_b, true), 4, var5, 5, var3);
            this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196409_a, true)).func_206870_a(BlockPane.field_196413_c, true), 4, var5, 6, var3);
            this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196414_y, true)).func_206870_a(BlockPane.field_196411_b, true), 5, var5, 5, var3);
            this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196414_y, true)).func_206870_a(BlockPane.field_196411_b, true), 6, var5, 5, var3);
            this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196414_y, true)).func_206870_a(BlockPane.field_196411_b, true), 7, var5, 5, var3);
         }

         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196409_a, true)).func_206870_a(BlockPane.field_196413_c, true), 4, 3, 2, var3);
         this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196409_a, true)).func_206870_a(BlockPane.field_196413_c, true), 4, 3, 8, var3);
         IBlockState var7 = (IBlockState)Blocks.field_150454_av.func_176223_P().func_206870_a(BlockDoor.field_176520_a, EnumFacing.WEST);
         IBlockState var6 = (IBlockState)((IBlockState)Blocks.field_150454_av.func_176223_P().func_206870_a(BlockDoor.field_176520_a, EnumFacing.WEST)).func_206870_a(BlockDoor.field_176523_O, DoubleBlockHalf.UPPER);
         this.func_175811_a(var1, var7, 4, 1, 2, var3);
         this.func_175811_a(var1, var6, 4, 2, 2, var3);
         this.func_175811_a(var1, var7, 4, 1, 8, var3);
         this.func_175811_a(var1, var6, 4, 2, 8, var3);
         return true;
      }
   }

   public static class RoomCrossing extends StrongholdPieces.Stronghold {
      protected int field_75013_b;

      public RoomCrossing() {
         super();
      }

      public RoomCrossing(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
         this.field_75013_b = var2.nextInt(5);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74768_a("Type", this.field_75013_b);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_75013_b = var1.func_74762_e("Type");
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74986_a((StrongholdPieces.Stairs2)var1, var2, var3, 4, 1);
         this.func_74989_b((StrongholdPieces.Stairs2)var1, var2, var3, 1, 4);
         this.func_74987_c((StrongholdPieces.Stairs2)var1, var2, var3, 1, 4);
      }

      public static StrongholdPieces.RoomCrossing func_175859_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -4, -1, 0, 11, 7, 11, var5);
         return func_74991_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new StrongholdPieces.RoomCrossing(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_74882_a(var1, var3, 0, 0, 0, 10, 6, 10, true, var2, StrongholdPieces.field_75204_e);
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 4, 1, 0);
         this.func_175804_a(var1, var3, 4, 1, 10, 6, 3, 10, field_202556_l, field_202556_l, false);
         this.func_175804_a(var1, var3, 0, 1, 4, 0, 3, 6, field_202556_l, field_202556_l, false);
         this.func_175804_a(var1, var3, 10, 1, 4, 10, 3, 6, field_202556_l, field_202556_l, false);
         int var5;
         switch(this.field_75013_b) {
         case 0:
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 5, 1, 5, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 5, 2, 5, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 5, 3, 5, var3);
            this.func_175811_a(var1, (IBlockState)Blocks.field_196591_bQ.func_176223_P().func_206870_a(BlockTorchWall.field_196532_a, EnumFacing.WEST), 4, 3, 5, var3);
            this.func_175811_a(var1, (IBlockState)Blocks.field_196591_bQ.func_176223_P().func_206870_a(BlockTorchWall.field_196532_a, EnumFacing.EAST), 6, 3, 5, var3);
            this.func_175811_a(var1, (IBlockState)Blocks.field_196591_bQ.func_176223_P().func_206870_a(BlockTorchWall.field_196532_a, EnumFacing.SOUTH), 5, 3, 4, var3);
            this.func_175811_a(var1, (IBlockState)Blocks.field_196591_bQ.func_176223_P().func_206870_a(BlockTorchWall.field_196532_a, EnumFacing.NORTH), 5, 3, 6, var3);
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
            for(var5 = 0; var5 < 5; ++var5) {
               this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 3, 1, 3 + var5, var3);
               this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 7, 1, 3 + var5, var3);
               this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 3 + var5, 1, 3, var3);
               this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 3 + var5, 1, 7, var3);
            }

            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 5, 1, 5, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 5, 2, 5, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 5, 3, 5, var3);
            this.func_175811_a(var1, Blocks.field_150355_j.func_176223_P(), 5, 4, 5, var3);
            break;
         case 2:
            for(var5 = 1; var5 <= 9; ++var5) {
               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 1, 3, var5, var3);
               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 9, 3, var5, var3);
            }

            for(var5 = 1; var5 <= 9; ++var5) {
               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), var5, 3, 1, var3);
               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), var5, 3, 9, var3);
            }

            this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 5, 1, 4, var3);
            this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 5, 1, 6, var3);
            this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 5, 3, 4, var3);
            this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 5, 3, 6, var3);
            this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 4, 1, 5, var3);
            this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 6, 1, 5, var3);
            this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 4, 3, 5, var3);
            this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 6, 3, 5, var3);

            for(var5 = 1; var5 <= 3; ++var5) {
               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 4, var5, 4, var3);
               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 6, var5, 4, var3);
               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 4, var5, 6, var3);
               this.func_175811_a(var1, Blocks.field_150347_e.func_176223_P(), 6, var5, 6, var3);
            }

            this.func_175811_a(var1, Blocks.field_150478_aa.func_176223_P(), 5, 3, 5, var3);

            for(var5 = 2; var5 <= 8; ++var5) {
               this.func_175811_a(var1, Blocks.field_196662_n.func_176223_P(), 2, 3, var5, var3);
               this.func_175811_a(var1, Blocks.field_196662_n.func_176223_P(), 3, 3, var5, var3);
               if (var5 <= 3 || var5 >= 7) {
                  this.func_175811_a(var1, Blocks.field_196662_n.func_176223_P(), 4, 3, var5, var3);
                  this.func_175811_a(var1, Blocks.field_196662_n.func_176223_P(), 5, 3, var5, var3);
                  this.func_175811_a(var1, Blocks.field_196662_n.func_176223_P(), 6, 3, var5, var3);
               }

               this.func_175811_a(var1, Blocks.field_196662_n.func_176223_P(), 7, 3, var5, var3);
               this.func_175811_a(var1, Blocks.field_196662_n.func_176223_P(), 8, 3, var5, var3);
            }

            IBlockState var6 = (IBlockState)Blocks.field_150468_ap.func_176223_P().func_206870_a(BlockLadder.field_176382_a, EnumFacing.WEST);
            this.func_175811_a(var1, var6, 9, 1, 3, var3);
            this.func_175811_a(var1, var6, 9, 2, 3, var3);
            this.func_175811_a(var1, var6, 9, 3, 3, var3);
            this.func_186167_a(var1, var3, var2, 3, 4, 8, LootTableList.field_186427_i);
         }

         return true;
      }
   }

   public static class RightTurn extends StrongholdPieces.LeftTurn {
      public RightTurn() {
         super();
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         EnumFacing var4 = this.func_186165_e();
         if (var4 != EnumFacing.NORTH && var4 != EnumFacing.EAST) {
            this.func_74989_b((StrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
         } else {
            this.func_74987_c((StrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
         }

      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_74882_a(var1, var3, 0, 0, 0, 4, 4, 4, true, var2, StrongholdPieces.field_75204_e);
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 1, 0);
         EnumFacing var5 = this.func_186165_e();
         if (var5 != EnumFacing.NORTH && var5 != EnumFacing.EAST) {
            this.func_175804_a(var1, var3, 0, 1, 1, 0, 3, 3, field_202556_l, field_202556_l, false);
         } else {
            this.func_175804_a(var1, var3, 4, 1, 1, 4, 3, 3, field_202556_l, field_202556_l, false);
         }

         return true;
      }
   }

   public static class LeftTurn extends StrongholdPieces.Stronghold {
      public LeftTurn() {
         super();
      }

      public LeftTurn(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         EnumFacing var4 = this.func_186165_e();
         if (var4 != EnumFacing.NORTH && var4 != EnumFacing.EAST) {
            this.func_74987_c((StrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
         } else {
            this.func_74989_b((StrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
         }

      }

      public static StrongholdPieces.LeftTurn func_175867_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -1, -1, 0, 5, 5, 5, var5);
         return func_74991_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new StrongholdPieces.LeftTurn(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_74882_a(var1, var3, 0, 0, 0, 4, 4, 4, true, var2, StrongholdPieces.field_75204_e);
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 1, 0);
         EnumFacing var5 = this.func_186165_e();
         if (var5 != EnumFacing.NORTH && var5 != EnumFacing.EAST) {
            this.func_175804_a(var1, var3, 4, 1, 1, 4, 3, 3, field_202556_l, field_202556_l, false);
         } else {
            this.func_175804_a(var1, var3, 0, 1, 1, 0, 3, 3, field_202556_l, field_202556_l, false);
         }

         return true;
      }
   }

   public static class StairsStraight extends StrongholdPieces.Stronghold {
      public StairsStraight() {
         super();
      }

      public StairsStraight(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74986_a((StrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
      }

      public static StrongholdPieces.StairsStraight func_175861_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -1, -7, 0, 5, 11, 8, var5);
         return func_74991_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new StrongholdPieces.StairsStraight(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_74882_a(var1, var3, 0, 0, 0, 4, 10, 7, true, var2, StrongholdPieces.field_75204_e);
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 7, 0);
         this.func_74990_a(var1, var2, var3, StrongholdPieces.Stronghold.Door.OPENING, 1, 1, 7);
         IBlockState var5 = (IBlockState)Blocks.field_196659_cl.func_176223_P().func_206870_a(BlockStairs.field_176309_a, EnumFacing.SOUTH);

         for(int var6 = 0; var6 < 6; ++var6) {
            this.func_175811_a(var1, var5, 1, 6 - var6, 1 + var6, var3);
            this.func_175811_a(var1, var5, 2, 6 - var6, 1 + var6, var3);
            this.func_175811_a(var1, var5, 3, 6 - var6, 1 + var6, var3);
            if (var6 < 5) {
               this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 1, 5 - var6, 1 + var6, var3);
               this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 2, 5 - var6, 1 + var6, var3);
               this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 3, 5 - var6, 1 + var6, var3);
            }
         }

         return true;
      }
   }

   public static class ChestCorridor extends StrongholdPieces.Stronghold {
      private boolean field_75002_c;

      public ChestCorridor() {
         super();
      }

      public ChestCorridor(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Chest", this.field_75002_c);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_75002_c = var1.func_74767_n("Chest");
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74986_a((StrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
      }

      public static StrongholdPieces.ChestCorridor func_175868_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -1, -1, 0, 5, 5, 7, var5);
         return func_74991_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new StrongholdPieces.ChestCorridor(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_74882_a(var1, var3, 0, 0, 0, 4, 4, 6, true, var2, StrongholdPieces.field_75204_e);
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 1, 0);
         this.func_74990_a(var1, var2, var3, StrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
         this.func_175804_a(var1, var3, 3, 1, 2, 3, 1, 4, Blocks.field_196696_di.func_176223_P(), Blocks.field_196696_di.func_176223_P(), false);
         this.func_175811_a(var1, Blocks.field_196573_bB.func_176223_P(), 3, 1, 1, var3);
         this.func_175811_a(var1, Blocks.field_196573_bB.func_176223_P(), 3, 1, 5, var3);
         this.func_175811_a(var1, Blocks.field_196573_bB.func_176223_P(), 3, 2, 2, var3);
         this.func_175811_a(var1, Blocks.field_196573_bB.func_176223_P(), 3, 2, 4, var3);

         for(int var5 = 2; var5 <= 4; ++var5) {
            this.func_175811_a(var1, Blocks.field_196573_bB.func_176223_P(), 2, 1, var5, var3);
         }

         if (!this.field_75002_c && var3.func_175898_b(new BlockPos(this.func_74865_a(3, 3), this.func_74862_a(2), this.func_74873_b(3, 3)))) {
            this.field_75002_c = true;
            this.func_186167_a(var1, var3, var2, 3, 2, 3, LootTableList.field_186428_j);
         }

         return true;
      }
   }

   public static class Straight extends StrongholdPieces.Stronghold {
      private boolean field_75019_b;
      private boolean field_75020_c;

      public Straight() {
         super();
      }

      public Straight(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
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

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_75019_b = var1.func_74767_n("Left");
         this.field_75020_c = var1.func_74767_n("Right");
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         this.func_74986_a((StrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
         if (this.field_75019_b) {
            this.func_74989_b((StrongholdPieces.Stairs2)var1, var2, var3, 1, 2);
         }

         if (this.field_75020_c) {
            this.func_74987_c((StrongholdPieces.Stairs2)var1, var2, var3, 1, 2);
         }

      }

      public static StrongholdPieces.Straight func_175862_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -1, -1, 0, 5, 5, 7, var5);
         return func_74991_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new StrongholdPieces.Straight(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_74882_a(var1, var3, 0, 0, 0, 4, 4, 6, true, var2, StrongholdPieces.field_75204_e);
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 1, 0);
         this.func_74990_a(var1, var2, var3, StrongholdPieces.Stronghold.Door.OPENING, 1, 1, 6);
         IBlockState var5 = (IBlockState)Blocks.field_196591_bQ.func_176223_P().func_206870_a(BlockTorchWall.field_196532_a, EnumFacing.EAST);
         IBlockState var6 = (IBlockState)Blocks.field_196591_bQ.func_176223_P().func_206870_a(BlockTorchWall.field_196532_a, EnumFacing.WEST);
         this.func_175809_a(var1, var3, var2, 0.1F, 1, 2, 1, var5);
         this.func_175809_a(var1, var3, var2, 0.1F, 3, 2, 1, var6);
         this.func_175809_a(var1, var3, var2, 0.1F, 1, 2, 5, var5);
         this.func_175809_a(var1, var3, var2, 0.1F, 3, 2, 5, var6);
         if (this.field_75019_b) {
            this.func_175804_a(var1, var3, 0, 1, 2, 0, 3, 4, field_202556_l, field_202556_l, false);
         }

         if (this.field_75020_c) {
            this.func_175804_a(var1, var3, 4, 1, 2, 4, 3, 4, field_202556_l, field_202556_l, false);
         }

         return true;
      }
   }

   public static class Stairs2 extends StrongholdPieces.Stairs {
      public StrongholdPieces.PieceWeight field_75027_a;
      public StrongholdPieces.PortalRoom field_75025_b;
      public List<StructurePiece> field_75026_c = Lists.newArrayList();

      public Stairs2() {
         super();
      }

      public Stairs2(int var1, Random var2, int var3, int var4) {
         super(0, var2, var3, var4);
      }
   }

   public static class Stairs extends StrongholdPieces.Stronghold {
      private boolean field_75024_a;

      public Stairs() {
         super();
      }

      public Stairs(int var1, Random var2, int var3, int var4) {
         super(var1);
         this.field_75024_a = true;
         this.func_186164_a(EnumFacing.Plane.HORIZONTAL.func_179518_a(var2));
         this.field_143013_d = StrongholdPieces.Stronghold.Door.OPENING;
         if (this.func_186165_e().func_176740_k() == EnumFacing.Axis.Z) {
            this.field_74887_e = new MutableBoundingBox(var3, 64, var4, var3 + 5 - 1, 74, var4 + 5 - 1);
         } else {
            this.field_74887_e = new MutableBoundingBox(var3, 64, var4, var3 + 5 - 1, 74, var4 + 5 - 1);
         }

      }

      public Stairs(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.field_75024_a = false;
         this.func_186164_a(var4);
         this.field_143013_d = this.func_74988_a(var2);
         this.field_74887_e = var3;
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74757_a("Source", this.field_75024_a);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_75024_a = var1.func_74767_n("Source");
      }

      public void func_74861_a(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         if (this.field_75024_a) {
            StrongholdPieces.field_75203_d = StrongholdPieces.Crossing.class;
         }

         this.func_74986_a((StrongholdPieces.Stairs2)var1, var2, var3, 1, 1);
      }

      public static StrongholdPieces.Stairs func_175863_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5, int var6) {
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -1, -7, 0, 5, 11, 5, var5);
         return func_74991_a(var7) && StructurePiece.func_74883_a(var0, var7) == null ? new StrongholdPieces.Stairs(var6, var1, var7, var5) : null;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_74882_a(var1, var3, 0, 0, 0, 4, 10, 4, true, var2, StrongholdPieces.field_75204_e);
         this.func_74990_a(var1, var2, var3, this.field_143013_d, 1, 7, 0);
         this.func_74990_a(var1, var2, var3, StrongholdPieces.Stronghold.Door.OPENING, 1, 1, 4);
         this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 2, 6, 1, var3);
         this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 1, 5, 1, var3);
         this.func_175811_a(var1, Blocks.field_150333_U.func_176223_P(), 1, 6, 1, var3);
         this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 1, 5, 2, var3);
         this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 1, 4, 3, var3);
         this.func_175811_a(var1, Blocks.field_150333_U.func_176223_P(), 1, 5, 3, var3);
         this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 2, 4, 3, var3);
         this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 3, 3, 3, var3);
         this.func_175811_a(var1, Blocks.field_150333_U.func_176223_P(), 3, 4, 3, var3);
         this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 3, 3, 2, var3);
         this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 3, 2, 1, var3);
         this.func_175811_a(var1, Blocks.field_150333_U.func_176223_P(), 3, 3, 1, var3);
         this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 2, 2, 1, var3);
         this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 1, 1, 1, var3);
         this.func_175811_a(var1, Blocks.field_150333_U.func_176223_P(), 1, 2, 1, var3);
         this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 1, 1, 2, var3);
         this.func_175811_a(var1, Blocks.field_150333_U.func_176223_P(), 1, 1, 3, var3);
         return true;
      }
   }

   public static class Corridor extends StrongholdPieces.Stronghold {
      private int field_74993_a;

      public Corridor() {
         super();
      }

      public Corridor(int var1, Random var2, MutableBoundingBox var3, EnumFacing var4) {
         super(var1);
         this.func_186164_a(var4);
         this.field_74887_e = var3;
         this.field_74993_a = var4 != EnumFacing.NORTH && var4 != EnumFacing.SOUTH ? var3.func_78883_b() : var3.func_78880_d();
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74768_a("Steps", this.field_74993_a);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_74993_a = var1.func_74762_e("Steps");
      }

      public static MutableBoundingBox func_175869_a(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, EnumFacing var5) {
         boolean var6 = true;
         MutableBoundingBox var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -1, -1, 0, 5, 5, 4, var5);
         StructurePiece var8 = StructurePiece.func_74883_a(var0, var7);
         if (var8 == null) {
            return null;
         } else {
            if (var8.func_74874_b().field_78895_b == var7.field_78895_b) {
               for(int var9 = 3; var9 >= 1; --var9) {
                  var7 = MutableBoundingBox.func_175897_a(var2, var3, var4, -1, -1, 0, 5, 5, var9 - 1, var5);
                  if (!var8.func_74874_b().func_78884_a(var7)) {
                     return MutableBoundingBox.func_175897_a(var2, var3, var4, -1, -1, 0, 5, 5, var9, var5);
                  }
               }
            }

            return null;
         }
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         for(int var5 = 0; var5 < this.field_74993_a; ++var5) {
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 0, 0, var5, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 1, 0, var5, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 2, 0, var5, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 3, 0, var5, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 4, 0, var5, var3);

            for(int var6 = 1; var6 <= 3; ++var6) {
               this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 0, var6, var5, var3);
               this.func_175811_a(var1, Blocks.field_201941_jj.func_176223_P(), 1, var6, var5, var3);
               this.func_175811_a(var1, Blocks.field_201941_jj.func_176223_P(), 2, var6, var5, var3);
               this.func_175811_a(var1, Blocks.field_201941_jj.func_176223_P(), 3, var6, var5, var3);
               this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 4, var6, var5, var3);
            }

            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 0, 4, var5, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 1, 4, var5, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 2, 4, var5, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 3, 4, var5, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), 4, 4, var5, var3);
         }

         return true;
      }
   }

   abstract static class Stronghold extends StructurePiece {
      protected StrongholdPieces.Stronghold.Door field_143013_d;

      public Stronghold() {
         super();
         this.field_143013_d = StrongholdPieces.Stronghold.Door.OPENING;
      }

      protected Stronghold(int var1) {
         super(var1);
         this.field_143013_d = StrongholdPieces.Stronghold.Door.OPENING;
      }

      protected void func_143012_a(NBTTagCompound var1) {
         var1.func_74778_a("EntryDoor", this.field_143013_d.name());
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         this.field_143013_d = StrongholdPieces.Stronghold.Door.valueOf(var1.func_74779_i("EntryDoor"));
      }

      protected void func_74990_a(IWorld var1, Random var2, MutableBoundingBox var3, StrongholdPieces.Stronghold.Door var4, int var5, int var6, int var7) {
         switch(var4) {
         case OPENING:
            this.func_175804_a(var1, var3, var5, var6, var7, var5 + 3 - 1, var6 + 3 - 1, var7, field_202556_l, field_202556_l, false);
            break;
         case WOOD_DOOR:
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), var5, var6, var7, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), var5, var6 + 1, var7, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), var5, var6 + 2, var7, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), var5 + 1, var6 + 2, var7, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), var5 + 2, var6 + 2, var7, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), var5 + 2, var6 + 1, var7, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), var5 + 2, var6, var7, var3);
            this.func_175811_a(var1, Blocks.field_180413_ao.func_176223_P(), var5 + 1, var6, var7, var3);
            this.func_175811_a(var1, (IBlockState)Blocks.field_180413_ao.func_176223_P().func_206870_a(BlockDoor.field_176523_O, DoubleBlockHalf.UPPER), var5 + 1, var6 + 1, var7, var3);
            break;
         case GRATES:
            this.func_175811_a(var1, Blocks.field_201941_jj.func_176223_P(), var5 + 1, var6, var7, var3);
            this.func_175811_a(var1, Blocks.field_201941_jj.func_176223_P(), var5 + 1, var6 + 1, var7, var3);
            this.func_175811_a(var1, (IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196414_y, true), var5, var6, var7, var3);
            this.func_175811_a(var1, (IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196414_y, true), var5, var6 + 1, var7, var3);
            this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196411_b, true)).func_206870_a(BlockPane.field_196414_y, true), var5, var6 + 2, var7, var3);
            this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196411_b, true)).func_206870_a(BlockPane.field_196414_y, true), var5 + 1, var6 + 2, var7, var3);
            this.func_175811_a(var1, (IBlockState)((IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196411_b, true)).func_206870_a(BlockPane.field_196414_y, true), var5 + 2, var6 + 2, var7, var3);
            this.func_175811_a(var1, (IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196411_b, true), var5 + 2, var6 + 1, var7, var3);
            this.func_175811_a(var1, (IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196411_b, true), var5 + 2, var6, var7, var3);
            break;
         case IRON_DOOR:
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), var5, var6, var7, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), var5, var6 + 1, var7, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), var5, var6 + 2, var7, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), var5 + 1, var6 + 2, var7, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), var5 + 2, var6 + 2, var7, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), var5 + 2, var6 + 1, var7, var3);
            this.func_175811_a(var1, Blocks.field_196696_di.func_176223_P(), var5 + 2, var6, var7, var3);
            this.func_175811_a(var1, Blocks.field_150454_av.func_176223_P(), var5 + 1, var6, var7, var3);
            this.func_175811_a(var1, (IBlockState)Blocks.field_150454_av.func_176223_P().func_206870_a(BlockDoor.field_176523_O, DoubleBlockHalf.UPPER), var5 + 1, var6 + 1, var7, var3);
            this.func_175811_a(var1, (IBlockState)Blocks.field_150430_aB.func_176223_P().func_206870_a(BlockButton.field_185512_D, EnumFacing.NORTH), var5 + 2, var6 + 1, var7 + 1, var3);
            this.func_175811_a(var1, (IBlockState)Blocks.field_150430_aB.func_176223_P().func_206870_a(BlockButton.field_185512_D, EnumFacing.SOUTH), var5 + 2, var6 + 1, var7 - 1, var3);
         }

      }

      protected StrongholdPieces.Stronghold.Door func_74988_a(Random var1) {
         int var2 = var1.nextInt(5);
         switch(var2) {
         case 0:
         case 1:
         default:
            return StrongholdPieces.Stronghold.Door.OPENING;
         case 2:
            return StrongholdPieces.Stronghold.Door.WOOD_DOOR;
         case 3:
            return StrongholdPieces.Stronghold.Door.GRATES;
         case 4:
            return StrongholdPieces.Stronghold.Door.IRON_DOOR;
         }
      }

      @Nullable
      protected StructurePiece func_74986_a(StrongholdPieces.Stairs2 var1, List<StructurePiece> var2, Random var3, int var4, int var5) {
         EnumFacing var6 = this.func_186165_e();
         if (var6 != null) {
            switch(var6) {
            case NORTH:
               return StrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a + var4, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78896_c - 1, var6, this.func_74877_c());
            case SOUTH:
               return StrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a + var4, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78892_f + 1, var6, this.func_74877_c());
            case WEST:
               return StrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78896_c + var4, var6, this.func_74877_c());
            case EAST:
               return StrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var5, this.field_74887_e.field_78896_c + var4, var6, this.func_74877_c());
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece func_74989_b(StrongholdPieces.Stairs2 var1, List<StructurePiece> var2, Random var3, int var4, int var5) {
         EnumFacing var6 = this.func_186165_e();
         if (var6 != null) {
            switch(var6) {
            case NORTH:
               return StrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.WEST, this.func_74877_c());
            case SOUTH:
               return StrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a - 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.WEST, this.func_74877_c());
            case WEST:
               return StrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c());
            case EAST:
               return StrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c - 1, EnumFacing.NORTH, this.func_74877_c());
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece func_74987_c(StrongholdPieces.Stairs2 var1, List<StructurePiece> var2, Random var3, int var4, int var5) {
         EnumFacing var6 = this.func_186165_e();
         if (var6 != null) {
            switch(var6) {
            case NORTH:
               return StrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.EAST, this.func_74877_c());
            case SOUTH:
               return StrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78893_d + 1, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78896_c + var5, EnumFacing.EAST, this.func_74877_c());
            case WEST:
               return StrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c());
            case EAST:
               return StrongholdPieces.func_175953_c(var1, var2, var3, this.field_74887_e.field_78897_a + var5, this.field_74887_e.field_78895_b + var4, this.field_74887_e.field_78892_f + 1, EnumFacing.SOUTH, this.func_74877_c());
            }
         }

         return null;
      }

      protected static boolean func_74991_a(MutableBoundingBox var0) {
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
      public Class<? extends StrongholdPieces.Stronghold> field_75194_a;
      public final int field_75192_b;
      public int field_75193_c;
      public int field_75191_d;

      public PieceWeight(Class<? extends StrongholdPieces.Stronghold> var1, int var2, int var3) {
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
