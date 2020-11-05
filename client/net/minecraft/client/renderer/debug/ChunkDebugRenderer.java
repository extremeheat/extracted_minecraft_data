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
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

public class ChunkDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private double lastUpdateTime = 4.9E-324D;
   private final int radius = 12;
   @Nullable
   private ChunkDebugRenderer.ChunkData data;

   public ChunkDebugRenderer(Minecraft var1) {
      super();
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
      private final Map<ChunkPos, String> clientData;
      private final CompletableFuture<Map<ChunkPos, String>> serverData;

      private ChunkData(IntegratedServer var2, double var3, double var5) {
         super();
         ClientLevel var7 = ChunkDebugRenderer.this.minecraft.level;
         ResourceKey var8 = var7.dimension();
         int var9 = (int)var3 >> 4;
         int var10 = (int)var5 >> 4;
         Builder var11 = ImmutableMap.builder();
         ClientChunkCache var12 = var7.getChunkSource();

         for(int var13 = var9 - 12; var13 <= var9 + 12; ++var13) {
            for(int var14 = var10 - 12; var14 <= var10 + 12; ++var14) {
               ChunkPos var15 = new ChunkPos(var13, var14);
               String var16 = "";
               LevelChunk var17 = var12.getChunk(var13, var14, false);
               var16 = var16 + "Client: ";
               if (var17 == null) {
                  var16 = var16 + "0n/a\n";
               } else {
                  var16 = var16 + (var17.isEmpty() ? " E" : "");
                  var16 = var16 + "\n";
               }

               var11.put(var15, var16);
            }
         }

         this.clientData = var11.build();
         this.serverData = var2.submit(() -> {
            ServerLevel var5 = var2.getLevel(var8);
            if (var5 == null) {
               return ImmutableMap.of();
            } else {
               Builder var6 = ImmutableMap.builder();
               ServerChunkCache var7 = var5.getChunkSource();

               for(int var8x = var9 - 12; var8x <= var9 + 12; ++var8x) {
                  for(int var9x = var10 - 12; var9x <= var10 + 12; ++var9x) {
                     ChunkPos var10x = new ChunkPos(var8x, var9x);
                     var6.put(var10x, "Server: " + var7.getChunkDebugData(var10x));
                  }
               }

               return var6.build();
            }
         });
      }

      // $FF: synthetic method
      ChunkData(IntegratedServer var2, double var3, double var5, Object var7) {
         this(var2, var3, var5);
      }
   }
}
