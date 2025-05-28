package net.minecraft.server.dialog.action;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.resources.ResourceLocation;

public record CustomAll(ResourceLocation id, Optional<CompoundTag> additions) implements Action {
   public static final MapCodec<CustomAll> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("id").forGetter(CustomAll::id), CompoundTag.CODEC.optionalFieldOf("additions").forGetter(CustomAll::additions)).apply(var0, CustomAll::new));

   public CustomAll(ResourceLocation var1, Optional<CompoundTag> var2) {
      super();
      this.id = var1;
      this.additions = var2;
   }

   public MapCodec<CustomAll> codec() {
      return MAP_CODEC;
   }

   public Optional<ClickEvent> createAction(Map<String, Action.ValueGetter> var1) {
      CompoundTag var2 = (CompoundTag)this.additions.map(CompoundTag::copy).orElseGet(CompoundTag::new);
      var1.forEach((var1x, var2x) -> var2.put(var1x, var2x.asTag()));
      return Optional.of(new ClickEvent.Custom(this.id, Optional.of(var2)));
   }
}
