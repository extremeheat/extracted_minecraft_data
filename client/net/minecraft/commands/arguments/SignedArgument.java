package net.minecraft.commands.arguments;

public interface SignedArgument<T> extends SerializableArgument<T> {
   SerializableArgument.Type<? extends SignedArgument<T>> type();
}
