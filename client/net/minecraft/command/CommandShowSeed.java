package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class CommandShowSeed extends CommandBase {
   public CommandShowSeed() {
      super();
   }

   @Override
   public boolean func_71519_b(ICommandSender var1) {
      return MinecraftServer.func_71276_C().func_71264_H() || super.func_71519_b(var1);
   }

   @Override
   public String func_71517_b() {
      return "seed";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.seed.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      Object var3 = var1 instanceof EntityPlayer ? ((EntityPlayer)var1).field_70170_p : MinecraftServer.func_71276_C().func_71218_a(0);
      var1.func_145747_a(new ChatComponentTranslation("commands.seed.success", ((World)var3).func_72905_C()));
   }
}
