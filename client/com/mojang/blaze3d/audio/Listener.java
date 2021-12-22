package com.mojang.blaze3d.audio;

import com.mojang.math.Vector3f;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL10;

public class Listener {
   private float gain = 1.0F;
   private Vec3 position;

   public Listener() {
      super();
      this.position = Vec3.ZERO;
   }

   public void setListenerPosition(Vec3 var1) {
      this.position = var1;
      AL10.alListener3f(4100, (float)var1.field_414, (float)var1.field_415, (float)var1.field_416);
   }

   public Vec3 getListenerPosition() {
      return this.position;
   }

   public void setListenerOrientation(Vector3f var1, Vector3f var2) {
      AL10.alListenerfv(4111, new float[]{var1.method_82(), var1.method_83(), var1.method_84(), var2.method_82(), var2.method_83(), var2.method_84()});
   }

   public void setGain(float var1) {
      AL10.alListenerf(4106, var1);
      this.gain = var1;
   }

   public float getGain() {
      return this.gain;
   }

   public void reset() {
      this.setListenerPosition(Vec3.ZERO);
      this.setListenerOrientation(Vector3f.field_293, Vector3f.field_292);
   }
}
