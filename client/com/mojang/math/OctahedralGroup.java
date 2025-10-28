package com.mojang.math;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.util.StringRepresentable;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Vector3i;
import org.jspecify.annotations.Nullable;

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

   private final Matrix3fc transformation;
   private final String name;
   private @Nullable Map<Direction, Direction> rotatedDirections;
   private final boolean invertX;
   private final boolean invertY;
   private final boolean invertZ;
   private final SymmetricGroup3 permutation;
   private static final OctahedralGroup[][] CAYLEY_TABLE = (OctahedralGroup[][])Util.make(() -> {
      OctahedralGroup[] var0 = values();
      OctahedralGroup[][] var1 = new OctahedralGroup[var0.length][var0.length];
      Map var2 = (Map)Arrays.stream(var0).collect(Collectors.toMap(OctahedralGroup::trace, (var0x) -> var0x));

      for(OctahedralGroup var6 : var0) {
         for(OctahedralGroup var10 : var0) {
            SymmetricGroup3 var11 = var10.permutation.compose(var6.permutation);
            boolean var12 = var6.inverts(Direction.Axis.X) ^ var10.inverts(var6.permutation.permuteAxis(Direction.Axis.X));
            boolean var13 = var6.inverts(Direction.Axis.Y) ^ var10.inverts(var6.permutation.permuteAxis(Direction.Axis.Y));
            boolean var14 = var6.inverts(Direction.Axis.Z) ^ var10.inverts(var6.permutation.permuteAxis(Direction.Axis.Z));
            var1[var6.ordinal()][var10.ordinal()] = (OctahedralGroup)var2.get(trace(var12, var13, var14, var11));
         }
      }

      return var1;
   });
   private static final OctahedralGroup[] INVERSE_TABLE = (OctahedralGroup[])Arrays.stream(values()).map((var0) -> (OctahedralGroup)Arrays.stream(values()).filter((var1) -> var0.compose(var1) == IDENTITY).findAny().get()).toArray((var0) -> new OctahedralGroup[var0]);
   private static final OctahedralGroup[][] XY_TABLE = (OctahedralGroup[][])Util.make(new OctahedralGroup[Quadrant.values().length][Quadrant.values().length], (var0) -> {
      for(Quadrant var4 : Quadrant.values()) {
         for(Quadrant var8 : Quadrant.values()) {
            OctahedralGroup var9 = IDENTITY;

            for(int var10 = 0; var10 < var8.shift; ++var10) {
               var9 = var9.compose(ROT_90_Y_NEG);
            }

            for(int var11 = 0; var11 < var4.shift; ++var11) {
               var9 = var9.compose(ROT_90_X_NEG);
            }

            var0[var4.ordinal()][var8.ordinal()] = var9;
         }
      }

   });

   private OctahedralGroup(final String var3, final SymmetricGroup3 var4, final boolean var5, final boolean var6, final boolean var7) {
      this.name = var3;
      this.invertX = var5;
      this.invertY = var6;
      this.invertZ = var7;
      this.permutation = var4;
      this.transformation = (new Matrix3f()).scaling(var5 ? -1.0F : 1.0F, var6 ? -1.0F : 1.0F, var7 ? -1.0F : 1.0F).mul(var4.transformation());
   }

   private static int trace(boolean var0, boolean var1, boolean var2, SymmetricGroup3 var3) {
      int var4 = (var2 ? 4 : 0) + (var1 ? 2 : 0) + (var0 ? 1 : 0);
      return var3.ordinal() << 3 | var4;
   }

   private int trace() {
      return trace(this.invertX, this.invertY, this.invertZ, this.permutation);
   }

   public OctahedralGroup compose(OctahedralGroup var1) {
      return CAYLEY_TABLE[this.ordinal()][var1.ordinal()];
   }

   public OctahedralGroup inverse() {
      return INVERSE_TABLE[this.ordinal()];
   }

   public Matrix3fc transformation() {
      return this.transformation;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   public Direction rotate(Direction var1) {
      if (this.rotatedDirections == null) {
         this.rotatedDirections = Util.<Direction, Direction>makeEnumMap(Direction.class, (var1x) -> {
            Direction.Axis var2 = var1x.getAxis();
            Direction.AxisDirection var3 = var1x.getAxisDirection();
            Direction.Axis var4 = this.permutation.inverse().permuteAxis(var2);
            Direction.AxisDirection var5 = this.inverts(var4) ? var3.opposite() : var3;
            return Direction.fromAxisAndDirection(var4, var5);
         });
      }

      return (Direction)this.rotatedDirections.get(var1);
   }

   public Vector3i rotate(Vector3i var1) {
      this.permutation.permuteVector(var1);
      var1.x *= this.invertX ? -1 : 1;
      var1.y *= this.invertY ? -1 : 1;
      var1.z *= this.invertZ ? -1 : 1;
      return var1;
   }

   public boolean inverts(Direction.Axis var1) {
      boolean var10000;
      switch (var1) {
         case X -> var10000 = this.invertX;
         case Y -> var10000 = this.invertY;
         case Z -> var10000 = this.invertZ;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public SymmetricGroup3 permutation() {
      return this.permutation;
   }

   public FrontAndTop rotate(FrontAndTop var1) {
      return FrontAndTop.fromFrontAndTop(this.rotate(var1.front()), this.rotate(var1.top()));
   }

   public static OctahedralGroup fromXYAngles(Quadrant var0, Quadrant var1) {
      return XY_TABLE[var0.ordinal()][var1.ordinal()];
   }

   // $FF: synthetic method
   private static OctahedralGroup[] $values() {
      return new OctahedralGroup[]{IDENTITY, ROT_180_FACE_XY, ROT_180_FACE_XZ, ROT_180_FACE_YZ, ROT_120_NNN, ROT_120_NNP, ROT_120_NPN, ROT_120_NPP, ROT_120_PNN, ROT_120_PNP, ROT_120_PPN, ROT_120_PPP, ROT_180_EDGE_XY_NEG, ROT_180_EDGE_XY_POS, ROT_180_EDGE_XZ_NEG, ROT_180_EDGE_XZ_POS, ROT_180_EDGE_YZ_NEG, ROT_180_EDGE_YZ_POS, ROT_90_X_NEG, ROT_90_X_POS, ROT_90_Y_NEG, ROT_90_Y_POS, ROT_90_Z_NEG, ROT_90_Z_POS, INVERSION, INVERT_X, INVERT_Y, INVERT_Z, ROT_60_REF_NNN, ROT_60_REF_NNP, ROT_60_REF_NPN, ROT_60_REF_NPP, ROT_60_REF_PNN, ROT_60_REF_PNP, ROT_60_REF_PPN, ROT_60_REF_PPP, SWAP_XY, SWAP_YZ, SWAP_XZ, SWAP_NEG_XY, SWAP_NEG_YZ, SWAP_NEG_XZ, ROT_90_REF_X_NEG, ROT_90_REF_X_POS, ROT_90_REF_Y_NEG, ROT_90_REF_Y_POS, ROT_90_REF_Z_NEG, ROT_90_REF_Z_POS};
   }
}
