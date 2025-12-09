package net.minecraft.client.renderer.gizmos;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.gizmos.GizmoPrimitives;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import org.joml.Vector4f;

public class DrawableGizmoPrimitives implements GizmoPrimitives {
   private final Group opaque = new Group(true);
   private final Group translucent = new Group(false);
   private boolean isEmpty = true;

   public DrawableGizmoPrimitives() {
      super();
   }

   private Group getGroup(int var1) {
      return ARGB.alpha(var1) < 255 ? this.translucent : this.opaque;
   }

   public void addPoint(Vec3 var1, int var2, float var3) {
      this.getGroup(var2).points.add(new Point(var1, var2, var3));
      this.isEmpty = false;
   }

   public void addLine(Vec3 var1, Vec3 var2, int var3, float var4) {
      this.getGroup(var3).lines.add(new Line(var1, var2, var3, var4));
      this.isEmpty = false;
   }

   public void addTriangleFan(Vec3[] var1, int var2) {
      this.getGroup(var2).triangleFans.add(new TriangleFan(var1, var2));
      this.isEmpty = false;
   }

   public void addQuad(Vec3 var1, Vec3 var2, Vec3 var3, Vec3 var4, int var5) {
      this.getGroup(var5).quads.add(new Quad(var1, var2, var3, var4, var5));
      this.isEmpty = false;
   }

   public void addText(Vec3 var1, String var2, TextGizmo.Style var3) {
      this.getGroup(var3.color()).texts.add(new Text(var1, var2, var3));
      this.isEmpty = false;
   }

   public void render(PoseStack var1, MultiBufferSource var2, CameraRenderState var3, Matrix4f var4) {
      this.opaque.render(var1, var2, var3, var4);
      this.translucent.render(var1, var2, var3, var4);
   }

   public boolean isEmpty() {
      return this.isEmpty;
   }

   static record Line(Vec3 start, Vec3 end, int color, float width) {
      Line(Vec3 var1, Vec3 var2, int var3, float var4) {
         super();
         this.start = var1;
         this.end = var2;
         this.color = var3;
         this.width = var4;
      }
   }

   static record TriangleFan(Vec3[] points, int color) {
      TriangleFan(Vec3[] var1, int var2) {
         super();
         this.points = var1;
         this.color = var2;
      }
   }

   static record Quad(Vec3 a, Vec3 b, Vec3 c, Vec3 d, int color) {
      Quad(Vec3 var1, Vec3 var2, Vec3 var3, Vec3 var4, int var5) {
         super();
         this.a = var1;
         this.b = var2;
         this.c = var3;
         this.d = var4;
         this.color = var5;
      }
   }

   static record Text(Vec3 pos, String text, TextGizmo.Style style) {
      final String text;
      final TextGizmo.Style style;

      Text(Vec3 var1, String var2, TextGizmo.Style var3) {
         super();
         this.pos = var1;
         this.text = var2;
         this.style = var3;
      }
   }

   static record Point(Vec3 pos, int color, float size) {
      final Vec3 pos;

      Point(Vec3 var1, int var2, float var3) {
         super();
         this.pos = var1;
         this.color = var2;
         this.size = var3;
      }
   }

   static record Group(boolean opaque, List<Line> lines, List<Quad> quads, List<TriangleFan> triangleFans, List<Text> texts, List<Point> points) {
      final List<Line> lines;
      final List<Quad> quads;
      final List<TriangleFan> triangleFans;
      final List<Text> texts;
      final List<Point> points;

      Group(boolean var1) {
         this(var1, new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList());
      }

      private Group(boolean var1, List<Line> var2, List<Quad> var3, List<TriangleFan> var4, List<Text> var5, List<Point> var6) {
         super();
         this.opaque = var1;
         this.lines = var2;
         this.quads = var3;
         this.triangleFans = var4;
         this.texts = var5;
         this.points = var6;
      }

      public void render(PoseStack var1, MultiBufferSource var2, CameraRenderState var3, Matrix4f var4) {
         this.renderQuads(var1, var2, var3);
         this.renderTriangleFans(var1, var2, var3);
         this.renderLines(var1, var2, var3, var4);
         this.renderTexts(var1, var2, var3);
         this.renderPoints(var1, var2, var3);
      }

      private void renderTexts(PoseStack var1, MultiBufferSource var2, CameraRenderState var3) {
         Minecraft var4 = Minecraft.getInstance();
         Font var5 = var4.font;
         if (var3.initialized) {
            double var6 = var3.pos.x();
            double var8 = var3.pos.y();
            double var10 = var3.pos.z();

            for(Text var13 : this.texts) {
               var1.pushPose();
               var1.translate((float)(var13.pos().x() - var6), (float)(var13.pos().y() - var8), (float)(var13.pos().z() - var10));
               var1.mulPose((Quaternionfc)var3.orientation);
               var1.scale(var13.style.scale() / 16.0F, -var13.style.scale() / 16.0F, var13.style.scale() / 16.0F);
               float var14;
               if (var13.style.adjustLeft().isEmpty()) {
                  var14 = (float)(-var5.width(var13.text)) / 2.0F;
               } else {
                  var14 = (float)(-var13.style.adjustLeft().getAsDouble()) / var13.style.scale();
               }

               var5.drawInBatch((String)var13.text, var14, 0.0F, var13.style.color(), false, var1.last().pose(), var2, Font.DisplayMode.NORMAL, 0, 15728880);
               var1.popPose();
            }

         }
      }

