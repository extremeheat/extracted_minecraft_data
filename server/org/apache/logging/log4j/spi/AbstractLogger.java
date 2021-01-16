package org.apache.logging.log4j.spi;

import java.io.Serializable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.message.DefaultFlowMessageFactory;
import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.FlowMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.MessageFactory2;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import org.apache.logging.log4j.message.ReusableMessageFactory;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.LambdaUtil;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;
import org.apache.logging.log4j.util.Supplier;

public abstract class AbstractLogger implements ExtendedLogger, Serializable {
   public static final Marker FLOW_MARKER = MarkerManager.getMarker("FLOW");
   public static final Marker ENTRY_MARKER;
   public static final Marker EXIT_MARKER;
   public static final Marker EXCEPTION_MARKER;
   public static final Marker THROWING_MARKER;
   public static final Marker CATCHING_MARKER;
   public static final Class<? extends MessageFactory> DEFAULT_MESSAGE_FACTORY_CLASS;
   public static final Class<? extends FlowMessageFactory> DEFAULT_FLOW_MESSAGE_FACTORY_CLASS;
   private static final long serialVersionUID = 2L;
   private static final String FQCN;
   private static final String THROWING = "Throwing";
   private static final String CATCHING = "Catching";
   protected final String name;
   private final MessageFactory2 messageFactory;
   private final FlowMessageFactory flowMessageFactory;

   public AbstractLogger() {
      super();
      this.name = this.getClass().getName();
      this.messageFactory = createDefaultMessageFactory();
      this.flowMessageFactory = createDefaultFlowMessageFactory();
   }

   public AbstractLogger(String var1) {
      this(var1, createDefaultMessageFactory());
   }

   public AbstractLogger(String var1, MessageFactory var2) {
      super();
      this.name = var1;
      this.messageFactory = var2 == null ? createDefaultMessageFactory() : narrow(var2);
      this.flowMessageFactory = createDefaultFlowMessageFactory();
   }

   public static void checkMessageFactory(ExtendedLogger var0, MessageFactory var1) {
      String var2 = var0.getName();
      MessageFactory var3 = var0.getMessageFactory();
      if (var1 != null && !var3.equals(var1)) {
         StatusLogger.getLogger().warn("The Logger {} was created with the message factory {} and is now requested with the message factory {}, which may create log events with unexpected formatting.", var2, var3, var1);
      } else if (var1 == null && !var3.getClass().equals(DEFAULT_MESSAGE_FACTORY_CLASS)) {
         StatusLogger.getLogger().warn("The Logger {} was created with the message factory {} and is now requested with a null message factory (defaults to {}), which may create log events with unexpected formatting.", var2, var3, DEFAULT_MESSAGE_FACTORY_CLASS.getName());
      }

   }

   public void catching(Level var1, Throwable var2) {
      this.catching(FQCN, var1, var2);
   }

   protected void catching(String var1, Level var2, Throwable var3) {
      if (this.isEnabled(var2, CATCHING_MARKER, (Object)null, (Throwable)null)) {
         this.logMessageSafely(var1, var2, CATCHING_MARKER, this.catchingMsg(var3), var3);
      }

   }

   public void catching(Throwable var1) {
      if (this.isEnabled(Level.ERROR, CATCHING_MARKER, (Object)null, (Throwable)null)) {
         this.logMessageSafely(FQCN, Level.ERROR, CATCHING_MARKER, this.catchingMsg(var1), var1);
      }

   }

   protected Message catchingMsg(Throwable var1) {
      return this.messageFactory.newMessage("Catching");
   }

   private static Class<? extends MessageFactory> createClassForProperty(String var0, Class<ReusableMessageFactory> var1, Class<ParameterizedMessageFactory> var2) {
      try {
         String var3 = Constants.ENABLE_THREADLOCALS ? var1.getName() : var2.getName();
         String var4 = PropertiesUtil.getProperties().getStringProperty(var0, var3);
         return LoaderUtil.loadClass(var4).asSubclass(MessageFactory.class);
      } catch (Throwable var5) {
         return var2;
      }
   }

   private static Class<? extends FlowMessageFactory> createFlowClassForProperty(String var0, Class<DefaultFlowMessageFactory> var1) {
      try {
         String var2 = PropertiesUtil.getProperties().getStringProperty(var0, var1.getName());
         return LoaderUtil.loadClass(var2).asSubclass(FlowMessageFactory.class);
      } catch (Throwable var3) {
         return var1;
      }
   }

   private static MessageFactory2 createDefaultMessageFactory() {
      try {
         MessageFactory var0 = (MessageFactory)DEFAULT_MESSAGE_FACTORY_CLASS.newInstance();
         return narrow(var0);
      } catch (IllegalAccessException | InstantiationException var1) {
         throw new IllegalStateException(var1);
      }
   }

   private static MessageFactory2 narrow(MessageFactory var0) {
      return (MessageFactory2)(var0 instanceof MessageFactory2 ? (MessageFactory2)var0 : new MessageFactory2Adapter(var0));
   }

   private static FlowMessageFactory createDefaultFlowMessageFactory() {
      try {
         return (FlowMessageFactory)DEFAULT_FLOW_MESSAGE_FACTORY_CLASS.newInstance();
      } catch (IllegalAccessException | InstantiationException var1) {
         throw new IllegalStateException(var1);
      }
   }

