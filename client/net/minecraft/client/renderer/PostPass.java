package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;

public class PostPass implements AutoCloseable {
   private final EffectInstance effect;
   public final RenderTarget inTarget;
   public final RenderTarget outTarget;
   private final List<IntSupplier> auxAssets = Lists.newArrayList();
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

   public void addAuxAsset(String var1, IntSupplier var2, int var3, int var4) {
      this.auxNames.add(this.auxNames.size(), var1);
      this.auxAssets.add(this.auxAssets.size(), var2);
      this.auxWidths.add(this.auxWidths.size(), var3);
      this.auxHeights.add(this.auxHeights.size(), var4);
   }

   public void setOrthoMatrix(Matrix4f var1) {
      this.shaderOrthoMatrix = var1;
   }

   public void process(float var1) {
      this.inTarget.unbindWrite();
      float var2 = (float)this.outTarget.width;
      float var3 = (float)this.outTarget.height;
      RenderSystem.viewport(0, 0, (int)var2, (int)var3);
      RenderTarget var10002 = this.inTarget;
      this.effect.setSampler("DiffuseSampler", var10002::getColorTextureId);

      for(int var4 = 0; var4 < this.auxAssets.size(); ++var4) {
         this.effect.setSampler((String)this.auxNames.get(var4), (IntSupplier)this.auxAssets.get(var4));
         this.effect.safeGetUniform("AuxSize" + var4).set((float)(Integer)this.auxWidths.get(var4), (float)(Integer)this.auxHeights.get(var4));
      }

      this.effect.safeGetUniform("ProjMat").set(this.shaderOrthoMatrix);
      this.effect.safeGetUniform("InSize").set((float)this.inTarget.width, (float)this.inTarget.height);
      this.effect.safeGetUniform("OutSize").set(var2, var3);
      this.effect.safeGetUniform("Time").set(var1);
      Minecraft var8 = Minecraft.getInstance();
      this.effect.safeGetUniform("ScreenSize").set((float)var8.getWindow().getWidth(), (float)var8.getWindow().getHeight());
      this.effect.apply();
      this.outTarget.clear(Minecraft.ON_OSX);
      this.outTarget.bindWrite(false);
      RenderSystem.depthFunc(519);
      BufferBuilder var5 = Tesselator.getInstance().getBuilder();
      var5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      var5.vertex(0.0D, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
      var5.vertex((double)var2, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
      var5.vertex((double)var2, (double)var3, 500.0D).color(255, 255, 255, 255).endVertex();
      var5.vertex(0.0D, (double)var3, 500.0D).color(255, 255, 255, 255).endVertex();
      var5.end();
      BufferUploader.end(var5);
      RenderSystem.depthFunc(515);
      this.effect.clear();
      this.outTarget.unbindWrite();
      this.inTarget.unbindRead();
      Iterator var6 = this.auxAssets.iterator();

      while(var6.hasNext()) {
         Object var7 = var6.next();
         if (var7 instanceof RenderTarget) {
            ((RenderTarget)var7).unbindRead();
         }
      }

   }

   public EffectInstance getEffect() {
      return this.effect;
   }
}
