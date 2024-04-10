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
import org.joml.Matrix3f;

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
   private static final OctahedralGroup[][] cayleyTable = Util.make(
      new OctahedralGroup[values().length][values().length],
      var0 -> {
         Map var1 = Arrays.stream(values())
            .collect(Collectors.toMap(var0x -> Pair.of(var0x.permutation, var0x.packInversions()), var0x -> (OctahedralGroup)var0x));
   
         for (OctahedralGroup var5 : values()) {
            for (OctahedralGroup var9 : values()) {
               BooleanList var10 = var5.packInversions();
               BooleanList var11 = var9.packInversions();
               SymmetricGroup3 var12 = var9.permutation.compose(var5.permutation);
               BooleanArrayList var13 = new BooleanArrayList(3);
   
               for (int var14 = 0; var14 < 3; var14++) {
                  var13.add(var10.getBoolean(var14) ^ var11.getBoolean(var5.permutation.permutation(var14)));
               }
   
               var0[var5.ordinal()][var9.ordinal()] = (OctahedralGroup)var1.get(Pair.of(var12, var13));
            }
         }
      }
   );
   private static final OctahedralGroup[] inverseTable = Arrays.stream(values())
      .map(var0 -> Arrays.stream(values()).filter(var1 -> var0.compose(var1) == IDENTITY).findAny().get())
      .toArray(OctahedralGroup[]::new);

   private OctahedralGroup(final String param3, final SymmetricGroup3 param4, final boolean param5, final boolean param6, final boolean param7) {
      this.name = nullxx;
      this.invertX = nullxxxx;
      this.invertY = nullxxxxx;
      this.invertZ = nullxxxxxx;
      this.permutation = nullxxx;
      this.transformation = new Matrix3f().scaling(nullxxxx ? -1.0F : 1.0F, nullxxxxx ? -1.0F : 1.0F, nullxxxxxx ? -1.0F : 1.0F);
      this.transformation.mul(nullxxx.transformation());
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
      return new Matrix3f(this.transformation);
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }

   public Direction rotate(Direction var1) {
      if (this.rotatedDirections == null) {
         this.rotatedDirections = Maps.newEnumMap(Direction.class);
         Direction.Axis[] var2 = Direction.Axis.values();

         for (Direction var6 : Direction.values()) {
            Direction.Axis var7 = var6.getAxis();
            Direction.AxisDirection var8 = var6.getAxisDirection();
            Direction.Axis var9 = var2[this.permutation.permutation(var7.ordinal())];
            Direction.AxisDirection var10 = this.inverts(var9) ? var8.opposite() : var8;
            Direction var11 = Direction.fromAxisAndDirection(var9, var10);
            this.rotatedDirections.put(var6, var11);
         }
      }

      return this.rotatedDirections.get(var1);
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
}
