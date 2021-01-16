package org.apache.logging.log4j.message;

public final class SimpleMessageFactory extends AbstractMessageFactory {
   public static final SimpleMessageFactory INSTANCE = new SimpleMessageFactory();
   private static final long serialVersionUID = 4418995198790088516L;

   public SimpleMessageFactory() {
      super();
   }

   public Message newMessage(String var1, Object... var2) {
      return new SimpleMessage(var1);
   }

   public Message newMessage(String var1, Object var2) {
      return new SimpleMessage(var1);
   }

   public Message newMessage(String var1, Object var2, Object var3) {
      return new SimpleMessage(var1);
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4) {
      return new SimpleMessage(var1);
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5) {
      return new SimpleMessage(var1);
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      return new SimpleMessage(var1);
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      return new SimpleMessage(var1);
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      return new SimpleMessage(var1);
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return new SimpleMessage(var1);
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return new SimpleMessage(var1);
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      return new SimpleMessage(var1);
   }
}
