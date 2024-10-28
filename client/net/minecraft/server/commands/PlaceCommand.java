package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Optional;
import net.minecraft.ResourceLocationException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.TemplateMirrorArgument;
import net.minecraft.commands.arguments.TemplateRotationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class PlaceCommand {
   private static final SimpleCommandExceptionType ERROR_FEATURE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.place.feature.failed"));
   private static final SimpleCommandExceptionType ERROR_JIGSAW_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.place.jigsaw.failed"));
   private static final SimpleCommandExceptionType ERROR_STRUCTURE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.place.structure.failed"));
   private static final DynamicCommandExceptionType ERROR_TEMPLATE_INVALID = new DynamicCommandExceptionType((var0) -> {
      return Component.translatableEscape("commands.place.template.invalid", var0);
   });
   private static final SimpleCommandExceptionType ERROR_TEMPLATE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.place.template.failed"));
   private static final SuggestionProvider<CommandSourceStack> SUGGEST_TEMPLATES = (var0, var1) -> {
      StructureTemplateManager var2 = ((CommandSourceStack)var0.getSource()).getLevel().getStructureManager();
      return SharedSuggestionProvider.suggestResource(var2.listTemplates(), var1);
   };

   public PlaceCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("place").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("feature").then(((RequiredArgumentBuilder)Commands.argument("feature", ResourceKeyArgument.key(Registries.CONFIGURED_FEATURE)).executes((var0x) -> {
         return placeFeature((CommandSourceStack)var0x.getSource(), ResourceKeyArgument.getConfiguredFeature(var0x, "feature"), BlockPos.containing(((CommandSourceStack)var0x.getSource()).getPosition()));
      })).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((var0x) -> {
         return placeFeature((CommandSourceStack)var0x.getSource(), ResourceKeyArgument.getConfiguredFeature(var0x, "feature"), BlockPosArgument.getLoadedBlockPos(var0x, "pos"));
      }))))).then(Commands.literal("jigsaw").then(Commands.argument("pool", ResourceKeyArgument.key(Registries.TEMPLATE_POOL)).then(Commands.argument("target", ResourceLocationArgument.id()).then(((RequiredArgumentBuilder)Commands.argument("max_depth", IntegerArgumentType.integer(1, 20)).executes((var0x) -> {
         return placeJigsaw((CommandSourceStack)var0x.getSource(), ResourceKeyArgument.getStructureTemplatePool(var0x, "pool"), ResourceLocationArgument.getId(var0x, "target"), IntegerArgumentType.getInteger(var0x, "max_depth"), BlockPos.containing(((CommandSourceStack)var0x.getSource()).getPosition()));
      })).then(Commands.argument("position", BlockPosArgument.blockPos()).executes((var0x) -> {
         return placeJigsaw((CommandSourceStack)var0x.getSource(), ResourceKeyArgument.getStructureTemplatePool(var0x, "pool"), ResourceLocationArgument.getId(var0x, "target"), IntegerArgumentType.getInteger(var0x, "max_depth"), BlockPosArgument.getLoadedBlockPos(var0x, "position"));
      }))))))).then(Commands.literal("structure").then(((RequiredArgumentBuilder)Commands.argument("structure", ResourceKeyArgument.key(Registries.STRUCTURE)).executes((var0x) -> {
         return placeStructure((CommandSourceStack)var0x.getSource(), ResourceKeyArgument.getStructure(var0x, "structure"), BlockPos.containing(((CommandSourceStack)var0x.getSource()).getPosition()));
      })).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((var0x) -> {
         return placeStructure((CommandSourceStack)var0x.getSource(), ResourceKeyArgument.getStructure(var0x, "structure"), BlockPosArgument.getLoadedBlockPos(var0x, "pos"));
      }))))).then(Commands.literal("template").then(((RequiredArgumentBuilder)Commands.argument("template", ResourceLocationArgument.id()).suggests(SUGGEST_TEMPLATES).executes((var0x) -> {
         return placeTemplate((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "template"), BlockPos.containing(((CommandSourceStack)var0x.getSource()).getPosition()), Rotation.NONE, Mirror.NONE, 1.0F, 0);
      })).then(((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes((var0x) -> {
         return placeTemplate((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "template"), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), Rotation.NONE, Mirror.NONE, 1.0F, 0);
      })).then(((RequiredArgumentBuilder)Commands.argument("rotation", TemplateRotationArgument.templateRotation()).executes((var0x) -> {
         return placeTemplate((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "template"), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), TemplateRotationArgument.getRotation(var0x, "rotation"), Mirror.NONE, 1.0F, 0);
      })).then(((RequiredArgumentBuilder)Commands.argument("mirror", TemplateMirrorArgument.templateMirror()).executes((var0x) -> {
         return placeTemplate((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "template"), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), TemplateRotationArgument.getRotation(var0x, "rotation"), TemplateMirrorArgument.getMirror(var0x, "mirror"), 1.0F, 0);
      })).then(((RequiredArgumentBuilder)Commands.argument("integrity", FloatArgumentType.floatArg(0.0F, 1.0F)).executes((var0x) -> {
         return placeTemplate((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "template"), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), TemplateRotationArgument.getRotation(var0x, "rotation"), TemplateMirrorArgument.getMirror(var0x, "mirror"), FloatArgumentType.getFloat(var0x, "integrity"), 0);
      })).then(Commands.argument("seed", IntegerArgumentType.integer()).executes((var0x) -> {
         return placeTemplate((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "template"), BlockPosArgument.getLoadedBlockPos(var0x, "pos"), TemplateRotationArgument.getRotation(var0x, "rotation"), TemplateMirrorArgument.getMirror(var0x, "mirror"), FloatArgumentType.getFloat(var0x, "integrity"), IntegerArgumentType.getInteger(var0x, "seed"));
      })))))))));
   }

   public static int placeFeature(CommandSourceStack var0, Holder.Reference<ConfiguredFeature<?, ?>> var1, BlockPos var2) throws CommandSyntaxException {
      ServerLevel var3 = var0.getLevel();
      ConfiguredFeature var4 = (ConfiguredFeature)var1.value();
      ChunkPos var5 = new ChunkPos(var2);
      checkLoaded(var3, new ChunkPos(var5.x - 1, var5.z - 1), new ChunkPos(var5.x + 1, var5.z + 1));
      if (!var4.place(var3, var3.getChunkSource().getGenerator(), var3.getRandom(), var2)) {
         throw ERROR_FEATURE_FAILED.create();
      } else {
         String var6 = var1.key().location().toString();
         var0.sendSuccess(() -> {
            return Component.translatable("commands.place.feature.success", var6, var2.getX(), var2.getY(), var2.getZ());
         }, true);
         return 1;
      }
   }

   public static int placeJigsaw(CommandSourceStack var0, Holder<StructureTemplatePool> var1, ResourceLocation var2, int var3, BlockPos var4) throws CommandSyntaxException {
      ServerLevel var5 = var0.getLevel();
      ChunkPos var6 = new ChunkPos(var4);
      checkLoaded(var5, var6, var6);
      if (!JigsawPlacement.generateJigsaw(var5, var1, var2, var3, var4, false)) {
         throw ERROR_JIGSAW_FAILED.create();
      } else {
         var0.sendSuccess(() -> {
            return Component.translatable("commands.place.jigsaw.success", var4.getX(), var4.getY(), var4.getZ());
         }, true);
         return 1;
      }
   }

   public static int placeStructure(CommandSourceStack var0, Holder.Reference<Structure> var1, BlockPos var2) throws CommandSyntaxException {
      ServerLevel var3 = var0.getLevel();
      Structure var4 = (Structure)var1.value();
      ChunkGenerator var5 = var3.getChunkSource().getGenerator();
      StructureStart var6 = var4.generate(var0.registryAccess(), var5, var5.getBiomeSource(), var3.getChunkSource().randomState(), var3.getStructureManager(), var3.getSeed(), new ChunkPos(var2), 0, var3, (var0x) -> {
         return true;
      });
      if (!var6.isValid()) {
         throw ERROR_STRUCTURE_FAILED.create();
      } else {
         BoundingBox var7 = var6.getBoundingBox();
         ChunkPos var8 = new ChunkPos(SectionPos.blockToSectionCoord(var7.minX()), SectionPos.blockToSectionCoord(var7.minZ()));
         ChunkPos var9 = new ChunkPos(SectionPos.blockToSectionCoord(var7.maxX()), SectionPos.blockToSectionCoord(var7.maxZ()));
         checkLoaded(var3, var8, var9);
         ChunkPos.rangeClosed(var8, var9).forEach((var3x) -> {
            var6.placeInChunk(var3, var3.structureManager(), var5, var3.getRandom(), new BoundingBox(var3x.getMinBlockX(), var3.getMinBuildHeight(), var3x.getMinBlockZ(), var3x.getMaxBlockX(), var3.getMaxBuildHeight(), var3x.getMaxBlockZ()), var3x);
         });
         String var10 = var1.key().location().toString();
         var0.sendSuccess(() -> {
            return Component.translatable("commands.place.structure.success", var10, var2.getX(), var2.getY(), var2.getZ());
         }, true);
         return 1;
      }
   }

   public static int placeTemplate(CommandSourceStack var0, ResourceLocation var1, BlockPos var2, Rotation var3, Mirror var4, float var5, int var6) throws CommandSyntaxException {
      ServerLevel var7 = var0.getLevel();
      StructureTemplateManager var8 = var7.getStructureManager();

      Optional var9;
      try {
         var9 = var8.get(var1);
      } catch (ResourceLocationException var13) {
         throw ERROR_TEMPLATE_INVALID.create(var1);
      }

      if (var9.isEmpty()) {
         throw ERROR_TEMPLATE_INVALID.create(var1);
      } else {
         StructureTemplate var10 = (StructureTemplate)var9.get();
         checkLoaded(var7, new ChunkPos(var2), new ChunkPos(var2.offset(var10.getSize())));
         StructurePlaceSettings var11 = (new StructurePlaceSettings()).setMirror(var4).setRotation(var3);
         if (var5 < 1.0F) {
            var11.clearProcessors().addProcessor(new BlockRotProcessor(var5)).setRandom(StructureBlockEntity.createRandom((long)var6));
         }

         boolean var12 = var10.placeInWorld(var7, var2, var2, var11, StructureBlockEntity.createRandom((long)var6), 2);
         if (!var12) {
            throw ERROR_TEMPLATE_FAILED.create();
         } else {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.place.template.success", Component.translationArg(var1), var2.getX(), var2.getY(), var2.getZ());
            }, true);
            return 1;
         }
      }
   }

   private static void checkLoaded(ServerLevel var0, ChunkPos var1, ChunkPos var2) throws CommandSyntaxException {
      if (ChunkPos.rangeClosed(var1, var2).filter((var1x) -> {
         return !var0.isLoaded(var1x.getWorldPosition());
      }).findAny().isPresent()) {
         throw BlockPosArgument.ERROR_NOT_LOADED.create();
      }
   }
}
