package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLightUpdatePacket implements Packet<ClientGamePacketListener> {
   private int x;
   private int z;
   private int skyYMask;
   private int blockYMask;
   private int emptySkyYMask;
   private int emptyBlockYMask;
   private List<byte[]> skyUpdates;
   private List<byte[]> blockUpdates;
   private boolean trustEdges;

   public ClientboundLightUpdatePacket() {
      super();
   }

   public ClientboundLightUpdatePacket(ChunkPos var1, LevelLightEngine var2, boolean var3) {
      super();
      this.x = var1.x;
      this.z = var1.z;
      this.trustEdges = var3;
      this.skyUpdates = Lists.newArrayList();
      this.blockUpdates = Lists.newArrayList();

      for(int var4 = 0; var4 < 18; ++var4) {
         DataLayer var5 = var2.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(var1, -1 + var4));
         DataLayer var6 = var2.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(var1, -1 + var4));
         if (var5 != null) {
            if (var5.isEmpty()) {
               this.emptySkyYMask |= 1 << var4;
            } else {
               this.skyYMask |= 1 << var4;
               this.skyUpdates.add(var5.getData().clone());
            }
         }

         if (var6 != null) {
            if (var6.isEmpty()) {
               this.emptyBlockYMask |= 1 << var4;
            } else {
               this.blockYMask |= 1 << var4;
               this.blockUpdates.add(var6.getData().clone());
            }
         }
      }

   }

   public ClientboundLightUpdatePacket(ChunkPos var1, LevelLightEngine var2, int var3, int var4, boolean var5) {
      super();
      this.x = var1.x;
      this.z = var1.z;
      this.trustEdges = var5;
      this.skyYMask = var3;
      this.blockYMask = var4;
      this.skyUpdates = Lists.newArrayList();
      this.blockUpdates = Lists.newArrayList();

      for(int var6 = 0; var6 < 18; ++var6) {
         DataLayer var7;
         if ((this.skyYMask & 1 << var6) != 0) {
            var7 = var2.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(var1, -1 + var6));
            if (var7 != null && !var7.isEmpty()) {
               this.skyUpdates.add(var7.getData().clone());
            } else {
               this.skyYMask &= ~(1 << var6);
               if (var7 != null) {
                  this.emptySkyYMask |= 1 << var6;
               }
            }
         }

         if ((this.blockYMask & 1 << var6) != 0) {
            var7 = var2.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(var1, -1 + var6));
            if (var7 != null && !var7.isEmpty()) {
               this.blockUpdates.add(var7.getData().clone());
            } else {
               this.blockYMask &= ~(1 << var6);
               if (var7 != null) {
                  this.emptyBlockYMask |= 1 << var6;
               }
            }
         }
      }

   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.x = var1.readVarInt();
      this.z = var1.readVarInt();
      this.trustEdges = var1.readBoolean();
      this.skyYMask = var1.readVarInt();
      this.blockYMask = var1.readVarInt();
      this.emptySkyYMask = var1.readVarInt();
      this.emptyBlockYMask = var1.readVarInt();
      this.skyUpdates = Lists.newArrayList();

      int var2;
      for(var2 = 0; var2 < 18; ++var2) {
         if ((this.skyYMask & 1 << var2) != 0) {
            this.skyUpdates.add(var1.readByteArray(2048));
         }
      }

      this.blockUpdates = Lists.newArrayList();

      for(var2 = 0; var2 < 18; ++var2) {
         if ((this.blockYMask & 1 << var2) != 0) {
            this.blockUpdates.add(var1.readByteArray(2048));
         }
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.x);
      var1.writeVarInt(this.z);
      var1.writeBoolean(this.trustEdges);
      var1.writeVarInt(this.skyYMask);
      var1.writeVarInt(this.blockYMask);
      var1.writeVarInt(this.emptySkyYMask);
      var1.writeVarInt(this.emptyBlockYMask);
      Iterator var2 = this.skyUpdates.iterator();

      byte[] var3;
      while(var2.hasNext()) {
         var3 = (byte[])var2.next();
         var1.writeByteArray(var3);
      }

      var2 = this.blockUpdates.iterator();

      while(var2.hasNext()) {
         var3 = (byte[])var2.next();
         var1.writeByteArray(var3);
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleLightUpdatePacked(this);
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public int getSkyYMask() {
      return this.skyYMask;
   }

   public int getEmptySkyYMask() {
      return this.emptySkyYMask;
   }

   public List<byte[]> getSkyUpdates() {
      return this.skyUpdates;
   }

   public int getBlockYMask() {
      return this.blockYMask;
   }

   public int getEmptyBlockYMask() {
      return this.emptyBlockYMask;
   }

   public List<byte[]> getBlockUpdates() {
      return this.blockUpdates;
   }

   public boolean getTrustEdges() {
      return this.trustEdges;
   }
}
