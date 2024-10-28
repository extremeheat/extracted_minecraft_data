package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLightUpdatePacketData {
   private static final StreamCodec<ByteBuf, byte[]> DATA_LAYER_STREAM_CODEC = ByteBufCodecs.byteArray(2048);
   private final BitSet skyYMask;
   private final BitSet blockYMask;
   private final BitSet emptySkyYMask;
   private final BitSet emptyBlockYMask;
   private final List<byte[]> skyUpdates;
   private final List<byte[]> blockUpdates;

   public ClientboundLightUpdatePacketData(ChunkPos var1, LevelLightEngine var2, @Nullable BitSet var3, @Nullable BitSet var4) {
      super();
      this.skyYMask = new BitSet();
      this.blockYMask = new BitSet();
      this.emptySkyYMask = new BitSet();
      this.emptyBlockYMask = new BitSet();
      this.skyUpdates = Lists.newArrayList();
      this.blockUpdates = Lists.newArrayList();

      for(int var5 = 0; var5 < var2.getLightSectionCount(); ++var5) {
         if (var3 == null || var3.get(var5)) {
            this.prepareSectionData(var1, var2, LightLayer.SKY, var5, this.skyYMask, this.emptySkyYMask, this.skyUpdates);
         }

         if (var4 == null || var4.get(var5)) {
            this.prepareSectionData(var1, var2, LightLayer.BLOCK, var5, this.blockYMask, this.emptyBlockYMask, this.blockUpdates);
         }
      }

   }

   public ClientboundLightUpdatePacketData(FriendlyByteBuf var1, int var2, int var3) {
      super();
      this.skyYMask = var1.readBitSet();
      this.blockYMask = var1.readBitSet();
      this.emptySkyYMask = var1.readBitSet();
      this.emptyBlockYMask = var1.readBitSet();
      this.skyUpdates = var1.readList(DATA_LAYER_STREAM_CODEC);
      this.blockUpdates = var1.readList(DATA_LAYER_STREAM_CODEC);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeBitSet(this.skyYMask);
      var1.writeBitSet(this.blockYMask);
      var1.writeBitSet(this.emptySkyYMask);
      var1.writeBitSet(this.emptyBlockYMask);
      var1.writeCollection(this.skyUpdates, DATA_LAYER_STREAM_CODEC);
      var1.writeCollection(this.blockUpdates, DATA_LAYER_STREAM_CODEC);
   }

   private void prepareSectionData(ChunkPos var1, LevelLightEngine var2, LightLayer var3, int var4, BitSet var5, BitSet var6, List<byte[]> var7) {
      DataLayer var8 = var2.getLayerListener(var3).getDataLayerData(SectionPos.of(var1, var2.getMinLightSection() + var4));
      if (var8 != null) {
         if (var8.isEmpty()) {
            var6.set(var4);
         } else {
            var5.set(var4);
            var7.add(var8.copy().getData());
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
}