   public void debug(Marker var1, CharSequence var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, (CharSequence)var2, (Throwable)null);
   }

   public void debug(Marker var1, CharSequence var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   public void debug(Marker var1, Message var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var2 != null ? var2.getThrowable() : null);
   }

   public void debug(Marker var1, Message var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   public void debug(Marker var1, Object var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, (Object)var2, (Throwable)null);
   }

   public void debug(Marker var1, Object var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   public void debug(Marker var1, String var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, (Throwable)null);
   }

   public void debug(Marker var1, String var2, Object... var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   public void debug(Marker var1, String var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   public void debug(Message var1) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, (Message)var1, (Throwable)(var1 != null ? var1.getThrowable() : null));
   }

   public void debug(Message var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, (Message)var1, (Throwable)var2);
   }

   public void debug(CharSequence var1) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, (CharSequence)var1, (Throwable)null);
   }

   public void debug(CharSequence var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, (CharSequence)var1, (Throwable)var2);
   }

   public void debug(Object var1) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, (Object)var1, (Throwable)null);
   }

   public void debug(Object var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, (Object)var1, (Throwable)var2);
   }

   public void debug(String var1) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, (String)var1, (Throwable)((Throwable)null));
   }

   public void debug(String var1, Object... var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, (String)var1, (Object[])var2);
   }

   public void debug(String var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, (String)var1, (Throwable)var2);
   }

   public void debug(Supplier<?> var1) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, (Supplier)var1, (Throwable)((Throwable)null));
   }

   public void debug(Supplier<?> var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, (Supplier)var1, (Throwable)var2);
   }

   public void debug(Marker var1, Supplier<?> var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, (Throwable)null);
   }

   public void debug(Marker var1, String var2, Supplier<?>... var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   public void debug(Marker var1, Supplier<?> var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   public void debug(String var1, Supplier<?>... var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, (String)var1, (Supplier[])var2);
   }

   public void debug(Marker var1, MessageSupplier var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, (Throwable)null);
   }

   public void debug(Marker var1, MessageSupplier var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   public void debug(MessageSupplier var1) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, (MessageSupplier)var1, (Throwable)((Throwable)null));
   }

   public void debug(MessageSupplier var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, (MessageSupplier)var1, (Throwable)var2);
   }

   public void debug(Marker var1, String var2, Object var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3);
   }

   public void debug(Marker var1, String var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4);
   }

   public void debug(Marker var1, String var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5);
   }

   public void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5, var6);
   }

   public void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5, var6, var7);
   }

   public void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      this.logIfEnabled(FQCN, Level.DEBUG, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   public void debug(String var1, Object var2) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, (String)var1, (Object)var2);
   }

   public void debug(String var1, Object var2, Object var3) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, var1, var2, var3);
   }

   public void debug(String var1, Object var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, var1, var2, var3, var4);
   }

   public void debug(String var1, Object var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, var1, var2, var3, var4, var5);
   }

   public void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, var1, var2, var3, var4, var5, var6);
   }

   public void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, var1, var2, var3, var4, var5, var6, var7);
   }

   public void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      this.logIfEnabled(FQCN, Level.DEBUG, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   protected EntryMessage enter(String var1, String var2, Supplier<?>... var3) {
      EntryMessage var4 = null;
      if (this.isEnabled(Level.TRACE, ENTRY_MARKER, (Object)null, (Throwable)null)) {
         this.logMessageSafely(var1, Level.TRACE, ENTRY_MARKER, var4 = this.entryMsg(var2, var3), (Throwable)null);
      }

      return var4;
   }

   /** @deprecated */
   @Deprecated
   protected EntryMessage enter(String var1, String var2, MessageSupplier... var3) {
      EntryMessage var4 = null;
      if (this.isEnabled(Level.TRACE, ENTRY_MARKER, (Object)null, (Throwable)null)) {
         this.logMessageSafely(var1, Level.TRACE, ENTRY_MARKER, var4 = this.entryMsg(var2, var3), (Throwable)null);
      }

      return var4;
   }

   protected EntryMessage enter(String var1, String var2, Object... var3) {
      EntryMessage var4 = null;
      if (this.isEnabled(Level.TRACE, ENTRY_MARKER, (Object)null, (Throwable)null)) {
         this.logMessageSafely(var1, Level.TRACE, ENTRY_MARKER, var4 = this.entryMsg(var2, var3), (Throwable)null);
      }

      return var4;
   }

   /** @deprecated */
   @Deprecated
   protected EntryMessage enter(String var1, MessageSupplier var2) {
      EntryMessage var3 = null;
      if (this.isEnabled(Level.TRACE, ENTRY_MARKER, (Object)null, (Throwable)null)) {
         this.logMessageSafely(var1, Level.TRACE, ENTRY_MARKER, var3 = this.flowMessageFactory.newEntryMessage(var2.get()), (Throwable)null);
      }

      return var3;
   }

   protected EntryMessage enter(String var1, Message var2) {
      EntryMessage var3 = null;
      if (this.isEnabled(Level.TRACE, ENTRY_MARKER, (Object)null, (Throwable)null)) {
         this.logMessageSafely(var1, Level.TRACE, ENTRY_MARKER, var3 = this.flowMessageFactory.newEntryMessage(var2), (Throwable)null);
      }

      return var3;
   }

   public void entry() {
      this.entry(FQCN, (Object[])null);
   }

   public void entry(Object... var1) {
      this.entry(FQCN, var1);
   }

   protected void entry(String var1, Object... var2) {
      if (this.isEnabled(Level.TRACE, ENTRY_MARKER, (Object)null, (Throwable)null)) {
         if (var2 == null) {
            this.logMessageSafely(var1, Level.TRACE, ENTRY_MARKER, this.entryMsg((String)null, (Supplier[])((Supplier[])null)), (Throwable)null);
         } else {
            this.logMessageSafely(var1, Level.TRACE, ENTRY_MARKER, this.entryMsg((String)null, (Object[])var2), (Throwable)null);
         }
      }

   }

   protected EntryMessage entryMsg(String var1, Object... var2) {
      int var3 = var2 == null ? 0 : var2.length;
      if (var3 == 0) {
         return Strings.isEmpty(var1) ? this.flowMessageFactory.newEntryMessage((Message)null) : this.flowMessageFactory.newEntryMessage(new SimpleMessage(var1));
      } else if (var1 != null) {
         return this.flowMessageFactory.newEntryMessage(new ParameterizedMessage(var1, var2));
      } else {
         StringBuilder var4 = new StringBuilder();
         var4.append("params(");

         for(int var5 = 0; var5 < var3; ++var5) {
            if (var5 > 0) {
               var4.append(", ");
            }

            Object var6 = var2[var5];
            var4.append(var6 instanceof Message ? ((Message)var6).getFormattedMessage() : String.valueOf(var6));
         }

         var4.append(')');
         return this.flowMessageFactory.newEntryMessage(new SimpleMessage(var4));
      }
   }

   protected EntryMessage entryMsg(String var1, MessageSupplier... var2) {
      int var3 = var2 == null ? 0 : var2.length;
      Object[] var4 = new Object[var3];

      for(int var5 = 0; var5 < var3; ++var5) {
         var4[var5] = var2[var5].get();
         var4[var5] = var4[var5] != null ? ((Message)var4[var5]).getFormattedMessage() : null;
      }

      return this.entryMsg(var1, var4);
   }

   protected EntryMessage entryMsg(String var1, Supplier<?>... var2) {
      int var3 = var2 == null ? 0 : var2.length;
      Object[] var4 = new Object[var3];

      for(int var5 = 0; var5 < var3; ++var5) {
         var4[var5] = var2[var5].get();
         if (var4[var5] instanceof Message) {
            var4[var5] = ((Message)var4[var5]).getFormattedMessage();
         }
      }

      return this.entryMsg(var1, var4);
   }

   public void error(Marker var1, Message var2) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var2 != null ? var2.getThrowable() : null);
   }

   public void error(Marker var1, Message var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   public void error(Marker var1, CharSequence var2) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, (CharSequence)var2, (Throwable)null);
   }

   public void error(Marker var1, CharSequence var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   public void error(Marker var1, Object var2) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, (Object)var2, (Throwable)null);
   }

   public void error(Marker var1, Object var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   public void error(Marker var1, String var2) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, (Throwable)null);
   }

   public void error(Marker var1, String var2, Object... var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   public void error(Marker var1, String var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   public void error(Message var1) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, (Message)var1, (Throwable)(var1 != null ? var1.getThrowable() : null));
   }

   public void error(Message var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, (Message)var1, (Throwable)var2);
   }

   public void error(CharSequence var1) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, (CharSequence)var1, (Throwable)null);
   }

   public void error(CharSequence var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, (CharSequence)var1, (Throwable)var2);
   }

   public void error(Object var1) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, (Object)var1, (Throwable)null);
   }

   public void error(Object var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, (Object)var1, (Throwable)var2);
   }

   public void error(String var1) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, (String)var1, (Throwable)((Throwable)null));
   }

   public void error(String var1, Object... var2) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, (String)var1, (Object[])var2);
   }

   public void error(String var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, (String)var1, (Throwable)var2);
   }

   public void error(Supplier<?> var1) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, (Supplier)var1, (Throwable)((Throwable)null));
   }

   public void error(Supplier<?> var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, (Supplier)var1, (Throwable)var2);
   }

   public void error(Marker var1, Supplier<?> var2) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, (Throwable)null);
   }

   public void error(Marker var1, String var2, Supplier<?>... var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   public void error(Marker var1, Supplier<?> var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   public void error(String var1, Supplier<?>... var2) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, (String)var1, (Supplier[])var2);
   }

   public void error(Marker var1, MessageSupplier var2) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, (Throwable)null);
   }

   public void error(Marker var1, MessageSupplier var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   public void error(MessageSupplier var1) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, (MessageSupplier)var1, (Throwable)((Throwable)null));
   }

   public void error(MessageSupplier var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, (MessageSupplier)var1, (Throwable)var2);
   }

   public void error(Marker var1, String var2, Object var3) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3);
   }

   public void error(Marker var1, String var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4);
   }

   public void error(Marker var1, String var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5);
   }

   public void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5, var6);
   }

   public void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5, var6, var7);
   }

   public void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      this.logIfEnabled(FQCN, Level.ERROR, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   public void error(String var1, Object var2) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, (String)var1, (Object)var2);
   }

   public void error(String var1, Object var2, Object var3) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, var1, var2, var3);
   }

   public void error(String var1, Object var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, var1, var2, var3, var4);
   }

   public void error(String var1, Object var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, var1, var2, var3, var4, var5);
   }

   public void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, var1, var2, var3, var4, var5, var6);
   }

   public void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, var1, var2, var3, var4, var5, var6, var7);
   }

   public void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      this.logIfEnabled(FQCN, Level.ERROR, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public void exit() {
      this.exit(FQCN, (Object)null);
   }

   public <R> R exit(R var1) {
      return this.exit(FQCN, var1);
   }

   protected <R> R exit(String var1, R var2) {
      this.logIfEnabled(var1, Level.TRACE, EXIT_MARKER, (Message)this.exitMsg((String)null, var2), (Throwable)null);
      return var2;
   }

   protected <R> R exit(String var1, String var2, R var3) {
      this.logIfEnabled(var1, Level.TRACE, EXIT_MARKER, (Message)this.exitMsg(var2, var3), (Throwable)null);
      return var3;
   }

   protected Message exitMsg(String var1, Object var2) {
      if (var2 == null) {
         return var1 == null ? this.messageFactory.newMessage("Exit") : this.messageFactory.newMessage("Exit: " + var1);
      } else {
         return var1 == null ? this.messageFactory.newMessage("Exit with(" + var2 + ')') : this.messageFactory.newMessage("Exit: " + var1, var2);
      }
   }

   public void fatal(Marker var1, Message var2) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var2 != null ? var2.getThrowable() : null);
   }

   public void fatal(Marker var1, Message var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   public void fatal(Marker var1, CharSequence var2) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, (CharSequence)var2, (Throwable)null);
   }

   public void fatal(Marker var1, CharSequence var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   public void fatal(Marker var1, Object var2) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, (Object)var2, (Throwable)null);
   }

   public void fatal(Marker var1, Object var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   public void fatal(Marker var1, String var2) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, (Throwable)null);
   }

   public void fatal(Marker var1, String var2, Object... var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   public void fatal(Marker var1, String var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   public void fatal(Message var1) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, (Message)var1, (Throwable)(var1 != null ? var1.getThrowable() : null));
   }

   public void fatal(Message var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, (Message)var1, (Throwable)var2);
   }

   public void fatal(CharSequence var1) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, (CharSequence)var1, (Throwable)null);
   }

   public void fatal(CharSequence var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, (CharSequence)var1, (Throwable)var2);
   }

   public void fatal(Object var1) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, (Object)var1, (Throwable)null);
   }

   public void fatal(Object var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, (Object)var1, (Throwable)var2);
   }

   public void fatal(String var1) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, (String)var1, (Throwable)((Throwable)null));
   }

   public void fatal(String var1, Object... var2) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, (String)var1, (Object[])var2);
   }

   public void fatal(String var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, (String)var1, (Throwable)var2);
   }

   public void fatal(Supplier<?> var1) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, (Supplier)var1, (Throwable)((Throwable)null));
   }

   public void fatal(Supplier<?> var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, (Supplier)var1, (Throwable)var2);
   }

   public void fatal(Marker var1, Supplier<?> var2) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, (Throwable)null);
   }

   public void fatal(Marker var1, String var2, Supplier<?>... var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   public void fatal(Marker var1, Supplier<?> var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   public void fatal(String var1, Supplier<?>... var2) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, (String)var1, (Supplier[])var2);
   }

   public void fatal(Marker var1, MessageSupplier var2) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, (Throwable)null);
   }

   public void fatal(Marker var1, MessageSupplier var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   public void fatal(MessageSupplier var1) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, (MessageSupplier)var1, (Throwable)((Throwable)null));
   }

   public void fatal(MessageSupplier var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, (MessageSupplier)var1, (Throwable)var2);
   }

   public void fatal(Marker var1, String var2, Object var3) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3);
   }

   public void fatal(Marker var1, String var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4);
   }

   public void fatal(Marker var1, String var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5);
   }

   public void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5, var6);
   }

   public void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5, var6, var7);
   }

   public void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      this.logIfEnabled(FQCN, Level.FATAL, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   public void fatal(String var1, Object var2) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, (String)var1, (Object)var2);
   }

   public void fatal(String var1, Object var2, Object var3) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, var1, var2, var3);
   }

   public void fatal(String var1, Object var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, var1, var2, var3, var4);
   }

   public void fatal(String var1, Object var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, var1, var2, var3, var4, var5);
   }

   public void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, var1, var2, var3, var4, var5, var6);
   }

   public void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, var1, var2, var3, var4, var5, var6, var7);
   }

   public void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      this.logIfEnabled(FQCN, Level.FATAL, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public <MF extends MessageFactory> MF getMessageFactory() {
      return this.messageFactory;
   }

   public String getName() {
      return this.name;
   }

   public void info(Marker var1, Message var2) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var2 != null ? var2.getThrowable() : null);
   }

   public void info(Marker var1, Message var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   public void info(Marker var1, CharSequence var2) {
      this.logIfEnabled(FQCN, Level.INFO, var1, (CharSequence)var2, (Throwable)null);
   }

   public void info(Marker var1, CharSequence var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   public void info(Marker var1, Object var2) {
      this.logIfEnabled(FQCN, Level.INFO, var1, (Object)var2, (Throwable)null);
   }

   public void info(Marker var1, Object var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   public void info(Marker var1, String var2) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, (Throwable)null);
   }

   public void info(Marker var1, String var2, Object... var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   public void info(Marker var1, String var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   public void info(Message var1) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, (Message)var1, (Throwable)(var1 != null ? var1.getThrowable() : null));
   }

   public void info(Message var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, (Message)var1, (Throwable)var2);
   }

   public void info(CharSequence var1) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, (CharSequence)var1, (Throwable)null);
   }

   public void info(CharSequence var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, (CharSequence)var1, (Throwable)var2);
   }

   public void info(Object var1) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, (Object)var1, (Throwable)null);
   }

   public void info(Object var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, (Object)var1, (Throwable)var2);
   }

   public void info(String var1) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, (String)var1, (Throwable)((Throwable)null));
   }

   public void info(String var1, Object... var2) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, (String)var1, (Object[])var2);
   }

   public void info(String var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, (String)var1, (Throwable)var2);
   }

   public void info(Supplier<?> var1) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, (Supplier)var1, (Throwable)((Throwable)null));
   }

   public void info(Supplier<?> var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, (Supplier)var1, (Throwable)var2);
   }

   public void info(Marker var1, Supplier<?> var2) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, (Throwable)null);
   }

   public void info(Marker var1, String var2, Supplier<?>... var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   public void info(Marker var1, Supplier<?> var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   public void info(String var1, Supplier<?>... var2) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, (String)var1, (Supplier[])var2);
   }

   public void info(Marker var1, MessageSupplier var2) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, (Throwable)null);
   }

   public void info(Marker var1, MessageSupplier var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   public void info(MessageSupplier var1) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, (MessageSupplier)var1, (Throwable)((Throwable)null));
   }

   public void info(MessageSupplier var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, (MessageSupplier)var1, (Throwable)var2);
   }

   public void info(Marker var1, String var2, Object var3) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3);
   }

   public void info(Marker var1, String var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4);
   }

   public void info(Marker var1, String var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5);
   }

   public void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5, var6);
   }

   public void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5, var6, var7);
   }

   public void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      this.logIfEnabled(FQCN, Level.INFO, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   public void info(String var1, Object var2) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, (String)var1, (Object)var2);
   }

   public void info(String var1, Object var2, Object var3) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, var1, var2, var3);
   }

   public void info(String var1, Object var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, var1, var2, var3, var4);
   }

   public void info(String var1, Object var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, var1, var2, var3, var4, var5);
   }

   public void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, var1, var2, var3, var4, var5, var6);
   }

   public void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, var1, var2, var3, var4, var5, var6, var7);
   }

   public void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      this.logIfEnabled(FQCN, Level.INFO, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public boolean isDebugEnabled() {
      return this.isEnabled(Level.DEBUG, (Marker)null, (String)null);
   }

   public boolean isDebugEnabled(Marker var1) {
      return this.isEnabled(Level.DEBUG, var1, (Object)null, (Throwable)null);
   }

   public boolean isEnabled(Level var1) {
      return this.isEnabled(var1, (Marker)null, (Object)null, (Throwable)null);
   }

   public boolean isEnabled(Level var1, Marker var2) {
      return this.isEnabled(var1, var2, (Object)null, (Throwable)null);
   }

   public boolean isErrorEnabled() {
      return this.isEnabled(Level.ERROR, (Marker)null, (Object)null, (Throwable)null);
   }

   public boolean isErrorEnabled(Marker var1) {
      return this.isEnabled(Level.ERROR, var1, (Object)null, (Throwable)null);
   }

   public boolean isFatalEnabled() {
      return this.isEnabled(Level.FATAL, (Marker)null, (Object)null, (Throwable)null);
   }

   public boolean isFatalEnabled(Marker var1) {
      return this.isEnabled(Level.FATAL, var1, (Object)null, (Throwable)null);
   }

   public boolean isInfoEnabled() {
      return this.isEnabled(Level.INFO, (Marker)null, (Object)null, (Throwable)null);
   }

   public boolean isInfoEnabled(Marker var1) {
      return this.isEnabled(Level.INFO, var1, (Object)null, (Throwable)null);
   }

   public boolean isTraceEnabled() {
      return this.isEnabled(Level.TRACE, (Marker)null, (Object)null, (Throwable)null);
   }

   public boolean isTraceEnabled(Marker var1) {
      return this.isEnabled(Level.TRACE, var1, (Object)null, (Throwable)null);
   }

   public boolean isWarnEnabled() {
      return this.isEnabled(Level.WARN, (Marker)null, (Object)null, (Throwable)null);
   }

   public boolean isWarnEnabled(Marker var1) {
      return this.isEnabled(Level.WARN, var1, (Object)null, (Throwable)null);
   }

   public void log(Level var1, Marker var2, Message var3) {
      this.logIfEnabled(FQCN, var1, var2, var3, var3 != null ? var3.getThrowable() : null);
   }

   public void log(Level var1, Marker var2, Message var3, Throwable var4) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4);
   }

   public void log(Level var1, Marker var2, CharSequence var3) {
      this.logIfEnabled(FQCN, var1, var2, var3, (Throwable)null);
   }

   public void log(Level var1, Marker var2, CharSequence var3, Throwable var4) {
      if (this.isEnabled(var1, var2, var3, var4)) {
         this.logMessage(FQCN, var1, var2, var3, var4);
      }

   }

   public void log(Level var1, Marker var2, Object var3) {
      this.logIfEnabled(FQCN, var1, var2, var3, (Throwable)null);
   }

   public void log(Level var1, Marker var2, Object var3, Throwable var4) {
      if (this.isEnabled(var1, var2, var3, var4)) {
         this.logMessage(FQCN, var1, var2, var3, var4);
      }

   }

   public void log(Level var1, Marker var2, String var3) {
      this.logIfEnabled(FQCN, var1, var2, var3, (Throwable)null);
   }

   public void log(Level var1, Marker var2, String var3, Object... var4) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4);
   }

   public void log(Level var1, Marker var2, String var3, Throwable var4) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4);
   }

   public void log(Level var1, Message var2) {
      this.logIfEnabled(FQCN, var1, (Marker)null, (Message)var2, (Throwable)(var2 != null ? var2.getThrowable() : null));
   }

   public void log(Level var1, Message var2, Throwable var3) {
      this.logIfEnabled(FQCN, var1, (Marker)null, (Message)var2, (Throwable)var3);
   }

   public void log(Level var1, CharSequence var2) {
      this.logIfEnabled(FQCN, var1, (Marker)null, (CharSequence)var2, (Throwable)null);
   }

   public void log(Level var1, CharSequence var2, Throwable var3) {
      this.logIfEnabled(FQCN, var1, (Marker)null, (CharSequence)var2, (Throwable)var3);
   }

   public void log(Level var1, Object var2) {
      this.logIfEnabled(FQCN, var1, (Marker)null, (Object)var2, (Throwable)null);
   }

   public void log(Level var1, Object var2, Throwable var3) {
      this.logIfEnabled(FQCN, var1, (Marker)null, (Object)var2, (Throwable)var3);
   }

   public void log(Level var1, String var2) {
      this.logIfEnabled(FQCN, var1, (Marker)null, (String)var2, (Throwable)((Throwable)null));
   }

   public void log(Level var1, String var2, Object... var3) {
      this.logIfEnabled(FQCN, var1, (Marker)null, (String)var2, (Object[])var3);
   }

   public void log(Level var1, String var2, Throwable var3) {
      this.logIfEnabled(FQCN, var1, (Marker)null, (String)var2, (Throwable)var3);
   }

   public void log(Level var1, Supplier<?> var2) {
      this.logIfEnabled(FQCN, var1, (Marker)null, (Supplier)var2, (Throwable)((Throwable)null));
   }

   public void log(Level var1, Supplier<?> var2, Throwable var3) {
      this.logIfEnabled(FQCN, var1, (Marker)null, (Supplier)var2, (Throwable)var3);
   }

   public void log(Level var1, Marker var2, Supplier<?> var3) {
      this.logIfEnabled(FQCN, var1, var2, var3, (Throwable)null);
   }

   public void log(Level var1, Marker var2, String var3, Supplier<?>... var4) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4);
   }

   public void log(Level var1, Marker var2, Supplier<?> var3, Throwable var4) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4);
   }

   public void log(Level var1, String var2, Supplier<?>... var3) {
      this.logIfEnabled(FQCN, var1, (Marker)null, (String)var2, (Supplier[])var3);
   }

   public void log(Level var1, Marker var2, MessageSupplier var3) {
      this.logIfEnabled(FQCN, var1, var2, var3, (Throwable)null);
   }

   public void log(Level var1, Marker var2, MessageSupplier var3, Throwable var4) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4);
   }

   public void log(Level var1, MessageSupplier var2) {
      this.logIfEnabled(FQCN, var1, (Marker)null, (MessageSupplier)var2, (Throwable)((Throwable)null));
   }

   public void log(Level var1, MessageSupplier var2, Throwable var3) {
      this.logIfEnabled(FQCN, var1, (Marker)null, (MessageSupplier)var2, (Throwable)var3);
   }

   public void log(Level var1, Marker var2, String var3, Object var4) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4);
   }

   public void log(Level var1, Marker var2, String var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5);
   }

   public void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6);
   }

   public void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6, var7);
   }

   public void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   public void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13) {
      this.logIfEnabled(FQCN, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
   }

   public void log(Level var1, String var2, Object var3) {
      this.logIfEnabled(FQCN, var1, (Marker)null, (String)var2, (Object)var3);
   }

   public void log(Level var1, String var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, var1, (Marker)null, var2, var3, var4);
   }

   public void log(Level var1, String var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, var1, (Marker)null, var2, var3, var4, var5);
   }

   public void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, var1, (Marker)null, var2, var3, var4, var5, var6);
   }

   public void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, var1, (Marker)null, var2, var3, var4, var5, var6, var7);
   }

   public void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, var1, (Marker)null, var2, var3, var4, var5, var6, var7, var8);
   }

   public void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, var1, (Marker)null, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, var1, (Marker)null, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      this.logIfEnabled(FQCN, var1, (Marker)null, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      this.logIfEnabled(FQCN, var1, (Marker)null, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   public void logIfEnabled(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessageSafely(var1, var2, var3, var4, var5);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, MessageSupplier var4, Throwable var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, Object var4, Throwable var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, CharSequence var4, Throwable var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, Supplier<?> var4, Throwable var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, String var4) {
      if (this.isEnabled(var2, var3, var4)) {
         this.logMessage(var1, var2, var3, var4);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Supplier<?>... var5) {
      if (this.isEnabled(var2, var3, var4)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object... var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      if (this.isEnabled(var2, var3, var4, var5, var6)) {
         this.logMessage(var1, var2, var3, var4, var5, var6);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7, var8)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7, var8);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7, var8, var9)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7, var8, var9, var10)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13, Object var14) {
      if (this.isEnabled(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14)) {
         this.logMessage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
      }

   }

   public void logIfEnabled(String var1, Level var2, Marker var3, String var4, Throwable var5) {
      if (this.isEnabled(var2, var3, var4, var5)) {
         this.logMessage(var1, var2, var3, var4, var5);
      }

   }

   protected void logMessage(String var1, Level var2, Marker var3, CharSequence var4, Throwable var5) {
      this.logMessageSafely(var1, var2, var3, this.messageFactory.newMessage(var4), var5);
   }

   protected void logMessage(String var1, Level var2, Marker var3, Object var4, Throwable var5) {
      this.logMessageSafely(var1, var2, var3, this.messageFactory.newMessage(var4), var5);
   }

   protected void logMessage(String var1, Level var2, Marker var3, MessageSupplier var4, Throwable var5) {
      Message var6 = LambdaUtil.get(var4);
      this.logMessageSafely(var1, var2, var3, var6, var5 == null && var6 != null ? var6.getThrowable() : var5);
   }

   protected void logMessage(String var1, Level var2, Marker var3, Supplier<?> var4, Throwable var5) {
      Message var6 = LambdaUtil.getMessage(var4, this.messageFactory);
      this.logMessageSafely(var1, var2, var3, var6, var5 == null && var6 != null ? var6.getThrowable() : var5);
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Throwable var5) {
      this.logMessageSafely(var1, var2, var3, this.messageFactory.newMessage(var4), var5);
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4) {
      Message var5 = this.messageFactory.newMessage(var4);
      this.logMessageSafely(var1, var2, var3, var5, var5.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object... var5) {
      Message var6 = this.messageFactory.newMessage(var4, var5);
      this.logMessageSafely(var1, var2, var3, var6, var6.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5) {
      Message var6 = this.messageFactory.newMessage(var4, var5);
      this.logMessageSafely(var1, var2, var3, var6, var6.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      Message var7 = this.messageFactory.newMessage(var4, var5, var6);
      this.logMessageSafely(var1, var2, var3, var7, var7.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      Message var8 = this.messageFactory.newMessage(var4, var5, var6, var7);
      this.logMessageSafely(var1, var2, var3, var8, var8.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      Message var9 = this.messageFactory.newMessage(var4, var5, var6, var7, var8);
      this.logMessageSafely(var1, var2, var3, var9, var9.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      Message var10 = this.messageFactory.newMessage(var4, var5, var6, var7, var8, var9);
      this.logMessageSafely(var1, var2, var3, var10, var10.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      Message var11 = this.messageFactory.newMessage(var4, var5, var6, var7, var8, var9, var10);
      this.logMessageSafely(var1, var2, var3, var11, var11.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      Message var12 = this.messageFactory.newMessage(var4, var5, var6, var7, var8, var9, var10, var11);
      this.logMessageSafely(var1, var2, var3, var12, var12.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      Message var13 = this.messageFactory.newMessage(var4, var5, var6, var7, var8, var9, var10, var11, var12);
      this.logMessageSafely(var1, var2, var3, var13, var13.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13) {
      Message var14 = this.messageFactory.newMessage(var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      this.logMessageSafely(var1, var2, var3, var14, var14.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13, Object var14) {
      Message var15 = this.messageFactory.newMessage(var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
      this.logMessageSafely(var1, var2, var3, var15, var15.getThrowable());
   }

   protected void logMessage(String var1, Level var2, Marker var3, String var4, Supplier<?>... var5) {
      Message var6 = this.messageFactory.newMessage(var4, LambdaUtil.getAll(var5));
      this.logMessageSafely(var1, var2, var3, var6, var6.getThrowable());
   }

   public void printf(Level var1, Marker var2, String var3, Object... var4) {
      if (this.isEnabled(var1, var2, var3, var4)) {
         StringFormattedMessage var5 = new StringFormattedMessage(var3, var4);
         this.logMessageSafely(FQCN, var1, var2, var5, var5.getThrowable());
      }

   }

   public void printf(Level var1, String var2, Object... var3) {
      if (this.isEnabled(var1, (Marker)null, var2, var3)) {
         StringFormattedMessage var4 = new StringFormattedMessage(var2, var3);
         this.logMessageSafely(FQCN, var1, (Marker)null, var4, var4.getThrowable());
      }

   }

   private void logMessageSafely(String var1, Level var2, Marker var3, Message var4, Throwable var5) {
      try {
         this.logMessage(var1, var2, var3, (Message)var4, (Throwable)var5);
      } finally {
         ReusableMessageFactory.release(var4);
      }

   }

   public <T extends Throwable> T throwing(T var1) {
      return this.throwing(FQCN, Level.ERROR, var1);
   }

   public <T extends Throwable> T throwing(Level var1, T var2) {
      return this.throwing(FQCN, var1, var2);
   }

   protected <T extends Throwable> T throwing(String var1, Level var2, T var3) {
      if (this.isEnabled(var2, THROWING_MARKER, (Object)null, (Throwable)null)) {
         this.logMessageSafely(var1, var2, THROWING_MARKER, this.throwingMsg(var3), var3);
      }

      return var3;
   }

   protected Message throwingMsg(Throwable var1) {
      return this.messageFactory.newMessage("Throwing");
   }

   public void trace(Marker var1, Message var2) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var2 != null ? var2.getThrowable() : null);
   }

   public void trace(Marker var1, Message var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   public void trace(Marker var1, CharSequence var2) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, (CharSequence)var2, (Throwable)null);
   }

   public void trace(Marker var1, CharSequence var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   public void trace(Marker var1, Object var2) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, (Object)var2, (Throwable)null);
   }

   public void trace(Marker var1, Object var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   public void trace(Marker var1, String var2) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, (Throwable)null);
   }

   public void trace(Marker var1, String var2, Object... var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   public void trace(Marker var1, String var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   public void trace(Message var1) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, (Message)var1, (Throwable)(var1 != null ? var1.getThrowable() : null));
   }

   public void trace(Message var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, (Message)var1, (Throwable)var2);
   }

   public void trace(CharSequence var1) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, (CharSequence)var1, (Throwable)null);
   }

   public void trace(CharSequence var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, (CharSequence)var1, (Throwable)var2);
   }

   public void trace(Object var1) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, (Object)var1, (Throwable)null);
   }

   public void trace(Object var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, (Object)var1, (Throwable)var2);
   }

   public void trace(String var1) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, (String)var1, (Throwable)((Throwable)null));
   }

   public void trace(String var1, Object... var2) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, (String)var1, (Object[])var2);
   }

   public void trace(String var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, (String)var1, (Throwable)var2);
   }

   public void trace(Supplier<?> var1) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, (Supplier)var1, (Throwable)((Throwable)null));
   }

   public void trace(Supplier<?> var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, (Supplier)var1, (Throwable)var2);
   }

   public void trace(Marker var1, Supplier<?> var2) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, (Throwable)null);
   }

   public void trace(Marker var1, String var2, Supplier<?>... var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   public void trace(Marker var1, Supplier<?> var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   public void trace(String var1, Supplier<?>... var2) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, (String)var1, (Supplier[])var2);
   }

   public void trace(Marker var1, MessageSupplier var2) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, (Throwable)null);
   }

   public void trace(Marker var1, MessageSupplier var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   public void trace(MessageSupplier var1) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, (MessageSupplier)var1, (Throwable)((Throwable)null));
   }

   public void trace(MessageSupplier var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, (MessageSupplier)var1, (Throwable)var2);
   }

   public void trace(Marker var1, String var2, Object var3) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3);
   }

   public void trace(Marker var1, String var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4);
   }

   public void trace(Marker var1, String var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5);
   }

   public void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5, var6);
   }

   public void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5, var6, var7);
   }

   public void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      this.logIfEnabled(FQCN, Level.TRACE, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   public void trace(String var1, Object var2) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, (String)var1, (Object)var2);
   }

   public void trace(String var1, Object var2, Object var3) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, var1, var2, var3);
   }

   public void trace(String var1, Object var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, var1, var2, var3, var4);
   }

   public void trace(String var1, Object var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, var1, var2, var3, var4, var5);
   }

   public void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, var1, var2, var3, var4, var5, var6);
   }

   public void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, var1, var2, var3, var4, var5, var6, var7);
   }

   public void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      this.logIfEnabled(FQCN, Level.TRACE, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public EntryMessage traceEntry() {
      return this.enter(FQCN, (String)null, (Object[])((Object[])null));
   }

   public EntryMessage traceEntry(String var1, Object... var2) {
      return this.enter(FQCN, var1, var2);
   }

   public EntryMessage traceEntry(Supplier<?>... var1) {
      return this.enter(FQCN, (String)null, (Supplier[])var1);
   }

   public EntryMessage traceEntry(String var1, Supplier<?>... var2) {
      return this.enter(FQCN, var1, var2);
   }

   public EntryMessage traceEntry(Message var1) {
      return this.enter(FQCN, var1);
   }

   public void traceExit() {
      this.exit(FQCN, (String)null, (Object)null);
   }

   public <R> R traceExit(R var1) {
      return this.exit(FQCN, (String)null, var1);
   }

   public <R> R traceExit(String var1, R var2) {
      return this.exit(FQCN, var1, var2);
   }

   public void traceExit(EntryMessage var1) {
      if (var1 != null && this.isEnabled(Level.TRACE, EXIT_MARKER, var1, (Throwable)null)) {
         this.logMessageSafely(FQCN, Level.TRACE, EXIT_MARKER, this.flowMessageFactory.newExitMessage(var1), (Throwable)null);
      }

   }

   public <R> R traceExit(EntryMessage var1, R var2) {
      if (var1 != null && this.isEnabled(Level.TRACE, EXIT_MARKER, var1, (Throwable)null)) {
         this.logMessageSafely(FQCN, Level.TRACE, EXIT_MARKER, this.flowMessageFactory.newExitMessage(var2, var1), (Throwable)null);
      }

      return var2;
   }

   public <R> R traceExit(Message var1, R var2) {
      if (var1 != null && this.isEnabled(Level.TRACE, EXIT_MARKER, var1, (Throwable)null)) {
         this.logMessageSafely(FQCN, Level.TRACE, EXIT_MARKER, this.flowMessageFactory.newExitMessage(var2, var1), (Throwable)null);
      }

      return var2;
   }

   public void warn(Marker var1, Message var2) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var2 != null ? var2.getThrowable() : null);
   }

   public void warn(Marker var1, Message var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   public void warn(Marker var1, CharSequence var2) {
      this.logIfEnabled(FQCN, Level.WARN, var1, (CharSequence)var2, (Throwable)null);
   }

   public void warn(Marker var1, CharSequence var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   public void warn(Marker var1, Object var2) {
      this.logIfEnabled(FQCN, Level.WARN, var1, (Object)var2, (Throwable)null);
   }

   public void warn(Marker var1, Object var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   public void warn(Marker var1, String var2) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, (Throwable)null);
   }

   public void warn(Marker var1, String var2, Object... var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   public void warn(Marker var1, String var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   public void warn(Message var1) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, (Message)var1, (Throwable)(var1 != null ? var1.getThrowable() : null));
   }

   public void warn(Message var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, (Message)var1, (Throwable)var2);
   }

   public void warn(CharSequence var1) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, (CharSequence)var1, (Throwable)null);
   }

   public void warn(CharSequence var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, (CharSequence)var1, (Throwable)var2);
   }

   public void warn(Object var1) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, (Object)var1, (Throwable)null);
   }

   public void warn(Object var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, (Object)var1, (Throwable)var2);
   }

   public void warn(String var1) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, (String)var1, (Throwable)((Throwable)null));
   }

   public void warn(String var1, Object... var2) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, (String)var1, (Object[])var2);
   }

   public void warn(String var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, (String)var1, (Throwable)var2);
   }

   public void warn(Supplier<?> var1) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, (Supplier)var1, (Throwable)((Throwable)null));
   }

   public void warn(Supplier<?> var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, (Supplier)var1, (Throwable)var2);
   }

   public void warn(Marker var1, Supplier<?> var2) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, (Throwable)null);
   }

   public void warn(Marker var1, String var2, Supplier<?>... var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   public void warn(Marker var1, Supplier<?> var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   public void warn(String var1, Supplier<?>... var2) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, (String)var1, (Supplier[])var2);
   }

   public void warn(Marker var1, MessageSupplier var2) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, (Throwable)null);
   }

   public void warn(Marker var1, MessageSupplier var2, Throwable var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   public void warn(MessageSupplier var1) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, (MessageSupplier)var1, (Throwable)((Throwable)null));
   }

   public void warn(MessageSupplier var1, Throwable var2) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, (MessageSupplier)var1, (Throwable)var2);
   }

   public void warn(Marker var1, String var2, Object var3) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3);
   }

   public void warn(Marker var1, String var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4);
   }

   public void warn(Marker var1, String var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5);
   }

   public void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5, var6);
   }

   public void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5, var6, var7);
   }

   public void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      this.logIfEnabled(FQCN, Level.WARN, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   public void warn(String var1, Object var2) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, (String)var1, (Object)var2);
   }

   public void warn(String var1, Object var2, Object var3) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, var1, var2, var3);
   }

   public void warn(String var1, Object var2, Object var3, Object var4) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, var1, var2, var3, var4);
   }

   public void warn(String var1, Object var2, Object var3, Object var4, Object var5) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, var1, var2, var3, var4, var5);
   }

   public void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, var1, var2, var3, var4, var5, var6);
   }

   public void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, var1, var2, var3, var4, var5, var6, var7);
   }

   public void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      this.logIfEnabled(FQCN, Level.WARN, (Marker)null, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   static {
      ENTRY_MARKER = MarkerManager.getMarker("ENTER").setParents(FLOW_MARKER);
      EXIT_MARKER = MarkerManager.getMarker("EXIT").setParents(FLOW_MARKER);
      EXCEPTION_MARKER = MarkerManager.getMarker("EXCEPTION");
      THROWING_MARKER = MarkerManager.getMarker("THROWING").setParents(EXCEPTION_MARKER);
      CATCHING_MARKER = MarkerManager.getMarker("CATCHING").setParents(EXCEPTION_MARKER);
      DEFAULT_MESSAGE_FACTORY_CLASS = createClassForProperty("log4j2.messageFactory", ReusableMessageFactory.class, ParameterizedMessageFactory.class);
      DEFAULT_FLOW_MESSAGE_FACTORY_CLASS = createFlowClassForProperty("log4j2.flowMessageFactory", DefaultFlowMessageFactory.class);
      FQCN = AbstractLogger.class.getName();
   }
}
