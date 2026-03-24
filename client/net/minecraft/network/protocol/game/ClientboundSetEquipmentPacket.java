package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ClientboundSetEquipmentPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetEquipmentPacket> STREAM_CODEC = Packet.<RegistryFriendlyByteBuf, ClientboundSetEquipmentPacket>codec(ClientboundSetEquipmentPacket::write, ClientboundSetEquipmentPacket::new);
   private static final byte CONTINUE_MASK = -128;
   private final int entity;
   private final List<Pair<EquipmentSlot, ItemStack>> slots;

   public ClientboundSetEquipmentPacket(final int entity, final List<Pair<EquipmentSlot, ItemStack>> slots) {
      super();
      this.entity = entity;
      this.slots = slots;
   }

   private ClientboundSetEquipmentPacket(final RegistryFriendlyByteBuf input) {
      super();
      this.entity = input.readVarInt();
      this.slots = Lists.newArrayList();

      int slotId;
      do {
         slotId = input.readByte();
         EquipmentSlot slot = (EquipmentSlot)EquipmentSlot.VALUES.get(slotId & 127);
         ItemStack itemStack = (ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode(input);
         this.slots.add(Pair.of(slot, itemStack));
      } while((slotId & -128) != 0);

   }

   private void write(final RegistryFriendlyByteBuf output) {
      output.writeVarInt(this.entity);
      int size = this.slots.size();

      for(int i = 0; i < size; ++i) {
         Pair<EquipmentSlot, ItemStack> e = (Pair)this.slots.get(i);
         EquipmentSlot slotType = (EquipmentSlot)e.getFirst();
         boolean shouldContinue = i != size - 1;
         int slotId = slotType.ordinal();
         output.writeByte(shouldContinue ? slotId | -128 : slotId);
         ItemStack.OPTIONAL_STREAM_CODEC.encode(output, (ItemStack)e.getSecond());
      }

   }

   public PacketType<ClientboundSetEquipmentPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_EQUIPMENT;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleSetEquipment(this);
   }

   public int getEntity() {
      return this.entity;
   }

   public List<Pair<EquipmentSlot, ItemStack>> getSlots() {
      return this.slots;
   }
}
