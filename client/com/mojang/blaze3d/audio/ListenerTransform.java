package com.mojang.blaze3d.audio;

import net.minecraft.world.phys.Vec3;

public record ListenerTransform(Vec3 position, Vec3 forward, Vec3 up) {
   public static final ListenerTransform INITIAL;

   public ListenerTransform(Vec3 position, Vec3 forward, Vec3 up) {
      super();
      this.position = position;
      this.forward = forward;
      this.up = up;
   }

   public Vec3 right() {
      return this.forward.cross(this.up);
   }

   public Vec3 position() {
      return this.position;
   }

   public Vec3 forward() {
      return this.forward;
   }

   public Vec3 up() {
      return this.up;
   }

   static {
      INITIAL = new ListenerTransform(Vec3.ZERO, new Vec3(0.0, 0.0, -1.0), new Vec3(0.0, 1.0, 0.0));
   }
}
