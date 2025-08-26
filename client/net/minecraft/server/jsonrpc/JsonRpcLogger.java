package net.minecraft.server.jsonrpc;

import com.mojang.logging.LogUtils;
import net.minecraft.server.jsonrpc.methods.ClientInfo;
import org.slf4j.Logger;

public class JsonRpcLogger {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String PREFIX = "RPC Connection #{}: ";

   public JsonRpcLogger() {
      super();
   }

   public void log(ClientInfo var1, String var2, Object... var3) {
      if (var3.length == 0) {
         LOGGER.info("RPC Connection #{}: " + var2, var1.connectionId());
      } else {
         LOGGER.info("RPC Connection #{}: " + var2, var1.connectionId(), var3);
      }

   }
}
