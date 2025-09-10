package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import org.joml.Vector3f;

public class HeightMapRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private static final int CHUNK_DIST = 2;
   private static final float BOX_HEIGHT = 0.09375F;

   public HeightMapRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9) {
      ClientLevel var10 = this.minecraft.level;
      VertexConsumer var11 = var2.getBuffer(RenderType.debugFilledBox());
      BlockPos var12 = BlockPos.containing(var3, 0.0, var7);

      for(int var13 = -2; var13 <= 2; ++var13) {
         for(int var14 = -2; var14 <= 2; ++var14) {
            ChunkAccess var15 = var10.getChunk(var12.offset(var13 * 16, 0, var14 * 16));

            for(Map.Entry var17 : var15.getHeightmaps()) {
               Heightmap.Types var18 = (Heightmap.Types)var17.getKey();
               ChunkPos var19 = var15.getPos();
               Vector3f var20 = this.getColor(var18);

               for(int var21 = 0; var21 < 16; ++var21) {
                  for(int var22 = 0; var22 < 16; ++var22) {
                     int var23 = SectionPos.sectionToBlockCoord(var19.x, var21);
                     int var24 = SectionPos.sectionToBlockCoord(var19.z, var22);
                     float var25 = (float)((double)((float)var10.getHeight(var18, var23, var24) + (float)var18.ordinal() * 0.09375F) - var5);
                     ShapeRenderer.addChainedFilledBoxVertices(var1, var11, (double)((float)var23 + 0.25F) - var3, (double)var25, (double)((float)var24 + 0.25F) - var7, (double)((float)var23 + 0.75F) - var3, (double)(var25 + 0.09375F), (double)((float)var24 + 0.75F) - var7, var20.x(), var20.y(), var20.z(), 1.0F);
                  }
               }
            }
         }
      }

   }

   private Vector3f getColor(Heightmap.Types var1) {
      Vector3f var10000;
      switch (var1) {
         case WORLD_SURFACE_WG -> var10000 = new Vector3f(1.0F, 1.0F, 0.0F);
         case OCEAN_FLOOR_WG -> var10000 = new Vector3f(1.0F, 0.0F, 1.0F);
         case WORLD_SURFACE -> var10000 = new Vector3f(0.0F, 0.7F, 0.0F);
         case OCEAN_FLOOR -> var10000 = new Vector3f(0.0F, 0.0F, 0.5F);
         case MOTION_BLOCKING -> var10000 = new Vector3f(0.0F, 0.3F, 0.3F);
         case MOTION_BLOCKING_NO_LEAVES -> var10000 = new Vector3f(0.0F, 0.5F, 0.5F);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
