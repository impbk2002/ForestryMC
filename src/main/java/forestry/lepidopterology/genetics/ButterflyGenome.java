/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.lepidopterology.genetics;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.ISpeciesRoot;
import forestry.api.lepidopterology.EnumButterflyChromosome;
import forestry.api.lepidopterology.IAlleleButterflyEffect;
import forestry.api.lepidopterology.IAlleleButterflySpecies;
import forestry.api.lepidopterology.IButterflyGenome;
import forestry.core.genetics.Genome;
import forestry.core.genetics.alleles.AlleleBoolean;
import forestry.core.genetics.alleles.AlleleTolerance;
import forestry.plugins.PluginLepidopterology;

public class ButterflyGenome extends Genome implements IButterflyGenome {

	/* CONSTRUCTOR */
	public ButterflyGenome(NBTTagCompound nbttagcompound) {
		super(nbttagcompound);
	}

	public ButterflyGenome(IChromosome[] chromosomes) {
		super(chromosomes);
	}

	// NBT RETRIEVAL
	public static IAlleleButterflySpecies getSpecies(ItemStack itemStack) {
		return (IAlleleButterflySpecies) getActiveAllele(itemStack, EnumButterflyChromosome.SPECIES, PluginLepidopterology.butterflyInterface);
	}

	/* SPECIES */
	@Override
	public IAlleleButterflySpecies getPrimary() {
		return (IAlleleButterflySpecies) getActiveAllele(EnumButterflyChromosome.SPECIES);
	}

	@Override
	public IAlleleButterflySpecies getSecondary() {
		return (IAlleleButterflySpecies) getInactiveAllele(EnumButterflyChromosome.SPECIES);
	}

	@Override
	public float getSize() {
		return ((IAlleleFloat) getActiveAllele(EnumButterflyChromosome.SIZE)).getValue();
	}
	
	@Override
	public int getLifespan() {
		return ((IAlleleInteger) getActiveAllele(EnumButterflyChromosome.LIFESPAN)).getValue();
	}
	
	@Override
	public float getSpeed() {
		return ((IAlleleFloat) getActiveAllele(EnumButterflyChromosome.SPEED)).getValue();
	}

	@Override
	public int getMetabolism() {
		return ((IAlleleInteger) getActiveAllele(EnumButterflyChromosome.METABOLISM)).getValue();
	}

	@Override
	public int getFertility() {
		return ((IAlleleInteger) getActiveAllele(EnumButterflyChromosome.FERTILITY)).getValue();
	}

	@Override
	public EnumTolerance getToleranceTemp() {
		return ((AlleleTolerance) getActiveAllele(EnumButterflyChromosome.TEMPERATURE_TOLERANCE)).getValue();
	}

	@Override
	public EnumTolerance getToleranceHumid() {
		return ((AlleleTolerance) getActiveAllele(EnumButterflyChromosome.HUMIDITY_TOLERANCE)).getValue();
	}

	@Override
	public boolean getNocturnal() {
		return ((AlleleBoolean) getActiveAllele(EnumButterflyChromosome.NOCTURNAL)).getValue();
	}

	@Override
	public boolean getTolerantFlyer() {
		return ((AlleleBoolean) getActiveAllele(EnumButterflyChromosome.TOLERANT_FLYER)).getValue();
	}

	@Override
	public boolean getFireResist() {
		return ((AlleleBoolean) getActiveAllele(EnumButterflyChromosome.FIRE_RESIST)).getValue();
	}

	@Override
	public IFlowerProvider getFlowerProvider() {
		return ((IAlleleFlowers) getActiveAllele(EnumButterflyChromosome.FLOWER_PROVIDER)).getProvider();
	}

	@Override
	public IAlleleButterflyEffect getEffect() {
		return (IAlleleButterflyEffect) getActiveAllele(EnumButterflyChromosome.EFFECT);
	}

	@Override
	public ISpeciesRoot getSpeciesRoot() {
		return PluginLepidopterology.butterflyInterface;
	}

}
