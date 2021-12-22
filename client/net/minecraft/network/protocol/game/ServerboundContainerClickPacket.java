package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.function.IntFunction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public class ServerboundContainerClickPacket implements Packet<ServerGamePacketListener> {
   private static final int MAX_SLOT_COUNT = 128;
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

   public ServerboundContainerClickPacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readByte();
      this.stateId = var1.readVarInt();
      this.slotNum = var1.readShort();
      this.buttonNum = var1.readByte();
      this.clickType = (ClickType)var1.readEnum(ClickType.class);
      IntFunction var2 = FriendlyByteBuf.limitValue(Int2ObjectOpenHashMap::new, 128);
      this.changedSlots = Int2ObjectMaps.unmodifiable((Int2ObjectMap)var1.readMap(var2, (var0) -> {
         return Integer.valueOf(var0.readShort());
      }, FriendlyByteBuf::readItem));
      this.carriedItem = var1.readItem();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeByte(this.containerId);
      var1.writeVarInt(this.stateId);
      var1.writeShort(this.slotNum);
      var1.writeByte(this.buttonNum);
      var1.writeEnum(this.clickType);
      var1.writeMap(this.changedSlots, FriendlyByteBuf::writeShort, FriendlyByteBuf::writeItem);
      var1.writeItem(this.carriedItem);
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
}
