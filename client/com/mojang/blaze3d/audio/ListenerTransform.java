package com.mojang.blaze3d.audio;

import net.minecraft.world.phys.Vec3;

public record ListenerTransform(Vec3 position, Vec3 forward, Vec3 up) {
   public static final ListenerTransform INITIAL;

   public ListenerTransform(Vec3 var1, Vec3 var2, Vec3 var3) {
      super();
      this.position = var1;
      this.forward = var2;
      this.up = var3;
   }

   public Vec3 right() {
      return this.forward.cross(this.up);
   }

   static {
      INITIAL = new ListenerTransform(Vec3.ZERO, new Vec3(0.0, 0.0, -1.0), new Vec3(0.0, 1.0, 0.0));
   }
}
