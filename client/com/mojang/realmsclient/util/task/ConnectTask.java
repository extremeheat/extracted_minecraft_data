package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsConnect;

public class ConnectTask extends LongRunningTask {
   private final RealmsConnect realmsConnect;
   private final RealmsServer server;
   private final RealmsServerAddress address;

   public ConnectTask(Screen var1, RealmsServer var2, RealmsServerAddress var3) {
      super();
      this.server = var2;
      this.address = var3;
      this.realmsConnect = new RealmsConnect(var1);
   }

   @Override
   public void run() {
      this.setTitle(Component.translatable("mco.connect.connecting"));
      this.realmsConnect.connect(this.server, ServerAddress.parseString(this.address.address));
   }

   @Override
   public void abortTask() {
      this.realmsConnect.abort();
      Minecraft.getInstance().getDownloadedPackSource().clearServerPack();
   }

   @Override
   public void tick() {
      this.realmsConnect.tick();
   }
}
