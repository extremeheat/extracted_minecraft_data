package org.apache.logging.log4j.core.net.mom.jms;

public class JmsTopicReceiver extends AbstractJmsReceiver {
   private JmsTopicReceiver() {
      super();
   }

   public static void main(String[] var0) throws Exception {
      JmsTopicReceiver var1 = new JmsTopicReceiver();
      var1.doMain(var0);
   }

   protected void usage() {
      System.err.println("Wrong number of arguments.");
      System.err.println("Usage: java " + JmsTopicReceiver.class.getName() + " TopicConnectionFactoryBindingName TopicBindingName username password");
   }
}
