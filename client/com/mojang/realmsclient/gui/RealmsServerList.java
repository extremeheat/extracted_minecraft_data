package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.dto.RealmsServer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;

public class RealmsServerList implements Iterable<RealmsServer> {
   private final Minecraft minecraft;
   private final Set<RealmsServer> removedServers = new HashSet();
   private List<RealmsServer> servers = List.of();

   public RealmsServerList(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void updateServersList(List<RealmsServer> var1) {
      ArrayList var2 = new ArrayList(var1);
      var2.sort(new RealmsServer.McoServerComparator(this.minecraft.getUser().getName()));
      boolean var3 = var2.removeAll(this.removedServers);
      if (!var3) {
         this.removedServers.clear();
      }

      this.servers = var2;
   }

   public void removeItem(RealmsServer var1) {
      this.servers.remove(var1);
      this.removedServers.add(var1);
   }

   public Iterator<RealmsServer> iterator() {
      return this.servers.iterator();
   }

   public boolean isEmpty() {
      return this.servers.isEmpty();
   }
}
