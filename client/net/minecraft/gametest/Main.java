package net.minecraft.gametest;

import net.minecraft.SharedConstants;
import net.minecraft.gametest.framework.GameTestMainUtil;
import net.minecraft.obfuscate.DontObfuscate;

public class Main {
   public Main() {
      super();
   }

   @DontObfuscate
   public static void main(String[] var0) throws Exception {
      SharedConstants.tryDetectVersion();
      GameTestMainUtil.runGameTestServer(var0, (var0x) -> {
      });
   }
}
