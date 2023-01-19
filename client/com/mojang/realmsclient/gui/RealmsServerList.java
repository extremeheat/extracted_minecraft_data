package com.mojang.realmsclient.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.dto.RealmsServer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;

public class RealmsServerList {
   private final Minecraft minecraft;
   private final Set<RealmsServer> removedServers = Sets.newHashSet();
   private List<RealmsServer> servers = Lists.newArrayList();

   public RealmsServerList(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public List<RealmsServer> updateServersList(List<RealmsServer> var1) {
      ArrayList var2 = new ArrayList(var1);
      var2.sort(new RealmsServer.McoServerComparator(this.minecraft.getUser().getName()));
      boolean var3 = var2.removeAll(this.removedServers);
      if (!var3) {
         this.removedServers.clear();
      }

      this.servers = var2;
      return List.copyOf(this.servers);
   }

   public synchronized List<RealmsServer> removeItem(RealmsServer var1) {
      this.servers.remove(var1);
      this.removedServers.add(var1);
      return List.copyOf(this.servers);
   }
}
