package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootContext {
   private final LootParams params;
   private final RandomSource random;
   private final HolderGetter.Provider lootDataResolver;
   private final Set<LootContext.VisitedEntry<?>> visitedElements = Sets.newLinkedHashSet();

   LootContext(LootParams var1, RandomSource var2, HolderGetter.Provider var3) {
      super();
      this.params = var1;
      this.random = var2;
      this.lootDataResolver = var3;
   }

   public boolean hasParam(LootContextParam<?> var1) {
      return this.params.hasParam(var1);
   }

   public <T> T getParam(LootContextParam<T> var1) {
      return this.params.getParameter(var1);
   }

   public void addDynamicDrops(ResourceLocation var1, Consumer<ItemStack> var2) {
      this.params.addDynamicDrops(var1, var2);
   }

   @Nullable
   public <T> T getParamOrNull(LootContextParam<T> var1) {
      return this.params.getParamOrNull(var1);
   }

   public boolean hasVisitedElement(LootContext.VisitedEntry<?> var1) {
      return this.visitedElements.contains(var1);
   }

   public boolean pushVisitedElement(LootContext.VisitedEntry<?> var1) {
      return this.visitedElements.add(var1);
   }

   public void popVisitedElement(LootContext.VisitedEntry<?> var1) {
      this.visitedElements.remove(var1);
   }

   public HolderGetter.Provider getResolver() {
      return this.lootDataResolver;
   }

   public RandomSource getRandom() {
      return this.random;
   }

   public float getLuck() {
      return this.params.getLuck();
   }

   public ServerLevel getLevel() {
      return this.params.getLevel();
   }

   public static LootContext.VisitedEntry<LootTable> createVisitedEntry(LootTable var0) {
      return new LootContext.VisitedEntry<>(LootDataType.TABLE, var0);
   }

   public static LootContext.VisitedEntry<LootItemCondition> createVisitedEntry(LootItemCondition var0) {
      return new LootContext.VisitedEntry<>(LootDataType.PREDICATE, var0);
   }

   public static LootContext.VisitedEntry<LootItemFunction> createVisitedEntry(LootItemFunction var0) {
      return new LootContext.VisitedEntry<>(LootDataType.MODIFIER, var0);
   }

   public static class Builder {
      private final LootParams params;
      @Nullable
      private RandomSource random;

      public Builder(LootParams var1) {
         super();
         this.params = var1;
      }

      public LootContext.Builder withOptionalRandomSeed(long var1) {
         if (var1 != 0L) {
            this.random = RandomSource.create(var1);
         }

         return this;
      }

      public LootContext.Builder withOptionalRandomSource(RandomSource var1) {
         this.random = var1;
         return this;
      }

      public ServerLevel getLevel() {
         return this.params.getLevel();
      }

      public LootContext create(Optional<ResourceLocation> var1) {
         ServerLevel var2 = this.getLevel();
         MinecraftServer var3 = var2.getServer();
         RandomSource var4 = Optional.ofNullable(this.random).or(() -> var1.map(var2::getRandomSequence)).orElseGet(var2::getRandom);
         return new LootContext(this.params, var4, var3.reloadableRegistries().lookup());
      }
   }

   public static enum EntityTarget implements StringRepresentable {
      THIS("this", LootContextParams.THIS_ENTITY),
      ATTACKER("attacker", LootContextParams.ATTACKING_ENTITY),
      DIRECT_ATTACKER("direct_attacker", LootContextParams.DIRECT_ATTACKING_ENTITY),
      ATTACKING_PLAYER("attacking_player", LootContextParams.LAST_DAMAGE_PLAYER);

      public static final StringRepresentable.EnumCodec<LootContext.EntityTarget> CODEC = StringRepresentable.fromEnum(LootContext.EntityTarget::values);
      private final String name;
      private final LootContextParam<? extends Entity> param;

      private EntityTarget(final String nullxx, final LootContextParam<? extends Entity> nullxxx) {
         this.name = nullxx;
         this.param = nullxxx;
      }

      public LootContextParam<? extends Entity> getParam() {
         return this.param;
      }

      public static LootContext.EntityTarget getByName(String var0) {
         LootContext.EntityTarget var1 = CODEC.byName(var0);
         if (var1 != null) {
            return var1;
         } else {
            throw new IllegalArgumentException("Invalid entity target " + var0);
         }
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
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
}
