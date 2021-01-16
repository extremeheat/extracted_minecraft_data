package org.apache.logging.log4j.core.net.mom.jms;

import org.apache.logging.log4j.core.net.server.JmsServer;

public abstract class AbstractJmsReceiver {
   public AbstractJmsReceiver() {
      super();
   }

   protected abstract void usage();

   protected void doMain(String... var1) throws Exception {
      if (var1.length != 4) {
         this.usage();
         System.exit(1);
      }

      JmsServer var2 = new JmsServer(var1[0], var1[1], var1[2], var1[3]);
      var2.run();
   }
}
