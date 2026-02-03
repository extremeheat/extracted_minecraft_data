package net.minecraft.client.renderer;

import org.joml.Vector3fc;

public record PanoramicScreenshotParameters(Vector3fc forwardVector) {
   public PanoramicScreenshotParameters {
      super();
   }
}
