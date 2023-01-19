package net.minecraft.world.level;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public abstract class BaseCommandBlock implements CommandSource {
   private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
   private static final Component DEFAULT_NAME = Component.literal("@");
   private long lastExecution = -1L;
   private boolean updateLastExecution = true;
   private int successCount;
   private boolean trackOutput = true;
   @Nullable
   private Component lastOutput;
   private String command = "";
   private Component name = DEFAULT_NAME;

   public BaseCommandBlock() {
      super();
   }

   public int getSuccessCount() {
      return this.successCount;
   }

   public void setSuccessCount(int var1) {
      this.successCount = var1;
   }

   public Component getLastOutput() {
      return this.lastOutput == null ? CommonComponents.EMPTY : this.lastOutput;
   }

   public CompoundTag save(CompoundTag var1) {
      var1.putString("Command", this.command);
      var1.putInt("SuccessCount", this.successCount);
      var1.putString("CustomName", Component.Serializer.toJson(this.name));
      var1.putBoolean("TrackOutput", this.trackOutput);
      if (this.lastOutput != null && this.trackOutput) {
         var1.putString("LastOutput", Component.Serializer.toJson(this.lastOutput));
      }

      var1.putBoolean("UpdateLastExecution", this.updateLastExecution);
      if (this.updateLastExecution && this.lastExecution > 0L) {
         var1.putLong("LastExecution", this.lastExecution);
      }

      return var1;
   }

   public void load(CompoundTag var1) {
      this.command = var1.getString("Command");
      this.successCount = var1.getInt("SuccessCount");
      if (var1.contains("CustomName", 8)) {
         this.setName(Component.Serializer.fromJson(var1.getString("CustomName")));
      }

      if (var1.contains("TrackOutput", 1)) {
         this.trackOutput = var1.getBoolean("TrackOutput");
      }

      if (var1.contains("LastOutput", 8) && this.trackOutput) {
         try {
            this.lastOutput = Component.Serializer.fromJson(var1.getString("LastOutput"));
         } catch (Throwable var3) {
            this.lastOutput = Component.literal(var3.getMessage());
         }
      } else {
         this.lastOutput = null;
      }

      if (var1.contains("UpdateLastExecution")) {
         this.updateLastExecution = var1.getBoolean("UpdateLastExecution");
      }

      if (this.updateLastExecution && var1.contains("LastExecution")) {
         this.lastExecution = var1.getLong("LastExecution");
      } else {
         this.lastExecution = -1L;
      }
   }

   public void setCommand(String var1) {
      this.command = var1;
      this.successCount = 0;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean performCommand(Level var1) {
      if (var1.isClientSide || var1.getGameTime() == this.lastExecution) {
         return false;
      } else if ("Searge".equalsIgnoreCase(this.command)) {
         this.lastOutput = Component.literal("#itzlipofutzli");
         this.successCount = 1;
         return true;
      } else {
         this.successCount = 0;
         MinecraftServer var2 = this.getLevel().getServer();
         if (var2.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
            try {
               this.lastOutput = null;
               CommandSourceStack var3 = this.createCommandSourceStack().withCallback((var1x, var2x, var3x) -> {
                  if (var2x) {
                     ++this.successCount;
                  }
               });
               var2.getCommands().performPrefixedCommand(var3, this.command);
            } catch (Throwable var6) {
               CrashReport var4 = CrashReport.forThrowable(var6, "Executing command block");
               CrashReportCategory var5 = var4.addCategory("Command to be executed");
               var5.setDetail("Command", this::getCommand);
               var5.setDetail("Name", () -> this.getName().getString());
               throw new ReportedException(var4);
            }
         }

         if (this.updateLastExecution) {
            this.lastExecution = var1.getGameTime();
         } else {
            this.lastExecution = -1L;
         }

         return true;
      }
   }

   public Component getName() {
      return this.name;
   }

   public void setName(@Nullable Component var1) {
      if (var1 != null) {
         this.name = var1;
      } else {
         this.name = DEFAULT_NAME;
      }
   }

   @Override
   public void sendSystemMessage(Component var1) {
      if (this.trackOutput) {
         this.lastOutput = Component.literal("[" + TIME_FORMAT.format(new Date()) + "] ").append(var1);
         this.onUpdated();
      }
   }

   public abstract ServerLevel getLevel();

   public abstract void onUpdated();

   public void setLastOutput(@Nullable Component var1) {
      this.lastOutput = var1;
   }

   public void setTrackOutput(boolean var1) {
      this.trackOutput = var1;
   }

   public boolean isTrackOutput() {
      return this.trackOutput;
   }

   public InteractionResult usedBy(Player var1) {
      if (!var1.canUseGameMasterBlocks()) {
         return InteractionResult.PASS;
      } else {
         if (var1.getCommandSenderWorld().isClientSide) {
            var1.openMinecartCommandBlock(this);
         }

         return InteractionResult.sidedSuccess(var1.level.isClientSide);
      }
   }

   public abstract Vec3 getPosition();

   public abstract CommandSourceStack createCommandSourceStack();

   @Override
   public boolean acceptsSuccess() {
      return this.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK) && this.trackOutput;
   }

   @Override
   public boolean acceptsFailure() {
      return this.trackOutput;
   }

   @Override
   public boolean shouldInformAdmins() {
      return this.getLevel().getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
   }
}
