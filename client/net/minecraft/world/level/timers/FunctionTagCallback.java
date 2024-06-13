package net.minecraft.world.level.timers;

import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;

public class FunctionTagCallback implements TimerCallback<MinecraftServer> {
   final ResourceLocation tagId;

   public FunctionTagCallback(ResourceLocation var1) {
      super();
      this.tagId = var1;
   }

   public void handle(MinecraftServer var1, TimerQueue<MinecraftServer> var2, long var3) {
      ServerFunctionManager var5 = var1.getFunctions();

      for (CommandFunction var8 : var5.getTag(this.tagId)) {
         var5.execute(var8, var5.getGameLoopSender());
      }
   }

   public static class Serializer extends TimerCallback.Serializer<MinecraftServer, FunctionTagCallback> {
      public Serializer() {
         super(new ResourceLocation("function_tag"), FunctionTagCallback.class);
      }

      public void serialize(CompoundTag var1, FunctionTagCallback var2) {
         var1.putString("Name", var2.tagId.toString());
      }

      public FunctionTagCallback deserialize(CompoundTag var1) {
         ResourceLocation var2 = new ResourceLocation(var1.getString("Name"));
         return new FunctionTagCallback(var2);
      }
   }
}
