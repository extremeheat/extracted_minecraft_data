package net.minecraft.client.renderer;

import org.joml.Vector3fc;

public record PanoramicScreenshotParameters(Vector3fc forwardVector) {
   public PanoramicScreenshotParameters(Vector3fc var1) {
      super();
      this.forwardVector = var1;
   }
}
