package net.minecraft.world.level;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public abstract class BaseCommandBlock implements CommandSource {
   private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
   private static final Component DEFAULT_NAME = Component.literal("@");
   private static final int NO_LAST_EXECUTION = -1;
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
      RegistryOps var3 = var2.createSerializationContext(NbtOps.INSTANCE);
      var1.storeNullable("CustomName", ComponentSerialization.CODEC, var3, this.customName);
      var1.putBoolean("TrackOutput", this.trackOutput);
      if (this.trackOutput) {
         var1.storeNullable("LastOutput", ComponentSerialization.CODEC, var3, this.lastOutput);
      }

      var1.putBoolean("UpdateLastExecution", this.updateLastExecution);
      if (this.updateLastExecution && this.lastExecution != -1L) {
         var1.putLong("LastExecution", this.lastExecution);
      }

      return var1;
   }

   public void load(CompoundTag var1, HolderLookup.Provider var2) {
      this.command = var1.getStringOr("Command", "");
      this.successCount = var1.getIntOr("SuccessCount", 0);
      this.setCustomName(BlockEntity.parseCustomNameSafe(var1.get("CustomName"), var2));
      this.trackOutput = var1.getBooleanOr("TrackOutput", true);
      if (this.trackOutput) {
         this.lastOutput = BlockEntity.parseCustomNameSafe(var1.get("LastOutput"), var2);
      } else {
         this.lastOutput = null;
      }

      this.updateLastExecution = var1.getBooleanOr("UpdateLastExecution", true);
      if (this.updateLastExecution) {
         this.lastExecution = var1.getLongOr("LastExecution", -1L);
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
            if (this.getLevel().theGame().server().isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
               try {
                  this.lastOutput = null;
                  CommandSourceStack var2 = this.createCommandSourceStack().withCallback((var1x, var2x) -> {
                     if (var1x) {
                        ++this.successCount;
                     }

                  });
                  this.getLevel().theGame().getCommands().performPrefixedCommand(var2, this.command);
               } catch (Throwable var5) {
                  CrashReport var3 = CrashReport.forThrowable(var5, "Executing command block");
                  CrashReportCategory var4 = var3.addCategory("Command to be executed");
                  var4.setDetail("Command", this::getCommand);
                  var4.setDetail("Name", (CrashReportDetail)(() -> this.getName().getString()));
                  throw new ReportedException(var3);
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

         return InteractionResult.SUCCESS;
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
