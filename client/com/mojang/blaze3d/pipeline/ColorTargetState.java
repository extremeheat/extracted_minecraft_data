package com.mojang.blaze3d.pipeline;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

public record ColorTargetState(Optional<BlendFunction> blendFunction, @ColorTargetState.WriteMask int writeMask) {
   public static final int WRITE_RED = 1;
   public static final int WRITE_GREEN = 2;
   public static final int WRITE_BLUE = 4;
   public static final int WRITE_ALPHA = 8;
   public static final int WRITE_COLOR = 7;
   public static final int WRITE_ALL = 15;
   public static final int WRITE_NONE = 0;
   public static final ColorTargetState DEFAULT = new ColorTargetState(Optional.empty(), 15);

   public ColorTargetState(final BlendFunction blendFunction) {
      this(Optional.of(blendFunction), 15);
   }

   public ColorTargetState {
      super();
   }

   public boolean writeRed() {
      return (this.writeMask & 1) != 0;
   }

   public boolean writeGreen() {
      return (this.writeMask & 2) != 0;
   }

   public boolean writeBlue() {
      return (this.writeMask & 4) != 0;
   }

   public boolean writeAlpha() {
      return (this.writeMask & 8) != 0;
   }

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
   public @interface WriteMask {
   }
}
