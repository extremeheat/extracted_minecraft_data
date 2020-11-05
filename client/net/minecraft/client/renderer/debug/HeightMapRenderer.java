package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Vector3f;
import java.util.Iterator;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

public class HeightMapRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public HeightMapRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      ClientLevel var9 = this.minecraft.level;
      RenderSystem.pushMatrix();
      RenderSystem.disableBlend();
      RenderSystem.disableTexture();
      RenderSystem.enableDepthTest();
      BlockPos var10 = new BlockPos(var3, 0.0D, var7);
      Tesselator var11 = Tesselator.getInstance();
      BufferBuilder var12 = var11.getBuilder();
      var12.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

      for(int var13 = -2; var13 <= 2; ++var13) {
         for(int var14 = -2; var14 <= 2; ++var14) {
            ChunkAccess var15 = var9.getChunk(var10.offset(var13 * 16, 0, var14 * 16));
            Iterator var16 = var15.getHeightmaps().iterator();

            while(var16.hasNext()) {
               Entry var17 = (Entry)var16.next();
               Heightmap.Types var18 = (Heightmap.Types)var17.getKey();
               ChunkPos var19 = var15.getPos();
               Vector3f var20 = this.getColor(var18);

               for(int var21 = 0; var21 < 16; ++var21) {
                  for(int var22 = 0; var22 < 16; ++var22) {
                     int var23 = SectionPos.sectionToBlockCoord(var19.x, var21);
                     int var24 = SectionPos.sectionToBlockCoord(var19.z, var22);
                     float var25 = (float)((double)((float)var9.getHeight(var18, var23, var24) + (float)var18.ordinal() * 0.09375F) - var5);
                     LevelRenderer.addChainedFilledBoxVertices(var12, (double)((float)var23 + 0.25F) - var3, (double)var25, (double)((float)var24 + 0.25F) - var7, (double)((float)var23 + 0.75F) - var3, (double)(var25 + 0.09375F), (double)((float)var24 + 0.75F) - var7, var20.x(), var20.y(), var20.z(), 1.0F);
                  }
               }
            }
         }
      }

      var11.end();
      RenderSystem.enableTexture();
      RenderSystem.popMatrix();
   }

   private Vector3f getColor(Heightmap.Types var1) {
      switch(var1) {
      case WORLD_SURFACE_WG:
         return new Vector3f(1.0F, 1.0F, 0.0F);
      case OCEAN_FLOOR_WG:
         return new Vector3f(1.0F, 0.0F, 1.0F);
      case WORLD_SURFACE:
         return new Vector3f(0.0F, 0.7F, 0.0F);
      case OCEAN_FLOOR:
         return new Vector3f(0.0F, 0.0F, 0.5F);
      case MOTION_BLOCKING:
         return new Vector3f(0.0F, 0.3F, 0.3F);
      case MOTION_BLOCKING_NO_LEAVES:
         return new Vector3f(0.0F, 0.5F, 0.5F);
      default:
         return new Vector3f(0.0F, 0.0F, 0.0F);
      }
   }
}
