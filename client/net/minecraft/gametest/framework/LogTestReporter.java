package net.minecraft.gametest.framework;

import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import org.slf4j.Logger;

public class LogTestReporter implements TestReporter {
   private static final Logger LOGGER = LogUtils.getLogger();

   public LogTestReporter() {
      super();
   }

   public void onTestFailed(GameTestInfo var1) {
      String var2 = var1.getStructureBlockPos().toShortString();
      if (var1.isRequired()) {
         LOGGER.error("{} failed at {}! {}", new Object[]{var1.getTestName(), var2, Util.describeError(var1.getError())});
      } else {
         LOGGER.warn("(optional) {} failed at {}. {}", new Object[]{var1.getTestName(), var2, Util.describeError(var1.getError())});
      }

   }

   public void onTestSuccess(GameTestInfo var1) {
   }
}
