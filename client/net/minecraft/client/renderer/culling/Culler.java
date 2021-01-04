package net.minecraft.client.renderer.culling;

import net.minecraft.world.phys.AABB;

public interface Culler {
   boolean isVisible(AABB var1);

   void prepare(double var1, double var3, double var5);
}
