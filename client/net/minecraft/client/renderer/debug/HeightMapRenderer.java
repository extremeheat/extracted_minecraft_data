package net.minecraft.client.renderer.debug;

import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3f;

public class HeightMapRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private static final int CHUNK_DIST = 2;
   private static final float BOX_HEIGHT = 0.09375F;

   public HeightMapRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      ClientLevel var10 = this.minecraft.level;
      BlockPos var11 = BlockPos.containing(var1, 0.0, var5);

      for(int var12 = -2; var12 <= 2; ++var12) {
         for(int var13 = -2; var13 <= 2; ++var13) {
            ChunkAccess var14 = var10.getChunk(var11.offset(var12 * 16, 0, var13 * 16));

            for(Map.Entry var16 : var14.getHeightmaps()) {
               Heightmap.Types var17 = (Heightmap.Types)var16.getKey();
               ChunkPos var18 = var14.getPos();
               Vector3f var19 = this.getColor(var17);

               for(int var20 = 0; var20 < 16; ++var20) {
                  for(int var21 = 0; var21 < 16; ++var21) {
                     int var22 = SectionPos.sectionToBlockCoord(var18.x, var20);
                     int var23 = SectionPos.sectionToBlockCoord(var18.z, var21);
                     float var24 = (float)var10.getHeight(var17, var22, var23) + (float)var17.ordinal() * 0.09375F;
                     Gizmos.cuboid(new AABB((double)((float)var22 + 0.25F), (double)var24, (double)((float)var23 + 0.25F), (double)((float)var22 + 0.75F), (double)(var24 + 0.09375F), (double)((float)var23 + 0.75F)), GizmoStyle.fill(ARGB.colorFromFloat(1.0F, var19.x(), var19.y(), var19.z())));
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
