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

   private Group getGroup(final int color) {
      return ARGB.alpha(color) < 255 ? this.translucent : this.opaque;
   }

   public void addPoint(final Vec3 pos, final int color, final float size) {
      this.getGroup(color).points.add(new Point(pos, color, size));
      this.isEmpty = false;
   }

   public void addLine(final Vec3 start, final Vec3 end, final int color, final float width) {
      this.getGroup(color).lines.add(new Line(start, end, color, width));
      this.isEmpty = false;
   }

   public void addTriangleFan(final Vec3[] points, final int color) {
      this.getGroup(color).triangleFans.add(new TriangleFan(points, color));
      this.isEmpty = false;
   }

   public void addQuad(final Vec3 a, final Vec3 b, final Vec3 c, final Vec3 d, final int color) {
      this.getGroup(color).quads.add(new Quad(a, b, c, d, color));
      this.isEmpty = false;
   }

   public void addText(final Vec3 pos, final String text, final TextGizmo.Style style) {
      this.getGroup(style.color()).texts.add(new Text(pos, text, style));
      this.isEmpty = false;
   }

   public void render(final PoseStack poseStack, final MultiBufferSource bufferSource, final CameraRenderState camera, final Matrix4f modelViewMatrix) {
      this.opaque.render(poseStack, bufferSource, camera, modelViewMatrix);
      this.translucent.render(poseStack, bufferSource, camera, modelViewMatrix);
   }

   public boolean isEmpty() {
      return this.isEmpty;
   }

   private static record Line(Vec3 start, Vec3 end, int color, float width) {
      private Line {
         super();
      }
   }

   private static record TriangleFan(Vec3[] points, int color) {
      private TriangleFan {
         super();
      }
   }

   private static record Quad(Vec3 a, Vec3 b, Vec3 c, Vec3 d, int color) {
      private Quad {
         super();
      }
   }

   private static record Text(Vec3 pos, String text, TextGizmo.Style style) {
      private Text {
         super();
      }
   }

   private static record Point(Vec3 pos, int color, float size) {
      private Point {
         super();
      }
   }

   private static record Group(boolean opaque, List<Line> lines, List<Quad> quads, List<TriangleFan> triangleFans, List<Text> texts, List<Point> points) {
      private Group(final boolean opaque) {
         this(opaque, new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList());
      }

      private Group {
         super();
      }

      public void render(final PoseStack poseStack, final MultiBufferSource bufferSource, final CameraRenderState camera, final Matrix4f modelViewMatrix) {
         this.renderQuads(poseStack, bufferSource, camera);
         this.renderTriangleFans(poseStack, bufferSource, camera);
         this.renderLines(poseStack, bufferSource, camera, modelViewMatrix);
         this.renderTexts(poseStack, bufferSource, camera);
         this.renderPoints(poseStack, bufferSource, camera);
      }

      private void renderTexts(final PoseStack poseStack, final MultiBufferSource bufferSource, final CameraRenderState camera) {
         Minecraft minecraft = Minecraft.getInstance();
         Font font = minecraft.font;
         if (camera.initialized) {
            double camX = camera.pos.x();
            double camY = camera.pos.y();
            double camZ = camera.pos.z();

            for(Text text : this.texts) {
               poseStack.pushPose();
               poseStack.translate((float)(text.pos().x() - camX), (float)(text.pos().y() - camY), (float)(text.pos().z() - camZ));
               poseStack.mulPose((Quaternionfc)camera.orientation);
               poseStack.scale(text.style.scale() / 16.0F, -text.style.scale() / 16.0F, text.style.scale() / 16.0F);
               float fontX;
               if (text.style.adjustLeft().isEmpty()) {
                  fontX = (float)(-font.width(text.text)) / 2.0F;
               } else {
                  fontX = (float)(-text.style.adjustLeft().getAsDouble()) / text.style.scale();
               }

               font.drawInBatch((String)text.text, fontX, 0.0F, text.style.color(), false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
               poseStack.popPose();
            }

         }
      }

      private void renderLines(final PoseStack poseStack, final MultiBufferSource bufferSource, final CameraRenderState camera, final Matrix4f modelViewMatrix) {
         VertexConsumer builder = bufferSource.getBuffer(this.opaque ? RenderTypes.lines() : RenderTypes.linesTranslucent());
         PoseStack.Pose pose = poseStack.last();
         Vector4f start = new Vector4f();
         Vector4f end = new Vector4f();
         Vector4f startViewSpace = new Vector4f();
         Vector4f endViewSpace = new Vector4f();
         Vector4f intersectionInWorld = new Vector4f();
         double camX = camera.pos.x();
         double camY = camera.pos.y();
         double camZ = camera.pos.z();

         for(Line line : this.lines) {
            start.set(line.start().x() - camX, line.start().y() - camY, line.start().z() - camZ, 1.0);
            end.set(line.end().x() - camX, line.end().y() - camY, line.end().z() - camZ, 1.0);
            start.mul(modelViewMatrix, startViewSpace);
            end.mul(modelViewMatrix, endViewSpace);
            boolean startIsBehindCamera = startViewSpace.z > -0.05F;
            boolean endIsBehindCamera = endViewSpace.z > -0.05F;
            if (!startIsBehindCamera || !endIsBehindCamera) {
               if (startIsBehindCamera || endIsBehindCamera) {
                  float denom = endViewSpace.z - startViewSpace.z;
                  if (Math.abs(denom) < 1.0E-9F) {
                     continue;
                  }

                  float intersection = Mth.clamp((-0.05F - startViewSpace.z) / denom, 0.0F, 1.0F);
                  start.lerp(end, intersection, intersectionInWorld);
                  if (startIsBehindCamera) {
                     start.set(intersectionInWorld);
                  } else {
                     end.set(intersectionInWorld);
                  }
               }

               builder.addVertex(pose, start.x, start.y, start.z).setNormal(pose, end.x - start.x, end.y - start.y, end.z - start.z).setColor(line.color()).setLineWidth(line.width());
               builder.addVertex(pose, end.x, end.y, end.z).setNormal(pose, end.x - start.x, end.y - start.y, end.z - start.z).setColor(line.color()).setLineWidth(line.width());
            }
         }

      }

      private void renderTriangleFans(final PoseStack poseStack, final MultiBufferSource bufferSource, final CameraRenderState camera) {
         PoseStack.Pose pose = poseStack.last();
         double camX = camera.pos.x();
         double camY = camera.pos.y();
         double camZ = camera.pos.z();

         for(TriangleFan triangleFan : this.triangleFans) {
            VertexConsumer builder = bufferSource.getBuffer(RenderTypes.debugTriangleFan());

            for(Vec3 point : triangleFan.points()) {
               builder.addVertex(pose, (float)(point.x() - camX), (float)(point.y() - camY), (float)(point.z() - camZ)).setColor(triangleFan.color());
            }
         }

      }

      private void renderQuads(final PoseStack poseStack, final MultiBufferSource bufferSource, final CameraRenderState camera) {
         VertexConsumer builder = bufferSource.getBuffer(RenderTypes.debugFilledBox());
         PoseStack.Pose pose = poseStack.last();
         double camX = camera.pos.x();
         double camY = camera.pos.y();
         double camZ = camera.pos.z();

         for(Quad quad : this.quads) {
            builder.addVertex(pose, (float)(quad.a().x() - camX), (float)(quad.a().y() - camY), (float)(quad.a().z() - camZ)).setColor(quad.color());
            builder.addVertex(pose, (float)(quad.b().x() - camX), (float)(quad.b().y() - camY), (float)(quad.b().z() - camZ)).setColor(quad.color());
            builder.addVertex(pose, (float)(quad.c().x() - camX), (float)(quad.c().y() - camY), (float)(quad.c().z() - camZ)).setColor(quad.color());
            builder.addVertex(pose, (float)(quad.d().x() - camX), (float)(quad.d().y() - camY), (float)(quad.d().z() - camZ)).setColor(quad.color());
         }

      }

      private void renderPoints(final PoseStack poseStack, final MultiBufferSource bufferSource, final CameraRenderState camera) {
         VertexConsumer builder = bufferSource.getBuffer(RenderTypes.debugPoint());
         PoseStack.Pose pose = poseStack.last();
         double camX = camera.pos.x();
         double camY = camera.pos.y();
         double camZ = camera.pos.z();

         for(Point point : this.points) {
            builder.addVertex(pose, (float)(point.pos.x() - camX), (float)(point.pos.y() - camY), (float)(point.pos.z() - camZ)).setColor(point.color()).setLineWidth(point.size());
         }

      }
   }
}
