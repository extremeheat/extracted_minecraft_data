package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ILocationArgument;
import net.minecraft.command.arguments.LocationInput;
import net.minecraft.command.arguments.RotationArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;

public class TeleportCommand {
   public static void func_198809_a(CommandDispatcher<CommandSource> var0) {
      LiteralCommandNode var1 = var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("teleport").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(((RequiredArgumentBuilder)Commands.func_197056_a("targets", EntityArgument.func_197093_b()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("location", Vec3Argument.func_197301_a()).executes((var0x) -> {
         return func_200559_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), ((CommandSource)var0x.getSource()).func_197023_e(), Vec3Argument.func_200385_b(var0x, "location"), (ILocationArgument)null, (TeleportCommand.Facing)null);
      })).then(Commands.func_197056_a("rotation", RotationArgument.func_197288_a()).executes((var0x) -> {
         return func_200559_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), ((CommandSource)var0x.getSource()).func_197023_e(), Vec3Argument.func_200385_b(var0x, "location"), RotationArgument.func_200384_a(var0x, "rotation"), (TeleportCommand.Facing)null);
      }))).then(((LiteralArgumentBuilder)Commands.func_197057_a("facing").then(Commands.func_197057_a("entity").then(((RequiredArgumentBuilder)Commands.func_197056_a("facingEntity", EntityArgument.func_197086_a()).executes((var0x) -> {
         return func_200559_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), ((CommandSource)var0x.getSource()).func_197023_e(), Vec3Argument.func_200385_b(var0x, "location"), (ILocationArgument)null, new TeleportCommand.Facing(EntityArgument.func_197088_a(var0x, "facingEntity"), EntityAnchorArgument.Type.FEET));
      })).then(Commands.func_197056_a("facingAnchor", EntityAnchorArgument.func_201024_a()).executes((var0x) -> {
         return func_200559_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), ((CommandSource)var0x.getSource()).func_197023_e(), Vec3Argument.func_200385_b(var0x, "location"), (ILocationArgument)null, new TeleportCommand.Facing(EntityArgument.func_197088_a(var0x, "facingEntity"), EntityAnchorArgument.func_201023_a(var0x, "facingAnchor")));
      }))))).then(Commands.func_197056_a("facingLocation", Vec3Argument.func_197301_a()).executes((var0x) -> {
         return func_200559_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), ((CommandSource)var0x.getSource()).func_197023_e(), Vec3Argument.func_200385_b(var0x, "location"), (ILocationArgument)null, new TeleportCommand.Facing(Vec3Argument.func_197300_a(var0x, "facingLocation")));
      }))))).then(Commands.func_197056_a("destination", EntityArgument.func_197086_a()).executes((var0x) -> {
         return func_201126_a((CommandSource)var0x.getSource(), EntityArgument.func_197097_b(var0x, "targets"), EntityArgument.func_197088_a(var0x, "destination"));
      })))).then(Commands.func_197056_a("location", Vec3Argument.func_197301_a()).executes((var0x) -> {
         return func_200559_a((CommandSource)var0x.getSource(), Collections.singleton(((CommandSource)var0x.getSource()).func_197027_g()), ((CommandSource)var0x.getSource()).func_197023_e(), Vec3Argument.func_200385_b(var0x, "location"), LocationInput.func_200383_d(), (TeleportCommand.Facing)null);
      }))).then(Commands.func_197056_a("destination", EntityArgument.func_197086_a()).executes((var0x) -> {
         return func_201126_a((CommandSource)var0x.getSource(), Collections.singleton(((CommandSource)var0x.getSource()).func_197027_g()), EntityArgument.func_197088_a(var0x, "destination"));
      })));
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("tp").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).redirect(var1));
   }

   private static int func_201126_a(CommandSource var0, Collection<? extends Entity> var1, Entity var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Entity var4 = (Entity)var3.next();
         func_201127_a(var0, var4, var0.func_197023_e(), var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, EnumSet.noneOf(SPacketPlayerPosLook.EnumFlags.class), var2.field_70177_z, var2.field_70125_A, (TeleportCommand.Facing)null);
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.teleport.success.entity.single", new Object[]{((Entity)var1.iterator().next()).func_145748_c_(), var2.func_145748_c_()}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.teleport.success.entity.multiple", new Object[]{var1.size(), var2.func_145748_c_()}), true);
      }

      return var1.size();
   }

   private static int func_200559_a(CommandSource var0, Collection<? extends Entity> var1, WorldServer var2, ILocationArgument var3, @Nullable ILocationArgument var4, @Nullable TeleportCommand.Facing var5) throws CommandSyntaxException {
      Vec3d var6 = var3.func_197281_a(var0);
      Vec2f var7 = var4 == null ? null : var4.func_197282_b(var0);
      EnumSet var8 = EnumSet.noneOf(SPacketPlayerPosLook.EnumFlags.class);
      if (var3.func_200380_a()) {
         var8.add(SPacketPlayerPosLook.EnumFlags.X);
      }

      if (var3.func_200381_b()) {
         var8.add(SPacketPlayerPosLook.EnumFlags.Y);
      }

      if (var3.func_200382_c()) {
         var8.add(SPacketPlayerPosLook.EnumFlags.Z);
      }

      if (var4 == null) {
         var8.add(SPacketPlayerPosLook.EnumFlags.X_ROT);
         var8.add(SPacketPlayerPosLook.EnumFlags.Y_ROT);
      } else {
         if (var4.func_200380_a()) {
            var8.add(SPacketPlayerPosLook.EnumFlags.X_ROT);
         }

         if (var4.func_200381_b()) {
            var8.add(SPacketPlayerPosLook.EnumFlags.Y_ROT);
         }
      }

      Iterator var9 = var1.iterator();

      while(var9.hasNext()) {
         Entity var10 = (Entity)var9.next();
         if (var4 == null) {
            func_201127_a(var0, var10, var2, var6.field_72450_a, var6.field_72448_b, var6.field_72449_c, var8, var10.field_70177_z, var10.field_70125_A, var5);
         } else {
            func_201127_a(var0, var10, var2, var6.field_72450_a, var6.field_72448_b, var6.field_72449_c, var8, var7.field_189983_j, var7.field_189982_i, var5);
         }
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.teleport.success.location.single", new Object[]{((Entity)var1.iterator().next()).func_145748_c_(), var6.field_72450_a, var6.field_72448_b, var6.field_72449_c}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.teleport.success.location.multiple", new Object[]{var1.size(), var6.field_72450_a, var6.field_72448_b, var6.field_72449_c}), true);
      }

      return var1.size();
   }

   private static void func_201127_a(CommandSource var0, Entity var1, WorldServer var2, double var3, double var5, double var7, Set<SPacketPlayerPosLook.EnumFlags> var9, float var10, float var11, @Nullable TeleportCommand.Facing var12) {
      if (var1 instanceof EntityPlayerMP) {
         var1.func_184210_p();
         if (((EntityPlayerMP)var1).func_70608_bn()) {
            ((EntityPlayerMP)var1).func_70999_a(true, true, false);
         }

         if (var2 == var1.field_70170_p) {
            ((EntityPlayerMP)var1).field_71135_a.func_175089_a(var3, var5, var7, var10, var11, var9);
         } else {
            ((EntityPlayerMP)var1).func_200619_a(var2, var3, var5, var7, var10, var11);
         }

         var1.func_70034_d(var10);
      } else {
         float var13 = MathHelper.func_76142_g(var10);
         float var14 = MathHelper.func_76142_g(var11);
         var14 = MathHelper.func_76131_a(var14, -90.0F, 90.0F);
         if (var2 == var1.field_70170_p) {
            var1.func_70012_b(var3, var5, var7, var13, var14);
            var1.func_70034_d(var13);
         } else {
            WorldServer var15 = (WorldServer)var1.field_70170_p;
            var15.func_72900_e(var1);
            var1.field_71093_bK = var2.field_73011_w.func_186058_p();
            var1.field_70128_L = false;
            Entity var16 = var1;
            var1 = var1.func_200600_R().func_200721_a(var2);
            if (var1 == null) {
               return;
            }

            var1.func_180432_n(var16);
            var1.func_70012_b(var3, var5, var7, var13, var14);
            var1.func_70034_d(var13);
            boolean var17 = var1.field_98038_p;
            var1.field_98038_p = true;
            var2.func_72838_d(var1);
            var1.field_98038_p = var17;
            var2.func_72866_a(var1, false);
            var16.field_70128_L = true;
         }
      }

      if (var12 != null) {
         var12.func_201124_a(var0, var1);
      }

      if (!(var1 instanceof EntityLivingBase) || !((EntityLivingBase)var1).func_184613_cA()) {
         var1.field_70181_x = 0.0D;
         var1.field_70122_E = true;
      }

   }

   static class Facing {
      private final Vec3d field_200549_a;
      private final Entity field_200550_b;
      private final EntityAnchorArgument.Type field_201125_c;

      public Facing(Entity var1, EntityAnchorArgument.Type var2) {
         super();
         this.field_200550_b = var1;
         this.field_201125_c = var2;
         this.field_200549_a = var2.func_201017_a(var1);
      }

      public Facing(Vec3d var1) {
         super();
         this.field_200550_b = null;
         this.field_200549_a = var1;
         this.field_201125_c = null;
      }

      public void func_201124_a(CommandSource var1, Entity var2) {
         if (this.field_200550_b != null) {
            if (var2 instanceof EntityPlayerMP) {
               ((EntityPlayerMP)var2).func_200618_a(var1.func_201008_k(), this.field_200550_b, this.field_201125_c);
            } else {
               var2.func_200602_a(var1.func_201008_k(), this.field_200549_a);
            }
         } else {
            var2.func_200602_a(var1.func_201008_k(), this.field_200549_a);
         }

      }
   }
}
