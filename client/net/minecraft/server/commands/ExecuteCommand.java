package net.minecraft.server.commands;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.HeightmapTypeArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.SlotsArgument;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.commands.execution.ChainModifiers;
import net.minecraft.commands.execution.CustomModifierExecutor;
import net.minecraft.commands.execution.ExecutionControl;
import net.minecraft.commands.execution.tasks.BuildContexts;
import net.minecraft.commands.execution.tasks.CallFunction;
import net.minecraft.commands.execution.tasks.FallthroughTask;
import net.minecraft.commands.execution.tasks.IsolatedCall;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;

public class ExecuteCommand {
   private static final int MAX_TEST_AREA = 32768;
   private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType(
      (var0, var1) -> Component.translatableEscape("commands.execute.blocks.toobig", var0, var1)
   );
   private static final SimpleCommandExceptionType ERROR_CONDITIONAL_FAILED = new SimpleCommandExceptionType(
      Component.translatable("commands.execute.conditional.fail")
   );
   private static final DynamicCommandExceptionType ERROR_CONDITIONAL_FAILED_COUNT = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("commands.execute.conditional.fail_count", var0)
   );
   @VisibleForTesting
   public static final Dynamic2CommandExceptionType ERROR_FUNCTION_CONDITION_INSTANTATION_FAILURE = new Dynamic2CommandExceptionType(
      (var0, var1) -> Component.translatableEscape("commands.execute.function.instantiationFailure", var0, var1)
   );
   private static final SuggestionProvider<CommandSourceStack> SUGGEST_PREDICATE = (var0, var1) -> {
      LootDataManager var2 = ((CommandSourceStack)var0.getSource()).getServer().getLootData();
      return SharedSuggestionProvider.suggestResource(var2.getKeys(LootDataType.PREDICATE), var1);
   };

   public ExecuteCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      LiteralCommandNode var2 = var0.register((LiteralArgumentBuilder)Commands.literal("execute").requires(var0x -> var0x.hasPermission(2)));
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                                                         "execute"
                                                      )
                                                      .requires(var0x -> var0x.hasPermission(2)))
                                                   .then(Commands.literal("run").redirect(var0.getRoot())))
                                                .then(addConditionals(var2, Commands.literal("if"), true, var1)))
                                             .then(addConditionals(var2, Commands.literal("unless"), false, var1)))
                                          .then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(var2, var0x -> {
                                             ArrayList var1xx = Lists.newArrayList();
                                    
                                             for(Entity var3 : EntityArgument.getOptionalEntities(var0x, "targets")) {
                                                var1xx.add(((CommandSourceStack)var0x.getSource()).withEntity(var3));
                                             }
                                    
                                             return var1xx;
                                          }))))
                                       .then(
                                          Commands.literal("at")
                                             .then(
                                                Commands.argument("targets", EntityArgument.entities())
                                                   .fork(
                                                      var2,
                                                      var0x -> {
                                                         ArrayList var1xx = Lists.newArrayList();
                                                
                                                         for(Entity var3 : EntityArgument.getOptionalEntities(var0x, "targets")) {
                                                            var1xx.add(
                                                               ((CommandSourceStack)var0x.getSource())
                                                                  .withLevel((ServerLevel)var3.level())
                                                                  .withPosition(var3.position())
                                                                  .withRotation(var3.getRotationVector())
                                                            );
                                                         }
                                                
                                                         return var1xx;
                                                      }
                                                   )
                                             )
                                       ))
                                    .then(
                                       ((LiteralArgumentBuilder)Commands.literal("store").then(wrapStores(var2, Commands.literal("result"), true)))
                                          .then(wrapStores(var2, Commands.literal("success"), false))
                                    ))
                                 .then(
                                    ((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("positioned")
                                             .then(
                                                Commands.argument("pos", Vec3Argument.vec3())
                                                   .redirect(
                                                      var2,
                                                      var0x -> ((CommandSourceStack)var0x.getSource())
                                                            .withPosition(Vec3Argument.getVec3(var0x, "pos"))
                                                            .withAnchor(EntityAnchorArgument.Anchor.FEET)
                                                   )
                                             ))
                                          .then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(var2, var0x -> {
                                             ArrayList var1xx = Lists.newArrayList();
                                    
                                             for(Entity var3 : EntityArgument.getOptionalEntities(var0x, "targets")) {
                                                var1xx.add(((CommandSourceStack)var0x.getSource()).withPosition(var3.position()));
                                             }
                                    
                                             return var1xx;
                                          }))))
                                       .then(
                                          Commands.literal("over")
                                             .then(Commands.argument("heightmap", HeightmapTypeArgument.heightmap()).redirect(var2, var0x -> {
                                                Vec3 var1xx = ((CommandSourceStack)var0x.getSource()).getPosition();
                                                ServerLevel var2xx = ((CommandSourceStack)var0x.getSource()).getLevel();
                                                double var3 = var1xx.x();
                                                double var5 = var1xx.z();
                                                if (!var2xx.hasChunk(SectionPos.blockToSectionCoord(var3), SectionPos.blockToSectionCoord(var5))) {
                                                   throw BlockPosArgument.ERROR_NOT_LOADED.create();
                                                } else {
                                                   int var7 = var2xx.getHeight(
                                                      HeightmapTypeArgument.getHeightmap(var0x, "heightmap"), Mth.floor(var3), Mth.floor(var5)
                                                   );
                                                   return ((CommandSourceStack)var0x.getSource()).withPosition(new Vec3(var3, (double)var7, var5));
                                                }
                                             }))
                                       )
                                 ))
                              .then(
                                 ((LiteralArgumentBuilder)Commands.literal("rotated")
                                       .then(
                                          Commands.argument("rot", RotationArgument.rotation())
                                             .redirect(
                                                var2,
                                                var0x -> ((CommandSourceStack)var0x.getSource())
                                                      .withRotation(
                                                         RotationArgument.getRotation(var0x, "rot").getRotation((CommandSourceStack)var0x.getSource())
                                                      )
                                             )
                                       ))
                                    .then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(var2, var0x -> {
                                       ArrayList var1xx = Lists.newArrayList();
                              
                                       for(Entity var3 : EntityArgument.getOptionalEntities(var0x, "targets")) {
                                          var1xx.add(((CommandSourceStack)var0x.getSource()).withRotation(var3.getRotationVector()));
                                       }
                              
                                       return var1xx;
                                    })))
                              ))
                           .then(
                              ((LiteralArgumentBuilder)Commands.literal("facing")
                                    .then(
                                       Commands.literal("entity")
                                          .then(
                                             Commands.argument("targets", EntityArgument.entities())
                                                .then(Commands.argument("anchor", EntityAnchorArgument.anchor()).fork(var2, var0x -> {
                                                   ArrayList var1xx = Lists.newArrayList();
                                                   EntityAnchorArgument.Anchor var2xx = EntityAnchorArgument.getAnchor(var0x, "anchor");
                                          
                                                   for(Entity var4 : EntityArgument.getOptionalEntities(var0x, "targets")) {
                                                      var1xx.add(((CommandSourceStack)var0x.getSource()).facing(var4, var2xx));
                                                   }
                                          
                                                   return var1xx;
                                                }))
                                          )
                                    ))
                                 .then(
                                    Commands.argument("pos", Vec3Argument.vec3())
                                       .redirect(var2, var0x -> ((CommandSourceStack)var0x.getSource()).facing(Vec3Argument.getVec3(var0x, "pos")))
                                 )
                           ))
                        .then(
                           Commands.literal("align")
                              .then(
                                 Commands.argument("axes", SwizzleArgument.swizzle())
                                    .redirect(
                                       var2,
                                       var0x -> ((CommandSourceStack)var0x.getSource())
                                             .withPosition(
                                                ((CommandSourceStack)var0x.getSource()).getPosition().align(SwizzleArgument.getSwizzle(var0x, "axes"))
                                             )
                                    )
                              )
                        ))
                     .then(
                        Commands.literal("anchored")
                           .then(
                              Commands.argument("anchor", EntityAnchorArgument.anchor())
                                 .redirect(var2, var0x -> ((CommandSourceStack)var0x.getSource()).withAnchor(EntityAnchorArgument.getAnchor(var0x, "anchor")))
                           )
                     ))
                  .then(
                     Commands.literal("in")
                        .then(
                           Commands.argument("dimension", DimensionArgument.dimension())
                              .redirect(var2, var0x -> ((CommandSourceStack)var0x.getSource()).withLevel(DimensionArgument.getDimension(var0x, "dimension")))
                        )
                  ))
               .then(
                  Commands.literal("summon")
                     .then(
                        Commands.argument("entity", ResourceArgument.resource(var1, Registries.ENTITY_TYPE))
                           .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                           .redirect(
                              var2,
                              var0x -> spawnEntityAndRedirect((CommandSourceStack)var0x.getSource(), ResourceArgument.getSummonableEntityType(var0x, "entity"))
                           )
                     )
               ))
            .then(createRelationOperations(var2, Commands.literal("on")))
      );
   }

   private static ArgumentBuilder<CommandSourceStack, ?> wrapStores(
      LiteralCommandNode<CommandSourceStack> var0, LiteralArgumentBuilder<CommandSourceStack> var1, boolean var2
   ) {
      var1.then(
         Commands.literal("score")
            .then(
               Commands.argument("targets", ScoreHolderArgument.scoreHolders())
                  .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                  .then(
                     Commands.argument("objective", ObjectiveArgument.objective())
                        .redirect(
                           var0,
                           var1x -> storeValue(
                                 (CommandSourceStack)var1x.getSource(),
                                 ScoreHolderArgument.getNamesWithDefaultWildcard(var1x, "targets"),
                                 ObjectiveArgument.getObjective(var1x, "objective"),
                                 var2
                              )
                        )
                  )
            )
      );
      var1.then(
         Commands.literal("bossbar")
            .then(
               ((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id())
                     .suggests(BossBarCommands.SUGGEST_BOSS_BAR)
                     .then(
                        Commands.literal("value")
                           .redirect(var0, var1x -> storeValue((CommandSourceStack)var1x.getSource(), BossBarCommands.getBossBar(var1x), true, var2))
                     ))
                  .then(
                     Commands.literal("max")
                        .redirect(var0, var1x -> storeValue((CommandSourceStack)var1x.getSource(), BossBarCommands.getBossBar(var1x), false, var2))
                  )
            )
      );

      for(DataCommands.DataProvider var4 : DataCommands.TARGET_PROVIDERS) {
         var4.wrap(
            var1,
            var3 -> var3.then(
                  ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument(
                                       "path", NbtPathArgument.nbtPath()
                                    )
                                    .then(
                                       Commands.literal("int")
                                          .then(
                                             Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                .redirect(
                                                   var0,
                                                   var2xx -> storeData(
                                                         (CommandSourceStack)var2xx.getSource(),
                                                         var4.access(var2xx),
                                                         NbtPathArgument.getPath(var2xx, "path"),
                                                         var1xxx -> IntTag.valueOf((int)((double)var1xxx * DoubleArgumentType.getDouble(var2xx, "scale"))),
                                                         var2
                                                      )
                                                )
                                          )
                                    ))
                                 .then(
                                    Commands.literal("float")
                                       .then(
                                          Commands.argument("scale", DoubleArgumentType.doubleArg())
                                             .redirect(
                                                var0,
                                                var2xx -> storeData(
                                                      (CommandSourceStack)var2xx.getSource(),
                                                      var4.access(var2xx),
                                                      NbtPathArgument.getPath(var2xx, "path"),
                                                      var1xxx -> FloatTag.valueOf((float)((double)var1xxx * DoubleArgumentType.getDouble(var2xx, "scale"))),
                                                      var2
                                                   )
                                             )
                                       )
                                 ))
                              .then(
                                 Commands.literal("short")
                                    .then(
                                       Commands.argument("scale", DoubleArgumentType.doubleArg())
                                          .redirect(
                                             var0,
                                             var2xx -> storeData(
                                                   (CommandSourceStack)var2xx.getSource(),
                                                   var4.access(var2xx),
                                                   NbtPathArgument.getPath(var2xx, "path"),
                                                   var1xxx -> ShortTag.valueOf((short)((int)((double)var1xxx * DoubleArgumentType.getDouble(var2xx, "scale")))),
                                                   var2
                                                )
                                          )
                                    )
                              ))
                           .then(
                              Commands.literal("long")
                                 .then(
                                    Commands.argument("scale", DoubleArgumentType.doubleArg())
                                       .redirect(
                                          var0,
                                          var2xx -> storeData(
                                                (CommandSourceStack)var2xx.getSource(),
                                                var4.access(var2xx),
                                                NbtPathArgument.getPath(var2xx, "path"),
                                                var1xxx -> LongTag.valueOf((long)((double)var1xxx * DoubleArgumentType.getDouble(var2xx, "scale"))),
                                                var2
                                             )
                                       )
                                 )
                           ))
                        .then(
                           Commands.literal("double")
                              .then(
                                 Commands.argument("scale", DoubleArgumentType.doubleArg())
                                    .redirect(
                                       var0,
                                       var2xx -> storeData(
                                             (CommandSourceStack)var2xx.getSource(),
                                             var4.access(var2xx),
                                             NbtPathArgument.getPath(var2xx, "path"),
                                             var1xxx -> DoubleTag.valueOf((double)var1xxx * DoubleArgumentType.getDouble(var2xx, "scale")),
                                             var2
                                          )
                                    )
                              )
                        ))
                     .then(
                        Commands.literal("byte")
                           .then(
                              Commands.argument("scale", DoubleArgumentType.doubleArg())
                                 .redirect(
                                    var0,
                                    var2xx -> storeData(
                                          (CommandSourceStack)var2xx.getSource(),
                                          var4.access(var2xx),
                                          NbtPathArgument.getPath(var2xx, "path"),
                                          var1xxx -> ByteTag.valueOf((byte)((int)((double)var1xxx * DoubleArgumentType.getDouble(var2xx, "scale")))),
                                          var2
                                       )
                                 )
                           )
                     )
               )
         );
      }

      return var1;
   }

   private static CommandSourceStack storeValue(CommandSourceStack var0, Collection<ScoreHolder> var1, Objective var2, boolean var3) {
      ServerScoreboard var4 = var0.getServer().getScoreboard();
      return var0.withCallback((var4x, var5) -> {
         for(ScoreHolder var7 : var1) {
            ScoreAccess var8 = var4.getOrCreatePlayerScore(var7, var2);
            int var9 = var3 ? var5 : (var4x ? 1 : 0);
            var8.set(var9);
         }
      }, CommandResultCallback::chain);
   }

   private static CommandSourceStack storeValue(CommandSourceStack var0, CustomBossEvent var1, boolean var2, boolean var3) {
      return var0.withCallback((var3x, var4) -> {
         int var5 = var3 ? var4 : (var3x ? 1 : 0);
         if (var2) {
            var1.setValue(var5);
         } else {
            var1.setMax(var5);
         }
      }, CommandResultCallback::chain);
   }

   private static CommandSourceStack storeData(CommandSourceStack var0, DataAccessor var1, NbtPathArgument.NbtPath var2, IntFunction<Tag> var3, boolean var4) {
      return var0.withCallback((var4x, var5) -> {
         try {
            CompoundTag var6 = var1.getData();
            int var7 = var4 ? var5 : (var4x ? 1 : 0);
            var2.set(var6, (Tag)var3.apply(var7));
            var1.setData(var6);
         } catch (CommandSyntaxException var8) {
         }
      }, CommandResultCallback::chain);
   }

   private static boolean isChunkLoaded(ServerLevel var0, BlockPos var1) {
      ChunkPos var2 = new ChunkPos(var1);
      LevelChunk var3 = var0.getChunkSource().getChunkNow(var2.x, var2.z);
      if (var3 == null) {
         return false;
      } else {
         return var3.getFullStatus() == FullChunkStatus.ENTITY_TICKING && var0.areEntitiesLoaded(var2.toLong());
      }
   }

   private static ArgumentBuilder<CommandSourceStack, ?> addConditionals(
      CommandNode<CommandSourceStack> var0, LiteralArgumentBuilder<CommandSourceStack> var1, boolean var2, CommandBuildContext var3
   ) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)var1.then(
                                    Commands.literal("block")
                                       .then(
                                          Commands.argument("pos", BlockPosArgument.blockPos())
                                             .then(
                                                addConditional(
                                                   var0,
                                                   Commands.argument("block", BlockPredicateArgument.blockPredicate(var3)),
                                                   var2,
                                                   var0x -> BlockPredicateArgument.getBlockPredicate(var0x, "block")
                                                         .test(
                                                            new BlockInWorld(
                                                               ((CommandSourceStack)var0x.getSource()).getLevel(),
                                                               BlockPosArgument.getLoadedBlockPos(var0x, "pos"),
                                                               true
                                                            )
                                                         )
                                                )
                                             )
                                       )
                                 ))
                                 .then(
                                    Commands.literal("biome")
                                       .then(
                                          Commands.argument("pos", BlockPosArgument.blockPos())
                                             .then(
                                                addConditional(
                                                   var0,
                                                   Commands.argument("biome", ResourceOrTagArgument.resourceOrTag(var3, Registries.BIOME)),
                                                   var2,
                                                   var0x -> ResourceOrTagArgument.getResourceOrTag(var0x, "biome", Registries.BIOME)
                                                         .test(
                                                            ((CommandSourceStack)var0x.getSource())
                                                               .getLevel()
                                                               .getBiome(BlockPosArgument.getLoadedBlockPos(var0x, "pos"))
                                                         )
                                                )
                                             )
                                       )
                                 ))
                              .then(
                                 Commands.literal("loaded")
                                    .then(
                                       addConditional(
                                          var0,
                                          Commands.argument("pos", BlockPosArgument.blockPos()),
                                          var2,
                                          var0x -> isChunkLoaded(((CommandSourceStack)var0x.getSource()).getLevel(), BlockPosArgument.getBlockPos(var0x, "pos"))
                                       )
                                    )
                              ))
                           .then(
                              Commands.literal("dimension")
                                 .then(
                                    addConditional(
                                       var0,
                                       Commands.argument("dimension", DimensionArgument.dimension()),
                                       var2,
                                       var0x -> DimensionArgument.getDimension(var0x, "dimension") == ((CommandSourceStack)var0x.getSource()).getLevel()
                                    )
                                 )
                           ))
                        .then(
                           Commands.literal("score")
                              .then(
                                 Commands.argument("target", ScoreHolderArgument.scoreHolder())
                                    .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                    .then(
                                       ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument(
                                                            "targetObjective", ObjectiveArgument.objective()
                                                         )
                                                         .then(
                                                            Commands.literal("=")
                                                               .then(
                                                                  Commands.argument("source", ScoreHolderArgument.scoreHolder())
                                                                     .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                                                     .then(
                                                                        addConditional(
                                                                           var0,
                                                                           Commands.argument("sourceObjective", ObjectiveArgument.objective()),
                                                                           var2,
                                                                           var0x -> checkScore(var0x, (var0xx, var1x) -> var0xx == var1x)
                                                                        )
                                                                     )
                                                               )
                                                         ))
                                                      .then(
                                                         Commands.literal("<")
                                                            .then(
                                                               Commands.argument("source", ScoreHolderArgument.scoreHolder())
                                                                  .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                                                  .then(
                                                                     addConditional(
                                                                        var0,
                                                                        Commands.argument("sourceObjective", ObjectiveArgument.objective()),
                                                                        var2,
                                                                        var0x -> checkScore(var0x, (var0xx, var1x) -> var0xx < var1x)
                                                                     )
                                                                  )
                                                            )
                                                      ))
                                                   .then(
                                                      Commands.literal("<=")
                                                         .then(
                                                            Commands.argument("source", ScoreHolderArgument.scoreHolder())
                                                               .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                                               .then(
                                                                  addConditional(
                                                                     var0,
                                                                     Commands.argument("sourceObjective", ObjectiveArgument.objective()),
                                                                     var2,
                                                                     var0x -> checkScore(var0x, (var0xx, var1x) -> var0xx <= var1x)
                                                                  )
                                                               )
                                                         )
                                                   ))
                                                .then(
                                                   Commands.literal(">")
                                                      .then(
                                                         Commands.argument("source", ScoreHolderArgument.scoreHolder())
                                                            .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                                            .then(
                                                               addConditional(
                                                                  var0,
                                                                  Commands.argument("sourceObjective", ObjectiveArgument.objective()),
                                                                  var2,
                                                                  var0x -> checkScore(var0x, (var0xx, var1x) -> var0xx > var1x)
                                                               )
                                                            )
                                                      )
                                                ))
                                             .then(
                                                Commands.literal(">=")
                                                   .then(
                                                      Commands.argument("source", ScoreHolderArgument.scoreHolder())
                                                         .suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS)
                                                         .then(
                                                            addConditional(
                                                               var0,
                                                               Commands.argument("sourceObjective", ObjectiveArgument.objective()),
                                                               var2,
                                                               var0x -> checkScore(var0x, (var0xx, var1x) -> var0xx >= var1x)
                                                            )
                                                         )
                                                   )
                                             ))
                                          .then(
                                             Commands.literal("matches")
                                                .then(
                                                   addConditional(
                                                      var0,
                                                      Commands.argument("range", RangeArgument.intRange()),
                                                      var2,
                                                      var0x -> checkScore(var0x, RangeArgument.Ints.getRange(var0x, "range"))
                                                   )
                                                )
                                          )
                                    )
                              )
                        ))
                     .then(
                        Commands.literal("blocks")
                           .then(
                              Commands.argument("start", BlockPosArgument.blockPos())
                                 .then(
                                    Commands.argument("end", BlockPosArgument.blockPos())
                                       .then(
                                          ((RequiredArgumentBuilder)Commands.argument("destination", BlockPosArgument.blockPos())
                                                .then(addIfBlocksConditional(var0, Commands.literal("all"), var2, false)))
                                             .then(addIfBlocksConditional(var0, Commands.literal("masked"), var2, true))
                                       )
                                 )
                           )
                     ))
                  .then(
                     Commands.literal("entity")
                        .then(
                           ((RequiredArgumentBuilder)Commands.argument("entities", EntityArgument.entities())
                                 .fork(var0, var1x -> expect(var1x, var2, !EntityArgument.getOptionalEntities(var1x, "entities").isEmpty())))
                              .executes(createNumericConditionalHandler(var2, var0x -> EntityArgument.getOptionalEntities(var0x, "entities").size()))
                        )
                  ))
               .then(
                  Commands.literal("predicate")
                     .then(
                        addConditional(
                           var0,
                           Commands.argument("predicate", ResourceLocationArgument.id()).suggests(SUGGEST_PREDICATE),
                           var2,
                           var0x -> checkCustomPredicate((CommandSourceStack)var0x.getSource(), ResourceLocationArgument.getPredicate(var0x, "predicate"))
                        )
                     )
               ))
            .then(
               Commands.literal("function")
                  .then(
                     Commands.argument("name", FunctionArgument.functions())
                        .suggests(FunctionCommand.SUGGEST_FUNCTION)
                        .fork(var0, new ExecuteCommand.ExecuteIfFunctionCustomModifier(var2))
                  )
            ))
         .then(
            ((LiteralArgumentBuilder)Commands.literal("items")
                  .then(
                     Commands.literal("entity")
                        .then(
                           Commands.argument("entities", EntityArgument.entities())
                              .then(
                                 Commands.argument("slots", SlotsArgument.slots())
                                    .then(
                                       ((RequiredArgumentBuilder)Commands.argument("item_predicate", ItemPredicateArgument.itemPredicate(var3))
                                             .fork(
                                                var0,
                                                var1x -> expect(
                                                      var1x,
                                                      var2,
                                                      countItems(
                                                            EntityArgument.getEntities(var1x, "entities"),
                                                            SlotsArgument.getSlots(var1x, "slots"),
                                                            ItemPredicateArgument.getItemPredicate(var1x, "item_predicate")
                                                         )
                                                         > 0
                                                   )
                                             ))
                                          .executes(
                                             createNumericConditionalHandler(
                                                var2,
                                                var0x -> countItems(
                                                      EntityArgument.getEntities(var0x, "entities"),
                                                      SlotsArgument.getSlots(var0x, "slots"),
                                                      ItemPredicateArgument.getItemPredicate(var0x, "item_predicate")
                                                   )
                                             )
                                          )
                                    )
                              )
                        )
                  ))
               .then(
                  Commands.literal("block")
                     .then(
                        Commands.argument("pos", BlockPosArgument.blockPos())
                           .then(
                              Commands.argument("slots", SlotsArgument.slots())
                                 .then(
                                    ((RequiredArgumentBuilder)Commands.argument("item_predicate", ItemPredicateArgument.itemPredicate(var3))
                                          .fork(
                                             var0,
                                             var1x -> expect(
                                                   var1x,
                                                   var2,
                                                   countItems(
                                                         (CommandSourceStack)var1x.getSource(),
                                                         BlockPosArgument.getLoadedBlockPos(var1x, "pos"),
                                                         SlotsArgument.getSlots(var1x, "slots"),
                                                         ItemPredicateArgument.getItemPredicate(var1x, "item_predicate")
                                                      )
                                                      > 0
                                                )
                                          ))
                                       .executes(
                                          createNumericConditionalHandler(
                                             var2,
                                             var0x -> countItems(
                                                   (CommandSourceStack)var0x.getSource(),
                                                   BlockPosArgument.getLoadedBlockPos(var0x, "pos"),
                                                   SlotsArgument.getSlots(var0x, "slots"),
                                                   ItemPredicateArgument.getItemPredicate(var0x, "item_predicate")
                                                )
                                          )
                                       )
                                 )
                           )
                     )
               )
         );

      for(DataCommands.DataProvider var5 : DataCommands.SOURCE_PROVIDERS) {
         var1.then(
            var5.wrap(
               Commands.literal("data"),
               var3x -> var3x.then(
                     ((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath())
                           .fork(var0, var2xx -> expect(var2xx, var2, checkMatchingData(var5.access(var2xx), NbtPathArgument.getPath(var2xx, "path")) > 0)))
                        .executes(
                           createNumericConditionalHandler(var2, var1xx -> checkMatchingData(var5.access(var1xx), NbtPathArgument.getPath(var1xx, "path")))
                        )
                  )
            )
         );
      }

      return var1;
   }

   private static int countItems(Iterable<? extends Entity> var0, SlotRange var1, Predicate<ItemStack> var2) {
      int var3 = 0;

      for(Entity var5 : var0) {
         IntList var6 = var1.slots();

         for(int var7 = 0; var7 < var6.size(); ++var7) {
            int var8 = var6.getInt(var7);
            SlotAccess var9 = var5.getSlot(var8);
            ItemStack var10 = var9.get();
            if (var2.test(var10)) {
               var3 += var10.getCount();
            }
         }
      }

      return var3;
   }

   private static int countItems(CommandSourceStack var0, BlockPos var1, SlotRange var2, Predicate<ItemStack> var3) throws CommandSyntaxException {
      int var4 = 0;
      Container var5 = ItemCommands.getContainer(var0, var1, ItemCommands.ERROR_SOURCE_NOT_A_CONTAINER);
      int var6 = var5.getContainerSize();
      IntList var7 = var2.slots();

      for(int var8 = 0; var8 < var7.size(); ++var8) {
         int var9 = var7.getInt(var8);
         if (var9 >= 0 && var9 < var6) {
            ItemStack var10 = var5.getItem(var9);
            if (var3.test(var10)) {
               var4 += var10.getCount();
            }
         }
      }

      return var4;
   }

   private static Command<CommandSourceStack> createNumericConditionalHandler(boolean var0, ExecuteCommand.CommandNumericPredicate var1) {
      return var0 ? var1x -> {
         int var2 = var1.test(var1x);
         if (var2 > 0) {
            ((CommandSourceStack)var1x.getSource()).sendSuccess(() -> Component.translatable("commands.execute.conditional.pass_count", var2), false);
            return var2;
         } else {
            throw ERROR_CONDITIONAL_FAILED.create();
         }
      } : var1x -> {
         int var2 = var1.test(var1x);
         if (var2 == 0) {
            ((CommandSourceStack)var1x.getSource()).sendSuccess(() -> Component.translatable("commands.execute.conditional.pass"), false);
            return 1;
         } else {
            throw ERROR_CONDITIONAL_FAILED_COUNT.create(var2);
         }
      };
   }

   private static int checkMatchingData(DataAccessor var0, NbtPathArgument.NbtPath var1) throws CommandSyntaxException {
      return var1.countMatching(var0.getData());
   }

   private static boolean checkScore(CommandContext<CommandSourceStack> var0, ExecuteCommand.IntBiPredicate var1) throws CommandSyntaxException {
      ScoreHolder var2 = ScoreHolderArgument.getName(var0, "target");
      Objective var3 = ObjectiveArgument.getObjective(var0, "targetObjective");
      ScoreHolder var4 = ScoreHolderArgument.getName(var0, "source");
      Objective var5 = ObjectiveArgument.getObjective(var0, "sourceObjective");
      ServerScoreboard var6 = ((CommandSourceStack)var0.getSource()).getServer().getScoreboard();
      ReadOnlyScoreInfo var7 = var6.getPlayerScoreInfo(var2, var3);
      ReadOnlyScoreInfo var8 = var6.getPlayerScoreInfo(var4, var5);
      return var7 != null && var8 != null ? var1.test(var7.value(), var8.value()) : false;
   }

   private static boolean checkScore(CommandContext<CommandSourceStack> var0, MinMaxBounds.Ints var1) throws CommandSyntaxException {
      ScoreHolder var2 = ScoreHolderArgument.getName(var0, "target");
      Objective var3 = ObjectiveArgument.getObjective(var0, "targetObjective");
      ServerScoreboard var4 = ((CommandSourceStack)var0.getSource()).getServer().getScoreboard();
      ReadOnlyScoreInfo var5 = var4.getPlayerScoreInfo(var2, var3);
      return var5 == null ? false : var1.matches(var5.value());
   }

   private static boolean checkCustomPredicate(CommandSourceStack var0, LootItemCondition var1) {
      ServerLevel var2 = var0.getLevel();
      LootParams var3 = new LootParams.Builder(var2)
         .withParameter(LootContextParams.ORIGIN, var0.getPosition())
         .withOptionalParameter(LootContextParams.THIS_ENTITY, var0.getEntity())
         .create(LootContextParamSets.COMMAND);
      LootContext var4 = new LootContext.Builder(var3).create(Optional.empty());
      var4.pushVisitedElement(LootContext.createVisitedEntry(var1));
      return var1.test(var4);
   }

   private static Collection<CommandSourceStack> expect(CommandContext<CommandSourceStack> var0, boolean var1, boolean var2) {
      return (Collection<CommandSourceStack>)(var2 == var1 ? Collections.singleton((CommandSourceStack)var0.getSource()) : Collections.emptyList());
   }

   private static ArgumentBuilder<CommandSourceStack, ?> addConditional(
      CommandNode<CommandSourceStack> var0, ArgumentBuilder<CommandSourceStack, ?> var1, boolean var2, ExecuteCommand.CommandPredicate var3
   ) {
      return var1.fork(var0, var2x -> expect(var2x, var2, var3.test(var2x))).executes(var2x -> {
         if (var2 == var3.test(var2x)) {
            ((CommandSourceStack)var2x.getSource()).sendSuccess(() -> Component.translatable("commands.execute.conditional.pass"), false);
            return 1;
         } else {
            throw ERROR_CONDITIONAL_FAILED.create();
         }
      });
   }

   private static ArgumentBuilder<CommandSourceStack, ?> addIfBlocksConditional(
      CommandNode<CommandSourceStack> var0, ArgumentBuilder<CommandSourceStack, ?> var1, boolean var2, boolean var3
   ) {
      return var1.fork(var0, var2x -> expect(var2x, var2, checkRegions(var2x, var3).isPresent()))
         .executes(var2 ? var1x -> checkIfRegions(var1x, var3) : var1x -> checkUnlessRegions(var1x, var3));
   }

   private static int checkIfRegions(CommandContext<CommandSourceStack> var0, boolean var1) throws CommandSyntaxException {
      OptionalInt var2 = checkRegions(var0, var1);
      if (var2.isPresent()) {
         ((CommandSourceStack)var0.getSource()).sendSuccess(() -> Component.translatable("commands.execute.conditional.pass_count", var2.getAsInt()), false);
         return var2.getAsInt();
      } else {
         throw ERROR_CONDITIONAL_FAILED.create();
      }
   }

   private static int checkUnlessRegions(CommandContext<CommandSourceStack> var0, boolean var1) throws CommandSyntaxException {
      OptionalInt var2 = checkRegions(var0, var1);
      if (var2.isPresent()) {
         throw ERROR_CONDITIONAL_FAILED_COUNT.create(var2.getAsInt());
      } else {
         ((CommandSourceStack)var0.getSource()).sendSuccess(() -> Component.translatable("commands.execute.conditional.pass"), false);
         return 1;
      }
   }

   private static OptionalInt checkRegions(CommandContext<CommandSourceStack> var0, boolean var1) throws CommandSyntaxException {
      return checkRegions(
         ((CommandSourceStack)var0.getSource()).getLevel(),
         BlockPosArgument.getLoadedBlockPos(var0, "start"),
         BlockPosArgument.getLoadedBlockPos(var0, "end"),
         BlockPosArgument.getLoadedBlockPos(var0, "destination"),
         var1
      );
   }

   private static OptionalInt checkRegions(ServerLevel var0, BlockPos var1, BlockPos var2, BlockPos var3, boolean var4) throws CommandSyntaxException {
      BoundingBox var5 = BoundingBox.fromCorners(var1, var2);
      BoundingBox var6 = BoundingBox.fromCorners(var3, var3.offset(var5.getLength()));
      BlockPos var7 = new BlockPos(var6.minX() - var5.minX(), var6.minY() - var5.minY(), var6.minZ() - var5.minZ());
      int var8 = var5.getXSpan() * var5.getYSpan() * var5.getZSpan();
      if (var8 > 32768) {
         throw ERROR_AREA_TOO_LARGE.create(32768, var8);
      } else {
         RegistryAccess var9 = var0.registryAccess();
         int var10 = 0;

         for(int var11 = var5.minZ(); var11 <= var5.maxZ(); ++var11) {
            for(int var12 = var5.minY(); var12 <= var5.maxY(); ++var12) {
               for(int var13 = var5.minX(); var13 <= var5.maxX(); ++var13) {
                  BlockPos var14 = new BlockPos(var13, var12, var11);
                  BlockPos var15 = var14.offset(var7);
                  BlockState var16 = var0.getBlockState(var14);
                  if (!var4 || !var16.is(Blocks.AIR)) {
                     if (var16 != var0.getBlockState(var15)) {
                        return OptionalInt.empty();
                     }

                     BlockEntity var17 = var0.getBlockEntity(var14);
                     BlockEntity var18 = var0.getBlockEntity(var15);
                     if (var17 != null) {
                        if (var18 == null) {
                           return OptionalInt.empty();
                        }

                        if (var18.getType() != var17.getType()) {
                           return OptionalInt.empty();
                        }

                        CompoundTag var19 = var17.saveWithoutMetadata(var9);
                        CompoundTag var20 = var18.saveWithoutMetadata(var9);
                        if (!var19.equals(var20)) {
                           return OptionalInt.empty();
                        }
                     }

                     ++var10;
                  }
               }
            }
         }

         return OptionalInt.of(var10);
      }
   }

   private static RedirectModifier<CommandSourceStack> expandOneToOneEntityRelation(Function<Entity, Optional<Entity>> var0) {
      return var1 -> {
         CommandSourceStack var2 = (CommandSourceStack)var1.getSource();
         Entity var3 = var2.getEntity();
         return (Collection)(var3 == null
            ? List.of()
            : ((Optional)var0.apply(var3)).filter(var0xx -> !var0xx.isRemoved()).map(var1x -> List.of(var2.withEntity(var1x))).orElse(List.of()));
      };
   }

   private static RedirectModifier<CommandSourceStack> expandOneToManyEntityRelation(Function<Entity, Stream<Entity>> var0) {
      return var1 -> {
         CommandSourceStack var2 = (CommandSourceStack)var1.getSource();
         Entity var3 = var2.getEntity();
         return var3 == null ? List.of() : ((Stream)var0.apply(var3)).filter(var0xx -> !var0xx.isRemoved()).map(var2::withEntity).toList();
      };
   }

   private static LiteralArgumentBuilder<CommandSourceStack> createRelationOperations(
      CommandNode<CommandSourceStack> var0, LiteralArgumentBuilder<CommandSourceStack> var1
   ) {
      return (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)var1.then(
                              Commands.literal("owner")
                                 .fork(
                                    var0,
                                    expandOneToOneEntityRelation(
                                       var0x -> var0x instanceof OwnableEntity var1xx ? Optional.ofNullable(var1xx.getOwner()) : Optional.empty()
                                    )
                                 )
                           ))
                           .then(
                              Commands.literal("leasher")
                                 .fork(
                                    var0,
                                    expandOneToOneEntityRelation(
                                       var0x -> var0x instanceof Mob var1xx ? Optional.ofNullable(var1xx.getLeashHolder()) : Optional.empty()
                                    )
                                 )
                           ))
                        .then(
                           Commands.literal("target")
                              .fork(
                                 var0,
                                 expandOneToOneEntityRelation(
                                    var0x -> var0x instanceof Targeting var1xx ? Optional.ofNullable(var1xx.getTarget()) : Optional.empty()
                                 )
                              )
                        ))
                     .then(
                        Commands.literal("attacker")
                           .fork(
                              var0,
                              expandOneToOneEntityRelation(
                                 var0x -> var0x instanceof Attackable var1xx ? Optional.ofNullable(var1xx.getLastAttacker()) : Optional.empty()
                              )
                           )
                     ))
                  .then(Commands.literal("vehicle").fork(var0, expandOneToOneEntityRelation(var0x -> Optional.ofNullable(var0x.getVehicle())))))
               .then(Commands.literal("controller").fork(var0, expandOneToOneEntityRelation(var0x -> Optional.ofNullable(var0x.getControllingPassenger())))))
            .then(
               Commands.literal("origin")
                  .fork(
                     var0,
                     expandOneToOneEntityRelation(var0x -> var0x instanceof TraceableEntity var1xx ? Optional.ofNullable(var1xx.getOwner()) : Optional.empty())
                  )
            ))
         .then(Commands.literal("passengers").fork(var0, expandOneToManyEntityRelation(var0x -> var0x.getPassengers().stream())));
   }

   private static CommandSourceStack spawnEntityAndRedirect(CommandSourceStack var0, Holder.Reference<EntityType<?>> var1) throws CommandSyntaxException {
      Entity var2 = SummonCommand.createEntity(var0, var1, var0.getPosition(), new CompoundTag(), true);
      return var0.withEntity(var2);
   }

   public static <T extends ExecutionCommandSource<T>> void scheduleFunctionConditionsAndTest(
      T var0,
      List<T> var1,
      Function<T, T> var2,
      IntPredicate var3,
      ContextChain<T> var4,
      @Nullable CompoundTag var5,
      ExecutionControl<T> var6,
      ExecuteCommand.CommandGetter<T, Collection<CommandFunction<T>>> var7,
      ChainModifiers var8
   ) {
      ArrayList var9 = new ArrayList(var1.size());

      Collection var10;
      try {
         var10 = (Collection)var7.get(var4.getTopContext().copyFor(var0));
      } catch (CommandSyntaxException var18) {
         var0.handleError(var18, var8.isForked(), var6.tracer());
         return;
      }

      int var11 = var10.size();
      if (var11 != 0) {
         ArrayList var12 = new ArrayList(var11);

         try {
            for(CommandFunction var14 : var10) {
               try {
                  var12.add(var14.instantiate(var5, var0.dispatcher()));
               } catch (FunctionInstantiationException var17) {
                  throw ERROR_FUNCTION_CONDITION_INSTANTATION_FAILURE.create(var14.id(), var17.messageComponent());
               }
            }
         } catch (CommandSyntaxException var19) {
            var0.handleError(var19, var8.isForked(), var6.tracer());
         }

         for(ExecutionCommandSource var22 : var1) {
            ExecutionCommandSource var15 = (ExecutionCommandSource)var2.apply(var22.clearCallbacks());
            CommandResultCallback var16 = (var3x, var4x) -> {
               if (var3.test(var4x)) {
                  var9.add(var22);
               }
            };
            var6.queueNext(new IsolatedCall<>(var2x -> {
               for(InstantiatedFunction var4xx : var12) {
                  var2x.queueNext(new CallFunction<T>(var4xx, var2x.currentFrame().returnValueConsumer(), true).bind((T)var15));
               }

               var2x.queueNext(FallthroughTask.instance());
            }, var16));
         }

         ContextChain var21 = var4.nextStage();
         String var23 = var4.getTopContext().getInput();
         var6.queueNext(new BuildContexts.Continuation<>(var23, var21, var8, (T)var0, var9));
      }
   }

   @FunctionalInterface
   public interface CommandGetter<T, R> {
      R get(CommandContext<T> var1) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface CommandNumericPredicate {
      int test(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface CommandPredicate {
      boolean test(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;
   }

   static class ExecuteIfFunctionCustomModifier implements CustomModifierExecutor.ModifierAdapter<CommandSourceStack> {
      private final IntPredicate check;

      ExecuteIfFunctionCustomModifier(boolean var1) {
         super();
         this.check = var1 ? var0 -> var0 != 0 : var0 -> var0 == 0;
      }

      public void apply(
         CommandSourceStack var1,
         List<CommandSourceStack> var2,
         ContextChain<CommandSourceStack> var3,
         ChainModifiers var4,
         ExecutionControl<CommandSourceStack> var5
      ) {
         ExecuteCommand.scheduleFunctionConditionsAndTest(
            var1, var2, FunctionCommand::modifySenderForExecution, this.check, var3, null, var5, var0 -> FunctionArgument.getFunctions(var0, "name"), var4
         );
      }
   }

   @FunctionalInterface
   interface IntBiPredicate {
      boolean test(int var1, int var2);
   }
}
