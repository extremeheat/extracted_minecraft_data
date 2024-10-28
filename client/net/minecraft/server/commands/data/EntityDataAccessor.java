package net.minecraft.server.commands.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class EntityDataAccessor implements DataAccessor {
   private static final SimpleCommandExceptionType ERROR_NO_PLAYERS = new SimpleCommandExceptionType(Component.translatable("commands.data.entity.invalid"));
   public static final Function<String, DataCommands.DataProvider> PROVIDER = (var0) -> {
      return new DataCommands.DataProvider() {
         public DataAccessor access(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException {
            return new EntityDataAccessor(EntityArgument.getEntity(var1, var0));
         }

         public ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> var1, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> var2) {
            return var1.then(Commands.literal("entity").then((ArgumentBuilder)var2.apply(Commands.argument(var0, EntityArgument.entity()))));
         }
      };
   };
   private final Entity entity;

   public EntityDataAccessor(Entity var1) {
      super();
      this.entity = var1;
   }

   public void setData(CompoundTag var1) throws CommandSyntaxException {
      if (this.entity instanceof Player) {
         throw ERROR_NO_PLAYERS.create();
      } else {
         UUID var2 = this.entity.getUUID();
         this.entity.load(var1);
         this.entity.setUUID(var2);
      }
   }

   public CompoundTag getData() {
      return NbtPredicate.getEntityTagToCompare(this.entity);
   }

   public Component getModifiedSuccess() {
      return Component.translatable("commands.data.entity.modified", this.entity.getDisplayName());
   }

   public Component getPrintSuccess(Tag var1) {
      return Component.translatable("commands.data.entity.query", this.entity.getDisplayName(), NbtUtils.toPrettyComponent(var1));
   }

   public Component getPrintSuccess(NbtPathArgument.NbtPath var1, double var2, int var4) {
      return Component.translatable("commands.data.entity.get", var1.asString(), this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", var2), var4);
   }
}
