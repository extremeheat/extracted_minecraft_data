package net.minecraft.data.info;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.game.GameProtocols;
import net.minecraft.network.protocol.handshake.HandshakeProtocols;
import net.minecraft.network.protocol.login.LoginProtocols;
import net.minecraft.network.protocol.status.StatusProtocols;

public class PacketReport implements DataProvider {
   private final PackOutput output;

   public PacketReport(PackOutput var1) {
      super();
      this.output = var1;
   }

   @Override
   public CompletableFuture<?> run(CachedOutput var1) {
      Path var2 = this.output.getOutputFolder(PackOutput.Target.REPORTS).resolve("packets.json");
      return DataProvider.saveStable(var1, this.serializePackets(), var2);
   }

   private JsonElement serializePackets() {
      JsonObject var1 = new JsonObject();
      Stream.of(
            HandshakeProtocols.SERVERBOUND_TEMPLATE,
            StatusProtocols.CLIENTBOUND_TEMPLATE,
            StatusProtocols.SERVERBOUND_TEMPLATE,
            LoginProtocols.CLIENTBOUND_TEMPLATE,
            LoginProtocols.SERVERBOUND_TEMPLATE,
            ConfigurationProtocols.CLIENTBOUND_TEMPLATE,
            ConfigurationProtocols.SERVERBOUND_TEMPLATE,
            GameProtocols.CLIENTBOUND_TEMPLATE,
            GameProtocols.SERVERBOUND_TEMPLATE
         )
         .collect(Collectors.groupingBy(ProtocolInfo.Unbound::id))
         .forEach((var1x, var2) -> {
            JsonObject var3 = new JsonObject();
            var1.add(var1x.id(), var3);
            var2.forEach(var1xx -> {
               JsonObject var2x = new JsonObject();
               var3.add(var1xx.flow().id(), var2x);
               var1xx.listPackets((var1xxx, var2xx) -> {
                  JsonObject var3x = new JsonObject();
                  var3x.addProperty("protocol_id", var2xx);
                  var2x.add(var1xxx.id().toString(), var3x);
               });
            });
         });
      return var1;
   }

   @Override
   public String getName() {
      return "Packet Report";
   }
}