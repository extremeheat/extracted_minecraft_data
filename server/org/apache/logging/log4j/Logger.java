package org.apache.logging.log4j;

import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

public interface Logger {
   void catching(Level var1, Throwable var2);

   void catching(Throwable var1);

   void debug(Marker var1, Message var2);

   void debug(Marker var1, Message var2, Throwable var3);

   void debug(Marker var1, MessageSupplier var2);

   void debug(Marker var1, MessageSupplier var2, Throwable var3);

   void debug(Marker var1, CharSequence var2);

   void debug(Marker var1, CharSequence var2, Throwable var3);

   void debug(Marker var1, Object var2);

   void debug(Marker var1, Object var2, Throwable var3);

   void debug(Marker var1, String var2);

   void debug(Marker var1, String var2, Object... var3);

   void debug(Marker var1, String var2, Supplier<?>... var3);

   void debug(Marker var1, String var2, Throwable var3);

   void debug(Marker var1, Supplier<?> var2);

   void debug(Marker var1, Supplier<?> var2, Throwable var3);

   void debug(Message var1);

   void debug(Message var1, Throwable var2);

   void debug(MessageSupplier var1);

   void debug(MessageSupplier var1, Throwable var2);

   void debug(CharSequence var1);

   void debug(CharSequence var1, Throwable var2);

   void debug(Object var1);

   void debug(Object var1, Throwable var2);

   void debug(String var1);

   void debug(String var1, Object... var2);

   void debug(String var1, Supplier<?>... var2);

   void debug(String var1, Throwable var2);

   void debug(Supplier<?> var1);

   void debug(Supplier<?> var1, Throwable var2);

   void debug(Marker var1, String var2, Object var3);

   void debug(Marker var1, String var2, Object var3, Object var4);

   void debug(Marker var1, String var2, Object var3, Object var4, Object var5);

