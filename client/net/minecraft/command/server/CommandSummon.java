package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandSummon extends CommandBase {
   public CommandSummon() {
      super();
   }

   public String func_71517_b() {
      return "summon";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.summon.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new WrongUsageException("commands.summon.usage", new Object[0]);
      } else {
         String var3 = var2[0];
         BlockPos var4 = var1.func_180425_c();
         Vec3 var5 = var1.func_174791_d();
         double var6 = var5.field_72450_a;
         double var8 = var5.field_72448_b;
         double var10 = var5.field_72449_c;
         if (var2.length >= 4) {
            var6 = func_175761_b(var6, var2[1], true);
            var8 = func_175761_b(var8, var2[2], false);
            var10 = func_175761_b(var10, var2[3], true);
            var4 = new BlockPos(var6, var8, var10);
         }

         World var12 = var1.func_130014_f_();
         if (!var12.func_175667_e(var4)) {
            throw new CommandException("commands.summon.outOfWorld", new Object[0]);
         } else if ("LightningBolt".equals(var3)) {
            var12.func_72942_c(new EntityLightningBolt(var12, var6, var8, var10));
            func_152373_a(var1, this, "commands.summon.success", new Object[0]);
         } else {
            NBTTagCompound var13 = new NBTTagCompound();
            boolean var14 = false;
            if (var2.length >= 5) {
               IChatComponent var15 = func_147178_a(var1, var2, 4);

               try {
                  var13 = JsonToNBT.func_180713_a(var15.func_150260_c());
                  var14 = true;
               } catch (NBTException var20) {
                  throw new CommandException("commands.summon.tagError", new Object[]{var20.getMessage()});
               }
            }

            var13.func_74778_a("id", var3);

            Entity var21;
            try {
               var21 = EntityList.func_75615_a(var13, var12);
            } catch (RuntimeException var19) {
               throw new CommandException("commands.summon.failed", new Object[0]);
            }

            if (var21 == null) {
               throw new CommandException("commands.summon.failed", new Object[0]);
            } else {
               var21.func_70012_b(var6, var8, var10, var21.field_70177_z, var21.field_70125_A);
               if (!var14 && var21 instanceof EntityLiving) {
                  ((EntityLiving)var21).func_180482_a(var12.func_175649_E(new BlockPos(var21)), (IEntityLivingData)null);
               }

               var12.func_72838_d(var21);
               Entity var16 = var21;

               for(NBTTagCompound var17 = var13; var16 != null && var17.func_150297_b("Riding", 10); var17 = var17.func_74775_l("Riding")) {
                  Entity var18 = EntityList.func_75615_a(var17.func_74775_l("Riding"), var12);
                  if (var18 != null) {
                     var18.func_70012_b(var6, var8, var10, var18.field_70177_z, var18.field_70125_A);
                     var12.func_72838_d(var18);
                     var16.func_70078_a(var18);
                  }

                  var16 = var18;
               }

               func_152373_a(var1, this, "commands.summon.success", new Object[0]);
            }
         }
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return func_175762_a(var2, EntityList.func_180124_b());
      } else {
         return var2.length > 1 && var2.length <= 4 ? func_175771_a(var2, 1, var3) : null;
      }
   }
}
