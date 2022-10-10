package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;

public class PlaySoundCommand {
   private static final SimpleCommandExceptionType field_198579_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.playsound.failed", new Object[0]));

   public static void func_198572_a(CommandDispatcher<CommandSource> var0) {
      RequiredArgumentBuilder var1 = Commands.func_197056_a("sound", ResourceLocationArgument.func_197197_a()).suggests(SuggestionProviders.field_197504_c);
      SoundCategory[] var2 = SoundCategory.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SoundCategory var5 = var2[var4];
         var1.then(func_198577_a(var5));
      }

      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("playsound").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(var1));
   }

   private static LiteralArgumentBuilder<CommandSource> func_198577_a(SoundCategory var0) {
      return (LiteralArgumentBuilder)Commands.func_197057_a(var0.func_187948_a()).then(((RequiredArgumentBuilder)Commands.func_197056_a("targets", EntityArgument.func_197094_d()).executes((var1) -> {
         return func_198573_a((CommandSource)var1.getSource(), EntityArgument.func_197090_e(var1, "targets"), ResourceLocationArgument.func_197195_e(var1, "sound"), var0, ((CommandSource)var1.getSource()).func_197036_d(), 1.0F, 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)Commands.func_197056_a("pos", Vec3Argument.func_197301_a()).executes((var1) -> {
         return func_198573_a((CommandSource)var1.getSource(), EntityArgument.func_197090_e(var1, "targets"), ResourceLocationArgument.func_197195_e(var1, "sound"), var0, Vec3Argument.func_197300_a(var1, "pos"), 1.0F, 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)Commands.func_197056_a("volume", FloatArgumentType.floatArg(0.0F)).executes((var1) -> {
         return func_198573_a((CommandSource)var1.getSource(), EntityArgument.func_197090_e(var1, "targets"), ResourceLocationArgument.func_197195_e(var1, "sound"), var0, Vec3Argument.func_197300_a(var1, "pos"), (Float)var1.getArgument("volume", Float.class), 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)Commands.func_197056_a("pitch", FloatArgumentType.floatArg(0.0F, 2.0F)).executes((var1) -> {
         return func_198573_a((CommandSource)var1.getSource(), EntityArgument.func_197090_e(var1, "targets"), ResourceLocationArgument.func_197195_e(var1, "sound"), var0, Vec3Argument.func_197300_a(var1, "pos"), (Float)var1.getArgument("volume", Float.class), (Float)var1.getArgument("pitch", Float.class), 0.0F);
      })).then(Commands.func_197056_a("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F)).executes((var1) -> {
         return func_198573_a((CommandSource)var1.getSource(), EntityArgument.func_197090_e(var1, "targets"), ResourceLocationArgument.func_197195_e(var1, "sound"), var0, Vec3Argument.func_197300_a(var1, "pos"), (Float)var1.getArgument("volume", Float.class), (Float)var1.getArgument("pitch", Float.class), (Float)var1.getArgument("minVolume", Float.class));
      }))))));
   }

   private static int func_198573_a(CommandSource var0, Collection<EntityPlayerMP> var1, ResourceLocation var2, SoundCategory var3, Vec3d var4, float var5, float var6, float var7) throws CommandSyntaxException {
      double var8 = Math.pow(var5 > 1.0F ? (double)(var5 * 16.0F) : 16.0D, 2.0D);
      int var10 = 0;
      Iterator var11 = var1.iterator();

      while(true) {
         EntityPlayerMP var12;
         Vec3d var21;
         float var22;
         while(true) {
            if (!var11.hasNext()) {
               if (var10 == 0) {
                  throw field_198579_a.create();
               }

               if (var1.size() == 1) {
                  var0.func_197030_a(new TextComponentTranslation("commands.playsound.success.single", new Object[]{var2, ((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
               } else {
                  var0.func_197030_a(new TextComponentTranslation("commands.playsound.success.single", new Object[]{var2, ((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
               }

               return var10;
            }

            var12 = (EntityPlayerMP)var11.next();
            double var13 = var4.field_72450_a - var12.field_70165_t;
            double var15 = var4.field_72448_b - var12.field_70163_u;
            double var17 = var4.field_72449_c - var12.field_70161_v;
            double var19 = var13 * var13 + var15 * var15 + var17 * var17;
            var21 = var4;
            var22 = var5;
            if (var19 <= var8) {
               break;
            }

            if (var7 > 0.0F) {
               double var23 = (double)MathHelper.func_76133_a(var19);
               var21 = new Vec3d(var12.field_70165_t + var13 / var23 * 2.0D, var12.field_70163_u + var15 / var23 * 2.0D, var12.field_70161_v + var17 / var23 * 2.0D);
               var22 = var7;
               break;
            }
         }

         var12.field_71135_a.func_147359_a(new SPacketCustomSound(var2, var3, var21, var22, var6));
         ++var10;
      }
   }
}
