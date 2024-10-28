package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ClientboundSetObjectivePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetObjectivePacket> STREAM_CODEC = Packet.codec(ClientboundSetObjectivePacket::write, ClientboundSetObjectivePacket::new);
   public static final int METHOD_ADD = 0;
   public static final int METHOD_REMOVE = 1;
   public static final int METHOD_CHANGE = 2;
   private final String objectiveName;
   private final Component displayName;
   private final ObjectiveCriteria.RenderType renderType;
   private final Optional<NumberFormat> numberFormat;
   private final int method;

   public ClientboundSetObjectivePacket(Objective var1, int var2) {
      super();
      this.objectiveName = var1.getName();
      this.displayName = var1.getDisplayName();
      this.renderType = var1.getRenderType();
      this.numberFormat = Optional.ofNullable(var1.numberFormat());
      this.method = var2;
   }

   private ClientboundSetObjectivePacket(RegistryFriendlyByteBuf var1) {
      super();
      this.objectiveName = var1.readUtf();
      this.method = var1.readByte();
      if (this.method != 0 && this.method != 2) {
         this.displayName = CommonComponents.EMPTY;
         this.renderType = ObjectiveCriteria.RenderType.INTEGER;
         this.numberFormat = Optional.empty();
      } else {
         this.displayName = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(var1);
         this.renderType = (ObjectiveCriteria.RenderType)var1.readEnum(ObjectiveCriteria.RenderType.class);
         this.numberFormat = (Optional)NumberFormatTypes.OPTIONAL_STREAM_CODEC.decode(var1);
      }

   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeUtf(this.objectiveName);
      var1.writeByte(this.method);
      if (this.method == 0 || this.method == 2) {
         ComponentSerialization.TRUSTED_STREAM_CODEC.encode(var1, this.displayName);
         var1.writeEnum(this.renderType);
         NumberFormatTypes.OPTIONAL_STREAM_CODEC.encode(var1, this.numberFormat);
      }

   }

   public PacketType<ClientboundSetObjectivePacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_OBJECTIVE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddObjective(this);
   }

   public String getObjectiveName() {
      return this.objectiveName;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public int getMethod() {
      return this.method;
   }

   public ObjectiveCriteria.RenderType getRenderType() {
      return this.renderType;
   }

   public Optional<NumberFormat> getNumberFormat() {
      return this.numberFormat;
   }
}
