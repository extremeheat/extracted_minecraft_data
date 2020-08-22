package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;

public class ChunkDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private double lastUpdateTime = Double.MIN_VALUE;
   private final int radius = 12;
   @Nullable
   private ChunkDebugRenderer.ChunkData data;

   public ChunkDebugRenderer(Minecraft var1) {
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      double var9 = (double)Util.getNanos();
      if (var9 - this.lastUpdateTime > 3.0E9D) {
         this.lastUpdateTime = var9;
         IntegratedServer var11 = this.minecraft.getSingleplayerServer();
         if (var11 != null) {
            this.data = new ChunkDebugRenderer.ChunkData(var11, var3, var7);
         } else {
            this.data = null;
         }
      }

      if (this.data != null) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.lineWidth(2.0F);
         RenderSystem.disableTexture();
         RenderSystem.depthMask(false);
         Map var24 = (Map)this.data.serverData.getNow((Object)null);
         double var12 = this.minecraft.gameRenderer.getMainCamera().getPosition().y * 0.85D;
         Iterator var14 = this.data.clientData.entrySet().iterator();

         while(var14.hasNext()) {
            Entry var15 = (Entry)var14.next();
            ChunkPos var16 = (ChunkPos)var15.getKey();
            String var17 = (String)var15.getValue();
            if (var24 != null) {
               var17 = var17 + (String)var24.get(var16);
            }

            String[] var18 = var17.split("\n");
            int var19 = 0;
            String[] var20 = var18;
            int var21 = var18.length;

            for(int var22 = 0; var22 < var21; ++var22) {
               String var23 = var20[var22];
               DebugRenderer.renderFloatingText(var23, (double)((var16.x << 4) + 8), var12 + (double)var19, (double)((var16.z << 4) + 8), -1, 0.15F);
               var19 -= 2;
            }
         }

         RenderSystem.depthMask(true);
         RenderSystem.enableTexture();
         RenderSystem.disableBlend();
      }

   }

   final class ChunkData {
      private final Map clientData;
      private final CompletableFuture serverData;

      private ChunkData(IntegratedServer var2, double var3, double var5) {
         ClientLevel var7 = ChunkDebugRenderer.this.minecraft.level;
         DimensionType var8 = ChunkDebugRenderer.this.minecraft.level.dimension.getType();
         ServerLevel var9;
         if (var2.getLevel(var8) != null) {
            var9 = var2.getLevel(var8);
         } else {
            var9 = null;
         }

         int var10 = (int)var3 >> 4;
         int var11 = (int)var5 >> 4;
         Builder var12 = ImmutableMap.builder();
         ClientChunkCache var13 = var7.getChunkSource();

         for(int var14 = var10 - 12; var14 <= var10 + 12; ++var14) {
            for(int var15 = var11 - 12; var15 <= var11 + 12; ++var15) {
               ChunkPos var16 = new ChunkPos(var14, var15);
               String var17 = "";
               LevelChunk var18 = var13.getChunk(var14, var15, false);
               var17 = var17 + "Client: ";
               if (var18 == null) {
                  var17 = var17 + "0n/a\n";
               } else {
                  var17 = var17 + (var18.isEmpty() ? " E" : "");
                  var17 = var17 + "\n";
               }

               var12.put(var16, var17);
            }
         }

         this.clientData = var12.build();
         this.serverData = var2.submit(() -> {
            Builder var4 = ImmutableMap.builder();
            ServerChunkCache var5 = var9.getChunkSource();

            for(int var6 = var10 - 12; var6 <= var10 + 12; ++var6) {
               for(int var7 = var11 - 12; var7 <= var11 + 12; ++var7) {
                  ChunkPos var8 = new ChunkPos(var6, var7);
                  var4.put(var8, "Server: " + var5.getChunkDebugData(var8));
               }
            }

            return var4.build();
         });
      }

      // $FF: synthetic method
      ChunkData(IntegratedServer var2, double var3, double var5, Object var7) {
         this(var2, var3, var5);
      }
   }
}
