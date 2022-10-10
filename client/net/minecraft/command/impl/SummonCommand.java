package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.NBTArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

public class SummonCommand {
   private static final SimpleCommandExceptionType field_198741_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.summon.failed", new Object[0]));

   public static void func_198736_a(CommandDispatcher<CommandSource> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("summon").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(((RequiredArgumentBuilder)Commands.func_197056_a("entity", EntitySummonArgument.func_211366_a()).suggests(SuggestionProviders.field_197505_d).executes((var0x) -> {
         return func_198737_a((CommandSource)var0x.getSource(), EntitySummonArgument.func_211368_a(var0x, "entity"), ((CommandSource)var0x.getSource()).func_197036_d(), new NBTTagCompound(), true);
      })).then(((RequiredArgumentBuilder)Commands.func_197056_a("pos", Vec3Argument.func_197301_a()).executes((var0x) -> {
         return func_198737_a((CommandSource)var0x.getSource(), EntitySummonArgument.func_211368_a(var0x, "entity"), Vec3Argument.func_197300_a(var0x, "pos"), new NBTTagCompound(), true);
      })).then(Commands.func_197056_a("nbt", NBTArgument.func_197131_a()).executes((var0x) -> {
         return func_198737_a((CommandSource)var0x.getSource(), EntitySummonArgument.func_211368_a(var0x, "entity"), Vec3Argument.func_197300_a(var0x, "pos"), NBTArgument.func_197130_a(var0x, "nbt"), false);
      })))));
   }

   private static int func_198737_a(CommandSource var0, ResourceLocation var1, Vec3d var2, NBTTagCompound var3, boolean var4) throws CommandSyntaxException {
      NBTTagCompound var5 = var3.func_74737_b();
      var5.func_74778_a("id", var1.toString());
      if (EntityType.func_200718_a(EntityType.field_200728_aG).equals(var1)) {
         EntityLightningBolt var7 = new EntityLightningBolt(var0.func_197023_e(), var2.field_72450_a, var2.field_72448_b, var2.field_72449_c, false);
         var0.func_197023_e().func_72942_c(var7);
         var0.func_197030_a(new TextComponentTranslation("commands.summon.success", new Object[]{var7.func_145748_c_()}), true);
         return 1;
      } else {
         Entity var6 = AnvilChunkLoader.func_186054_a(var5, var0.func_197023_e(), var2.field_72450_a, var2.field_72448_b, var2.field_72449_c, true);
         if (var6 == null) {
            throw field_198741_a.create();
         } else {
            var6.func_70012_b(var2.field_72450_a, var2.field_72448_b, var2.field_72449_c, var6.field_70177_z, var6.field_70125_A);
            if (var4 && var6 instanceof EntityLiving) {
               ((EntityLiving)var6).func_204210_a(var0.func_197023_e().func_175649_E(new BlockPos(var6)), (IEntityLivingData)null, (NBTTagCompound)null);
            }

            var0.func_197030_a(new TextComponentTranslation("commands.summon.success", new Object[]{var6.func_145748_c_()}), true);
            return 1;
         }
      }
   }
}
