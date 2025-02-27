package com.mojang.blaze3d.opengl;

import com.mojang.logging.LogUtils;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.util.StringUtil;
import org.lwjgl.opengl.EXTDebugLabel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.KHRDebug;
import org.slf4j.Logger;

public abstract class GlDebugLabel {
   private static final Logger LOGGER = LogUtils.getLogger();

   public GlDebugLabel() {
      super();
   }

   public void applyLabel(GlBuffer var1) {
   }

   public void applyLabel(GlTexture var1) {
   }

   public void applyLabel(GlShaderModule var1) {
   }

   public void applyLabel(GlProgram var1) {
   }

   public void applyLabel(VertexArrayCache.VertexArray var1) {
   }

   public static GlDebugLabel create(GLCapabilities var0, boolean var1, Set<String> var2) {
      if (var1) {
         if (var0.GL_KHR_debug && GlDevice.USE_GL_KHR_debug) {
            var2.add("GL_KHR_debug");
            return new Core();
         }

         if (var0.GL_EXT_debug_label && GlDevice.USE_GL_EXT_debug_label) {
            var2.add("GL_EXT_debug_label");
            return new Ext();
         }

         LOGGER.warn("Debug labels unavailable: neither KHR_debug nor EXT_debug_label are supported");
      }

      return new Empty();
   }

   public boolean exists() {
      return false;
   }

   static class Empty extends GlDebugLabel {
      Empty() {
         super();
      }
   }

   static class Core extends GlDebugLabel {
      private final int maxLabelLength = GL11.glGetInteger(33512);

      Core() {
         super();
      }

      public void applyLabel(GlBuffer var1) {
         var1.ensureBufferExists();
         Supplier var2 = var1.label;
         if (var2 != null) {
            KHRDebug.glObjectLabel(33504, var1.handle, StringUtil.truncateStringIfNecessary((String)var2.get(), this.maxLabelLength, true));
         }

      }

      public void applyLabel(GlTexture var1) {
         KHRDebug.glObjectLabel(5890, var1.id, StringUtil.truncateStringIfNecessary(var1.getLabel(), this.maxLabelLength, true));
      }

      public void applyLabel(GlShaderModule var1) {
         KHRDebug.glObjectLabel(33505, var1.getShaderId(), StringUtil.truncateStringIfNecessary(var1.getDebugLabel(), this.maxLabelLength, true));
      }

      public void applyLabel(GlProgram var1) {
         KHRDebug.glObjectLabel(33506, var1.getProgramId(), StringUtil.truncateStringIfNecessary(var1.getDebugLabel(), this.maxLabelLength, true));
      }

      public void applyLabel(VertexArrayCache.VertexArray var1) {
         KHRDebug.glObjectLabel(32884, var1.id, StringUtil.truncateStringIfNecessary(var1.format.toString(), this.maxLabelLength, true));
      }

      public boolean exists() {
         return true;
      }
   }

   static class Ext extends GlDebugLabel {
      Ext() {
         super();
      }

      public void applyLabel(GlBuffer var1) {
         var1.ensureBufferExists();
         Supplier var2 = var1.label;
         if (var2 != null) {
            EXTDebugLabel.glLabelObjectEXT(37201, var1.handle, StringUtil.truncateStringIfNecessary((String)var2.get(), 256, true));
         }

      }

      public void applyLabel(GlTexture var1) {
         EXTDebugLabel.glLabelObjectEXT(5890, var1.id, StringUtil.truncateStringIfNecessary(var1.getLabel(), 256, true));
      }

      public void applyLabel(GlShaderModule var1) {
         EXTDebugLabel.glLabelObjectEXT(35656, var1.getShaderId(), StringUtil.truncateStringIfNecessary(var1.getDebugLabel(), 256, true));
      }

      public void applyLabel(GlProgram var1) {
         EXTDebugLabel.glLabelObjectEXT(35648, var1.getProgramId(), StringUtil.truncateStringIfNecessary(var1.getDebugLabel(), 256, true));
      }

      public void applyLabel(VertexArrayCache.VertexArray var1) {
         EXTDebugLabel.glLabelObjectEXT(32884, var1.id, StringUtil.truncateStringIfNecessary(var1.format.toString(), 256, true));
      }

      public boolean exists() {
         return true;
      }
   }
}
