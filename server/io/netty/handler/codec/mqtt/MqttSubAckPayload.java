package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MqttSubAckPayload {
   private final List<Integer> grantedQoSLevels;

   public MqttSubAckPayload(int... var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("grantedQoSLevels");
      } else {
         ArrayList var2 = new ArrayList(var1.length);
         int[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = var3[var5];
            var2.add(var6);
         }

         this.grantedQoSLevels = Collections.unmodifiableList(var2);
      }
   }

   public MqttSubAckPayload(Iterable<Integer> var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("grantedQoSLevels");
      } else {
         ArrayList var2 = new ArrayList();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Integer var4 = (Integer)var3.next();
            if (var4 == null) {
               break;
            }

            var2.add(var4);
         }

         this.grantedQoSLevels = Collections.unmodifiableList(var2);
      }
   }

   public List<Integer> grantedQoSLevels() {
      return this.grantedQoSLevels;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '[' + "grantedQoSLevels=" + this.grantedQoSLevels + ']';
   }
}
