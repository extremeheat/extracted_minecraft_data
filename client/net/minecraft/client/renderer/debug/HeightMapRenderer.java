package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
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

   @Override
   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      ClientLevel var9 = this.minecraft.level;
      VertexConsumer var10 = var2.getBuffer(RenderType.debugFilledBox());
      BlockPos var11 = BlockPos.containing(var3, 0.0, var7);

      for(int var12 = -2; var12 <= 2; ++var12) {
         for(int var13 = -2; var13 <= 2; ++var13) {
            ChunkAccess var14 = var9.getChunk(var11.offset(var12 * 16, 0, var13 * 16));

            for(Entry var16 : var14.getHeightmaps()) {
               Heightmap.Types var17 = (Heightmap.Types)var16.getKey();
               ChunkPos var18 = var14.getPos();
               Vector3f var19 = this.getColor(var17);

               for(int var20 = 0; var20 < 16; ++var20) {
                  for(int var21 = 0; var21 < 16; ++var21) {
                     int var22 = SectionPos.sectionToBlockCoord(var18.x, var20);
                     int var23 = SectionPos.sectionToBlockCoord(var18.z, var21);
                     float var24 = (float)((double)((float)var9.getHeight(var17, var22, var23) + (float)var17.ordinal() * 0.09375F) - var5);
                     LevelRenderer.addChainedFilledBoxVertices(
                        var1,
                        var10,
                        (double)((float)var22 + 0.25F) - var3,
                        (double)var24,
                        (double)((float)var23 + 0.25F) - var7,
                        (double)((float)var22 + 0.75F) - var3,
                        (double)(var24 + 0.09375F),
                        (double)((float)var23 + 0.75F) - var7,
                        var19.x(),
                        var19.y(),
                        var19.z(),
                        1.0F
                     );
                  }
               }
            }
         }
      }
   }

   private Vector3f getColor(Heightmap.Types var1) {
      return switch(var1) {
         case WORLD_SURFACE_WG -> new Vector3f(1.0F, 1.0F, 0.0F);
         case OCEAN_FLOOR_WG -> new Vector3f(1.0F, 0.0F, 1.0F);
         case WORLD_SURFACE -> new Vector3f(0.0F, 0.7F, 0.0F);
         case OCEAN_FLOOR -> new Vector3f(0.0F, 0.0F, 0.5F);
         case MOTION_BLOCKING -> new Vector3f(0.0F, 0.3F, 0.3F);
         case MOTION_BLOCKING_NO_LEAVES -> new Vector3f(0.0F, 0.5F, 0.5F);
      };
   }
}
