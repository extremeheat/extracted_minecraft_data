package net.minecraft.client.resources.model;

import com.mojang.math.OctahedralGroup;
import com.mojang.math.Transformation;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockMath;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class BlockModelRotation implements ModelState {
   private static final Map<OctahedralGroup, BlockModelRotation> BY_GROUP_ORDINAL = Util.<OctahedralGroup, BlockModelRotation>makeEnumMap(OctahedralGroup.class, BlockModelRotation::new);
   public static final BlockModelRotation IDENTITY;
   final Transformation transformation;
   final Map<Direction, Matrix4fc> faceMapping = new EnumMap(Direction.class);
   final Map<Direction, Matrix4fc> inverseFaceMapping = new EnumMap(Direction.class);
   private final WithUvLock withUvLock = new WithUvLock(this);

   private BlockModelRotation(OctahedralGroup var1) {
      super();
      if (var1 != OctahedralGroup.IDENTITY) {
         this.transformation = new Transformation(new Matrix4f(var1.transformation()));
      } else {
         this.transformation = Transformation.identity();
      }

      for(Direction var5 : Direction.values()) {
         Matrix4fc var6 = BlockMath.getFaceTransformation(this.transformation, var5).getMatrix();
         this.faceMapping.put(var5, var6);
         this.inverseFaceMapping.put(var5, var6.invertAffine(new Matrix4f()));
      }

   }

   public Transformation transformation() {
      return this.transformation;
   }

   public static BlockModelRotation get(OctahedralGroup var0) {
      return (BlockModelRotation)BY_GROUP_ORDINAL.get(var0);
   }

   public ModelState withUvLock() {
      return this.withUvLock;
   }

   static {
      IDENTITY = get(OctahedralGroup.IDENTITY);
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
