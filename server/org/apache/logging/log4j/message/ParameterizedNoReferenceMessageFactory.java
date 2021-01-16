package org.apache.logging.log4j.message;

public final class ParameterizedNoReferenceMessageFactory extends AbstractMessageFactory {
   private static final long serialVersionUID = 5027639245636870500L;
   public static final ParameterizedNoReferenceMessageFactory INSTANCE = new ParameterizedNoReferenceMessageFactory();

   public ParameterizedNoReferenceMessageFactory() {
      super();
   }

   public Message newMessage(String var1, Object... var2) {
      if (var2 == null) {
         return new SimpleMessage(var1);
      } else {
         ParameterizedMessage var3 = new ParameterizedMessage(var1, var2);
         return new ParameterizedNoReferenceMessageFactory.StatusMessage(var3.getFormattedMessage(), var3.getThrowable());
      }
   }

   static class StatusMessage implements Message {
      private final String formattedMessage;
      private final Throwable throwable;

      public StatusMessage(String var1, Throwable var2) {
         super();
         this.formattedMessage = var1;
         this.throwable = var2;
      }

      public String getFormattedMessage() {
         return this.formattedMessage;
      }

      public String getFormat() {
         return this.formattedMessage;
      }

      public Object[] getParameters() {
         return null;
      }

      public Throwable getThrowable() {
         return this.throwable;
      }
   }
}
