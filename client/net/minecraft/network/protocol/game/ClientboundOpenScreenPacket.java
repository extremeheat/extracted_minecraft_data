package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.inventory.MenuType;

public class ClientboundOpenScreenPacket implements Packet<ClientGamePacketListener> {
   private final int containerId;
   private final MenuType<?> type;
   private final Component title;

   public ClientboundOpenScreenPacket(int var1, MenuType<?> var2, Component var3) {
      super();
      this.containerId = var1;
      this.type = var2;
      this.title = var3;
   }

   public ClientboundOpenScreenPacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readVarInt();
      this.type = var1.readById(Registry.MENU);
      this.title = var1.readComponent();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.containerId);
      var1.writeId(Registry.MENU, this.type);
      var1.writeComponent(this.title);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleOpenScreen(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   @Nullable
   public MenuType<?> getType() {
      return this.type;
   }

   public Component getTitle() {
      return this.title;
   }
}
