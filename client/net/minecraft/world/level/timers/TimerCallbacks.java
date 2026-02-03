package net.minecraft.world.level.timers;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ExtraCodecs;

public class TimerCallbacks<C> {
   public static final TimerCallbacks<MinecraftServer> SERVER_CALLBACKS;
   private final ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends TimerCallback<C>>> idMapper = new ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends TimerCallback<C>>>();
   private final Codec<TimerCallback<C>> codec;

   @VisibleForTesting
   public TimerCallbacks() {
      super();
      this.codec = this.idMapper.codec(Identifier.CODEC).dispatch("Type", TimerCallback::codec, Function.identity());
   }

   public TimerCallbacks<C> register(final Identifier id, final MapCodec<? extends TimerCallback<C>> codec) {
      this.idMapper.put(id, codec);
      return this;
   }

   public Codec<TimerCallback<C>> codec() {
      return this.codec;
   }

   static {
      SERVER_CALLBACKS = (new TimerCallbacks<MinecraftServer>()).register(Identifier.withDefaultNamespace("function"), FunctionCallback.CODEC).register(Identifier.withDefaultNamespace("function_tag"), FunctionTagCallback.CODEC);
   }
}
