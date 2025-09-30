package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.lwjgl.opengl.ARBVertexAttribBinding;
import org.lwjgl.opengl.GLCapabilities;

public abstract class VertexArrayCache {
   public VertexArrayCache() {
      super();
   }

   public static VertexArrayCache create(GLCapabilities var0, GlDebugLabel var1, Set<String> var2) {
      if (var0.GL_ARB_vertex_attrib_binding && GlDevice.USE_GL_ARB_vertex_attrib_binding) {
         var2.add("GL_ARB_vertex_attrib_binding");
         return new Separate(var1);
      } else {
         return new Emulated(var1);
      }
   }

   public abstract void bindVertexArray(VertexFormat var1, @Nullable GlBuffer var2);

   static class Emulated extends VertexArrayCache {
      private final Map<VertexFormat, VertexArray> cache = new HashMap();
      private final GlDebugLabel debugLabels;

      public Emulated(GlDebugLabel var1) {
         super();
         this.debugLabels = var1;
      }

      public void bindVertexArray(VertexFormat var1, @Nullable GlBuffer var2) {
         VertexArray var3 = (VertexArray)this.cache.get(var1);
         if (var3 == null) {
            int var4 = GlStateManager._glGenVertexArrays();
            GlStateManager._glBindVertexArray(var4);
            if (var2 != null) {
               GlStateManager._glBindBuffer(34962, var2.handle);
               setupCombinedAttributes(var1, true);
            }

            VertexArray var5 = new VertexArray(var4, var1, var2);
            this.debugLabels.applyLabel(var5);
            this.cache.put(var1, var5);
         } else {
            GlStateManager._glBindVertexArray(var3.id);
            if (var2 != null && var3.lastVertexBuffer != var2) {
               GlStateManager._glBindBuffer(34962, var2.handle);
               var3.lastVertexBuffer = var2;
               setupCombinedAttributes(var1, false);
            }

         }
      }

      private static void setupCombinedAttributes(VertexFormat var0, boolean var1) {
         int var2 = var0.getVertexSize();
         List var3 = var0.getElements();

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            VertexFormatElement var5 = (VertexFormatElement)var3.get(var4);
            if (var1) {
               GlStateManager._enableVertexAttribArray(var4);
            }

            switch (var5.usage()) {
               case POSITION:
               case GENERIC:
               case UV:
                  if (var5.type() == VertexFormatElement.Type.FLOAT) {
                     GlStateManager._vertexAttribPointer(var4, var5.count(), GlConst.toGl(var5.type()), false, var2, (long)var0.getOffset(var5));
                  } else {
                     GlStateManager._vertexAttribIPointer(var4, var5.count(), GlConst.toGl(var5.type()), var2, (long)var0.getOffset(var5));
                  }
                  break;
               case NORMAL:
               case COLOR:
                  GlStateManager._vertexAttribPointer(var4, var5.count(), GlConst.toGl(var5.type()), true, var2, (long)var0.getOffset(var5));
            }
         }

      }
   }

   static class Separate extends VertexArrayCache {
      private final Map<VertexFormat, VertexArray> cache = new HashMap();
      private final GlDebugLabel debugLabels;
      private final boolean needsMesaWorkaround;

      public Separate(GlDebugLabel var1) {
         super();
         this.debugLabels = var1;
         if ("Mesa".equals(GlStateManager._getString(7936))) {
            String var2 = GlStateManager._getString(7938);
            this.needsMesaWorkaround = var2.contains("25.0.0") || var2.contains("25.0.1") || var2.contains("25.0.2");
         } else {
            this.needsMesaWorkaround = false;
         }

      }

      public void bindVertexArray(VertexFormat var1, @Nullable GlBuffer var2) {
         VertexArray var3 = (VertexArray)this.cache.get(var1);
         if (var3 != null) {
            GlStateManager._glBindVertexArray(var3.id);
            if (var2 != null && var3.lastVertexBuffer != var2) {
               if (this.needsMesaWorkaround && var3.lastVertexBuffer != null && var3.lastVertexBuffer.handle == var2.handle) {
                  ARBVertexAttribBinding.glBindVertexBuffer(0, 0, 0L, 0);
               }

               ARBVertexAttribBinding.glBindVertexBuffer(0, var2.handle, 0L, var1.getVertexSize());
               var3.lastVertexBuffer = var2;
            }

         } else {
            int var4 = GlStateManager._glGenVertexArrays();
            GlStateManager._glBindVertexArray(var4);
            if (var2 != null) {
               List var5 = var1.getElements();

               for(int var6 = 0; var6 < var5.size(); ++var6) {
                  VertexFormatElement var7 = (VertexFormatElement)var5.get(var6);
                  GlStateManager._enableVertexAttribArray(var6);
                  switch (var7.usage()) {
                     case POSITION:
                     case GENERIC:
                     case UV:
                        if (var7.type() == VertexFormatElement.Type.FLOAT) {
                           ARBVertexAttribBinding.glVertexAttribFormat(var6, var7.count(), GlConst.toGl(var7.type()), false, var1.getOffset(var7));
                        } else {
                           ARBVertexAttribBinding.glVertexAttribIFormat(var6, var7.count(), GlConst.toGl(var7.type()), var1.getOffset(var7));
                        }
                        break;
                     case NORMAL:
                     case COLOR:
                        ARBVertexAttribBinding.glVertexAttribFormat(var6, var7.count(), GlConst.toGl(var7.type()), true, var1.getOffset(var7));
                  }

                  ARBVertexAttribBinding.glVertexAttribBinding(var6, 0);
               }
            }

            if (var2 != null) {
               ARBVertexAttribBinding.glBindVertexBuffer(0, var2.handle, 0L, var1.getVertexSize());
            }

            VertexArray var8 = new VertexArray(var4, var1, var2);
            this.debugLabels.applyLabel(var8);
            this.cache.put(var1, var8);
         }
      }
   }

   public static class VertexArray {
      final int id;
      final VertexFormat format;
      @Nullable
      GlBuffer lastVertexBuffer;

      VertexArray(int var1, VertexFormat var2, @Nullable GlBuffer var3) {
         super();
         this.id = var1;
         this.format = var2;
         this.lastVertexBuffer = var3;
      }
   }
}
