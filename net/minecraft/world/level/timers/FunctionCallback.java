package net.minecraft.world.level.timers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;

public class FunctionCallback implements TimerCallback {
   private final ResourceLocation functionId;

   public FunctionCallback(ResourceLocation var1) {
      this.functionId = var1;
   }

   public void handle(MinecraftServer var1, TimerQueue var2, long var3) {
      ServerFunctionManager var5 = var1.getFunctions();
      var5.get(this.functionId).ifPresent((var1x) -> {
         var5.execute(var1x, var5.getGameLoopSender());
      });
   }

   public static class Serializer extends TimerCallback.Serializer {
      public Serializer() {
         super(new ResourceLocation("function"), FunctionCallback.class);
      }

      public void serialize(CompoundTag var1, FunctionCallback var2) {
         var1.putString("Name", var2.functionId.toString());
      }

      public FunctionCallback deserialize(CompoundTag var1) {
         ResourceLocation var2 = new ResourceLocation(var1.getString("Name"));
         return new FunctionCallback(var2);
      }

      // $FF: synthetic method
      public TimerCallback deserialize(CompoundTag var1) {
         return this.deserialize(var1);
      }
   }
}
