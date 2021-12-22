package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerList {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final List<ServerData> serverList = Lists.newArrayList();

   public ServerList(Minecraft var1) {
      super();
      this.minecraft = var1;
      this.load();
   }

   public void load() {
      try {
         this.serverList.clear();
         CompoundTag var1 = NbtIo.read(new File(this.minecraft.gameDirectory, "servers.dat"));
         if (var1 == null) {
            return;
         }

         ListTag var2 = var1.getList("servers", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            this.serverList.add(ServerData.read(var2.getCompound(var3)));
         }
      } catch (Exception var4) {
         LOGGER.error("Couldn't load server list", var4);
      }

   }

   public void save() {
      try {
         ListTag var1 = new ListTag();
         Iterator var2 = this.serverList.iterator();

         while(var2.hasNext()) {
            ServerData var3 = (ServerData)var2.next();
            var1.add(var3.write());
         }

         CompoundTag var7 = new CompoundTag();
         var7.put("servers", var1);
         File var8 = File.createTempFile("servers", ".dat", this.minecraft.gameDirectory);
         NbtIo.write(var7, var8);
         File var4 = new File(this.minecraft.gameDirectory, "servers.dat_old");
         File var5 = new File(this.minecraft.gameDirectory, "servers.dat");
         Util.safeReplaceFile(var5, var8, var4);
      } catch (Exception var6) {
         LOGGER.error("Couldn't save server list", var6);
      }

   }

   public ServerData get(int var1) {
      return (ServerData)this.serverList.get(var1);
   }

   public void remove(ServerData var1) {
      this.serverList.remove(var1);
   }

   public void add(ServerData var1) {
      this.serverList.add(var1);
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

   public static void saveSingleServer(ServerData var0) {
      ServerList var1 = new ServerList(Minecraft.getInstance());
      var1.load();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         ServerData var3 = var1.get(var2);
         if (var3.name.equals(var0.name) && var3.field_277.equals(var0.field_277)) {
            var1.replace(var2, var0);
            break;
         }
      }

      var1.save();
   }
}
