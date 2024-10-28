package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class ServerboundContainerClickPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundContainerClickPacket> STREAM_CODEC = Packet.codec(ServerboundContainerClickPacket::write, ServerboundContainerClickPacket::new);
   private static final int MAX_SLOT_COUNT = 128;
   private static final StreamCodec<RegistryFriendlyByteBuf, Int2ObjectMap<ItemStack>> SLOTS_STREAM_CODEC;
   private final int containerId;
   private final int stateId;
   private final int slotNum;
   private final int buttonNum;
   private final ClickType clickType;
   private final ItemStack carriedItem;
   private final Int2ObjectMap<ItemStack> changedSlots;

   public ServerboundContainerClickPacket(int var1, int var2, int var3, int var4, ClickType var5, ItemStack var6, Int2ObjectMap<ItemStack> var7) {
      super();
      this.containerId = var1;
      this.stateId = var2;
      this.slotNum = var3;
      this.buttonNum = var4;
      this.clickType = var5;
      this.carriedItem = var6;
      this.changedSlots = Int2ObjectMaps.unmodifiable(var7);
   }

   private ServerboundContainerClickPacket(RegistryFriendlyByteBuf var1) {
      super();
      this.containerId = var1.readByte();
      this.stateId = var1.readVarInt();
      this.slotNum = var1.readShort();
      this.buttonNum = var1.readByte();
      this.clickType = (ClickType)var1.readEnum(ClickType.class);
      this.changedSlots = Int2ObjectMaps.unmodifiable((Int2ObjectMap)SLOTS_STREAM_CODEC.decode(var1));
      this.carriedItem = (ItemStack)ItemStack.OPTIONAL_STREAM_CODEC.decode(var1);
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
      var1.writeVarInt(this.stateId);
      var1.writeShort(this.slotNum);
      var1.writeByte(this.buttonNum);
      var1.writeEnum(this.clickType);
      SLOTS_STREAM_CODEC.encode(var1, this.changedSlots);
      ItemStack.OPTIONAL_STREAM_CODEC.encode(var1, this.carriedItem);
   }

   public PacketType<ServerboundContainerClickPacket> type() {
      return GamePacketTypes.SERVERBOUND_CONTAINER_CLICK;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleContainerClick(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public int getSlotNum() {
      return this.slotNum;
   }

   public int getButtonNum() {
      return this.buttonNum;
   }

   public ItemStack getCarriedItem() {
      return this.carriedItem;
   }

   public Int2ObjectMap<ItemStack> getChangedSlots() {
      return this.changedSlots;
   }

   public ClickType getClickType() {
      return this.clickType;
   }

   public int getStateId() {
      return this.stateId;
   }

   static {
      SLOTS_STREAM_CODEC = ByteBufCodecs.map(Int2ObjectOpenHashMap::new, ByteBufCodecs.SHORT.map(Short::intValue, Integer::shortValue), ItemStack.OPTIONAL_STREAM_CODEC, 128);
   }
}
