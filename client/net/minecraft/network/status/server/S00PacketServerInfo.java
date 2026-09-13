package net.minecraft.network.status.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.ServerStatusResponse$MinecraftProtocolVersionIdentifier;
import net.minecraft.network.ServerStatusResponse$MinecraftProtocolVersionIdentifier$Serializer;
import net.minecraft.network.ServerStatusResponse$PlayerCountData;
import net.minecraft.network.ServerStatusResponse$PlayerCountData$Serializer;
import net.minecraft.network.ServerStatusResponse$Serializer;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.ChatStyle$Serializer;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent$Serializer;

public class S00PacketServerInfo extends Packet {
   private static final Gson field_149297_a = new GsonBuilder()
      .registerTypeAdapter(
         ServerStatusResponse$MinecraftProtocolVersionIdentifier.class, new ServerStatusResponse$MinecraftProtocolVersionIdentifier$Serializer()
      )
      .registerTypeAdapter(ServerStatusResponse$PlayerCountData.class, new ServerStatusResponse$PlayerCountData$Serializer())
      .registerTypeAdapter(ServerStatusResponse.class, new ServerStatusResponse$Serializer())
      .registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent$Serializer())
      .registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle$Serializer())
      .registerTypeAdapterFactory(new EnumTypeAdapterFactory())
      .create();
   private ServerStatusResponse field_149296_b;

   public S00PacketServerInfo() {
      super();
   }

   public S00PacketServerInfo(ServerStatusResponse var1) {
      super();
      this.field_149296_b = var1;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149296_b = (ServerStatusResponse)field_149297_a.fromJson(var1.func_150789_c(32767), ServerStatusResponse.class);
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150785_a(field_149297_a.toJson(this.field_149296_b));
   }

   public void func_148833_a(INetHandlerStatusClient var1) {
      var1.func_147397_a(this);
   }

   public ServerStatusResponse func_149294_c() {
      return this.field_149296_b;
   }

   @Override
   public boolean func_148836_a() {
      return true;
   }
}
