package net.minecraft.profiler;

import java.util.HashMap;
import java.util.TimerTask;
import net.minecraft.util.HttpUtil;

class PlayerUsageSnooper$1 extends TimerTask {
   PlayerUsageSnooper$1(PlayerUsageSnooper var1) {
      super();
      this.field_76344_a = var1;
   }

   @Override
   public void run() {
      if (PlayerUsageSnooper.access$000(this.field_76344_a).func_70002_Q()) {
         HashMap var1;
         synchronized(PlayerUsageSnooper.access$100(this.field_76344_a)) {
            var1 = new HashMap(PlayerUsageSnooper.access$200(this.field_76344_a));
            if (PlayerUsageSnooper.access$300(this.field_76344_a) == 0) {
               var1.putAll(PlayerUsageSnooper.access$400(this.field_76344_a));
            }

            var1.put("snooper_count", PlayerUsageSnooper.access$308(this.field_76344_a));
            var1.put("snooper_token", PlayerUsageSnooper.access$500(this.field_76344_a));
         }

         HttpUtil.func_151226_a(PlayerUsageSnooper.access$600(this.field_76344_a), var1, true);
      }
   }
}
