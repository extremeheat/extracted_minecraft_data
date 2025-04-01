package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.LocalCoordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.RoomerinoComponentino;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class RoomCommand {
   public static final LocalCoordinates LAZY_DEV_TARGET = new LocalCoordinates(0.0, 0.0, 6.0);
   private static final SuggestionProvider<CommandSourceStack> SUGGEST_PACK_TEMPLATES;
   private static final SuggestionProvider<CommandSourceStack> SUGGEST_WORLD_TEMPLATES;

   public RoomCommand() {
      super();
   }

   private static SuggestionProvider<CommandSourceStack> sluggestion(StructureTemplateManager.SourceType var0) {
      return (var1, var2) -> {
         StructureTemplateManager var3 = ((CommandSourceStack)var1.getSource()).getLevel().getStructureManager();
         return SharedSuggestionProvider.suggestResource(var3.listTemplates(var0).filter((var0x) -> var0x.getPath().startsWith("hub/room/")).map(RoomerinoComponentino::structureToId), var2);
      };
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("room").then(Commands.literal("start").then(Commands.argument("structure", ResourceLocationArgument.id()).then(Commands.argument("xSize", IntegerArgumentType.integer(1)).then(Commands.argument("zSize", IntegerArgumentType.integer(1)).then(Commands.argument("ySize", IntegerArgumentType.integer(1)).executes((var0x) -> createTemplate((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "structure"), IntegerArgumentType.getInteger(var0x, "xSize"), IntegerArgumentType.getInteger(var0x, "zSize"), IntegerArgumentType.getInteger(var0x, "ySize"))))))))).then(Commands.literal("save").then(Commands.argument("structure", ResourceLocationArgument.id()).suggests(SUGGEST_WORLD_TEMPLATES).executes((var0x) -> save((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "structure")))))).then(Commands.literal("key").then(Commands.argument("structure", ResourceLocationArgument.id()).suggests(SUGGEST_PACK_TEMPLATES).executes((var0x) -> giveKey((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getId(var0x, "structure"))))));
   }

   private static int createTemplate(CommandSourceStack var0, ResourceLocation var1, int var2, int var3, int var4) {
      BlockPos var5 = LAZY_DEV_TARGET.getBlockPos(var0);
      ServerLevel var6 = var0.getLevel();

      for(int var7 = 0; var7 < var2; ++var7) {
         for(int var8 = 0; var8 < var4; ++var8) {
            for(int var9 = 0; var9 < var3; ++var9) {
               if (var7 == 0 || var7 == var2 - 1 || var9 == 0 || var9 == var3 - 1 || var8 == 0) {
                  var6.setBlock(var5.offset(var7, var8, var9), Blocks.BEDROCK.defaultBlockState(), 3);
               }
            }
         }
      }

      BlockPos var10 = var5.below();
      var6.setBlock(var10, (BlockState)Blocks.STRUCTURE_BLOCK.defaultBlockState().setValue(StructureBlock.MODE, StructureMode.SAVE), 3);
      Optional var11 = var6.getBlockEntity(var10, BlockEntityType.STRUCTURE_BLOCK);
      var11.ifPresent((var4x) -> {
         var4x.setStructureName(RoomerinoComponentino.idToStructure(var1));
         var4x.setIgnoreEntities(false);
         var4x.setStructureSize(new Vec3i(var2, var4, var3));
      });
      String var12 = "/room save " + String.valueOf(var1);
      var0.sendSuccess(() -> Component.literal("When done, save structure in structure block below and type ").append((Component)Component.literal(var12).withStyle(ChatFormatting.GRAY)).append(" or click ").append((Component)Component.literal("here").withStyle((UnaryOperator)((var1) -> var1.withColor(ChatFormatting.GRAY).withUnderlined(true).withClickEvent(new ClickEvent.SuggestCommand(var12))))), true);
      return var2 * var4 * var3;
   }

   private static int save(CommandSourceStack var0, ResourceLocation var1) {
      ResourceLocation var2 = RoomerinoComponentino.idToStructure(var1);
      var0.getLevel().theGame().getStructureManager().copyToFinal(var0, var2);
      giveKey(var0, var1);
      return 1;
   }

   private static int giveKey(CommandSourceStack var0, ResourceLocation var1) {
      ServerPlayer var2 = var0.getPlayer();
      if (var2 != null) {
         ItemStack var3 = new ItemStack(Items.SHIMMERING_KEY);
         var3.set(DataComponents.ROOM, new RoomerinoComponentino(var1));
         var2.getInventory().add(var3);
      }

      var0.sendSuccess(() -> Component.literal("\ud83d\udd11 ").append((Component)Component.translatable(var1.toLanguageKey("room"))), false);
      return 1;
   }

   static {
      SUGGEST_PACK_TEMPLATES = sluggestion(StructureTemplateManager.SourceType.PACK);
      SUGGEST_WORLD_TEMPLATES = sluggestion(StructureTemplateManager.SourceType.WORLD);
   }
}
