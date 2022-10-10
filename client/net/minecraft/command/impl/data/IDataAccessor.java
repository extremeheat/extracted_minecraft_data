package net.minecraft.command.impl.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

public interface IDataAccessor {
   void func_198925_a(NBTTagCompound var1) throws CommandSyntaxException;

   NBTTagCompound func_198923_a() throws CommandSyntaxException;

   ITextComponent func_198921_b();

   ITextComponent func_198924_b(INBTBase var1);

   ITextComponent func_198922_a(NBTPathArgument.NBTPath var1, double var2, int var4);
}