   void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6);

   void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7);

   void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   void debug(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12);

   void debug(String var1, Object var2);

   void debug(String var1, Object var2, Object var3);

   void debug(String var1, Object var2, Object var3, Object var4);

   void debug(String var1, Object var2, Object var3, Object var4, Object var5);

   void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6);

   void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7);

   void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   void debug(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   /** @deprecated */
   @Deprecated
   void entry();

   void entry(Object... var1);

   void error(Marker var1, Message var2);

   void error(Marker var1, Message var2, Throwable var3);

   void error(Marker var1, MessageSupplier var2);

   void error(Marker var1, MessageSupplier var2, Throwable var3);

   void error(Marker var1, CharSequence var2);

   void error(Marker var1, CharSequence var2, Throwable var3);

   void error(Marker var1, Object var2);

   void error(Marker var1, Object var2, Throwable var3);

   void error(Marker var1, String var2);

   void error(Marker var1, String var2, Object... var3);

   void error(Marker var1, String var2, Supplier<?>... var3);

   void error(Marker var1, String var2, Throwable var3);

   void error(Marker var1, Supplier<?> var2);

   void error(Marker var1, Supplier<?> var2, Throwable var3);

   void error(Message var1);

   void error(Message var1, Throwable var2);

   void error(MessageSupplier var1);

   void error(MessageSupplier var1, Throwable var2);

   void error(CharSequence var1);

   void error(CharSequence var1, Throwable var2);

   void error(Object var1);

   void error(Object var1, Throwable var2);

   void error(String var1);

   void error(String var1, Object... var2);

   void error(String var1, Supplier<?>... var2);

   void error(String var1, Throwable var2);

   void error(Supplier<?> var1);

   void error(Supplier<?> var1, Throwable var2);

   void error(Marker var1, String var2, Object var3);

   void error(Marker var1, String var2, Object var3, Object var4);

   void error(Marker var1, String var2, Object var3, Object var4, Object var5);

   void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6);

   void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7);

   void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   void error(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12);

   void error(String var1, Object var2);

   void error(String var1, Object var2, Object var3);

   void error(String var1, Object var2, Object var3, Object var4);

   void error(String var1, Object var2, Object var3, Object var4, Object var5);

   void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6);

   void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7);

   void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   void error(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   /** @deprecated */
   @Deprecated
   void exit();

   /** @deprecated */
   @Deprecated
   <R> R exit(R var1);

   void fatal(Marker var1, Message var2);

   void fatal(Marker var1, Message var2, Throwable var3);

   void fatal(Marker var1, MessageSupplier var2);

   void fatal(Marker var1, MessageSupplier var2, Throwable var3);

   void fatal(Marker var1, CharSequence var2);

   void fatal(Marker var1, CharSequence var2, Throwable var3);

   void fatal(Marker var1, Object var2);

   void fatal(Marker var1, Object var2, Throwable var3);

   void fatal(Marker var1, String var2);

   void fatal(Marker var1, String var2, Object... var3);

   void fatal(Marker var1, String var2, Supplier<?>... var3);

   void fatal(Marker var1, String var2, Throwable var3);

   void fatal(Marker var1, Supplier<?> var2);

   void fatal(Marker var1, Supplier<?> var2, Throwable var3);

   void fatal(Message var1);

   void fatal(Message var1, Throwable var2);

   void fatal(MessageSupplier var1);

   void fatal(MessageSupplier var1, Throwable var2);

   void fatal(CharSequence var1);

   void fatal(CharSequence var1, Throwable var2);

   void fatal(Object var1);

   void fatal(Object var1, Throwable var2);

   void fatal(String var1);

   void fatal(String var1, Object... var2);

   void fatal(String var1, Supplier<?>... var2);

   void fatal(String var1, Throwable var2);

   void fatal(Supplier<?> var1);

   void fatal(Supplier<?> var1, Throwable var2);

   void fatal(Marker var1, String var2, Object var3);

   void fatal(Marker var1, String var2, Object var3, Object var4);

   void fatal(Marker var1, String var2, Object var3, Object var4, Object var5);

   void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6);

   void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7);

   void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   void fatal(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12);

   void fatal(String var1, Object var2);

   void fatal(String var1, Object var2, Object var3);

   void fatal(String var1, Object var2, Object var3, Object var4);

   void fatal(String var1, Object var2, Object var3, Object var4, Object var5);

   void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6);

   void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7);

   void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   void fatal(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   Level getLevel();

   <MF extends MessageFactory> MF getMessageFactory();

   String getName();

   void info(Marker var1, Message var2);

   void info(Marker var1, Message var2, Throwable var3);

   void info(Marker var1, MessageSupplier var2);

   void info(Marker var1, MessageSupplier var2, Throwable var3);

   void info(Marker var1, CharSequence var2);

   void info(Marker var1, CharSequence var2, Throwable var3);

   void info(Marker var1, Object var2);

   void info(Marker var1, Object var2, Throwable var3);

   void info(Marker var1, String var2);

   void info(Marker var1, String var2, Object... var3);

   void info(Marker var1, String var2, Supplier<?>... var3);

   void info(Marker var1, String var2, Throwable var3);

   void info(Marker var1, Supplier<?> var2);

   void info(Marker var1, Supplier<?> var2, Throwable var3);

   void info(Message var1);

   void info(Message var1, Throwable var2);

   void info(MessageSupplier var1);

   void info(MessageSupplier var1, Throwable var2);

   void info(CharSequence var1);

   void info(CharSequence var1, Throwable var2);

   void info(Object var1);

   void info(Object var1, Throwable var2);

   void info(String var1);

   void info(String var1, Object... var2);

   void info(String var1, Supplier<?>... var2);

   void info(String var1, Throwable var2);

   void info(Supplier<?> var1);

   void info(Supplier<?> var1, Throwable var2);

   void info(Marker var1, String var2, Object var3);

   void info(Marker var1, String var2, Object var3, Object var4);

   void info(Marker var1, String var2, Object var3, Object var4, Object var5);

   void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6);

   void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7);

   void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   void info(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12);

   void info(String var1, Object var2);

   void info(String var1, Object var2, Object var3);

   void info(String var1, Object var2, Object var3, Object var4);

   void info(String var1, Object var2, Object var3, Object var4, Object var5);

   void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6);

   void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7);

   void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   void info(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   boolean isDebugEnabled();

   boolean isDebugEnabled(Marker var1);

   boolean isEnabled(Level var1);

   boolean isEnabled(Level var1, Marker var2);

   boolean isErrorEnabled();

   boolean isErrorEnabled(Marker var1);

   boolean isFatalEnabled();

   boolean isFatalEnabled(Marker var1);

   boolean isInfoEnabled();

   boolean isInfoEnabled(Marker var1);

   boolean isTraceEnabled();

   boolean isTraceEnabled(Marker var1);

   boolean isWarnEnabled();

   boolean isWarnEnabled(Marker var1);

   void log(Level var1, Marker var2, Message var3);

   void log(Level var1, Marker var2, Message var3, Throwable var4);

   void log(Level var1, Marker var2, MessageSupplier var3);

   void log(Level var1, Marker var2, MessageSupplier var3, Throwable var4);

   void log(Level var1, Marker var2, CharSequence var3);

   void log(Level var1, Marker var2, CharSequence var3, Throwable var4);

   void log(Level var1, Marker var2, Object var3);

   void log(Level var1, Marker var2, Object var3, Throwable var4);

   void log(Level var1, Marker var2, String var3);

   void log(Level var1, Marker var2, String var3, Object... var4);

   void log(Level var1, Marker var2, String var3, Supplier<?>... var4);

   void log(Level var1, Marker var2, String var3, Throwable var4);

   void log(Level var1, Marker var2, Supplier<?> var3);

   void log(Level var1, Marker var2, Supplier<?> var3, Throwable var4);

   void log(Level var1, Message var2);

   void log(Level var1, Message var2, Throwable var3);

   void log(Level var1, MessageSupplier var2);

   void log(Level var1, MessageSupplier var2, Throwable var3);

   void log(Level var1, CharSequence var2);

   void log(Level var1, CharSequence var2, Throwable var3);

   void log(Level var1, Object var2);

   void log(Level var1, Object var2, Throwable var3);

   void log(Level var1, String var2);

   void log(Level var1, String var2, Object... var3);

   void log(Level var1, String var2, Supplier<?>... var3);

   void log(Level var1, String var2, Throwable var3);

   void log(Level var1, Supplier<?> var2);

   void log(Level var1, Supplier<?> var2, Throwable var3);

   void log(Level var1, Marker var2, String var3, Object var4);

   void log(Level var1, Marker var2, String var3, Object var4, Object var5);

   void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6);

   void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7);

   void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12);

   void log(Level var1, Marker var2, String var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13);

   void log(Level var1, String var2, Object var3);

   void log(Level var1, String var2, Object var3, Object var4);

   void log(Level var1, String var2, Object var3, Object var4, Object var5);

   void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6);

   void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7);

   void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   void log(Level var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12);

   void printf(Level var1, Marker var2, String var3, Object... var4);

   void printf(Level var1, String var2, Object... var3);

   <T extends Throwable> T throwing(Level var1, T var2);

   <T extends Throwable> T throwing(T var1);

   void trace(Marker var1, Message var2);

   void trace(Marker var1, Message var2, Throwable var3);

   void trace(Marker var1, MessageSupplier var2);

   void trace(Marker var1, MessageSupplier var2, Throwable var3);

   void trace(Marker var1, CharSequence var2);

   void trace(Marker var1, CharSequence var2, Throwable var3);

   void trace(Marker var1, Object var2);

   void trace(Marker var1, Object var2, Throwable var3);

   void trace(Marker var1, String var2);

   void trace(Marker var1, String var2, Object... var3);

   void trace(Marker var1, String var2, Supplier<?>... var3);

   void trace(Marker var1, String var2, Throwable var3);

   void trace(Marker var1, Supplier<?> var2);

   void trace(Marker var1, Supplier<?> var2, Throwable var3);

   void trace(Message var1);

   void trace(Message var1, Throwable var2);

   void trace(MessageSupplier var1);

   void trace(MessageSupplier var1, Throwable var2);

   void trace(CharSequence var1);

   void trace(CharSequence var1, Throwable var2);

   void trace(Object var1);

   void trace(Object var1, Throwable var2);

   void trace(String var1);

   void trace(String var1, Object... var2);

   void trace(String var1, Supplier<?>... var2);

   void trace(String var1, Throwable var2);

   void trace(Supplier<?> var1);

   void trace(Supplier<?> var1, Throwable var2);

   void trace(Marker var1, String var2, Object var3);

   void trace(Marker var1, String var2, Object var3, Object var4);

   void trace(Marker var1, String var2, Object var3, Object var4, Object var5);

   void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6);

   void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7);

   void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   void trace(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12);

   void trace(String var1, Object var2);

   void trace(String var1, Object var2, Object var3);

   void trace(String var1, Object var2, Object var3, Object var4);

   void trace(String var1, Object var2, Object var3, Object var4, Object var5);

   void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6);

   void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7);

   void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   void trace(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   EntryMessage traceEntry();

   EntryMessage traceEntry(String var1, Object... var2);

   EntryMessage traceEntry(Supplier<?>... var1);

   EntryMessage traceEntry(String var1, Supplier<?>... var2);

   EntryMessage traceEntry(Message var1);

   void traceExit();

   <R> R traceExit(R var1);

   <R> R traceExit(String var1, R var2);

   void traceExit(EntryMessage var1);

   <R> R traceExit(EntryMessage var1, R var2);

   <R> R traceExit(Message var1, R var2);

   void warn(Marker var1, Message var2);

   void warn(Marker var1, Message var2, Throwable var3);

   void warn(Marker var1, MessageSupplier var2);

   void warn(Marker var1, MessageSupplier var2, Throwable var3);

   void warn(Marker var1, CharSequence var2);

   void warn(Marker var1, CharSequence var2, Throwable var3);

   void warn(Marker var1, Object var2);

   void warn(Marker var1, Object var2, Throwable var3);

   void warn(Marker var1, String var2);

   void warn(Marker var1, String var2, Object... var3);

   void warn(Marker var1, String var2, Supplier<?>... var3);

   void warn(Marker var1, String var2, Throwable var3);

   void warn(Marker var1, Supplier<?> var2);

   void warn(Marker var1, Supplier<?> var2, Throwable var3);

   void warn(Message var1);

   void warn(Message var1, Throwable var2);

   void warn(MessageSupplier var1);

   void warn(MessageSupplier var1, Throwable var2);

   void warn(CharSequence var1);

   void warn(CharSequence var1, Throwable var2);

   void warn(Object var1);

   void warn(Object var1, Throwable var2);

   void warn(String var1);

   void warn(String var1, Object... var2);

   void warn(String var1, Supplier<?>... var2);

   void warn(String var1, Throwable var2);

   void warn(Supplier<?> var1);

   void warn(Supplier<?> var1, Throwable var2);

   void warn(Marker var1, String var2, Object var3);

   void warn(Marker var1, String var2, Object var3, Object var4);

   void warn(Marker var1, String var2, Object var3, Object var4, Object var5);

   void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6);

   void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7);

   void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

   void warn(Marker var1, String var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12);

   void warn(String var1, Object var2);

   void warn(String var1, Object var2, Object var3);

   void warn(String var1, Object var2, Object var3, Object var4);

   void warn(String var1, Object var2, Object var3, Object var4, Object var5);

   void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6);

   void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7);

   void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8);

   void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9);

   void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

   void warn(String var1, Object var2, Object var3, Object var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);
}
