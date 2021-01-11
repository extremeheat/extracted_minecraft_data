package net.minecraft.server.integrated;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

public class IntegratedPlayerList extends ServerConfigurationManager {
   private NBTTagCompound field_72416_e;

   public IntegratedPlayerList(IntegratedServer var1) {
      super(var1);
      this.func_152611_a(10);
   }

   protected void func_72391_b(EntityPlayerMP var1) {
      if (var1.func_70005_c_().equals(this.func_72365_p().func_71214_G())) {
         this.field_72416_e = new NBTTagCompound();
         var1.func_70109_d(this.field_72416_e);
      }

      super.func_72391_b(var1);
   }

   public String func_148542_a(SocketAddress var1, GameProfile var2) {
      return var2.getName().equalsIgnoreCase(this.func_72365_p().func_71214_G()) && this.func_152612_a(var2.getName()) != null ? "That name is already taken." : super.func_148542_a(var1, var2);
   }

   public IntegratedServer func_72365_p() {
      return (IntegratedServer)super.func_72365_p();
   }

   public NBTTagCompound func_72378_q() {
      return this.field_72416_e;
   }

   // $FF: synthetic method
   public MinecraftServer func_72365_p() {
      return this.func_72365_p();
   }
}
