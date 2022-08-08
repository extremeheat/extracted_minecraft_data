package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.GameRules;

public class ServerFunctionManager {
   private static final Component NO_RECURSIVE_TRACES = Component.translatable("commands.debug.function.noRecursion");
   private static final ResourceLocation TICK_FUNCTION_TAG = new ResourceLocation("tick");
   private static final ResourceLocation LOAD_FUNCTION_TAG = new ResourceLocation("load");
   final MinecraftServer server;
   @Nullable
   private ExecutionContext context;
   private List<CommandFunction> ticking = ImmutableList.of();
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
         Collection var1 = this.library.getTag(LOAD_FUNCTION_TAG);
         this.executeTagFunctions(var1, LOAD_FUNCTION_TAG);
      }

   }

   private void executeTagFunctions(Collection<CommandFunction> var1, ResourceLocation var2) {
      ProfilerFiller var10000 = this.server.getProfiler();
      Objects.requireNonNull(var2);
      var10000.push(var2::toString);
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         CommandFunction var4 = (CommandFunction)var3.next();
         this.execute(var4, this.getGameLoopSender());
      }

      this.server.getProfiler().pop();
   }

   public int execute(CommandFunction var1, CommandSourceStack var2) {
      return this.execute(var1, var2, (TraceCallbacks)null);
   }

   public int execute(CommandFunction var1, CommandSourceStack var2, @Nullable TraceCallbacks var3) {
      if (this.context != null) {
         if (var3 != null) {
            this.context.reportError(NO_RECURSIVE_TRACES.getString());
            return 0;
         } else {
            this.context.delayFunctionCall(var1, var2);
            return 0;
         }
      } else {
         int var4;
         try {
            this.context = new ExecutionContext(var3);
            var4 = this.context.runTopCommand(var1, var2);
         } finally {
            this.context = null;
         }

         return var4;
      }
   }

   public void replaceLibrary(ServerFunctionLibrary var1) {
      this.library = var1;
      this.postReload(var1);
   }

   private void postReload(ServerFunctionLibrary var1) {
      this.ticking = ImmutableList.copyOf(var1.getTag(TICK_FUNCTION_TAG));
      this.postReload = true;
   }

   public CommandSourceStack getGameLoopSender() {
      return this.server.createCommandSourceStack().withPermission(2).withSuppressedOutput();
   }

   public Optional<CommandFunction> get(ResourceLocation var1) {
      return this.library.getFunction(var1);
   }

   public Collection<CommandFunction> getTag(ResourceLocation var1) {
      return this.library.getTag(var1);
   }

   public Iterable<ResourceLocation> getFunctionNames() {
      return this.library.getFunctions().keySet();
   }

   public Iterable<ResourceLocation> getTagNames() {
      return this.library.getAvailableTags();
   }

   public interface TraceCallbacks {
      void onCommand(int var1, String var2);

      void onReturn(int var1, String var2, int var3);

      void onError(int var1, String var2);

      void onCall(int var1, ResourceLocation var2, int var3);
   }

   private class ExecutionContext {
      private int depth;
      @Nullable
      private final TraceCallbacks tracer;
      private final Deque<QueuedCommand> commandQueue = Queues.newArrayDeque();
      private final List<QueuedCommand> nestedCalls = Lists.newArrayList();

      ExecutionContext(@Nullable TraceCallbacks var2) {
         super();
         this.tracer = var2;
      }

      void delayFunctionCall(CommandFunction var1, CommandSourceStack var2) {
         int var3 = ServerFunctionManager.this.getCommandLimit();
         if (this.commandQueue.size() + this.nestedCalls.size() < var3) {
            this.nestedCalls.add(new QueuedCommand(var2, this.depth, new CommandFunction.FunctionEntry(var1)));
         }

      }

      int runTopCommand(CommandFunction var1, CommandSourceStack var2) {
         int var3 = ServerFunctionManager.this.getCommandLimit();
         int var4 = 0;
         CommandFunction.Entry[] var5 = var1.getEntries();

         for(int var6 = var5.length - 1; var6 >= 0; --var6) {
            this.commandQueue.push(new QueuedCommand(var2, 0, var5[var6]));
         }

         do {
            if (this.commandQueue.isEmpty()) {
               return var4;
            }

            try {
               QueuedCommand var11 = (QueuedCommand)this.commandQueue.removeFirst();
               ProfilerFiller var10000 = ServerFunctionManager.this.server.getProfiler();
               Objects.requireNonNull(var11);
               var10000.push(var11::toString);
               this.depth = var11.depth;
               var11.execute(ServerFunctionManager.this, this.commandQueue, var3, this.tracer);
               if (!this.nestedCalls.isEmpty()) {
                  List var10 = Lists.reverse(this.nestedCalls);
                  Deque var10001 = this.commandQueue;
                  Objects.requireNonNull(var10001);
                  var10.forEach(var10001::addFirst);
                  this.nestedCalls.clear();
               }
            } finally {
               ServerFunctionManager.this.server.getProfiler().pop();
            }

            ++var4;
         } while(var4 < var3);

         return var4;
      }

      public void reportError(String var1) {
         if (this.tracer != null) {
            this.tracer.onError(this.depth, var1);
         }

      }
   }

   public static class QueuedCommand {
      private final CommandSourceStack sender;
      final int depth;
      private final CommandFunction.Entry entry;

      public QueuedCommand(CommandSourceStack var1, int var2, CommandFunction.Entry var3) {
         super();
         this.sender = var1;
         this.depth = var2;
         this.entry = var3;
      }

      public void execute(ServerFunctionManager var1, Deque<QueuedCommand> var2, int var3, @Nullable TraceCallbacks var4) {
         try {
            this.entry.execute(var1, this.sender, var2, var3, this.depth, var4);
         } catch (CommandSyntaxException var6) {
            if (var4 != null) {
               var4.onError(this.depth, var6.getRawMessage().getString());
            }
         } catch (Exception var7) {
            if (var4 != null) {
               var4.onError(this.depth, var7.getMessage());
            }
         }

      }

      public String toString() {
         return this.entry.toString();
      }
   }
}
