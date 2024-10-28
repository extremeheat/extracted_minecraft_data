package net.minecraft.world.level;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
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
   @Nullable
   private Component customName;

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

   public CompoundTag save(CompoundTag var1, HolderLookup.Provider var2) {
      var1.putString("Command", this.command);
      var1.putInt("SuccessCount", this.successCount);
      if (this.customName != null) {
         var1.putString("CustomName", Component.Serializer.toJson(this.customName, var2));
      }

      var1.putBoolean("TrackOutput", this.trackOutput);
      if (this.lastOutput != null && this.trackOutput) {
         var1.putString("LastOutput", Component.Serializer.toJson(this.lastOutput, var2));
      }

      var1.putBoolean("UpdateLastExecution", this.updateLastExecution);
      if (this.updateLastExecution && this.lastExecution > 0L) {
         var1.putLong("LastExecution", this.lastExecution);
      }

      return var1;
   }

   public void load(CompoundTag var1, HolderLookup.Provider var2) {
      this.command = var1.getString("Command");
      this.successCount = var1.getInt("SuccessCount");
      if (var1.contains("CustomName", 8)) {
         this.setCustomName(BlockEntity.parseCustomNameSafe(var1.getString("CustomName"), var2));
      } else {
         this.setCustomName((Component)null);
      }

      if (var1.contains("TrackOutput", 1)) {
         this.trackOutput = var1.getBoolean("TrackOutput");
      }

      if (var1.contains("LastOutput", 8) && this.trackOutput) {
         try {
            this.lastOutput = Component.Serializer.fromJson(var1.getString("LastOutput"), var2);
         } catch (Throwable var4) {
            this.lastOutput = Component.literal(var4.getMessage());
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
      if (!var1.isClientSide && var1.getGameTime() != this.lastExecution) {
         if ("Searge".equalsIgnoreCase(this.command)) {
            this.lastOutput = Component.literal("#itzlipofutzli");
            this.successCount = 1;
            return true;
         } else {
            this.successCount = 0;
            MinecraftServer var2 = this.getLevel().getServer();
            if (var2.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
               try {
                  this.lastOutput = null;
                  CommandSourceStack var3 = this.createCommandSourceStack().withCallback((var1x, var2x) -> {
                     if (var1x) {
                        ++this.successCount;
                     }

                  });
                  var2.getCommands().performPrefixedCommand(var3, this.command);
               } catch (Throwable var6) {
                  CrashReport var4 = CrashReport.forThrowable(var6, "Executing command block");
                  CrashReportCategory var5 = var4.addCategory("Command to be executed");
                  var5.setDetail("Command", this::getCommand);
                  var5.setDetail("Name", () -> {
                     return this.getName().getString();
                  });
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
      } else {
         return false;
      }
   }

   public Component getName() {
      return this.customName != null ? this.customName : DEFAULT_NAME;
   }

   @Nullable
   public Component getCustomName() {
      return this.customName;
   }

   public void setCustomName(@Nullable Component var1) {
      this.customName = var1;
   }

   public void sendSystemMessage(Component var1) {
      if (this.trackOutput) {
         SimpleDateFormat var10001 = TIME_FORMAT;
         Date var10002 = new Date();
         this.lastOutput = Component.literal("[" + var10001.format(var10002) + "] ").append(var1);
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

         return InteractionResult.sidedSuccess(var1.level().isClientSide);
      }
   }

   public abstract Vec3 getPosition();

   public abstract CommandSourceStack createCommandSourceStack();

   public boolean acceptsSuccess() {
      return this.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK) && this.trackOutput;
   }

   public boolean acceptsFailure() {
      return this.trackOutput;
   }

   public boolean shouldInformAdmins() {
      return this.getLevel().getGameRules().getBoolean(GameRules.RULE_COMMANDBLOCKOUTPUT);
   }

   public abstract boolean isValid();
}
