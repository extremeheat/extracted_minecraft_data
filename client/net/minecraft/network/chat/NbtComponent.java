package net.minecraft.network.chat;

import com.google.common.base.Joiner;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class NbtComponent extends BaseComponent implements ContextAwareComponent {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final boolean interpreting;
   protected final String nbtPathPattern;
   @Nullable
   protected final NbtPathArgument.NbtPath compiledNbtPath;

   @Nullable
   private static NbtPathArgument.NbtPath compileNbtPath(String var0) {
      try {
         return (new NbtPathArgument()).parse(new StringReader(var0));
      } catch (CommandSyntaxException var2) {
         return null;
      }
   }

   public NbtComponent(String var1, boolean var2) {
      this(var1, compileNbtPath(var1), var2);
   }

   protected NbtComponent(String var1, @Nullable NbtPathArgument.NbtPath var2, boolean var3) {
      super();
      this.nbtPathPattern = var1;
      this.compiledNbtPath = var2;
      this.interpreting = var3;
   }

   protected abstract Stream<CompoundTag> getData(CommandSourceStack var1) throws CommandSyntaxException;

   public String getNbtPath() {
      return this.nbtPathPattern;
   }

   public boolean isInterpreting() {
      return this.interpreting;
   }

   public MutableComponent resolve(@Nullable CommandSourceStack var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      if (var1 != null && this.compiledNbtPath != null) {
         Stream var4 = this.getData(var1).flatMap((var1x) -> {
            try {
               return this.compiledNbtPath.get(var1x).stream();
            } catch (CommandSyntaxException var3) {
               return Stream.empty();
            }
         }).map(Tag::getAsString);
         return (MutableComponent)(this.interpreting ? (MutableComponent)var4.flatMap((var3x) -> {
            try {
               MutableComponent var4 = Component.Serializer.fromJson(var3x);
               return Stream.of(ComponentUtils.updateForEntity(var1, var4, var2, var3));
            } catch (Exception var5) {
               LOGGER.warn("Failed to parse component: " + var3x, var5);
               return Stream.of();
            }
         }).reduce((var0, var1x) -> {
            return var0.append(", ").append((Component)var1x);
         }).orElse(new TextComponent("")) : new TextComponent(Joiner.on(", ").join(var4.iterator())));
      } else {
         return new TextComponent("");
      }
   }

   public static class StorageNbtComponent extends NbtComponent {
      private final ResourceLocation id;

      public StorageNbtComponent(String var1, boolean var2, ResourceLocation var3) {
         super(var1, var2);
         this.id = var3;
      }

      public StorageNbtComponent(String var1, @Nullable NbtPathArgument.NbtPath var2, boolean var3, ResourceLocation var4) {
         super(var1, var2, var3);
         this.id = var4;
      }

      public ResourceLocation getId() {
         return this.id;
      }

      public NbtComponent.StorageNbtComponent plainCopy() {
         return new NbtComponent.StorageNbtComponent(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.id);
      }

      protected Stream<CompoundTag> getData(CommandSourceStack var1) {
         CompoundTag var2 = var1.getServer().getCommandStorage().get(this.id);
         return Stream.of(var2);
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof NbtComponent.StorageNbtComponent)) {
            return false;
         } else {
            NbtComponent.StorageNbtComponent var2 = (NbtComponent.StorageNbtComponent)var1;
            return Objects.equals(this.id, var2.id) && Objects.equals(this.nbtPathPattern, var2.nbtPathPattern) && super.equals(var1);
         }
      }

      public String toString() {
         return "StorageNbtComponent{id='" + this.id + '\'' + "path='" + this.nbtPathPattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
      }

      // $FF: synthetic method
      public BaseComponent plainCopy() {
         return this.plainCopy();
      }

      // $FF: synthetic method
      public MutableComponent plainCopy() {
         return this.plainCopy();
      }
   }

   public static class BlockNbtComponent extends NbtComponent {
      private final String posPattern;
      @Nullable
      private final Coordinates compiledPos;

      public BlockNbtComponent(String var1, boolean var2, String var3) {
         super(var1, var2);
         this.posPattern = var3;
         this.compiledPos = this.compilePos(this.posPattern);
      }

      @Nullable
      private Coordinates compilePos(String var1) {
         try {
            return BlockPosArgument.blockPos().parse(new StringReader(var1));
         } catch (CommandSyntaxException var3) {
            return null;
         }
      }

      private BlockNbtComponent(String var1, @Nullable NbtPathArgument.NbtPath var2, boolean var3, String var4, @Nullable Coordinates var5) {
         super(var1, var2, var3);
         this.posPattern = var4;
         this.compiledPos = var5;
      }

      @Nullable
      public String getPos() {
         return this.posPattern;
      }

      public NbtComponent.BlockNbtComponent plainCopy() {
         return new NbtComponent.BlockNbtComponent(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.posPattern, this.compiledPos);
      }

      protected Stream<CompoundTag> getData(CommandSourceStack var1) {
         if (this.compiledPos != null) {
            ServerLevel var2 = var1.getLevel();
            BlockPos var3 = this.compiledPos.getBlockPos(var1);
            if (var2.isLoaded(var3)) {
               BlockEntity var4 = var2.getBlockEntity(var3);
               if (var4 != null) {
                  return Stream.of(var4.save(new CompoundTag()));
               }
            }
         }

         return Stream.empty();
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof NbtComponent.BlockNbtComponent)) {
            return false;
         } else {
            NbtComponent.BlockNbtComponent var2 = (NbtComponent.BlockNbtComponent)var1;
            return Objects.equals(this.posPattern, var2.posPattern) && Objects.equals(this.nbtPathPattern, var2.nbtPathPattern) && super.equals(var1);
         }
      }

      public String toString() {
         return "BlockPosArgument{pos='" + this.posPattern + '\'' + "path='" + this.nbtPathPattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
      }

      // $FF: synthetic method
      public BaseComponent plainCopy() {
         return this.plainCopy();
      }

      // $FF: synthetic method
      public MutableComponent plainCopy() {
         return this.plainCopy();
      }
   }

   public static class EntityNbtComponent extends NbtComponent {
      private final String selectorPattern;
      @Nullable
      private final EntitySelector compiledSelector;

      public EntityNbtComponent(String var1, boolean var2, String var3) {
         super(var1, var2);
         this.selectorPattern = var3;
         this.compiledSelector = compileSelector(var3);
      }

      @Nullable
      private static EntitySelector compileSelector(String var0) {
         try {
            EntitySelectorParser var1 = new EntitySelectorParser(new StringReader(var0));
            return var1.parse();
         } catch (CommandSyntaxException var2) {
            return null;
         }
      }

      private EntityNbtComponent(String var1, @Nullable NbtPathArgument.NbtPath var2, boolean var3, String var4, @Nullable EntitySelector var5) {
         super(var1, var2, var3);
         this.selectorPattern = var4;
         this.compiledSelector = var5;
      }

      public String getSelector() {
         return this.selectorPattern;
      }

      public NbtComponent.EntityNbtComponent plainCopy() {
         return new NbtComponent.EntityNbtComponent(this.nbtPathPattern, this.compiledNbtPath, this.interpreting, this.selectorPattern, this.compiledSelector);
      }

      protected Stream<CompoundTag> getData(CommandSourceStack var1) throws CommandSyntaxException {
         if (this.compiledSelector != null) {
            List var2 = this.compiledSelector.findEntities(var1);
            return var2.stream().map(NbtPredicate::getEntityTagToCompare);
         } else {
            return Stream.empty();
         }
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof NbtComponent.EntityNbtComponent)) {
            return false;
         } else {
            NbtComponent.EntityNbtComponent var2 = (NbtComponent.EntityNbtComponent)var1;
            return Objects.equals(this.selectorPattern, var2.selectorPattern) && Objects.equals(this.nbtPathPattern, var2.nbtPathPattern) && super.equals(var1);
         }
      }

      public String toString() {
         return "EntityNbtComponent{selector='" + this.selectorPattern + '\'' + "path='" + this.nbtPathPattern + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
      }

      // $FF: synthetic method
      public BaseComponent plainCopy() {
         return this.plainCopy();
      }

      // $FF: synthetic method
      public MutableComponent plainCopy() {
         return this.plainCopy();
      }
   }
}
