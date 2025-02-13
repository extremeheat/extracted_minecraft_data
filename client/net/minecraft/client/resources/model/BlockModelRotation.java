package net.minecraft.client.resources.model;

import com.mojang.math.OctahedralGroup;
import com.mojang.math.Quadrant;
import com.mojang.math.Transformation;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockMath;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public enum BlockModelRotation implements ModelState {
   X0_Y0(Quadrant.R0, Quadrant.R0),
   X0_Y90(Quadrant.R0, Quadrant.R90),
   X0_Y180(Quadrant.R0, Quadrant.R180),
   X0_Y270(Quadrant.R0, Quadrant.R270),
   X90_Y0(Quadrant.R90, Quadrant.R0),
   X90_Y90(Quadrant.R90, Quadrant.R90),
   X90_Y180(Quadrant.R90, Quadrant.R180),
   X90_Y270(Quadrant.R90, Quadrant.R270),
   X180_Y0(Quadrant.R180, Quadrant.R0),
   X180_Y90(Quadrant.R180, Quadrant.R90),
   X180_Y180(Quadrant.R180, Quadrant.R180),
   X180_Y270(Quadrant.R180, Quadrant.R270),
   X270_Y0(Quadrant.R270, Quadrant.R0),
   X270_Y90(Quadrant.R270, Quadrant.R90),
   X270_Y180(Quadrant.R270, Quadrant.R180),
   X270_Y270(Quadrant.R270, Quadrant.R270);

   private static final BlockModelRotation[][] XY_TABLE = (BlockModelRotation[][])Util.make(new BlockModelRotation[Quadrant.values().length][Quadrant.values().length], (var0) -> {
      for(BlockModelRotation var4 : values()) {
         var0[var4.xRotation.ordinal()][var4.yRotation.ordinal()] = var4;
      }

   });
   private final Quadrant xRotation;
   private final Quadrant yRotation;
   final Transformation transformation;
   private final OctahedralGroup actualRotation;
   final Map<Direction, Matrix4fc> faceMapping = new EnumMap(Direction.class);
   final Map<Direction, Matrix4fc> inverseFaceMapping = new EnumMap(Direction.class);
   private final WithUvLock withUvLock = new WithUvLock(this);

   private BlockModelRotation(final Quadrant var3, final Quadrant var4) {
      this.xRotation = var3;
      this.yRotation = var4;
      this.actualRotation = OctahedralGroup.fromXYAngles(var3, var4);
      if (this.actualRotation != OctahedralGroup.IDENTITY) {
         this.transformation = new Transformation(new Matrix4f(this.actualRotation.transformation()));
      } else {
         this.transformation = Transformation.identity();
      }

      for(Direction var8 : Direction.values()) {
         Matrix4fc var9 = BlockMath.getFaceTransformation(this.transformation, var8).getMatrix();
         this.faceMapping.put(var8, var9);
         this.inverseFaceMapping.put(var8, var9.invertAffine(new Matrix4f()));
      }

   }

   public Transformation transformation() {
      return this.transformation;
   }

   public static BlockModelRotation by(Quadrant var0, Quadrant var1) {
      return XY_TABLE[var0.ordinal()][var1.ordinal()];
   }

   public OctahedralGroup actualRotation() {
      return this.actualRotation;
   }

   public ModelState withUvLock() {
      return this.withUvLock;
   }

   // $FF: synthetic method
   private static BlockModelRotation[] $values() {
      return new BlockModelRotation[]{X0_Y0, X0_Y90, X0_Y180, X0_Y270, X90_Y0, X90_Y90, X90_Y180, X90_Y270, X180_Y0, X180_Y90, X180_Y180, X180_Y270, X270_Y0, X270_Y90, X270_Y180, X270_Y270};
   }

   static record WithUvLock(BlockModelRotation parent) implements ModelState {
      WithUvLock(BlockModelRotation var1) {
         super();
         this.parent = var1;
      }

      public Transformation transformation() {
         return this.parent.transformation;
      }

      public Matrix4fc faceTransformation(Direction var1) {
         return (Matrix4fc)this.parent.faceMapping.getOrDefault(var1, NO_TRANSFORM);
      }

      public Matrix4fc inverseFaceTransformation(Direction var1) {
         return (Matrix4fc)this.parent.inverseFaceMapping.getOrDefault(var1, NO_TRANSFORM);
      }
   }
}
