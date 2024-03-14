package net.minecraft.advancements;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.CriterionValidator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootDataResolver;

public record Advancement(
   Optional<ResourceLocation> c,
   Optional<DisplayInfo> d,
   AdvancementRewards e,
   Map<String, Criterion<?>> f,
   AdvancementRequirements g,
   boolean h,
   Optional<Component> i
) {
   private final Optional<ResourceLocation> parent;
   private final Optional<DisplayInfo> display;
   private final AdvancementRewards rewards;
   private final Map<String, Criterion<?>> criteria;
   private final AdvancementRequirements requirements;
   private final boolean sendsTelemetryEvent;
   private final Optional<Component> name;
   private static final Codec<Map<String, Criterion<?>>> CRITERIA_CODEC = ExtraCodecs.validate(
      Codec.unboundedMap(Codec.STRING, Criterion.CODEC),
      var0 -> var0.isEmpty() ? DataResult.error(() -> "Advancement criteria cannot be empty") : DataResult.success(var0)
   );
   public static final Codec<Advancement> CODEC = ExtraCodecs.validate(
      RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(ResourceLocation.CODEC, "parent").forGetter(Advancement::parent),
                  ExtraCodecs.strictOptionalField(DisplayInfo.CODEC, "display").forGetter(Advancement::display),
                  ExtraCodecs.strictOptionalField(AdvancementRewards.CODEC, "rewards", AdvancementRewards.EMPTY).forGetter(Advancement::rewards),
                  CRITERIA_CODEC.fieldOf("criteria").forGetter(Advancement::criteria),
                  ExtraCodecs.strictOptionalField(AdvancementRequirements.CODEC, "requirements").forGetter(var0x -> Optional.of(var0x.requirements())),
                  ExtraCodecs.strictOptionalField(Codec.BOOL, "sends_telemetry_event", false).forGetter(Advancement::sendsTelemetryEvent)
               )
               .apply(var0, (var0x, var1, var2, var3, var4, var5) -> {
                  AdvancementRequirements var6 = (AdvancementRequirements)var4.orElseGet(() -> AdvancementRequirements.allOf(var3.keySet()));
                  return new Advancement(var0x, var1, var2, var3, var6, var5);
               })
      ),
      Advancement::validate
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, Advancement> STREAM_CODEC = StreamCodec.ofMember(Advancement::write, Advancement::read);

   public Advancement(
      Optional<ResourceLocation> var1,
      Optional<DisplayInfo> var2,
      AdvancementRewards var3,
      Map<String, Criterion<?>> var4,
      AdvancementRequirements var5,
      boolean var6
   ) {
      this(var1, var2, var3, Map.copyOf(var4), var5, var6, var2.map(Advancement::decorateName));
   }

   public Advancement(
      Optional<ResourceLocation> var1,
      Optional<DisplayInfo> var2,
      AdvancementRewards var3,
      Map<String, Criterion<?>> var4,
      AdvancementRequirements var5,
      boolean var6,
      Optional<Component> var7
   ) {
      super();
      this.parent = var1;
      this.display = var2;
      this.rewards = var3;
      this.criteria = var4;
      this.requirements = var5;
      this.sendsTelemetryEvent = var6;
      this.name = var7;
   }

   private static DataResult<Advancement> validate(Advancement var0) {
      return var0.requirements().validate(var0.criteria().keySet()).map(var1 -> var0);
   }

   private static Component decorateName(DisplayInfo var0) {
      Component var1 = var0.getTitle();
      ChatFormatting var2 = var0.getType().getChatColor();
      MutableComponent var3 = ComponentUtils.mergeStyles(var1.copy(), Style.EMPTY.withColor(var2)).append("\n").append(var0.getDescription());
      MutableComponent var4 = var1.copy().withStyle(var1x -> var1x.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var3)));
      return ComponentUtils.wrapInSquareBrackets(var4).withStyle(var2);
   }

   public static Component name(AdvancementHolder var0) {
      return var0.value().name().orElseGet(() -> Component.literal(var0.id().toString()));
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeOptional(this.parent, FriendlyByteBuf::writeResourceLocation);
      DisplayInfo.STREAM_CODEC.apply(ByteBufCodecs::optional).encode(var1, this.display);
      this.requirements.write(var1);
      var1.writeBoolean(this.sendsTelemetryEvent);
   }

   private static Advancement read(RegistryFriendlyByteBuf var0) {
      return new Advancement(
         var0.readOptional(FriendlyByteBuf::readResourceLocation),
         (Optional<DisplayInfo>)DisplayInfo.STREAM_CODEC.apply(ByteBufCodecs::optional).decode(var0),
         AdvancementRewards.EMPTY,
         Map.of(),
         new AdvancementRequirements(var0),
         var0.readBoolean()
      );
   }

   public boolean isRoot() {
      return this.parent.isEmpty();
   }

   public void validate(ProblemReporter var1, LootDataResolver var2) {
      this.criteria.forEach((var2x, var3) -> {
         CriterionValidator var4 = new CriterionValidator(var1.forChild(var2x), var2);
         var3.triggerInstance().validate(var4);
      });
   }

   public static class Builder {
      private Optional<ResourceLocation> parent = Optional.empty();
      private Optional<DisplayInfo> display = Optional.empty();
      private AdvancementRewards rewards = AdvancementRewards.EMPTY;
      private final com.google.common.collect.ImmutableMap.Builder<String, Criterion<?>> criteria = ImmutableMap.builder();
      private Optional<AdvancementRequirements> requirements = Optional.empty();
      private AdvancementRequirements.Strategy requirementsStrategy = AdvancementRequirements.Strategy.AND;
      private boolean sendsTelemetryEvent;

      public Builder() {
         super();
      }

      public static Advancement.Builder advancement() {
         return new Advancement.Builder().sendsTelemetryEvent();
      }

      public static Advancement.Builder recipeAdvancement() {
         return new Advancement.Builder();
      }

      public Advancement.Builder parent(AdvancementHolder var1) {
         this.parent = Optional.of(var1.id());
         return this;
      }

      @Deprecated(
         forRemoval = true
      )
      public Advancement.Builder parent(ResourceLocation var1) {
         this.parent = Optional.of(var1);
         return this;
      }

      public Advancement.Builder display(
         ItemStack var1, Component var2, Component var3, @Nullable ResourceLocation var4, AdvancementType var5, boolean var6, boolean var7, boolean var8
      ) {
         return this.display(new DisplayInfo(var1, var2, var3, Optional.ofNullable(var4), var5, var6, var7, var8));
      }

      public Advancement.Builder display(
         ItemLike var1, Component var2, Component var3, @Nullable ResourceLocation var4, AdvancementType var5, boolean var6, boolean var7, boolean var8
      ) {
         return this.display(new DisplayInfo(new ItemStack(var1.asItem()), var2, var3, Optional.ofNullable(var4), var5, var6, var7, var8));
      }

      public Advancement.Builder display(DisplayInfo var1) {
         this.display = Optional.of(var1);
         return this;
      }

      public Advancement.Builder rewards(AdvancementRewards.Builder var1) {
         return this.rewards(var1.build());
      }

      public Advancement.Builder rewards(AdvancementRewards var1) {
         this.rewards = var1;
         return this;
      }

      public Advancement.Builder addCriterion(String var1, Criterion<?> var2) {
         this.criteria.put(var1, var2);
         return this;
      }

      public Advancement.Builder requirements(AdvancementRequirements.Strategy var1) {
         this.requirementsStrategy = var1;
         return this;
      }

      public Advancement.Builder requirements(AdvancementRequirements var1) {
         this.requirements = Optional.of(var1);
         return this;
      }

      public Advancement.Builder sendsTelemetryEvent() {
         this.sendsTelemetryEvent = true;
         return this;
      }

      public AdvancementHolder build(ResourceLocation var1) {
         ImmutableMap var2 = this.criteria.buildOrThrow();
         AdvancementRequirements var3 = (AdvancementRequirements)this.requirements.orElseGet(() -> this.requirementsStrategy.create(var2.keySet()));
         return new AdvancementHolder(var1, new Advancement(this.parent, this.display, this.rewards, var2, var3, this.sendsTelemetryEvent));
      }

      public AdvancementHolder save(Consumer<AdvancementHolder> var1, String var2) {
         AdvancementHolder var3 = this.build(new ResourceLocation(var2));
         var1.accept(var3);
         return var3;
      }
   }
}
