package forestry.farming.logic;

import forestry.core.utils.vect.Vect;
import forestry.plugins.compat.PluginIC2;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Objects;

public class CropBasicIC2Crop extends Crop {
	private final TileEntity tileEntity;

	public TileEntity getTileEntity() {
		return tileEntity;
	}

	public CropBasicIC2Crop(World world, TileEntity tileEntity, Vect position) {
		super(world, position);
		this.tileEntity = tileEntity;
	}

	public Vect getPosition() {
		return position;
	}

	public boolean isHarvestable(){
		return PluginIC2.instance.canHarvestCrop(this.tileEntity);
	}

	@Override
	protected boolean isCrop(Vect pos) {
		return PluginIC2.instance.canHarvestCrop(this.tileEntity);
	}

	@Override
	protected Collection<ItemStack> harvestBlock(Vect pos) {
		return PluginIC2.instance.getCropDrops(this.tileEntity);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CropBasicIC2Crop))
			return false;
		return Objects.equals(position, ((CropBasicIC2Crop) o).position);
	}

	@Override
	public int hashCode() {
		return position.hashCode();
	}
}
