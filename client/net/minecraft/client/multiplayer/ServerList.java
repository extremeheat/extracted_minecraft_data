package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerList {
   private static final Logger field_147415_a = LogManager.getLogger();
   private final Minecraft field_78859_a;
   private final List<ServerData> field_78858_b = Lists.newArrayList();

   public ServerList(Minecraft var1) {
      super();
      this.field_78859_a = var1;
      this.func_78853_a();
   }

   public void func_78853_a() {
      try {
         this.field_78858_b.clear();
         NBTTagCompound var1 = CompressedStreamTools.func_74797_a(new File(this.field_78859_a.field_71412_D, "servers.dat"));
         if (var1 == null) {
            return;
         }

         NBTTagList var2 = var1.func_150295_c("servers", 10);

         for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
            this.field_78858_b.add(ServerData.func_78837_a(var2.func_150305_b(var3)));
         }
      } catch (Exception var4) {
         field_147415_a.error("Couldn't load server list", var4);
      }

   }

   public void func_78855_b() {
      try {
         NBTTagList var1 = new NBTTagList();
         Iterator var2 = this.field_78858_b.iterator();

         while(var2.hasNext()) {
            ServerData var3 = (ServerData)var2.next();
            var1.func_74742_a(var3.func_78836_a());
         }

         NBTTagCompound var5 = new NBTTagCompound();
         var5.func_74782_a("servers", var1);
         CompressedStreamTools.func_74793_a(var5, new File(this.field_78859_a.field_71412_D, "servers.dat"));
      } catch (Exception var4) {
         field_147415_a.error("Couldn't save server list", var4);
      }

   }

   public ServerData func_78850_a(int var1) {
      return (ServerData)this.field_78858_b.get(var1);
   }

   public void func_78851_b(int var1) {
      this.field_78858_b.remove(var1);
   }

   public void func_78849_a(ServerData var1) {
      this.field_78858_b.add(var1);
   }

   public int func_78856_c() {
      return this.field_78858_b.size();
   }

   public void func_78857_a(int var1, int var2) {
      ServerData var3 = this.func_78850_a(var1);
      this.field_78858_b.set(var1, this.func_78850_a(var2));
      this.field_78858_b.set(var2, var3);
      this.func_78855_b();
   }

   public void func_147413_a(int var1, ServerData var2) {
      this.field_78858_b.set(var1, var2);
   }

   public static void func_147414_b(ServerData var0) {
      ServerList var1 = new ServerList(Minecraft.func_71410_x());
      var1.func_78853_a();

      for(int var2 = 0; var2 < var1.func_78856_c(); ++var2) {
         ServerData var3 = var1.func_78850_a(var2);
         if (var3.field_78847_a.equals(var0.field_78847_a) && var3.field_78845_b.equals(var0.field_78845_b)) {
            var1.func_147413_a(var2, var0);
            break;
         }
      }

      var1.func_78855_b();
   }
}
