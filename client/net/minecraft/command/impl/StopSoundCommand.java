package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketStopSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;

public class StopSoundCommand {
   public static void func_198730_a(CommandDispatcher<CommandSource> var0) {
      RequiredArgumentBuilder var1 = (RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("targets", EntityArgument.func_197094_d()).executes((var0x) -> {
         return func_198733_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), (SoundCategory)null, (ResourceLocation)null);
      })).then(Commands.func_197057_a("*").then(Commands.func_197056_a("sound", ResourceLocationArgument.func_197197_a()).suggests(SuggestionProviders.field_197504_c).executes((var0x) -> {
         return func_198733_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), (SoundCategory)null, ResourceLocationArgument.func_197195_e(var0x, "sound"));
      })));
      SoundCategory[] var2 = SoundCategory.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SoundCategory var5 = var2[var4];
         var1.then(((LiteralArgumentBuilder)Commands.func_197057_a(var5.func_187948_a()).executes((var1x) -> {
            return func_198733_a((CommandSource)var1x.getSource(), EntityArgument.func_197090_e(var1x, "targets"), var5, (ResourceLocation)null);
         })).then(Commands.func_197056_a("sound", ResourceLocationArgument.func_197197_a()).suggests(SuggestionProviders.field_197504_c).executes((var1x) -> {
            return func_198733_a((CommandSource)var1x.getSource(), EntityArgument.func_197090_e(var1x, "targets"), var5, ResourceLocationArgument.func_197195_e(var1x, "sound"));
         })));
      }

      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("stopsound").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(var1));
   }

   private static int func_198733_a(CommandSource var0, Collection<EntityPlayerMP> var1, @Nullable SoundCategory var2, @Nullable ResourceLocation var3) {
      SPacketStopSound var4 = new SPacketStopSound(var3, var2);
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         EntityPlayerMP var6 = (EntityPlayerMP)var5.next();
         var6.field_71135_a.func_147359_a(var4);
      }

      if (var2 != null) {
         if (var3 != null) {
            var0.func_197030_a(new TextComponentTranslation("commands.stopsound.success.source.sound", new Object[]{var3, var2.func_187948_a()}), true);
         } else {
            var0.func_197030_a(new TextComponentTranslation("commands.stopsound.success.source.any", new Object[]{var2.func_187948_a()}), true);
         }
      } else if (var3 != null) {
         var0.func_197030_a(new TextComponentTranslation("commands.stopsound.success.sourceless.sound", new Object[]{var3}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.stopsound.success.sourceless.any", new Object[0]), true);
      }

      return var1.size();
   }
}
