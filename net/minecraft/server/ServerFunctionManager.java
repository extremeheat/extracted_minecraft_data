package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.SimpleResource;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.profiling.GameProfiler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerFunctionManager implements ResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation TICK_FUNCTION_TAG = new ResourceLocation("tick");
   private static final ResourceLocation LOAD_FUNCTION_TAG = new ResourceLocation("load");
   public static final int PATH_PREFIX_LENGTH = "functions/".length();
   public static final int PATH_SUFFIX_LENGTH = ".mcfunction".length();
   private final MinecraftServer server;
   private final Map functions = Maps.newHashMap();
   private boolean isInFunction;
   private final ArrayDeque commandQueue = new ArrayDeque();
   private final List nestedCalls = Lists.newArrayList();
   private final TagCollection tags = new TagCollection(this::get, "tags/functions", true, "function");
   private final List ticking = Lists.newArrayList();
   private boolean postReload;

   public ServerFunctionManager(MinecraftServer var1) {
      this.server = var1;
   }

   public Optional get(ResourceLocation var1) {
      return Optional.ofNullable(this.functions.get(var1));
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public int getCommandLimit() {
      return this.server.getGameRules().getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
   }

   public Map getFunctions() {
      return this.functions;
   }

   public CommandDispatcher getDispatcher() {
      return this.server.getCommands().getDispatcher();
   }

   public void tick() {
      GameProfiler var10000 = this.server.getProfiler();
      ResourceLocation var10001 = TICK_FUNCTION_TAG;
      var10000.push(var10001::toString);
      Iterator var1 = this.ticking.iterator();

      while(var1.hasNext()) {
         CommandFunction var2 = (CommandFunction)var1.next();
         this.execute(var2, this.getGameLoopSender());
      }

      this.server.getProfiler().pop();
      if (this.postReload) {
         this.postReload = false;
         Collection var4 = this.getTags().getTagOrEmpty(LOAD_FUNCTION_TAG).getValues();
         var10000 = this.server.getProfiler();
         var10001 = LOAD_FUNCTION_TAG;
         var10000.push(var10001::toString);
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            CommandFunction var3 = (CommandFunction)var5.next();
            this.execute(var3, this.getGameLoopSender());
         }

         this.server.getProfiler().pop();
      }

   }

   public int execute(CommandFunction var1, CommandSourceStack var2) {
      int var3 = this.getCommandLimit();
      if (this.isInFunction) {
         if (this.commandQueue.size() + this.nestedCalls.size() < var3) {
            this.nestedCalls.add(new ServerFunctionManager.QueuedCommand(this, var2, new CommandFunction.FunctionEntry(var1)));
         }

         return 0;
      } else {
         try {
            this.isInFunction = true;
            int var4 = 0;
            CommandFunction.Entry[] var5 = var1.getEntries();

            int var6;
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
            return var6;
         } finally {
            this.commandQueue.clear();
            this.nestedCalls.clear();
            this.isInFunction = false;
         }
      }
   }

   public void onResourceManagerReload(ResourceManager var1) {
      this.functions.clear();
      this.ticking.clear();
      Collection var2 = var1.listResources("functions", (var0) -> {
         return var0.endsWith(".mcfunction");
      });
      ArrayList var3 = Lists.newArrayList();
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         ResourceLocation var5 = (ResourceLocation)var4.next();
         String var6 = var5.getPath();
         ResourceLocation var7 = new ResourceLocation(var5.getNamespace(), var6.substring(PATH_PREFIX_LENGTH, var6.length() - PATH_SUFFIX_LENGTH));
         var3.add(CompletableFuture.supplyAsync(() -> {
            return readLinesAsync(var1, var5);
         }, SimpleResource.IO_EXECUTOR).thenApplyAsync((var2x) -> {
            return CommandFunction.fromLines(var7, this, var2x);
         }, this.server.getBackgroundTaskExecutor()).handle((var2x, var3x) -> {
            return this.addFunction(var2x, var3x, var5);
         }));
      }

      CompletableFuture.allOf((CompletableFuture[])var3.toArray(new CompletableFuture[0])).join();
      if (!this.functions.isEmpty()) {
         LOGGER.info("Loaded {} custom command functions", this.functions.size());
      }

      this.tags.load((Map)this.tags.prepare(var1, this.server.getBackgroundTaskExecutor()).join());
      this.ticking.addAll(this.tags.getTagOrEmpty(TICK_FUNCTION_TAG).getValues());
      this.postReload = true;
   }

   @Nullable
   private CommandFunction addFunction(CommandFunction var1, @Nullable Throwable var2, ResourceLocation var3) {
      if (var2 != null) {
         LOGGER.error("Couldn't load function at {}", var3, var2);
         return null;
      } else {
         synchronized(this.functions) {
            this.functions.put(var1.getId(), var1);
            return var1;
         }
      }
   }

   private static List readLinesAsync(ResourceManager var0, ResourceLocation var1) {
      try {
         Resource var2 = var0.getResource(var1);
         Throwable var3 = null;

         List var4;
         try {
            var4 = IOUtils.readLines(var2.getInputStream(), StandardCharsets.UTF_8);
         } catch (Throwable var14) {
            var3 = var14;
            throw var14;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var13) {
                     var3.addSuppressed(var13);
                  }
               } else {
                  var2.close();
               }
            }

         }

         return var4;
      } catch (IOException var16) {
         throw new CompletionException(var16);
      }
   }

   public CommandSourceStack getGameLoopSender() {
      return this.server.createCommandSourceStack().withPermission(2).withSuppressedOutput();
   }

   public CommandSourceStack getCompilationContext() {
      return new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, (ServerLevel)null, this.server.getFunctionCompilationLevel(), "", new TextComponent(""), this.server, (Entity)null);
   }

   public TagCollection getTags() {
      return this.tags;
   }

   public static class QueuedCommand {
      private final ServerFunctionManager manager;
      private final CommandSourceStack sender;
      private final CommandFunction.Entry entry;

      public QueuedCommand(ServerFunctionManager var1, CommandSourceStack var2, CommandFunction.Entry var3) {
         this.manager = var1;
         this.sender = var2;
         this.entry = var3;
      }

      public void execute(ArrayDeque var1, int var2) {
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
