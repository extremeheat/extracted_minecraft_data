package net.minecraft.world.level;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public abstract class BaseCommandBlock {
   private static final Component DEFAULT_NAME = Component.literal("@");
   private static final int NO_LAST_EXECUTION = -1;
   private long lastExecution = -1L;
   private boolean updateLastExecution = true;
   private int successCount;
   private boolean trackOutput = true;
   @Nullable Component lastOutput;
   private String command = "";
   private @Nullable Component customName;

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

   public void save(ValueOutput var1) {
      var1.putString("Command", this.command);
      var1.putInt("SuccessCount", this.successCount);
      var1.storeNullable("CustomName", ComponentSerialization.CODEC, this.customName);
      var1.putBoolean("TrackOutput", this.trackOutput);
      if (this.trackOutput) {
         var1.storeNullable("LastOutput", ComponentSerialization.CODEC, this.lastOutput);
      }

      var1.putBoolean("UpdateLastExecution", this.updateLastExecution);
      if (this.updateLastExecution && this.lastExecution != -1L) {
         var1.putLong("LastExecution", this.lastExecution);
      }

   }

   public void load(ValueInput var1) {
      this.command = var1.getStringOr("Command", "");
      this.successCount = var1.getIntOr("SuccessCount", 0);
      this.setCustomName(BlockEntity.parseCustomNameSafe(var1, "CustomName"));
      this.trackOutput = var1.getBooleanOr("TrackOutput", true);
      if (this.trackOutput) {
         this.lastOutput = BlockEntity.parseCustomNameSafe(var1, "LastOutput");
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

   public boolean performCommand(ServerLevel var1) {
      if (var1.getGameTime() == this.lastExecution) {
         return false;
      } else if ("Searge".equalsIgnoreCase(this.command)) {
         this.lastOutput = Component.literal("#itzlipofutzli");
         this.successCount = 1;
         return true;
      } else {
         this.successCount = 0;
         if (var1.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
            try {
               this.lastOutput = null;

               try (CloseableCommandBlockSource var2 = this.createSource(var1)) {
                  CommandSource var8 = (CommandSource)Objects.requireNonNullElse(var2, CommandSource.NULL);
                  CommandSourceStack var9 = this.createCommandSourceStack(var1, var8).withCallback((var1x, var2x) -> {
                     if (var1x) {
                        ++this.successCount;
                     }

                  });
                  var1.getServer().getCommands().performPrefixedCommand(var9, this.command);
               }
            } catch (Throwable var7) {
               CrashReport var3 = CrashReport.forThrowable(var7, "Executing command block");
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
   }

   private @Nullable CloseableCommandBlockSource createSource(ServerLevel var1) {
      return this.trackOutput ? new CloseableCommandBlockSource(var1) : null;
   }

   public Component getName() {
      return this.customName != null ? this.customName : DEFAULT_NAME;
   }

   public @Nullable Component getCustomName() {
      return this.customName;
   }

   public void setCustomName(@Nullable Component var1) {
      this.customName = var1;
   }

   public abstract void onUpdated(ServerLevel var1);

   public void setLastOutput(@Nullable Component var1) {
      this.lastOutput = var1;
   }

   public void setTrackOutput(boolean var1) {
      this.trackOutput = var1;
   }

   public boolean isTrackOutput() {
      return this.trackOutput;
   }

   public abstract CommandSourceStack createCommandSourceStack(ServerLevel var1, CommandSource var2);

   public abstract boolean isValid();

   protected class CloseableCommandBlockSource implements CommandSource, AutoCloseable {
      private final ServerLevel level;
      private static final DateTimeFormatter TIME_FORMAT;
      private boolean closed;

      protected CloseableCommandBlockSource(final ServerLevel var2) {
         super();
         this.level = var2;
      }

      public boolean acceptsSuccess() {
         return !this.closed && (Boolean)this.level.getGameRules().get(GameRules.SEND_COMMAND_FEEDBACK);
      }

      public boolean acceptsFailure() {
         return !this.closed;
      }

      public boolean shouldInformAdmins() {
         return !this.closed && (Boolean)this.level.getGameRules().get(GameRules.COMMAND_BLOCK_OUTPUT);
      }

      public void sendSystemMessage(Component var1) {
         if (!this.closed) {
            DateTimeFormatter var10001 = TIME_FORMAT;
            BaseCommandBlock.this.lastOutput = Component.literal("[" + var10001.format(ZonedDateTime.now()) + "] ").append(var1);
            BaseCommandBlock.this.onUpdated(this.level);
         }

      }

      public void close() throws Exception {
         this.closed = true;
      }

      static {
         TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ROOT);
      }
   }
}
