package net.minecraft.command;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public interface ICommandSender {
   String func_70005_c_();

   IChatComponent func_145748_c_();

   void func_145747_a(IChatComponent var1);

   boolean func_70003_b(int var1, String var2);

   ChunkCoordinates func_82114_b();

   World func_130014_f_();
}
