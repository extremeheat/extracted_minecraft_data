package net.minecraft.server.dedicated;

import net.minecraft.command.CommandSource;

public class PendingCommand {
   public final String field_73702_a;
   public final CommandSource field_73701_b;

   public PendingCommand(String var1, CommandSource var2) {
      super();
      this.field_73702_a = var1;
      this.field_73701_b = var2;
   }
}
