package net.minecraft.network.protocol.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;

public class ClientboundStatusResponsePacket implements Packet<ClientStatusPacketListener> {
   private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(ServerStatus.Version.class, new ServerStatus.Version.Serializer()).registerTypeAdapter(ServerStatus.Players.class, new ServerStatus.Players.Serializer()).registerTypeAdapter(ServerStatus.class, new ServerStatus.Serializer()).registerTypeHierarchyAdapter(Component.class, new Component.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory()).create();
   private ServerStatus status;

   public ClientboundStatusResponsePacket() {
      super();
   }

   public ClientboundStatusResponsePacket(ServerStatus var1) {
      super();
      this.status = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.status = (ServerStatus)GsonHelper.fromJson(GSON, var1.readUtf(32767), ServerStatus.class);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeUtf(GSON.toJson(this.status));
   }

   public void handle(ClientStatusPacketListener var1) {
      var1.handleStatusResponse(this);
   }

   public ServerStatus getStatus() {
      return this.status;
   }
}
