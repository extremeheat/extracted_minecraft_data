package net.minecraft.client.renderer.feature.submit;

import net.minecraft.util.Mth;
import org.joml.Matrix4fc;

public interface TranslucentSubmit extends SubmitNode {
   float distanceToCameraSq();

   static float computeDistanceToCameraSq(final Matrix4fc pose) {
      return Mth.lengthSquared(pose.m30(), pose.m31(), pose.m32());
   }
}
