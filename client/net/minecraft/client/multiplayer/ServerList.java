package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.thread.ConsecutiveExecutor;
import org.slf4j.Logger;

public class ServerList {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ConsecutiveExecutor IO_EXECUTOR = new ConsecutiveExecutor(Util.backgroundExecutor(), "server-list-io");
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

         for (int var3 = 0; var3 < var2.size(); var3++) {
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

         for (ServerData var3 : this.serverList) {
            CompoundTag var4 = var3.write();
            var4.putBoolean("hidden", false);
            var1.add(var4);
         }

         for (ServerData var10 : this.hiddenServerList) {
            CompoundTag var12 = var10.write();
            var12.putBoolean("hidden", true);
            var1.add(var12);
         }

         CompoundTag var9 = new CompoundTag();
         var9.put("servers", var1);
         Path var11 = this.minecraft.gameDirectory.toPath();
         Path var13 = Files.createTempFile(var11, "servers", ".dat");
         NbtIo.write(var9, var13);
         Path var5 = var11.resolve("servers.dat_old");
         Path var6 = var11.resolve("servers.dat");
         Util.safeReplaceFile(var6, var13, var5);
      } catch (Exception var7) {
         LOGGER.error("Couldn't save server list", var7);
      }
   }

   public ServerData get(int var1) {
      return this.serverList.get(var1);
   }

   @Nullable
   public ServerData get(String var1) {
      for (ServerData var3 : this.serverList) {
         if (var3.ip.equals(var1)) {
            return var3;
         }
      }

      for (ServerData var5 : this.hiddenServerList) {
         if (var5.ip.equals(var1)) {
            return var5;
         }
      }

      return null;
   }

   @Nullable
   public ServerData unhide(String var1) {
      for (int var2 = 0; var2 < this.hiddenServerList.size(); var2++) {
         ServerData var3 = this.hiddenServerList.get(var2);
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

         while (this.hiddenServerList.size() > 16) {
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
      for (int var2 = 0; var2 < var1.size(); var2++) {
         ServerData var3 = (ServerData)var1.get(var2);
         if (Objects.equals(var3.name, var0.name) && var3.ip.equals(var0.ip)) {
            var1.set(var2, var0);
            return true;
         }
      }

      return false;
   }

   public static void saveSingleServer(ServerData var0) {
      IO_EXECUTOR.schedule(() -> {
         ServerList var1 = new ServerList(Minecraft.getInstance());
         var1.load();
         if (!set(var0, var1.serverList)) {
            set(var0, var1.hiddenServerList);
         }

         var1.save();
      });
   }
}
