package net.minecraft.server.bossevents;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

public class CustomBossEvents {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Codec<Map<ResourceLocation, CustomBossEvent.Packed>> EVENTS_CODEC;
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
      var1.removeAllPlayers();
      this.events.remove(var1.getTextId());
   }

   public Collection<ResourceLocation> getIds() {
      return this.events.keySet();
   }

   public Collection<CustomBossEvent> getEvents() {
      return this.events.values();
   }

   public CompoundTag save(HolderLookup.Provider var1) {
      Map var2 = Util.mapValues(this.events, CustomBossEvent::pack);
      return (CompoundTag)EVENTS_CODEC.encodeStart(var1.createSerializationContext(NbtOps.INSTANCE), var2).getOrThrow();
   }

   public void load(CompoundTag var1, HolderLookup.Provider var2) {
      Map var3 = (Map)EVENTS_CODEC.parse(var2.createSerializationContext(NbtOps.INSTANCE), var1).resultOrPartial((var0) -> LOGGER.error("Failed to parse boss bar events: {}", var0)).orElse(Map.of());
      var3.forEach((var1x, var2x) -> this.events.put(var1x, CustomBossEvent.load(var1x, var2x)));
   }

   public void onPlayerConnect(ServerPlayer var1) {
      for(CustomBossEvent var3 : this.events.values()) {
         var3.onPlayerConnect(var1);
      }

   }

   public void onPlayerDisconnect(ServerPlayer var1) {
      for(CustomBossEvent var3 : this.events.values()) {
         var3.onPlayerDisconnect(var1);
      }

   }

   static {
      EVENTS_CODEC = Codec.unboundedMap(ResourceLocation.CODEC, CustomBossEvent.Packed.CODEC);
   }
}
