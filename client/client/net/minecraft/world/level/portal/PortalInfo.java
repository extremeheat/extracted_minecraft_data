package net.minecraft.world.level.portal;

import net.minecraft.world.phys.Vec3;

public class PortalInfo {
   public final Vec3 pos;
   public final Vec3 speed;
   public final float yRot;
   public final float xRot;

   public PortalInfo(Vec3 var1, Vec3 var2, float var3, float var4) {
      super();
      this.pos = var1;
      this.speed = var2;
      this.yRot = var3;
      this.xRot = var4;
   }
}
