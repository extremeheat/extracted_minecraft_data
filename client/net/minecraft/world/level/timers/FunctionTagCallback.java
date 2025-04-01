package net.minecraft.world.level.timers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.TheGame;

public record FunctionTagCallback(ResourceLocation tagId) implements TimerCallback<TheGame> {
   public static final MapCodec<FunctionTagCallback> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("Name").forGetter(FunctionTagCallback::tagId)).apply(var0, FunctionTagCallback::new));

   public FunctionTagCallback(ResourceLocation var1) {
      super();
      this.tagId = var1;
   }

   public void handle(TheGame var1, TimerQueue<TheGame> var2, long var3) {
      ServerFunctionManager var5 = var1.getFunctions();

      for(CommandFunction var8 : var5.getTag(this.tagId)) {
         var5.execute(var8, var5.getGameLoopSender());
      }

   }

   public MapCodec<FunctionTagCallback> codec() {
      return CODEC;
   }
}
