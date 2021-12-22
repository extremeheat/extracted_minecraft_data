package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLightUpdatePacketData {
   private final BitSet skyYMask;
   private final BitSet blockYMask;
   private final BitSet emptySkyYMask;
   private final BitSet emptyBlockYMask;
   private final List<byte[]> skyUpdates;
   private final List<byte[]> blockUpdates;
   private final boolean trustEdges;

   public ClientboundLightUpdatePacketData(ChunkPos var1, LevelLightEngine var2, @Nullable BitSet var3, @Nullable BitSet var4, boolean var5) {
      super();
      this.trustEdges = var5;
      this.skyYMask = new BitSet();
      this.blockYMask = new BitSet();
      this.emptySkyYMask = new BitSet();
      this.emptyBlockYMask = new BitSet();
      this.skyUpdates = Lists.newArrayList();
      this.blockUpdates = Lists.newArrayList();

      for(int var6 = 0; var6 < var2.getLightSectionCount(); ++var6) {
         if (var3 == null || var3.get(var6)) {
            this.prepareSectionData(var1, var2, LightLayer.SKY, var6, this.skyYMask, this.emptySkyYMask, this.skyUpdates);
         }

         if (var4 == null || var4.get(var6)) {
            this.prepareSectionData(var1, var2, LightLayer.BLOCK, var6, this.blockYMask, this.emptyBlockYMask, this.blockUpdates);
         }
      }

   }

   public ClientboundLightUpdatePacketData(FriendlyByteBuf var1, int var2, int var3) {
      super();
      this.trustEdges = var1.readBoolean();
      this.skyYMask = var1.readBitSet();
      this.blockYMask = var1.readBitSet();
      this.emptySkyYMask = var1.readBitSet();
      this.emptyBlockYMask = var1.readBitSet();
      this.skyUpdates = var1.readList((var0) -> {
         return var0.readByteArray(2048);
      });
      this.blockUpdates = var1.readList((var0) -> {
         return var0.readByteArray(2048);
      });
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeBoolean(this.trustEdges);
      var1.writeBitSet(this.skyYMask);
      var1.writeBitSet(this.blockYMask);
      var1.writeBitSet(this.emptySkyYMask);
      var1.writeBitSet(this.emptyBlockYMask);
      var1.writeCollection(this.skyUpdates, FriendlyByteBuf::writeByteArray);
      var1.writeCollection(this.blockUpdates, FriendlyByteBuf::writeByteArray);
   }

   private void prepareSectionData(ChunkPos var1, LevelLightEngine var2, LightLayer var3, int var4, BitSet var5, BitSet var6, List<byte[]> var7) {
      DataLayer var8 = var2.getLayerListener(var3).getDataLayerData(SectionPos.method_72(var1, var2.getMinLightSection() + var4));
      if (var8 != null) {
         if (var8.isEmpty()) {
            var6.set(var4);
         } else {
            var5.set(var4);
            var7.add((byte[])var8.getData().clone());
         }
      }

   }

   public BitSet getSkyYMask() {
      return this.skyYMask;
   }

   public BitSet getEmptySkyYMask() {
      return this.emptySkyYMask;
   }

   public List<byte[]> getSkyUpdates() {
      return this.skyUpdates;
   }

   public BitSet getBlockYMask() {
      return this.blockYMask;
   }

   public BitSet getEmptyBlockYMask() {
      return this.emptyBlockYMask;
   }

   public List<byte[]> getBlockUpdates() {
      return this.blockUpdates;
   }

   public boolean getTrustEdges() {
      return this.trustEdges;
   }
}
