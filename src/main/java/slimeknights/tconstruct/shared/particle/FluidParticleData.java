package slimeknights.tconstruct.shared.particle;

import com.mojang.serialization.MapCodec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

/** Particle data for a fluid particle */
@RequiredArgsConstructor
public class FluidParticleData implements ParticleOptions {
  public static MapCodec<FluidParticleData> codec(ParticleType<FluidParticleData> type) {
    return FluidStack.CODEC.fieldOf("fluid").xmap(fluid -> new FluidParticleData(type, fluid), data -> data.fluid);
  }

  public static StreamCodec<? super RegistryFriendlyByteBuf, FluidParticleData> streamCodec(ParticleType<FluidParticleData> type) {
    return StreamCodec.of(
      (buffer, data) -> FluidStack.OPTIONAL_STREAM_CODEC.encode(buffer, data.fluid),
      buffer -> new FluidParticleData(type, FluidStack.OPTIONAL_STREAM_CODEC.decode(buffer))
    );
  }

  @Getter
  private final ParticleType<FluidParticleData> type;
  @Getter
  private final FluidStack fluid;

  @SuppressWarnings("deprecation")
  public String writeToString() {
    StringBuilder builder = new StringBuilder();
    builder.append(BuiltInRegistries.PARTICLE_TYPE.getKey(getType()));
    builder.append(" ");
    builder.append(BuiltInRegistries.FLUID.getKey(fluid.getFluid()));
    return builder.toString();
  }

  /** Particle type for a fluid particle */
  public static class Type extends ParticleType<FluidParticleData> {
    public Type() {
      super(false);
    }

    @Override
    public MapCodec<FluidParticleData> codec() {
      return FluidParticleData.codec(this);
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, FluidParticleData> streamCodec() {
      return FluidParticleData.streamCodec(this);
    }
  }
}
