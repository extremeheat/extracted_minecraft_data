package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
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

   public void run() {
      this.setTitle(new TranslatableComponent("mco.connect.connecting"));
      net.minecraft.realms.RealmsServerAddress var1 = net.minecraft.realms.RealmsServerAddress.parseString(this.address.address);
      this.realmsConnect.connect(this.server, var1.getHost(), var1.getPort());
   }

   public void abortTask() {
      this.realmsConnect.abort();
      Minecraft.getInstance().getClientPackSource().clearServerPack();
   }

   public void tick() {
      this.realmsConnect.tick();
   }
}
