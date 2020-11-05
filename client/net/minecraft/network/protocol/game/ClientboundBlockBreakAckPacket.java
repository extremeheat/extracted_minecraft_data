package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientboundBlockBreakAckPacket implements Packet<ClientGamePacketListener> {
   private static final Logger LOGGER = LogManager.getLogger();
   private BlockPos pos;
   private BlockState state;
   ServerboundPlayerActionPacket.Action action;
   private boolean allGood;

   public ClientboundBlockBreakAckPacket() {
      super();
   }

   public ClientboundBlockBreakAckPacket(BlockPos var1, BlockState var2, ServerboundPlayerActionPacket.Action var3, boolean var4, String var5) {
      super();
      this.pos = var1.immutable();
      this.state = var2;
      this.action = var3;
      this.allGood = var4;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.pos = var1.readBlockPos();
      this.state = (BlockState)Block.BLOCK_STATE_REGISTRY.byId(var1.readVarInt());
      this.action = (ServerboundPlayerActionPacket.Action)var1.readEnum(ServerboundPlayerActionPacket.Action.class);
      this.allGood = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeBlockPos(this.pos);
      var1.writeVarInt(Block.getId(this.state));
      var1.writeEnum(this.action);
      var1.writeBoolean(this.allGood);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBlockBreakAck(this);
   }

   public BlockState getState() {
      return this.state;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public boolean allGood() {
      return this.allGood;
   }

   public ServerboundPlayerActionPacket.Action action() {
      return this.action;
   }
}
