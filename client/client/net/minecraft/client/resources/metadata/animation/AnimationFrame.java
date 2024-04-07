package net.minecraft.client.resources.metadata.animation;

public class AnimationFrame {
   public static final int UNKNOWN_FRAME_TIME = -1;
   private final int index;
   private final int time;

   public AnimationFrame(int var1) {
      this(var1, -1);
   }

   public AnimationFrame(int var1, int var2) {
      super();
      this.index = var1;
      this.time = var2;
   }

   public int getTime(int var1) {
      return this.time == -1 ? var1 : this.time;
   }

   public int getIndex() {
      return this.index;
   }
}
