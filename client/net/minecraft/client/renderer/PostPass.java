package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;

public class PostPass implements AutoCloseable {
   private final EffectInstance effect;
   public final RenderTarget inTarget;
   public final RenderTarget outTarget;
   private final List<Object> auxAssets = Lists.newArrayList();
   private final List<String> auxNames = Lists.newArrayList();
   private final List<Integer> auxWidths = Lists.newArrayList();
   private final List<Integer> auxHeights = Lists.newArrayList();
   private Matrix4f shaderOrthoMatrix;

   public PostPass(ResourceManager var1, String var2, RenderTarget var3, RenderTarget var4) throws IOException {
      super();
      this.effect = new EffectInstance(var1, var2);
      this.inTarget = var3;
      this.outTarget = var4;
   }

   public void close() {
      this.effect.close();
   }

   public void addAuxAsset(String var1, Object var2, int var3, int var4) {
      this.auxNames.add(this.auxNames.size(), var1);
      this.auxAssets.add(this.auxAssets.size(), var2);
      this.auxWidths.add(this.auxWidths.size(), var3);
      this.auxHeights.add(this.auxHeights.size(), var4);
   }

   private void prepareState() {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.disableDepthTest();
      GlStateManager.disableAlphaTest();
      GlStateManager.disableFog();
      GlStateManager.disableLighting();
      GlStateManager.disableColorMaterial();
      GlStateManager.enableTexture();
      GlStateManager.bindTexture(0);
   }

   public void setOrthoMatrix(Matrix4f var1) {
      this.shaderOrthoMatrix = var1;
   }

   public void process(float var1) {
      this.prepareState();
      this.inTarget.unbindWrite();
      float var2 = (float)this.outTarget.width;
      float var3 = (float)this.outTarget.height;
      GlStateManager.viewport(0, 0, (int)var2, (int)var3);
      this.effect.setSampler("DiffuseSampler", this.inTarget);

      for(int var4 = 0; var4 < this.auxAssets.size(); ++var4) {
         this.effect.setSampler((String)this.auxNames.get(var4), this.auxAssets.get(var4));
         this.effect.safeGetUniform("AuxSize" + var4).set((float)(Integer)this.auxWidths.get(var4), (float)(Integer)this.auxHeights.get(var4));
      }

      this.effect.safeGetUniform("ProjMat").set(this.shaderOrthoMatrix);
      this.effect.safeGetUniform("InSize").set((float)this.inTarget.width, (float)this.inTarget.height);
      this.effect.safeGetUniform("OutSize").set(var2, var3);
      this.effect.safeGetUniform("Time").set(var1);
      Minecraft var9 = Minecraft.getInstance();
      this.effect.safeGetUniform("ScreenSize").set((float)var9.window.getWidth(), (float)var9.window.getHeight());
      this.effect.apply();
      this.outTarget.clear(Minecraft.ON_OSX);
      this.outTarget.bindWrite(false);
      GlStateManager.depthMask(false);
      GlStateManager.colorMask(true, true, true, true);
      Tesselator var5 = Tesselator.getInstance();
      BufferBuilder var6 = var5.getBuilder();
      var6.begin(7, DefaultVertexFormat.POSITION_COLOR);
      var6.vertex(0.0D, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
      var6.vertex((double)var2, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
      var6.vertex((double)var2, (double)var3, 500.0D).color(255, 255, 255, 255).endVertex();
      var6.vertex(0.0D, (double)var3, 500.0D).color(255, 255, 255, 255).endVertex();
      var5.end();
      GlStateManager.depthMask(true);
      GlStateManager.colorMask(true, true, true, true);
      this.effect.clear();
      this.outTarget.unbindWrite();
      this.inTarget.unbindRead();
      Iterator var7 = this.auxAssets.iterator();

      while(var7.hasNext()) {
         Object var8 = var7.next();
         if (var8 instanceof RenderTarget) {
            ((RenderTarget)var8).unbindRead();
         }
      }

   }

   public EffectInstance getEffect() {
      return this.effect;
   }
}
