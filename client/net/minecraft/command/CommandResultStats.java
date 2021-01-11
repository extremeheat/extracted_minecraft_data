package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandResultStats {
   private static final int field_179676_a = CommandResultStats.Type.values().length;
   private static final String[] field_179674_b;
   private String[] field_179675_c;
   private String[] field_179673_d;

   public CommandResultStats() {
      super();
      this.field_179675_c = field_179674_b;
      this.field_179673_d = field_179674_b;
   }

   public void func_179672_a(final ICommandSender var1, CommandResultStats.Type var2, int var3) {
      String var4 = this.field_179675_c[var2.func_179636_a()];
      if (var4 != null) {
         ICommandSender var5 = new ICommandSender() {
            public String func_70005_c_() {
               return var1.func_70005_c_();
            }

            public IChatComponent func_145748_c_() {
               return var1.func_145748_c_();
            }

            public void func_145747_a(IChatComponent var1x) {
               var1.func_145747_a(var1x);
            }

            public boolean func_70003_b(int var1x, String var2) {
               return true;
            }

            public BlockPos func_180425_c() {
               return var1.func_180425_c();
            }

            public Vec3 func_174791_d() {
               return var1.func_174791_d();
            }

            public World func_130014_f_() {
               return var1.func_130014_f_();
            }

            public Entity func_174793_f() {
               return var1.func_174793_f();
            }

            public boolean func_174792_t_() {
               return var1.func_174792_t_();
            }

            public void func_174794_a(CommandResultStats.Type var1x, int var2) {
               var1.func_174794_a(var1x, var2);
            }
         };

         String var6;
         try {
            var6 = CommandBase.func_175758_e(var5, var4);
         } catch (EntityNotFoundException var11) {
            return;
         }

         String var7 = this.field_179673_d[var2.func_179636_a()];
         if (var7 != null) {
            Scoreboard var8 = var1.func_130014_f_().func_96441_U();
            ScoreObjective var9 = var8.func_96518_b(var7);
            if (var9 != null) {
               if (var8.func_178819_b(var6, var9)) {
                  Score var10 = var8.func_96529_a(var6, var9);
                  var10.func_96647_c(var3);
               }
            }
         }
      }
   }

   public void func_179668_a(NBTTagCompound var1) {
      if (var1.func_150297_b("CommandStats", 10)) {
         NBTTagCompound var2 = var1.func_74775_l("CommandStats");
         CommandResultStats.Type[] var3 = CommandResultStats.Type.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            CommandResultStats.Type var6 = var3[var5];
            String var7 = var6.func_179637_b() + "Name";
            String var8 = var6.func_179637_b() + "Objective";
            if (var2.func_150297_b(var7, 8) && var2.func_150297_b(var8, 8)) {
               String var9 = var2.func_74779_i(var7);
               String var10 = var2.func_74779_i(var8);
               func_179667_a(this, var6, var9, var10);
            }
         }

      }
   }

   public void func_179670_b(NBTTagCompound var1) {
      NBTTagCompound var2 = new NBTTagCompound();
      CommandResultStats.Type[] var3 = CommandResultStats.Type.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         CommandResultStats.Type var6 = var3[var5];
         String var7 = this.field_179675_c[var6.func_179636_a()];
         String var8 = this.field_179673_d[var6.func_179636_a()];
         if (var7 != null && var8 != null) {
            var2.func_74778_a(var6.func_179637_b() + "Name", var7);
            var2.func_74778_a(var6.func_179637_b() + "Objective", var8);
         }
      }

      if (!var2.func_82582_d()) {
         var1.func_74782_a("CommandStats", var2);
      }

   }

   public static void func_179667_a(CommandResultStats var0, CommandResultStats.Type var1, String var2, String var3) {
      if (var2 != null && var2.length() != 0 && var3 != null && var3.length() != 0) {
         if (var0.field_179675_c == field_179674_b || var0.field_179673_d == field_179674_b) {
            var0.field_179675_c = new String[field_179676_a];
            var0.field_179673_d = new String[field_179676_a];
         }

         var0.field_179675_c[var1.func_179636_a()] = var2;
         var0.field_179673_d[var1.func_179636_a()] = var3;
      } else {
         func_179669_a(var0, var1);
      }
   }

   private static void func_179669_a(CommandResultStats var0, CommandResultStats.Type var1) {
      if (var0.field_179675_c != field_179674_b && var0.field_179673_d != field_179674_b) {
         var0.field_179675_c[var1.func_179636_a()] = null;
         var0.field_179673_d[var1.func_179636_a()] = null;
         boolean var2 = true;
         CommandResultStats.Type[] var3 = CommandResultStats.Type.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            CommandResultStats.Type var6 = var3[var5];
            if (var0.field_179675_c[var6.func_179636_a()] != null && var0.field_179673_d[var6.func_179636_a()] != null) {
               var2 = false;
               break;
            }
         }

         if (var2) {
            var0.field_179675_c = field_179674_b;
            var0.field_179673_d = field_179674_b;
         }

      }
   }

   public void func_179671_a(CommandResultStats var1) {
      CommandResultStats.Type[] var2 = CommandResultStats.Type.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         CommandResultStats.Type var5 = var2[var4];
         func_179667_a(this, var5, var1.field_179675_c[var5.func_179636_a()], var1.field_179673_d[var5.func_179636_a()]);
      }

   }

   static {
      field_179674_b = new String[field_179676_a];
   }

   public static enum Type {
      SUCCESS_COUNT(0, "SuccessCount"),
      AFFECTED_BLOCKS(1, "AffectedBlocks"),
      AFFECTED_ENTITIES(2, "AffectedEntities"),
      AFFECTED_ITEMS(3, "AffectedItems"),
      QUERY_RESULT(4, "QueryResult");

      final int field_179639_f;
      final String field_179640_g;

      private Type(int var3, String var4) {
         this.field_179639_f = var3;
         this.field_179640_g = var4;
      }

      public int func_179636_a() {
         return this.field_179639_f;
      }

      public String func_179637_b() {
         return this.field_179640_g;
      }

      public static String[] func_179634_c() {
         String[] var0 = new String[values().length];
         int var1 = 0;
         CommandResultStats.Type[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            CommandResultStats.Type var5 = var2[var4];
            var0[var1++] = var5.func_179637_b();
         }

         return var0;
      }

      public static CommandResultStats.Type func_179635_a(String var0) {
         CommandResultStats.Type[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            CommandResultStats.Type var4 = var1[var3];
            if (var4.func_179637_b().equals(var0)) {
               return var4;
            }
         }

         return null;
      }
   }
}
