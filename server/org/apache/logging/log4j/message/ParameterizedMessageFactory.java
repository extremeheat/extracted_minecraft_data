package org.apache.logging.log4j.message;

public final class ParameterizedMessageFactory extends AbstractMessageFactory {
   public static final ParameterizedMessageFactory INSTANCE = new ParameterizedMessageFactory();
   private static final long serialVersionUID = -8970940216592525651L;

   public ParameterizedMessageFactory() {
      super();
   }

   public Message newMessage(String var1, Object... var2) {
      return new ParameterizedMessage(var1, var2);
   }

   public Message newMessage(String var1, Object var2) {
      return new ParameterizedMessage(var1, var2);
   }

   public Message newMessage(String var1, Object var2, Object var3) {
      return new ParameterizedMessage(var1, var2, var3);
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4) {
      return new ParameterizedMessage(var1, new Object[]{var2, var3, var4});
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5) {
      return new ParameterizedMessage(var1, new Object[]{var2, var3, var4, var5});
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      return new ParameterizedMessage(var1, new Object[]{var2, var3, var4, var5, var6});
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      return new ParameterizedMessage(var1, new Object[]{var2, var3, var4, var5, var6, var7});
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      return new ParameterizedMessage(var1, new Object[]{var2, var3, var4, var5, var6, var7, var8});
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return new ParameterizedMessage(var1, new Object[]{var2, var3, var4, var5, var6, var7, var8, var9});
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return new ParameterizedMessage(var1, new Object[]{var2, var3, var4, var5, var6, var7, var8, var9, var10});
   }

   public Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      return new ParameterizedMessage(var1, new Object[]{var2, var3, var4, var5, var6, var7, var8, var9, var10, var11});
   }
}
