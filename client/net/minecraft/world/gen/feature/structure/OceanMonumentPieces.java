package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class OceanMonumentPieces {
   public static void func_175970_a() {
      StructureIO.func_143031_a(OceanMonumentPieces.MonumentBuilding.class, "OMB");
      StructureIO.func_143031_a(OceanMonumentPieces.MonumentCoreRoom.class, "OMCR");
      StructureIO.func_143031_a(OceanMonumentPieces.DoubleXRoom.class, "OMDXR");
      StructureIO.func_143031_a(OceanMonumentPieces.DoubleXYRoom.class, "OMDXYR");
      StructureIO.func_143031_a(OceanMonumentPieces.DoubleYRoom.class, "OMDYR");
      StructureIO.func_143031_a(OceanMonumentPieces.DoubleYZRoom.class, "OMDYZR");
      StructureIO.func_143031_a(OceanMonumentPieces.DoubleZRoom.class, "OMDZR");
      StructureIO.func_143031_a(OceanMonumentPieces.EntryRoom.class, "OMEntry");
      StructureIO.func_143031_a(OceanMonumentPieces.Penthouse.class, "OMPenthouse");
      StructureIO.func_143031_a(OceanMonumentPieces.SimpleRoom.class, "OMSimple");
      StructureIO.func_143031_a(OceanMonumentPieces.SimpleTopRoom.class, "OMSimpleT");
   }

   static class YZDoubleRoomFitHelper implements OceanMonumentPieces.MonumentRoomFitHelper {
      private YZDoubleRoomFitHelper() {
         super();
      }

      public boolean func_175969_a(OceanMonumentPieces.RoomDefinition var1) {
         if (var1.field_175966_c[EnumFacing.NORTH.func_176745_a()] && !var1.field_175965_b[EnumFacing.NORTH.func_176745_a()].field_175963_d && var1.field_175966_c[EnumFacing.UP.func_176745_a()] && !var1.field_175965_b[EnumFacing.UP.func_176745_a()].field_175963_d) {
            OceanMonumentPieces.RoomDefinition var2 = var1.field_175965_b[EnumFacing.NORTH.func_176745_a()];
            return var2.field_175966_c[EnumFacing.UP.func_176745_a()] && !var2.field_175965_b[EnumFacing.UP.func_176745_a()].field_175963_d;
         } else {
            return false;
         }
      }

      public OceanMonumentPieces.Piece func_175968_a(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         var2.field_175963_d = true;
         var2.field_175965_b[EnumFacing.NORTH.func_176745_a()].field_175963_d = true;
         var2.field_175965_b[EnumFacing.UP.func_176745_a()].field_175963_d = true;
         var2.field_175965_b[EnumFacing.NORTH.func_176745_a()].field_175965_b[EnumFacing.UP.func_176745_a()].field_175963_d = true;
         return new OceanMonumentPieces.DoubleYZRoom(var1, var2, var3);
      }

      // $FF: synthetic method
      YZDoubleRoomFitHelper(Object var1) {
         this();
      }
   }

   static class XYDoubleRoomFitHelper implements OceanMonumentPieces.MonumentRoomFitHelper {
      private XYDoubleRoomFitHelper() {
         super();
      }

      public boolean func_175969_a(OceanMonumentPieces.RoomDefinition var1) {
         if (var1.field_175966_c[EnumFacing.EAST.func_176745_a()] && !var1.field_175965_b[EnumFacing.EAST.func_176745_a()].field_175963_d && var1.field_175966_c[EnumFacing.UP.func_176745_a()] && !var1.field_175965_b[EnumFacing.UP.func_176745_a()].field_175963_d) {
            OceanMonumentPieces.RoomDefinition var2 = var1.field_175965_b[EnumFacing.EAST.func_176745_a()];
            return var2.field_175966_c[EnumFacing.UP.func_176745_a()] && !var2.field_175965_b[EnumFacing.UP.func_176745_a()].field_175963_d;
         } else {
            return false;
         }
      }

      public OceanMonumentPieces.Piece func_175968_a(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         var2.field_175963_d = true;
         var2.field_175965_b[EnumFacing.EAST.func_176745_a()].field_175963_d = true;
         var2.field_175965_b[EnumFacing.UP.func_176745_a()].field_175963_d = true;
         var2.field_175965_b[EnumFacing.EAST.func_176745_a()].field_175965_b[EnumFacing.UP.func_176745_a()].field_175963_d = true;
         return new OceanMonumentPieces.DoubleXYRoom(var1, var2, var3);
      }

      // $FF: synthetic method
      XYDoubleRoomFitHelper(Object var1) {
         this();
      }
   }

   static class ZDoubleRoomFitHelper implements OceanMonumentPieces.MonumentRoomFitHelper {
      private ZDoubleRoomFitHelper() {
         super();
      }

      public boolean func_175969_a(OceanMonumentPieces.RoomDefinition var1) {
         return var1.field_175966_c[EnumFacing.NORTH.func_176745_a()] && !var1.field_175965_b[EnumFacing.NORTH.func_176745_a()].field_175963_d;
      }

      public OceanMonumentPieces.Piece func_175968_a(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         OceanMonumentPieces.RoomDefinition var4 = var2;
         if (!var2.field_175966_c[EnumFacing.NORTH.func_176745_a()] || var2.field_175965_b[EnumFacing.NORTH.func_176745_a()].field_175963_d) {
            var4 = var2.field_175965_b[EnumFacing.SOUTH.func_176745_a()];
         }

         var4.field_175963_d = true;
         var4.field_175965_b[EnumFacing.NORTH.func_176745_a()].field_175963_d = true;
         return new OceanMonumentPieces.DoubleZRoom(var1, var4, var3);
      }

      // $FF: synthetic method
      ZDoubleRoomFitHelper(Object var1) {
         this();
      }
   }

   static class XDoubleRoomFitHelper implements OceanMonumentPieces.MonumentRoomFitHelper {
      private XDoubleRoomFitHelper() {
         super();
      }

      public boolean func_175969_a(OceanMonumentPieces.RoomDefinition var1) {
         return var1.field_175966_c[EnumFacing.EAST.func_176745_a()] && !var1.field_175965_b[EnumFacing.EAST.func_176745_a()].field_175963_d;
      }

      public OceanMonumentPieces.Piece func_175968_a(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         var2.field_175963_d = true;
         var2.field_175965_b[EnumFacing.EAST.func_176745_a()].field_175963_d = true;
         return new OceanMonumentPieces.DoubleXRoom(var1, var2, var3);
      }

      // $FF: synthetic method
      XDoubleRoomFitHelper(Object var1) {
         this();
      }
   }

   static class YDoubleRoomFitHelper implements OceanMonumentPieces.MonumentRoomFitHelper {
      private YDoubleRoomFitHelper() {
         super();
      }

      public boolean func_175969_a(OceanMonumentPieces.RoomDefinition var1) {
         return var1.field_175966_c[EnumFacing.UP.func_176745_a()] && !var1.field_175965_b[EnumFacing.UP.func_176745_a()].field_175963_d;
      }

      public OceanMonumentPieces.Piece func_175968_a(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         var2.field_175963_d = true;
         var2.field_175965_b[EnumFacing.UP.func_176745_a()].field_175963_d = true;
         return new OceanMonumentPieces.DoubleYRoom(var1, var2, var3);
      }

      // $FF: synthetic method
      YDoubleRoomFitHelper(Object var1) {
         this();
      }
   }

   static class FitSimpleRoomTopHelper implements OceanMonumentPieces.MonumentRoomFitHelper {
      private FitSimpleRoomTopHelper() {
         super();
      }

      public boolean func_175969_a(OceanMonumentPieces.RoomDefinition var1) {
         return !var1.field_175966_c[EnumFacing.WEST.func_176745_a()] && !var1.field_175966_c[EnumFacing.EAST.func_176745_a()] && !var1.field_175966_c[EnumFacing.NORTH.func_176745_a()] && !var1.field_175966_c[EnumFacing.SOUTH.func_176745_a()] && !var1.field_175966_c[EnumFacing.UP.func_176745_a()];
      }

      public OceanMonumentPieces.Piece func_175968_a(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         var2.field_175963_d = true;
         return new OceanMonumentPieces.SimpleTopRoom(var1, var2, var3);
      }

      // $FF: synthetic method
      FitSimpleRoomTopHelper(Object var1) {
         this();
      }
   }

   static class FitSimpleRoomHelper implements OceanMonumentPieces.MonumentRoomFitHelper {
      private FitSimpleRoomHelper() {
         super();
      }

      public boolean func_175969_a(OceanMonumentPieces.RoomDefinition var1) {
         return true;
      }

      public OceanMonumentPieces.Piece func_175968_a(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         var2.field_175963_d = true;
         return new OceanMonumentPieces.SimpleRoom(var1, var2, var3);
      }

      // $FF: synthetic method
      FitSimpleRoomHelper(Object var1) {
         this();
      }
   }

   interface MonumentRoomFitHelper {
      boolean func_175969_a(OceanMonumentPieces.RoomDefinition var1);

      OceanMonumentPieces.Piece func_175968_a(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3);
   }

   static class RoomDefinition {
      private final int field_175967_a;
      private final OceanMonumentPieces.RoomDefinition[] field_175965_b = new OceanMonumentPieces.RoomDefinition[6];
      private final boolean[] field_175966_c = new boolean[6];
      private boolean field_175963_d;
      private boolean field_175964_e;
      private int field_175962_f;

      public RoomDefinition(int var1) {
         super();
         this.field_175967_a = var1;
      }

      public void func_175957_a(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2) {
         this.field_175965_b[var1.func_176745_a()] = var2;
         var2.field_175965_b[var1.func_176734_d().func_176745_a()] = this;
      }

      public void func_175958_a() {
         for(int var1 = 0; var1 < 6; ++var1) {
            this.field_175966_c[var1] = this.field_175965_b[var1] != null;
         }

      }

      public boolean func_175959_a(int var1) {
         if (this.field_175964_e) {
            return true;
         } else {
            this.field_175962_f = var1;

            for(int var2 = 0; var2 < 6; ++var2) {
               if (this.field_175965_b[var2] != null && this.field_175966_c[var2] && this.field_175965_b[var2].field_175962_f != var1 && this.field_175965_b[var2].func_175959_a(var1)) {
                  return true;
               }
            }

            return false;
         }
      }

      public boolean func_175961_b() {
         return this.field_175967_a >= 75;
      }

      public int func_175960_c() {
         int var1 = 0;

         for(int var2 = 0; var2 < 6; ++var2) {
            if (this.field_175966_c[var2]) {
               ++var1;
            }
         }

         return var1;
      }
   }

   public static class Penthouse extends OceanMonumentPieces.Piece {
      public Penthouse() {
         super();
      }

      public Penthouse(EnumFacing var1, MutableBoundingBox var2) {
         super(var1, var2);
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_175804_a(var1, var3, 2, -1, 2, 11, -1, 11, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 0, -1, 0, 1, -1, 11, field_175828_a, field_175828_a, false);
         this.func_175804_a(var1, var3, 12, -1, 0, 13, -1, 11, field_175828_a, field_175828_a, false);
         this.func_175804_a(var1, var3, 2, -1, 0, 11, -1, 1, field_175828_a, field_175828_a, false);
         this.func_175804_a(var1, var3, 2, -1, 12, 11, -1, 13, field_175828_a, field_175828_a, false);
         this.func_175804_a(var1, var3, 0, 0, 0, 0, 0, 13, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 13, 0, 0, 13, 0, 13, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 0, 0, 12, 0, 0, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 0, 13, 12, 0, 13, field_175826_b, field_175826_b, false);

         for(int var5 = 2; var5 <= 11; var5 += 3) {
            this.func_175811_a(var1, field_175825_e, 0, 0, var5, var3);
            this.func_175811_a(var1, field_175825_e, 13, 0, var5, var3);
            this.func_175811_a(var1, field_175825_e, var5, 0, 0, var3);
         }

         this.func_175804_a(var1, var3, 2, 0, 3, 4, 0, 9, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 9, 0, 3, 11, 0, 9, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 4, 0, 9, 9, 0, 11, field_175826_b, field_175826_b, false);
         this.func_175811_a(var1, field_175826_b, 5, 0, 8, var3);
         this.func_175811_a(var1, field_175826_b, 8, 0, 8, var3);
         this.func_175811_a(var1, field_175826_b, 10, 0, 10, var3);
         this.func_175811_a(var1, field_175826_b, 3, 0, 10, var3);
         this.func_175804_a(var1, var3, 3, 0, 3, 3, 0, 7, field_175827_c, field_175827_c, false);
         this.func_175804_a(var1, var3, 10, 0, 3, 10, 0, 7, field_175827_c, field_175827_c, false);
         this.func_175804_a(var1, var3, 6, 0, 10, 7, 0, 10, field_175827_c, field_175827_c, false);
         byte var8 = 3;

         for(int var6 = 0; var6 < 2; ++var6) {
            for(int var7 = 2; var7 <= 8; var7 += 3) {
               this.func_175804_a(var1, var3, var8, 0, var7, var8, 2, var7, field_175826_b, field_175826_b, false);
            }

            var8 = 10;
         }

         this.func_175804_a(var1, var3, 5, 0, 10, 5, 2, 10, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 8, 0, 10, 8, 2, 10, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 6, -1, 7, 7, -1, 8, field_175827_c, field_175827_c, false);
         this.func_209179_a(var1, var3, 6, -1, 3, 7, -1, 4);
         this.func_175817_a(var1, var3, 6, 1, 6);
         return true;
      }
   }

   public static class WingRoom extends OceanMonumentPieces.Piece {
      private int field_175834_o;

      public WingRoom() {
         super();
      }

      public WingRoom(EnumFacing var1, MutableBoundingBox var2, int var3) {
         super(var1, var2);
         this.field_175834_o = var3 & 1;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.field_175834_o == 0) {
            int var5;
            for(var5 = 0; var5 < 4; ++var5) {
               this.func_175804_a(var1, var3, 10 - var5, 3 - var5, 20 - var5, 12 + var5, 3 - var5, 20, field_175826_b, field_175826_b, false);
            }

            this.func_175804_a(var1, var3, 7, 0, 6, 15, 0, 16, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 6, 0, 6, 6, 3, 20, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 16, 0, 6, 16, 3, 20, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 7, 1, 7, 7, 1, 20, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 15, 1, 7, 15, 1, 20, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 7, 1, 6, 9, 3, 6, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 13, 1, 6, 15, 3, 6, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 8, 1, 7, 9, 1, 7, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 13, 1, 7, 14, 1, 7, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 9, 0, 5, 13, 0, 5, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 10, 0, 7, 12, 0, 7, field_175827_c, field_175827_c, false);
            this.func_175804_a(var1, var3, 8, 0, 10, 8, 0, 12, field_175827_c, field_175827_c, false);
            this.func_175804_a(var1, var3, 14, 0, 10, 14, 0, 12, field_175827_c, field_175827_c, false);

            for(var5 = 18; var5 >= 7; var5 -= 3) {
               this.func_175811_a(var1, field_175825_e, 6, 3, var5, var3);
               this.func_175811_a(var1, field_175825_e, 16, 3, var5, var3);
            }

            this.func_175811_a(var1, field_175825_e, 10, 0, 10, var3);
            this.func_175811_a(var1, field_175825_e, 12, 0, 10, var3);
            this.func_175811_a(var1, field_175825_e, 10, 0, 12, var3);
            this.func_175811_a(var1, field_175825_e, 12, 0, 12, var3);
            this.func_175811_a(var1, field_175825_e, 8, 3, 6, var3);
            this.func_175811_a(var1, field_175825_e, 14, 3, 6, var3);
            this.func_175811_a(var1, field_175826_b, 4, 2, 4, var3);
            this.func_175811_a(var1, field_175825_e, 4, 1, 4, var3);
            this.func_175811_a(var1, field_175826_b, 4, 0, 4, var3);
            this.func_175811_a(var1, field_175826_b, 18, 2, 4, var3);
            this.func_175811_a(var1, field_175825_e, 18, 1, 4, var3);
            this.func_175811_a(var1, field_175826_b, 18, 0, 4, var3);
            this.func_175811_a(var1, field_175826_b, 4, 2, 18, var3);
            this.func_175811_a(var1, field_175825_e, 4, 1, 18, var3);
            this.func_175811_a(var1, field_175826_b, 4, 0, 18, var3);
            this.func_175811_a(var1, field_175826_b, 18, 2, 18, var3);
            this.func_175811_a(var1, field_175825_e, 18, 1, 18, var3);
            this.func_175811_a(var1, field_175826_b, 18, 0, 18, var3);
            this.func_175811_a(var1, field_175826_b, 9, 7, 20, var3);
            this.func_175811_a(var1, field_175826_b, 13, 7, 20, var3);
            this.func_175804_a(var1, var3, 6, 0, 21, 7, 4, 21, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 15, 0, 21, 16, 4, 21, field_175826_b, field_175826_b, false);
            this.func_175817_a(var1, var3, 11, 2, 16);
         } else if (this.field_175834_o == 1) {
            this.func_175804_a(var1, var3, 9, 3, 18, 13, 3, 20, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 9, 0, 18, 9, 2, 18, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 13, 0, 18, 13, 2, 18, field_175826_b, field_175826_b, false);
            byte var9 = 9;
            boolean var6 = true;
            boolean var7 = true;

            int var8;
            for(var8 = 0; var8 < 2; ++var8) {
               this.func_175811_a(var1, field_175826_b, var9, 6, 20, var3);
               this.func_175811_a(var1, field_175825_e, var9, 5, 20, var3);
               this.func_175811_a(var1, field_175826_b, var9, 4, 20, var3);
               var9 = 13;
            }

            this.func_175804_a(var1, var3, 7, 3, 7, 15, 3, 14, field_175826_b, field_175826_b, false);
            var9 = 10;

            for(var8 = 0; var8 < 2; ++var8) {
               this.func_175804_a(var1, var3, var9, 0, 10, var9, 6, 10, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, var9, 0, 12, var9, 6, 12, field_175826_b, field_175826_b, false);
               this.func_175811_a(var1, field_175825_e, var9, 0, 10, var3);
               this.func_175811_a(var1, field_175825_e, var9, 0, 12, var3);
               this.func_175811_a(var1, field_175825_e, var9, 4, 10, var3);
               this.func_175811_a(var1, field_175825_e, var9, 4, 12, var3);
               var9 = 12;
            }

            var9 = 8;

            for(var8 = 0; var8 < 2; ++var8) {
               this.func_175804_a(var1, var3, var9, 0, 7, var9, 2, 7, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, var9, 0, 14, var9, 2, 14, field_175826_b, field_175826_b, false);
               var9 = 14;
            }

            this.func_175804_a(var1, var3, 8, 3, 8, 8, 3, 13, field_175827_c, field_175827_c, false);
            this.func_175804_a(var1, var3, 14, 3, 8, 14, 3, 13, field_175827_c, field_175827_c, false);
            this.func_175817_a(var1, var3, 11, 5, 13);
         }

         return true;
      }
   }

   public static class MonumentCoreRoom extends OceanMonumentPieces.Piece {
      public MonumentCoreRoom() {
         super();
      }

      public MonumentCoreRoom(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         super(1, var1, var2, 2, 2, 2);
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_175819_a(var1, var3, 1, 8, 0, 14, 8, 14, field_175828_a);
         boolean var5 = true;
         IBlockState var6 = field_175826_b;
         this.func_175804_a(var1, var3, 0, 7, 0, 0, 7, 15, var6, var6, false);
         this.func_175804_a(var1, var3, 15, 7, 0, 15, 7, 15, var6, var6, false);
         this.func_175804_a(var1, var3, 1, 7, 0, 15, 7, 0, var6, var6, false);
         this.func_175804_a(var1, var3, 1, 7, 15, 14, 7, 15, var6, var6, false);

         int var8;
         for(var8 = 1; var8 <= 6; ++var8) {
            var6 = field_175826_b;
            if (var8 == 2 || var8 == 6) {
               var6 = field_175828_a;
            }

            for(int var7 = 0; var7 <= 15; var7 += 15) {
               this.func_175804_a(var1, var3, var7, var8, 0, var7, var8, 1, var6, var6, false);
               this.func_175804_a(var1, var3, var7, var8, 6, var7, var8, 9, var6, var6, false);
               this.func_175804_a(var1, var3, var7, var8, 14, var7, var8, 15, var6, var6, false);
            }

            this.func_175804_a(var1, var3, 1, var8, 0, 1, var8, 0, var6, var6, false);
            this.func_175804_a(var1, var3, 6, var8, 0, 9, var8, 0, var6, var6, false);
            this.func_175804_a(var1, var3, 14, var8, 0, 14, var8, 0, var6, var6, false);
            this.func_175804_a(var1, var3, 1, var8, 15, 14, var8, 15, var6, var6, false);
         }

         this.func_175804_a(var1, var3, 6, 3, 6, 9, 6, 9, field_175827_c, field_175827_c, false);
         this.func_175804_a(var1, var3, 7, 4, 7, 8, 5, 8, Blocks.field_150340_R.func_176223_P(), Blocks.field_150340_R.func_176223_P(), false);

         for(var8 = 3; var8 <= 6; var8 += 3) {
            for(int var9 = 6; var9 <= 9; var9 += 3) {
               this.func_175811_a(var1, field_175825_e, var9, var8, 6, var3);
               this.func_175811_a(var1, field_175825_e, var9, var8, 9, var3);
            }
         }

         this.func_175804_a(var1, var3, 5, 1, 6, 5, 2, 6, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 1, 9, 5, 2, 9, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 10, 1, 6, 10, 2, 6, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 10, 1, 9, 10, 2, 9, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 6, 1, 5, 6, 2, 5, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 9, 1, 5, 9, 2, 5, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 6, 1, 10, 6, 2, 10, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 9, 1, 10, 9, 2, 10, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 2, 5, 5, 6, 5, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 2, 10, 5, 6, 10, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 10, 2, 5, 10, 6, 5, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 10, 2, 10, 10, 6, 10, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 7, 1, 5, 7, 6, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 10, 7, 1, 10, 7, 6, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 7, 9, 5, 7, 14, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 10, 7, 9, 10, 7, 14, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 7, 5, 6, 7, 5, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 7, 10, 6, 7, 10, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 9, 7, 5, 14, 7, 5, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 9, 7, 10, 14, 7, 10, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 2, 1, 2, 2, 1, 3, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 3, 1, 2, 3, 1, 2, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 13, 1, 2, 13, 1, 3, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 12, 1, 2, 12, 1, 2, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 2, 1, 12, 2, 1, 13, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 3, 1, 13, 3, 1, 13, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 13, 1, 12, 13, 1, 13, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 12, 1, 13, 12, 1, 13, field_175826_b, field_175826_b, false);
         return true;
      }
   }

   public static class DoubleYZRoom extends OceanMonumentPieces.Piece {
      public DoubleYZRoom() {
         super();
      }

      public DoubleYZRoom(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         super(1, var1, var2, 1, 2, 2);
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         OceanMonumentPieces.RoomDefinition var5 = this.field_175830_k.field_175965_b[EnumFacing.NORTH.func_176745_a()];
         OceanMonumentPieces.RoomDefinition var6 = this.field_175830_k;
         OceanMonumentPieces.RoomDefinition var7 = var5.field_175965_b[EnumFacing.UP.func_176745_a()];
         OceanMonumentPieces.RoomDefinition var8 = var6.field_175965_b[EnumFacing.UP.func_176745_a()];
         if (this.field_175830_k.field_175967_a / 25 > 0) {
            this.func_175821_a(var1, var3, 0, 8, var5.field_175966_c[EnumFacing.DOWN.func_176745_a()]);
            this.func_175821_a(var1, var3, 0, 0, var6.field_175966_c[EnumFacing.DOWN.func_176745_a()]);
         }

         if (var8.field_175965_b[EnumFacing.UP.func_176745_a()] == null) {
            this.func_175819_a(var1, var3, 1, 8, 1, 6, 8, 7, field_175828_a);
         }

         if (var7.field_175965_b[EnumFacing.UP.func_176745_a()] == null) {
            this.func_175819_a(var1, var3, 1, 8, 8, 6, 8, 14, field_175828_a);
         }

         int var9;
         IBlockState var10;
         for(var9 = 1; var9 <= 7; ++var9) {
            var10 = field_175826_b;
            if (var9 == 2 || var9 == 6) {
               var10 = field_175828_a;
            }

            this.func_175804_a(var1, var3, 0, var9, 0, 0, var9, 15, var10, var10, false);
            this.func_175804_a(var1, var3, 7, var9, 0, 7, var9, 15, var10, var10, false);
            this.func_175804_a(var1, var3, 1, var9, 0, 6, var9, 0, var10, var10, false);
            this.func_175804_a(var1, var3, 1, var9, 15, 6, var9, 15, var10, var10, false);
         }

         for(var9 = 1; var9 <= 7; ++var9) {
            var10 = field_175827_c;
            if (var9 == 2 || var9 == 6) {
               var10 = field_175825_e;
            }

            this.func_175804_a(var1, var3, 3, var9, 7, 4, var9, 8, var10, var10, false);
         }

         if (var6.field_175966_c[EnumFacing.SOUTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 3, 1, 0, 4, 2, 0);
         }

         if (var6.field_175966_c[EnumFacing.EAST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 7, 1, 3, 7, 2, 4);
         }

         if (var6.field_175966_c[EnumFacing.WEST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 0, 1, 3, 0, 2, 4);
         }

         if (var5.field_175966_c[EnumFacing.NORTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 3, 1, 15, 4, 2, 15);
         }

         if (var5.field_175966_c[EnumFacing.WEST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 0, 1, 11, 0, 2, 12);
         }

         if (var5.field_175966_c[EnumFacing.EAST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 7, 1, 11, 7, 2, 12);
         }

         if (var8.field_175966_c[EnumFacing.SOUTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 3, 5, 0, 4, 6, 0);
         }

         if (var8.field_175966_c[EnumFacing.EAST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 7, 5, 3, 7, 6, 4);
            this.func_175804_a(var1, var3, 5, 4, 2, 6, 4, 5, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 6, 1, 2, 6, 3, 2, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 6, 1, 5, 6, 3, 5, field_175826_b, field_175826_b, false);
         }

         if (var8.field_175966_c[EnumFacing.WEST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 0, 5, 3, 0, 6, 4);
            this.func_175804_a(var1, var3, 1, 4, 2, 2, 4, 5, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 1, 1, 2, 1, 3, 2, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 1, 1, 5, 1, 3, 5, field_175826_b, field_175826_b, false);
         }

         if (var7.field_175966_c[EnumFacing.NORTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 3, 5, 15, 4, 6, 15);
         }

         if (var7.field_175966_c[EnumFacing.WEST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 0, 5, 11, 0, 6, 12);
            this.func_175804_a(var1, var3, 1, 4, 10, 2, 4, 13, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 1, 1, 10, 1, 3, 10, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 1, 1, 13, 1, 3, 13, field_175826_b, field_175826_b, false);
         }

         if (var7.field_175966_c[EnumFacing.EAST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 7, 5, 11, 7, 6, 12);
            this.func_175804_a(var1, var3, 5, 4, 10, 6, 4, 13, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 6, 1, 10, 6, 3, 10, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 6, 1, 13, 6, 3, 13, field_175826_b, field_175826_b, false);
         }

         return true;
      }
   }

   public static class DoubleXYRoom extends OceanMonumentPieces.Piece {
      public DoubleXYRoom() {
         super();
      }

      public DoubleXYRoom(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         super(1, var1, var2, 2, 2, 1);
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         OceanMonumentPieces.RoomDefinition var5 = this.field_175830_k.field_175965_b[EnumFacing.EAST.func_176745_a()];
         OceanMonumentPieces.RoomDefinition var6 = this.field_175830_k;
         OceanMonumentPieces.RoomDefinition var7 = var6.field_175965_b[EnumFacing.UP.func_176745_a()];
         OceanMonumentPieces.RoomDefinition var8 = var5.field_175965_b[EnumFacing.UP.func_176745_a()];
         if (this.field_175830_k.field_175967_a / 25 > 0) {
            this.func_175821_a(var1, var3, 8, 0, var5.field_175966_c[EnumFacing.DOWN.func_176745_a()]);
            this.func_175821_a(var1, var3, 0, 0, var6.field_175966_c[EnumFacing.DOWN.func_176745_a()]);
         }

         if (var7.field_175965_b[EnumFacing.UP.func_176745_a()] == null) {
            this.func_175819_a(var1, var3, 1, 8, 1, 7, 8, 6, field_175828_a);
         }

         if (var8.field_175965_b[EnumFacing.UP.func_176745_a()] == null) {
            this.func_175819_a(var1, var3, 8, 8, 1, 14, 8, 6, field_175828_a);
         }

         for(int var9 = 1; var9 <= 7; ++var9) {
            IBlockState var10 = field_175826_b;
            if (var9 == 2 || var9 == 6) {
               var10 = field_175828_a;
            }

            this.func_175804_a(var1, var3, 0, var9, 0, 0, var9, 7, var10, var10, false);
            this.func_175804_a(var1, var3, 15, var9, 0, 15, var9, 7, var10, var10, false);
            this.func_175804_a(var1, var3, 1, var9, 0, 15, var9, 0, var10, var10, false);
            this.func_175804_a(var1, var3, 1, var9, 7, 14, var9, 7, var10, var10, false);
         }

         this.func_175804_a(var1, var3, 2, 1, 3, 2, 7, 4, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 3, 1, 2, 4, 7, 2, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 3, 1, 5, 4, 7, 5, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 13, 1, 3, 13, 7, 4, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 11, 1, 2, 12, 7, 2, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 11, 1, 5, 12, 7, 5, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 1, 3, 5, 3, 4, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 10, 1, 3, 10, 3, 4, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 7, 2, 10, 7, 5, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 5, 2, 5, 7, 2, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 10, 5, 2, 10, 7, 2, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 5, 5, 5, 7, 5, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 10, 5, 5, 10, 7, 5, field_175826_b, field_175826_b, false);
         this.func_175811_a(var1, field_175826_b, 6, 6, 2, var3);
         this.func_175811_a(var1, field_175826_b, 9, 6, 2, var3);
         this.func_175811_a(var1, field_175826_b, 6, 6, 5, var3);
         this.func_175811_a(var1, field_175826_b, 9, 6, 5, var3);
         this.func_175804_a(var1, var3, 5, 4, 3, 6, 4, 4, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 9, 4, 3, 10, 4, 4, field_175826_b, field_175826_b, false);
         this.func_175811_a(var1, field_175825_e, 5, 4, 2, var3);
         this.func_175811_a(var1, field_175825_e, 5, 4, 5, var3);
         this.func_175811_a(var1, field_175825_e, 10, 4, 2, var3);
         this.func_175811_a(var1, field_175825_e, 10, 4, 5, var3);
         if (var6.field_175966_c[EnumFacing.SOUTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 3, 1, 0, 4, 2, 0);
         }

         if (var6.field_175966_c[EnumFacing.NORTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 3, 1, 7, 4, 2, 7);
         }

         if (var6.field_175966_c[EnumFacing.WEST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 0, 1, 3, 0, 2, 4);
         }

         if (var5.field_175966_c[EnumFacing.SOUTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 11, 1, 0, 12, 2, 0);
         }

         if (var5.field_175966_c[EnumFacing.NORTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 11, 1, 7, 12, 2, 7);
         }

         if (var5.field_175966_c[EnumFacing.EAST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 15, 1, 3, 15, 2, 4);
         }

         if (var7.field_175966_c[EnumFacing.SOUTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 3, 5, 0, 4, 6, 0);
         }

         if (var7.field_175966_c[EnumFacing.NORTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 3, 5, 7, 4, 6, 7);
         }

         if (var7.field_175966_c[EnumFacing.WEST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 0, 5, 3, 0, 6, 4);
         }

         if (var8.field_175966_c[EnumFacing.SOUTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 11, 5, 0, 12, 6, 0);
         }

         if (var8.field_175966_c[EnumFacing.NORTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 11, 5, 7, 12, 6, 7);
         }

         if (var8.field_175966_c[EnumFacing.EAST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 15, 5, 3, 15, 6, 4);
         }

         return true;
      }
   }

   public static class DoubleZRoom extends OceanMonumentPieces.Piece {
      public DoubleZRoom() {
         super();
      }

      public DoubleZRoom(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         super(1, var1, var2, 1, 1, 2);
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         OceanMonumentPieces.RoomDefinition var5 = this.field_175830_k.field_175965_b[EnumFacing.NORTH.func_176745_a()];
         OceanMonumentPieces.RoomDefinition var6 = this.field_175830_k;
         if (this.field_175830_k.field_175967_a / 25 > 0) {
            this.func_175821_a(var1, var3, 0, 8, var5.field_175966_c[EnumFacing.DOWN.func_176745_a()]);
            this.func_175821_a(var1, var3, 0, 0, var6.field_175966_c[EnumFacing.DOWN.func_176745_a()]);
         }

         if (var6.field_175965_b[EnumFacing.UP.func_176745_a()] == null) {
            this.func_175819_a(var1, var3, 1, 4, 1, 6, 4, 7, field_175828_a);
         }

         if (var5.field_175965_b[EnumFacing.UP.func_176745_a()] == null) {
            this.func_175819_a(var1, var3, 1, 4, 8, 6, 4, 14, field_175828_a);
         }

         this.func_175804_a(var1, var3, 0, 3, 0, 0, 3, 15, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 7, 3, 0, 7, 3, 15, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 3, 0, 7, 3, 0, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 3, 15, 6, 3, 15, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 0, 2, 0, 0, 2, 15, field_175828_a, field_175828_a, false);
         this.func_175804_a(var1, var3, 7, 2, 0, 7, 2, 15, field_175828_a, field_175828_a, false);
         this.func_175804_a(var1, var3, 1, 2, 0, 7, 2, 0, field_175828_a, field_175828_a, false);
         this.func_175804_a(var1, var3, 1, 2, 15, 6, 2, 15, field_175828_a, field_175828_a, false);
         this.func_175804_a(var1, var3, 0, 1, 0, 0, 1, 15, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 7, 1, 0, 7, 1, 15, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 1, 0, 7, 1, 0, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 1, 15, 6, 1, 15, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 1, 1, 1, 1, 2, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 6, 1, 1, 6, 1, 2, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 3, 1, 1, 3, 2, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 6, 3, 1, 6, 3, 2, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 1, 13, 1, 1, 14, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 6, 1, 13, 6, 1, 14, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 3, 13, 1, 3, 14, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 6, 3, 13, 6, 3, 14, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 2, 1, 6, 2, 3, 6, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 1, 6, 5, 3, 6, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 2, 1, 9, 2, 3, 9, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 1, 9, 5, 3, 9, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 3, 2, 6, 4, 2, 6, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 3, 2, 9, 4, 2, 9, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 2, 2, 7, 2, 2, 8, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 2, 7, 5, 2, 8, field_175826_b, field_175826_b, false);
         this.func_175811_a(var1, field_175825_e, 2, 2, 5, var3);
         this.func_175811_a(var1, field_175825_e, 5, 2, 5, var3);
         this.func_175811_a(var1, field_175825_e, 2, 2, 10, var3);
         this.func_175811_a(var1, field_175825_e, 5, 2, 10, var3);
         this.func_175811_a(var1, field_175826_b, 2, 3, 5, var3);
         this.func_175811_a(var1, field_175826_b, 5, 3, 5, var3);
         this.func_175811_a(var1, field_175826_b, 2, 3, 10, var3);
         this.func_175811_a(var1, field_175826_b, 5, 3, 10, var3);
         if (var6.field_175966_c[EnumFacing.SOUTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 3, 1, 0, 4, 2, 0);
         }

         if (var6.field_175966_c[EnumFacing.EAST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 7, 1, 3, 7, 2, 4);
         }

         if (var6.field_175966_c[EnumFacing.WEST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 0, 1, 3, 0, 2, 4);
         }

         if (var5.field_175966_c[EnumFacing.NORTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 3, 1, 15, 4, 2, 15);
         }

         if (var5.field_175966_c[EnumFacing.WEST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 0, 1, 11, 0, 2, 12);
         }

         if (var5.field_175966_c[EnumFacing.EAST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 7, 1, 11, 7, 2, 12);
         }

         return true;
      }
   }

   public static class DoubleXRoom extends OceanMonumentPieces.Piece {
      public DoubleXRoom() {
         super();
      }

      public DoubleXRoom(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         super(1, var1, var2, 2, 1, 1);
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         OceanMonumentPieces.RoomDefinition var5 = this.field_175830_k.field_175965_b[EnumFacing.EAST.func_176745_a()];
         OceanMonumentPieces.RoomDefinition var6 = this.field_175830_k;
         if (this.field_175830_k.field_175967_a / 25 > 0) {
            this.func_175821_a(var1, var3, 8, 0, var5.field_175966_c[EnumFacing.DOWN.func_176745_a()]);
            this.func_175821_a(var1, var3, 0, 0, var6.field_175966_c[EnumFacing.DOWN.func_176745_a()]);
         }

         if (var6.field_175965_b[EnumFacing.UP.func_176745_a()] == null) {
            this.func_175819_a(var1, var3, 1, 4, 1, 7, 4, 6, field_175828_a);
         }

         if (var5.field_175965_b[EnumFacing.UP.func_176745_a()] == null) {
            this.func_175819_a(var1, var3, 8, 4, 1, 14, 4, 6, field_175828_a);
         }

         this.func_175804_a(var1, var3, 0, 3, 0, 0, 3, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 15, 3, 0, 15, 3, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 3, 0, 15, 3, 0, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 3, 7, 14, 3, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 0, 2, 0, 0, 2, 7, field_175828_a, field_175828_a, false);
         this.func_175804_a(var1, var3, 15, 2, 0, 15, 2, 7, field_175828_a, field_175828_a, false);
         this.func_175804_a(var1, var3, 1, 2, 0, 15, 2, 0, field_175828_a, field_175828_a, false);
         this.func_175804_a(var1, var3, 1, 2, 7, 14, 2, 7, field_175828_a, field_175828_a, false);
         this.func_175804_a(var1, var3, 0, 1, 0, 0, 1, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 15, 1, 0, 15, 1, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 1, 0, 15, 1, 0, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 1, 7, 14, 1, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 1, 0, 10, 1, 4, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 6, 2, 0, 9, 2, 3, field_175828_a, field_175828_a, false);
         this.func_175804_a(var1, var3, 5, 3, 0, 10, 3, 4, field_175826_b, field_175826_b, false);
         this.func_175811_a(var1, field_175825_e, 6, 2, 3, var3);
         this.func_175811_a(var1, field_175825_e, 9, 2, 3, var3);
         if (var6.field_175966_c[EnumFacing.SOUTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 3, 1, 0, 4, 2, 0);
         }

         if (var6.field_175966_c[EnumFacing.NORTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 3, 1, 7, 4, 2, 7);
         }

         if (var6.field_175966_c[EnumFacing.WEST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 0, 1, 3, 0, 2, 4);
         }

         if (var5.field_175966_c[EnumFacing.SOUTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 11, 1, 0, 12, 2, 0);
         }

         if (var5.field_175966_c[EnumFacing.NORTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 11, 1, 7, 12, 2, 7);
         }

         if (var5.field_175966_c[EnumFacing.EAST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 15, 1, 3, 15, 2, 4);
         }

         return true;
      }
   }

   public static class DoubleYRoom extends OceanMonumentPieces.Piece {
      public DoubleYRoom() {
         super();
      }

      public DoubleYRoom(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         super(1, var1, var2, 1, 2, 1);
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.field_175830_k.field_175967_a / 25 > 0) {
            this.func_175821_a(var1, var3, 0, 0, this.field_175830_k.field_175966_c[EnumFacing.DOWN.func_176745_a()]);
         }

         OceanMonumentPieces.RoomDefinition var5 = this.field_175830_k.field_175965_b[EnumFacing.UP.func_176745_a()];
         if (var5.field_175965_b[EnumFacing.UP.func_176745_a()] == null) {
            this.func_175819_a(var1, var3, 1, 8, 1, 6, 8, 6, field_175828_a);
         }

         this.func_175804_a(var1, var3, 0, 4, 0, 0, 4, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 7, 4, 0, 7, 4, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 4, 0, 6, 4, 0, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 4, 7, 6, 4, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 2, 4, 1, 2, 4, 2, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 4, 2, 1, 4, 2, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 4, 1, 5, 4, 2, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 6, 4, 2, 6, 4, 2, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 2, 4, 5, 2, 4, 6, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 4, 5, 1, 4, 5, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 4, 5, 5, 4, 6, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 6, 4, 5, 6, 4, 5, field_175826_b, field_175826_b, false);
         OceanMonumentPieces.RoomDefinition var6 = this.field_175830_k;

         for(int var7 = 1; var7 <= 5; var7 += 4) {
            byte var8 = 0;
            if (var6.field_175966_c[EnumFacing.SOUTH.func_176745_a()]) {
               this.func_175804_a(var1, var3, 2, var7, var8, 2, var7 + 2, var8, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 5, var7, var8, 5, var7 + 2, var8, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 3, var7 + 2, var8, 4, var7 + 2, var8, field_175826_b, field_175826_b, false);
            } else {
               this.func_175804_a(var1, var3, 0, var7, var8, 7, var7 + 2, var8, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 0, var7 + 1, var8, 7, var7 + 1, var8, field_175828_a, field_175828_a, false);
            }

            var8 = 7;
            if (var6.field_175966_c[EnumFacing.NORTH.func_176745_a()]) {
               this.func_175804_a(var1, var3, 2, var7, var8, 2, var7 + 2, var8, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 5, var7, var8, 5, var7 + 2, var8, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 3, var7 + 2, var8, 4, var7 + 2, var8, field_175826_b, field_175826_b, false);
            } else {
               this.func_175804_a(var1, var3, 0, var7, var8, 7, var7 + 2, var8, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 0, var7 + 1, var8, 7, var7 + 1, var8, field_175828_a, field_175828_a, false);
            }

            byte var9 = 0;
            if (var6.field_175966_c[EnumFacing.WEST.func_176745_a()]) {
               this.func_175804_a(var1, var3, var9, var7, 2, var9, var7 + 2, 2, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, var9, var7, 5, var9, var7 + 2, 5, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, var9, var7 + 2, 3, var9, var7 + 2, 4, field_175826_b, field_175826_b, false);
            } else {
               this.func_175804_a(var1, var3, var9, var7, 0, var9, var7 + 2, 7, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, var9, var7 + 1, 0, var9, var7 + 1, 7, field_175828_a, field_175828_a, false);
            }

            var9 = 7;
            if (var6.field_175966_c[EnumFacing.EAST.func_176745_a()]) {
               this.func_175804_a(var1, var3, var9, var7, 2, var9, var7 + 2, 2, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, var9, var7, 5, var9, var7 + 2, 5, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, var9, var7 + 2, 3, var9, var7 + 2, 4, field_175826_b, field_175826_b, false);
            } else {
               this.func_175804_a(var1, var3, var9, var7, 0, var9, var7 + 2, 7, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, var9, var7 + 1, 0, var9, var7 + 1, 7, field_175828_a, field_175828_a, false);
            }

            var6 = var5;
         }

         return true;
      }
   }

   public static class SimpleTopRoom extends OceanMonumentPieces.Piece {
      public SimpleTopRoom() {
         super();
      }

      public SimpleTopRoom(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         super(1, var1, var2, 1, 1, 1);
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.field_175830_k.field_175967_a / 25 > 0) {
            this.func_175821_a(var1, var3, 0, 0, this.field_175830_k.field_175966_c[EnumFacing.DOWN.func_176745_a()]);
         }

         if (this.field_175830_k.field_175965_b[EnumFacing.UP.func_176745_a()] == null) {
            this.func_175819_a(var1, var3, 1, 4, 1, 6, 4, 6, field_175828_a);
         }

         for(int var5 = 1; var5 <= 6; ++var5) {
            for(int var6 = 1; var6 <= 6; ++var6) {
               if (var2.nextInt(3) != 0) {
                  int var7 = 2 + (var2.nextInt(4) == 0 ? 0 : 1);
                  IBlockState var8 = Blocks.field_196577_ad.func_176223_P();
                  this.func_175804_a(var1, var3, var5, var7, var6, var5, 3, var6, var8, var8, false);
               }
            }
         }

         this.func_175804_a(var1, var3, 0, 1, 0, 0, 1, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 7, 1, 0, 7, 1, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 1, 0, 6, 1, 0, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 1, 7, 6, 1, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 0, 2, 0, 0, 2, 7, field_175827_c, field_175827_c, false);
         this.func_175804_a(var1, var3, 7, 2, 0, 7, 2, 7, field_175827_c, field_175827_c, false);
         this.func_175804_a(var1, var3, 1, 2, 0, 6, 2, 0, field_175827_c, field_175827_c, false);
         this.func_175804_a(var1, var3, 1, 2, 7, 6, 2, 7, field_175827_c, field_175827_c, false);
         this.func_175804_a(var1, var3, 0, 3, 0, 0, 3, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 7, 3, 0, 7, 3, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 3, 0, 6, 3, 0, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 3, 7, 6, 3, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 0, 1, 3, 0, 2, 4, field_175827_c, field_175827_c, false);
         this.func_175804_a(var1, var3, 7, 1, 3, 7, 2, 4, field_175827_c, field_175827_c, false);
         this.func_175804_a(var1, var3, 3, 1, 0, 4, 2, 0, field_175827_c, field_175827_c, false);
         this.func_175804_a(var1, var3, 3, 1, 7, 4, 2, 7, field_175827_c, field_175827_c, false);
         if (this.field_175830_k.field_175966_c[EnumFacing.SOUTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 3, 1, 0, 4, 2, 0);
         }

         return true;
      }
   }

   public static class SimpleRoom extends OceanMonumentPieces.Piece {
      private int field_175833_o;

      public SimpleRoom() {
         super();
      }

      public SimpleRoom(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         super(1, var1, var2, 1, 1, 1);
         this.field_175833_o = var3.nextInt(3);
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (this.field_175830_k.field_175967_a / 25 > 0) {
            this.func_175821_a(var1, var3, 0, 0, this.field_175830_k.field_175966_c[EnumFacing.DOWN.func_176745_a()]);
         }

         if (this.field_175830_k.field_175965_b[EnumFacing.UP.func_176745_a()] == null) {
            this.func_175819_a(var1, var3, 1, 4, 1, 6, 4, 6, field_175828_a);
         }

         boolean var5 = this.field_175833_o != 0 && var2.nextBoolean() && !this.field_175830_k.field_175966_c[EnumFacing.DOWN.func_176745_a()] && !this.field_175830_k.field_175966_c[EnumFacing.UP.func_176745_a()] && this.field_175830_k.func_175960_c() > 1;
         if (this.field_175833_o == 0) {
            this.func_175804_a(var1, var3, 0, 1, 0, 2, 1, 2, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 0, 3, 0, 2, 3, 2, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 0, 2, 0, 0, 2, 2, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 1, 2, 0, 2, 2, 0, field_175828_a, field_175828_a, false);
            this.func_175811_a(var1, field_175825_e, 1, 2, 1, var3);
            this.func_175804_a(var1, var3, 5, 1, 0, 7, 1, 2, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 5, 3, 0, 7, 3, 2, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 7, 2, 0, 7, 2, 2, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 5, 2, 0, 6, 2, 0, field_175828_a, field_175828_a, false);
            this.func_175811_a(var1, field_175825_e, 6, 2, 1, var3);
            this.func_175804_a(var1, var3, 0, 1, 5, 2, 1, 7, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 0, 3, 5, 2, 3, 7, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 0, 2, 5, 0, 2, 7, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 1, 2, 7, 2, 2, 7, field_175828_a, field_175828_a, false);
            this.func_175811_a(var1, field_175825_e, 1, 2, 6, var3);
            this.func_175804_a(var1, var3, 5, 1, 5, 7, 1, 7, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 5, 3, 5, 7, 3, 7, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 7, 2, 5, 7, 2, 7, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 5, 2, 7, 6, 2, 7, field_175828_a, field_175828_a, false);
            this.func_175811_a(var1, field_175825_e, 6, 2, 6, var3);
            if (this.field_175830_k.field_175966_c[EnumFacing.SOUTH.func_176745_a()]) {
               this.func_175804_a(var1, var3, 3, 3, 0, 4, 3, 0, field_175826_b, field_175826_b, false);
            } else {
               this.func_175804_a(var1, var3, 3, 3, 0, 4, 3, 1, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 3, 2, 0, 4, 2, 0, field_175828_a, field_175828_a, false);
               this.func_175804_a(var1, var3, 3, 1, 0, 4, 1, 1, field_175826_b, field_175826_b, false);
            }

            if (this.field_175830_k.field_175966_c[EnumFacing.NORTH.func_176745_a()]) {
               this.func_175804_a(var1, var3, 3, 3, 7, 4, 3, 7, field_175826_b, field_175826_b, false);
            } else {
               this.func_175804_a(var1, var3, 3, 3, 6, 4, 3, 7, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 3, 2, 7, 4, 2, 7, field_175828_a, field_175828_a, false);
               this.func_175804_a(var1, var3, 3, 1, 6, 4, 1, 7, field_175826_b, field_175826_b, false);
            }

            if (this.field_175830_k.field_175966_c[EnumFacing.WEST.func_176745_a()]) {
               this.func_175804_a(var1, var3, 0, 3, 3, 0, 3, 4, field_175826_b, field_175826_b, false);
            } else {
               this.func_175804_a(var1, var3, 0, 3, 3, 1, 3, 4, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 0, 2, 3, 0, 2, 4, field_175828_a, field_175828_a, false);
               this.func_175804_a(var1, var3, 0, 1, 3, 1, 1, 4, field_175826_b, field_175826_b, false);
            }

            if (this.field_175830_k.field_175966_c[EnumFacing.EAST.func_176745_a()]) {
               this.func_175804_a(var1, var3, 7, 3, 3, 7, 3, 4, field_175826_b, field_175826_b, false);
            } else {
               this.func_175804_a(var1, var3, 6, 3, 3, 7, 3, 4, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 7, 2, 3, 7, 2, 4, field_175828_a, field_175828_a, false);
               this.func_175804_a(var1, var3, 6, 1, 3, 7, 1, 4, field_175826_b, field_175826_b, false);
            }
         } else if (this.field_175833_o == 1) {
            this.func_175804_a(var1, var3, 2, 1, 2, 2, 3, 2, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 2, 1, 5, 2, 3, 5, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 5, 1, 5, 5, 3, 5, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 5, 1, 2, 5, 3, 2, field_175826_b, field_175826_b, false);
            this.func_175811_a(var1, field_175825_e, 2, 2, 2, var3);
            this.func_175811_a(var1, field_175825_e, 2, 2, 5, var3);
            this.func_175811_a(var1, field_175825_e, 5, 2, 5, var3);
            this.func_175811_a(var1, field_175825_e, 5, 2, 2, var3);
            this.func_175804_a(var1, var3, 0, 1, 0, 1, 3, 0, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 0, 1, 1, 0, 3, 1, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 0, 1, 7, 1, 3, 7, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 0, 1, 6, 0, 3, 6, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 6, 1, 7, 7, 3, 7, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 7, 1, 6, 7, 3, 6, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 6, 1, 0, 7, 3, 0, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 7, 1, 1, 7, 3, 1, field_175826_b, field_175826_b, false);
            this.func_175811_a(var1, field_175828_a, 1, 2, 0, var3);
            this.func_175811_a(var1, field_175828_a, 0, 2, 1, var3);
            this.func_175811_a(var1, field_175828_a, 1, 2, 7, var3);
            this.func_175811_a(var1, field_175828_a, 0, 2, 6, var3);
            this.func_175811_a(var1, field_175828_a, 6, 2, 7, var3);
            this.func_175811_a(var1, field_175828_a, 7, 2, 6, var3);
            this.func_175811_a(var1, field_175828_a, 6, 2, 0, var3);
            this.func_175811_a(var1, field_175828_a, 7, 2, 1, var3);
            if (!this.field_175830_k.field_175966_c[EnumFacing.SOUTH.func_176745_a()]) {
               this.func_175804_a(var1, var3, 1, 3, 0, 6, 3, 0, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 1, 2, 0, 6, 2, 0, field_175828_a, field_175828_a, false);
               this.func_175804_a(var1, var3, 1, 1, 0, 6, 1, 0, field_175826_b, field_175826_b, false);
            }

            if (!this.field_175830_k.field_175966_c[EnumFacing.NORTH.func_176745_a()]) {
               this.func_175804_a(var1, var3, 1, 3, 7, 6, 3, 7, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 1, 2, 7, 6, 2, 7, field_175828_a, field_175828_a, false);
               this.func_175804_a(var1, var3, 1, 1, 7, 6, 1, 7, field_175826_b, field_175826_b, false);
            }

            if (!this.field_175830_k.field_175966_c[EnumFacing.WEST.func_176745_a()]) {
               this.func_175804_a(var1, var3, 0, 3, 1, 0, 3, 6, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 0, 2, 1, 0, 2, 6, field_175828_a, field_175828_a, false);
               this.func_175804_a(var1, var3, 0, 1, 1, 0, 1, 6, field_175826_b, field_175826_b, false);
            }

            if (!this.field_175830_k.field_175966_c[EnumFacing.EAST.func_176745_a()]) {
               this.func_175804_a(var1, var3, 7, 3, 1, 7, 3, 6, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 7, 2, 1, 7, 2, 6, field_175828_a, field_175828_a, false);
               this.func_175804_a(var1, var3, 7, 1, 1, 7, 1, 6, field_175826_b, field_175826_b, false);
            }
         } else if (this.field_175833_o == 2) {
            this.func_175804_a(var1, var3, 0, 1, 0, 0, 1, 7, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 7, 1, 0, 7, 1, 7, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 1, 1, 0, 6, 1, 0, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 1, 1, 7, 6, 1, 7, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 0, 2, 0, 0, 2, 7, field_175827_c, field_175827_c, false);
            this.func_175804_a(var1, var3, 7, 2, 0, 7, 2, 7, field_175827_c, field_175827_c, false);
            this.func_175804_a(var1, var3, 1, 2, 0, 6, 2, 0, field_175827_c, field_175827_c, false);
            this.func_175804_a(var1, var3, 1, 2, 7, 6, 2, 7, field_175827_c, field_175827_c, false);
            this.func_175804_a(var1, var3, 0, 3, 0, 0, 3, 7, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 7, 3, 0, 7, 3, 7, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 1, 3, 0, 6, 3, 0, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 1, 3, 7, 6, 3, 7, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 0, 1, 3, 0, 2, 4, field_175827_c, field_175827_c, false);
            this.func_175804_a(var1, var3, 7, 1, 3, 7, 2, 4, field_175827_c, field_175827_c, false);
            this.func_175804_a(var1, var3, 3, 1, 0, 4, 2, 0, field_175827_c, field_175827_c, false);
            this.func_175804_a(var1, var3, 3, 1, 7, 4, 2, 7, field_175827_c, field_175827_c, false);
            if (this.field_175830_k.field_175966_c[EnumFacing.SOUTH.func_176745_a()]) {
               this.func_209179_a(var1, var3, 3, 1, 0, 4, 2, 0);
            }

            if (this.field_175830_k.field_175966_c[EnumFacing.NORTH.func_176745_a()]) {
               this.func_209179_a(var1, var3, 3, 1, 7, 4, 2, 7);
            }

            if (this.field_175830_k.field_175966_c[EnumFacing.WEST.func_176745_a()]) {
               this.func_209179_a(var1, var3, 0, 1, 3, 0, 2, 4);
            }

            if (this.field_175830_k.field_175966_c[EnumFacing.EAST.func_176745_a()]) {
               this.func_209179_a(var1, var3, 7, 1, 3, 7, 2, 4);
            }
         }

         if (var5) {
            this.func_175804_a(var1, var3, 3, 1, 3, 4, 1, 4, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 3, 2, 3, 4, 2, 4, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 3, 3, 3, 4, 3, 4, field_175826_b, field_175826_b, false);
         }

         return true;
      }
   }

   public static class EntryRoom extends OceanMonumentPieces.Piece {
      public EntryRoom() {
         super();
      }

      public EntryRoom(EnumFacing var1, OceanMonumentPieces.RoomDefinition var2) {
         super(1, var1, var2, 1, 1, 1);
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.func_175804_a(var1, var3, 0, 3, 0, 2, 3, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 3, 0, 7, 3, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 0, 2, 0, 1, 2, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 6, 2, 0, 7, 2, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 0, 1, 0, 0, 1, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 7, 1, 0, 7, 1, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 0, 1, 7, 7, 3, 7, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 1, 1, 0, 2, 3, 0, field_175826_b, field_175826_b, false);
         this.func_175804_a(var1, var3, 5, 1, 0, 6, 3, 0, field_175826_b, field_175826_b, false);
         if (this.field_175830_k.field_175966_c[EnumFacing.NORTH.func_176745_a()]) {
            this.func_209179_a(var1, var3, 3, 1, 7, 4, 2, 7);
         }

         if (this.field_175830_k.field_175966_c[EnumFacing.WEST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 0, 1, 3, 1, 2, 4);
         }

         if (this.field_175830_k.field_175966_c[EnumFacing.EAST.func_176745_a()]) {
            this.func_209179_a(var1, var3, 6, 1, 3, 7, 2, 4);
         }

         return true;
      }
   }

   public static class MonumentBuilding extends OceanMonumentPieces.Piece {
      private OceanMonumentPieces.RoomDefinition field_175845_o;
      private OceanMonumentPieces.RoomDefinition field_175844_p;
      private final List<OceanMonumentPieces.Piece> field_175843_q = Lists.newArrayList();

      public MonumentBuilding() {
         super();
      }

      public MonumentBuilding(Random var1, int var2, int var3, EnumFacing var4) {
         super(0);
         this.func_186164_a(var4);
         EnumFacing var5 = this.func_186165_e();
         if (var5.func_176740_k() == EnumFacing.Axis.Z) {
            this.field_74887_e = new MutableBoundingBox(var2, 39, var3, var2 + 58 - 1, 61, var3 + 58 - 1);
         } else {
            this.field_74887_e = new MutableBoundingBox(var2, 39, var3, var2 + 58 - 1, 61, var3 + 58 - 1);
         }

         List var6 = this.func_175836_a(var1);
         this.field_175845_o.field_175963_d = true;
         this.field_175843_q.add(new OceanMonumentPieces.EntryRoom(var5, this.field_175845_o));
         this.field_175843_q.add(new OceanMonumentPieces.MonumentCoreRoom(var5, this.field_175844_p, var1));
         ArrayList var7 = Lists.newArrayList();
         var7.add(new OceanMonumentPieces.XYDoubleRoomFitHelper());
         var7.add(new OceanMonumentPieces.YZDoubleRoomFitHelper());
         var7.add(new OceanMonumentPieces.ZDoubleRoomFitHelper());
         var7.add(new OceanMonumentPieces.XDoubleRoomFitHelper());
         var7.add(new OceanMonumentPieces.YDoubleRoomFitHelper());
         var7.add(new OceanMonumentPieces.FitSimpleRoomTopHelper());
         var7.add(new OceanMonumentPieces.FitSimpleRoomHelper());
         Iterator var8 = var6.iterator();

         while(true) {
            while(true) {
               OceanMonumentPieces.RoomDefinition var9;
               do {
                  do {
                     if (!var8.hasNext()) {
                        int var15 = this.field_74887_e.field_78895_b;
                        int var16 = this.func_74865_a(9, 22);
                        int var17 = this.func_74873_b(9, 22);
                        Iterator var18 = this.field_175843_q.iterator();

                        while(var18.hasNext()) {
                           OceanMonumentPieces.Piece var12 = (OceanMonumentPieces.Piece)var18.next();
                           var12.func_74874_b().func_78886_a(var16, var15, var17);
                        }

                        MutableBoundingBox var19 = MutableBoundingBox.func_175899_a(this.func_74865_a(1, 1), this.func_74862_a(1), this.func_74873_b(1, 1), this.func_74865_a(23, 21), this.func_74862_a(8), this.func_74873_b(23, 21));
                        MutableBoundingBox var20 = MutableBoundingBox.func_175899_a(this.func_74865_a(34, 1), this.func_74862_a(1), this.func_74873_b(34, 1), this.func_74865_a(56, 21), this.func_74862_a(8), this.func_74873_b(56, 21));
                        MutableBoundingBox var13 = MutableBoundingBox.func_175899_a(this.func_74865_a(22, 22), this.func_74862_a(13), this.func_74873_b(22, 22), this.func_74865_a(35, 35), this.func_74862_a(17), this.func_74873_b(35, 35));
                        int var14 = var1.nextInt();
                        this.field_175843_q.add(new OceanMonumentPieces.WingRoom(var5, var19, var14++));
                        this.field_175843_q.add(new OceanMonumentPieces.WingRoom(var5, var20, var14++));
                        this.field_175843_q.add(new OceanMonumentPieces.Penthouse(var5, var13));
                        return;
                     }

                     var9 = (OceanMonumentPieces.RoomDefinition)var8.next();
                  } while(var9.field_175963_d);
               } while(var9.func_175961_b());

               Iterator var10 = var7.iterator();

               while(var10.hasNext()) {
                  OceanMonumentPieces.MonumentRoomFitHelper var11 = (OceanMonumentPieces.MonumentRoomFitHelper)var10.next();
                  if (var11.func_175969_a(var9)) {
                     this.field_175843_q.add(var11.func_175968_a(var5, var9, var1));
                     break;
                  }
               }
            }
         }
      }

      private List<OceanMonumentPieces.RoomDefinition> func_175836_a(Random var1) {
         OceanMonumentPieces.RoomDefinition[] var2 = new OceanMonumentPieces.RoomDefinition[75];

         int var3;
         int var4;
         boolean var5;
         int var6;
         for(var3 = 0; var3 < 5; ++var3) {
            for(var4 = 0; var4 < 4; ++var4) {
               var5 = false;
               var6 = func_175820_a(var3, 0, var4);
               var2[var6] = new OceanMonumentPieces.RoomDefinition(var6);
            }
         }

         for(var3 = 0; var3 < 5; ++var3) {
            for(var4 = 0; var4 < 4; ++var4) {
               var5 = true;
               var6 = func_175820_a(var3, 1, var4);
               var2[var6] = new OceanMonumentPieces.RoomDefinition(var6);
            }
         }

         for(var3 = 1; var3 < 4; ++var3) {
            for(var4 = 0; var4 < 2; ++var4) {
               var5 = true;
               var6 = func_175820_a(var3, 2, var4);
               var2[var6] = new OceanMonumentPieces.RoomDefinition(var6);
            }
         }

         this.field_175845_o = var2[field_175823_g];

         int var8;
         int var9;
         int var11;
         int var12;
         int var13;
         for(var3 = 0; var3 < 5; ++var3) {
            for(var4 = 0; var4 < 5; ++var4) {
               for(int var16 = 0; var16 < 3; ++var16) {
                  var6 = func_175820_a(var3, var16, var4);
                  if (var2[var6] != null) {
                     EnumFacing[] var7 = EnumFacing.values();
                     var8 = var7.length;

                     for(var9 = 0; var9 < var8; ++var9) {
                        EnumFacing var10 = var7[var9];
                        var11 = var3 + var10.func_82601_c();
                        var12 = var16 + var10.func_96559_d();
                        var13 = var4 + var10.func_82599_e();
                        if (var11 >= 0 && var11 < 5 && var13 >= 0 && var13 < 5 && var12 >= 0 && var12 < 3) {
                           int var14 = func_175820_a(var11, var12, var13);
                           if (var2[var14] != null) {
                              if (var13 == var4) {
                                 var2[var6].func_175957_a(var10, var2[var14]);
                              } else {
                                 var2[var6].func_175957_a(var10.func_176734_d(), var2[var14]);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         OceanMonumentPieces.RoomDefinition var15 = new OceanMonumentPieces.RoomDefinition(1003);
         OceanMonumentPieces.RoomDefinition var17 = new OceanMonumentPieces.RoomDefinition(1001);
         OceanMonumentPieces.RoomDefinition var18 = new OceanMonumentPieces.RoomDefinition(1002);
         var2[field_175831_h].func_175957_a(EnumFacing.UP, var15);
         var2[field_175832_i].func_175957_a(EnumFacing.SOUTH, var17);
         var2[field_175829_j].func_175957_a(EnumFacing.SOUTH, var18);
         var15.field_175963_d = true;
         var17.field_175963_d = true;
         var18.field_175963_d = true;
         this.field_175845_o.field_175964_e = true;
         this.field_175844_p = var2[func_175820_a(var1.nextInt(4), 0, 2)];
         this.field_175844_p.field_175963_d = true;
         this.field_175844_p.field_175965_b[EnumFacing.EAST.func_176745_a()].field_175963_d = true;
         this.field_175844_p.field_175965_b[EnumFacing.NORTH.func_176745_a()].field_175963_d = true;
         this.field_175844_p.field_175965_b[EnumFacing.EAST.func_176745_a()].field_175965_b[EnumFacing.NORTH.func_176745_a()].field_175963_d = true;
         this.field_175844_p.field_175965_b[EnumFacing.UP.func_176745_a()].field_175963_d = true;
         this.field_175844_p.field_175965_b[EnumFacing.EAST.func_176745_a()].field_175965_b[EnumFacing.UP.func_176745_a()].field_175963_d = true;
         this.field_175844_p.field_175965_b[EnumFacing.NORTH.func_176745_a()].field_175965_b[EnumFacing.UP.func_176745_a()].field_175963_d = true;
         this.field_175844_p.field_175965_b[EnumFacing.EAST.func_176745_a()].field_175965_b[EnumFacing.NORTH.func_176745_a()].field_175965_b[EnumFacing.UP.func_176745_a()].field_175963_d = true;
         ArrayList var19 = Lists.newArrayList();
         OceanMonumentPieces.RoomDefinition[] var20 = var2;
         var8 = var2.length;

         for(var9 = 0; var9 < var8; ++var9) {
            OceanMonumentPieces.RoomDefinition var23 = var20[var9];
            if (var23 != null) {
               var23.func_175958_a();
               var19.add(var23);
            }
         }

         var15.func_175958_a();
         Collections.shuffle(var19, var1);
         int var21 = 1;
         Iterator var22 = var19.iterator();

         label95:
         while(var22.hasNext()) {
            OceanMonumentPieces.RoomDefinition var24 = (OceanMonumentPieces.RoomDefinition)var22.next();
            int var25 = 0;
            var11 = 0;

            while(true) {
               while(true) {
                  do {
                     if (var25 >= 2 || var11 >= 5) {
                        continue label95;
                     }

                     ++var11;
                     var12 = var1.nextInt(6);
                  } while(!var24.field_175966_c[var12]);

                  var13 = EnumFacing.func_82600_a(var12).func_176734_d().func_176745_a();
                  var24.field_175966_c[var12] = false;
                  var24.field_175965_b[var12].field_175966_c[var13] = false;
                  if (var24.func_175959_a(var21++) && var24.field_175965_b[var12].func_175959_a(var21++)) {
                     ++var25;
                  } else {
                     var24.field_175966_c[var12] = true;
                     var24.field_175965_b[var12].field_175966_c[var13] = true;
                  }
               }
            }
         }

         var19.add(var15);
         var19.add(var17);
         var19.add(var18);
         return var19;
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         int var5 = Math.max(var1.func_181545_F(), 64) - this.field_74887_e.field_78895_b;
         this.func_209179_a(var1, var3, 0, 0, 0, 58, var5, 58);
         this.func_175840_a(false, 0, var1, var2, var3);
         this.func_175840_a(true, 33, var1, var2, var3);
         this.func_175839_b(var1, var2, var3);
         this.func_175837_c(var1, var2, var3);
         this.func_175841_d(var1, var2, var3);
         this.func_175835_e(var1, var2, var3);
         this.func_175842_f(var1, var2, var3);
         this.func_175838_g(var1, var2, var3);

         int var6;
         label72:
         for(var6 = 0; var6 < 7; ++var6) {
            int var7 = 0;

            while(true) {
               while(true) {
                  if (var7 >= 7) {
                     continue label72;
                  }

                  if (var7 == 0 && var6 == 3) {
                     var7 = 6;
                  }

                  int var8 = var6 * 9;
                  int var9 = var7 * 9;

                  for(int var10 = 0; var10 < 4; ++var10) {
                     for(int var11 = 0; var11 < 4; ++var11) {
                        this.func_175811_a(var1, field_175826_b, var8 + var10, 0, var9 + var11, var3);
                        this.func_175808_b(var1, field_175826_b, var8 + var10, -1, var9 + var11, var3);
                     }
                  }

                  if (var6 != 0 && var6 != 6) {
                     var7 += 6;
                  } else {
                     ++var7;
                  }
               }
            }
         }

         for(var6 = 0; var6 < 5; ++var6) {
            this.func_209179_a(var1, var3, -1 - var6, 0 + var6 * 2, -1 - var6, -1 - var6, 23, 58 + var6);
            this.func_209179_a(var1, var3, 58 + var6, 0 + var6 * 2, -1 - var6, 58 + var6, 23, 58 + var6);
            this.func_209179_a(var1, var3, 0 - var6, 0 + var6 * 2, -1 - var6, 57 + var6, 23, -1 - var6);
            this.func_209179_a(var1, var3, 0 - var6, 0 + var6 * 2, 58 + var6, 57 + var6, 23, 58 + var6);
         }

         Iterator var12 = this.field_175843_q.iterator();

         while(var12.hasNext()) {
            OceanMonumentPieces.Piece var13 = (OceanMonumentPieces.Piece)var12.next();
            if (var13.func_74874_b().func_78884_a(var3)) {
               var13.func_74875_a(var1, var2, var3, var4);
            }
         }

         return true;
      }

      private void func_175840_a(boolean var1, int var2, IWorld var3, Random var4, MutableBoundingBox var5) {
         boolean var6 = true;
         if (this.func_175818_a(var5, var2, 0, var2 + 23, 20)) {
            this.func_175804_a(var3, var5, var2 + 0, 0, 0, var2 + 24, 0, 20, field_175828_a, field_175828_a, false);
            this.func_209179_a(var3, var5, var2 + 0, 1, 0, var2 + 24, 10, 20);

            int var7;
            for(var7 = 0; var7 < 4; ++var7) {
               this.func_175804_a(var3, var5, var2 + var7, var7 + 1, var7, var2 + var7, var7 + 1, 20, field_175826_b, field_175826_b, false);
               this.func_175804_a(var3, var5, var2 + var7 + 7, var7 + 5, var7 + 7, var2 + var7 + 7, var7 + 5, 20, field_175826_b, field_175826_b, false);
               this.func_175804_a(var3, var5, var2 + 17 - var7, var7 + 5, var7 + 7, var2 + 17 - var7, var7 + 5, 20, field_175826_b, field_175826_b, false);
               this.func_175804_a(var3, var5, var2 + 24 - var7, var7 + 1, var7, var2 + 24 - var7, var7 + 1, 20, field_175826_b, field_175826_b, false);
               this.func_175804_a(var3, var5, var2 + var7 + 1, var7 + 1, var7, var2 + 23 - var7, var7 + 1, var7, field_175826_b, field_175826_b, false);
               this.func_175804_a(var3, var5, var2 + var7 + 8, var7 + 5, var7 + 7, var2 + 16 - var7, var7 + 5, var7 + 7, field_175826_b, field_175826_b, false);
            }

            this.func_175804_a(var3, var5, var2 + 4, 4, 4, var2 + 6, 4, 20, field_175828_a, field_175828_a, false);
            this.func_175804_a(var3, var5, var2 + 7, 4, 4, var2 + 17, 4, 6, field_175828_a, field_175828_a, false);
            this.func_175804_a(var3, var5, var2 + 18, 4, 4, var2 + 20, 4, 20, field_175828_a, field_175828_a, false);
            this.func_175804_a(var3, var5, var2 + 11, 8, 11, var2 + 13, 8, 20, field_175828_a, field_175828_a, false);
            this.func_175811_a(var3, field_175824_d, var2 + 12, 9, 12, var5);
            this.func_175811_a(var3, field_175824_d, var2 + 12, 9, 15, var5);
            this.func_175811_a(var3, field_175824_d, var2 + 12, 9, 18, var5);
            var7 = var2 + (var1 ? 19 : 5);
            int var8 = var2 + (var1 ? 5 : 19);

            int var9;
            for(var9 = 20; var9 >= 5; var9 -= 3) {
               this.func_175811_a(var3, field_175824_d, var7, 5, var9, var5);
            }

            for(var9 = 19; var9 >= 7; var9 -= 3) {
               this.func_175811_a(var3, field_175824_d, var8, 5, var9, var5);
            }

            for(var9 = 0; var9 < 4; ++var9) {
               int var10 = var1 ? var2 + 24 - (17 - var9 * 3) : var2 + 17 - var9 * 3;
               this.func_175811_a(var3, field_175824_d, var10, 5, 5, var5);
            }

            this.func_175811_a(var3, field_175824_d, var8, 5, 5, var5);
            this.func_175804_a(var3, var5, var2 + 11, 1, 12, var2 + 13, 7, 12, field_175828_a, field_175828_a, false);
            this.func_175804_a(var3, var5, var2 + 12, 1, 11, var2 + 12, 7, 13, field_175828_a, field_175828_a, false);
         }

      }

      private void func_175839_b(IWorld var1, Random var2, MutableBoundingBox var3) {
         if (this.func_175818_a(var3, 22, 5, 35, 17)) {
            this.func_209179_a(var1, var3, 25, 0, 0, 32, 8, 20);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.func_175804_a(var1, var3, 24, 2, 5 + var4 * 4, 24, 4, 5 + var4 * 4, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 22, 4, 5 + var4 * 4, 23, 4, 5 + var4 * 4, field_175826_b, field_175826_b, false);
               this.func_175811_a(var1, field_175826_b, 25, 5, 5 + var4 * 4, var3);
               this.func_175811_a(var1, field_175826_b, 26, 6, 5 + var4 * 4, var3);
               this.func_175811_a(var1, field_175825_e, 26, 5, 5 + var4 * 4, var3);
               this.func_175804_a(var1, var3, 33, 2, 5 + var4 * 4, 33, 4, 5 + var4 * 4, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 34, 4, 5 + var4 * 4, 35, 4, 5 + var4 * 4, field_175826_b, field_175826_b, false);
               this.func_175811_a(var1, field_175826_b, 32, 5, 5 + var4 * 4, var3);
               this.func_175811_a(var1, field_175826_b, 31, 6, 5 + var4 * 4, var3);
               this.func_175811_a(var1, field_175825_e, 31, 5, 5 + var4 * 4, var3);
               this.func_175804_a(var1, var3, 27, 6, 5 + var4 * 4, 30, 6, 5 + var4 * 4, field_175828_a, field_175828_a, false);
            }
         }

      }

      private void func_175837_c(IWorld var1, Random var2, MutableBoundingBox var3) {
         if (this.func_175818_a(var3, 15, 20, 42, 21)) {
            this.func_175804_a(var1, var3, 15, 0, 21, 42, 0, 21, field_175828_a, field_175828_a, false);
            this.func_209179_a(var1, var3, 26, 1, 21, 31, 3, 21);
            this.func_175804_a(var1, var3, 21, 12, 21, 36, 12, 21, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 17, 11, 21, 40, 11, 21, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 16, 10, 21, 41, 10, 21, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 15, 7, 21, 42, 9, 21, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 16, 6, 21, 41, 6, 21, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 17, 5, 21, 40, 5, 21, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 21, 4, 21, 36, 4, 21, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 22, 3, 21, 26, 3, 21, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 31, 3, 21, 35, 3, 21, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 23, 2, 21, 25, 2, 21, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 32, 2, 21, 34, 2, 21, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 28, 4, 20, 29, 4, 21, field_175826_b, field_175826_b, false);
            this.func_175811_a(var1, field_175826_b, 27, 3, 21, var3);
            this.func_175811_a(var1, field_175826_b, 30, 3, 21, var3);
            this.func_175811_a(var1, field_175826_b, 26, 2, 21, var3);
            this.func_175811_a(var1, field_175826_b, 31, 2, 21, var3);
            this.func_175811_a(var1, field_175826_b, 25, 1, 21, var3);
            this.func_175811_a(var1, field_175826_b, 32, 1, 21, var3);

            int var4;
            for(var4 = 0; var4 < 7; ++var4) {
               this.func_175811_a(var1, field_175827_c, 28 - var4, 6 + var4, 21, var3);
               this.func_175811_a(var1, field_175827_c, 29 + var4, 6 + var4, 21, var3);
            }

            for(var4 = 0; var4 < 4; ++var4) {
               this.func_175811_a(var1, field_175827_c, 28 - var4, 9 + var4, 21, var3);
               this.func_175811_a(var1, field_175827_c, 29 + var4, 9 + var4, 21, var3);
            }

            this.func_175811_a(var1, field_175827_c, 28, 12, 21, var3);
            this.func_175811_a(var1, field_175827_c, 29, 12, 21, var3);

            for(var4 = 0; var4 < 3; ++var4) {
               this.func_175811_a(var1, field_175827_c, 22 - var4 * 2, 8, 21, var3);
               this.func_175811_a(var1, field_175827_c, 22 - var4 * 2, 9, 21, var3);
               this.func_175811_a(var1, field_175827_c, 35 + var4 * 2, 8, 21, var3);
               this.func_175811_a(var1, field_175827_c, 35 + var4 * 2, 9, 21, var3);
            }

            this.func_209179_a(var1, var3, 15, 13, 21, 42, 15, 21);
            this.func_209179_a(var1, var3, 15, 1, 21, 15, 6, 21);
            this.func_209179_a(var1, var3, 16, 1, 21, 16, 5, 21);
            this.func_209179_a(var1, var3, 17, 1, 21, 20, 4, 21);
            this.func_209179_a(var1, var3, 21, 1, 21, 21, 3, 21);
            this.func_209179_a(var1, var3, 22, 1, 21, 22, 2, 21);
            this.func_209179_a(var1, var3, 23, 1, 21, 24, 1, 21);
            this.func_209179_a(var1, var3, 42, 1, 21, 42, 6, 21);
            this.func_209179_a(var1, var3, 41, 1, 21, 41, 5, 21);
            this.func_209179_a(var1, var3, 37, 1, 21, 40, 4, 21);
            this.func_209179_a(var1, var3, 36, 1, 21, 36, 3, 21);
            this.func_209179_a(var1, var3, 33, 1, 21, 34, 1, 21);
            this.func_209179_a(var1, var3, 35, 1, 21, 35, 2, 21);
         }

      }

      private void func_175841_d(IWorld var1, Random var2, MutableBoundingBox var3) {
         if (this.func_175818_a(var3, 21, 21, 36, 36)) {
            this.func_175804_a(var1, var3, 21, 0, 22, 36, 0, 36, field_175828_a, field_175828_a, false);
            this.func_209179_a(var1, var3, 21, 1, 22, 36, 23, 36);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.func_175804_a(var1, var3, 21 + var4, 13 + var4, 21 + var4, 36 - var4, 13 + var4, 21 + var4, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 21 + var4, 13 + var4, 36 - var4, 36 - var4, 13 + var4, 36 - var4, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 21 + var4, 13 + var4, 22 + var4, 21 + var4, 13 + var4, 35 - var4, field_175826_b, field_175826_b, false);
               this.func_175804_a(var1, var3, 36 - var4, 13 + var4, 22 + var4, 36 - var4, 13 + var4, 35 - var4, field_175826_b, field_175826_b, false);
            }

            this.func_175804_a(var1, var3, 25, 16, 25, 32, 16, 32, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 25, 17, 25, 25, 19, 25, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 32, 17, 25, 32, 19, 25, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 25, 17, 32, 25, 19, 32, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 32, 17, 32, 32, 19, 32, field_175826_b, field_175826_b, false);
            this.func_175811_a(var1, field_175826_b, 26, 20, 26, var3);
            this.func_175811_a(var1, field_175826_b, 27, 21, 27, var3);
            this.func_175811_a(var1, field_175825_e, 27, 20, 27, var3);
            this.func_175811_a(var1, field_175826_b, 26, 20, 31, var3);
            this.func_175811_a(var1, field_175826_b, 27, 21, 30, var3);
            this.func_175811_a(var1, field_175825_e, 27, 20, 30, var3);
            this.func_175811_a(var1, field_175826_b, 31, 20, 31, var3);
            this.func_175811_a(var1, field_175826_b, 30, 21, 30, var3);
            this.func_175811_a(var1, field_175825_e, 30, 20, 30, var3);
            this.func_175811_a(var1, field_175826_b, 31, 20, 26, var3);
            this.func_175811_a(var1, field_175826_b, 30, 21, 27, var3);
            this.func_175811_a(var1, field_175825_e, 30, 20, 27, var3);
            this.func_175804_a(var1, var3, 28, 21, 27, 29, 21, 27, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 27, 21, 28, 27, 21, 29, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 28, 21, 30, 29, 21, 30, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 30, 21, 28, 30, 21, 29, field_175828_a, field_175828_a, false);
         }

      }

      private void func_175835_e(IWorld var1, Random var2, MutableBoundingBox var3) {
         int var4;
         if (this.func_175818_a(var3, 0, 21, 6, 58)) {
            this.func_175804_a(var1, var3, 0, 0, 21, 6, 0, 57, field_175828_a, field_175828_a, false);
            this.func_209179_a(var1, var3, 0, 1, 21, 6, 7, 57);
            this.func_175804_a(var1, var3, 4, 4, 21, 6, 4, 53, field_175828_a, field_175828_a, false);

            for(var4 = 0; var4 < 4; ++var4) {
               this.func_175804_a(var1, var3, var4, var4 + 1, 21, var4, var4 + 1, 57 - var4, field_175826_b, field_175826_b, false);
            }

            for(var4 = 23; var4 < 53; var4 += 3) {
               this.func_175811_a(var1, field_175824_d, 5, 5, var4, var3);
            }

            this.func_175811_a(var1, field_175824_d, 5, 5, 52, var3);

            for(var4 = 0; var4 < 4; ++var4) {
               this.func_175804_a(var1, var3, var4, var4 + 1, 21, var4, var4 + 1, 57 - var4, field_175826_b, field_175826_b, false);
            }

            this.func_175804_a(var1, var3, 4, 1, 52, 6, 3, 52, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 5, 1, 51, 5, 3, 53, field_175828_a, field_175828_a, false);
         }

         if (this.func_175818_a(var3, 51, 21, 58, 58)) {
            this.func_175804_a(var1, var3, 51, 0, 21, 57, 0, 57, field_175828_a, field_175828_a, false);
            this.func_209179_a(var1, var3, 51, 1, 21, 57, 7, 57);
            this.func_175804_a(var1, var3, 51, 4, 21, 53, 4, 53, field_175828_a, field_175828_a, false);

            for(var4 = 0; var4 < 4; ++var4) {
               this.func_175804_a(var1, var3, 57 - var4, var4 + 1, 21, 57 - var4, var4 + 1, 57 - var4, field_175826_b, field_175826_b, false);
            }

            for(var4 = 23; var4 < 53; var4 += 3) {
               this.func_175811_a(var1, field_175824_d, 52, 5, var4, var3);
            }

            this.func_175811_a(var1, field_175824_d, 52, 5, 52, var3);
            this.func_175804_a(var1, var3, 51, 1, 52, 53, 3, 52, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 52, 1, 51, 52, 3, 53, field_175828_a, field_175828_a, false);
         }

         if (this.func_175818_a(var3, 0, 51, 57, 57)) {
            this.func_175804_a(var1, var3, 7, 0, 51, 50, 0, 57, field_175828_a, field_175828_a, false);
            this.func_209179_a(var1, var3, 7, 1, 51, 50, 10, 57);

            for(var4 = 0; var4 < 4; ++var4) {
               this.func_175804_a(var1, var3, var4 + 1, var4 + 1, 57 - var4, 56 - var4, var4 + 1, 57 - var4, field_175826_b, field_175826_b, false);
            }
         }

      }

      private void func_175842_f(IWorld var1, Random var2, MutableBoundingBox var3) {
         int var4;
         if (this.func_175818_a(var3, 7, 21, 13, 50)) {
            this.func_175804_a(var1, var3, 7, 0, 21, 13, 0, 50, field_175828_a, field_175828_a, false);
            this.func_209179_a(var1, var3, 7, 1, 21, 13, 10, 50);
            this.func_175804_a(var1, var3, 11, 8, 21, 13, 8, 53, field_175828_a, field_175828_a, false);

            for(var4 = 0; var4 < 4; ++var4) {
               this.func_175804_a(var1, var3, var4 + 7, var4 + 5, 21, var4 + 7, var4 + 5, 54, field_175826_b, field_175826_b, false);
            }

            for(var4 = 21; var4 <= 45; var4 += 3) {
               this.func_175811_a(var1, field_175824_d, 12, 9, var4, var3);
            }
         }

         if (this.func_175818_a(var3, 44, 21, 50, 54)) {
            this.func_175804_a(var1, var3, 44, 0, 21, 50, 0, 50, field_175828_a, field_175828_a, false);
            this.func_209179_a(var1, var3, 44, 1, 21, 50, 10, 50);
            this.func_175804_a(var1, var3, 44, 8, 21, 46, 8, 53, field_175828_a, field_175828_a, false);

            for(var4 = 0; var4 < 4; ++var4) {
               this.func_175804_a(var1, var3, 50 - var4, var4 + 5, 21, 50 - var4, var4 + 5, 54, field_175826_b, field_175826_b, false);
            }

            for(var4 = 21; var4 <= 45; var4 += 3) {
               this.func_175811_a(var1, field_175824_d, 45, 9, var4, var3);
            }
         }

         if (this.func_175818_a(var3, 8, 44, 49, 54)) {
            this.func_175804_a(var1, var3, 14, 0, 44, 43, 0, 50, field_175828_a, field_175828_a, false);
            this.func_209179_a(var1, var3, 14, 1, 44, 43, 10, 50);

            for(var4 = 12; var4 <= 45; var4 += 3) {
               this.func_175811_a(var1, field_175824_d, var4, 9, 45, var3);
               this.func_175811_a(var1, field_175824_d, var4, 9, 52, var3);
               if (var4 == 12 || var4 == 18 || var4 == 24 || var4 == 33 || var4 == 39 || var4 == 45) {
                  this.func_175811_a(var1, field_175824_d, var4, 9, 47, var3);
                  this.func_175811_a(var1, field_175824_d, var4, 9, 50, var3);
                  this.func_175811_a(var1, field_175824_d, var4, 10, 45, var3);
                  this.func_175811_a(var1, field_175824_d, var4, 10, 46, var3);
                  this.func_175811_a(var1, field_175824_d, var4, 10, 51, var3);
                  this.func_175811_a(var1, field_175824_d, var4, 10, 52, var3);
                  this.func_175811_a(var1, field_175824_d, var4, 11, 47, var3);
                  this.func_175811_a(var1, field_175824_d, var4, 11, 50, var3);
                  this.func_175811_a(var1, field_175824_d, var4, 12, 48, var3);
                  this.func_175811_a(var1, field_175824_d, var4, 12, 49, var3);
               }
            }

            for(var4 = 0; var4 < 3; ++var4) {
               this.func_175804_a(var1, var3, 8 + var4, 5 + var4, 54, 49 - var4, 5 + var4, 54, field_175828_a, field_175828_a, false);
            }

            this.func_175804_a(var1, var3, 11, 8, 54, 46, 8, 54, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var3, 14, 8, 44, 43, 8, 53, field_175828_a, field_175828_a, false);
         }

      }

      private void func_175838_g(IWorld var1, Random var2, MutableBoundingBox var3) {
         int var4;
         if (this.func_175818_a(var3, 14, 21, 20, 43)) {
            this.func_175804_a(var1, var3, 14, 0, 21, 20, 0, 43, field_175828_a, field_175828_a, false);
            this.func_209179_a(var1, var3, 14, 1, 22, 20, 14, 43);
            this.func_175804_a(var1, var3, 18, 12, 22, 20, 12, 39, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 18, 12, 21, 20, 12, 21, field_175826_b, field_175826_b, false);

            for(var4 = 0; var4 < 4; ++var4) {
               this.func_175804_a(var1, var3, var4 + 14, var4 + 9, 21, var4 + 14, var4 + 9, 43 - var4, field_175826_b, field_175826_b, false);
            }

            for(var4 = 23; var4 <= 39; var4 += 3) {
               this.func_175811_a(var1, field_175824_d, 19, 13, var4, var3);
            }
         }

         if (this.func_175818_a(var3, 37, 21, 43, 43)) {
            this.func_175804_a(var1, var3, 37, 0, 21, 43, 0, 43, field_175828_a, field_175828_a, false);
            this.func_209179_a(var1, var3, 37, 1, 22, 43, 14, 43);
            this.func_175804_a(var1, var3, 37, 12, 22, 39, 12, 39, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var3, 37, 12, 21, 39, 12, 21, field_175826_b, field_175826_b, false);

            for(var4 = 0; var4 < 4; ++var4) {
               this.func_175804_a(var1, var3, 43 - var4, var4 + 9, 21, 43 - var4, var4 + 9, 43 - var4, field_175826_b, field_175826_b, false);
            }

            for(var4 = 23; var4 <= 39; var4 += 3) {
               this.func_175811_a(var1, field_175824_d, 38, 13, var4, var3);
            }
         }

         if (this.func_175818_a(var3, 15, 37, 42, 43)) {
            this.func_175804_a(var1, var3, 21, 0, 37, 36, 0, 43, field_175828_a, field_175828_a, false);
            this.func_209179_a(var1, var3, 21, 1, 37, 36, 14, 43);
            this.func_175804_a(var1, var3, 21, 12, 37, 36, 12, 39, field_175828_a, field_175828_a, false);

            for(var4 = 0; var4 < 4; ++var4) {
               this.func_175804_a(var1, var3, 15 + var4, var4 + 9, 43 - var4, 42 - var4, var4 + 9, 43 - var4, field_175826_b, field_175826_b, false);
            }

            for(var4 = 21; var4 <= 36; var4 += 3) {
               this.func_175811_a(var1, field_175824_d, var4, 13, 38, var3);
            }
         }

      }
   }

   public abstract static class Piece extends StructurePiece {
      protected static final IBlockState field_175828_a;
      protected static final IBlockState field_175826_b;
      protected static final IBlockState field_175827_c;
      protected static final IBlockState field_175824_d;
      protected static final IBlockState field_175825_e;
      protected static final IBlockState field_175822_f;
      protected static final Set<Block> field_212180_g;
      protected static final int field_175823_g;
      protected static final int field_175831_h;
      protected static final int field_175832_i;
      protected static final int field_175829_j;
      protected OceanMonumentPieces.RoomDefinition field_175830_k;

      protected static final int func_175820_a(int var0, int var1, int var2) {
         return var1 * 25 + var2 * 5 + var0;
      }

      public Piece() {
         super(0);
      }

      public Piece(int var1) {
         super(var1);
      }

      public Piece(EnumFacing var1, MutableBoundingBox var2) {
         super(1);
         this.func_186164_a(var1);
         this.field_74887_e = var2;
      }

      protected Piece(int var1, EnumFacing var2, OceanMonumentPieces.RoomDefinition var3, int var4, int var5, int var6) {
         super(var1);
         this.func_186164_a(var2);
         this.field_175830_k = var3;
         int var7 = var3.field_175967_a;
         int var8 = var7 % 5;
         int var9 = var7 / 5 % 5;
         int var10 = var7 / 25;
         if (var2 != EnumFacing.NORTH && var2 != EnumFacing.SOUTH) {
            this.field_74887_e = new MutableBoundingBox(0, 0, 0, var6 * 8 - 1, var5 * 4 - 1, var4 * 8 - 1);
         } else {
            this.field_74887_e = new MutableBoundingBox(0, 0, 0, var4 * 8 - 1, var5 * 4 - 1, var6 * 8 - 1);
         }

         switch(var2) {
         case NORTH:
            this.field_74887_e.func_78886_a(var8 * 8, var10 * 4, -(var9 + var6) * 8 + 1);
            break;
         case SOUTH:
            this.field_74887_e.func_78886_a(var8 * 8, var10 * 4, var9 * 8);
            break;
         case WEST:
            this.field_74887_e.func_78886_a(-(var9 + var6) * 8 + 1, var10 * 4, var8 * 8);
            break;
         default:
            this.field_74887_e.func_78886_a(var9 * 8, var10 * 4, var8 * 8);
         }

      }

      protected void func_143012_a(NBTTagCompound var1) {
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
      }

      protected void func_209179_a(IWorld var1, MutableBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8) {
         for(int var9 = var4; var9 <= var7; ++var9) {
            for(int var10 = var3; var10 <= var6; ++var10) {
               for(int var11 = var5; var11 <= var8; ++var11) {
                  IBlockState var12 = this.func_175807_a(var1, var10, var9, var11, var2);
                  if (!field_212180_g.contains(var12.func_177230_c())) {
                     if (this.func_74862_a(var9) >= var1.func_181545_F() && var12 != field_175822_f) {
                        this.func_175811_a(var1, Blocks.field_150350_a.func_176223_P(), var10, var9, var11, var2);
                     } else {
                        this.func_175811_a(var1, field_175822_f, var10, var9, var11, var2);
                     }
                  }
               }
            }
         }

      }

      protected void func_175821_a(IWorld var1, MutableBoundingBox var2, int var3, int var4, boolean var5) {
         if (var5) {
            this.func_175804_a(var1, var2, var3 + 0, 0, var4 + 0, var3 + 2, 0, var4 + 8 - 1, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var2, var3 + 5, 0, var4 + 0, var3 + 8 - 1, 0, var4 + 8 - 1, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var2, var3 + 3, 0, var4 + 0, var3 + 4, 0, var4 + 2, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var2, var3 + 3, 0, var4 + 5, var3 + 4, 0, var4 + 8 - 1, field_175828_a, field_175828_a, false);
            this.func_175804_a(var1, var2, var3 + 3, 0, var4 + 2, var3 + 4, 0, var4 + 2, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var2, var3 + 3, 0, var4 + 5, var3 + 4, 0, var4 + 5, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var2, var3 + 2, 0, var4 + 3, var3 + 2, 0, var4 + 4, field_175826_b, field_175826_b, false);
            this.func_175804_a(var1, var2, var3 + 5, 0, var4 + 3, var3 + 5, 0, var4 + 4, field_175826_b, field_175826_b, false);
         } else {
            this.func_175804_a(var1, var2, var3 + 0, 0, var4 + 0, var3 + 8 - 1, 0, var4 + 8 - 1, field_175828_a, field_175828_a, false);
         }

      }

      protected void func_175819_a(IWorld var1, MutableBoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, IBlockState var9) {
         for(int var10 = var4; var10 <= var7; ++var10) {
            for(int var11 = var3; var11 <= var6; ++var11) {
               for(int var12 = var5; var12 <= var8; ++var12) {
                  if (this.func_175807_a(var1, var11, var10, var12, var2) == field_175822_f) {
                     this.func_175811_a(var1, var9, var11, var10, var12, var2);
                  }
               }
            }
         }

      }

      protected boolean func_175818_a(MutableBoundingBox var1, int var2, int var3, int var4, int var5) {
         int var6 = this.func_74865_a(var2, var3);
         int var7 = this.func_74873_b(var2, var3);
         int var8 = this.func_74865_a(var4, var5);
         int var9 = this.func_74873_b(var4, var5);
         return var1.func_78885_a(Math.min(var6, var8), Math.min(var7, var9), Math.max(var6, var8), Math.max(var7, var9));
      }

      protected boolean func_175817_a(IWorld var1, MutableBoundingBox var2, int var3, int var4, int var5) {
         int var6 = this.func_74865_a(var3, var5);
         int var7 = this.func_74862_a(var4);
         int var8 = this.func_74873_b(var3, var5);
         if (var2.func_175898_b(new BlockPos(var6, var7, var8))) {
            EntityElderGuardian var9 = new EntityElderGuardian(var1.func_201672_e());
            var9.func_70691_i(var9.func_110138_aP());
            var9.func_70012_b((double)var6 + 0.5D, (double)var7, (double)var8 + 0.5D, 0.0F, 0.0F);
            var9.func_204210_a(var1.func_175649_E(new BlockPos(var9)), (IEntityLivingData)null, (NBTTagCompound)null);
            var1.func_72838_d(var9);
            return true;
         } else {
            return false;
         }
      }

      static {
         field_175828_a = Blocks.field_180397_cI.func_176223_P();
         field_175826_b = Blocks.field_196779_gQ.func_176223_P();
         field_175827_c = Blocks.field_196781_gR.func_176223_P();
         field_175824_d = field_175826_b;
         field_175825_e = Blocks.field_180398_cJ.func_176223_P();
         field_175822_f = Blocks.field_150355_j.func_176223_P();
         field_212180_g = ImmutableSet.builder().add(Blocks.field_150432_aD).add(Blocks.field_150403_cj).add(Blocks.field_205164_gk).add(field_175822_f.func_177230_c()).build();
         field_175823_g = func_175820_a(2, 0, 0);
         field_175831_h = func_175820_a(2, 2, 0);
         field_175832_i = func_175820_a(0, 1, 0);
         field_175829_j = func_175820_a(4, 1, 0);
      }
   }
}
