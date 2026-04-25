package slimeknights.tconstruct.common.data.model;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;
import slimeknights.tconstruct.TConstruct;

import java.util.concurrent.CompletableFuture;

/** Minimal 1.21.1-compatible stub while sprite source datagen is migrated. */
public class TinkerSpriteSourceProvider extends SpriteSourceProvider {
  public TinkerSpriteSourceProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
    super(output, lookupProvider, TConstruct.MOD_ID, fileHelper);
  }

  @Override
  protected void gather() {
  }
}
