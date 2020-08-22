package net.minecraft.realms;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

public class RealmsDefaultVertexFormat {
   public static final RealmsVertexFormat POSITION_COLOR;
   public static final RealmsVertexFormat POSITION_TEX_COLOR;

   static {
      POSITION_COLOR = new RealmsVertexFormat(DefaultVertexFormat.POSITION_COLOR);
      POSITION_TEX_COLOR = new RealmsVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR);
   }
}
