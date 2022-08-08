package net.minecraft.world.level.timers;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

public class TimerCallbacks<C> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final TimerCallbacks<MinecraftServer> SERVER_CALLBACKS = (new TimerCallbacks()).register(new FunctionCallback.Serializer()).register(new FunctionTagCallback.Serializer());
   private final Map<ResourceLocation, TimerCallback.Serializer<C, ?>> idToSerializer = Maps.newHashMap();
   private final Map<Class<?>, TimerCallback.Serializer<C, ?>> classToSerializer = Maps.newHashMap();

   @VisibleForTesting
   public TimerCallbacks() {
      super();
   }

   public TimerCallbacks<C> register(TimerCallback.Serializer<C, ?> var1) {
      this.idToSerializer.put(var1.getId(), var1);
      this.classToSerializer.put(var1.getCls(), var1);
      return this;
   }

   private <T extends TimerCallback<C>> TimerCallback.Serializer<C, T> getSerializer(Class<?> var1) {
      return (TimerCallback.Serializer)this.classToSerializer.get(var1);
   }

   public <T extends TimerCallback<C>> CompoundTag serialize(T var1) {
      TimerCallback.Serializer var2 = this.getSerializer(var1.getClass());
      CompoundTag var3 = new CompoundTag();
      var2.serialize(var3, var1);
      var3.putString("Type", var2.getId().toString());
      return var3;
   }

   @Nullable
   public TimerCallback<C> deserialize(CompoundTag var1) {
      ResourceLocation var2 = ResourceLocation.tryParse(var1.getString("Type"));
      TimerCallback.Serializer var3 = (TimerCallback.Serializer)this.idToSerializer.get(var2);
      if (var3 == null) {
         LOGGER.error("Failed to deserialize timer callback: {}", var1);
         return null;
      } else {
         try {
            return var3.deserialize(var1);
         } catch (Exception var5) {
            LOGGER.error("Failed to deserialize timer callback: {}", var1, var5);
            return null;
         }
      }
   }
}
