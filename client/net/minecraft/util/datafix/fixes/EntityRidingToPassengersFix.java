package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EntityRidingToPassengersFix extends DataFix {
   public EntityRidingToPassengersFix(final Schema outputSchema, final boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      Schema inputSchema = this.getInputSchema();
      Schema outputSchema = this.getOutputSchema();
      Type<?> oldEntityTreeType = inputSchema.getTypeRaw(References.ENTITY_TREE);
      Type<?> newEntityTreeType = outputSchema.getTypeRaw(References.ENTITY_TREE);
      Type<?> entityType = inputSchema.getTypeRaw(References.ENTITY);
      return this.cap(inputSchema, outputSchema, oldEntityTreeType, newEntityTreeType, entityType);
   }

   private <OldEntityTree, NewEntityTree, Entity> TypeRewriteRule cap(final Schema inputSchema, final Schema outputType, final Type<OldEntityTree> oldEntityTreeType, final Type<NewEntityTree> newEntityTreeType, final Type<Entity> entityType) {
      Type<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> oldType = DSL.named(References.ENTITY_TREE.typeName(), DSL.and(DSL.optional(DSL.field("Riding", oldEntityTreeType)), entityType));
      Type<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> newType = DSL.named(References.ENTITY_TREE.typeName(), DSL.and(DSL.optional(DSL.field("Passengers", DSL.list(newEntityTreeType))), entityType));
      Type<?> oldEntityType = inputSchema.getType(References.ENTITY_TREE);
      Type<?> newEntityType = outputType.getType(References.ENTITY_TREE);
      if (!Objects.equals(oldEntityType, oldType)) {
         throw new IllegalStateException("Old entity type is not what was expected.");
      } else if (!newEntityType.equals(newType, true, true)) {
         throw new IllegalStateException("New entity type is not what was expected.");
      } else {
         OpticFinder<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> entityTreeFinder = DSL.typeFinder(oldType);
         OpticFinder<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> newEntityTreeValueFinder = DSL.typeFinder(newType);
         OpticFinder<NewEntityTree> newEntityTreeFinder = DSL.typeFinder(newEntityTreeType);
         Type<?> oldPlayerType = inputSchema.getType(References.PLAYER);
         Type<?> newPlayerType = outputType.getType(References.PLAYER);
         return TypeRewriteRule.seq(this.fixTypeEverywhere("EntityRidingToPassengerFix", oldType, newType, (ops) -> (input) -> {
               Optional<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> passenger = Optional.empty();
               Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>> updating = input;

               while(true) {
                  Either<List<NewEntityTree>, Unit> passengersValue = (Either)DataFixUtils.orElse(passenger.map((p) -> {
                     Typed<NewEntityTree> newEntity = (Typed)newEntityTreeType.pointTyped(ops).orElseThrow(() -> new IllegalStateException("Could not create new entity tree"));
                     NewEntityTree newEntityTree = (NewEntityTree)newEntity.set(newEntityTreeValueFinder, p).getOptional(newEntityTreeFinder).orElseThrow(() -> new IllegalStateException("Should always have an entity tree here"));
                     return Either.left(ImmutableList.of(newEntityTree));
                  }), Either.right(DSL.unit()));
                  passenger = Optional.of(Pair.of(References.ENTITY_TREE.typeName(), Pair.of(passengersValue, ((Pair)updating.getSecond()).getSecond())));
                  Optional<OldEntityTree> riding = ((Either)((Pair)updating.getSecond()).getFirst()).left();
                  if (riding.isEmpty()) {
                     return (Pair)passenger.orElseThrow(() -> new IllegalStateException("Should always have an entity tree here"));
                  }

                  updating = (Pair)(new Typed(oldEntityTreeType, ops, riding.get())).getOptional(entityTreeFinder).orElseThrow(() -> new IllegalStateException("Should always have an entity here"));
               }
            }), this.writeAndRead("player RootVehicle injecter", oldPlayerType, newPlayerType));
      }
   }
}
