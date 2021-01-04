package com.mojang.blaze3d.vertex;

public class DefaultVertexFormat {
   public static final VertexFormatElement ELEMENT_POSITION;
   public static final VertexFormatElement ELEMENT_COLOR;
   public static final VertexFormatElement ELEMENT_UV0;
   public static final VertexFormatElement ELEMENT_UV1;
   public static final VertexFormatElement ELEMENT_NORMAL;
   public static final VertexFormatElement ELEMENT_PADDING;
   public static final VertexFormat BLOCK;
   public static final VertexFormat BLOCK_NORMALS;
   public static final VertexFormat ENTITY;
   public static final VertexFormat PARTICLE;
   public static final VertexFormat POSITION;
   public static final VertexFormat POSITION_COLOR;
   public static final VertexFormat POSITION_TEX;
   public static final VertexFormat POSITION_NORMAL;
   public static final VertexFormat POSITION_TEX_COLOR;
   public static final VertexFormat POSITION_TEX_NORMAL;
   public static final VertexFormat POSITION_TEX2_COLOR;
   public static final VertexFormat POSITION_TEX_COLOR_NORMAL;

   static {
      ELEMENT_POSITION = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3);
      ELEMENT_COLOR = new VertexFormatElement(0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);
      ELEMENT_UV0 = new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
      ELEMENT_UV1 = new VertexFormatElement(1, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
      ELEMENT_NORMAL = new VertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3);
      ELEMENT_PADDING = new VertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.PADDING, 1);
      BLOCK = (new VertexFormat()).addElement(ELEMENT_POSITION).addElement(ELEMENT_COLOR).addElement(ELEMENT_UV0).addElement(ELEMENT_UV1);
      BLOCK_NORMALS = (new VertexFormat()).addElement(ELEMENT_POSITION).addElement(ELEMENT_COLOR).addElement(ELEMENT_UV0).addElement(ELEMENT_NORMAL).addElement(ELEMENT_PADDING);
      ENTITY = (new VertexFormat()).addElement(ELEMENT_POSITION).addElement(ELEMENT_UV0).addElement(ELEMENT_NORMAL).addElement(ELEMENT_PADDING);
      PARTICLE = (new VertexFormat()).addElement(ELEMENT_POSITION).addElement(ELEMENT_UV0).addElement(ELEMENT_COLOR).addElement(ELEMENT_UV1);
      POSITION = (new VertexFormat()).addElement(ELEMENT_POSITION);
      POSITION_COLOR = (new VertexFormat()).addElement(ELEMENT_POSITION).addElement(ELEMENT_COLOR);
      POSITION_TEX = (new VertexFormat()).addElement(ELEMENT_POSITION).addElement(ELEMENT_UV0);
      POSITION_NORMAL = (new VertexFormat()).addElement(ELEMENT_POSITION).addElement(ELEMENT_NORMAL).addElement(ELEMENT_PADDING);
      POSITION_TEX_COLOR = (new VertexFormat()).addElement(ELEMENT_POSITION).addElement(ELEMENT_UV0).addElement(ELEMENT_COLOR);
      POSITION_TEX_NORMAL = (new VertexFormat()).addElement(ELEMENT_POSITION).addElement(ELEMENT_UV0).addElement(ELEMENT_NORMAL).addElement(ELEMENT_PADDING);
      POSITION_TEX2_COLOR = (new VertexFormat()).addElement(ELEMENT_POSITION).addElement(ELEMENT_UV0).addElement(ELEMENT_UV1).addElement(ELEMENT_COLOR);
      POSITION_TEX_COLOR_NORMAL = (new VertexFormat()).addElement(ELEMENT_POSITION).addElement(ELEMENT_UV0).addElement(ELEMENT_COLOR).addElement(ELEMENT_NORMAL).addElement(ELEMENT_PADDING);
   }
}
