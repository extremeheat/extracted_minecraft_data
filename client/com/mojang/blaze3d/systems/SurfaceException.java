package com.mojang.blaze3d.systems;

public class SurfaceException extends Exception {
   public SurfaceException(final String message) {
      super(message);
   }

   public SurfaceException(final Throwable cause) {
      super(cause);
   }
}
