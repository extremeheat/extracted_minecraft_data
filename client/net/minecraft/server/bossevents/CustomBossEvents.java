package net.minecraft.server.bossevents;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class CustomBossEvents {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Codec<Map<Identifier, CustomBossEvent.Packed>> EVENTS_CODEC;
   private final Map<Identifier, CustomBossEvent> events = Maps.newHashMap();

   public CustomBossEvents() {
      super();
   }

   public @Nullable CustomBossEvent get(final Identifier id) {
      return (CustomBossEvent)this.events.get(id);
   }

   public CustomBossEvent create(final Identifier id, final Component name) {
      CustomBossEvent result = new CustomBossEvent(id, name);
      this.events.put(id, result);
      return result;
   }

   public void remove(final CustomBossEvent event) {
      this.events.remove(event.getTextId());
   }

   public Collection<Identifier> getIds() {
      return this.events.keySet();
   }

   public Collection<CustomBossEvent> getEvents() {
      return this.events.values();
   }

   public CompoundTag save(final HolderLookup.Provider registries) {
      Map<Identifier, CustomBossEvent.Packed> packedEvents = Util.mapValues(this.events, CustomBossEvent::pack);
      return (CompoundTag)EVENTS_CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), packedEvents).getOrThrow();
   }

   public void load(final CompoundTag tag, final HolderLookup.Provider registries) {
      Map<Identifier, CustomBossEvent.Packed> events = (Map)EVENTS_CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag).resultOrPartial((error) -> LOGGER.error("Failed to parse boss bar events: {}", error)).orElse(Map.of());
      events.forEach((id, packed) -> this.events.put(id, CustomBossEvent.load(id, packed)));
   }

   public void onPlayerConnect(final ServerPlayer player) {
      for(CustomBossEvent event : this.events.values()) {
         event.onPlayerConnect(player);
      }

   }

   public void onPlayerDisconnect(final ServerPlayer player) {
      for(CustomBossEvent event : this.events.values()) {
         event.onPlayerDisconnect(player);
      }

   }

   static {
      EVENTS_CODEC = Codec.unboundedMap(Identifier.CODEC, CustomBossEvent.Packed.CODEC);
   }
}
