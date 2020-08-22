package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;

public class Lighting {
   public static void turnBackOn() {
      RenderSystem.enableLighting();
      RenderSystem.enableColorMaterial();
      RenderSystem.colorMaterial(1032, 5634);
   }

   public static void turnOff() {
      RenderSystem.disableLighting();
      RenderSystem.disableColorMaterial();
   }

   public static void setupLevel(Matrix4f var0) {
      RenderSystem.setupLevelDiffuseLighting(var0);
   }

   public static void setupForFlatItems() {
      RenderSystem.setupGuiFlatDiffuseLighting();
   }

   public static void setupFor3DItems() {
      RenderSystem.setupGui3DDiffuseLighting();
   }
}
