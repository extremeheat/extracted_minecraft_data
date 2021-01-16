package org.apache.logging.log4j.message;

public interface MessageFactory2 extends MessageFactory {
   Message newMessage(CharSequence var1);

   Message newMessage(String var1, Object var2);

   Message newMessage(String var1, Object var2, Object var3);

   Message newMessage(String var1, Object var2, Object var3, Object var4);

   Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5);

   Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6);

   Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7);

   Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   Message newMessage(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);
}
