package net.minecraft.world.level.timers;

import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandFunction;
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
      Collection var6 = var5.getTag(this.tagId);
      Iterator var7 = var6.iterator();

      while(var7.hasNext()) {
         CommandFunction var8 = (CommandFunction)var7.next();
         var5.execute(var8, var5.getGameLoopSender());
      }

   }

   // $FF: synthetic method
   public void handle(Object var1, TimerQueue var2, long var3) {
      this.handle((MinecraftServer)var1, var2, var3);
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

      // $FF: synthetic method
      public TimerCallback deserialize(CompoundTag var1) {
         return this.deserialize(var1);
      }
   }
}
