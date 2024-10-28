package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class ServerFunctionManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation TICK_FUNCTION_TAG = ResourceLocation.withDefaultNamespace("tick");
   private static final ResourceLocation LOAD_FUNCTION_TAG = ResourceLocation.withDefaultNamespace("load");
   private final MinecraftServer server;
   private List<CommandFunction<CommandSourceStack>> ticking = ImmutableList.of();
   private boolean postReload;
   private ServerFunctionLibrary library;

   public ServerFunctionManager(MinecraftServer var1, ServerFunctionLibrary var2) {
      super();
      this.server = var1;
      this.library = var2;
      this.postReload(var2);
   }

   public CommandDispatcher<CommandSourceStack> getDispatcher() {
      return this.server.getCommands().getDispatcher();
   }

   public void tick() {
      if (this.server.tickRateManager().runsNormally()) {
         if (this.postReload) {
            this.postReload = false;
            Collection var1 = this.library.getTag(LOAD_FUNCTION_TAG);
            this.executeTagFunctions(var1, LOAD_FUNCTION_TAG);
         }

         this.executeTagFunctions(this.ticking, TICK_FUNCTION_TAG);
      }
   }

   private void executeTagFunctions(Collection<CommandFunction<CommandSourceStack>> var1, ResourceLocation var2) {
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

   public void execute(CommandFunction<CommandSourceStack> var1, CommandSourceStack var2) {
      ProfilerFiller var3 = this.server.getProfiler();
      var3.push(() -> {
         return "function " + String.valueOf(var1.id());
      });

      try {
         InstantiatedFunction var4 = var1.instantiate((CompoundTag)null, this.getDispatcher());
         Commands.executeCommandInContext(var2, (var2x) -> {
            ExecutionContext.queueInitialFunctionCall(var2x, var4, var2, CommandResultCallback.EMPTY);
         });
      } catch (FunctionInstantiationException var9) {
      } catch (Exception var10) {
         LOGGER.warn("Failed to execute function {}", var1.id(), var10);
      } finally {
         var3.pop();
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

   public Optional<CommandFunction<CommandSourceStack>> get(ResourceLocation var1) {
      return this.library.getFunction(var1);
   }

   public Collection<CommandFunction<CommandSourceStack>> getTag(ResourceLocation var1) {
      return this.library.getTag(var1);
   }

   public Iterable<ResourceLocation> getFunctionNames() {
      return this.library.getFunctions().keySet();
   }

   public Iterable<ResourceLocation> getTagNames() {
      return this.library.getAvailableTags();
   }
}
