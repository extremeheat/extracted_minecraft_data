package com.mojang.math;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.util.StringRepresentable;

public enum OctahedralGroup implements StringRepresentable {
   IDENTITY("identity", SymmetricGroup3.P123, false, false, false),
   ROT_180_FACE_XY("rot_180_face_xy", SymmetricGroup3.P123, true, true, false),
   ROT_180_FACE_XZ("rot_180_face_xz", SymmetricGroup3.P123, true, false, true),
   ROT_180_FACE_YZ("rot_180_face_yz", SymmetricGroup3.P123, false, true, true),
   ROT_120_NNN("rot_120_nnn", SymmetricGroup3.P231, false, false, false),
   ROT_120_NNP("rot_120_nnp", SymmetricGroup3.P312, true, false, true),
   ROT_120_NPN("rot_120_npn", SymmetricGroup3.P312, false, true, true),
   ROT_120_NPP("rot_120_npp", SymmetricGroup3.P231, true, false, true),
   ROT_120_PNN("rot_120_pnn", SymmetricGroup3.P312, true, true, false),
   ROT_120_PNP("rot_120_pnp", SymmetricGroup3.P231, true, true, false),
   ROT_120_PPN("rot_120_ppn", SymmetricGroup3.P231, false, true, true),
   ROT_120_PPP("rot_120_ppp", SymmetricGroup3.P312, false, false, false),
   ROT_180_EDGE_XY_NEG("rot_180_edge_xy_neg", SymmetricGroup3.P213, true, true, true),
   ROT_180_EDGE_XY_POS("rot_180_edge_xy_pos", SymmetricGroup3.P213, false, false, true),
   ROT_180_EDGE_XZ_NEG("rot_180_edge_xz_neg", SymmetricGroup3.P321, true, true, true),
   ROT_180_EDGE_XZ_POS("rot_180_edge_xz_pos", SymmetricGroup3.P321, false, true, false),
   ROT_180_EDGE_YZ_NEG("rot_180_edge_yz_neg", SymmetricGroup3.P132, true, true, true),
   ROT_180_EDGE_YZ_POS("rot_180_edge_yz_pos", SymmetricGroup3.P132, true, false, false),
   ROT_90_X_NEG("rot_90_x_neg", SymmetricGroup3.P132, false, false, true),
   ROT_90_X_POS("rot_90_x_pos", SymmetricGroup3.P132, false, true, false),
   ROT_90_Y_NEG("rot_90_y_neg", SymmetricGroup3.P321, true, false, false),
   ROT_90_Y_POS("rot_90_y_pos", SymmetricGroup3.P321, false, false, true),
   ROT_90_Z_NEG("rot_90_z_neg", SymmetricGroup3.P213, false, true, false),
   ROT_90_Z_POS("rot_90_z_pos", SymmetricGroup3.P213, true, false, false),
   INVERSION("inversion", SymmetricGroup3.P123, true, true, true),
   INVERT_X("invert_x", SymmetricGroup3.P123, true, false, false),
   INVERT_Y("invert_y", SymmetricGroup3.P123, false, true, false),
   INVERT_Z("invert_z", SymmetricGroup3.P123, false, false, true),
   ROT_60_REF_NNN("rot_60_ref_nnn", SymmetricGroup3.P312, true, true, true),
   ROT_60_REF_NNP("rot_60_ref_nnp", SymmetricGroup3.P231, true, false, false),
   ROT_60_REF_NPN("rot_60_ref_npn", SymmetricGroup3.P231, false, false, true),
   ROT_60_REF_NPP("rot_60_ref_npp", SymmetricGroup3.P312, false, false, true),
   ROT_60_REF_PNN("rot_60_ref_pnn", SymmetricGroup3.P231, false, true, false),
   ROT_60_REF_PNP("rot_60_ref_pnp", SymmetricGroup3.P312, true, false, false),
   ROT_60_REF_PPN("rot_60_ref_ppn", SymmetricGroup3.P312, false, true, false),
   ROT_60_REF_PPP("rot_60_ref_ppp", SymmetricGroup3.P231, true, true, true),
   SWAP_XY("swap_xy", SymmetricGroup3.P213, false, false, false),
   SWAP_YZ("swap_yz", SymmetricGroup3.P132, false, false, false),
   SWAP_XZ("swap_xz", SymmetricGroup3.P321, false, false, false),
   SWAP_NEG_XY("swap_neg_xy", SymmetricGroup3.P213, true, true, false),
   SWAP_NEG_YZ("swap_neg_yz", SymmetricGroup3.P132, false, true, true),
   SWAP_NEG_XZ("swap_neg_xz", SymmetricGroup3.P321, true, false, true),
   ROT_90_REF_X_NEG("rot_90_ref_x_neg", SymmetricGroup3.P132, true, false, true),
   ROT_90_REF_X_POS("rot_90_ref_x_pos", SymmetricGroup3.P132, true, true, false),
   ROT_90_REF_Y_NEG("rot_90_ref_y_neg", SymmetricGroup3.P321, true, true, false),
   ROT_90_REF_Y_POS("rot_90_ref_y_pos", SymmetricGroup3.P321, false, true, true),
   ROT_90_REF_Z_NEG("rot_90_ref_z_neg", SymmetricGroup3.P213, false, true, true),
   ROT_90_REF_Z_POS("rot_90_ref_z_pos", SymmetricGroup3.P213, true, false, true);

