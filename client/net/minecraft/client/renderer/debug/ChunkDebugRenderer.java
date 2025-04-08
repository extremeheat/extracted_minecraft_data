package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

public class ChunkDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   final Minecraft minecraft;
   private double lastUpdateTime = 4.9E-324;
   private final int radius = 12;
   @Nullable
   private ChunkData data;

   public ChunkDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      double var9 = (double)Util.getNanos();
      if (var9 - this.lastUpdateTime > 3.0E9) {
         this.lastUpdateTime = var9;
         IntegratedServer var11 = this.minecraft.getSingleplayerServer();
         if (var11 != null) {
            this.data = new ChunkData(var11, var3, var7);
         } else {
            this.data = null;
         }
      }

      if (this.data != null) {
         Map var24 = (Map)this.data.serverData.getNow((Object)null);
         double var12 = this.minecraft.gameRenderer.getMainCamera().getPosition().y * 0.85;

         for(Map.Entry var15 : this.data.clientData.entrySet()) {
            ChunkPos var16 = (ChunkPos)var15.getKey();
            String var17 = (String)var15.getValue();
            if (var24 != null) {
               var17 = var17 + (String)var24.get(var16);
            }

            String[] var18 = var17.split("\n");
            int var19 = 0;

            for(String var23 : var18) {
               DebugRenderer.renderFloatingText(var1, var2, var23, (double)SectionPos.sectionToBlockCoord(var16.x, 8), var12 + (double)var19, (double)SectionPos.sectionToBlockCoord(var16.z, 8), -1, 0.15F, true, 0.0F, true);
               var19 -= 2;
            }
         }
      }

   }

   final class ChunkData {
      final Map<ChunkPos, String> clientData;
      final CompletableFuture<Map<ChunkPos, String>> serverData;

      ChunkData(final IntegratedServer var2, final double var3, final double var5) {
         super();
         ClientLevel var7 = ChunkDebugRenderer.this.minecraft.level;
         ResourceKey var8 = var7.dimension();
         int var9 = SectionPos.posToSectionCoord(var3);
         int var10 = SectionPos.posToSectionCoord(var5);
         ImmutableMap.Builder var11 = ImmutableMap.builder();
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
               ImmutableMap.Builder var6 = ImmutableMap.builder();
               ServerChunkCache var7 = var5.getChunkSource();

               for(int var8x = var9 - 12; var8x <= var9 + 12; ++var8x) {
                  for(int var9x = var10 - 12; var9x <= var10 + 12; ++var9x) {
                     ChunkPos var10x = new ChunkPos(var8x, var9x);
                     String var10002 = var7.getChunkDebugData(var10x);
                     var6.put(var10x, "Server: " + var10002);
                  }
               }

               return var6.build();
            }
         });
      }
   }
}
