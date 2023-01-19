package net.minecraft.commands.arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignableCommand;

public record ArgumentSignatures(List<ArgumentSignatures.Entry> b) {
   private final List<ArgumentSignatures.Entry> entries;
   public static final ArgumentSignatures EMPTY = new ArgumentSignatures(List.of());
   private static final int MAX_ARGUMENT_COUNT = 8;
   private static final int MAX_ARGUMENT_NAME_LENGTH = 16;

   public ArgumentSignatures(FriendlyByteBuf var1) {
      this(var1.readCollection(FriendlyByteBuf.limitValue(ArrayList::new, 8), ArgumentSignatures.Entry::new));
   }

   public ArgumentSignatures(List<ArgumentSignatures.Entry> var1) {
      super();
      this.entries = var1;
   }

   @Nullable
   public MessageSignature get(String var1) {
      for(ArgumentSignatures.Entry var3 : this.entries) {
         if (var3.name.equals(var1)) {
            return var3.signature;
         }
      }

      return null;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeCollection(this.entries, (var0, var1x) -> var1x.write(var0));
   }

   public static ArgumentSignatures signCommand(SignableCommand<?> var0, ArgumentSignatures.Signer var1) {
      List var2 = var0.arguments().stream().map(var1x -> {
         MessageSignature var2x = var1.sign(var1x.value());
         return var2x != null ? new ArgumentSignatures.Entry(var1x.name(), var2x) : null;
      }).filter(Objects::nonNull).toList();
      return new ArgumentSignatures(var2);
   }

   public static record Entry(String a, MessageSignature b) {
      final String name;
      final MessageSignature signature;

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
