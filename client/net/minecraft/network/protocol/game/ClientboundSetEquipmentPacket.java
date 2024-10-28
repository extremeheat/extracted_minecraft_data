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
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetEquipmentPacket> STREAM_CODEC = Packet.codec(ClientboundSetEquipmentPacket::write, ClientboundSetEquipmentPacket::new);
   private static final byte CONTINUE_MASK = -128;
   private final int entity;
   private final List<Pair<EquipmentSlot, ItemStack>> slots;

   public ClientboundSetEquipmentPacket(int var1, List<Pair<EquipmentSlot, ItemStack>> var2) {
      super();
      this.entity = var1;
      this.slots = var2;
   }

   private ClientboundSetEquipmentPacket(RegistryFriendlyByteBuf var1) {
      super();
      this.entity = var1.readVarInt();
      EquipmentSlot[] var2 = EquipmentSlot.values();
      this.slots = Lists.newArrayList();

      byte var3;
      do {
         var3 = var1.readByte();
         EquipmentSlot var4 = var2[var3 & 127];
         ItemStack var5 = (ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode(var1);
         this.slots.add(Pair.of(var4, var5));
      } while((var3 & -128) != 0);

   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeVarInt(this.entity);
      int var2 = this.slots.size();

      for(int var3 = 0; var3 < var2; ++var3) {
         Pair var4 = (Pair)this.slots.get(var3);
         EquipmentSlot var5 = (EquipmentSlot)var4.getFirst();
         boolean var6 = var3 != var2 - 1;
         int var7 = var5.ordinal();
         var1.writeByte(var6 ? var7 | -128 : var7);
         ItemStack.OPTIONAL_STREAM_CODEC.encode(var1, (ItemStack)var4.getSecond());
      }

   }

   public PacketType<ClientboundSetEquipmentPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_EQUIPMENT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetEquipment(this);
   }

   public int getEntity() {
      return this.entity;
   }

   public List<Pair<EquipmentSlot, ItemStack>> getSlots() {
      return this.slots;
   }
}
