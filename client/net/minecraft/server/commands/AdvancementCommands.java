package net.minecraft.server.commands;

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
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementCommands {
   private static final SuggestionProvider<CommandSourceStack> SUGGEST_ADVANCEMENTS = (var0, var1) -> {
      Collection var2 = ((CommandSourceStack)var0.getSource()).getServer().getAdvancements().getAllAdvancements();
      return SharedSuggestionProvider.suggestResource(var2.stream().map(Advancement::getId), var1);
   };

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("advancement").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("grant").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((var0x) -> {
         return perform((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(var0x, "advancement"), AdvancementCommands.Mode.ONLY));
      })).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((var0x, var1) -> {
         return SharedSuggestionProvider.suggest((Iterable)ResourceLocationArgument.getAdvancement(var0x, "advancement").getCriteria().keySet(), var1);
      }).executes((var0x) -> {
         return performCriterion((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), AdvancementCommands.Action.GRANT, ResourceLocationArgument.getAdvancement(var0x, "advancement"), StringArgumentType.getString(var0x, "criterion"));
      }))))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((var0x) -> {
         return perform((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(var0x, "advancement"), AdvancementCommands.Mode.FROM));
      })))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((var0x) -> {
         return perform((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(var0x, "advancement"), AdvancementCommands.Mode.UNTIL));
      })))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((var0x) -> {
         return perform((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), AdvancementCommands.Action.GRANT, getAdvancements(ResourceLocationArgument.getAdvancement(var0x, "advancement"), AdvancementCommands.Mode.THROUGH));
      })))).then(Commands.literal("everything").executes((var0x) -> {
         return perform((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), AdvancementCommands.Action.GRANT, ((CommandSourceStack)var0x.getSource()).getServer().getAdvancements().getAllAdvancements());
      }))))).then(Commands.literal("revoke").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then(Commands.literal("only").then(((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((var0x) -> {
         return perform((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(var0x, "advancement"), AdvancementCommands.Mode.ONLY));
      })).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((var0x, var1) -> {
         return SharedSuggestionProvider.suggest((Iterable)ResourceLocationArgument.getAdvancement(var0x, "advancement").getCriteria().keySet(), var1);
      }).executes((var0x) -> {
         return performCriterion((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), AdvancementCommands.Action.REVOKE, ResourceLocationArgument.getAdvancement(var0x, "advancement"), StringArgumentType.getString(var0x, "criterion"));
      }))))).then(Commands.literal("from").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((var0x) -> {
         return perform((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(var0x, "advancement"), AdvancementCommands.Mode.FROM));
      })))).then(Commands.literal("until").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((var0x) -> {
         return perform((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(var0x, "advancement"), AdvancementCommands.Mode.UNTIL));
      })))).then(Commands.literal("through").then(Commands.argument("advancement", ResourceLocationArgument.id()).suggests(SUGGEST_ADVANCEMENTS).executes((var0x) -> {
         return perform((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), AdvancementCommands.Action.REVOKE, getAdvancements(ResourceLocationArgument.getAdvancement(var0x, "advancement"), AdvancementCommands.Mode.THROUGH));
      })))).then(Commands.literal("everything").executes((var0x) -> {
         return perform((CommandSourceStack)var0x.getSource(), EntityArgument.getPlayers(var0x, "targets"), AdvancementCommands.Action.REVOKE, ((CommandSourceStack)var0x.getSource()).getServer().getAdvancements().getAllAdvancements());
      })))));
   }

   private static int perform(CommandSourceStack var0, Collection<ServerPlayer> var1, AdvancementCommands.Action var2, Collection<Advancement> var3) {
      int var4 = 0;

      ServerPlayer var6;
      for(Iterator var5 = var1.iterator(); var5.hasNext(); var4 += var2.perform(var6, (Iterable)var3)) {
         var6 = (ServerPlayer)var5.next();
      }

      if (var4 == 0) {
         if (var3.size() == 1) {
            if (var1.size() == 1) {
               throw new CommandRuntimeException(new TranslatableComponent(var2.getKey() + ".one.to.one.failure", new Object[]{((Advancement)var3.iterator().next()).getChatComponent(), ((ServerPlayer)var1.iterator().next()).getDisplayName()}));
            } else {
               throw new CommandRuntimeException(new TranslatableComponent(var2.getKey() + ".one.to.many.failure", new Object[]{((Advancement)var3.iterator().next()).getChatComponent(), var1.size()}));
            }
         } else if (var1.size() == 1) {
            throw new CommandRuntimeException(new TranslatableComponent(var2.getKey() + ".many.to.one.failure", new Object[]{var3.size(), ((ServerPlayer)var1.iterator().next()).getDisplayName()}));
         } else {
            throw new CommandRuntimeException(new TranslatableComponent(var2.getKey() + ".many.to.many.failure", new Object[]{var3.size(), var1.size()}));
         }
      } else {
         if (var3.size() == 1) {
            if (var1.size() == 1) {
               var0.sendSuccess(new TranslatableComponent(var2.getKey() + ".one.to.one.success", new Object[]{((Advancement)var3.iterator().next()).getChatComponent(), ((ServerPlayer)var1.iterator().next()).getDisplayName()}), true);
            } else {
               var0.sendSuccess(new TranslatableComponent(var2.getKey() + ".one.to.many.success", new Object[]{((Advancement)var3.iterator().next()).getChatComponent(), var1.size()}), true);
            }
         } else if (var1.size() == 1) {
            var0.sendSuccess(new TranslatableComponent(var2.getKey() + ".many.to.one.success", new Object[]{var3.size(), ((ServerPlayer)var1.iterator().next()).getDisplayName()}), true);
         } else {
            var0.sendSuccess(new TranslatableComponent(var2.getKey() + ".many.to.many.success", new Object[]{var3.size(), var1.size()}), true);
         }

         return var4;
      }
   }

   private static int performCriterion(CommandSourceStack var0, Collection<ServerPlayer> var1, AdvancementCommands.Action var2, Advancement var3, String var4) {
      int var5 = 0;
      if (!var3.getCriteria().containsKey(var4)) {
         throw new CommandRuntimeException(new TranslatableComponent("commands.advancement.criterionNotFound", new Object[]{var3.getChatComponent(), var4}));
      } else {
         Iterator var6 = var1.iterator();

         while(var6.hasNext()) {
            ServerPlayer var7 = (ServerPlayer)var6.next();
            if (var2.performCriterion(var7, var3, var4)) {
               ++var5;
            }
         }

         if (var5 == 0) {
            if (var1.size() == 1) {
               throw new CommandRuntimeException(new TranslatableComponent(var2.getKey() + ".criterion.to.one.failure", new Object[]{var4, var3.getChatComponent(), ((ServerPlayer)var1.iterator().next()).getDisplayName()}));
            } else {
               throw new CommandRuntimeException(new TranslatableComponent(var2.getKey() + ".criterion.to.many.failure", new Object[]{var4, var3.getChatComponent(), var1.size()}));
            }
         } else {
            if (var1.size() == 1) {
               var0.sendSuccess(new TranslatableComponent(var2.getKey() + ".criterion.to.one.success", new Object[]{var4, var3.getChatComponent(), ((ServerPlayer)var1.iterator().next()).getDisplayName()}), true);
            } else {
               var0.sendSuccess(new TranslatableComponent(var2.getKey() + ".criterion.to.many.success", new Object[]{var4, var3.getChatComponent(), var1.size()}), true);
            }

            return var5;
         }
      }
   }

   private static List<Advancement> getAdvancements(Advancement var0, AdvancementCommands.Mode var1) {
      ArrayList var2 = Lists.newArrayList();
      if (var1.parents) {
         for(Advancement var3 = var0.getParent(); var3 != null; var3 = var3.getParent()) {
            var2.add(var3);
         }
      }

      var2.add(var0);
      if (var1.children) {
         addChildren(var0, var2);
      }

      return var2;
   }

   private static void addChildren(Advancement var0, List<Advancement> var1) {
      Iterator var2 = var0.getChildren().iterator();

      while(var2.hasNext()) {
         Advancement var3 = (Advancement)var2.next();
         var1.add(var3);
         addChildren(var3, var1);
      }

   }

   static enum Mode {
      ONLY(false, false),
      THROUGH(true, true),
      FROM(false, true),
      UNTIL(true, false),
      EVERYTHING(true, true);

      private final boolean parents;
      private final boolean children;

      private Mode(boolean var3, boolean var4) {
         this.parents = var3;
         this.children = var4;
      }
   }

   static enum Action {
      GRANT("grant") {
         protected boolean perform(ServerPlayer var1, Advancement var2) {
            AdvancementProgress var3 = var1.getAdvancements().getOrStartProgress(var2);
            if (var3.isDone()) {
               return false;
            } else {
               Iterator var4 = var3.getRemainingCriteria().iterator();

               while(var4.hasNext()) {
                  String var5 = (String)var4.next();
                  var1.getAdvancements().award(var2, var5);
               }

               return true;
            }
         }

         protected boolean performCriterion(ServerPlayer var1, Advancement var2, String var3) {
            return var1.getAdvancements().award(var2, var3);
         }
      },
      REVOKE("revoke") {
         protected boolean perform(ServerPlayer var1, Advancement var2) {
            AdvancementProgress var3 = var1.getAdvancements().getOrStartProgress(var2);
            if (!var3.hasProgress()) {
               return false;
            } else {
               Iterator var4 = var3.getCompletedCriteria().iterator();

               while(var4.hasNext()) {
                  String var5 = (String)var4.next();
                  var1.getAdvancements().revoke(var2, var5);
               }

               return true;
            }
         }

         protected boolean performCriterion(ServerPlayer var1, Advancement var2, String var3) {
            return var1.getAdvancements().revoke(var2, var3);
         }
      };

      private final String key;

      private Action(String var3) {
         this.key = "commands.advancement." + var3;
      }

      public int perform(ServerPlayer var1, Iterable<Advancement> var2) {
         int var3 = 0;
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            Advancement var5 = (Advancement)var4.next();
            if (this.perform(var1, var5)) {
               ++var3;
            }
         }

         return var3;
      }

      protected abstract boolean perform(ServerPlayer var1, Advancement var2);

      protected abstract boolean performCriterion(ServerPlayer var1, Advancement var2, String var3);

      protected String getKey() {
         return this.key;
      }

      // $FF: synthetic method
      Action(String var3, Object var4) {
         this(var3);
      }
   }
}
