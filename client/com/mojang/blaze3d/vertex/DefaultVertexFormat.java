package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableMap;

public class DefaultVertexFormat {
   public static final VertexFormatElement ELEMENT_POSITION;
   public static final VertexFormatElement ELEMENT_COLOR;
   public static final VertexFormatElement ELEMENT_UV0;
   public static final VertexFormatElement ELEMENT_UV1;
   public static final VertexFormatElement ELEMENT_UV2;
   public static final VertexFormatElement ELEMENT_NORMAL;
   public static final VertexFormatElement ELEMENT_PADDING;
   public static final VertexFormatElement ELEMENT_UV;
   public static final VertexFormat BLIT_SCREEN;
   public static final VertexFormat BLOCK;
   public static final VertexFormat NEW_ENTITY;
   public static final VertexFormat PARTICLE;
   public static final VertexFormat POSITION;
   public static final VertexFormat POSITION_COLOR;
   public static final VertexFormat POSITION_COLOR_NORMAL;
   public static final VertexFormat POSITION_COLOR_LIGHTMAP;
   public static final VertexFormat POSITION_TEX;
   public static final VertexFormat POSITION_COLOR_TEX;
   public static final VertexFormat POSITION_TEX_COLOR;
   public static final VertexFormat POSITION_COLOR_TEX_LIGHTMAP;
   public static final VertexFormat POSITION_TEX_LIGHTMAP_COLOR;
   public static final VertexFormat POSITION_TEX_COLOR_NORMAL;

   public DefaultVertexFormat() {
      super();
   }

   static {
      ELEMENT_POSITION = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3);
      ELEMENT_COLOR = new VertexFormatElement(0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);
      ELEMENT_UV0 = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
      ELEMENT_UV1 = new VertexFormatElement(1, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
      ELEMENT_UV2 = new VertexFormatElement(2, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
      ELEMENT_NORMAL = new VertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3);
      ELEMENT_PADDING = new VertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.PADDING, 1);
      ELEMENT_UV = ELEMENT_UV0;
      BLIT_SCREEN = new VertexFormat(ImmutableMap.builder().put("Position", ELEMENT_POSITION).put("UV", ELEMENT_UV).put("Color", ELEMENT_COLOR).build());
      BLOCK = new VertexFormat(ImmutableMap.builder().put("Position", ELEMENT_POSITION).put("Color", ELEMENT_COLOR).put("UV0", ELEMENT_UV0).put("UV2", ELEMENT_UV2).put("Normal", ELEMENT_NORMAL).put("Padding", ELEMENT_PADDING).build());
      NEW_ENTITY = new VertexFormat(ImmutableMap.builder().put("Position", ELEMENT_POSITION).put("Color", ELEMENT_COLOR).put("UV0", ELEMENT_UV0).put("UV1", ELEMENT_UV1).put("UV2", ELEMENT_UV2).put("Normal", ELEMENT_NORMAL).put("Padding", ELEMENT_PADDING).build());
      PARTICLE = new VertexFormat(ImmutableMap.builder().put("Position", ELEMENT_POSITION).put("UV0", ELEMENT_UV0).put("Color", ELEMENT_COLOR).put("UV2", ELEMENT_UV2).build());
      POSITION = new VertexFormat(ImmutableMap.builder().put("Position", ELEMENT_POSITION).build());
      POSITION_COLOR = new VertexFormat(ImmutableMap.builder().put("Position", ELEMENT_POSITION).put("Color", ELEMENT_COLOR).build());
      POSITION_COLOR_NORMAL = new VertexFormat(ImmutableMap.builder().put("Position", ELEMENT_POSITION).put("Color", ELEMENT_COLOR).put("Normal", ELEMENT_NORMAL).put("Padding", ELEMENT_PADDING).build());
      POSITION_COLOR_LIGHTMAP = new VertexFormat(ImmutableMap.builder().put("Position", ELEMENT_POSITION).put("Color", ELEMENT_COLOR).put("UV2", ELEMENT_UV2).build());
      POSITION_TEX = new VertexFormat(ImmutableMap.builder().put("Position", ELEMENT_POSITION).put("UV0", ELEMENT_UV0).build());
      POSITION_COLOR_TEX = new VertexFormat(ImmutableMap.builder().put("Position", ELEMENT_POSITION).put("Color", ELEMENT_COLOR).put("UV0", ELEMENT_UV0).build());
      POSITION_TEX_COLOR = new VertexFormat(ImmutableMap.builder().put("Position", ELEMENT_POSITION).put("UV0", ELEMENT_UV0).put("Color", ELEMENT_COLOR).build());
      POSITION_COLOR_TEX_LIGHTMAP = new VertexFormat(ImmutableMap.builder().put("Position", ELEMENT_POSITION).put("Color", ELEMENT_COLOR).put("UV0", ELEMENT_UV0).put("UV2", ELEMENT_UV2).build());
      POSITION_TEX_LIGHTMAP_COLOR = new VertexFormat(ImmutableMap.builder().put("Position", ELEMENT_POSITION).put("UV0", ELEMENT_UV0).put("UV2", ELEMENT_UV2).put("Color", ELEMENT_COLOR).build());
      POSITION_TEX_COLOR_NORMAL = new VertexFormat(ImmutableMap.builder().put("Position", ELEMENT_POSITION).put("UV0", ELEMENT_UV0).put("Color", ELEMENT_COLOR).put("Normal", ELEMENT_NORMAL).put("Padding", ELEMENT_PADDING).build());
   }
}
