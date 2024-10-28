package com.mojang.blaze3d.vertex;

public class DefaultVertexFormat {
   public static final VertexFormat BLIT_SCREEN;
   public static final VertexFormat BLOCK;
   public static final VertexFormat NEW_ENTITY;
   public static final VertexFormat PARTICLE;
   public static final VertexFormat POSITION;
   public static final VertexFormat POSITION_COLOR;
   public static final VertexFormat POSITION_COLOR_NORMAL;
   public static final VertexFormat POSITION_COLOR_LIGHTMAP;
   public static final VertexFormat POSITION_TEX;
   public static final VertexFormat POSITION_TEX_COLOR;
   public static final VertexFormat POSITION_COLOR_TEX_LIGHTMAP;
   public static final VertexFormat POSITION_TEX_LIGHTMAP_COLOR;
   public static final VertexFormat POSITION_TEX_COLOR_NORMAL;

   public DefaultVertexFormat() {
      super();
   }

   static {
      BLIT_SCREEN = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).build();
      BLOCK = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Color", VertexFormatElement.COLOR).add("UV0", VertexFormatElement.UV0).add("UV2", VertexFormatElement.UV2).add("Normal", VertexFormatElement.NORMAL).padding(1).build();
      NEW_ENTITY = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Color", VertexFormatElement.COLOR).add("UV0", VertexFormatElement.UV0).add("UV1", VertexFormatElement.UV1).add("UV2", VertexFormatElement.UV2).add("Normal", VertexFormatElement.NORMAL).padding(1).build();
      PARTICLE = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("UV0", VertexFormatElement.UV0).add("Color", VertexFormatElement.COLOR).add("UV2", VertexFormatElement.UV2).build();
      POSITION = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).build();
      POSITION_COLOR = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Color", VertexFormatElement.COLOR).build();
      POSITION_COLOR_NORMAL = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Color", VertexFormatElement.COLOR).add("Normal", VertexFormatElement.NORMAL).padding(1).build();
      POSITION_COLOR_LIGHTMAP = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Color", VertexFormatElement.COLOR).add("UV2", VertexFormatElement.UV2).build();
      POSITION_TEX = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("UV0", VertexFormatElement.UV0).build();
      POSITION_TEX_COLOR = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("UV0", VertexFormatElement.UV0).add("Color", VertexFormatElement.COLOR).build();
      POSITION_COLOR_TEX_LIGHTMAP = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("Color", VertexFormatElement.COLOR).add("UV0", VertexFormatElement.UV0).add("UV2", VertexFormatElement.UV2).build();
      POSITION_TEX_LIGHTMAP_COLOR = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("UV0", VertexFormatElement.UV0).add("UV2", VertexFormatElement.UV2).add("Color", VertexFormatElement.COLOR).build();
      POSITION_TEX_COLOR_NORMAL = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("UV0", VertexFormatElement.UV0).add("Color", VertexFormatElement.COLOR).add("Normal", VertexFormatElement.NORMAL).padding(1).build();
   }
}
