package net.minecraft.world.entity.schedule;

public class Keyframe {
   private final int timeStamp;
   private final float value;

   public Keyframe(int var1, float var2) {
      super();
      this.timeStamp = var1;
      this.value = var2;
   }

   public int getTimeStamp() {
      return this.timeStamp;
   }

   public float getValue() {
      return this.value;
   }
}
