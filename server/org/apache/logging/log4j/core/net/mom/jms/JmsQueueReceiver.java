package org.apache.logging.log4j.core.net.mom.jms;

public class JmsQueueReceiver extends AbstractJmsReceiver {
   private JmsQueueReceiver() {
      super();
   }

   public static void main(String[] var0) throws Exception {
      JmsQueueReceiver var1 = new JmsQueueReceiver();
      var1.doMain(var0);
   }

   protected void usage() {
      System.err.println("Wrong number of arguments.");
      System.err.println("Usage: java " + JmsQueueReceiver.class.getName() + " QueueConnectionFactoryBindingName QueueBindingName username password");
   }
}
