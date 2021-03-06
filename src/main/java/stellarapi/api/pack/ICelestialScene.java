package stellarapi.api.pack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import stellarapi.api.celestials.CelestialCollection;
import stellarapi.api.celestials.CelestialObject;
import stellarapi.api.celestials.IEffectorType;
import stellarapi.api.render.IAdaptiveRenderer;
import stellarapi.api.view.IAtmosphereEffect;
import stellarapi.api.view.ICCoordinates;
import stellarapi.api.world.ICelestialHelper;

/**
 * Actual implementation of celestial scene for each world.
 * */
public interface ICelestialScene extends INBTSerializable<NBTTagCompound> {

	/**
	 * Prepare data for collection registry, coordinates and sky effect.
	 * */
	public void prepare();

	/**
	 * Register celestial collections here.
	 * */
	public void onRegisterCollection(Consumer<CelestialCollection> colRegistry,
			BiConsumer<IEffectorType, CelestialObject> effRegistry);

	/**
	 * Creates coordinates, or returns <code>null</code> if this pack doesn't provide coordinates.
	 * Nonnull for now.
	 * TODO Refactor on 1.13
	 * */
	public ICCoordinates createCoordinates();

	/**
	 * Creates sky effect, or returns <code>null</code> if this pack doesn't provide sky effect.
	 * Nonnull for now.
	 * */
	public IAtmosphereEffect createAtmosphereEffect();

	/**
	 * Creates celestial helper for world provider.
	 * Return <code>null</code> to not replace the world provider.
	 * */
	public @Nullable ICelestialHelper createCelestialHelper();

	/** Creates the sky renderer after the pack is determined. */
	public @Nullable IAdaptiveRenderer createSkyRenderer();


	/** Gets the update tag for server-client synchronization. */
	default public NBTTagCompound getUpdateTag() {
		return this.serializeNBT();
	}

	/** Handles the update tag for server-client synchronization. */
	default public void handleUpdateTag(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}
}