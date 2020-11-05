package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.Util;

public class Lighting {
   private static final Vector3f DIFFUSE_LIGHT_0 = (Vector3f)Util.make(new Vector3f(0.2F, 1.0F, -0.7F), Vector3f::normalize);
   private static final Vector3f DIFFUSE_LIGHT_1 = (Vector3f)Util.make(new Vector3f(-0.2F, 1.0F, 0.7F), Vector3f::normalize);
   private static final Vector3f NETHER_DIFFUSE_LIGHT_0 = (Vector3f)Util.make(new Vector3f(0.2F, 1.0F, -0.7F), Vector3f::normalize);
   private static final Vector3f NETHER_DIFFUSE_LIGHT_1 = (Vector3f)Util.make(new Vector3f(-0.2F, -1.0F, 0.7F), Vector3f::normalize);

   public static void turnBackOn() {
      RenderSystem.enableLighting();
      RenderSystem.enableColorMaterial();
      RenderSystem.colorMaterial(1032, 5634);
   }

   public static void turnOff() {
      RenderSystem.disableLighting();
      RenderSystem.disableColorMaterial();
   }

   public static void setupNetherLevel(Matrix4f var0) {
      RenderSystem.setupLevelDiffuseLighting(NETHER_DIFFUSE_LIGHT_0, NETHER_DIFFUSE_LIGHT_1, var0);
   }

   public static void setupLevel(Matrix4f var0) {
      RenderSystem.setupLevelDiffuseLighting(DIFFUSE_LIGHT_0, DIFFUSE_LIGHT_1, var0);
   }

   public static void setupForFlatItems() {
      RenderSystem.setupGuiFlatDiffuseLighting(DIFFUSE_LIGHT_0, DIFFUSE_LIGHT_1);
   }

   public static void setupFor3DItems() {
      RenderSystem.setupGui3DDiffuseLighting(DIFFUSE_LIGHT_0, DIFFUSE_LIGHT_1);
   }
}
