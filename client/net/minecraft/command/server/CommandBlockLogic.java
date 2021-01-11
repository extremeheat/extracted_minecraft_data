package net.minecraft.command.server;

import io.netty.buffer.ByteBuf;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

public abstract class CommandBlockLogic implements ICommandSender {
   private static final SimpleDateFormat field_145766_a = new SimpleDateFormat("HH:mm:ss");
   private int field_145764_b;
   private boolean field_145765_c = true;
   private IChatComponent field_145762_d = null;
   private String field_145763_e = "";
   private String field_145761_f = "@";
   private final CommandResultStats field_175575_g = new CommandResultStats();

   public CommandBlockLogic() {
      super();
   }

   public int func_145760_g() {
      return this.field_145764_b;
   }

   public IChatComponent func_145749_h() {
      return this.field_145762_d;
   }

   public void func_145758_a(NBTTagCompound var1) {
      var1.func_74778_a("Command", this.field_145763_e);
      var1.func_74768_a("SuccessCount", this.field_145764_b);
      var1.func_74778_a("CustomName", this.field_145761_f);
      var1.func_74757_a("TrackOutput", this.field_145765_c);
      if (this.field_145762_d != null && this.field_145765_c) {
         var1.func_74778_a("LastOutput", IChatComponent.Serializer.func_150696_a(this.field_145762_d));
      }

      this.field_175575_g.func_179670_b(var1);
   }

   public void func_145759_b(NBTTagCompound var1) {
      this.field_145763_e = var1.func_74779_i("Command");
      this.field_145764_b = var1.func_74762_e("SuccessCount");
      if (var1.func_150297_b("CustomName", 8)) {
         this.field_145761_f = var1.func_74779_i("CustomName");
      }

      if (var1.func_150297_b("TrackOutput", 1)) {
         this.field_145765_c = var1.func_74767_n("TrackOutput");
      }

      if (var1.func_150297_b("LastOutput", 8) && this.field_145765_c) {
         this.field_145762_d = IChatComponent.Serializer.func_150699_a(var1.func_74779_i("LastOutput"));
      }

      this.field_175575_g.func_179668_a(var1);
   }

   public boolean func_70003_b(int var1, String var2) {
      return var1 <= 2;
   }

   public void func_145752_a(String var1) {
      this.field_145763_e = var1;
      this.field_145764_b = 0;
   }

   public String func_145753_i() {
      return this.field_145763_e;
   }

   public void func_145755_a(World var1) {
      if (var1.field_72995_K) {
         this.field_145764_b = 0;
      }

      MinecraftServer var2 = MinecraftServer.func_71276_C();
      if (var2 != null && var2.func_175578_N() && var2.func_82356_Z()) {
         ICommandManager var3 = var2.func_71187_D();

         try {
            this.field_145762_d = null;
            this.field_145764_b = var3.func_71556_a(this, this.field_145763_e);
         } catch (Throwable var7) {
            CrashReport var5 = CrashReport.func_85055_a(var7, "Executing command block");
            CrashReportCategory var6 = var5.func_85058_a("Command to be executed");
            var6.func_71500_a("Command", new Callable<String>() {
               public String call() throws Exception {
                  return CommandBlockLogic.this.func_145753_i();
               }

               // $FF: synthetic method
               public Object call() throws Exception {
                  return this.call();
               }
            });
            var6.func_71500_a("Name", new Callable<String>() {
               public String call() throws Exception {
                  return CommandBlockLogic.this.func_70005_c_();
               }

               // $FF: synthetic method
               public Object call() throws Exception {
                  return this.call();
               }
            });
            throw new ReportedException(var5);
         }
      } else {
         this.field_145764_b = 0;
      }

   }

   public String func_70005_c_() {
      return this.field_145761_f;
   }

   public IChatComponent func_145748_c_() {
      return new ChatComponentText(this.func_70005_c_());
   }

   public void func_145754_b(String var1) {
      this.field_145761_f = var1;
   }

   public void func_145747_a(IChatComponent var1) {
      if (this.field_145765_c && this.func_130014_f_() != null && !this.func_130014_f_().field_72995_K) {
         this.field_145762_d = (new ChatComponentText("[" + field_145766_a.format(new Date()) + "] ")).func_150257_a(var1);
         this.func_145756_e();
      }

   }

   public boolean func_174792_t_() {
      MinecraftServer var1 = MinecraftServer.func_71276_C();
      return var1 == null || !var1.func_175578_N() || var1.field_71305_c[0].func_82736_K().func_82766_b("commandBlockOutput");
   }

   public void func_174794_a(CommandResultStats.Type var1, int var2) {
      this.field_175575_g.func_179672_a(this, var1, var2);
   }

   public abstract void func_145756_e();

   public abstract int func_145751_f();

   public abstract void func_145757_a(ByteBuf var1);

   public void func_145750_b(IChatComponent var1) {
      this.field_145762_d = var1;
   }

   public void func_175573_a(boolean var1) {
      this.field_145765_c = var1;
   }

   public boolean func_175571_m() {
      return this.field_145765_c;
   }

   public boolean func_175574_a(EntityPlayer var1) {
      if (!var1.field_71075_bZ.field_75098_d) {
         return false;
      } else {
         if (var1.func_130014_f_().field_72995_K) {
            var1.func_146095_a(this);
         }

         return true;
      }
   }

   public CommandResultStats func_175572_n() {
      return this.field_175575_g;
   }
}
