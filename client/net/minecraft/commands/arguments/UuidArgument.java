package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class UuidArgument implements ArgumentType<UUID> {
   public static final SimpleCommandExceptionType ERROR_INVALID_UUID = new SimpleCommandExceptionType(Component.translatable("argument.uuid.invalid"));
   private static final Collection<String> EXAMPLES = Arrays.asList("dd12be42-52a9-4a91-a8a1-11c01849e498");
   private static final Pattern ALLOWED_CHARACTERS = Pattern.compile("^([-A-Fa-f0-9]+)");

   public UuidArgument() {
      super();
   }

   public static UUID getUuid(CommandContext<CommandSourceStack> var0, String var1) {
      return (UUID)var0.getArgument(var1, UUID.class);
   }

   public static UuidArgument uuid() {
      return new UuidArgument();
   }

   public UUID parse(StringReader var1) throws CommandSyntaxException {
      String var2 = var1.getRemaining();
      Matcher var3 = ALLOWED_CHARACTERS.matcher(var2);
      if (var3.find()) {
         String var4 = var3.group(1);

         try {
            UUID var5 = UUID.fromString(var4);
            var1.setCursor(var1.getCursor() + var4.length());
            return var5;
         } catch (IllegalArgumentException var6) {
         }
      }

      throw ERROR_INVALID_UUID.createWithContext(var1);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
