package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;

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

   public void render(long var1) {
      double var3 = (double)Util.getNanos();
      if (var3 - this.lastUpdateTime > 3.0E9D) {
         this.lastUpdateTime = var3;
         IntegratedServer var5 = this.minecraft.getSingleplayerServer();
         if (var5 != null) {
            this.data = new ChunkDebugRenderer.ChunkData(var5);
         } else {
            this.data = null;
         }
      }

      if (this.data != null) {
         GlStateManager.disableFog();
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.lineWidth(2.0F);
         GlStateManager.disableTexture();
         GlStateManager.depthMask(false);
         Map var18 = (Map)this.data.serverData.getNow((Object)null);
         double var6 = this.minecraft.gameRenderer.getMainCamera().getPosition().y * 0.85D;
         Iterator var8 = this.data.clientData.entrySet().iterator();

         while(var8.hasNext()) {
            Entry var9 = (Entry)var8.next();
            ChunkPos var10 = (ChunkPos)var9.getKey();
            String var11 = (String)var9.getValue();
            if (var18 != null) {
               var11 = var11 + (String)var18.get(var10);
            }

            String[] var12 = var11.split("\n");
            int var13 = 0;
            String[] var14 = var12;
            int var15 = var12.length;

            for(int var16 = 0; var16 < var15; ++var16) {
               String var17 = var14[var16];
               DebugRenderer.renderFloatingText(var17, (double)((var10.x << 4) + 8), var6 + (double)var13, (double)((var10.z << 4) + 8), -1, 0.15F);
               var13 -= 2;
            }
         }

         GlStateManager.depthMask(true);
         GlStateManager.enableTexture();
         GlStateManager.disableBlend();
         GlStateManager.enableFog();
      }

   }

   final class ChunkData {
      private final Map<ChunkPos, String> clientData;
      private final CompletableFuture<Map<ChunkPos, String>> serverData;

      private ChunkData(IntegratedServer var2) {
         super();
         MultiPlayerLevel var3 = ChunkDebugRenderer.this.minecraft.level;
         DimensionType var4 = ChunkDebugRenderer.this.minecraft.level.dimension.getType();
         ServerLevel var5;
         if (var2.getLevel(var4) != null) {
            var5 = var2.getLevel(var4);
         } else {
            var5 = null;
         }

         Camera var6 = ChunkDebugRenderer.this.minecraft.gameRenderer.getMainCamera();
         int var7 = (int)var6.getPosition().x >> 4;
         int var8 = (int)var6.getPosition().z >> 4;
         Builder var9 = ImmutableMap.builder();
         ClientChunkCache var10 = var3.getChunkSource();

         for(int var11 = var7 - 12; var11 <= var7 + 12; ++var11) {
            for(int var12 = var8 - 12; var12 <= var8 + 12; ++var12) {
               ChunkPos var13 = new ChunkPos(var11, var12);
               String var14 = "";
               LevelChunk var15 = var10.getChunk(var11, var12, false);
               var14 = var14 + "Client: ";
               if (var15 == null) {
                  var14 = var14 + "0n/a\n";
               } else {
                  var14 = var14 + (var15.isEmpty() ? " E" : "");
                  var14 = var14 + "\n";
               }

               var9.put(var13, var14);
            }
         }

         this.clientData = var9.build();
         this.serverData = var2.submit(() -> {
            Builder var4 = ImmutableMap.builder();
            ServerChunkCache var5x = var5.getChunkSource();

            for(int var6 = var7 - 12; var6 <= var7 + 12; ++var6) {
               for(int var7x = var8 - 12; var7x <= var8 + 12; ++var7x) {
                  ChunkPos var8x = new ChunkPos(var6, var7x);
                  var4.put(var8x, "Server: " + var5x.getChunkDebugData(var8x));
               }
            }

            return var4.build();
         });
      }

      // $FF: synthetic method
      ChunkData(IntegratedServer var2, Object var3) {
         this(var2);
      }
   }
}
