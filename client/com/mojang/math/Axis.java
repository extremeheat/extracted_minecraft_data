package com.mojang.math;

import org.joml.Quaternionf;
import org.joml.Vector3f;

@FunctionalInterface
public interface Axis {
   Axis XN = (var0) -> {
      return (new Quaternionf()).rotationX(-var0);
   };
   Axis XP = (var0) -> {
      return (new Quaternionf()).rotationX(var0);
   };
   Axis YN = (var0) -> {
      return (new Quaternionf()).rotationY(-var0);
   };
   Axis YP = (var0) -> {
      return (new Quaternionf()).rotationY(var0);
   };
   Axis ZN = (var0) -> {
      return (new Quaternionf()).rotationZ(-var0);
   };
   Axis ZP = (var0) -> {
      return (new Quaternionf()).rotationZ(var0);
   };

   static Axis of(Vector3f var0) {
      return (var1) -> {
         return (new Quaternionf()).rotationAxis(var1, var0);
      };
   }

   Quaternionf rotation(float var1);

   default Quaternionf rotationDegrees(float var1) {
      return this.rotation(var1 * 0.017453292F);
   }
}
