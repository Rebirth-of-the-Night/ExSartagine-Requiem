package subaraki.exsartagine.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import subaraki.exsartagine.gui.client.screen.*;
import subaraki.exsartagine.gui.common.*;
import subaraki.exsartagine.tileentity.*;
import subaraki.exsartagine.util.Reference;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityPan && ID == Reference.PAN)
			return new ContainerPan(player.inventory, (TileEntityPan)te);
		if(te instanceof TileEntitySmelter && ID == Reference.SMELTER)
			return new ContainerSmelter(player.inventory, (TileEntitySmelter)te);
		if(te instanceof TileEntityPot && ID == Reference.POT)
			return new ContainerPot(player.inventory, (TileEntityPot)te);
		if(te instanceof TileEntityRange && ID == Reference.RANGE)
			return new ContainerRange(player.inventory, (TileEntityRange)te);
		if(te instanceof KettleBlockEntity && ID == Reference.KETTLE)
			return new KettleContainer(player.inventory, (KettleBlockEntity)te);
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityPan && ID == Reference.PAN)
			return new GuiPan(player, (TileEntityPan)te);
		if(te instanceof TileEntitySmelter && ID == Reference.SMELTER)
			return new GuiSmelter(player, (TileEntitySmelter)te);
		if(te instanceof TileEntityPot && ID == Reference.POT)
			return new GuiPot(player, (TileEntityPot)te);
		if(te instanceof TileEntityRange && ID == Reference.RANGE)
			return new GuiRange(player, (TileEntityRange)te);
		if(te instanceof KettleBlockEntity && ID == Reference.KETTLE)
			return new KettleScreen(player, (KettleBlockEntity) te);
		return null;
	}
}
