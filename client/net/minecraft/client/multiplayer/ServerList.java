package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.thread.ProcessorMailbox;
import org.slf4j.Logger;

public class ServerList {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ProcessorMailbox<Runnable> IO_MAILBOX = ProcessorMailbox.create(Util.backgroundExecutor(), "server-list-io");
   private static final int MAX_HIDDEN_SERVERS = 16;
   private final Minecraft minecraft;
   private final List<ServerData> serverList = Lists.newArrayList();
   private final List<ServerData> hiddenServerList = Lists.newArrayList();

   public ServerList(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void load() {
      try {
         this.serverList.clear();
         this.hiddenServerList.clear();
         CompoundTag var1 = NbtIo.read(this.minecraft.gameDirectory.toPath().resolve("servers.dat"));
         if (var1 == null) {
            return;
         }

         ListTag var2 = var1.getList("servers", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            CompoundTag var4 = var2.getCompound(var3);
            ServerData var5 = ServerData.read(var4);
            if (var4.getBoolean("hidden")) {
               this.hiddenServerList.add(var5);
            } else {
               this.serverList.add(var5);
            }
         }
      } catch (Exception var6) {
         LOGGER.error("Couldn't load server list", var6);
      }

   }

   public void save() {
      try {
         ListTag var1 = new ListTag();
         Iterator var2 = this.serverList.iterator();

         ServerData var3;
         CompoundTag var4;
         while(var2.hasNext()) {
            var3 = (ServerData)var2.next();
            var4 = var3.write();
            var4.putBoolean("hidden", false);
            var1.add(var4);
         }

         var2 = this.hiddenServerList.iterator();

         while(var2.hasNext()) {
            var3 = (ServerData)var2.next();
            var4 = var3.write();
            var4.putBoolean("hidden", true);
            var1.add(var4);
         }

         CompoundTag var8 = new CompoundTag();
         var8.put("servers", var1);
         Path var9 = this.minecraft.gameDirectory.toPath();
         Path var10 = Files.createTempFile(var9, "servers", ".dat");
         NbtIo.write(var8, var10);
         Path var5 = var9.resolve("servers.dat_old");
         Path var6 = var9.resolve("servers.dat");
         Util.safeReplaceFile(var6, var10, var5);
      } catch (Exception var7) {
         LOGGER.error("Couldn't save server list", var7);
      }

   }

   public ServerData get(int var1) {
      return (ServerData)this.serverList.get(var1);
   }

   @Nullable
   public ServerData get(String var1) {
      Iterator var2 = this.serverList.iterator();

      ServerData var3;
      do {
         if (!var2.hasNext()) {
            var2 = this.hiddenServerList.iterator();

            do {
               if (!var2.hasNext()) {
                  return null;
               }

               var3 = (ServerData)var2.next();
            } while(!var3.ip.equals(var1));

            return var3;
         }

         var3 = (ServerData)var2.next();
      } while(!var3.ip.equals(var1));

      return var3;
   }

   @Nullable
   public ServerData unhide(String var1) {
      for(int var2 = 0; var2 < this.hiddenServerList.size(); ++var2) {
         ServerData var3 = (ServerData)this.hiddenServerList.get(var2);
         if (var3.ip.equals(var1)) {
            this.hiddenServerList.remove(var2);
            this.serverList.add(var3);
            return var3;
         }
      }

      return null;
   }

   public void remove(ServerData var1) {
      if (!this.serverList.remove(var1)) {
         this.hiddenServerList.remove(var1);
      }

   }

   public void add(ServerData var1, boolean var2) {
      if (var2) {
         this.hiddenServerList.add(0, var1);

         while(this.hiddenServerList.size() > 16) {
            this.hiddenServerList.remove(this.hiddenServerList.size() - 1);
         }
      } else {
         this.serverList.add(var1);
      }

   }

   public int size() {
      return this.serverList.size();
   }

   public void swap(int var1, int var2) {
      ServerData var3 = this.get(var1);
      this.serverList.set(var1, this.get(var2));
      this.serverList.set(var2, var3);
      this.save();
   }

   public void replace(int var1, ServerData var2) {
      this.serverList.set(var1, var2);
   }

   private static boolean set(ServerData var0, List<ServerData> var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         ServerData var3 = (ServerData)var1.get(var2);
         if (var3.name.equals(var0.name) && var3.ip.equals(var0.ip)) {
            var1.set(var2, var0);
            return true;
         }
      }

      return false;
   }

   public static void saveSingleServer(ServerData var0) {
      IO_MAILBOX.tell(() -> {
         ServerList var1 = new ServerList(Minecraft.getInstance());
         var1.load();
         if (!set(var0, var1.serverList)) {
            set(var0, var1.hiddenServerList);
         }

         var1.save();
      });
   }
}
