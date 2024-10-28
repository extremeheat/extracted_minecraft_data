package net.minecraft.server.network.config;

import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.configuration.ClientboundFinishConfigurationPacket;
import net.minecraft.server.network.ConfigurationTask;

public class JoinWorldTask implements ConfigurationTask {
   public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type("join_world");

   public JoinWorldTask() {
      super();
   }

   public void start(Consumer<Packet<?>> var1) {
      var1.accept(ClientboundFinishConfigurationPacket.INSTANCE);
   }

   public ConfigurationTask.Type type() {
      return TYPE;
   }
}
