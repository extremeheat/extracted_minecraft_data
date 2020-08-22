package net.minecraft.client.resources.metadata.animation;

public class AnimationFrame {
   private final int index;
   private final int time;

   public AnimationFrame(int var1) {
      this(var1, -1);
   }

   public AnimationFrame(int var1, int var2) {
      this.index = var1;
      this.time = var2;
   }

   public boolean isTimeUnknown() {
      return this.time == -1;
   }

   public int getTime() {
      return this.time;
   }

   public int getIndex() {
      return this.index;
   }
}
