package net.minecraft.client.resources.metadata.animation;

public record FrameSize(int a, int b) {
   private final int width;
   private final int height;

   public FrameSize(int var1, int var2) {
      super();
      this.width = var1;
      this.height = var2;
   }
}
