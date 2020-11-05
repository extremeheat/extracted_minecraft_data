package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.GameRules;

public class ServerFunctionManager {
   private static final ResourceLocation TICK_FUNCTION_TAG = new ResourceLocation("tick");
   private static final ResourceLocation LOAD_FUNCTION_TAG = new ResourceLocation("load");
   private final MinecraftServer server;
   private boolean isInFunction;
   private final ArrayDeque<ServerFunctionManager.QueuedCommand> commandQueue = new ArrayDeque();
   private final List<ServerFunctionManager.QueuedCommand> nestedCalls = Lists.newArrayList();
   private final List<CommandFunction> ticking = Lists.newArrayList();
   private boolean postReload;
   private ServerFunctionLibrary library;

   public ServerFunctionManager(MinecraftServer var1, ServerFunctionLibrary var2) {
      super();
      this.server = var1;
      this.library = var2;
      this.postReload(var2);
   }

   public int getCommandLimit() {
      return this.server.getGameRules().getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
   }

   public CommandDispatcher<CommandSourceStack> getDispatcher() {
      return this.server.getCommands().getDispatcher();
   }

   public void tick() {
      this.executeTagFunctions(this.ticking, TICK_FUNCTION_TAG);
      if (this.postReload) {
         this.postReload = false;
         List var1 = this.library.getTags().getTagOrEmpty(LOAD_FUNCTION_TAG).getValues();
         this.executeTagFunctions(var1, LOAD_FUNCTION_TAG);
      }

   }

   private void executeTagFunctions(Collection<CommandFunction> var1, ResourceLocation var2) {
      this.server.getProfiler().push(var2::toString);
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         CommandFunction var4 = (CommandFunction)var3.next();
         this.execute(var4, this.getGameLoopSender());
      }

      this.server.getProfiler().pop();
   }

   public int execute(CommandFunction var1, CommandSourceStack var2) {
      int var3 = this.getCommandLimit();
      if (this.isInFunction) {
         if (this.commandQueue.size() + this.nestedCalls.size() < var3) {
            this.nestedCalls.add(new ServerFunctionManager.QueuedCommand(this, var2, new CommandFunction.FunctionEntry(var1)));
         }

         return 0;
      } else {
         int var6;
         try {
            this.isInFunction = true;
            int var4 = 0;
            CommandFunction.Entry[] var5 = var1.getEntries();

            for(var6 = var5.length - 1; var6 >= 0; --var6) {
               this.commandQueue.push(new ServerFunctionManager.QueuedCommand(this, var2, var5[var6]));
            }

            while(!this.commandQueue.isEmpty()) {
               try {
                  ServerFunctionManager.QueuedCommand var15 = (ServerFunctionManager.QueuedCommand)this.commandQueue.removeFirst();
                  this.server.getProfiler().push(var15::toString);
                  var15.execute(this.commandQueue, var3);
                  if (!this.nestedCalls.isEmpty()) {
                     List var10000 = Lists.reverse(this.nestedCalls);
                     ArrayDeque var10001 = this.commandQueue;
                     var10000.forEach(var10001::addFirst);
                     this.nestedCalls.clear();
                  }
               } finally {
                  this.server.getProfiler().pop();
               }

               ++var4;
               if (var4 >= var3) {
                  var6 = var4;
                  return var6;
               }
            }

            var6 = var4;
         } finally {
            this.commandQueue.clear();
            this.nestedCalls.clear();
            this.isInFunction = false;
         }

         return var6;
      }
   }

   public void replaceLibrary(ServerFunctionLibrary var1) {
      this.library = var1;
      this.postReload(var1);
   }

   private void postReload(ServerFunctionLibrary var1) {
      this.ticking.clear();
      this.ticking.addAll(var1.getTags().getTagOrEmpty(TICK_FUNCTION_TAG).getValues());
      this.postReload = true;
   }

   public CommandSourceStack getGameLoopSender() {
      return this.server.createCommandSourceStack().withPermission(2).withSuppressedOutput();
   }

   public Optional<CommandFunction> get(ResourceLocation var1) {
      return this.library.getFunction(var1);
   }

   public Tag<CommandFunction> getTag(ResourceLocation var1) {
      return this.library.getTag(var1);
   }

   public Iterable<ResourceLocation> getFunctionNames() {
      return this.library.getFunctions().keySet();
   }

   public Iterable<ResourceLocation> getTagNames() {
      return this.library.getTags().getAvailableTags();
   }

   public static class QueuedCommand {
      private final ServerFunctionManager manager;
      private final CommandSourceStack sender;
      private final CommandFunction.Entry entry;

      public QueuedCommand(ServerFunctionManager var1, CommandSourceStack var2, CommandFunction.Entry var3) {
         super();
         this.manager = var1;
         this.sender = var2;
         this.entry = var3;
      }

      public void execute(ArrayDeque<ServerFunctionManager.QueuedCommand> var1, int var2) {
         try {
            this.entry.execute(this.manager, this.sender, var1, var2);
         } catch (Throwable var4) {
         }

      }

      public String toString() {
         return this.entry.toString();
      }
   }
}
