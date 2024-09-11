package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class TeleportCommand {
   private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(
      Component.translatable("commands.teleport.invalidPosition")
   );

   public TeleportCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      LiteralCommandNode var1 = var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("teleport")
                     .requires(var0x -> var0x.hasPermission(2)))
                  .then(
                     Commands.argument("location", Vec3Argument.vec3())
                        .executes(
                           var0x -> teleportToPos(
                                 (CommandSourceStack)var0x.getSource(),
                                 Collections.singleton(((CommandSourceStack)var0x.getSource()).getEntityOrException()),
                                 ((CommandSourceStack)var0x.getSource()).getLevel(),
                                 Vec3Argument.getCoordinates(var0x, "location"),
                                 WorldCoordinates.current(),
                                 null
                              )
                        )
                  ))
               .then(
                  Commands.argument("destination", EntityArgument.entity())
                     .executes(
                        var0x -> teleportToEntity(
                              (CommandSourceStack)var0x.getSource(),
                              Collections.singleton(((CommandSourceStack)var0x.getSource()).getEntityOrException()),
                              EntityArgument.getEntity(var0x, "destination")
                           )
                     )
               ))
            .then(
               ((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities())
                     .then(
                        ((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("location", Vec3Argument.vec3())
                                 .executes(
                                    var0x -> teleportToPos(
                                          (CommandSourceStack)var0x.getSource(),
                                          EntityArgument.getEntities(var0x, "targets"),
                                          ((CommandSourceStack)var0x.getSource()).getLevel(),
                                          Vec3Argument.getCoordinates(var0x, "location"),
                                          null,
                                          null
                                       )
                                 ))
                              .then(
                                 Commands.argument("rotation", RotationArgument.rotation())
                                    .executes(
                                       var0x -> teleportToPos(
                                             (CommandSourceStack)var0x.getSource(),
                                             EntityArgument.getEntities(var0x, "targets"),
                                             ((CommandSourceStack)var0x.getSource()).getLevel(),
                                             Vec3Argument.getCoordinates(var0x, "location"),
                                             RotationArgument.getRotation(var0x, "rotation"),
                                             null
                                          )
                                    )
                              ))
                           .then(
                              ((LiteralArgumentBuilder)Commands.literal("facing")
                                    .then(
                                       Commands.literal("entity")
                                          .then(
                                             ((RequiredArgumentBuilder)Commands.argument("facingEntity", EntityArgument.entity())
                                                   .executes(
                                                      var0x -> teleportToPos(
                                                            (CommandSourceStack)var0x.getSource(),
                                                            EntityArgument.getEntities(var0x, "targets"),
                                                            ((CommandSourceStack)var0x.getSource()).getLevel(),
                                                            Vec3Argument.getCoordinates(var0x, "location"),
                                                            null,
                                                            new TeleportCommand.LookAtEntity(
                                                               EntityArgument.getEntity(var0x, "facingEntity"), EntityAnchorArgument.Anchor.FEET
                                                            )
                                                         )
                                                   ))
                                                .then(
                                                   Commands.argument("facingAnchor", EntityAnchorArgument.anchor())
                                                      .executes(
                                                         var0x -> teleportToPos(
                                                               (CommandSourceStack)var0x.getSource(),
                                                               EntityArgument.getEntities(var0x, "targets"),
                                                               ((CommandSourceStack)var0x.getSource()).getLevel(),
                                                               Vec3Argument.getCoordinates(var0x, "location"),
                                                               null,
                                                               new TeleportCommand.LookAtEntity(
                                                                  EntityArgument.getEntity(var0x, "facingEntity"),
                                                                  EntityAnchorArgument.getAnchor(var0x, "facingAnchor")
                                                               )
                                                            )
                                                      )
                                                )
                                          )
                                    ))
                                 .then(
                                    Commands.argument("facingLocation", Vec3Argument.vec3())
                                       .executes(
                                          var0x -> teleportToPos(
                                                (CommandSourceStack)var0x.getSource(),
                                                EntityArgument.getEntities(var0x, "targets"),
                                                ((CommandSourceStack)var0x.getSource()).getLevel(),
                                                Vec3Argument.getCoordinates(var0x, "location"),
                                                null,
                                                new TeleportCommand.LookAtPosition(Vec3Argument.getVec3(var0x, "facingLocation"))
                                             )
                                       )
                                 )
                           )
                     ))
                  .then(
                     Commands.argument("destination", EntityArgument.entity())
                        .executes(
                           var0x -> teleportToEntity(
                                 (CommandSourceStack)var0x.getSource(),
                                 EntityArgument.getEntities(var0x, "targets"),
                                 EntityArgument.getEntity(var0x, "destination")
                              )
                        )
                  )
            )
      );
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tp").requires(var0x -> var0x.hasPermission(2))).redirect(var1));
   }

   private static int teleportToEntity(CommandSourceStack var0, Collection<? extends Entity> var1, Entity var2) throws CommandSyntaxException {
      for (Entity var4 : var1) {
         performTeleport(
            var0, var4, (ServerLevel)var2.level(), var2.getX(), var2.getY(), var2.getZ(), EnumSet.noneOf(Relative.class), var2.getYRot(), var2.getXRot(), null
         );
      }

      if (var1.size() == 1) {
         var0.sendSuccess(
            () -> Component.translatable("commands.teleport.success.entity.single", ((Entity)var1.iterator().next()).getDisplayName(), var2.getDisplayName()),
            true
         );
      } else {
         var0.sendSuccess(() -> Component.translatable("commands.teleport.success.entity.multiple", var1.size(), var2.getDisplayName()), true);
      }

      return var1.size();
   }

   private static int teleportToPos(
      CommandSourceStack var0,
      Collection<? extends Entity> var1,
      ServerLevel var2,
      Coordinates var3,
      @Nullable Coordinates var4,
      @Nullable TeleportCommand.LookAt var5
   ) throws CommandSyntaxException {
      Vec3 var6 = var3.getPosition(var0);
      Vec2 var7 = var4 == null ? null : var4.getRotation(var0);
      EnumSet var8 = EnumSet.noneOf(Relative.class);
      if (var3.isXRelative()) {
         var8.add(Relative.DELTA_X);
      }

      if (var3.isYRelative()) {
         var8.add(Relative.DELTA_Y);
      }

      if (var3.isZRelative()) {
         var8.add(Relative.DELTA_Z);
      }

      if (var4 == null) {
         var8.add(Relative.X_ROT);
         var8.add(Relative.Y_ROT);
      } else {
         if (var4.isXRelative()) {
            var8.add(Relative.X_ROT);
         }

         if (var4.isYRelative()) {
            var8.add(Relative.Y_ROT);
         }
      }

      for (Entity var10 : var1) {
         if (var4 == null) {
            performTeleport(var0, var10, var2, var6.x, var6.y, var6.z, var8, var10.getYRot(), var10.getXRot(), var5);
         } else {
            performTeleport(var0, var10, var2, var6.x, var6.y, var6.z, var8, var7.y, var7.x, var5);
         }
      }

      if (var1.size() == 1) {
         var0.sendSuccess(
            () -> Component.translatable(
                  "commands.teleport.success.location.single",
                  ((Entity)var1.iterator().next()).getDisplayName(),
                  formatDouble(var6.x),
                  formatDouble(var6.y),
                  formatDouble(var6.z)
               ),
            true
         );
      } else {
         var0.sendSuccess(
            () -> Component.translatable(
                  "commands.teleport.success.location.multiple", var1.size(), formatDouble(var6.x), formatDouble(var6.y), formatDouble(var6.z)
               ),
            true
         );
      }

      return var1.size();
   }

   private static String formatDouble(double var0) {
      return String.format(Locale.ROOT, "%f", var0);
   }

   private static void performTeleport(
      CommandSourceStack var0,
      Entity var1,
      ServerLevel var2,
      double var3,
      double var5,
      double var7,
      Set<Relative> var9,
      float var10,
      float var11,
      @Nullable TeleportCommand.LookAt var12
   ) throws CommandSyntaxException {
      BlockPos var13 = BlockPos.containing(var3, var5, var7);
      if (!Level.isInSpawnableBounds(var13)) {
         throw INVALID_POSITION.create();
      } else {
         float var14 = var9.contains(Relative.Y_ROT) ? var10 - var1.getYRot() : var10;
         float var15 = var9.contains(Relative.X_ROT) ? var11 - var1.getXRot() : var11;
         float var16 = Mth.wrapDegrees(var14);
         float var17 = Mth.wrapDegrees(var15);
         if (var1.teleportTo(var2, var3, var5, var7, var9, var16, var17, true)) {
            if (var12 != null) {
               var12.perform(var0, var1);
            }

            if (!(var1 instanceof LivingEntity var18) || !var18.isFallFlying()) {
               var1.setDeltaMovement(var1.getDeltaMovement().multiply(1.0, 0.0, 1.0));
               var1.setOnGround(true);
            }

            if (var1 instanceof PathfinderMob var19) {
               var19.getNavigation().stop();
            }
         }
      }
   }

   @FunctionalInterface
   interface LookAt {
      void perform(CommandSourceStack var1, Entity var2);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
