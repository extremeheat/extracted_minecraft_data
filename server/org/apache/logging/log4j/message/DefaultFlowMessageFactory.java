package org.apache.logging.log4j.message;

import java.io.Serializable;

public class DefaultFlowMessageFactory implements FlowMessageFactory, Serializable {
   private static final String EXIT_DEFAULT_PREFIX = "Exit";
   private static final String ENTRY_DEFAULT_PREFIX = "Enter";
   private static final long serialVersionUID = 8578655591131397576L;
   private final String entryText;
   private final String exitText;

   public DefaultFlowMessageFactory() {
      this("Enter", "Exit");
   }

   public DefaultFlowMessageFactory(String var1, String var2) {
      super();
      this.entryText = var1;
      this.exitText = var2;
   }

   public String getEntryText() {
      return this.entryText;
   }

   public String getExitText() {
      return this.exitText;
   }

   public EntryMessage newEntryMessage(Message var1) {
      return new DefaultFlowMessageFactory.SimpleEntryMessage(this.entryText, this.makeImmutable(var1));
   }

   private Message makeImmutable(Message var1) {
      return (Message)(!(var1 instanceof ReusableMessage) ? var1 : new SimpleMessage(var1.getFormattedMessage()));
   }

   public ExitMessage newExitMessage(EntryMessage var1) {
      return new DefaultFlowMessageFactory.SimpleExitMessage(this.exitText, var1);
   }

   public ExitMessage newExitMessage(Object var1, EntryMessage var2) {
      return new DefaultFlowMessageFactory.SimpleExitMessage(this.exitText, var1, var2);
   }

   public ExitMessage newExitMessage(Object var1, Message var2) {
      return new DefaultFlowMessageFactory.SimpleExitMessage(this.exitText, var1, var2);
   }

   private static final class SimpleExitMessage extends DefaultFlowMessageFactory.AbstractFlowMessage implements ExitMessage {
      private static final long serialVersionUID = 1L;
      private final Object result;
      private final boolean isVoid;

      SimpleExitMessage(String var1, EntryMessage var2) {
         super(var1, var2.getMessage());
         this.result = null;
         this.isVoid = true;
      }

      SimpleExitMessage(String var1, Object var2, EntryMessage var3) {
         super(var1, var3.getMessage());
         this.result = var2;
         this.isVoid = false;
      }

      SimpleExitMessage(String var1, Object var2, Message var3) {
         super(var1, var3);
         this.result = var2;
         this.isVoid = false;
      }

      public String getFormattedMessage() {
         String var1 = super.getFormattedMessage();
         return this.isVoid ? var1 : var1 + ": " + this.result;
      }
   }

   private static final class SimpleEntryMessage extends DefaultFlowMessageFactory.AbstractFlowMessage implements EntryMessage {
      private static final long serialVersionUID = 1L;

      SimpleEntryMessage(String var1, Message var2) {
         super(var1, var2);
      }
   }

   private static class AbstractFlowMessage implements FlowMessage {
      private static final long serialVersionUID = 1L;
      private final Message message;
      private final String text;

      AbstractFlowMessage(String var1, Message var2) {
         super();
         this.message = var2;
         this.text = var1;
      }

      public String getFormattedMessage() {
         return this.message != null ? this.text + " " + this.message.getFormattedMessage() : this.text;
      }

      public String getFormat() {
         return this.message != null ? this.text + ": " + this.message.getFormat() : this.text;
      }

      public Object[] getParameters() {
         return this.message != null ? this.message.getParameters() : null;
      }

      public Throwable getThrowable() {
         return this.message != null ? this.message.getThrowable() : null;
      }

      public Message getMessage() {
         return this.message;
      }

      public String getText() {
         return this.text;
      }
   }
}
