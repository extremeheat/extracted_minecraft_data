package net.minecraft.gametest.framework;

import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import org.slf4j.Logger;

public class LogTestReporter implements TestReporter {
   private static final Logger LOGGER = LogUtils.getLogger();

   public LogTestReporter() {
      super();
   }

   @Override
   public void onTestFailed(GameTestInfo var1) {
      if (var1.isRequired()) {
         LOGGER.error("{} failed! {}", var1.getTestName(), Util.describeError(var1.getError()));
      } else {
         LOGGER.warn("(optional) {} failed. {}", var1.getTestName(), Util.describeError(var1.getError()));
      }
   }

   @Override
   public void onTestSuccess(GameTestInfo var1) {
   }
}
