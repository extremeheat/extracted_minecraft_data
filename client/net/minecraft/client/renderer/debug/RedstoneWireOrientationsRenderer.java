package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.network.protocol.common.custom.RedstoneWireOrientationsDebugPayload;
import net.minecraft.world.level.redstone.Orientation;
import org.joml.Vector3f;

public class RedstoneWireOrientationsRenderer implements DebugRenderer.SimpleDebugRenderer {
   public static final int TIMEOUT = 200;
   private final Minecraft minecraft;
   private final List<RedstoneWireOrientationsDebugPayload> updatedWires = Lists.newArrayList();

   RedstoneWireOrientationsRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void addWireOrientations(RedstoneWireOrientationsDebugPayload var1) {
      this.updatedWires.add(var1);
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      VertexConsumer var9 = var2.getBuffer(RenderType.lines());
      long var10 = this.minecraft.level.getGameTime();
      Iterator var12 = this.updatedWires.iterator();

      while(var12.hasNext()) {
         RedstoneWireOrientationsDebugPayload var13 = (RedstoneWireOrientationsDebugPayload)var12.next();
         long var14 = var10 - var13.time();
         if (var14 > 200L) {
            var12.remove();
         } else {
            for(RedstoneWireOrientationsDebugPayload.Wire var17 : var13.wires()) {
               Vector3f var18 = var17.pos().getBottomCenter().subtract(var3, var5 - 0.1, var7).toVector3f();
               Orientation var19 = var17.orientation();
               ShapeRenderer.renderVector(var1, var9, var18, var19.getFront().getUnitVec3().scale(0.5), -16776961);
               ShapeRenderer.renderVector(var1, var9, var18, var19.getUp().getUnitVec3().scale(0.4), -65536);
               ShapeRenderer.renderVector(var1, var9, var18, var19.getSide().getUnitVec3().scale(0.3), -256);
            }
         }
      }

   }
}
