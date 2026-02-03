package net.minecraft.client.resources.model;

import com.mojang.math.OctahedralGroup;
import com.mojang.math.Transformation;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockMath;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class BlockModelRotation implements ModelState {
   private static final Map<OctahedralGroup, BlockModelRotation> BY_GROUP_ORDINAL = Util.<OctahedralGroup, BlockModelRotation>makeEnumMap(OctahedralGroup.class, BlockModelRotation::new);
   public static final BlockModelRotation IDENTITY;
   private final OctahedralGroup orientation;
   private final Transformation transformation;
   private final Map<Direction, Matrix4fc> faceMapping = new EnumMap(Direction.class);
   private final Map<Direction, Matrix4fc> inverseFaceMapping = new EnumMap(Direction.class);
   private final WithUvLock withUvLock = new WithUvLock(this);

   private BlockModelRotation(final OctahedralGroup orientation) {
      super();
      this.orientation = orientation;
      if (orientation != OctahedralGroup.IDENTITY) {
         this.transformation = new Transformation(new Matrix4f(orientation.transformation()));
      } else {
         this.transformation = Transformation.identity();
      }

      for(Direction face : Direction.values()) {
         Matrix4fc faceTransform = BlockMath.getFaceTransformation(this.transformation, face).getMatrix();
         this.faceMapping.put(face, faceTransform);
         this.inverseFaceMapping.put(face, faceTransform.invertAffine(new Matrix4f()));
      }

   }

   public Transformation transformation() {
      return this.transformation;
   }

   public static BlockModelRotation get(final OctahedralGroup group) {
      return (BlockModelRotation)BY_GROUP_ORDINAL.get(group);
   }

   public ModelState withUvLock() {
      return this.withUvLock;
   }

   public String toString() {
      return "simple[" + this.orientation.getSerializedName() + "]";
   }

   static {
      IDENTITY = get(OctahedralGroup.IDENTITY);
   }

   private static record WithUvLock(BlockModelRotation parent) implements ModelState {
      private WithUvLock {
         super();
      }

      public Transformation transformation() {
         return this.parent.transformation;
      }

      public Matrix4fc faceTransformation(final Direction face) {
         return (Matrix4fc)this.parent.faceMapping.getOrDefault(face, NO_TRANSFORM);
      }

      public Matrix4fc inverseFaceTransformation(final Direction face) {
         return (Matrix4fc)this.parent.inverseFaceMapping.getOrDefault(face, NO_TRANSFORM);
      }

      public String toString() {
         return "uvLocked[" + this.parent.orientation.getSerializedName() + "]";
      }
   }
}
