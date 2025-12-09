package net.minecraft.world.level.timers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;

public record FunctionCallback(Identifier functionId) implements TimerCallback<MinecraftServer> {
   public static final MapCodec<FunctionCallback> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Identifier.CODEC.fieldOf("Name").forGetter(FunctionCallback::functionId)).apply(var0, FunctionCallback::new));

   public FunctionCallback(Identifier var1) {
      super();
      this.functionId = var1;
   }

   public void handle(MinecraftServer var1, TimerQueue<MinecraftServer> var2, long var3) {
      ServerFunctionManager var5 = var1.getFunctions();
      var5.get(this.functionId).ifPresent((var1x) -> var5.execute(var1x, var5.getGameLoopSender()));
   }

   public MapCodec<FunctionCallback> codec() {
      return CODEC;
   }
}
