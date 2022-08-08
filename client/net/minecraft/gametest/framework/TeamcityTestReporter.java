package net.minecraft.gametest.framework;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import org.slf4j.Logger;

public class TeamcityTestReporter implements TestReporter {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Escaper ESCAPER = Escapers.builder().addEscape('\'', "|'").addEscape('\n', "|n").addEscape('\r', "|r").addEscape('|', "||").addEscape('[', "|[").addEscape(']', "|]").build();

   public TeamcityTestReporter() {
      super();
   }

   public void onTestFailed(GameTestInfo var1) {
      String var2 = ESCAPER.escape(var1.getTestName());
      String var3 = ESCAPER.escape(var1.getError().getMessage());
      String var4 = ESCAPER.escape(Util.describeError(var1.getError()));
      LOGGER.info("##teamcity[testStarted name='{}']", var2);
      if (var1.isRequired()) {
         LOGGER.info("##teamcity[testFailed name='{}' message='{}' details='{}']", new Object[]{var2, var3, var4});
      } else {
         LOGGER.info("##teamcity[testIgnored name='{}' message='{}' details='{}']", new Object[]{var2, var3, var4});
      }

      LOGGER.info("##teamcity[testFinished name='{}' duration='{}']", var2, var1.getRunTime());
   }

   public void onTestSuccess(GameTestInfo var1) {
      String var2 = ESCAPER.escape(var1.getTestName());
      LOGGER.info("##teamcity[testStarted name='{}']", var2);
      LOGGER.info("##teamcity[testFinished name='{}' duration='{}']", var2, var1.getRunTime());
   }
}
