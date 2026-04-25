package slimeknights.tconstruct.compat.neoforge.common.crafting;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Temporary compatibility shim for Forge-style conditional recipe builders used by legacy datagen.
 * TODO 1.21.1: replace callsites with direct RecipeOutput.withConditions(...) usage and remove this class.
 */
@SuppressWarnings("unused")
public final class ConditionalRecipe {
  private ConditionalRecipe() {}

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private final List<Entry> entries = new ArrayList<>();
    private final List<ICondition> pendingConditions = new ArrayList<>();

    public Builder addCondition(ICondition condition) {
      pendingConditions.add(condition);
      return this;
    }

    public Builder addRecipe(Consumer<RecipeOutput> recipe) {
      entries.add(new Entry(List.copyOf(pendingConditions), recipe));
      pendingConditions.clear();
      return this;
    }

    public Builder generateAdvancement() {
      return this;
    }

    public void build(RecipeOutput output, ResourceLocation id) {
      for (int i = 0; i < entries.size(); i++) {
        Entry entry = entries.get(i);
        RecipeOutput wrapped = output.withConditions(entry.conditions.toArray(ICondition[]::new));
        entry.recipe.accept(wrapped);
      }
    }

    private record Entry(List<ICondition> conditions, Consumer<RecipeOutput> recipe) {}
  }
}
