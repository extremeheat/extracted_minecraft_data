package net.minecraft.world.level.timers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;

public record FunctionTagCallback(Identifier tagId) implements TimerCallback<MinecraftServer> {
   public static final MapCodec<FunctionTagCallback> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Identifier.CODEC.fieldOf("Name").forGetter(FunctionTagCallback::tagId)).apply(var0, FunctionTagCallback::new));

   public FunctionTagCallback(Identifier var1) {
      super();
      this.tagId = var1;
   }

   public void handle(MinecraftServer var1, TimerQueue<MinecraftServer> var2, long var3) {
      ServerFunctionManager var5 = var1.getFunctions();

      for(CommandFunction var8 : var5.getTag(this.tagId)) {
         var5.execute(var8, var5.getGameLoopSender());
      }

   }

   public MapCodec<FunctionTagCallback> codec() {
      return CODEC;
   }
}
