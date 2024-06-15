package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.FriendlyByteBuf;

public class SingletonArgumentInfo<A extends ArgumentType<?>> implements ArgumentTypeInfo<A, SingletonArgumentInfo<A>.Template> {
   private final SingletonArgumentInfo<A>.Template template;

   private SingletonArgumentInfo(Function<CommandBuildContext, A> var1) {
      super();
      this.template = new SingletonArgumentInfo.Template(var1);
   }

   public static <T extends ArgumentType<?>> SingletonArgumentInfo<T> contextFree(Supplier<T> var0) {
      return new SingletonArgumentInfo<>(var1 -> (T)var0.get());
   }

   public static <T extends ArgumentType<?>> SingletonArgumentInfo<T> contextAware(Function<CommandBuildContext, T> var0) {
      return new SingletonArgumentInfo<>(var0);
   }

   public void serializeToNetwork(SingletonArgumentInfo<A>.Template var1, FriendlyByteBuf var2) {
   }

   public void serializeToJson(SingletonArgumentInfo<A>.Template var1, JsonObject var2) {
   }

   public SingletonArgumentInfo<A>.Template deserializeFromNetwork(FriendlyByteBuf var1) {
      return this.template;
   }

   public SingletonArgumentInfo<A>.Template unpack(A var1) {
      return this.template;
   }

   public final class Template implements ArgumentTypeInfo.Template<A> {
      private final Function<CommandBuildContext, A> constructor;

      public Template(final Function<CommandBuildContext, A> nullx) {
         super();
         this.constructor = nullx;
      }

      @Override
      public A instantiate(CommandBuildContext var1) {
         return this.constructor.apply(var1);
      }

      @Override
      public ArgumentTypeInfo<A, ?> type() {
         return SingletonArgumentInfo.this;
      }
   }
}
