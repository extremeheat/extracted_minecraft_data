package net.minecraft.server.bossevents;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
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

   public CompoundTag save(HolderLookup.Provider var1) {
      CompoundTag var2 = new CompoundTag();
      Iterator var3 = this.events.values().iterator();

      while(var3.hasNext()) {
         CustomBossEvent var4 = (CustomBossEvent)var3.next();
         var2.put(var4.getTextId().toString(), var4.save(var1));
      }

      return var2;
   }

   public void load(CompoundTag var1, HolderLookup.Provider var2) {
      Iterator var3 = var1.getAllKeys().iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         ResourceLocation var5 = ResourceLocation.parse(var4);
         this.events.put(var5, CustomBossEvent.load(var1.getCompound(var4), var5, var2));
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
