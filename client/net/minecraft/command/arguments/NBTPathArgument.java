package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCollection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentTranslation;

public class NBTPathArgument implements ArgumentType<NBTPathArgument.NBTPath> {
   private static final Collection<String> field_201316_a = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", ".");
   private static final DynamicCommandExceptionType field_197153_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("arguments.nbtpath.child.invalid", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType field_197154_b = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("arguments.nbtpath.element.invalid", new Object[]{var0});
   });
   private static final SimpleCommandExceptionType field_201948_d = new SimpleCommandExceptionType(new TextComponentTranslation("arguments.nbtpath.node.invalid", new Object[0]));

   public NBTPathArgument() {
      super();
   }

   public static NBTPathArgument func_197149_a() {
      return new NBTPathArgument();
   }

   public static NBTPathArgument.NBTPath func_197148_a(CommandContext<CommandSource> var0, String var1) {
      return (NBTPathArgument.NBTPath)var0.getArgument(var1, NBTPathArgument.NBTPath.class);
   }

   public NBTPathArgument.NBTPath parse(StringReader var1) throws CommandSyntaxException {
      ArrayList var2 = Lists.newArrayList();
      int var3 = var1.getCursor();

      while(var1.canRead() && var1.peek() != ' ') {
         switch(var1.peek()) {
         case '"':
            var2.add(new NBTPathArgument.ChildNode(var1.readString()));
            break;
         case '[':
            var1.skip();
            var2.add(new NBTPathArgument.ElementNode(var1.readInt()));
            var1.expect(']');
            break;
         default:
            var2.add(new NBTPathArgument.ChildNode(this.func_197151_a(var1)));
         }

         if (var1.canRead()) {
            char var4 = var1.peek();
            if (var4 != ' ' && var4 != '[') {
               var1.expect('.');
            }
         }
      }

      return new NBTPathArgument.NBTPath(var1.getString().substring(var3, var1.getCursor()), (NBTPathArgument.INode[])var2.toArray(new NBTPathArgument.INode[0]));
   }

   private String func_197151_a(StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();

      while(var1.canRead() && func_197146_a(var1.peek())) {
         var1.skip();
      }

      if (var1.getCursor() == var2) {
         throw field_201948_d.createWithContext(var1);
      } else {
         return var1.getString().substring(var2, var1.getCursor());
      }
   }

   public Collection<String> getExamples() {
      return field_201316_a;
   }

   private static boolean func_197146_a(char var0) {
      return var0 != ' ' && var0 != '"' && var0 != '[' && var0 != ']' && var0 != '.';
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   static class ElementNode implements NBTPathArgument.INode {
      private final int field_197138_a;

      public ElementNode(int var1) {
         super();
         this.field_197138_a = var1;
      }

      public INBTBase func_197137_a(INBTBase var1) throws CommandSyntaxException {
         if (var1 instanceof NBTTagCollection) {
            NBTTagCollection var2 = (NBTTagCollection)var1;
            if (var2.size() > this.field_197138_a) {
               return var2.func_197647_c(this.field_197138_a);
            }
         }

         throw NBTPathArgument.field_197154_b.create(this.field_197138_a);
      }

      public INBTBase func_197135_a(INBTBase var1, Supplier<INBTBase> var2) throws CommandSyntaxException {
         return this.func_197137_a(var1);
      }

      public INBTBase func_197134_a() {
         return new NBTTagList();
      }

      public void func_197136_a(INBTBase var1, INBTBase var2) throws CommandSyntaxException {
         if (var1 instanceof NBTTagCollection) {
            NBTTagCollection var3 = (NBTTagCollection)var1;
            if (var3.size() > this.field_197138_a) {
               var3.func_197648_a(this.field_197138_a, var2);
               return;
            }
         }

         throw NBTPathArgument.field_197154_b.create(this.field_197138_a);
      }

      public void func_197133_b(INBTBase var1) throws CommandSyntaxException {
         if (var1 instanceof NBTTagCollection) {
            NBTTagCollection var2 = (NBTTagCollection)var1;
            if (var2.size() > this.field_197138_a) {
               var2.func_197649_b(this.field_197138_a);
               return;
            }
         }

         throw NBTPathArgument.field_197154_b.create(this.field_197138_a);
      }
   }

   static class ChildNode implements NBTPathArgument.INode {
      private final String field_197139_a;

      public ChildNode(String var1) {
         super();
         this.field_197139_a = var1;
      }

      public INBTBase func_197137_a(INBTBase var1) throws CommandSyntaxException {
         if (var1 instanceof NBTTagCompound) {
            return ((NBTTagCompound)var1).func_74781_a(this.field_197139_a);
         } else {
            throw NBTPathArgument.field_197153_a.create(this.field_197139_a);
         }
      }

      public INBTBase func_197135_a(INBTBase var1, Supplier<INBTBase> var2) throws CommandSyntaxException {
         if (var1 instanceof NBTTagCompound) {
            NBTTagCompound var3 = (NBTTagCompound)var1;
            if (var3.func_74764_b(this.field_197139_a)) {
               return var3.func_74781_a(this.field_197139_a);
            } else {
               INBTBase var4 = (INBTBase)var2.get();
               var3.func_74782_a(this.field_197139_a, var4);
               return var4;
            }
         } else {
            throw NBTPathArgument.field_197153_a.create(this.field_197139_a);
         }
      }

      public INBTBase func_197134_a() {
         return new NBTTagCompound();
      }

      public void func_197136_a(INBTBase var1, INBTBase var2) throws CommandSyntaxException {
         if (var1 instanceof NBTTagCompound) {
            NBTTagCompound var3 = (NBTTagCompound)var1;
            var3.func_74782_a(this.field_197139_a, var2);
         } else {
            throw NBTPathArgument.field_197153_a.create(this.field_197139_a);
         }
      }

      public void func_197133_b(INBTBase var1) throws CommandSyntaxException {
         if (var1 instanceof NBTTagCompound) {
            NBTTagCompound var2 = (NBTTagCompound)var1;
            if (var2.func_74764_b(this.field_197139_a)) {
               var2.func_82580_o(this.field_197139_a);
               return;
            }
         }

         throw NBTPathArgument.field_197153_a.create(this.field_197139_a);
      }
   }

   interface INode {
      INBTBase func_197137_a(INBTBase var1) throws CommandSyntaxException;

      INBTBase func_197135_a(INBTBase var1, Supplier<INBTBase> var2) throws CommandSyntaxException;

      INBTBase func_197134_a();

      void func_197136_a(INBTBase var1, INBTBase var2) throws CommandSyntaxException;

      void func_197133_b(INBTBase var1) throws CommandSyntaxException;
   }

   public static class NBTPath {
      private final String field_197144_a;
      private final NBTPathArgument.INode[] field_197145_b;

      public NBTPath(String var1, NBTPathArgument.INode[] var2) {
         super();
         this.field_197144_a = var1;
         this.field_197145_b = var2;
      }

      public INBTBase func_197143_a(INBTBase var1) throws CommandSyntaxException {
         NBTPathArgument.INode[] var2 = this.field_197145_b;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            NBTPathArgument.INode var5 = var2[var4];
            var1 = var5.func_197137_a(var1);
         }

         return var1;
      }

      public INBTBase func_197142_a(INBTBase var1, INBTBase var2) throws CommandSyntaxException {
         for(int var3 = 0; var3 < this.field_197145_b.length; ++var3) {
            NBTPathArgument.INode var4 = this.field_197145_b[var3];
            if (var3 < this.field_197145_b.length - 1) {
               int var5 = var3 + 1;
               var1 = var4.func_197135_a(var1, () -> {
                  return this.field_197145_b[var5].func_197134_a();
               });
            } else {
               var4.func_197136_a(var1, var2);
            }
         }

         return var1;
      }

      public String toString() {
         return this.field_197144_a;
      }

      public void func_197140_b(INBTBase var1) throws CommandSyntaxException {
         for(int var2 = 0; var2 < this.field_197145_b.length; ++var2) {
            NBTPathArgument.INode var3 = this.field_197145_b[var2];
            if (var2 < this.field_197145_b.length - 1) {
               var1 = var3.func_197137_a(var1);
            } else {
               var3.func_197133_b(var1);
            }
         }

      }
   }
}
