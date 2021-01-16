package net.minecraft.server.bossevents;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class CustomBossEvents {
   private final Map<ResourceLocation, CustomBossEvent> events = Maps.newHashMap();

   public CustomBossEvents() {
      super();
   }

   @Nullable
   public CustomBossEvent get(ResourceLocation var1) {
      return (CustomBossEvent)this.events.get(var1);
   }

   public CustomBossEvent create(ResourceLocation var1, Component var2) {
      CustomBossEvent var3 = new CustomBossEvent(var1, var2);
      this.events.put(var1, var3);
      return var3;
   }

   public void remove(CustomBossEvent var1) {
      this.events.remove(var1.getTextId());
   }

   public Collection<ResourceLocation> getIds() {
      return this.events.keySet();
   }

   public Collection<CustomBossEvent> getEvents() {
      return this.events.values();
   }

   public CompoundTag save() {
      CompoundTag var1 = new CompoundTag();
      Iterator var2 = this.events.values().iterator();

      while(var2.hasNext()) {
         CustomBossEvent var3 = (CustomBossEvent)var2.next();
         var1.put(var3.getTextId().toString(), var3.save());
      }

      return var1;
   }

   public void load(CompoundTag var1) {
      Iterator var2 = var1.getAllKeys().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         ResourceLocation var4 = new ResourceLocation(var3);
         this.events.put(var4, CustomBossEvent.load(var1.getCompound(var3), var4));
      }

   }

   public void onPlayerConnect(ServerPlayer var1) {
      Iterator var2 = this.events.values().iterator();

      while(var2.hasNext()) {
         CustomBossEvent var3 = (CustomBossEvent)var2.next();
         var3.onPlayerConnect(var1);
      }

   }

   public void onPlayerDisconnect(ServerPlayer var1) {
      Iterator var2 = this.events.values().iterator();

      while(var2.hasNext()) {
         CustomBossEvent var3 = (CustomBossEvent)var2.next();
         var3.onPlayerDisconnect(var1);
      }

   }
}
