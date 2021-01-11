package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandHandler implements ICommandManager {
   private static final Logger field_147175_a = LogManager.getLogger();
   private final Map<String, ICommand> field_71562_a = Maps.newHashMap();
   private final Set<ICommand> field_71561_b = Sets.newHashSet();

   public CommandHandler() {
      super();
   }

   public int func_71556_a(ICommandSender var1, String var2) {
      var2 = var2.trim();
      if (var2.startsWith("/")) {
         var2 = var2.substring(1);
      }

      String[] var3 = var2.split(" ");
      String var4 = var3[0];
      var3 = func_71559_a(var3);
      ICommand var5 = (ICommand)this.field_71562_a.get(var4);
      int var6 = this.func_82370_a(var5, var3);
      int var7 = 0;
      ChatComponentTranslation var8;
      if (var5 == null) {
         var8 = new ChatComponentTranslation("commands.generic.notFound", new Object[0]);
         var8.func_150256_b().func_150238_a(EnumChatFormatting.RED);
         var1.func_145747_a(var8);
      } else if (var5.func_71519_b(var1)) {
         if (var6 > -1) {
            List var12 = PlayerSelector.func_179656_b(var1, var3[var6], Entity.class);
            String var9 = var3[var6];
            var1.func_174794_a(CommandResultStats.Type.AFFECTED_ENTITIES, var12.size());
            Iterator var10 = var12.iterator();

            while(var10.hasNext()) {
               Entity var11 = (Entity)var10.next();
               var3[var6] = var11.func_110124_au().toString();
               if (this.func_175786_a(var1, var3, var5, var2)) {
                  ++var7;
               }
            }

            var3[var6] = var9;
         } else {
            var1.func_174794_a(CommandResultStats.Type.AFFECTED_ENTITIES, 1);
            if (this.func_175786_a(var1, var3, var5, var2)) {
               ++var7;
            }
         }
      } else {
         var8 = new ChatComponentTranslation("commands.generic.permission", new Object[0]);
         var8.func_150256_b().func_150238_a(EnumChatFormatting.RED);
         var1.func_145747_a(var8);
      }

      var1.func_174794_a(CommandResultStats.Type.SUCCESS_COUNT, var7);
      return var7;
   }

   protected boolean func_175786_a(ICommandSender var1, String[] var2, ICommand var3, String var4) {
      ChatComponentTranslation var6;
      try {
         var3.func_71515_b(var1, var2);
         return true;
      } catch (WrongUsageException var7) {
         var6 = new ChatComponentTranslation("commands.generic.usage", new Object[]{new ChatComponentTranslation(var7.getMessage(), var7.func_74844_a())});
         var6.func_150256_b().func_150238_a(EnumChatFormatting.RED);
         var1.func_145747_a(var6);
      } catch (CommandException var8) {
         var6 = new ChatComponentTranslation(var8.getMessage(), var8.func_74844_a());
         var6.func_150256_b().func_150238_a(EnumChatFormatting.RED);
         var1.func_145747_a(var6);
      } catch (Throwable var9) {
         var6 = new ChatComponentTranslation("commands.generic.exception", new Object[0]);
         var6.func_150256_b().func_150238_a(EnumChatFormatting.RED);
         var1.func_145747_a(var6);
         field_147175_a.warn("Couldn't process command: '" + var4 + "'");
      }

      return false;
   }

   public ICommand func_71560_a(ICommand var1) {
      this.field_71562_a.put(var1.func_71517_b(), var1);
      this.field_71561_b.add(var1);
      Iterator var2 = var1.func_71514_a().iterator();

      while(true) {
         String var3;
         ICommand var4;
         do {
            if (!var2.hasNext()) {
               return var1;
            }

            var3 = (String)var2.next();
            var4 = (ICommand)this.field_71562_a.get(var3);
         } while(var4 != null && var4.func_71517_b().equals(var3));

         this.field_71562_a.put(var3, var1);
      }
   }

   private static String[] func_71559_a(String[] var0) {
      String[] var1 = new String[var0.length - 1];
      System.arraycopy(var0, 1, var1, 0, var0.length - 1);
      return var1;
   }

   public List<String> func_180524_a(ICommandSender var1, String var2, BlockPos var3) {
      String[] var4 = var2.split(" ", -1);
      String var5 = var4[0];
      if (var4.length == 1) {
         ArrayList var9 = Lists.newArrayList();
         Iterator var7 = this.field_71562_a.entrySet().iterator();

         while(var7.hasNext()) {
            Entry var8 = (Entry)var7.next();
            if (CommandBase.func_71523_a(var5, (String)var8.getKey()) && ((ICommand)var8.getValue()).func_71519_b(var1)) {
               var9.add(var8.getKey());
            }
         }

         return var9;
      } else {
         if (var4.length > 1) {
            ICommand var6 = (ICommand)this.field_71562_a.get(var5);
            if (var6 != null && var6.func_71519_b(var1)) {
               return var6.func_180525_a(var1, func_71559_a(var4), var3);
            }
         }

         return null;
      }
   }

   public List<ICommand> func_71557_a(ICommandSender var1) {
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.field_71561_b.iterator();

      while(var3.hasNext()) {
         ICommand var4 = (ICommand)var3.next();
         if (var4.func_71519_b(var1)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public Map<String, ICommand> func_71555_a() {
      return this.field_71562_a;
   }

   private int func_82370_a(ICommand var1, String[] var2) {
      if (var1 == null) {
         return -1;
      } else {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var1.func_82358_a(var2, var3) && PlayerSelector.func_82377_a(var2[var3])) {
               return var3;
            }
         }

         return -1;
      }
   }
}
