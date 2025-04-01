package net.minecraft.world.level.timers;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TheGame;
import net.minecraft.util.ExtraCodecs;

public class TimerCallbacks<C> {
   public static final TimerCallbacks<TheGame> SERVER_CALLBACKS;
   private final ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends TimerCallback<C>>> idMapper = new ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends TimerCallback<C>>>();
   private final Codec<TimerCallback<C>> codec;

   @VisibleForTesting
   public TimerCallbacks() {
      super();
      this.codec = this.idMapper.codec(ResourceLocation.CODEC).dispatch("Type", TimerCallback::codec, Function.identity());
   }

   public TimerCallbacks<C> register(ResourceLocation var1, MapCodec<? extends TimerCallback<C>> var2) {
      this.idMapper.put(var1, var2);
      return this;
   }

   public Codec<TimerCallback<C>> codec() {
      return this.codec;
   }

   static {
      SERVER_CALLBACKS = (new TimerCallbacks<TheGame>()).register(ResourceLocation.withDefaultNamespace("function"), FunctionCallback.CODEC).register(ResourceLocation.withDefaultNamespace("function_tag"), FunctionTagCallback.CODEC);
   }
}
