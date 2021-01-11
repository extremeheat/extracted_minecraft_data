package net.minecraft.command;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandReplaceItem extends CommandBase {
   private static final Map<String, Integer> field_175785_a = Maps.newHashMap();

   public CommandReplaceItem() {
      super();
   }

   public String func_71517_b() {
      return "replaceitem";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.replaceitem.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 1) {
         throw new WrongUsageException("commands.replaceitem.usage", new Object[0]);
      } else {
         boolean var3;
         if (var2[0].equals("entity")) {
            var3 = false;
         } else {
            if (!var2[0].equals("block")) {
               throw new WrongUsageException("commands.replaceitem.usage", new Object[0]);
            }

            var3 = true;
         }

         byte var4;
         if (var3) {
            if (var2.length < 6) {
               throw new WrongUsageException("commands.replaceitem.block.usage", new Object[0]);
            }

            var4 = 4;
         } else {
            if (var2.length < 4) {
               throw new WrongUsageException("commands.replaceitem.entity.usage", new Object[0]);
            }

            var4 = 2;
         }

         int var16 = var4 + 1;
         int var5 = this.func_175783_e(var2[var4]);

         Item var6;
         try {
            var6 = func_147179_f(var1, var2[var16]);
         } catch (NumberInvalidException var15) {
            if (Block.func_149684_b(var2[var16]) != Blocks.field_150350_a) {
               throw var15;
            }

            var6 = null;
         }

         ++var16;
         int var7 = var2.length > var16 ? func_175764_a(var2[var16++], 1, 64) : 1;
         int var8 = var2.length > var16 ? func_175755_a(var2[var16++]) : 0;
         ItemStack var9 = new ItemStack(var6, var7, var8);
         if (var2.length > var16) {
            String var10 = func_147178_a(var1, var2, var16).func_150260_c();

            try {
               var9.func_77982_d(JsonToNBT.func_180713_a(var10));
            } catch (NBTException var14) {
               throw new CommandException("commands.replaceitem.tagError", new Object[]{var14.getMessage()});
            }
         }

         if (var9.func_77973_b() == null) {
            var9 = null;
         }

         if (var3) {
            var1.func_174794_a(CommandResultStats.Type.AFFECTED_ITEMS, 0);
            BlockPos var18 = func_175757_a(var1, var2, 1, false);
            World var11 = var1.func_130014_f_();
            TileEntity var12 = var11.func_175625_s(var18);
            if (var12 == null || !(var12 instanceof IInventory)) {
               throw new CommandException("commands.replaceitem.noContainer", new Object[]{var18.func_177958_n(), var18.func_177956_o(), var18.func_177952_p()});
            }

            IInventory var13 = (IInventory)var12;
            if (var5 >= 0 && var5 < var13.func_70302_i_()) {
               var13.func_70299_a(var5, var9);
            }
         } else {
            Entity var17 = func_175768_b(var1, var2[1]);
            var1.func_174794_a(CommandResultStats.Type.AFFECTED_ITEMS, 0);
            if (var17 instanceof EntityPlayer) {
               ((EntityPlayer)var17).field_71069_bz.func_75142_b();
            }

            if (!var17.func_174820_d(var5, var9)) {
               throw new CommandException("commands.replaceitem.failed", new Object[]{var5, var7, var9 == null ? "Air" : var9.func_151000_E()});
            }

            if (var17 instanceof EntityPlayer) {
               ((EntityPlayer)var17).field_71069_bz.func_75142_b();
            }
         }

         var1.func_174794_a(CommandResultStats.Type.AFFECTED_ITEMS, var7);
         func_152373_a(var1, this, "commands.replaceitem.success", new Object[]{var5, var7, var9 == null ? "Air" : var9.func_151000_E()});
      }
   }

   private int func_175783_e(String var1) throws CommandException {
      if (!field_175785_a.containsKey(var1)) {
         throw new CommandException("commands.generic.parameter.invalid", new Object[]{var1});
      } else {
         return (Integer)field_175785_a.get(var1);
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return func_71530_a(var2, new String[]{"entity", "block"});
      } else if (var2.length == 2 && var2[0].equals("entity")) {
         return func_71530_a(var2, this.func_175784_d());
      } else if (var2.length >= 2 && var2.length <= 4 && var2[0].equals("block")) {
         return func_175771_a(var2, 1, var3);
      } else if (var2.length == 3 && var2[0].equals("entity") || var2.length == 5 && var2[0].equals("block")) {
         return func_175762_a(var2, field_175785_a.keySet());
      } else {
         return (var2.length != 4 || !var2[0].equals("entity")) && (var2.length != 6 || !var2[0].equals("block")) ? null : func_175762_a(var2, Item.field_150901_e.func_148742_b());
      }
   }

   protected String[] func_175784_d() {
      return MinecraftServer.func_71276_C().func_71213_z();
   }

   public boolean func_82358_a(String[] var1, int var2) {
      return var1.length > 0 && var1[0].equals("entity") && var2 == 1;
   }

   static {
      int var0;
      for(var0 = 0; var0 < 54; ++var0) {
         field_175785_a.put("slot.container." + var0, var0);
      }

      for(var0 = 0; var0 < 9; ++var0) {
         field_175785_a.put("slot.hotbar." + var0, var0);
      }

      for(var0 = 0; var0 < 27; ++var0) {
         field_175785_a.put("slot.inventory." + var0, 9 + var0);
      }

      for(var0 = 0; var0 < 27; ++var0) {
         field_175785_a.put("slot.enderchest." + var0, 200 + var0);
      }

      for(var0 = 0; var0 < 8; ++var0) {
         field_175785_a.put("slot.villager." + var0, 300 + var0);
      }

      for(var0 = 0; var0 < 15; ++var0) {
         field_175785_a.put("slot.horse." + var0, 500 + var0);
      }

      field_175785_a.put("slot.weapon", 99);
      field_175785_a.put("slot.armor.head", 103);
      field_175785_a.put("slot.armor.chest", 102);
      field_175785_a.put("slot.armor.legs", 101);
      field_175785_a.put("slot.armor.feet", 100);
      field_175785_a.put("slot.horse.saddle", 400);
      field_175785_a.put("slot.horse.armor", 401);
      field_175785_a.put("slot.horse.chest", 499);
   }
}
