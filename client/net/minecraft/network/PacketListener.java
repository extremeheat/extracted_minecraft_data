package net.minecraft.network;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

public interface PacketListener {
   PacketFlow flow();

   ConnectionProtocol protocol();

   void onDisconnect(Component var1);

   boolean isAcceptingMessages();

   default boolean shouldHandleMessage(Packet<?> var1) {
      return this.isAcceptingMessages();
   }

   default boolean shouldPropagateHandlingExceptions() {
      return true;
   }

   default void fillCrashReport(CrashReport var1) {
      CrashReportCategory var2 = var1.addCategory("Connection");
      var2.setDetail("Protocol", () -> this.protocol().id());
      var2.setDetail("Flow", () -> this.flow().toString());
      this.fillListenerSpecificCrashDetails(var2);
   }

   default void fillListenerSpecificCrashDetails(CrashReportCategory var1) {
   }
}
