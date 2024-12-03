package net.minecraft.commands.arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignableCommand;

public record ArgumentSignatures(List<Entry> entries) {
   public static final ArgumentSignatures EMPTY = new ArgumentSignatures(List.of());
   private static final int MAX_ARGUMENT_COUNT = 8;
   private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

   public ArgumentSignatures(FriendlyByteBuf var1) {
      this((List)var1.readCollection(FriendlyByteBuf.limitValue(ArrayList::new, 8), Entry::new));
   }

   public ArgumentSignatures(List<Entry> var1) {
      super();
      this.entries = var1;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeCollection(this.entries, (var0, var1x) -> var1x.write(var0));
   }

   public static ArgumentSignatures signCommand(SignableCommand<?> var0, Signer var1) {
      List var2 = var0.arguments().stream().map((var1x) -> {
         MessageSignature var2 = var1.sign(var1x.value());
         return var2 != null ? new Entry(var1x.name(), var2) : null;
      }).filter(Objects::nonNull).toList();
      return new ArgumentSignatures(var2);
   }

   public static record Entry(String name, MessageSignature signature) {
      public Entry(FriendlyByteBuf var1) {
         this(var1.readUtf(16), MessageSignature.read(var1));
      }

      public Entry(String var1, MessageSignature var2) {
         super();
         this.name = var1;
         this.signature = var2;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeUtf(this.name, 16);
         MessageSignature.write(var1, this.signature);
      }
   }

   @FunctionalInterface
   public interface Signer {
      @Nullable
      MessageSignature sign(String var1);
   }
}