      private void renderLines(PoseStack var1, MultiBufferSource var2, CameraRenderState var3, Matrix4f var4) {
         VertexConsumer var5 = var2.getBuffer(this.opaque ? RenderTypes.lines() : RenderTypes.linesTranslucent());
         PoseStack.Pose var6 = var1.last();
         Vector4f var7 = new Vector4f();
         Vector4f var8 = new Vector4f();
         Vector4f var9 = new Vector4f();
         Vector4f var10 = new Vector4f();
         Vector4f var11 = new Vector4f();
         double var12 = var3.pos.x();
         double var14 = var3.pos.y();
         double var16 = var3.pos.z();

         for(Line var19 : this.lines) {
            var7.set(var19.start().x() - var12, var19.start().y() - var14, var19.start().z() - var16, 1.0);
            var8.set(var19.end().x() - var12, var19.end().y() - var14, var19.end().z() - var16, 1.0);
            var7.mul(var4, var9);
            var8.mul(var4, var10);
            boolean var20 = var9.z > -0.05F;
            boolean var21 = var10.z > -0.05F;
            if (!var20 || !var21) {
               if (var20 || var21) {
                  float var22 = var10.z - var9.z;
                  if (Math.abs(var22) < 1.0E-9F) {
                     continue;
                  }

                  float var23 = Mth.clamp((-0.05F - var9.z) / var22, 0.0F, 1.0F);
                  var7.lerp(var8, var23, var11);
                  if (var20) {
                     var7.set(var11);
                  } else {
                     var8.set(var11);
                  }
               }

               var5.addVertex(var6, var7.x, var7.y, var7.z).setNormal(var6, var8.x - var7.x, var8.y - var7.y, var8.z - var7.z).setColor(var19.color()).setLineWidth(var19.width());
               var5.addVertex(var6, var8.x, var8.y, var8.z).setNormal(var6, var8.x - var7.x, var8.y - var7.y, var8.z - var7.z).setColor(var19.color()).setLineWidth(var19.width());
            }
         }

      }

      private void renderTriangleFans(PoseStack var1, MultiBufferSource var2, CameraRenderState var3) {
         PoseStack.Pose var4 = var1.last();
         double var5 = var3.pos.x();
         double var7 = var3.pos.y();
         double var9 = var3.pos.z();

         for(TriangleFan var12 : this.triangleFans) {
            VertexConsumer var13 = var2.getBuffer(RenderTypes.debugTriangleFan());

            for(Vec3 var17 : var12.points()) {
               var13.addVertex(var4, (float)(var17.x() - var5), (float)(var17.y() - var7), (float)(var17.z() - var9)).setColor(var12.color());
            }
         }

      }

      private void renderQuads(PoseStack var1, MultiBufferSource var2, CameraRenderState var3) {
         VertexConsumer var4 = var2.getBuffer(RenderTypes.debugFilledBox());
         PoseStack.Pose var5 = var1.last();
         double var6 = var3.pos.x();
         double var8 = var3.pos.y();
         double var10 = var3.pos.z();

         for(Quad var13 : this.quads) {
            var4.addVertex(var5, (float)(var13.a().x() - var6), (float)(var13.a().y() - var8), (float)(var13.a().z() - var10)).setColor(var13.color());
            var4.addVertex(var5, (float)(var13.b().x() - var6), (float)(var13.b().y() - var8), (float)(var13.b().z() - var10)).setColor(var13.color());
            var4.addVertex(var5, (float)(var13.c().x() - var6), (float)(var13.c().y() - var8), (float)(var13.c().z() - var10)).setColor(var13.color());
            var4.addVertex(var5, (float)(var13.d().x() - var6), (float)(var13.d().y() - var8), (float)(var13.d().z() - var10)).setColor(var13.color());
         }

      }

      private void renderPoints(PoseStack var1, MultiBufferSource var2, CameraRenderState var3) {
         VertexConsumer var4 = var2.getBuffer(RenderTypes.debugPoint());
         PoseStack.Pose var5 = var1.last();
         double var6 = var3.pos.x();
         double var8 = var3.pos.y();
         double var10 = var3.pos.z();

         for(Point var13 : this.points) {
            var4.addVertex(var5, (float)(var13.pos.x() - var6), (float)(var13.pos.y() - var8), (float)(var13.pos.z() - var10)).setColor(var13.color()).setLineWidth(var13.size());
         }

      }
   }
}
