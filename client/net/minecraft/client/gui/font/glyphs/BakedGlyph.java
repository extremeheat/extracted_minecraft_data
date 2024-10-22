package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import org.joml.Matrix4f;

public class BakedGlyph {
   private final GlyphRenderTypes renderTypes;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private final float left;
   private final float right;
   private final float up;
   private final float down;

   public BakedGlyph(GlyphRenderTypes var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      super();
      this.renderTypes = var1;
      this.u0 = var2;
      this.u1 = var3;
      this.v0 = var4;
      this.v1 = var5;
      this.left = var6;
      this.right = var7;
      this.up = var8;
      this.down = var9;
   }

   public void renderChar(BakedGlyph.GlyphInstance var1, Matrix4f var2, VertexConsumer var3, int var4) {
      Style var5 = var1.style();
      boolean var6 = var5.isItalic();
      float var7 = var1.x();
      float var8 = var1.y();
      int var9 = var1.color();
      this.render(var6, var7, var8, var2, var3, var9, var4);
      if (var5.isBold()) {
         this.render(var6, var7 + var1.boldOffset(), var8, var2, var3, var9, var4);
      }
   }

   private void render(boolean var1, float var2, float var3, Matrix4f var4, VertexConsumer var5, int var6, int var7) {
      float var8 = var2 + this.left;
      float var9 = var2 + this.right;
      float var10 = var3 + this.up;
      float var11 = var3 + this.down;
      float var12 = var1 ? 1.0F - 0.25F * this.up : 0.0F;
      float var13 = var1 ? 1.0F - 0.25F * this.down : 0.0F;
      var5.addVertex(var4, var8 + var12, var10, 0.0F).setColor(var6).setUv(this.u0, this.v0).setLight(var7);
      var5.addVertex(var4, var8 + var13, var11, 0.0F).setColor(var6).setUv(this.u0, this.v1).setLight(var7);
      var5.addVertex(var4, var9 + var13, var11, 0.0F).setColor(var6).setUv(this.u1, this.v1).setLight(var7);
      var5.addVertex(var4, var9 + var12, var10, 0.0F).setColor(var6).setUv(this.u1, this.v0).setLight(var7);
   }

   public void renderEffect(BakedGlyph.Effect var1, Matrix4f var2, VertexConsumer var3, int var4) {
      var3.addVertex(var2, var1.x0, var1.y0, var1.depth).setColor(var1.color).setUv(this.u0, this.v0).setLight(var4);
      var3.addVertex(var2, var1.x1, var1.y0, var1.depth).setColor(var1.color).setUv(this.u0, this.v1).setLight(var4);
      var3.addVertex(var2, var1.x1, var1.y1, var1.depth).setColor(var1.color).setUv(this.u1, this.v1).setLight(var4);
      var3.addVertex(var2, var1.x0, var1.y1, var1.depth).setColor(var1.color).setUv(this.u1, this.v0).setLight(var4);
   }

   public RenderType renderType(Font.DisplayMode var1) {
      return this.renderTypes.select(var1);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
