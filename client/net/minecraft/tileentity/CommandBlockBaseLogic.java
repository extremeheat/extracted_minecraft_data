package net.minecraft.tileentity;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class CommandBlockBaseLogic implements ICommandSource {
   private static final SimpleDateFormat field_145766_a = new SimpleDateFormat("HH:mm:ss");
   private long field_193041_b = -1L;
   private boolean field_193042_c = true;
   private int field_145764_b;
   private boolean field_145765_c = true;
   private ITextComponent field_145762_d;
   private String field_145763_e = "";
   private ITextComponent field_145761_f = new TextComponentString("@");

   public CommandBlockBaseLogic() {
      super();
   }

   public int func_145760_g() {
      return this.field_145764_b;
   }

   public void func_184167_a(int var1) {
      this.field_145764_b = var1;
   }

   public ITextComponent func_145749_h() {
      return (ITextComponent)(this.field_145762_d == null ? new TextComponentString("") : this.field_145762_d);
   }

   public NBTTagCompound func_189510_a(NBTTagCompound var1) {
      var1.func_74778_a("Command", this.field_145763_e);
      var1.func_74768_a("SuccessCount", this.field_145764_b);
      var1.func_74778_a("CustomName", ITextComponent.Serializer.func_150696_a(this.field_145761_f));
      var1.func_74757_a("TrackOutput", this.field_145765_c);
      if (this.field_145762_d != null && this.field_145765_c) {
         var1.func_74778_a("LastOutput", ITextComponent.Serializer.func_150696_a(this.field_145762_d));
      }

      var1.func_74757_a("UpdateLastExecution", this.field_193042_c);
      if (this.field_193042_c && this.field_193041_b > 0L) {
         var1.func_74772_a("LastExecution", this.field_193041_b);
      }

      return var1;
   }

   public void func_145759_b(NBTTagCompound var1) {
      this.field_145763_e = var1.func_74779_i("Command");
      this.field_145764_b = var1.func_74762_e("SuccessCount");
      if (var1.func_150297_b("CustomName", 8)) {
         this.field_145761_f = ITextComponent.Serializer.func_150699_a(var1.func_74779_i("CustomName"));
      }

      if (var1.func_150297_b("TrackOutput", 1)) {
         this.field_145765_c = var1.func_74767_n("TrackOutput");
      }

      if (var1.func_150297_b("LastOutput", 8) && this.field_145765_c) {
         try {
            this.field_145762_d = ITextComponent.Serializer.func_150699_a(var1.func_74779_i("LastOutput"));
         } catch (Throwable var3) {
            this.field_145762_d = new TextComponentString(var3.getMessage());
         }
      } else {
         this.field_145762_d = null;
      }

      if (var1.func_74764_b("UpdateLastExecution")) {
         this.field_193042_c = var1.func_74767_n("UpdateLastExecution");
      }

      if (this.field_193042_c && var1.func_74764_b("LastExecution")) {
         this.field_193041_b = var1.func_74763_f("LastExecution");
      } else {
         this.field_193041_b = -1L;
      }

   }

   public void func_145752_a(String var1) {
      this.field_145763_e = var1;
      this.field_145764_b = 0;
   }

   public String func_145753_i() {
      return this.field_145763_e;
   }

   public boolean func_145755_a(World var1) {
      if (!var1.field_72995_K && var1.func_82737_E() != this.field_193041_b) {
         if ("Searge".equalsIgnoreCase(this.field_145763_e)) {
            this.field_145762_d = new TextComponentString("#itzlipofutzli");
            this.field_145764_b = 1;
            return true;
         } else {
            this.field_145764_b = 0;
            MinecraftServer var2 = this.func_195043_d().func_73046_m();
            if (var2 != null && var2.func_175578_N() && var2.func_82356_Z() && !StringUtils.func_151246_b(this.field_145763_e)) {
               try {
                  this.field_145762_d = null;
                  CommandSource var3 = this.func_195042_h().func_197029_a((var1x, var2x, var3x) -> {
                     if (var2x) {
                        ++this.field_145764_b;
                     }

                  });
                  var2.func_195571_aL().func_197059_a(var3, this.field_145763_e);
               } catch (Throwable var6) {
                  CrashReport var4 = CrashReport.func_85055_a(var6, "Executing command block");
                  CrashReportCategory var5 = var4.func_85058_a("Command to be executed");
                  var5.func_189529_a("Command", this::func_145753_i);
                  var5.func_189529_a("Name", () -> {
                     return this.func_207404_l().getString();
                  });
                  throw new ReportedException(var4);
               }
            }

            if (this.field_193042_c) {
               this.field_193041_b = var1.func_82737_E();
            } else {
               this.field_193041_b = -1L;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public ITextComponent func_207404_l() {
      return this.field_145761_f;
   }

   public void func_207405_b(ITextComponent var1) {
      this.field_145761_f = var1;
   }

   public void func_145747_a(ITextComponent var1) {
      if (this.field_145765_c) {
         this.field_145762_d = (new TextComponentString("[" + field_145766_a.format(new Date()) + "] ")).func_150257_a(var1);
         this.func_145756_e();
      }

   }

   public abstract WorldServer func_195043_d();

   public abstract void func_145756_e();

   public void func_145750_b(@Nullable ITextComponent var1) {
      this.field_145762_d = var1;
   }

   public void func_175573_a(boolean var1) {
      this.field_145765_c = var1;
   }

   public boolean func_175571_m() {
      return this.field_145765_c;
   }

   public boolean func_175574_a(EntityPlayer var1) {
      if (!var1.func_195070_dx()) {
         return false;
      } else {
         if (var1.func_130014_f_().field_72995_K) {
            var1.func_184809_a(this);
         }

         return true;
      }
   }

   public abstract Vec3d func_210165_f();

   public abstract CommandSource func_195042_h();

   public boolean func_195039_a() {
      return this.func_195043_d().func_82736_K().func_82766_b("sendCommandFeedback") && this.field_145765_c;
   }

   public boolean func_195040_b() {
      return this.field_145765_c;
   }

   public boolean func_195041_r_() {
      return this.func_195043_d().func_82736_K().func_82766_b("commandBlockOutput");
   }
}
