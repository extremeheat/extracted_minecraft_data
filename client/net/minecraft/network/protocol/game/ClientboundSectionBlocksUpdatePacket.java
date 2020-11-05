package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.io.IOException;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;

public class ClientboundSectionBlocksUpdatePacket implements Packet<ClientGamePacketListener> {
   private SectionPos sectionPos;
   private short[] positions;
   private BlockState[] states;
   private boolean suppressLightUpdates;

   public ClientboundSectionBlocksUpdatePacket() {
      super();
   }

   public ClientboundSectionBlocksUpdatePacket(SectionPos var1, ShortSet var2, LevelChunkSection var3, boolean var4) {
      super();
      this.sectionPos = var1;
      this.suppressLightUpdates = var4;
      this.initFields(var2.size());
      int var5 = 0;

      for(ShortIterator var6 = var2.iterator(); var6.hasNext(); ++var5) {
         short var7 = (Short)var6.next();
         this.positions[var5] = var7;
         this.states[var5] = var3.getBlockState(SectionPos.sectionRelativeX(var7), SectionPos.sectionRelativeY(var7), SectionPos.sectionRelativeZ(var7));
      }

   }

   private void initFields(int var1) {
      this.positions = new short[var1];
      this.states = new BlockState[var1];
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.sectionPos = SectionPos.of(var1.readLong());
      this.suppressLightUpdates = var1.readBoolean();
      int var2 = var1.readVarInt();
      this.initFields(var2);

      for(int var3 = 0; var3 < this.positions.length; ++var3) {
         long var4 = var1.readVarLong();
         this.positions[var3] = (short)((int)(var4 & 4095L));
         this.states[var3] = (BlockState)Block.BLOCK_STATE_REGISTRY.byId((int)(var4 >>> 12));
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
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
