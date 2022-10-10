package net.minecraft.command.impl.data;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class EntityDataAccessor implements IDataAccessor {
   private static final SimpleCommandExceptionType field_198927_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.data.entity.invalid", new Object[0]));
   public static final DataCommand.IDataProvider field_198926_a = new DataCommand.IDataProvider() {
      public IDataAccessor func_198919_a(CommandContext<CommandSource> var1) throws CommandSyntaxException {
         return new EntityDataAccessor(EntityArgument.func_197088_a(var1, "target"));
      }

      public ArgumentBuilder<CommandSource, ?> func_198920_a(ArgumentBuilder<CommandSource, ?> var1, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> var2) {
         return var1.then(Commands.func_197057_a("entity").then((ArgumentBuilder)var2.apply(Commands.func_197056_a("target", EntityArgument.func_197086_a()))));
      }
   };
   private final Entity field_198928_c;

   public EntityDataAccessor(Entity var1) {
      super();
      this.field_198928_c = var1;
   }

   public void func_198925_a(NBTTagCompound var1) throws CommandSyntaxException {
      if (this.field_198928_c instanceof EntityPlayer) {
         throw field_198927_b.create();
      } else {
         UUID var2 = this.field_198928_c.func_110124_au();
         this.field_198928_c.func_70020_e(var1);
         this.field_198928_c.func_184221_a(var2);
      }
   }

   public NBTTagCompound func_198923_a() {
      return NBTPredicate.func_196981_b(this.field_198928_c);
   }

   public ITextComponent func_198921_b() {
      return new TextComponentTranslation("commands.data.entity.modified", new Object[]{this.field_198928_c.func_145748_c_()});
   }

   public ITextComponent func_198924_b(INBTBase var1) {
      return new TextComponentTranslation("commands.data.entity.query", new Object[]{this.field_198928_c.func_145748_c_(), var1.func_197637_c()});
   }

   public ITextComponent func_198922_a(NBTPathArgument.NBTPath var1, double var2, int var4) {
      return new TextComponentTranslation("commands.data.entity.get", new Object[]{var1, this.field_198928_c.func_145748_c_(), String.format(Locale.ROOT, "%.2f", var2), var4});
   }
}
