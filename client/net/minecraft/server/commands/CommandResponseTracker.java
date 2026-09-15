package net.minecraft.server.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class CommandResponseTracker<Element> {
   private int totalValue;
   private @Nullable Element onlyElement;
   private int elementCount;
   private @Nullable Element onlyNonZeroElement;
   private int nonZeroElementCount;

   public CommandResponseTracker() {
      super();
   }

   public static <T> CommandResponseTracker<T> create() {
      return new CommandResponseTracker<T>();
   }

   public void track(final Element element, final int value) {
      this.totalValue += value;
      if (++this.elementCount == 1) {
         this.onlyElement = element;
      } else {
         this.onlyElement = null;
      }

      if (value != 0) {
         if (++this.nonZeroElementCount == 1) {
            this.onlyNonZeroElement = element;
         } else {
            this.onlyNonZeroElement = null;
         }
      }

   }

   public void track(final Element element, final boolean value) {
      this.track(element, value ? 1 : 0);
   }

   public void track(final Element element) {
      this.track(element, 1);
   }

   private @Nullable Element firstElement(final ElementType type) {
      Object var10000;
      switch (type.ordinal()) {
         case 0 -> var10000 = this.onlyElement;
         case 1 -> var10000 = this.onlyNonZeroElement;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return (Element)var10000;
   }

   private int elementCount(final ElementType type) {
      int var10000;
      switch (type.ordinal()) {
         case 0 -> var10000 = this.elementCount;
         case 1 -> var10000 = this.nonZeroElementCount;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public int totalValue() {
      return this.totalValue;
   }

   public static <Element> Messages<Element> messages(final SingleHandler<Component, Element> onSingle, final MultipleHandler<Component> onMultiple) {
      return new Messages<Element>((ErrorHandler)null, new Dispatch(onSingle, onMultiple));
   }

   public static <Element> Messages<Element> messages(final ErrorHandler onZero, final SingleHandler<Component, Element> onSingle, final MultipleHandler<Component> onMultiple) {
      return new Messages<Element>(onZero, new Dispatch(onSingle, onMultiple));
   }

   public static <Element> Messages<Element> messages(final SimpleCommandExceptionType onZero, final SingleHandler<Component, Element> onSingle, final MultipleHandler<Component> onMultiple) {
      Objects.requireNonNull(onZero);
      return new Messages<Element>(onZero::create, new Dispatch(onSingle, onMultiple));
   }

   public <Result> Result dispatch(final ElementType elementType, final Dispatch<Result, ? super Element> dispatch) {
      Element firstElement = (Element)this.firstElement(elementType);
      return (Result)(firstElement != null ? dispatch.onSingle().create(firstElement, this.totalValue) : dispatch.onMultiple().create(this.elementCount(elementType), this.totalValue));
   }

   public int sendFeedback(final CommandSourceStack sourceStack, final boolean broadcast, final ElementType elementType, final Messages<? super Element> messages) throws CommandSyntaxException {
      messages.throwIfZero(this.elementCount(elementType));
      sourceStack.sendSuccess(() -> (Component)this.dispatch(elementType, messages.onSuccess()), broadcast);
      return this.totalValue;
   }

   public int sendFeedback(final CommandSourceStack sourceStack, final boolean broadcast, final Messages<? super Element> messages) throws CommandSyntaxException {
      return this.sendFeedback(sourceStack, broadcast, CommandResponseTracker.ElementType.NON_ZERO, messages);
   }

   public static <Element, Arg> MessagesWithArg<Element, Arg> messages(final SingleHandlerWithArg<Component, Element, Arg> onSingle, final MultipleHandlerWithArg<Component, Arg> onMultiple) {
      return new MessagesWithArg<Element, Arg>((ErrorHandlerWithArg)null, new DispatchWithArg(onSingle, onMultiple));
   }

   public static <Element, Arg> MessagesWithArg<Element, Arg> messages(final ErrorHandlerWithArg<Arg> onZero, final SingleHandlerWithArg<Component, Element, Arg> onSingle, final MultipleHandlerWithArg<Component, Arg> onMultiple) {
      return new MessagesWithArg<Element, Arg>(onZero, new DispatchWithArg(onSingle, onMultiple));
   }

   public static <Element, Arg> MessagesWithArg<Element, Arg> messages(final SimpleCommandExceptionType onZero, final SingleHandlerWithArg<Component, Element, Arg> onSingle, final MultipleHandlerWithArg<Component, Arg> onMultiple) {
      return new MessagesWithArg<Element, Arg>((var1) -> onZero.create(), new DispatchWithArg(onSingle, onMultiple));
   }

   public <Result, Arg> Result dispatch(final ElementType elementType, final DispatchWithArg<Result, ? super Element, Arg> dispatch, final Arg argument) {
      Element firstElement = (Element)this.firstElement(elementType);
      return (Result)(firstElement != null ? dispatch.onSingle().create(firstElement, this.totalValue, argument) : dispatch.onMultiple().create(this.elementCount(elementType), this.totalValue, argument));
   }

   public <Arg> int sendFeedback(final CommandSourceStack sourceStack, final boolean broadcast, final ElementType elementType, final MessagesWithArg<? super Element, Arg> messages, final Arg argument) throws CommandSyntaxException {
      messages.throwIfZero(this.elementCount(elementType), argument);
      sourceStack.sendSuccess(() -> (Component)this.dispatch(elementType, messages.onSuccess(), argument), broadcast);
      return this.totalValue;
   }

   public <Arg> int sendFeedback(final CommandSourceStack sourceStack, final boolean broadcast, final MessagesWithArg<? super Element, Arg> messages, final Arg argument) throws CommandSyntaxException {
      return this.sendFeedback(sourceStack, broadcast, CommandResponseTracker.ElementType.NON_ZERO, messages, argument);
   }

   public static <Element, Arg0, Arg1> MessagesWithArgs<Element, Arg0, Arg1> messages(final SingleHandlerWithArgs<Component, Element, Arg0, Arg1> onSingle, final MultipleHandlerWithArgs<Component, Arg0, Arg1> onMultiple) {
      return new MessagesWithArgs<Element, Arg0, Arg1>((ErrorHandlerWithArgs)null, new DispatchWithArgs(onSingle, onMultiple));
   }

   public static <Element, Arg0, Arg1> MessagesWithArgs<Element, Arg0, Arg1> messages(final ErrorHandlerWithArgs<Arg0, Arg1> onZero, final SingleHandlerWithArgs<Component, Element, Arg0, Arg1> onSingle, final MultipleHandlerWithArgs<Component, Arg0, Arg1> onMultiple) {
      return new MessagesWithArgs<Element, Arg0, Arg1>(onZero, new DispatchWithArgs(onSingle, onMultiple));
   }

   public static <Element, Arg0, Arg1> MessagesWithArgs<Element, Arg0, Arg1> messages(final SimpleCommandExceptionType onZero, final SingleHandlerWithArgs<Component, Element, Arg0, Arg1> onSingle, final MultipleHandlerWithArgs<Component, Arg0, Arg1> onMultiple) {
      return new MessagesWithArgs<Element, Arg0, Arg1>((var1, var2) -> onZero.create(), new DispatchWithArgs(onSingle, onMultiple));
   }

   public <Result, Arg0, Arg1> Result dispatch(final ElementType elementType, final DispatchWithArgs<Result, ? super Element, Arg0, Arg1> messages, final Arg0 argument0, final Arg1 argument1) {
      Element firstElement = (Element)this.firstElement(elementType);
      return (Result)(firstElement != null ? messages.onSingle().create(firstElement, this.totalValue, argument0, argument1) : messages.onMultiple().create(this.elementCount(elementType), this.totalValue, argument0, argument1));
   }

   public <Arg0, Arg1> int sendFeedback(final CommandSourceStack sourceStack, final boolean broadcast, final ElementType elementType, final MessagesWithArgs<? super Element, Arg0, Arg1> messages, final Arg0 argument0, final Arg1 argument1) throws CommandSyntaxException {
      messages.throwIfZero(this.elementCount(elementType), argument0, argument1);
      sourceStack.sendSuccess(() -> (Component)this.dispatch(elementType, messages.onSuccess(), argument0, argument1), broadcast);
      return this.totalValue;
   }

   public <Arg0, Arg1> int sendFeedback(final CommandSourceStack sourceStack, final boolean broadcast, final MessagesWithArgs<? super Element, Arg0, Arg1> messages, final Arg0 argument0, final Arg1 argument1) throws CommandSyntaxException {
      return this.sendFeedback(sourceStack, broadcast, CommandResponseTracker.ElementType.NON_ZERO, messages, argument0, argument1);
   }

   public static enum ElementType {
      ANY,
      NON_ZERO;

      private ElementType() {
      }

      // $FF: synthetic method
      private static ElementType[] $values() {
         return new ElementType[]{ANY, NON_ZERO};
      }
   }

   public static record Dispatch<Result, Element>(SingleHandler<Result, Element> onSingle, MultipleHandler<Result> onMultiple) {
      public Dispatch {
         super();
      }
   }

   public static record Messages<Element>(@Nullable ErrorHandler onZero, Dispatch<Component, Element> onSuccess) {
      public Messages {
         super();
      }

      public void throwIfZero(final int value) throws CommandSyntaxException {
         if (this.onZero != null && value == 0) {
            throw this.onZero.get();
         }
      }
   }

   public static record DispatchWithArg<Result, Element, Arg>(SingleHandlerWithArg<Result, Element, Arg> onSingle, MultipleHandlerWithArg<Result, Arg> onMultiple) {
      public DispatchWithArg {
         super();
      }
   }

   public static record MessagesWithArg<Element, Arg>(@Nullable ErrorHandlerWithArg<Arg> onZero, DispatchWithArg<Component, Element, Arg> onSuccess) {
      public MessagesWithArg {
         super();
      }

      public void throwIfZero(final int value, final Arg argument) throws CommandSyntaxException {
         if (this.onZero != null && value == 0) {
            throw this.onZero.get(argument);
         }
      }
   }

   public static record DispatchWithArgs<Result, Element, Arg0, Arg1>(SingleHandlerWithArgs<Result, Element, Arg0, Arg1> onSingle, MultipleHandlerWithArgs<Result, Arg0, Arg1> onMultiple) {
      public DispatchWithArgs {
         super();
      }
   }

   public static record MessagesWithArgs<Element, Arg0, Arg1>(@Nullable ErrorHandlerWithArgs<Arg0, Arg1> onZero, DispatchWithArgs<Component, Element, Arg0, Arg1> onSuccess) {
      public MessagesWithArgs {
         super();
      }

      public void throwIfZero(final int value, final Arg0 argument0, final Arg1 argument1) throws CommandSyntaxException {
         if (this.onZero != null && value == 0) {
            throw this.onZero.get(argument0, argument1);
         }
      }
   }

   @FunctionalInterface
   public interface ErrorHandler {
      CommandSyntaxException get();
   }

   @FunctionalInterface
   public interface ErrorHandlerWithArg<Arg> {
      CommandSyntaxException get(Arg argument);
   }

   @FunctionalInterface
   public interface ErrorHandlerWithArgs<Arg0, Arg1> {
      CommandSyntaxException get(Arg0 argument0, Arg1 argument1);
   }

   @FunctionalInterface
   public interface MultipleHandler<Result> {
      Result create(int elementCount, int totalValue);
   }

   @FunctionalInterface
   public interface MultipleHandlerWithArg<Result, Arg> {
      Result create(int elementCount, int totalValue, Arg argument);
   }

   @FunctionalInterface
   public interface MultipleHandlerWithArgs<Result, Arg0, Arg1> {
      Result create(int elementCount, int totalValue, Arg0 argument0, Arg1 argument1);
   }

   @FunctionalInterface
   public interface SingleHandler<Result, Element> {
      Result create(Element element, int totalValue);
   }

   @FunctionalInterface
   public interface SingleHandlerWithArg<Result, Element, Arg> {
      Result create(Element element, int totalValue, Arg argument);
   }

   @FunctionalInterface
   public interface SingleHandlerWithArgs<Result, Element, Arg0, Arg1> {
      Result create(Element element, int totalValue, Arg0 argument0, Arg1 argument1);
   }
}