   private final Matrix3f transformation;
   private final String name;
   @Nullable
   private Map<Direction, Direction> rotatedDirections;
   private final boolean invertX;
   private final boolean invertY;
   private final boolean invertZ;
   private final SymmetricGroup3 permutation;
   private static final OctahedralGroup[][] cayleyTable = (OctahedralGroup[][])Util.make(new OctahedralGroup[values().length][values().length], (var0) -> {
      Map var1 = (Map)Arrays.stream(values()).collect(Collectors.toMap((var0x) -> {
         return Pair.of(var0x.permutation, var0x.packInversions());
      }, (var0x) -> {
         return var0x;
      }));
      OctahedralGroup[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         OctahedralGroup var5 = var2[var4];
         OctahedralGroup[] var6 = values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            OctahedralGroup var9 = var6[var8];
            BooleanList var10 = var5.packInversions();
            BooleanList var11 = var9.packInversions();
            SymmetricGroup3 var12 = var9.permutation.compose(var5.permutation);
            BooleanArrayList var13 = new BooleanArrayList(3);

            for(int var14 = 0; var14 < 3; ++var14) {
               var13.add(var10.getBoolean(var14) ^ var11.getBoolean(var5.permutation.permutation(var14)));
            }

            var0[var5.ordinal()][var9.ordinal()] = (OctahedralGroup)var1.get(Pair.of(var12, var13));
         }
      }

   });
   private static final OctahedralGroup[] inverseTable = (OctahedralGroup[])Arrays.stream(values()).map((var0) -> {
      return (OctahedralGroup)Arrays.stream(values()).filter((var1) -> {
         return var0.compose(var1) == IDENTITY;
      }).findAny().get();
   }).toArray((var0) -> {
      return new OctahedralGroup[var0];
   });

   private OctahedralGroup(String var3, SymmetricGroup3 var4, boolean var5, boolean var6, boolean var7) {
      this.name = var3;
      this.invertX = var5;
      this.invertY = var6;
      this.invertZ = var7;
      this.permutation = var4;
      this.transformation = new Matrix3f();
      this.transformation.m00 = var5 ? -1.0F : 1.0F;
      this.transformation.m11 = var6 ? -1.0F : 1.0F;
      this.transformation.m22 = var7 ? -1.0F : 1.0F;
      this.transformation.mul(var4.transformation());
   }

   private BooleanList packInversions() {
      return new BooleanArrayList(new boolean[]{this.invertX, this.invertY, this.invertZ});
   }

   public OctahedralGroup compose(OctahedralGroup var1) {
      return cayleyTable[this.ordinal()][var1.ordinal()];
   }

   public OctahedralGroup inverse() {
      return inverseTable[this.ordinal()];
   }

   public Matrix3f transformation() {
      return this.transformation.copy();
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   public Direction rotate(Direction var1) {
      if (this.rotatedDirections == null) {
         this.rotatedDirections = Maps.newEnumMap(Direction.class);
         Direction[] var2 = Direction.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Direction var5 = var2[var4];
            Direction.Axis var6 = var5.getAxis();
            Direction.AxisDirection var7 = var5.getAxisDirection();
            Direction.Axis var8 = Direction.Axis.values()[this.permutation.permutation(var6.ordinal())];
            Direction.AxisDirection var9 = this.inverts(var8) ? var7.opposite() : var7;
            Direction var10 = Direction.fromAxisAndDirection(var8, var9);
            this.rotatedDirections.put(var5, var10);
         }
      }

      return (Direction)this.rotatedDirections.get(var1);
   }

   public boolean inverts(Direction.Axis var1) {
      switch (var1) {
         case X:
            return this.invertX;
         case Y:
            return this.invertY;
         case Z:
         default:
            return this.invertZ;
      }
   }

   public FrontAndTop rotate(FrontAndTop var1) {
      return FrontAndTop.fromFrontAndTop(this.rotate(var1.front()), this.rotate(var1.top()));
   }

   // $FF: synthetic method
   private static OctahedralGroup[] $values() {
      return new OctahedralGroup[]{IDENTITY, ROT_180_FACE_XY, ROT_180_FACE_XZ, ROT_180_FACE_YZ, ROT_120_NNN, ROT_120_NNP, ROT_120_NPN, ROT_120_NPP, ROT_120_PNN, ROT_120_PNP, ROT_120_PPN, ROT_120_PPP, ROT_180_EDGE_XY_NEG, ROT_180_EDGE_XY_POS, ROT_180_EDGE_XZ_NEG, ROT_180_EDGE_XZ_POS, ROT_180_EDGE_YZ_NEG, ROT_180_EDGE_YZ_POS, ROT_90_X_NEG, ROT_90_X_POS, ROT_90_Y_NEG, ROT_90_Y_POS, ROT_90_Z_NEG, ROT_90_Z_POS, INVERSION, INVERT_X, INVERT_Y, INVERT_Z, ROT_60_REF_NNN, ROT_60_REF_NNP, ROT_60_REF_NPN, ROT_60_REF_NPP, ROT_60_REF_PNN, ROT_60_REF_PNP, ROT_60_REF_PPN, ROT_60_REF_PPP, SWAP_XY, SWAP_YZ, SWAP_XZ, SWAP_NEG_XY, SWAP_NEG_YZ, SWAP_NEG_XZ, ROT_90_REF_X_NEG, ROT_90_REF_X_POS, ROT_90_REF_Y_NEG, ROT_90_REF_Y_POS, ROT_90_REF_Z_NEG, ROT_90_REF_Z_POS};
   }
}
