package com.mojang.blaze3d.platform;

import java.util.OptionalInt;

public class DisplayData {
   public final int width;
   public final int height;
   public final OptionalInt fullscreenWidth;
   public final OptionalInt fullscreenHeight;
   public final boolean isFullscreen;

   public DisplayData(int var1, int var2, OptionalInt var3, OptionalInt var4, boolean var5) {
      super();
      this.width = var1;
      this.height = var2;
      this.fullscreenWidth = var3;
      this.fullscreenHeight = var4;
      this.isFullscreen = var5;
   }
}
