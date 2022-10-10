package net.minecraft.network.login;

import net.minecraft.network.INetHandler;
import net.minecraft.network.login.server.SPacketCustomPayloadLogin;
import net.minecraft.network.login.server.SPacketDisconnectLogin;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;

public interface INetHandlerLoginClient extends INetHandler {
   void func_147389_a(SPacketEncryptionRequest var1);

   void func_147390_a(SPacketLoginSuccess var1);

   void func_147388_a(SPacketDisconnectLogin var1);

   void func_180464_a(SPacketEnableCompression var1);

   void func_209521_a(SPacketCustomPayloadLogin var1);
}
