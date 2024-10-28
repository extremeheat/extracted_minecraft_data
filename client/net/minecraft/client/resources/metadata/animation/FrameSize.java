package net.minecraft.client.resources.metadata.animation;

public record FrameSize(int width, int height) {
   public FrameSize(int width, int height) {
      super();
      this.width = width;
      this.height = height;
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }
}
