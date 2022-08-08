package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class ClientboundSectionBlocksUpdatePacket implements Packet<ClientGamePacketListener> {
   private static final int POS_IN_SECTION_BITS = 12;
   private final SectionPos sectionPos;
   private final short[] positions;
   private final BlockState[] states;
   private final boolean suppressLightUpdates;

   public ClientboundSectionBlocksUpdatePacket(SectionPos var1, ShortSet var2, LevelChunkSection var3, boolean var4) {
      super();
      this.sectionPos = var1;
      this.suppressLightUpdates = var4;
      int var5 = var2.size();
      this.positions = new short[var5];
      this.states = new BlockState[var5];
      int var6 = 0;

      for(ShortIterator var7 = var2.iterator(); var7.hasNext(); ++var6) {
         short var8 = (Short)var7.next();
         this.positions[var6] = var8;
         this.states[var6] = var3.getBlockState(SectionPos.sectionRelativeX(var8), SectionPos.sectionRelativeY(var8), SectionPos.sectionRelativeZ(var8));
      }

   }

   public ClientboundSectionBlocksUpdatePacket(FriendlyByteBuf var1) {
      super();
      this.sectionPos = SectionPos.of(var1.readLong());
      this.suppressLightUpdates = var1.readBoolean();
      int var2 = var1.readVarInt();
      this.positions = new short[var2];
      this.states = new BlockState[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         long var4 = var1.readVarLong();
         this.positions[var3] = (short)((int)(var4 & 4095L));
         this.states[var3] = (BlockState)Block.BLOCK_STATE_REGISTRY.byId((int)(var4 >>> 12));
      }

   }

   public void write(FriendlyByteBuf var1) {
      var1.writeLong(this.sectionPos.asLong());
      var1.writeBoolean(this.suppressLightUpdates);
      var1.writeVarInt(this.positions.length);

      for(int var2 = 0; var2 < this.positions.length; ++var2) {
         var1.writeVarLong((long)(Block.getId(this.states[var2]) << 12 | this.positions[var2]));
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleChunkBlocksUpdate(this);
   }

   public void runUpdates(BiConsumer<BlockPos, BlockState> var1) {
      BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();

      for(int var3 = 0; var3 < this.positions.length; ++var3) {
         short var4 = this.positions[var3];
         var2.set(this.sectionPos.relativeToBlockX(var4), this.sectionPos.relativeToBlockY(var4), this.sectionPos.relativeToBlockZ(var4));
         var1.accept(var2, this.states[var3]);
      }

   }

   public boolean shouldSuppressLightUpdates() {
      return this.suppressLightUpdates;
   }
}
