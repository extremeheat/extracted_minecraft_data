package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;

public class AdvancementCommand {
   private static final SuggestionProvider<CommandSource> field_198218_a = (var0, var1) -> {
      Collection var2 = ((CommandSource)var0.getSource()).func_197028_i().func_191949_aK().func_195438_b();
      return ISuggestionProvider.func_212476_a(var2.stream().map(Advancement::func_192067_g), var1);
   };

   public static void func_198199_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("advancement").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197057_a("grant").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("targets", EntityArgument.func_197094_d()).then(Commands.func_197057_a("only").then(((RequiredArgumentBuilder)Commands.func_197056_a("advancement", ResourceLocationArgument.func_197197_a()).suggests(field_198218_a).executes((var0x) -> {
         return func_198214_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), AdvancementCommand.Action.GRANT, func_198216_a(ResourceLocationArgument.func_197198_a(var0x, "advancement"), AdvancementCommand.Mode.ONLY));
      })).then(Commands.func_197056_a("criterion", StringArgumentType.greedyString()).suggests((var0x, var1) -> {
         return ISuggestionProvider.func_197005_b(ResourceLocationArgument.func_197198_a(var0x, "advancement").func_192073_f().keySet(), var1);
      }).executes((var0x) -> {
         return func_198203_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), AdvancementCommand.Action.GRANT, ResourceLocationArgument.func_197198_a(var0x, "advancement"), StringArgumentType.getString(var0x, "criterion"));
      }))))).then(Commands.func_197057_a("from").then(Commands.func_197056_a("advancement", ResourceLocationArgument.func_197197_a()).suggests(field_198218_a).executes((var0x) -> {
         return func_198214_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), AdvancementCommand.Action.GRANT, func_198216_a(ResourceLocationArgument.func_197198_a(var0x, "advancement"), AdvancementCommand.Mode.FROM));
      })))).then(Commands.func_197057_a("until").then(Commands.func_197056_a("advancement", ResourceLocationArgument.func_197197_a()).suggests(field_198218_a).executes((var0x) -> {
         return func_198214_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), AdvancementCommand.Action.GRANT, func_198216_a(ResourceLocationArgument.func_197198_a(var0x, "advancement"), AdvancementCommand.Mode.UNTIL));
      })))).then(Commands.func_197057_a("through").then(Commands.func_197056_a("advancement", ResourceLocationArgument.func_197197_a()).suggests(field_198218_a).executes((var0x) -> {
         return func_198214_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), AdvancementCommand.Action.GRANT, func_198216_a(ResourceLocationArgument.func_197198_a(var0x, "advancement"), AdvancementCommand.Mode.THROUGH));
      })))).then(Commands.func_197057_a("everything").executes((var0x) -> {
         return func_198214_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), AdvancementCommand.Action.GRANT, ((CommandSource)var0x.getSource()).func_197028_i().func_191949_aK().func_195438_b());
      }))))).then(Commands.func_197057_a("revoke").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("targets", EntityArgument.func_197094_d()).then(Commands.func_197057_a("only").then(((RequiredArgumentBuilder)Commands.func_197056_a("advancement", ResourceLocationArgument.func_197197_a()).suggests(field_198218_a).executes((var0x) -> {
         return func_198214_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), AdvancementCommand.Action.REVOKE, func_198216_a(ResourceLocationArgument.func_197198_a(var0x, "advancement"), AdvancementCommand.Mode.ONLY));
      })).then(Commands.func_197056_a("criterion", StringArgumentType.greedyString()).suggests((var0x, var1) -> {
         return ISuggestionProvider.func_197005_b(ResourceLocationArgument.func_197198_a(var0x, "advancement").func_192073_f().keySet(), var1);
      }).executes((var0x) -> {
         return func_198203_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), AdvancementCommand.Action.REVOKE, ResourceLocationArgument.func_197198_a(var0x, "advancement"), StringArgumentType.getString(var0x, "criterion"));
      }))))).then(Commands.func_197057_a("from").then(Commands.func_197056_a("advancement", ResourceLocationArgument.func_197197_a()).suggests(field_198218_a).executes((var0x) -> {
         return func_198214_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), AdvancementCommand.Action.REVOKE, func_198216_a(ResourceLocationArgument.func_197198_a(var0x, "advancement"), AdvancementCommand.Mode.FROM));
      })))).then(Commands.func_197057_a("until").then(Commands.func_197056_a("advancement", ResourceLocationArgument.func_197197_a()).suggests(field_198218_a).executes((var0x) -> {
         return func_198214_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), AdvancementCommand.Action.REVOKE, func_198216_a(ResourceLocationArgument.func_197198_a(var0x, "advancement"), AdvancementCommand.Mode.UNTIL));
      })))).then(Commands.func_197057_a("through").then(Commands.func_197056_a("advancement", ResourceLocationArgument.func_197197_a()).suggests(field_198218_a).executes((var0x) -> {
         return func_198214_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), AdvancementCommand.Action.REVOKE, func_198216_a(ResourceLocationArgument.func_197198_a(var0x, "advancement"), AdvancementCommand.Mode.THROUGH));
      })))).then(Commands.func_197057_a("everything").executes((var0x) -> {
         return func_198214_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), AdvancementCommand.Action.REVOKE, ((CommandSource)var0x.getSource()).func_197028_i().func_191949_aK().func_195438_b());
      })))));
   }

   private static int func_198214_a(CommandSource var0, Collection<EntityPlayerMP> var1, AdvancementCommand.Action var2, Collection<Advancement> var3) {
      int var4 = 0;

      EntityPlayerMP var6;
      for(Iterator var5 = var1.iterator(); var5.hasNext(); var4 += var2.func_198180_a(var6, var3)) {
         var6 = (EntityPlayerMP)var5.next();
      }

      if (var4 == 0) {
         if (var3.size() == 1) {
            if (var1.size() == 1) {
               throw new CommandException(new TextComponentTranslation(var2.func_198181_a() + ".one.to.one.failure", new Object[]{((Advancement)var3.iterator().next()).func_193123_j(), ((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}));
            } else {
               throw new CommandException(new TextComponentTranslation(var2.func_198181_a() + ".one.to.many.failure", new Object[]{((Advancement)var3.iterator().next()).func_193123_j(), var1.size()}));
            }
         } else if (var1.size() == 1) {
            throw new CommandException(new TextComponentTranslation(var2.func_198181_a() + ".many.to.one.failure", new Object[]{var3.size(), ((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}));
         } else {
            throw new CommandException(new TextComponentTranslation(var2.func_198181_a() + ".many.to.many.failure", new Object[]{var3.size(), var1.size()}));
         }
      } else {
         if (var3.size() == 1) {
            if (var1.size() == 1) {
               var0.func_197030_a(new TextComponentTranslation(var2.func_198181_a() + ".one.to.one.success", new Object[]{((Advancement)var3.iterator().next()).func_193123_j(), ((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
            } else {
               var0.func_197030_a(new TextComponentTranslation(var2.func_198181_a() + ".one.to.many.success", new Object[]{((Advancement)var3.iterator().next()).func_193123_j(), var1.size()}), true);
            }
         } else if (var1.size() == 1) {
            var0.func_197030_a(new TextComponentTranslation(var2.func_198181_a() + ".many.to.one.success", new Object[]{var3.size(), ((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
         } else {
            var0.func_197030_a(new TextComponentTranslation(var2.func_198181_a() + ".many.to.many.success", new Object[]{var3.size(), var1.size()}), true);
         }

         return var4;
      }
   }

   private static int func_198203_a(CommandSource var0, Collection<EntityPlayerMP> var1, AdvancementCommand.Action var2, Advancement var3, String var4) {
      int var5 = 0;
      if (!var3.func_192073_f().containsKey(var4)) {
         throw new CommandException(new TextComponentTranslation("commands.advancement.criterionNotFound", new Object[]{var3.func_193123_j(), var4}));
      } else {
         Iterator var6 = var1.iterator();

         while(var6.hasNext()) {
            EntityPlayerMP var7 = (EntityPlayerMP)var6.next();
            if (var2.func_198182_a(var7, var3, var4)) {
               ++var5;
            }
         }

         if (var5 == 0) {
            if (var1.size() == 1) {
               throw new CommandException(new TextComponentTranslation(var2.func_198181_a() + ".criterion.to.one.failure", new Object[]{var4, var3.func_193123_j(), ((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}));
            } else {
               throw new CommandException(new TextComponentTranslation(var2.func_198181_a() + ".criterion.to.many.failure", new Object[]{var4, var3.func_193123_j(), var1.size()}));
            }
         } else {
            if (var1.size() == 1) {
               var0.func_197030_a(new TextComponentTranslation(var2.func_198181_a() + ".criterion.to.one.success", new Object[]{var4, var3.func_193123_j(), ((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
            } else {
               var0.func_197030_a(new TextComponentTranslation(var2.func_198181_a() + ".criterion.to.many.success", new Object[]{var4, var3.func_193123_j(), var1.size()}), true);
            }

            return var5;
         }
      }
   }

   private static List<Advancement> func_198216_a(Advancement var0, AdvancementCommand.Mode var1) {
      ArrayList var2 = Lists.newArrayList();
      if (var1.field_198194_f) {
         for(Advancement var3 = var0.func_192070_b(); var3 != null; var3 = var3.func_192070_b()) {
            var2.add(var3);
         }
      }

      var2.add(var0);
      if (var1.field_198195_g) {
         func_198207_a(var0, var2);
      }

      return var2;
   }

   private static void func_198207_a(Advancement var0, List<Advancement> var1) {
      Iterator var2 = var0.func_192069_e().iterator();

      while(var2.hasNext()) {
         Advancement var3 = (Advancement)var2.next();
         var1.add(var3);
         func_198207_a(var3, var1);
      }

   }

   static enum Mode {
      ONLY(false, false),
      THROUGH(true, true),
      FROM(false, true),
      UNTIL(true, false),
      EVERYTHING(true, true);

      private final boolean field_198194_f;
      private final boolean field_198195_g;

      private Mode(boolean var3, boolean var4) {
         this.field_198194_f = var3;
         this.field_198195_g = var4;
      }
   }

   static enum Action {
      GRANT("grant") {
         protected boolean func_198179_a(EntityPlayerMP var1, Advancement var2) {
            AdvancementProgress var3 = var1.func_192039_O().func_192747_a(var2);
            if (var3.func_192105_a()) {
               return false;
            } else {
               Iterator var4 = var3.func_192107_d().iterator();

               while(var4.hasNext()) {
                  String var5 = (String)var4.next();
                  var1.func_192039_O().func_192750_a(var2, var5);
               }

               return true;
            }
         }

         protected boolean func_198182_a(EntityPlayerMP var1, Advancement var2, String var3) {
            return var1.func_192039_O().func_192750_a(var2, var3);
         }
      },
      REVOKE("revoke") {
         protected boolean func_198179_a(EntityPlayerMP var1, Advancement var2) {
            AdvancementProgress var3 = var1.func_192039_O().func_192747_a(var2);
            if (!var3.func_192108_b()) {
               return false;
            } else {
               Iterator var4 = var3.func_192102_e().iterator();

               while(var4.hasNext()) {
                  String var5 = (String)var4.next();
                  var1.func_192039_O().func_192744_b(var2, var5);
               }

               return true;
            }
         }

         protected boolean func_198182_a(EntityPlayerMP var1, Advancement var2, String var3) {
            return var1.func_192039_O().func_192744_b(var2, var3);
         }
      };

      private final String field_198186_c;

      private Action(String var3) {
         this.field_198186_c = "commands.advancement." + var3;
      }

      public int func_198180_a(EntityPlayerMP var1, Iterable<Advancement> var2) {
         int var3 = 0;
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            Advancement var5 = (Advancement)var4.next();
            if (this.func_198179_a(var1, var5)) {
               ++var3;
            }
         }

         return var3;
      }

      protected abstract boolean func_198179_a(EntityPlayerMP var1, Advancement var2);

      protected abstract boolean func_198182_a(EntityPlayerMP var1, Advancement var2, String var3);

      protected String func_198181_a() {
         return this.field_198186_c;
      }

      // $FF: synthetic method
      Action(String var3, Object var4) {
         this(var3);
      }
   }
}
