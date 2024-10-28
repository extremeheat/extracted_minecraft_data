package net.minecraft.network.protocol.game;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;

public class ClientboundSetDisplayObjectivePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetDisplayObjectivePacket> STREAM_CODEC = Packet.codec(ClientboundSetDisplayObjectivePacket::write, ClientboundSetDisplayObjectivePacket::new);
   private final DisplaySlot slot;
   private final String objectiveName;

   public ClientboundSetDisplayObjectivePacket(DisplaySlot var1, @Nullable Objective var2) {
      super();
      this.slot = var1;
      if (var2 == null) {
         this.objectiveName = "";
      } else {
         this.objectiveName = var2.getName();
      }

   }

   private ClientboundSetDisplayObjectivePacket(FriendlyByteBuf var1) {
      super();
      this.slot = (DisplaySlot)var1.readById(DisplaySlot.BY_ID);
      this.objectiveName = var1.readUtf();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeById(DisplaySlot::id, this.slot);
      var1.writeUtf(this.objectiveName);
   }

   public PacketType<ClientboundSetDisplayObjectivePacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_DISPLAY_OBJECTIVE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetDisplayObjective(this);
   }

   public DisplaySlot getSlot() {
      return this.slot;
   }

   @Nullable
   public String getObjectiveName() {
      return Objects.equals(this.objectiveName, "") ? null : this.objectiveName;
   }
}
